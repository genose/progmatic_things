# Rapport de Stage — Migration COBOL vers Java 8

**Auteur :** Sebastien Cotillard  
**Date :** Septembre 2026  
**Etablissement :** Ecole / Formation COBOL et Systemes Mainframe  
**Entreprise d'accueil :** *(confidentiel)*  
**Duree :** *(a completer)*  
**Tuteur entreprise :** *(a completer)*  
**Tuteur pedagogique :** Mr Marc Pohoryles

---

## Sommaire

1. [Remerciements](#1-remerciements)
2. [Introduction](#2-introduction)
3. [Contexte technique](#3-contexte-technique)
4. [Perimetre CRM/EXTRANET — Migration COBOL vers Java 8](#4-perimetre-crmextranet--migration-cobol-vers-java-8)
   - [D05\_VERIF\_CRM](#41-d05_verif_crm)
   - [T10\_MAJ\_DTLIVR\_BDCRM](#42-t10_maj_dtlivr_bdcrm)
   - [D02\_EXTCDE\_CRMCSP1](#43-d02_extcde_crmcsp1)
   - [D05\_INTCDEFAC\_CRM\_V2](#44-d05_intcdefac_crm_v2)
5. [Architecture Java 8 — Ports et Adaptateurs](#5-architecture-java-8--ports-et-adaptateurs)
6. [Strategie TDD](#6-strategie-tdd)
7. [Projet GSTK — Systeme CICS/COBOL sur Mainframe MVS](#7-projet-gstk--systeme-cicscobol-sur-mainframe-mvs)
8. [Pipeline CI/CD generique MVS TK5](#8-pipeline-cicd-generique-mvs-tk5)
9. [Competences acquises](#9-competences-acquises)
10. [Bilan et perspectives](#10-bilan-et-perspectives)
11. [Annexes](#11-annexes)

---

## 1. Remerciements

Je tiens a exprimer ma sincere gratitude a l'ensemble de l'equipe pedagogique pour la qualite de l'encadrement dispense tout au long de cette formation.

Apprendre le COBOL en partant de zero n'est pas une demarche banale en 2026. C'est un langage que peu d'etablissements enseignent encore, et la decision de l'inclure dans ce parcours de formation temoigne d'une vision realiste du marche : des millions de lignes de COBOL continuent de faire tourner les systemes critiques de la finance, de la sante et de la logistique mondiale. Avoir eu acces a cet enseignement represente une opportunite rare et precieuse.

Je remercie en particulier :

- L'equipe pedagogique pour avoir concu un cursus progressif et coherent, des fondamentaux du langage jusqu'aux projets mainframe complets (CICS, BMS, JCL, Oracle Rdb) ;
- Les intervenants qui ont partage leur experience du terrain, notamment sur les environnements OpenVMS, MVS et les conventions de developpement industriel en COBOL ;
- L'encadrement du stage, qui a su proposer un perimetre de travail ambitieux — la migration de vrais programmes de production vers Java 8 — permettant de confronter immediatement les apprentissages theoriques a la realite d'un code legacy en exploitation.

Je souhaite egalement remercier **Claudius CLI** ([claude-code-source-build-community-edition-noAVX-foroldtimer](https://github.com/genose/claude-code-source-build-community-edition-noAVX-foroldtimer)), assistant IA qui a apporte une aide precieuse sur deux axes techniques majeurs : la redaction et la mise au point du pipeline CI/CD MVS (Makefile, scripts de compilation et deploiement CICS, hot-reload, refactorisation multi-projets), ainsi que la configuration de l'environnement de developpement VSCode pour le COBOL (extensions, schema copybooks, integration GnuCOBOL). Son assistance a permis de gagner un temps considerable sur des taches d'infrastructure complexes, et de se concentrer sur l'essentiel : la logique metier et la qualite du code.

Cette formation m'a ouvert des portes vers un secteur porteur et sous-represente en termes de nouveaux developpeurs, ou la demande de competences COBOL reste forte et durable.

---

## 2. Introduction

Ce rapport presente les travaux realises au cours du stage portant sur la **migration de programmes COBOL legacy vers Java 8**, dans le contexte d'un systeme de gestion de commandes pharmaceutiques (perimetre CRM/EXTRANET).

### Un apprentissage recent du COBOL

COBOL est un langage que j'ai decouvert recemment, dans le cadre de cette formation. Ce n'est pas un langage qu'on rencontre dans les cursus informatiques classiques, et son paradigme — structure rigide en colonnes, divisions IDENTIFICATION/ENVIRONMENT/DATA/PROCEDURE, donnees autodocumentees par leur PIC — est profondement different des langages modernes. La courbe d'apprentissage est reelle, mais l'investissement est justifie.

En quelques semaines, la progression a ete significative :

- **Fondamentaux** : `DISPLAY`, `ACCEPT`, `EVALUATE`, `COMPUTE`, structures de controle ;
- **Donnees** : `OCCURS`, `REDEFINES`, `MOVE CORRESPONDING`, `INSPECT` ;
- **Fichiers** : `SELECT`/`FD`, enregistrements sequentiels a longueur fixe ;
- **SQL embarque** : curseurs Oracle Rdb, `SQLCODE`, gestion des transactions (`SET TRANSACTION`, `COMMIT`, `ROLLBACK`) ;
- **CICS** : programmation pseudo-conversationnelle, `COMMAREA`, `EXEC CICS SEND MAP`, `EXEC CICS RETURN` ;
- **Environnement mainframe** : JCL, BMS, pipeline CI/CD MVS.

Ce parcours a fourni les bases necessaires pour lire, comprendre et analyser des programmes COBOL de production complexes, puis les migrer fidelement vers Java 8.

### Objectifs du stage

1. **Analyser** le comportement exact de chaque programme COBOL de facon exhaustive, en documentant toutes les regles metier, implicites ou non ;
2. **Reecrire** ces programmes en Java 8, en garantissant la **parite fonctionnelle** avec les sources COBOL, par une approche pilotee par les tests (TDD) ;
3. **Pratiquer** le developpement CICS/COBOL complet sur un environnement mainframe reel (emulateur MVS TK5) via le projet GSTK ;
4. **Outiller** le developpement mainframe avec un pipeline CI/CD generique reutilisable sur tout projet du depot.

---

## 3. Contexte technique

### Environnement source (COBOL / OpenVMS)

| Composant | Detail |
| --------- | ------ |
| Langage | COBOL avec SQL embarque (RDB/COBOL) |
| Systeme d'exploitation | OpenVMS (Digital / HP) |
| Base de donnees | Oracle Rdb |
| Execution | Batchs planifies par le scheduler OpenVMS |
| Format fichiers | RMS sequentiel, largeur fixe positionnelle |

Les bases de donnees principales du perimetre CRM/EXTRANET sont :

| Alias | Base | Usage |
| ----- | ---- | ----- |
| `D` | `BD_DEPOT` | Commandes, articles, clients |
| `S` | `BD_CRM` | Statuts CRM, facturation |
| `E` | `BD_CRM` | Table E.CDE_FAC (UPSERT CDEFAC) |
| `T` | `BD_TRANSPORT` | Donnees de transport |

### Environnement cible (Java 8)

| Composant | Detail |
| --------- | ------ |
| Langage | Java 8 |
| Build | Maven 3 |
| Tests | JUnit 4 |
| Architecture | Ports et adaptateurs (hexagonale) |
| Acces base | JDBC Oracle Rdb |

---

## 4. Perimetre CRM/EXTRANET — Migration COBOL vers Java 8

Quatre programmes COBOL ont ete etudies et migres :

| Programme | Role | Tests |
| --------- | ---- | ----- |
| `D05_VERIF_CRM` | Verification synchronisation DEPOT/CRM | **23 tests** |
| `T10_MAJ_DTLIVR_BDCRM` | Mise a jour date de livraison depuis fichiers transport | **36 tests** |
| `D02_EXTCDE_CRMCSP1` | Generation fichiers de confirmation d'expedition (CSP, 197 chars) | **92 tests** |
| `D05_INTCDEFAC_CRM_V2` | Integration UPSERT CDEFAC vers BD_CRM.S.CDE_FAC | **23 tests** |

**Total : 174 tests — 0 echec.**

---

### 4.1 D05\_VERIF\_CRM

#### Role fonctionnel

Batch de verification de la coherence entre la base de commandes du depot (`BD_DEPOT`) et la base CRM (`BD_CRM`). Pour une date de bon de livraison (`DATEBL`) et un code depot (`CODDEP`) donnes, il identifie les commandes dont le statut differe entre les deux bases, puis les marque pour re-synchronisation.

#### Parametres

```
P-CODDEP  PIC XX      Code depot (ex : "CO", "MO")
P-DATEBL  PIC 9(8)    Date YYYYMMDD (ex : "20241108")
P-MAJ     PIC X       'O' = effectuer les mises a jour
```

#### Flux en deux phases

1. **Phase READ ONLY** : parcours du curseur `CURCDE` sur `BD_DEPOT.D.CDE`, comparaison du champ `STATUT` avec `STATENCOURS` dans `BD_CRM.S.CDE_FAC`. Les ecarts sont accumules dans un tableau interne (capacite : 9 000 entrees).
2. **Phase READ WRITE** (si `P-MAJ='O'` et ecarts > 0) : mise a jour de `D.CDE` avec `STATCRM=''` et `FLAG_CRM='O'`. Un seul `COMMIT` global.

#### Regles metier critiques preservees

| Code | Regle |
| ---- | ----- |
| B-D05-03 | Statuts `FAT` (DEPOT) et `FAP` (CRM) sont **equivalents** — pas une anomalie |
| B-D05-03 | Statuts `GLT` (DEPOT) et `GLP` (CRM) sont **equivalents** |
| B-D05-04 | Cle UPDATE = `(CODLAB, NUMCDE, NUMRAL)` — `CODDEP` **absent** du WHERE |
| B-D05-06 | Un seul `COMMIT` apres tous les UPDATEs (pas de commit par ligne) |
| B-D05-09 | Overflow > 9 000 entrees → warning DISPLAY, traitement continue |

---

### 4.2 T10\_MAJ\_DTLIVR\_BDCRM

#### Role fonctionnel

Batch de mise a jour de la date de livraison (`DTLIVR`) et du flag de livraison (`FLAGLIV='O'`) dans `BD_CRM.E.CDE_FAC`, a partir de fichiers de retour de transport au format `MAJBDSTAT*.DAT`.

#### Format des enregistrements d'entree (39 caracteres fixes)

| Champ | Longueur | Description |
| ----- | -------- | ----------- |
| `MAJBD-CODDEP` | 4 | Code depot |
| `MAJBD-CODLAB` | 4 | Code laboratoire |
| `MAJBD-NUMCDE` | 7 | Numero commande |
| `MAJBD-NUMRAL` | 1 | Numero ralliement |
| `MAJBD-DATLIV` | 23 | Date livraison — format VMS ASCII `DD-MON-YYYY HH:MM:SS.CC` |

#### Regles metier critiques preservees

| Code | Regle |
| ---- | ----- |
| B-T10-03 | `CODDEP="FO"` remplace par `"MO"` **avant** toute requete SQL |
| B-T10-05 | Recherche et MAJ 9994 : `CODDEP='CO'` **hardcode** |
| B-T10-07 | `COMMIT` **par enregistrement** (fidelite exacte au COBOL) |
| B-T10-09 | Mise a jour 9994 utilise la **meme** `DATBIN` que l'update principal |
| B-T10-10 | Deadlock → `STOP RUN` (pas de retry) |

#### Cascade 3628 → 9994

Quand `CODLAB='3628'`, le programme verifie si une commande miroir existe sous `CODLAB='9994'` et `CODDEP='CO'`, et la met a jour avec la meme date de livraison. La cle de la commande 9994 est encodee dans le champ `CDELAB` de l'enregistrement 3628.

---

### 4.3 D02\_EXTCDE\_CRMCSP1

#### Role fonctionnel

Batch de generation de fichiers de confirmation d'expedition a largeur fixe (197 caracteres par enregistrement), a destination de deux depots pharmaceutiques (CO et MO). Il extrait les commandes en statut `CRV` depuis `BD_DEPOT`, les enrichit avec les donnees de lignes, d'articles et de clients, puis produit les fichiers de transmission.

#### Structure des enregistrements (197 caracteres)

| Position | Longueur | Contenu |
| -------- | -------- | ------- |
| 1–7 | 7 | En-tete fixe (code depot, date...) |
| 8–197 | 190 | Corps variable selon TYPMES (REDEFINES COBOL) |

Sept types d'enregistrements (`TYPMES`) correspondent aux sept REDEFINES du COBOL : `DEBCDE`, `REFCDE`, `LINTXT`, `TXTCDE`, `LINCDE`, `FINCDE`, `FINMES`. Chaque variante est implementee en Java par une classe `XxxFormatter` implementant l'interface `TransmissionRecord`.

#### Resultats

92 tests unitaires couvrant les 7 formateurs, le codec de date VMS, et les cas limites (troncature, padding, separateurs).

---

### 4.4 D05\_INTCDEFAC\_CRM\_V2

#### Role fonctionnel

Batch d'integration (`UPSERT`) du fichier sequentiel `RMS_CDEFAC` (140+ champs par enregistrement) vers la table `BD_CRM.S.CDE_FAC`. Pour chaque enregistrement, le programme verifie si la commande existe deja en CRM et effectue soit une creation (INSERT, 80+ colonnes) soit une mise a jour (UPDATE, 70+ colonnes sans `FLAGSTD`/`FLAGDET`).

#### Regles metier critiques preservees

| Code | Regle |
| ---- | ----- |
| B-D05I-01 | `NUMCDE = 0` → skip silencieux |
| B-D05I-02 | Statut terminal (FAT, GLT, FAP, GLP) → skip, sauf `CODLAB='3010'` avec `CPTFAC > 20 000` |
| B-D05I-03 | `CODLAB='9994'` → updates conditionnels sur `BD_DEPOT.D.CDE` (FLAGLIV, FLAGFAC, FLAGBLO, FLAGSUP) |
| B-D05I-04 | `COMMIT` par enregistrement |
| B-D05I-05 | `DATFAC`/`DATECH` vides → date sentinelle `17-NOV-1858` (epoch zero Oracle Rdb) |
| B-D05I-06 | UPSERT : SELECT → si NOT FOUND → INSERT, sinon UPDATE |
| B-D05I-10 | UPDATE ne modifie pas `FLAGSTD` ni `FLAGDET` (presents en INSERT seulement) |

#### Architecture

Reutilise `VmsDateCodec` du module D02 (cross-package dans le meme projet Maven) pour la conversion `DD-MON-YYYY HH:MM:SS.CC` → `java.sql.Timestamp`.

---

## 5. Architecture Java 8 — Ports et Adaptateurs

L'architecture retenue pour la migration est le **modele hexagonal** (ports et adaptateurs), qui permet de tester la logique metier independamment de la base de donnees et des fichiers.

```
┌───────────────────────────────────────────────────┐
│  Main  (orchestration + wiring des composants)    │
└──────────────────┬────────────────────────────────┘
                   │
         ┌─────────▼─────────┐
         │   Service (app)   │  Logique metier — pur Java, testable
         └─────────┬─────────┘
                   │ interface port (contrat)
         ┌─────────▼─────────┐
         │  Adapter JDBC     │  Oracle Rdb via JDBC
         │  Adapter File     │  Lecture/ecriture fichiers
         └─────────┬─────────┘
                   │
         Oracle Rdb / Fichiers positionnels
```

### Gestion transactionnelle (fidelite au COBOL)

| Programme | COBOL | Java |
| --------- | ----- | ---- |
| D05 | `SET TRANSACTION READ ONLY` → scan → `ROLLBACK` → `SET TRANSACTION READ WRITE` → boucle UPDATE → `COMMIT` unique | Connexion read-only pour le scan ; connexion `rwConn` auto-commit=false ; `commit()` unique apres la boucle |
| T10 | `SET TRANSACTION READ WRITE` → UPDATE → [UPDATE 9994] → `COMMIT` par enregistrement | `txConn` auto-commit=false ; `commit()` apres chaque enregistrement |
| D02 | `COMMIT` apres chaque commande traitee | Meme granularite dans le service |
| D05_INTCDEFAC | `SET TRANSACTION READ WRITE` → UPSERT → `COMMIT` par enregistrement | `commit()` apres chaque enregistrement dans `IntCdeFacService.traiterRecord()` |

---

## 6. Strategie TDD

L'approche TDD (Test-Driven Development) a ete adoptee pour garantir la parite fonctionnelle avec les programmes COBOL. Chaque regle metier identifiee dans l'analyse est directement traduite en un test unitaire nomme d'apres la regle.

### Resultats des tests (ensemble des 4 programmes)

| Classe de test | Programme | Nb tests |
| -------------- | --------- | -------- |
| `StatutEquivalenceTest` | D05 | 8 |
| `VerifCrmServiceTest` | D05 | 15 |
| `MajbdstatFileReaderTest` | T10 | 7 |
| `VmsDateParserTest` | T10 | 7 |
| `CascadeLabo3628Test` | T10 | 7 |
| `MajDtlivrServiceTest` | T10 | 9 |
| `DeliveryCodexNormalizerTest` | T10 | 6 |
| `VmsDateCodecTest` | D02 | 15 |
| `FixedFieldTest` | D02 | 12 |
| `DebcdeFormatterTest` | D02 | 7 |
| `RefcdeFormatterTest` | D02 | 16 |
| `LintxtFormatterTest` | D02 | 5 |
| `TxtcdeFormatterTest` | D02 | 7 |
| `LincdeFormatterTest` | D02 | 14 |
| `FincdeFormatterTest` | D02 | 8 |
| `FinmesFormatterTest` | D02 | 8 |
| `StatutFilterTest` | D05_INTCDEFAC | 10 |
| `DateExtractorTest` | D05_INTCDEFAC | 13 |
| **Total** | | **174** |

**174 tests — 0 echec.**

### Exemples de cas couverts

| Comportement COBOL | Test Java |
| ------------------ | --------- |
| FAT (DEPOT) equivalent a FAP (CRM) | `StatutEquivalenceTest.fat_equivaut_a_fap` |
| Commande absente de CRM = discordante | `VerifCrmServiceTest.commandeAbsenteDeCrm_estDiscordante` |
| P-MAJ=false → aucun UPDATE | `VerifCrmServiceTest.majFalse_aucunUpdateEffectue_memeAvecDiscordantes` |
| Overflow 9 000 entrees → warning | `VerifCrmServiceTest.overflow_9000_commandesNok_flagTableOverflow` |
| FO→MO avant toute requete SQL | `MajDtlivrServiceTest.coddep_fo_estRemappeEnMo_avantUpdate` |
| Meme DATBIN pour les deux updates | `MajDtlivrServiceTest.update9994_utilise_memeDatliv_queUpdatePrincipal` |
| Enregistrement fixe 197 chars (DEBCDE) | `DebcdeFormatterTest` (7 cas) |
| DATFAC vide → sentinelle 1858-11-17 | `DateExtractorTest.datfac_vide_retourne_sentinelle` |
| Statut terminal FAT → skip sauf 3010 | `StatutFilterTest.statut_fat_codlab3010_cptfac_eleve_doitTraiter` |

---

## 7. Projet GSTK — Systeme CICS/COBOL sur Mainframe MVS

En complement de la migration, une activite de formation sur les technologies mainframe a ete realisee a travers le projet **GSTK** (Gestion de Stock), deploye sur un emulateur MVS TK5 avec KICKS v1.5.0 (emulateur CICS).

Ce projet a constitue le premier contact pratique avec un environnement mainframe complet : de l'ecriture du COBOL CICS a la compilation par JCL, en passant par la definition des transactions dans CEDA et le test sur terminal 3270 (x3270). Partir de zero sur cet ecosysteme et aboutir a 8 programmes fonctionnels et interactifs est l'un des apprentissages les plus marquants de la formation.

### Architecture

8 programmes pseudo-conversationnels CICS/COBOL, chacun avec son mapset BMS :

| Trans | Programme | Ecran | Acces DB |
| ----- | --------- | ----- | -------- |
| G000 | GSTK000 | Menu + KPIs globaux | SELECT agregats |
| G001 | GSTK001 | Liste articles + filtres | CURSOR + FETCH |
| G002 | GSTK002 | Entree marchandise | INSERT + UPDATE |
| G003 | GSTK003 | Sortie marchandise | INSERT + UPDATE |
| G004 | GSTK004 | Fiche article (creer/modifier) | INSERT ou UPDATE |
| G005 | GSTK005 | Rapport par categorie | CURSOR GROUP BY |
| G006 | GSTK006 | Alertes stock critique | CURSOR WHERE |
| G007 | GSTK007 | Historique mouvements | CURSOR + COUNT |

### Concepts mainframe decouverts

- **Pseudo-conversationnel CICS** : chaque tache se termine par `EXEC CICS RETURN TRANSID(...) COMMAREA(...)`, la tache suivante recharge la COMMAREA. Ce paradigme, tres different de la programmation evenementielle habituelle, requiert de repenser completement la gestion de l'etat applicatif.
- **BMS (Basic Mapping Support)** : definition des ecrans 3270 par mapsets, generation automatique des suffixes `I`/`O`/`A`/`L` pour chaque champ.
- **Pagination sans LIMIT/OFFSET** : implementation manuelle par saut des pages precedentes via `FETCH` en boucle — contrainte propre a COBOL CICS qui oblige a construire sa propre logique de navigation.

### COMMAREA (263 octets)

Structure partagee entre tous les programmes via le copybook `GSTKCOMM` :

| Champ | Longueur | Usage |
| ----- | -------- | ----- |
| `CA-TRAN-RETOUR` | 8 | Transaction d'origine |
| `CA-OPERATEUR` | 10 | Code operateur |
| `CA-SESSION-ID` | 25 | Identifiant session |
| `CA-MSG-RETOUR` | 79 | Message au retour |
| `CA-ART-CODE-SELEC` | 10 | Article selectionne (G001→G003/G004) |
| `CA-FILTRE-*` | 29 | Filtres partages |
| `CA-PAGE-COURANTE` / `CA-NB-PAGES` | 4+4 | Pagination |
| FILLER local | 55 | Usage programme-specifique |

---

## 8. Pipeline CI/CD generique MVS TK5

Un pipeline CI/CD complet a ete developpe dans `CI_CD_TK5/`, initialement pour le projet GSTK, puis refactorise pour fonctionner avec **n'importe quel projet** du depot COBOL.

### Principe

Un systeme de configuration par projet (`conf/${PROJECT_NAME}.conf`) centralise toutes les specificites : patterns de fichiers sources, noms de datasets MVS, jobs JCL, presence ou non de CICS, commandes de validation locale.

```bash
make ci                  # pipeline GSTK (defaut)
make ci PROJECT=crm      # pipeline CRM (batch, pas de CICS)
make projects            # lister les projets disponibles
```

### Structure

```
CI_CD_TK5/
├── conf/
│   ├── gstk.conf            # config GSTK (8 programmes CICS)
│   ├── crm.conf             # config CRM (4 programmes batch)
│   └── gstk_cics_tests.sh  # assertions CICS GSTK
├── lib/
│   └── project.sh           # loader generique
└── mvs/
    ├── 01_upload.sh    → sources COBOL/BMS via lecteur de cartes ou IND$FILE
    ├── 02_submit.sh    → soumission JCL (alloc, bms, cobol)
    ├── 03_cics.sh      → CEDA install/newcopy (skip si HAS_CICS=0)
    ├── 06_build.sh     → build incremental par MD5
    ├── 07_watch.sh     → hot-reload sur sauvegarde fichier
    ├── 08_test_cics.sh → tests CICS automatises via s3270
    ├── 09_ci.sh        → pipeline 5 etapes
    └── herc.sh         → pilotage Hercules (syslog, spool, commandes MVS)
```

### Pipeline CI/CD en 5 etapes (09_ci.sh)

| Etape | Action | Parametrage |
| ----- | ------ | ----------- |
| 1 | Verification syntaxe COBOL locale (GnuCOBOL) | `CHECK_COBOL_CMD` dans conf |
| 2 | Tests unitaires locaux | `CHECK_UNIT_CMD` (SQL pour GSTK, `mvn test` pour CRM) |
| 3 | Build MVS incremental | `06_build.sh` ou delegation `MVS_UPLOAD_CMD`+`MVS_COMPILE_CMD` |
| 4 | Verification spool JES2 (ABEND ?) | `herc.sh spool` |
| 5 | Tests CICS automatises | `08_test_cics.sh` — skippee si `HAS_CICS=0` |

### Integration CRM

Le `crm.conf` delegue l'upload et la compilation aux scripts specifiques du projet CRM (`CRM/scripts/mvs/01_upload.sh`, `02_compile.sh`) deja en place, sans dupliquer la logique. La validation locale utilise `mvn test` (174 tests).

---

## 9. Competences acquises

### COBOL — un langage appris de zero

La particularite de ce stage est d'avoir ete conduit apres un apprentissage recent du COBOL. Contrairement aux intervenants seniors qui baignent dans ce langage depuis des decennies, j'ai du rapidement construire une lecture fluide du code, identifier les idiomes caracteristiques (REDEFINES, niveaux de donnees, SQL embarque, conventions de nommage) et comprendre les conventions de programmation propres a chaque environnement (OpenVMS, MVS/CICS).

Cette position de "nouveau venu" dans le langage a paradoxalement ete un atout pour la migration : elle oblige a ne rien supposer, a tout verifier, a questionner chaque comportement plutot que de le tenir pour acquis — une discipline directement utile pour produire des analyses exhaustives et des tests de caracterisation complets.

### Techniques

| Domaine | Niveau acquis |
| ------- | ------------- |
| Lecture de COBOL legacy OpenVMS | Analyse autonome de programmes de production (1 000+ lignes) |
| SQL Oracle Rdb embarque | Curseurs, indicateurs NULL, transactions explicites, UPSERT |
| Java 8 — architecture hexagonale | Ports, adaptateurs, services, injection de dependances |
| TDD / JUnit 4 | Ecriture tests avant code, 174 tests, couverture exhaustive des regles metier |
| CICS pseudo-conversationnel | COMMAREA, BMS, `RETURN`/`XCTL`, gestion etat session |
| MVS / JCL | Soumission de jobs, compilation COBOL + BMS, deploiement CICS |
| CI/CD mainframe generique | Makefile multi-projet, scripts shell, hot-reload, conf par projet |
| Git | Commits atomiques, renommage avec historique |

### Methodologiques

- Redaction d'analyses techniques exhaustives servant de reference unique pour la migration (> 12 sections par programme, chaque comportement code et tracable)
- Principe "parite fonctionnelle avant modernisation" : ne pas refactorer avant d'avoir une suite de tests de caracterisation stable
- Documentation systematique des regles metier avec codes traceables (`B-D05-03`, `B-T10-07`, `B-D05I-02`, etc.) permettant de relier chaque test a la regle COBOL d'origine
- Conception d'outils reutilisables (pipeline CI/CD generique) plutot que de solutions a usage unique

---

## 10. Bilan et perspectives

### Realisations

| Livrable | Detail |
| -------- | ------ |
| D05_VERIF_CRM | Migration Java 8 complete — 23 tests, 0 echec |
| T10_MAJ_DTLIVR_BDCRM | Migration Java 8 complete — 36 tests, 0 echec |
| D02_EXTCDE_CRMCSP1 | Migration Java 8 complete — 92 tests, 0 echec |
| D05_INTCDEFAC_CRM_V2 | Migration Java 8 complete — 23 tests, 0 echec |
| GSTK | 8 programmes CICS/COBOL fonctionnels sur MVS TK5 |
| CI_CD_TK5 | Pipeline CI/CD generique multi-projets (GSTK + CRM) |

**Total : 174 tests — 0 echec.**

### Points de difficulte rencontres

1. **Equivalences de statuts non documentees** (FAT/FAP, GLT/GLP) : seule la lecture minutieuse du source COBOL a permis de les identifier. Elles sont critiques pour eviter de generer de fausses anomalies.
2. **Gestion transactionnelle COBOL → JDBC** : reproduire exactement le comportement COBOL (commit global vs commit par enregistrement) necessite une gestion explicite de `Connection.setAutoCommit(false)`.
3. **Format de date VMS** (`DD-MON-YYYY HH:MM:SS.CC`) : format non standard, absent des bibliotheques Java. Implemente par parser dedie, reutilise entre D02 et D05_INTCDEFAC.
4. **Cascade 3628 → 9994** (T10) et **traitement 9994** (D05_INTCDEFAC) : comportements implicites, non documentes dans les specifications fonctionnelles. Identifies uniquement dans les sources COBOL.
5. **Date sentinelle 17-NOV-1858** : epoch zero Oracle Rdb, utilisee comme substitut de NULL pour les dates facultatives. Comportement a reproduire exactement pour eviter les erreurs de conversion.
6. **Pipeline CI/CD generique** : la refactorisation du pipeline pour le rendre multi-projets a necessite de concevoir un systeme de configuration extensible sans casser la compatibilite GSTK existante.

### Opportunites ouvertes par la formation

La maitrise du COBOL — meme partielle — ouvre un acces a un segment du marche du travail qui reste structurellement en tension. Les developpeurs capables de lire, maintenir et migrer du code COBOL legacy sont rares, et leur rarete s'accentue au fil des departs en retraite des generations qui ont ecrit ces systemes.

Les debouches concrets a l'issue de cette formation sont multiples :

- **Maintenance et evolution de systemes legacy** : banques, assurances, caisses de retraite, laboratoires pharmaceutiques — tous ces secteurs exploitent encore des millions de lignes de COBOL en production ;
- **Projets de migration** (comme celui realise durant le stage) : Java, Python, ou microservices cloud, avec besoin de garantir la parite fonctionnelle ;
- **Consulting specialise** : l'expertise mainframe (MVS, CICS, JCL, Oracle Rdb) est un profil recherche pour les grands programmes de modernisation ;
- **Developpement full-stack mainframe** : la combinaison COBOL + Java + TDD + CI/CD est un profil hybride de plus en plus valorise dans les equipes qui maintiennent des systemes critiques tout en les faisant evoluer.

Cette formation a donc ete bien plus qu'un apprentissage technique : c'est une orientation professionnelle vers un domaine exigeant, stable, et ou la valeur d'un developpeur forme et rigoureux est immediatement reconnue.

### Perspectives

- Etendre le pipeline CI/CD a d'autres projets du depot par simple ajout d'un fichier `.conf`
- Mettre en place des tests d'integration avec une base Oracle Rdb de test
- Approfondir les competences JCL pour un deploiement sur z/OS reel

---

## 11. Annexes

### A. Structure du depot de code

```
COBOL/
├── basics/         Fondamentaux COBOL (GnuCOBOL)
├── strings/        Manipulation de chaines
├── arrays/         Tableaux OCCURS/SEARCH
├── files/          Fichiers sequentiels
├── games/          Programmes interactifs (Pendu)
├── studies/        Exercices et quizz (CH-VII a CH-X)
├── studies_2/      Conception GSTK — ecrans PDF, SQL, copybook, sources COBOL
│
├── GSTK/           Systeme CICS/COBOL MVS TK5
│   ├── GSTK000.cbl ... GSTK007.cbl
│   ├── GSTK000M.bms ... GSTK007M.bms
│   ├── Copybook.cbl
│   └── scripts/
│
├── CRM/            Migration COBOL → Java 8 (4 programmes)
│   ├── D05_VERIF_CRM.SCO
│   ├── T10_MAJ_DTLIVR_BDCRM.COB
│   ├── D02_EXTCDE_CRMCSP1.COB
│   ├── D05_INTCDEFAC_CRM_V2.SCO
│   ├── ANALYSE_*.md
│   ├── crm-java/   (projet Maven — 174 tests)
│   │   └── src/.../crm/{d05,t10,d02,d05intcde}/
│   ├── Makefile
│   └── scripts/mvs/
│
├── CI_CD_TK5/      Pipeline CI/CD MVS generique
│   ├── Makefile
│   ├── conf/       gstk.conf, crm.conf
│   ├── lib/        project.sh (loader)
│   ├── mvs/        01_upload.sh ... 12_kicks_install.sh
│   └── jcl/        GSTKBMS.jcl, GSTKCOMP.jcl
│
└── stage_report/   Ce rapport
```

### B. Commandes de test et de CI/CD

```bash
# Lancer les 174 tests du projet CRM Java
cd CRM/crm-java && mvn test

# Pipeline CI/CD GSTK (syntaxe + SQL + build MVS + tests CICS)
cd CI_CD_TK5 && make ci

# Pipeline CI/CD CRM (syntaxe COBOL + mvn test + build MVS)
cd CI_CD_TK5 && make ci PROJECT=crm

# Build incremental (detecte les fichiers modifies)
cd CI_CD_TK5 && make inc

# Lister les projets disponibles
cd CI_CD_TK5 && make projects

# Deployer GSTK sur MVS TK5
cd CI_CD_TK5 && make build
```

### C. Glossaire

| Terme | Definition |
| ----- | ---------- |
| COBOL | Common Business-Oriented Language — langage de programmation mainframe |
| CICS | Customer Information Control System — moniteur transactionnel IBM |
| BMS | Basic Mapping Support — systeme de definition d'ecrans 3270 pour CICS |
| Oracle Rdb | Base de donnees relationnelle Oracle pour OpenVMS |
| OpenVMS | Systeme d'exploitation Digital/HP pour miniordinateurs VAX/Alpha |
| MVS | Multiple Virtual Storage — systeme d'exploitation IBM pour mainframe |
| JCL | Job Control Language — langage de soumission de jobs MVS |
| TDD | Test-Driven Development — developpement pilote par les tests |
| COMMAREA | Zone de communication entre programmes CICS (max 32 Ko) |
| UPSERT | Operation INSERT ou UPDATE selon existence de la ligne (SELECT → INSERT ou UPDATE) |
| SQLCODE | Code retour SQL (0 = OK, 100 = NOT FOUND, negatif = erreur) |
| CRV | Statut commande "Confirmee-Recue-Validee" |
| DATBIN | Representation binaire VMS d'une date/heure (quadword 64 bits) |
| Sentinelle 1858 | Date `17-NOV-1858` = epoch zero Oracle Rdb, substitut de NULL |
| Ports & adapters | Architecture hexagonale separant logique metier et infrastructures |
| HLQ | High Level Qualifier — prefixe des datasets MVS (ex : HERC02) |
| KICKS | Kent Integrated CICS Knockout System — emulateur CICS pour MVS 3.8j |
