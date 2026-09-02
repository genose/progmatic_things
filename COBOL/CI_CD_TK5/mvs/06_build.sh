#!/usr/bin/env bash
# ============================================================
# 06_build.sh — Build incrémental GSTK sur MVS TK5
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
#
# Détecte les fichiers modifiés depuis le dernier build via MD5.
# N'uploade et ne recompile que ce qui a changé.
#
# Usage :
#   bash scripts/mvs/06_build.sh          # build incrémental
#   bash scripts/mvs/06_build.sh --full   # forcer full rebuild
#   bash scripts/mvs/06_build.sh --dry    # voir ce qui changerait
# ============================================================
set -euo pipefail

GSTK_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../GSTK" && pwd)"
SCRIPTS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MVS_DIR="$(dirname "${BASH_SOURCE[0]}")"
CHECKSUM_FILE="${MVS_DIR}/.checksums"
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

detect_changes() {
    load_checksums

    for f in "$GSTK_DIR"/GSTK00*.cbl "$GSTK_DIR/Copybook.cbl"; do
        [[ -f "$f" ]] || continue
        local chk; chk=$(file_md5 "$f")
        NEW_CHECKSUMS["$f"]="$chk"
        if [[ $FORCE -eq 1 ]] || [[ "${OLD_CHECKSUMS[$f]:-}" != "$chk" ]]; then
            CBL_CHANGED+=("$f")
        fi
    done

    for f in "$GSTK_DIR"/GSTK00*M.bms; do
        [[ -f "$f" ]] || continue
        local chk; chk=$(file_md5 "$f")
        NEW_CHECKSUMS["$f"]="$chk"
        if [[ $FORCE -eq 1 ]] || [[ "${OLD_CHECKSUMS[$f]:-}" != "$chk" ]]; then
            BMS_CHANGED+=("$f")
        fi
    done
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

    s3270_cmd "String(\"SUBMIT '${HLQ}.GSTK.JCL(${member})'\")" >/dev/null
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
echo "=== Build incrémental GSTK — $(date '+%d/%m/%Y %H:%M:%S') ==="
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
    base=$(basename "$f" .bms)
    printf "  %-15s → %s.GSTK.BMS(%s) ... " "$base" "$HLQ" "$base"
    upload_one "$f" "${HLQ}.GSTK.BMS(${base})" && ok "OK" || fail "ERREUR"
done

for f in "${CBL_CHANGED[@]:-}"; do
    [[ -z "${f:-}" ]] && continue
    base=$(basename "$f" .cbl)
    if [[ "$base" == "Copybook" ]]; then
        printf "  %-15s → %s.GSTK.COPYLIB(GSTKCOMM) ... " "GSTKCOMM" "$HLQ"
        upload_one "$f" "${HLQ}.GSTK.COPYLIB(GSTKCOMM)" && ok "OK" || fail "ERREUR"
    else
        printf "  %-15s → %s.GSTK.SOURCE(%s) ... " "$base" "$HLQ" "$base"
        upload_one "$f" "${HLQ}.GSTK.SOURCE(${base})" && ok "OK" || fail "ERREUR"
    fi
done

[[ $ERRORS -gt 0 ]] && { fail "Upload échoué — abandon"; exit 1; }
echo ""

# ---- Assemblage BMS si des BMS ont changé ----
if [[ ${#BMS_CHANGED[@]} -gt 0 ]]; then
    echo "--- Assemblage BMS ---"
    submit_and_wait "GSTKBMS" "GSTKBMS" 180 && ok "BMS assemblés" || { fail "Assemblage BMS"; exit 1; }
    echo ""
fi

# ---- Compilation COBOL si des .cbl ont changé ----
if [[ ${#CBL_CHANGED[@]} -gt 0 ]]; then
    echo "--- Compilation COBOL/CICS ---"
    submit_and_wait "GSTKCOMP" "GSTKCOMP" 300 && ok "Programmes compilés" || { fail "Compilation COBOL"; exit 1; }
    echo ""
fi

# ---- CICS newcopy ----
echo "--- CICS newcopy ---"
if [[ $DRY -eq 0 ]]; then
    bash "${MVS_DIR}/03_cics.sh" newcopy && ok "Modules rechargés dans CICS"
else
    changed "DRY: bash 03_cics.sh newcopy"
fi
echo ""

# ---- Sauvegarder les checksums (build réussi) ----
[[ $DRY -eq 0 ]] && save_checksums

# ---- Log du build ----
echo "$(date '+%Y-%m-%d %H:%M:%S') | BUILD OK | CBL:${#CBL_CHANGED[@]} BMS:${#BMS_CHANGED[@]}" >> "$BUILD_LOG"

echo -e "${GREEN}✓ Build terminé${NC} — $(date '+%H:%M:%S')"
echo "  Prochain build détectera uniquement les nouveaux changements."
echo "  Taper G000 dans x3270 pour tester."
