#!/usr/bin/env bash
# ============================================================
# 07_watch.sh — Surveillance fichiers → build automatique
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
#
# macOS  : utilise fswatch (brew install fswatch  ou  sudo port install fswatch)
# Linux/WSL : utilise inotifywait (sudo apt install inotify-tools)
#
# Usage :
#   bash mvs/07_watch.sh          # surveiller PROJECT_DIR
#   bash mvs/07_watch.sh --check  # build + test auto
#
# Configuration projet via PROJECT_NAME (défaut: gstk) :
#   PROJECT_NAME=crm bash mvs/07_watch.sh
# ============================================================
set -euo pipefail

CI_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "${CI_DIR}/lib/project.sh"

MVS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ---- Détecter le gestionnaire de surveillance selon l'OS ----
_OS="$(uname -s)"
WATCHER=""
FSWATCH=""
INOTIFYWAIT=""

if [[ "$_OS" == "Darwin" ]]; then
    for _f in \
        "${FSWATCH:-}" \
        /opt/local/bin/fswatch \
        /opt/homebrew/bin/fswatch \
        /usr/local/bin/fswatch; do
        [[ -x "${_f:-}" ]] && { FSWATCH="$_f"; break; }
    done
    [[ -z "$FSWATCH" ]] && FSWATCH="$(command -v fswatch 2>/dev/null || true)"
    [[ -x "${FSWATCH:-}" ]] && WATCHER="fswatch"
else
    # Linux / WSL
    INOTIFYWAIT="$(command -v inotifywait 2>/dev/null || true)"
    [[ -x "${INOTIFYWAIT:-}" ]] && WATCHER="inotifywait"
fi

if [[ -z "$WATCHER" ]]; then
    echo "Aucun gestionnaire de surveillance trouvé." >&2
    if [[ "$_OS" == "Darwin" ]]; then
        echo "  macOS : brew install fswatch" >&2
        echo "       ou: sudo port install fswatch" >&2
    else
        echo "  Linux/WSL : sudo apt install inotify-tools   (Debian/Ubuntu)" >&2
        echo "              sudo dnf install inotify-tools   (Fedora/RHEL)" >&2
        echo "              sudo pacman -S inotify-tools     (Arch)" >&2
    fi
    exit 1
fi

AUTO_TEST="${1:-}"
DEBOUNCE=2          # secondes entre deux builds (éviter double-trigger)
LAST_BUILD=0

YELLOW='\033[1;33m'; GREEN='\033[0;32m'; CYAN='\033[0;36m'; NC='\033[0m'

# Construire le pattern d'extensions depuis WATCH_EXTENSIONS
EXT_PATTERN=$(IFS="|"; echo "${WATCH_EXTENSIONS[*]+"${WATCH_EXTENSIONS[*]}"}")

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
        .DS_Store|*.swp|*.tmp|*~|*.log|.checksums*) return 0 ;;
    esac

    # Vérifier l'extension contre WATCH_EXTENSIONS
    local ext_ok=0
    local e
    for e in "${WATCH_EXTENSIONS[@]+"${WATCH_EXTENSIONS[@]}"}"; do
        if [[ "$ext" == "$e" ]]; then
            ext_ok=1
            break
        fi
    done
    [[ $ext_ok -eq 0 ]] && return 0

    clear
    echo -e "${YELLOW}═══════════════════════════════════════════${NC}"
    echo -e "${YELLOW}  CHANGEMENT : ${filename}${NC}"
    echo -e "${YELLOW}  $(date '+%d/%m/%Y %H:%M:%S')${NC}"
    echo -e "${YELLOW}═══════════════════════════════════════════${NC}"
    echo ""

    # Étape 1 : vérification syntaxe locale (rapide)
    if [[ -n "${CHECK_COBOL_CMD:-}" ]]; then
        echo -e "${CYAN}[1/3] Vérification syntaxe COBOL...${NC}"
        if eval "${CHECK_COBOL_CMD}" "$filename" 2>&1; then
            echo -e "${GREEN}✓ Syntaxe OK${NC}"
        else
            echo -e "\033[0;31m✗ Erreurs syntaxe — build annulé\033[0m"
            echo "$(date '+%Y-%m-%d %H:%M:%S') | SYNTAX FAIL | $filename" \
                >> "$MVS_DIR/.build.log"
            return 0
        fi
        echo ""
    fi

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
    if [[ "$AUTO_TEST" == "--check" && "${HAS_CICS}" == "1" ]]; then
        echo -e "${CYAN}[3/3] Tests CICS automatisés...${NC}"
        bash "$MVS_DIR/08_test_cics.sh" smoke || true
    fi

    echo ""
    echo -e "${GREEN}✓ Prêt — $(date '+%H:%M:%S')${NC}"
    if [[ "${HAS_CICS}" == "1" && ${#CICS_TRANSACTIONS[@]} -gt 0 ]]; then
        echo "Taper ${CICS_TRANSACTIONS[0]} dans x3270 pour tester."
    fi
}

# ============================================================
# Main
# ============================================================
clear
echo -e "${CYAN}═══════════════════════════════════════════${NC}"
echo -e "${CYAN}  ${PROJECT_LABEL} WATCH — Hot-reload MVS TK5${NC}"
echo -e "${CYAN}  Surveillance : ${PROJECT_DIR}${NC}"
echo -e "${CYAN}  Filtres : *.${EXT_PATTERN//|/ *.}${NC}"
[[ "$AUTO_TEST" == "--check" ]] && \
    echo -e "${CYAN}  Mode : build + tests auto${NC}" || \
    echo -e "${CYAN}  Mode : build auto (sans tests)${NC}"
echo -e "${CYAN}  Ctrl-C pour arrêter${NC}"
echo -e "${CYAN}═══════════════════════════════════════════${NC}"
echo ""
echo "En attente de modifications..."

# Lancer la surveillance (fswatch sur macOS, inotifywait sur Linux/WSL)
if [[ "$WATCHER" == "fswatch" ]]; then
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
        "$PROJECT_DIR"
else
    # inotifywait : -m = monitor mode (infini), --format = une ligne par fichier
    "$INOTIFYWAIT" \
        -m -r \
        -e close_write,create \
        --format '%w%f' \
        --exclude '(scripts/|\.git|\.build\.log|\.checksums)' \
        "$PROJECT_DIR" 2>/dev/null
fi | while IFS= read -r changed_path; do
    on_change "$changed_path"
done
