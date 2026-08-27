#!/usr/bin/env bash
# ============================================================
# 09_ci.sh — Pipeline CI/CD complet GSTK
#
# Enchaîne toutes les étapes de validation et déploiement.
# Conçu pour être lancé depuis un hook git ou manuellement.
#
# Étapes :
#   1. Vérification syntaxe COBOL (local, GnuCOBOL)
#   2. Tests SQL PostgreSQL (local)
#   3. Build incrémental MVS (upload + compile + newcopy)
#   4. Tests CICS automatisés (s3270)
#   5. Rapport final
#
# Usage :
#   bash scripts/mvs/09_ci.sh              # pipeline complet
#   bash scripts/mvs/09_ci.sh --no-mvs    # local seulement (check + sql)
#   bash scripts/mvs/09_ci.sh --no-test   # build sans tests CICS
#   bash scripts/mvs/09_ci.sh --fast      # smoke test uniquement
# ============================================================
set -euo pipefail

MVS_DIR="$(dirname "${BASH_SOURCE[0]}")"
SCRIPTS_DIR="$(cd "${MVS_DIR}/.." && pwd)"
CI_LOG="${MVS_DIR}/.ci_history.log"
REPORT_DIR="${MVS_DIR}/.reports"
mkdir -p "$REPORT_DIR"

TS=$(date '+%Y%m%d_%H%M%S')
REPORT="${REPORT_DIR}/ci_${TS}.txt"

# Flags
NO_MVS=0; NO_TEST=0; FAST=0
for arg in "$@"; do
    [[ "$arg" == "--no-mvs"  ]] && NO_MVS=1
    [[ "$arg" == "--no-test" ]] && NO_TEST=1
    [[ "$arg" == "--fast"    ]] && FAST=1
done

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
BOLD='\033[1m'

# ============================================================
# Journalisation dual (écran + fichier rapport)
# ============================================================
LOG_ERRORS=0
log() { echo -e "$*" | tee -a "$REPORT"; }
step() {
    local num="$1"; local label="$2"
    log ""
    log "${CYAN}${BOLD}━━━ Étape ${num} : ${label} ━━━${NC}"
}
ok()   { log "${GREEN}  ✓ $*${NC}"; }
fail() { log "${RED}  ✗ $*${NC}"; (( LOG_ERRORS++ )) || true; }
warn() { log "${YELLOW}  ⚠ $*${NC}"; }
skip() { log "  - $* (ignoré)"; }

# ============================================================
# Chronomètre
# ============================================================
STEP_START=0
timer_start() { STEP_START=$(date +%s); }
timer_end() {
    local elapsed=$(( $(date +%s) - STEP_START ))
    echo "  [${elapsed}s]"
}

# ============================================================
# Header rapport
# ============================================================
{
    echo "════════════════════════════════════════════════════"
    echo " GSTK CI/CD RAPPORT — ${TS//_/ }"
    echo " Git  : $(git -C "$(cd "$MVS_DIR/../.."; pwd)" log --oneline -1 2>/dev/null || echo 'N/A')"
    echo " Host : $(hostname)"
    echo "════════════════════════════════════════════════════"
} | tee "$REPORT"

CI_STATUS="OK"
declare -A STEP_RESULTS

# ============================================================
# ÉTAPE 1 : Syntaxe COBOL (GnuCOBOL local)
# ============================================================
step "1/5" "Vérification syntaxe COBOL"
timer_start

if bash "$SCRIPTS_DIR/04_cobc_check.sh" 2>&1 | tee -a "$REPORT"; then
    ok "Syntaxe COBOL valide"
    STEP_RESULTS[syntax]="PASS"
else
    fail "Erreurs de syntaxe COBOL détectées"
    STEP_RESULTS[syntax]="FAIL"
    CI_STATUS="FAILED"
    if [[ $NO_MVS -eq 0 ]]; then
        fail "Arrêt — corriger les erreurs de syntaxe avant de déployer"
        log ""
        echo "CI: ABORTED (syntax)" >> "$CI_LOG"
        exit 1
    fi
fi
timer_end

# ============================================================
# ÉTAPE 2 : Tests SQL PostgreSQL
# ============================================================
step "2/5" "Tests SQL PostgreSQL"
timer_start

if bash "$SCRIPTS_DIR/05_test_sql.sh" 2>&1 | tee -a "$REPORT"; then
    ok "Toutes les requêtes SQL passent"
    STEP_RESULTS[sql]="PASS"
else
    rc=$?
    fail "Certaines requêtes SQL ont échoué (rc=$rc)"
    STEP_RESULTS[sql]="FAIL"
    CI_STATUS="FAILED"
    warn "Les requêtes SQL échouées pourraient causer des SQLCODE non-zéro en CICS"
fi
timer_end

# ============================================================
# ÉTAPE 3 : Build MVS (upload + compile + newcopy)
# ============================================================
if [[ $NO_MVS -eq 1 ]]; then
    step "3/5" "Build MVS"
    skip "Mode --no-mvs actif"
    STEP_RESULTS[build]="SKIP"
else
    step "3/5" "Build incrémental MVS TK5"
    timer_start

    if bash "$MVS_DIR/06_build.sh" 2>&1 | tee -a "$REPORT"; then
        ok "Build MVS réussi"
        STEP_RESULTS[build]="PASS"
    else
        fail "Build MVS échoué"
        STEP_RESULTS[build]="FAIL"
        CI_STATUS="FAILED"
        warn "Vérifier le syslog : bash scripts/mvs/herc.sh spool"
    fi
    timer_end
fi

# ============================================================
# ÉTAPE 4 : Lecture spool pour confirmer RC=0
# ============================================================
if [[ $NO_MVS -eq 0 ]]; then
    step "4/5" "Vérification spool JES2"
    timer_start

    spool_output=$(bash "$MVS_DIR/herc.sh" spool 2>/dev/null || true)
    log "$spool_output"

    # Chercher ABEND ou JCL ERROR dans le spool récent
    if echo "$spool_output" | grep -qiE "ABEND|JCL ERROR|IEF450I|IEF352I"; then
        fail "ABEND ou JCL ERROR détecté dans le spool"
        STEP_RESULTS[spool]="FAIL"
        CI_STATUS="FAILED"
    else
        ok "Pas d'ABEND détecté dans le spool"
        STEP_RESULTS[spool]="PASS"
    fi
    timer_end
else
    step "4/5" "Vérification spool JES2"
    skip "Mode --no-mvs actif"
    STEP_RESULTS[spool]="SKIP"
fi

# ============================================================
# ÉTAPE 5 : Tests CICS automatisés
# ============================================================
if [[ $NO_TEST -eq 1 ]] || [[ $NO_MVS -eq 1 ]]; then
    step "5/5" "Tests CICS"
    skip "Ignoré (--no-test ou --no-mvs)"
    STEP_RESULTS[cics]="SKIP"
else
    step "5/5" "Tests CICS automatisés"
    timer_start

    test_mode="all"
    [[ $FAST -eq 1 ]] && test_mode="smoke"

    if bash "$MVS_DIR/08_test_cics.sh" "$test_mode" 2>&1 | tee -a "$REPORT"; then
        ok "Tests CICS : tous passent"
        STEP_RESULTS[cics]="PASS"
    else
        fail "Tests CICS : échecs détectés"
        STEP_RESULTS[cics]="FAIL"
        CI_STATUS="FAILED"
    fi
    timer_end
fi

# ============================================================
# RAPPORT FINAL
# ============================================================
TOTAL_DURATION=$(( $(date +%s) - $(stat -f %B "$REPORT" 2>/dev/null || echo $(date +%s)) ))

{
    echo ""
    echo "════════════════════════════════════════════════════"
    echo " RÉSUMÉ CI/CD"
    echo "────────────────────────────────────────────────────"
    for step in syntax sql build spool cics; do
        status="${STEP_RESULTS[$step]:-SKIP}"
        case "$status" in
            PASS) icon="✓" ;;
            FAIL) icon="✗" ;;
            *)    icon="-" ;;
        esac
        printf "  %s  %-10s : %s\n" "$icon" "$step" "$status"
    done
    echo "────────────────────────────────────────────────────"
    printf "  Statut global   : %s\n" "$CI_STATUS"
    echo "════════════════════════════════════════════════════"
    echo " Rapport complet : $REPORT"
} | tee -a "$REPORT"

# ---- Historique des builds ----
echo "$(date '+%Y-%m-%d %H:%M:%S') | $CI_STATUS | syntax:${STEP_RESULTS[syntax]:-?} sql:${STEP_RESULTS[sql]:-?} build:${STEP_RESULTS[build]:-?} cics:${STEP_RESULTS[cics]:-?}" \
    >> "$CI_LOG"

# ---- Lien vers le dernier rapport ----
ln -sf "$REPORT" "${REPORT_DIR}/latest.txt"

[[ "$CI_STATUS" == "OK" ]] && exit 0 || exit 1
