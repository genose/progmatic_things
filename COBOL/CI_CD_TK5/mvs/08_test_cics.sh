#!/usr/bin/env bash
# ============================================================
# 08_test_cics.sh — Tests CICS automatisés via s3270
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
#
# Lance chaque transaction CICS, capture l'écran 3270 (ASCII)
# et vérifie que les champs attendus sont présents.
# Les suites de tests spécifiques au projet sont chargées
# depuis CICS_TESTS_FILE (défini dans la conf projet).
#
# Usage :
#   bash mvs/08_test_cics.sh all        # tous les tests
#   bash mvs/08_test_cics.sh smoke      # tests rapides
#   bash mvs/08_test_cics.sh trans G007 # une transaction
#   bash mvs/08_test_cics.sh report     # afficher le dernier rapport
#
# Configuration projet via PROJECT_NAME (défaut: gstk) :
#   PROJECT_NAME=crm bash mvs/08_test_cics.sh all
# ============================================================
set -euo pipefail

CI_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "${CI_DIR}/lib/project.sh"

# Vérifier si le projet a CICS
if [[ "${HAS_CICS}" == "0" ]]; then
    echo "Projet ${PROJECT_LABEL} : pas de CICS — tests CICS non applicables"
    exit 0
fi

TK5_HOST="${TK5_HOST:-localhost}"
TK5_PORT="${TK5_PORT:-3270}"
MVS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPORT_FILE="${MVS_DIR}/.test_report_${PROJECT_NAME}.txt"

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
        s3270_login

        echo "  Démarrage KICKS via CLIST..." >&2
        s3270_cmd "String(\"EXEC 'KICKS.KICKSSYS.V1R5M0.CLIST(KICKS)'\")" 5 >/dev/null
        s3270_cmd "Enter()" 60 >/dev/null 2>&1 || true
        sleep 10

        local scr
        scr=$(s3270_cmd "Ascii()" 10 2>/dev/null || true)
        if ! echo "$scr" | grep -qiE "KICKS|CICS"; then
            echo "WARNING: KICKS ne semble pas actif" >&2
        fi
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
# Charger les suites de tests spécifiques au projet
# Si CICS_TESTS_FILE est défini et existe, le sourcer.
# Sinon définir des no-ops avec message informatif.
# ============================================================
if [[ -n "${CICS_TESTS_FILE:-}" && -f "${CICS_TESTS_FILE}" ]]; then
    source "${CICS_TESTS_FILE}"
else
    run_project_smoke() {
        section "SMOKE TESTS"
        skip "Aucun fichier de tests CICS configuré pour ${PROJECT_LABEL}"
        echo "  Définir CICS_TESTS_FILE dans conf/${PROJECT_NAME}.conf"
    }
    run_project_tests() {
        section "TESTS COMPLETS"
        skip "Aucun fichier de tests CICS configuré pour ${PROJECT_LABEL}"
        echo "  Définir CICS_TESTS_FILE dans conf/${PROJECT_NAME}.conf"
    }
fi

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
        echo "${PROJECT_LABEL} CICS TEST REPORT — $(date '+%Y-%m-%d %H:%M:%S')"
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
echo "=== Tests CICS ${PROJECT_LABEL} — $(date '+%d/%m/%Y %H:%M') ==="
echo "Terminal : ${TK5_HOST}:${TK5_PORT}"
echo ""

case "${1:-all}" in
    all)    run_project_tests;              print_summary ;;
    smoke)  run_project_smoke;              print_summary ;;
    trans)  run_trans "${2:-${CICS_TRANSACTIONS[0]:-TRAN}}"; print_summary ;;
    report)
        [[ -f "$REPORT_FILE" ]] && cat "$REPORT_FILE" \
            || echo "Aucun rapport — lancer d'abord : $0 all"
        ;;
    *)
        echo "Usage: $0 {all|smoke|trans <TRAN>|report}"
        exit 1
        ;;
esac
