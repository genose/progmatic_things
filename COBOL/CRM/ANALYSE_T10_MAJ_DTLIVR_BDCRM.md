# Analyse technique — T10_MAJ_DTLIVR_BDCRM.COB

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
9. [Remapping FO → MO et cascade labo 3628 → 9994](#9-remapping-fo--mo-et-cascade-labo-3628--9994)
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
| Nom du programme | `T10_MAJ_DTLIVR_BDCRM` |
| Fichier source | `T10_MAJ_DTLIVR_BDCRM.COB` (699 lignes) |
| Auteur | JM BELVAL |
| Date de création | 22/02/2011 |
| Version | 1.000 |
| Historique | V3.001-5813 AP:JMB — LIB:NOUVEAU PROGRAMME |
|  | V3.001-3913 AP:FC — LIB:PASSAGE V3.000 (NO COLIS SUR 8) |
| Fonctionnalité | MAJ DTLIVR DANS BDEXTRANET |
| Environnement | OpenVMS / COBOL / Oracle Rdb SQL embarqué |

**Rôle** : programme batch qui met à jour la date de livraison (`DTLIVR`) et le flag de livraison (`FLAGLIV='O'`) dans la table `E.CDE_FAC` à partir de fichiers de retour de transport (fichiers RT). Il découvre automatiquement les fichiers à traiter via `LIB$FIND_FILE` (wildcard VMS), traite chaque enregistrement individuellement avec commit par enregistrement, et gère deux cas particuliers :

- Remapping dépôt `FO` → `MO` avant toute opération SQL
- Cascade laboratoire `3628` → `9994` (commande miroir dépôt CO)

---

## 2. Fichiers d'entrée et de sortie

| Nom logique | Nom RMS / Pattern | Mode | Rôle |
|---|---|---|---|
| `FIC-TELE` | `FOUND-FILESPEC` (variable) | INPUT séquentiel | Fichier RT courant (1 enregistrement = 1 livraison) |
| `FIC-MESSAGES` | `RMS_MSG` | INPUT INDEXED | Libellés des messages d'erreur |
| `FIC-ANOMALIES` | `RMS_ANO` | OUTPUT | Anomalies (erreurs SQL fatales, FIN-ANORMALE) |

Le pattern de découverte des fichiers est : `DIRDAT:MAJBDSTAT*.DAT;*` (wildcard VMS, toutes versions).

---

## 3. Paramètres d'entrée

**Aucun paramètre explicite.** Le programme est autonome : il découvre et traite tous les fichiers `DIRDAT:MAJBDSTAT*.DAT;*` présents dans le répertoire `DIRDAT` au moment de l'exécution.

---

## 4. Variables de travail clés

### 4.1 Flags

| Variable COBOL | PIC | Valeur initiale | Valeur 88 | Rôle |
|---|---|---|---|---|
| `FLAG-FINFIC` | `PIC X` | `'N'` | `FINFIC VALUE "O"` | Fin du fichier FIC-TELE courant (EOF) |
| `FLAG-CDE9994` | `PIC X` | `'N'` | `CDE9994 VALUE "O"` | Commande miroir 9994 trouvée pour un labo 3628 |
| `FLAG-TROUVE` | `PIC X` | `'N'` | `TROUVE VALUE "O"` | Résultat GESTION-SQLSTATE : ligne trouvée ou succès SQL |
| `FLAG-DEADLOCK` | `PIC X` | `'N'` | `DEADLOCK VALUE "O"` | Deadlock détecté sur Rdb → FIN-ANORMALE (B-T10-10) |
| `FLAG-FIC-TROUVE` | `PIC X` | `'O'` | `FIC-TROUVE VALUE "O"` | Contrôle boucle LIB$FIND_FILE (false = plus de fichiers) |
| `FLAG-CREAT-OK` | `PIC X` | `'N'` | `CREAT-OK VALUE "O"` | UPDATE réussi → sortie de boucle MAJ-DTLIVR |
| `FLAG-TRANSACTION-OK` | `PIC X` | `'N'` | `TRANSACTION-OK VALUE "O"` | Transaction active |

Note : 15 autres flags déclarés mais non utilisés dans le flux principal (code vestigial).

### 4.2 Découverte des fichiers

| Variable COBOL | PIC | Valeur initiale | Rôle |
|---|---|---|---|
| `FICHIER01` | `PIC X(400)` | `"DIRDAT:MAJBDSTAT*.DAT;*"` | Pattern VMS initial |
| `FILESPEC` | `PIC X(255)` | — | Pattern courant passé à LIB$FIND_FILE |
| `FOUND-FILESPEC` | `PIC X(255)` | — | Chemin complet du fichier trouvé par LIB$FIND_FILE |
| `CONTEXT` | `PIC S9(9) COMP` | — | Contexte d'itération LIB$FIND_FILE (curseur opaque) |
| `STATUS_VALUE` | `PIC S9(9) COMP` | — | Statut retourné par LIB$FIND_FILE |
| `STATUS_RESULT` | `PIC S9(9) COMP` | — | Résultat GIVING de LIB$FIND_FILE |
| `RMS$_NMF` | `PIC S9(9) COMP EXTERNAL` | — | Constante VMS : no more files |
| `RMS$_FNF` | `PIC S9(9) COMP EXTERNAL` | — | Constante VMS : file not found |
| `RMS$_NORMAL` | `PIC S9(9) COMP EXTERNAL` | — | Constante VMS : succès |
| `SS$_NORMAL` | `PIC S9(9) COMP EXTERNAL` | — | Constante VMS : succès système |

### 4.3 Dates

| Variable COBOL | PIC | Rôle |
|---|---|---|
| `W-DATASC` | `PIC X(23)` | Date livraison ASCII VMS lue depuis le fichier (format `DD-MON-YYYY HH:MM:SS.CC`) |
| `W-DATBIN` | `PIC S9(11)V9(7) COMP` | Date livraison en binaire VMS après `SYS$BINTIM` — utilisée dans les deux UPDATEs |
| `W-DATSYS` | `PIC S9(11)V9(7) COMP` | Date système courante en binaire VMS |
| `W-DATJOU` | `PIC S9(11)V9(7) COMP` | Minuit aujourd'hui — **calculé mais jamais utilisé** (code vestigial) |
| `ONE-DAY-ASCII` | `PIC X(16)` | `"0001 00:00:00.00"` — intervalle 1 jour |
| `ONE-DAY-INTERVAL` | `PIC S9(11)V9(7) COMP` | Intervalle binaire 1 jour — **jamais utilisé** (code vestigial) |
| `LIMITE-ASCII` | `PIC X(16)` | `"0060 00:00:00.00"` — intervalle 60 jours |
| `LIMITE-INTERVAL` | `PIC S9(11)V9(7) COMP` | Intervalle binaire 60 jours — **jamais utilisé** (code vestigial) |
| `W-DATLIM` | `PIC S9(11)V9(7) COMP` | Aujourd'hui - 60j — **jamais utilisé** (code vestigial) |

### 4.4 Données de l'enregistrement courant

| Variable COBOL | PIC | Rôle |
|---|---|---|
| `W-NUMCDE` | `PIC 9(7)` | Numéro de commande — **7 chiffres** (différent de D05 qui est PIC 9(9)) |
| `W-NUMRAL` | `PIC 9(1)` | Numéro de ralliement |
| `W-CDELAB` | `PIC X(8)` | Résultat SELECT CDELAB(1:8) — 8 chars, numérique si valide |
| `W-CODDEP` | `PIC X(4)` | Code dépôt courant (après normalisation FO→MO) |
| `W-CODLAB` | `PIC X(4)` | Code laboratoire courant |

### 4.5 Cascade 9994

| Variable COBOL | PIC | Rôle |
|---|---|---|
| `W-NUMCDE-9994` | `PIC 9(7)` | Numéro commande miroir = W-CDELAB(1:7) converti en numérique |
| `W-NUMRAL-9994` | `PIC 9(1)` | Numéro ralliement miroir = W-CDELAB(8:1) converti en numérique |

### 4.6 Signalement d'erreur VMS

| Variable COBOL | PIC | Valeur | Rôle |
|---|---|---|---|
| `LOGNAM` | `PIC X(6)` | `"ARRPRG"` | Nom du logique VMS à positionner en erreur |
| `VALLOG` | `PIC X` | `"O"` | Valeur positionnée en erreur → `LIB$SET_LOGICAL ARRPRG=O` |

### 4.7 Retry (déclaré, non utilisé réellement)

| Variable COBOL | PIC | Valeur initiale | Rôle |
|---|---|---|---|
| `NBR-TRIES` | `PIC 9(3)` | — | Compteur de tentatives — déclaré, jamais incrémenté |
| `MAX-TRIES` | `PIC 9(3)` | `999` | Limite de tentatives — déclarée, jamais comparée |

---

## 5. Base de données et SQL

### 5.1 Base déclarée

| Alias SQL | Base logique | Table principale utilisée |
|---|---|---|
| `E` | `BD_CRM` / BD_EXTRANET | `E.CDE_FAC` |

### 5.2 Structure du fichier d'entrée (ENR-MAJDTLIVR)

Définie par `COPY "DIRCOB:T10_DESC_FICMAJBDSTAT.LIB"` (bibliothèque externe, non disponible). Champs déduits de l'usage dans le programme :

| Champ | PIC déduit | Description |
|---|---|---|
| `MAJBD-CODDEP` | `PIC X(4)` | Code dépôt source |
| `MAJBD-CODLAB` | `PIC X(4)` | Code laboratoire |
| `MAJBD-NUMCDE` | `PIC 9(7)` | Numéro de commande (7 chiffres) |
| `MAJBD-NUMRAL` | `PIC 9(1)` | Numéro de ralliement |
| `MAJBD-DATLIV` | `PIC X(23)` | Date de livraison en format VMS ASCII `DD-MON-YYYY HH:MM:SS.CC` |

### 5.3 Requête TST-3628-9994 étape 1 (recherche commande principale)

```sql
SELECT SUBSTRING(CDELAB FROM 1 FOR 8) INTO :W-CDELAB
  FROM E.CDE_FAC
 WHERE CODDEP = :W-CODDEP
   AND CODLAB = :W-CODLAB
   AND NUMCDE = :W-NUMCDE
   AND NUMRAL = :W-NUMRAL
 LIMIT TO 1 ROW
```

### 5.4 Requête TST-3628-9994 étape 2 (recherche commande miroir 9994)

```sql
SELECT SUBSTRING(CDELAB FROM 1 FOR 8) INTO :W-CDELAB
  FROM E.CDE_FAC
 WHERE CODDEP = 'CO'
   AND CODLAB = '9994'
   AND NUMCDE = :W-NUMCDE-9994
   AND NUMRAL = :W-NUMRAL-9994
 LIMIT TO 1 ROW
```

Note : `CODDEP='CO'` et `CODLAB='9994'` sont des **constantes hardcodées** (B-T10-05).

### 5.5 UPDATE principal (MAJ-DTLIVR-BDSTATSEXTRANET)

```sql
UPDATE E.CDE_FAC
   SET DTLIVR  = :W-DATBIN,
       FLAGLIV = 'O'
 WHERE CODDEP = :W-CODDEP
   AND CODLAB = :W-CODLAB
   AND NUMCDE = :W-NUMCDE
   AND NUMRAL = :W-NUMRAL
```

### 5.6 UPDATE cascade 9994 (MAJ-DTLIVR-BDSTATSEXTRANET-9994)

```sql
UPDATE E.CDE_FAC
   SET DTLIVR  = :W-DATBIN,
       FLAGLIV = 'O'
 WHERE CODDEP = 'CO'
   AND CODLAB = '9994'
   AND NUMCDE = :W-NUMCDE-9994
   AND NUMRAL = :W-NUMRAL-9994
```

`W-DATBIN` est la même valeur que pour l'UPDATE principal (B-T10-08).

### 5.7 Gestion des transactions

| Phase | Transaction SQL | Commit/Rollback |
|---|---|---|
| Initialisation | `SET TRANSACTION READ ONLY` | `COMMIT` immédiat après initialisation |
| Par enregistrement | `SET TRANSACTION READ WRITE` | `COMMIT` **par enregistrement** (pas de batch) |
| FIN-ANORMALE | — | `ROLLBACK` puis `STOP RUN` |

---

## 6. Appels externes VMS et équivalents Java

### 6.1 Format date ASCII VMS

Le format standard VMS ASCII est : `DD-MON-YYYY HH:MM:SS.CC` (23 caractères).
- `DD` : jour sur 2 chiffres
- `MON` : mois en anglais 3 lettres majuscules
- `YYYY` : année sur 4 chiffres
- `HH:MM:SS.CC` : heure, minute, seconde, centièmes

Exemple : `"08-AUG-2024 14:30:00.00"`

Le binaire VMS est un entier 64 bits (PIC S9(11)V9(7) COMP) comptant les intervalles de 100 nanosecondes depuis l'epoch VMS : **17 novembre 1858 à minuit**.

`MAJBD-DATLIV` est déjà dans ce format — il est passé tel quel à `SYS$BINTIM` (B-T10-01).

### 6.2 Tableau des appels

| Appel VMS | Signature COBOL | Rôle VMS | Équivalent Java 8 |
|---|---|---|---|
| `SYS$BINTIM` (epoch) | `CALL "SYS$BINTIM" USING BY DESCRIPTOR W-D-DATE BY REFERENCE W-DATE-ZERO` | Convertit `"17-NOV-1858 00:00:00.00"` en entier binaire VMS (epoch = 0) | Non nécessaire en Java si on travaille avec `LocalDateTime` |
| `SYS$GETTIM` | `CALL "SYS$GETTIM" USING BY REFERENCE W-DATSYS` | Retourne l'heure système courante en binaire VMS | `LocalDateTime.now()` |
| `SYS$ASCTIM` | `CALL "SYS$ASCTIM" USING BY REFERENCE W-LENGTH BY DESCRIPTOR W-D-DATE BY REFERENCE W-DATSYS BY VALUE W-FLAG` | Convertit un binaire VMS en format ASCII VMS (`DD-MON-YYYY HH:MM:SS.CC`) | `DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss.SS", Locale.ENGLISH).format(dt).toUpperCase()` |
| `SYS$BINTIM` (datliv) | `CALL "SYS$BINTIM" USING BY DESCRIPTOR W-DATASC BY REFERENCE W-DATBIN` | Convertit la date ASCII VMS du fichier en binaire VMS | `DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss.SS", Locale.ENGLISH)` (voir `VmsDateCodec`) |
| `SYS$BINTIM` (1 jour) | `CALL "SYS$BINTIM" USING BY DESCRIPTOR ONE-DAY-ASCII BY REFERENCE ONE-DAY-INTERVAL` | Calcule l'intervalle binaire de 1 jour — **code vestigial** | Ne pas implémenter |
| `SYS$BINTIM` (60 jours) | `CALL "SYS$BINTIM" USING BY DESCRIPTOR LIMITE-ASCII BY REFERENCE LIMITE-INTERVAL` | Calcule l'intervalle binaire de 60 jours — **code vestigial** | Ne pas implémenter |
| `LIB$SUB_TIMES` | `CALL "LIB$SUB_TIMES" USING BY REFERENCE W-DATSYS BY REFERENCE LIMITE-INTERVAL BY REFERENCE W-DATLIM` | Calcule aujourd'hui - 60 jours — **jamais utilisé** | Ne pas implémenter |
| `LIB$FIND_FILE` | `CALL "LIB$FIND_FILE" USING BY DESCRIPTOR FILESPEC FOUND-FILESPEC BY REFERENCE CONTEXT OMITTED OMITTED BY REFERENCE STATUS_VALUE OMITTED GIVING STATUS_RESULT` | Découverte itérative des fichiers correspondant au wildcard VMS ; retourne un fichier à chaque appel via `CONTEXT` | `Files.newDirectoryStream(dir, "MAJBDSTAT*.DAT")` ou `PathMatcher` |
| `LIB$FIND_FILE_END` | `CALL "LIB$FIND_FILE_END" USING BY REFERENCE CONTEXT GIVING STATUS_RESULT` | Libère le contexte d'itération LIB$FIND_FILE | `DirectoryStream.close()` (auto dans try-with-resources) |
| `LIB$SET_LOGICAL` | `CALL "LIB$SET_LOGICAL" USING BY DESCRIPTOR LOGNAM BY DESCRIPTOR VALLOG` | Positionne le logique VMS `ARRPRG=O` pour signaler une erreur au planificateur batch | `System.exit(1)` — code retour non-zéro |
| `SYS$PUTMSG` | `CALL "SYS$PUTMSG" USING RDB$MESSAGE_VECTOR` | Affiche un message d'erreur VMS depuis le vecteur de messages Rdb | `System.err.println(...)` ou logger |
| `SQL$GET_ERROR_TEXT` | `CALL "SQL$GET_ERROR_TEXT" USING BY DESCRIPTOR GET-ERROR-BUFFER BY REFERENCE GET-MSG-LEN` | Récupère le texte d'erreur Rdb (jusqu'à 300 chars, 4 fragments) | `SQLException.getMessage()` |

---

## 7. Gestion SQLSTATE

GESTION-SQLSTATE est appelé après chaque opération SQL. Table complète telle qu'elle apparaît dans le source T10 :

```cobol
MOVE 'N' TO FLAG-DEADLOCK.
EVALUATE SQLSTATE
  WHEN '02000'              MOVE 'N' TO FLAG-TROUVE
  WHEN '22002'              MOVE 'O' TO FLAG-TROUVE
  WHEN 'R1001'              MOVE 'O' TO FLAG-DEADLOCK
  WHEN 'R1002'              MOVE 'O' TO FLAG-DEADLOCK
  WHEN '00000' THRU '01999' MOVE 'O' TO FLAG-TROUVE
  WHEN '02001' THRU '22001' PERFORM FIN-ANORMALE
  WHEN '22003' THRU 'R1000' PERFORM FIN-ANORMALE
  WHEN 'R1003' THRU 'S9999' PERFORM FIN-ANORMALE
END-EVALUATE.
IF DEADLOCK
   DISPLAY "DEADLOCK !!!!!"
   MOVE "N" TO FLAG-TROUVE
   PERFORM FIN-ANORMALE
END-IF.
```

| Plage SQLSTATE | Action | Notes |
|---|---|---|
| `'02000'` | `FLAG-TROUVE='N'` | Not found — normal |
| `'22002'` | `FLAG-TROUVE='O'` | Indicateur null — traité comme trouvé |
| `'R1001'` | `FLAG-DEADLOCK='O'` | Deadlock Rdb |
| `'R1002'` | `FLAG-DEADLOCK='O'` | Lock conflict Rdb |
| `'00000'` à `'01999'` | `FLAG-TROUVE='O'` | Succès et avertissements SQL |
| `'02001'` à `'22001'` | `FIN-ANORMALE` | Erreurs SQL fatales |
| `'22003'` à `'R1000'` | `FIN-ANORMALE` | Erreurs SQL fatales |
| `'R1003'` à `'S9999'` | `FIN-ANORMALE` | Erreurs SQL fatales |

**Comportement DEADLOCK critique (B-T10-10)** : `R1001`/`R1002` positionnent `FLAG-DEADLOCK='O'`, puis le bloc `IF DEADLOCK` appelle `FIN-ANORMALE` → `STOP RUN`. Il n'y a **pas de retry réel** malgré la boucle `PERFORM MAJ-DTLIVR... UNTIL CREAT-OK` — un deadlock termine le programme.

**Différence fondamentale D05 vs T10 pour les deadlocks** :

| Programme | Comportement deadlock |
|---|---|
| D05 | `FLAG-REJET='O'`, sortie de boucle TRT-CDE, **programme continue** |
| T10 | `FIN-ANORMALE` → `ROLLBACK` → `LIB$SET_LOGICAL ARRPRG=O` → `STOP RUN` |

**FIN-ANORMALE (T10)** :

```
FIN-ANORMALE
├── CALL SYS$PUTMSG(RDB$MESSAGE_VECTOR)      ← affichage message VMS
├── DISPLAY "FIN ANORMALE"
├── DISPLAY "SQLSTATE " SQLSTATE
├── DISPLAY NUM-MSG " " ANO_MESSAGE
├── IF NUM-MSG > "2000" → WRITE ANO-ENREG
│   ELSE → CALL D00_MSGGES
├── SQL$GET_ERROR_TEXT → GET-ERROR-BUFFER (300 chars)
├── DISPLAY GET-ERROR-BUFFER(1:GET-MSG-LEN)
├── WRITE ANO-ENREG (1 à 4 fragments de 80 chars)
├── DISPLAY "AVANT STOP RUN"
├── FERMETURE-FICHIERS           ← CLOSE FIC-TELE
├── EXEC SQL ROLLBACK
├── MOVE "O" TO VALLOG
├── CALL LIB$SET_LOGICAL USING LOGNAM VALLOG  ← ARRPRG = "O"
└── STOP RUN
```

---

## 8. Flux d'exécution complet

```
TRAITEMENT-PRINCIPAL
│
├── EXEC SQL SET TRANSACTION READ ONLY
│
├── INITIALISATION
│   ├── Compteurs → 0
│   ├── CALL SYS$BINTIM("17-NOV-1858 ") → W-DATE-ZERO      (epoch VMS)
│   ├── CALL SYS$GETTIM → W-DATSYS                          (heure système)
│   ├── CALL SYS$ASCTIM(W-DATSYS) → W-D-DATE               (ASCII courant)
│   ├── Forcer W-D-HEURE ← "00:00:00.00"
│   ├── CALL SYS$BINTIM(W-D-DATE) → W-DATJOU               (minuit aujourd'hui — vestigial)
│   ├── CALL SYS$BINTIM("0001 00:00:00.00") → ONE-DAY-INTERVAL  (vestigial)
│   ├── CALL SYS$BINTIM("0060 00:00:00.00") → LIMITE-INTERVAL   (vestigial)
│   └── CALL LIB$SUB_TIMES(W-DATSYS, LIMITE-INTERVAL) → W-DATLIM (vestigial)
│
├── EXEC SQL COMMIT                            ← clôt transaction READ ONLY d'init
│
├── OPEN-FILES (FIC-MESSAGES + FIC-ANOMALIES)
│
├── MOVE FICHIER01 TO FILESPEC                 ← "DIRDAT:MAJBDSTAT*.DAT;*"
├── MOVE "O" TO FLAG-FIC-TROUVE
│
├── PERFORM TRT-FILES UNTIL NOT FIC-TROUVE
│   │
│   └── [TRT-FILES]
│       ├── CALL LIB$FIND_FILE(FILESPEC, FOUND-FILESPEC, CONTEXT) → STATUS_RESULT
│       ├── IF STATUS_RESULT = RMS$_NORMAL
│       │   ├── MOVE "O" TO FLAG-FIC-TROUVE
│       │   └── TRT-FICHIER
│       │       ├── OUVERTURE-FICHIERS : OPEN INPUT FIC-TELE (avec FOUND-FILESPEC)
│       │       ├── MOVE "N" TO FLAG-FINFIC
│       │       ├── PERFORM TRAITEMENT-INFOS UNTIL FINFIC
│       │       │   └── [voir section 9.2]
│       │       └── FERMETURE-FICHIERS : CLOSE FIC-TELE
│       ├── IF STATUS_RESULT = RMS$_NMF OR RMS$_FNF
│       │   ├── MOVE "N" TO FLAG-FIC-TROUVE
│       │   └── CALL LIB$FIND_FILE_END(CONTEXT)
│       └── (RENAME-FICHIER commenté — fichiers non archivés)
│
├── DISPLAY "PLUS DE FICHIER RI A INTEGRER"
├── CLOSE-FILES
└── STOP RUN
```

### 8.1 TRAITEMENT-INFOS (traitement par enregistrement)

```
TRAITEMENT-INFOS
│
├── INITIALIZE ENR-MAJDTLIVR
├── READ FIC-TELE AT END → MOVE "O" TO FLAG-FINFIC
│
└── IF NOT FINFIC
    ├── MOVE MAJBD-CODDEP → W-CODDEP
    ├── MOVE MAJBD-CODLAB → W-CODLAB
    ├── MOVE MAJBD-NUMCDE → W-NUMCDE
    ├── MOVE MAJBD-NUMRAL → W-NUMRAL
    │
    ├── [Remapping] IF W-CODDEP = "FO" → MOVE "MO" TO W-CODDEP   (B-T10-03)
    │
    ├── MOVE "N" TO FLAG-CDE9994
    ├── IF W-CODLAB = '3628' → PERFORM TST-3628-9994               (B-T10-04/05/06)
    │
    ├── MOVE MAJBD-DATLIV TO W-DATASC                              (B-T10-01)
    ├── CALL SYS$BINTIM(W-DATASC) → W-DATBIN                      (conversion ASCII→binaire)
    │
    ├── DISPLAY "MAJ DTLIVR CDE BDCRM CODDEP=" W-CODDEP ...
    │
    ├── PERFORM MAJ-DTLIVR-BDSTATSEXTRANET UNTIL CREAT-OK
    │   └── [UPDATE E.CDE_FAC + GESTION-SQLSTATE + COMMIT si succès]
    │
    ├── MOVE "N" TO FLAG-CREAT-OK
    │
    └── IF CDE9994
        └── PERFORM MAJ-DTLIVR-BDSTATSEXTRANET-9994 UNTIL CREAT-OK
            └── [UPDATE E.CDE_FAC (CO/9994) + GESTION-SQLSTATE + COMMIT si succès]
```

### 8.2 MAJ-DTLIVR-BDSTATSEXTRANET (boucle par enregistrement)

```
MAJ-DTLIVR-BDSTATSEXTRANET [PERFORM UNTIL CREAT-OK]
├── MOVE "N" TO FLAG-CREAT-OK
├── IF DEADLOCK → EXEC SQL ROLLBACK             ← en théorie pour retry, jamais atteint
├── EXEC SQL SET TRANSACTION READ WRITE
├── EXEC SQL UPDATE E.CDE_FAC SET DTLIVR=:W-DATBIN, FLAGLIV='O'
│   WHERE CODDEP=:W-CODDEP AND CODLAB=:W-CODLAB AND NUMCDE=:W-NUMCDE AND NUMRAL=:W-NUMRAL
├── GESTION-SQLSTATE
└── IF NOT DEADLOCK
    ├── MOVE "O" TO FLAG-CREAT-OK               ← sortie de boucle
    └── EXEC SQL COMMIT                          ← COMMIT par enregistrement (B-T10-07)
```

---

## 9. Remapping FO → MO et cascade labo 3628 → 9994

### 9.1 Remapping FO → MO (B-T10-03)

```cobol
IF W-CODDEP = "FO"
   MOVE "MO" TO W-CODDEP
END-IF
```

Ce remapping s'applique **avant** toute requête SQL, y compris avant `TST-3628-9994`. Si `CODDEP="FO"` et `CODLAB='3628'`, la recherche dans `CDE_FAC` sera faite avec `CODDEP='MO'`.

### 9.2 Cascade laboratoire 3628 → 9994 (TST-3628-9994)

Certaines commandes du labo 3628 ont une commande miroir sous le labo 9994 (dépôt CO). Si cette commande miroir existe, elle doit recevoir la même mise à jour de date de livraison.

```
TST-3628-9994
│
├── [Étape 1] SELECT CDELAB(1:8) INTO :W-CDELAB
│   FROM E.CDE_FAC
│   WHERE CODDEP=:W-CODDEP AND CODLAB='3628'
│         AND NUMCDE=:W-NUMCDE AND NUMRAL=:W-NUMRAL
│   LIMIT TO 1 ROW
├── GESTION-SQLSTATE
│
└── IF TROUVE
    └── IF W-CDELAB IS NUMERIC                    ← tous les 8 chars sont des chiffres
        ├── W-NUMCDE-9994 ← W-CDELAB(1:7)         ← 7 premiers chars = numéro commande (B-T10-06)
        ├── W-NUMRAL-9994 ← W-CDELAB(8:1)         ← 8e char = numéro ralliement
        │
        ├── [Étape 2] SELECT CDELAB(1:8) INTO :W-CDELAB
        │   FROM E.CDE_FAC
        │   WHERE CODDEP='CO' AND CODLAB='9994'    ← CODDEP='CO' hardcodé (B-T10-05)
        │         AND NUMCDE=:W-NUMCDE-9994 AND NUMRAL=:W-NUMRAL-9994
        │   LIMIT TO 1 ROW
        ├── GESTION-SQLSTATE
        │
        └── IF TROUVE
            ├── DISPLAY "commande 3628 ==> Commande 9994 TROUVE " W-CDELAB
            └── MOVE "O" TO FLAG-CDE9994           ← déclenchera MAJ-DTLIVR-9994
```

**Règles critiques** :
- Si `CDELAB` contient un caractère non numérique (espace, lettre, etc.) → lien 9994 ignoré silencieusement (B-T10-04)
- `CODDEP='CO'` est hardcodé pour la commande miroir, indépendant de `W-CODDEP` (B-T10-05)
- `numcde9994` = 7 premiers chars de CDELAB, `numral9994` = 8e char (B-T10-06)
- L'`UPDATE` 9994 utilise la même valeur `W-DATBIN` que l'UPDATE principal (B-T10-08)

---

## 10. Comportements critiques à préserver

| Code | Règle | Impact Java |
|---|---|---|
| B-T10-01 | `MAJBD-DATLIV` est déjà en format VMS ASCII `DD-MON-YYYY HH:MM:SS.CC` — passé tel quel à `SYS$BINTIM`, aucune conversion de format | Parser directement avec `DateTimeFormatter` sur ce format |
| B-T10-02 | `LIB$FIND_FILE` itère tous les fichiers `MAJBDSTAT*.DAT` un par un via `CONTEXT` | `Files.newDirectoryStream(dir, glob)` ou `PathMatcher` |
| B-T10-03 | Remapping `FO→MO` appliqué **avant** tout traitement SQL, y compris `TST-3628-9994` | `DeliveryCodexNormalizer.normalize()` en premier |
| B-T10-04 | Si `W-CDELAB IS NUMERIC` est faux → lien 9994 ignoré silencieusement, aucune exception | `isNumeric()` retourne false → `Optional.empty()` |
| B-T10-05 | Recherche et UPDATE 9994 : `CODDEP='CO'` hardcodé, indépendant de `W-CODDEP` | Constante `"CO"` dans `existsCdeFac9994()` et `updateDtlivr9994()` |
| B-T10-06 | `W-NUMCDE-9994 = CDELAB(1:7)`, `W-NUMRAL-9994 = CDELAB(8:1)` — extraction par position | `Integer.parseInt(cdelab.substring(0, 7))` et `Integer.parseInt(cdelab.substring(7, 8))` |
| B-T10-07 | `COMMIT` par enregistrement (pas de batch, pas de transaction globale) | L'adapter appelle `COMMIT` dans `updateDtlivr()` |
| B-T10-08 | L'UPDATE 9994 utilise la même `W-DATBIN` que l'UPDATE principal | Passer le même `datliv` aux deux appels `updateDtlivr` |
| B-T10-09 | Clé UPDATE principale : `(CODDEP, CODLAB, NUMCDE, NUMRAL)` — toutes les colonnes | Signature `updateDtlivr(coddep, codlab, numcde, numral, datliv)` |
| B-T10-10 | DEADLOCK → `FIN-ANORMALE` → `STOP RUN` (pas de retry réel malgré la boucle) | Lever `RuntimeException` depuis l'adapter ; ne pas implémenter de retry |
| B-T10-11 | `FIN-ANORMALE` : `LIB$SET_LOGICAL ARRPRG=O` signale l'erreur au planificateur batch | `System.exit(1)` ou exception non catchée → code retour non-zéro |
| B-T10-12 | `CVT-MONTH-ASC-NUM`, `CVT-MONTH-NUM-ASC`, `RENAME-FICHIER` = code mort | Ne pas implémenter |
| B-T10-13 | `W-DATJOU`, `W-DATLIM`, `ONE-DAY-INTERVAL`, `LIMITE-INTERVAL` = code d'init vestigial | Ne pas implémenter |

---

## 11. Architecture Java 8 cible

### 11.1 Structure des packages

```
com.example.crm.t10
├── application/
│   ├── MajDtlivrService.java
│   └── CascadeLabo3628.java
├── domain/
│   ├── LivraisonRecord.java
│   ├── Livraison9994.java
│   └── DeliveryCodexNormalizer.java
├── port/
│   └── CdeFacRepository.java
└── adapter/
    ├── file/
    │   └── MajbdstatFileReader.java   (lecture fichier plat — impl. hors scope TDD unitaire)
    └── jdbc/
        └── RdbCdeFacRepository.java   (impl. JDBC — hors scope TDD unitaire)
```

### 11.2 LivraisonRecord.java

```java
package com.example.crm.t10.domain;

/**
 * Représente un enregistrement du fichier MAJBDSTAT*.DAT
 * (COPY DIRCOB:T10_DESC_FICMAJBDSTAT.LIB).
 *
 * datliv : date de livraison en format VMS ASCII "DD-MON-YYYY HH:MM:SS.CC"
 *          passée telle quelle à SYS$BINTIM (B-T10-01).
 * numcde : PIC 9(7) dans T10 (7 chiffres, différent de D05 qui est PIC 9(9)).
 */
public final class LivraisonRecord {

    private final String coddep;
    private final String codlab;
    private final int    numcde;
    private final int    numral;
    private final String datliv;

    public LivraisonRecord(String coddep, String codlab, int numcde, int numral, String datliv) {
        this.coddep = coddep;
        this.codlab = codlab;
        this.numcde = numcde;
        this.numral = numral;
        this.datliv = datliv;
    }

    public String getCoddep() { return coddep; }
    public String getCodlab() { return codlab; }
    public int    getNumcde() { return numcde; }
    public int    getNumral() { return numral; }
    public String getDatliv() { return datliv; }
}
```

### 11.3 Livraison9994.java

```java
package com.example.crm.t10.domain;

/**
 * Résultat de la détection d'une commande miroir labo 9994 (TST-3628-9994).
 *
 * numcde9994 = W-CDELAB(1:7) converti en entier  (B-T10-06)
 * numral9994 = W-CDELAB(8:1) converti en entier
 * La mise à jour 9994 utilise toujours CODDEP='CO' (hardcodé — B-T10-05).
 */
public final class Livraison9994 {

    private final int numcde9994;
    private final int numral9994;

    public Livraison9994(int numcde9994, int numral9994) {
        this.numcde9994 = numcde9994;
        this.numral9994 = numral9994;
    }

    public int getNumcde9994() { return numcde9994; }
    public int getNumral9994() { return numral9994; }
}
```

### 11.4 DeliveryCodexNormalizer.java

```java
package com.example.crm.t10.domain;

/**
 * Normalisation du code dépôt (B-T10-03).
 *
 * Règle : si CODDEP = "FO" → remplacer par "MO".
 * S'applique avant toute requête SQL, y compris le check 3628/9994.
 */
public final class DeliveryCodexNormalizer {

    private DeliveryCodexNormalizer() {}

    public static String normalize(String coddep) {
        return "FO".equals(coddep) ? "MO" : coddep;
    }
}
```

### 11.5 CdeFacRepository.java (port)

```java
package com.example.crm.t10.port;

import java.util.Optional;

/**
 * Port d'accès à E.CDE_FAC (BD_CRM) pour T10_MAJ_DTLIVR_BDCRM.
 *
 * findCdelab       → SELECT SUBSTRING(CDELAB FROM 1 FOR 8) FROM E.CDE_FAC
 *                    WHERE CODDEP=? AND CODLAB=? AND NUMCDE=? AND NUMRAL=?
 *                    LIMIT TO 1 ROW. Empty si '02000'.
 *
 * existsCdeFac9994 → même SELECT avec CODDEP='CO' CODLAB='9994' (B-T10-05).
 *
 * updateDtlivr     → UPDATE E.CDE_FAC SET DTLIVR=:datliv, FLAGLIV='O'
 *                    WHERE CODDEP=? AND CODLAB=? AND NUMCDE=? AND NUMRAL=?
 *                    L'adapter gère SET TRANSACTION READ WRITE + COMMIT (B-T10-07).
 *
 * updateDtlivr9994 → même UPDATE avec CODDEP='CO' CODLAB='9994' (B-T10-09).
 *                    datliv identique à la commande principale (B-T10-08).
 */
public interface CdeFacRepository {

    Optional<String> findCdelab(String coddep, String codlab, int numcde, int numral);

    boolean existsCdeFac9994(int numcde9994, int numral9994);

    void updateDtlivr(String coddep, String codlab, int numcde, int numral, String datliv);

    void updateDtlivr9994(int numcde9994, int numral9994, String datliv);
}
```

### 11.6 CascadeLabo3628.java

```java
package com.example.crm.t10.application;

import com.example.crm.t10.domain.Livraison9994;
import com.example.crm.t10.port.CdeFacRepository;

import java.util.Optional;

/**
 * Détection de la commande miroir labo 9994 (TST-3628-9994 dans T10).
 *
 * Algorithme :
 * 1. SELECT CDELAB(1:8) FROM E.CDE_FAC WHERE CODDEP=coddep AND CODLAB='3628'
 *    AND NUMCDE=numcde AND NUMRAL=numral LIMIT TO 1 ROW.
 * 2. Si trouvé ET W-CDELAB IS NUMERIC (tous chiffres) :
 *    numcde9994 = CDELAB[0..6] (7 chars), numral9994 = CDELAB[7] (1 char).
 * 3. Vérifier existence dans E.CDE_FAC avec CODDEP='CO' CODLAB='9994'.
 * 4. Si trouvé → retourner Optional<Livraison9994>.
 *
 * Si CDELAB non numérique → Optional.empty() (ignoré silencieusement — B-T10-04).
 */
public class CascadeLabo3628 {

    private final CdeFacRepository repo;

    public CascadeLabo3628(CdeFacRepository repo) {
        this.repo = repo;
    }

    /**
     * @param coddep coddep déjà normalisé (FO→MO appliqué en amont)
     * @param numcde numéro de commande (7 chiffres dans T10)
     * @param numral numéro de ralliement
     * @return Livraison9994 si une commande miroir CO/9994 existe, sinon empty
     */
    public Optional<Livraison9994> detecter(String coddep, int numcde, int numral) {
        Optional<String> cdelabOpt = repo.findCdelab(coddep, "3628", numcde, numral);
        if (!cdelabOpt.isPresent()) {
            return Optional.empty();
        }

        String cdelab = cdelabOpt.get();
        if (!isNumeric(cdelab)) {
            // B-T10-04 : CDELAB non numérique → lien 9994 ignoré silencieusement
            return Optional.empty();
        }

        int numcde9994 = Integer.parseInt(cdelab.substring(0, 7));
        int numral9994 = Integer.parseInt(cdelab.substring(7, 8));

        if (repo.existsCdeFac9994(numcde9994, numral9994)) {
            return Optional.of(new Livraison9994(numcde9994, numral9994));
        }
        return Optional.empty();
    }

    private static boolean isNumeric(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }
}
```

### 11.7 MajDtlivrService.java

```java
package com.example.crm.t10.application;

import com.example.crm.t10.domain.DeliveryCodexNormalizer;
import com.example.crm.t10.domain.Livraison9994;
import com.example.crm.t10.domain.LivraisonRecord;
import com.example.crm.t10.port.CdeFacRepository;

import java.util.Optional;

/**
 * Migration de TRAITEMENT-INFOS dans T10_MAJ_DTLIVR_BDCRM.COB.
 *
 * Par enregistrement :
 *   1. Normaliser coddep (FO→MO) — B-T10-03.
 *   2. Si codlab='3628' → détecter commande miroir 9994.
 *   3. UPDATE E.CDE_FAC SET DTLIVR=datliv, FLAGLIV='O'
 *      avec COMMIT par enregistrement (B-T10-07).
 *   4. Si CDE9994 → UPDATE 9994 avec même datliv (B-T10-08).
 *
 * Deadlock : délégué à l'adapter JDBC. En cas de deadlock,
 * l'adapter doit lever une RuntimeException (→ équivalent STOP RUN, B-T10-10).
 * Le service ne gère pas de retry.
 */
public class MajDtlivrService {

    private final CdeFacRepository repo;
    private final CascadeLabo3628  cascade;

    public MajDtlivrService(CdeFacRepository repo) {
        this.repo    = repo;
        this.cascade = new CascadeLabo3628(repo);
    }

    /** Constructeur pour injection du cascade (tests). */
    public MajDtlivrService(CdeFacRepository repo, CascadeLabo3628 cascade) {
        this.repo    = repo;
        this.cascade = cascade;
    }

    /**
     * Traite un enregistrement de livraison.
     *
     * @param record enregistrement brut du fichier MAJBDSTAT
     */
    public void traiterRecord(LivraisonRecord record) {
        // Étape 1 : normalisation coddep FO→MO (B-T10-03)
        String coddep = DeliveryCodexNormalizer.normalize(record.getCoddep());
        String codlab = record.getCodlab();
        int    numcde = record.getNumcde();
        int    numral = record.getNumral();
        String datliv = record.getDatliv();

        // Étape 2 : détection cascade 3628/9994
        Optional<Livraison9994> livraison9994 = Optional.empty();
        if ("3628".equals(codlab)) {
            livraison9994 = cascade.detecter(coddep, numcde, numral);
        }

        // Étape 3 : UPDATE principal (COMMIT par enregistrement via adapter)
        repo.updateDtlivr(coddep, codlab, numcde, numral, datliv);

        // Étape 4 : UPDATE 9994 si cascade détectée (même datliv — B-T10-08)
        if (livraison9994.isPresent()) {
            Livraison9994 l = livraison9994.get();
            repo.updateDtlivr9994(l.getNumcde9994(), l.getNumral9994(), datliv);
        }
    }
}
```

---

## 12. Stratégie TDD

### 12.1 Cycle Red / Green / Refactor

**Itération 1 — DeliveryCodexNormalizer (règle pure)**

- RED : écrire `DeliveryCodexNormalizerTest` avec les 6 cas (FO→MO, CO inchangé, MO inchangé, code vide, fo minuscule)
- GREEN : implémenter le ternaire `"FO".equals(coddep) ? "MO" : coddep`
- REFACTOR : rendre la classe `final` avec constructeur privé

**Itération 2 — CascadeLabo3628 (logique de résolution 9994)**

- RED : écrire `CascadeLabo3628Test` avec stub `StubRepo` — 7 cas
- GREEN : implémenter la logique en deux SELECT via le port
- RED : `cdelab_nonNumerique_retourneEmptySilencieusement()` — rouge si isNumeric absent
- GREEN : ajouter `isNumeric()` en méthode privée
- REFACTOR : extraire les indices `0,7` / `7,8` en constantes nommées

**Itération 3 — MajDtlivrService (orchestration principale)**

- RED : `coddep_fo_estRemappeEnMo_avantUpdate()` — rouge
- GREEN : appeler `DeliveryCodexNormalizer.normalize()` en premier
- RED : `labo3628_avec9994_deuxUpdates()` — rouge
- GREEN : implémenter le branchement `if ("3628".equals(codlab))`
- RED : `fo_plus_labo3628_remappage_avant_cascade()` — vérifier ordre des opérations
- GREEN : confirmer que `normalize()` est appelé avant `cascade.detecter()`

**Itération 4 — Tests golden master**

Avant de connecter la vraie base : préparer un jeu de données de référence issu d'une exécution COBOL connue. Comparer ligne à ligne les `UPDATE` produits par Java avec ceux journalisés par le COBOL.

### 12.2 Stubs pour les tests

```java
private static class StubRepo implements CdeFacRepository {
    final List<String> updatesCalls  = new ArrayList<>();
    final List<String> updates9994   = new ArrayList<>();
    final Map<String, String>  cdelabs    = new HashMap<>();
    final Map<String, Boolean> exist9994  = new HashMap<>();

    @Override
    public Optional<String> findCdelab(String coddep, String codlab, int numcde, int numral) {
        return Optional.ofNullable(cdelabs.get(coddep + "|" + codlab + "|" + numcde + "|" + numral));
    }

    @Override
    public boolean existsCdeFac9994(int numcde9994, int numral9994) {
        return Boolean.TRUE.equals(exist9994.get(numcde9994 + "|" + numral9994));
    }

    @Override
    public void updateDtlivr(String coddep, String codlab, int numcde, int numral, String datliv) {
        updatesCalls.add(coddep + "|" + codlab + "|" + numcde + "|" + numral + "|" + datliv);
    }

    @Override
    public void updateDtlivr9994(int numcde9994, int numral9994, String datliv) {
        updates9994.add(numcde9994 + "|" + numral9994 + "|" + datliv);
    }
}
```

---

## 13. Matrice des tests

### 13.1 DeliveryCodexNormalizerTest (6 tests)

| ID | Méthode de test | Règle testée |
|---|---|---|
| DN-01 | `fo_estRemplaceParMo` | "FO" → "MO" |
| DN-02 | `co_estInchange` | "CO" → "CO" |
| DN-03 | `mo_estInchange` | "MO" → "MO" |
| DN-04 | `autreCode_estInchange` | "XX", "DP" → inchangés |
| DN-05 | `codeVide_estInchange` | "" → "" |
| DN-06 | `fo_minuscule_nestPasRemappe` | "fo" → "fo" (case-sensitive, B-T10-03) |

### 13.2 CascadeLabo3628Test (7 tests) — code JUnit inline pour les cas clés

```java
@Test
public void cdelab_numerique_9994Existe_retournelivraison9994() {
    // "12345671" → numcde9994=1234567, numral9994=1
    repo.addCdelab("MO", "3628", 9990001, 0, "12345671");
    repo.addExist9994(1234567, 1);

    Optional<Livraison9994> result = cascade.detecter("MO", 9990001, 0);

    assertTrue(result.isPresent());
    assertEquals(1234567, result.get().getNumcde9994());
    assertEquals(1, result.get().getNumral9994());
}

@Test
public void cdelab_nonNumerique_retourneEmptySilencieusement() {
    // B-T10-04 : CDELAB avec chars non numériques → ignoré silencieusement
    repo.addCdelab("MO", "3628", 9990003, 0, "ABC12345");

    Optional<Livraison9994> result = cascade.detecter("MO", 9990003, 0);

    assertFalse(result.isPresent());
}

@Test
public void cdelab_avecEspace_nonNumerique_retourneEmpty() {
    // Espace = non numérique → lien 9994 ignoré
    repo.addCdelab("MO", "3628", 9990004, 0, "1234567 ");

    Optional<Livraison9994> result = cascade.detecter("MO", 9990004, 0);

    assertFalse(result.isPresent());
}

@Test
public void extraction_numcde9994_et_numral9994_depuis_cdelab() {
    // "98765432" → numcde9994=9876543, numral9994=2 (B-T10-06)
    repo.addCdelab("CO", "3628", 5555555, 1, "98765432");
    repo.addExist9994(9876543, 2);

    Optional<Livraison9994> result = cascade.detecter("CO", 5555555, 1);

    assertTrue(result.isPresent());
    assertEquals(9876543, result.get().getNumcde9994());
    assertEquals(2, result.get().getNumral9994());
}

@Test
public void coddepTransmisTelQuelFindCdelab() {
    // Le service utilise le coddep reçu (déjà normalisé FO→MO en amont, B-T10-03)
    repo.addCdelab("MO", "3628", 1111111, 0, "22222220");
    repo.addExist9994(2222222, 0);

    // Appel avec "MO" → trouvé
    assertTrue(cascade.detecter("MO", 1111111, 0).isPresent());
    // Appel avec "FO" → non trouvé (normalisation non faite dans cascade)
    assertFalse(cascade.detecter("FO", 1111111, 0).isPresent());
}
```

### 13.3 MajDtlivrServiceTest (8 tests) — code JUnit inline pour les cas clés

```java
@Test
public void coddep_fo_estRemappeEnMo_avantUpdate() {
    // B-T10-03 : FO → MO avant tout SQL
    LivraisonRecord rec = new LivraisonRecord("FO", "0001", 1111111, 0,
            "08-AUG-2024 00:00:00.00");
    serviceSans9994.traiterRecord(rec);

    assertEquals(1, repo.updatesCalls.size());
    assertTrue("coddep doit être MO",
            repo.updatesCalls.get(0).startsWith("MO|"));
}

@Test
public void labo3628_avec9994_deuxUpdates() {
    // B-T10-07 + B-T10-08 : deux UPDATEs, même datliv
    LivraisonRecord rec = new LivraisonRecord("CO", "3628", 6666666, 0,
            "20-MAR-2024 00:00:00.00");
    serviceAvec9994.traiterRecord(rec);

    assertEquals(1, repo.updatesCalls.size());
    assertTrue(repo.updatesCalls.get(0).startsWith("CO|3628|6666666|0|"));
    assertEquals(1, repo.updates9994.size());
    // numcde9994=9876543, numral9994=1 (définis dans StubCascade)
    assertEquals("9876543|1|20-MAR-2024 00:00:00.00", repo.updates9994.get(0));
}

@Test
public void update9994_utilise_memeDatliv_queUpdatePrincipal() {
    // B-T10-08 : même W-DATBIN pour les deux updates
    String datliv = "25-DEC-2023 10:00:00.00";
    LivraisonRecord rec = new LivraisonRecord("CO", "3628", 7777777, 0, datliv);
    serviceAvec9994.traiterRecord(rec);

    assertTrue("datliv principal", repo.updatesCalls.get(0).endsWith(datliv));
    assertTrue("datliv 9994",      repo.updates9994.get(0).endsWith(datliv));
}

@Test
public void fo_plus_labo3628_remappage_avant_cascade() {
    // B-T10-03 : FO→MO avant TST-3628-9994
    final List<String> codeDepotRecu = new ArrayList<>();
    CascadeLabo3628 cascadeCapture = new CascadeLabo3628(repo) {
        @Override
        public Optional<Livraison9994> detecter(String coddep, int numcde, int numral) {
            codeDepotRecu.add(coddep);
            return Optional.empty();
        }
    };

    MajDtlivrService svc = new MajDtlivrService(repo, cascadeCapture);
    svc.traiterRecord(new LivraisonRecord("FO", "3628", 8888888, 0,
            "08-AUG-2024 00:00:00.00"));

    assertEquals(1, codeDepotRecu.size());
    assertEquals("MO", codeDepotRecu.get(0));  // FO normalisé en MO avant cascade
}

@Test
public void cascade_seulementAppeleePourLabo3628() {
    // La cascade n'est appelée que si codlab='3628'
    final List<String> cascadeAppels = new ArrayList<>();
    CascadeLabo3628 cascadeCapture = new CascadeLabo3628(repo) {
        @Override
        public Optional<Livraison9994> detecter(String coddep, int numcde, int numral) {
            cascadeAppels.add("called");
            return Optional.empty();
        }
    };

    MajDtlivrService svc = new MajDtlivrService(repo, cascadeCapture);
    svc.traiterRecord(new LivraisonRecord("CO", "9999", 1234567, 0,
            "08-AUG-2024 00:00:00.00"));

    assertTrue(cascadeAppels.isEmpty());
}

@Test
public void nonLabo3628_aucunUpdate9994() {
    LivraisonRecord rec = new LivraisonRecord("CO", "0001", 4444444, 0,
            "08-AUG-2024 00:00:00.00");
    serviceSans9994.traiterRecord(rec);

    assertTrue(repo.updates9994.isEmpty());
}

@Test
public void update_principal_utilise_bons_parametres() {
    LivraisonRecord rec = new LivraisonRecord("CO", "LABO", 3333333, 1,
            "15-JAN-2024 14:30:00.00");
    serviceSans9994.traiterRecord(rec);

    assertEquals(1, repo.updatesCalls.size());
    assertEquals("CO|LABO|3333333|1|15-JAN-2024 14:30:00.00",
            repo.updatesCalls.get(0));
}

@Test
public void coddep_co_estInchange() {
    LivraisonRecord rec = new LivraisonRecord("CO", "0001", 2222222, 0,
            "08-AUG-2024 00:00:00.00");
    serviceSans9994.traiterRecord(rec);

    assertTrue(repo.updatesCalls.get(0).startsWith("CO|"));
}
```

### 13.4 Table complète des tests

| ID | Classe de test | Scénario | Résultat attendu |
|---|---|---|---|
| T-T10-01 | DeliveryCodexNormalizerTest | FO → MO | normalize("FO")="MO" |
| T-T10-02 | DeliveryCodexNormalizerTest | CO inchangé | normalize("CO")="CO" |
| T-T10-03 | DeliveryCodexNormalizerTest | fo minuscule non mappé | normalize("fo")="fo" |
| T-T10-04 | CascadeLabo3628Test | CDELAB numérique + 9994 existe | Optional présent, numcde9994/numral9994 corrects |
| T-T10-05 | CascadeLabo3628Test | CDELAB non numérique | Optional.empty() silencieux |
| T-T10-06 | CascadeLabo3628Test | CDELAB avec espace | Optional.empty() |
| T-T10-07 | CascadeLabo3628Test | 9994 absent | Optional.empty() |
| T-T10-08 | CascadeLabo3628Test | Extraction positions (1:7) et (8:1) | numcde9994 et numral9994 corrects |
| T-T10-09 | CascadeLabo3628Test | Commande 3628 absente | Optional.empty() |
| T-T10-10 | CascadeLabo3628Test | coddep transmis tel quel | findCdelab reçoit le coddep normalisé |
| T-T10-11 | MajDtlivrServiceTest | FO → MO avant UPDATE | updatesCalls commence par "MO|" |
| T-T10-12 | MajDtlivrServiceTest | CO inchangé | updatesCalls commence par "CO|" |
| T-T10-13 | MajDtlivrServiceTest | Update principal paramètres corrects | chaîne complète coddep|codlab|numcde|numral|datliv |
| T-T10-14 | MajDtlivrServiceTest | Non-3628 → aucun update 9994 | updates9994 vide |
| T-T10-15 | MajDtlivrServiceTest | 3628 sans 9994 → 1 seul update | updates9994 vide |
| T-T10-16 | MajDtlivrServiceTest | 3628 avec 9994 → 2 updates | updatesCalls.size=1, updates9994.size=1 |
| T-T10-17 | MajDtlivrServiceTest | Même datliv pour les deux updates | datliv identique dans les deux listes |
| T-T10-18 | MajDtlivrServiceTest | FO + 3628 → cascade reçoit "MO" | codeDepotRecu contient "MO" |
| T-T10-19 | MajDtlivrServiceTest | Cascade appelée uniquement pour 3628 | cascadeAppels vide si codlab != "3628" |
| T-T10-20 | DeliveryCodexNormalizerTest | 4 autres codes inchangés | XX, DP, etc. |
| T-T10-21 | CascadeLabo3628Test | Extraction numcde9994/numral9994 sur 8 chars | "98765432" → 9876543/2 |

---

## 14. Définition of Done

- [ ] Tous les 21 tests (6 `DeliveryCodexNormalizerTest` + 7 `CascadeLabo3628Test` + 8 `MajDtlivrServiceTest`) passent au vert
- [ ] Aucun test n'accède à la base de données réelle (stubs uniquement)
- [ ] B-T10-01 à B-T10-13 : chaque comportement critique est couvert par au moins un test nominatif
- [ ] `DeliveryCodexNormalizer.normalize("fo")` retourne `"fo"` (case-sensitive vérifié)
- [ ] `CascadeLabo3628.detecter()` retourne `Optional.empty()` si CDELAB contient un espace ou une lettre
- [ ] Les positions d'extraction sont correctes : `substring(0,7)` → numcde9994, `substring(7,8)` → numral9994
- [ ] `updateDtlivr9994()` reçoit la même valeur `datliv` que `updateDtlivr()` (B-T10-08)
- [ ] La cascade n'est appelée que si `codlab.equals("3628")`
- [ ] Le remapping `FO→MO` est appliqué avant l'appel à `cascade.detecter()` (B-T10-03)
- [ ] `CVT-MONTH-ASC-NUM`, `CVT-MONTH-NUM-ASC` et les variables de date vestigiales ne sont pas implémentés (B-T10-12/13)
- [ ] Code review : aucune constante VMS dans les packages `domain` et `application`
- [ ] Couverture Jacoco ≥ 90 % sur `MajDtlivrService`, `CascadeLabo3628`, `DeliveryCodexNormalizer`
- [ ] Javadoc sur tous les types publics avec référence aux comportements critiques (B-T10-xx)
- [ ] Note sur `W-NUMCDE PIC 9(7)` documentée : T10 utilise des numéros de commande sur 7 chiffres, D05 sur 9 — ne pas mélanger les types entre les deux modules

---

## 15. Ordre de réalisation recommandé

1. **`DeliveryCodexNormalizer`** — règle FO→MO pure, sans dépendance. Tests : T-T10-01, T-T10-02, T-T10-03, T-T10-20.
2. **`LivraisonRecord` + `Livraison9994`** — value objects immutables. Tests : construction/getters.
3. **`CdeFacRepository`** (interface seule) + `StubRepo` — fondation des tests unitaires.
4. **`CascadeLabo3628`** — logique TST-3628-9994 complète. Tests : T-T10-04 à T-T10-10, T-T10-21.
5. **`MajDtlivrService`** — orchestration principale (normalisation + cascade + updates). Tests : T-T10-11 à T-T10-19.
6. **`MajbdstatFileReader`** (adapter lecture fichier) — parser le format MAJBDSTAT. Tests d'intégration avec fichiers de test.
7. **`RdbCdeFacRepository`** (adapter JDBC) — implémentation des 4 méthodes du port. Tests d'intégration avec vraie base. Vérifier la conversion `datliv` (String ASCII VMS → type DTLIVR de la table).
8. **Golden master** — exécuter le COBOL original sur un jeu de données de référence, comparer avec la sortie Java. Critère de parité comportementale pour les deux types d'UPDATE (principal et 9994).
