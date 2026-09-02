# Analyse Technique — Programme COBOL `D02_EXTCDE_CRMCSP1`

> **Objectif de ce document** : fournir une référence autonome et exhaustive permettant à un développeur Java 8 de réécrire ce programme sans jamais consulter le source COBOL original. Chaque comportement, chaque subtilité métier et chaque cas particulier sont documentés ici avec précision.

---

## Table des matières

1. [Identité du programme](#1-identité-du-programme)
2. [Fichiers d'entrée et de sortie](#2-fichiers-dentrée-et-de-sortie)
3. [Paramètres d'entrée](#3-paramètres-dentrée)
4. [Format des enregistrements de sortie](#4-format-des-enregistrements-de-sortie)
5. [Variables de travail clés](#5-variables-de-travail-clés)
6. [Base de données et SQL](#6-base-de-données-et-sql)
7. [Appels externes VMS et leurs équivalents Java](#7-appels-externes-vms-et-leurs-équivalents-java)
8. [Gestion de SQLSTATE](#8-gestion-de-sqlstate)
9. [Flux d'exécution complet](#9-flux-dexécution-complet)
10. [Comportements critiques à préserver en Java](#10-comportements-critiques-à-préserver-en-java)
11. [Séquence des enregistrements dans les fichiers de sortie](#11-séquence-des-enregistrements-dans-les-fichiers-de-sortie)
12. [Architecture Java 8 recommandée](#12-architecture-java-8-recommandée)

---

## 1. Identité du programme

| Attribut | Valeur |
| --- | --- |
| **Nom** | `D02_EXTCDE_CRMCSP1` |
| **Langage** | RDB/COBOL (COBOL avec SQL Oracle Rdb embarqué, exécuté sous OpenVMS) |
| **Auteur** | AG.DUROURE |
| **Date de création** | 16 juin 2015 |
| **Dérivé de** | `D02_EXTCDE_HOPITAUX1` |
| **Rôle fonctionnel** | Génération de fichiers de confirmation d'expédition à largeur fixe (positionnelle) pour un système de dépôt pharmaceutique (CSP) |

### Description fonctionnelle

Le programme extrait des commandes (`CDE`) en statut `CRV` (confirmées reçues validées) d'une base Oracle Rdb, les enrichit avec des données de lignes de commande (`CDL`), d'articles (`ART`), de clients (`CLI`), de paramètres (`PAR`) et de messages (`MES`), puis produit des fichiers de transmission à format positionnel fixe (197 caractères par enregistrement) à destination de deux dépôts : **CO** et **MO**.

---

## 2. Fichiers d'entrée et de sortie

| Nom logique | Nom physique (RMS) | Mode | Longueur enreg. | Description |
| --- | --- | --- | --- | --- |
| `FIC-MESSAGES` | `RMS_MSG` | INPUT | via COPY | Libellés des messages d'erreur |
| `FIC-ANOMALIES` | `RMS_ANO` | OUTPUT | via COPY | Enregistrements d'anomalies |
| `FIC-TRANSMITCO` | `RMS_TRANSCO` | OUTPUT | 197 caractères | Fichier de transmission pour le dépôt CO |
| `FIC-TRANSMITMO` | `RMS_TRANSMO` | OUTPUT | 197 caractères | Fichier de transmission pour le dépôt MO |
| `FIC-MAJ` | `RMS_MAJ` | OUTPUT | 23 caractères | Fichier de mise à jour des commandes traitées |

Tous les fichiers partagent la variable `CODE-STATUS` (déclarée `EXTERNAL PIC XX`) pour le code retour d'opération fichier.

### Comportement en cas d'erreur fichier

- **Erreur à l'ouverture** : appel de `FIN-ANOOPEN` → `D00_MSGGES` → `STOP RUN`
- **Erreur à la fermeture** : appel de `FIN-ANOFERME` → `D00_MSGGES` → `STOP RUN`
- **Erreur en écriture MAJ** : saut immédiat à `FIN-ANOFERME`

---

## 3. Paramètres d'entrée

Les paramètres sont lus depuis le copybook `PROC_COM` dans la structure `PARAM` :

| Nom champ | Format COBOL | Contenu |
| --- | --- | --- |
| `P-CODDEP` | `PIC XX` | Code dépôt (2 caractères) |
| `P-CODLAB` | `PIC XXXX` | Code laboratoire à traiter (4 caractères) |
| `P-CODREP` | `PIC X` | Mode reprise : `'R'` = plage de dates, sinon = mode normal |
| `P-DATDEB` | `PIC X(23)` | Date de début en format ASCII VMS `"DD-MON-YYYY HH:MI:SS.CC"` |
| `P-DATFIN` | `PIC X(23)` | Date de fin en format ASCII VMS `"DD-MON-YYYY HH:MI:SS.CC"` |
| `P-QUERETOUR` | `PIC X` | Code retour d'environnement |

### Validation des paramètres

```
SI P-CODREP = 'R'
  ET (P-DATDEB = ESPACES OU P-DATFIN = ESPACES)
ALORS STOP RUN immédiat
```

En Java : lever une `IllegalArgumentException` ou un retour d'erreur immédiat avant tout traitement.

---

## 4. Format des enregistrements de sortie

### Structure générale : `ENREG-TRANSMIT` (197 caractères)

```
Positions  1- 6  : ENR-TYPMES    PIC X(6)   — type d'enregistrement
Position      7  : ENR-FILLER    PIC X      — séparateur (toujours ESPACES)
Positions  8-197 : corps (190 caractères)   — variant selon TYPMES (REDEFINES)
```

Le corps de 190 caractères est défini en COBOL par un champ principal `ENR-DEBCDE` et cinq REDEFINES. Chaque REDEFINES correspond à un type de message différent.

---

### 4.1 DEBCDE — Début de commande (TYPMES = `"DEBCDE"`)

Corps du record (190 caractères) :

| Position dans le corps | Longueur | Champ | Valeur fixe | Java / SQL |
| --- | --- | --- | --- | --- |
| 1–35 | 35 | `ENR-DEBCDE-EMETEUR` | `"183 CSP"` complété à 35 par des espaces à droite | `String emeteur = "183 CSP"` — littéral |
| 36–70 | 35 | `ENR-DEBCDE-RECEPTE` | `"183 CSP"` complété à 35 par des espaces à droite | `String recepte = "183 CSP"` — littéral |
| 71 | 1 | `ENR-DEBCDE-TEST` | `"P"` (flag production) | `String test = "P"` — littéral |
| 72–171 | 100 | `ENR-DEBCDE-FILLER` | Espaces | espaces — INITIALIZE |
| 172–190 | 19 | `FILLER` | Espaces | espaces — INITIALIZE |

**Total corps** : 35+35+1+100+19 = 190 ✓

---

### 4.2 REFCDE — Référence commande (TYPMES = `"REFCDE"`)

Corps du record (190 caractères) — REDEFINES ENR-DEBCDE :

| Position dans le corps | Longueur | Champ | Contenu | Java / SQL |
| --- | --- | --- | --- | --- |
| 1–6 | 6 | `ENR-REFCDE-DATCDE` | Date commande en format `AAMMJJ` (= YYMMDD) | `String datcde` — `D.CDE.DATCDE` converti → `ficDatecde.substring(2,8)` |
| 7 | 1 | `FILLER` | Espace | `' '` |
| 8–29 | 22 | `ENR-REFCDE-CDELAB` | Référence laboratoire de la commande | `String cdelab` — `D.CDE.CDELAB` |
| 30 | 1 | `FILLER` | Espace | `' '` |
| 31–65 | 35 | `ENR-REFCDE-REFCDE` | Référence commande `W-REFCDE` | `String refcde` — `D.CDE.REFCDE` (hôte `:w-refcde`) |
| 66 | 1 | `FILLER` | Espace | `' '` |
| 67–68 | 2 | `ENR-REFCDE-TYPCDE` | Type de commande (`TYPCDE` de `CDE`) | `String typcde` — `D.CDE.TYPCDE` |
| 69 | 1 | `FILLER` | Espace | `' '` |
| 70–75 | 6 | `ENR-REFCDE-CLICSP` | `PIC 9(06)` — code client CSP (numérique) | `long clicsp` — `D.CDE.CLICSP` |
| 76 | 1 | `FILLER` | Espace | `' '` |
| 77–111 | 35 | `ENR-REFCDE-NOMLIV` | **Voir astuce NOMLIV ci-dessous (T3)** | `String nomliv` — calculé depuis `clicsp` (voir T3 / §8 du guide) |
| 112 | 1 | `ENR-REFCDE-FILLER` | Espace | `' '` |
| 113–115 | 3 | `ENR-REFCDE-CODOPE` | Code opération (`CODOPE` de `CDE`) | `String codope` — `D.CDE.CODOPE` |
| 116 | 1 | `FILLER` | Espace | `' '` |
| 117–126 | 10 | `ENR-REFCDE-NUMDOC` | Numéro de document issu de `P.DOCENT`, ou espaces | `String numdoc` — `P.DOCENT.NUMDOC` ou espaces |
| 127 | 1 | `FILLER` | Espace | `' '` |
| 128 | 1 | `ENR-REFCDE-CDESAISIE` | `CDESAISIE` de `CDE` | `String cdesaisie` — `D.CDE.CDESAISIE` |
| 129 | 1 | `FILLER` | Espace | `' '` |
| 130–152 | 23 | `ENR-REFCDE-DATREC` | Date réception ASCII VMS complète (23 chars) | `String datrec` — `D.CDE.DATREC` converti ASCII VMS 23 cars |
| 153 | 1 | `FILLER` | Espace | `' '` |
| 154 | 1 | `ENR-REFCDE-TRAFIC` | `TRAFIC` de `CDE`, ou `"N"` si vide | `String trafic` — `D.CDE.TRAFIC` ou `"N"` |
| 155 | 1 | `FILLER` | Espace | `' '` |
| 156–163 | 8 | `ENR-REFCDE-DATEBP` | Date livraison prévue `YYYYMMDD` | `String datebp` — `D.CDE.DATEBP` converti YYYYMMDD, ou date du jour si NULL |
| 164 | 1 | `FILLER` | Espace | `' '` |
| 165–171 | 7 | `ENR-REFCDE-CODSAI` | `CODSAI` de `CDE` | `String codsai` — `D.CDE.CODSAI` |
| 172 | 1 | `FILLER` | Espace | `' '` |
| 173–176 | 4 | `ENR-REFCDE-CODSTR` | `CODSTR` de `CDE` | `String codstr` — `D.CDE.CODSTR` |
| 177 | 1 | `FILLER` | Espace | `' '` |
| 178–181 | 4 | `ENR-REFCDE-STREXP` | `STREXP` de `CDE` | `String strexp` — `D.CDE.STREXP` |
| 182 | 1 | `FILLER` | Espace | `' '` |
| 183–190 | 8 | `ENR-REFCDE-CODREP` | `CODREP` de `CDE` | `String codrep` — `D.CDE.CODREP` |

**Total corps** : 6+1+22+1+35+1+2+1+6+1+35+1+3+1+10+1+1+1+23+1+1+1+8+1+7+1+4+1+4+1+8 = 190 ✓

---

### 4.3 LINTXT — Ligne texte commentaire (TYPMES = `"LINTXT"`)

Corps du record (190 caractères) — REDEFINES ENR-DEBCDE :

| Position dans le corps | Longueur | Champ | Contenu | Java / SQL |
| --- | --- | --- | --- | --- |
| 1–114 | 114 | `ENR-LINTXT-MESSAGE` | Commentaire issu de la table `MES` (champ `COMMENT`) | `String message` — `D.MES.COMMENT` (via `W-COMMENT`) |
| 115–190 | 76 | (implicite) | Espaces | espaces — INITIALIZE |

---

### 4.4 TXTCDE — Texte libre (TYPMES = `"TXTCDE"`)

Corps du record (190 caractères) — REDEFINES ENR-DEBCDE :

| Position dans le corps | Longueur | Champ | Contenu | Java / SQL |
| --- | --- | --- | --- | --- |
| 1–80 | 80 | `ENR-TXTCDE-MESSAGE` | Texte libre (horaire ou contact) | `String message` — `D.CDL.LIBELL` (CODART=`QU00013` ou `QU000132`) |
| 81 | 1 | `FILLER` | Espace | `' '` |
| 82–83 | 2 | `ENR-TXTCDE-TYPDOC` | `"RT"` pour horaire, `"BL"` pour contact | `"RT"` ou `"BL"` — littéral |
| 84–190 | 107 | (implicite) | Espaces | espaces — INITIALIZE |

---

### 4.5 LINCDE — Ligne de commande (TYPMES = `"LINCDE"`)

Corps du record (190 caractères) — REDEFINES ENR-DEBCDE :

| Position dans le corps | Longueur | Champ | Contenu | Java / SQL |
| --- | --- | --- | --- | --- |
| 1–10 | 10 | `ENR-LINCDE-CODART` | Code article | `String codart` — `LigneCumulee.codart` ← `D.CDL.CODART` |
| 11 | 1 | `FILLER` | Espace | `' '` |
| 12–18 | 7 | `ENR-LINCDE-QTCCDE` | `PIC 9(07)` — quantité livrée (**reçoit `TCDL-QTELIV`**, voir T7) | `long qteliv` — somme `D.CDL.QTLCDE` |
| 19 | 1 | `FILLER` | Espace | `' '` |
| 20–26 | 7 | `ENR-LINCDE-QTCGRT` | `PIC 9(07)` — quantité gratuite (`TCDL-QTCGRT`) | `long qtcgrt` — somme `D.CDL.QTCGRT + QTCECH` |
| 27 | 1 | `FILLER` | Espace | `' '` |
| 28–62 | 35 | `ENR-LINCDE-LIBELL` | **Toujours ESPACES** (forcé systématiquement) | espaces — forcé ; jamais `D.ART.LIBELL` |
| 63 | 1 | `FILLER` | Espace | `' '` |
| 64–67 | 4 | `ENR-LINCDE-CODLABLAB` | Code laboratoire | `String codlablab` — `LigneCumulee.codlablab` ← `D.CDL.CODLAB` |
| 68–105 | 38 | `FILLER` | Espaces (rembourrage final) | espaces |

---

### 4.6 FINCDE — Fin de groupe commande (TYPMES = `"FINCDE"`)

Corps du record (190 caractères) — REDEFINES ENR-DEBCDE :

| Position dans le corps | Longueur | Champ | Contenu | Java / SQL |
| --- | --- | --- | --- | --- |
| 1–8 | 8 | `ENR-FINCDE-SUMQTE` | `PIC 9(08)` — somme des quantités pour ce dépôt | `long sumqte` — `W-SUMQTE-CO`/`W-SUMQTE-MO` (cumul `D.CDL.QTLCDE`) |
| 9 | 1 | `FILLER` | Espace | `' '` |
| 10–17 | 8 | `ENR-FINCDE-NBLIG` | `PIC 9(08)` — nombre de lignes pour ce dépôt | `long nblig` — `W-NB-CDL-CO`/`W-NB-CDL-MO` (compteur LINCDE écrits) |
| 18–114 | 97 | `FILLER` | Espaces | espaces |

---

### 4.7 FINMES — Fin de message global (TYPMES = `"FINMES"`)

Corps du record (190 caractères) — REDEFINES ENR-DEBCDE :

| Position dans le corps | Longueur | Champ | Contenu | Java / SQL |
| --- | --- | --- | --- | --- |
| 1–8 | 8 | `ENR-FINMES-NBCDE` | `PIC 9(08)` — nombre total de commandes transmises | `long nbcde` — `NB-CDE-CO` ou `NB-CDE-MO` |
| 9–105 | 97 | `FILLER` | Espaces | espaces |

---

## 5. Variables de travail clés

### 5.1 Compteurs et indicateurs

| Variable COBOL | Format | Rôle |
| --- | --- | --- |
| `NB-CDL` | `PIC 9999` | Nombre total d'entrées dans `TABLE-CDL` (incrémenté **2 fois** par groupe CDL) |
| `W-NB-CDL-CO` | `PIC 9999` | Nombre de lignes CDL depot CO par commande (réinitialisé dans `STOCK-LIG-DESADV`) |
| `W-NB-CDL-MO` | `PIC 9999` | Nombre de lignes CDL depot MO par commande (réinitialisé dans `STOCK-LIG-DESADV`) |
| `NB-CDE` | `PIC 9999` | Nombre total de commandes traitées |
| `NB-CDE-CO` | `PIC 9999` | Commandes ayant des lignes CO |
| `NB-CDE-MO` | `PIC 9999` | Commandes ayant des lignes MO |
| `IND-CDL` | `PIC 9999` | Index courant dans `TABLE-CDL` lors de l'écriture |
| `W-SUMQTE-CO` | `PIC 9(10)` | Somme des quantités CO par groupe laboratoire |
| `W-SUMQTE-MO` | `PIC 9(10)` | Somme des quantités MO par groupe laboratoire |
| `PREV-CODDEP` | `PIC X(2)` | Dépôt précédent (détermine quel fichier reçoit FINCDE) |
| `PREV-CODLAB` | `PIC X(4)` | Laboratoire précédent (déclenche l'écriture de REFCDE) |

### 5.2 Indicateurs booléens (niveaux 88)

| Variable flag | Valeur 88 | Signification |
| --- | --- | --- |
| `FLAG-TROUVE` | `TROUVE` | Indicateur général "trouvé" |
| `FLAG-FIN-CDE` | `FIN-CDE` | Fin du curseur `CURCDE` ou `CURCDE_R` |
| `FLAG-FIN-CDL` | `FIN-CDL` | Fin du curseur `CURCDL` |
| `FLAG-FIN-RECHERCHE-DIV` | `FIN-RECHERCHE-DIV` | Fin du curseur `CURDIV` |
| `FLAG-BL-TROUVE` | `BL-TROUVE` | Message BL trouvé dans `MES` |
| `FLAG-RT-TROUVE` | `RT-TROUVE` | Message RT trouvé dans `MES` |
| `FLAG-ERREUR` | `ERREUR` | Indicateur d'erreur |
| `FLAG-NOPAR` | `NOPAR` | Aucun paramètre trouvé |
| `FLAG-CDE-NONFAC` | `CDE-NONFAC` | Commande non-facturée |

### 5.3 Variables de date

| Variable | Format COBOL | Rôle |
| --- | --- | --- |
| `W-DATE-SYS` | `PIC S9(11)V9(7) COMP` | Date courante binaire VMS |
| `W-DATE-VMS` | `PIC S9(11)V9(7) COMP` | Date courante binaire VMS (référence principale) |
| `W-DATE-NULL` | `PIC S9(11)V9(7) COMP` | Date binaire VMS pour "17-NOV-1858 00:00:00.00" (date zéro) |
| `W-DATE-ASCII` | `PIC X(23)` | Date courante en ASCII VMS |
| `W-DATE-ZERO` | `PIC X(23)` | Valeur fixe `"17-NOV-1858 00:00:00.00"` (date de référence zéro VMS) |
| `W-DATDEB` | `PIC S9(11)V9(7) COMP` | Date début de la plage de reprise (mode R) |
| `W-DATFIN` | `PIC S9(11)V9(7) COMP` | Date fin de la plage de reprise (mode R) |
| `PARAM-DATE-BIN` | `PIC S9(11)V9(7) COMP` | Date binaire pour conversion en `YYYYMMDD` |
| `PARAM-DATE-TXT` | Groupe : `DAT-AA PIC XXXX` + `DAT-MM PIC XX` + `DAT-JJ PIC XX` | Date courante en `YYYYMMDD` (8 caractères) |

### 5.4 Structure TABLE-CDL (tableau de 1 000 entrées)

```
TAB-CDL OCCURS 1000 :
  TCDL-NUMLIG    PIC 9(4)     — numéro de ligne
  TCDL-CODART    PIC X(10)    — code article
  TCDL-ARTLAB    PIC X(15)    — libellé laboratoire de l'article
  TCDL-ARTSAI    PIC X(07)    — code article saisie
  TCDL-LIBELL    PIC X(35)    — libellé article
  TCDL-ARTCIP    PIC X(7)     — code CIP article
  TCDL-LOTFAB    PIC X(12)    — numéro de lot de fabrication
  TCDL-QTELIV    PIC 9(8)     — quantité livrée (= cumul QTLCDE)
  TCDL-QTCGRT    PIC 9(8)     — quantité gratuite (= cumul QTCGRT + QTCECH)
  TCDL-QTECDE    PIC 9(8)     — quantité commandée
  TCDL-QTLCDE    PIC 9(8)     — quantité livrée unitaire
  TCDL-CODENR    PIC X        — '1' = entrée récapitulative, '3' = entrée lot
  TCDL-CODLAB    PIC X(4)     — code laboratoire
  TCDL-CODDEP    PIC X(2)     — dépôt : "CO" ou "MO"
  TCDL-CODLABLAB PIC X(4)     — code laboratoire lab
```

**Règle critique** : chaque groupe CDL produit exactement **2 entrées** dans `TABLE-CDL` :
- Entrée 1 : `TCDL-CODENR = '1'` (récapitulative — `TCDL-LOTFAB` toujours à espaces) — **jamais écrite** dans les fichiers de transmission
- Entrée 2 : `TCDL-CODENR = '3'` (détail lot — lot conditionnel selon `GESLOT`) — **seule écrite** via `FORMAT-ORDERS-CDL`

---

## 6. Base de données et SQL

### 6.1 Bases de données déclarées

| Alias | Base | Utilisation |
| --- | --- | --- |
| `D` | `BD_DEPOT` | Principale — toutes les tables métier |
| `T` | `BD_TRANSPORT` | Tables `STR`, `REC` |
| `S` | `BD_STATS` | Déclarée mais non utilisée dans le flux principal |
| `P` | `BD_PDF` | Table `DOCENT` |

### 6.2 Tables utilisées

`D.MVT`, `D.MES`, `D.CDE`, `D.CDL`, `D.ART`, `D.CLI`, `D.CDO`, `D.CCL`, `D.PAR`, `D.RTE`, `D.RTL`, `T.STR`, `T.REC`, `P.DOCENT`

### 6.3 Curseurs SQL

#### Curseur CURCDE (mode normal — `P-CODREP ≠ 'R'`)

```sql
SELECT DISTINCT
  cde.NUMCDE, cde.NUMRAL, STATUT, TYPNFA, QUANTA, MOISFA, CPTFAC,
  CPTFAC, GENCLILAB, CLICSP, TYPCDE, cde.CDELAB, NBSTD, NBDTL, LIVTOT,
  PDSSTD, PDSDTL, VOLSTD, VOLDTL, DATCDE, DATEBL, REFCDE, RESVIS, CLILAB,
  TYPCDELAB, CODSTR, TYPNFA, QUANTA, MOISFA, CPTFAC, CODREP,
  RAISOCL, NOMLIV, ADR1L, ADR2L, VILLEL, CPOSTL, DATSAI,
  cde.CODOPE, CDE.ADELI, CDE.CDESAISIE, CDE.DATREC, TRAFIC, CDE.DATEBP,
  CDE.CODSAI, CDE.CODMES, CDE.STREXP, CDE.CODLABLAB
FROM D.CDE
  JOIN D.CDO ON (cde.NUMCDE = cdo.numcde AND cde.NUMRAL = cdo.numral)
WHERE CODLAB = :W-CODLAB
  AND STATUT = 'CRV'
  AND DATPOR IS NULL
  AND :P-CODREP <> 'R'
```

> **Note** : les colonnes `TYPNFA`, `QUANTA`, `MOISFA`, `CPTFAC` apparaissent chacune **deux fois** dans le SELECT. Il s'agit d'un comportement COBOL spécifique — chaque occurrence est mappée sur une variable hôte différente.
>
> `CDE.DATREC` est récupéré avec un **indicateur NULL** dans `CURCDE`.

#### Curseur CURCDE_R (mode reprise — `P-CODREP = 'R'`)

Mêmes colonnes, clause WHERE différente :

```sql
WHERE CODLAB = :W-CODLAB
  AND STATUT = 'CRV'
  AND DATEBL > :W-DATDEB
  AND DATEBL < :W-DATFIN
```

> **Note** : dans `CURCDE_R`, `DATREC` n'a **pas** d'indicateur NULL (contrairement à `CURCDE`).

#### Curseur CURCDL (par commande)

```sql
SELECT L.NUMCDE, L.NUMRAL, CODART, L.NUMLIG, L.REFLIG, L.LOTFAB,
  L.QTCCDE, L.QTCECH, L.QTCGRT, L.QTLCDE, L.QTLECH, L.QTLGRT,
  L.LIVTOT, L.MENSPE, L.ARTSAI, L.CODDEP, L.CODLAB
FROM D.CDL L
  JOIN D.ART A USING(CODART)
WHERE (L.NUMCDE = :W-NUMCDE AND L.NUMRAL = :W-NUMRAL)
  AND A.CODLAB = :W-CODLAB
  AND CODSSS = ''
  AND QTCCDE > 0
  AND CODDEP <> ''
ORDER BY L.CODDEP, L.CODLAB
```

#### Curseur CURDIV (itération sur les laboratoires)

```sql
SELECT CODLAB FROM D.LAB WHERE CODLAB = :P-CODLAB
```

Permet de traiter un ou plusieurs laboratoires via un seul curseur. En pratique, `P-CODLAB` contient un seul code.

#### Curseur CURPAR (commandes non-facturées)

```sql
SELECT ARGUM, FONCT FROM D.PAR
WHERE CODENT = 'LAB'
  AND CHAMPS = 'NONFAC'
  AND ARGUM STARTING WITH :P-CODLAB
```

### 6.4 Requêtes mono-ligne (SELECT sans curseur)

| Paragraphe COBOL | Table | Clé | Colonnes récupérées |
| --- | --- | --- | --- |
| `FIND-LAST-DATE` | `D.PAR` | `CODENT='LAB'`, `CHAMPS='DATRAS'`, `ARGUM=lab` | `FONCT` (date dernière transmission) |
| `CREAT-DATRAS` | `D.PAR` | INSERT | `CODENT`, `FONCT`, `DATCRE`, `DATMAJ`, `CODOPE`, `CHAMPS`, `ARGUM` |
| `RECHERCHE-HORAIRE` | `D.CDL` | `NUMCDE`, `NUMRAL`, `CODART='QU00013'` | `LIBELL` → `W-LIB-HORAIRE` |
| `RECHERCHE-CONTACT` | `D.CDL` | `NUMCDE`, `NUMRAL`, `CODART='QU000132'` | `LIBELL` → `W-LIB-CONTACT` |
| `RECHERCHE-MES-BL-RT (BL)` | `D.MES` | `TYPDOC='BL'`, `CODMES=CDE.CODMES` | `CODMES`, `CODENT`, `TYPDOC`, `COMMENT` |
| `RECHERCHE-MES-BL-RT (RT)` | `D.MES` | `TYPDOC='RT'`, `CODMES=CDE.CODMES ORDER BY NUMLIG LIMIT 1` | `CODMES`, `CODENT`, `TYPDOC`, `COMMENT` |
| `RECH-NUMDOC` | `P.DOCENT` | `CODLAB`, `NUMCDE`, `NUMRAL LIMIT 1` | `NUMDOC` → `W-NUMDOC` |
| `LECTURE-LAB` | `D.LAB` | `CODLAB=W-CODLAB` | `TYPTRS`, `NUMRAL` |
| `LECTURE-ART` | `D.ART` | `CODLAB=W-CODLAB`, `CODART=W-CODART` | `ARTLAB`, `GESLOT`, `ARTCIP`, `LIBELL` |
| `LECTURE-CLI` | `D.CLI` | `CLICSP=CDE.CLICSP` | `GENCLI`, `DEPART`, `CLICIP`, `CIPPDV`, `PAYS` |
| `get-cli-cde` | `D.CLI` | `CLICSP=CDE.CLICSP` | `raisoc`, `nom`, `adres1`, `adres2`, `ville`, `cpost`, `telep`, `CIPPDV`, `pays`, `CLIPAY`, `CLICIP`, `CLICSP` |
| `get-cli-pay` | `D.CLI` | `CLICSP=W-CLIPAY` | `CLICIP`, `CIPPDV` |
| `get-cli` | `D.CLI` | `CLICSP=rte-clicsp` | `raisoc`, `nom`, `adres1`, `adres2`, `ville`, `cpost`, `telep`, `CIPPDV`, `pays`, `CLIPAY`, `CLICIP`, `CLICSP` |
| `LECTURE-CDO` | `D.CDO` | `NUMCDE=CDE.NUMCDE`, `NUMRAL=CDE.NUMRAL` | `raisocl`, `nomliv`, `adr1l`, `adr2l`, `villel`, `cpostl` |
| `LECTURE-CCL` | `D.CCL` | `CODLAB=P-CODLAB`, `CLICSP=CDE.CLICSP` | `clilab` |
| `LECTURE-MVT` | `D.MVT` | `CODDEP`, `CODLAB`, `CODART`, `LOTFAB`, `CODMVT IN ('070','071','072')`, `cast(datmvt as date ansi) = cast(CDE.DATEBL as date ansi)`, `REFDOC STARTING WITH W-NUMCDE LIMIT 1` | `datper` |
| `LECTURE-STR` | `T.STR` | `CODSTR=CDE.CODSTR` | `NOM` |
| `LECTURE-PAR-LABLAB` | `D.PAR` | `CODENT='LAB'`, `CHAMPS='LABLAB'`, `ARGUM STARTING WITH W-CODLAB LIMIT 1` | `FONCT` |
| `LECTURE-PAR-TYPLAB` | `D.PAR` | `CODENT='LAB'`, `CHAMPS='TYPLAB'`, `ARGUM STARTING WITH W-CODLAB`, `FONCT STARTING WITH CDE.TYPCDE LIMIT 1` | `ARGUM` |
| `LECTURE-DEPLAB` | `D.PAR` | `CODENT='LAB'`, `CHAMPS='DEPLAB'`, `ARGUM=WS-ARGUM(CODLAB+DEPART) LIMIT 1` | `FONCT(1:2)` → remplacement de `CODDEP` |

---

## 7. Appels externes VMS et leurs équivalents Java

Ces appels sont des fonctions système OpenVMS qui n'ont pas d'équivalent direct en Java. Ils doivent être réimplémentés.

| Appel VMS | Fonction VMS | Équivalent Java 8 |
| --- | --- | --- |
| `LIB$DATE_TIME` | Obtenir la date/heure courante en ASCII VMS `"DD-MON-YYYY HH:MI:SS.CC"` | `LocalDateTime.now()` avec formatage personnalisé |
| `SYS$BINTIM` | Convertir une date ASCII VMS en quadword binaire (`PIC S9(11)V9(7) COMP`) | Parsage vers `LocalDateTime` |
| `SYS$ASCTIM` | Convertir un quadword binaire en date ASCII VMS | Formatage de `LocalDateTime` |
| `D00_DATEDI` | Convertir ASCII VMS en format standard | Formateur personnalisé |
| `D00_YYYYMMDD` | Convertir une date binaire en `YYYYMMDD` | `DateTimeFormatter.ofPattern("yyyyMMdd")` |
| `D00_NUMEXP` | Obtenir le numéro d'expédition depuis `PARAM-BASNUMCDE` (`NUMCDE+NUMRAL+NUMEXP+MSG`) | Dépôt personnalisé |
| `D00_MSGGES` | Gestionnaire de messages d'erreur (écrit dans le fichier anomalies, arrête) | Exception avec journalisation |
| `LIB$SPAWN` | Lancer un sous-processus (utilisé uniquement pour diagnostic temporel `SH TIME`) | **Supprimer complètement** |
| `sys$putmsg` | Affichage de message VMS | `Logger` |
| `SQL$GET_ERROR_TEXT` | Texte d'erreur Oracle Rdb | `SQLException.getMessage()` JDBC |

### Format de date ASCII VMS

Le format ASCII VMS est : `"DD-MON-YYYY HH:MI:SS.CC"` où :
- `DD` = jour sur 2 chiffres (avec espace pour les jours 1–9)
- `MON` = mois en 3 lettres anglaises majuscules (`JAN`, `FEB`, `MAR`, `APR`, `MAY`, `JUN`, `JUL`, `AUG`, `SEP`, `OCT`, `NOV`, `DEC`)
- `YYYY` = année sur 4 chiffres
- `HH:MI:SS.CC` = heure, minute, seconde, centième

Exemple : `" 5-JAN-2015 14:30:22.00"`

### Conversion des mois (numéro ↔ abréviation 3 lettres)

| Numéro | Abréviation |
| --- | --- |
| 01 | JAN |
| 02 | FEB |
| 03 | MAR |
| 04 | APR |
| 05 | MAY |
| 06 | JUN |
| 07 | JUL |
| 08 | AUG |
| 09 | SEP |
| 10 | OCT |
| 11 | NOV |
| 12 | DEC |

---

## 8. Gestion de SQLSTATE

Le paragraphe `GESTION-SQLSTATE` est appelé après **chaque instruction SQL**. Voici les règles de traitement :

| Plage SQLSTATE | Résultat | Action |
| --- | --- | --- |
| `'00000'` à `'01999'` | Succès | `FLAG-TROUVE = 'O'` (trouvé) |
| `'02000'` | Non trouvé | `FLAG-TROUVE = 'N'` (non trouvé) |
| `'22002'` | Indicateur NULL positionné | `FLAG-TROUVE = 'O'` (traité comme succès — voir T8) |
| `'02001'` à `'22001'` | Erreur SQL | `FIN-ANORMALE` (erreur fatale, arrêt) |
| `'22003'` à `'S9999'` | Erreur SQL grave | `FIN-ANORMALE` (erreur fatale, arrêt) |

### Équivalent Java

```java
// Requête retournant un résultat
try (ResultSet rs = stmt.executeQuery()) {
    if (rs.next()) {
        // traiter rs — vérifier rs.wasNull() après chaque getXxx()
        found = true;
    } else {
        found = false; // équivalent SQLSTATE '02000'
    }
} catch (SQLException e) {
    throw new FatalSqlException(e); // équivalent FIN-ANORMALE
}
```

---

## 9. Flux d'exécution complet

### 9.1 TRAITEMENT-PRINCIPAL (programme principal)

```
1.  Initialiser PARAM à espaces ; lire les paramètres via PROC_COM (depuis l'environnement)
2.  Afficher "DEBUT PROGRAMME AVEC : " + PARAM
3.  SI P-CODREP = "R" ET (P-DATDEB = ESPACES OU P-DATFIN = ESPACES) → STOP RUN
4.  P-CODDEP → W-CODDEP
5.  P-CODLAB → W-CODLAB
6.  Appeler LIB$DATE_TIME → W-DATE-ASCII (date courante en chaîne VMS)
7.  Appeler D00_DATEDI(W-DATE-ASCII, W-DATE-STD) (convertir en format standard)
8.  W-DATE-ASCII → W-D-DATE
9.  Appeler LIB$DATE_TIME → W-D-DATE (rafraîchissement)
10. Appeler SYS$BINTIM(W-D-DATE) → W-DATE-SYS (date binaire courante)
11. Appeler SYS$BINTIM(W-DATE-ASCII) → W-DATE-VMS (date binaire courante — référence principale)
12. Appeler SYS$BINTIM("17-NOV-1858 00:00:00.00") → W-DATE-NULL (date zéro binaire)
13. Appeler D00_YYYYMMDD(W-DATE-VMS) → PARAM-DATE-TXT (texte YYYYMMDD)
14. SET TRANSACTION READ ONLY
15. Exécuter OPEN-FILES
16. SI P-CODREP = "R" :
      SYS$BINTIM(P-DATDEB) → W-DATDEB
      SYS$BINTIM(P-DATFIN) → W-DATFIN
    SINON :
      Exécuter FIND-LAST-DATE → W-DATBIN (stocké dans W-LAST-DATE)
17. Exécuter TRAITEMENT
18. Exécuter CLOSE-FILES
19. ROLLBACK
20. STOP RUN
```

---

### 9.2 TRAITEMENT (traitement global)

```
1.  W-D-DATE → ENREG-DATPOR (champ date dans l'enregistrement MAJ)
2.  Écrire dans RMS_MAJ (enregistrement de date)
3.  Initialiser ENREG-TRANSMIT
4.  Construire l'enregistrement DEBCDE :
      ENR-TYPMES       = "DEBCDE"
      ENR-FILLER       = ESPACES
      ENR-DEBCDE-EMETEUR = "183 CSP" + espaces jusqu'à 35 caractères
      ENR-DEBCDE-RECEPTE = "183 CSP" + espaces jusqu'à 35 caractères
      ENR-DEBCDE-TEST  = "P"
      ENR-DEBCDE-FILLER = ESPACES
5.  Écrire ENREG-TRANSMITCO (DEBCDE vers fichier CO)
6.  Écrire ENREG-TRANSMITMO (DEBCDE vers fichier MO)
7.  NB-CDE = 0
8.  Ouvrir CURDIV ; boucler RECHERCHE-DIV jusqu'à FIN-RECHERCHE-DIV ; fermer CURDIV
9.  Construire FINMES pour CO : ENR-TYPMES="FINMES", ENR-FINMES-NBCDE=NB-CDE-CO → écrire TRANSCO
10. Construire FINMES pour MO : ENR-FINMES-NBCDE=NB-CDE-MO → écrire TRANSMO
```

---

### 9.3 RECHERCHE-DIV (par itération laboratoire)

```
FETCH CURDIV → W-CODLAB
Évaluer SQLSTATE :
  CAS '02000' : FLAG-FIN-RECHERCHE-DIV = "O"
  CAS '00000'-'01999' OU '22002' :
    Exécuter LECTURE-LAB
    FLAG-FIN-CDE = "N"
    SI P-CODREP = "R" :
      Ouvrir CURCDE_R ; boucler LECTURE-DES-CDES-R jusqu'à FIN-CDE ; fermer CURCDE_R
    SINON :
      Ouvrir CURCDE ; boucler LECTURE-DES-CDES jusqu'à FIN-CDE ; fermer CURCDE
  CAS '02001'-'22001' OU '22003'-'S9999' : Exécuter FIN-ANORMALE
```

---

### 9.4 FIND-LAST-DATE (recherche de la dernière date de transmission)

```
FLAG-TROUVE = "N"
Construire WWARGUM = P-CODLAB + "N" (5 caractères)
SELECT FONCT FROM D.PAR
  WHERE CODENT='LAB' AND CHAMPS='DATRAS' AND ARGUM=:WWARGUM

SI TROUVE :
  FONCT → W-FONCT (= W-FONCTION : structure JJ-MM-SI-AA-HEURE)
  Convertir le numéro de mois en abréviation 3 lettres (01→JAN, ..., 12→DEC)
  Reconstruire W-FONCTION au format VMS "JJ-MON-SI-AA HH:MI:SS.CC"
  Appeler SYS$BINTIM(W-FONCTION) → W-DATBIN
SINON :
  Exécuter CREAT-DATRAS
```

La colonne `FONCT` dans `D.PAR` stocke la date sous forme de chaîne numérique avec les champs : JJ (jour), MM (mois numérique), SI (siècle+millénaire), AA (année sur 2 chiffres), HEURE (HH:MI:SS.CC). Le programme reconstitue le format VMS à partir de ces éléments.

---

### 9.5 CREAT-DATRAS (création de la date de référence)

```
ROLLBACK
SET TRANSACTION READ WRITE RESERVING D.PAR FOR SHARED WRITE
Construire la date courante en chaîne VMS
INSERT INTO D.PAR VALUES (
  CODENT='LAB',
  FONCT=<date_courante_formatée>,
  DATCRE=<date courante>,
  DATMAJ=<date courante>,
  CODOPE='SYS',
  CHAMPS='DATRAS',
  ARGUM=<WWARGUM>
)
COMMIT
SET TRANSACTION READ ONLY
```

**Important** : `DATRAS` est stockée dans la colonne `FONCT` de `D.PAR` en tant que chaîne de date formatée (numérique : JJ, MM, SI, AA + HEURE).

---

### 9.6 LECTURE-DES-CDES / LECTURE-DES-CDES-R (par commande)

```
FETCH CURCDE (ou CURCDE_R) → tous les champs CDE+CDO avec indicateurs NULL
Exécuter GESTION-SQLSTATE
SI TROUVE :
  Exécuter TRT-TROUVE
SINON :
  FLAG-FIN-CDE = "O"
```

---

### 9.7 TRT-TROUVE (commande trouvée)

```
1.  Copier les champs de la commande vers les variables de travail
    (W-NUMCDE, W-CDELAB, W-NUMRAL, W-REFCDE, etc.)
2.  Appeler D00_NUMEXP(PARAM-BASNUMCDE) → WP-NUMEXP → W-NUMEXP
3.  Exécuter LECTURE-CLI (récupérer les infos client pour CDE.CLICSP)
4.  Exécuter RECHERCHE-MES-BL-RT (récupérer les messages BL/RT)
5.  Exécuter TRAIT-FAT
```

---

### 9.8 RECHERCHE-MES-BL-RT (messages BL et RT)

```
Initialiser MES
FLAG-BL-TROUVE = "N" ; FLAG-RT-TROUVE = "N" ; W-COMMENT = ESPACES

Requête BL : SELECT WHERE TYPDOC='BL' AND CODMES=CDE.CODMES
  SI SQLSTATE='02000' : FLAG-TROUVE="N"
  SINON : FLAG-TROUVE="O" ; FLAG-BL-TROUVE="O" ; W-COMMENT = MES.COMMENT

Requête RT : SELECT WHERE TYPDOC='RT' AND CODMES=CDE.CODMES ORDER BY NUMLIG LIMIT 1
  SI TROUVE ET NON BL-TROUVE : W-COMMENT = MES.COMMENT

Règle de priorité : si BL trouvé, BL a la priorité ; RT n'est utilisé que si BL est absent.
```

---

### 9.9 TRAIT-FAT (phase de chargement CDL)

```
CDELAB de CDE → W-CDELAB
FLAG-FIN-CDL = "N"
NB-CDL = 0 ; ESPACES → W-NUMLIG, W-CODART
W-NB-CDL-CO = 0 ; W-NB-CDL-MO = 0
Ouvrir CURCDL
Boucler LECTURE-DES-CDL jusqu'à FIN-CDL
Fermer CURCDL
Exécuter RECHERCHE-HORAIRE
Exécuter RECHERCHE-CONTACT
Exécuter STOCK-FIC-DESADV
```

---

### 9.10 LECTURE-DES-CDL (par ligne CDL)

```
FETCH CURCDL → champs CDL
Exécuter GESTION-SQLSTATE
SI TROUVE :
  Exécuter TRT-TROUVE-CDL
SINON :
  Exécuter TRT-NOT-TROUVE-CDL
```

---

### 9.11 TRT-TROUVE-CDL (logique d'accumulation CDL)

```
1.  SI CODLAB dans CDL = "2951" : Exécuter LECTURE-DEPLAB (peut remplacer CODDEP)
2.  SI CODDEP = "MO" : W-NB-CDL-MO += 1
    SI CODDEP = "CO" : W-NB-CDL-CO += 1
    (NOTE : ces compteurs sont distincts de ceux réinitialisés dans STOCK-LIG-DESADV)
3.  CDL.CODART → CDL-CODART ; CDL.NUMLIG → CDL-NUMLIG
4.  SI W-NUMLIG ≠ CDL-NUMLIG OU W-CODART ≠ CDL-CODART :  ← RUPTURE DE GROUPE
      SI un groupe précédent existe (W-NUMLIG ≠ ESPACES OU W-CODART ≠ ESPACES) :
        Exécuter TRT-CDL-CUMULEE  (vider le groupe précédent)
      Exécuter INIT-QTE  (réinitialiser les accumulateurs)
      Déplacer quantités/champs CDL vers les variables de travail W-xxx
5.  Cumuler :
      W-QTECDE += CDL.QTCCDE
      W-QTETOT += CDL.QTLCDE       ← total livré
      W-QTRCDE += CDL.QTRCDE
      W-QTCGRT += CDL.QTCGRT + CDL.QTCECH   ← quantités gratuites + échanges
```

---

### 9.12 TRT-NOT-TROUVE-CDL (fin du curseur CDL)

```
IMPORTANT : les variables hôtes CDL contiennent encore les valeurs du dernier FETCH.
Copier CDL-CODART → W-CODART    (CDL-CODART défini depuis le dernier FETCH)
Copier CDL-NUMLIG → W-NUMLIG
CDL.QTCCDE → W-QTCCDEL
CDL.REFLIG → W-REFLIG
CDL.MENSPE → W-MENSPE
CDL.LOTFAB → W-LOTFAB
CDL.ARTSAI → W-ARTSAI
CDL.CODDEP → W-CDL-CODDEP
CDL.CODLAB → W-CDL-CODLAB
Exécuter TRT-CDL-CUMULEE  (vider le dernier groupe)
FLAG-FIN-CDL = "O"
```

**En Java** : lorsque `ResultSet.next()` retourne `false`, les valeurs du dernier appel réussi à `next()` doivent avoir été préservées dans des variables locales. Ne pas accéder au `ResultSet` après que `next()` a retourné `false`.

---

### 9.13 TRT-CDL-CUMULEE (vider un groupe CDL dans TABLE-CDL)

```
Exécuter LECTURE-ART (récupérer ART pour W-CODLAB + W-CODART)

Entrée 1 (CODENR = '1') :
  NB-CDL += 1
  TCDL-CODENR(NB-CDL)  = '1'
  TCDL-ARTLAB(NB-CDL)  = W-ARTLAB
  TCDL-LIBELL(NB-CDL)  = ART.LIBELL
  TCDL-CODART(NB-CDL)  = W-CODART
  TCDL-ARTCIP(NB-CDL)  = W-ARTCIP
  TCDL-LOTFAB(NB-CDL)  = ESPACES   ← TOUJOURS vidé dans l'entrée 1
  SI W-REFLIG(1:4) ≠ ESPACES : TCDL-NUMLIG = W-REFLIG(1:4)
  SINON                       : TCDL-NUMLIG = W-NUMLIG
  TCDL-QTELIV(NB-CDL)  = W-QTETOT
  TCDL-QTECDE(NB-CDL)  = W-QTECDE
  TCDL-QTCGRT(NB-CDL)  = W-QTCGRT
  TCDL-ARTSAI(NB-CDL)  = W-ARTSAI
  TCDL-CODLAB(NB-CDL)  = W-CDL-CODLAB
  TCDL-CODDEP(NB-CDL)  = W-CDL-CODDEP
  W-VERIFQTE-CDE       = W-QTETOT
  W-VERIFQTE-CLL       = 0
  W-QTELIVT += W-QTETOT
  W-QTELIVC += W-QTETOT

Exécuter TRAITEMENT-SANS-LOT → Entrée 2 (CODENR = '3') :
  NB-CDL += 1
  TCDL-CODENR(NB-CDL)  = '3'
  SI W-GESLOT ≠ '4' ET ≠ '5' : W-LOTFAB = ESPACES (vider le lot)
  TCDL-NUMLIG, ARTLAB, LIBELL, CODART, ARTCIP, LOTFAB, ARTSAI, CODDEP, CODLAB = identiques à entrée 1
  TCDL-QTELIV(NB-CDL)  = W-QTETOT
  TCDL-QTCGRT(NB-CDL)  = W-QTCGRT
  TCDL-CODLABLAB(NB-CDL) = W-CODLABLAB
  WS-QTELIV            = W-QTETOT
  W-VERIFQTE-CLL       += W-QTETOT
```

**Règle sur GESLOT** : le numéro de lot (`LOTFAB`) n'est conservé dans l'entrée CODENR='3' que si `GESLOT` vaut `'4'` ou `'5'`. Pour toute autre valeur, `LOTFAB` est forcé à espaces.

---

### 9.14 STOCK-FIC-DESADV (par commande — calcul des champs intermédiaires)

```
1.  Écrire la référence commande dans RMS_MAJ (NUMCDE, NUMRAL, CDELAB)
2.  Calculer les champs FIC-TYPE1 (espace de travail intermédiaire, non écrit directement) :
    - FIC-NUMCDE, FIC-NUMRAL issus de CDE
    - FIC-DATCDE : convertir CDE.DATCDE (binaire VMS) via SYS$ASCTIM → réarranger en YYYYMMDD
    - FIC-DATSAI : même conversion pour CDE.DATSAI
    - FIC-DATREC : CDE.DATREC binaire → ASCII (chaîne VMS complète 23 caractères)
    - FIC-DATEBL : si IDATEBL=-1 → PARAM-DATE-TXT (date courante YYYYMMDD) ; sinon conversion dates
    - FIC-DATEBP : si IDATEBP=-1 → PARAM-DATE-TXT ; sinon conversion dates
    - FIC-TYPCDE = CDE.TYPCDE
    - FIC-NUMFAC : TYPNFA(1:2) + QUANTA(3:1) + MOISFA(4:2) + CPTFAC(6:5), espaces→zéros
    - FIC-CDELAB = CDE.CDELAB
    - FIC-CODREP : si W-NUMCDE = FIC-CDELAB(1:7) → ESPACES ; sinon W-CODREP
    - FIC-NBCOL = NBSTD + NBDTL ; FIC-PDS = PDSSTD + PDSDTL
    - FIC-UNITE = "KG" ; FIC-STATUT = W-STATUT
    - FIC-VOL = VOLSTD + VOLDTL
    - LECTURE-CCL → FIC-CLILAB2 = CCL.CLILAB
    - CDE.CLICSP → FIC-CLILAB (numérique) ; FIC-CLILAB-X remplacement espaces par '0'
    - LECTURE-CDO → FIC-RAISOCL, FIC-NOMLIV, FIC-ADR1L, FIC-ADR2L, FIC-VILLEL, FIC-CPOSTL
    - get-cli-cde → toutes les données CLI
    - get-cli-pay → données CLI du payeur
    - CLI.TELEP → FIC-TELEP

3.  Compter les commandes avec lignes :
    SI W-NB-CDL-CO > 0 : NB-CDE-CO += 1
    SI W-NB-CDL-MO > 0 : NB-CDE-MO += 1
    (Compteurs issus de TRT-TROUVE-CDL ; réinitialisés plus tard dans STOCK-LIG-DESADV)

4.  PREV-CODLAB = ESPACES ; IND-CDL = 0 ; W-SUMQTE = 0
5.  Boucler STOCK-LIG-DESADV jusqu'à IND-CDL = NB-CDL
6.  Exécuter WRITE-FINCDE
```

#### Détail de la conversion de date (FIC-DATCDE, FIC-DATSAI, FIC-DATEBL, FIC-DATEBP)

```
SYS$ASCTIM(binaire_vms) → W-DATE-ASCII "DD-MON-YYYY HH:MI:SS.CC"
W-DATE-ASCII(8:4) → champ(1:4)   (année = caractères 8–11)
CVT-MOIS : W-DATE-ASCII(4:3) → W-MOIS-NUM (mois en 3 lettres → 2 chiffres)
W-MOIS-NUM → champ(5:2)          (mois = positions 5–6)
W-DATE-ASCII(1:2) → champ(7:2)   (jour  = caractères 1–2)
INSPECT champ REPLACING ALL ESPACES BY "0"
Résultat : YYYYMMDD (8 caractères)
```

---

### 9.15 STOCK-LIG-DESADV (par entrée TABLE-CDL)

```
IND-CDL += 1

SI TCDL-CODLAB(IND-CDL) ≠ PREV-CODLAB :  ← changement de groupe laboratoire
  SI IND-CDL > 1 :
    Exécuter WRITE-FINCDE  (clore le groupe laboratoire précédent)
  W-SUMQTE-CO = 0 ; W-SUMQTE-MO = 0
  W-NB-CDL-CO = 0 ; W-NB-CDL-MO = 0   ← réinitialiser compteurs lignes pour ce groupe lab
  Exécuter WRITE-REFCDE
  Exécuter WRITE-LINTXT
  SI W-LIB-HORAIRE ≠ ESPACES : Exécuter WRITE-TXTHOR
  SI W-LIB-CONTACT ≠ ESPACES : Exécuter WRITE-TXTCON

SI TCDL-CODENR(IND-CDL) = '1' :
  Renseigner FIC-TYPE2 (format ancien, non écrit dans les fichiers de transmission)
  (définit FIC-TYPENR='1', FIC-REFLIG, FIC-ARTLAB, FIC-QTLCDE=QTELIV, FIC-QTCCDE=QTECDE)

SINON (CODENR = '3') :
  Renseigner FIC-TYPE3
  Exécuter LECTURE-MVT (récupérer DATPER pour cet article/lot/dépôt)
  W-DATPER = "20" + MVT.DATPER(3:6)  (reconstruire la date complète)
  Exécuter FORMAT-ORDERS-CDL → Écrire LINCDE dans le fichier approprié
  SI CODDEP = "CO" : W-NB-CDL-CO += 1
  SI CODDEP = "MO" : W-NB-CDL-MO += 1

TCDL-CODLAB(IND-CDL) → PREV-CODLAB
TCDL-CODDEP(IND-CDL) → PREV-CODDEP
```

---

### 9.16 FORMAT-ORDERS-CDL (écriture LINCDE)

```
Initialiser ENREG-TRANSMIT
ENR-TYPMES           = "LINCDE"
ENR-FILLER           = ESPACES
ENR-LINCDE-CODART    = TCDL-CODART(IND-CDL)
ENR-LINCDE-QTCCDE    = TCDL-QTELIV(IND-CDL)   ← ATTENTION : ce champ reçoit QTELIV (voir T7)
ENR-LINCDE-QTCGRT    = TCDL-QTCGRT(IND-CDL)
ENR-LINCDE-CODLABLAB = TCDL-CODLABLAB(IND-CDL)
ENR-LINCDE-LIBELL    = ESPACES   ← TOUJOURS forcé à espaces

W-SUMQTE += TCDL-QTELIV(IND-CDL)

SI CODDEP = "CO" :
  Écrire ENREG-TRANSMITCO
  W-SUMQTE-CO += TCDL-QTELIV(IND-CDL)
SI CODDEP = "MO" :
  Écrire ENREG-TRANSMITMO
  W-SUMQTE-MO += TCDL-QTELIV(IND-CDL)
```

---

### 9.17 WRITE-REFCDE (écriture du header de commande)

```
ENR-TYPMES              = "REFCDE"
ENR-REFCDE-DATCDE       = FIC-DATCDE(3:6)   ← AAMMJJ (caractères 3–8 de YYYYMMDD = YYMMDD)
ENR-REFCDE-REFCDE       = W-REFCDE
ENR-REFCDE-CDELAB       = CDE.CDELAB
ENR-REFCDE-CODSAI       = CDE.CODSAI
ENR-REFCDE-CODSTR       = CDE.CODSTR
ENR-REFCDE-STREXP       = CDE.STREXP
ENR-REFCDE-DATREC       = FIC-DATREC (23 caractères ASCII VMS complets)
ENR-REFCDE-DATEBP       = FIC-DATEBP (8 caractères YYYYMMDD)
ENR-REFCDE-CLICSP       = CDE.CLICSP (6 chiffres numériques)

ASTUCE NOMLIV (voir T3) :
  Déplacer CDE.CLICSP dans ENR-REFCDE-NOMLIV (35 chars) → "000123" + 29 espaces
  Déplacer ENR-REFCDE-NOMLIV(4:6) dans ENR-REFCDE-NOMLIV (les 6 chars de position 4–9 écrasent positions 1–6)
  Résultat : les caractères 4–9 de la chaîne numérique CLICSP en positions 1–6

ENR-REFCDE-CODOPE       = CDE.CODOPE
ENR-REFCDE-CODREP       = CDE.CODREP
ENR-REFCDE-CDESAISIE    = CDE.CDESAISIE
ENR-REFCDE-TYPCDE       = CDE.TYPCDE
ENR-REFCDE-TRAFIC       : si CDE.TRAFIC = ESPACES → "N", sinon CDE.TRAFIC
RECH-NUMDOC → si trouvé : W-NUMDOC → ENR-REFCDE-NUMDOC ; sinon ESPACES

Routage :
  Si TCDL-CODDEP(IND-CDL) = "CO" → écrire dans TRANSCO
  Si TCDL-CODDEP(IND-CDL) = "MO" → écrire dans TRANSMO
```

---

### 9.18 WRITE-LINTXT (écriture du commentaire)

```
ENR-TYPMES           = "LINTXT"
ENR-LINTXT-MESSAGE   = W-COMMENT (114 caractères, BL prioritaire sur RT)
Routage identique à REFCDE (par TCDL-CODDEP(IND-CDL))
```

---

### 9.19 WRITE-TXTHOR / WRITE-TXTCON (textes libres)

```
TXTHOR :
  ENR-TYPMES         = "TXTCDE"
  ENR-TXTCDE-MESSAGE = W-LIB-HORAIRE (issu de QU00013)
  ENR-TXTCDE-TYPDOC  = "RT"
  Écrit uniquement si W-LIB-HORAIRE ≠ ESPACES

TXTCON :
  ENR-TYPMES         = "TXTCDE"
  ENR-TXTCDE-MESSAGE = W-LIB-CONTACT (issu de QU000132)
  ENR-TXTCDE-TYPDOC  = "BL"
  Écrit uniquement si W-LIB-CONTACT ≠ ESPACES

Les deux utilisent le routage par TCDL-CODDEP(IND-CDL)
```

---

### 9.20 WRITE-FINCDE (écriture du pied de groupe)

```
Version CO :
  ENR-TYPMES           = "FINCDE"
  ENR-FINCDE-SUMQTE    = W-SUMQTE-CO
  ENR-FINCDE-NBLIG     = W-NB-CDL-CO
  → ENREG-TRANSMITCO (préparé)
  SI PREV-CODDEP = "CO" : Écrire ENREG-TRANSMITCO

Version MO :
  ENR-FINCDE-SUMQTE    = W-SUMQTE-MO
  ENR-FINCDE-NBLIG     = W-NB-CDL-MO
  → ENREG-TRANSMITMO (préparé)
  SI PREV-CODDEP = "MO" : Écrire ENREG-TRANSMITMO
```

**CRITIQUE** : `FINCDE` n'est écrit que pour le dépôt dont la valeur correspond à `PREV-CODDEP` (le dépôt du **dernier** enregistrement vu). Si un groupe contient à la fois des lignes CO et MO, seul le dépôt du dernier traitement reçoit un `FINCDE` à ce moment. L'autre reçoit son `FINCDE` lors du déclenchement suivant (changement de groupe lab ou fin des lignes). Voir T4.

---

### 9.21 LECTURE-DEPLAB (remplacement de dépôt pour lab 2951)

```
WS-ARGUM = CDL.CODLAB (4 chars) + CLI.DEPART (2 chars) = clé de 6 chars
SELECT FONCT FROM D.PAR
  WHERE CODENT='LAB' AND CHAMPS='DEPLAB' AND ARGUM=:WS-ARGUM LIMIT 1
SI TROUVE : CDL.CODDEP = WFONCT(1:2)   (remplacer le code dépôt)
```

---

## 10. Comportements critiques à préserver en Java

### T1 — Double incrémentation de NB-CDL par groupe CDL

`TRT-CDL-CUMULEE` appelle `TRAITEMENT-SANS-LOT`, qui exécute `ADD 1 TO NB-CDL`. Ainsi, chaque groupe CDL crée exactement **2 entrées** dans `TABLE-CDL`. L'entrée 1 (`CODENR='1'`) n'est jamais écrite dans les fichiers ; elle existe uniquement comme donnée intermédiaire. Seule l'entrée 2 (`CODENR='3'`) est écrite via `FORMAT-ORDERS-CDL`.

**En Java** : lors de l'accumulation des lignes CDL, créer systématiquement une paire d'objets `LigneCumulee` : un avec `codenr='1'`, un avec `codenr='3'`. Ne traiter dans la boucle d'écriture que ceux dont `codenr == '3'`.

---

### T2 — TRT-NOT-TROUVE-CDL réutilise les valeurs périmées du curseur

Quand le curseur CDL est épuisé (SQLSTATE `'02000'`), les variables hôtes COBOL (`CDL.CODART`, `CDL.NUMLIG`, etc.) contiennent encore les valeurs du dernier FETCH réussi. Le code copie explicitement ces valeurs périmées vers les variables de travail `W-xxx` avant d'appeler `TRT-CDL-CUMULEE`.

**En Java** : lorsque `ResultSet.next()` retourne `false`, les valeurs doivent avoir été mises en cache depuis la dernière itération réussie. Ne pas accéder au `ResultSet` après que `next()` retourne `false`. Conserver les champs du dernier enregistrement dans des variables locales entre les itérations.

```java
CdlRow lastRow = null;
while (rs.next()) {
    CdlRow current = new CdlRow(rs); // lire tous les champs
    // traitement...
    lastRow = current; // conserver pour après la boucle
}
if (lastRow != null) {
    // flush du dernier groupe avec lastRow (équivalent TRT-NOT-TROUVE-CDL)
    flushCdlGroup(lastRow);
}
```

---

### T3 — Le champ NOMLIV contient CLICSP décalé (pas un nom de livraison)

Le champ `ENR-REFCDE-NOMLIV` (35 caractères) ne contient **pas** un nom de lieu de livraison. Le traitement est le suivant :

1. Déplacer `CDE.CLICSP` (numérique, ex. `"000123"`) dans le champ 35 caractères → `"000123"` + 29 espaces
2. Déplacer la sous-chaîne positions 4–9 (`"123   "`) dans les positions 1–6 du même champ
3. Résultat final : `"123   "` + 29 espaces

**En Java** :
```java
String clicsp = String.format("%06d", clicspValue); // "000123"
String padded = String.format("%-35s", clicsp);     // "000123" + 29 espaces
String nomliv = String.format("%-35s", padded.substring(3, 9)); // chars 4-9 (index 3-8)
// → "123   " + 29 espaces
```

---

### T4 — WRITE-FINCDE n'écrit que pour PREV-CODDEP

Le routage de `FINCDE` utilise `PREV-CODDEP` (le dépôt du **dernier** enregistrement traité), pas le dépôt courant. Si les lignes d'un groupe lab alternent entre CO et MO :
- À la fin du groupe, seul le dépôt correspondant à la **dernière** ligne écrite reçoit un `FINCDE`
- L'autre dépôt reçoit son `FINCDE` lors du prochain appel à `WRITE-FINCDE` (changement de groupe lab ou fin de toutes les lignes)

Cette logique doit être reproduite fidèlement en Java pour garantir que chaque dépôt actif obtient exactement un `FINCDE` par groupe laboratoire.

---

### T5 — NB-CDE-CO/MO incrémentés avant la réinitialisation des compteurs dans STOCK-LIG-DESADV

Dans `STOCK-FIC-DESADV`, le test `W-NB-CDL-CO > 0` (issu des comptages de `TRT-TROUVE-CDL`) et l'incrémentation de `NB-CDE-CO`/`NB-CDE-MO` ont lieu **avant** que `STOCK-LIG-DESADV` réinitialise `W-NB-CDL-CO` et `W-NB-CDL-MO` à 0 (lors du changement de lab). Les accumulateurs externes `NB-CDE-CO`/`NB-CDE-MO` sont donc correctement renseignés par commande.

---

### T6 — Remplacement de dépôt pour le lab 2951

Uniquement pour `CODLAB IN CDL = "2951"`. Avant d'incrémenter les compteurs CO/MO, le programme consulte `D.PAR` avec la clé `CODENT='LAB'`, `CHAMPS='DEPLAB'`, `ARGUM=CODLAB+DEPART`. Si trouvé, `CODDEP` dans la ligne CDL est remplacé par les 2 premiers caractères de `FONCT`. Ce remplacement a lieu **avant** le comptage CO/MO.

---

### T7 — Le champ LINCDE QTCCDE reçoit la quantité livrée (QTELIV)

Malgré son nom `ENR-LINCDE-QTCCDE` (qui suggère "quantité commandée"), ce champ est alimenté par `TCDL-QTELIV` (la somme des `QTLCDE` des lignes), c'est-à-dire la **quantité livrée**. Ce n'est pas une erreur mais une décision métier délibérée.

---

### T8 — SQLSTATE '22002' traité comme succès

Oracle Rdb retourne `'22002'` lorsqu'un indicateur NULL est positionné sur une colonne récupérée. `GESTION-SQLSTATE` traite cela comme "trouvé" (`FLAG-TROUVE = 'O'`). En Java, gérer les colonnes NULL via des vérifications explicites après chaque `getXxx()` en appelant `rs.wasNull()`.

---

### T9 — Indicateur de date -1 signifie NULL

`IDATEBL = -1` et `IDATEBP = -1` indiquent que la colonne date est `NULL` dans la base. Dans ce cas, la date courante (`PARAM-DATE-TXT` = `YYYYMMDD`) est substituée. En Java : utiliser `rs.wasNull()` après `rs.getDate()` ou `rs.getObject()`.

---

### T10 — DATCDE dans REFCDE est au format YYMMDD

`FIC-DATCDE` est calculé en format `YYYYMMDD` (8 caractères). L'affectation `ENR-REFCDE-DATCDE = FIC-DATCDE(3:6)` prend les caractères de la **position 3, longueur 6**, soit les caractères 3 à 8 de `YYYYMMDD`.

Décomposition : `YYYYMMDD` → positions 1–2 = `YY` (siècle ignoré), position 3 = début de l'extraction
- Position 3 = 3e caractère de la chaîne `YYYYMMDD`
- En COBOL, la notation `(3:6)` signifie : commencer à la position 3, prendre 6 caractères
- Résultat : `YYMMDD` (les 2 chiffres de l'année, les 2 du mois, les 2 du jour)

**En Java** :
```java
String ficDatecde = "20150616"; // YYYYMMDD
String datcde = ficDatecde.substring(2, 8); // chars index 2-7 = "150616" (YYMMDD)
```

---

### T11 — REFCDE écrit une seule fois par groupe laboratoire, pas par commande

`WRITE-REFCDE` est appelé dans `STOCK-LIG-DESADV` uniquement lors d'un **changement de `TCDL-CODLAB`**, pas à chaque commande. Si toutes les lignes CDL d'une commande appartiennent au même laboratoire, il n'y a qu'un seul en-tête `REFCDE` pour toute la commande.

---

### T12 — La transaction est READ ONLY pour le traitement principal

Le programme utilise `SET TRANSACTION READ ONLY` (snapshot non verrouillant sous Oracle Rdb). `CREAT-DATRAS` bascule brièvement en `READ WRITE` (avec `COMMIT`), puis revient en `READ ONLY`. À la fin, `ROLLBACK` est appelé même pour une transaction en lecture seule (sans effet, mais requis par Oracle Rdb).

**En Java** : configurer la connexion JDBC en `setAutoCommit(false)` et `setReadOnly(true)` pour le traitement principal. Pour `CREAT-DATRAS`, utiliser une connexion ou une transaction séparée en écriture.

---

## 10bis. Flow général du programme et tables de conditions

Cette section complète la section 9 en représentant visuellement les flux d'exécution et en documentant **chaque condition** (IF, EVALUATE) avec ses branches et effets.

---

### 10bis.1 Diagramme de flux principal

```
TRAITEMENT-PRINCIPAL
│
├─ [Validation] P-CODREP='R' ET (P-DATDEB=SPACES OU P-DATFIN=SPACES)
│   └─ OUI → STOP RUN immédiat (aucun fichier ouvert, aucun traitement)
│
├─ Initialisation dates (LIB$DATE_TIME, SYS$BINTIM, D00_YYYYMMDD)
├─ SET TRANSACTION READ ONLY
├─ OPEN-FILES
│
├─ [Condition mode] P-CODREP = 'R' ?
│   ├─ OUI → SYS$BINTIM(P-DATDEB) → W-DATDEB
│   │         SYS$BINTIM(P-DATFIN) → W-DATFIN
│   └─ NON → FIND-LAST-DATE
│               └─ Trouvé dans D.PAR (DATRAS) ?
│                   ├─ OUI → convertir → W-DATBIN
│                   └─ NON → CREAT-DATRAS (INSERT + COMMIT)
│
└─ TRAITEMENT
    ├─ Écrire DEBCDE → TRANSCO + TRANSMO
    ├─ Ouvrir CURDIV → boucle RECHERCHE-DIV
    │   └─ [Pour chaque laboratoire FETCH CURDIV]
    │       ├─ SQLSTATE '02000' → FIN-RECHERCHE-DIV = O, sortir boucle
    │       ├─ SQLSTATE erreur  → FIN-ANORMALE
    │       └─ SQLSTATE succès  → LECTURE-LAB
    │                              [P-CODREP='R' ?]
    │                              ├─ OUI → CURCDE_R → boucle LECTURE-DES-CDES-R
    │                              └─ NON → CURCDE   → boucle LECTURE-DES-CDES
    │                                         └─ [Pour chaque commande FETCH]
    │                                             ├─ Non trouvé → FLAG-FIN-CDE='O'
    │                                             └─ Trouvé → TRT-TROUVE
    │                                                 └─ → TRAIT-FAT
    │                                                     └─ → STOCK-FIC-DESADV
    ├─ Fermer CURDIV
    ├─ Écrire FINMES CO (NB-CDE-CO) → TRANSCO
    └─ Écrire FINMES MO (NB-CDE-MO) → TRANSMO
```

---

### 10bis.2 Diagramme de flux TRAIT-FAT / accumulation CDL

```
TRAIT-FAT (par commande)
│
├─ Initialiser : NB-CDL=0, W-NUMLIG=SPACES, W-CODART=SPACES
│                W-NB-CDL-CO=0, W-NB-CDL-MO=0
│
├─ Ouvrir CURCDL → boucle LECTURE-DES-CDL
│   │
│   ├─ [FETCH CURCDL]
│   │   ├─ SQLSTATE erreur → FIN-ANORMALE
│   │   ├─ SQLSTATE '02000' (non trouvé) → TRT-NOT-TROUVE-CDL
│   │   │   ├─ Copier valeurs PÉRIMÉES du dernier FETCH → W-xxx
│   │   │   ├─ TRT-CDL-CUMULEE (vider dernier groupe)
│   │   │   └─ FLAG-FIN-CDL = 'O' → sortir boucle
│   │   └─ SQLSTATE succès → TRT-TROUVE-CDL
│   │       │
│   │       ├─ [CODLAB IN CDL = '2951' ?]
│   │       │   └─ OUI → LECTURE-DEPLAB
│   │       │             [Trouvé dans D.PAR DEPLAB ?]
│   │       │             └─ OUI → CDL.CODDEP = FONCT(1:2) (remplacement)
│   │       │
│   │       ├─ [CODDEP = 'MO' ?] OUI → W-NB-CDL-MO += 1
│   │       ├─ [CODDEP = 'CO' ?] OUI → W-NB-CDL-CO += 1
│   │       │
│   │       ├─ [RUPTURE DE GROUPE : W-NUMLIG≠CDL-NUMLIG OU W-CODART≠CDL-CODART ?]
│   │       │   └─ OUI :
│   │       │       ├─ [Groupe précédent existe ? W-NUMLIG≠SPACES OU W-CODART≠SPACES]
│   │       │       │   └─ OUI → TRT-CDL-CUMULEE (vider groupe précédent)
│   │       │       └─ INIT-QTE (remettre accumulateurs à 0)
│   │       │           Copier CDL courant → W-xxx (nouvelle clé de groupe)
│   │       │
│   │       └─ Cumuler dans le groupe courant :
│   │           W-QTECDE += CDL.QTCCDE
│   │           W-QTETOT += CDL.QTLCDE
│   │           W-QTCGRT += CDL.QTCGRT + CDL.QTCECH
│   │
│   └─ [Continuer jusqu'à FIN-CDL]
│
├─ Fermer CURCDL
├─ RECHERCHE-HORAIRE (SELECT CDL WHERE CODART='QU00013')
├─ RECHERCHE-CONTACT (SELECT CDL WHERE CODART='QU000132')
└─ STOCK-FIC-DESADV

```

---

### 10bis.3 Diagramme de flux TRT-CDL-CUMULEE (création des 2 entrées TABLE-CDL)

```
TRT-CDL-CUMULEE
│
├─ LECTURE-ART (SELECT D.ART WHERE CODLAB=W-CODLAB AND CODART=W-CODART)
│   → W-ARTLAB, W-GESLOT, W-ARTCIP, ART.LIBELL
│
├─ ENTRÉE 1 (CODENR='1') — NB-CDL += 1
│   ├─ TCDL-LOTFAB = SPACES (TOUJOURS — lot jamais conservé en entrée 1)
│   ├─ [W-REFLIG(1:4) ≠ SPACES ?]
│   │   ├─ OUI → TCDL-NUMLIG = W-REFLIG(1:4)
│   │   └─ NON → TCDL-NUMLIG = W-NUMLIG
│   └─ Remplir tous les autres champs depuis W-xxx
│
└─ TRAITEMENT-SANS-LOT → ENTRÉE 2 (CODENR='3') — NB-CDL += 1
    ├─ [W-GESLOT = '4' OU W-GESLOT = '5' ?]
    │   ├─ OUI → TCDL-LOTFAB = W-LOTFAB (conserver le lot)
    │   └─ NON → W-LOTFAB = SPACES, TCDL-LOTFAB = SPACES
    ├─ [W-REFLIG(1:4) ≠ SPACES ?]
    │   ├─ OUI → TCDL-NUMLIG = W-REFLIG(1:4)
    │   └─ NON → W-REFLIG(1:4) = W-NUMLIG → TCDL-NUMLIG = W-REFLIG(1:4)
    └─ Remplir TCDL-CODLABLAB = W-CODLABLAB (champ absent de l'entrée 1)
```

---

### 10bis.4 Diagramme de flux STOCK-LIG-DESADV (écriture par entrée TABLE-CDL)

```
STOCK-LIG-DESADV (boucle sur IND-CDL de 1 à NB-CDL)
│
├─ IND-CDL += 1
│
├─ [TCDL-CODLAB(IND-CDL) ≠ PREV-CODLAB ?]  ← CHANGEMENT DE GROUPE LAB
│   └─ OUI :
│       ├─ [IND-CDL > 1 ?]
│       │   └─ OUI → WRITE-FINCDE (clore groupe lab précédent)
│       ├─ W-SUMQTE-CO=0, W-SUMQTE-MO=0
│       ├─ W-NB-CDL-CO=0, W-NB-CDL-MO=0
│       ├─ WRITE-REFCDE
│       ├─ WRITE-LINTXT
│       ├─ [W-LIB-HORAIRE ≠ SPACES ?] OUI → WRITE-TXTHOR (TXTCDE/RT)
│       └─ [W-LIB-CONTACT ≠ SPACES ?] OUI → WRITE-TXTCON (TXTCDE/BL)
│
├─ [TCDL-CODENR(IND-CDL) = '1' ?]
│   ├─ OUI → Remplir FIC-TYPE2 (usage interne uniquement, PAS d'écriture fichier)
│   └─ NON (= '3') :
│       ├─ Remplir FIC-TYPE3
│       ├─ LECTURE-MVT → W-DATPER
│       ├─ FORMAT-ORDERS-CDL (→ WRITE LINCDE)
│       │   ├─ [TCDL-CODDEP = 'CO' ?] OUI → WRITE TRANSCO + W-SUMQTE-CO += QTELIV
│       │   └─ [TCDL-CODDEP = 'MO' ?] OUI → WRITE TRANSMO + W-SUMQTE-MO += QTELIV
│       ├─ [TCDL-CODDEP = 'CO' ?] OUI → W-NB-CDL-CO += 1
│       └─ [TCDL-CODDEP = 'MO' ?] OUI → W-NB-CDL-MO += 1
│
├─ PREV-CODLAB = TCDL-CODLAB(IND-CDL)
└─ PREV-CODDEP = TCDL-CODDEP(IND-CDL)

```

Après la boucle (IND-CDL = NB-CDL) :

```
WRITE-FINCDE (clore le dernier groupe lab)
│
├─ [PREV-CODDEP = 'CO' ?] OUI → WRITE FINCDE vers TRANSCO
└─ [PREV-CODDEP = 'MO' ?] OUI → WRITE FINCDE vers TRANSMO

```

---

### 10bis.5 Tables de toutes les conditions du programme

#### Condition 1 — Validation des paramètres (TRAITEMENT-PRINCIPAL)

| Condition | Valeur | Résultat |
| --- | --- | --- |
| `P-CODREP = 'R'` ET `P-DATDEB = SPACES` | vrai | `STOP RUN` immédiat, aucun traitement |
| `P-CODREP = 'R'` ET `P-DATFIN = SPACES` | vrai | `STOP RUN` immédiat, aucun traitement |
| Toute autre combinaison | — | Continuer normalement |

#### Condition 2 — Choix du mode de sélection des commandes

| `P-CODREP` | Curseur utilisé | Filtre WHERE |
| --- | --- | --- |
| `'R'` | `CURCDE_R` | `DATEBL > W-DATDEB AND DATEBL < W-DATFIN` |
| Toute autre valeur | `CURCDE` | `DATPOR IS NULL AND :P-CODREP <> 'R'` |

#### Condition 3 — Résultat de FIND-LAST-DATE (recherche DATRAS dans D.PAR)

| SQLSTATE | `FLAG-TROUVE` | Action |
| --- | --- | --- |
| `'00000'`–`'01999'` | `'O'` | Convertir `FONCT` → `W-DATBIN` |
| `'02000'` | `'N'` | Appeler `CREAT-DATRAS` (INSERT + COMMIT) |
| Autre | — | `FIN-ANORMALE` |

#### Condition 4 — GESTION-SQLSTATE (après chaque instruction SQL)

| Plage SQLSTATE | `FLAG-TROUVE` | Action |
| --- | --- | --- |
| `'00000'`–`'01999'` | `'O'` (trouvé) | Continuer |
| `'02000'` | `'N'` (non trouvé) | Continuer |
| `'22002'` | `'O'` (trouvé) | Continuer (NULL indicator positionné mais traité comme succès) |
| `'02001'`–`'22001'` | — | `FIN-ANORMALE` (arrêt fatal) |
| `'22003'`–`'S9999'` | — | `FIN-ANORMALE` (arrêt fatal) |

#### Condition 5 — Résultat FETCH CURDIV (boucle laboratoires)

| SQLSTATE | Action |
| --- | --- |
| `'02000'` | `FLAG-FIN-RECHERCHE-DIV = 'O'` → sortir la boucle |
| `'00000'`–`'01999'` | `LECTURE-LAB` + ouvrir curseur commandes |
| `'22002'` | Même traitement que succès (indicateur NULL) |
| `'02001'`–`'22001'` | `FIN-ANORMALE` |
| `'22003'`–`'S9999'` | `FIN-ANORMALE` |

#### Condition 6 — Priorité des messages BL / RT (RECHERCHE-MES-BL-RT)

| Message BL présent | Message RT présent | `W-COMMENT` contient |
| --- | --- | --- |
| OUI | (peu importe) | Commentaire du message **BL** |
| NON | OUI | Commentaire du **premier** message RT (`ORDER BY NUMLIG LIMIT 1`) |
| NON | NON | `SPACES` |

#### Condition 7 — Rupture de groupe dans TRT-TROUVE-CDL

| `W-NUMLIG ≠ CDL-NUMLIG` | `W-CODART ≠ CDL-CODART` | Action |
| --- | --- | --- |
| OUI | (peu importe) | Rupture de groupe — si groupe précédent existe : `TRT-CDL-CUMULEE` |
| (peu importe) | OUI | Rupture de groupe — si groupe précédent existe : `TRT-CDL-CUMULEE` |
| NON | NON | Cumuler dans le groupe courant |

Existence du groupe précédent :

| `W-NUMLIG = SPACES` | `W-CODART = SPACES` | Groupe précédent existe ? |
| --- | --- | --- |
| OUI | OUI | NON — premier passage, rien à vider |
| NON | (peu importe) | OUI → `TRT-CDL-CUMULEE` |
| (peu importe) | NON | OUI → `TRT-CDL-CUMULEE` |

#### Condition 8 — Conservation du lot (TRAITEMENT-SANS-LOT / GESLOT)

| Valeur `W-GESLOT` | Action sur `TCDL-LOTFAB` (entrée CODENR='3') |
| --- | --- |
| `'4'` | Conserver `W-LOTFAB` tel quel |
| `'5'` | Conserver `W-LOTFAB` tel quel |
| Toute autre valeur | `W-LOTFAB = SPACES` puis `TCDL-LOTFAB = SPACES` |

Rappel : dans l'entrée CODENR=`'1'`, `TCDL-LOTFAB` est **toujours** à `SPACES` quelle que soit la valeur de `GESLOT`.

#### Condition 9 — Remplacement de dépôt pour le laboratoire 2951 (LECTURE-DEPLAB)

| `CODLAB IN CDL` | Action |
| --- | --- |
| `'2951'` | Appeler `LECTURE-DEPLAB` |
| Autre valeur | Pas d'appel, `CODDEP` inchangé |

Résultat de `LECTURE-DEPLAB` :

| Trouvé dans `D.PAR` (`CHAMPS='DEPLAB'`) | Action |
| --- | --- |
| OUI | `CDL.CODDEP = WFONCT(1:2)` (remplacement avant comptage CO/MO) |
| NON | `CDL.CODDEP` inchangé |

#### Condition 10 — Comptage CO / MO par ligne CDL (TRT-TROUVE-CDL)

| `CODDEP` (après éventuel remplacement DEPLAB) | Variable incrémentée |
| --- | --- |
| `'CO'` | `W-NB-CDL-CO += 1` |
| `'MO'` | `W-NB-CDL-MO += 1` |
| Autre valeur | Aucune incrémentation |

#### Condition 11 — Comptage des commandes avec lignes (STOCK-FIC-DESADV)

| `W-NB-CDL-CO` | `W-NB-CDL-MO` | Action |
| --- | --- | --- |
| `> 0` | (peu importe) | `NB-CDE-CO += 1` |
| (peu importe) | `> 0` | `NB-CDE-MO += 1` |
| `= 0` | `= 0` | Ni `NB-CDE-CO` ni `NB-CDE-MO` ne sont incrémentés |

#### Condition 12 — Dates NULL dans FETCH CURCDE (indicateurs négatifs)

| Indicateur | Valeur | Comportement |
| --- | --- | --- |
| `IDATEBL` | `-1` (NULL SQL) | `FIC-DATEBL = PARAM-DATE-TXT` (date courante YYYYMMDD) |
| `IDATEBL` | `≥ 0` | Conversion `CDE.DATEBL` binaire → `FIC-DATEBL` YYYYMMDD |
| `IDATEBP` | `-1` (NULL SQL) | `FIC-DATEBP = PARAM-DATE-TXT` (date courante YYYYMMDD) |
| `IDATEBP` | `≥ 0` | Conversion `CDE.DATEBP` binaire → `FIC-DATEBP` YYYYMMDD |
| `IDATREC` | `-1` (NULL SQL) | Uniquement dans `CURCDE` (pas d'indicateur dans `CURCDE_R`) |

#### Condition 13 — Construction de FIC-CODREP (STOCK-FIC-DESADV)

| `W-NUMCDE = FIC-CDELAB(1:7)` | Valeur de `FIC-CODREP` |
| --- | --- |
| OUI (la commande interne correspond à CDELAB) | `SPACES` |
| NON | `W-CODREP` (valeur lue depuis `CDE.CODREP`) |

#### Condition 14 — Changement de groupe lab dans STOCK-LIG-DESADV

| `TCDL-CODLAB(IND-CDL) ≠ PREV-CODLAB` | `IND-CDL > 1` | Action |
| --- | --- | --- |
| NON | (peu importe) | Aller directement à l'écriture LINCDE ou FIC-TYPE2 |
| OUI | NON (premier élément, IND-CDL=1) | Écrire REFCDE, LINTXT, TXTHOR?, TXTCON? (pas de FINCDE préalable) |
| OUI | OUI | WRITE-FINCDE (groupe précédent), puis REFCDE, LINTXT, TXTHOR?, TXTCON? |

#### Condition 15 — Sélection du type d'enregistrement CDL à écrire (STOCK-LIG-DESADV)

| `TCDL-CODENR(IND-CDL)` | Action |
| --- | --- |
| `'1'` | Renseigner `FIC-TYPE2` (usage interne seulement) — **aucune écriture** dans `TRANSCO`/`TRANSMO` |
| `'3'` | `LECTURE-MVT` + `FORMAT-ORDERS-CDL` → **écriture LINCDE** dans le fichier approprié |

#### Condition 16 — Routage LINCDE vers CO ou MO (FORMAT-ORDERS-CDL)

| `TCDL-CODDEP(IND-CDL)` | Fichier écrit | Accumulateur mis à jour |
| --- | --- | --- |
| `'CO'` | `ENREG-TRANSMITCO` | `W-SUMQTE-CO += TCDL-QTELIV` |
| `'MO'` | `ENREG-TRANSMITMO` | `W-SUMQTE-MO += TCDL-QTELIV` |
| Autre | Aucun fichier | Aucun accumulateur |

#### Condition 17 — Routage REFCDE, LINTXT, TXTCDE vers CO ou MO (WRITE-REFCDE, WRITE-LINTXT, WRITE-TXTHOR, WRITE-TXTCON)

| `TCDL-CODDEP(IND-CDL)` | Fichier écrit |
| --- | --- |
| `'CO'` | `ENREG-TRANSMITCO` |
| `'MO'` | `ENREG-TRANSMITMO` |

Ces 4 types d'enregistrements utilisent tous le même routage basé sur `TCDL-CODDEP(IND-CDL)`.

#### Condition 18 — Écriture de FINCDE (WRITE-FINCDE)

| `PREV-CODDEP` | Fichier écrit | Données utilisées |
| --- | --- | --- |
| `'CO'` | `ENREG-TRANSMITCO` | `W-SUMQTE-CO`, `W-NB-CDL-CO` |
| `'MO'` | `ENREG-TRANSMITMO` | `W-SUMQTE-MO`, `W-NB-CDL-MO` |
| Autre | Aucun fichier | — |

**Attention** : les deux tests (`PREV-CODDEP = 'CO'` et `PREV-CODDEP = 'MO'`) sont indépendants — en pratique `PREV-CODDEP` ne peut valoir qu'un seul dépôt à la fois, donc un seul FINCDE est écrit par appel.

#### Condition 19 — Écriture de TXTHOR (texte horaire)

| `W-LIB-HORAIRE` | Action |
| --- | --- |
| `= SPACES` (aucun article QU00013 dans CDL) | Pas d'écriture TXTCDE/RT |
| `≠ SPACES` | Écrire `TXTCDE` avec `TYPDOC='RT'` |

#### Condition 20 — Écriture de TXTCON (texte contact)

| `W-LIB-CONTACT` | Action |
| --- | --- |
| `= SPACES` (aucun article QU000132 dans CDL) | Pas d'écriture TXTCDE/BL |
| `≠ SPACES` | Écrire `TXTCDE` avec `TYPDOC='BL'` |

#### Condition 21 — Valeur du champ TRAFIC dans REFCDE

| `CDE.TRAFIC` | `ENR-REFCDE-TRAFIC` |
| --- | --- |
| `SPACES` | `'N'` |
| Toute autre valeur | Valeur de `CDE.TRAFIC` telle quelle |

#### Condition 22 — Valeur du champ NUMDOC dans REFCDE (RECH-NUMDOC)

| Résultat de la recherche dans `P.DOCENT` | `ENR-REFCDE-NUMDOC` |
| --- | --- |
| Trouvé | `W-NUMDOC` (10 caractères depuis `DOCENT`) |
| Non trouvé (`SQLSTATE '02000'`) | `SPACES` (10 espaces) |

#### Condition 23 — Construction de NUMLIG dans TABLE-CDL

Appliqué à la fois dans `TRT-CDL-CUMULEE` (entrée 1) et `TRAITEMENT-SANS-LOT` (entrée 2) :

| `W-REFLIG(1:4)` | `TCDL-NUMLIG` affecté |
| --- | --- |
| `≠ SPACES` | `W-REFLIG(1:4)` (4 premiers caractères de REFLIG) |
| `= SPACES` | `W-NUMLIG` |

Dans `TRAITEMENT-SANS-LOT` uniquement, si `W-REFLIG(1:4) = SPACES` :

- `W-REFLIG(1:4) = W-NUMLIG` (modification de W-REFLIG)
- Puis `TCDL-NUMLIG = W-REFLIG(1:4)` (= W-NUMLIG)

#### Condition 24 — Erreurs d'ouverture de fichier (OPEN-FILES)

| Fichier | `CODE-STATUS ≠ '00'` | Message erreur | Action |
| --- | --- | --- | --- |
| `FIC-MESSAGES` (INPUT) | OUI | `NUM-MSG='1001'`, `PARAM-01='RMS_MSG'` | `FIN-ANOOPEN` → STOP RUN |
| `FIC-ANOMALIES` (OUTPUT) | OUI | `NUM-MSG='1001'`, `PARAM-01='RMS_ANO'` | `FIN-ANOOPEN` → STOP RUN |
| `FIC-TRANSMITCO` (OUTPUT) | OUI | `NUM-MSG='1001'`, `PARAM-01='TRANSMIT'` | `FIN-ANOOPEN` → STOP RUN |
| `FIC-TRANSMITMO` (OUTPUT) | OUI | `NUM-MSG='1001'`, `PARAM-01='TRANSMIT'` | `FIN-ANOOPEN` → STOP RUN |
| `FIC-MAJ` (OUTPUT) | OUI | `NUM-MSG='1001'`, `PARAM-01='FIC-REJ'` | `FIN-ANORMALE` → STOP RUN |

#### Condition 25 — Erreurs de fermeture de fichier (CLOSE-FILES)

| Fichier | `CODE-STATUS ≠ '00'` | Message | Action |
| --- | --- | --- | --- |
| `FIC-TRANSMITCO` | OUI | `NUM-MSG='1004'`, `PARAM-01='TRANSMIT'` | `FIN-ANOFERME` → STOP RUN |
| `FIC-TRANSMITMO` | OUI | `NUM-MSG='1004'`, `PARAM-01='TRANSMIT'` | `FIN-ANOFERME` → STOP RUN |
| `FIC-MAJ` | OUI | `NUM-MSG='1004'`, `PARAM-01='FIC-REJ'` | `FIN-ANOFERME` → STOP RUN |
| `FIC-ANOMALIES` | OUI | `NUM-MSG='1004'`, `PARAM-01='RMS_ANO'` | `FIN-ANOFERME` → STOP RUN |
| `FIC-MESSAGES` | OUI | `NUM-MSG='1004'`, `PARAM-01='RMS_MSG'` | `FIN-ANOFERME` → STOP RUN |

#### Condition 26 — Erreur en écriture du fichier MAJ (STOCK-FIC-DESADV / FIND-LAST-DATE)

| `CODE-STATUS` | Action |
| --- | --- |
| `'00'` | Continuer normalement |
| Toute autre valeur | `NUM-MSG='1003'`, `PARAM-01='RMS_MAJ'` → `GO TO FIN-ANOFERME` |

#### Condition 27 — Indicateur NULL CIPPDV dans CLI

| Indicateur `ICIPPDV` | Action |
| --- | --- |
| `< 0` (NULL SQL) | `CLI.CIPPDV = 0` |
| `≥ 0` | Conserver la valeur lue |

Appliqué dans `get-cli`, `get-cli-cde`, `get-cli-pay` et `LECTURE-CLI`.

---

### 10bis.6 Séquence détaillée des enregistrements avec conditions de déclenchement

```
Fichier TRANSCO et/ou TRANSMO :

[Début de programme]
  → DEBCDE                    ← toujours, 1 seul fois par fichier

[Pour chaque laboratoire dans CURDIV]
  [Pour chaque commande CDE/CDO éligible]
    [Pour chaque entrée dans TABLE-CDL]

      Si c'est la PREMIÈRE entrée OU si CODLAB change :
        → REFCDE              ← routé vers CO ou MO selon TCDL-CODDEP(IND-CDL)
        → LINTXT              ← idem
        → TXTCDE/RT           ← seulement si W-LIB-HORAIRE ≠ SPACES
        → TXTCDE/BL           ← seulement si W-LIB-CONTACT ≠ SPACES
        (si changement de lab et pas première entrée)
        → FINCDE              ← pour le dépôt précédent (PREV-CODDEP)

      Si CODENR = '3' :
        → LINCDE              ← routé vers CO ou MO selon TCDL-CODDEP

    [Fin des entrées TABLE-CDL de la commande]
    → FINCDE                  ← pour le dernier dépôt vu (PREV-CODDEP)

[Fin de tous les laboratoires]
  → FINMES                    ← NB-CDE-CO vers TRANSCO, NB-CDE-MO vers TRANSMO
```

---

## 11. Séquence des enregistrements dans les fichiers de sortie

### Structure globale du fichier

```
DEBCDE                          ← 1 seul, en tête de fichier
[commandes...]
FINMES                          ← 1 seul, en pied de fichier
```

### Structure par commande

```
(lors du premier CDL ou changement de laboratoire) :
  REFCDE                        ← en-tête de groupe lab
  LINTXT                        ← commentaire (toujours présent)
  TXTCDE/RT                     ← horaire (QU00013), si non vide
  TXTCDE/BL                     ← contact (QU000132), si non vide

Pour chaque entrée CODENR='3' dans TABLE-CDL :
  LINCDE                        ← ligne article

(lors du changement de laboratoire ou en fin) :
  FINCDE                        ← pied de groupe lab
```

### Règle de routage vers CO ou MO

Chaque enregistrement est écrit dans le fichier correspondant au dépôt de la ligne CDL concernée (`TCDL-CODDEP`). Une commande avec des lignes dans les deux dépôts génère des enregistrements dans les deux fichiers.

---

## 12. Architecture Java 8 recommandée

### 12.1 Remplacement des REDEFINES

Chaque variante REDEFINES devient une classe séparée implémentant une interface commune :

```java
public interface TransmissionRecord {
    /**
     * Retourne exactement 197 caractères formatés à largeur fixe.
     */
    String format();
}
```

Classes à créer :
- `DebcdeRecord` — TYPMES = `"DEBCDE"`
- `RefcdeRecord` — TYPMES = `"REFCDE"`
- `LintxtRecord` — TYPMES = `"LINTXT"`
- `TxtcdeRecord` — TYPMES = `"TXTCDE"`
- `LincdeRecord` — TYPMES = `"LINCDE"`
- `FincdeRecord` — TYPMES = `"FINCDE"`
- `FinmesRecord` — TYPMES = `"FINMES"`

### 12.2 Remplacement des dates VMS

```java
public interface LegacyDateCodec {
    /** Convertit en ASCII VMS "DD-MON-YYYY HH:MI:SS.CC" */
    String toVmsAscii(LocalDateTime dt);

    /** Parse depuis ASCII VMS "DD-MON-YYYY HH:MI:SS.CC" */
    LocalDateTime fromVmsAscii(String s);

    /** Formater en "YYYYMMDD" */
    String toYyyyMmDd(LocalDateTime dt);

    /** Formater en "YYMMDD" (pour DATCDE dans REFCDE — voir T10) */
    String toYyMmDd(LocalDateTime dt);
}
```

### 12.3 Remplacement de TABLE-CDL

```java
public class LigneCumulee {
    private final String numlig;
    private final String codart;
    private final String artlab;
    private final String artsai;
    private final String libell;
    private final String artcip;
    private final String lotfab;
    private final long   qteliv;   // = cumul QTLCDE
    private final long   qtcgrt;   // = cumul QTCGRT + QTCECH
    private final long   qtecde;   // quantité commandée
    private final long   qtlcde;
    private final char   codenr;   // '1' = récapitulatif, '3' = lot
    private final String codlab;
    private final String coddep;   // "CO" ou "MO"
    private final String codlablab;
}
```

La liste `List<LigneCumulee>` remplace le tableau `TABLE-CDL OCCURS 1000`.

### 12.4 Remplacement des flags SQLSTATE

```java
// Requête retournant 0 ou 1 résultat
Optional<T> findOne(PreparedStatement stmt) throws FatalSqlException;

// Requête retournant plusieurs lignes
List<T> findAll(PreparedStatement stmt) throws FatalSqlException;
```

Utiliser `Optional<T>` pour les requêtes pouvant ne rien retourner. Utiliser des exceptions vérifiées pour les erreurs SQL fatales.

### 12.5 Formatage à largeur fixe

```java
public final class FixedField {

    /**
     * Format COBOL PIC X(n) : texte cadré à gauche, complété par des espaces à droite.
     * Si value est null ou plus court que width, compléter par des espaces.
     * Si value est plus long que width, tronquer à droite.
     */
    public static String alphaLeft(String value, int width) {
        if (value == null) value = "";
        return String.format("%-" + width + "s", value).substring(0, width);
    }

    /**
     * Format COBOL PIC 9(n) : nombre cadré à droite, complété par des zéros à gauche.
     */
    public static String numericRight(long value, int width) {
        return String.format("%0" + width + "d", value);
    }
}
```

### 12.6 Architecture générale du programme Java

```
ExtcdeApplication (main)
  ├── ParameterReader           — lecture des paramètres (P-CODDEP, P-CODLAB, etc.)
  ├── VmsDateCodecImpl          — conversions dates VMS ↔ LocalDateTime
  ├── TransactionManager        — gestion READ ONLY / READ WRITE JDBC
  ├── FileManager               — ouverture/fermeture des fichiers RMS_TRANSCO, RMS_TRANSMO, RMS_MAJ
  ├── DatrasRepository          — FIND-LAST-DATE + CREAT-DATRAS (D.PAR CHAMPS='DATRAS')
  ├── CdeProcessor              — boucle principale sur CURCDE / CURCDE_R
  │   ├── CdlAccumulator        — lecture CURCDL, accumulation groupes, construction TABLE-CDL
  │   ├── MesRepository         — RECHERCHE-MES-BL-RT (D.MES)
  │   ├── ArtRepository         — LECTURE-ART (D.ART)
  │   ├── CliRepository         — LECTURE-CLI, get-cli-cde, get-cli-pay
  │   ├── ParRepository         — LECTURE-DEPLAB, LECTURE-PAR-LABLAB, LECTURE-PAR-TYPLAB
  │   ├── DocEntRepository      — RECH-NUMDOC (P.DOCENT)
  │   ├── MvtRepository         — LECTURE-MVT (D.MVT)
  │   └── TransmissionWriter    — écriture REFCDE, LINTXT, TXTCDE, LINCDE, FINCDE
  └── GlobalWriter              — écriture DEBCDE, FINMES
```

### 12.7 Points d'attention pour l'écriture des tests

1. **Test de la logique NOMLIV** (T3) : vérifier que `clicsp = "000123"` produit `"123   "` + 29 espaces en position 1–35 de `ENR-REFCDE-NOMLIV`.
2. **Test du double NB-CDL** (T1) : pour N groupes CDL distincts, vérifier que `tableCdl.size() == 2 * N`.
3. **Test TRT-NOT-TROUVE-CDL** (T2) : simuler un `ResultSet` épuisé et vérifier que le dernier groupe est bien flushé avec les valeurs de la dernière ligne lue.
4. **Test FINCDE routing** (T4) : pour un groupe lab avec lignes CO en dernier, vérifier que seul TRANSCO reçoit FINCDE ; TRANSMO le reçoit lors du déclenchement suivant.
5. **Test DATCDE format YYMMDD** (T10) : vérifier que `"20150616"` → `"150616"` (positions 3–8 en COBOL = index 2 à 8 en Java).
6. **Test LINCDE QTCCDE** (T7) : vérifier que la quantité écrite dans `ENR-LINCDE-QTCCDE` est `qteliv` (livré) et non `qtecde` (commandé).
7. **Test TRAFIC** : vérifier que la valeur espaces dans `CDE.TRAFIC` produit `"N"` dans `ENR-REFCDE-TRAFIC`.
8. **Test priorité BL/RT** (§9.8) : BL présent + RT présent → `W-COMMENT` = commentaire BL. BL absent + RT présent → `W-COMMENT` = commentaire RT.

---

## Annexe — Correspondance des paragraphes COBOL vers méthodes Java

| Paragraphe COBOL | Méthode Java équivalente |
| --- | --- |
| `TRAITEMENT-PRINCIPAL` | `main()` ou `ExtcdeApplication.run()` |
| `TRAITEMENT` | `CdeProcessor.process()` |
| `RECHERCHE-DIV` | `CdeProcessor.processLab(String codlab)` |
| `FIND-LAST-DATE` | `DatrasRepository.findLastDate(String codlab)` |
| `CREAT-DATRAS` | `DatrasRepository.createDatras(String codlab, LocalDateTime now)` |
| `LECTURE-DES-CDES` | `CdeProcessor.fetchOrders(Connection, String codlab)` |
| `TRT-TROUVE` | `CdeProcessor.processOrder(CdeRow)` |
| `RECHERCHE-MES-BL-RT` | `MesRepository.findBlOrRt(String codmes)` |
| `TRAIT-FAT` | `CdlAccumulator.loadCdl(String numcde, String numral)` |
| `LECTURE-DES-CDL` | `CdlAccumulator.fetchCdl()` |
| `TRT-TROUVE-CDL` | `CdlAccumulator.accumulateCdlRow(CdlRow)` |
| `TRT-NOT-TROUVE-CDL` | `CdlAccumulator.flushLastGroup(CdlRow lastRow)` |
| `TRT-CDL-CUMULEE` | `CdlAccumulator.flushGroup()` |
| `TRAITEMENT-SANS-LOT` | `CdlAccumulator.addLotEntry()` |
| `STOCK-FIC-DESADV` | `TransmissionWriter.writeOrder(List<LigneCumulee>)` |
| `STOCK-LIG-DESADV` | `TransmissionWriter.writeCdlEntry(LigneCumulee, int index)` |
| `FORMAT-ORDERS-CDL` | `TransmissionWriter.writeLincde(LigneCumulee)` |
| `WRITE-REFCDE` | `TransmissionWriter.writeRefcde(...)` |
| `WRITE-LINTXT` | `TransmissionWriter.writeLintxt(String comment)` |
| `WRITE-TXTHOR` | `TransmissionWriter.writeTxtHoraire(String lib)` |
| `WRITE-TXTCON` | `TransmissionWriter.writeTxtContact(String lib)` |
| `WRITE-FINCDE` | `TransmissionWriter.writeFincde()` |
| `LECTURE-DEPLAB` | `ParRepository.findDeplab(String codlab, String depart)` |
| `GESTION-SQLSTATE` | Gestion d'exception JDBC inline |
| `FIN-ANORMALE` | `throw new FatalProcessingException(...)` |

---

*Fin du document — Toute l'information nécessaire à la réécriture en Java 8 du programme `D02_EXTCDE_CRMCSP1` est contenue dans ce document.*
