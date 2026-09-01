#!/usr/bin/env bash
# ============================================================
# 12_kicks_install.sh — Installation de KICKS sur MVS TK5
#
# KICKS = Kent Integrated CICS Knockout System v1.5.0
# Remplace CICS sur MVS 3.8j — tourne sous TSO (pas de STC)
#
# Phases :
#   1  dasd     Créer volume KICKS0 (3350) + attacher Hercules
#   2  ickdsf   Formater le volume avec ICKDSF
#   3  catalog  Créer catalogue UCKICKS0 + alias KICKS (IDCAMS)
#   4  xmi      Télécharger + charger le XMI dans le lecteur de cartes
#   5  recv     Décompresser XMI → KICKS.V1R5M0.INSTALL (RECV370)
#   6  rcvkick  Décompresser les librairies KICKS (RCVKICK2)
#   7  dynamnbr Augmenter DYNAMNBR=64 dans SYS1.PROCLIB(IKJACCNT)
#   8  jcl      Mettre à jour GSTKBMS.jcl + GSTKCOMP.jcl pour KICKS
#
# Usage :
#   bash scripts/mvs/12_kicks_install.sh all
#   bash scripts/mvs/12_kicks_install.sh dasd       # une phase
#   bash scripts/mvs/12_kicks_install.sh dasd ickdsf catalog  # plusieurs
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GSTK_DIR="$(cd "${SCRIPT_DIR}/../../GSTK" && pwd)"

source "${SCRIPT_DIR}/s3270_lib.sh"

# ============================================================
# Configuration
# ============================================================
DOCKER_CONTAINER="${DOCKER_CONTAINER:-mvs-tk5}"
HERC_URL="${HERC_URL:-http://localhost:8038}"
USER="${TSO_USER:-HERC02}"
PASS="${TSO_PASS:-CUL8TR}"

KICKS_VOLSER="KICKS0"
KICKS_DEVADDR="0351"
KICKS_DEVTYPE="3350"
KICKS_DASD_DOCKER="/opt/tk5/dasd/kicks0.3350"   # chemin dans Docker
KICKS_DASD_REL="dasd/kicks0.3350"               # relatif à /opt/tk5 (pour Hercules)

KICKS_XMI_NAME="kicks-tso-v1r5m0.xmi"
KICKS_XMI_LOCAL="/tmp/${KICKS_XMI_NAME}"
KICKS_XMI_URL="https://github.com/moshix/kicks/raw/master/kicks-tso-v1r5m0/${KICKS_XMI_NAME}"

# ============================================================
# Couleurs
# ============================================================
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'
ok()   { echo -e "${GREEN}✓${NC} $*"; }
fail() { echo -e "${RED}✗ ERREUR :${NC} $*" >&2; exit 1; }
warn() { echo -e "${YELLOW}⚠${NC}  $*"; }
info() { echo -e "${CYAN}ℹ${NC}  $*"; }
step() { echo -e "\n${BOLD}=== $* ===${NC}"; }

# ============================================================
# Helpers Hercules + JES2
# ============================================================

herc_cmd() {
    curl -s -X POST "${HERC_URL}/cgi-bin/tasks/syslog" \
        --data-urlencode "command=$1" \
        --data "norefresh=1" --data "msgcount=5" \
        | sed 's/<[^>]*>//g' \
        | grep -v "^Command:\|^Only\|^Refresh\|^$" \
        | tail -5
}

herc_syslog() {
    local n="${1:-40}"
    curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=${n}" \
        | sed 's/<[^>]*>//g' \
        | grep -v "^Command:\|^Only\|^Refresh\|^$"
}

submit_cardreader() {
    # Envoyer du texte ASCII au lecteur de cartes (conversion ASCII→EBCDIC par TK5)
    # Utilise un fichier temp pour éviter les problèmes d'expansion de variables
    # dans les here-docs et les subtilités de printf avec les sauts de ligne.
    local jcl="$1"
    local _tmpjcl
    _tmpjcl=$(mktemp /tmp/kicks_jcl.XXXXXX)
    printf '%s\n' "$jcl" > "${_tmpjcl}"
    docker exec -i "${DOCKER_CONTAINER}" bash -c \
        "exec 3<>/dev/tcp/127.0.0.1/3505; cat >&3; exec 3>&-" \
        < "${_tmpjcl}" \
        2>/dev/null \
        && ok "JCL envoyé au lecteur de cartes JES2" \
        || { rm -f "${_tmpjcl}"; fail "Échec envoi lecteur de cartes"; return 1; }
    rm -f "${_tmpjcl}"
}

wait_job() {
    local jobname="$1"
    local maxwait="${2:-180}"
    local elapsed=0
    printf "  Attente %s " "$jobname"
    while [[ $elapsed -lt $maxwait ]]; do
        sleep 5; elapsed=$(( elapsed + 5 )); printf "."
        local sl
        sl=$(herc_syslog 80)
        if echo "$sl" | grep -qi "HASP395.*${jobname}\|${jobname}.*HASP395"; then
            echo " OK (${elapsed}s)"
            if echo "$sl" | grep -qi "HASP396.*${jobname}\|ABEND"; then
                warn "ABEND possible pour ${jobname} — vérifier : bash herc.sh log 60"
            fi
            return 0
        fi
        if echo "$sl" | grep -qi "IEF452I.*${jobname}\|IEF453I.*${jobname}"; then
            echo " ERREUR JCL"
            warn "Erreur JCL pour ${jobname} — vérifier : bash herc.sh log 60"
            return 1
        fi
    done
    echo " timeout (${maxwait}s) — bash herc.sh log 60"
    return 1
}

# ============================================================
# Phase 1 : Créer le volume DASD KICKS0 et l'attacher à Hercules
# ============================================================
phase1_dasd() {
    step "Phase 1 : Volume DASD KICKS0"

    # Créer le DASD si inexistant
    if docker exec "${DOCKER_CONTAINER}" test -f "${KICKS_DASD_DOCKER}" 2>/dev/null; then
        ok "DASD ${KICKS_DASD_DOCKER} existe déjà — passage à la suite"
    else
        info "Création DASD ${KICKS_DEVTYPE} (200 cylindres) — VOLSER ${KICKS_VOLSER}..."
        docker exec "${DOCKER_CONTAINER}" \
            /opt/hercules/bin/dasdinit -z "${KICKS_DASD_DOCKER}" "${KICKS_DEVTYPE}" "${KICKS_VOLSER}" \
            && ok "DASD créé : ${KICKS_DASD_DOCKER}" \
            || fail "dasdinit échoué"
    fi

    # Ajouter à usr_dasd.cnf si absent
    local cfg_line="${KICKS_DEVADDR} ${KICKS_DEVTYPE} ${KICKS_DASD_REL}"
    if docker exec "${DOCKER_CONTAINER}" grep -q "${KICKS_DEVADDR}" \
            /opt/tk5/dasd.usr/usr_dasd.cnf 2>/dev/null; then
        ok "Périphérique ${KICKS_DEVADDR} déjà dans usr_dasd.cnf"
    else
        docker exec "${DOCKER_CONTAINER}" bash -c \
            "echo '${cfg_line}' >> /opt/tk5/dasd.usr/usr_dasd.cnf"
        ok "Ajouté à usr_dasd.cnf : ${cfg_line}"
    fi

    # Attacher dynamiquement à Hercules (sans IPL)
    info "Attachement Hercules du périphérique ${KICKS_DEVADDR}..."
    local resp
    resp=$(herc_cmd "attach ${KICKS_DEVADDR} ${KICKS_DEVTYPE} ${KICKS_DASD_REL}" 2>/dev/null || true)
    sleep 2

    local devstat
    devstat=$(herc_cmd "devstat ${KICKS_DEVADDR}" 2>/dev/null || echo "  (devstat non disponible)")
    echo "  DevStat ${KICKS_DEVADDR}: $devstat"
    ok "Phase 1 terminée"
}

# ============================================================
# Phase 2 : Formater le volume avec ICKDSF
# ============================================================
phase2_ickdsf() {
    step "Phase 2 : Formatage KICKS0 (ICKDSF)"
    warn "NOTE : device 0351 doit être dans le UCB MVS (requis après restart Docker)"
    warn "Si vous voyez IEF238D, faire 'docker restart mvs-tk5' et relancer cette phase"

    submit_cardreader "//KICKDSFJ JOB ,'KICKS ICKDSF',CLASS=A,MSGCLASS=A,
//             MSGLEVEL=(1,1),NOTIFY=${USER},
//             USER=${USER},PASSWORD=${PASS}
//ICKDSF   EXEC PGM=ICKDSF,REGION=4096K
//SYSPRINT DD   SYSOUT=A
//SYSIN    DD   *
  INIT UNITADDRESS(${KICKS_DEVADDR}) NOVERIFY VOLID(${KICKS_VOLSER}) -
       VTOC(0,1,14) PURGE INDEX(1)
/*
//"

    wait_job "KICKDSFJ" 180
}

# ============================================================
# Phase 3 : Créer le catalogue utilisateur UCKICKS0
# ============================================================
phase3_catalog() {
    step "Phase 3 : Catalogue UCKICKS0 + alias KICKS (IDCAMS)"

    submit_cardreader "//KICKIDCM JOB ,'KICKS IDCAMS',CLASS=A,MSGCLASS=A,
//             MSGLEVEL=(1,1),NOTIFY=${USER},
//             USER=${USER},PASSWORD=${PASS}
//IDCAMS   EXEC PGM=IDCAMS,REGION=4096K
//SYSPRINT DD   SYSOUT=A
//SYSIN    DD   *
  DEFINE USERCATALOG (                                               -
         NAME(UCKICKS0)                                              -
         VOLUME(${KICKS_VOLSER})                                     -
         CYLINDERS(5 1))
  DEFINE ALIAS (NAME(KICKS) RELATE(UCKICKS0))
/*
//"

    wait_job "KICKIDCM" 120
}

# ============================================================
# Phase 4 : Télécharger le XMI + uploader sur MVS via IND$FILE
#
# Pourquoi IND$FILE et pas devinit ?
#   Le lecteur de cartes TK5 (000C) est un sockdev ASCII : il ne
#   peut pas lire un fichier binaire EBCDIC via devinit (access()
#   échoue côté QEMU user-mode). IND$FILE s3270 en mode binaire
#   transfère le fichier EBCDIC FB80 tel quel vers un dataset MVS,
#   sans conversion — fiable et sans IPL requis.
#
# Séquence :
#   4a  Télécharger XMI localement
#   4b  Allouer HERC02.KICKS.XMI sur MVS (lecteur de cartes)
#   4c  Uploader le XMI via IND$FILE (s3270 binary, RECFM=FB,LRECL=80)
# ============================================================
phase4_xmi() {
    step "Phase 4 : Download + upload XMI KICKS → MVS"

    # --- 4a : Télécharger ---
    if [[ -f "${KICKS_XMI_LOCAL}" ]]; then
        ok "XMI déjà présent : ${KICKS_XMI_LOCAL} ($(du -h "${KICKS_XMI_LOCAL}" | cut -f1))"
    else
        info "Téléchargement KICKS v1.5.0 depuis GitHub..."
        curl -L --progress-bar -o "${KICKS_XMI_LOCAL}" "${KICKS_XMI_URL}" \
            && ok "XMI téléchargé : $(du -h "${KICKS_XMI_LOCAL}" | cut -f1)" \
            || fail "Échec téléchargement — vérifier la connexion internet"
    fi

    # Valider format (multiple de 80 octets)
    local filesize
    filesize=$(wc -c < "${KICKS_XMI_LOCAL}")
    if (( filesize % 80 != 0 )); then
        warn "Taille XMI (${filesize}) non multiple de 80 — corrompu ?"
        warn "Essayer : rm ${KICKS_XMI_LOCAL} && relancer cette phase"
    else
        ok "XMI valide : ${filesize} octets = $((filesize / 80)) enregistrements FB80"
    fi

    # --- 4b : Upload XMI via IND$FILE (s3270 binary) ---
    # Transfer() alloue le dataset MVS automatiquement si inexistant.
    info "Upload XMI → MVS via IND\$FILE (s3270, mode binaire)..."
    info "Taille : $(du -h "${KICKS_XMI_LOCAL}" | cut -f1) — prévoir 3-10 min..."

    s3270_start || fail "s3270 ne démarre pas"
    { set +e; s3270_login; _login_rc=$?; set -e; }
    if [[ $_login_rc -ne 0 ]]; then
        s3270_stop; fail "Login TSO échoué (rc=$_login_rc) — relancer la phase xmi"
    fi

    # Assurer l'état TSO READY avant Transfer() (sortir d'ISPF si nécessaire)
    info "Navigation vers TSO READY..."
    local _scr _tries=0
    while [[ $_tries -lt 10 ]]; do
        _scr=$(s3270_cmd "Ascii()" 10 2>/dev/null || true)
        if echo "$_scr" | grep -qE "^\s*READY"; then
            ok "TSO READY confirmé"
            break
        fi
        # Sortir d'ISPF / autre écran
        s3270_cmd "String(\"END\")" 5 >/dev/null 2>&1 || true
        s3270_cmd "Enter()" 30 >/dev/null 2>&1 || true
        sleep 2
        _tries=$(( _tries + 1 ))
    done

    # Mode=binary : pas de conversion charset, EBCDIC pur (le XMI est déjà EBCDIC FB80)
    # NOTE : LocalFile sans guillemets — s3270 scriptport traite "..." comme faisant
    #        partie du chemin (pas de stripping des quotes), donc on passe le path brut.
    if s3270_cmd \
        "Transfer(Direction=send,HostFile=\"'${USER}.KICKS.XMI'\",LocalFile=${KICKS_XMI_LOCAL},Host=tso,Recfm=fixed,Lrecl=80,BlockSize=3200,Mode=binary)" \
        900; then
        ok "XMI uploadé sur MVS : ${USER}.KICKS.XMI"
    else
        s3270_stop
        fail "Échec IND\$FILE upload — relancer la phase xmi"
    fi

    s3270_stop
    ok "Phase 4 terminée"
}

# ============================================================
# Phase 5 : TSO RECEIVE — décompresser HERC02.KICKS.XMI
#           → KICKS.V1R5M0.INSTALL
#
# TSO RECEIVE est la méthode standard pour les XMI (XMIT format).
# On se connecte via s3270, on navigue vers TSO READY, puis on
# exécute : RECEIVE INDS('HERC02.KICKS.XMI')
# Quand TSO affiche la DSN proposée, on répond avec DA+ (accept par défaut).
# ============================================================
phase5_recv() {
    step "Phase 5 : TSO RECEIVE — KICKS.V1R5M0.INSTALL"

    info "Connexion TSO via s3270..."
    s3270_start || fail "s3270 ne démarre pas"
    { set +e; s3270_login; _login_rc=$?; set -e; }
    if [[ $_login_rc -ne 0 ]]; then
        s3270_stop; fail "Login TSO échoué — relancer la phase recv"
    fi

    # Naviguer vers TSO READY
    info "Navigation vers TSO READY..."
    local _scr _tries=0
    while [[ $_tries -lt 10 ]]; do
        _scr=$(s3270_cmd "Ascii()" 10 2>/dev/null || true)
        if echo "$_scr" | grep -qE "READY"; then break; fi
        s3270_cmd "String(\"END\")" 5 >/dev/null 2>&1 || true
        s3270_cmd "Enter()" 30 >/dev/null 2>&1 || true
        sleep 2; _tries=$(( _tries + 1 ))
    done
    ok "TSO READY"

    # TSO RECEIVE INDS(...)
    # Note : Single quotes in String() peuvent causer des problèmes avec le parser
    # scriptport. On utilise la forme sans quotes (TSO accepte les noms non-quotés).
    info "TSO RECEIVE INDS('${USER}.KICKS.XMI')..."
    s3270_cmd "String(\"RECEIVE INDS('${USER}.KICKS.XMI')\")" 5 >/dev/null 2>&1 || {
        # Fallback : sans quotes
        s3270_cmd "String(\"RECEIVE INDS(${USER}.KICKS.XMI)\")" 5 >/dev/null 2>&1 || true
    }
    s3270_cmd "Enter()" 120 >/dev/null 2>&1 || true
    sleep 8

    # TSO demande de confirmer ou modifier le dataset cible
    # Répondre avec rien (Enter) pour accepter la DSN par défaut = KICKS.V1R5M0.INSTALL
    _scr=$(s3270_cmd "Ascii()" 10 2>/dev/null || true)
    info "Écran TSO après RECEIVE :"
    echo "$_scr" | head -5

    if echo "$_scr" | grep -qiE "DSN=|Enter|RECEIVE|INMR"; then
        # Accepter la DSN proposée par RECEIVE
        s3270_cmd "Enter()" 120 >/dev/null 2>&1 || true
        sleep 5
        _scr=$(s3270_cmd "Ascii()" 10 2>/dev/null || true)
        info "Écran après confirmation :"
        echo "$_scr" | head -8
    fi

    # Attendre que RECEIVE se termine (peut prendre 1-2 min pour 8 MB)
    info "Attente fin TSO RECEIVE (1-3 min)..."
    local _deadline=$(( SECONDS + 300 ))
    while [[ $SECONDS -lt $_deadline ]]; do
        sleep 5
        _scr=$(s3270_cmd "Ascii()" 10 2>/dev/null || true)
        if echo "$_scr" | grep -qE "READY|RECEIVED|INMR99|COMPLETE"; then
            ok "RECEIVE terminé"
            break
        fi
        if echo "$_scr" | grep -qiE "ERROR|NOTFOUND|INVALID"; then
            warn "Erreur possible dans TSO RECEIVE:"
            echo "$_scr" | grep -iE "ERROR|NOTFOUND|INVALID" | head -3
            break
        fi
        printf "."
    done
    echo ""

    # Vérifier le résultat final
    _scr=$(s3270_cmd "Ascii()" 10 2>/dev/null || true)
    echo "$_scr" | head -10

    s3270_stop
    ok "Phase 5 terminée"
    info "Vérifier que KICKS.V1R5M0.INSTALL a été créé :"
    info "  TSO LISTDS 'KICKS.V1R5M0.INSTALL'"
}

# ============================================================
# Phase 6 : RCVKICK2 — décompresser toutes les librairies KICKS
# ============================================================
phase6_rcvkick() {
    step "Phase 6 : Décompression librairies KICKS (RCVKICK2)"

    info "RCVKICK2 se trouve dans KICKS.V1R5M0.INSTALL(V1R5M0)"
    info "On soumet un JCL wrapper avec les paramètres corrects..."

    # On ne peut pas facilement lire/éditer le membre V1R5M0 en batch.
    # On soumet un JCL qui EXEC RCVKICK2 via in-stream SYSIN avec les
    # overrides nécessaires (UID=KICKS, VOL=KICKS0).
    #
    # NOTE : Si ce JCL échoue, éditer manuellement :
    #   TSO EDIT KICKS.V1R5M0.INSTALL(V1R5M0)
    #   Modifier : UID → KICKS, VOLUMES → KICKS0, XMITIN → KICKS.V1R5M0.INSTALL
    #   Puis : SUBMIT KICKS.V1R5M0.INSTALL(V1R5M0)

    submit_cardreader "//KICKRCK2 JOB ,'KICKS RCVKICK2',CLASS=A,MSGCLASS=A,
//             MSGLEVEL=(1,1),NOTIFY=${USER},
//             USER=${USER},PASSWORD=${PASS}
//*  Wrapper RCVKICK2 — décompresse les librairies KICKS sur KICKS0
//RCVKICK2 EXEC PROC=RCVKICK2,UID='KICKS',
//             XMITIN='KICKS.V1R5M0.INSTALL'
//SYSPRINT DD   SYSOUT=A
//* Si PROC=RCVKICK2 échoue (proc non trouvée), utiliser IKJEFT01 :
//* Voir note dans script 12_kicks_install.sh phase6_rcvkick()
//"

    # RCVKICK2 n'est pas une proc cataloguée avant installation KICKS —
    # le JCL ci-dessus échoue systématiquement. On passe directement au
    # fallback TSO SUBMIT qui soumet le membre V1R5M0 directement.
    warn "EXEC PROC=RCVKICK2 non disponible avant installation — TSO SUBMIT direct..."
    _phase6_fallback_tso

    ok "Phase 6 terminée"
}

_phase6_fallback_tso() {
    info "Soumission via TSO : SUBMIT 'KICKS.V1R5M0.INSTALL(V1R5M0)'..."

    s3270_start || { warn "s3270 non disponible — étape manuelle requise"; return 1; }
    s3270_login

    # Envoyer la commande TSO SUBMIT
    s3270_cmd "String(\"SUBMIT 'KICKS.V1R5M0.INSTALL(V1R5M0)'\")" 5 >/dev/null
    s3270_cmd "Enter()" 30 >/dev/null
    sleep 5

    local screen
    screen=$(s3270_screen 2>/dev/null || echo "")
    echo "$screen" | head -5

    s3270_stop

    wait_job "RCVKICK2" 600 || true

    warn "Si RCVKICK2 échoue, éditer manuellement dans TSO :"
    warn "  EDIT 'KICKS.V1R5M0.INSTALL(V1R5M0)'"
    warn "  Modifier : UID=KICKS, VOLUMES(KICKS0), XMITIN=KICKS.V1R5M0.INSTALL"
    warn "  Soumettre : SUBMIT 'KICKS.V1R5M0.INSTALL(V1R5M0)'"
}

# ============================================================
# Phase 7 : Augmenter DYNAMNBR dans SYS1.PROCLIB(IKJACCNT)
# ============================================================
phase7_dynamnbr() {
    step "Phase 7 : TSO DYNAMNBR = 64"

    info "KICKS requiert DYNAMNBR ≥ 64 (défaut TK5 = 20)"

    # DYNAMNBR est dans SYS1.PROCLIB(IKJACCNT) :
    #   //IKJACCNT EXEC PGM=IKJEFT01,DYNAMNBR=20,...
    # Référence : Jay Moseley KICKS install guide + KICKS User's Guide 1.5.0
    submit_cardreader "//KICKDYNA JOB ,'KICKS DYNAMNBR',CLASS=A,MSGCLASS=A,
//             MSGLEVEL=(1,1),NOTIFY=${USER},
//             USER=${USER},PASSWORD=${PASS}
//UPDATE   EXEC PGM=IEBUPDTE,PARM=MOD
//SYSPRINT DD   SYSOUT=A
//SYSUT2   DD   DSN=SYS1.PROCLIB,DISP=SHR
//SYSIN    DD   DATA,DLM='!!'
./ CHANGE NAME=IKJACCNT
DYNAMNBR=20                                                     UPDATE 20
DYNAMNBR=64                                                     SEQFLD 20
!!
//"

    wait_job "KICKDYNA" 60 || warn "KICKDYNA non trouvé — vérifier si DYNAMNBR déjà à 64"
    warn "Si KICKDYNA échoue : TSO EDIT 'SYS1.PROCLIB(IKJACCNT)' et modifier DYNAMNBR manuellement"
    ok "Phase 7 terminée — se reconnecter à TSO pour appliquer"
}

# ============================================================
# Phase 8 : Mettre à jour GSTKBMS.jcl + GSTKCOMP.jcl pour KICKS
# ============================================================
phase8_jcl() {
    step "Phase 8 : Mise à jour JCL GSTK pour KICKS"

    local bms_jcl="${SCRIPT_DIR}/../jcl/GSTKBMS.jcl"
    local comp_jcl="${SCRIPT_DIR}/../jcl/GSTKCOMP.jcl"

    # --- GSTKBMS.jcl : remplacer &CICSHLQ..SDFHMAC par KICKS.KICKSSYS.V1R5M0.KICTMAC ---
    # SDFHMAC = macro lib CICS ; KICTMAC = macro lib KICKS (remplacement direct)
    # L'assembleur reste ASMA90 (disponible sur TK5)
    if grep -q "SDFHMAC" "${bms_jcl}" 2>/dev/null; then
        sed -i.bak \
            -e 's|&CICSHLQ\.\.SDFHMAC|KICKS.KICKSSYS.V1R5M0.KICTMAC|g' \
            "${bms_jcl}"
        rm -f "${bms_jcl}.bak"
        ok "GSTKBMS.jcl : &CICSHLQ..SDFHMAC → KICKS.KICKSSYS.V1R5M0.KICTMAC"
    else
        warn "GSTKBMS.jcl : aucune référence SDFHMAC (déjà à jour ?)"
    fi

    # --- GSTKCOMP.jcl : substitutions KICKS (Source : KICKS User's Guide 1.5.0) ---
    # DFHITCL   → KIKCOBCL  (proc KICKS pour OS/VS COBOL, disponible sur MVS 3.8j)
    # DFHEITALC → KIKCOBCL  (pas de DB2 sur TK5 — on utilise la proc sans precompiler)
    # SDFHCOB   → KICKS.KICKSSYS.V1R5M0.COBCOPY  (copybooks COBOL KICKS)
    # SDFHLOAD  → KICKS.KICKSSYS.V1R5M0.SKIKLOAD (load library KICKS)
    # Les DD DB2 (SDSNMACS, SDSNLOAD, PC.DBRMLIB) sont commentés (pas de DB2 sur TK5).
    if grep -q "DFHITCL\|DFHEITALC" "${comp_jcl}" 2>/dev/null; then
        sed -i.bak \
            -e 's|EXEC DFHITCL|EXEC KIKCOBCL|g' \
            -e 's|EXEC DFHEITALC|EXEC KIKCOBCL|g' \
            -e 's|&CICSHLQ\.\.SDFHCOB|KICKS.KICKSSYS.V1R5M0.COBCOPY|g' \
            -e 's|&CICSHLQ\.\.SDFHLOAD|KICKS.KICKSSYS.V1R5M0.SKIKLOAD|g' \
            -e 's|^//.*SDSNMACS.*$|//* &  (DB2 non disponible sur TK5)|g' \
            -e 's|^//.*SDSNLOAD.*$|//* &  (DB2 non disponible sur TK5)|g' \
            -e 's|^//PC\.DBRMLIB.*$|//* &  (DB2 non disponible sur TK5)|g' \
            "${comp_jcl}"
        rm -f "${comp_jcl}.bak"
        ok "GSTKCOMP.jcl : DFHITCL/DFHEITALC → KIKCOBCL, COBCOPY, SKIKLOAD, DB2 commenté"
    else
        warn "GSTKCOMP.jcl : aucune référence DFHITCL/DFHEITALC (déjà à jour ?)"
    fi
    warn "Note : si le compilateur TK5 est COBOL II, remplacer KIKCOBCL par KIKCB2CL"

    ok "Phase 8 terminée"
    echo ""
    echo "  Fichier modifié : scripts/jcl/GSTKBMS.jcl"
    echo ""
    echo "  Pour démarrer KICKS après installation complète :"
    echo "    TSO: EXEC 'KICKS.KICKSSYS.V1R5M0.CLIST(KICKS)'"
    echo "    (ou via s3270 : bash scripts/mvs/13_kicks_start.sh)"
}


# ============================================================
# Afficher l'état KICKS sur MVS
# ============================================================
kicks_status() {
    echo "=== ÉTAT KICKS sur MVS TK5 ==="
    echo ""
    echo "--- Volume KICKS0 ---"
    herc_cmd "devstat ${KICKS_DEVADDR}" || echo "  (périphérique non disponible)"
    echo ""
    echo "--- Syslog récent ---"
    herc_syslog 30 | grep -iE "KICKS|KICK|KICS|CICS|HASP|IEF" | tail -15 || echo "  (rien de notable)"
    echo ""
    echo "--- DASD Docker ---"
    docker exec "${DOCKER_CONTAINER}" ls -lh "${KICKS_DASD_DOCKER}" 2>/dev/null \
        || echo "  (DASD non créé)"
}

# ============================================================
# Main
# ============================================================
echo "=============================================="
echo " KICKS v1.5.0 Installation — MVS TK5"
echo " Container : ${DOCKER_CONTAINER}"
echo " Volume    : ${KICKS_VOLSER} @ ${KICKS_DEVADDR}"
echo "=============================================="
echo ""

trap 's3270_stop 2>/dev/null; true' EXIT

PHASES=("$@")
[[ ${#PHASES[@]} -eq 0 ]] && PHASES=("help")

for phase in "${PHASES[@]}"; do
    case "$phase" in
        all)
            phase1_dasd
            phase2_ickdsf
            phase3_catalog
            phase4_xmi
            phase5_recv
            phase6_rcvkick
            phase7_dynamnbr
            phase8_jcl
            echo ""
            ok "=== Installation KICKS terminée ==="
            echo ""
            echo "Prochaine étape :"
            echo "  bash scripts/mvs/13_kicks_start.sh"
            echo "  ou via TSO : EXEC 'KICKS.KICKSSYS.V1R5M0.CLIST(KICKS)'"
            ;;
        dasd)    phase1_dasd ;;
        ickdsf)  phase2_ickdsf ;;
        catalog) phase3_catalog ;;
        xmi)     phase4_xmi ;;
        recv)    phase5_recv ;;
        rcvkick) phase6_rcvkick ;;
        dynamnbr) phase7_dynamnbr ;;
        jcl)     phase8_jcl ;;
        status)  kicks_status ;;
        help|*)
            cat <<HELP
Usage: bash scripts/mvs/12_kicks_install.sh <phase> [phase2] ...

Phases disponibles :
  all       Toutes les phases dans l'ordre (installation complète)
  dasd      Créer volume KICKS0 + attacher Hercules
  ickdsf    Formater le volume avec ICKDSF
  catalog   Créer catalogue UCKICKS0 + alias KICKS
  xmi       Télécharger KICKS XMI + préparer lecteur de cartes
  recv      Attendre job KICKRECV (RECV370)
  rcvkick   Décompresser librairies KICKS (RCVKICK2)
  dynamnbr  Augmenter DYNAMNBR=64 dans TSO
  jcl       Mettre à jour GSTKBMS.jcl + GSTKCOMP.jcl

Utilitaires :
  status    Afficher l'état KICKS sur MVS

Exemple :
  bash scripts/mvs/12_kicks_install.sh all
  bash scripts/mvs/12_kicks_install.sh dasd ickdsf catalog
HELP
            ;;
    esac
done
