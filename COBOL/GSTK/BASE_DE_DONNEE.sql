-- ============================================================
-- SGBD : IBM Db2 for z/OS (compatible PostgreSQL / MySQL)
-- SCHEMA : GSTK
-- VERSION : 2.4
-- DATE : 2025-06-15
-- ============================================================

-- ============================================================
-- TABLE 1 : ARTICLES
-- Référentiel central des articles en stock
-- ============================================================

CREATE TABLE GSTK.ARTICLES (
    -- Identification
    ART_CODE          CHAR(10)        NOT NULL,   -- Code article unique  ex: ART-001
    ART_DESIGNATION   VARCHAR(50)     NOT NULL,   -- Libellé article
    ART_DESCRIPTION   VARCHAR(200),               -- Description longue
    ART_CODE_BARRE    CHAR(13),                   -- EAN-13
    
    -- Classification
    ART_CATEGORIE     CHAR(15)        NOT NULL,   -- ELECTRONIQUE / BUREAUTIQUE...
    ART_SOUS_CAT      CHAR(15),                   -- Sous-catégorie optionnelle
    ART_UNITE         CHAR(10)        NOT NULL    -- UNITE / KG / LITRE / METRE
                      DEFAULT 'UNITE',
    
    -- Stock
    ART_QTE_STOCK     DECIMAL(10,3)   NOT NULL    -- Quantité en stock
                      DEFAULT 0,
    ART_QTE_MIN       DECIMAL(10,3)   NOT NULL    -- Seuil alerte minimum
                      DEFAULT 0,
    ART_QTE_MAX       DECIMAL(10,3)   NOT NULL    -- Stock maximum autorisé
                      DEFAULT 9999,
    ART_QTE_REORDER   DECIMAL(10,3),              -- Quantité de réappro. conseillée
    
    -- Prix
    ART_PRIX_ACHAT    DECIMAL(12,4)   NOT NULL    -- Prix d'achat unitaire
                      DEFAULT 0,
    ART_PRIX_VENTE    DECIMAL(12,4),              -- Prix de vente unitaire
    ART_TVA_TAUX      DECIMAL(5,2)                -- Taux TVA applicable (%)
                      DEFAULT 20.00,
    ART_DEVISE        CHAR(3)                     -- EUR / USD / GBP
                      DEFAULT 'EUR',
    
    -- Logistique
    ART_EMPLACEMENT   CHAR(15),                   -- Localisation entrepôt ex: A1-R2-C3
    ART_POIDS_KG      DECIMAL(8,3),               -- Poids unitaire en kg
    ART_VOLUME_L      DECIMAL(8,3),               -- Volume unitaire en litres
    ART_DELAI_APPRO   SMALLINT                    -- Délai réappro. en jours
                      DEFAULT 5,
    
    -- Fournisseur principal
    FRN_CODE          CHAR(10),                   -- FK → FOURNISSEURS
    
    -- Statut & Audit
    ART_STATUT        CHAR(10)        NOT NULL    -- ACTIF / INACTIF / ARCHIVE
                      DEFAULT 'ACTIF',
    ART_DATE_CREATION TIMESTAMP       NOT NULL
                      DEFAULT CURRENT_TIMESTAMP,
    ART_DATE_MAJ      TIMESTAMP,                  -- Dernière modification
    ART_OPERATEUR     CHAR(10)        NOT NULL,   -- Opérateur créateur
    ART_COMMENTAIRE   VARCHAR(200),               -- Notes libres
    
    -- Contraintes
    CONSTRAINT PK_ARTICLES PRIMARY KEY (ART_CODE),
    CONSTRAINT CK_QTE_STOCK  CHECK (ART_QTE_STOCK  >= 0),
    CONSTRAINT CK_QTE_MIN    CHECK (ART_QTE_MIN    >= 0),
    CONSTRAINT CK_QTE_MAX    CHECK (ART_QTE_MAX    >= ART_QTE_MIN),
    CONSTRAINT CK_PRIX_ACHAT CHECK (ART_PRIX_ACHAT >= 0),
    CONSTRAINT CK_TVA        CHECK (ART_TVA_TAUX BETWEEN 0 AND 100),
    CONSTRAINT CK_STATUT     CHECK (ART_STATUT IN ('ACTIF','INACTIF','ARCHIVE')),
    CONSTRAINT CK_UNITE      CHECK (ART_UNITE IN 
                              ('UNITE','KG','LITRE','METRE','BOITE','PALETTE'))
);

-- Index articles
CREATE UNIQUE INDEX IDX_ART_BARRE    ON GSTK.ARTICLES (ART_CODE_BARRE)
       WHERE ART_CODE_BARRE IS NOT NULL;
CREATE        INDEX IDX_ART_CAT      ON GSTK.ARTICLES (ART_CATEGORIE);
CREATE        INDEX IDX_ART_STATUT   ON GSTK.ARTICLES (ART_STATUT);
CREATE        INDEX IDX_ART_FRN      ON GSTK.ARTICLES (FRN_CODE);
CREATE        INDEX IDX_ART_EMPL     ON GSTK.ARTICLES (ART_EMPLACEMENT);

COMMENT ON TABLE  GSTK.ARTICLES              IS 'Référentiel des articles en stock';
COMMENT ON COLUMN GSTK.ARTICLES.ART_CODE     IS 'Code article unique - Format: ART-NNN';
COMMENT ON COLUMN GSTK.ARTICLES.ART_QTE_MIN  IS 'Seuil déclenchant une alerte critique';


-- ============================================================
-- TABLE 2 : FOURNISSEURS
-- Référentiel des fournisseurs
-- ============================================================

CREATE TABLE GSTK.FOURNISSEURS (
    -- Identification
    FRN_CODE          CHAR(10)        NOT NULL,   -- Code fournisseur ex: FRN-001
    FRN_NOM           VARCHAR(60)     NOT NULL,   -- Raison sociale
    FRN_NOM_COURT     CHAR(20),                   -- Nom abrégé pour affichage 3270
    
    -- Contact
    FRN_ADRESSE1      VARCHAR(80),
    FRN_ADRESSE2      VARCHAR(80),
    FRN_CODE_POSTAL   CHAR(10),
    FRN_VILLE         VARCHAR(50),
    FRN_PAYS          CHAR(3)         DEFAULT 'FR',
    FRN_TEL           CHAR(20),
    FRN_EMAIL         VARCHAR(80),
    FRN_CONTACT       VARCHAR(50),                -- Nom du commercial
    
    -- Commercial
    FRN_DEVISE        CHAR(3)         DEFAULT 'EUR',
    FRN_DELAI_MOYEN   SMALLINT        DEFAULT 5,  -- Délai livraison moyen (jours)
    FRN_MIN_CMD       DECIMAL(12,2),              -- Montant minimum commande
    FRN_FRANCO_PORT   DECIMAL(12,2),              -- Seuil franco de port
    FRN_REMISE_PCT    DECIMAL(5,2)    DEFAULT 0,  -- Remise habituelle (%)
    FRN_COND_PAIE     CHAR(20),                   -- Conditions paiement ex: 30J NET
    
    -- Statut & Audit
    FRN_STATUT        CHAR(10)        NOT NULL    DEFAULT 'ACTIF',
    FRN_DATE_CREATION TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    FRN_DATE_MAJ      TIMESTAMP,
    FRN_OPERATEUR     CHAR(10)        NOT NULL,
    FRN_COMMENTAIRE   VARCHAR(200),
    
    CONSTRAINT PK_FOURNISSEURS PRIMARY KEY (FRN_CODE),
    CONSTRAINT CK_FRN_STATUT CHECK (FRN_STATUT IN ('ACTIF','INACTIF','BLACKLIST'))
);

CREATE INDEX IDX_FRN_NOM    ON GSTK.FOURNISSEURS (FRN_NOM);
CREATE INDEX IDX_FRN_STATUT ON GSTK.FOURNISSEURS (FRN_STATUT);

COMMENT ON TABLE GSTK.FOURNISSEURS IS 'Référentiel fournisseurs';


-- ============================================================
-- TABLE 3 : BONS_ENTREE
-- En-têtes des bons de réception marchandise
-- ============================================================

CREATE TABLE GSTK.BONS_ENTREE (
    -- Identification
    BEN_NUMERO        CHAR(12)        NOT NULL,   -- N° bon ex: BR-001005
    BEN_DATE          DATE            NOT NULL    -- Date de réception
                      DEFAULT CURRENT_DATE,
    BEN_HEURE         TIME            NOT NULL    -- Heure de réception
                      DEFAULT CURRENT_TIME,
    
    -- Fournisseur
    FRN_CODE          CHAR(10),                   -- FK → FOURNISSEURS
    BEN_FOURNISSEUR   VARCHAR(60),                -- Nom fournisseur (libre si hors réf.)
    BEN_REF_FRN       VARCHAR(30),                -- Référence bon fournisseur
    BEN_NUM_CMD       VARCHAR(20),                -- N° bon de commande associé
    
    -- Totaux (calculés)
    BEN_NB_LIGNES     SMALLINT        DEFAULT 0,
    BEN_MONTANT_HT    DECIMAL(14,2)   DEFAULT 0,
    BEN_MONTANT_TVA   DECIMAL(14,2)   DEFAULT 0,
    BEN_MONTANT_TTC   DECIMAL(14,2)   DEFAULT 0,
    
    -- Logistique
    BEN_TRANSPORTEUR  VARCHAR(40),
    BEN_NUM_BL        VARCHAR(30),                -- N° bordereau de livraison
    
    -- Statut & Audit
    BEN_STATUT        CHAR(15)        NOT NULL    DEFAULT 'VALIDE',
    BEN_COMMENTAIRE   VARCHAR(300),
    BEN_DATE_SAISIE   TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    BEN_OPERATEUR     CHAR(10)        NOT NULL,
    BEN_POSTE         CHAR(10),                   -- Terminal 3270 de saisie
    
    CONSTRAINT PK_BONS_ENTREE PRIMARY KEY (BEN_NUMERO),
    CONSTRAINT FK_BEN_FRN FOREIGN KEY (FRN_CODE)
               REFERENCES GSTK.FOURNISSEURS (FRN_CODE),
    CONSTRAINT CK_BEN_STATUT CHECK (BEN_STATUT IN 
               ('BROUILLON','VALIDE','ANNULE','LITIGE'))
);

CREATE INDEX IDX_BEN_DATE   ON GSTK.BONS_ENTREE (BEN_DATE);
CREATE INDEX IDX_BEN_FRN    ON GSTK.BONS_ENTREE (FRN_CODE);
CREATE INDEX IDX_BEN_STATUT ON GSTK.BONS_ENTREE (BEN_STATUT);

COMMENT ON TABLE GSTK.BONS_ENTREE IS 'En-têtes bons de réception marchandise';


-- ============================================================
-- TABLE 4 : LIGNES_ENTREE
-- Détail des lignes de bons de réception
-- ============================================================

CREATE TABLE GSTK.LIGNES_ENTREE (
    -- Clé composée
    BEN_NUMERO        CHAR(12)        NOT NULL,   -- FK → BONS_ENTREE
    LEN_LIGNE         SMALLINT        NOT NULL,   -- N° ligne dans le bon
    
    -- Article
    ART_CODE          CHAR(10)        NOT NULL,   -- FK → ARTICLES
    LEN_DESIGNATION   VARCHAR(50),                -- Désignation au moment de la saisie
    
    -- Quantités
    LEN_QTE_RECUE     DECIMAL(10,3)   NOT NULL,   -- Quantité physiquement reçue
    LEN_QTE_CMD       DECIMAL(10,3),              -- Quantité commandée (pour contrôle)
    LEN_QTE_REFUS     DECIMAL(10,3)   DEFAULT 0, -- Quantité refusée (casse, non-conf.)
    LEN_UNITE         CHAR(10),                   -- Unité de mesure
    
    -- Prix
    LEN_PRIX_ACHAT    DECIMAL(12,4)   NOT NULL,   -- Prix unitaire d'achat
    LEN_TVA_TAUX      DECIMAL(5,2)    DEFAULT 20.00,
    LEN_MONTANT_HT    DECIMAL(14,2),              -- Calculé : QTE × PRIX
    LEN_MONTANT_TVA   DECIMAL(14,2),
    LEN_MONTANT_TTC   DECIMAL(14,2),
    
    -- Traçabilité lot
    LEN_NUM_LOT       VARCHAR(30),                -- N° de lot fabricant
    LEN_DATE_FAB      DATE,                       -- Date de fabrication
    LEN_DATE_EXP      DATE,                       -- Date d'expiration
    LEN_NUM_SERIE     VARCHAR(50),                -- N° de série (si applicable)
    
    -- Emplacement destination
    LEN_EMPLACEMENT   CHAR(15),                   -- Emplacement de rangement
    
    -- Statut ligne
    LEN_STATUT        CHAR(10)        DEFAULT 'VALIDE',
    LEN_COMMENTAIRE   VARCHAR(200),
    
    -- Stock avant / après (snapshot)
    LEN_STOCK_AVANT   DECIMAL(10,3),              -- Stock avant réception
    LEN_STOCK_APRES   DECIMAL(10,3),              -- Stock après réception
    
    CONSTRAINT PK_LIGNES_ENTREE PRIMARY KEY (BEN_NUMERO, LEN_LIGNE),
    CONSTRAINT FK_LEN_BON FOREIGN KEY (BEN_NUMERO)
               REFERENCES GSTK.BONS_ENTREE (BEN_NUMERO)
               ON DELETE CASCADE,
    CONSTRAINT FK_LEN_ART FOREIGN KEY (ART_CODE)
               REFERENCES GSTK.ARTICLES (ART_CODE),
    CONSTRAINT CK_LEN_QTE CHECK (LEN_QTE_RECUE >= 0),
    CONSTRAINT CK_LEN_PRIX CHECK (LEN_PRIX_ACHAT >= 0)
);

CREATE INDEX IDX_LEN_ART ON GSTK.LIGNES_ENTREE (ART_CODE);
CREATE INDEX IDX_LEN_LOT ON GSTK.LIGNES_ENTREE (LEN_NUM_LOT)
       WHERE LEN_NUM_LOT IS NOT NULL;

COMMENT ON TABLE GSTK.LIGNES_ENTREE IS 'Lignes détail des bons de réception';


-- ============================================================
-- TABLE 5 : BONS_SORTIE
-- En-têtes des bons de sortie marchandise
-- ============================================================

CREATE TABLE GSTK.BONS_SORTIE (
    -- Identification
    BSO_NUMERO        CHAR(12)        NOT NULL,   -- N° bon ex: BS-002006
    BSO_DATE          DATE            NOT NULL    DEFAULT CURRENT_DATE,
    BSO_HEURE         TIME            NOT NULL    DEFAULT CURRENT_TIME,
    
    -- Demandeur
    BSO_DEMANDEUR     VARCHAR(60),                -- Nom / service demandeur
    BSO_CENTRE_COUT   CHAR(15),                   -- Centre de coût imputable
    BSO_NUM_CMD       VARCHAR(20),                -- N° bon de commande client
    BSO_NUM_AFFAIRE   VARCHAR(20),                -- N° affaire (si applicable)
    
    -- Motif
    BSO_MOTIF         CHAR(20)        NOT NULL,   -- VENTE / USAGE INTERNE...
    
    -- Totaux
    BSO_NB_LIGNES     SMALLINT        DEFAULT 0,
    BSO_MONTANT_HT    DECIMAL(14,2)   DEFAULT 0,
    BSO_MONTANT_TTC   DECIMAL(14,2)   DEFAULT 0,
    
    -- Livraison
    BSO_ADRESSE_LIV   VARCHAR(200),               -- Adresse de livraison
    BSO_TRANSPORTEUR  VARCHAR(40),
    
    -- Statut & Audit
    BSO_STATUT        CHAR(15)        NOT NULL    DEFAULT 'VALIDE',
    BSO_COMMENTAIRE   VARCHAR(300),
    BSO_DATE_SAISIE   TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    BSO_OPERATEUR     CHAR(10)        NOT NULL,
    BSO_POSTE         CHAR(10),
    
    CONSTRAINT PK_BONS_SORTIE PRIMARY KEY (BSO_NUMERO),
    CONSTRAINT CK_BSO_MOTIF CHECK (BSO_MOTIF IN (
               'VENTE','CASSE','TRANSFERT','INVENTAIRE',
               'USAGE INTERNE','DESTRUCTION','RETOUR FRN')),
    CONSTRAINT CK_BSO_STATUT CHECK (BSO_STATUT IN (
               'BROUILLON','VALIDE','ANNULE','EN COURS'))
);

CREATE INDEX IDX_BSO_DATE      ON GSTK.BONS_SORTIE (BSO_DATE);
CREATE INDEX IDX_BSO_DEMANDEUR ON GSTK.BONS_SORTIE (BSO_DEMANDEUR);
CREATE INDEX IDX_BSO_MOTIF     ON GSTK.BONS_SORTIE (BSO_MOTIF);
CREATE INDEX IDX_BSO_STATUT    ON GSTK.BONS_SORTIE (BSO_STATUT);

COMMENT ON TABLE GSTK.BONS_SORTIE IS 'En-têtes bons de sortie marchandise';


-- ============================================================
-- TABLE 6 : LIGNES_SORTIE
-- Détail des lignes de bons de sortie
-- ============================================================

CREATE TABLE GSTK.LIGNES_SORTIE (
    BSO_NUMERO        CHAR(12)        NOT NULL,   -- FK → BONS_SORTIE
    LSO_LIGNE         SMALLINT        NOT NULL,
    
    ART_CODE          CHAR(10)        NOT NULL,   -- FK → ARTICLES
    LSO_DESIGNATION   VARCHAR(50),
    
    LSO_QTE_SORTIE    DECIMAL(10,3)   NOT NULL,   -- Quantité sortie
    LSO_UNITE         CHAR(10),
    
    LSO_PRIX_UNIT     DECIMAL(12,4),              -- Prix unitaire au moment de la sortie
    LSO_TVA_TAUX      DECIMAL(5,2)    DEFAULT 20.00,
    LSO_MONTANT_HT    DECIMAL(14,2),
    LSO_MONTANT_TTC   DECIMAL(14,2),
    
    LSO_NUM_LOT       VARCHAR(30),                -- Lot prélevé (FIFO/FEFO)
    LSO_EMPLACEMENT   CHAR(15),                   -- Emplacement source
    
    LSO_STATUT        CHAR(10)        DEFAULT 'VALIDE',
    LSO_COMMENTAIRE   VARCHAR(200),
    
    LSO_STOCK_AVANT   DECIMAL(10,3),
    LSO_STOCK_APRES   DECIMAL(10,3),
    
    CONSTRAINT PK_LIGNES_SORTIE PRIMARY KEY (BSO_NUMERO, LSO_LIGNE),
    CONSTRAINT FK_LSO_BON FOREIGN KEY (BSO_NUMERO)
               REFERENCES GSTK.BONS_SORTIE (BSO_NUMERO)
               ON DELETE CASCADE,
    CONSTRAINT FK_LSO_ART FOREIGN KEY (ART_CODE)
               REFERENCES GSTK.ARTICLES (ART_CODE),
    CONSTRAINT CK_LSO_QTE CHECK (LSO_QTE_SORTIE > 0)
);

CREATE INDEX IDX_LSO_ART ON GSTK.LIGNES_SORTIE (ART_CODE);

COMMENT ON TABLE GSTK.LIGNES_SORTIE IS 'Lignes détail des bons de sortie';


-- ============================================================
-- TABLE 7 : MOUVEMENTS_STOCK
-- Journal complet de tous les mouvements (audit trail)
-- ============================================================

CREATE TABLE GSTK.MOUVEMENTS_STOCK (
    -- Clé technique auto-incrémentée
    MVT_ID            BIGINT          NOT NULL
                      GENERATED ALWAYS AS IDENTITY
                      (START WITH 1 INCREMENT BY 1),
    
    -- Horodatage
    MVT_DATE          DATE            NOT NULL    DEFAULT CURRENT_DATE,
    MVT_HEURE         TIME            NOT NULL    DEFAULT CURRENT_TIME,
    MVT_TIMESTAMP     TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    
    -- Type de mouvement
    MVT_TYPE          CHAR(10)        NOT NULL,   -- ENTREE / SORTIE / AJUST / TRANSFERT
    MVT_SENS          CHAR(1)         NOT NULL,   -- + (entrée) ou - (sortie)
    MVT_MOTIF         CHAR(20),                   -- Motif détaillé
    
    -- Article
    ART_CODE          CHAR(10)        NOT NULL,   -- FK → ARTICLES
    MVT_DESIGNATION   VARCHAR(50),                -- Snapshot désignation
    MVT_UNITE         CHAR(10),
    
    -- Quantités & Prix
    MVT_QUANTITE      DECIMAL(10,3)   NOT NULL,   -- Toujours positif
    MVT_PRIX_UNIT     DECIMAL(12,4),
    MVT_MONTANT_HT    DECIMAL(14,2),
    
    -- Stock snapshot
    MVT_STOCK_AVANT   DECIMAL(10,3)   NOT NULL,
    MVT_STOCK_APRES   DECIMAL(10,3)   NOT NULL,
    
    -- Référence document source
    MVT_NUM_BON       CHAR(12),                   -- BR-XXXXXX ou BS-XXXXXX
    MVT_NUM_LIGNE     SMALLINT,                   -- N° ligne dans le bon
    
    -- Traçabilité
    MVT_NUM_LOT       VARCHAR(30),
    MVT_EMPLACEMENT   CHAR(15),
    
    -- Tiers
    MVT_TIERS         VARCHAR(60),                -- Fournisseur ou demandeur
    MVT_CENTRE_COUT   CHAR(15),
    
    -- Audit
    MVT_OPERATEUR     CHAR(10)        NOT NULL,
    MVT_POSTE         CHAR(10),                   -- Terminal 3270
    MVT_PROGRAMME     CHAR(8),                    -- Programme COBOL source
    MVT_COMMENTAIRE   VARCHAR(300),
    
    CONSTRAINT PK_MOUVEMENTS  PRIMARY KEY (MVT_ID),
    CONSTRAINT FK_MVT_ART     FOREIGN KEY (ART_CODE)
               REFERENCES GSTK.ARTICLES (ART_CODE),
    CONSTRAINT CK_MVT_TYPE    CHECK (MVT_TYPE IN 
               ('ENTREE','SORTIE','AJUST+','AJUST-','TRANSFERT','INVENTAIRE')),
    CONSTRAINT CK_MVT_SENS    CHECK (MVT_SENS IN ('+','-')),
    CONSTRAINT CK_MVT_QTE     CHECK (MVT_QUANTITE > 0),
    CONSTRAINT CK_MVT_STOCKS  CHECK (MVT_STOCK_APRES >= 0)
);

CREATE INDEX IDX_MVT_ART       ON GSTK.MOUVEMENTS_STOCK (ART_CODE);
CREATE INDEX IDX_MVT_DATE      ON GSTK.MOUVEMENTS_STOCK (MVT_DATE);
CREATE INDEX IDX_MVT_TYPE      ON GSTK.MOUVEMENTS_STOCK (MVT_TYPE);
CREATE INDEX IDX_MVT_BON       ON GSTK.MOUVEMENTS_STOCK (MVT_NUM_BON);
CREATE INDEX IDX_MVT_OP        ON GSTK.MOUVEMENTS_STOCK (MVT_OPERATEUR);
CREATE INDEX IDX_MVT_TIMESTAMP ON GSTK.MOUVEMENTS_STOCK (MVT_TIMESTAMP DESC);

COMMENT ON TABLE  GSTK.MOUVEMENTS_STOCK          IS 'Journal audit trail de tous les mouvements';
COMMENT ON COLUMN GSTK.MOUVEMENTS_STOCK.MVT_SENS IS '+ = entrée stock, - = sortie stock';


-- ============================================================
-- TABLE 8 : CATEGORIES
-- Référentiel des catégories d'articles
-- ============================================================

CREATE TABLE GSTK.CATEGORIES (
    CAT_CODE          CHAR(15)        NOT NULL,
    CAT_LIBELLE       VARCHAR(50)     NOT NULL,
    CAT_LIBELLE_COURT CHAR(10),                   -- Pour affichage 3270
    CAT_DESCRIPTION   VARCHAR(200),
    CAT_COMPTE_COMPTA CHAR(10),                   -- Compte comptable associé
    CAT_TVA_DEFAUT    DECIMAL(5,2)    DEFAULT 20.00,
    CAT_STATUT        CHAR(10)        DEFAULT 'ACTIF',
    CAT_ORDRE_AFF     SMALLINT        DEFAULT 99, -- Ordre d'affichage
    
    CONSTRAINT PK_CATEGORIES PRIMARY KEY (CAT_CODE),
    CONSTRAINT CK_CAT_STATUT CHECK (CAT_STATUT IN ('ACTIF','INACTIF'))
);

COMMENT ON TABLE GSTK.CATEGORIES IS 'Référentiel des catégories articles';


-- ============================================================
-- TABLE 9 : EMPLACEMENTS
-- Plan de l'entrepôt / magasin
-- ============================================================

CREATE TABLE GSTK.EMPLACEMENTS (
    EMP_CODE          CHAR(15)        NOT NULL,   -- ex: A1-R2-C3
    EMP_ZONE          CHAR(5),                    -- Zone ex: A, B, C, D
    EMP_ALLEE         CHAR(5),                    -- Allée
    EMP_RANGEE        CHAR(5),                    -- Rangée (R1, R2...)
    EMP_COLONNE       CHAR(5),                    -- Colonne (C1, C2...)
    EMP_NIVEAU        CHAR(5),                    -- Niveau / étage
    EMP_TYPE          CHAR(15),                   -- RAYONNAGE / SOL / FRIGO...
    EMP_CAPACITE_MAX  DECIMAL(10,3),              -- Capacité max (unités ou m3)
    EMP_LONGUEUR_CM   SMALLINT,
    EMP_LARGEUR_CM    SMALLINT,
    EMP_HAUTEUR_CM    SMALLINT,
    EMP_STATUT        CHAR(10)        DEFAULT 'LIBRE', -- LIBRE / OCCUPE / BLOQUE
    EMP_COMMENTAIRE   VARCHAR(200),
    
    CONSTRAINT PK_EMPLACEMENTS PRIMARY KEY (EMP_CODE),
    CONSTRAINT CK_EMP_STATUT CHECK (EMP_STATUT IN ('LIBRE','OCCUPE','BLOQUE','RESERVE'))
);

CREATE INDEX IDX_EMP_ZONE   ON GSTK.EMPLACEMENTS (EMP_ZONE);
CREATE INDEX IDX_EMP_STATUT ON GSTK.EMPLACEMENTS (EMP_STATUT);

COMMENT ON TABLE GSTK.EMPLACEMENTS IS 'Plan de localisation entrepôt';


-- ============================================================
-- TABLE 10 : OPERATEURS
-- Utilisateurs du système (authentification 3270)
-- ============================================================

CREATE TABLE GSTK.OPERATEURS (
    OPE_CODE          CHAR(10)        NOT NULL,   -- ex: ADMIN01, USER01
    OPE_NOM           VARCHAR(50)     NOT NULL,
    OPE_PRENOM        VARCHAR(50),
    OPE_PASSWORD_HASH CHAR(64)        NOT NULL,   -- SHA-256
    OPE_PROFIL        CHAR(15)        NOT NULL,   -- ADMIN / GESTIONNAIRE / LECTEUR
    OPE_TERMINAL_DEF  CHAR(10),                   -- Terminal 3270 par défaut
    OPE_DATE_CREATION TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    OPE_DERNIERE_CNX  TIMESTAMP,
    OPE_NB_CNXIONS    INTEGER         DEFAULT 0,
    OPE_STATUT        CHAR(10)        DEFAULT 'ACTIF',
    OPE_DATE_EXP_PWD  DATE,                       -- Expiration mot de passe
    
    CONSTRAINT PK_OPERATEURS PRIMARY KEY (OPE_CODE),
    CONSTRAINT CK_OPE_PROFIL CHECK (OPE_PROFIL IN ('ADMIN','GESTIONNAIRE','LECTEUR')),
    CONSTRAINT CK_OPE_STATUT CHECK (OPE_STATUT IN ('ACTIF','INACTIF','VERROUILLE'))
);

COMMENT ON TABLE GSTK.OPERATEURS IS 'Utilisateurs et droits accès système';


-- ============================================================
-- TABLE 11 : SESSIONS
-- Journal des sessions 3270 (sécurité / audit)
-- ============================================================

CREATE TABLE GSTK.SESSIONS (
    SES_ID            CHAR(25)        NOT NULL,   -- ex: SES-20250615-001
    OPE_CODE          CHAR(10)        NOT NULL,   -- FK → OPERATEURS
    SES_TERMINAL      CHAR(10)        NOT NULL,   -- Terminal 3270 ex: TRM-042
    SES_DATE_DEBUT    TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    SES_DATE_FIN      TIMESTAMP,
    SES_STATUT        CHAR(10)        DEFAULT 'OUVERTE',  -- OUVERTE / FERMEE / TIMEOUT
    SES_IP_ORIGINE    VARCHAR(45),                -- Adresse IP
    SES_NB_ECRANS     INTEGER         DEFAULT 0,  -- Nb d'écrans consultés
    SES_NB_TRANSACT   INTEGER         DEFAULT 0,  -- Nb de transactions
    
    CONSTRAINT PK_SESSIONS  PRIMARY KEY (SES_ID),
    CONSTRAINT FK_SES_OPE   FOREIGN KEY (OPE_CODE)
               REFERENCES GSTK.OPERATEURS (OPE_CODE),
    CONSTRAINT CK_SES_STATUT CHECK (SES_STATUT IN ('OUVERTE','FERMEE','TIMEOUT','ERREUR'))
);

CREATE INDEX IDX_SES_OPE  ON GSTK.SESSIONS (OPE_CODE);
CREATE INDEX IDX_SES_DATE ON GSTK.SESSIONS (SES_DATE_DEBUT);

COMMENT ON TABLE GSTK.SESSIONS IS 'Journal des sessions terminaux 3270';


-- ============================================================
-- TABLE 12 : ALERTES_STOCK
-- Historique des alertes générées
-- ============================================================

CREATE TABLE GSTK.ALERTES_STOCK (
    ALT_ID            BIGINT          NOT NULL
                      GENERATED ALWAYS AS IDENTITY,
    ART_CODE          CHAR(10)        NOT NULL,   -- FK → ARTICLES
    ALT_TYPE          CHAR(15)        NOT NULL,   -- RUPTURE / CRITIQUE / FAIBLE / DEPASST
    ALT_DATE_GEN      TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    ALT_QTE_MOMENT    DECIMAL(10,3)   NOT NULL,   -- Stock au moment de l'alerte
    ALT_QTE_MIN       DECIMAL(10,3)   NOT NULL,   -- Seuil au moment de l'alerte
    ALT_DATE_RESOL    TIMESTAMP,                  -- Date de résolution
    ALT_STATUT        CHAR(10)        DEFAULT 'ACTIVE',
    ALT_OPERATEUR_RES CHAR(10),                   -- Opérateur ayant résolu
    ALT_COMMENTAIRE   VARCHAR(200),
    
    CONSTRAINT PK_ALERTES      PRIMARY KEY (ALT_ID),
    CONSTRAINT FK_ALT_ART      FOREIGN KEY (ART_CODE)
               REFERENCES GSTK.ARTICLES (ART_CODE),
    CONSTRAINT CK_ALT_TYPE     CHECK (ALT_TYPE IN 
               ('RUPTURE','CRITIQUE','FAIBLE','DEPASSEMENT_MAX')),
    CONSTRAINT CK_ALT_STATUT   CHECK (ALT_STATUT IN ('ACTIVE','RESOLUE','IGNOREE'))
);

CREATE INDEX IDX_ALT_ART    ON GSTK.ALERTES_STOCK (ART_CODE);
CREATE INDEX IDX_ALT_STATUT ON GSTK.ALERTES_STOCK (ALT_STATUT);
CREATE INDEX IDX_ALT_DATE   ON GSTK.ALERTES_STOCK (ALT_DATE_GEN DESC);

COMMENT ON TABLE GSTK.ALERTES_STOCK IS 'Historique des alertes de stock';


-- ============================================================
-- CLÉS ÉTRANGÈRES INTER-TABLES
-- ============================================================

ALTER TABLE GSTK.ARTICLES
    ADD CONSTRAINT FK_ART_FRN
    FOREIGN KEY (FRN_CODE)
    REFERENCES GSTK.FOURNISSEURS (FRN_CODE);

ALTER TABLE GSTK.ARTICLES
    ADD CONSTRAINT FK_ART_CAT
    FOREIGN KEY (ART_CATEGORIE)
    REFERENCES GSTK.CATEGORIES (CAT_CODE);

ALTER TABLE GSTK.ARTICLES
    ADD CONSTRAINT FK_ART_EMP
    FOREIGN KEY (ART_EMPLACEMENT)
    REFERENCES GSTK.EMPLACEMENTS (EMP_CODE);


-- ============================================================
-- VUES MÉTIER
-- ============================================================

-- Vue stock avec statut calculé
CREATE VIEW GSTK.V_STOCK_COMPLET AS
SELECT
    a.ART_CODE,
    a.ART_DESIGNATION,
    a.ART_CATEGORIE,
    c.CAT_LIBELLE_COURT        AS CAT_LIBELLE,
    a.ART_UNITE,
    a.ART_QTE_STOCK,
    a.ART_QTE_MIN,
    a.ART_QTE_MAX,
    a.ART_PRIX_ACHAT,
    a.ART_PRIX_VENTE,
    a.ART_QTE_STOCK * a.ART_PRIX_ACHAT  AS VALEUR_STOCK_ACHAT,
    a.ART_QTE_STOCK * a.ART_PRIX_VENTE  AS VALEUR_STOCK_VENTE,
    a.ART_EMPLACEMENT,
    a.FRN_CODE,
    f.FRN_NOM_COURT            AS FOURNISSEUR,
    a.ART_DELAI_APPRO,
    -- Statut calculé
    CASE
        WHEN a.ART_QTE_STOCK = 0                        THEN 'RUPTURE'
        WHEN a.ART_QTE_STOCK <= a.ART_QTE_MIN           THEN 'CRITIQUE'
        WHEN a.ART_QTE_STOCK <= a.ART_QTE_MIN * 1.5     THEN 'FAIBLE'
        WHEN a.ART_QTE_STOCK >= a.ART_QTE_MAX           THEN 'SATURE'
        ELSE                                                  'NORMAL'
    END                        AS STATUT_STOCK,
    -- Taux de remplissage (%)
    CASE
        WHEN a.ART_QTE_MAX = 0 THEN 0
        ELSE DECIMAL(a.ART_QTE_STOCK * 100 / a.ART_QTE_MAX, 5, 1)
    END                        AS TAUX_REMPLISSAGE,
    -- Ecart au seuil mini
    a.ART_QTE_STOCK - a.ART_QTE_MIN  AS ECART_SEUIL_MIN,
    a.ART_STATUT,
    a.ART_DATE_MAJ
FROM       GSTK.ARTICLES     a
LEFT JOIN  GSTK.CATEGORIES   c ON c.CAT_CODE  = a.ART_CATEGORIE
LEFT JOIN  GSTK.FOURNISSEURS f ON f.FRN_CODE  = a.FRN_CODE
WHERE a.ART_STATUT = 'ACTIF';

COMMENT ON VIEW GSTK.V_STOCK_COMPLET IS 'Vue stock enrichie avec statuts calculés';


-- Vue alertes actives
CREATE VIEW GSTK.V_ALERTES_ACTIVES AS
SELECT
    a.ART_CODE,
    a.ART_DESIGNATION,
    a.ART_CATEGORIE,
    a.ART_QTE_STOCK,
    a.ART_QTE_MIN,
    a.ART_QTE_MAX,
    a.ART_QTE_MIN - a.ART_QTE_STOCK  AS QTE_A_COMMANDER,
    f.FRN_NOM_COURT                   AS FOURNISSEUR,
    f.FRN_DELAI_MOYEN                 AS DELAI_APPRO_J,
    a.ART_EMPLACEMENT,
    CASE
        WHEN a.ART_QTE_STOCK = 0                THEN 'RUPTURE'
        WHEN a.ART_QTE_STOCK <= a.ART_QTE_MIN   THEN 'CRITIQUE'
        ELSE                                          'FAIBLE'
    END AS NIVEAU_ALERTE
FROM      GSTK.ARTICLES     a
LEFT JOIN GSTK.FOURNISSEURS f ON f.FRN_CODE = a.FRN_CODE
WHERE a.ART_STATUT    = 'ACTIF'
  AND a.ART_QTE_STOCK <= a.ART_QTE_MIN * 1.5
ORDER BY a.ART_QTE_STOCK ASC;

COMMENT ON VIEW GSTK.V_ALERTES_ACTIVES IS 'Articles sous seuil nécessitant action';


-- Vue statistiques par catégorie (pour écran rapport GSTK005)
CREATE VIEW GSTK.V_STATS_CATEGORIES AS
SELECT
    a.ART_CATEGORIE,
    c.CAT_LIBELLE,
    COUNT(*)                                        AS NB_ARTICLES,
    SUM(a.ART_QTE_STOCK * a.ART_PRIX_ACHAT)        AS VALEUR_TOTALE,
    SUM(a.ART_QTE_STOCK)                            AS QTE_TOTALE,
    COUNT(CASE WHEN a.ART_QTE_STOCK = 0       
               THEN 1 END)                          AS NB_RUPTURES,
    COUNT(CASE WHEN a.ART_QTE_STOCK <= a.ART_QTE_MIN 
               AND  a.ART_QTE_STOCK > 0
               THEN 1 END)                          AS NB_CRITIQUES,
    AVG(a.ART_PRIX_ACHAT)                           AS PRIX_MOYEN
FROM      GSTK.ARTICLES   a
LEFT JOIN GSTK.CATEGORIES c ON c.CAT_CODE = a.ART_CATEGORIE
WHERE a.ART_STATUT = 'ACTIF'
GROUP BY a.ART_CATEGORIE, c.CAT_LIBELLE;

COMMENT ON VIEW GSTK.V_STATS_CATEGORIES IS 'Statistiques stock par catégorie';


-- Vue historique mouvements enrichie (pour écran GSTK007)
CREATE VIEW GSTK.V_HISTORIQUE AS
SELECT
    m.MVT_ID,
    m.MVT_DATE,
    m.MVT_HEURE,
    m.MVT_TIMESTAMP,
    m.MVT_TYPE,
    m.MVT_SENS,
    m.MVT_NUM_BON,
    m.ART_CODE,
    m.MVT_DESIGNATION,
    m.MVT_QUANTITE,
    m.MVT_PRIX_UNIT,
    m.MVT_MONTANT_HT,
    m.MVT_STOCK_AVANT,
    m.MVT_STOCK_APRES,
    m.MVT_TIERS,
    m.MVT_OPERATEUR,
    m.MVT_POSTE,
    m.MVT_COMMENTAIRE,
    m.MVT_NUM_LOT,
    m.MVT_EMPLACEMENT,
    a.ART_DESIGNATION  AS ART_DESIGN_ACTUEL,
    a.ART_CATEGORIE
FROM      GSTK.MOUVEMENTS_STOCK m
LEFT JOIN GSTK.ARTICLES         a ON a.ART_CODE = m.ART_CODE
ORDER BY  m.MVT_TIMESTAMP DESC;

COMMENT ON VIEW GSTK.V_HISTORIQUE IS 'Journal mouvements enrichi pour GSTK007';


-- ============================================================
-- SEQUENCES (numérotation automatique des bons)
-- ============================================================

CREATE SEQUENCE GSTK.SEQ_BON_ENTREE
    START WITH 1001
    INCREMENT BY 1
    NO MAXVALUE
    NO CYCLE
    CACHE 10;

CREATE SEQUENCE GSTK.SEQ_BON_SORTIE
    START WITH 2001
    INCREMENT BY 1
    NO MAXVALUE
    NO CYCLE
    CACHE 10;

COMMENT ON SEQUENCE GSTK.SEQ_BON_ENTREE IS 'Numérotation bons BR-XXXXXX';
COMMENT ON SEQUENCE GSTK.SEQ_BON_SORTIE IS 'Numérotation bons BS-XXXXXX';


-- ============================================================
-- TRIGGERS (mise à jour automatique)
-- ============================================================

-- Trigger : MAJ stock ARTICLES après mouvement
CREATE OR REPLACE TRIGGER GSTK.TRG_MAJ_STOCK_ENTREE
AFTER INSERT ON GSTK.LIGNES_ENTREE
FOR EACH ROW
BEGIN
    UPDATE GSTK.ARTICLES
    SET    ART_QTE_STOCK = ART_QTE_STOCK + NEW.LEN_QTE_RECUE,
           ART_PRIX_ACHAT = CASE
               WHEN NEW.LEN_PRIX_ACHAT > 0 THEN NEW.LEN_PRIX_ACHAT
               ELSE ART_PRIX_ACHAT
           END,
           ART_DATE_MAJ   = CURRENT_TIMESTAMP
    WHERE  ART_CODE = NEW.ART_CODE;
END;

CREATE OR REPLACE TRIGGER GSTK.TRG_MAJ_STOCK_SORTIE
AFTER INSERT ON GSTK.LIGNES_SORTIE
FOR EACH ROW
BEGIN
    UPDATE GSTK.ARTICLES
    SET    ART_QTE_STOCK = ART_QTE_STOCK - NEW.LSO_QTE_SORTIE,
           ART_DATE_MAJ  = CURRENT_TIMESTAMP
    WHERE  ART_CODE = NEW.ART_CODE;
END;

-- Trigger : Mise à jour date MAJ articles
CREATE OR REPLACE TRIGGER GSTK.TRG_ART_DATE_MAJ
BEFORE UPDATE ON GSTK.ARTICLES
FOR EACH ROW
BEGIN
    SET NEW.ART_DATE_MAJ = CURRENT_TIMESTAMP;
END;


-- ============================================================
-- DONNÉES DE RÉFÉRENCE (INSERT initiaux)
-- ============================================================

INSERT INTO GSTK.CATEGORIES VALUES
('ELECTRONIQUE', 'Matériel Electronique',    'ELECTRON.', NULL, '606100', 20.00, 'ACTIF', 1),
('BUREAUTIQUE',  'Fournitures Bureautique',  'BUREAUT.',  NULL, '606200', 20.00, 'ACTIF', 2),
('OUTILLAGE',    'Outillage & Matériel',     'OUTILLA.',  NULL, '606300', 20.00, 'ACTIF', 3),
('ALIMENTAIRE',  'Produits Alimentaires',    'ALIMENT.',  NULL, '606400',  5.50, 'ACTIF', 4),
('AUTRE',        'Divers / Non classé',      'AUTRE',     NULL, '606900', 20.00, 'ACTIF', 9);

INSERT INTO GSTK.OPERATEURS (OPE_CODE, OPE_NOM, OPE_PRENOM, OPE_PASSWORD_HASH, OPE_PROFIL, OPE_STATUT) VALUES
('ADMIN01', 'ADMINISTRATEUR', 'SYSTEME', 'e3b0c44298fc1c149afb4c8996fb92427ae41e4649b934ca495991b7852b855', 'ADMIN', 'ACTIF'),
('USER01',  'MARTIN',         'PIERRE',  'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'GESTIONNAIRE', 'ACTIF'),
('USER02',  'DUPONT',         'MARIE',   'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'LECTEUR',      'ACTIF');