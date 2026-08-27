-- ============================================================
-- SCHEMA POSTGRESQL - GSTK (adapté depuis DB2)
-- Compatibilité : PostgreSQL 14+
-- Usage : psql -U $(whoami) -f 01_pg_schema.sql
-- ============================================================
-- Différences DB2 → PostgreSQL appliquées :
--   • Triggers réécrits en PL/pgSQL
--   • MVT_ID : GENERATED ALWAYS → sequence explicite (INSERT avec ID)
--   • ART_CODE → MVT_ART_CODE dans MOUVEMENTS_STOCK (nom utilisé par les pgms)
--   • CHECK MVT_TYPE étendu : 'BON ENTREE', 'BON SORTIE' (valeurs pgms)
--   • CHECK MVT_SENS étendu : 'E', 'S' (valeurs programmes GSTK002/003)
--   • SYSIBM.SYSDUMMY1 → gstk.seq_mvt accessible via nextval()
-- ============================================================

\set ON_ERROR_STOP on

-- Supprimer et recréer le schéma proprement
DROP SCHEMA IF EXISTS gstk CASCADE;
CREATE SCHEMA gstk;

-- ============================================================
-- SEQUENCES
-- ============================================================
CREATE SEQUENCE gstk.seq_bon_entree START WITH 1001 INCREMENT BY 1 NO CYCLE CACHE 10;
CREATE SEQUENCE gstk.seq_bon_sortie START WITH 2001 INCREMENT BY 1 NO CYCLE CACHE 10;
-- Séquence pour MVT_ID (utilisée explicitement par GSTK002/003 via NEXT VALUE FOR)
CREATE SEQUENCE gstk.seq_mvt       START WITH 1    INCREMENT BY 1 NO CYCLE CACHE 20;

-- ============================================================
-- TABLE : CATEGORIES
-- ============================================================
CREATE TABLE gstk.categories (
    cat_code          CHAR(15)        NOT NULL,
    cat_libelle       VARCHAR(50)     NOT NULL,
    cat_libelle_court CHAR(10),
    cat_description   VARCHAR(200),
    cat_compte_compta CHAR(10),
    cat_tva_defaut    DECIMAL(5,2)    DEFAULT 20.00,
    cat_statut        CHAR(10)        DEFAULT 'ACTIF',
    cat_ordre_aff     SMALLINT        DEFAULT 99,
    CONSTRAINT pk_categories  PRIMARY KEY (cat_code),
    CONSTRAINT ck_cat_statut  CHECK (cat_statut IN ('ACTIF','INACTIF'))
);

-- ============================================================
-- TABLE : EMPLACEMENTS
-- ============================================================
CREATE TABLE gstk.emplacements (
    emp_code          CHAR(15)        NOT NULL,
    emp_zone          CHAR(5),
    emp_allee         CHAR(5),
    emp_rangee        CHAR(5),
    emp_colonne       CHAR(5),
    emp_niveau        CHAR(5),
    emp_type          CHAR(15),
    emp_capacite_max  DECIMAL(10,3),
    emp_longueur_cm   SMALLINT,
    emp_largeur_cm    SMALLINT,
    emp_hauteur_cm    SMALLINT,
    emp_statut        CHAR(10)        DEFAULT 'LIBRE',
    emp_commentaire   VARCHAR(200),
    CONSTRAINT pk_emplacements  PRIMARY KEY (emp_code),
    CONSTRAINT ck_emp_statut    CHECK (emp_statut IN ('LIBRE','OCCUPE','BLOQUE','RESERVE'))
);

-- ============================================================
-- TABLE : FOURNISSEURS
-- ============================================================
CREATE TABLE gstk.fournisseurs (
    frn_code          CHAR(10)        NOT NULL,
    frn_nom           VARCHAR(60)     NOT NULL,
    frn_nom_court     CHAR(20),
    frn_adresse1      VARCHAR(80),
    frn_adresse2      VARCHAR(80),
    frn_code_postal   CHAR(10),
    frn_ville         VARCHAR(50),
    frn_pays          CHAR(3)         DEFAULT 'FR',
    frn_tel           CHAR(20),
    frn_email         VARCHAR(80),
    frn_contact       VARCHAR(50),
    frn_devise        CHAR(3)         DEFAULT 'EUR',
    frn_delai_moyen   SMALLINT        DEFAULT 5,
    frn_min_cmd       DECIMAL(12,2),
    frn_franco_port   DECIMAL(12,2),
    frn_remise_pct    DECIMAL(5,2)    DEFAULT 0,
    frn_cond_paie     CHAR(20),
    frn_statut        CHAR(10)        NOT NULL DEFAULT 'ACTIF',
    frn_date_creation TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    frn_date_maj      TIMESTAMP,
    frn_operateur     CHAR(10)        NOT NULL,
    frn_commentaire   VARCHAR(200),
    CONSTRAINT pk_fournisseurs  PRIMARY KEY (frn_code),
    CONSTRAINT ck_frn_statut    CHECK (frn_statut IN ('ACTIF','INACTIF','BLACKLIST'))
);

-- ============================================================
-- TABLE : ARTICLES
-- ============================================================
CREATE TABLE gstk.articles (
    art_code          CHAR(10)        NOT NULL,
    art_designation   VARCHAR(50)     NOT NULL,
    art_description   VARCHAR(200),
    art_code_barre    CHAR(13),
    art_categorie     CHAR(15)        NOT NULL,
    art_sous_cat      CHAR(15),
    art_unite         CHAR(10)        NOT NULL DEFAULT 'UNITE',
    art_qte_stock     DECIMAL(10,3)   NOT NULL DEFAULT 0,
    art_qte_min       DECIMAL(10,3)   NOT NULL DEFAULT 0,
    art_qte_max       DECIMAL(10,3)   NOT NULL DEFAULT 9999,
    art_qte_reorder   DECIMAL(10,3),
    art_prix_achat    DECIMAL(12,4)   NOT NULL DEFAULT 0,
    art_prix_vente    DECIMAL(12,4),
    art_tva_taux      DECIMAL(5,2)    DEFAULT 20.00,
    art_devise        CHAR(3)         DEFAULT 'EUR',
    art_emplacement   CHAR(15),
    art_poids_kg      DECIMAL(8,3),
    art_volume_l      DECIMAL(8,3),
    art_delai_appro   SMALLINT        DEFAULT 5,
    frn_code          CHAR(10),
    art_statut        CHAR(10)        NOT NULL DEFAULT 'ACTIF',
    art_date_creation TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    art_date_maj      TIMESTAMP,
    art_operateur     CHAR(10)        NOT NULL,
    art_commentaire   VARCHAR(200),
    CONSTRAINT pk_articles    PRIMARY KEY (art_code),
    CONSTRAINT ck_qte_stock   CHECK (art_qte_stock  >= 0),
    CONSTRAINT ck_qte_min     CHECK (art_qte_min    >= 0),
    CONSTRAINT ck_prix_achat  CHECK (art_prix_achat >= 0),
    CONSTRAINT ck_tva         CHECK (art_tva_taux BETWEEN 0 AND 100),
    CONSTRAINT ck_statut      CHECK (art_statut IN ('ACTIF','INACTIF','ARCHIVE')),
    CONSTRAINT ck_unite       CHECK (art_unite IN
                              ('UNITE','KG','LITRE','METRE','BOITE','PALETTE'))
);

CREATE UNIQUE INDEX idx_art_barre   ON gstk.articles (art_code_barre)
       WHERE art_code_barre IS NOT NULL;
CREATE        INDEX idx_art_cat     ON gstk.articles (art_categorie);
CREATE        INDEX idx_art_statut  ON gstk.articles (art_statut);
CREATE        INDEX idx_art_frn     ON gstk.articles (frn_code);
CREATE        INDEX idx_art_empl    ON gstk.articles (art_emplacement);

-- ============================================================
-- TABLE : BONS_ENTREE
-- ============================================================
CREATE TABLE gstk.bons_entree (
    ben_numero        CHAR(12)        NOT NULL,
    ben_date          DATE            NOT NULL DEFAULT CURRENT_DATE,
    ben_heure         TIME            NOT NULL DEFAULT CURRENT_TIME,
    frn_code          CHAR(10),
    ben_fournisseur   VARCHAR(60),
    ben_ref_frn       VARCHAR(30),
    ben_num_cmd       VARCHAR(20),
    ben_nb_lignes     SMALLINT        DEFAULT 0,
    ben_montant_ht    DECIMAL(14,2)   DEFAULT 0,
    ben_montant_tva   DECIMAL(14,2)   DEFAULT 0,
    ben_montant_ttc   DECIMAL(14,2)   DEFAULT 0,
    ben_transporteur  VARCHAR(40),
    ben_num_bl        VARCHAR(30),
    ben_statut        CHAR(15)        NOT NULL DEFAULT 'VALIDE',
    ben_commentaire   VARCHAR(300),
    ben_date_saisie   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ben_operateur     CHAR(10)        NOT NULL,
    ben_poste         CHAR(10),
    CONSTRAINT pk_bons_entree  PRIMARY KEY (ben_numero),
    CONSTRAINT fk_ben_frn      FOREIGN KEY (frn_code)
                               REFERENCES gstk.fournisseurs (frn_code),
    CONSTRAINT ck_ben_statut   CHECK (ben_statut IN
                               ('BROUILLON','VALIDE','ANNULE','LITIGE'))
);

CREATE INDEX idx_ben_date   ON gstk.bons_entree (ben_date);
CREATE INDEX idx_ben_frn    ON gstk.bons_entree (frn_code);
CREATE INDEX idx_ben_statut ON gstk.bons_entree (ben_statut);

-- ============================================================
-- TABLE : BONS_SORTIE
-- ============================================================
CREATE TABLE gstk.bons_sortie (
    bso_numero        CHAR(12)        NOT NULL,
    bso_date          DATE            NOT NULL DEFAULT CURRENT_DATE,
    bso_heure         TIME            NOT NULL DEFAULT CURRENT_TIME,
    bso_demandeur     VARCHAR(60),
    bso_centre_cout   CHAR(15),
    bso_num_cmd       VARCHAR(20),
    bso_num_affaire   VARCHAR(20),
    bso_motif         CHAR(20)        NOT NULL DEFAULT 'USAGE INTERNE',
    bso_nb_lignes     SMALLINT        DEFAULT 0,
    bso_montant_ht    DECIMAL(14,2)   DEFAULT 0,
    bso_montant_ttc   DECIMAL(14,2)   DEFAULT 0,
    bso_adresse_liv   VARCHAR(200),
    bso_transporteur  VARCHAR(40),
    bso_statut        CHAR(15)        NOT NULL DEFAULT 'VALIDE',
    bso_commentaire   VARCHAR(300),
    bso_date_saisie   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    bso_operateur     CHAR(10)        NOT NULL,
    bso_poste         CHAR(10),
    CONSTRAINT pk_bons_sortie  PRIMARY KEY (bso_numero),
    CONSTRAINT ck_bso_statut   CHECK (bso_statut IN
                               ('BROUILLON','VALIDE','ANNULE','EN COURS'))
);

-- ============================================================
-- TABLE : MOUVEMENTS_STOCK
-- IMPORTANT : MVT_ART_CODE (et non ART_CODE) pour correspondre
--             aux programmes COBOL GSTK002/003/007
--             MVT_ID alimenté par gstk.seq_mvt (pas IDENTITY pure)
--             CHECK MVT_TYPE / MVT_SENS adaptés aux valeurs des pgms
-- ============================================================
CREATE TABLE gstk.mouvements_stock (
    mvt_id            BIGINT          NOT NULL DEFAULT nextval('gstk.seq_mvt'),
    mvt_date          DATE            NOT NULL DEFAULT CURRENT_DATE,
    mvt_heure         TIME            NOT NULL DEFAULT CURRENT_TIME,
    mvt_timestamp     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mvt_type          CHAR(20)        NOT NULL,
    mvt_sens          CHAR(1)         NOT NULL,
    mvt_motif         CHAR(20),
    mvt_art_code      CHAR(10)        NOT NULL,   -- nom utilisé par les pgms COBOL
    mvt_designation   VARCHAR(50),
    mvt_unite         CHAR(10),
    mvt_quantite      DECIMAL(10,3)   NOT NULL,
    mvt_prix_unit     DECIMAL(12,4),
    mvt_montant_ht    DECIMAL(14,2),
    mvt_stock_avant   DECIMAL(10,3)   NOT NULL DEFAULT 0,
    mvt_stock_apres   DECIMAL(10,3)   NOT NULL DEFAULT 0,
    mvt_num_bon       CHAR(12),
    mvt_num_ligne     SMALLINT,
    mvt_num_lot       VARCHAR(30),
    mvt_emplacement   CHAR(15),
    mvt_tiers         VARCHAR(60),
    mvt_centre_cout   CHAR(15),
    mvt_operateur     CHAR(10)        NOT NULL,
    mvt_poste         CHAR(10),
    mvt_programme     CHAR(8),
    mvt_commentaire   VARCHAR(300),
    CONSTRAINT pk_mouvements   PRIMARY KEY (mvt_id),
    CONSTRAINT fk_mvt_art      FOREIGN KEY (mvt_art_code)
                               REFERENCES gstk.articles (art_code),
    -- Valeurs réelles utilisées par les pgms COBOL (BON ENTREE / BON SORTIE)
    CONSTRAINT ck_mvt_type     CHECK (mvt_type IN
                               ('BON ENTREE','BON SORTIE','AJUST+','AJUST-',
                                'ENTREE','SORTIE','TRANSFERT','INVENTAIRE')),
    -- E=entrée S=sortie (pgms) et +/- (convention schéma original)
    CONSTRAINT ck_mvt_sens     CHECK (mvt_sens IN ('E','S','+','-')),
    CONSTRAINT ck_mvt_qte      CHECK (mvt_quantite > 0),
    CONSTRAINT ck_mvt_stocks   CHECK (mvt_stock_apres >= 0)
);

CREATE INDEX idx_mvt_art       ON gstk.mouvements_stock (mvt_art_code);
CREATE INDEX idx_mvt_date      ON gstk.mouvements_stock (mvt_date);
CREATE INDEX idx_mvt_type      ON gstk.mouvements_stock (mvt_type);
CREATE INDEX idx_mvt_bon       ON gstk.mouvements_stock (mvt_num_bon);
CREATE INDEX idx_mvt_op        ON gstk.mouvements_stock (mvt_operateur);
CREATE INDEX idx_mvt_timestamp ON gstk.mouvements_stock (mvt_timestamp DESC);

-- ============================================================
-- TABLE : OPERATEURS
-- ============================================================
CREATE TABLE gstk.operateurs (
    ope_code          CHAR(10)        NOT NULL,
    ope_nom           VARCHAR(50)     NOT NULL,
    ope_prenom        VARCHAR(50),
    ope_password_hash CHAR(64)        NOT NULL,
    ope_profil        CHAR(15)        NOT NULL,
    ope_terminal_def  CHAR(10),
    ope_date_creation TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    ope_derniere_cnx  TIMESTAMP,
    ope_nb_cnxions    INTEGER         DEFAULT 0,
    ope_statut        CHAR(10)        DEFAULT 'ACTIF',
    ope_date_exp_pwd  DATE,
    CONSTRAINT pk_operateurs  PRIMARY KEY (ope_code),
    CONSTRAINT ck_ope_profil  CHECK (ope_profil IN ('ADMIN','GESTIONNAIRE','LECTEUR')),
    CONSTRAINT ck_ope_statut  CHECK (ope_statut IN ('ACTIF','INACTIF','VERROUILLE'))
);

-- ============================================================
-- TRIGGERS (PL/pgSQL — syntaxe PostgreSQL)
-- ============================================================

-- Trigger : mise à jour date_maj articles
CREATE OR REPLACE FUNCTION gstk.fn_art_date_maj()
RETURNS TRIGGER AS $$
BEGIN
    NEW.art_date_maj := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_art_date_maj
BEFORE UPDATE ON gstk.articles
FOR EACH ROW EXECUTE FUNCTION gstk.fn_art_date_maj();

-- ============================================================
-- VUES
-- ============================================================
CREATE VIEW gstk.v_stock_complet AS
SELECT
    a.art_code, a.art_designation, a.art_categorie,
    a.art_unite, a.art_qte_stock, a.art_qte_min, a.art_qte_max,
    a.art_prix_achat, a.art_prix_vente,
    a.art_qte_stock * a.art_prix_achat  AS valeur_stock_achat,
    a.art_qte_stock * a.art_prix_vente  AS valeur_stock_vente,
    a.art_emplacement, a.frn_code, a.art_delai_appro,
    CASE
        WHEN a.art_qte_stock = 0                      THEN 'RUPTURE'
        WHEN a.art_qte_stock <= a.art_qte_min         THEN 'CRITIQUE'
        WHEN a.art_qte_stock <= a.art_qte_min * 1.5   THEN 'FAIBLE'
        WHEN a.art_qte_stock >= a.art_qte_max         THEN 'SATURE'
        ELSE                                               'NORMAL'
    END AS statut_stock,
    a.art_statut, a.art_date_maj
FROM gstk.articles a
WHERE a.art_statut = 'ACTIF';

CREATE VIEW gstk.v_alertes_actives AS
SELECT
    a.art_code, a.art_designation, a.art_categorie,
    a.art_qte_stock, a.art_qte_min,
    a.art_qte_min - a.art_qte_stock AS qte_a_commander,
    a.art_delai_appro,
    CASE
        WHEN a.art_qte_stock = 0              THEN 'RUPTURE'
        WHEN a.art_qte_stock <= a.art_qte_min THEN 'CRITIQUE'
        ELSE                                       'FAIBLE'
    END AS niveau_alerte
FROM gstk.articles a
WHERE a.art_statut    = 'ACTIF'
  AND a.art_qte_stock <= a.art_qte_min
ORDER BY a.art_qte_stock ASC;

CREATE VIEW gstk.v_stats_categories AS
SELECT
    a.art_categorie,
    COUNT(*)                                    AS nb_articles,
    SUM(a.art_qte_stock * a.art_prix_achat)     AS valeur_totale,
    SUM(a.art_qte_stock)                        AS qte_totale,
    COUNT(CASE WHEN a.art_qte_stock <= a.art_qte_min THEN 1 END) AS nb_alertes
FROM gstk.articles a
WHERE a.art_statut = 'ACTIF'
GROUP BY a.art_categorie;

-- ============================================================
-- DONNEES DE REFERENCE
-- ============================================================
INSERT INTO gstk.categories VALUES
('ELECTRONIQUE', 'Matériel Electronique',   'ELECTRON.', NULL, '606100', 20.00, 'ACTIF', 1),
('BUREAUTIQUE',  'Fournitures Bureautique', 'BUREAUT.',  NULL, '606200', 20.00, 'ACTIF', 2),
('OUTILLAGE',    'Outillage & Matériel',    'OUTILLA.',  NULL, '606300', 20.00, 'ACTIF', 3),
('ALIMENTAIRE',  'Produits Alimentaires',   'ALIMENT.',  NULL, '606400',  5.50, 'ACTIF', 4),
('AUTRE',        'Divers / Non classé',     'AUTRE',     NULL, '606900', 20.00, 'ACTIF', 9);

INSERT INTO gstk.operateurs
    (ope_code, ope_nom, ope_prenom, ope_password_hash, ope_profil, ope_statut)
VALUES
('ADMIN01', 'ADMINISTRATEUR', 'SYSTEME', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 'ADMIN',        'ACTIF'),
('USER01',  'MARTIN',         'PIERRE',  'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'GESTIONNAIRE', 'ACTIF'),
('USER02',  'DUPONT',         'MARIE',   'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'LECTEUR',      'ACTIF');

\echo '✓ Schema GSTK créé avec succès (PostgreSQL)'
