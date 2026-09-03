# COBOL 2026

```text
╔══════════════════════════════════════════════════════════════╗
║                        genose.org                            ║
║              Sebastien Cotillard · progmatic_things          ║
║          ── compilation of works and R&D ──                  ║
╚══════════════════════════════════════════════════════════════╝
```

[![Platform](https://img.shields.io/badge/platform-macOS%20%2F%20Linux%20%2F%20WSL-black?style=flat-square)](https://github.com)
[![GnuCOBOL](https://img.shields.io/badge/GnuCOBOL-3.2-success?style=flat-square)](https://gnucobol.sourceforge.io/)
[![MVS TK5](https://img.shields.io/badge/MVS-TK5%20%2F%20Hercules-blue?style=flat-square)](https://wotho.pebble.ink/tk5/)
[![CICS](https://img.shields.io/badge/CICS-KICKS%20v1.5%20%2F%20CICS%2FVS%201.7-orange?style=flat-square)](http://www.kicksfortso.com/)
[![Java 8](https://img.shields.io/badge/Java-8%20TDD-red?style=flat-square)](https://www.java.com/)
[![Tests](https://img.shields.io/badge/tests-174%20passing-brightgreen?style=flat-square)](CRM/)

Exercices pédagogiques · Projets mainframe COBOL · Migration Java

---

## Projets

| Projet | Description | Technologie |
| ------ | ----------- | ----------- |
| [GSTK/](GSTK/README.md) | Gestion de stock CICS/COBOL — 8 programmes, 8 mapsets BMS | MVS TK5 · CICS · DB2 |
| [CRM/](CRM/README.md) | Migration COBOL → Java 8 TDD — 4 programmes, 174 tests | Java 8 · Maven · Oracle Rdb |
| [CI_CD_TK5/](CI_CD_TK5/README.md) | Pipeline CI/CD générique MVS TK5 — macOS/Linux/WSL | Bash · Docker · s3270 |
| [basics/](basics/) | Fondamentaux COBOL — DISPLAY, IF, EVALUATE, COMPUTE | GnuCOBOL |
| [strings/](strings/) | Manipulation de chaînes — INSPECT, MOVE CORRESPONDING | GnuCOBOL |
| [arrays/](arrays/) | Tableaux — OCCURS, SEARCH, fusion, push, delete | GnuCOBOL |
| [files/](files/) | Fichiers séquentiels — SELECT/FD, READ, WRITE | GnuCOBOL |
| [games/](games/) | Jeu du Pendu interactif | GnuCOBOL |

---

## Mainframe

### GSTK — Gestion de Stock CICS/COBOL

Système complet de gestion de stock sur IBM Mainframe (MVS 3.8j / z/OS).

- **8 programmes** pseudo-conversationnels (GSTK000–GSTK007)
- **8 mapsets BMS** (3270) — menu, consultation, entrée/sortie, rapports, alertes, historique
- **COMMAREA 263 octets** partagée entre tous les programmes
- **SQL DB2** — curseurs, pagination, SYNCPOINT
- **Backend CICS** : KICKS v1.5.0 (défaut) ou CICS/VS 1.7
- **Éditeur BMS** : [CICS BMS ncurses WYSIWYG editor](https://github.com/genose/genose.org-project20262808-CICS_BMS_ncurses_wysiwyg_editor) — éditeur d'écrans 3270 en mode terminal

```bash
# Déploiement depuis CI_CD_TK5/
make build                    # upload + bms + cobol + newcopy CICS
make ci                       # pipeline complet (syntaxe + SQL + build + tests)
CICS_BACKEND=cicsvs make ci   # avec CICS/VS 1.7 au lieu de KICKS
```

→ [GSTK/README.md](GSTK/README.md) · [CI_CD_TK5/README.md](CI_CD_TK5/README.md)

---

### CRM — Migration COBOL → Java 8 TDD

Migration de 4 programmes batch OpenVMS/COBOL (Oracle Rdb SQL embarqué) vers Java 8 en architecture **ports & adapters**.

| Programme COBOL | Rôle | Tests Java |
| --------------- | ---- | ---------- |
| `D05_VERIF_CRM.SCO` | Synchronisation commandes DEPOT ↔ CRM | 29 |
| `T10_MAJ_DTLIVR_BDCRM.COB` | Mise à jour dates livraison DTLIVR | 11 |
| `D02_EXTCDE_CRMCSP1.COB` | Génération fichiers confirmation CSP (197 chars) | 75 |
| `D05_INTCDEFAC_CRM_V2.SCO` | UPSERT CDEFAC → BD_CRM.S.CDE_FAC | 59 |

174 tests · 0 échec · 18 classes de tests · TDD B-codes tracés

```bash
cd CRM/crm-java
mvn test    # 174 tests, 0 failure
```

→ [CRM/README.md](CRM/README.md)

---

### CI_CD_TK5 — Pipeline CI/CD générique MVS TK5

Pipeline Bash complet pour le développement COBOL/CICS sur MVS 3.8j émulé (Hercules + Docker).
Compatible **macOS · Linux · Windows WSL2**.

```bash
# Installation guidée (assistant interactif)
bash CI_CD_TK5/install_CICD_TK5.sh --full
#  ├─ installe les dépendances système
#  ├─ configure .env
#  ├─ choisit et installe le backend CICS (KICKS ou CICS/VS 1.7)
#  └─ make install-all

# Usage quotidien
make build              # build incrémental (MD5)
make watch              # hot-reload sur sauvegarde
make ci                 # pipeline 5 étapes
make ci PROJECT=crm     # changer de projet
```

#### Backend CICS

| Backend | Mode | Ressources | Durée |
| ------- | ---- | ---------- | ----- |
| **KICKS v1.5.0** | TSO (CLIST) | CEDA/CEMT interactif | ~30 min |
| **CICS/VS 1.7** | VTAM region (STC) | Tables assemblées (PCT/PPT/TCT/FCT) | plusieurs jours |
| **Les deux** | Auto-dispatch | — | — |

```bash
bash CI_CD_TK5/mvs/03_cics.sh detect                        # détection automatique du backend
CICS_BACKEND=cicsvs bash CI_CD_TK5/mvs/03_cics.sh newcopy   # forcer CICS/VS
```

→ [CI_CD_TK5/README.md](CI_CD_TK5/README.md)

---

## Exercices GnuCOBOL

### basics/ — Fondamentaux COBOL

| Fichier | Contenu |
| ------- | ------- |
| `01_hello_variables.cbl` | Hello world + déclaration de variables |
| `02_hello_display.cbl` | DISPLAY simple |
| `03_arithmetic_compute.cbl` | COMPUTE, opérations arithmétiques |
| `04_accept_evaluate.cbl` | ACCEPT + EVALUATE (retraite, conditions) |
| `05_conditions_if.cbl` | IF/ELSE, conditions genre |
| `06_multiply_compute.cbl` | TEST-MULTIPLY, COMPUTE GIVING |
| `07_hello_world.cbl` | Hello world minimal |

### strings/ — Manipulation de chaînes

| Fichier | Contenu |
| ------- | ------- |
| `01_move_corresponding.cbl` | MOVE CORRESPONDING entre groupes |
| `02_inspect_tally.cbl` | INSPECT TALLYING, comptage de caractères |

### arrays/ — Tableaux OCCURS/SEARCH

| Fichier | Contenu |
| ------- | ------- |
| `01_occurs_machines.cbl` | OCCURS 5 + PERFORM VARYING |
| `02_tableau_init_loop.cbl` | Initialisation tableau + boucle |
| `03_occurs_nested_classes.cbl` | OCCURS imbriqués 3 niveaux |
| `04_occurs_inventaire.cbl` | OCCURS + affichage inventaire |
| `05_occurs_count.cbl` | COMPUTE count occurrences |
| `06_occurs_sum.cbl` | Somme d'un tableau INDEXED BY |
| `07_occurs_search.cbl` | SEARCH / SET / INDEXED BY |
| `08_merge_arrays.cbl` | Fusion de deux tableaux |
| `09_array_push.cbl` | Push dynamique dans un tableau |
| `10_array_delete_pop.cbl` | Delete + pop par index |

### files/ — Fichiers séquentiels

| Fichier | Données | Contenu |
| ------- | ------- | ------- |
| `01_sequential_read_employee.cbl` | `EMPLOYEE.DAT` | Lecture fichier séquentiel |
| `02_sequential_read_report_ventes.cbl` | `VENTES-LOGIQUE.dat` | Rapport ventes quotidiennes |

### games/ — Projets interactifs

| Fichier | Contenu |
| ------- | ------- |
| `01_pendu.cbl` | Jeu du Pendu interactif (ACCEPT, boucles, tableaux) |

---

## Compilation rapide (GnuCOBOL)

```bash
# Setup environnement
source ./setup_cobol_env.sh
mkdir -p bin

# Programme simple
"$COBC" -x -o bin/pendu games/01_pendu.cbl && bin/pendu

# Programme avec fichier de données
cd files
"$COBC" -x -o ../bin/ventes 02_sequential_read_report_ventes.cbl
../bin/ventes   # lit VENTES-LOGIQUE.dat dans le répertoire courant
cd ..
```

---

## Structure

```text
COBOL/
├── README.md                   Ce fichier
├── COBOL_VSCODE_SETUP.md       Setup VS Code + GnuCOBOL + CI_CD_TK5
├── setup_cobol_env.sh          Environnement GnuCOBOL (macOS/Linux/WSL)
│
├── basics/                     Fondamentaux COBOL
├── strings/                    Manipulation de chaînes
├── arrays/                     Tableaux OCCURS/SEARCH
├── files/                      Fichiers séquentiels
├── games/                      Jeu du Pendu
│
├── GSTK/                       Système gestion de stock CICS/COBOL (MVS TK5)
│   ├── GSTK000–007.cbl         8 programmes pseudo-conversationnels
│   ├── GSTK000–007M.bms        8 mapsets BMS (écrans 3270)
│   ├── Copybook.cbl            COMMAREA partagée (263 octets)
│   └── scripts/                Tests locaux PostgreSQL + vérification syntaxe
│
├── CRM/                        Migration COBOL → Java 8 TDD (stage)
│   ├── *.SCO / *.COB           Sources COBOL OpenVMS originaux (4 programmes)
│   ├── ANALYSE_*.md            Analyses techniques par programme
│   ├── crm-java/               Projet Maven Java 8 (174 tests, ports & adapters)
│   └── scripts/mvs/            Upload + compilation MVS
│
├── CI_CD_TK5/                  Pipeline CI/CD générique MVS TK5 / KICKS / CICS-VS
│   ├── install_CICD_TK5.sh     Assistant installation cross-platform
│   ├── Makefile                make ci | make build | make watch | …
│   ├── conf/                   Configurations par projet (gstk.conf, crm.conf)
│   ├── lib/                    Loaders partagés (project.sh, cics_detect.sh)
│   ├── mvs/                    Scripts pipeline (upload, compile, CICS, CI, watch)
│   │   ├── 12_kicks_install.sh Installation KICKS v1.5.0 (8 phases)
│   │   └── 13_cicsvs_install.sh Installation CICS/VS 1.7 (8 phases)
│   ├── jcl/                    JCL BMS + COBOL MVS
│   └── cics/                   Définitions CEDA/CEMT
│
└── stage_report/               Rapport de stage (COBOL → Java migration)
```

---

## Setup

Voir [COBOL_VSCODE_SETUP.md](./COBOL_VSCODE_SETUP.md) — macOS / Linux / Windows WSL + VS Code + CI_CD_TK5.

---

## Remerciements

Merci à **Mr Marc Pohoryles** pour l'encadrement pédagogique, l'enthousiasme communicatif
et la qualité des exercices proposés — du COBOL fondamental jusqu'aux pipelines CI/CD MVS TK5.

---

genose.org · Sebastien Cotillard · progmatic_things
