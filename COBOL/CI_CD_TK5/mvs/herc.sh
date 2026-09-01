#!/usr/bin/env bash
# ============================================================
# herc.sh — Pilotage Hercules/MVS TK5
#
# Deux interfaces :
#   PORT 8038  → Hercules HTTP console (commandes Hercules)
#   PORT 3270  → TN3270 via s3270 (commandes MVS/JES2/CICS)
#
# DISTINCTION IMPORTANTE :
#   Commandes HERCULES (port 8038) : ipl, devlist, devstat, quit...
#   Commandes MVS/JES2 (port 3270) : $D A, D A,L, START, STOP...
#   Les commandes MVS ne passent pas par l'API HTTP — utiliser s3270.
#
# Usage :
#   bash herc.sh log [N]         # syslog Hercules (dernières N lignes)
#   bash herc.sh watch           # syslog en temps réel
#   bash herc.sh devlist         # liste des périphériques Hercules
#   bash herc.sh mvs "$D A"      # commande MVS/JES2 via s3270
#   bash herc.sh mvs "D A,L"     # commandes MVS (console opérateur)
#   bash herc.sh spool           # extraire les jobs en spool
# ============================================================
set -euo pipefail

HERC_URL="${HERC_URL:-http://localhost:8038}"
TK5_HOST="${TK5_HOST:-localhost}"
TK5_PORT="${TK5_PORT:-3270}"
TSO_USER="${TSO_USER:-HERC02}"
TSO_PASS="${TSO_PASS:-CUL8TR}"

MVS_DIR="$(dirname "${BASH_SOURCE[0]}")"
source "${MVS_DIR}/s3270_lib.sh"

CYAN='\033[0;36m'; YELLOW='\033[1;33m'; GREEN='\033[0;32m'; NC='\033[0m'

trap 's3270_stop' EXIT

strip_html() { sed 's/<[^>]*>//g' | sed '/^[[:space:]]*$/d'; }

# ---- Syslog Hercules (lecture seule, toujours fonctionnel) ----
herc_log() {
    local n="${1:-20}"
    curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=${n}" \
        | strip_html \
        | grep -v "^Command:\|^Only show\|^Refresh\|^Interval" \
        | tail -"${n}"
}

# ---- Commande HERCULES native (ipl, devlist, quit, etc.) ----
herc_cmd() {
    local cmd="$1"
    echo -e "${CYAN}[HERC] ${cmd}${NC}"
    curl -s -X POST "${HERC_URL}/cgi-bin/tasks/syslog" \
         --data-urlencode "command=${cmd}" \
         --data "norefresh=1" --data "msgcount=8" \
        | strip_html \
        | grep -v "^Command:\|^Only show\|^Refresh\|^Interval" \
        | tail -8
}

# ---- Commande MVS opérateur via console HTTP Hercules ----
# Hercules transmet la commande au système MVS quand elle est
# préfixée par '/'. Pas besoin de SDSF (non disponible sur TK5).
# Exemples :
#   bash herc.sh mvs '$D A'    → /$D A  (JES2 : jobs actifs)
#   bash herc.sh mvs 'D A,L'   → /D A,L (MVS : display long)
mvs_cmd() {
    local cmd="$1"
    echo -e "${CYAN}[MVS] /${cmd}${NC}"
    curl -s -X POST "${HERC_URL}/cgi-bin/tasks/syslog" \
         --data-urlencode "command=/${cmd}" \
         --data "norefresh=1" --data "msgcount=10" \
        | strip_html \
        | grep -v "^Command:\|^Only show\|^Refresh\|^Interval" \
        | tail -10
}

# ---- Extraire les N dernières lignes de la spool JES2 ----
show_spool_activity() {
    echo -e "${CYAN}=== Activité JES2 (syslog récent) ===${NC}"
    herc_log 40 | grep -E "HASP|JOB|STEP|RC=|ABEND|IEF|ENDED|STARTED" || echo "  (aucune activité récente)"
}

# ---- Main ----
case "${1:-help}" in
    log)
        herc_log "${2:-25}"
        ;;
    watch)
        echo -e "${YELLOW}Surveillance syslog MVS (Ctrl-C pour arrêter)...${NC}"
        while true; do
            clear
            echo "=== MVS TK5 SYSLOG — $(date '+%H:%M:%S') ==="
            herc_log 35
            sleep 3
        done
        ;;
    devlist)
        herc_cmd "devlist"
        ;;
    devstat)
        herc_cmd "devstat ${2:-0}"
        ;;
    ipl)
        echo -e "${YELLOW}⚠ IPL du système — confirmer ? (oui/non)${NC}"
        read -r confirm
        [[ "$confirm" == "oui" ]] && herc_cmd "ipl 0a80" || echo "Annulé."
        ;;
    mvs)
        shift
        mvs_cmd "$*"
        ;;
    spool)
        show_spool_activity
        ;;
    status)
        echo "=== ETAT HERCULES TK5 ==="
        echo ""
        echo "--- Syslog récent (filtré JES2/CICS) ---"
        herc_log 30 | grep -E "HASP|CICS|DFHSI|JOB|STC|TSO|READY|ABEND|ENDED" \
            | tail -15 || echo "  (rien de significatif)"
        echo ""
        echo "--- Container Docker ---"
        docker inspect mvs-tk5 --format \
            'Statut: {{.State.Status}}  Démarré: {{.State.StartedAt}}' 2>/dev/null \
            || echo "  (docker inspect non disponible)"
        ;;
    help|*)
        cat <<'HELP'
Usage: bash herc.sh <action> [args]

  log [N]          Afficher les N dernières lignes du syslog (défaut: 25)
  watch            Syslog en temps réel (Ctrl-C pour arrêter)
  status           Résumé état Hercules + container Docker
  spool            Activité JES2 récente (jobs soumis/terminés)

  devlist          Liste périphériques Hercules
  devstat [addr]   Statut d'un périphérique (ex: 0 = IPL device)

  mvs <cmd>        Envoyer une commande MVS via s3270 (TSO/SDSF)
                   Exemple : bash herc.sh mvs '$D A'

NOTE : Les commandes JES2 ($D A, $P, etc.) ne passent pas par le
port 8038 (API Hercules). Elles nécessitent :
  • s3270 → TSO → SDSF (accès console opérateur)
  • ou directement dans x3270 sur un terminal CICS/TSO
HELP
        ;;
esac
