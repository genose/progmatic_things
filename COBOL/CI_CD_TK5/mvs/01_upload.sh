#!/usr/bin/env bash
# ============================================================
# 01_upload.sh — Upload des sources vers MVS (générique)
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
#
# Mode par défaut : IEBUPDTE via lecteur de cartes (port 3505)
#   - Rapide, pas de s3270 requis
#   - Utilise docker exec + /dev/tcp/127.0.0.1/3505
#
# Fallback : IND$FILE via TN3270/s3270 (--indffile)
#
# Usage :
#   bash mvs/01_upload.sh            # tout uploader
#   bash mvs/01_upload.sh --bms      # BMS seulement
#   bash mvs/01_upload.sh --jcl      # JCL seulement
#   bash mvs/01_upload.sh --cbl      # COBOL seulement
#   bash mvs/01_upload.sh --indffile # mode IND$FILE
#
# Configuration projet via PROJECT_NAME (défaut: gstk) :
#   PROJECT_NAME=crm bash mvs/01_upload.sh
# ============================================================
set -euo pipefail

CI_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "${CI_DIR}/lib/project.sh"
source "${CI_DIR}/mvs/s3270_lib.sh"

TK5_HOST="${TK5_HOST:-localhost}"
TK5_PORT="${TK5_PORT:-3270}"
TSO_USER="${TSO_USER:-HERC02}"
TSO_PASS="${TSO_PASS:-CUL8TR}"
HLQ="${HLQ:-HERC02}"
HERC_URL="${HERC_URL:-http://localhost:8038}"
_DOCKER_CONTAINER="${DOCKER_CONTAINER:-mvs-tk5}"
_CARDREADER_PORT="${CARDREADER_PORT:-3505}"

MVS_DIR="$(dirname "${BASH_SOURCE[0]}")"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
ok()   { echo -e "  ${GREEN}✓${NC} $*"; }
fail() { echo -e "  ${RED}✗${NC} $*"; (( ERRORS++ )) || true; }
warn() { echo -e "  ${YELLOW}⚠${NC}  $*"; }
info() { echo -e "  ${CYAN}ℹ${NC}  $*"; }

ERRORS=0

# ============================================================
# Générer un job IEBUPDTE et l'envoyer au lecteur de cartes.
#
# $1 = jobname (≤8 chars)
# $2 = dataset MVS cible (ex: HERC02.GSTK.SOURCE)
# $3... = paires "localfile:MEMBERNAME"
#
# Génère le JCL en bash pure (sans sous-processus python) :
#   - Chaque ligne de source paddée à exactement 80 chars
#   - Délimiteur ZZ pour éviter le conflit avec /*
# ============================================================
cardreader_iebupdte() {
    local jobname="$1"
    local target_ds="$2"
    shift 2
    local pairs=("$@")

    if ! docker inspect "${_DOCKER_CONTAINER}" >/dev/null 2>&1; then
        fail "Container ${_DOCKER_CONTAINER} non accessible"
        return 1
    fi

    info "IEBUPDTE → ${target_ds} (${#pairs[@]} membre(s))..."

    local jname
    jname=$(printf '%-8s' "${jobname:0:8}" | tr '[:lower:]' '[:upper:]')

    # Générer le JCL et l'envoyer directement au lecteur de cartes
    {
        printf '//%s JOB ,'"'"'UPLOAD'"'"',CLASS=A,MSGCLASS=A,\n' "${jname}"
        printf '//             MSGLEVEL=(1,1),NOTIFY=%s,\n'        "${TSO_USER}"
        printf '//             USER=%s,PASSWORD=%s\n'               "${TSO_USER}" "${TSO_PASS}"
        printf '//STEP1   EXEC PGM=IEBUPDTE,PARM=NEW\n'
        printf '//SYSPRINT DD SYSOUT=A\n'
        printf '//SYSUT2   DD DSN=%s,DISP=OLD\n'                   "${target_ds}"
        printf '//SYSIN    DD DATA,DLM=ZZ\n'

        local pair localfile membername
        for pair in "${pairs[@]}"; do
            localfile="${pair%%:*}"
            membername="${pair##*:}"
            membername=$(printf '%s' "${membername:0:8}" | tr '[:lower:]' '[:upper:]')

            printf './ ADD NAME=%s\n' "${membername}"

            if [[ -f "${localfile}" ]]; then
                while IFS= read -r line || [[ -n "${line}" ]]; do
                    # Padder/tronquer à exactement 80 caractères
                    printf '%-80.80s\n' "${line}"
                done < "${localfile}"
            else
                warn "Fichier introuvable : ${localfile}" >&2
            fi
        done

        printf 'ZZ\n'
    } | docker exec -i "${_DOCKER_CONTAINER}" bash -c \
        "exec 3<>/dev/tcp/127.0.0.1/${_CARDREADER_PORT}; cat >&3; exec 3>&-" \
        2>/dev/null \
        && ok "JCL envoyé — ${jname} vers ${target_ds}" \
        || { fail "Échec envoi lecteur de cartes (${jname})"; return 1; }
}

# ============================================================
# Attendre la fin d'un job via le syslog Hercules
# ============================================================
wait_upload_job() {
    local jobname="$1"
    local maxwait="${2:-120}"
    local elapsed=0

    # Attendre d'abord que le job apparaisse (HASP100)
    local appeared=0
    local tries=0
    while [[ $tries -lt 12 && $appeared -eq 0 ]]; do
        sleep 2; tries=$(( tries + 1 ))
        local sl
        sl=$(curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=20" \
            | sed 's/<[^>]*>//g' 2>/dev/null || true)
        echo "$sl" | grep -qi "HASP100.*${jobname}" && appeared=1
    done

    printf "  Attente fin de %-10s " "${jobname}"
    while [[ $elapsed -lt $maxwait ]]; do
        sleep 5; elapsed=$(( elapsed + 5 )); printf "."
        local sl
        sl=$(curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=60" \
            | sed 's/<[^>]*>//g' 2>/dev/null || true)
        if echo "$sl" | grep -qi "HASP395.*${jobname}\|HASP396.*${jobname}"; then
            echo " terminé (${elapsed}s)"
            if echo "$sl" | grep -qi "IEF452I.*${jobname}\|IEF453I.*${jobname}\|ABEND.*${jobname}"; then
                fail "${jobname} terminé en erreur — vérifier le spool"
                return 1
            fi
            ok "${jobname} terminé avec succès"
            return 0
        fi
    done
    warn "${jobname} timeout (${maxwait}s)"
    return 1
}

# ============================================================
# Construire les paires "fichier_local:MEMBRE" pour les sources COBOL
# Depuis CBL_FILES (liste explicite) ou CBL_PATTERN (glob)
# ============================================================
_build_cbl_pairs() {
    local -n _pairs_ref="$1"

    # Copybooks en premier
    local cpair
    for cpair in "${COPYBOOK_FILES[@]+"${COPYBOOK_FILES[@]}"}"; do
        local cfile="${cpair%%:*}"
        local cmember="${cpair##*:}"
        _pairs_ref+=("${PROJECT_DIR}/${cfile}:${cmember}")
    done

    # Sources COBOL
    if [[ -n "${CBL_PATTERN}" ]]; then
        for f in "${PROJECT_DIR}"/${CBL_PATTERN}; do
            [[ -f "$f" ]] || continue
            local base; base=$(basename "$f")
            local ext="${base##*.}"
            local stem="${base%.*}"
            local member; member=$(printf '%s' "${stem:0:8}" | tr '[:lower:]' '[:upper:]')
            _pairs_ref+=("${f}:${member}")
        done
    elif [[ ${#CBL_FILES[@]} -gt 0 ]]; then
        local pair
        for pair in "${CBL_FILES[@]}"; do
            local lf="${pair%%:*}"
            local mb="${pair##*:}"
            _pairs_ref+=("${PROJECT_DIR}/${lf}:${mb}")
        done
    fi
}

# ============================================================
# Groupes d'upload — mode lecteur de cartes
# ============================================================
upload_sources_cr() {
    echo "--- Sources COBOL (lecteur de cartes) ---"
    local pairs=()
    _build_cbl_pairs pairs
    [[ ${#pairs[@]} -eq 0 ]] && { warn "Aucune source COBOL à uploader"; return 0; }
    cardreader_iebupdte "${JCL_UPLOAD_CBL_JOBNAME}" "${HLQ}.${APP_SUFFIX}.SOURCE" "${pairs[@]}" \
        && wait_upload_job "${JCL_UPLOAD_CBL_JOBNAME}" 120
}

upload_bms_cr() {
    if [[ -z "${BMS_PATTERN}" && ${#BMS_FILES[@]} -eq 0 ]]; then
        info "Pas de BMS configurés pour ${PROJECT_LABEL} — skip"
        return 0
    fi
    echo "--- BMS Mapsets (lecteur de cartes) ---"
    local pairs=()
    if [[ -n "${BMS_PATTERN}" ]]; then
        for f in "${PROJECT_DIR}"/${BMS_PATTERN}; do
            [[ -f "$f" ]] || continue
            local base; base=$(basename "$f" .bms)
            pairs+=("${f}:${base}")
        done
    else
        local pair
        for pair in "${BMS_FILES[@]}"; do
            pairs+=("${PROJECT_DIR}/${pair%%:*}:${pair##*:}")
        done
    fi
    [[ ${#pairs[@]} -eq 0 ]] && { warn "Aucun BMS trouvé"; return 0; }
    cardreader_iebupdte "${JCL_UPLOAD_BMS_JOBNAME}" "${HLQ}.${APP_SUFFIX}.BMS" "${pairs[@]}" \
        && wait_upload_job "${JCL_UPLOAD_BMS_JOBNAME}" 120
}

upload_copybook_cr() {
    if [[ ${#COPYBOOK_FILES[@]} -eq 0 ]]; then
        info "Pas de copybooks configurés pour ${PROJECT_LABEL} — skip"
        return 0
    fi
    echo "--- Copybook (lecteur de cartes) ---"
    local pairs=()
    local cpair
    for cpair in "${COPYBOOK_FILES[@]}"; do
        pairs+=("${PROJECT_DIR}/${cpair%%:*}:${cpair##*:}")
    done
    cardreader_iebupdte "${JCL_UPLOAD_CPY_JOBNAME}" "${HLQ}.${APP_SUFFIX}.COPYLIB" "${pairs[@]}" \
        && wait_upload_job "${JCL_UPLOAD_CPY_JOBNAME}" 60
}

upload_jcl_cr() {
    echo "--- JCL (lecteur de cartes) ---"
    local pairs=()
    [[ -n "${JCL_ALLOC_FILE:-}" && -f "${JCL_ALLOC_FILE}" ]] \
        && pairs+=("${JCL_ALLOC_FILE}:${JCL_ALLOC_JOBNAME}")
    [[ -n "${JCL_BMS_FILE:-}"   && -f "${JCL_BMS_FILE}"   ]] \
        && pairs+=("${JCL_BMS_FILE}:${JCL_BMS_MEMBER}")
    [[ -n "${JCL_COMP_FILE:-}"  && -f "${JCL_COMP_FILE}"  ]] \
        && pairs+=("${JCL_COMP_FILE}:${JCL_COMP_MEMBER}")
    [[ ${#pairs[@]} -eq 0 ]] && { warn "Aucun JCL local trouvé — skip"; return 0; }
    cardreader_iebupdte "${JCL_UPLOAD_JCL_JOBNAME}" "${HLQ}.${APP_SUFFIX}.JCL" "${pairs[@]}" \
        && wait_upload_job "${JCL_UPLOAD_JCL_JOBNAME}" 60
}

# ============================================================
# Groupes d'upload — mode IND$FILE (s3270)
# ============================================================
_SESSION_ACTIVE=0
trap 's3270_stop' EXIT

ensure_session() {
    if [[ $_SESSION_ACTIVE -eq 0 ]]; then
        echo "  Connexion TSO..."
        s3270_start || { fail "s3270 ne démarre pas"; exit 1; }
        s3270_login
        _SESSION_ACTIVE=1
    fi
}

transfer_file() {
    local localfile="$1" mvs_ds="$2" lrecl="${3:-80}"
    local blksize=$(( lrecl * 39 ))
    [[ -f "$localfile" ]] || { fail "Introuvable : $localfile"; return 1; }
    printf "  %-30s → %-40s\n" "$(basename "$localfile")" "'${mvs_ds}'"
    ensure_session
    s3270_cmd \
        "Transfer(Direction=send,HostFile=\"'${mvs_ds}'\",LocalFile=\"${localfile}\",Host=tso,Recfm=fixed,Lrecl=${lrecl},BlockSize=${blksize},Cr=add)" \
        120 >/dev/null \
        && ok "OK" || fail "ERREUR"
}

upload_sources_indf() {
    echo "--- Sources COBOL (IND\$FILE) ---"
    local cpair
    for cpair in "${COPYBOOK_FILES[@]+"${COPYBOOK_FILES[@]}"}"; do
        local lf="${cpair%%:*}"; local mb="${cpair##*:}"
        transfer_file "${PROJECT_DIR}/${lf}" "${HLQ}.${APP_SUFFIX}.COPYLIB(${mb})"
    done
    if [[ -n "${CBL_PATTERN}" ]]; then
        for f in "${PROJECT_DIR}"/${CBL_PATTERN}; do
            [[ -f "$f" ]] || continue
            local base; base=$(basename "$f")
            local stem="${base%.*}"
            local member; member=$(printf '%s' "${stem:0:8}" | tr '[:lower:]' '[:upper:]')
            transfer_file "$f" "${HLQ}.${APP_SUFFIX}.SOURCE(${member})"
        done
    else
        local pair
        for pair in "${CBL_FILES[@]+"${CBL_FILES[@]}"}"; do
            transfer_file "${PROJECT_DIR}/${pair%%:*}" "${HLQ}.${APP_SUFFIX}.SOURCE(${pair##*:})"
        done
    fi
}

upload_bms_indf() {
    if [[ -z "${BMS_PATTERN}" && ${#BMS_FILES[@]} -eq 0 ]]; then
        info "Pas de BMS configurés — skip"
        return 0
    fi
    echo "--- BMS Mapsets (IND\$FILE) ---"
    if [[ -n "${BMS_PATTERN}" ]]; then
        for f in "${PROJECT_DIR}"/${BMS_PATTERN}; do
            [[ -f "$f" ]] || continue
            local base; base=$(basename "$f" .bms)
            transfer_file "$f" "${HLQ}.${APP_SUFFIX}.BMS(${base})"
        done
    else
        local pair
        for pair in "${BMS_FILES[@]}"; do
            transfer_file "${PROJECT_DIR}/${pair%%:*}" "${HLQ}.${APP_SUFFIX}.BMS(${pair##*:})"
        done
    fi
}

upload_jcl_indf() {
    echo "--- JCL (IND\$FILE) ---"
    [[ -n "${JCL_BMS_FILE:-}"  && -f "${JCL_BMS_FILE}"  ]] \
        && transfer_file "${JCL_BMS_FILE}"  "${HLQ}.${APP_SUFFIX}.JCL(${JCL_BMS_MEMBER})"
    [[ -n "${JCL_COMP_FILE:-}" && -f "${JCL_COMP_FILE}" ]] \
        && transfer_file "${JCL_COMP_FILE}" "${HLQ}.${APP_SUFFIX}.JCL(${JCL_COMP_MEMBER})"
    [[ -n "${JCL_ALLOC_FILE:-}" && -f "${JCL_ALLOC_FILE}" ]] \
        && transfer_file "${JCL_ALLOC_FILE}" "${HLQ}.${APP_SUFFIX}.JCL(${JCL_ALLOC_JOBNAME})"
}

# ============================================================
# Main
# ============================================================
echo "=== Upload ${PROJECT_LABEL} → MVS TK5 ==="
echo "HLQ : ${HLQ}  /  User : ${TSO_USER}"
echo ""

# Si MVS_UPLOAD_CMD défini, déléguer entièrement
if [[ -n "${MVS_UPLOAD_CMD:-}" ]]; then
    info "Délégation upload : ${MVS_UPLOAD_CMD}"
    eval "${MVS_UPLOAD_CMD}" "$@"
    exit $?
fi

MODE="cardreader"
POSITIONAL=()
for arg in "$@"; do
    case "$arg" in
        --indffile)   MODE="indffile" ;;
        --cardreader) MODE="cardreader" ;;
        *)            POSITIONAL+=("$arg") ;;
    esac
done
set -- "${POSITIONAL[@]+"${POSITIONAL[@]}"}"
ACTION="${1:-all}"

if [[ "$MODE" == "indffile" ]]; then
    [[ -x "${S3270:-}" ]] || { echo "${RED}s3270 introuvable${NC}"; exit 1; }
    nc -z "$TK5_HOST" "$TK5_PORT" 2>/dev/null || { echo "${RED}TK5 non joignable${NC}"; exit 1; }
    case "$ACTION" in
        all)   upload_sources_indf; upload_bms_indf; upload_jcl_indf ;;
        --bms) upload_bms_indf ;;
        --jcl) upload_jcl_indf ;;
        --cbl) upload_sources_indf ;;
        *)     echo "Usage: $0 [--indffile] [all|--bms|--jcl|--cbl]"; exit 1 ;;
    esac
else
    docker inspect "${_DOCKER_CONTAINER}" >/dev/null 2>&1 \
        || { echo -e "${RED}Container ${_DOCKER_CONTAINER} non accessible${NC}"; exit 1; }
    case "$ACTION" in
        all)   upload_copybook_cr; upload_sources_cr; upload_bms_cr; upload_jcl_cr ;;
        --bms) upload_bms_cr ;;
        --jcl) upload_jcl_cr ;;
        --cbl) upload_copybook_cr; upload_sources_cr ;;
        *)     echo "Usage: $0 [--cardreader|--indffile] [all|--bms|--jcl|--cbl]"; exit 1 ;;
    esac
fi

echo ""
if [[ $ERRORS -eq 0 ]]; then
    echo -e "${GREEN}✓ Upload terminé${NC}"
else
    echo -e "${RED}✗ ${ERRORS} erreur(s)${NC}"
fi
exit $ERRORS
