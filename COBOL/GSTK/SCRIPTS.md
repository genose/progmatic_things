# Scripts GSTK — Documentation

Tous les scripts se trouvent dans `scripts/`.  
Le `Makefile` regroupe les commandes fréquentes.

---

## Vue d'ensemble

```
scripts/
├── Makefile                     Point d'entrée (make check, make build, make ci...)
│
├── 01_pg_schema.sql             Schéma PostgreSQL adapté depuis DB2
├── 02_pg_data.sql               Données de test (10 articles, 8 mouvements)
├── 03_pg_setup.sh               Setup complet PostgreSQL (schema + data)
├── 04_cobc_check.sh             Vérification syntaxe GnuCOBOL (local)
├── 05_test_sql.sh               Tests unitaires SQL par programme
│
├── jcl/
│   ├── GSTKBMS.jcl              Assemblage des 8 mapsets BMS (MVS)
│   └── GSTKCOMP.jcl             Compilation COBOL/CICS/DB2 + link edit (MVS)
│
├── cics/
│   └── CEDA_GSTK.txt            Commandes CEDA/CEMT à exécuter dans x3270
│
└── mvs/
    ├── herc.sh                  Interface Hercules HTTP (syslog, status)
    ├── 00_alloc.jcl             Allocation initiale des datasets MVS
    ├── 01_upload.sh             Upload IND$FILE des sources vers MVS
    ├── 02_submit.sh             Soumission JCL + attente fin de job
    ├── 03_cics.sh               Définitions CICS (CEDA + CEMT) via s3270
    ├── 06_build.sh              Build incrémental (MD5, ne recompile que le changé)
    ├── 07_watch.sh              fswatch → build auto à chaque sauvegarde VSCode
    ├── 08_test_cics.sh          Tests CICS automatisés (capture écran + assertions)
    ├── 09_ci.sh                 Pipeline CI/CD complet 5 étapes
    ├── 10_spool_reader.sh       Lecture spool JES2 + détection ABEND/RC
    └── 11_git_setup.sh          git init + hook pre-commit bloquant
```

---

## Scripts locaux (Mac)

### `03_pg_setup.sh` — Setup PostgreSQL

```bash
bash scripts/03_pg_setup.sh           # créer/mettre à jour la base
bash scripts/03_pg_setup.sh --reset   # supprimer et recréer de zéro
```

Crée la base `gstk`, charge `01_pg_schema.sql` puis `02_pg_data.sql`.

**Adaptations DB2 → PostgreSQL dans `01_pg_schema.sql` :**
- `GENERATED ALWAYS AS IDENTITY` → `DEFAULT nextval('gstk.seq_mvt')`
- Triggers PL/pgSQL au lieu de SQL procédural DB2
- Colonne `MVT_ART_CODE` (au lieu de `ART_CODE`) pour correspondre aux programmes COBOL
- CHECK `MVT_TYPE` élargi : 'BON ENTREE', 'BON SORTIE' (valeurs réelles insérées)
- CHECK `MVT_SENS` élargi : 'E', 'S' (au lieu de '+', '-')

---

### `04_cobc_check.sh` — Vérification syntaxe

```bash
bash scripts/04_cobc_check.sh              # tous les programmes
bash scripts/04_cobc_check.sh GSTK007     # un seul
```

Flags utilisés : `-std=ibm -fsyntax-only -fno-cics -fno-sql -W`

Les warnings sur `EXEC CICS` et `EXEC SQL` sont normaux (pas de runtime CICS local).

---

### `05_test_sql.sh` — Tests SQL unitaires

```bash
bash scripts/05_test_sql.sh
```

Teste programme par programme :
- GSTK001 : curseur articles + filtre LIKE
- GSTK002 : lecture article, INSERT mouvement, nextval séquence
- GSTK003 : vérification stock avant sortie, INSERT sortie
- GSTK004 : INSERT article, UPDATE, archive
- GSTK005 : agrégats KPI, GROUP BY catégorie
- GSTK006 : curseur alertes (stock < min), tri criticité
- GSTK007 : curseur historique, COUNT/SUM total, filtres date/type

Exit code = nombre de tests échoués.

---

### `Makefile` — Commandes fréquentes

```bash
make check          # syntaxe COBOL + tests SQL (local)
make pg-setup       # créer la base PostgreSQL
make pg-reset       # recréer la base de zéro

make upload         # uploader tous les sources vers MVS
make bms            # assembler les 8 mapsets BMS
make cobol          # compiler les 8 programmes COBOL
make deploy         # CICS NEWCOPY (recharger sans restart)
make build          # upload + bms + cobol + deploy
make inc            # build incrémental (06_build.sh)

make test           # tests CICS automatisés
make ci             # pipeline complet (check+build+test)
make watch          # surveiller les fichiers + build auto

make log            # syslog MVS en temps réel
make status         # état Hercules + CICS
make spool          # activité JES2 récente

make install-all    # première installation complète
make clean          # réinitialiser les checksums (force full rebuild)
```

Variables surchargeables :
```bash
make upload PROG=GSTK007     # uploader un seul programme
make test-one TRANS=G007     # tester une transaction
make spool-read JOB=GSTKCOMP # lire la spool d'un job
```

---

## Scripts MVS (communication avec TK5)

### Prérequis

```bash
# Container TK5 en cours d'exécution
docker ps | grep mvs-tk5        # doit montrer port 3270 et 8038

# Variables d'environnement (optionnel, valeurs par défaut ci-dessous)
export TK5_HOST=localhost
export TK5_PORT=3270
export TSO_USER=HERC02
export TSO_PASS=CUL8TR
export HLQ=HERC02
export HERC_URL=http://localhost:8038
export S3270=/usr/local/bin/s3270
```

---

### `herc.sh` — Interface Hercules

```bash
bash scripts/mvs/herc.sh log [N]      # N dernières lignes du syslog (défaut: 25)
bash scripts/mvs/herc.sh watch        # syslog en temps réel (Ctrl-C pour arrêter)
bash scripts/mvs/herc.sh status       # état Hercules + container Docker
bash scripts/mvs/herc.sh spool        # événements JES2 récents (HASP, ABEND)
bash scripts/mvs/herc.sh devlist      # liste des périphériques Hercules
```

> **Important :** L'API HTTP Hercules (port 8038) accepte uniquement les commandes Hercules (`devlist`, `devstat`, `ipl`...). Les commandes JES2 (`$D A`, `$P`) nécessitent s3270 → TSO → SDSF.

---

### `01_upload.sh` — Upload sources

Protocole IND$FILE (TN3270 file transfer) via `s3270`.

```bash
bash scripts/mvs/01_upload.sh           # tout uploader (sources + BMS + copybook + JCL)
bash scripts/mvs/01_upload.sh --cbl     # sources COBOL + copybook seulement
bash scripts/mvs/01_upload.sh --bms     # BMS seulement
bash scripts/mvs/01_upload.sh --jcl     # JCL seulement
bash scripts/mvs/01_upload.sh GSTK007  # un programme + son BMS
```

Correspondance des datasets MVS :

| Fichier local         | Dataset MVS                      |
|-----------------------|----------------------------------|
| GSTK00x.cbl           | HLQ.GSTK.SOURCE(GSTK00x)        |
| GSTK00xM.bms          | HLQ.GSTK.BMS(GSTK00xM)          |
| Copybook.cbl          | HLQ.GSTK.COPYLIB(GSTKCOMM)      |
| scripts/jcl/*.jcl     | HLQ.GSTK.JCL(GSTKBMS/GSTKCOMP) |

---

### `02_submit.sh` — Soumission JCL

```bash
bash scripts/mvs/02_submit.sh alloc    # allouer les datasets MVS (1ère fois uniquement)
bash scripts/mvs/02_submit.sh bms      # assembler les mapsets BMS (GSTKBMS.jcl)
bash scripts/mvs/02_submit.sh cobol    # compiler les programmes (GSTKCOMP.jcl)
bash scripts/mvs/02_submit.sh all      # séquence complète guidée
bash scripts/mvs/02_submit.sh watch    # surveiller le syslog MVS
bash scripts/mvs/02_submit.sh status   # jobs actifs + initiateurs JES2
```

Le script attend la fin du job en surveillant le syslog Hercules (pattern `HASP395`).

---

### `03_cics.sh` — Définitions CICS

```bash
bash scripts/mvs/03_cics.sh install    # CEDA DEF (8 MAPSET + 8 PROGRAM + 8 TRANS) + INSTALL
bash scripts/mvs/03_cics.sh newcopy    # CEMT SET PROG(GSTKxxx) NEWCOPY (après recompilation)
bash scripts/mvs/03_cics.sh status     # CEMT INQ PROG(GSTK*)
bash scripts/mvs/03_cics.sh trans G007 # lancer une transaction en test
```

**Ordre d'installation CICS (à faire une fois) :**
1. `install` → définit toutes les ressources
2. Vérifier dans x3270 : `CEMT INQ PROG(GSTK*)` → 8 programmes ENABLED
3. Taper `G000` → menu principal

**Après chaque recompilation :**
```bash
bash scripts/mvs/03_cics.sh newcopy
```

---

### `06_build.sh` — Build incrémental

Détecte les fichiers modifiés via MD5, n'uploade et ne recompile que ceux-là.

```bash
bash scripts/mvs/06_build.sh           # build incrémental (MD5 checksum)
bash scripts/mvs/06_build.sh --full    # forcer le rebuild de tout
bash scripts/mvs/06_build.sh --dry     # voir ce qui changerait sans rien faire
```

Les checksums sont stockés dans `scripts/mvs/.checksums`.  
`make clean` les efface pour forcer un full rebuild.

**Séquence interne :**
1. Calculer MD5 de tous les `.cbl`, `.bms`, `.cpy`
2. Comparer avec `.checksums`
3. Upload uniquement des fichiers changés
4. Assembler BMS si des `.bms` ont changé
5. Compiler COBOL si des `.cbl` ont changé
6. CICS NEWCOPY
7. Sauvegarder les nouveaux checksums

---

### `07_watch.sh` — Hot-reload

Utilise `fswatch` (MacPorts) pour surveiller le répertoire `GSTK/`.

```bash
bash scripts/mvs/07_watch.sh           # build auto (sans tests)
bash scripts/mvs/07_watch.sh --check   # build + tests CICS auto
```

**Séquence sur chaque sauvegarde :**
1. Détection du fichier modifié (filtre : `.cbl`, `.bms`, `.cpy`)
2. Vérification syntaxe locale (GnuCOBOL) — si erreur, build annulé
3. Build incrémental MVS (`06_build.sh`)
4. (Optionnel avec `--check`) tests CICS smoke

Debounce de 1,5 s pour éviter les double-triggers sur sauvegarde VSCode.

---

### `08_test_cics.sh` — Tests CICS automatisés

Capture les écrans 3270 via s3270 et vérifie la présence de chaînes attendues.

```bash
bash scripts/mvs/08_test_cics.sh all         # tous les tests (8 transactions)
bash scripts/mvs/08_test_cics.sh smoke       # tests rapides (G000 + G001)
bash scripts/mvs/08_test_cics.sh trans G007  # une transaction
bash scripts/mvs/08_test_cics.sh report      # afficher le dernier rapport
```

Chaque test vérifie :
- Le titre de l'écran (`GSTKxxx`)
- Les colonnes ou champs attendus
- L'absence d'erreurs CICS (`PGMIDERR`, `ABEND`, `MAPFAIL`)

---

### `09_ci.sh` — Pipeline CI/CD

```bash
bash scripts/mvs/09_ci.sh              # pipeline complet
bash scripts/mvs/09_ci.sh --no-mvs    # local uniquement (syntaxe + SQL)
bash scripts/mvs/09_ci.sh --no-test   # build sans tests CICS
bash scripts/mvs/09_ci.sh --fast      # smoke tests uniquement
```

**Étapes :**

| Étape | Action                    | En cas d'échec                    |
|-------|---------------------------|-----------------------------------|
| 1/5   | Syntaxe COBOL             | Arrêt du pipeline                 |
| 2/5   | Tests SQL PostgreSQL      | Warning, pipeline continue        |
| 3/5   | Build incrémental MVS     | Erreur signalée, pipeline continue|
| 4/5   | Lecture spool JES2        | Détection ABEND/JCL ERROR         |
| 5/5   | Tests CICS automatisés    | Rapport d'échec                   |

Rapport sauvegardé dans `scripts/mvs/.reports/ci_YYYYMMDD_HHMMSS.txt`.  
Lien symbolique `scripts/mvs/.reports/latest.txt` → dernier rapport.  
Historique dans `scripts/mvs/.ci_history.log`.

---

### `10_spool_reader.sh` — Lecture spool

```bash
bash scripts/mvs/10_spool_reader.sh              # événements JES2 récents + historique CI
bash scripts/mvs/10_spool_reader.sh GSTKCOMP     # events d'un job spécifique
bash scripts/mvs/10_spool_reader.sh --errors     # uniquement les ABEND/JCL ERROR
bash scripts/mvs/10_spool_reader.sh --rc GSTKCOMP # return codes par étape
bash scripts/mvs/10_spool_reader.sh --history    # historique des builds CI
```

---

### `11_git_setup.sh` — Git + hooks

```bash
bash scripts/mvs/11_git_setup.sh                 # git init + .gitignore + hook pre-commit
bash scripts/mvs/11_git_setup.sh --with-mvs-push # ajoute aussi le hook post-commit (build auto)
```

**Hook pre-commit (bloquant) :**
- Vérifie la syntaxe GnuCOBOL des fichiers `.cbl` dans le commit
- Vérifie la structure BMS (DFHMSD + DFHMDI présents) des `.bms`
- Test SQL minimal si PostgreSQL disponible
- Bloque le commit en cas d'erreur

**Hook post-commit (optionnel `--with-mvs-push`) :**
- Lance `06_build.sh` en arrière-plan après chaque commit réussi

---

## Workflow recommandé

### Développement d'une fonctionnalité

```bash
# Terminal 1 : hot-reload actif
bash scripts/mvs/07_watch.sh

# Terminal 2 : travailler dans VSCode
# → sauvegarder un .cbl → build automatique dans Terminal 1
# → taper G00x dans x3270 pour tester
```

### Validation avant rendu

```bash
make ci                    # pipeline complet
cat scripts/mvs/.reports/latest.txt   # lire le rapport
```

### Diagnostic d'une erreur de compilation MVS

```bash
bash scripts/mvs/10_spool_reader.sh --rc GSTKCOMP    # return codes
bash scripts/mvs/10_spool_reader.sh --errors          # ABEND et JCL ERROR
bash scripts/mvs/herc.sh log 50                       # syslog brut
```

### Diagnostic d'une erreur CICS

```bash
# Dans x3270, sur l'écran d'erreur CICS :
CEDF G007          # mode débogage pas-à-pas (trace EXEC CICS)

# Via script :
bash scripts/mvs/08_test_cics.sh trans G007   # capturer l'écran d'erreur

# Codes d'erreur fréquents :
# PGMIDERR  → programme non défini ou non trouvé en LOADLIB
#             → bash scripts/mvs/03_cics.sh newcopy
# MAPFAIL   → mapset non assemblé
#             → bash scripts/mvs/02_submit.sh bms
# LENGERR   → longueur COMMAREA incorrecte (vérifier LENGTH=263)
# NOTOPEN   → connexion DB2 non ouverte (vérifier CEMT INQ DB2CONN)
```
