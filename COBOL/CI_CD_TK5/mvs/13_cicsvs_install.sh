#!/usr/bin/env bash
# ============================================================
# 13_cicsvs_install.sh — Installation CICS/VS 1.7 sur MVS TK5
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-03
#
# CICS/VS 1.7 est le CICS IBM d'origine pour MVS 3.8j.
# Contrairement à KICKS (qui tourne sous TSO), CICS/VS 1.7
# s'exécute comme une région VTAM (Started Task — STC).
#
# Source : bitsavers.org (images bandes IBM CICS/VS 1.7)
# Référence communauté : Jay Moseley, H390-MVS mailing list
#
# ╔══════════════════════════════════════════════════════════╗
# ║  AVERTISSEMENT — PROJET AVANCÉ                           ║
# ║  Durée estimée : plusieurs jours                         ║
# ║  Difficulté : élevée (VTAM, SIT, tables assemblées)      ║
# ║  Pour du développement courant, utiliser KICKS :         ║
# ║    bash mvs/12_kicks_install.sh all                      ║
# ╚══════════════════════════════════════════════════════════╝
#
# Différences clés avec KICKS :
#   KICKS   — tourne sous TSO (EXEC KICKS), pas de STC
#   CICS/VS — tourne comme région VTAM (STC), vrais VTAM terminals
#
#   KICKS   — ressources définies par commandes CEDA/CEMT interactives
#   CICS/VS — ressources définies par tables assemblées (PCT, PPT, TCT, FCT)
#              (RDO/CEDA n'existe qu'à partir de CICS 2.1)
#
# Phases :
#   1  check     Vérifier les prérequis (VTAM, tape, espace DASD)
#   2  dasd      Créer volume CICS0 (3350) + attacher Hercules
#   3  tape      Charger image bande CICS/VS 1.7 → DASD MVS
#   4  vtam      Définir APPL VTAM + entrées terminaux CICS
#   5  sit       Assembler SIT (System Initialization Table)
#   6  tables    Assembler tables CICS (PCT, PPT, TCT, FCT)
#   7  stc       Installer région CICS dans SYS1.PROCLIB
#   8  start     Démarrer la région CICS + vérifier
#   status        Afficher l'état de la région CICS
#
# Usage :
#   bash mvs/13_cicsvs_install.sh check
#   bash mvs/13_cicsvs_install.sh all
#   bash mvs/13_cicsvs_install.sh check dasd tape
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CI_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

source "${SCRIPT_DIR}/s3270_lib.sh"

# ============================================================
# Configuration
# ============================================================
DOCKER_CONTAINER="${DOCKER_CONTAINER:-mvs-tk5}"
HERC_URL="${HERC_URL:-http://localhost:8038}"
TSO_USER="${TSO_USER:-HERC02}"
TSO_PASS="${TSO_PASS:-CUL8TR}"
HLQ="${HLQ:-HERC02}"

# Volume DASD dédié CICS/VS
CICS_VOLSER="CICS0"
CICS_DEVADDR="0352"          # 0352 = premier slot libre après KICKS0@0351
CICS_DEVTYPE="3350"
CICS_DASD_DOCKER="/opt/tk5/dasd/cics0.3350"
CICS_DASD_REL="dasd/cics0.3350"

# Identifiants CICS région
CICS_APPLID="${CICS_APPLID:-CICS01}"     # VTAM APPLID de la région CICS
CICS_SYSID="${CICS_SYSID:-CICS}"        # SYSIDNT (4 chars max)
CICS_HLQ="${CICS_HLQ:-CICS17}"         # HLQ des datasets CICS/VS installés

# Fichier image bande CICS/VS 1.7 (à obtenir sur bitsavers.org)
# La bande est distribuée comme fichier AWS (Automated Tape emulator) ou HET.
CICS_TAPE_LOCAL="${CICS_TAPE_LOCAL:-/tmp/cicsvs17.aws}"

# ============================================================
# Couleurs / helpers
# ============================================================
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'

ok()   { echo -e "${GREEN}✓${NC} $*"; }
fail() { echo -e "${RED}✗ ERREUR :${NC} $*" >&2; }
warn() { echo -e "${YELLOW}⚠${NC}  $*"; }
info() { echo -e "${CYAN}ℹ${NC}  $*"; }
step() { echo -e "\n${BOLD}=== $* ===${NC}"; }
note() { echo -e "${YELLOW}NOTE :${NC} $*"; }

# ============================================================
# Helpers Hercules / JES2
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
    local jcl="$1"
    local _tmp; _tmp=$(mktemp /tmp/cicsvs_jcl.XXXXXX)
    printf '%s\n' "$jcl" > "${_tmp}"
    docker exec -i "${DOCKER_CONTAINER}" bash -c \
        "exec 3<>/dev/tcp/127.0.0.1/3505; cat >&3; exec 3>&-" \
        < "${_tmp}" 2>/dev/null \
        && ok "JCL envoyé au lecteur de cartes" \
        || { rm -f "${_tmp}"; fail "Échec envoi lecteur de cartes"; return 1; }
    rm -f "${_tmp}"
}

wait_job() {
    local jobname="$1"
    local maxwait="${2:-180}"
    local elapsed=0
    printf "  Attente %s " "$jobname"
    while [[ $elapsed -lt $maxwait ]]; do
        sleep 5; elapsed=$(( elapsed + 5 )); printf "."
        local sl; sl=$(herc_syslog 80)
        if echo "$sl" | grep -qi "HASP395.*${jobname}\|${jobname}.*HASP395"; then
            echo " OK (${elapsed}s)"
            echo "$sl" | grep -qi "ABEND\|HASP396.*${jobname}" \
                && warn "ABEND détecté — vérifier : bash mvs/herc.sh log 60"
            return 0
        fi
        if echo "$sl" | grep -qi "IEF452I.*${jobname}\|IEF453I.*${jobname}"; then
            echo " ERREUR JCL"; return 1
        fi
    done
    echo " timeout (${maxwait}s)"
    return 1
}

# ============================================================
# Phase 1 : Vérification des prérequis
# ============================================================
phase1_check() {
    step "Phase 1 : Vérification des prérequis"

    local ok_all=1

    # Docker
    if ! docker info &>/dev/null 2>&1; then
        fail "Docker daemon non disponible"
        ok_all=0
    else
        ok "Docker OK"
    fi

    # Container mvs-tk5 running
    local state
    state=$(docker inspect --format '{{.State.Status}}' "${DOCKER_CONTAINER}" 2>/dev/null || echo "absent")
    if [[ "$state" == "running" ]]; then
        ok "Container ${DOCKER_CONTAINER} — running"
    else
        fail "Container ${DOCKER_CONTAINER} — ${state}"
        ok_all=0
    fi

    # VTAM actif (chercher IST dans le syslog)
    if herc_syslog 100 | grep -q "IST"; then
        ok "VTAM actif (IST messages présents dans syslog)"
    else
        warn "VTAM : aucun message IST récent — vérifier que VTAM est démarré"
        info "  Dans la console Hercules : start net"
    fi

    # Espace DASD (besoin ~200 cylinders 3350 pour CICS/VS)
    info "Volume CICS0 (${CICS_DEVADDR}) :"
    if docker exec "${DOCKER_CONTAINER}" test -f "${CICS_DASD_DOCKER}" 2>/dev/null; then
        ok "DASD ${CICS_DASD_DOCKER} déjà présent"
    else
        info "DASD non encore créé — sera créé en phase 2"
    fi

    # Image bande CICS/VS 1.7
    echo ""
    note "Image bande CICS/VS 1.7"
    if [[ -f "${CICS_TAPE_LOCAL}" ]]; then
        ok "Image bande trouvée : ${CICS_TAPE_LOCAL} ($(du -h "${CICS_TAPE_LOCAL}" | cut -f1))"
    else
        fail "Image bande absente : ${CICS_TAPE_LOCAL}"
        ok_all=0
        echo ""
        echo "  Pour obtenir CICS/VS 1.7 :"
        echo "  1. Aller sur https://bitsavers.org/bits/IBM/cics/"
        echo "     ou https://bitsavers.org/bits/IBM/System_370/OS_MVS/"
        echo "  2. Télécharger l'image de distribution CICS/VS 1.7"
        echo "     (format AWS ou HET — fichier *.aws ou *.het)"
        echo "  3. La placer ici : ${CICS_TAPE_LOCAL}"
        echo "     ou surcharger : export CICS_TAPE_LOCAL=/chemin/vers/cicsvs17.aws"
        echo ""
        echo "  Référence communauté (hobbyists MVS) :"
        echo "  - H390-MVS mailing list / Yahoo Groups archives"
        echo "  - Forum cbttape.org + Jay Moseley CICS/VS install guide"
        echo "  - https://www.jaymoseley.com/hercules/"
    fi

    echo ""
    if [[ $ok_all -eq 1 ]]; then
        ok "Tous les prérequis sont satisfaits — continuer avec la phase 2"
    else
        warn "Des prérequis sont manquants — corriger avant de continuer"
    fi
}

# ============================================================
# Phase 2 : Volume DASD CICS0
# ============================================================
phase2_dasd() {
    step "Phase 2 : Volume DASD CICS0 (${CICS_DEVADDR})"

    if docker exec "${DOCKER_CONTAINER}" test -f "${CICS_DASD_DOCKER}" 2>/dev/null; then
        ok "DASD ${CICS_DASD_DOCKER} existe déjà"
    else
        info "Création DASD ${CICS_DEVTYPE} (200 cylinders, VOLSER ${CICS_VOLSER})..."
        docker exec "${DOCKER_CONTAINER}" \
            /opt/hercules/bin/dasdinit -z "${CICS_DASD_DOCKER}" "${CICS_DEVTYPE}" "${CICS_VOLSER}" \
            && ok "DASD créé" \
            || { fail "dasdinit échoué"; return 1; }
    fi

    # Ajouter à usr_dasd.cnf
    local cfg_line="${CICS_DEVADDR} ${CICS_DEVTYPE} ${CICS_DASD_REL}"
    if docker exec "${DOCKER_CONTAINER}" grep -q "${CICS_DEVADDR}" \
            /opt/tk5/dasd.usr/usr_dasd.cnf 2>/dev/null; then
        ok "Périphérique ${CICS_DEVADDR} déjà dans usr_dasd.cnf"
    else
        docker exec "${DOCKER_CONTAINER}" bash -c \
            "echo '${cfg_line}' >> /opt/tk5/dasd.usr/usr_dasd.cnf"
        ok "Ajouté à usr_dasd.cnf : ${cfg_line}"
    fi

    # Attacher dynamiquement à Hercules
    info "Attachement Hercules du périphérique ${CICS_DEVADDR}..."
    herc_cmd "attach ${CICS_DEVADDR} ${CICS_DEVTYPE} ${CICS_DASD_REL}" >/dev/null 2>&1 || true
    sleep 2
    herc_cmd "devstat ${CICS_DEVADDR}" || true

    # Formater VTOC
    submit_cardreader "//CICSDSFJ JOB ,'CICS ICKDSF',CLASS=A,MSGCLASS=A,
//             MSGLEVEL=(1,1),NOTIFY=${TSO_USER},
//             USER=${TSO_USER},PASSWORD=${TSO_PASS}
//ICKDSF   EXEC PGM=ICKDSF,REGION=4096K
//SYSPRINT DD   SYSOUT=A
//SYSIN    DD   *
  INIT UNITADDRESS(${CICS_DEVADDR}) NOVERIFY VOLID(${CICS_VOLSER}) -
       VTOC(0,1,14) PURGE INDEX(1)
/*
//"

    wait_job "CICSDSFJ" 180
    ok "Phase 2 terminée — volume ${CICS_VOLSER} formaté"
}

# ============================================================
# Phase 3 : Restauration bande CICS/VS 1.7 sur DASD
# ============================================================
phase3_tape() {
    step "Phase 3 : Restauration bande CICS/VS 1.7"

    if [[ ! -f "${CICS_TAPE_LOCAL}" ]]; then
        fail "Image bande absente : ${CICS_TAPE_LOCAL}"
        info "Voir la phase 1 (check) pour les instructions d'obtention"
        return 1
    fi

    info "Image bande : ${CICS_TAPE_LOCAL} ($(du -h "${CICS_TAPE_LOCAL}" | cut -f1))"
    info "Format attendu : AWS (*.aws) ou HET (*.het)"
    echo ""

    # Copier l'image bande dans le container
    info "Copie de l'image bande dans le container..."
    docker cp "${CICS_TAPE_LOCAL}" "${DOCKER_CONTAINER}:/tmp/cicsvs17.aws" \
        && ok "Image copiée dans le container" \
        || { fail "docker cp échoué"; return 1; }

    # Attacher la bande virtuelle à Hercules (unité 0380 = première unité bande)
    info "Attachement bande virtuelle (unit 0380)..."
    herc_cmd "devinit 0380 /tmp/cicsvs17.aws awstape" || true
    sleep 2

    # JCL IEHDASDR pour restaurer la bande vers le volume CICS0
    # IEHDASDR est le restore utility IBM standard pour les bandes de distribution.
    # La structure exacte des labels/files dépend de la version de la bande.
    warn "La structure de la bande varie selon la source (bitsavers / CBT Tape)."
    warn "Vérifier le contenu de la bande avec IEBPTPCH si la restauration échoue."

    submit_cardreader "//CICSRSTR JOB ,'CICS RESTORE',CLASS=A,MSGCLASS=A,
//             MSGLEVEL=(1,1),NOTIFY=${TSO_USER},
//             USER=${TSO_USER},PASSWORD=${TSO_PASS}
//*  Restauration CICS/VS 1.7 depuis bande AWS → volume CICS0
//*  Si IEHDASDR échoue, utiliser IEBCOPY programme par programme
//*  Voir note dans 13_cicsvs_install.sh phase3_tape()
//RESTORE  EXEC PGM=IEHDASDR,REGION=4096K
//SYSPRINT DD   SYSOUT=A
//TAPE     DD   UNIT=0380,DISP=(OLD,PASS),
//              DSN=CICSVS17.DISTRIB,
//              DCB=(RECFM=FB,LRECL=80,BLKSIZE=3200)
//DASD     DD   UNIT=3350,VOL=SER=${CICS_VOLSER},DISP=SHR
//SYSIN    DD   *
  RESTORE INDD=TAPE,OUTDD=DASD
/*
//"

    wait_job "CICSRSTR" 600 || {
        warn "IEHDASDR n'a pas complété dans les délais"
        warn ""
        warn "Alternatives si IEHDASDR échoue :"
        warn "  1. Utiliser TAPEMAP pour lister la structure de la bande :"
        warn "     bash mvs/herc.sh mvs 'devinit 0380 /tmp/cicsvs17.aws awstape'"
        warn "     Puis soumettre TAPEMAP job (voir CBT Tape file 299)"
        warn ""
        warn "  2. Restaurer manuellement fichier par fichier avec IEBCOPY"
        warn "     en identifiant les PDSs sur la bande"
        warn ""
        warn "  3. Consulter : https://www.jaymoseley.com/hercules/ (section CICS/VS)"
        return 1
    }

    ok "Phase 3 terminée — CICS/VS 1.7 restauré sur ${CICS_VOLSER}"
    info "Vérifier les datasets créés :"
    info "  TSO LISTCAT VOLUME(${CICS_VOLSER})"
}

# ============================================================
# Phase 4 : Définitions VTAM (APPL + terminaux CICS)
# ============================================================
phase4_vtam() {
    step "Phase 4 : Définitions VTAM pour CICS/VS"

    # CICS/VS tourne comme VTAM application (APPL), pas sous TSO.
    # Chaque terminal 3270 accédant à CICS doit avoir une définition VTAM.
    #
    # Structure VTAM nécessaire :
    #   SYS1.VTAMLST(CICSMJR)  — major node : APPL + terminaux
    #   puis : V NET,ACT,ID=CICSMJR  dans la console MVS
    #
    # Nommage des terminaux :
    #   CICSAPPL = APPLID de la région (=${CICS_APPLID})
    #   CICSLUxx = LU names des terminaux 3270 CICS

    info "Génération des définitions VTAM dans SYS1.VTAMLST(CICSMJR)..."

    # Contenu du major node VTAM pour CICS/VS
    # APPL : définit la région CICS comme application VTAM
    # TERMINAL : définit les LU 3270 qui peuvent se connecter à CICS
    local vtam_defs
    vtam_defs="CICSMJR  VBUILD TYPE=APPL
${CICS_APPLID}  APPL  ACBNAME=${CICS_APPLID},AUTH=(ACQ),
               MODETAB=ISTINCLM,DLOGMOD=D4C32XX3
*
* Terminaux 3270 pour CICS/VS
* CICS accède aux LUs via son TCT (Terminal Control Table)
CICST001 APPL  ACBNAME=CICST001,AUTH=(PASS),MODETAB=ISTINCLM
CICST002 APPL  ACBNAME=CICST002,AUTH=(PASS),MODETAB=ISTINCLM
CICST003 APPL  ACBNAME=CICST003,AUTH=(PASS),MODETAB=ISTINCLM
CICST004 APPL  ACBNAME=CICST004,AUTH=(PASS),MODETAB=ISTINCLM"

    # Écrire les définitions dans SYS1.VTAMLST via IEBUPDTE
    submit_cardreader "//CICSVTAM JOB ,'CICS VTAM DEFS',CLASS=A,MSGCLASS=A,
//             MSGLEVEL=(1,1),NOTIFY=${TSO_USER},
//             USER=${TSO_USER},PASSWORD=${TSO_PASS}
//*  Création SYS1.VTAMLST(CICSMJR) — major node CICS/VS
//UPDATE   EXEC PGM=IEBUPDTE,PARM=NEW
//SYSPRINT DD   SYSOUT=A
//SYSUT2   DD   DSN=SYS1.VTAMLST,DISP=SHR
//SYSIN    DD   DATA,DLM='!!'
./ ADD NAME=CICSMJR
CICSMJR  VBUILD TYPE=APPL
${CICS_APPLID}  APPL  ACBNAME=${CICS_APPLID},AUTH=(ACQ),
               MODETAB=ISTINCLM,DLOGMOD=D4C32XX3
CICST001 APPL  ACBNAME=CICST001,AUTH=(PASS),MODETAB=ISTINCLM
CICST002 APPL  ACBNAME=CICST002,AUTH=(PASS),MODETAB=ISTINCLM
CICST003 APPL  ACBNAME=CICST003,AUTH=(PASS),MODETAB=ISTINCLM
CICST004 APPL  ACBNAME=CICST004,AUTH=(PASS),MODETAB=ISTINCLM
!!
//"

    wait_job "CICSVTAM" 60

    # Activer le major node VTAM
    info "Activation du major node VTAM CICSMJR..."
    herc_cmd "/${TSO_USER} V NET,ACT,ID=CICSMJR" 2>/dev/null || true
    sleep 3

    # Vérifier dans le syslog
    local sl; sl=$(herc_syslog 20)
    if echo "$sl" | grep -q "IST097I\|IST093I\|CICSMJR"; then
        ok "Major node CICSMJR activé (messages IST dans syslog)"
    else
        warn "Major node non confirmé — vérifier syslog : bash mvs/herc.sh log 30"
        warn "Commande manuelle : V NET,ACT,ID=CICSMJR  (console MVS)"
    fi

    ok "Phase 4 terminée"
    note "Les LU names des terminaux (CICST001–CICST004) doivent correspondre"
    note "exactement aux noms définis dans la TCT CICS (phase 6)"
}

# ============================================================
# Phase 5 : Assemblage SIT (System Initialization Table)
# ============================================================
phase5_sit() {
    step "Phase 5 : Assemblage SIT (System Initialization Table)"

    # La SIT est la table maîtresse de configuration CICS/VS.
    # Elle est assemblée avec la macro DFHSIT et link-éditée dans la LOADLIB CICS.
    # Paramètres clés pour MVS TK5 :
    #   APPLID  — VTAM APPLID de la région
    #   SYSIDNT — identifiant 4-chars de la région
    #   MN      — message numbers (oui = avec numéros)
    #   TRMIDNT — non pour MVS/VTAM (utiliser TCT)
    #   AMXT    — max active tasks
    #   CMXT    — max concurrent transactions

    info "Génération et assemblage de la SIT CICS/VS..."
    info "APPLID=${CICS_APPLID}, SYSIDNT=${CICS_SYSID}"

    # Source assembleur SIT → HERC02.CICS.SIT.SOURCE temporairement
    submit_cardreader "//CICSSIT  JOB ,'CICS SIT ASSY',CLASS=A,MSGCLASS=A,
//             MSGLEVEL=(1,1),NOTIFY=${TSO_USER},
//             USER=${TSO_USER},PASSWORD=${TSO_PASS}
//*  Assemblage SIT CICS/VS 1.7
//*  Référence : CICS/VS 1.7 System Programmer Reference (SC33-0171)
//SITASM   EXEC PGM=ASMA90,
//              PARM='DECK,NOOBJECT',
//              REGION=2048K
//SYSLIB   DD   DSN=${CICS_HLQ}.SDFHMAC,DISP=SHR
//SYSPRINT DD   SYSOUT=A
//PUNCH    DD   DSN=${HLQ}.CICS.SIT.OBJ,DISP=(NEW,PASS),
//              UNIT=SYSDA,SPACE=(CYL,(1,1))
//SYSIN    DD   *
DFHSIT   DFHSIT TYPE=INITIAL,                                          C
               APPLID=${CICS_APPLID},                                  C
               SYSIDNT=${CICS_SYSID},                                  C
               GRPLIST=DFHLIST,                                        C
               AMXT=5,                                                 C
               CMXT=5,                                                 C
               MN=YES,                                                 C
               TRMIDNT=NO,                                             C
               START=COLD
         END
/*
//SITLKED  EXEC PGM=IEWL,
//              PARM='XREF,LIST,NCAL',
//              REGION=512K
//SYSLIN   DD   DSN=${HLQ}.CICS.SIT.OBJ,DISP=(OLD,DELETE)
//SYSPRINT DD   SYSOUT=A
//SYSLMOD  DD   DSN=${CICS_HLQ}.SDFHLOAD(DFHSIT),DISP=SHR
//SYSUT1   DD   UNIT=SYSDA,SPACE=(CYL,(1,1))
//"

    wait_job "CICSSIT" 120

    ok "Phase 5 terminée"
    warn "Si l'assemblage échoue (SDFHMAC introuvable) :"
    warn "  Ajuster CICS_HLQ selon les datasets réels restaurés depuis la bande"
    warn "  TSO LISTCAT ENT('${CICS_HLQ}.SDFHMAC') pour vérifier"
}

# ============================================================
# Phase 6 : Assemblage tables CICS (PCT, PPT, TCT, FCT)
# ============================================================
phase6_tables() {
    step "Phase 6 : Assemblage tables CICS/VS (PCT, PPT, TCT, FCT)"

    # CICS/VS 1.7 utilise des tables assemblées (pas de RDO/CEDA).
    # Ces tables définissent :
    #   PCT — Program Control Table  : transactions (ex: CESN, CEMT, CESF + custom)
    #   PPT — Processing Program Table : programmes (load lib + lang)
    #   TCT — Terminal Control Table   : terminaux 3270
    #   FCT — File Control Table       : fichiers VSAM (si utilisés)
    #
    # Référence : CICS/VS 1.7 Resource Definition Guide (SC33-0166)

    info "Assemblage tables CICS/VS (PCT + PPT + TCT + FCT)..."
    warn "Ce JCL crée des tables minimales pour démarrage initial."
    warn "Ajouter les transactions/programmes projet dans les membres source."

    # ---- Table PCT (transactions) ----
    submit_cardreader "//CICSPCT  JOB ,'CICS PCT ASSY',CLASS=A,MSGCLASS=A,
//             MSGLEVEL=(1,1),NOTIFY=${TSO_USER},
//             USER=${TSO_USER},PASSWORD=${TSO_PASS}
//PCTASM   EXEC PGM=ASMA90,PARM='DECK,NOOBJECT',REGION=2048K
//SYSLIB   DD   DSN=${CICS_HLQ}.SDFHMAC,DISP=SHR
//SYSPRINT DD   SYSOUT=A
//PUNCH    DD   DSN=${HLQ}.CICS.PCT.OBJ,DISP=(NEW,PASS),
//              UNIT=SYSDA,SPACE=(CYL,(1,1))
//SYSIN    DD   *
* PCT — Program Control Table CICS/VS 1.7
* Inclut les transactions CICS standard + stub pour transactions projet
DFHPCT   DFHPCT TYPE=INITIAL
*
* Transactions CICS internes (toujours inclure)
         DFHPCT TYPE=ENTRY,TRANSID=CESN,PROGRAM=DFHSIGN,TWASIZE=0
         DFHPCT TYPE=ENTRY,TRANSID=CESF,PROGRAM=DFHSNEP,TWASIZE=0
         DFHPCT TYPE=ENTRY,TRANSID=CEMT,PROGRAM=DFHEMTP,TWASIZE=0
         DFHPCT TYPE=ENTRY,TRANSID=CEDF,PROGRAM=DFHEDFP,TWASIZE=0
         DFHPCT TYPE=ENTRY,TRANSID=CECI,PROGRAM=DFHECIP,TWASIZE=0
         DFHPCT TYPE=ENTRY,TRANSID=CMSG,PROGRAM=DFHMSGP,TWASIZE=0
*
* Transactions GSTK — adapter les TRANSID/PROGRAM pour le projet
*        DFHPCT TYPE=ENTRY,TRANSID=G000,PROGRAM=GSTK000,TWASIZE=0
*        DFHPCT TYPE=ENTRY,TRANSID=G001,PROGRAM=GSTK001,TWASIZE=0
*
         DFHPCT TYPE=FINAL
         END
/*
//PCTLKED  EXEC PGM=IEWL,PARM='XREF,LIST,NCAL',REGION=512K
//SYSLIN   DD   DSN=${HLQ}.CICS.PCT.OBJ,DISP=(OLD,DELETE)
//SYSPRINT DD   SYSOUT=A
//SYSLMOD  DD   DSN=${CICS_HLQ}.SDFHLOAD(DFHPCT),DISP=SHR
//SYSUT1   DD   UNIT=SYSDA,SPACE=(CYL,(1,1))
//"
    wait_job "CICSPCT" 120

    # ---- Table PPT (programmes) ----
    submit_cardreader "//CICSPPT  JOB ,'CICS PPT ASSY',CLASS=A,MSGCLASS=A,
//             MSGLEVEL=(1,1),NOTIFY=${TSO_USER},
//             USER=${TSO_USER},PASSWORD=${TSO_PASS}
//PPTASM   EXEC PGM=ASMA90,PARM='DECK,NOOBJECT',REGION=2048K
//SYSLIB   DD   DSN=${CICS_HLQ}.SDFHMAC,DISP=SHR
//SYSPRINT DD   SYSOUT=A
//PUNCH    DD   DSN=${HLQ}.CICS.PPT.OBJ,DISP=(NEW,PASS),
//              UNIT=SYSDA,SPACE=(CYL,(1,1))
//SYSIN    DD   *
* PPT — Processing Program Table CICS/VS 1.7
DFHPPT   DFHPPT TYPE=INITIAL
*
* Programmes CICS internes
         DFHPPT TYPE=ENTRY,PROGRAM=DFHSIGN,PGMLANG=ASSEMBLER
         DFHPPT TYPE=ENTRY,PROGRAM=DFHSNEP,PGMLANG=ASSEMBLER
         DFHPPT TYPE=ENTRY,PROGRAM=DFHEMTP,PGMLANG=ASSEMBLER
         DFHPPT TYPE=ENTRY,PROGRAM=DFHEDFP,PGMLANG=ASSEMBLER
         DFHPPT TYPE=ENTRY,PROGRAM=DFHECIP,PGMLANG=ASSEMBLER
         DFHPPT TYPE=ENTRY,PROGRAM=DFHMSGP,PGMLANG=ASSEMBLER
*
* Programmes GSTK — adapter pour le projet
*        DFHPPT TYPE=ENTRY,PROGRAM=GSTK000,PGMLANG=COBOL
*        DFHPPT TYPE=ENTRY,PROGRAM=GSTK001,PGMLANG=COBOL
*
         DFHPPT TYPE=FINAL
         END
/*
//PPTLKED  EXEC PGM=IEWL,PARM='XREF,LIST,NCAL',REGION=512K
//SYSLIN   DD   DSN=${HLQ}.CICS.PPT.OBJ,DISP=(OLD,DELETE)
//SYSPRINT DD   SYSOUT=A
//SYSLMOD  DD   DSN=${CICS_HLQ}.SDFHLOAD(DFHPPT),DISP=SHR
//SYSUT1   DD   UNIT=SYSDA,SPACE=(CYL,(1,1))
//"
    wait_job "CICSPPT" 120

    # ---- Table TCT (terminaux 3270) ----
    submit_cardreader "//CICSTCT  JOB ,'CICS TCT ASSY',CLASS=A,MSGCLASS=A,
//             MSGLEVEL=(1,1),NOTIFY=${TSO_USER},
//             USER=${TSO_USER},PASSWORD=${TSO_PASS}
//TCTASM   EXEC PGM=ASMA90,PARM='DECK,NOOBJECT',REGION=2048K
//SYSLIB   DD   DSN=${CICS_HLQ}.SDFHMAC,DISP=SHR
//SYSPRINT DD   SYSOUT=A
//PUNCH    DD   DSN=${HLQ}.CICS.TCT.OBJ,DISP=(NEW,PASS),
//              UNIT=SYSDA,SPACE=(CYL,(1,1))
//SYSIN    DD   *
* TCT — Terminal Control Table CICS/VS 1.7
* Les TERMID correspondent aux ACBNAME définis dans VTAM (phase 4)
DFHTCT   DFHTCT TYPE=INITIAL
*
         DFHTCT TYPE=TERMINAL,ACCMETH=VTAM,                           C
               TRMTYPE=L3270,TRMIDNT=T001,                            C
               NETNAME=CICST001
         DFHTCT TYPE=TERMINAL,ACCMETH=VTAM,                           C
               TRMTYPE=L3270,TRMIDNT=T002,                            C
               NETNAME=CICST002
         DFHTCT TYPE=TERMINAL,ACCMETH=VTAM,                           C
               TRMTYPE=L3270,TRMIDNT=T003,                            C
               NETNAME=CICST003
         DFHTCT TYPE=TERMINAL,ACCMETH=VTAM,                           C
               TRMTYPE=L3270,TRMIDNT=T004,                            C
               NETNAME=CICST004
*
         DFHTCT TYPE=FINAL
         END
/*
//TCTLKED  EXEC PGM=IEWL,PARM='XREF,LIST,NCAL',REGION=512K
//SYSLIN   DD   DSN=${HLQ}.CICS.TCT.OBJ,DISP=(OLD,DELETE)
//SYSPRINT DD   SYSOUT=A
//SYSLMOD  DD   DSN=${CICS_HLQ}.SDFHLOAD(DFHTCT),DISP=SHR
//SYSUT1   DD   UNIT=SYSDA,SPACE=(CYL,(1,1))
//"
    wait_job "CICSTCT" 120

    ok "Phase 6 terminée — tables PCT, PPT, TCT assemblées"
    note "Pour ajouter les programmes GSTK, décommenter les lignes marquées"
    note "dans les JCLs générés et relancer cette phase"
}

# ============================================================
# Phase 7 : STC CICS dans SYS1.PROCLIB
# ============================================================
phase7_stc() {
    step "Phase 7 : Installation Started Task CICS dans SYS1.PROCLIB"

    # La région CICS/VS tourne comme STC, pas comme job interactif.
    # Le JCL PROC définit les DDs requis :
    #   DFHCSD    — CICS CSD (pas utilisé en CICS/VS 1.7, laisser vide)
    #   DFHSTAT   — statistiques CICS
    #   DFHRPL    — LOADLIB contenant les programmes CICS + applications
    #   SYSPRINT  — messages CICS
    #   SYSMDUMP  — dump
    # La SIT, PCT, PPT, TCT sont dans SDFHLOAD (DD DFHRPL).

    info "Création SYS1.PROCLIB(${CICS_APPLID}) — JCL de la région CICS..."

    submit_cardreader "//CICSJCL  JOB ,'CICS STC JCL',CLASS=A,MSGCLASS=A,
//             MSGLEVEL=(1,1),NOTIFY=${TSO_USER},
//             USER=${TSO_USER},PASSWORD=${TSO_PASS}
//*  Installe le PROC CICS dans SYS1.PROCLIB
//UPDATE   EXEC PGM=IEBUPDTE,PARM=NEW
//SYSPRINT DD   SYSOUT=A
//SYSUT2   DD   DSN=SYS1.PROCLIB,DISP=SHR
//SYSIN    DD   DATA,DLM='!!'
./ ADD NAME=${CICS_APPLID}
//* ============================================================
//* Région CICS/VS 1.7 — ${CICS_APPLID} (SYSIDNT=${CICS_SYSID})
//* Auteur  : Sebastien Cotillard
//* Démarrage : S ${CICS_APPLID}  (console MVS)
//* Arrêt     : CEMT PERFORM SHUTDOWN  (terminal CICS)
//*             ou : P ${CICS_APPLID}  (console MVS — shutdown brutal)
//* ============================================================
//${CICS_APPLID} PROC
//DFHSIP   EXEC PGM=DFHSIP,TIME=1440,REGION=4096K,
//              PARM='SIT=6,APPLID=${CICS_APPLID}'
//STEPLIB  DD   DSN=${CICS_HLQ}.SDFHLOAD,DISP=SHR
//DFHRPL   DD   DSN=${CICS_HLQ}.SDFHLOAD,DISP=SHR
//*             Ajouter LOADLIB des programmes applicatifs si besoin :
//*         DD  DSN=${HLQ}.GSTK.LOADLIB,DISP=SHR
//DFHCSD   DD   DUMMY
//DFHSTAT  DD   DSN=${HLQ}.CICS.STAT,DISP=(NEW,CATLG),
//              UNIT=SYSDA,SPACE=(CYL,(5,2)),
//              DCB=(RECFM=VB,LRECL=260,BLKSIZE=2640)
//SYSPRINT DD   SYSOUT=A
//SYSMDUMP DD   SYSOUT=A
//  PEND
!!
//"

    wait_job "CICSJCL" 60

    ok "Phase 7 terminée — PROC ${CICS_APPLID} installé dans SYS1.PROCLIB"
    info "Démarrer la région avec : S ${CICS_APPLID}"
    info "  ou depuis le script : bash mvs/13_cicsvs_install.sh start"
}

# ============================================================
# Phase 8 : Démarrage de la région CICS + vérification
# ============================================================
phase8_start() {
    step "Phase 8 : Démarrage de la région CICS/VS"

    info "Envoi commande START ${CICS_APPLID} à MVS..."
    herc_cmd "S ${CICS_APPLID}" || {
        warn "Commande START non confirmée via API HTTP"
        info "Alternative : console Hercules → S ${CICS_APPLID}"
    }

    info "Attente initialisation CICS (30-90 s)..."
    local deadline=$(( SECONDS + 120 ))
    local started=0
    while [[ $SECONDS -lt $deadline ]]; do
        sleep 5; printf "."
        local sl; sl=$(herc_syslog 40)
        # DFHSI1500 = "CICS is initialized and ready for work"
        if echo "$sl" | grep -q "DFHSI1500\|INITIALIZED AND READY"; then
            echo ""; ok "CICS/VS initialisé ! (DFHSI1500)"
            started=1; break
        fi
        # Erreurs d'init fréquentes
        if echo "$sl" | grep -qE "DFHSI1503|DFHPA0301|DFH.*ABEND|S0C[0-9]"; then
            echo ""
            fail "Erreur d'initialisation CICS détectée"
            herc_syslog 60 | grep -E "DFH|ABEND|S0C" | tail -10
            break
        fi
    done
    echo ""

    if [[ $started -eq 0 ]]; then
        warn "CICS non confirmé démarré — vérifier le syslog :"
        info "  bash mvs/herc.sh log 80"
        info "  ou bash mvs/herc.sh watch  (temps réel)"
        echo ""
        warn "Causes fréquentes d'échec :"
        warn "  - SDFHLOAD non trouvé (HLQ incorrect — CICS_HLQ=${CICS_HLQ})"
        warn "  - VTAM non actif (phase 4 non complète)"
        warn "  - APPLID déjà utilisé (S ${CICS_APPLID} déjà actif ?)"
        warn "  - SIT non assemblée (phase 5 non complète)"
    else
        echo ""
        ok "Région CICS/VS 1.7 opérationnelle"
        echo ""
        info "Connexion à CICS depuis un terminal 3270 :"
        info "  x3270 localhost:3270"
        info "  Dans TSO : LOGON ${TSO_USER} → puis connection VTAM vers ${CICS_APPLID}"
        info "  Ou directement : x3270 localhost:3270/${CICS_APPLID}"
        echo ""
        info "Transactions CICS disponibles :"
        info "  CESN — Sign-on (CICSUSER / CICSUSER)"
        info "  CEMT — Master Terminal (gestion région)"
        info "  CEDF — Execution Diagnostic Facility (debugger)"
        info "  CECI — Command-Level Interpreter (test EXEC CICS)"
    fi
}

# ============================================================
# Statut de la région CICS
# ============================================================
cics_status() {
    echo "=== ÉTAT CICS/VS 1.7 — ${CICS_APPLID} ==="
    echo ""
    echo "--- Syslog récent (messages DFH) ---"
    herc_syslog 60 | grep -E "DFH|${CICS_APPLID}|VTAM|IST09" | tail -20 \
        || echo "  (aucun message CICS récent)"
    echo ""
    echo "--- Jobs/STC actifs ---"
    herc_cmd '$D A' | grep -E "${CICS_APPLID}|CICS" || echo "  (region non visible dans \$D A)"
    echo ""
    echo "--- Volume CICS0 ---"
    herc_cmd "devstat ${CICS_DEVADDR}" || echo "  (périphérique ${CICS_DEVADDR} non disponible)"
}

# ============================================================
# Main
# ============================================================
echo ""
echo -e "${BOLD}══════════════════════════════════════════════════${NC}"
echo -e "${BOLD}  CICS/VS 1.7 — Installation MVS TK5              ${NC}"
echo -e "${BOLD}  Container : ${DOCKER_CONTAINER}                  ${NC}"
echo -e "${BOLD}  Région    : ${CICS_APPLID} / ${CICS_SYSID}       ${NC}"
echo -e "${BOLD}══════════════════════════════════════════════════${NC}"
echo ""
warn "PROJET AVANCÉ — Voir README CI_CD_TK5 section 'CICS/VS 1.7'"
warn "Pour du développement courant, préférer KICKS :"
warn "  bash mvs/12_kicks_install.sh all"
echo ""

trap 's3270_stop 2>/dev/null; true' EXIT

PHASES=("$@")
[[ ${#PHASES[@]} -eq 0 ]] && PHASES=("help")

for phase in "${PHASES[@]}"; do
    case "$phase" in
        all)
            phase1_check
            phase2_dasd
            phase3_tape
            phase4_vtam
            phase5_sit
            phase6_tables
            phase7_stc
            phase8_start
            echo ""
            ok "=== Installation CICS/VS 1.7 terminée ==="
            ;;
        check)    phase1_check ;;
        dasd)     phase2_dasd ;;
        tape)     phase3_tape ;;
        vtam)     phase4_vtam ;;
        sit)      phase5_sit ;;
        tables)   phase6_tables ;;
        stc)      phase7_stc ;;
        start)    phase8_start ;;
        status)   cics_status ;;
        help|*)
            cat <<HELP
Usage: bash mvs/13_cicsvs_install.sh <phase> [phase2] ...

AVERTISSEMENT : projet avancé, plusieurs jours d'installation.
                Pour du développement courant, utiliser KICKS (12_kicks_install.sh).

Phases :
  all      Toutes les phases dans l'ordre
  check    Vérifier les prérequis + localiser la bande CICS/VS
  dasd     Créer volume DASD CICS0 (0352) + formater VTOC
  tape     Restaurer bande CICS/VS 1.7 sur DASD (nécessite image AWS/HET)
  vtam     Définir APPL VTAM + terminaux dans SYS1.VTAMLST
  sit      Assembler SIT (System Initialization Table)
  tables   Assembler tables PCT, PPT, TCT, FCT
  stc      Installer PROC CICS dans SYS1.PROCLIB
  start    Démarrer la région CICS + vérifier DFHSI1500

Utilitaires :
  status   Afficher état région CICS/VS

Variables d'environnement :
  CICS_APPLID     VTAM APPLID de la région  (défaut: CICS01)
  CICS_SYSID      Identifiant CICS 4 chars  (défaut: CICS)
  CICS_HLQ        HLQ datasets CICS/VS      (défaut: CICS17)
  CICS_TAPE_LOCAL Chemin image bande AWS/HET (défaut: /tmp/cicsvs17.aws)

Source bande CICS/VS 1.7 :
  https://bitsavers.org/bits/IBM/cics/
  https://www.jaymoseley.com/hercules/

Différence avec KICKS :
  KICKS   — tourne sous TSO, installation simple, tables via CEDA/CEMT
  CICS/VS — tourne comme région VTAM (STC), tables assemblées (PCT/PPT/TCT/FCT)
HELP
            ;;
    esac
done
