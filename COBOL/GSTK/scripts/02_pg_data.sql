-- ============================================================
-- DONNEES DE TEST - GSTK
-- Usage : psql gstk -f 02_pg_data.sql
-- Scénario : articles variés, quelques en alerte, mouvements
-- ============================================================

\set ON_ERROR_STOP on
\echo '--- Insertion articles de test ---'

INSERT INTO gstk.articles
    (art_code, art_designation, art_categorie, art_unite,
     art_qte_stock, art_qte_min, art_qte_max,
     art_prix_achat, art_prix_vente, art_tva_taux,
     art_emplacement, art_delai_appro, art_statut, art_operateur)
VALUES
-- BUREAUTIQUE : stock normal
('ART-001', 'CLAVIER USB AZERTY',       'BUREAUTIQUE', 'UNITE', 42,  5, 100,  18.50,  35.00, 20.00, 'A1-R1-C1',  5, 'ACTIF', 'ADMIN01'),
('ART-002', 'SOURIS OPTIQUE FILAIRE',   'BUREAUTIQUE', 'UNITE', 38,  5,  80,  10.00,  22.00, 20.00, 'A1-R1-C2',  5, 'ACTIF', 'ADMIN01'),
('ART-003', 'ECRAN 24P FULL HD',        'ELECTRONIQUE','UNITE',  8,  3,  20, 180.00, 299.00, 20.00, 'A1-R2-C1', 10, 'ACTIF', 'ADMIN01'),
('ART-004', 'CABLE USB-C 2M',           'BUREAUTIQUE', 'UNITE', 95, 20, 200,   2.00,   8.00, 20.00, 'A2-R1-C1',  3, 'ACTIF', 'ADMIN01'),
-- BUREAUTIQUE : en alerte (stock < min)
('ART-005', 'RAMETTE PAPIER A4 80G',    'BUREAUTIQUE', 'BOITE',  2, 10,  50,   4.50,   8.00, 20.00, 'A2-R1-C2',  2, 'ACTIF', 'ADMIN01'),
('ART-006', 'CARTOUCHE ENCRE NOIRE',    'BUREAUTIQUE', 'UNITE',  1,  5,  30,  12.00,  22.00, 20.00, 'A2-R2-C1',  7, 'ACTIF', 'ADMIN01'),
-- ELECTRONIQUE
('ART-007', 'DISQUE SSD 1TO',           'ELECTRONIQUE','UNITE', 15,  5,  30,  55.00,  99.00, 20.00, 'B1-R1-C1',  5, 'ACTIF', 'ADMIN01'),
('ART-008', 'SWITCH 8 PORTS',           'ELECTRONIQUE','UNITE',  0,  2,  10,  35.00,  65.00, 20.00, 'B1-R1-C2', 10, 'ACTIF', 'ADMIN01'),
-- OUTILLAGE
('ART-009', 'TOURNEVIS CRUCIFORME JGO', 'OUTILLAGE',  'UNITE', 12,  3,  20,  15.00,  28.00, 20.00, 'C1-R1-C1',  5, 'ACTIF', 'ADMIN01'),
('ART-010', 'MULTIMETRE NUMERIQUE',     'OUTILLAGE',  'UNITE',  3,  4,  15,  22.00,  45.00, 20.00, 'C1-R1-C2', 15, 'ACTIF', 'ADMIN01'),
-- Article ARCHIVE (non visible dans GSTK001/006)
('ART-099', 'FLOPPY 3.5P BOITE 10',    'BUREAUTIQUE', 'BOITE',  0,  0,   0,   0.00,   0.00, 20.00, NULL,         0, 'ARCHIVE','ADMIN01');

\echo '--- Insertion mouvements de test ---'

-- Mouvements entrée (simulant GSTK002)
INSERT INTO gstk.mouvements_stock
    (mvt_id, mvt_date, mvt_heure, mvt_type, mvt_sens,
     mvt_art_code, mvt_designation, mvt_quantite, mvt_prix_unit, mvt_montant_ht,
     mvt_stock_avant, mvt_stock_apres,
     mvt_num_bon, mvt_operateur, mvt_programme)
VALUES
(nextval('gstk.seq_mvt'), CURRENT_DATE - 5, '08:30:00', 'BON ENTREE', 'E',
 'ART-001', 'CLAVIER USB AZERTY',     20, 18.50,  370.00, 22, 42, 'BR-001001', 'USER01', 'GSTK002'),
(nextval('gstk.seq_mvt'), CURRENT_DATE - 5, '09:15:00', 'BON ENTREE', 'E',
 'ART-003', 'ECRAN 24P FULL HD',       5,180.00,  900.00,  3,  8, 'BR-001001', 'USER01', 'GSTK002'),
(nextval('gstk.seq_mvt'), CURRENT_DATE - 3, '10:00:00', 'BON ENTREE', 'E',
 'ART-007', 'DISQUE SSD 1TO',         10, 55.00,  550.00,  5, 15, 'BR-001002', 'ADMIN01','GSTK002'),
-- Mouvements sortie (simulant GSTK003)
(nextval('gstk.seq_mvt'), CURRENT_DATE - 2, '14:00:00', 'BON SORTIE', 'S',
 'ART-001', 'CLAVIER USB AZERTY',      5, 35.00,  175.00, 47, 42, 'BS-002001', 'USER01', 'GSTK003'),
(nextval('gstk.seq_mvt'), CURRENT_DATE - 2, '14:30:00', 'BON SORTIE', 'S',
 'ART-002', 'SOURIS OPTIQUE FILAIRE',  2, 22.00,   44.00, 40, 38, 'BS-002001', 'USER01', 'GSTK003'),
(nextval('gstk.seq_mvt'), CURRENT_DATE - 1, '09:00:00', 'BON SORTIE', 'S',
 'ART-005', 'RAMETTE PAPIER A4 80G',   8,  8.00,   64.00, 10,  2, 'BS-002002', 'USER02', 'GSTK003'),
(nextval('gstk.seq_mvt'), CURRENT_DATE - 1, '09:10:00', 'BON SORTIE', 'S',
 'ART-006', 'CARTOUCHE ENCRE NOIRE',   4, 22.00,   88.00,  5,  1, 'BS-002002', 'USER02', 'GSTK003'),
(nextval('gstk.seq_mvt'), CURRENT_DATE,     '11:00:00', 'BON SORTIE', 'S',
 'ART-008', 'SWITCH 8 PORTS',          1, 65.00,   65.00,  1,  0, 'BS-002003', 'ADMIN01','GSTK003');

\echo '--- Vérification des données ---'
SELECT 'Articles ACTIF' AS type, count(*) AS nb FROM gstk.articles WHERE art_statut='ACTIF'
UNION ALL
SELECT 'Articles en alerte', count(*) FROM gstk.articles
       WHERE art_statut='ACTIF' AND art_qte_stock < art_qte_min
UNION ALL
SELECT 'Mouvements total', count(*) FROM gstk.mouvements_stock
UNION ALL
SELECT 'Mouvements aujourd''hui', count(*) FROM gstk.mouvements_stock
       WHERE mvt_date = CURRENT_DATE;

\echo '✓ Données de test insérées'
