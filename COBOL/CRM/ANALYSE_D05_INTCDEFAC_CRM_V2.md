# Analyse Technique — Programme COBOL `D05_INTCDEFAC_CRM_V2`

> **Objectif** : reference autonome permettant a un developpeur Java 8 de reecrire ce programme sans consulter le source COBOL original.

**Auteur COBOL original** : B. Caure — cree le 11/08/2003, modifie le 28/10/2011

---

## Table des matieres

1. [Identite du programme](#1-identite-du-programme)
2. [Fichiers d'entree et de sortie](#2-fichiers-dentree-et-de-sortie)
3. [Parametres d'entree](#3-parametres-dentree)
4. [Format de l'enregistrement FIC-CDEFAC](#4-format-de-lenregistrement-fic-cdefac)
5. [Bases de donnees](#5-bases-de-donnees)
6. [Flux d'execution complet](#6-flux-dexecution-complet)
7. [Logique UPSERT sur S.CDE_FAC](#7-logique-upsert-sur-scde_fac)
8. [Traitement des dates](#8-traitement-des-dates)
9. [Traitement special CODLAB 9994](#9-traitement-special-codlab-9994)
10. [Gestion des erreurs SQL](#10-gestion-des-erreurs-sql)
11. [Comportements critiques a preserver en Java](#11-comportements-critiques-a-preserver-en-java)
12. [Architecture Java 8 recommandee](#12-architecture-java-8-recommandee)

---

## 1. Identite du programme

| Attribut | Valeur |
| --- | --- |
| **Nom** | `D05_INTCDEFAC_CRM_V2` |
| **Langage** | RDB/COBOL (SQL Oracle Rdb embarque, OpenVMS) |
| **Auteur** | B. Caure |
| **Creation** | 11 aout 2003 |
| **Derniere modif.** | 28 octobre 2011 |
| **Role fonctionnel** | Integration UPSERT du fichier combine commandes+factures (`FIC-CDEFAC`) dans la base CRM (`BD_CRM.S.CDE_FAC`) |

### Description fonctionnelle

Le programme lit sequentiellement un fichier positionnel `FIC-CDEFAC` contenant des enregistrements qui aggregent donnees commande, donnees de livraison, donnees de facturation et flags de suivi. Pour chaque enregistrement, il effectue un **UPSERT** dans `S.CDE_FAC` (base CRM/EXTRANET) : si la cle `(CODLAB, CODDEP, NUMCDE, NUMRAL)` existe dans CRM, il met a jour les 70+ colonnes ; sinon, il cree la ligne (INSERT avec 80+ colonnes). Il met egalement a jour `D.CDE` (BD_DEPOT) pour le cas particulier `CODLAB='9994'`.

---

## 2. Fichiers d'entree et de sortie

| Nom logique | Nom physique (RMS) | Mode | Description |
| --- | --- | --- | --- |
| `FIC-MESSAGES` | `RMS_MSG` | INPUT, INDEXED | Libelles messages d'erreur (cle `MSG-CLE`) |
| `FIC-ANOMALIES` | `RMS_ANO` | OUTPUT | Enregistrements d'anomalies |
| `FIC-CDEFAC` | `RMS_CDEFAC` | INPUT | Enregistrements combines commande/facture |

### Comportement en cas d'erreur fichier

| Etape | Comportement |
| --- | --- |
| Ouverture FIC-MESSAGES | Erreur → `FIN-ANORMALE` (MSG 1001) → `STOP RUN` |
| Ouverture FIC-ANOMALIES | Erreur → DISPLAY avertissement uniquement, programme continue |
| Ouverture FIC-CDEFAC | Erreur → `FIN-ANORMALE` (MSG 1001) → `STOP RUN` |
| Fermeture FIC-MESSAGES | Erreur → `FIN-ANOFERME` (MSG 1004) → `STOP RUN` |
| Fermeture FIC-CDEFAC | Erreur → `FIN-ANOFERME` (MSG 1004) → `STOP RUN` |

---

## 3. Parametres d'entree

Le seul parametre est lu depuis le copybook `PROC-COM` dans la structure `PARAM-RECU` :

| Champ | Format COBOL | Description |
| --- | --- | --- |
| `P-CODDEP` | `PIC X(2)` | Code depot (ex : "CO", "MO") |

Le parametre est utilise comme filtre implicite dans la logique de traitement mais n'est **pas** passe directement dans les requetes SQL (les enregistrements CDEFAC portent deja leur propre `ENR-CODDEP`).

---

## 4. Format de l'enregistrement FIC-CDEFAC

L'enregistrement `ENR-CDEFAC` est un enregistrement positionnel a longueur fixe regroupant l'ensemble des informations d'une commande (DEPOT) et de sa facturation (CRM).

### Champs principaux

| Champ | Type COBOL | Longueur | Description |
| --- | --- | --- | --- |
| `ENR-CODLAB` | `PIC X(4)` | 4 | Code laboratoire |
| `ENR-CODDEP` | `PIC X(2)` | 2 | Code depot |
| `ENR-SSLABO` | `PIC X(4)` | 4 | Sous-laboratoire |
| `ENR-NUMCDE` | `PIC 9(8)` | 8 | Numero commande |
| `ENR-NUMRAL` | `PIC 9` | 1 | Numero ralliement |
| `ENR-MOYRGL` | `PIC X(2)` | 2 | Mode de reglement |
| `ENR-DELRGL` | `PIC X(3)` | 3 | Delai de reglement |
| `ENR-CODREP` | `PIC X(8)` | 8 | Code representant |
| `ENR-DATCDE` | `PIC X(23)` | 23 | Date commande (format VMS ASCII) |
| `ENR-DATEBL` | `PIC X(23)` | 23 | Date bon de livraison (format VMS ASCII) |
| `ENR-ESCOMP` | `PIC 9(2)V9(4)` | 6 | Escompte |
| `ENR-FRGEST1` | `PIC S9(6)V9(2)` | 8 | Frais de gestion |
| `ENR-FLAGCDE` | `PIC X` | 1 | Flag commande |
| `ENR-FLAGLIV` | `PIC X` | 1 | Flag livraison (`O`/`N`) |
| `ENR-FLAGFAC` | `PIC X` | 1 | Flag facture (`O`/`N`) |
| `ENR-FLAGBLO` | `PIC X` | 1 | Flag bloque (`O`/`N`) |
| `ENR-FLAGSUP` | `PIC X` | 1 | Flag supprime (`O`/`N`) |
| `ENR-MOTIFSUP` | `PIC XXX` | 3 | Motif suppression |
| `ENR-FLAGDIF` | `PIC X` | 1 | Flag differe |
| `ENR-FLAGRAL` | `PIC X` | 1 | Flag ralliement |
| `ENR-FLAGSTD` | `PIC X` | 1 | Flag standard |
| `ENR-FLAGDET` | `PIC X` | 1 | Flag detail |
| `ENR-GENCLI` | `PIC X` | 1 | Genre client |
| `ENR-LIBGENCLI` | `PIC X(40)` | 40 | Libelle genre client |
| `ENR-CLILAB` | `PIC X(10)` | 10 | Code client laboratoire |
| `ENR-FLAGOCC` | `PIC X` | 1 | Flag occasion |
| `ENR-CLICSP` | `PIC 9(8)` | 8 | Code client CSP |
| `ENR-NOMLIV` | `PIC X(40)` | 40 | Nom livraison |
| `ENR-RAISOCL` | `PIC X(40)` | 40 | Raison sociale livraison |
| `ENR-ADR1L` | `PIC X(40)` | 40 | Adresse 1 livraison |
| `ENR-ADR2L` | `PIC X(40)` | 40 | Adresse 2 livraison |
| `ENR-VILLEL` | `PIC X(32)` | 32 | Ville livraison |
| `ENR-DEPARL` | `PIC XX` | 2 | Departement livraison |
| `ENR-CPOSTL` | `PIC X(5)` | 5 | Code postal livraison |
| `ENR-UGA` | `PIC X(3)` | 3 | UGA |
| `ENR-UGA746` | `PIC X(5)` | 5 | UGA 746 |
| `ENR-CLIFAC` | `PIC 9(8)` | 8 | Code client facturation |
| `ENR-NOMFAC` | `PIC X(40)` | 40 | Nom facturation |
| `ENR-RAISOCF` | `PIC X(40)` | 40 | Raison sociale facturation |
| `ENR-ADR1F` | `PIC X(40)` | 40 | Adresse 1 facturation |
| `ENR-ADR2F` | `PIC X(40)` | 40 | Adresse 2 facturation |
| `ENR-VILLEF` | `PIC X(32)` | 32 | Ville facturation |
| `ENR-DEPARF` | `PIC X(2)` | 2 | Departement facturation |
| `ENR-CPOSTF` | `PIC X(5)` | 5 | Code postal facturation |
| `ENR-CLIPAY` | `PIC 9(8)` | 8 | Code client payeur |
| `ENR-NOMPAY` | `PIC X(40)` | 40 | Nom payeur |
| `ENR-RAISPAY` | `PIC X(40)` | 40 | Raison sociale payeur |
| `ENR-CLIGRP` | `PIC 9(8)` | 8 | Code client groupe |
| `ENR-NOMGRP` | `PIC X(40)` | 40 | Nom groupe |
| `ENR-RAISGRP` | `PIC X(40)` | 40 | Raison sociale groupe |
| `ENR-TYPNFA` | `PIC X(2)` | 2 | Type normalisation facture |
| `ENR-QUANTA` | `PIC X` | 1 | Quantite avance |
| `ENR-MOISFA` | `PIC XX` | 2 | Mois facturation |
| `ENR-CPTFAC` | `PIC 9(8)` | 8 | Compteur facture |
| `ENR-DATFAC` | `PIC X(23)` | 23 | Date facture (format VMS ASCII) |
| `ENR-CODREPN` | `PIC X(8)` | 8 | Code representant nouveau |
| `ENR-CODREPR` | `PIC X(8)` | 8 | Code representant remplacant |
| `ENR-REGFAC` | `PIC X` | 1 | Regle facturation |
| `ENR-NOMREP` | `PIC X(40)` | 40 | Nom representant |
| `ENR-NOMREPN` | `PIC X(40)` | 40 | Nom representant nouveau |
| `ENR-NOMREPR` | `PIC X(40)` | 40 | Nom representant remplacant |
| `ENR-FLAGENCOURS` | `PIC X` | 1 | Flag en cours |
| `ENR-QTENCOURS` | `PIC S9(8)` | 8 | Quantite en cours |
| `ENR-STATENCOURS` | `PIC X(3)` | 3 | Statut en cours |
| `ENR-REMISEE` | `PIC 9(6)V9(4)` | 10 | Remise |
| `ENR-MTHT` | `PIC S9(8)V9(2)` | 10 | Montant HT |
| `ENR-DATSAI` | `PIC X(23)` | 23 | Date saisie (format VMS ASCII) |
| `ENR-DATECH` | `PIC X(23)` | 23 | Date echeance (format VMS ASCII) |
| `ENR-CIPPDV` | `PIC 9(9)` | 9 | Code CIPPDV |
| `ENR-GENCLIFAC` | `PIC X` | 1 | Genre client facturation |
| `ENR-LIBGENCLIFAC` | `PIC X(40)` | 40 | Libelle genre client facturation |
| `ENR-CODREPC` | `PIC X(5)` | 5 | Code representant cible |
| `ENR-NOMREPC` | `PIC X(80)` | 80 | Nom representant cible |
| `ENR-DTLIVS` | `PIC X(23)` | 23 | Date livraison souhaitee (format VMS ASCII) |
| `ENR-POIDS` | `PIC 9(7)V9(4)` | 11 | Poids |
| `ENR-CDELAB` | `PIC X(22)` | 22 | Reference commande laboratoire |
| `ENR-REFCDE` | `PIC X(35)` | 35 | Reference commande |
| `ENR-DOSEXP` | `PIC X(10)` | 10 | Dossier expedition |
| `ENR-VOLSTD` | `PIC 9(8)` | 8 | Volume standard |
| `ENR-VOLDTL` | `PIC 9(8)` | 8 | Volume detail |
| `ENR-LIBPAYS` | `PIC X(80)` | 80 | Libelle pays |
| `ENR-PROMOS` | `PIC X(4)` | 4 | Promotions |
| `ENR-NBCOLIS` | `PIC 9(6)` | 6 | Nombre de colis |
| `ENR-NBPAL` | `PIC 9(6)` | 6 | Nombre de palettes |
| `ENR-DTLIVR` | `PIC X(23)` | 23 | Date livraison reelle (format VMS ASCII) |
| `ENR-FLAGEXP` | `PIC X` | 1 | Flag expedition |

---

## 5. Bases de donnees

| Alias | Base | Tables utilisees |
| --- | --- | --- |
| `S` | `BD_CRM` | `S.CDE_FAC` (lecture et UPSERT) |
| `D` | `BD_DEPOT` | `D.CDE` (mise a jour conditionnelle CODLAB 9994) |

`BD_STATS` est declaree mais commentee dans le source — non utilisee.

Transaction : `SET TRANSACTION READ WRITE` au debut du programme.

---

## 6. Flux d'execution complet

```
TRAITEMENT-PRINCIPAL
  SET TRANSACTION READ WRITE
  LIB$DATE_TIME → W-D-DATE (date systeme ASCII)
  SYS$BINTIM(W-D-DATE) → W-D-DATE-BIN (date systeme en binaire)
  OUVERTURE-FICHIERS
  LOOP: LECTURE-FICCDEFAC UNTIL FINFIC
    READ FIC-CDEFAC
      AT END: FLAG-FINFIC='O' → EXIT loop
    SI ENR-NUMCDE = 0 → GO TO LECTURE-FICCDEFAC (skip)
    verif-statut-final → SELECT S.CDE_FAC → ws-statencours, ws-cptfac
    SI statut terminal (FAT/GLT/FAP/GLP):
      SI CODLAB='3010' AND cptfac>20000 AND ws-cptfac=0 → continuer
      SINON → GO TO LECTURE-FICCDEFAC (skip)
    SI CODLAB='9994':
      SI FLAGLIV='O' → UPDATE-CDE-LIV (D.CDE SET DTLIVR)
      SI FLAGFAC='O' → UPDATE-CDE-FAC (D.CDE SET DATEBL, STATUT='FAT')
      SI FLAGBLO='O' → UPDATE-CDE-BLO (D.CDE SET STATUT='BLO')
      SI FLAGSUP='O' AND FLAGFAC='N' → UPDATE-CDE-SUP (D.CDE SET STATUT='SEI')
    TRAITEMENT-DATES (6 dates VMS → binaire)
    RECHERCHE-CDEFAC
      EXTRACTION-DATE (mois, annee, trimestre pour 3 dates)
      SI FLAG-ACREER → CREATION-CDEFAC (INSERT S.CDE_FAC)
      SINON          → UPDATE-CDEFAC   (UPDATE S.CDE_FAC)
    COMMIT
  END-LOOP
  COMMIT final
  FERMETURE-FICHIERS
  STOP RUN
```

---

## 7. Logique UPSERT sur S.CDE_FAC

### verif-statut-final — determination du mode

```sql
SELECT statencours, cptfac
INTO :Ws-statencours, :ws-cptfac
FROM S.CDE_FAC
WHERE CODDEP = :ENR-CODDEP
  AND NUMCDE = :ENR-NUMCDE
  AND NUMRAL = :ENR-NUMRAL
  AND codlab = :ENR-codlab
LIMIT TO 1 ROW
```

- `SQLSTATE = '02000'` (NOT FOUND) → `FLAG-ACREER = 'O'` — la ligne sera creee
- `SQLSTATE` OK → `FLAG-TROUVE = 'O'` — la ligne sera mise a jour

### Filtre statut terminal

Si `ws-statencours IN ('FAT', 'GLT', 'FAP', 'GLP')` :
- **Cas general** : skip de l'enregistrement (`GO TO LECTURE-FICCDEFAC`)
- **Exception CODLAB 3010** : si `ENR-CODLAB='3010' AND ENR-CPTFAC > 20000 AND ws-cptfac = 0` → traitement continue malgre le statut terminal

### UPDATE-CDEFAC

`UPDATE S.CDE_FAC SET` 70+ colonnes `WHERE CODLAB=:ENR-CODLAB AND CODDEP=:ENR-CODDEP AND NUMCDE=:ENR-NUMCDE AND NUMRAL=:ENR-NUMRAL`

**Note** : les champs `FLAGSTD` et `FLAGDET` sont presents dans l'INSERT mais **absents** de l'UPDATE. Comportement a preserver en Java.

### CREATION-CDEFAC

`INSERT INTO S.CDE_FAC` 80+ colonnes avec les memes donnees.

Colonnes supplementaires dans l'INSERT uniquement : `FLAGSTD`, `FLAGDET`, `STATENCOURS` (depuis ENR-STATENCOURS), `MONTBRUTCDE`, `MONTREM1CDE`, `MONTREM2CDE`, `MONTREM3CDE`, `MTREMICDE` (tous initialement a zero via INITIALIZE).

---

## 8. Traitement des dates

### Conversion VMS → binaire

Toutes les dates du fichier CDEFAC sont au format VMS ASCII `"DD-MON-YYYY HH:MM:SS.CC"`. Le programme les convertit en representation binaire via `SYS$BINTIM` avant chaque insertion ou mise a jour.

Les 6 dates traitees :

| Champ source | Variable binaire | Colonnes SQL |
| --- | --- | --- |
| `ENR-DATCDE` | `W-DATCDE-BIN` | `DATCDE` |
| `ENR-DATEBL` | `W-DATEBL-BIN` | `DATEBL` |
| `ENR-DATSAI` | `W-DATSAI-BIN` | `DATSAI` |
| `ENR-DATFAC` | `W-DATFAC-BIN` | `DATFAC` |
| `ENR-DATECH` | `W-DATECH-BIN` | `DATECH` |
| `ENR-DTLIVS` | `W-DTLIVS-BIN` | `DTLIVS` |
| `ENR-DTLIVR` | `W-DTLIVR-BIN` | `DTLIVR` |

### Dates sentinelles (valeur nulle Oracle Rdb)

```
SI ENR-DATFAC = ESPACES → MOVE '17-NOV-1858' TO ENR-DATFAC
SI ENR-DATECH = ESPACES → MOVE '17-NOV-1858' TO ENR-DATECH
```

`17-NOV-1858` est la date epoch zero d'Oracle Rdb (equivalent de `NULL` pour les dates stockees en binaire). En Java : `Timestamp.valueOf("1858-11-17 00:00:00")`.

### Extraction mois/annee/trimestre

Pour chaque enregistrement, le programme extrait de 3 dates (CDE, BL, FAC) :
- **Annee** : `ENR-DATCDE(8:4)` → `W-ANNEECDE`
- **Mois** : correspondance des abreviations anglaises (`JAN`→`01`, ..., `DEC`→`12`)
- **Trimestre** : `1` si mois < 04 ; `2` si < 07 ; `3` si < 10 ; `4` sinon

Ces valeurs alimentent les colonnes `ANNEECDE`, `MOISCDE`, `TRIMESTRECDE`, `ANNEEBL`, `MOISBL`, `TRIMESTREBL`, `ANNEEFAC`, `MOISFAC`, `TRIMESTREFAC` dans `S.CDE_FAC`.

---

## 9. Traitement special CODLAB 9994

Avant l'UPSERT principal, si `ENR-CODLAB = '9994'`, le programme effectue des mises a jour conditionnelles sur `D.CDE` (BD_DEPOT) :

| Condition | SQL |
| --- | --- |
| `ENR-FLAGLIV = 'O'` | `UPDATE D.CDE SET DTLIVR=:W-DTLIVR-BIN WHERE NUMCDE=X AND NUMRAL=X AND CODLAB='9994'` |
| `ENR-FLAGFAC = 'O'` | `UPDATE D.CDE SET DATEBL=:W-DATEBL-BIN, STATUT='FAT' WHERE NUMCDE=X AND NUMRAL=X AND CODLAB='9994'` |
| `ENR-FLAGBLO = 'O'` | `UPDATE D.CDE SET STATUT='BLO' WHERE NUMCDE=X AND NUMRAL=X AND CODLAB='9994'` |
| `ENR-FLAGSUP = 'O' AND FLAGFAC = 'N'` | `UPDATE D.CDE SET STATUT='SEI' WHERE NUMCDE=X AND NUMRAL=X AND CODLAB='9994'` |

**Note** : ces updates sur D.CDE sont effectues avec `CODLAB='9994'` **hardcode**, independamment de la valeur reelle de `ENR-CODLAB`.

---

## 10. Gestion des erreurs SQL

```
GESTION-SQLSTATE :
  SQLSTATE = '02000'              → FLAG-TROUVE = 'N' (NOT FOUND)
  SQLSTATE = '22002'              → FLAG-TROUVE = 'O' (NULL indicator)
  SQLSTATE dans '00000'-'01999'  → FLAG-TROUVE = 'O' (succes)
  SQLSTATE dans '02001'-'22001'  → FIN-ANORMALE
  SQLSTATE dans '22003'-'S9999'  → FIN-ANORMALE
```

**FIN-ANORMALE** :
1. DISPLAY SQLSTATE
2. Si `NUM-MSG > "2000"` → WRITE ANO-ENREG directement
3. Sinon → appel `D00_MSGGES` (gestionnaire messages)
4. Appel `SQL$GET_ERROR_TEXT` pour obtenir le texte d'erreur Oracle Rdb (jusqu'a 300 chars, decoupes en tranches de 80 et 60)
5. WRITE ANO-ENREG (message d'erreur)
6. FERMETURE-FICHIERS
7. `ROLLBACK`
8. `STOP RUN`

---

## 11. Comportements critiques a preserver en Java

| Code | Regle |
| --- | --- |
| B-D05I-01 | `ENR-NUMCDE = 0` → enregistrement **ignore** sans erreur |
| B-D05I-02 | Statut terminal (`FAT`/`GLT`/`FAP`/`GLP`) → skip, **sauf** si `CODLAB='3010' AND CPTFAC>20000 AND ws-cptfac=0` |
| B-D05I-03 | `CODLAB='9994'` → updates conditionnels sur `D.CDE` avant l'UPSERT principal |
| B-D05I-04 | `COMMIT` apres **chaque enregistrement** (pas un commit global) |
| B-D05I-05 | `DATFAC` ou `DATECH` vide → substituer `'17-NOV-1858'` (`Timestamp 1858-11-17`) avant SQL |
| B-D05I-06 | Mode UPSERT : `SELECT` → si absent (`SQLSTATE 02000`) → `INSERT`, sinon → `UPDATE` |
| B-D05I-07 | Trimestre : `1` si mois < 04 ; `2` si < 07 ; `3` si < 10 ; `4` sinon |
| B-D05I-08 | Erreur SQL → `ROLLBACK` + arret immediat (`STOP RUN`) |
| B-D05I-09 | `FLAG-DEBUG='O'` fixe dans le source → l'UPDATE est **toujours execute** (pas de mode dry-run) |
| B-D05I-10 | `FLAGSTD` et `FLAGDET` : inseres a la creation, **non mis a jour** lors de l'UPDATE |
| B-D05I-11 | `MONTBRUTCDE`, `MONTREM1CDE`, `MONTREM2CDE`, `MONTREM3CDE`, `MTREMICDE` : inseres a la creation avec valeur **zero** (pas de champ source dans ENR-CDEFAC) |

---

## 12. Architecture Java 8 recommandee

```
crm-java/src/main/java/com/example/crm/d05intcde/
├── domain/
│   ├── CdeFacRecord.java        Enregistrement FIC-CDEFAC (tous les champs)
│   ├── CdeFacKey.java           Cle (codlab, coddep, numcde, numral)
│   └── DateExtracts.java        Annee/mois/trimestre pour les 3 dates CDE/BL/FAC
├── port/
│   ├── CdeFacRepository.java    Interface : upsert(record), selectStatut(key)
│   └── BdDepotCdePort.java      Interface : updateCdeLiv/updateCdeFac/updateCdeBlo/updateCdeSup
├── application/
│   ├── IntCdeFacService.java    Orchestration principale (boucle + commit)
│   ├── DateExtractor.java       Extraction mois/annee/trimestre depuis VMS ASCII
│   └── StatutFilter.java        Regle B-D05I-02 (statut terminal)
├── adapter/
│   ├── file/
│   │   └── CdeFacFileReader.java   Lecture sequentielle RMS_CDEFAC
│   └── jdbc/
│       ├── RdbCdeFacCrmRepo.java   JDBC S.CDE_FAC (SELECT, INSERT, UPDATE)
│       └── RdbCdeBdDepotRepo.java  JDBC D.CDE (UPDATE LIV/FAC/BLO/SUP)
└── main/
    └── D05IntcdefacMain.java    Point d'entree batch
```

### Tests TDD prioritaires

| Comportement | Classe de test |
| --- | --- |
| `NUMCDE=0` → skip | `IntCdeFacServiceTest.numcde_zero_estIgnore` |
| Statut terminal → skip | `StatutFilterTest.statut_fat_estTerminal` |
| Exception CODLAB 3010 | `StatutFilterTest.codlab3010_cptfacElevee_passeMalgre_statutTerminal` |
| CODLAB 9994 → updates D.CDE | `IntCdeFacServiceTest.codlab9994_declenche_updates_bdDepot` |
| FLAGSUP+FLAGFAC='N' requis pour SUP | `IntCdeFacServiceTest.update_sei_seulementSi_flagsup_et_flagfacN` |
| Commit par enregistrement | `IntCdeFacServiceTest.commit_apres_chaque_enregistrement` |
| DATFAC vide → 1858-11-17 | `DateExtractorTest.datfac_vide_substitue_sentinelle_1858` |
| Trimestre Q1..Q4 | `DateExtractorTest.trimestre_calculE_correctement` |
| INSERT si NOT FOUND | `IntCdeFacServiceTest.insertion_si_absent_de_crm` |
| UPDATE si FOUND | `IntCdeFacServiceTest.update_si_present_en_crm` |
| FLAGSTD non mis a jour en UPDATE | `IntCdeFacServiceTest.flagstd_absent_de_update_presente_dans_insert` |
| Erreur SQL → ROLLBACK + stop | `IntCdeFacServiceTest.erreur_sql_declenche_rollback_et_stop` |
