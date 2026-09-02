#!/usr/bin/env bash
# ============================================================
# 06_build.sh — Build incrémental sur MVS TK5 (générique)
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
#
# Détecte les fichiers modifiés depuis le dernier build via MD5.
# N'uploade et ne recompile que ce qui a changé.
#
# Usage :
#   bash mvs/06_build.sh          # build incrémental
#   bash mvs/06_build.sh --full   # forcer full rebuild
#   bash mvs/06_build.sh --dry    # voir ce qui changerait
#
# Configuration projet via PROJECT_NAME (défaut: gstk) :
#   PROJECT_NAME=crm bash mvs/06_build.sh
# ============================================================
set -euo pipefail

CI_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "${CI_DIR}/lib/project.sh"

MVS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CHECKSUM_FILE="${MVS_DIR}/.checksums_${PROJECT_NAME}"
BUILD_LOG="${MVS_DIR}/.build.log"

TK5_HOST="${TK5_HOST:-localhost}"
TK5_PORT="${TK5_PORT:-3270}"
TSO_USER="${TSO_USER:-HERC02}"
TSO_PASS="${TSO_PASS:-CUL8TR}"
HLQ="${HLQ:-HERC02}"
HERC_URL="${HERC_URL:-http://localhost:8038}"

source "${MVS_DIR}/s3270_lib.sh"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
ok()      { echo -e "  ${GREEN}✓${NC} $*"; }
changed() { echo -e "  ${YELLOW}~${NC} $*"; }
skip()    { echo -e "  -  $*"; }
fail()    { echo -e "  ${RED}✗${NC} $*"; (( ERRORS++ )) || true; }

ERRORS=0
FORCE=0
DRY=0
[[ "${1:-}" == "--full" ]] && FORCE=1
[[ "${1:-}" == "--dry"  ]] && DRY=1

trap 's3270_stop' EXIT

# ============================================================
# MD5 portable (macOS md5 ou Linux md5sum)
# ============================================================
file_md5() {
    if command -v md5sum >/dev/null 2>&1; then
        md5sum "$1" | awk '{print $1}'
    else
        md5 -q "$1"
    fi
}

# ============================================================
# Charger / sauver les checksums
# ============================================================
declare -A OLD_CHECKSUMS NEW_CHECKSUMS

load_checksums() {
    [[ -f "$CHECKSUM_FILE" ]] || return
    while IFS='|' read -r path chk; do
        OLD_CHECKSUMS["$path"]="$chk"
    done < "$CHECKSUM_FILE"
}

save_checksums() {
    : > "$CHECKSUM_FILE"
    for path in "${!NEW_CHECKSUMS[@]}"; do
        echo "${path}|${NEW_CHECKSUMS[$path]}" >> "$CHECKSUM_FILE"
    done
}

# ============================================================
# Détecter les fichiers modifiés
# ============================================================
CBL_CHANGED=()
BMS_CHANGED=()

_track_file() {
    local f="$1"
    [[ -f "$f" ]] || return
    local chk; chk=$(file_md5 "$f")
    NEW_CHECKSUMS["$f"]="$chk"
    if [[ $FORCE -eq 1 ]] || [[ "${OLD_CHECKSUMS[$f]:-}" != "$chk" ]]; then
        CBL_CHANGED+=("$f")
    fi
}

detect_changes() {
    load_checksums

    # Sources COBOL : via pattern ou liste explicite
    if [[ -n "${CBL_PATTERN}" ]]; then
        for f in "${PROJECT_DIR}"/${CBL_PATTERN}; do
            _track_file "$f"
        done
    fi
    local pair
    for pair in "${CBL_FILES[@]+"${CBL_FILES[@]}"}"; do
        _track_file "${PROJECT_DIR}/${pair%%:*}"
    done

    # Copybooks
    for pair in "${COPYBOOK_FILES[@]+"${COPYBOOK_FILES[@]}"}"; do
        _track_file "${PROJECT_DIR}/${pair%%:*}"
    done

    # BMS
    if [[ -n "${BMS_PATTERN}" ]]; then
        for f in "${PROJECT_DIR}"/${BMS_PATTERN}; do
            [[ -f "$f" ]] || continue
            local chk; chk=$(file_md5 "$f")
            NEW_CHECKSUMS["$f"]="$chk"
            if [[ $FORCE -eq 1 ]] || [[ "${OLD_CHECKSUMS[$f]:-}" != "$chk" ]]; then
                BMS_CHANGED+=("$f")
            fi
        done
    fi
    for pair in "${BMS_FILES[@]+"${BMS_FILES[@]}"}"; do
        local f="${PROJECT_DIR}/${pair%%:*}"
        [[ -f "$f" ]] || continue
        local chk; chk=$(file_md5 "$f")
        NEW_CHECKSUMS["$f"]="$chk"
        if [[ $FORCE -eq 1 ]] || [[ "${OLD_CHECKSUMS[$f]:-}" != "$chk" ]]; then
            BMS_CHANGED+=("$f")
        fi
    done
}

# ============================================================
# Résoudre le membre MVS pour un fichier COBOL
# Priorité : COPYBOOK_FILES → CBL_FILES → CBL_PATTERN (stem 8c)
# Retourne également le dataset cible (SOURCE ou COPYLIB)
# ============================================================
_resolve_cbl_member() {
    local filepath="$1"
    local base; base=$(basename "$filepath")

    # Chercher dans COPYBOOK_FILES
    local cpair
    for cpair in "${COPYBOOK_FILES[@]+"${COPYBOOK_FILES[@]}"}"; do
        if [[ "${PROJECT_DIR}/${cpair%%:*}" == "$filepath" ]]; then
            echo "COPYLIB:${cpair##*:}"
            return
        fi
    done

    # Chercher dans CBL_FILES
    local pair
    for pair in "${CBL_FILES[@]+"${CBL_FILES[@]}"}"; do
        if [[ "${PROJECT_DIR}/${pair%%:*}" == "$filepath" ]]; then
            echo "SOURCE:${pair##*:}"
            return
        fi
    done

    # Fallback : stem limité à 8 chars en majuscule (CBL_PATTERN)
    local stem="${base%.*}"
    local member; member=$(printf '%s' "${stem:0:8}" | tr '[:lower:]' '[:upper:]')
    echo "SOURCE:${member}"
}

# ============================================================
# Upload IND$FILE (une session TSO par fichier si --dry=0)
# ============================================================
upload_one() {
    local localfile="$1"
    local mvs_ds="$2"
    local lrecl="${3:-80}"

    if [[ $DRY -eq 1 ]]; then
        changed "DRY: $(basename "$localfile") → ${mvs_ds}"
        return
    fi

    local blksize=$(( lrecl * 39 ))

    s3270_start || { fail "s3270 ne démarre pas"; return 1; }
    s3270_login

    s3270_cmd \
        "Transfer(Direction=send,HostFile=\"'${mvs_ds}'\",LocalFile=\"${localfile}\",Host=tso,Recfm=fixed,Lrecl=${lrecl},BlockSize=${blksize},Cr=add)" \
        120 >/dev/null \
        && ok "OK" || fail "ERREUR"

    s3270_stop
}

# ============================================================
# Soumettre un JCL membre et attendre la fin via syslog
# ============================================================
submit_and_wait() {
    local member="$1"
    local jobname="$2"
    local maxwait="${3:-240}"

    if [[ $DRY -eq 1 ]]; then
        changed "DRY: soumettre ${member}"
        return 0
    fi

    s3270_start || { fail "s3270 ne démarre pas"; return 1; }
    s3270_login

    s3270_cmd "String(\"SUBMIT '${HLQ}.${APP_SUFFIX}.JCL(${member})'\")" >/dev/null
    s3270_cmd "Enter()" 30 >/dev/null
    sleep 3

    s3270_stop

    # Attendre fin du job en surveillant le syslog Hercules
    local elapsed=0
    printf "    Attente %s " "$jobname"
    while [[ $elapsed -lt $maxwait ]]; do
        sleep 5; elapsed=$(( elapsed + 5 )); printf "."
        local done_line
        done_line=$(curl -s "${HERC_URL}/cgi-bin/tasks/syslog?numlines=20" \
            | sed 's/<[^>]*>//g' \
            | grep "HASP395\|HASP163" \
            | grep -i "$jobname" | tail -1 || true)
        if [[ -n "$done_line" ]]; then
            echo " OK"
            echo "    $done_line"
            if echo "$done_line" | grep -qi "ABEND\|JCL ERROR"; then
                fail "Job $jobname terminé en ERREUR"
                return 1
            fi
            return 0
        fi
    done
    echo " timeout (${maxwait}s)"
    return 1
}

# ============================================================
# Main
# ============================================================
echo "=== Build incrémental ${PROJECT_LABEL} — $(date '+%d/%m/%Y %H:%M:%S') ==="
[[ $FORCE -eq 1 ]] && echo -e "${YELLOW}Mode FULL rebuild${NC}"
[[ $DRY -eq 1 ]]   && echo -e "${YELLOW}Mode DRY-RUN (aucun changement réel)${NC}"
echo ""

detect_changes

# ---- Résumé des changements ----
echo "Fichiers modifiés détectés :"
if [[ ${#CBL_CHANGED[@]} -eq 0 && ${#BMS_CHANGED[@]} -eq 0 ]]; then
    echo "  Aucun changement — build non nécessaire."
    echo "  (Utiliser --full pour forcer)"
    exit 0
fi
for f in "${BMS_CHANGED[@]:-}"; do [[ -n "${f:-}" ]] && changed "BMS : $(basename "$f")"; done
for f in "${CBL_CHANGED[@]:-}"; do [[ -n "${f:-}" ]] && changed "CBL : $(basename "$f")"; done
echo ""

# ---- Upload fichiers modifiés ----
echo "--- Upload vers MVS ---"
for f in "${BMS_CHANGED[@]:-}"; do
    [[ -z "${f:-}" ]] && continue
    # Résoudre membre BMS
    local_member=""
    local pair
    for pair in "${BMS_FILES[@]+"${BMS_FILES[@]}"}"; do
        if [[ "${PROJECT_DIR}/${pair%%:*}" == "$f" ]]; then
            local_member="${pair##*:}"
            break
        fi
    done
    if [[ -z "$local_member" ]]; then
        local base; base=$(basename "$f" .bms)
        local_member=$(printf '%s' "${base:0:8}" | tr '[:lower:]' '[:upper:]')
    fi
    printf "  %-15s → %s.%s.BMS(%s) ... " "$(basename "$f")" "$HLQ" "$APP_SUFFIX" "$local_member"
    upload_one "$f" "${HLQ}.${APP_SUFFIX}.BMS(${local_member})" && ok "OK" || fail "ERREUR"
done

for f in "${CBL_CHANGED[@]:-}"; do
    [[ -z "${f:-}" ]] && continue
    local resolved; resolved=$(_resolve_cbl_member "$f")
    local ds_type="${resolved%%:*}"
    local member="${resolved##*:}"
    if [[ "$ds_type" == "COPYLIB" ]]; then
        printf "  %-15s → %s.%s.COPYLIB(%s) ... " "$(basename "$f")" "$HLQ" "$APP_SUFFIX" "$member"
        upload_one "$f" "${HLQ}.${APP_SUFFIX}.COPYLIB(${member})" && ok "OK" || fail "ERREUR"
    else
        printf "  %-15s → %s.%s.SOURCE(%s) ... " "$(basename "$f")" "$HLQ" "$APP_SUFFIX" "$member"
        upload_one "$f" "${HLQ}.${APP_SUFFIX}.SOURCE(${member})" && ok "OK" || fail "ERREUR"
    fi
done

[[ $ERRORS -gt 0 ]] && { fail "Upload échoué — abandon"; exit 1; }
echo ""

# ---- Assemblage BMS si des BMS ont changé ----
if [[ ${#BMS_CHANGED[@]} -gt 0 && -n "${JCL_BMS_MEMBER:-}" ]]; then
    echo "--- Assemblage BMS ---"
    submit_and_wait "${JCL_BMS_MEMBER}" "${JCL_BMS_JOBNAME}" 180 \
        && ok "BMS assemblés" || { fail "Assemblage BMS"; exit 1; }
    echo ""
fi

# ---- Compilation COBOL si des .cbl ont changé ----
if [[ ${#CBL_CHANGED[@]} -gt 0 ]]; then
    echo "--- Compilation COBOL ---"
    if [[ -n "${MVS_COMPILE_CMD:-}" ]]; then
        if [[ $DRY -eq 1 ]]; then
            changed "DRY: ${MVS_COMPILE_CMD}"
        else
            eval "${MVS_COMPILE_CMD}" && ok "Programmes compilés" || { fail "Compilation COBOL"; exit 1; }
        fi
    elif [[ -n "${JCL_COMP_MEMBER:-}" ]]; then
        submit_and_wait "${JCL_COMP_MEMBER}" "${JCL_COMP_JOBNAME}" 300 \
            && ok "Programmes compilés" || { fail "Compilation COBOL"; exit 1; }
    fi
    echo ""
fi

# ---- CICS newcopy (seulement si HAS_CICS=1) ----
if [[ "${HAS_CICS}" == "1" ]]; then
    echo "--- CICS newcopy ---"
    if [[ $DRY -eq 0 ]]; then
        bash "${MVS_DIR}/03_cics.sh" newcopy && ok "Modules rechargés dans CICS"
    else
        changed "DRY: bash mvs/03_cics.sh newcopy"
    fi
    echo ""
fi

# ---- Sauvegarder les checksums (build réussi) ----
[[ $DRY -eq 0 ]] && save_checksums

# ---- Log du build ----
echo "$(date '+%Y-%m-%d %H:%M:%S') | ${PROJECT_NAME} | BUILD OK | CBL:${#CBL_CHANGED[@]} BMS:${#BMS_CHANGED[@]}" >> "$BUILD_LOG"

echo -e "${GREEN}✓ Build terminé${NC} — $(date '+%H:%M:%S')"
echo "  Prochain build détectera uniquement les nouveaux changements."
if [[ "${HAS_CICS}" == "1" && ${#CICS_TRANSACTIONS[@]} -gt 0 ]]; then
    echo "  Taper ${CICS_TRANSACTIONS[0]} dans x3270 pour tester."
fi
