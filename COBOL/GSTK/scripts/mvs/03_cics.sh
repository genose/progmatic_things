#!/usr/bin/env bash
# ============================================================
# 03_cics.sh — Définitions CICS via s3270 (CEDA + CEMT)
#
# Automatise la saisie des commandes CEDA dans CICS :
#   - MAPSET (x8), PROGRAM (x8), TRANSACTION (x8)
#   - CEDA INSTALL GROUP(GSTK)
#   - CEMT SET PROG NEWCOPY (après recompilation)
#
# Usage :
#   bash scripts/mvs/03_cics.sh install    # première installation
#   bash scripts/mvs/03_cics.sh newcopy    # recharger après recompilation
#   bash scripts/mvs/03_cics.sh status     # vérifier les programmes
#   bash scripts/mvs/03_cics.sh trans G007 # tester une transaction
# ============================================================
set -euo pipefail

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
# Se connecter à un terminal CICS (pas TSO)
# TK5 : après Reset+Clear, le terminal VTAM est en mode CICS.
# Si le terminal atterrit en TSO, ajuster la séquence.
# ============================================================
cics_connect() {
    s3270_start || { echo "ERROR: impossible de démarrer s3270" >&2; exit 1; }
    # Passer le banner Hercules → écran VTAM (CICS ou TSO selon config)
    sleep 2
    s3270_cmd "Reset()" 5 >/dev/null 2>&1 || true
    sleep 1
    s3270_cmd "Clear()" 60 >/dev/null 2>&1 || true
    sleep 3   # attendre que VTAM présente l'écran CICS
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
    info "Installation groupe GSTK dans CICS..."
    cics_connect

    # MAPSETS BMS
    for m in GSTK000M GSTK001M GSTK002M GSTK003M \
              GSTK004M GSTK005M GSTK006M GSTK007M; do
        cics_send "CEDA DEF MAPSET(${m}) GROUP(GSTK) RESIDENT(NO)"
    done

    # PROGRAMMES COBOL
    for p in GSTK000 GSTK001 GSTK002 GSTK003 \
              GSTK004 GSTK005 GSTK006 GSTK007; do
        cics_send "CEDA DEF PROGRAM(${p}) GROUP(GSTK) LANGUAGE(COBOL)"
    done

    # TRANSACTIONS
    for n in 000 001 002 003 004 005 006 007; do
        cics_send "CEDA DEF TRANS(G${n}) GROUP(GSTK) PROGRAM(GSTK${n})"
    done

    # INSTALLER LE GROUPE
    s3270_cmd "Clear()" 5 >/dev/null 2>&1 || true
    s3270_cmd "String(\"CEDA INSTALL GROUP(GSTK)\")" >/dev/null
    s3270_cmd "Enter()" 30 >/dev/null
    sleep 2

    local screen
    screen=$(s3270_screen)
    echo "$screen" | grep -v "^[[:space:]]*$" | head -5 | sed 's/^/  /'

    s3270_stop

    ok "Commandes CEDA envoyées."
    echo ""
    echo "Vérifier dans x3270 :"
    echo "  CEMT INQ PROG(GSTK*)    → doit montrer 8 programmes EN(ENABLED)"
    echo "  CEMT INQ TRAN(G*)       → doit montrer 8 transactions EN(ENABLED)"
}

# ============================================================
# NEWCOPY : recharger les modules après recompilation
# ============================================================
cics_newcopy() {
    info "NEWCOPY de tous les programmes GSTK..."
    cics_connect

    for p in GSTK000 GSTK001 GSTK002 GSTK003 \
              GSTK004 GSTK005 GSTK006 GSTK007; do
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
    info "Statut programmes GSTK dans CICS..."
    cics_connect

    s3270_cmd "Clear()" 5 >/dev/null 2>&1 || true
    s3270_cmd "String(\"CEMT INQ PROG(GSTK*)\")" >/dev/null
    s3270_cmd "Enter()" 10 >/dev/null
    sleep 2

    s3270_screen | grep -v "^[[:space:]]*$"
    s3270_stop
}

# ============================================================
# Lancer une transaction CICS (test rapide)
# ============================================================
cics_trans() {
    local trans="${1:-G000}"
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
echo "=== Pilotage CICS GSTK via s3270 ==="
echo "Terminal : ${TK5_HOST}:${TK5_PORT}"
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
        cics_trans "${2:-G000}"
        ;;
    *)
        echo "Usage: $0 {install|newcopy|status|trans <G00x>}"
        echo ""
        echo "Ordre recommandé :"
        echo "  1. $0 install          # définir MAPSET/PROGRAM/TRANS + INSTALL"
        echo "  2. $0 status           # vérifier que les 8 pgms sont ENABLED"
        echo "  3. $0 trans G000       # tester le menu principal"
        echo ""
        echo "Après chaque recompilation :"
        echo "  $0 newcopy             # recharger les modules sans restart CICS"
        ;;
esac
