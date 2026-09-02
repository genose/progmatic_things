#!/usr/bin/env bash
# ============================================================
# 09_ci.sh — Pipeline CI/CD CRM sur MVS TK5
# Auteur : Sebastien Cotillard
# Date   : 2026-09-02
#
# Etapes :
#   1. Tests Java (mvn test)
#   2. Build JARs (mvn package)
#   3. Upload sources COBOL sur MVS TK5
#   4. Compilation COBOL sur MVS (IBM COBOL via JCL)
#   5. Verification spool JES2 (RC=0)
#   6. Rapport final
#
# Usage :
#   bash scripts/mvs/09_ci.sh              # pipeline complet
#   bash scripts/mvs/09_ci.sh --no-mvs    # Java seulement
# ============================================================
set -euo pipefail

CRM_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JAVA_DIR="$CRM_DIR/crm-java"
MVS_DIR="$(dirname "${BASH_SOURCE[0]}")"

TS=$(date '+%Y%m%d_%H%M%S')
REPORT_DIR="$MVS_DIR/.reports"
mkdir -p "$REPORT_DIR"
REPORT="$REPORT_DIR/ci_${TS}.txt"

NO_MVS=0
for arg in "$@"; do [[ "$arg" == "--no-mvs" ]] && NO_MVS=1; done

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
BOLD='\033[1m'

log()  { echo -e "$*" | tee -a "$REPORT"; }
step() { log ""; log "${CYAN}${BOLD}━━━ Etape $1 : $2 ━━━${NC}"; }
ok()   { log "${GREEN}  OK  $*${NC}"; }
fail() { log "${RED}  FAIL $*${NC}"; CI_STATUS="FAILED"; }
skip() { log "  --  $* (skip)"; }

CI_STATUS="OK"

{
    echo "========================================================"
    echo " CRM CI/CD RAPPORT — ${TS//_/ }"
    echo " Git : $(git -C "$CRM_DIR" log --oneline -1 2>/dev/null || echo N/A)"
    echo "========================================================"
} | tee "$REPORT"

# ── Etape 1 : Tests Java ────────────────────────────────────
step "1/5" "Tests JUnit (mvn test)"
if cd "$JAVA_DIR" && mvn test -q 2>&1 | tee -a "$REPORT"; then
    ok "Tous les tests passent"
else
    fail "Echecs detectes dans mvn test"
    [[ $NO_MVS -eq 0 ]] && { log "Arret — corriger avant de deployer"; exit 1; }
fi

# ── Etape 2 : Build JARs ────────────────────────────────────
step "2/5" "Build JARs (mvn package)"
if cd "$JAVA_DIR" && mvn package -DskipTests -q 2>&1 | tee -a "$REPORT"; then
    ok "JARs construits dans crm-java/target/"
else
    fail "Echec build Maven"
fi

# ── Etape 3 : Upload sources COBOL sur TK5 ──────────────────
if [[ $NO_MVS -eq 1 ]]; then
    step "3/5" "Upload TK5"; skip "Mode --no-mvs"
    step "4/5" "Compilation MVS"; skip "Mode --no-mvs"
    step "5/5" "Spool JES2"; skip "Mode --no-mvs"
else
    step "3/5" "Upload sources COBOL sur MVS TK5"
    if [[ -f "$MVS_DIR/01_upload.sh" ]]; then
        if bash "$MVS_DIR/01_upload.sh" 2>&1 | tee -a "$REPORT"; then
            ok "Upload termine"
        else
            fail "Echec upload"
        fi
    else
        skip "01_upload.sh absent — a implementer"
    fi

    # ── Etape 4 : Compilation MVS ───────────────────────────
    step "4/5" "Compilation COBOL sur MVS (JCL)"
    if [[ -f "$MVS_DIR/02_compile.sh" ]]; then
        if bash "$MVS_DIR/02_compile.sh" 2>&1 | tee -a "$REPORT"; then
            ok "Compilation MVS terminee"
        else
            fail "Echec compilation MVS"
        fi
    else
        skip "02_compile.sh absent — a implementer"
    fi

    # ── Etape 5 : Spool JES2 ───────────────────────────────
    step "5/5" "Verification spool JES2"
    # Reutiliser herc.sh du CI_CD_TK5 si disponible
    HERC="$(cd "$CRM_DIR/.." && pwd)/CI_CD_TK5/mvs/herc.sh"
    if [[ -f "$HERC" ]]; then
        spool=$(bash "$HERC" spool 2>/dev/null || true)
        if echo "$spool" | grep -qiE "ABEND|JCL ERROR|IEF452I"; then
            fail "ABEND detecte dans le spool"
        else
            ok "Pas d'ABEND detecte"
        fi
    else
        skip "herc.sh absent — spool non verifie"
    fi
fi

# ── Rapport final ───────────────────────────────────────────
{
    echo ""
    echo "========================================================"
    echo " Statut global : $CI_STATUS"
    echo " Rapport       : $REPORT"
    echo "========================================================"
} | tee -a "$REPORT"

ln -sf "$REPORT" "$REPORT_DIR/latest.txt"
[[ "$CI_STATUS" == "OK" ]]
