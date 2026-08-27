# TROUBLESHOOTING — GSTK

Diagnostic et résolution des problèmes fréquents.

---

## Erreurs CICS (dans x3270)

### `PGMIDERR`

```
TRANSACTION ABENDED. CONDITION: PGMIDERR
```

**Cause :** Le programme n'est pas défini dans CICS ou n'existe pas en LOADLIB.

**Vérifier :**
```
CEMT INQ PROG(GSTK007)
```
Doit afficher `EN(ENABLED)`. Si `PGMIDERR` : programme absent du catalogue CICS.

**Corriger :**
```bash
# 1. S'assurer que le programme est compilé
bash scripts/mvs/02_submit.sh cobol

# 2. Définir dans CICS si absent
bash scripts/mvs/03_cics.sh install

# 3. Recharger si déjà défini mais ancien module
bash scripts/mvs/03_cics.sh newcopy
```

---

### `MAPFAIL`

```
TRANSACTION ABENDED. CONDITION: MAPFAIL
```

**Cause :** Le mapset BMS n'est pas assemblé ou n'est pas défini dans CICS.

**Corriger :**
```bash
bash scripts/mvs/02_submit.sh bms        # ré-assembler les BMS
bash scripts/mvs/03_cics.sh newcopy      # recharger
```
Puis dans x3270 :
```
CEMT INQ MAPSET(GSTK007M)
```

---

### `LENGERR`

**Cause :** La longueur de la COMMAREA dans `EXEC CICS RETURN LENGTH(xxx)` ne correspond pas à `EIBCALEN` reçu.

**Vérifier dans le programme :**
- Toutes les instructions `EXEC CICS RETURN` ont `LENGTH(263)`
- Le programme appelant (ex : GSTK000) passe aussi `LENGTH(263)` dans `EXEC CICS XCTL`

---

### `ASRA` (S0C7 — données numériques)

**Cause :** Un champ numérique contient des données non numériques (souvent une variable host SQL non initialisée).

**Déboguer avec CEDF :**
```
CEDF G007
```
CEDF trace chaque `EXEC CICS` et affiche les zones de données. S0C7 se produit généralement sur un `COMPUTE` ou `MOVE` numérique.

**Causes fréquentes dans GSTK :**
- `HV-MVT-QTE` (COMP-3) non initialisé avant un `COMPUTE W-MNT = HV-MVT-QTE * ...`
- Champ BMS `NUM` contenant des espaces récupérés par `RECEIVE MAP`

**Corriger :** Initialiser toutes les variables numériques :
```cobol
MOVE ZERO TO HV-MVT-QTE HV-MVT-MHT W-I W-CNT
```

---

### `NOTOPEN` — connexion DB2

**Cause :** La connexion CICS-DB2 n'est pas active.

**Vérifier :**
```
CEMT INQ DB2CONN
```

**Note :** TK5 (MVS 3.8j) n'a pas DB2. Les programmes GSTK avec SQL ne peuvent pas s'exécuter nativement sur TK5. Pour les tests SQL, utiliser PostgreSQL local.

---

### Écran qui reste vide après `Enter`

**Cause :** `SEND MAP DATAONLY` sans données dans le mapset output → tous les champs sont espaces → écran vide.

**Corriger :** Vérifier que `5000-AFFICHER-ECRAN` remplit au moins les champs fixes (DATHR, OPENAM, TERNAM, PAGCUR, PAGTOT) avant `SEND MAP`.

---

### Les filtres se réinitialisent à chaque touche PF

**Cause :** Les filtres ne sont pas sauvegardés en COMMAREA avant `EXEC CICS RETURN`.

**Vérifier dans `5000-AFFICHER-ECRAN` :**
```cobol
MOVE W-FIL-CODE  TO GSTK-COMMAREA(209:12)
MOVE W-FIL-LIBL  TO GSTK-COMMAREA(221:22)
```
Et dans `2000-RETOUR-TRANSACTION` (début) :
```cobol
MOVE GSTK-COMMAREA(209:12) TO W-FIL-CODE
MOVE GSTK-COMMAREA(221:22) TO W-FIL-LIBL
```

---

### La pagination ne fonctionne pas (PF7/PF8 sans effet)

**Cause 1 :** `W-PAGE-CUR` et `W-PAGE-TOT` ne sont pas sauvegardés.  
Solution : utiliser `CA-PAGE-COURANTE` et `CA-NB-PAGES` dans la COMMAREA publique, ou sauvegarder dans le FILLER.

**Cause 2 :** Le curseur est rouvert avec les mêmes paramètres mais `W-SKIP` est calculé depuis `W-PAGE-CUR` non mis à jour.  
Vérifier que `W-PAGE-CUR` est incrémenté/décrémenté *avant* l'appel à `4000-REQUETE-xxx`.

---

## Erreurs de compilation COBOL

### `COBOL: DECLARE CURSOR not at start of PROCEDURE DIVISION`

**Cause :** Le `EXEC SQL DECLARE ... CURSOR FOR` n'est pas immédiatement après `PROCEDURE DIVISION.`

**Corriger :** Déplacer toutes les déclarations de curseur en tête absolue de PROCEDURE DIVISION, avant `0000-MAIN.`

---

### `undefined name 'DFHCOMMAREA'` ou `EIBCALEN`

Normal quand on compile avec GnuCOBOL sans `-fstd=ibm` ou sans les inclusions CICS. Ces noms sont fournis par le runtime CICS, pas par le compilateur.

Compilation locale :
```bash
cobc -std=ibm -fsyntax-only -fno-cics -fno-sql -I GSTK/ GSTK007.cbl
```
`-fno-cics` et `-fno-sql` ignorent les blocs `EXEC CICS` et `EXEC SQL`.

---

### Output field name incorrect (`MNTHTEO` vs `MNTHEO`)

Le suffixe `O` s'ajoute au *nom complet* du champ BMS :
- Champ BMS `MNTHTE` (6 chars) → `MNTHTEO` en COBOL (7 chars)
- Champ BMS `QTESOR` (6 chars) → `QTESORO` en COBOL (7 chars)

Rechercher les incohérences :
```bash
grep -n "IN GSTK00[0-9]O" GSTK/GSTK007.cbl
```
Croiser avec les noms dans le BMS correspondant.

---

### `W-PROG-CIBLE` — erreur de niveau

```
COBOL: Level number must be between 01 and 49, or 66, 77, 88
```

**Cause :** Un `01 W-PROG-CIBLE` déclaré par erreur dans PROCEDURE DIVISION.

**Corriger :** Déplacer la déclaration dans `WORKING-STORAGE SECTION`.

---

## Erreurs SQL / PostgreSQL

### `ERROR: column "mvt_art_code" does not exist`

Le schéma DB2 original utilise `ART_CODE` dans MOUVEMENTS_STOCK mais les programmes COBOL insèrent dans `MVT_ART_CODE`.

**Corriger :** Utiliser `scripts/01_pg_schema.sql` (adapté) plutôt que `BASE de donnée.sql` (schéma original DB2).

```bash
bash scripts/03_pg_setup.sh --reset   # recrée avec le bon schéma
```

---

### `ERROR: new row violates check constraint "ck_mvt_type"`

**Cause :** Le programme insère `'BON ENTREE'` mais le CHECK n'accepte que `'ENTREE'`.

**Corriger :** Le schéma PostgreSQL adapté (`01_pg_schema.sql`) étend le CHECK :
```sql
CONSTRAINT ck_mvt_type CHECK (mvt_type IN
    ('BON ENTREE','BON SORTIE','AJUST+','AJUST-','ENTREE','SORTIE',...))
```

---

### `SQLCODE +100` — curseur vide

Normal en fin de résultats. Le programme doit détecter :
```cobol
IF SQLCODE NOT = 0
    MOVE 'N' TO W-FETCH-OK
END-IF
```
Si `W-NB-MVT = 0`, afficher un message "AUCUN ENREGISTREMENT TROUVE" dans `MSGRTRО`.

---

### La séquence `SEQ_MVT` n'existe pas

```
ERROR: relation "gstk.seq_mvt" does not exist
```

Le schéma DB2 original ne définit que `SEQ_BON_ENTREE` et `SEQ_BON_SORTIE`.  
`SEQ_MVT` est ajoutée par `01_pg_schema.sql` :

```bash
psql gstk -c "CREATE SEQUENCE gstk.seq_mvt START WITH 1 INCREMENT BY 1;"
```

---

## Erreurs MVS / JES2

### Job `GSTKCOMP` terminé avec RC=8 ou RC=12

RC=8 = warnings, RC=12 = erreurs bloquantes dans la compilation COBOL.

**Lire le listing :**
```bash
bash scripts/mvs/10_spool_reader.sh --rc GSTKCOMP
```

**Causes fréquentes :**
- COPYLIB introuvable → vérifier que `HLQ.GSTK.COPYLIB(GSTKCOMM)` existe (uploader Copybook.cbl)
- SQL DBRM non généré → vérifier le step DFHEITALC (préprocesseur CICS)
- Dataset LOADLIB plein → augmenter le SPACE dans `00_alloc.jcl`

---

### `HASP000 OK` mais le job n'apparaît pas

Le job a été soumis mais JES2 l'a rejeté avant de le démarrer.

**Vérifier :**
```bash
bash scripts/mvs/herc.sh log 30
```
Chercher `HASP165` (job rejected) ou `IEF453I` (JCL error).

Cause habituelle : `CLASS=A` non disponible → changer en `CLASS=B` ou `CLASS=*` dans le JCL.

---

### Upload IND$FILE échoue silencieusement

**Symptôme :** `01_upload.sh` affiche OK mais le dataset MVS est vide.

**Causes :**
1. Le dataset MVS n'est pas alloué → soumettre `00_alloc.jcl` d'abord
2. Le membre dépasse 80 colonnes → vérifier que le fichier local est bien FB80
3. Timeout s3270 → augmenter `Wait(30,Output)` à `Wait(60,Output)`

**Vérifier en TSO :**
```
LISTDS 'HERC02.GSTK.SOURCE' MEMBERS
```

---

## Problèmes s3270 / TN3270

### `s3270: Unable to connect`

```bash
nc -z localhost 3270 && echo "port ouvert" || echo "TK5 non démarré"
docker ps | grep mvs-tk5
```

Si le container est arrêté :
```bash
docker start mvs-tk5
# Attendre 2-3 min le démarrage complet de MVS avant de connecter
bash scripts/mvs/herc.sh watch   # surveiller le démarrage
```

TK5 est prêt quand le syslog affiche `$HASP000 OK`.

---

### `fswatch` ne détecte pas les sauvegardes VSCode

VSCode sauvegarde en mode "atomic write" (rename) — certaines versions de fswatch ne détectent pas `Renamed`.

**Corriger dans `07_watch.sh` :** ajouter `--event Renamed` :
```bash
"$FSWATCH" \
    --event Updated \
    --event Created \
    --event Renamed \      # ← ajouter
    ...
```

---

## Connexion x3270

```bash
# Connexion directe
x3270 localhost:3270

# Mode terminal (dans le terminal VSCode)
c3270 localhost:3270

# Scripté (CI/CD)
s3270 localhost:3270
```

**Credentials TK5 par défaut :**
- Utilisateur TSO : `HERC02`
- Mot de passe : `CUL8TR`

**Premier écran TK5 :** `ENTER LOGON ID —` → taper `HERC02` + Enter  
**Écran TSO :** taper le mot de passe + Enter  
**Prompt TSO :** `READY` → taper une commande TSO ou `CICS` pour accéder à CICS

---

## Diagnostic rapide (checklist)

Avant de déployer sur MVS :
```bash
bash scripts/04_cobc_check.sh     # ✓ 0 erreur de syntaxe
bash scripts/05_test_sql.sh       # ✓ 0 test SQL échoué
bash scripts/mvs/herc.sh spool    # ✓ pas d'ABEND récent
```

Après déploiement, dans x3270 :
```
CEMT INQ PROG(GSTK*)              → 8 programmes ENABLED
CEMT INQ TRAN(G*)                 → 8 transactions ENABLED
G000                              → menu principal s'affiche
```
