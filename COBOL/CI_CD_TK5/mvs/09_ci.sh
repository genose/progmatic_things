#!/usr/bin/env bash
# ============================================================
# 09_ci.sh — Pipeline CI/CD complet (générique)
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
#
# Enchaîne toutes les étapes de validation et déploiement.
# Conçu pour être lancé depuis un hook git ou manuellement.
#
# Étapes :
#   1. Vérification syntaxe COBOL (local, GnuCOBOL)
#   2. Tests unitaires (CHECK_UNIT_CMD — mvn test, SQL, etc.)
#   3. Build incrémental MVS (upload + compile + newcopy)
#   4. Vérification spool JES2
#   5. Tests CICS automatisés (s3270) — skip si HAS_CICS=0
#
# Usage :
#   bash mvs/09_ci.sh              # pipeline complet
#   bash mvs/09_ci.sh --no-mvs    # local seulement (check + unit)
#   bash mvs/09_ci.sh --no-test   # build sans tests CICS
#   bash mvs/09_ci.sh --fast      # smoke test uniquement
#
# Configuration projet via PROJECT_NAME (défaut: gstk) :
#   PROJECT_NAME=crm bash mvs/09_ci.sh --no-mvs
# ============================================================
set -euo pipefail

CI_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "${CI_DIR}/lib/project.sh"

MVS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CI_LOG="${MVS_DIR}/.ci_history.log"
REPORT_DIR="${MVS_DIR}/.reports"
mkdir -p "$REPORT_DIR"

TS=$(date '+%Y%m%d_%H%M%S')
REPORT="${REPORT_DIR}/ci_${PROJECT_NAME}_${TS}.txt"

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
    echo " ${PROJECT_LABEL} CI/CD RAPPORT — ${TS//_/ }"
    echo " Git  : $(git -C "${PROJECT_DIR}" log --oneline -1 2>/dev/null || echo 'N/A')"
    echo " Host : $(hostname)"
    echo "════════════════════════════════════════════════════"
} | tee "$REPORT"

HERC_URL="${HERC_URL:-http://localhost:8038}"
CI_STATUS="OK"
declare -A STEP_RESULTS

# ============================================================
# ÉTAPE 1 : Syntaxe COBOL (GnuCOBOL local)
# ============================================================
step "1/5" "Vérification syntaxe COBOL"
timer_start

if [[ -n "${CHECK_COBOL_CMD:-}" ]]; then
    if eval "${CHECK_COBOL_CMD}" 2>&1 | tee -a "$REPORT"; then
        ok "Syntaxe COBOL valide"
        STEP_RESULTS[syntax]="PASS"
    else
        fail "Erreurs de syntaxe COBOL détectées"
        STEP_RESULTS[syntax]="FAIL"
        CI_STATUS="FAILED"
        if [[ $NO_MVS -eq 0 ]]; then
            fail "Arrêt — corriger les erreurs de syntaxe avant de déployer"
            log ""
            echo "CI: ABORTED (syntax) | ${PROJECT_NAME}" >> "$CI_LOG"
            exit 1
        fi
    fi
else
    skip "CHECK_COBOL_CMD non défini pour ${PROJECT_LABEL}"
    STEP_RESULTS[syntax]="SKIP"
fi
timer_end

# ============================================================
# ÉTAPE 2 : Tests unitaires
# ============================================================
step "2/5" "Tests unitaires"
timer_start

if [[ -n "${CHECK_UNIT_CMD:-}" ]]; then
    if eval "${CHECK_UNIT_CMD}" 2>&1 | tee -a "$REPORT"; then
        ok "Tests unitaires OK"
        STEP_RESULTS[unit]="PASS"
    else
        rc=$?
        fail "Tests unitaires échoués (rc=$rc)"
        STEP_RESULTS[unit]="FAIL"
        CI_STATUS="FAILED"
        warn "Des tests unitaires échoués peuvent causer des erreurs en production"
    fi
else
    skip "CHECK_UNIT_CMD non défini pour ${PROJECT_LABEL}"
    STEP_RESULTS[unit]="SKIP"
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

    build_ok=0
    if [[ -n "${MVS_UPLOAD_CMD:-}" || -n "${MVS_COMPILE_CMD:-}" ]]; then
        # Déléguer aux scripts du projet
        if [[ -n "${MVS_UPLOAD_CMD:-}" ]]; then
            eval "${MVS_UPLOAD_CMD}" 2>&1 | tee -a "$REPORT" && true || build_ok=$?
        fi
        if [[ $build_ok -eq 0 && -n "${MVS_COMPILE_CMD:-}" ]]; then
            eval "${MVS_COMPILE_CMD}" 2>&1 | tee -a "$REPORT" && true || build_ok=$?
        fi
    else
        # Build générique via 06_build.sh
        bash "${MVS_DIR}/06_build.sh" 2>&1 | tee -a "$REPORT" && true || build_ok=$?
    fi

    if [[ $build_ok -eq 0 ]]; then
        ok "Build MVS réussi"
        STEP_RESULTS[build]="PASS"
    else
        fail "Build MVS échoué"
        STEP_RESULTS[build]="FAIL"
        CI_STATUS="FAILED"
        warn "Vérifier le syslog : bash mvs/herc.sh spool"
    fi
    timer_end
fi

# ============================================================
# ÉTAPE 4 : Lecture spool pour confirmer RC=0
# ============================================================
if [[ $NO_MVS -eq 0 ]]; then
    step "4/5" "Vérification spool JES2"
    timer_start

    spool_output=$(bash "${MVS_DIR}/herc.sh" spool 2>/dev/null || true)
    log "$spool_output"

    # Chercher ABEND ou JCL ERROR dans le spool récent
    if echo "$spool_output" | grep -qiE "ABEND|JCL ERROR|IEF452I|IEF453I"; then
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
# ÉTAPE 5 : Tests CICS automatisés (skip si HAS_CICS=0)
# ============================================================
if [[ "${HAS_CICS}" == "0" ]]; then
    step "5/5" "Tests CICS"
    skip "Projet ${PROJECT_LABEL} : pas de CICS"
    STEP_RESULTS[cics]="SKIP"
elif [[ $NO_TEST -eq 1 ]] || [[ $NO_MVS -eq 1 ]]; then
    step "5/5" "Tests CICS"
    skip "Ignoré (--no-test ou --no-mvs)"
    STEP_RESULTS[cics]="SKIP"
else
    step "5/5" "Tests CICS automatisés"
    timer_start

    test_mode="all"
    [[ $FAST -eq 1 ]] && test_mode="smoke"

    if bash "${MVS_DIR}/08_test_cics.sh" "$test_mode" 2>&1 | tee -a "$REPORT"; then
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
{
    echo ""
    echo "════════════════════════════════════════════════════"
    echo " RÉSUMÉ CI/CD — ${PROJECT_LABEL}"
    echo "────────────────────────────────────────────────────"
    for s in syntax unit build spool cics; do
        status="${STEP_RESULTS[$s]:-SKIP}"
        case "$status" in
            PASS) icon="✓" ;;
            FAIL) icon="✗" ;;
            *)    icon="-" ;;
        esac
        printf "  %s  %-10s : %s\n" "$icon" "$s" "$status"
    done
    echo "────────────────────────────────────────────────────"
    printf "  Statut global   : %s\n" "$CI_STATUS"
    echo "════════════════════════════════════════════════════"
    echo " Rapport complet : $REPORT"
} | tee -a "$REPORT"

# ---- Historique des builds ----
echo "$(date '+%Y-%m-%d %H:%M:%S') | ${PROJECT_NAME} | $CI_STATUS | syntax:${STEP_RESULTS[syntax]:-?} unit:${STEP_RESULTS[unit]:-?} build:${STEP_RESULTS[build]:-?} cics:${STEP_RESULTS[cics]:-?}" \
    >> "$CI_LOG"

# ---- Lien vers le dernier rapport ----
ln -sf "$REPORT" "${REPORT_DIR}/latest_${PROJECT_NAME}.txt"

[[ "$CI_STATUS" == "OK" ]] && exit 0 || exit 1
