# Réécriture de D02_EXTCDE_CRMCSP1 en Java 8 avec TDD

> Ce document est le guide de migration. Il s'appuie sur l'analyse complète disponible dans `ANALYSE_D02_EXTCDE_CRMCSP1.md`. En cas de doute sur un comportement précis, consulter l'analyse en premier.

---

## 1. Objectif

Ce document décrit le comportement du batch COBOL `D02_EXTCDE_CRMCSP1` et propose une stratégie de réécriture en Java 8 pilotée par les tests.

L'objectif prioritaire est la **parité fonctionnelle et binaire** avec le programme existant. La modernisation du modèle ou des règles ne doit commencer qu'après obtention d'une suite de tests de caractérisation stable.

---

## 2. Rôle du programme

Le programme construit deux fichiers positionnels de confirmation d'expédition :

- `RMS_TRANSCO` pour le dépôt `CO` ;
- `RMS_TRANSMO` pour le dépôt `MO`.

Il produit aussi :

- `RMS_MAJ`, fichier des commandes traitées et de la date de traitement ;
- `RMS_ANO`, fichier des anomalies ;
- des messages de diagnostic sur la sortie standard.

Les données viennent principalement de la base `BD_DEPOT`, avec des lectures complémentaires dans `BD_TRANSPORT` et `BD_PDF`.

---

## 3. Paramètres d'entrée

Le groupe `PARAM` contient :

| Champ | Taille | Rôle |
| --- | --- | --- |
| `P-CODDEP` | 2 | Dépôt demandé |
| `P-CODLAB` | 4 | Laboratoire à traiter |
| `P-CODREP` | 1 | Mode de reprise : `'R'` = plage de dates, sinon mode normal |
| `P-DATDEB` | 23 | Date de début au format ASCII VMS `"DD-MON-YYYY HH:MI:SS.CC"` |
| `P-DATFIN` | 23 | Date de fin au format ASCII VMS |
| `P-QUERETOUR` | 1 | Paramètre transmis par l'environnement |

### Validation obligatoire

```text
SI P-CODREP = 'R' ET (P-DATDEB = SPACES OU P-DATFIN = SPACES)
  → STOP RUN immédiat avant tout traitement
```

En Java : lever une `IllegalArgumentException` avant toute ouverture de fichier ou de connexion.

---

## 4. Sources de données

| Alias | Base | Tables utilisées |
| --- | --- | --- |
| `D` | `BD_DEPOT` | `CDE`, `CDO`, `CDL`, `ART`, `MES`, `CLI`, `CCL`, `PAR`, `LAB`, `MVT` |
| `T` | `BD_TRANSPORT` | `STR`, `REC` |
| `S` | `BD_STATS` | Déclarée, non utilisée dans le flux principal |
| `P` | `BD_PDF` | `DOCENT` |

### Sélection des commandes

Deux curseurs existent :

- Mode normal (`P-CODREP ≠ 'R'`) — curseur `CURCDE` : commandes du laboratoire au statut `CRV`, avec `DATPOR IS NULL` ;
- Mode reprise (`P-CODREP = 'R'`) — curseur `CURCDE_R` : commandes `CRV` dont `DATEBL` est strictement comprise entre `P-DATDEB` et `P-DATFIN` (bornes exclues).

**Différence importante entre les deux curseurs** : dans `CURCDE`, la colonne `DATREC` est récupérée avec un indicateur NULL. Dans `CURCDE_R`, elle n'a pas d'indicateur NULL.

Les commandes sont lues depuis `CDE` joint à `CDO` par `(NUMCDE, NUMRAL)`.

### Sélection des lignes

Les lignes viennent de `CDL` joint à `ART` par `CODART` avec les contraintes suivantes :

- même `(NUMCDE, NUMRAL)` que la commande ;
- `ART.CODLAB = P-CODLAB` ;
- `CODSSS` vide ;
- `QTCCDE > 0` ;
- `CODDEP` non vide ;
- tri par `CDL.CODDEP`, puis `CDL.CODLAB`.

---

## 5. Déroulement du batch

```text
TRAITEMENT-PRINCIPAL
│
├─ Validation paramètres → STOP RUN si reprise sans dates
├─ Initialisation dates VMS (LIB$DATE_TIME, SYS$BINTIM, D00_YYYYMMDD)
├─ SET TRANSACTION READ ONLY
├─ OPEN-FILES (5 fichiers)
│
├─ [P-CODREP = 'R' ?]
│   ├─ OUI → convertir P-DATDEB/P-DATFIN en binaire VMS
│   └─ NON → FIND-LAST-DATE (recherche DATRAS dans D.PAR)
│               ├─ Trouvé → convertir date → W-DATBIN
│               └─ Non trouvé → CREAT-DATRAS (INSERT COMMIT, reprendre en READ ONLY)
│
└─ TRAITEMENT
    ├─ Écrire DEBCDE → TRANSCO + TRANSMO
    ├─ Ouvrir CURDIV (lab) → boucle RECHERCHE-DIV
    │   └─ Pour chaque laboratoire :
    │       ├─ LECTURE-LAB
    │       ├─ [P-CODREP='R' ?] → CURCDE_R sinon CURCDE
    │       └─ Pour chaque commande :
    │           ├─ LECTURE-CLI + RECHERCHE-MES-BL-RT
    │           └─ TRAIT-FAT
    │               ├─ Ouvrir CURCDL → accumulation TABLE-CDL
    │               ├─ RECHERCHE-HORAIRE (QU00013)
    │               ├─ RECHERCHE-CONTACT (QU000132)
    │               └─ STOCK-FIC-DESADV → écriture fichiers
    ├─ Fermer CURDIV
    ├─ Écrire FINMES (NB-CDE-CO) → TRANSCO
    └─ Écrire FINMES (NB-CDE-MO) → TRANSMO
```

### Séquence des enregistrements dans les fichiers de sortie

```text
DEBCDE                      ← une seule fois, tête de fichier

  Pour chaque commande, pour chaque groupe laboratoire dans TABLE-CDL :
    REFCDE                  ← une fois par changement de CODLAB
    LINTXT                  ← toujours (même si vide)
    TXTCDE/RT               ← seulement si article QU00013 présent
    TXTCDE/BL               ← seulement si article QU000132 présent

    Pour chaque entrée CODENR='3' dans TABLE-CDL :
      LINCDE

    FINCDE                  ← une fois par groupe laboratoire (routé via PREV-CODDEP)

FINMES                      ← une seule fois, pied de fichier
```

---

## 6. Format des fichiers transmis

Chaque enregistrement a une longueur fixe de **197 caractères** :

- type de message : 6 caractères ;
- séparateur/filler : 1 caractère (toujours espace) ;
- corps : 190 caractères.

Les chaînes COBOL sont complétées par des espaces. Les numériques d'affichage sont alignés et complétés selon les règles COBOL. La réécriture Java doit produire exactement les mêmes octets, y compris les espaces finaux.

### Gestion de `REDEFINES`

En COBOL, `REDEFINES` superpose plusieurs descriptions sur la **même zone mémoire**. Il ne réserve pas une nouvelle zone — il donne simplement plusieurs noms différents aux mêmes octets.

**Conséquence directe** : les 7 variantes (`DEBCDE`, `REFCDE`, `LINTXT`, `TXTCDE`, `LINCDE`, `FINCDE`, `FINMES`) partagent toutes les mêmes 190 octets du corps. On ne les additionne jamais. Un seul type est utilisé à la fois par enregistrement.

#### Source COBOL exact — structure ENREG-TRANSMIT

```cobol
01  ENREG-TRANSMIT.
   02 ENR-TYPMES  PIC X(6).          *> positions 1-6  : type d'enregistrement
   02 ENR-FILLER  PIC X.             *> position 7     : séparateur (toujours espace)

   *> ── Corps 190 octets (positions 8-197) ──────────────────────────────
   02 ENR-DEBCDE.                    *> définition de base
      03 ENR-DEBCDE-EMETEUR  PIC X(35).
      03 ENR-DEBCDE-RECEPTE  PIC X(35).
      03 ENR-DEBCDE-TEST     PIC X(1).
      03 ENR-DEBCDE-FILLER   PIC X(100).
      03 FILLER              PIC X(19).

   02 ENR-REFCDE REDEFINES ENR-DEBCDE.   *> même 190 octets, vue différente
      03 ENR-REFCDE-DATCDE    PIC X(06).
      03 FILLER               PIC X.
      03 ENR-REFCDE-CDELAB    PIC X(22).
      03 FILLER               PIC X.
      03 ENR-REFCDE-REFCDE    PIC X(35).
      03 FILLER               PIC X.
      03 ENR-REFCDE-TYPCDE    PIC X(02).
      03 FILLER               PIC X.
      03 ENR-REFCDE-CLICSP    PIC 9(06).
      03 FILLER               PIC X.
      03 ENR-REFCDE-NOMLIV    PIC X(35).
      03 ENR-REFCDE-FILLER    PIC X.
      03 ENR-REFCDE-CODOPE    PIC X(3).
      03 FILLER               PIC X.
      03 ENR-REFCDE-NUMDOC    PIC X(10).
      03 FILLER               PIC X.
      03 ENR-REFCDE-CDESAISIE PIC X.
      03 FILLER               PIC X.
      03 ENR-REFCDE-DATREC    PIC X(23).
      03 FILLER               PIC X.
      03 ENR-REFCDE-TRAFIC    PIC X.
      03 FILLER               PIC X.
      03 ENR-REFCDE-DATEBP    PIC X(8).
      03 FILLER               PIC X.
      03 ENR-REFCDE-CODSAI    PIC X(7).
      03 FILLER               PIC X.
      03 ENR-REFCDE-CODSTR    PIC X(4).
      03 FILLER               PIC X.
      03 ENR-REFCDE-STREXP    PIC X(4).
      03 FILLER               PIC X.
      03 ENR-REFCDE-CODREP    PIC X(8).

   02 ENR-LINTXT REDEFINES ENR-DEBCDE.
      03 ENR-LINTXT-MESSAGE   PIC X(114).
      *> octets 115-190 du corps : non adressés (espaces via INITIALIZE)

   02 ENR-TXTCDE REDEFINES ENR-DEBCDE.
      03 ENR-TXTCDE-MESSAGE   PIC X(80).
      03 FILLER               PIC X.
      03 ENR-TXTCDE-TYPDOC    PIC X(02).
      *> octets 84-190 du corps : non adressés (espaces via INITIALIZE)

   02 ENR-LINCDE REDEFINES ENR-DEBCDE.
      03 ENR-LINCDE-CODART    PIC X(10).
      03 FILLER               PIC X.
      03 ENR-LINCDE-QTCCDE    PIC 9(07).
      03 FILLER               PIC X.
      03 ENR-LINCDE-QTCGRT    PIC 9(07).
      03 FILLER               PIC X.
      03 ENR-LINCDE-LIBELL    PIC X(35).
      03 FILLER               PIC X.
      03 ENR-LINCDE-CODLABLAB PIC X(4).
      03 FILLER               PIC X(38).
      *> octets 106-190 du corps : non adressés (espaces via INITIALIZE)

   02 ENR-FINCDE REDEFINES ENR-DEBCDE.
      03 ENR-FINCDE-SUMQTE    PIC 9(08).
      03 FILLER               PIC X.
      03 ENR-FINCDE-NBLIG     PIC 9(08).
      03 FILLER               PIC X(97).
      *> octets 115-190 du corps : non adressés (espaces via INITIALIZE)

   02 ENR-FINMES REDEFINES ENR-DEBCDE.
      03 ENR-FINMES-NBCDE     PIC 9(08).
      03 FILLER               PIC X(97).
      *> octets 106-190 du corps : non adressés (espaces via INITIALIZE)
```

> **Règle INITIALIZE** : avant de construire chaque enregistrement, le programme exécute `INITIALIZE ENREG-TRANSMIT` qui met tous les champs alphabétiques à espaces et numériques à zéros. Les octets non adressés par une variante sont donc des espaces.

---

### Layout octet par octet — les 7 types d'enregistrement

Les positions ci-dessous sont **absolues** dans l'enregistrement de 197 caractères.

#### DEBCDE (197 octets)

| Pos. début | Pos. fin | Long. | Nom COBOL | PIC | Contenu | Java / SQL |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 6 | 6 | `ENR-TYPMES` | `X(6)` | `"DEBCDE"` | `String` — littéral `"DEBCDE"` |
| 7 | 7 | 1 | `ENR-FILLER` | `X` | espace | `' '` — littéral |
| 8 | 42 | 35 | `ENR-DEBCDE-EMETEUR` | `X(35)` | `"183 CSP"` + 28 espaces | `String emeteur = "183 CSP"` — littéral |
| 43 | 77 | 35 | `ENR-DEBCDE-RECEPTE` | `X(35)` | `"183 CSP"` + 28 espaces | `String recepte = "183 CSP"` — littéral |
| 78 | 78 | 1 | `ENR-DEBCDE-TEST` | `X` | `"P"` | `String test = "P"` — littéral |
| 79 | 178 | 100 | `ENR-DEBCDE-FILLER` | `X(100)` | espaces | espaces — INITIALIZE |
| 179 | 197 | 19 | `FILLER` | `X(19)` | espaces | espaces — INITIALIZE |

**Total : 197** ✓

---

#### REFCDE (197 octets)

| Pos. début | Pos. fin | Long. | Nom COBOL | PIC | Contenu | Java / SQL |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 6 | 6 | `ENR-TYPMES` | `X(6)` | `"REFCDE"` | `String` — littéral `"REFCDE"` |
| 7 | 7 | 1 | `ENR-FILLER` | `X` | espace | `' '` |
| 8 | 13 | 6 | `ENR-REFCDE-DATCDE` | `X(6)` | date commande **YYMMDD** | `String datcde` — `D.CDE.DATCDE` converti YYYYMMDD → `ficDatecde.substring(2,8)` |
| 14 | 14 | 1 | `FILLER` | `X` | espace | `' '` |
| 15 | 36 | 22 | `ENR-REFCDE-CDELAB` | `X(22)` | référence laboratoire commande | `String cdelab` — `D.CDE.CDELAB` |
| 37 | 37 | 1 | `FILLER` | `X` | espace | `' '` |
| 38 | 72 | 35 | `ENR-REFCDE-REFCDE` | `X(35)` | `W-REFCDE` | `String refcde` — `D.CDE.REFCDE` (hôte `:w-refcde`) |
| 73 | 73 | 1 | `FILLER` | `X` | espace | `' '` |
| 74 | 75 | 2 | `ENR-REFCDE-TYPCDE` | `X(2)` | type commande | `String typcde` — `D.CDE.TYPCDE` |
| 76 | 76 | 1 | `FILLER` | `X` | espace | `' '` |
| 77 | 82 | 6 | `ENR-REFCDE-CLICSP` | `9(6)` | code client CSP (zéros à gauche) | `long clicsp` — `D.CDE.CLICSP` |
| 83 | 83 | 1 | `FILLER` | `X` | espace | `' '` |
| 84 | 118 | 35 | `ENR-REFCDE-NOMLIV` | `X(35)` | **astuce CLICSP décalé — voir §8** | `String nomliv` — calculé depuis `clicsp` (voir §8) |
| 119 | 119 | 1 | `ENR-REFCDE-FILLER` | `X` | espace | `' '` |
| 120 | 122 | 3 | `ENR-REFCDE-CODOPE` | `X(3)` | code opération | `String codope` — `D.CDE.CODOPE` |
| 123 | 123 | 1 | `FILLER` | `X` | espace | `' '` |
| 124 | 133 | 10 | `ENR-REFCDE-NUMDOC` | `X(10)` | N° doc `P.DOCENT`, ou 10 espaces | `String numdoc` — `P.DOCENT.NUMDOC` ou espaces |
| 134 | 134 | 1 | `FILLER` | `X` | espace | `' '` |
| 135 | 135 | 1 | `ENR-REFCDE-CDESAISIE` | `X` | indicateur de saisie | `String cdesaisie` — `D.CDE.CDESAISIE` |
| 136 | 136 | 1 | `FILLER` | `X` | espace | `' '` |
| 137 | 159 | 23 | `ENR-REFCDE-DATREC` | `X(23)` | date réception ASCII VMS complet | `String datrec` — `D.CDE.DATREC` converti ASCII VMS 23 cars |
| 160 | 160 | 1 | `FILLER` | `X` | espace | `' '` |
| 161 | 161 | 1 | `ENR-REFCDE-TRAFIC` | `X` | `TRAFIC` de CDE, ou `'N'` si vide | `String trafic` — `D.CDE.TRAFIC` ou `"N"` |
| 162 | 162 | 1 | `FILLER` | `X` | espace | `' '` |
| 163 | 170 | 8 | `ENR-REFCDE-DATEBP` | `X(8)` | date livraison prévue YYYYMMDD | `String datebp` — `D.CDE.DATEBP` converti YYYYMMDD, ou date du jour si NULL |
| 171 | 171 | 1 | `FILLER` | `X` | espace | `' '` |
| 172 | 178 | 7 | `ENR-REFCDE-CODSAI` | `X(7)` | code saisie | `String codsai` — `D.CDE.CODSAI` |
| 179 | 179 | 1 | `FILLER` | `X` | espace | `' '` |
| 180 | 183 | 4 | `ENR-REFCDE-CODSTR` | `X(4)` | code sous-traitant transport | `String codstr` — `D.CDE.CODSTR` |
| 184 | 184 | 1 | `FILLER` | `X` | espace | `' '` |
| 185 | 188 | 4 | `ENR-REFCDE-STREXP` | `X(4)` | structure expédition | `String strexp` — `D.CDE.STREXP` |
| 189 | 189 | 1 | `FILLER` | `X` | espace | `' '` |
| 190 | 197 | 8 | `ENR-REFCDE-CODREP` | `X(8)` | code reprise | `String codrep` — `D.CDE.CODREP` |

**Total : 197** ✓ (corps = 6+1+22+1+35+1+2+1+6+1+35+1+3+1+10+1+1+1+23+1+1+1+8+1+7+1+4+1+4+1+8 = 190)

---

#### LINTXT (197 octets)

| Pos. début | Pos. fin | Long. | Nom COBOL | PIC | Contenu | Java / SQL |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 6 | 6 | `ENR-TYPMES` | `X(6)` | `"LINTXT"` | `String` — littéral `"LINTXT"` |
| 7 | 7 | 1 | `ENR-FILLER` | `X` | espace | `' '` |
| 8 | 121 | 114 | `ENR-LINTXT-MESSAGE` | `X(114)` | commentaire MES (BL ou RT) | `String message` — `D.MES.COMMENT` (via `W-COMMENT`) |
| 122 | 197 | 76 | *(non adressé)* | — | espaces (via INITIALIZE) | espaces |

**Total : 197** ✓

---

#### TXTCDE (197 octets)

| Pos. début | Pos. fin | Long. | Nom COBOL | PIC | Contenu | Java / SQL |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 6 | 6 | `ENR-TYPMES` | `X(6)` | `"TXTCDE"` | `String` — littéral `"TXTCDE"` |
| 7 | 7 | 1 | `ENR-FILLER` | `X` | espace | `' '` |
| 8 | 87 | 80 | `ENR-TXTCDE-MESSAGE` | `X(80)` | texte horaire (`W-LIB-HORAIRE`) ou contact (`W-LIB-CONTACT`) | `String message` — `D.CDL.LIBELL` (CODART=`QU00013` ou `QU000132`) |
| 88 | 88 | 1 | `FILLER` | `X` | espace | `' '` |
| 89 | 90 | 2 | `ENR-TXTCDE-TYPDOC` | `X(2)` | `"RT"` (horaire) ou `"BL"` (contact) | `"RT"` ou `"BL"` — littéral |
| 91 | 197 | 107 | *(non adressé)* | — | espaces (via INITIALIZE) | espaces |

**Total : 197** ✓

---

#### LINCDE (197 octets)

| Pos. début | Pos. fin | Long. | Nom COBOL | PIC | Contenu | Java / SQL |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 6 | 6 | `ENR-TYPMES` | `X(6)` | `"LINCDE"` | `String` — littéral `"LINCDE"` |
| 7 | 7 | 1 | `ENR-FILLER` | `X` | espace | `' '` |
| 8 | 17 | 10 | `ENR-LINCDE-CODART` | `X(10)` | code article | `String codart` — `LigneCumulee.codart` ← `D.CDL.CODART` |
| 18 | 18 | 1 | `FILLER` | `X` | espace | `' '` |
| 19 | 25 | 7 | `ENR-LINCDE-QTCCDE` | `9(7)` | **quantité livrée** (`TCDL-QTELIV`) — nom trompeur | `long qteliv` — somme `D.CDL.QTLCDE` |
| 26 | 26 | 1 | `FILLER` | `X` | espace | `' '` |
| 27 | 33 | 7 | `ENR-LINCDE-QTCGRT` | `9(7)` | quantité gratuite (`TCDL-QTCGRT`) | `long qtcgrt` — somme `D.CDL.QTCGRT + QTCECH` |
| 34 | 34 | 1 | `FILLER` | `X` | espace | `' '` |
| 35 | 69 | 35 | `ENR-LINCDE-LIBELL` | `X(35)` | **TOUJOURS 35 espaces** (forcé) | espaces — forcé ; jamais `D.ART.LIBELL` |
| 70 | 70 | 1 | `FILLER` | `X` | espace | `' '` |
| 71 | 74 | 4 | `ENR-LINCDE-CODLABLAB` | `X(4)` | code laboratoire | `String codlablab` — `LigneCumulee.codlablab` ← `D.CDL.CODLAB` |
| 75 | 112 | 38 | `FILLER` | `X(38)` | espaces | espaces |
| 113 | 197 | 85 | *(non adressé)* | — | espaces (via INITIALIZE) | espaces |

**Total : 197** ✓

---

#### FINCDE (197 octets)

| Pos. début | Pos. fin | Long. | Nom COBOL | PIC | Contenu | Java / SQL |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 6 | 6 | `ENR-TYPMES` | `X(6)` | `"FINCDE"` | `String` — littéral `"FINCDE"` |
| 7 | 7 | 1 | `ENR-FILLER` | `X` | espace | `' '` |
| 8 | 15 | 8 | `ENR-FINCDE-SUMQTE` | `9(8)` | somme quantités livrées pour ce dépôt | `long sumqte` — `W-SUMQTE-CO`/`W-SUMQTE-MO` (cumul `D.CDL.QTLCDE`) |
| 16 | 16 | 1 | `FILLER` | `X` | espace | `' '` |
| 17 | 24 | 8 | `ENR-FINCDE-NBLIG` | `9(8)` | nombre de lignes LINCDE pour ce dépôt | `long nblig` — `W-NB-CDL-CO`/`W-NB-CDL-MO` (compteur LINCDE écrits) |
| 25 | 121 | 97 | `FILLER` | `X(97)` | espaces | espaces |
| 122 | 197 | 76 | *(non adressé)* | — | espaces (via INITIALIZE) | espaces |

**Total : 197** ✓

---

#### FINMES (197 octets)

| Pos. début | Pos. fin | Long. | Nom COBOL | PIC | Contenu | Java / SQL |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 6 | 6 | `ENR-TYPMES` | `X(6)` | `"FINMES"` | `String` — littéral `"FINMES"` |
| 7 | 7 | 1 | `ENR-FILLER` | `X` | espace | `' '` |
| 8 | 15 | 8 | `ENR-FINMES-NBCDE` | `9(8)` | nombre total commandes CO ou MO | `long nbcde` — `NB-CDE-CO` ou `NB-CDE-MO` |
| 16 | 112 | 97 | `FILLER` | `X(97)` | espaces | espaces |
| 113 | 197 | 85 | *(non adressé)* | — | espaces (via INITIALIZE) | espaces |

**Total : 197** ✓

---

### Traduction Java : `TransmissionRecord`

En Java 8, il ne faut ni additionner les tailles des variantes ni reproduire une mémoire partagée mutable. Utiliser un contrat commun et un formatteur par variante :

```java
public interface TransmissionRecord {
    String format(); // retourne exactement 197 caractères
}
```

Les implémentations `DebcdeRecord`, `RefcdeRecord`, `LintxtRecord`, `TxtcdeRecord`, `LincdeRecord`, `FincdeRecord` et `FinmesRecord` construisent chacune un enregistrement complet de 197 caractères en appliquant les positions ci-dessus.

Chaque formatteur doit :

1. Construire un tableau de 197 espaces (équivalent de `INITIALIZE ENREG-TRANSMIT`) ;
2. Écrire `ENR-TYPMES` en positions 1–6 ;
3. Laisser la position 7 à espace (`ENR-FILLER`) ;
4. Écrire chaque champ nommé à sa position absolue (tableau ci-dessus) ;
5. Laisser tous les `FILLER` et octets non adressés à espace ;
6. Vérifier en assertion que `result.length() == 197`.

```java
// Squelette d'un formatteur
public final class LincdeRecord implements TransmissionRecord {

    private final String codart;
    private final long   qteliv;   // reçoit TCDL-QTELIV — voir §8
    private final long   qtcgrt;
    private final String codlablab;

    @Override
    public String format() {
        char[] rec = new char[197];
        Arrays.fill(rec, ' ');

        // ENR-TYPMES pos 1-6 (index 0-5)
        FixedField.writeAlpha(rec, 0, "LINCDE", 6);
        // ENR-FILLER pos 7 (index 6) : déjà espace

        // Corps — positions absolues 8-197 (index 7-196)
        FixedField.writeAlpha  (rec,  7, codart,     10); // pos  8-17
        // FILLER pos 18 (index 17) : déjà espace
        FixedField.writeNumeric(rec, 18, qteliv,      7); // pos 19-25
        // FILLER pos 26 (index 25) : déjà espace
        FixedField.writeNumeric(rec, 26, qtcgrt,      7); // pos 27-33
        // FILLER pos 34 (index 33) : déjà espace
        // LIBELL pos 35-69 (index 34-68) : déjà 35 espaces — ne pas écrire
        // FILLER pos 70 (index 69) : déjà espace
        FixedField.writeAlpha  (rec, 70, codlablab,   4); // pos 71-74
        // FILLER pos 75-112 (index 74-111) : déjà 38 espaces
        // non adressé pos 113-197 (index 112-196) : déjà espaces

        return new String(rec);
    }
}
```

---

## 7. Règles de cumul et de routage

### Accumulation des lignes CDL (TABLE-CDL)

Les lignes CDL sont lues via `CURCDL`, triées par `CODDEP, CODLAB`. Le programme cumule les lignes tant que `(NUMLIG, CODART)` reste identique.

**Règle critique — double entrée** : chaque groupe CDL produit **2 entrées** dans `TABLE-CDL` :

- Entrée `CODENR='1'` : récapitulatif, `LOTFAB` toujours à espaces. Jamais écrite dans les fichiers de transmission.
- Entrée `CODENR='3'` : détail lot, `LOTFAB` conservé si `GESLOT ∈ {'4','5'}`, sinon vidé. Seule cette entrée génère un enregistrement `LINCDE`.

Pour chaque groupe `(NUMLIG, CODART)` :

- quantité commandée : somme de `CDL.QTCCDE` → `TCDL-QTECDE` ;
- quantité livrée : somme de `CDL.QTLCDE` → `TCDL-QTELIV` ;
- quantité gratuite : somme de `CDL.QTCGRT + CDL.QTCECH` → `TCDL-QTCGRT` ;
- dépôt de destination : `CDL.CODDEP` (modifiable par LECTURE-DEPLAB si lab=2951).

### Règle de conservation du lot (GESLOT)

| `ART.GESLOT` | `TCDL-LOTFAB` dans l'entrée CODENR='3' |
| --- | --- |
| `'4'` | Valeur de `CDL.LOTFAB` conservée |
| `'5'` | Valeur de `CDL.LOTFAB` conservée |
| Toute autre valeur | Forcé à SPACES |

### Règle de remplacement de dépôt (laboratoire 2951)

Pour les articles dont `CDL.CODLAB = '2951'` uniquement, avant de compter les lignes CO/MO :

1. Construire la clé `WS-ARGUM = CODLAB(4) + CLI.DEPART(2)` ;
2. Chercher dans `D.PAR` où `CODENT='LAB'`, `CHAMPS='DEPLAB'`, `ARGUM=WS-ARGUM` ;
3. Si trouvé : remplacer `CDL.CODDEP` par `FONCT(1:2)`.

### Compteurs CO / MO

Les compteurs `W-NB-CDL-CO` et `W-NB-CDL-MO` sont incrémentés dans deux contextes différents et indépendants :

- Dans `TRT-TROUVE-CDL` (pendant la lecture CURCDL) : comptage par dépôt de chaque ligne CDL brute. Utilisé dans `STOCK-FIC-DESADV` pour déterminer si `NB-CDE-CO`/`NB-CDE-MO` doivent être incrémentés.
- Dans `STOCK-LIG-DESADV` (pendant l'écriture) : comptage par dépôt des seules entrées `CODENR='3'` effectivement écrites. Réinitialisé à 0 à chaque changement de groupe laboratoire. Utilisé dans `FINCDE`.

### Routage vers CO ou MO

Chaque enregistrement (`REFCDE`, `LINTXT`, `TXTCDE`, `LINCDE`) est écrit dans le fichier correspondant à `TCDL-CODDEP(IND-CDL)`.

`FINCDE` est routé selon `PREV-CODDEP` (le dépôt du **dernier** enregistrement traité), pas le dépôt courant.

---

## 8. Dates, valeurs numériques et comportements spéciaux

### Dates VMS

Le programme utilise des dates binaires VMS déclarées en COBOL comme :

```cobol
PIC S9(11)V9(7) COMP
```

Les appels `SYS$BINTIM` et `SYS$ASCTIM` assurent la conversion entre date binaire et texte `"DD-MON-YYYY HH:MI:SS.CC"`.

En Java 8, isoler cette conversion derrière une interface `LegacyDateCodec`. Ne pas disperser des conversions `LocalDateTime` dans les repositories et les formatteurs.

```java
public interface LegacyDateCodec {
    String toVmsAscii(LocalDateTime dt);        // "DD-MON-YYYY HH:MI:SS.CC"
    LocalDateTime fromVmsAscii(String s);
    String toYyyyMmDd(LocalDateTime dt);        // "YYYYMMDD"
    String toYyMmDd(LocalDateTime dt);          // "YYMMDD" — pour DATCDE dans REFCDE
}
```

### Conversion de date vers les champs positionnels

```text
SYS$ASCTIM(binaire) → W-DATE-ASCII "DD-MON-YYYY HH:MI:SS.CC"
  chars 8-11 (année 4 chiffres) → positions 1-4 du champ
  chars 4-6  (mois abrégé)     → convertir en 2 chiffres → positions 5-6
  chars 1-2  (jour)            → positions 7-8
  INSPECT : remplacer espaces par '0'
Résultat : "YYYYMMDD" (8 caractères)
```

Référence de conversion des mois : `JAN=01`, `FEB=02`, `MAR=03`, `APR=04`, `MAY=05`, `JUN=06`, `JUL=07`, `AUG=08`, `SEP=09`, `OCT=10`, `NOV=11`, `DEC=12`.

### Format DATCDE dans REFCDE : YYMMDD (6 caractères)

`FIC-DATCDE` est calculé en `YYYYMMDD` (8 chars). Mais l'affectation vers `ENR-REFCDE-DATCDE` prend `FIC-DATCDE(3:6)` en syntaxe COBOL (position 3, longueur 6) :

```text
"YYYYMMDD"[3:6] → "YYMMDD"   (le siècle est ignoré)
```

En Java : `ficDatecde.substring(2, 8)`.

### Dates NULL et substitution

| Indicateur | Valeur | Résultat |
| --- | --- | --- |
| `IDATEBL` | `-1` (NULL SQL) | `FIC-DATEBL = PARAM-DATE-TXT` (date du jour YYYYMMDD) |
| `IDATEBL` | `≥ 0` | Conversion normale via `SYS$ASCTIM` |
| `IDATEBP` | `-1` (NULL SQL) | `FIC-DATEBP = PARAM-DATE-TXT` (date du jour YYYYMMDD) |
| `IDATEBP` | `≥ 0` | Conversion normale |

### Astuce NOMLIV dans REFCDE

Le champ `ENR-REFCDE-NOMLIV` (35 caractères) ne contient **pas** un nom de livraison. Il contient une sous-chaîne décalée de `CLICSP` :

```text
CDE.CLICSP → ENR-REFCDE-NOMLIV (35 chars) : "000123" + 29 espaces
MOVE ENR-REFCDE-NOMLIV(4:6) TO ENR-REFCDE-NOMLIV
Résultat : "123   " + 29 espaces
```

En Java :

```java
String clicsp = String.format("%06d", clicspValue);   // "000123"
String padded  = String.format("%-35s", clicsp);      // "000123" + 29 espaces
String nomliv  = String.format("%-35s", padded.substring(3, 9)); // "123   " + 29 espaces
```

### Champ LINCDE-QTCCDE reçoit la quantité livrée

Malgré son nom, `ENR-LINCDE-QTCCDE` (`PIC 9(07)`) est alimenté par `TCDL-QTELIV` (somme des `QTLCDE`), pas par `QTCCDE`. C'est une décision métier délibérée.

### Décimaux

Par exemple :

```cobol
PIC 9(6)V99
```

correspond à un décimal non signé de 8 chiffres dont 2 décimales. En Java, utiliser `BigDecimal`, jamais `double` :

```java
BigDecimal poids = new BigDecimal("123456.78");
```

La valeur maximale représentable est `999999.99`.

---

## 9. Comportements à caractériser avant réécriture

Ces points doivent être couverts par des tests exécutés contre le COBOL ou des fichiers de référence avant de choisir un comportement Java.

| N° | Comportement | Paragraphe COBOL |
| --- | --- | --- |
| B01 | Chaque groupe CDL crée 2 entrées dans TABLE-CDL (`CODENR='1'` puis `CODENR='3'`). Seule l'entrée `'3'` est écrite. | `TRT-CDL-CUMULEE` + `TRAITEMENT-SANS-LOT` |
| B02 | Quand le curseur CDL est épuisé, les variables hôtes contiennent encore les valeurs du dernier FETCH. Ces valeurs périmées sont explicitement réutilisées. | `TRT-NOT-TROUVE-CDL` |
| B03 | `FINCDE` est écrit pour le dépôt de `PREV-CODDEP` (dernier vu), pas du dépôt courant. | `WRITE-FINCDE` |
| B04 | `NB-CDE-CO`/`NB-CDE-MO` sont incrémentés depuis les compteurs de `TRT-TROUVE-CDL` avant que `STOCK-LIG-DESADV` les réinitialise. | `STOCK-FIC-DESADV` |
| B05 | Les NULL SQL sont parfois traités par indicateur (`IDATEBL`, `IDATEBP`) et parfois sans indicateur selon le curseur (`CURCDE` vs `CURCDE_R` pour `DATREC`). | `LECTURE-DES-CDES` vs `LECTURE-DES-CDES-R` |
| B06 | Les troncatures et compléments lors des `MOVE` entre champs de tailles différentes doivent être reproduits octet par octet. | Tout `MOVE` entre champs alphanumériques |
| B07 | L'encodage des fichiers RMS doit être identifié sur l'environnement VMS avant de fixer l'encodage Java. | Fichiers `FIC-TRANSMITCO`, `FIC-TRANSMITMO` |
| B08 | SQLSTATE `'22002'` est traité comme succès (`FLAG-TROUVE='O'`). | `GESTION-SQLSTATE` |
| B09 | Le champ `NOMLIV` reçoit une sous-chaîne décalée de `CLICSP`, pas un nom de livraison. | `WRITE-REFCDE` |
| B10 | `DATCDE` dans `REFCDE` est en format `YYMMDD`, non `YYYYMMDD`. | `WRITE-REFCDE` |
| B11 | `REFCDE` est écrit une seule fois par changement de groupe laboratoire, pas une fois par commande. | `STOCK-LIG-DESADV` |
| B12 | La transaction est `READ ONLY` pour le traitement principal. `CREAT-DATRAS` bascule brièvement en `READ WRITE`. | `TRAITEMENT-PRINCIPAL`, `CREAT-DATRAS` |

Ces comportements ne doivent pas être « corrigés » pendant la première phase de migration.

---

## 10. Architecture Java 8 cible

Une architecture ports/adaptateurs limite le couplage entre logique métier, JDBC et fichiers.

```text
com.example.expedition
├── application
│   └── ExportExpeditionService
├── domain
│   ├── Commande
│   ├── LigneCommande
│   ├── LigneCumulee          ← remplace TABLE-CDL (CODENR, CODDEP, QTELIV, QTCGRT…)
│   ├── Depot                 ← enum CO / MO
│   └── ExportResult
├── port
│   ├── CommandeRepository    ← CURCDE / CURCDE_R
│   ├── CdlRepository         ← CURCDL
│   ├── ReferenceRepository   ← ART, CLI, MES, PAR, MVT, CDO, CCL, DOCENT, LAB
│   ├── TransmissionWriter    ← écriture vers RMS_TRANSCO / RMS_TRANSMO
│   ├── MajWriter             ← écriture vers RMS_MAJ
│   ├── AnomalieWriter        ← écriture vers RMS_ANO
│   └── ClockPort             ← date courante déterministe pour les tests
├── adapter
│   ├── jdbc
│   ├── file
│   └── vmsdate               ← LegacyDateCodecImpl
└── format
    ├── FixedField             ← alphaLeft, numericRight
    ├── DebcdeFormatter
    ├── RefcdeFormatter
    ├── LintxtFormatter
    ├── TxtcdeFormatter
    ├── LincdeFormatter
    ├── FincdeFormatter
    └── FinmesFormatter
```

### Responsabilités

- `ExportExpeditionService` orchestre le batch et les transactions.
- Les repositories exposent des objets métier et encapsulent le SQL Oracle Rdb.
- Les formatteurs construisent des chaînes de 197 caractères sans accès base.
- `TransmissionWriter` route les enregistrements vers `CO` ou `MO`.
- `ClockPort` rend la date courante déterministe dans les tests.
- `LegacyDateCodec` centralise les formats VMS.
- Les variantes COBOL `REDEFINES` deviennent des implémentations distinctes de `TransmissionRecord`, jamais des champs concaténés.

Java 8 ne fournit ni `record` ni textes multilignes. Utiliser des classes immuables classiques, des constructeurs explicites et `java.time`.

### Formatage à largeur fixe

```java
public final class FixedField {

    /** PIC X(n) : cadré à gauche, complété par des espaces, tronqué si trop long. */
    public static String alphaLeft(String value, int width) {
        if (value == null) value = "";
        return String.format("%-" + width + "s", value).substring(0, width);
    }

    /** PIC 9(n) : cadré à droite, complété par des zéros. */
    public static String numericRight(long value, int width) {
        return String.format("%0" + width + "d", value);
    }
}
```

### Remplacement de TABLE-CDL

```java
public final class LigneCumulee {
    private final String numlig;       // TCDL-NUMLIG PIC 9(4)
    private final String codart;       // TCDL-CODART PIC X(10)
    private final String artlab;       // TCDL-ARTLAB PIC X(15)
    private final String artsai;       // TCDL-ARTSAI PIC X(07)
    private final String libell;       // TCDL-LIBELL PIC X(35)
    private final String artcip;       // TCDL-ARTCIP PIC X(7)
    private final String lotfab;       // TCDL-LOTFAB PIC X(12) — vide si GESLOT ∉ {4,5}
    private final long   qteliv;       // TCDL-QTELIV — somme QTLCDE
    private final long   qtcgrt;       // TCDL-QTCGRT — somme QTCGRT + QTCECH
    private final long   qtecde;       // TCDL-QTECDE — somme QTCCDE
    private final char   codenr;       // '1' = récapitulatif, '3' = lot
    private final String codlab;       // TCDL-CODLAB PIC X(4)
    private final String coddep;       // TCDL-CODDEP "CO" ou "MO"
    private final String codlablab;    // TCDL-CODLABLAB PIC X(4)
}
```

La liste `List<LigneCumulee>` remplace le tableau `TABLE-CDL OCCURS 1000`.

### Gestion des résultats SQL

```java
// Requête pouvant retourner 0 ou 1 résultat
Optional<T> findOne(PreparedStatement stmt) throws FatalSqlException;

// Requête retournant plusieurs lignes
List<T> findAll(PreparedStatement stmt) throws FatalSqlException;
```

Pour reproduire le comportement de `TRT-NOT-TROUVE-CDL` (valeurs périmées du curseur) :

```java
CdlRow lastRow = null;
while (rs.next()) {
    CdlRow current = new CdlRow(rs);  // lire tous les champs
    accumulate(current, lastRow);
    lastRow = current;
}
if (lastRow != null) {
    flushLastGroup(lastRow);  // équivalent TRT-NOT-TROUVE-CDL
}
```

---

## 11. Stratégie Test Driven Development

### Principe

La boucle de travail est :

1. **Red** : écrire un test de comportement qui échoue.
2. **Green** : implémenter le minimum pour le faire passer.
3. **Refactor** : simplifier sans modifier la sortie observable.

Chaque incrément doit conserver les tests de caractérisation et les tests unitaires précédents.

### Étape 0 — Capturer le comportement COBOL

Avant le code Java :

1. Constituer des jeux de données Rdb anonymisés ;
2. Exécuter le COBOL avec une date système contrôlée ;
3. Conserver les fichiers `TRANSCO`, `TRANSMO`, `MAJ` et `ANO` ;
4. Calculer leur taille et leur empreinte SHA-256 ;
5. Découper chaque fichier en enregistrements de 197 caractères ;
6. Documenter les données SQL ayant produit chaque fichier.

Ces fichiers sont les **golden masters**. Une sortie Java n'est conforme que si elle est identique octet par octet, sauf divergence explicitement approuvée.

### Étape 1 — Formatteurs positionnels

Commencer par les composants sans base de données :

```java
@Test
public void shouldFormatUnsignedDecimalWithSixIntegerAndTwoDecimalDigits() {
    assertEquals("12345678", CobolNumeric.format(new BigDecimal("123456.78"), 6, 2));
}

@Test
public void shouldCreateA197CharacterDebcdeRecord() {
    String record = new DebcdeFormatter().format();

    assertEquals(197, record.length());
    assertEquals("DEBCDE", record.substring(0, 6));
    assertEquals(" ", record.substring(6, 7));   // ENR-FILLER
    assertEquals("P", record.substring(77, 78)); // ENR-DEBCDE-TEST
}
```

Ordre conseillé :

1. Remplissage espaces et zéros (`FixedField`) ;
2. Numériques COBOL et `BigDecimal` ;
3. `LegacyDateCodec` — toutes les conversions VMS ;
4. `DEBCDE` ;
5. `REFCDE` — dont NOMLIV, DATCDE en YYMMDD, TRAFIC='N' si vide ;
6. `LINTXT` et `TXTCDE` ;
7. `LINCDE` — dont QTCCDE reçoit QTELIV, LIBELL toujours SPACES ;
8. `FINCDE` et `FINMES`.

### Étape 2 — Règles de cumul

Tester la logique d'accumulation avec des objets en mémoire :

```java
@Test
public void shouldAccumulateLinesWithSameLineNumberAndArticle() {
    LigneCumulee result = accumulator.accumulate(Arrays.asList(
        line("0001", "ART0000001", 2, 1, 0),
        line("0001", "ART0000001", 3, 2, 1)
    ));

    assertEquals(5L, result.getQuantiteLivree());    // somme QTLCDE
    assertEquals(4L, result.getQuantiteGratuite());  // somme QTCGRT+QTCECH
}

@Test
public void shouldProduceTwoTableCdlEntriesPerGroup() {
    List<LigneCumulee> entries = accumulator.buildTableCdl(...);

    // Chaque groupe CDL → 2 entrées
    assertEquals(2, entries.size());
    assertEquals('1', entries.get(0).getCodenr());
    assertEquals('3', entries.get(1).getCodenr());
}

@Test
public void shouldAlwaysHaveEmptyLotfabForCodenr1() {
    List<LigneCumulee> entries = accumulator.buildTableCdl(...);
    // Entrée CODENR='1' : lot toujours vide
    assertEquals("", entries.get(0).getLotfab().trim());
}
```

Cas obligatoires :

- Même `(NUMLIG, CODART)` sur plusieurs lignes ;
- Changement de `NUMLIG` ;
- Même ligne avec changement de `CODART` ;
- Quantité gratuite incluant `QTCECH` ;
- Ligne CO, ligne MO, puis changement de laboratoire ;
- `GESLOT` hors `{'4','5'}` → LOTFAB vidé dans CODENR='3' ;
- Laboratoire `2951` avec remplacement de dépôt depuis `D.PAR` ;
- Flush du dernier groupe via valeurs périmées (B02).

### Étape 3 — Orchestration

Utiliser des doubles de test pour les ports :

```java
@Test
public void shouldWriteHeaderRecordsBeforeAnyCommand() {
    FakeTransmissionWriter writer = new FakeTransmissionWriter();
    ExportExpeditionService service = serviceWithNoCommands(writer);

    service.execute(parameters());

    assertEquals("DEBCDE", writer.recordsFor(Depot.CO).get(0).substring(0, 6));
    assertEquals("DEBCDE", writer.recordsFor(Depot.MO).get(0).substring(0, 6));
}

@Test
public void shouldWriteFinmesWithCorrectOrderCount() {
    FakeTransmissionWriter writer = new FakeTransmissionWriter();
    ExportExpeditionService service = serviceWithTwoCoOrders(writer);

    service.execute(parameters());

    String finmes = writer.recordsFor(Depot.CO).stream()
        .filter(r -> r.startsWith("FINMES")).findFirst().orElseThrow();
    assertEquals("00000002", finmes.substring(7, 15)); // NB-CDE-CO = 2
}
```

Vérifier :

- Ordre exact des types d'enregistrement (DEBCDE → REFCDE → LINTXT → [TXTCDE] → LINCDE → FINCDE → FINMES) ;
- Absence/présence conditionnelle de `TXTCDE` selon QU00013/QU000132 ;
- Routage CO/MO par `TCDL-CODDEP` pour LINCDE/REFCDE/LINTXT/TXTCDE ;
- Routage FINCDE par `PREV-CODDEP` ;
- Compteurs `FINCDE` (SUMQTE, NBLIG) et `FINMES` (NBCDE) ;
- Écriture de `MAJ` ;
- Arrêt sur erreur de fichier ou SQL.

### Étape 4 — JDBC

Tester les adapteurs JDBC avec une base compatible ou un environnement Oracle Rdb de test. Ne pas tester le SQL uniquement avec des mocks.

Pour chaque requête :

- Vérifier les paramètres ;
- Vérifier l'ordre des résultats ;
- Couvrir les valeurs nulles (indicateurs `-1`) ;
- Couvrir `'02000'` (non trouvé), succès et erreur ;
- Comparer les lignes retournées avec le COBOL.

Les noms et types SQL doivent rester proches des tables pour faciliter la comparaison.

### Étape 5 — Tests bout en bout

Pour chaque scénario golden master :

1. Charger les données de référence ;
2. Fixer l'horloge via `ClockPort` ;
3. Lancer le batch Java ;
4. Comparer `TRANSCO`, `TRANSMO`, `MAJ` et `ANO` ;
5. Afficher la première position divergente en cas d'échec.

```java
assertArrayEquals(
    Files.readAllBytes(expectedPath),
    Files.readAllBytes(actualPath)
);
```

---

## 12. Matrice minimale de tests

| ID | Scénario | Résultat attendu | Comportement couvert |
| --- | --- | --- | --- |
| T01 | Aucune commande | `DEBCDE` puis `FINMES=0` dans CO et MO | Flux minimal |
| T02 | Une commande avec une ligne CO | Sortie uniquement dans CO entre en-tête et fin | Routage CO |
| T03 | Une commande avec une ligne MO | Sortie uniquement dans MO entre en-tête et fin | Routage MO |
| T04 | Une commande répartie CO/MO | Totaux indépendants dans les deux fichiers | Routage mixte |
| T05 | Plusieurs CDL même ligne/article | Quantités cumulées ; 2 entrées TABLE-CDL par groupe | B01 |
| T06 | Dernier groupe CDL flushé correctement | Flush via valeurs périmées du curseur | B02 |
| T07 | Message BL présent | `LINTXT` contient le BL | Priorité BL/RT |
| T08 | BL absent, RT présent | `LINTXT` contient le premier RT | Priorité BL/RT |
| T09 | BL et RT absents | `LINTXT` contient 114 espaces | Priorité BL/RT |
| T10 | Articles horaire et contact présents | Deux `TXTCDE`, respectivement `RT` et `BL` | Présence TXTCDE |
| T11 | Article horaire absent | Pas de `TXTCDE/RT` | Absence TXTCDE |
| T12 | `TRAFIC` vide | Valeur émise `'N'` | Condition 21 |
| T13 | `DATEBP` nulle (IDATEBP=-1) | `FIC-DATEBP = date courante YYYYMMDD` | B05, condition 12 |
| T14 | `DATEBL` nulle (IDATEBL=-1) | `FIC-DATEBL = date courante YYYYMMDD` | B05, condition 12 |
| T15 | `DOCENT` absent | `NUMDOC` rempli d'espaces | Condition 22 |
| T16 | Mode reprise sans dates | Échec avant traitement | Validation paramètres |
| T17 | Mode reprise avec bornes | Sélection strictement entre les dates (bornes exclues) | Curseur CURCDE_R |
| T18 | Laboratoire 2951 | Dépôt remplacé selon `D.PAR/DEPLAB` | B — lab 2951 |
| T19 | `GESLOT` hors {'4','5'} | `LOTFAB` vidé dans entrée CODENR='3' | Condition 8 |
| T20 | `GESLOT` = '4' ou '5' | `LOTFAB` conservé dans entrée CODENR='3' | Condition 8 |
| T21 | Erreur SQL | Anomalie et arrêt conformes | FIN-ANORMALE |
| T22 | Erreur d'écriture MAJ | Fermeture et code message conformes | Condition 26 |
| T23 | `PIC 9(6)V99` | Format exact, bornes et arrondi interdit | Décimaux COBOL |
| T24 | CLICSP → NOMLIV | `"000123"` → `"123   "` + 29 espaces sur 35 | B09 |
| T25 | DATCDE dans REFCDE | `"20150616"` → `"150616"` (YYMMDD, 6 chars) | B10 |
| T26 | LINCDE QTCCDE | Contient la quantité livrée (QTELIV), pas commandée | B — T7 |
| T27 | Changement de lab dans TABLE-CDL | FINCDE écrit pour PREV-CODDEP avant nouveau REFCDE | B03 |
| T28 | Commande sans ligne CO ni MO | `NB-CDE-CO` et `NB-CDE-MO` non incrémentés | Condition 11 |
| T29 | DATRAS absent dans D.PAR | INSERT dans D.PAR + COMMIT + retour READ ONLY | B12, CREAT-DATRAS |
| T30 | LIBELL dans LINCDE | Toujours 35 espaces, jamais la valeur ART.LIBELL | Format LINCDE |

---

## 13. Définition de Done de la migration

La réécriture est considérée équivalente lorsque :

- tous les tests unitaires sont verts sous Java 8 ;
- tous les tests JDBC sont verts sur une base représentative ;
- tous les golden masters sont identiques octet par octet ;
- chaque enregistrement transmis mesure 197 caractères avant encodage ;
- les compteurs et l'ordre des enregistrements sont identiques ;
- les erreurs SQL et fichier ont un comportement documenté ;
- aucune règle métier n'est modifiée sans test et décision explicite ;
- le COBOL et le Java peuvent être exécutés en parallèle sur un lot de validation.

---

## 14. Ordre recommandé de réalisation

1. Capturer les golden masters COBOL.
2. Implémenter `FixedField` et `LegacyDateCodec` (primitives de formatage).
3. Implémenter et tester chaque type d'enregistrement (T01, T24, T25, T26, T30).
4. Implémenter l'accumulation CDL et la double entrée TABLE-CDL (T05, T06, T19, T20).
5. Implémenter le routage et les compteurs CO/MO (T02, T03, T04, T27, T28).
6. Implémenter les repositories JDBC (T13, T14, T15, T18).
7. Implémenter l'orchestrateur et la logique MES/TXTCDE (T07, T08, T09, T10, T11).
8. Implémenter la gestion des dates (T12, T25, T29).
9. Comparer les sorties Java/COBOL sur golden masters.
10. Corriger uniquement les divergences prouvées.
11. Refactorer après obtention de la parité complète.
