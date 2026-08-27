# Analyse technique — D05_VERIF_CRM.SCO

> Document de référence pour la migration Java 8 TDD. Exhaustif — permet de réécrire le programme sans consulter le COBOL original.

---

## Table des matières

1. [Identité du programme](#1-identité-du-programme)
2. [Fichiers d'entrée et de sortie](#2-fichiers-dentrée-et-de-sortie)
3. [Paramètres d'entrée](#3-paramètres-dentrée)
4. [Variables de travail clés](#4-variables-de-travail-clés)
5. [Base de données et SQL](#5-base-de-données-et-sql)
6. [Appels externes VMS et équivalents Java](#6-appels-externes-vms-et-équivalents-java)
7. [Gestion SQLSTATE](#7-gestion-sqlstate)
8. [Flux d'exécution complet](#8-flux-dexécution-complet)
9. [Conversion de date TRANSFO-DATEBL](#9-conversion-de-date-transfo-datebl)
10. [Comportements critiques à préserver](#10-comportements-critiques-à-préserver)
11. [Architecture Java 8 cible](#11-architecture-java-8-cible)
12. [Stratégie TDD](#12-stratégie-tdd)
13. [Matrice des tests](#13-matrice-des-tests)
14. [Définition of Done](#14-définition-of-done)
15. [Ordre de réalisation recommandé](#15-ordre-de-réalisation-recommandé)

---

## 1. Identité du programme

| Champ | Valeur |
|---|---|
| Nom du programme | `D05_VERIF_CRM` |
| Fichier source | `D05_VERIF_CRM.SCO` (525 lignes) |
| Auteur | C. Rodier |
| Date de création | 19/10/2016 |
| Version | 1.000 |
| Historique | V2.000-3580 AP:EM — LIB:CREATION DU PROGRAMME |
| Fonctionnalité | Vérif cde DEPOT et CRM en phase pour 1 date |
| Environnement | OpenVMS / COBOL / Oracle Rdb SQL embarqué |

**Rôle** : programme batch qui vérifie que les commandes présentes dans `BD_DEPOT` pour une date de bon de livraison (`DATEBL`) donnée sont synchronisées avec leur équivalent dans `BD_CRM`. Il opère en deux phases dans deux transactions séparées :

1. **Phase READ ONLY** : scan curseur CURCDE sur BD_DEPOT, comparaison des statuts avec BD_CRM, accumulation des écarts dans un tableau interne (max 9 000 entrées).
2. **Phase READ WRITE** (optionnelle, si `P-MAJ='O'` et des écarts existent) : relecture du tableau, mise à jour des commandes en écart dans BD_DEPOT.

---

## 2. Fichiers d'entrée et de sortie

| Nom logique | Nom RMS | Mode | Rôle |
|---|---|---|---|
| `FIC-ANOMALIES` | `RMS_ANO` | OUTPUT | Anomalies écrites en cas d'erreur SQL fatale (FIN-ANORMALE) |

Pas de fichier d'entrée RMS : les données sont lues via curseur SQL directement en base.

---

## 3. Paramètres d'entrée

Le programme reçoit ses paramètres via la zone logique VMS (COPY WORK_COM / PROC_COM) dans la structure suivante :

```cobol
01 PARAM.
   02 P-CODDEP  PIC XX.
   02 P-DATEBL.
      03 P-AA   PIC 9999.
      03 P-MM   PIC 99.
      03 P-JJ   PIC 99.
   02 P-MAJ     PIC X.
```

| Paramètre | PIC | Description |
|---|---|---|
| `P-CODDEP` | `PIC XX` | Code dépôt (2 caractères) — utilisé comme clé dans la recherche CRM |
| `P-DATEBL` | `PIC 9(8)` total | Date du bon de livraison, format `YYYYMMDD` (P-AA + P-MM + P-JJ) |
| `P-MAJ` | `PIC X` | `'O'` = effectuer les mises à jour, toute autre valeur = lecture seule |

---

## 4. Variables de travail clés

### 4.1 Flags

| Variable COBOL | PIC | Valeur initiale | Valeur 88 | Rôle |
|---|---|---|---|---|
| `FLAG-TROUVE` | `PIC X` | `'N'` | `TROUVE VALUE "O"` | Résultat de GESTION-SQLSTATE : ligne trouvée ou succès SQL |
| `FLAG-FINCDE` | `PIC X` | `'N'` | `FINCDE VALUE "O"` | Fin du curseur CURCDE (EOF) |
| `FLAG-DEADLOCK` | `PIC X` | `'N'` | `DEADLOCK VALUE "O"` | Deadlock détecté sur Rdb |
| `FLAG-REJET` | `PIC X` | `'N'` | `REJET VALUE "O"` | Mis à `'O'` en cas de deadlock (sortie TRT-CDE) |
| `FLAG-REJET-GLO` | `PIC X` | `'N'` | `REJET-GLO VALUE "O"` | Rejet global (non utilisé dans flux principal) |

### 4.2 Dates

| Variable COBOL | PIC | Rôle |
|---|---|---|
| `W-D-DATE1` | groupe | Conteneur date/heure système |
| `W-D-JMA1` | `PIC X(12)` | Partie date ASCII (JMA) |
| `W-D-HEURE1` | `PIC X(11)` | Partie heure ASCII |
| `W-DATSYS` | `PIC S9(11)V9(7) COMP` | Date système en binaire VMS |
| `ED-HEURE` | `PIC X(11)` | Heure courante pour affichage |
| `W-DATE-ASCII` | `PIC X(23)` | Date au format VMS ASCII `DD-MON-YYYY HH:MM:SS.CC` |
| `W-DATE-RDB` | `PIC S9(11)V9(7) COMP` | Date binaire VMS intermédiaire |
| `W-DATE-ZERO` | `PIC S9(11)V9(7) COMP` | Epoch VMS (17-NOV-1858 = zéro) |
| `W-DATDEB` | `PIC S9(11)V9(7) COMP` | Date début binaire VMS (intermédiaire TRANSFO-DATEBL) |
| `W-DATEBL` | `PIC S9(11)V9(7) COMP` | Date BL binaire VMS (minuit), passée au curseur CURCDE |
| `W-YYYYMMDD` | `PIC X(8)` | Date format `YYYYMMDD` |
| `W-MOIS-UK` | `PIC XXX` | Mois en anglais (JAN, FEB, ...) après conversion |
| `W-MOIS-FR` | `PIC XXX` | Mois numérique source (01, 02, ...) avant conversion |

### 4.3 Tableau d'accumulation

```cobol
01 tab-cde.
   02 t-occ       OCCURS 9000.
      03 t-NUMCDE  PIC 9(9).
      03 t-NUMRAL  PIC 9.
      03 t-statut  PIC XXX.
      03 t-codlab  PIC X(4).
01 cpt-cde              PIC 9(4).
```

Capacité maximale : **9 000 entrées**. Dépassement → `DISPLAY "table trop petite"` puis poursuite.
`cpt-cde` sert d'index d'écriture en phase 1 et d'index de lecture en phase 2.

### 4.4 Zone de travail courante

```cobol
01 ws-cde.
   03 w-NUMCDE  PIC 9(9).
   03 w-NUMRAL  PIC 9.
   03 w-statut  PIC XXX.
   03 w-codlab  PIC X(4).
01 w-statencours PIC XXX.
01 w-statencours PIC XXX.
```

### 4.5 Compteurs et constantes Rdb

| Variable COBOL | PIC | Rôle |
|---|---|---|
| `W-CPT` | `PIC 9(6)` | Compteur (utilisé dans le code mort GET-CDE-CRM-CPT) |
| `RDB$_DEADLOCK` | `PIC S9(9) COMP EXTERNAL` | Code erreur Rdb deadlock |
| `RDB$_LOCK_CONFLICT` | `PIC S9(9) COMP EXTERNAL` | Code erreur Rdb lock conflict |
| `RDB$_INTEG_FAIL` | `PIC S9(9) COMP EXTERNAL` | Code erreur Rdb intégrité |
| `EXCEPT` | `PIC S9(9) COMP` | Exception courante |
| `CODE-STATUS EXTERNAL` | `PIC XX` | Code status externe |
| `SQLSTATE` | `PIC X(5)` | Code SQLSTATE standard |

---

## 5. Base de données et SQL

### 5.1 Bases déclarées

| Alias SQL | Base logique | Table principale utilisée |
|---|---|---|
| `D` | `BD_DEPOT` | `D.CDE` |
| `S` | `BD_CRM` | `S.CDE_FAC` |

### 5.2 Curseur CURCDE

```sql
DECLARE CURCDE CURSOR FOR
  SELECT numcde, numral, statut, codlab
    FROM D.CDE
   WHERE cast(cast(datebl AS DATE ANSI) AS DATE VMS) = :W-DATEBL
     AND FLAG_CRM = 'N'
     AND STATUT <> 'THO'
     AND STATUT <> 'THO'
     AND STATUT <> 'SIX'
     AND STATUT <> 'EIX'
```

**Remarque** : la clause `STATUT <> 'THO'` apparaît **deux fois** dans le source original (bug originel). Comportement identique à une seule clause. Filtre effectif = `NOT IN ('THO', 'SIX', 'EIX')`.

### 5.3 Requête GET-CDE-CRM

```sql
SELECT STATENCOURS INTO :W-STATENCOURS
  FROM S.CDE_FAC
 WHERE CODDEP  = :P-CODDEP
   AND CODLAB  = :W-CODLAB
   AND NUMCDE  = :W-NUMCDE
   AND NUMRAL  = :W-NUMRAL
 LIMIT TO 1 ROW
```

Clé de recherche : `(P-CODDEP, W-CODLAB, W-NUMCDE, W-NUMRAL)`. `P-CODDEP` est le paramètre global, pas un champ de la ligne courante.

### 5.4 Requête UPDATE TRT-TABCDE

```sql
UPDATE D.CDE
   SET STATCRM = '', FLAG_CRM = 'O'
 WHERE CODLAB  = :W-CODLAB
   AND NUMCDE  = :W-NUMCDE
   AND NUMRAL  = :W-NUMRAL
```

**Attention** : CODDEP est absent de la clause WHERE (comportement original — B-D05-04).

### 5.5 Code mort : GET-CDE-CRM-CPT

```sql
SELECT COUNT(*) INTO :W-CPT
  FROM S.CDE_FAC
 WHERE CODDEP  = :P-CODDEP
   AND CODLAB  = :W-CODLAB
   AND NUMCDE  = :W-NUMCDE
   AND NUMRAL  = :W-NUMRAL
```

Ce paragraphe est déclaré mais l'appel `PERFORM GET-CDE-CRM-CPT` est **commenté** dans TRT-CDE. Ne pas implémenter en Java.

### 5.6 Gestion des transactions

| Phase | Transaction SQL | Commit/Rollback |
|---|---|---|
| Phase 1 (scan) | `SET TRANSACTION READ ONLY` | `ROLLBACK` si `P-MAJ='O'` ET `cpt-cde > 0`, sinon transaction close implicitement à STOP RUN |
| Phase 2 (MAJ) | `SET TRANSACTION READ WRITE RESERVING D.CDE FOR SHARED WRITE` | `COMMIT` unique après **tous** les UPDATEs |

---

## 6. Appels externes VMS et équivalents Java

### 6.1 Format date ASCII VMS

Le format standard VMS ASCII pour les dates est : `DD-MON-YYYY HH:MM:SS.CC` (23 caractères).
- `DD` : jour sur 2 chiffres
- `MON` : mois en anglais 3 lettres majuscules (JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC)
- `YYYY` : année sur 4 chiffres
- `HH:MM:SS.CC` : heure, minute, seconde, centièmes

Exemple : `"08-NOV-2024 00:00:00.00"`

Le binaire VMS est un entier 64 bits (PIC S9(11)V9(7) COMP) comptant les intervalles de 100 nanosecondes depuis l'epoch VMS : **17 novembre 1858 à minuit**.

### 6.2 Tableau des appels

| Appel VMS | Signature COBOL | Rôle VMS | Équivalent Java 8 |
|---|---|---|---|
| `LIB$DATE_TIME` | `CALL "LIB$DATE_TIME" USING BY DESCRIPTOR W-D-DATSYS` | Retourne la date/heure courante en format ASCII VMS (23 chars) dans le descripteur | `LocalDateTime.now()` formaté avec `DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss.SS", Locale.ENGLISH).toUpperCase()` |
| `D00_DATEDI` | `CALL "D00_DATEDI" USING W-D-DATSYS W-D-DATE` | Sous-programme maison : formate la date système pour affichage | Fonction utilitaire interne, non à réimplémenter |
| `SYS$BINTIM` | `CALL "SYS$BINTIM" USING BY DESCRIPTOR W-DATE-ASCII BY REFERENCE W-DATE-ZERO` | Convertit une date ASCII VMS en valeur binaire 64 bits (100 ns depuis epoch 17-NOV-1858) | Parser avec `DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss.SS", Locale.ENGLISH)` → `LocalDateTime` ou `Instant` ; en test, utiliser `LocalDate.parse()` |
| `SYS$PUTMSG` | `CALL "SYS$PUTMSG" USING RDB$MESSAGE_VECTOR` | Affiche un message d'erreur VMS depuis le vecteur de messages Rdb | `System.err.println(...)` ou logger |
| `SQL$GET_ERROR_TEXT` | `CALL "SQL$GET_ERROR_TEXT" USING BY DESCRIPTOR GET-ERROR-BUFFER BY REFERENCE GET-MSG-LEN` | Récupère le texte d'erreur Rdb (jusqu'à 300 chars, 4 fragments de 80/80/80/60) | `SQLException.getMessage()` |
| `D00_MSGGES` | `CALL "D00_MSGGES" USING ZONES-PARAMETRES` | Gestionnaire de messages d'erreur maison (appelé si NUM-MSG ≤ 2000) | Logger d'erreur applicatif |

**Note** : `D05` n'utilise **pas** `LIB$SET_LOGICAL` (contrairement à `T10`). En cas de FIN-ANORMALE, le programme termine sans positionner de logique VMS d'erreur.

---

## 7. Gestion SQLSTATE

GESTION-SQLSTATE est appelé après chaque opération SQL. Voici la table complète telle qu'elle apparaît dans le source :

```cobol
MOVE 'N' TO FLAG-TROUVE.
MOVE 'N' TO FLAG-DEADLOCK.
EVALUATE SQLSTATE
  WHEN '02000'              MOVE 'N' TO FLAG-TROUVE
  WHEN '22002'              MOVE 'O' TO FLAG-TROUVE
  WHEN '00000' THRU '01999' MOVE 'O' TO FLAG-TROUVE
  WHEN '02001' THRU '22001' PERFORM FIN-ANORMALE
  WHEN '22003' THRU 'R1000' PERFORM FIN-ANORMALE
  WHEN 'R1001'              MOVE 'O' TO FLAG-DEADLOCK
  WHEN 'R1002'              MOVE 'O' TO FLAG-DEADLOCK
  WHEN 'R1003' THRU 'S9999' PERFORM FIN-ANORMALE
END-EVALUATE.
IF DEADLOCK
   DISPLAY "DEADLOCK " NUM-MSG " " ANO_MESSAGE
   MOVE 'O' TO FLAG-REJET
END-IF.
```

| Plage SQLSTATE | Action | Notes |
|---|---|---|
| `'02000'` | `FLAG-TROUVE='N'` | Not found — normal, pas d'erreur |
| `'22002'` | `FLAG-TROUVE='O'` | Indicateur null — traité comme trouvé |
| `'00000'` à `'01999'` | `FLAG-TROUVE='O'` | Succès et avertissements SQL |
| `'02001'` à `'22001'` | `FIN-ANORMALE` | Erreurs SQL fatales |
| `'22003'` à `'R1000'` | `FIN-ANORMALE` | Erreurs SQL fatales |
| `'R1001'` | `FLAG-DEADLOCK='O'` | Deadlock Rdb |
| `'R1002'` | `FLAG-DEADLOCK='O'` | Lock conflict Rdb |
| `'R1003'` à `'S9999'` | `FIN-ANORMALE` | Erreurs SQL fatales |

**Comportement DEADLOCK spécifique à D05** : après le `EVALUATE`, si `DEADLOCK` est vrai, le programme positionne `FLAG-REJET='O'` mais **ne fait pas de ROLLBACK ni de FIN-ANORMALE**. La boucle `TRT-CDE` s'arrête (condition `OR DEADLOCK`). Le programme continue vers la phase MAJ éventuelle.

**FIN-ANORMALE** :
1. `CALL SYS$PUTMSG` (affichage message VMS)
2. `DISPLAY "FIN ANORMALE"` + `DISPLAY "SQLSTATE " SQLSTATE`
3. `WRITE ANO-ENREG` dans FIC-ANOMALIES
4. `CALL SQL$GET_ERROR_TEXT` → affichage texte erreur Rdb
5. `CLOSE-FILES`
6. `ROLLBACK`
7. `STOP RUN`

---

## 8. Flux d'exécution complet

```
TRAITEMENT-PRINCIPAL
│
├── TRANSFO-DATEBL
│   └── Convertit P-DATEBL (YYYYMMDD) → W-DATEBL (binaire VMS, minuit)
│       (détail section 9)
│
├── OPEN-FILES
│   └── OPEN OUTPUT FIC-ANOMALIES (RMS_ANO)
│
├── EXEC SQL SET TRANSACTION READ ONLY
│
├── INITIALIZE tab-cde
├── MOVE 0 TO cpt-cde
│
├── OPEN CURCDE
│
├── PERFORM TRT-CDE UNTIL FINCDE OR DEADLOCK
│   │
│   └── [TRT-CDE]
│       ├── FETCH CURCDE INTO w-NUMCDE, w-NUMRAL, w-STATUT, w-CODLAB
│       ├── GESTION-SQLSTATE
│       │   ├── Si TROUVE (= ligne fetched) → GET-CDE-CRM
│       │   │   ├── SELECT STATENCOURS FROM S.CDE_FAC ...
│       │   │   ├── GESTION-SQLSTATE
│       │   │   ├── Si TROUVE ET statuts équivalents → DISPLAY "OK" (rien dans tableau)
│       │   │   ├── Si TROUVE ET statuts différents  → CHARGE-TABLEAU
│       │   │   └── Si NOT TROUVE (absent CRM)        → CHARGE-TABLEAU (NOK)
│       │   └── Si NOT TROUVE (EOF cursor) → MOVE 'O' TO FLAG-FINCDE
│       └── [CHARGE-TABLEAU]
│           ├── Si cpt-cde < 9000 : t-occ(cpt-cde+1) ← ws-cde ; cpt-cde + 1
│           └── Sinon             : DISPLAY "table trop petite" (continue)
│
├── CLOSE CURCDE
│
├── DISPLAY "Nb commande a maj " cpt-cde
│
├── IF P-MAJ = 'O'
│   │
│   ├── IF cpt-cde > 0
│   │   ├── EXEC SQL ROLLBACK            ← fin transaction READ ONLY
│   │   └── EXEC SQL SET TRANSACTION READ WRITE RESERVING D.CDE FOR SHARED WRITE
│   │
│   ├── PERFORM VARYING cpt-cde FROM 1 BY 1
│   │   UNTIL cpt-cde > 9000 OR t-codlab(cpt-cde) = SPACES
│   │   └── [DECHARGE-TABLEAU → ws-cde]
│   │       └── [TRT-TABCDE]
│   │           ├── UPDATE D.CDE SET STATCRM='', FLAG_CRM='O'
│   │           │   WHERE CODLAB=w-CODLAB AND NUMCDE=w-NUMCDE AND NUMRAL=w-NUMRAL
│   │           └── GESTION-SQLSTATE
│   │
│   └── IF cpt-cde > 0
│       └── EXEC SQL COMMIT              ← 1 seul COMMIT global
│
├── CLOSE-FILES
└── STOP RUN
```

---

## 9. Conversion de date TRANSFO-DATEBL

**Entrée** : `P-DATEBL = "YYYYMMDD"` (8 caractères alphanumériques)
**Sortie** : `W-DATEBL` = entier binaire VMS (PIC S9(11)V9(7) COMP, unité = 100 ns depuis 17-NOV-1858)

```cobol
*-- Étape 1 : initialiser l'epoch VMS (date zéro)
MOVE "17-NOV-1858 " TO W-DATE-ASCII(01:12).
CALL "SYS$BINTIM" USING BY DESCRIPTOR W-DATE-ASCII BY REFERENCE W-DATE-ZERO.
MOVE W-DATE-ZERO TO W-DATEBL.

*-- Étape 2 : construire "DD-MON-YYYY " dans W-DATE-ASCII
MOVE P-DATEBL(7:2) TO W-DATE-ASCII(1:2).    *-- positions 7-8 = JJ
MOVE P-DATEBL(1:4) TO W-DATE-ASCII(8:4).    *-- positions 1-4 = YYYY
MOVE P-DATEBL(5:2) TO W-MOIS-FR.            *-- positions 5-6 = MM numérique
PERFORM CVT-MOIS-FR-UK.                     *-- "08" → "AUG"
MOVE W-MOIS-UK TO W-DATE-ASCII(4:3).        *-- positions 4-6 = MON
*   Résultat intermédiaire : "DD-MON-YYYY "
CALL "SYS$BINTIM" USING BY DESCRIPTOR W-DATE-ASCII BY REFERENCE W-DATDEB.
*   (W-DATDEB non utilisé ensuite)

*-- Étape 3 : ajouter l'heure minuit pour W-DATEBL
MOVE "00:00:00.00" TO W-DATE-ASCII(13:11).
*   Format final : "DD-MON-YYYY 00:00:00.00"
CALL "SYS$BINTIM" USING BY DESCRIPTOR W-DATE-ASCII BY REFERENCE W-DATEBL.
```

`W-DATEBL` représente **minuit pile** de la date demandée. Le curseur CURCDE utilise `cast(cast(datebl as date ansi) as date vms)` pour comparer sans heure.

**Table CVT-MOIS-FR-UK** (EVALUATE W-MOIS-FR, 12 entrées) :

| MM | MON | MM | MON |
|---|---|---|---|
| 01 | JAN | 07 | JUL |
| 02 | FEB | 08 | AUG |
| 03 | MAR | 09 | SEP |
| 04 | APR | 10 | OCT |
| 05 | MAY | 11 | NOV |
| 06 | JUN | 12 | DEC |

**Équivalent Java 8** : `LocalDate.parse(dateBl, DateTimeFormatter.ofPattern("yyyyMMdd"))`. La conversion en binaire VMS n'est nécessaire qu'en adaptateur JDBC ; en logique métier, travailler avec `LocalDate`.

---

## 10. Comportements critiques à préserver

| Code | Règle | Impact Java |
|---|---|---|
| B-D05-01 | Curseur CURCDE filtre `THO` deux fois (duplicata source) — filtre effectif = `NOT IN ('THO','SIX','EIX')` | La requête JDBC doit exclure ces trois statuts |
| B-D05-02 | Commande absente de CRM (not found = `'02000'`) → traitée comme discordante (même chemin que statuts différents) | `Optional.empty()` → discordante |
| B-D05-03 | FAT≡FAP et GLT≡GLP : équivalences métier légitimes, ne pas traiter comme écarts | Implémenter dans `StatutEquivalence.areEquivalent()` |
| B-D05-04 | Clé UPDATE = `(CODLAB, NUMCDE, NUMRAL)` sans CODDEP | Signature `updateStatcrm(codlab, numcde, numral)` sans coddep |
| B-D05-05 | UPDATE : `STATCRM←''` (chaîne vide), `FLAG_CRM←'O'`, STATUT **inchangé** | Ne pas modifier STATUT |
| B-D05-06 | Un seul COMMIT global après tous les UPDATEs (pas de commit par UPDATE) | COMMIT délégué à l'adapter après la boucle complète |
| B-D05-07 | ROLLBACK conditionnel : seulement si `P-MAJ='O'` ET `cpt-cde > 0` | Si la liste est vide, pas de ROLLBACK ni de nouvelle transaction |
| B-D05-08 | DEADLOCK en phase scan → `FLAG-REJET='O'`, sortie de boucle TRT-CDE, programme continue vers phase MAJ | En Java : capturer le deadlock, ne pas relancer, continuer avec le tableau déjà rempli |
| B-D05-09 | tab-cde overflow (> 9 000) → `DISPLAY "table trop petite"` uniquement, pas d'arrêt | `tableOverflow = true` dans le résultat, log warning |
| B-D05-10 | GET-CDE-CRM-CPT = code mort (appel commenté dans TRT-CDE) | Ne pas implémenter en Java |

---

## 11. Architecture Java 8 cible

### 11.1 Structure des packages

```
com.example.crm.d05
├── application/
│   └── VerifCrmService.java
├── domain/
│   ├── CommandeDepot.java
│   ├── CommandeDiscordante.java
│   ├── StatutEquivalence.java
│   └── VerifCrmResult.java
├── port/
│   └── DepotCdeRepository.java
└── adapter/
    └── jdbc/
        └── RdbDepotCdeRepository.java   (impl. JDBC, hors scope TDD unitaire)
```

### 11.2 CommandeDepot.java

```java
package com.example.crm.d05.domain;

/**
 * Représente une ligne retournée par CURCDE (BD_DEPOT.CDE).
 * Champs: numcde(9), numral(1), statut(3), codlab(4).
 */
public final class CommandeDepot {

    private final int numcde;
    private final int numral;
    private final String statut;
    private final String codlab;

    public CommandeDepot(int numcde, int numral, String statut, String codlab) {
        this.numcde = numcde;
        this.numral = numral;
        this.statut = statut;
        this.codlab = codlab;
    }

    public int getNumcde()    { return numcde; }
    public int getNumral()    { return numral; }
    public String getStatut() { return statut; }
    public String getCodlab() { return codlab; }
}
```

### 11.3 CommandeDiscordante.java

```java
package com.example.crm.d05.domain;

/**
 * Entrée dans tab-cde : commande dont le statut DEPOT ≠ statut CRM.
 * Clé UPDATE : (codlab, numcde, numral) — CODDEP absent (B-D05-04).
 */
public final class CommandeDiscordante {

    private final int numcde;
    private final int numral;
    private final String statut;
    private final String codlab;

    public CommandeDiscordante(int numcde, int numral, String statut, String codlab) {
        this.numcde = numcde;
        this.numral = numral;
        this.statut = statut;
        this.codlab = codlab;
    }

    public int getNumcde()    { return numcde; }
    public int getNumral()    { return numral; }
    public String getStatut() { return statut; }
    public String getCodlab() { return codlab; }
}
```

### 11.4 StatutEquivalence.java

```java
package com.example.crm.d05.domain;

/**
 * Règles d'équivalence de statuts entre BD_DEPOT et BD_CRM (B-D05-03).
 *
 * Statuts équivalents (considérés comme synchronisés) :
 *   - Tout statut identique (FAX == FAX, etc.)
 *   - FAT (DEPOT) == FAP (CRM)
 *   - GLT (DEPOT) == GLP (CRM)
 *
 * La comparaison est directionnelle : statutDepot est la valeur COBOL,
 * statutCrm est STATENCOURS lu dans S.CDE_FAC.
 */
public final class StatutEquivalence {

    private StatutEquivalence() {}

    public static boolean areEquivalent(String statutDepot, String statutCrm) {
        if (statutDepot.equals(statutCrm)) return true;
        if ("FAT".equals(statutDepot) && "FAP".equals(statutCrm)) return true;
        if ("GLT".equals(statutDepot) && "GLP".equals(statutCrm)) return true;
        return false;
    }
}
```

### 11.5 VerifCrmResult.java

```java
package com.example.crm.d05.domain;

import java.util.Collections;
import java.util.List;

/**
 * Résultat de VerifCrmService.run().
 *
 * discordantes : commandes dont le statut DEPOT ≠ CRM (contenu de tab-cde).
 * tableOverflow : true si plus de 9000 discordantes détectées (B-D05-09).
 * nbMaj : nombre d'UPDATEs effectivement exécutés (0 si maj=false ou liste vide).
 */
public final class VerifCrmResult {

    private final List<CommandeDiscordante> discordantes;
    private final boolean tableOverflow;
    private final int nbMaj;

    public VerifCrmResult(List<CommandeDiscordante> discordantes,
                          boolean tableOverflow,
                          int nbMaj) {
        this.discordantes  = Collections.unmodifiableList(discordantes);
        this.tableOverflow = tableOverflow;
        this.nbMaj         = nbMaj;
    }

    public List<CommandeDiscordante> getDiscordantes() { return discordantes; }
    public boolean isTableOverflow()                   { return tableOverflow; }
    public int getNbMaj()                              { return nbMaj; }
}
```

### 11.6 DepotCdeRepository.java (port)

```java
package com.example.crm.d05.port;

import com.example.crm.d05.domain.CommandeDepot;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Port d'accès aux données pour D05_VERIF_CRM.
 *
 * findByDatebl  → résultat du curseur CURCDE (déjà filtré FLAG_CRM='N',
 *                 STATUT NOT IN ('THO','SIX','EIX'), cast date = W-DATEBL).
 * findStatencours → SELECT STATENCOURS FROM S.CDE_FAC LIMIT TO 1 ROW.
 *                   Empty si '02000' (not found).
 * updateStatcrm → UPDATE D.CDE SET STATCRM='', FLAG_CRM='O'
 *                 WHERE CODLAB=? AND NUMCDE=? AND NUMRAL=?   (pas de CODDEP — B-D05-04).
 */
public interface DepotCdeRepository {

    List<CommandeDepot> findByDatebl(String coddep, LocalDate datebl);

    Optional<String> findStatencours(String coddep, String codlab, int numcde, int numral);

    void updateStatcrm(String codlab, int numcde, int numral);
}
```

### 11.7 VerifCrmService.java (service applicatif)

```java
package com.example.crm.d05.application;

import com.example.crm.d05.domain.CommandeDepot;
import com.example.crm.d05.domain.CommandeDiscordante;
import com.example.crm.d05.domain.StatutEquivalence;
import com.example.crm.d05.domain.VerifCrmResult;
import com.example.crm.d05.port.DepotCdeRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Migration de TRAITEMENT-PRINCIPAL dans D05_VERIF_CRM.SCO.
 *
 * Phase 1 (READ ONLY) :
 *   - Parcourt toutes les commandes CURCDE pour la date demandée.
 *   - Compare chaque statut avec STATENCOURS dans S.CDE_FAC.
 *   - Accumule les discordantes dans tab-cde (max TABLE_MAX=9000).
 *
 * Phase 2 (READ WRITE, si maj=true et discordantes>0) :
 *   - UPDATE D.CDE SET STATCRM='', FLAG_CRM='O' pour chaque discordante.
 *   - Un seul COMMIT global implicite (géré par l'adapter, B-D05-06).
 *
 * La gestion transactionnelle (SET TRANSACTION READ ONLY / ROLLBACK /
 * SET TRANSACTION READ WRITE / COMMIT) est déléguée à l'adapter JDBC.
 */
public class VerifCrmService {

    private static final int TABLE_MAX = 9000;
    private static final DateTimeFormatter FMT_YYYYMMDD =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    private final String coddep;
    private final DepotCdeRepository repo;

    public VerifCrmService(String coddep, DepotCdeRepository repo) {
        this.coddep = coddep;
        this.repo   = repo;
    }

    /**
     * @param dateBl chaîne YYYYMMDD (= P-DATEBL dans COBOL)
     * @param maj    true = effectuer les UPDATEs (= P-MAJ='O')
     */
    public VerifCrmResult run(String dateBl, boolean maj) {
        LocalDate datebl = LocalDate.parse(dateBl, FMT_YYYYMMDD);

        // --- Phase 1 : scan READ ONLY ---
        List<CommandeDepot> commandes = repo.findByDatebl(coddep, datebl);

        List<CommandeDiscordante> discordantes = new ArrayList<>();
        boolean tableOverflow = false;

        for (CommandeDepot cmd : commandes) {
            Optional<String> statencours = repo.findStatencours(
                    coddep, cmd.getCodlab(), cmd.getNumcde(), cmd.getNumral());

            boolean ok = statencours.isPresent()
                    && StatutEquivalence.areEquivalent(cmd.getStatut(), statencours.get());

            if (!ok) {
                if (discordantes.size() < TABLE_MAX) {
                    discordantes.add(new CommandeDiscordante(
                            cmd.getNumcde(), cmd.getNumral(),
                            cmd.getStatut(), cmd.getCodlab()));
                } else {
                    tableOverflow = true;
                    // Comportement COBOL : display warning, traitement continue (B-D05-09)
                }
            }
        }

        // --- Phase 2 : mises à jour READ WRITE (si demandées) ---
        int nbMaj = 0;
        if (maj && !discordantes.isEmpty()) {
            // ROLLBACK + SET TRANSACTION READ WRITE = géré par l'adapter
            for (CommandeDiscordante d : discordantes) {
                repo.updateStatcrm(d.getCodlab(), d.getNumcde(), d.getNumral());
                nbMaj++;
            }
            // COMMIT unique = géré par l'adapter (B-D05-06)
        }

        return new VerifCrmResult(discordantes, tableOverflow, nbMaj);
    }
}
```

---

## 12. Stratégie TDD

### 12.1 Cycle Red / Green / Refactor

**Itération 1 — StatutEquivalence (règles pures)**
- RED : écrire `StatutEquivalenceTest` avec les 8 cas (identique, FAT/FAP, GLT/GLP, différent, directionnel, vide)
- GREEN : implémenter les trois conditions dans `areEquivalent()`
- REFACTOR : vérifier qu'aucune branche n'est redondante

**Itération 2 — Value objects**
- RED : écrire un test qui construit `CommandeDepot` et lit les getters
- GREEN : implémenter les constructeurs et getters
- REFACTOR : rendre les classes `final`, supprimer les setters

**Itération 3 — VerifCrmService phase scan**
- RED : `commandeAbsenteDeCrm_estDiscordante()` — rouge car service pas encore codé
- GREEN : implémenter la boucle de scan avec le stub `StubRepo`
- RED : `overflow_9000_commandesNok_flagTableOverflow()` — rouge
- GREEN : ajouter la condition `discordantes.size() < TABLE_MAX`

**Itération 4 — VerifCrmService phase MAJ**
- RED : `majTrue_avecDiscordantes_updateParDiscordante()` — rouge
- GREEN : implémenter le bloc `if (maj && !discordantes.isEmpty())`
- RED : `updateN_utilise_codlab_numcde_numral_sansCODDEP()` — vérifier B-D05-04
- GREEN : confirmer que la signature `updateStatcrm(codlab, numcde, numral)` n'inclut pas coddep

**Itération 5 — Tests de régression golden master**
Avant de connecter la vraie base : préparer un jeu de données de référence issu d'une exécution COBOL connue (golden master). L'adaptateur JDBC doit produire exactement le même résultat que le COBOL pour ce jeu.

### 12.2 Stub du repository

Pour tous les tests unitaires, utiliser un `StubRepo` manuel :

```java
private static class StubRepo implements DepotCdeRepository {
    final List<CommandeDepot> commandes = new ArrayList<>();
    final Map<String, String> statencours = new HashMap<>();
    final List<String> updatesEffectues = new ArrayList<>();

    @Override
    public List<CommandeDepot> findByDatebl(String coddep, LocalDate datebl) {
        return commandes;
    }

    @Override
    public Optional<String> findStatencours(String coddep, String codlab,
                                            int numcde, int numral) {
        String key = coddep + "|" + codlab + "|" + numcde + "|" + numral;
        return Optional.ofNullable(statencours.get(key));
    }

    @Override
    public void updateStatcrm(String codlab, int numcde, int numral) {
        updatesEffectues.add(codlab + "|" + numcde + "|" + numral);
    }

    void ajouterCommande(int numcde, int numral, String statut, String codlab) {
        commandes.add(new CommandeDepot(numcde, numral, statut, codlab));
    }

    void ajouterStatencours(String coddep, String codlab, int numcde, int numral, String statut) {
        statencours.put(coddep + "|" + codlab + "|" + numcde + "|" + numral, statut);
    }
}
```

---

## 13. Matrice des tests

### 13.1 StatutEquivalenceTest (8 tests)

| ID | Méthode de test | Règle testée |
|---|---|---|
| SE-01 | `statutsIdentiques_sontEquivalents` | FAX/FAX, GLT/GLT, FAT/FAT → true |
| SE-02 | `fat_fap_estEquivalent` | FAT/FAP → true |
| SE-03 | `glt_glp_estEquivalent` | GLT/GLP → true |
| SE-04 | `statutsDifferents_sontNonEquivalents` | FAT/GLT, FAX/FAP → false |
| SE-05 | `comparaisonEstDirectionnelle_fap_fat_estNonEquivalent` | FAP/FAT → false (B-D05-03) |
| SE-06 | `comparaisonEstDirectionnelle_glp_glt_estNonEquivalent` | GLP/GLT → false |
| SE-07 | `statut_vide_avec_vide_estEquivalent` | ""/""→ true |
| SE-08 | `statut_vide_avec_autreStatut_estNonEquivalent` | ""/FAX → false |

### 13.2 VerifCrmServiceTest (15 tests) — code JUnit inline pour les cas clés

```java
@Test
public void commandeAbsenteDeCrm_estDiscordante() {
    // B-D05-02 : absent de CRM → NOK (même traitement que statuts différents)
    repo.ajouterCommande(1000005, 0, "FAX", "0001");
    // pas d'entrée dans statencours → findStatencours retourne Optional.empty()

    VerifCrmResult res = service.run("20241108", false);

    assertEquals(1, res.getDiscordantes().size());
    assertFalse(res.isTableOverflow());
}

@Test
public void updateN_utilise_codlab_numcde_numral_sansCODDEP() {
    // B-D05-04 : la clé UPDATE = (codlab, numcde, numral) sans coddep
    repo.ajouterCommande(1000050, 0, "FAT", "LABO");

    service.run("20241108", true);

    assertEquals(1, repo.updatesEffectues.size());
    assertEquals("LABO|1000050|0", repo.updatesEffectues.get(0));
    // "CO" (le coddep) ne doit pas apparaître dans la clé d'update
}

@Test
public void majTrue_avecDiscordantes_updateParDiscordante() {
    repo.ajouterCommande(1000040, 0, "FAT", "0001"); // absente → NOK
    repo.ajouterCommande(1000041, 1, "GLT", "0002"); // absente → NOK

    VerifCrmResult res = service.run("20241108", true);

    assertEquals(2, res.getNbMaj());
    assertTrue(repo.updatesEffectues.contains("0001|1000040|0"));
    assertTrue(repo.updatesEffectues.contains("0002|1000041|1"));
}

@Test
public void majTrue_aucuneDiscordante_aucunUpdate() {
    // B-D05-07 : si cpt-cde = 0, pas de ROLLBACK ni de nouvelle transaction
    repo.ajouterCommande(1000030, 0, "FAX", "0001");
    repo.ajouterStatencours("CO", "0001", 1000030, 0, "FAX"); // OK

    VerifCrmResult res = service.run("20241108", true);

    assertTrue(repo.updatesEffectues.isEmpty());
    assertEquals(0, res.getNbMaj());
}

@Test
public void overflow_9000_commandesNok_flagTableOverflow() {
    // B-D05-09 : overflow → flag, pas d'arrêt
    for (int i = 0; i < 9001; i++) {
        repo.ajouterCommande(i + 1, 0, "FAT", "0001");
        // pas de statencours → toutes discordantes
    }

    VerifCrmResult res = service.run("20241108", false);

    assertEquals(9000, res.getDiscordantes().size());
    assertTrue(res.isTableOverflow());
}

@Test
public void fat_fap_commandeEstOk_pasDeDiscordante() {
    // B-D05-03 : FAT (DEPOT) ≡ FAP (CRM) → OK
    repo.ajouterCommande(1000002, 0, "FAT", "0001");
    repo.ajouterStatencours("CO", "0001", 1000002, 0, "FAP");

    VerifCrmResult res = service.run("20241108", false);

    assertTrue(res.getDiscordantes().isEmpty());
}

@Test
public void parseDateBl_yyyymmdd_transmisAuRepo() {
    final List<LocalDate> datesRecues = new ArrayList<>();
    DepotCdeRepository repoCapture = new DepotCdeRepository() {
        @Override
        public List<CommandeDepot> findByDatebl(String coddep, LocalDate datebl) {
            datesRecues.add(datebl);
            return Collections.emptyList();
        }
        @Override public Optional<String> findStatencours(String c, String l, int n, int r) { return Optional.empty(); }
        @Override public void updateStatcrm(String l, int n, int r) {}
    };

    new VerifCrmService("CO", repoCapture).run("20241108", false);

    assertEquals(1, datesRecues.size());
    assertEquals(LocalDate.of(2024, 11, 8), datesRecues.get(0));
}

@Test
public void coddepEstTransmisAuRepoPourfindStatencours() {
    // B-D05-02 : P-CODDEP (paramètre) utilisé dans findStatencours, pas un champ interne
    final List<String> coddesParcours = new ArrayList<>();
    repo.ajouterCommande(1, 0, "FAT", "L001");

    DepotCdeRepository repoCapture = new DepotCdeRepository() {
        @Override public List<CommandeDepot> findByDatebl(String coddep, LocalDate d) { return repo.commandes; }
        @Override
        public Optional<String> findStatencours(String coddep, String codlab, int numcde, int numral) {
            coddesParcours.add(coddep);
            return Optional.empty();
        }
        @Override public void updateStatcrm(String l, int n, int r) {}
    };

    new VerifCrmService("MO", repoCapture).run("20241108", false);

    assertEquals(1, coddesParcours.size());
    assertEquals("MO", coddesParcours.get(0));
}
```

### 13.3 Table complète des tests

| ID | Scénario | Données | Résultat attendu |
|---|---|---|---|
| T-D05-01 | Statuts identiques | DEPOT=FAX, CRM=FAX | OK, liste vide |
| T-D05-02 | FAT≡FAP | DEPOT=FAT, CRM=FAP | OK, liste vide |
| T-D05-03 | GLT≡GLP | DEPOT=GLT, CRM=GLP | OK, liste vide |
| T-D05-04 | Écart réel | DEPOT=FAT, CRM=GLT | 1 discordante |
| T-D05-05 | Absente de CRM | findStatencours → empty | 1 discordante |
| T-D05-06 | Mix OK et NOK | 3 commandes, 1 NOK | 1 discordante |
| T-D05-07 | P-MAJ=false | 3 discordantes | liste remplie, 0 UPDATE |
| T-D05-08 | P-MAJ=true, cpt=0 | 0 discordante | 0 UPDATE, 0 ROLLBACK |
| T-D05-09 | P-MAJ=true, cpt>0 | 2 discordantes | 2 UPDATEs, clé sans CODDEP |
| T-D05-10 | Overflow tableau | 9 001 discordantes | 9 000 entrées, tableOverflow=true |
| T-D05-11 | Exact 9 000 discordantes | 9 000 entrées | tableOverflow=false |
| T-D05-12 | Parse date YYYYMMDD | "20241108" | LocalDate(2024,11,8) |
| T-D05-13 | CODDEP transmis à findStatencours | coddep="MO" | findStatencours reçoit "MO" |
| T-D05-14 | Directionnalité FAP/FAT | DEPOT=FAP, CRM=FAT | discordante (B-D05-03) |
| T-D05-15 | Aucune commande | findByDatebl → liste vide | résultat vide, nbMaj=0 |

---

## 14. Définition of Done

- [ ] Tous les 23 tests (15 `VerifCrmServiceTest` + 8 `StatutEquivalenceTest`) passent au vert
- [ ] Aucun test n'accède à la base de données réelle (stubs uniquement)
- [ ] B-D05-01 à B-D05-10 : chaque comportement critique est couvert par au moins un test nominatif
- [ ] `StatutEquivalence.areEquivalent()` est directionnelle : `FAP/FAT` retourne false
- [ ] La clé `updateStatcrm` ne contient pas `coddep` (vérifiable via le stub)
- [ ] `tableOverflow` est vrai à partir de 9 001 discordantes, faux à 9 000 exactement
- [ ] `VerifCrmResult.getDiscordantes()` retourne une liste non modifiable
- [ ] La conversion `"20241108"` → `LocalDate.of(2024, 11, 8)` est vérifiée par test
- [ ] Le code mort `GET-CDE-CRM-CPT` n'est pas implémenté
- [ ] Code review : aucune dépendance vers VMS ou Oracle Rdb dans les packages `domain` et `application`
- [ ] Couverture de code (Jacoco) ≥ 90 % sur `VerifCrmService` et `StatutEquivalence`
- [ ] Documentation Javadoc sur tous les types publics avec référence aux comportements critiques (B-D05-xx)

---

## 15. Ordre de réalisation recommandé

1. **`StatutEquivalence`** — règles pures, testable sans dépendance. Tests : SE-01 à SE-08.
2. **`CommandeDepot` + `CommandeDiscordante`** — value objects immutables. Tests : construction/getters.
3. **`VerifCrmResult`** — agrégat de sortie. Test : liste non modifiable.
4. **`DepotCdeRepository`** (interface seule) + `StubRepo` — fondation des tests unitaires.
5. **`VerifCrmService` phase scan** — boucle de comparaison. Tests : T-D05-01 à T-D05-06, T-D05-10, T-D05-11, T-D05-12, T-D05-13, T-D05-14, T-D05-15.
6. **`VerifCrmService` phase MAJ** — bloc `if (maj && ...)`. Tests : T-D05-07, T-D05-08, T-D05-09.
7. **`RdbDepotCdeRepository`** (adapter JDBC) — tests d'intégration avec vraie base. Vérifier la conversion `LocalDate` → date VMS binaire dans le WHERE du curseur.
8. **Golden master** — exécuter le COBOL original sur un jeu de données de référence, comparer avec la sortie Java. Critère de parité comportementale.
