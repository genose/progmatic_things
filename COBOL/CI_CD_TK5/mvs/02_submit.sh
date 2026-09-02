#!/usr/bin/env bash
# ============================================================
# 02_submit.sh — Soumission JCL vers MVS via s3270 + suivi
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
#
# Usage :
#   bash mvs/02_submit.sh alloc    # allouer datasets (1ère fois)
#   bash mvs/02_submit.sh bms      # assembler les BMS
#   bash mvs/02_submit.sh cobol    # compiler les programmes
#   bash mvs/02_submit.sh all      # tout dans l'ordre
#   bash mvs/02_submit.sh watch    # surveiller la spool
#
# Configuration projet via PROJECT_NAME (défaut: gstk) :
#   PROJECT_NAME=crm bash mvs/02_submit.sh cobol
# ============================================================
set -euo pipefail

CI_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "${CI_DIR}/lib/project.sh"

TK5_HOST="${TK5_HOST:-localhost}"
TK5_PORT="${TK5_PORT:-3270}"
TSO_USER="${TSO_USER:-HERC02}"
TSO_PASS="${TSO_PASS:-CUL8TR}"
HLQ="${HLQ:-HERC02}"
HERC_URL="${HERC_URL:-http://localhost:8038}"

MVS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "${MVS_DIR}/s3270_lib.sh"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
ok()   { echo -e "${GREEN}✓${NC} $*"; }
fail() { echo -e "${RED}✗${NC} $*"; }
warn() { echo -e "${YELLOW}⚠${NC}  $*"; }
info() { echo -e "${CYAN}ℹ${NC}  $*"; }

trap 's3270_stop' EXIT

# ============================================================
# Envoyer une commande MVS via Hercules HTTP
# ============================================================
herc_cmd() {
    curl -s -X POST "${HERC_URL}/cgi-bin/tasks/syslog" \
         --data-urlencode "command=$1" \
         --data "norefresh=1" --data "msgcount=3" \
        | sed 's/<[^>]*>//g' | grep -v "^Command:\|^Only\|^Refresh\|^$" | tail -5
}

# ============================================================
# Vérifier que s3270 et TK5 sont disponibles
# ============================================================
check_s3270() {
    [[ -x "${S3270:-}" ]] || {
        echo -e "${RED}s3270 introuvable.${NC} Installer via MacPorts : sudo port install x3270"
        echo "  ou définir : export S3270=/chemin/vers/s3270"
        exit 1
    }
    if ! nc -z "$TK5_HOST" "$TK5_PORT" 2>/dev/null; then
        echo -e "${RED}TK5 non joignable (${TK5_HOST}:${TK5_PORT})${NC}"
        echo "  Démarrer le container : docker start mvs-tk5"
        exit 1
    fi
}

# ============================================================
# Soumettre un JCL depuis un membre MVS via TSO SUBMIT
# ============================================================
submit_jcl_member() {
    local member="$1"
    local jobname="$2"
    info "Soumission de ${HLQ}.${APP_SUFFIX}.JCL(${member})..."

    s3270_start || { fail "s3270 ne démarre pas"; return 1; }
    s3270_login

    s3270_cmd "String(\"SUBMIT '${HLQ}.${APP_SUFFIX}.JCL(${member})'\")" >/dev/null
    s3270_cmd "Enter()" 30 >/dev/null
    sleep 3

    s3270_stop

    info "Statut JES2 :"
    herc_cmd '$D A' | grep -i "${jobname}" || echo "  (job terminé ou non trouvé)"
}

# ============================================================
# Soumettre un JCL via le lecteur de cartes Hercules (sockdev port 3505)
#
# Avantages vs TSO SUBMIT * :
#   - Pas besoin de session TSO (pas de login 5 min)
#   - Fiable : bash /dev/tcp dans le container → port 3505 Hercules
#
# Préprocessing automatique du JCL :
#   - &SYSUID → TSO_USER (non résolu via lecteur de cartes)
#   - MSGCLASS=X → MSGCLASS=A (X n'est pas configuré dans TK5)
#   - (ACCT) → supprimé (pas d'account dans TK5)
#
# Prérequis : docker CLI disponible sur le Mac, container mvs-tk5 démarré.
# ============================================================
_DOCKER_CONTAINER="${DOCKER_CONTAINER:-mvs-tk5}"
_CARDREADER_PORT="${CARDREADER_PORT:-3505}"

submit_jcl_cardreader() {
    local jclfile="$1"
    local jobname="${2:-JOB}"
    info "Soumission via lecteur de cartes : $(basename "$jclfile") → JES2..."

    [[ -f "$jclfile" ]] || { fail "JCL introuvable : $jclfile"; return 1; }

    # Vérifier que docker est disponible
    if ! docker inspect "${_DOCKER_CONTAINER}" >/dev/null 2>&1; then
        fail "Container ${_DOCKER_CONTAINER} non accessible — utiliser le mode TSO"
        return 1
    fi

    local hlq="${HLQ:-HERC02}"
    local user="${TSO_USER:-HERC02}"
    local pass="${TSO_PASS:-CUL8TR}"

    # Préprocesser le JCL et envoyer au lecteur de cartes
    local cicshlq="${CICSHLQ:-CICSTS}"
    local cobhlq="${COBHLQ:-IGY}"
    local db2hlq="${DB2HLQ:-DB2}"
    sed \
        "s/NOTIFY=&SYSUID/NOTIFY=${user}/g" \
        "$jclfile" \
        | sed "s/&SYSUID/${user}/g" \
        | sed "s/&TSO_PASS/${pass}/g" \
        | sed "s/MSGCLASS=X/MSGCLASS=A/g" \
        | sed "s/(ACCT)//g" \
        | sed "s|&HLQ\.\.|${hlq}.|g" \
        | sed "s|&HLQ\.|${hlq}.|g" \
        | sed "s|&CICSHLQ\.\.|${cicshlq}.|g" \
        | sed "s|&CICSHLQ\.|${cicshlq}.|g" \
        | sed "s|&COBHLQ\.\.|${cobhlq}.|g" \
        | sed "s|&COBHLQ\.|${cobhlq}.|g" \
        | sed "s|&DB2HLQ\.\.|${db2hlq}.|g" \
        | sed "s|&DB2HLQ\.|${db2hlq}.|g" \
        | sed "/^\/\/[[:space:]]*SET /d" \
        | docker exec -i "${_DOCKER_CONTAINER}" bash -c \
            "exec 3<>/dev/tcp/127.0.0.1/${_CARDREADER_PORT}; cat >&3; exec 3>&-" \
        2>/dev/null \
        && ok "JCL envoyé au lecteur de cartes JES2" \
        || { fail "Échec envoi lecteur de cartes"; return 1; }
}

# ============================================================
# Soumettre un JCL local via TSO SUBMIT * (inline)
# OBSOLÈTE : utiliser submit_jcl_cardreader() à la place.
# Conservé pour référence / fallback sans docker.
# ============================================================
submit_jcl_inline() {
    local jclfile="$1"
    local jobname="${2:-JOB}"
    info "Soumission inline (TSO) de $(basename "$jclfile")..."

    [[ -f "$jclfile" ]] || { fail "JCL introuvable : $jclfile"; return 1; }

    s3270_start || { fail "s3270 ne démarre pas"; return 1; }
    s3270_login

    # TSO READY → SUBMIT *
    s3270_cmd "String(\"SUBMIT *\")" >/dev/null
    s3270_cmd "Enter()" 10 >/dev/null
    sleep 1

    # Envoyer chaque ligne JCL
    while IFS= read -r line; do
        [[ -z "$line" ]] && continue
        local safe
        safe=$(printf '%s' "$line" | sed 's/\\/\\\\/g; s/"/\\"/g')
        s3270_cmd "String(\"${safe}\")" 5 >/dev/null
        s3270_cmd "Enter()" 5 >/dev/null
    done < "$jclfile"

    # Terminer la soumission inline
    s3270_cmd "String(\"/*\")" >/dev/null
    s3270_cmd "Enter()" 30 >/dev/null
    sleep 3

    s3270_stop

    info "Statut JES2 :"
    herc_cmd '$D A' | grep -i "$jobname" || echo "  (job terminé ou non trouvé)"
}

# ============================================================
# Attendre la fin d'un job via HASP395 dans le syslog
# ============================================================
wait_job() {
    local jobname="$1"
    local maxwait="${2:-120}"

    # Récupérer le numéro JES2 du job le plus récent (JOB NNNN)
    local jobnum=""
    local attempts=0
    while [[ -z "$jobnum" && $attempts -lt 10 ]]; do
        sleep 2
        attempts=$(( attempts + 1 ))
        local sl
        sl=$(curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=30" \
            | sed 's/<[^>]*>//g' 2>/dev/null || true)
        jobnum=$(echo "$sl" \
            | grep -i "HASP100.*${jobname}" \
            | tail -1 \
            | grep -oE 'JOB[[:space:]]+[0-9]+' \
            | tr -s ' ' | tail -1)
    done

    if [[ -z "$jobnum" ]]; then
        warn "Numéro JES2 non trouvé pour ${jobname} — surveillance par nom uniquement"
    else
        info "Job soumis : ${jobnum} (${jobname})"
    fi

    local elapsed=0
    printf "  Attente fin de %s " "$jobname"
    while [[ $elapsed -lt $maxwait ]]; do
        sleep 5
        elapsed=$(( elapsed + 5 ))
        printf "."
        local syslog
        syslog=$(curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=80" \
            | sed 's/<[^>]*>//g' 2>/dev/null || true)
        local match_pattern="${jobname}"
        [[ -n "$jobnum" ]] && match_pattern="${jobnum}.*HASP395\|HASP395.*${jobname}"
        if echo "$syslog" | grep -qi "${match_pattern}"; then
            echo " terminé (${elapsed}s)"
            if echo "$syslog" | grep -qi "ABEND.*${jobname}\|${jobname}.*ABEND"; then
                fail "Job ${jobname} terminé en ABEND — vérifier le syslog"
            elif [[ -n "$jobnum" ]] && echo "$syslog" | grep -qi "${jobnum}.*IEF453\|IEF453.*${jobname}"; then
                fail "Job ${jobname} terminé en erreur JCL (IEF453I)"
            else
                ok "Job ${jobname} terminé avec succès"
            fi
            return 0
        fi
    done
    echo " timeout (${maxwait}s) — vérifier syslog : bash mvs/herc.sh log 50"
    return 1
}

# ============================================================
# Lire la spool récente (via Hercules syslog)
# ============================================================
show_spool() {
    echo "--- Syslog récent ---"
    curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=30" \
        | sed 's/<[^>]*>//g' | grep -v "^Command:\|^Only\|^Refresh\|^$" | tail -20
}

# ============================================================
# Main
# ============================================================
echo "=== Soumission JCL ${PROJECT_LABEL} → MVS TK5 ==="
echo "Hôte  : ${TK5_HOST}:${TK5_PORT}"
echo "HLQ   : ${HLQ}"
echo ""

ACTION="${1:-help}"

case "$ACTION" in
    alloc)
        echo "--- Allocation des datasets MVS ---"
        echo "(IMPORTANT : à faire une seule fois avant la première compilation)"
        [[ -n "${JCL_ALLOC_FILE:-}" && -f "${JCL_ALLOC_FILE}" ]] \
            || { fail "JCL_ALLOC_FILE non défini ou absent"; exit 1; }
        submit_jcl_cardreader "${JCL_ALLOC_FILE}" "${JCL_ALLOC_JOBNAME}"
        sleep 5
        if curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=30" \
                | sed 's/<[^>]*>//g' | grep -qi "HASP100.*${JCL_ALLOC_JOBNAME}\|${JCL_ALLOC_JOBNAME}.*SUBM"; then
            info "Job ${JCL_ALLOC_JOBNAME} reçu par JES2"
        else
            warn "${JCL_ALLOC_JOBNAME} non visible dans syslog — vérifier la connexion TK5"
        fi
        wait_job "${JCL_ALLOC_JOBNAME}" 120
        show_spool
        ok "Datasets alloués. Uploader les sources maintenant :"
        echo "   bash mvs/01_upload.sh"
        ;;

    bms)
        if [[ -z "${JCL_BMS_FILE:-}" ]]; then
            info "Projet ${PROJECT_LABEL} : pas de BMS configuré — skip"
            exit 0
        fi
        echo "--- Assemblage BMS ---"
        [[ -f "${JCL_BMS_FILE}" ]] || { fail "JCL_BMS_FILE introuvable : ${JCL_BMS_FILE}"; exit 1; }
        submit_jcl_cardreader "${JCL_BMS_FILE}" "${JCL_BMS_JOBNAME}"
        wait_job "${JCL_BMS_JOBNAME}" 300
        show_spool
        ;;

    cobol)
        echo "--- Compilation COBOL ---"
        if [[ -n "${MVS_COMPILE_CMD:-}" ]]; then
            info "Délégation compilation : ${MVS_COMPILE_CMD}"
            eval "${MVS_COMPILE_CMD}"
        else
            [[ -n "${JCL_COMP_FILE:-}" && -f "${JCL_COMP_FILE}" ]] \
                || { fail "JCL_COMP_FILE non défini ou absent"; exit 1; }
            submit_jcl_cardreader "${JCL_COMP_FILE}" "${JCL_COMP_JOBNAME}"
            wait_job "${JCL_COMP_JOBNAME}" 600
            show_spool
        fi
        ;;

    all)
        echo "--- Séquence complète (alloc → upload → bms → cobol) ---"
        echo "Étape 1/4 : allocation"
        [[ -n "${JCL_ALLOC_FILE:-}" && -f "${JCL_ALLOC_FILE}" ]] \
            || { fail "JCL_ALLOC_FILE non défini ou absent"; exit 1; }
        submit_jcl_cardreader "${JCL_ALLOC_FILE}" "${JCL_ALLOC_JOBNAME}"
        wait_job "${JCL_ALLOC_JOBNAME}" 120

        echo "Étape 2/4 : upload des sources"
        bash "${MVS_DIR}/01_upload.sh"

        if [[ -n "${JCL_BMS_FILE:-}" && -f "${JCL_BMS_FILE}" ]]; then
            echo "Étape 3/4 : assemblage BMS"
            submit_jcl_cardreader "${JCL_BMS_FILE}" "${JCL_BMS_JOBNAME}"
            wait_job "${JCL_BMS_JOBNAME}" 300
        else
            echo "Étape 3/4 : assemblage BMS — skip (pas de BMS)"
        fi

        echo "Étape 4/4 : compilation COBOL"
        if [[ -n "${MVS_COMPILE_CMD:-}" ]]; then
            eval "${MVS_COMPILE_CMD}"
        else
            [[ -n "${JCL_COMP_FILE:-}" && -f "${JCL_COMP_FILE}" ]] \
                || { fail "JCL_COMP_FILE non défini ou absent"; exit 1; }
            submit_jcl_cardreader "${JCL_COMP_FILE}" "${JCL_COMP_JOBNAME}"
            wait_job "${JCL_COMP_JOBNAME}" 600
        fi

        show_spool
        ok "Séquence terminée."
        if [[ "${HAS_CICS:-0}" == "1" ]]; then
            echo "   bash mvs/03_cics.sh"
        fi
        ;;

    watch)
        echo "Surveillance syslog MVS (Ctrl-C pour arrêter)..."
        while true; do
            clear
            echo "=== MVS SYSLOG — $(date '+%H:%M:%S') ==="
            curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=40" \
                | sed 's/<[^>]*>//g' | grep -v "^Command:\|^Only\|^Refresh\|^$" | tail -35
            sleep 3
        done
        ;;

    status)
        echo "--- Jobs actifs ---"
        herc_cmd '$D A'
        echo ""
        echo "--- Initiateurs JES2 ---"
        herc_cmd '$D I'
        ;;

    *)
        echo "Usage: $0 {alloc|bms|cobol|all|watch|status}"
        echo ""
        echo "Ordre recommandé (première installation) :"
        echo "  1. $0 alloc           # créer les datasets MVS"
        echo "  2. bash mvs/01_upload.sh  # uploader les sources"
        if [[ -n "${JCL_BMS_FILE:-}" ]]; then
            echo "  3. $0 bms             # assembler les mapsets BMS"
        fi
        echo "  4. $0 cobol           # compiler les programmes"
        if [[ "${HAS_CICS:-0}" == "1" ]]; then
            echo "  5. bash mvs/03_cics.sh install  # définir transactions CICS"
        fi
        ;;
esac
