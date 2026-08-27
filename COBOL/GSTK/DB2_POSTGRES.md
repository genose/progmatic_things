# DB2 ↔ PostgreSQL — Guide d'adaptation GSTK

Les programmes COBOL GSTK sont écrits pour **IBM DB2 z/OS**.  
Pour les tests locaux, on utilise **PostgreSQL 14** comme substitut.

Ce guide documente les différences et les adaptations appliquées dans `scripts/01_pg_schema.sql`.

---

## Différences syntaxiques SQL

| DB2 z/OS                                      | PostgreSQL 14                                | Notes                         |
|-----------------------------------------------|----------------------------------------------|-------------------------------|
| `GENERATED ALWAYS AS IDENTITY (START WITH 1)` | `DEFAULT nextval('gstk.seq_mvt')`            | Séquence créée séparément     |
| `SELECT NEXT VALUE FOR GSTK.SEQ_MVT FROM SYSIBM.SYSDUMMY1` | `SELECT nextval('gstk.seq_mvt')` | SYSIBM.SYSDUMMY1 n'existe pas |
| `CURRENT DATE`                                | `CURRENT_DATE`                               | Sans espace en PG             |
| `CURRENT TIME`                                | `CURRENT_TIME`                               | Idem                          |
| `CURRENT TIMESTAMP`                           | `CURRENT_TIMESTAMP`                          | Identique                     |
| `DATE(MVT_TIMESTAMP)`                         | `MVT_TIMESTAMP::DATE` ou `DATE(MVT_TIMESTAMP)` | Les deux marchent en PG     |
| `DECIMAL(10,3)`                               | `DECIMAL(10,3)` ou `NUMERIC(10,3)`           | Identique                     |
| `CHAR(n)`                                     | `CHAR(n)`                                    | Identique (padded avec espaces)|
| `VARCHAR(n)`                                  | `VARCHAR(n)`                                 | Identique                     |
| `SMALLINT`                                    | `SMALLINT`                                   | Identique                     |
| `BIGINT`                                      | `BIGINT`                                     | Identique                     |
| `TIMESTAMP`                                   | `TIMESTAMP`                                  | Identique                     |
| `NULLIF(col, 0)`                              | `NULLIF(col, 0)`                             | Identique                     |
| `COALESCE(expr, 0)`                           | `COALESCE(expr, 0)`                          | Identique                     |

---

## Triggers

### DB2 (syntaxe originale)
```sql
CREATE OR REPLACE TRIGGER GSTK.TRG_ART_DATE_MAJ
BEFORE UPDATE ON GSTK.ARTICLES
FOR EACH ROW
BEGIN
    SET NEW.ART_DATE_MAJ = CURRENT_TIMESTAMP;
END;
```

### PostgreSQL (adaptation)
```sql
-- DB2 : trigger inline avec SET NEW.xxx
-- PostgreSQL : nécessite une fonction séparée

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
```

---

## Séquences

### DB2
```sql
-- Déclaration
CREATE SEQUENCE GSTK.SEQ_MVT
    START WITH 1 INCREMENT BY 1 NO MAXVALUE NO CYCLE CACHE 20;

-- Usage dans COBOL (EXEC SQL)
EXEC SQL
    SELECT NEXT VALUE FOR GSTK.SEQ_MVT
    INTO :HV-MVT-ID
    FROM SYSIBM.SYSDUMMY1
END-EXEC.
```

### PostgreSQL
```sql
-- Déclaration (identique sauf syntaxe)
CREATE SEQUENCE gstk.seq_mvt
    START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;

-- Usage équivalent
SELECT nextval('gstk.seq_mvt');

-- Pour les tests unitaires SQL (05_test_sql.sh) :
SELECT nextval('gstk.seq_mvt') AS prochaine_id;
```

**Dans les programmes COBOL sur MVS (DB2 réel) :** la syntaxe `FROM SYSIBM.SYSDUMMY1` reste inchangée.

---

## Nommage — colonne `MVT_ART_CODE`

Le schéma DB2 original (`BASE de donnée.sql`) utilise `ART_CODE` dans `MOUVEMENTS_STOCK` :
```sql
-- Schéma DB2 original
ART_CODE  CHAR(10) NOT NULL,  -- FK → ARTICLES
```

Les programmes COBOL GSTK002 et GSTK003 insèrent dans la colonne `MVT_ART_CODE` :
```cobol
EXEC SQL
    INSERT INTO GSTK.MOUVEMENTS_STOCK
        (MVT_ID, MVT_TYPE, MVT_SENS, MVT_ART_CODE, ...)
    VALUES
        (:HV-MVT-ID, :HV-MVT-TYPE, :HV-MVT-SENS, :HV-ART-CODE, ...)
END-EXEC.
```

**Adaptation PostgreSQL :** la colonne est nommée `mvt_art_code` dans `01_pg_schema.sql`.  
Sur MVS avec DB2 réel : renommer la colonne dans le JCL DB2 BIND, ou adapter les programmes COBOL.

---

## CHECK constraints élargies

### DB2 original
```sql
CONSTRAINT CK_MVT_TYPE CHECK (MVT_TYPE IN
    ('ENTREE','SORTIE','AJUST+','AJUST-','TRANSFERT','INVENTAIRE')),
CONSTRAINT CK_MVT_SENS CHECK (MVT_SENS IN ('+','-')),
```

### Valeurs réellement insérées par les programmes COBOL
- GSTK002 insère `MVT_TYPE = 'BON ENTREE'`, `MVT_SENS = 'E'`
- GSTK003 insère `MVT_TYPE = 'BON SORTIE'`, `MVT_SENS = 'S'`

### Adaptation PostgreSQL (`01_pg_schema.sql`)
```sql
CONSTRAINT ck_mvt_type CHECK (mvt_type IN
    ('BON ENTREE','BON SORTIE','AJUST+','AJUST-',
     'ENTREE','SORTIE','TRANSFERT','INVENTAIRE')),
CONSTRAINT ck_mvt_sens CHECK (mvt_sens IN ('E','S','+','-')),
```

---

## Pagination — compatibilité

Les programmes GSTK utilisent l'approche **FETCH-skip** (compatible DB2 et PostgreSQL) :

```cobol
* Ouvrir le curseur
EXEC SQL OPEN CURS-MVT END-EXEC.
* Sauter (page-1)*10 lignes
PERFORM VARYING W-CNT FROM 1 BY 1 UNTIL W-CNT > W-SKIP OR FIN-FETCH
    EXEC SQL FETCH CURS-MVT INTO :HV-xxx END-EXEC
    IF SQLCODE NOT = 0 MOVE 'N' TO W-FETCH-OK END-IF
END-PERFORM.
* Lire 10 lignes
PERFORM VARYING W-I FROM 1 BY 1 UNTIL W-I > 10 OR FIN-FETCH
    EXEC SQL FETCH CURS-MVT INTO :HV-xxx END-EXEC
    ...
END-PERFORM.
```

Alternative PostgreSQL uniquement (non utilisée dans les programmes car incompatible DB2) :
```sql
SELECT ... FROM ... LIMIT 10 OFFSET 20;
```

---

## Casse — schéma et noms d'objets

| DB2 z/OS                      | PostgreSQL                        |
|-------------------------------|-----------------------------------|
| Insensible à la casse (UPPER) | Insensible si non quoté           |
| `GSTK.ARTICLES`               | `gstk.articles` (converti en bas) |
| Quoter force la casse : `"Articles"` | Idem : `"Articles"` ≠ `articles` |

Dans `01_pg_schema.sql`, tous les objets sont en minuscules non quotés → accès identique en majuscules ou minuscules.

Les programmes COBOL utilisent des majuscules dans leurs SQL (`FROM GSTK.ARTICLES`) : PostgreSQL les convertit automatiquement en minuscules, ce qui fonctionne.

---

## Date de mouvement — comparaison

### DB2
```sql
WHERE DATE(MVT_TIMESTAMP) = CURRENT_DATE
```

### PostgreSQL
```sql
WHERE MVT_TIMESTAMP::DATE = CURRENT_DATE
-- ou :
WHERE DATE(MVT_TIMESTAMP) = CURRENT_DATE   -- aussi valide en PG
```

Les deux syntaxes fonctionnent dans PostgreSQL. La version DB2 est maintenue dans les programmes COBOL.

---

## Adaptation des host variables COBOL

Les types DB2 et PostgreSQL mappent identiquement aux types COBOL :

| Colonne SQL          | Type COBOL ESQL        | Déclaration                        |
|----------------------|------------------------|------------------------------------|
| CHAR(10)             | PIC X(10)              | `05 HV-ART-CODE PIC X(10).`       |
| DECIMAL(10,3)        | PIC S9(7)V999 COMP-3   | `05 HV-QTE PIC S9(7)V999 COMP-3.` |
| DECIMAL(12,4)        | PIC S9(8)V9999 COMP-3  |                                    |
| DECIMAL(14,2)        | PIC S9(12)V99 COMP-3   |                                    |
| BIGINT / INTEGER     | PIC S9(12) COMP-3      |                                    |
| SMALLINT             | PIC S9(4) COMP         |                                    |
| DATE (YYYY-MM-DD)    | PIC X(10)              |                                    |
| TIME (HH:MM:SS)      | PIC X(8)               |                                    |
| VARCHAR(n)           | PIC X(n)               | (longueur max)                     |

---

## Script de test SQL (`05_test_sql.sh`)

Teste les requêtes PostgreSQL qui correspondent aux curseurs DB2 des programmes.  
Un test qui échoue en PostgreSQL = risque de `SQLCODE` non-zéro sur MVS.

```bash
bash scripts/05_test_sql.sh
# Sortie : ✓ / ✗ par programme
# Exit code = nombre de tests échoués
```

---

## Récapitulatif des adaptations appliquées

| # | Adaptation                          | Fichier                  |
|---|-------------------------------------|--------------------------|
| 1 | `GENERATED ALWAYS` → séquence explicite `nextval()` | `01_pg_schema.sql` |
| 2 | `SYSIBM.SYSDUMMY1` → non nécessaire en PG (tests SQL directs) | `05_test_sql.sh` |
| 3 | Triggers `BEGIN...SET NEW` → fonctions PL/pgSQL | `01_pg_schema.sql` |
| 4 | Colonne `MVT_ART_CODE` au lieu de `ART_CODE` | `01_pg_schema.sql` |
| 5 | CHECK MVT_TYPE élargi à 'BON ENTREE'/'BON SORTIE' | `01_pg_schema.sql` |
| 6 | CHECK MVT_SENS élargi à 'E'/'S' | `01_pg_schema.sql` |
| 7 | `CREATE SCHEMA gstk` explicite | `01_pg_schema.sql` |
| 8 | `DROP SCHEMA IF EXISTS gstk CASCADE` pour reset propre | `01_pg_schema.sql` |
| 9 | Index partiels `WHERE col IS NOT NULL` → identique en PG | `01_pg_schema.sql` |
