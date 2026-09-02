# GSTK — Système de Gestion de Stock

**Auteur :** Sebastien Cotillard

Projet CICS/COBOL sur IBM Mainframe (MVS TK5 / z/OS).  
8 programmes pseudo-conversationnels, 8 mapsets BMS, base DB2.

---

## Structure du projet

```
GSTK/
├── Copybook.cbl          Structures partagées (COMMAREA, enregistrements)
├── BASE de donnée.sql    Schéma DB2 original (12 tables + vues + séquences)
├── Ecrans/               Maquettes PDF des écrans (fournis par l'enseignant)
│
├── GSTK000.cbl / GSTK000M.bms   Menu principal
├── GSTK001.cbl / GSTK001M.bms   Consultation stock
├── GSTK002.cbl / GSTK002M.bms   Entrée marchandise
├── GSTK003.cbl / GSTK003M.bms   Sortie marchandise
├── GSTK004.cbl / GSTK004M.bms   Création/Modification article
├── GSTK005.cbl / GSTK005M.bms   Rapports stock par catégorie
├── GSTK006.cbl / GSTK006M.bms   Alertes stock critique
├── GSTK007.cbl / GSTK007M.bms   Historique mouvements
│
└── scripts/              Scripts de test et déploiement (voir SCRIPTS.md)
```

---

## Programmes

| Trans | Programme | Écran                        | Accès DB2        |
|-------|-----------|------------------------------|------------------|
| G000  | GSTK000   | Menu + KPIs globaux          | SELECT agrégats  |
| G001  | GSTK001   | Liste articles + filtres     | CURSOR + FETCH   |
| G002  | GSTK002   | Entrée marchandise           | INSERT + UPDATE  |
| G003  | GSTK003   | Sortie marchandise           | INSERT + UPDATE  |
| G004  | GSTK004   | Fiche article (créer/modif)  | INSERT ou UPDATE |
| G005  | GSTK005   | Rapport par catégorie        | CURSOR GROUP BY  |
| G006  | GSTK006   | Alertes stock critique       | CURSOR WHERE     |
| G007  | GSTK007   | Historique mouvements        | CURSOR + COUNT   |

Navigation PF standard :

| Touche | Action                              |
|--------|-------------------------------------|
| PF3    | Retour menu (XCTL vers GSTK000)     |
| PF5    | Appliquer filtres / Rechercher      |
| PF6    | Valider saisie / Accès entrée stock |
| PF7    | Page précédente                     |
| PF8    | Page suivante                       |
| PF9    | Archiver article (GSTK004)          |
| PF12   | RAZ filtres / Annuler               |

---

## Architecture CICS

### Pseudo-conversational

Chaque programme se termine par :
```cobol
EXEC CICS RETURN
    TRANSID(C-TRANS)
    COMMAREA(GSTK-COMMAREA)
    LENGTH(263)
END-EXEC.
```

Chaque tâche commence par :
```cobol
IF EIBCALEN = ZERO
    PERFORM 1000-PREMIERE-ENTREE
ELSE
    MOVE DFHCOMMAREA TO GSTK-COMMAREA
    PERFORM 2000-RETOUR-TRANSACTION
END-IF
```

### COMMAREA (263 octets)

Définie dans `Copybook.cbl` sous `GSTK-COMMAREA` :

| Champ              | Offset | Longueur | Usage                                    |
|--------------------|--------|----------|------------------------------------------|
| CA-TRAN-RETOUR     | 0      | 8        | Transaction d'origine                    |
| CA-OPERATEUR       | 8      | 10       | Code opérateur connecté                  |
| CA-SESSION-ID      | 18     | 25       | Identifiant session                      |
| CA-DATE-SAISIE     | 43     | 10       | Date courante                            |
| CA-HEURE-SAISIE    | 53     | 8        | Heure courante                           |
| CA-TERMINAL        | 61     | 10       | ID terminal 3270                         |
| CA-PROFIL          | 71     | 15       | Profil opérateur                         |
| CA-MSG-RETOUR      | 86     | 79       | Message à afficher au retour             |
| CA-ART-CODE-SELEC  | 165    | 10       | Article sélectionné (G001→G003/G004)     |
| CA-FILTRE-CAT      | 175    | 15       | Filtre catégorie partagé                 |
| CA-FILTRE-STATUT   | 190    | 10       | Filtre statut partagé                    |
| CA-PAGE-COURANTE   | 200    | 4        | Page courante partagée                   |
| CA-NB-PAGES        | 204    | 4        | Nombre de pages partagé                  |
| FILLER (local)     | 208    | 55       | Usage programme-spécifique (voir ci-bas) |

**Utilisation du FILLER (octets 208+) par programme :**

| Programme | Offset | Contenu                                |
|-----------|--------|----------------------------------------|
| GSTK001   | 208:12 | W-FIL-CODE (filtre code article)       |
| GSTK001   | 220:22 | W-FIL-LIBL (filtre libellé)            |
| GSTK004   | 208:1  | W-MODE ('C'=créer, 'M'=modifier)       |
| GSTK004   | 209:10 | W-ART-CODE (code article en cours)     |
| GSTK006   | 208:12 | W-FIL-CODE (filtre code article)       |
| GSTK006   | 220:17 | W-FIL-CATEG (filtre catégorie)         |
| GSTK007   | 208:12 | W-FIL-CODE                             |
| GSTK007   | 220:10 | W-FIL-DATE-DEB                         |
| GSTK007   | 230:10 | W-FIL-DATE-FIN                         |
| GSTK007   | 240:22 | W-FIL-TYPE                             |

### BMS — Conventions de nommage

Les champs BMS génèrent automatiquement des suffixes :

| Suffixe | Description              | Exemple (champ ARTCOD) |
|---------|--------------------------|------------------------|
| L       | Longueur reçue (input)   | ARTCODL                |
| A       | Attribut (input)         | ARTCODA                |
| I       | Donnée reçue (input)     | ARTCODI                |
| O       | Donnée envoyée (output)  | ARTCODO                |

Attention : le nom du suffixe O est `fieldname + O` — si le champ s'appelle `MNTHTE` (6 chars), l'output est `MNTHTEO` (7 chars), pas `MNTHEO`.

---

## Base de données

### Tables principales

**GSTK.ARTICLES** — Référentiel articles
- `ART_CODE` CHAR(10) PK
- `ART_DESIGNATION`, `ART_CATEGORIE`, `ART_STATUT` ('ACTIF'/'INACTIF'/'ARCHIVE')
- `ART_QTE_STOCK`, `ART_QTE_MIN`, `ART_QTE_MAX` DECIMAL(10,3)
- `ART_PRIX_ACHAT`, `ART_PRIX_VENTE` DECIMAL(12,4)

**GSTK.MOUVEMENTS_STOCK** — Journal audit
- `MVT_ID` séquence via `GSTK.SEQ_MVT`
- `MVT_TYPE` : 'BON ENTREE' / 'BON SORTIE' (valeurs utilisées par les programmes)
- `MVT_SENS` : 'E' (entrée) / 'S' (sortie)
- `MVT_ART_CODE` CHAR(10) FK → ARTICLES

### SQL DB2 — points clés

```cobol
* Curseur statique — OBLIGATOIREMENT en tête de PROCEDURE DIVISION
EXEC SQL
    DECLARE CURS-ART CURSOR FOR
    SELECT ART_CODE, ... FROM GSTK.ARTICLES
    WHERE ART_CODE LIKE :HV-FIL-CODE
    ORDER BY ART_CODE
END-EXEC.

* Séquence ID
EXEC SQL
    SELECT NEXT VALUE FOR GSTK.SEQ_MVT
    INTO :HV-MVT-ID
    FROM SYSIBM.SYSDUMMY1
END-EXEC.

* Commit explicite obligatoire
EXEC CICS SYNCPOINT END-EXEC.
* ou annulation :
EXEC CICS SYNCPOINT ROLLBACK END-EXEC.
```

### Pagination (sans LIMIT/OFFSET)

```cobol
EXEC SQL OPEN CURS-ART END-EXEC.
* Sauter les pages précédentes
PERFORM VARYING W-CNT FROM 1 BY 1 UNTIL W-CNT > W-SKIP OR FIN-FETCH
    EXEC SQL FETCH CURS-ART INTO :HV-xxx END-EXEC
    IF SQLCODE NOT = 0
        MOVE 'N' TO W-FETCH-OK
    END-IF
END-PERFORM.
* Lire 10 lignes
PERFORM VARYING W-I FROM 1 BY 1 UNTIL W-I > 10 OR FIN-FETCH
    EXEC SQL FETCH CURS-ART INTO :HV-xxx END-EXEC
    ...
END-PERFORM.
EXEC SQL CLOSE CURS-ART END-EXEC.
```

---

## Démarrage rapide

### Tests locaux (Mac, sans MVS)

```bash
# 1. PostgreSQL — créer la base de test
bash scripts/03_pg_setup.sh

# 2. Vérifier la syntaxe COBOL (tous les programmes)
bash scripts/04_cobc_check.sh

# 3. Tester toutes les requêtes SQL
bash scripts/05_test_sql.sh
```

### Déploiement sur MVS TK5

```bash
# Prérequis : container mvs-tk5 en cours d'exécution
docker ps | grep mvs-tk5

# Uploader les sources
bash scripts/mvs/01_upload.sh

# Assembler BMS + compiler COBOL
bash scripts/mvs/02_submit.sh bms
bash scripts/mvs/02_submit.sh cobol

# Définir les transactions CICS
bash scripts/mvs/03_cics.sh install

# Tester dans x3270 : taper G000
x3270 localhost:3270
```

### Mode développement (hot-reload)

```bash
# Surveille les fichiers .cbl/.bms → build auto à chaque sauvegarde
bash scripts/mvs/07_watch.sh

# Pipeline CI/CD complet
make ci
```

Voir [SCRIPTS.md](SCRIPTS.md) pour la documentation complète des scripts.
