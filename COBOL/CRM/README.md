# CRM — Programmes COBOL et migration Java 8

Ce dossier contient les sources COBOL OpenVMS du périmètre **CRM/EXTRANET**, les analyses techniques associées, et le projet Java 8 TDD issu de la migration.

---

## Contenu du dossier

| Fichier / Dossier | Type | Rôle |
|-------------------|------|------|
| `D05_VERIF_CRM.SCO` | Source COBOL | Vérification synchro commandes DEPOT/CRM |
| `T10_MAJ_DTLIVR_BDCRM.COB` | Source COBOL | Mise à jour date de livraison dans BD_CRM |
| `ANALYSE_D05_VERIF_CRM.md` | Analyse technique | Analyse détaillée D05, comportements, TDD |
| `ANALYSE_T10_MAJ_DTLIVR_BDCRM.md` | Analyse technique | Analyse détaillée T10, comportements, TDD |
| `crm-java/` | Projet Maven Java 8 | Migration TDD des deux programmes |

---

## D05_VERIF_CRM.SCO

### Rôle

Batch OpenVMS/COBOL (Oracle Rdb SQL embarqué) qui vérifie que les commandes présentes dans **BD_DEPOT** pour une date de bon de livraison (`DATEBL`) sont synchronisées avec leur équivalent dans **BD_CRM**.

Opère en **deux phases** :
1. **Scan READ ONLY** : parcourt `D.CDE` via le curseur CURCDE, compare `STATUT` avec `STATENCOURS` dans `S.CDE_FAC`, accumule les écarts dans un tableau interne (max 9 000 entrées).
2. **MAJ READ WRITE** (si `P-MAJ='O'` et écarts > 0) : met à jour `D.CDE` avec `STATCRM=''` et `FLAG_CRM='O'` pour chaque commande en écart. Un seul COMMIT global.

### Paramètres

```
P-CODDEP  PIC XX       — code dépôt (ex : "CO", "MO")
P-DATEBL  PIC 9(8)     — date YYYYMMDD (ex : "20241108")
P-MAJ     PIC X        — 'O' = effectuer les mises à jour
```

### Bases de données

| Alias | Base | Table |
|-------|------|-------|
| D | BD_DEPOT | D.CDE |
| S | BD_CRM | S.CDE_FAC |

### Règles métier critiques

| Code | Règle |
|------|-------|
| B-D05-03 | FAT(DEPOT)≡FAP(CRM) et GLT(DEPOT)≡GLP(CRM) sont des statuts **équivalents** (pas des anomalies) |
| B-D05-04 | Clé UPDATE = `(CODLAB, NUMCDE, NUMRAL)` — **CODDEP absent** du WHERE |
| B-D05-05 | UPDATE : `STATCRM←''`, `FLAG_CRM←'O'`, STATUT inchangé |
| B-D05-06 | Un seul `COMMIT` global après **tous** les UPDATEs (pas de commit par ligne) |
| B-D05-07 | `ROLLBACK` conditionnel : seulement si `P-MAJ='O'` ET `cpt-cde > 0` |
| B-D05-09 | Overflow tableau (> 9 000) → DISPLAY warning, traitement continue |

### Filtre CURCDE

```sql
WHERE cast(cast(datebl AS DATE ANSI) AS DATE VMS) = :W-DATEBL
  AND FLAG_CRM = 'N'
  AND STATUT <> 'THO'   -- (écrit deux fois dans le source — comportement identique)
  AND STATUT <> 'SIX'
  AND STATUT <> 'EIX'
```

---

## T10_MAJ_DTLIVR_BDCRM.COB

### Rôle

Batch OpenVMS/COBOL qui met à jour `DTLIVR` et `FLAGLIV='O'` dans `E.CDE_FAC` (BD_CRM) à partir des fichiers de retour de transport (format `MAJBDSTAT*.DAT`).

Découverte automatique des fichiers via `LIB$FIND_FILE` sur le pattern `DIRDAT:MAJBDSTAT*.DAT;*`.

### Paramètres

Aucun paramètre explicite. Le programme est autonome (lit tous les fichiers présents).

### Base de données

| Alias | Base | Table |
|-------|------|-------|
| E | BD_CRM | E.CDE_FAC |

### Format fichier d'entrée (MAJBDSTAT*.DAT)

| Champ | Description |
|-------|-------------|
| `MAJBD-CODDEP` | Code dépôt (4 chars) |
| `MAJBD-CODLAB` | Code laboratoire (4 chars) |
| `MAJBD-NUMCDE` | Numéro commande (7 chiffres) |
| `MAJBD-NUMRAL` | Numéro ralliement (1 chiffre) |
| `MAJBD-DATLIV` | Date livraison — format VMS ASCII `DD-MON-YYYY HH:MM:SS.CC` |

### Règles métier critiques

| Code | Règle |
|------|-------|
| B-T10-01 | `MAJBD-DATLIV` est déjà en format VMS ASCII — passé directement à `SYS$BINTIM` |
| B-T10-03 | `CODDEP="FO"` → remplacé par `"MO"` **avant** toute requête SQL |
| B-T10-05 | Recherche et MAJ 9994 : `CODDEP='CO'` **hardcodé** |
| B-T10-07 | `COMMIT` **par enregistrement** (pas de commit global) |
| B-T10-09 | Update 9994 utilise la même `DATBIN` que l'update principal |
| B-T10-10 | DEADLOCK → `STOP RUN` (pas de retry réel malgré la boucle) |
| B-T10-11 | Erreur fatale → `LIB$SET_LOGICAL ARRPRG=O` (signal VMS batch) |

### Cascade 3628 → 9994 (TST-3628-9994)

Si `CODLAB='3628'`, le programme vérifie si une commande miroir existe sous `CODLAB='9994'` et `CODDEP='CO'` :

```
1. SELECT CDELAB(1:8) FROM E.CDE_FAC WHERE CODDEP=coddep AND CODLAB='3628'
2. Si CDELAB IS NUMERIC :
     numcde9994 = CDELAB[0:7]   (7 premiers chiffres)
     numral9994 = CDELAB[7:8]   (8e chiffre)
3. SELECT FROM E.CDE_FAC WHERE CODDEP='CO' AND CODLAB='9994'
     AND NUMCDE=numcde9994 AND NUMRAL=numral9994
4. Si trouvé → UPDATE 9994 avec même DTLIVR
```

---

## Projet Java 8 — `crm-java/`

### Lancer les tests

```bash
cd crm-java
mvn test
```

**59 tests — 0 échec.**

### Lancer les programmes (sur environnement avec Oracle Rdb JDBC)

```bash
# D05 — vérification synchro DEPOT/CRM
java -cp target/d05-verif-crm.jar:lib/rdb-jdbc.jar \
     com.example.crm.d05.main.D05Main \
     jdbc:rdb://host/BD_DEPOT  jdbc:rdb://host/BD_CRM \
     user pass \
     CO 20241108 O

# T10 — mise à jour DTLIVR depuis fichiers MAJBDSTAT
java -cp target/t10-maj-dtlivr-bdcrm.jar:lib/rdb-jdbc.jar \
     com.example.crm.t10.main.T10Main \
     jdbc:rdb://host/BD_CRM user pass /data/majbdstat
```

### Structure complète

```
crm-java/src/main/java/com/example/crm/
├── common/
│   └── DriverManagerDataSource.java      — DataSource minimal (DriverManager)
├── d05/
│   ├── adapter/jdbc/
│   │   └── RdbDepotCdeRepository.java    — JDBC Oracle Rdb (BD_DEPOT + BD_CRM)
│   ├── application/
│   │   └── VerifCrmService.java          — logique principale D05
│   ├── domain/
│   │   ├── CommandeDepot.java            — ligne CURCDE
│   │   ├── CommandeDiscordante.java       — entrée tab-cde
│   │   ├── StatutEquivalence.java         — règles FAT≡FAP, GLT≡GLP
│   │   └── VerifCrmResult.java           — résultat du service
│   ├── main/
│   │   └── D05Main.java                  — point d'entrée batch D05
│   └── port/
│       └── DepotCdeRepository.java       — port accès BD_DEPOT + BD_CRM
└── t10/
    ├── adapter/
    │   ├── file/
    │   │   └── MajbdstatFileReader.java   — lecteur fichier MAJBDSTAT*.DAT
    │   └── jdbc/
    │       ├── RdbCdeFacRepository.java   — JDBC Oracle Rdb (BD_CRM.E.CDE_FAC)
    │       └── VmsDateParser.java         — "DD-MON-YYYY HH:MM:SS.CC" → Timestamp
    ├── application/
    │   ├── CascadeLabo3628.java           — détection commande miroir 9994
    │   └── MajDtlivrService.java          — traitement par enregistrement
    ├── domain/
    │   ├── DeliveryCodexNormalizer.java   — règle FO→MO
    │   ├── Livraison9994.java             — données commande miroir
    │   └── LivraisonRecord.java           — enregistrement MAJBDSTAT
    ├── main/
    │   └── T10Main.java                   — point d'entrée batch T10
    └── port/
        └── CdeFacRepository.java         — port accès E.CDE_FAC

crm-java/src/test/java/com/example/crm/
├── d05/
│   ├── application/VerifCrmServiceTest.java         — 15 tests
│   └── domain/StatutEquivalenceTest.java            —  8 tests
└── t10/
    ├── adapter/
    │   ├── file/MajbdstatFileReaderTest.java         —  7 tests
    │   └── jdbc/VmsDateParserTest.java               —  7 tests
    ├── application/CascadeLabo3628Test.java          —  7 tests
    ├── application/MajDtlivrServiceTest.java         —  9 tests
    └── domain/DeliveryCodexNormalizerTest.java       —  6 tests
```

### Architecture ports & adapters

```text
┌───────────────────────────────────────────────────┐
│  D05Main / T10Main  (orchestration + wiring)      │
└──────────────────┬────────────────────────────────┘
                   │
         ┌─────────▼─────────┐
         │   Service (app)   │  VerifCrmService / MajDtlivrService
         └─────────┬─────────┘
                   │ interface port
         ┌─────────▼─────────┐
         │  Adapter JDBC     │  RdbDepotCdeRepository / RdbCdeFacRepository
         │  Adapter File     │  MajbdstatFileReader
         └─────────┬─────────┘
                   │
         Oracle Rdb / Fichier MAJBDSTAT*.DAT
```

### Gestion transactionnelle (fidèle au COBOL)

| Programme | Comportement COBOL | Java |
| --------- | ------------------ | ---- |
| D05 | `SET TRANSACTION READ ONLY` → scan → `ROLLBACK` → `SET TRANSACTION READ WRITE` → loop UPDATE → `COMMIT` unique | Scan sur connexions éphémères read-only ; `rwConn` auto-commit=false maintenu pendant tous les `updateStatcrm()` ; `commit()` appelé par `VerifCrmService` après la boucle |
| T10 | `SET TRANSACTION READ WRITE` → UPDATE → [UPDATE 9994] → `COMMIT` par enregistrement | `txConn` auto-commit=false ouverte sur premier update ; `commit()` appelé par `MajDtlivrService.traiterRecord()` après chaque enregistrement |

### Couverture des comportements critiques

| Comportement | Testé dans |
|-------------|-----------|
| FAT≡FAP, GLT≡GLP | `StatutEquivalenceTest` |
| Absent de CRM = NOK | `VerifCrmServiceTest.commandeAbsenteDeCrm_estDiscordante` |
| Clé UPDATE sans CODDEP | `VerifCrmServiceTest.updateN_utilise_codlab_numcde_numral_sansCODDEP` |
| MAJ=false → aucun UPDATE | `VerifCrmServiceTest.majFalse_aucunUpdateEffectue_memeAvecDiscordantes` |
| Overflow 9000 | `VerifCrmServiceTest.overflow_9000_commandesNok_flagTableOverflow` |
| FO→MO avant SQL | `MajDtlivrServiceTest.coddep_fo_estRemappeEnMo_avantUpdate` |
| FO→MO avant cascade 3628 | `MajDtlivrServiceTest.fo_plus_labo3628_remappage_avant_cascade` |
| CDELAB non numérique = ignoré | `CascadeLabo3628Test.cdelab_nonNumerique_retourneEmptySilencieusement` |
| CODDEP='CO' hardcodé pour 9994 | `CascadeLabo3628Test.extraction_numcde9994_et_numral9994_depuis_cdelab` |
| Même DATBIN pour les deux updates | `MajDtlivrServiceTest.update9994_utilise_memeDatliv_queUpdatePrincipal` |
| Cascade seulement pour codlab=3628 | `MajDtlivrServiceTest.cascade_seulementAppeleePourLabo3628` |
| Format VMS "DD-MON-YYYY HH:MM:SS.CC" | `VmsDateParserTest` (7 tests dont 12 mois) |
| Enregistrement fixe 39 chars | `MajbdstatFileReaderTest` (parse, readAll, CR+LF, trop court) |

---

## Références

- [ANALYSE_D05_VERIF_CRM.md](ANALYSE_D05_VERIF_CRM.md) — analyse complète D05 (19 sections)
- [ANALYSE_T10_MAJ_DTLIVR_BDCRM.md](ANALYSE_T10_MAJ_DTLIVR_BDCRM.md) — analyse complète T10 (19 sections)
- Programmes COBOL sources : [D05_VERIF_CRM.SCO](D05_VERIF_CRM.SCO), [T10_MAJ_DTLIVR_BDCRM.COB](T10_MAJ_DTLIVR_BDCRM.COB)
