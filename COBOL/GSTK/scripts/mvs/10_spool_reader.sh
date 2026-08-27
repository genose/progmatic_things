#!/usr/bin/env bash
# ============================================================
# 10_spool_reader.sh — Lecture output JCL depuis MVS via s3270
#
# Accède à la spool JES2 via TSO SDSF pour lire les listings
# de compilation et détecter les erreurs (RC, ABEND, MNOTE).
#
# Usage :
#   bash scripts/mvs/10_spool_reader.sh                  # jobs récents
#   bash scripts/mvs/10_spool_reader.sh GSTKCOMP         # chercher ce job
#   bash scripts/mvs/10_spool_reader.sh --errors         # seulement les erreurs
#   bash scripts/mvs/10_spool_reader.sh --cobol-errors   # erreurs compilation COBOL
# ============================================================
set -euo pipefail

TK5_HOST="${TK5_HOST:-localhost}"
TK5_PORT="${TK5_PORT:-3270}"
TSO_USER="${TSO_USER:-HERC02}"
TSO_PASS="${TSO_PASS:-CUL8TR}"
HERC_URL="${HERC_URL:-http://localhost:8038}"
MVS_DIR="$(dirname "${BASH_SOURCE[0]}")"
SPOOL_CACHE="${MVS_DIR}/.spool_cache"
mkdir -p "$SPOOL_CACHE"

source "${MVS_DIR}/s3270_lib.sh"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'

trap 's3270_stop' EXIT

# ============================================================
# Lire les N dernières lignes du syslog Hercules
# et extraire les événements JES2 significatifs
# ============================================================
parse_syslog_jobs() {
    local n="${1:-100}"
    local raw
    raw=$(curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=${n}" \
        | sed 's/<[^>]*>//g' \
        | grep -v "^Command:\|^Only show\|^Refresh\|^$")

    echo -e "${CYAN}=== Événements JES2 récents ===${NC}"
    echo ""

    echo "$raw" | while IFS= read -r line; do
        case "$line" in
            *HASP100*)
                echo -e "${GREEN}[SOUMIS ]${NC} $line" ;;
            *HASP373*|*IEF403I*)
                echo -e "${CYAN}[DÉMARRÉ]${NC} $line" ;;
            *HASP395*|*IEF404I*)
                if echo "$line" | grep -qiE "ABEND|ERROR"; then
                    echo -e "${RED}[ERREUR ]${NC} $line"
                else
                    echo -e "${GREEN}[TERMINÉ]${NC} $line"
                fi
                ;;
            *IEF142I*|*IEF272I*)
                echo -e "  RC      : $line" ;;
            *ABEND*|*IEA995I*)
                echo -e "${RED}[ABEND  ]${NC} $line" ;;
            *JCL\ ERROR*|*IEF630I*)
                echo -e "${RED}[JCL ERR]${NC} $line" ;;
        esac
    done
}

# ============================================================
# Accéder à SDSF via s3270 et capturer la liste des jobs
# ============================================================
sdsf_list_jobs() {
    local jobfilter="${1:-GSTK*}"
    echo -e "${CYAN}=== Jobs SDSF (filtre: $jobfilter) ===${NC}"

    s3270_start || return 1
    s3270_login

    # Naviguer vers SDSF
    s3270_cmd "String(\"SDSF\")" >/dev/null
    s3270_cmd "Enter()" 30 >/dev/null
    sleep 2

    # Filtrer les jobs
    s3270_cmd "String(\"ST ${jobfilter}\")" >/dev/null
    s3270_cmd "Enter()" 10 >/dev/null
    sleep 2

    s3270_screen | grep -v "^[[:space:]]*$"
    s3270_stop
}

# ============================================================
# Accéder à SDSF et lire le listing d'un job spécifique
# ============================================================
sdsf_read_job() {
    local jobname="${1:-GSTKCOMP}"
    local ddname="${2:-JESMSGLG}"
    local outfile="${SPOOL_CACHE}/${jobname}_${ddname}_$(date +%H%M%S).txt"

    echo -e "${CYAN}=== Lecture spool : $jobname / $ddname ===${NC}"

    s3270_start || return 1
    s3270_login

    # Naviguer vers SDSF → filtre job
    s3270_cmd "String(\"SDSF\")" >/dev/null
    s3270_cmd "Enter()" 30 >/dev/null
    sleep 2

    s3270_cmd "String(\"ST ${jobname}\")" >/dev/null
    s3270_cmd "Enter()" 10 >/dev/null
    sleep 2

    # Sélectionner le job (touche S)
    s3270_cmd "String(\"S\")" >/dev/null
    s3270_cmd "Tab()" 5 >/dev/null || true
    s3270_cmd "Enter()" 10 >/dev/null
    sleep 2

    local screen
    screen=$(s3270_screen)
    s3270_stop

    echo "$screen" | tee "$outfile"
    echo ""
    echo -e "${CYAN}Sauvegardé : $outfile${NC}"

    analyze_spool_output "$outfile"
}

# ============================================================
# Analyser un listing de compilation pour erreurs
# ============================================================
analyze_spool_output() {
    local file="$1"
    [[ -f "$file" ]] || return

    echo ""
    echo -e "${CYAN}=== Analyse listing ===${NC}"

    local errors warnings mnotes abends rc_lines
    errors=$(grep -c "^.*IGYOP.*E " "$file" 2>/dev/null || echo 0)
    warnings=$(grep -c "^.*IGYOP.*W " "$file" 2>/dev/null || echo 0)
    mnotes=$(grep -c "MNOTE" "$file" 2>/dev/null || echo 0)
    abends=$(grep -c "ABEND\|S0C[0-9]" "$file" 2>/dev/null || echo 0)
    rc_lines=$(grep -E "RC=|RETURN CODE=" "$file" 2>/dev/null || echo "")

    echo "  Erreurs COBOL (E) : $errors"
    echo "  Warnings    (W)   : $warnings"
    echo "  MNOTE             : $mnotes"
    echo "  ABEND             : $abends"
    [[ -n "$rc_lines" ]] && echo "  Return codes :" && echo "$rc_lines" | sed 's/^/    /'

    echo ""
    if [[ "$errors" -gt 0 ]] || [[ "$abends" -gt 0 ]]; then
        echo -e "${RED}✗ COMPILATION ÉCHOUÉE${NC}"
        echo ""
        echo "Lignes d'erreur :"
        grep -E "IGYOP.*[EW] |MNOTE|ABEND" "$file" | head -20 | sed 's/^/  /'
    elif [[ "$warnings" -gt 0 ]]; then
        echo -e "${YELLOW}⚠ Compilé avec $warnings warning(s)${NC}"
    else
        echo -e "${GREEN}✓ Compilation sans erreur${NC}"
    fi
}

# ============================================================
# Return codes depuis le syslog pour un job
# ============================================================
parse_compile_rc() {
    local jobname="${1:-GSTKCOMP}"
    echo -e "${CYAN}=== Return Codes : $jobname ===${NC}"

    curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=200" \
        | sed 's/<[^>]*>//g' \
        | grep -E "IEF142I|IEF272I|HASP395|HASP250|${jobname}" \
        | tail -30 \
        | while IFS= read -r line; do
            if echo "$line" | grep -qE "RC= *0 |COND CODE 0000"; then
                echo -e "  ${GREEN}✓${NC} $line"
            elif echo "$line" | grep -qE "RC= *[1-9]|ABEND|ERROR"; then
                echo -e "  ${RED}✗${NC} $line"
            else
                echo "    $line"
            fi
        done
}

# ============================================================
# Erreurs uniquement depuis le syslog
# ============================================================
show_errors_only() {
    echo -e "${CYAN}=== Erreurs et Warnings MVS récents ===${NC}"
    curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=300" \
        | sed 's/<[^>]*>//g' \
        | grep -iE "ABEND|ERROR|S0C[0-9]|JCL ERR|IGYOP.*[EW] |IEF45[0-9]|IEF35[0-9]" \
        | grep -v "^[[:space:]]*$" \
        | tail -30 \
        | while IFS= read -r line; do
            if echo "$line" | grep -qiE "ABEND|S0C|JCL ERR"; then
                echo -e "${RED}  $line${NC}"
            else
                echo -e "${YELLOW}  $line${NC}"
            fi
        done
}

# ============================================================
# Historique CI/CD
# ============================================================
show_ci_history() {
    local history="${MVS_DIR}/.ci_history.log"
    echo -e "${CYAN}=== Historique CI/CD ===${NC}"
    if [[ -f "$history" ]]; then
        tail -10 "$history" | while IFS= read -r line; do
            if echo "$line" | grep -q "| OK |"; then
                echo -e "${GREEN}$line${NC}"
            else
                echo -e "${RED}$line${NC}"
            fi
        done
    else
        echo "  Pas d'historique CI (lancer 09_ci.sh d'abord)"
    fi
}

# ============================================================
# Main
# ============================================================
case "${1:-recent}" in
    recent|"")
        parse_syslog_jobs 150
        echo ""
        show_ci_history
        ;;
    --errors)
        show_errors_only
        ;;
    --cobol-errors)
        latest=$(ls -t "$SPOOL_CACHE"/*.txt 2>/dev/null | head -1 || true)
        if [[ -n "$latest" ]]; then
            echo "Analyse du listing : $latest"
            analyze_spool_output "$latest"
        else
            echo "Pas de listing en cache — lancer d'abord : $0 GSTKCOMP"
            parse_compile_rc "GSTKCOMP"
        fi
        ;;
    --rc|--rc=*)
        jobname="${1#--rc=}"
        [[ "$jobname" == "--rc" ]] && jobname="${2:-GSTKCOMP}"
        parse_compile_rc "$jobname"
        ;;
    --history)
        show_ci_history
        ;;
    GSTK*|JOB*)
        jobname="$1"
        echo "=== Spool : $jobname ==="
        parse_compile_rc "$jobname"
        echo ""
        echo "Liste des événements dans le syslog :"
        curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=300" \
            | sed 's/<[^>]*>//g' \
            | grep -i "$jobname" | tail -20 || echo "  (aucun événement trouvé)"
        ;;
    help|*)
        cat <<'HELP'
Usage: bash 10_spool_reader.sh [option]

  (aucun)           Événements JES2 récents + historique CI
  GSTKCOMP          Chercher les events d'un job spécifique
  --errors          Uniquement les erreurs MVS (ABEND, JCL ERR, COBOL E)
  --cobol-errors    Analyser le dernier listing COBOL en cache
  --rc GSTKCOMP     Return codes du job spécifié
  --history         Historique des builds CI

Exemples :
  bash 10_spool_reader.sh GSTKBMS       # events assemblage BMS
  bash 10_spool_reader.sh --rc GSTKCOMP # RC de chaque étape COBOL
  bash 10_spool_reader.sh --errors       # tous les ABEND récents
HELP
        ;;
esac
