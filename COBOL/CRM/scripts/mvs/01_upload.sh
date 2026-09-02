#!/usr/bin/env bash
# ============================================================
# 01_upload.sh — Upload des sources COBOL CRM vers MVS TK5
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
#   bash scripts/mvs/01_upload.sh            # tout uploader
#   bash scripts/mvs/01_upload.sh --cbl      # COBOL seulement
#   bash scripts/mvs/01_upload.sh --jcl      # JCL seulement
#   bash scripts/mvs/01_upload.sh --indffile # mode IND$FILE
# ============================================================
set -euo pipefail

CRM_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
MVS_DIR="$(dirname "${BASH_SOURCE[0]}")"
CI_MVS_DIR="$(cd "${CRM_DIR}/../../CI_CD_TK5/mvs" 2>/dev/null && pwd || echo "")"

TK5_HOST="${TK5_HOST:-localhost}"
TK5_PORT="${TK5_PORT:-3270}"
TSO_USER="${TSO_USER:-HERC02}"
TSO_PASS="${TSO_PASS:-CUL8TR}"
HLQ="${HLQ:-HERC02}"
HERC_URL="${HERC_URL:-http://localhost:8038}"
_DOCKER_CONTAINER="${DOCKER_CONTAINER:-mvs-tk5}"
_CARDREADER_PORT="${CARDREADER_PORT:-3505}"

# Réutiliser s3270_lib.sh du CI_CD_TK5 s'il est disponible
if [[ -n "${CI_MVS_DIR}" && -f "${CI_MVS_DIR}/s3270_lib.sh" ]]; then
    source "${CI_MVS_DIR}/s3270_lib.sh"
fi

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
ok()   { echo -e "  ${GREEN}OK${NC}  $*"; }
fail() { echo -e "  ${RED}FAIL${NC} $*"; (( ERRORS++ )) || true; }
warn() { echo -e "  ${YELLOW}WARN${NC} $*"; }
info() { echo -e "  ${CYAN}INFO${NC} $*"; }

ERRORS=0

# ============================================================
# Envoyer un job IEBUPDTE au lecteur de cartes Hercules.
# $1 = jobname  $2 = dataset MVS cible  $3... = "local:MEMBRE"
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

    {
        printf '//%s JOB ,'"'"'CRM UPLOAD'"'"',CLASS=A,MSGCLASS=A,\n'  "${jname}"
        printf '//             MSGLEVEL=(1,1),NOTIFY=%s,\n'              "${TSO_USER}"
        printf '//             USER=%s,PASSWORD=%s\n'                    "${TSO_USER}" "${TSO_PASS}"
        printf '//STEP1   EXEC PGM=IEBUPDTE,PARM=NEW\n'
        printf '//SYSPRINT DD SYSOUT=A\n'
        printf '//SYSUT2   DD DSN=%s,DISP=OLD\n'                        "${target_ds}"
        printf '//SYSIN    DD DATA,DLM=ZZ\n'

        local pair localfile membername
        for pair in "${pairs[@]}"; do
            localfile="${pair%%:*}"
            membername="${pair##*:}"
            membername=$(printf '%s' "${membername:0:8}" | tr '[:lower:]' '[:upper:]')
            printf './ ADD NAME=%s\n' "${membername}"
            if [[ -f "${localfile}" ]]; then
                while IFS= read -r line || [[ -n "${line}" ]]; do
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
        && ok "Envoyé : ${jname} → ${target_ds}" \
        || { fail "Echec envoi lecteur de cartes (${jname})"; return 1; }
}

wait_upload_job() {
    local jobname="$1"
    local maxwait="${2:-120}"
    local elapsed=0

    local appeared=0 tries=0
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
            echo " termine (${elapsed}s)"
            if echo "$sl" | grep -qi "IEF452I.*${jobname}\|ABEND.*${jobname}"; then
                fail "${jobname} termine en erreur — verifier le spool"
                return 1
            fi
            ok "${jobname} termine avec succes"
            return 0
        fi
    done
    warn "${jobname} timeout (${maxwait}s)"
    return 1
}

# ============================================================
# Sources COBOL CRM
# ============================================================
upload_sources_cr() {
    echo "--- Sources COBOL CRM (lecteur de cartes) ---"
    local pairs=(
        "${CRM_DIR}/D05_VERIF_CRM.SCO:D05VERIF"
        "${CRM_DIR}/T10_MAJ_DTLIVR_BDCRM.COB:T10MAJDT"
        "${CRM_DIR}/D02_EXTCDE_CRMCSP1.COB:D02EXTCD"
        "${CRM_DIR}/D05_INTCDEFAC_CRM_V2.SCO:D05INTCD"
    )
    cardreader_iebupdte "CRMUSR" "${HLQ}.CRM.SOURCE" "${pairs[@]}" \
        && wait_upload_job "CRMUSR" 120
}

upload_jcl_cr() {
    echo "--- JCL CRM (lecteur de cartes) ---"
    local alloc_jcl="${MVS_DIR}/00_alloc.jcl"
    local comp_jcl="${MVS_DIR}/CRMCOMP.jcl"
    local pairs=()
    [[ -f "${alloc_jcl}" ]] && pairs+=("${alloc_jcl}:CRMALLOC")
    [[ -f "${comp_jcl}" ]]  && pairs+=("${comp_jcl}:CRMCOMP")
    if [[ ${#pairs[@]} -eq 0 ]]; then
        warn "Aucun JCL CRM trouve dans ${MVS_DIR} — skip"
        return 0
    fi
    cardreader_iebupdte "CRMUJCL" "${HLQ}.CRM.JCL" "${pairs[@]}" \
        && wait_upload_job "CRMUJCL" 60
}

# ============================================================
# Mode IND$FILE (s3270)
# ============================================================
transfer_file() {
    local localfile="$1" mvs_ds="$2" lrecl="${3:-80}"
    local blksize=$(( lrecl * 39 ))
    [[ -f "$localfile" ]] || { fail "Introuvable : $localfile"; return 1; }
    printf "  %-35s → %s\n" "$(basename "$localfile")" "'${mvs_ds}'"
    s3270_cmd \
        "Transfer(Direction=send,HostFile=\"'${mvs_ds}'\",LocalFile=\"${localfile}\",Host=tso,Recfm=fixed,Lrecl=${lrecl},BlockSize=${blksize},Cr=add)" \
        120 >/dev/null \
        && ok "OK" || fail "ERREUR"
}

_SESSION_ACTIVE=0
trap 's3270_stop 2>/dev/null || true' EXIT

ensure_session() {
    if [[ $_SESSION_ACTIVE -eq 0 ]]; then
        echo "  Connexion TSO..."
        s3270_start || { fail "s3270 ne demarre pas"; exit 1; }
        s3270_login
        _SESSION_ACTIVE=1
    fi
}

upload_sources_indf() {
    echo "--- Sources COBOL CRM (IND\$FILE) ---"
    ensure_session
    transfer_file "${CRM_DIR}/D05_VERIF_CRM.SCO"         "${HLQ}.CRM.SOURCE(D05VERIF)"
    transfer_file "${CRM_DIR}/T10_MAJ_DTLIVR_BDCRM.COB"  "${HLQ}.CRM.SOURCE(T10MAJDT)"
    transfer_file "${CRM_DIR}/D02_EXTCDE_CRMCSP1.COB"    "${HLQ}.CRM.SOURCE(D02EXTCD)"
    transfer_file "${CRM_DIR}/D05_INTCDEFAC_CRM_V2.SCO"  "${HLQ}.CRM.SOURCE(D05INTCD)"
}

# ============================================================
# Main
# ============================================================
echo "=== Upload CRM → MVS TK5 ==="
echo "HLQ  : ${HLQ}  /  User : ${TSO_USER}"
echo ""

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
    if ! type s3270_start &>/dev/null; then
        echo -e "${RED}s3270_lib.sh non charge (CI_CD_TK5 absent)${NC}"; exit 1
    fi
    nc -z "$TK5_HOST" "$TK5_PORT" 2>/dev/null \
        || { echo -e "${RED}TK5 non joignable (${TK5_HOST}:${TK5_PORT})${NC}"; exit 1; }
    case "$ACTION" in
        all)   upload_sources_indf ;;
        --cbl) upload_sources_indf ;;
        *)     echo "Usage: $0 [--indffile] [all|--cbl]"; exit 1 ;;
    esac
else
    docker inspect "${_DOCKER_CONTAINER}" >/dev/null 2>&1 \
        || { echo -e "${RED}Container ${_DOCKER_CONTAINER} non accessible${NC}"; exit 1; }
    case "$ACTION" in
        all)   upload_sources_cr; upload_jcl_cr ;;
        --cbl) upload_sources_cr ;;
        --jcl) upload_jcl_cr ;;
        *)     echo "Usage: $0 [--cardreader|--indffile] [all|--cbl|--jcl]"; exit 1 ;;
    esac
fi

echo ""
if [[ $ERRORS -eq 0 ]]; then
    echo -e "${GREEN}Upload CRM termine${NC}"
else
    echo -e "${RED}${ERRORS} erreur(s)${NC}"
fi
exit $ERRORS
