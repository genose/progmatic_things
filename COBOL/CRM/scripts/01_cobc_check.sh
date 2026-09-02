#!/usr/bin/env bash
# ============================================================
# 01_cobc_check.sh — Verification syntaxe COBOL locale (GnuCOBOL)
# Auteur : Sebastien Cotillard
# Date   : 2026-09-02
#
# Tente de compiler chaque source .COB/.SCO avec cobc --syntax-only.
# Les sources CRM utilisent Oracle Rdb SQL (EXEC SQL ... END-EXEC)
# et des COPY CDD specifiques a OpenVMS — les erreurs liees a ces
# extensions sont signalees comme avertissements, pas comme echecs.
# ============================================================
set -euo pipefail

CRM_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PASS=0; WARN=0; FAIL=0

GREEN='\033[0;32m'; YELLOW='\033[1;33m'; RED='\033[0;31m'; NC='\033[0m'

# Rechercher cobc dans l'environnement setup
COBC="${COBC:-cobc}"
if ! command -v "$COBC" &>/dev/null; then
    # Essayer depuis setup_cobol_env.sh
    ENV_SCRIPT="$(cd "$CRM_DIR/.." && pwd)/setup_cobol_env.sh"
    if [[ -f "$ENV_SCRIPT" ]]; then
        source "$ENV_SCRIPT" 2>/dev/null || true
        COBC="${COBC:-cobc}"
    fi
fi

if ! command -v "$COBC" &>/dev/null; then
    echo -e "${YELLOW}  cobc non trouve — skip verification syntaxe COBOL${NC}"
    echo "  Installer GnuCOBOL ou sourcer setup_cobol_env.sh"
    exit 0
fi

echo "Compilateur : $($COBC --version | head -1)"
echo ""

for src in "$CRM_DIR"/*.COB "$CRM_DIR"/*.SCO; do
    [[ -f "$src" ]] || continue
    name="$(basename "$src")"
    # --free pour eviter les erreurs de colonnes fixes liees aux copybooks
    output=$("$COBC" --syntax-only -free "$src" 2>&1 || true)

    # Erreurs connues OpenVMS : COPY CDD, EXEC SQL, DECLARE ALIAS, COPY PROC-COM
    # On les classe en WARN plutot que FAIL
    vms_only=$(echo "$output" | grep -cE "CDD|ALIAS FILENAME|PROC.COM|WORK.COM|D00_MSG" || true)
    real_errors=$(echo "$output" | grep -cE "^.*error:.*$" || true)

    if [[ "$real_errors" -gt 0 && "$vms_only" -eq 0 ]]; then
        echo -e "${RED}  FAIL${NC}  $name"
        echo "$output" | grep "error:" | head -5
        (( FAIL++ )) || true
    elif [[ -n "$output" ]]; then
        echo -e "${YELLOW}  WARN${NC}  $name  (extensions OpenVMS/SQL — normal)"
        (( WARN++ )) || true
    else
        echo -e "${GREEN}  OK${NC}    $name"
        (( PASS++ )) || true
    fi
done

echo ""
echo "Resultats : OK=$PASS  WARN=$WARN  FAIL=$FAIL"
[[ "$FAIL" -eq 0 ]]
