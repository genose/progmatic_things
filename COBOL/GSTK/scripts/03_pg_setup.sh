#!/usr/bin/env bash
# ============================================================
# SETUP POSTGRESQL COMPLET - GSTK
# Usage : bash scripts/03_pg_setup.sh [--reset]
#
# --reset : supprime la base et recrée depuis zéro
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DBNAME="gstk"
DBUSER="$(whoami)"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
ok()   { echo -e "${GREEN}✓${NC} $*"; }
warn() { echo -e "${YELLOW}⚠${NC}  $*"; }
fail() { echo -e "${RED}✗${NC} $*" >&2; exit 1; }

# ---- Vérifications préalables ----
command -v psql >/dev/null 2>&1 || fail "psql introuvable — PostgreSQL installé ?"

echo "=== Setup GSTK PostgreSQL ==="
echo "Base     : $DBNAME"
echo "Utilisateur : $DBUSER"
echo ""

# ---- Option --reset ----
if [[ "${1:-}" == "--reset" ]]; then
    warn "Option --reset : suppression de la base '$DBNAME'"
    psql -U "$DBUSER" postgres -c "DROP DATABASE IF EXISTS $DBNAME;" \
        && ok "Base supprimée" || warn "Base inexistante, on continue"
fi

# ---- Créer la base si elle n'existe pas ----
if psql -U "$DBUSER" -lqt 2>/dev/null | cut -d'|' -f1 | grep -qw "$DBNAME"; then
    warn "Base '$DBNAME' existe déjà. Utiliser --reset pour repartir de zéro."
else
    psql -U "$DBUSER" postgres -c "CREATE DATABASE $DBNAME ENCODING='UTF8';" \
        && ok "Base '$DBNAME' créée"
fi

# ---- Charger le schéma ----
echo ""
echo "--- Chargement du schéma ---"
psql -U "$DBUSER" "$DBNAME" -f "$SCRIPT_DIR/01_pg_schema.sql" \
    && ok "Schéma chargé"

# ---- Charger les données de test ----
echo ""
echo "--- Chargement des données de test ---"
psql -U "$DBUSER" "$DBNAME" -f "$SCRIPT_DIR/02_pg_data.sql" \
    && ok "Données de test chargées"

# ---- Vérification finale ----
echo ""
echo "=== Vérification ==="
psql -U "$DBUSER" "$DBNAME" -c "
SELECT
    schemaname,
    tablename,
    (SELECT COUNT(*) FROM information_schema.columns c
     WHERE c.table_schema=t.schemaname AND c.table_name=t.tablename) AS nb_colonnes
FROM pg_tables t
WHERE schemaname='gstk'
ORDER BY tablename;
"

echo ""
psql -U "$DBUSER" "$DBNAME" -c "
SELECT art_code, art_designation, art_qte_stock, art_qte_min,
       CASE WHEN art_qte_stock < art_qte_min THEN '⚠ ALERTE' ELSE 'OK' END AS statut
FROM gstk.articles
WHERE art_statut='ACTIF'
ORDER BY art_code;
"

echo ""
ok "Setup terminé. Connexion : psql $DBNAME"
echo "   Pour tester : psql $DBNAME -c \"SELECT * FROM gstk.v_alertes_actives;\""
