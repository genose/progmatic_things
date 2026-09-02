# COBOL 2026

![Platform macOS](https://img.shields.io/badge/platform-macOS-black)
![GnuCOBOL Ready](https://img.shields.io/badge/GnuCOBOL-ready-success)

**Auteur :** Sebastien Cotillard — exercices pédagogiques et projets mainframe COBOL.

## Structure

```text
COBOL/
├── basics/       Fondamentaux — DISPLAY, ACCEPT, IF, EVALUATE, COMPUTE
├── strings/      Manipulation de chaînes — INSPECT, MOVE CORRESPONDING
├── arrays/       Tableaux — OCCURS, INDEXED BY, SEARCH, fusion/push/delete
├── files/        Fichiers séquentiels — SELECT/FD, READ, WRITE
├── games/        Programmes interactifs — Jeu du Pendu
│
├── GSTK/         Système de gestion de stock CICS/COBOL (MVS TK5)
├── CRM/          Migration COBOL → Java 8 (stage)
├── CI_CD_TK5/    Pipeline CI/CD pour MVS TK5 / KICKS
│   ├── Makefile
│   ├── mvs/      Scripts upload, compile, deploy, watch, CI
│   ├── jcl/      JCL BMS + COBOL MVS
│   └── cics/     Définitions CEDA/CEMT
│
└── setup_cobol_env.sh   Environnement GnuCOBOL (macOS/Linux/Windows WSL)
```

## Programmes par groupe

### basics/

| Fichier | Contenu |
| --- | --- |
| `01_hello_variables.cbl` | Hello world + déclaration de variables |
| `02_hello_display.cbl` | DISPLAY simple |
| `03_arithmetic_compute.cbl` | COMPUTE, opérations arithmétiques |
| `04_accept_evaluate.cbl` | ACCEPT + EVALUATE (retraite, conditions) |
| `05_conditions_if.cbl` | IF/ELSE, conditions genre |
| `06_multiply_compute.cbl` | TEST-MULTIPLY, COMPUTE GIVING |
| `07_hello_world.cbl` | Hello world minimal |

### strings/

| Fichier | Contenu |
| --- | --- |
| `01_move_corresponding.cbl` | MOVE CORRESPONDING entre groupes |
| `02_inspect_tally.cbl` | INSPECT TALLYING, comptage de caractères |

### arrays/

| Fichier | Contenu |
| --- | --- |
| `01_occurs_machines.cbl` | OCCURS 5 + PERFORM VARYING |
| `02_tableau_init_loop.cbl` | Initialisation tableau + boucle |
| `03_occurs_nested_classes.cbl` | OCCURS imbriqués 3 niveaux (classes/élèves/notes) |
| `04_occurs_inventaire.cbl` | OCCURS + affichage inventaire produits |
| `05_occurs_count.cbl` | COMPUTE count occurrences |
| `06_occurs_sum.cbl` | Somme d'un tableau INDEXED BY |
| `07_occurs_search.cbl` | SEARCH / SET / INDEXED BY |
| `08_merge_arrays.cbl` | Fusion de deux tableaux dans un troisième |
| `09_array_push.cbl` | Push dynamique dans un tableau |
| `10_array_delete_pop.cbl` | Delete + pop par index |

### files/

| Fichier | Données | Contenu |
| --- | --- | --- |
| `01_sequential_read_employee.cbl` | `EMPLOYEE.DAT` | Lecture fichier séquentiel, affichage employés |
| `02_sequential_read_report_ventes.cbl` | `VENTES-LOGIQUE.dat` | Rapport ventes quotidiennes depuis fichier |

### games/

| Fichier | Contenu |
| --- | --- |
| `01_pendu.cbl` | Jeu du Pendu interactif (ACCEPT, boucles, tableaux) |

---

## Compilation rapide

```bash
source ./setup_cobol_env.sh
mkdir -p bin

# Programme sans fichier de données
"$COBC" -x -o bin/pendu games/01_pendu.cbl && bin/pendu

# Programme avec fichier de données (lancer depuis files/)
cd files
"$COBC" -x -o ../bin/ventes 02_sequential_read_report_ventes.cbl
../bin/ventes   # lit VENTES-LOGIQUE.dat dans le répertoire courant
cd ..
```

## Setup complet

Voir [COBOL_VSCODE_SETUP.md](./COBOL_VSCODE_SETUP.md) — macOS / Linux / Windows WSL.

## Projets mainframe

- [GSTK/README.md](./GSTK/README.md) — Gestion de stock CICS/COBOL, MVS TK5
- [CRM/README.md](./CRM/README.md) — Migration COBOL → Java 8 TDD (4 programmes : D05_VERIF, T10_MAJ_DTLIVR, D02_EXTCDE, D05_INTCDEFAC)
- [CI_CD_TK5/README.md](./CI_CD_TK5/README.md) — Pipeline CI/CD MVS TK5 / KICKS (scripts, JCL, tests CICS automatisés)
