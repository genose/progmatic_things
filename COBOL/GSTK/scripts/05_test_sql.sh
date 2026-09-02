#!/usr/bin/env bash
# ============================================================
# TESTS SQL - Valider les requêtes GSTK avant déploiement CICS
# Auteur   : Sebastien Cotillard
# Date     : 2026-09-02
# Usage : bash scripts/05_test_sql.sh
#
# Teste les requêtes SQL de chaque programme dans PostgreSQL.
# Un SQLCODE ≠ 0 sur MVS = une erreur ici = problème réel.
# ============================================================
set -uo pipefail

DBNAME="${PGDATABASE:-gstk}"
DBUSER="${PGUSER:-$(whoami)}"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
ok()      { echo -e "  ${GREEN}✓${NC} $*"; }
fail()    { echo -e "  ${RED}✗${NC} $*"; (( ERRORS++ )); }
section() { echo -e "\n${CYAN}=== $* ===${NC}"; }

ERRORS=0

run_sql() {
    local label="$1"
    local sql="$2"
    local result
    result=$(psql -U "$DBUSER" "$DBNAME" -tAc "$sql" 2>&1)
    local rc=$?
    if [[ $rc -eq 0 && -n "$result" ]]; then
        ok "$label"
        echo "$result" | head -5 | sed 's/^/       /'
    elif [[ $rc -eq 0 ]]; then
        ok "$label (0 lignes)"
    else
        fail "$label"
        echo "       $result"
    fi
}

echo "=== Tests SQL GSTK - $(date '+%d/%m/%Y %H:%M') ==="
echo "Base : $DBNAME@$DBUSER"

# ---- GSTK001 : Consultation stock ----
section "GSTK001 - Consultation stock"

run_sql "Curseur articles ACTIF" "
SELECT art_code, art_designation, art_categorie, art_qte_stock
FROM gstk.articles
WHERE art_code       LIKE '%'
  AND art_designation LIKE '%'
  AND art_categorie   LIKE '%'
  AND art_statut      LIKE '%'
  AND art_statut     <> 'ARCHIVE'
ORDER BY art_code
LIMIT 10;"

run_sql "Barre ASCII : MAX par catégorie" "
SELECT art_categorie, MAX(art_qte_stock)
FROM gstk.articles WHERE art_statut='ACTIF'
GROUP BY art_categorie;"

# ---- GSTK002 : Entrée marchandise ----
section "GSTK002 - Entrée marchandise"

run_sql "Lecture article par code" "
SELECT art_code, art_designation, art_categorie,
       art_qte_stock, art_qte_min, art_qte_max,
       art_prix_achat, art_statut, art_emplacement
FROM gstk.articles
WHERE art_code = 'ART-001';"

run_sql "SELECT NEXT VALUE séquence MVT" "
SELECT nextval('gstk.seq_mvt') AS prochaine_id;"

run_sql "INSERT mouvement test (sans commit)" "
BEGIN;
INSERT INTO gstk.mouvements_stock
    (mvt_id, mvt_type, mvt_sens, mvt_art_code, mvt_designation,
     mvt_quantite, mvt_prix_unit, mvt_montant_ht,
     mvt_stock_avant, mvt_stock_apres, mvt_operateur, mvt_programme)
VALUES
    (nextval('gstk.seq_mvt'), 'BON ENTREE', 'E', 'ART-001',
     'CLAVIER USB AZERTY', 5, 18.50, 92.50, 42, 47, 'TEST01', 'GSTK002');
ROLLBACK;"

# ---- GSTK003 : Sortie marchandise ----
section "GSTK003 - Sortie marchandise"

run_sql "Vérification stock avant sortie" "
SELECT art_code, art_qte_stock, art_qte_min,
       art_qte_stock - 5 AS stock_apres_sortie,
       CASE WHEN art_qte_stock - 5 < 0 THEN 'BLOQUE' ELSE 'OK' END AS validation
FROM gstk.articles
WHERE art_code = 'ART-001';"

run_sql "INSERT sortie test (sans commit)" "
BEGIN;
INSERT INTO gstk.mouvements_stock
    (mvt_id, mvt_type, mvt_sens, mvt_art_code, mvt_designation,
     mvt_quantite, mvt_prix_unit, mvt_montant_ht,
     mvt_stock_avant, mvt_stock_apres, mvt_operateur, mvt_programme)
VALUES
    (nextval('gstk.seq_mvt'), 'BON SORTIE', 'S', 'ART-001',
     'CLAVIER USB AZERTY', 2, 35.00, 70.00, 42, 40, 'TEST01', 'GSTK003');
ROLLBACK;"

# ---- GSTK004 : Création/Modification article ----
section "GSTK004 - Gestion articles"

run_sql "INSERT article test (sans commit)" "
BEGIN;
INSERT INTO gstk.articles
    (art_code, art_designation, art_categorie, art_unite,
     art_qte_stock, art_qte_min, art_qte_max,
     art_prix_achat, art_prix_vente,
     art_statut, art_operateur)
VALUES ('ART-TST', 'ARTICLE TEST', 'AUTRE', 'UNITE',
        0, 1, 10, 5.00, 10.00, 'ACTIF', 'TEST01');
ROLLBACK;"

run_sql "UPDATE article (sans commit)" "
BEGIN;
UPDATE gstk.articles
SET art_designation = 'MODIFIE', art_date_maj = CURRENT_TIMESTAMP
WHERE art_code = 'ART-001';
ROLLBACK;"

run_sql "Archive article (sans commit)" "
BEGIN;
UPDATE gstk.articles SET art_statut='ARCHIVE' WHERE art_code = 'ART-099';
ROLLBACK;"

# ---- GSTK005 : Rapports ----
section "GSTK005 - Rapports par catégorie"

run_sql "KPI globaux (articles actifs, valeur, alertes)" "
SELECT
    COUNT(*) FILTER (WHERE art_statut='ACTIF')                        AS nb_actifs,
    SUM(art_qte_stock * art_prix_achat) FILTER (WHERE art_statut='ACTIF') AS valeur_totale,
    COUNT(*) FILTER (WHERE art_qte_stock < art_qte_min AND art_statut='ACTIF') AS nb_alertes,
    COUNT(*) FILTER (WHERE mvt_date = CURRENT_DATE)                    AS mvts_jour
FROM gstk.articles
LEFT JOIN gstk.mouvements_stock ON mvt_art_code = art_code;"

run_sql "Stats par catégorie (curseur GSTK005)" "
SELECT art_categorie,
       COUNT(*)                             AS nb_art,
       SUM(art_qte_stock)                   AS qte_totale,
       SUM(art_qte_stock * art_prix_achat)  AS valeur,
       COUNT(*) FILTER (WHERE art_qte_stock < art_qte_min) AS alertes
FROM gstk.articles
WHERE art_statut = 'ACTIF'
GROUP BY art_categorie
ORDER BY art_categorie;"

# ---- GSTK006 : Alertes ----
section "GSTK006 - Alertes stock critique"

run_sql "Curseur alertes (stock < min, trié criticité)" "
SELECT art_code, art_designation, art_categorie,
       art_qte_stock, art_qte_min,
       art_qte_min - art_qte_stock AS manquant,
       art_delai_appro
FROM gstk.articles
WHERE art_qte_stock < art_qte_min
  AND art_statut = 'ACTIF'
ORDER BY (art_qte_stock / NULLIF(art_qte_min::numeric, 0)) ASC;"

# ---- GSTK007 : Historique ----
section "GSTK007 - Historique mouvements"

run_sql "Curseur historique (sans filtre)" "
SELECT mvt_date, mvt_heure, mvt_type, mvt_sens,
       mvt_art_code, mvt_designation, mvt_quantite, mvt_montant_ht,
       mvt_operateur
FROM gstk.mouvements_stock
WHERE mvt_art_code LIKE '%'
  AND mvt_type     LIKE '%'
ORDER BY mvt_date DESC, mvt_heure DESC
LIMIT 10;"

run_sql "Totaux période (COUNT + SUM)" "
SELECT COUNT(*) AS nb_mvt,
       COALESCE(SUM(mvt_montant_ht), 0) AS total_ht
FROM gstk.mouvements_stock
WHERE mvt_art_code LIKE '%'
  AND mvt_type     LIKE '%';"

run_sql "Filtre par type BON SORTIE" "
SELECT mvt_date, mvt_art_code, mvt_quantite, mvt_montant_ht
FROM gstk.mouvements_stock
WHERE mvt_type = 'BON SORTIE'
ORDER BY mvt_date DESC;"

run_sql "Filtre date (conversion DD/MM/YYYY → YYYY-MM-DD)" "
SELECT mvt_date, mvt_art_code, mvt_type
FROM gstk.mouvements_stock
WHERE mvt_date >= (CURRENT_DATE - INTERVAL '7 days')::DATE
ORDER BY mvt_date DESC;"

# ---- Résultat ----
echo ""
echo "=== Résultat ==="
if [[ $ERRORS -eq 0 ]]; then
    echo -e "${GREEN}✓ Tous les tests SQL passent — requêtes prêtes pour CICS${NC}"
else
    echo -e "${RED}✗ ${ERRORS} test(s) échoué(s) — corriger avant déploiement MVS${NC}"
fi
exit $ERRORS
