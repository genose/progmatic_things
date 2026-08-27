#!/usr/bin/env bash
# ============================================================
# 08_test_cics.sh — Tests CICS automatisés via s3270
#
# Lance chaque transaction GSTK, capture l'écran 3270 (ASCII)
# et vérifie que les champs attendus sont présents.
#
# Usage :
#   bash scripts/mvs/08_test_cics.sh all        # tous les tests
#   bash scripts/mvs/08_test_cics.sh smoke      # tests rapides (G000 + G001)
#   bash scripts/mvs/08_test_cics.sh trans G007 # une transaction
#   bash scripts/mvs/08_test_cics.sh report     # afficher le dernier rapport
# ============================================================
set -euo pipefail

TK5_HOST="${TK5_HOST:-localhost}"
TK5_PORT="${TK5_PORT:-3270}"
MVS_DIR="$(dirname "${BASH_SOURCE[0]}")"
REPORT_FILE="${MVS_DIR}/.test_report.txt"

source "${MVS_DIR}/s3270_lib.sh"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'

PASS=0; FAIL=0; SKIP=0

pass() { echo -e "  ${GREEN}[PASS]${NC} $*"; (( PASS++ )); }
fail() { echo -e "  ${RED}[FAIL]${NC} $*"; (( FAIL++ )); }
skip() { echo -e "  ${YELLOW}[SKIP]${NC} $*"; (( SKIP++ )); }
section() { echo -e "\n${CYAN}=== $* ===${NC}"; }

# ============================================================
# Session CICS persistante (une seule connexion pour tous les tests)
# ============================================================
_CICS_SESSION=0

ensure_cics_session() {
    if [[ $_CICS_SESSION -eq 0 ]]; then
        s3270_start || { echo "ERROR: s3270 ne démarre pas" >&2; exit 1; }
        # Passer le banner Hercules → écran VTAM (CICS ou TSO)
        sleep 2
        s3270_cmd "Reset()" 5 >/dev/null 2>&1 || true
        sleep 1
        s3270_cmd "Clear()" 60 >/dev/null 2>&1 || true
        sleep 3
        _CICS_SESSION=1
    fi
}

trap 's3270_stop' EXIT

# ============================================================
# Capturer l'écran d'une transaction CICS
# $1 = code transaction (ex: G001)
# $2 = action PF supplémentaire après l'écran initial (optionnel)
# ============================================================
capture_screen() {
    local trans="$1"
    local extra_key="${2:-}"

    ensure_cics_session

    # Retour à l'état initial CICS
    s3270_cmd "Clear()" 5 >/dev/null 2>&1 || true
    sleep 0.5

    # Lancer la transaction
    s3270_cmd "String(\"${trans}\")" 5 >/dev/null
    s3270_cmd "Enter()" 30 >/dev/null
    sleep 2

    if [[ -n "$extra_key" ]]; then
        s3270_cmd "${extra_key}" 10 >/dev/null
        sleep 1
    fi

    # Capturer l'écran
    s3270_screen
}

# ============================================================
# Test générique : transaction + vérification contenu écran
# $1 = nom du test
# $2 = transaction CICS
# $3... = chaînes devant être présentes dans l'écran
# ============================================================
assert_screen() {
    local testname="$1"
    local trans="$2"
    shift 2
    local expected=("$@")

    local screen
    screen=$(capture_screen "$trans" 2>/dev/null) || {
        fail "$testname — impossible de capturer l'écran"
        return
    }

    if [[ -z "$screen" ]]; then
        fail "$testname — écran vide (transaction non lancée ?)"
        return
    fi

    local ok=1
    local missing=()
    for str in "${expected[@]}"; do
        if ! echo "$screen" | grep -qF "$str"; then
            ok=0
            missing+=("'$str'")
        fi
    done

    if [[ $ok -eq 1 ]]; then
        pass "$testname"
    else
        fail "$testname — absent de l'écran : ${missing[*]}"
        echo "$screen" | grep -v "^[[:space:]]*$" | head -5 | sed 's/^/       /'
    fi
}

# ============================================================
# Test : vérifier qu'une erreur N'apparaît PAS
# ============================================================
assert_no_error() {
    local testname="$1"
    local trans="$2"

    local screen
    screen=$(capture_screen "$trans" 2>/dev/null) || {
        fail "$testname — capture échouée"
        return
    }

    if echo "$screen" | grep -qiE "PGMIDERR|ABEND|TRANSACTION ABENDED|MAPFAIL|ERROR"; then
        local errline
        errline=$(echo "$screen" | grep -iE "PGMIDERR|ABEND|MAPFAIL|ERROR" | head -1)
        fail "$testname — erreur CICS détectée : $errline"
    else
        pass "$testname — aucune erreur CICS"
    fi
}

# ============================================================
# SUITE : Tests smoke (rapides)
# ============================================================
run_smoke() {
    section "SMOKE TESTS (G000 + G001)"

    assert_screen "G000 - Menu principal s'affiche" "G000" \
        "GSTK000" "MENU"

    assert_no_error "G000 - Pas d'erreur CICS" "G000"

    assert_screen "G001 - Consultation stock s'affiche" "G001" \
        "GSTK001" "CODE ART" "DATE"

    assert_no_error "G001 - Pas d'erreur CICS" "G001"
}

# ============================================================
# SUITE : Tests complets de chaque transaction
# ============================================================
run_all() {
    section "G000 — Menu principal"
    assert_screen "G000 - titre affiché"       "G000" "GSTK000" "MENU"
    assert_screen "G000 - options du menu"      "G000" "G001" "G002" "G003"
    assert_no_error "G000 - pas d'erreur CICS" "G000"

    section "G001 — Consultation stock"
    assert_screen "G001 - en-tête affiché"     "G001" "GSTK001" "CODE ART"
    assert_screen "G001 - colonnes présentes"  "G001" "STOCK" "DESIGNATION" "CATEGORIE"
    assert_screen "G001 - pagination"          "G001" "PAGE"
    assert_no_error "G001 - pas d'erreur"      "G001"

    section "G002 — Entrée marchandise"
    assert_screen "G002 - en-tête affiché"     "G002" "GSTK002" "ENTREE"
    assert_screen "G002 - champs de saisie"    "G002" "CODE ART" "QUANTITE"
    assert_screen "G002 - instructions PF"     "G002" "PF5" "PF6"
    assert_no_error "G002 - pas d'erreur"      "G002"

    section "G003 — Sortie marchandise"
    assert_screen "G003 - en-tête affiché"     "G003" "GSTK003" "SORTIE"
    assert_screen "G003 - champs de saisie"    "G003" "CODE ART" "QUANTITE"
    assert_no_error "G003 - pas d'erreur"      "G003"

    section "G004 — Gestion articles"
    assert_screen "G004 - en-tête affiché"     "G004" "GSTK004"
    assert_screen "G004 - champs article"      "G004" "CODE" "DESIGNATION"
    assert_no_error "G004 - pas d'erreur"      "G004"

    section "G005 — Rapports stock"
    assert_screen "G005 - en-tête affiché"     "G005" "GSTK005" "RAPPORT"
    assert_screen "G005 - stats globales"      "G005" "CATEGORIE"
    assert_no_error "G005 - pas d'erreur"      "G005"

    section "G006 — Alertes stock critique"
    assert_screen "G006 - en-tête affiché"     "G006" "GSTK006" "ALERTE"
    assert_screen "G006 - colonnes alertes"    "G006" "CODE ART" "STOCK" "MINIMUM"
    assert_no_error "G006 - pas d'erreur"      "G006"

    section "G007 — Historique mouvements"
    assert_screen "G007 - en-tête affiché"     "G007" "GSTK007" "HISTORIQUE"
    assert_screen "G007 - colonnes historique" "G007" "DATE" "TYPE" "QUANTITE"
    assert_screen "G007 - filtres disponibles" "G007" "PF5" "PF7" "PF8"
    assert_no_error "G007 - pas d'erreur"      "G007"
}

# ============================================================
# Test d'une transaction spécifique
# ============================================================
run_trans() {
    local trans="$1"
    section "Test manuel : ${trans}"
    echo "Capture de l'écran ${trans}..."
    local screen
    screen=$(capture_screen "$trans" 2>/dev/null)
    if [[ -z "$screen" ]]; then
        fail "Écran vide"
    else
        echo "$screen" | grep -v "^[[:space:]]*$" | head -24
        echo ""
        assert_no_error "${trans} - pas d'erreur CICS" "$trans"
    fi
}

# ============================================================
# Rapport
# ============================================================
write_report() {
    local total=$(( PASS + FAIL + SKIP ))
    {
        echo "GSTK CICS TEST REPORT — $(date '+%Y-%m-%d %H:%M:%S')"
        echo "PASS: $PASS / FAIL: $FAIL / SKIP: $SKIP / TOTAL: $total"
        [[ $FAIL -gt 0 ]] && echo "STATUT: FAILED" || echo "STATUT: OK"
    } > "$REPORT_FILE"
}

print_summary() {
    local total=$(( PASS + FAIL + SKIP ))
    echo ""
    echo "════════════════════════════════════"
    echo " Résultat : $PASS PASS  $FAIL FAIL  $SKIP SKIP  (/$total)"
    if [[ $FAIL -eq 0 ]]; then
        echo -e " ${GREEN}✓ TOUS LES TESTS PASSENT${NC}"
    else
        echo -e " ${RED}✗ ${FAIL} TEST(S) ÉCHOUÉ(S)${NC}"
    fi
    echo "════════════════════════════════════"
    write_report
    [[ $FAIL -gt 0 ]] && return 1 || return 0
}

# ============================================================
# Main
# ============================================================
echo "=== Tests CICS GSTK — $(date '+%d/%m/%Y %H:%M') ==="
echo "Terminal : ${TK5_HOST}:${TK5_PORT}"
echo ""

case "${1:-all}" in
    all)    run_all;              print_summary ;;
    smoke)  run_smoke;            print_summary ;;
    trans)  run_trans "${2:-G000}"; print_summary ;;
    report)
        [[ -f "$REPORT_FILE" ]] && cat "$REPORT_FILE" \
            || echo "Aucun rapport — lancer d'abord : $0 all"
        ;;
    *)
        echo "Usage: $0 {all|smoke|trans <G00x>|report}"
        exit 1
        ;;
esac
