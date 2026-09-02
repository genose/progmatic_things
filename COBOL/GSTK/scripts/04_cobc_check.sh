#!/usr/bin/env bash
# ============================================================
# VERIFICATION SYNTAXE COBOL - GSTK (GnuCOBOL 3.2)
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
# Usage : bash scripts/04_cobc_check.sh [programme]
#
# Sans argument : vérifie tous les programmes GSTK00*.cbl
# Avec argument : bash scripts/04_cobc_check.sh GSTK007
#
# Stratégie locale :
#   GnuCOBOL 3.2 ne supporte pas -fno-cics/-fno-sql.
#   On prétraite chaque .cbl (Python) : EXEC SQL/CICS → CONTINUE.
#   Les erreurs COPY sur GSTKxxxM (copybooks BMS) sont attendues et
#   filtrées — la validation complète se fait sur MVS.
# ============================================================
set -uo pipefail

GSTK_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COBC="${COBC:-/opt/local/bin/cobc}"
COPY_DIR="$GSTK_DIR"
TMP_DIR="$(mktemp -d /tmp/gstk_check_XXXX)"
trap 'rm -rf "$TMP_DIR"' EXIT

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
ok()   { echo -e "${GREEN}✓${NC} $*"; }
warn() { echo -e "${YELLOW}⚠${NC}  $*"; }
fail() { echo -e "${RED}✗${NC} $*"; }

# Vérifier le compilateur
if [[ ! -x "$COBC" ]]; then
    COBC="$(command -v cobc 2>/dev/null)" || { echo "cobc introuvable" >&2; exit 1; }
fi
echo "Compilateur : $COBC ($("$COBC" --version 2>&1 | head -1))"
echo "Copybooks   : $COPY_DIR"
echo ""

# ---- Copybook GSTKCOMM ----
GSTKCOMM="$COPY_DIR/GSTKCOMM.cpy"
if [[ ! -f "$GSTKCOMM" ]]; then
    ln -s "$COPY_DIR/Copybook.cbl" "$GSTKCOMM" 2>/dev/null \
        && warn "Lien créé : GSTKCOMM.cpy → Copybook.cbl" \
        || warn "Impossible de créer GSTKCOMM.cpy"
fi

# ---- Stubs pour les copybooks BMS (GSTKxxxM) ----
# Ces fichiers sont générés par l'assembleur BMS sur MVS.
# On crée des stubs minimaux pour que COPY ne bloque pas.
for N in 0 1 2 3 4 5 6 7; do
    STUB="$TMP_DIR/GSTK00${N}M.cpy"
    cat > "$STUB" <<EOF
      *----------------------------------------------------------------*
      * STUB GSTK00${N}M - remplace le copybook BMS genere sur MVS.
      * Le verif locale ne peut pas valider les references aux champs
      * BMS (GSTKxxxI / GSTKxxxO). Validation complete sur MVS.
      *----------------------------------------------------------------*
       01 GSTK00${N}I.
          05 FILLER PIC X(3840).
       01 GSTK00${N}O REDEFINES GSTK00${N}I.
          05 FILLER PIC X(3840).
EOF
done

# ---- Préprocesseur Python : EXEC SQL/CICS → CONTINUE ----
preprocess() {
    local src="$1" dst="$2"
    python3 - "$src" "$dst" <<'PYEOF'
import sys, re

src, dst = sys.argv[1], sys.argv[2]
with open(src, encoding='utf-8', errors='replace') as f:
    lines = f.readlines()

out = []
in_exec = False
exec_buf = []

for line in lines:
    stripped = line.rstrip()
    upper = stripped.upper().lstrip()

    if not in_exec:
        if re.match(r'\s*EXEC\s+(SQL|CICS)\b', stripped, re.IGNORECASE):
            in_exec = True
            exec_buf = [line]
            # inline END-EXEC on same line?
            if re.search(r'END-EXEC', stripped, re.IGNORECASE):
                in_exec = False
                out.append('           CONTINUE.\n')
                exec_buf = []
        else:
            out.append(line)
    else:
        exec_buf.append(line)
        if re.search(r'END-EXEC', stripped, re.IGNORECASE):
            in_exec = False
            # Replace entire block with blank lines + CONTINUE on last line
            for bl in exec_buf[:-1]:
                out.append('\n')
            out.append('           CONTINUE.\n')
            exec_buf = []

# If file ended inside EXEC block (malformed)
for bl in exec_buf:
    out.append('\n')

with open(dst, 'w', encoding='utf-8') as f:
    f.writelines(out)
PYEOF
}

# ---- Sélection des programmes ----
if [[ $# -gt 0 ]]; then
    PROG="${1%.cbl}"
    FILES=("$GSTK_DIR/${PROG}.cbl")
else
    FILES=("$GSTK_DIR"/GSTK00*.cbl)
fi

ERR_COUNT=0
OK_COUNT=0

COBC_FLAGS=(
    -std=ibm
    -fsyntax-only
    -W
    -I "$TMP_DIR"
    -I "$COPY_DIR"
)

echo "=== Vérification syntaxe COBOL ==="
echo ""

for CBL in "${FILES[@]}"; do
    [[ -f "$CBL" ]] || { warn "Fichier introuvable : $CBL"; continue; }
    PROG=$(basename "$CBL" .cbl)
    printf "%-12s ... " "$PROG"

    # Prétraiter le fichier
    PREPROCESSED="$TMP_DIR/${PROG}.cbl"
    if ! preprocess "$CBL" "$PREPROCESSED"; then
        fail "Erreur de prétraitement"
        (( ERR_COUNT++ ))
        continue
    fi

    OUTPUT=$("$COBC" "${COBC_FLAGS[@]}" "$PREPROCESSED" 2>&1)
    RC=$?

    # Filtrer les erreurs/warnings attendus sur les stubs BMS
    REAL_ERRORS=$(echo "$OUTPUT" | grep "error:" \
        | grep -v "FILLER\|redefines\|stub\|GSTK00[0-9]M" \
        | grep -v "GSTKCOMM\|GSTKCPY\|DFHAID\|DFHBMSCA\|SQLCA" \
        | grep -v "No such file or directory" \
        || true)

    WARNINGS=$(echo "$OUTPUT" | grep "warning:" \
        | grep -v "FILLER\|stub\|GSTK00[0-9]M" \
        | wc -l | tr -d ' ')

    if [[ -z "$REAL_ERRORS" ]]; then
        if [[ "$WARNINGS" -gt 0 ]]; then
            warn "OK (${WARNINGS} warning(s))"
            echo "$OUTPUT" | grep "warning:" | grep -v "FILLER\|stub" | sed 's|'"$TMP_DIR/"'||;s/^/    /'
        else
            ok "OK"
        fi
        (( OK_COUNT++ ))
    else
        fail "ERREUR"
        echo "$REAL_ERRORS" | sed 's|'"$TMP_DIR/"'|'"$GSTK_DIR/"'|;s/^/    /'
        (( ERR_COUNT++ ))
    fi
done

echo ""
echo "=== Résultat ==="
[[ $OK_COUNT -gt 0 ]]  && ok "${OK_COUNT} programme(s) valide(s)"
[[ $ERR_COUNT -gt 0 ]] && fail "${ERR_COUNT} programme(s) avec erreurs"

# ---- Vérification structure BMS ----
echo ""
echo "=== Vérification BMS ==="
warn "Assemblage IBM (ASMA90) requis sur MVS. Vérification locale : structure uniquement."
for BMS in "$GSTK_DIR"/GSTK00*M.bms; do
    MAPSET=$(basename "$BMS" .bms)
    CNT_MSD=$(grep -c "DFHMSD" "$BMS" 2>/dev/null || echo 0)
    CNT_MDI=$(grep -c "DFHMDI" "$BMS" 2>/dev/null || echo 0)
    CNT_MDF=$(grep -c "DFHMDF" "$BMS" 2>/dev/null || echo 0)
    if [[ $CNT_MSD -gt 0 && $CNT_MDI -gt 0 ]]; then
        printf "%-12s : %d DFHMSD, %d DFHMDI, %d DFHMDF " "$MAPSET" "$CNT_MSD" "$CNT_MDI" "$CNT_MDF"
        ok ""
    else
        printf "%-12s : structure incomplète (MSD=%d MDI=%d) " "$MAPSET" "$CNT_MSD" "$CNT_MDI"
        fail ""
    fi
done

exit $ERR_COUNT
