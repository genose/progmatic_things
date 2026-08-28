#!/usr/bin/env bash
# ============================================================
# 11_git_setup.sh — Initialiser git + hooks CI/CD pour GSTK
#
# Configure :
#   - Dépôt git dans /private/tmp/volatile_hd/COBOL/GSTK
#   - .gitignore adapté aux fichiers COBOL/MVS
#   - Hook pre-commit : syntaxe COBOL + SQL avant chaque commit
#   - Hook post-commit : build MVS automatique (optionnel)
#
# Usage :
#   bash scripts/mvs/11_git_setup.sh
#   bash scripts/mvs/11_git_setup.sh --with-mvs-push  # push MVS au commit
# ============================================================
set -euo pipefail

GSTK_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SCRIPTS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MVS_DIR="$(dirname "${BASH_SOURCE[0]}")"
GIT_DIR="${GSTK_DIR}/.git"

GREEN='\033[0;32m'; CYAN='\033[0;36m'; YELLOW='\033[1;33m'; NC='\033[0m'
ok()   { echo -e "${GREEN}✓${NC} $*"; }
info() { echo -e "${CYAN}ℹ${NC}  $*"; }
warn() { echo -e "${YELLOW}⚠${NC}  $*"; }

MVS_PUSH=0
[[ "${1:-}" == "--with-mvs-push" ]] && MVS_PUSH=1

echo "=== Setup Git + Hooks CI/CD pour GSTK ==="
echo "Répertoire : $GSTK_DIR"
echo ""

# ---- Initialiser git si nécessaire ----
if [[ ! -d "$GIT_DIR" ]]; then
    info "Initialisation du dépôt git..."
    git -C "$GSTK_DIR" init
    git -C "$GSTK_DIR" config user.name  "GSTK Dev"
    git -C "$GSTK_DIR" config user.email "gstk@tk5.local"
    ok "Dépôt git initialisé"
else
    ok "Dépôt git existant"
fi

# ---- .gitignore ----
cat > "${GSTK_DIR}/.gitignore" <<'EOF'
# Objets compilés et loadlib
*.obj
*.lst
*.load

# Fichiers temporaires éditeurs
*.swp
*~
*.bak
*.tmp

# macOS
.DS_Store
**/.DS_Store

# Checksums et caches de build
scripts/mvs/.checksums
scripts/mvs/.build.log
scripts/mvs/.spool_cache/
scripts/mvs/.reports/
scripts/mvs/.ci_history.log
scripts/mvs/.test_report.txt

# Logs PostgreSQL
*.log

# Secrets (ne jamais committer)
.env
*.password
*.secret
EOF
ok ".gitignore créé"

# ---- Hook pre-commit : syntaxe COBOL + SQL ----
PRECOMMIT="${GIT_DIR}/hooks/pre-commit"
cat > "$PRECOMMIT" <<HOOK
#!/usr/bin/env bash
# ============================================================
# pre-commit : Vérification syntaxe COBOL + SQL avant commit
# ============================================================
set -euo pipefail

GSTK_DIR="\$(git rev-parse --show-toplevel)"
SCRIPTS="\${GSTK_DIR}/scripts"

echo ""
echo "┌─────────────────────────────────────────┐"
echo "│   GSTK pre-commit check                 │"
echo "└─────────────────────────────────────────┘"

# 1. Vérifier uniquement les fichiers .cbl/.bms modifiés dans ce commit
CHANGED_CBL=\$(git diff --cached --name-only --diff-filter=ACM | grep -E '\\.cbl$' || true)
CHANGED_BMS=\$(git diff --cached --name-only --diff-filter=ACM | grep -E '\\.bms$' || true)

ERRORS=0

if [[ -n "\$CHANGED_CBL" ]]; then
    echo ""
    echo "  Vérification COBOL (\$(echo "\$CHANGED_CBL" | wc -l | tr -d ' ') fichiers)..."
    # Utiliser 04_cobc_check.sh qui gère le prétraitement EXEC SQL/CICS
    if ! bash "\${SCRIPTS}/04_cobc_check.sh" 2>&1 | grep -v "^Compilateur\|^Copybooks\|^===\|^$"; then
        (( ERRORS++ ))
    fi
fi

# 2. Test SQL rapide si des .cbl ont changé (requêtes de base)
if [[ -n "\$CHANGED_CBL" ]]; then
    echo ""
    echo "  Test SQL minimal (PostgreSQL)..."
    if psql -U "\$(whoami)" gstk -c "SELECT COUNT(*) FROM gstk.articles;" > /dev/null 2>&1; then
        echo "    PostgreSQL : OK"
    else
        echo "    PostgreSQL : non disponible (test ignoré)"
    fi
fi

# 3. Vérification .bms : structure minimale
if [[ -n "\$CHANGED_BMS" ]]; then
    echo ""
    echo "  Vérification BMS..."
    for bms in \$CHANGED_BMS; do
        mapset=\$(basename "\$bms" .bms)
        mdf_count=\$(grep -c "DFHMDF" "\${GSTK_DIR}/\${bms}" 2>/dev/null || echo 0)
        if grep -q "DFHMSD" "\${GSTK_DIR}/\${bms}" && grep -q "DFHMDI" "\${GSTK_DIR}/\${bms}"; then
            echo "    \${mapset} : OK (\${mdf_count} champs)"
        else
            echo "    \${mapset} : ERREUR (DFHMSD ou DFHMDI manquant)"
            (( ERRORS++ ))
        fi
    done
fi

echo ""
if [[ \$ERRORS -eq 0 ]]; then
    echo "  ✓ Vérifications OK — commit autorisé"
    echo ""
    exit 0
else
    echo "  ✗ \${ERRORS} erreur(s) — commit bloqué"
    echo "  Corriger les erreurs avant de committer."
    echo ""
    exit 1
fi
HOOK
chmod +x "$PRECOMMIT"
ok "Hook pre-commit installé"

# ---- Hook post-commit : build MVS (optionnel) ----
POSTCOMMIT="${GIT_DIR}/hooks/post-commit"
if [[ $MVS_PUSH -eq 1 ]]; then
    cat > "$POSTCOMMIT" <<HOOK
#!/usr/bin/env bash
# post-commit : déclencher le build MVS automatiquement
set -euo pipefail
GSTK_DIR="\$(git rev-parse --show-toplevel)"
echo ""
echo "  → Build MVS automatique (post-commit)..."
bash "\${GSTK_DIR}/scripts/mvs/06_build.sh" &
echo "  Build lancé en arrière-plan (PID \$!)"
HOOK
    chmod +x "$POSTCOMMIT"
    ok "Hook post-commit installé (build MVS auto)"
else
    warn "Hook post-commit non installé (ajouter --with-mvs-push pour activer)"
fi

# ---- Commit initial ----
info "Premier commit..."
git -C "$GSTK_DIR" add -A
git -C "$GSTK_DIR" commit -m "feat: projet GSTK initial (8 programmes CICS/COBOL + scripts CI)" \
    --no-verify 2>/dev/null || ok "Fichiers déjà committés (aucun changement)"

echo ""
echo "=== Git configuré ==="
echo ""
echo "Workflow de développement :"
echo ""
echo "  1. Modifier un .cbl ou .bms dans le répertoire GSTK/"
echo "  2. git add GSTK007.cbl"
echo "  3. git commit -m 'fix: ...'    ← pre-commit vérifie syntaxe"
echo "  4. bash scripts/mvs/06_build.sh  ← déployer sur TK5"
echo ""
echo "ou en mode watch (tout automatique) :"
echo "  bash scripts/mvs/07_watch.sh"
echo ""
git -C "$GSTK_DIR" log --oneline -5
