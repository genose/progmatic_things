#!/usr/bin/env bash
# ============================================================
# 03_cics.sh — Définitions CICS via s3270 (CEDA + CEMT)
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
#
# Automatise la saisie des commandes CEDA dans CICS :
#   - MAPSET, PROGRAM, TRANSACTION (depuis la conf projet)
#   - CEDA INSTALL GROUP(...)
#   - CEMT SET PROG NEWCOPY (après recompilation)
#
# Usage :
#   bash mvs/03_cics.sh install    # première installation
#   bash mvs/03_cics.sh newcopy    # recharger après recompilation
#   bash mvs/03_cics.sh status     # vérifier les programmes
#   bash mvs/03_cics.sh trans G007 # tester une transaction
#
# Configuration projet via PROJECT_NAME (défaut: gstk) :
#   PROJECT_NAME=crm bash mvs/03_cics.sh install
# ============================================================
set -euo pipefail

CI_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "${CI_DIR}/lib/project.sh"

# Vérifier si le projet a CICS
if [[ "${HAS_CICS}" == "0" ]]; then
    echo "Projet ${PROJECT_LABEL} : pas de CICS configuré — skip"
    exit 0
fi

TK5_HOST="${TK5_HOST:-localhost}"
TK5_PORT="${TK5_PORT:-3270}"
TSO_USER="${TSO_USER:-HERC02}"
TSO_PASS="${TSO_PASS:-CUL8TR}"

MVS_DIR="$(dirname "${BASH_SOURCE[0]}")"
source "${MVS_DIR}/s3270_lib.sh"

GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
ok()   { echo -e "${GREEN}✓${NC} $*"; }
info() { echo -e "${CYAN}ℹ${NC}  $*"; }

trap 's3270_stop' EXIT

# ============================================================
# Se connecter à KICKS depuis TSO
#
# KICKS n'est pas une application VTAM autonome — il démarre
# depuis TSO via CLIST. Séquence :
#   1. s3270_login  → TSO READY
#   2. EXEC 'KICKS.KICKSSYS.V1R5M0.CLIST(KICKS)' → KICKS actif
# ============================================================
cics_connect() {
    s3270_start || { echo "ERROR: impossible de démarrer s3270" >&2; exit 1; }
    s3270_login

    info "Démarrage KICKS via CLIST..."
    s3270_cmd "String(\"EXEC 'KICKS.KICKSSYS.V1R5M0.CLIST(KICKS)'\")" 5 >/dev/null
    s3270_cmd "Enter()" 60 >/dev/null 2>&1 || true
    sleep 10

    local scr
    scr=$(s3270_cmd "Ascii()" 10 2>/dev/null || true)
    if ! echo "$scr" | grep -qiE "KICKS|CICS"; then
        echo "WARNING: KICKS ne semble pas actif — vérifier l'installation" >&2
    fi
}

# ============================================================
# Envoyer une commande CICS (Clear → saisir → Enter)
# ============================================================
cics_send() {
    local cmd="$1"
    local wait="${2:-10}"
    s3270_cmd "Clear()" 5 >/dev/null 2>&1 || true
    s3270_cmd "String(\"${cmd}\")" 5 >/dev/null
    s3270_cmd "Enter()" "${wait}" >/dev/null
    sleep 1
}

# ============================================================
# Installation complète : MAPSET + PROGRAM + TRANSACTION
# ============================================================
cics_install() {
    info "Installation groupe ${CICS_GROUP} dans CICS..."
    cics_connect

    # MAPSETS BMS
    local m
    for m in "${CICS_MAPSETS[@]+"${CICS_MAPSETS[@]}"}"; do
        cics_send "CEDA DEF MAPSET(${m}) GROUP(${CICS_GROUP}) RESIDENT(NO)"
    done

    # PROGRAMMES COBOL
    local p
    for p in "${CICS_PROGRAMS[@]+"${CICS_PROGRAMS[@]}"}"; do
        cics_send "CEDA DEF PROGRAM(${p}) GROUP(${CICS_GROUP}) LANGUAGE(COBOL)"
    done

    # TRANSACTIONS
    local t prog_idx=0
    for t in "${CICS_TRANSACTIONS[@]+"${CICS_TRANSACTIONS[@]}"}"; do
        local prog="${CICS_PROGRAMS[$prog_idx]:-${t}}"
        cics_send "CEDA DEF TRANS(${t}) GROUP(${CICS_GROUP}) PROGRAM(${prog})"
        prog_idx=$(( prog_idx + 1 ))
    done

    # INSTALLER LE GROUPE
    s3270_cmd "Clear()" 5 >/dev/null 2>&1 || true
    s3270_cmd "String(\"CEDA INSTALL GROUP(${CICS_GROUP})\")" >/dev/null
    s3270_cmd "Enter()" 30 >/dev/null
    sleep 2

    local screen
    screen=$(s3270_screen)
    echo "$screen" | grep -v "^[[:space:]]*$" | head -5 | sed 's/^/  /'

    s3270_stop

    ok "Commandes CEDA envoyées."
    echo ""
    echo "Vérifier dans x3270 :"
    echo "  CEMT INQ PROG(${CICS_GROUP}*)    → doit montrer les programmes EN(ENABLED)"
    echo "  CEMT INQ TRAN(*)                 → doit montrer les transactions EN(ENABLED)"
}

# ============================================================
# NEWCOPY : recharger les modules après recompilation
# ============================================================
cics_newcopy() {
    info "NEWCOPY de tous les programmes ${CICS_GROUP}..."
    cics_connect

    local p
    for p in "${CICS_PROGRAMS[@]+"${CICS_PROGRAMS[@]}"}"; do
        cics_send "CEMT SET PROG(${p}) NEWCOPY" 10
    done

    local screen
    screen=$(s3270_screen)
    echo "$screen" | grep -v "^[[:space:]]*$" | head -5 | sed 's/^/  /'

    s3270_stop
    ok "NEWCOPY terminé — programmes rechargés."
}

# ============================================================
# Vérifier le statut des programmes CICS
# ============================================================
cics_status() {
    info "Statut programmes ${CICS_GROUP} dans CICS..."
    cics_connect

    s3270_cmd "Clear()" 5 >/dev/null 2>&1 || true
    s3270_cmd "String(\"CEMT INQ PROG(${CICS_GROUP}*)\")" >/dev/null
    s3270_cmd "Enter()" 10 >/dev/null
    sleep 2

    s3270_screen | grep -v "^[[:space:]]*$"
    s3270_stop
}

# ============================================================
# Lancer une transaction CICS (test rapide)
# ============================================================
cics_trans() {
    local trans="${1:-${CICS_TRANSACTIONS[0]:-TRAN}}"
    info "Lancement de la transaction $trans..."
    cics_connect

    s3270_cmd "Clear()" 5 >/dev/null 2>&1 || true
    s3270_cmd "String(\"${trans}\")" >/dev/null
    s3270_cmd "Enter()" 10 >/dev/null
    sleep 2

    s3270_screen | grep -v "^[[:space:]]*$"
    s3270_stop
}

# ============================================================
# Main
# ============================================================
echo "=== Pilotage CICS ${PROJECT_LABEL} via s3270 ==="
echo "Terminal : ${TK5_HOST}:${TK5_PORT}"
echo "Groupe   : ${CICS_GROUP}"
echo ""

case "${1:-help}" in
    install)
        cics_install
        ;;
    newcopy|reload)
        cics_newcopy
        ;;
    status)
        cics_status
        ;;
    trans)
        cics_trans "${2:-${CICS_TRANSACTIONS[0]:-TRAN}}"
        ;;
    *)
        echo "Usage: $0 {install|newcopy|status|trans <TRAN>}"
        echo ""
        echo "Ordre recommandé :"
        echo "  1. $0 install          # définir MAPSET/PROGRAM/TRANS + INSTALL"
        echo "  2. $0 status           # vérifier que les pgms sont ENABLED"
        if [[ ${#CICS_TRANSACTIONS[@]} -gt 0 ]]; then
            echo "  3. $0 trans ${CICS_TRANSACTIONS[0]}  # tester la première transaction"
        fi
        echo ""
        echo "Après chaque recompilation :"
        echo "  $0 newcopy             # recharger les modules sans restart CICS"
        ;;
esac
