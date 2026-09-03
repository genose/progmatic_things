# CI_CD_TK5 — Pipeline CI/CD pour MVS TK5 / KICKS

**Auteur :** Sebastien Cotillard

Pipeline complet de développement COBOL/CICS sur MVS 3.8j émulé par Hercules (container Docker `mvs-tk5`) avec KICKS v1.5.0 comme substitut CICS.

Compatible **macOS / Linux / Windows WSL2**.

---

## Installation rapide

```bash
# 1. Vérifier les prérequis
bash CI_CD_TK5/install_CICD_TK5.sh

# 2. Installer les dépendances manquantes automatiquement
bash CI_CD_TK5/install_CICD_TK5.sh --install

# 3. Créer le fichier de configuration .env (interactif)
bash CI_CD_TK5/install_CICD_TK5.sh --env

# Tout en une commande (install + .env + make install-all)
bash CI_CD_TK5/install_CICD_TK5.sh --full
```

Options du script :

| Option | Action |
| ------ | ------ |
| _(aucune)_ | Vérifier les prérequis et afficher l'état |
| `--install` | Installer les dépendances manquantes via le gestionnaire de paquets |
| `--env` | Créer / régénérer le fichier `.env` (interactif) |
| `--full` | `--install` + `--env` + `make install-all` |
| `--help` | Afficher l'aide |

---

## Prérequis

| Outil | Usage | macOS | Linux / WSL | Windows |
| ----- | ----- | ----- | ----------- | ------- |
| Docker | Container `mvs-tk5` (Hercules + MVS 3.8j) | `brew install --cask docker` | `sudo apt install docker.io` | [Docker Desktop](https://www.docker.com/products/docker-desktop/) |
| s3270 | Pilotage terminal 3270 (upload, CICS, tests) | `brew install x3270` ou `sudo port install x3270` | `sudo apt install x3270` | WSL → même que Linux |
| fswatch / inotifywait | Surveillance fichiers pour `make watch` | `brew install fswatch` | `sudo apt install inotify-tools` | WSL → même que Linux |
| GnuCOBOL | Vérification syntaxe locale | `brew install gnucobol` | `sudo apt install gnucobol` | WSL → même que Linux |
| PostgreSQL | Tests SQL locaux (schéma GSTK) | `brew install postgresql@16` | `sudo apt install postgresql` | WSL → même que Linux |
| curl | Communication API Hercules (port 8038) | préinstallé | préinstallé | préinstallé (Win10+) |

> **Windows** : utiliser WSL2 (Ubuntu) et ouvrir le projet dans VS Code via Remote-WSL. Les commandes Linux s'appliquent ensuite sans modification.
>
> `s3270` doit être accessible via `$S3270` ou dans `$PATH`.
> Le container Docker s'appelle `mvs-tk5` par défaut (modifiable via `DOCKER_CONTAINER`).

---

## Structure

```
CI_CD_TK5/
├── install_CICD_TK5.sh  — Script d'installation cross-platform (macOS/Linux/WSL)
├── .env                  — Configuration locale (généré par install_CICD_TK5.sh --env)
├── Makefile              — Cibles make (make ci, make build, make watch…)
├── mvs/
│   ├── 00_alloc.jcl      — JCL allocation datasets MVS (1ère fois)
│   ├── 01_upload.sh      — Upload sources COBOL/BMS/JCL vers MVS
│   ├── 02_submit.sh      — Soumission JCL (alloc, bms, cobol, all)
│   ├── 03_cics.sh        — Définitions CICS (install, newcopy, status)
│   ├── 06_build.sh       — Build incrémental par MD5 (upload+compile+newcopy)
│   ├── 07_watch.sh       — Surveillance fichiers → build automatique (fswatch)
│   ├── 08_test_cics.sh   — Tests CICS automatisés via s3270
│   ├── 09_ci.sh          — Pipeline CI/CD complet (5 étapes)
│   ├── 10_spool_reader.sh — Lecture spool JES2 (listings compilation)
│   ├── 11_git_setup.sh   — Git hooks pre-commit + post-commit MVS
│   ├── 12_kicks_install.sh — Installation KICKS v1.5.0 (8 phases)
│   ├── herc.sh           — Pilotage Hercules (log, spool, mvs, watch)
│   └── s3270_lib.sh      — Bibliothèque partagée s3270 (login, cmd, screen)
├── jcl/
│   ├── GSTKBMS.jcl       — Assemblage BMS (ASMA90, 8 mapsets)
│   └── GSTKCOMP.jcl      — Compilation COBOL/CICS (IGYCRCTL ou KIKCOBCL/KICKS)
└── cics/
    └── CEDA_GSTK.txt     — Commandes CEDA (MAPSET, PROGRAM, TRANSACTION)
```

---

## Configuration

Les scripts lisent les variables d'environnement suivantes. Valeurs par défaut pour MVS TK5 standard :

```bash
export TK5_HOST=localhost       # adresse Hercules TN3270
export TK5_PORT=3270            # port TN3270
export TSO_USER=HERC02          # utilisateur TSO
export TSO_PASS=CUL8TR          # mot de passe TSO
export HLQ=HERC02               # High Level Qualifier MVS
export HERC_URL=http://localhost:8038   # API HTTP Hercules (syslog, commandes)
export DOCKER_CONTAINER=mvs-tk5 # nom du container Docker
export COBHLQ=IGY               # HLQ compilateur IBM COBOL (IGY.SIGYCOMP)

# s3270 — auto-détecté si dans $PATH ; surcharger si nécessaire
# macOS MacPorts : export S3270=/opt/local/bin/s3270
# macOS Homebrew : export S3270=/opt/homebrew/bin/s3270
# Linux / WSL    : export S3270=/usr/bin/s3270   (ou laisser vide si dans $PATH)
```

Placer dans un `.env` local ou dans `~/.zshrc` / `~/.bash_profile` (macOS/Linux) ou `~/.bashrc` (WSL).

---

## Démarrage rapide

### Première installation

```bash
# 0. Vérifier / installer les prérequis (macOS/Linux/WSL)
bash install_CICD_TK5.sh --install
bash install_CICD_TK5.sh --env     # configurer .env
source .env

# 1. Démarrer le container MVS TK5
docker start mvs-tk5

# 2. (si KICKS pas encore installé) — voir section KICKS
bash mvs/12_kicks_install.sh all

# 3. Allouer les datasets MVS
bash mvs/02_submit.sh alloc

# 4. Uploader les sources
bash mvs/01_upload.sh all

# 5. Compiler (BMS + COBOL)
bash mvs/02_submit.sh bms
bash mvs/02_submit.sh cobol

# 6. Définir les ressources CICS
bash mvs/03_cics.sh install

# 7. Vérifier
bash mvs/03_cics.sh status
# → doit montrer 8 programmes ENABLED

# 8. Tester
bash mvs/08_test_cics.sh smoke
```

Ou en une seule commande depuis `CI_CD_TK5/` :
```bash
bash install_CICD_TK5.sh --full
# équivalent à : --install + --env + make install-all
```

### Cycle de développement quotidien

```bash
# Build incrémental (ne recompile que les fichiers modifiés)
make build          # upload + bms + cobol + newcopy CICS

# Ou automatique dès qu'un fichier change
make watch          # fswatch → 06_build.sh sur chaque sauvegarde

# Tests
make test           # tous les tests CICS (G000–G007)

# Pipeline complet (check syntaxe + SQL + build + tests)
make ci
```

---

## Référence des scripts

### `01_upload.sh` — Upload vers MVS

Envoie les sources locaux vers les datasets MVS par **lecteur de cartes** (mode par défaut, docker requis) ou **IND$FILE** (mode s3270).

```bash
bash mvs/01_upload.sh            # tout uploader (COBOL + BMS + JCL)
bash mvs/01_upload.sh --bms      # mapsets BMS seulement
bash mvs/01_upload.sh --jcl      # JCL seulement
bash mvs/01_upload.sh --cbl      # sources COBOL seulement
bash mvs/01_upload.sh --indffile # mode IND$FILE (s3270)
```

Datasets cibles :

| Source locale | Dataset MVS |
| ------------- | ----------- |
| `GSTK/*.cbl` | `HLQ.GSTK.SOURCE` |
| `GSTK/Copybook.cbl` | `HLQ.GSTK.COPYLIB(GSTKCOMM)` |
| `GSTK/*.bms` | `HLQ.GSTK.BMS` |
| `CI_CD_TK5/jcl/*.jcl` | `HLQ.GSTK.JCL` |

### `02_submit.sh` — Soumission JCL

Soumet les JCL de compilation/allocation via lecteur de cartes et attend la fin (HASP395 dans syslog).

```bash
bash mvs/02_submit.sh alloc    # allouer les datasets (1ère fois, irréversible)
bash mvs/02_submit.sh bms      # assembler les 8 mapsets BMS (ASMA90)
bash mvs/02_submit.sh cobol    # compiler les 8 programmes COBOL
bash mvs/02_submit.sh all      # alloc → upload → bms → cobol
bash mvs/02_submit.sh watch    # surveillance syslog en temps réel
bash mvs/02_submit.sh status   # jobs actifs + initiateurs JES2
```

### `03_cics.sh` — Gestion CICS/KICKS

Se connecte à KICKS via TSO (`EXEC 'KICKS.KICKSSYS.V1R5M0.CLIST(KICKS)'`) et envoie les commandes CEDA/CEMT.

```bash
bash mvs/03_cics.sh install    # CEDA DEF MAPSET/PROGRAM/TRANS + CEDA INSTALL GROUP(GSTK)
bash mvs/03_cics.sh newcopy    # CEMT SET PROG(*) NEWCOPY (après recompilation)
bash mvs/03_cics.sh status     # CEMT INQ PROG(GSTK*) + CEMT INQ TRAN(G*)
bash mvs/03_cics.sh trans G007 # lancer une transaction et capturer l'écran
```

### `06_build.sh` — Build incrémental

Calcule les MD5 des fichiers GSTK locaux. N'uploade et ne recompile que ce qui a changé depuis le dernier build réussi (état stocké dans `mvs/.checksums`).

```bash
bash mvs/06_build.sh           # build incrémental (auto-détection)
bash mvs/06_build.sh --full    # forcer rebuild complet
bash mvs/06_build.sh --dry     # afficher ce qui changerait sans agir
```

### `07_watch.sh` — Surveillance fichiers

Utilise `fswatch` pour détecter les sauvegardes dans `GSTK/` et déclencher `06_build.sh` automatiquement.

```bash
bash mvs/07_watch.sh           # build auto sur modification
bash mvs/07_watch.sh --check   # build + tests CICS auto
```

### `08_test_cics.sh` — Tests automatisés CICS

Lance chaque transaction GSTK (G000–G007) via s3270, capture l'écran 3270 et vérifie les champs attendus.

```bash
bash mvs/08_test_cics.sh all          # tous les tests (8 transactions × plusieurs assertions)
bash mvs/08_test_cics.sh smoke        # tests rapides : G000 + G001 seulement
bash mvs/08_test_cics.sh trans G004   # une transaction spécifique (capture + no-error)
bash mvs/08_test_cics.sh report       # afficher le dernier rapport
```

Résultat : `PASS / FAIL / SKIP` par assertion + rapport dans `mvs/.test_report.txt`.

### `09_ci.sh` — Pipeline CI/CD complet

```bash
bash mvs/09_ci.sh              # pipeline complet (5 étapes)
bash mvs/09_ci.sh --no-mvs    # local seulement (syntaxe + SQL)
bash mvs/09_ci.sh --no-test   # build sans tests CICS
bash mvs/09_ci.sh --fast      # smoke test uniquement
```

Étapes :

| # | Étape | Script |
| - | ----- | ------ |
| 1 | Syntaxe COBOL locale (GnuCOBOL) | `GSTK/scripts/04_cobc_check.sh` |
| 2 | Tests SQL PostgreSQL | `GSTK/scripts/05_test_sql.sh` |
| 3 | Build incrémental MVS | `06_build.sh` |
| 4 | Vérification spool JES2 (ABEND ?) | `herc.sh spool` |
| 5 | Tests CICS automatisés | `08_test_cics.sh` |

Rapport complet dans `mvs/.reports/ci_YYYYMMDD_HHMMSS.txt` + lien `mvs/.reports/latest.txt`.

### `10_spool_reader.sh` — Lecture spool JES2

Accède à la spool via TSO/SDSF pour extraire les listings de compilation.

```bash
bash mvs/10_spool_reader.sh                 # jobs récents
bash mvs/10_spool_reader.sh GSTKCOMP        # chercher ce job
bash mvs/10_spool_reader.sh --errors        # seulement les erreurs (RC ≠ 0)
bash mvs/10_spool_reader.sh --cobol-errors  # erreurs compilation COBOL (MNOTE, E)
```

### `11_git_setup.sh` — Hooks git

Configure les hooks git pour le pipeline CI/CD :
- **pre-commit** : syntaxe COBOL + SQL, bloque le commit si échec
- **post-commit** (optionnel) : `06_build.sh` automatique après chaque commit

```bash
bash mvs/11_git_setup.sh                # hooks locaux (check seulement)
bash mvs/11_git_setup.sh --with-mvs-push # hooks + build MVS auto au commit
```

### `herc.sh` — Pilotage Hercules

Interface entre le Mac et le système Hercules. Utilise le port 8038 (API HTTP) pour les commandes Hercules et MVS.

```bash
bash mvs/herc.sh log [N]        # dernières N lignes du syslog (défaut: 25)
bash mvs/herc.sh watch          # syslog en temps réel (Ctrl-C)
bash mvs/herc.sh status         # résumé Hercules + container Docker
bash mvs/herc.sh spool          # activité JES2 récente (HASP, IEF, ABEND)
bash mvs/herc.sh devlist        # liste des périphériques
bash mvs/herc.sh devstat 0351   # statut d'un périphérique
bash mvs/herc.sh mvs '$D A'     # commande MVS opérateur (jobs actifs JES2)
```

**Note importante :** les commandes JES2 (`$D A`, `$P jobname`, etc.) sont préfixées par `/` et transmises à MVS via l'API HTTP Hercules — pas via s3270. Elles apparaissent dans le syslog mais ne retournent pas de réponse dans la même requête HTTP.

### `s3270_lib.sh` — Bibliothèque s3270

Fichier source (ne pas exécuter directement). Fournit les fonctions partagées :

| Fonction | Rôle |
| -------- | ---- |
| `s3270_start` | Ouvrir une session s3270 vers `TK5_HOST:TK5_PORT` |
| `s3270_stop` | Fermer la session |
| `s3270_login` | Séquence login TSO (HERC02 / CUL8TR) |
| `s3270_cmd <cmd> [timeout]` | Envoyer une action s3270 (String, Enter, PF…) |
| `s3270_screen` | Capturer l'écran courant (ASCII) |

---

## Installation KICKS v1.5.0

KICKS (Kent Integrated CICS Knockout System) remplace CICS sur MVS 3.8j. Il tourne sous TSO (pas de STC).

```bash
# Installation complète en une commande
bash mvs/12_kicks_install.sh all

# Ou phase par phase
bash mvs/12_kicks_install.sh dasd      # créer volume KICKS0 (3350 @ 0351)
bash mvs/12_kicks_install.sh ickdsf    # formater le volume
bash mvs/12_kicks_install.sh catalog   # créer catalogue UCKICKS0 + alias KICKS
bash mvs/12_kicks_install.sh xmi       # télécharger + uploader le XMI KICKS (~8 MB)
bash mvs/12_kicks_install.sh recv      # TSO RECEIVE → KICKS.V1R5M0.INSTALL
bash mvs/12_kicks_install.sh rcvkick   # RCVKICK2 → librairies KICKS sur KICKS0
bash mvs/12_kicks_install.sh dynamnbr  # augmenter DYNAMNBR=64 dans SYS1.PROCLIB
bash mvs/12_kicks_install.sh jcl       # adapter GSTKBMS.jcl + GSTKCOMP.jcl pour KICKS
bash mvs/12_kicks_install.sh status    # état KICKS sur MVS
```

Après installation, démarrer KICKS dans TSO :
```
TSO: EXEC 'KICKS.KICKSSYS.V1R5M0.CLIST(KICKS)'
```

### Adaptations JCL pour KICKS

La phase 8 (`jcl`) substitue automatiquement dans les JCL :

| Avant (CICS standard) | Après (KICKS) |
| --------------------- | ------------- |
| `&CICSHLQ..SDFHMAC` | `KICKS.KICKSSYS.V1R5M0.KICTMAC` |
| `EXEC DFHITCL` | `EXEC KIKCOBCL` |
| `EXEC DFHEITALC` | `EXEC KIKCOBCL` |
| `&CICSHLQ..SDFHCOB` | `KICKS.KICKSSYS.V1R5M0.COBCOPY` |
| `&CICSHLQ..SDFHLOAD` | `KICKS.KICKSSYS.V1R5M0.SKIKLOAD` |

> Si le compilateur TK5 est COBOL II (pas IBM COBOL), remplacer `KIKCOBCL` par `KIKCB2CL`.

---

## JCL de référence

### `jcl/GSTKBMS.jcl` — Assemblage BMS

8 steps (`ASMG000`–`ASMG007`) : assembleur `ASMA90` + link-éditeur `IEWL`.

- **Entrée :** `HLQ.GSTK.BMS(GSTKnnnM)`
- **Sortie :** `HLQ.GSTK.LOADLIB(GSTKnnnM)`
- **Macro lib :** `SYS1.MACLIB` + `KICKS.KICKSSYS.V1R5M0.KICTMAC`

### `jcl/GSTKCOMP.jcl` — Compilation COBOL

8 steps (`G000`–`G007`) : compilateur `IGYCRCTL` (ou `KIKCOBCL` avec KICKS) + link-éditeur.

- **Entrée :** `HLQ.GSTK.SOURCE(GSTKnnn)`
- **Copybook :** `HLQ.GSTK.COPYLIB` (GSTKCOMM)
- **Sortie :** `HLQ.GSTK.LOADLIB(GSTKnnn)`

### `mvs/00_alloc.jcl` — Allocation datasets

Crée les datasets MVS nécessaires (à exécuter une seule fois) :

| Dataset | RECFM | LRECL | Usage |
| ------- | ----- | ----- | ----- |
| `HLQ.GSTK.SOURCE` | FB | 80 | Sources COBOL |
| `HLQ.GSTK.COPYLIB` | FB | 80 | Copybooks |
| `HLQ.GSTK.BMS` | FB | 80 | Mapsets BMS |
| `HLQ.GSTK.JCL` | FB | 80 | JCL |
| `HLQ.GSTK.LOADLIB` | U | — | Load modules CICS |

---

## Cibles `make`

| Cible | Action |
| ----- | ------ |
| `make check` | Syntaxe COBOL + tests SQL (local, sans MVS) |
| `make pg-setup` | Créer/recréer la base PostgreSQL locale |
| `make upload` | Uploader tout vers MVS (`01_upload.sh all`) |
| `make bms` | Assembler les BMS (`02_submit.sh bms`) |
| `make cobol` | Compiler COBOL (`02_submit.sh cobol`) |
| `make deploy` | CICS newcopy (`03_cics.sh newcopy`) |
| `make build` | upload + bms + cobol + deploy |
| `make inc` | Build incrémental (`06_build.sh`) |
| `make watch` | Surveillance auto (`07_watch.sh`) |
| `make test` | Tests CICS complets (`08_test_cics.sh all`) |
| `make ci` | Pipeline complet (`09_ci.sh`) |
| `make log` | Syslog MVS temps réel (`herc.sh watch`) |
| `make status` | État Hercules + CICS |
| `make spool` | Activité JES2 récente |
| `make install-all` | Installation complète (1ère fois) |
| `make clean` | Supprimer les checksums (prochain build = full) |

---

## Dépannage

### Le container Docker n'est pas joignable

```bash
docker start mvs-tk5
# Attendre ~30s que MVS et JES2 démarrent
bash mvs/herc.sh log 10
```

### s3270 ne démarre pas

```bash
# Vérifier que s3270 est dans le PATH
which s3270 || echo "non trouvé"

# Surcharger le chemin si nécessaire (exemples)
export S3270=/opt/local/bin/s3270      # macOS MacPorts
export S3270=/opt/homebrew/bin/s3270   # macOS Homebrew
export S3270=/usr/bin/s3270            # Linux / WSL

# Vérifier que TK5 écoute sur le port 3270
nc -z localhost 3270 && echo "OK" || echo "TK5 non joignable"
```

### ABEND dans le spool

```bash
bash mvs/herc.sh spool
# ou pour plus de détail
bash mvs/10_spool_reader.sh GSTKCOMP --cobol-errors
```

Causes fréquentes :
- `IEF452I` / `JCL ERROR` : erreur JCL (dataset non alloué, nom de membre incorrect)
- `ABEND S0C1` : programme non link-édité ou LOADLIB non dans le DFHRPL CICS
- `MAPFAIL` : mapset non chargé — refaire `bash mvs/03_cics.sh install`

### KICKS ne démarre pas

```bash
bash mvs/12_kicks_install.sh status
# Vérifier DYNAMNBR
# Dans TSO : EXECUTIL TSSCMD IKJACCNT DYNAMNBR
```

KICKS démarre sous TSO (pas en STC) — il faut une session 3270 active :
```
x3270 localhost:3270
# Logon HERC02 / CUL8TR
# TSO: EXEC 'KICKS.KICKSSYS.V1R5M0.CLIST(KICKS)'
```

### Build incrémental ne détecte pas les changements

```bash
make clean   # supprimer .checksums
make build   # full rebuild
```

---

## Références

- [GSTK/README.md](../GSTK/README.md) — Projet GSTK (programmes COBOL/CICS)
- [MVS TK5](http://www.prince-webdesign.nl/index.php/software/16-hercules-mvs-3-8j-turnkey-5) — distribution MVS 3.8j Turnkey 5
- [KICKS User's Guide 1.5.0](http://www.kicksfortso.com/) — documentation KICKS
- [s3270 man page](https://x3270.miraheze.org/wiki/S3270) — automatisation terminal 3270
