#!/usr/bin/env bash
# ============================================================
# 03_cics.sh — Pilotage CICS : KICKS v1.5.0 et/ou CICS/VS 1.7
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-03
#
# Détecte automatiquement le backend CICS disponible (KICKS ou
# CICS/VS 1.7) et adapte les opérations en conséquence.
#
# Sélection du backend (par priorité) :
#   1. Variable CICS_BACKEND dans conf/<projet>.conf ou .env
#   2. Détection automatique (DASD Docker) si CICS_BACKEND=auto
#
# CICS_BACKEND valeurs :
#   kicks   — KICKS v1.5.0 (tourne sous TSO, commandes CEDA/CEMT)
#   cicsvs  — CICS/VS 1.7 (STC VTAM, tables assemblées, CEMT uniquement)
#   both    — opérer sur les deux backends
#   auto    — détection automatique, préférence KICKS si les deux installés
#
# Usage :
#   bash mvs/03_cics.sh install    # définir + installer les ressources CICS
#   bash mvs/03_cics.sh newcopy    # recharger après recompilation
#   bash mvs/03_cics.sh status     # vérifier programmes + backend
#   bash mvs/03_cics.sh trans G007 # tester une transaction
#   bash mvs/03_cics.sh detect     # afficher la détection de backend
#
# Configuration projet via PROJECT_NAME (défaut: gstk) :
#   PROJECT_NAME=crm bash mvs/03_cics.sh install
#   CICS_BACKEND=cicsvs bash mvs/03_cics.sh status
# ============================================================
set -euo pipefail

CI_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "${CI_DIR}/lib/project.sh"

if [[ "${HAS_CICS}" == "0" ]]; then
    echo "Projet ${PROJECT_LABEL} : pas de CICS configuré — skip"
    exit 0
fi

MVS_DIR="$(dirname "${BASH_SOURCE[0]}")"
source "${MVS_DIR}/s3270_lib.sh"
source "${CI_DIR}/lib/cics_detect.sh"

TK5_HOST="${TK5_HOST:-localhost}"
TK5_PORT="${TK5_PORT:-3270}"
TSO_USER="${TSO_USER:-HERC02}"
TSO_PASS="${TSO_PASS:-CUL8TR}"
HLQ="${HLQ:-HERC02}"
HERC_URL="${HERC_URL:-http://localhost:8038}"

# CICS/VS region params (depuis conf ou valeurs par défaut)
CICS_APPLID="${CICS_APPLID:-CICS01}"
CICS_USER="${CICS_USER:-CICSUSER}"
CICS_PASS_CICS="${CICS_PASS_CICS:-CICSUSER}"   # mot de passe CICS/VS (≠ TSO_PASS)

GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'
RED='\033[0;31m'; BOLD='\033[1m'; NC='\033[0m'
ok()   { echo -e "${GREEN}✓${NC} $*"; }
warn() { echo -e "${YELLOW}⚠${NC}  $*"; }
info() { echo -e "${CYAN}ℹ${NC}  $*"; }
fail() { echo -e "${RED}✗${NC} $*" >&2; }

trap 's3270_stop 2>/dev/null; true' EXIT

# ============================================================
# Helpers Hercules (pour CICS/VS STC management)
# ============================================================
herc_cmd() {
    curl -s -X POST "${HERC_URL}/cgi-bin/tasks/syslog" \
        --data-urlencode "command=$1" \
        --data "norefresh=1" --data "msgcount=5" \
        | sed 's/<[^>]*>//g' \
        | grep -v "^Command:\|^Only\|^Refresh\|^$" \
        | tail -5 || true
}

herc_syslog() {
    local n="${1:-60}"
    curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=${n}" \
        | sed 's/<[^>]*>//g' \
        | grep -v "^Command:\|^Only\|^Refresh\|^$" || true
}

# ============================================================
# Commande CICS générique (Clear → saisie → Enter)
# Compatible KICKS et CICS/VS (mêmes commandes CEMT)
# ============================================================
cics_send() {
    local cmd="$1"
    local wait="${2:-10}"
    s3270_cmd "Clear()" 5 >/dev/null 2>&1 || true
    s3270_cmd "String(\"${cmd}\")" 5 >/dev/null
    s3270_cmd "Enter()" "${wait}" >/dev/null 2>&1 || true
    sleep 1
}

# ============================================================
# ============================================================
#  BACKEND : KICKS v1.5.0
# ============================================================
# ============================================================

# Connexion KICKS : TSO login → EXEC KICKS CLIST
kicks_connect() {
    s3270_start || { fail "impossible de démarrer s3270"; exit 1; }
    s3270_login

    info "Démarrage KICKS via CLIST..."
    s3270_cmd "String(\"EXEC 'KICKS.KICKSSYS.V1R5M0.CLIST(KICKS)'\")" 5 >/dev/null
    s3270_cmd "Enter()" 60 >/dev/null 2>&1 || true
    sleep 10

    local scr; scr=$(s3270_cmd "Ascii()" 10 2>/dev/null || true)
    if ! echo "$scr" | grep -qiE "KICKS|CICS"; then
        warn "KICKS ne semble pas actif — vérifier l'installation"
        info "  bash mvs/12_kicks_install.sh status"
    fi
}

kicks_install() {
    info "KICKS : installation groupe ${CICS_GROUP} (CEDA)..."
    kicks_connect

    local m; for m in "${CICS_MAPSETS[@]+"${CICS_MAPSETS[@]}"}"; do
        cics_send "CEDA DEF MAPSET(${m}) GROUP(${CICS_GROUP}) RESIDENT(NO)"
    done

    local p; for p in "${CICS_PROGRAMS[@]+"${CICS_PROGRAMS[@]}"}"; do
        cics_send "CEDA DEF PROGRAM(${p}) GROUP(${CICS_GROUP}) LANGUAGE(COBOL)"
    done

    local t prog_idx=0
    for t in "${CICS_TRANSACTIONS[@]+"${CICS_TRANSACTIONS[@]}"}"; do
        local prog="${CICS_PROGRAMS[$prog_idx]:-${t}}"
        cics_send "CEDA DEF TRANS(${t}) GROUP(${CICS_GROUP}) PROGRAM(${prog})"
        prog_idx=$(( prog_idx + 1 ))
    done

    s3270_cmd "Clear()" 5 >/dev/null 2>&1 || true
    s3270_cmd "String(\"CEDA INSTALL GROUP(${CICS_GROUP})\")" >/dev/null
    s3270_cmd "Enter()" 30 >/dev/null 2>&1 || true
    sleep 2

    local screen; screen=$(s3270_screen 2>/dev/null || true)
    echo "$screen" | grep -v "^[[:space:]]*$" | head -5 | sed 's/^/  /'
    s3270_stop
    ok "KICKS : commandes CEDA envoyées"
    echo "  Vérifier : CEMT INQ PROG(${CICS_GROUP}*)"
}

kicks_newcopy() {
    info "KICKS : NEWCOPY programmes ${CICS_GROUP}..."
    kicks_connect

    local p; for p in "${CICS_PROGRAMS[@]+"${CICS_PROGRAMS[@]}"}"; do
        cics_send "CEMT SET PROG(${p}) NEWCOPY" 10
    done

    local screen; screen=$(s3270_screen 2>/dev/null || true)
    echo "$screen" | grep -v "^[[:space:]]*$" | head -5 | sed 's/^/  /'
    s3270_stop
    ok "KICKS : NEWCOPY terminé"
}

kicks_status() {
    info "KICKS : statut programmes ${CICS_GROUP}..."
    kicks_connect

    s3270_cmd "Clear()" 5 >/dev/null 2>&1 || true
    s3270_cmd "String(\"CEMT INQ PROG(${CICS_GROUP}*)\")" >/dev/null
    s3270_cmd "Enter()" 10 >/dev/null 2>&1 || true
    sleep 2

    s3270_screen 2>/dev/null | grep -v "^[[:space:]]*$" | head -20
    s3270_stop
}

kicks_trans() {
    local trans="${1:-${CICS_TRANSACTIONS[0]:-TRAN}}"
    info "KICKS : transaction ${trans}..."
    kicks_connect

    s3270_cmd "Clear()" 5 >/dev/null 2>&1 || true
    s3270_cmd "String(\"${trans}\")" >/dev/null
    s3270_cmd "Enter()" 10 >/dev/null 2>&1 || true
    sleep 2

    s3270_screen 2>/dev/null | grep -v "^[[:space:]]*$"
    s3270_stop
}

# ============================================================
# ============================================================
#  BACKEND : CICS/VS 1.7
# ============================================================
# ============================================================

# Connexion CICS/VS : VTAM LOGON APPLID → signon CESN si requis
cicsvs_connect() {
    s3270_start || { fail "impossible de démarrer s3270"; exit 1; }

    # Sur le panel VTAM, demander directement la région CICS/VS
    info "Connexion VTAM → CICS/VS (APPLID=${CICS_APPLID})..."
    s3270_cmd "String(\"LOGON APPLID(${CICS_APPLID})\")" 5 >/dev/null 2>&1 || true
    s3270_cmd "Enter()" 30 >/dev/null 2>&1 || true
    sleep 5

    local scr; scr=$(s3270_cmd "Ascii()" 10 2>/dev/null || true)

    # CESN signon si CICS/VS demande authentification
    if echo "$scr" | grep -qiE "SIGNON|CESN|Sign.*On|USERID"; then
        info "Signon CICS/VS (CESN)..."
        s3270_cmd "String(\"CESN\")" 5 >/dev/null 2>&1 || true
        s3270_cmd "Enter()" 10 >/dev/null 2>&1 || true
        sleep 2
        # Remplir USERID et PASSWORD (tab entre les deux champs)
        s3270_cmd "String(\"${CICS_USER}\")" 5 >/dev/null 2>&1 || true
        s3270_cmd "Tab()" 3 >/dev/null 2>&1 || true
        s3270_cmd "String(\"${CICS_PASS_CICS}\")" 5 >/dev/null 2>&1 || true
        s3270_cmd "Enter()" 15 >/dev/null 2>&1 || true
        sleep 3
    fi

    # Vérification
    scr=$(s3270_cmd "Ascii()" 10 2>/dev/null || true)
    if ! echo "$scr" | grep -qiE "CICS|DFH|VTAM"; then
        warn "CICS/VS ne semble pas actif (APPLID=${CICS_APPLID})"
        info "  Vérifier : bash mvs/13_cicsvs_install.sh status"
        info "  Démarrer : bash mvs/13_cicsvs_install.sh start"
    fi
}

# install CICS/VS : réassemblage tables + redémarrage région
# CICS/VS 1.7 n'a pas de CEDA — les ressources sont dans les tables assemblées
cicsvs_install() {
    info "CICS/VS 1.7 : réassemblage tables + redémarrage région..."
    warn "CEDA n'existe pas dans CICS/VS 1.7 — les ressources sont dans les tables assemblées"
    warn "Ajouter programmes/transactions dans les sources PCT/PPT de 13_cicsvs_install.sh"
    echo ""

    # Phase 6 : réassembler PCT + PPT + TCT + FCT
    info "Réassemblage des tables CICS/VS (PCT, PPT, TCT, FCT)..."
    if ! bash "${MVS_DIR}/13_cicsvs_install.sh" tables; then
        fail "Réassemblage tables échoué — vérifier les JCLs"
        return 1
    fi

    # Redémarrer la région CICS/VS pour charger les nouvelles tables
    info "Arrêt de la région CICS/VS (${CICS_APPLID})..."
    herc_cmd "P ${CICS_APPLID}" >/dev/null 2>&1 || true
    sleep 15

    info "Démarrage de la région CICS/VS..."
    herc_cmd "S ${CICS_APPLID}" >/dev/null 2>&1 || true

    # Attendre DFHSI1500 (région prête)
    info "Attente initialisation CICS/VS (DFHSI1500)..."
    local deadline=$(( SECONDS + 120 ))
    while [[ $SECONDS -lt $deadline ]]; do
        sleep 5; printf "."
        herc_syslog 40 | grep -q "DFHSI1500\|INITIALIZED AND READY" \
            && { echo ""; ok "CICS/VS réinitialisé avec les nouvelles tables"; return 0; }
    done
    echo ""
    warn "DFHSI1500 non reçu dans les délais — vérifier :"
    info "  bash mvs/herc.sh log 80"
    info "  bash mvs/13_cicsvs_install.sh status"
}

# newcopy CICS/VS : CEMT SET PROG NEWCOPY (programmes déjà définis dans PPT)
cicsvs_newcopy() {
    info "CICS/VS 1.7 : NEWCOPY programmes ${CICS_GROUP}..."
    warn "NEWCOPY fonctionne pour les programmes déjà définis dans la PPT."
    warn "Pour ajouter de nouveaux programmes, utiliser : bash mvs/03_cics.sh install"
    cicsvs_connect

    local p; for p in "${CICS_PROGRAMS[@]+"${CICS_PROGRAMS[@]}"}"; do
        cics_send "CEMT SET PROG(${p}) NEWCOPY" 10
    done

    local screen; screen=$(s3270_screen 2>/dev/null || true)
    echo "$screen" | grep -v "^[[:space:]]*$" | head -5 | sed 's/^/  /'
    s3270_stop
    ok "CICS/VS : NEWCOPY terminé"
}

# status CICS/VS : syslog DFH + $D A + CEMT INQ
cicsvs_status() {
    info "CICS/VS 1.7 : statut région ${CICS_APPLID}..."

    echo ""
    echo "--- STC actifs (D A) ---"
    herc_cmd '$D A' | grep -iE "${CICS_APPLID}|CICS" || echo "  (région non visible dans D A)"

    echo ""
    echo "--- Messages DFH récents ---"
    herc_syslog 80 | grep -E "DFH|${CICS_APPLID}" | tail -10 \
        || echo "  (aucun message DFH récent)"

    echo ""
    echo "--- CEMT INQ PROG ---"
    cicsvs_connect
    s3270_cmd "Clear()" 5 >/dev/null 2>&1 || true
    s3270_cmd "String(\"CEMT INQ PROG(*)\")" >/dev/null
    s3270_cmd "Enter()" 15 >/dev/null 2>&1 || true
    sleep 2
    s3270_screen 2>/dev/null | grep -v "^[[:space:]]*$" | head -20
    s3270_stop
}

cicsvs_trans() {
    local trans="${1:-${CICS_TRANSACTIONS[0]:-TRAN}}"
    info "CICS/VS : transaction ${trans}..."
    cicsvs_connect

    s3270_cmd "Clear()" 5 >/dev/null 2>&1 || true
    s3270_cmd "String(\"${trans}\")" >/dev/null
    s3270_cmd "Enter()" 10 >/dev/null 2>&1 || true
    sleep 2

    s3270_screen 2>/dev/null | grep -v "^[[:space:]]*$"
    s3270_stop
}

# ============================================================
# Dispatch : exécute l'opération sur le(s) backend(s) actif(s)
# ============================================================
run_op() {
    local op="$1"
    shift
    local backend; backend=$(cics_resolve_backend)

    case "$backend" in
        kicks)
            echo -e "${BOLD}Backend : $(cics_backend_label kicks)${NC}"
            "kicks_${op}" "$@"
            ;;
        cicsvs)
            echo -e "${BOLD}Backend : $(cics_backend_label cicsvs)${NC}"
            "cicsvs_${op}" "$@"
            ;;
        both)
            echo -e "${BOLD}Backend : $(cics_backend_label both)${NC}"
            echo ""
            echo -e "${CYAN}--- KICKS ---${NC}"
            "kicks_${op}" "$@"
            echo ""
            echo -e "${CYAN}--- CICS/VS 1.7 ---${NC}"
            "cicsvs_${op}" "$@"
            ;;
        none)
            fail "Aucun backend CICS disponible (CICS_BACKEND=${CICS_BACKEND:-auto})"
            exit 1
            ;;
    esac
}

# ============================================================
# Main
# ============================================================
echo "=== Pilotage CICS — ${PROJECT_LABEL} ==="
echo "Terminal : ${TK5_HOST}:${TK5_PORT}"
echo "Groupe   : ${CICS_GROUP}"
echo ""

ACTION="${1:-help}"

case "$ACTION" in
    install)
        run_op "install"
        ;;
    newcopy|reload)
        run_op "newcopy"
        ;;
    status)
        # Affiche d'abord le résumé de détection, puis le statut du backend
        cics_print_status
        echo ""
        run_op "status"
        ;;
    trans)
        run_op "trans" "${2:-${CICS_TRANSACTIONS[0]:-TRAN}}"
        ;;
    detect)
        # Affiche uniquement la détection de backend (sans s3270)
        cics_print_status
        echo ""
        local installed; installed=$(cics_detect_installed)
        local running;   running=$(cics_detect_running)
        local resolved;  resolved=$(cics_resolve_backend)
        echo "Détails :"
        echo "  CICS_BACKEND=${CICS_BACKEND:-auto}"
        echo "  Installé  : ${installed}"
        echo "  En cours  : ${running}"
        echo "  Effectif  : ${resolved}"
        echo ""
        case "$installed" in
            none)
                warn "Aucun backend CICS installé"
                info "KICKS  : bash mvs/12_kicks_install.sh all"
                info "CICS/VS: bash mvs/13_cicsvs_install.sh all  (avancé)"
                ;;
            kicks)
                ok "KICKS installé — prêt à l'emploi"
                [[ "$running" == "none" ]] && \
                    info "KICKS non démarré — lancer depuis TSO : EXEC 'KICKS.KICKSSYS.V1R5M0.CLIST(KICKS)'"
                ;;
            cicsvs)
                ok "CICS/VS 1.7 installé"
                [[ "$running" == "none" ]] && \
                    info "Région non démarrée : bash mvs/13_cicsvs_install.sh start"
                ;;
            both)
                ok "KICKS + CICS/VS 1.7 installés"
                info "Backend effectif : ${resolved} (CICS_BACKEND=${CICS_BACKEND:-auto})"
                info "Changer : export CICS_BACKEND=cicsvs  ou  CICS_BACKEND=both"
                ;;
        esac
        ;;
    help|*)
        cat <<HELP
Usage : bash mvs/03_cics.sh <action> [args]

Actions :
  install          Installer les ressources CICS (CEDA pour KICKS, tables+restart pour CICS/VS)
  newcopy          Recharger les modules après recompilation (CEMT SET PROG NEWCOPY)
  status           État du backend + programmes CICS
  trans <TRANSID>  Tester une transaction (défaut: ${CICS_TRANSACTIONS[0]:-TRAN})
  detect           Afficher la détection de backend (sans connexion CICS)

Backend sélection (CICS_BACKEND) :
  auto    Détection automatique — préférence KICKS si les deux installés [défaut]
  kicks   Forcer KICKS v1.5.0 (TSO, CEDA/CEMT)
  cicsvs  Forcer CICS/VS 1.7 (VTAM STC, tables assemblées, CEMT)
  both    Opérer sur les deux backends

Exemples :
  bash mvs/03_cics.sh detect
  bash mvs/03_cics.sh install
  bash mvs/03_cics.sh newcopy
  CICS_BACKEND=cicsvs bash mvs/03_cics.sh status
  CICS_BACKEND=both   bash mvs/03_cics.sh newcopy
HELP
        ;;
esac
