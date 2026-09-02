#!/usr/bin/env bash
# ============================================================
# 07_watch.sh — Surveillance fichiers → build automatique
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
#
# Utilise fswatch (MacPorts : /opt/local/bin/fswatch) pour
# détecter les sauvegardes dans le répertoire GSTK.
# Déclenche automatiquement 06_build.sh sur chaque modification.
#
# Usage :
#   bash scripts/mvs/07_watch.sh          # surveiller GSTK/
#   bash scripts/mvs/07_watch.sh --check  # build + test auto
# ============================================================
set -euo pipefail

GSTK_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../GSTK" && pwd)"
MVS_DIR="$(dirname "${BASH_SOURCE[0]}")"
SCRIPTS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
GSTK_SCRIPTS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../GSTK/scripts" && pwd)"

FSWATCH="${FSWATCH:-/opt/local/bin/fswatch}"
[[ ! -x "$FSWATCH" ]] && FSWATCH="$(command -v fswatch 2>/dev/null)" || true
[[ ! -x "${FSWATCH:-}" ]] && {
    echo "fswatch introuvable."
    echo "Installer : sudo port install fswatch"
    echo "         ou: brew install fswatch"
    exit 1
}

AUTO_TEST="${1:-}"
DEBOUNCE=2          # secondes entre deux builds (éviter double-trigger)
LAST_BUILD=0

YELLOW='\033[1;33m'; GREEN='\033[0;32m'; CYAN='\033[0;36m'; NC='\033[0m'

# ============================================================
# Déclencher le build pour un fichier modifié
# ============================================================
on_change() {
    local filepath="$1"
    local now; now=$(date +%s)

    # Debounce : ignorer si build lancé il y a moins de $DEBOUNCE s
    if (( now - LAST_BUILD < DEBOUNCE )); then
        return 0
    fi
    LAST_BUILD=$now

    local filename; filename=$(basename "$filepath")
    local ext="${filename##*.}"

    # Ignorer fichiers non-pertinents
    case "$filename" in
        .DS_Store|*.swp|*.tmp|*~|*.log|.checksums) return 0 ;;
    esac
    case "$ext" in
        cbl|bms|cpy) ;;
        *) return 0 ;;
    esac

    clear
    echo -e "${YELLOW}═══════════════════════════════════════════${NC}"
    echo -e "${YELLOW}  CHANGEMENT : ${filename}${NC}"
    echo -e "${YELLOW}  $(date '+%d/%m/%Y %H:%M:%S')${NC}"
    echo -e "${YELLOW}═══════════════════════════════════════════${NC}"
    echo ""

    # Étape 1 : vérification syntaxe locale (rapide)
    echo -e "${CYAN}[1/3] Vérification syntaxe COBOL...${NC}"
    if bash "$GSTK_SCRIPTS_DIR/04_cobc_check.sh" "$filename" 2>&1; then
        echo -e "${GREEN}✓ Syntaxe OK${NC}"
    else
        echo -e "\033[0;31m✗ Erreurs syntaxe — build annulé\033[0m"
        echo "$(date '+%Y-%m-%d %H:%M:%S') | SYNTAX FAIL | $filename" \
            >> "$MVS_DIR/.build.log"
        return 0
    fi
    echo ""

    # Étape 2 : build incrémental MVS
    echo -e "${CYAN}[2/3] Build incrémental MVS...${NC}"
    if bash "$MVS_DIR/06_build.sh"; then
        echo -e "${GREEN}✓ Build OK${NC}"
    else
        echo -e "\033[0;31m✗ Build échoué\033[0m"
        return 0
    fi
    echo ""

    # Étape 3 (optionnel) : tests CICS
    if [[ "$AUTO_TEST" == "--check" ]]; then
        echo -e "${CYAN}[3/3] Tests CICS automatisés...${NC}"
        bash "$MVS_DIR/08_test_cics.sh" smoke || true
    fi

    echo ""
    echo -e "${GREEN}✓ Prêt — $(date '+%H:%M:%S')${NC}"
    echo "Taper G000 dans x3270 pour tester."
}

# ============================================================
# Main
# ============================================================
clear
echo -e "${CYAN}═══════════════════════════════════════════${NC}"
echo -e "${CYAN}  GSTK WATCH — Hot-reload MVS TK5${NC}"
echo -e "${CYAN}  Surveillance : ${GSTK_DIR}${NC}"
echo -e "${CYAN}  Filtres : *.cbl *.bms *.cpy${NC}"
[[ "$AUTO_TEST" == "--check" ]] && \
    echo -e "${CYAN}  Mode : build + tests auto${NC}" || \
    echo -e "${CYAN}  Mode : build auto (sans tests)${NC}"
echo -e "${CYAN}  Ctrl-C pour arrêter${NC}"
echo -e "${CYAN}═══════════════════════════════════════════${NC}"
echo ""
echo "En attente de modifications..."

# fswatch options :
#   -o   : émettre un seul événement par batch (pas par fichier)
#   -r   : récursif
#   -e   : exclure (scripts/, .git/, etc.)
#   --event Updated,Created : ignorer les suppressions

"$FSWATCH" \
    --recursive \
    --event Updated \
    --event Created \
    --exclude "scripts/" \
    --exclude "\.git" \
    --exclude "\.build\.log" \
    --exclude "\.checksums" \
    --exclude "\.DS_Store" \
    --latency 1.5 \
    "$GSTK_DIR" \
| while IFS= read -r changed_path; do
    on_change "$changed_path"
done
