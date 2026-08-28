# Guide COBOL/CICS — Patterns utilisés dans GSTK

Référence technique des patterns récurrents dans les 8 programmes.

---

## Structure type d'un programme GSTK

```cobol
IDENTIFICATION DIVISION.
PROGRAM-ID. GSTKxxx.

ENVIRONMENT DIVISION.
CONFIGURATION SECTION.
SPECIAL-NAMES.
    DECIMAL-POINT IS COMMA.          ← virgule comme séparateur décimal

DATA DIVISION.
WORKING-STORAGE SECTION.

    COPY GSTKxxxM.                   ← map input/output (BMS généré)
    COPY GSTKCOMM.                   ← COMMAREA + enregistrements partagés
    COPY DFHAID.                     ← constantes touches PF (obligatoire si EVALUATE EIBAID)

    EXEC SQL INCLUDE SQLCA END-EXEC. ← SQL Communication Area

    01 HV-VARIABLES.                 ← host variables SQL (PIC S9 COMP-3)
       ...
    01 W-FLAGS.
       05 W-FETCH-OK PIC X VALUE 'Y'.
          88 FETCH-OK  VALUE 'Y'.
          88 FIN-FETCH VALUE 'N'.

PROCEDURE DIVISION.

*   CURSEUR STATIC — OBLIGATOIREMENT ICI (avant tout paragraphe)
    EXEC SQL DECLARE CURS-xxx CURSOR FOR
        SELECT ... FROM GSTK.TABLE
        WHERE col LIKE :HV-FILTRE
        ORDER BY col
    END-EXEC.

0000-MAIN.
    EVALUATE TRUE
        WHEN EIBCALEN = ZERO
            PERFORM 1000-PREMIERE-ENTREE
        WHEN OTHER
            PERFORM 2000-RETOUR-TRANSACTION
    END-EVALUATE
    STOP RUN.
```

---

## Pattern pseudo-conversational

### Première entrée

```cobol
1000-PREMIERE-ENTREE.
    INITIALIZE GSTK-COMMAREA.        ← éviter une COMMAREA non initialisée au premier RETURN
    EXEC CICS SEND MAP(C-MAP)
        MAPSET(C-MAPSET)
        MAPONLY ERASE         ← effacer l'écran, envoyer seulement la map statique
    END-EXEC
    EXEC CICS RETURN
        TRANSID(C-TRANS)
        COMMAREA(GSTK-COMMAREA)
        LENGTH(263)
    END-EXEC.
```

### Retour transaction

```cobol
2000-RETOUR-TRANSACTION.
    MOVE DFHCOMMAREA TO GSTK-COMMAREA    ← restaurer COMMAREA
*   Restaurer état local depuis FILLER
    MOVE GSTK-COMMAREA(209:12) TO W-FIL-CODE

    EXEC CICS RECEIVE MAP(C-MAP)
        MAPSET(C-MAPSET)
        INTO(GSTKxxxI)                   ← lire les saisies de l'opérateur
    END-EXEC

*   Mettre à jour filtres uniquement si l'opérateur a saisi quelque chose
    IF FILCOL IN GSTKxxxI > ZERO         ← L > 0 = champ modifié (MDT set)
        MOVE FILCOI IN GSTKxxxI TO W-FIL-CODE(1:10)
    END-IF

    EVALUATE EIBAID                       ← identifier la touche PF
        WHEN DFHPF3  PERFORM 6000-RETOUR-MENU
        WHEN DFHPF5  ...
        WHEN DFHPF7  ...
        WHEN DFHPF8  ...
        WHEN OTHER   ...
    END-EVALUATE.
```

### Afficher l'écran et retourner

```cobol
5000-AFFICHER-ECRAN.
*   Remplir les champs de sortie
    MOVE W-VALEUR  TO CHAMPО IN GSTKxxxO

*   Sauvegarder l'état local dans le FILLER COMMAREA
    MOVE W-FIL-CODE TO GSTK-COMMAREA(209:12)

    EXEC CICS SEND MAP(C-MAP)
        MAPSET(C-MAPSET)
        FROM(GSTKxxxO)
        DATAONLY CURSOR             ← mettre à jour les données + positionner le curseur
    END-EXEC
*   Variante si premier affichage ou écran complet nécessaire :
*   ERASE CURSOR                   ← effacer + redessiner l'écran entier + curseur
*   NB : DATAONLY et ERASE sont mutuellement exclusifs — ne pas combiner

    EXEC CICS RETURN
        TRANSID(C-TRANS)
        COMMAREA(GSTK-COMMAREA)
        LENGTH(263)
    END-EXEC.
```

---

## Pattern MAPFAIL sur XCTL entrant

Quand un programme est appelé par `EXEC CICS XCTL` avec une COMMAREA (EIBCALEN = 263),
il entre dans `2000-RETOUR-TRANSACTION` et tente immédiatement `RECEIVE MAP`.
La map n'ayant jamais été envoyée au terminal par ce programme, **MAPFAIL est normal**.

Le gestionnaire MAPFAIL doit donc initialiser l'écran à partir de `CA-ART-CODE-SELEC` :

```cobol
2000-RETOUR-TRANSACTION.
    MOVE DFHCOMMAREA TO GSTK-COMMAREA
    EXEC CICS RECEIVE MAP(C-MAP) MAPSET(C-MAPSET)
        INTO(GSTKxxxI)
        RESP(W-RESP) RESP2(W-RESP2)
    END-EXEC.
    IF W-RESP = DFHRESP(MAPFAIL)
*       XCTL entrant ou touche CLEAR : charger l'article passé en COMMAREA
        IF CA-ART-CODE-SELEC NOT = SPACES
            MOVE CA-ART-CODE-SELEC TO W-ART-CODE
            PERFORM 3000-RECHERCHER-ARTICLE   ← ou 3000-CHARGER-ARTICLE
        END-IF
        PERFORM 5000-AFFICHER-ECRAN
        GO TO 2000-FIN
    END-IF.
```

**Erreur à éviter :** un MAPFAIL qui ne fait que `PERFORM 5000-AFFICHER-ECRAN` sans
charger l'article affiche un écran vide même quand le programme appelant a sélectionné
un article via `CA-ART-CODE-SELEC`.

---

## Pattern DFHAID — Touches PF

```cobol
* DFHAID définit les constantes pour chaque touche
EVALUATE EIBAID
    WHEN DFHENTER  ...          ← touche ENTER
    WHEN DFHPF1    ...          ← PF1
    WHEN DFHPF3    ...          ← PF3 (menu)
    WHEN DFHPF5    ...          ← PF5 (filtrer)
    WHEN DFHPF6    ...          ← PF6 (valider)
    WHEN DFHPF7    ...          ← PF7 (page -)
    WHEN DFHPF8    ...          ← PF8 (page +)
    WHEN DFHPF12   ...          ← PF12 (RAZ)
    WHEN DFHCLEAR  ...          ← touche CLEAR
    WHEN OTHER
        MOVE 'TOUCHE NON RECONNUE' TO MSGRTRО IN GSTKxxxO
END-EVALUATE.
```

---

## Pattern XCTL — Changer de programme

```cobol
* Naviguer vers un autre programme en passant la COMMAREA
6000-RETOUR-MENU.
    MOVE 'GSTK000' TO W-PROG-CIBLE
    EXEC CICS XCTL
        PROGRAM(W-PROG-CIBLE)
        COMMAREA(GSTK-COMMAREA)
        LENGTH(263)
    END-EXEC.
```

---

## Pattern DB2 — Curseur avec pagination

```cobol
* 1. Compter le total (pour calculer le nombre de pages)
EXEC SQL
    SELECT COUNT(*)
    INTO :HV-TOT-CNT
    FROM GSTK.ARTICLES
    WHERE ART_CODE LIKE :HV-FIL-CODE
    AND ART_STATUT <> 'ARCHIVE'
END-EXEC.

COMPUTE W-PAGE-TOT = (HV-TOT-CNT + 9) / 10.

* 2. Ouvrir le curseur
EXEC SQL OPEN CURS-ART END-EXEC.

* 3. Sauter les pages précédentes
COMPUTE W-SKIP = (W-PAGE-CUR - 1) * 10.
PERFORM VARYING W-CNT FROM 1 BY 1
    UNTIL W-CNT > W-SKIP OR FIN-FETCH
    EXEC SQL FETCH CURS-ART INTO :HV-xxx END-EXEC
    IF SQLCODE NOT = 0
        MOVE 'N' TO W-FETCH-OK
    END-IF
END-PERFORM.

* 4. Lire 10 lignes pour l'écran
PERFORM VARYING W-I FROM 1 BY 1
    UNTIL W-I > 10 OR FIN-FETCH
    EXEC SQL FETCH CURS-ART INTO :HV-xxx END-EXEC
    IF SQLCODE = 0
        MOVE HV-xxx TO WL-xxx(W-I)
        MOVE '1' TO WL-ACTIF(W-I)
    ELSE
        MOVE 'N' TO W-FETCH-OK
    END-IF
END-PERFORM.

* 5. Toujours fermer le curseur
EXEC SQL CLOSE CURS-ART END-EXEC.
```

---

## Pattern DB2 — INSERT + SYNCPOINT

```cobol
* Récupérer un ID séquence
EXEC SQL
    SELECT NEXT VALUE FOR GSTK.SEQ_MVT
    INTO :HV-MVT-ID
    FROM SYSIBM.SYSDUMMY1
END-EXEC.

* INSERT
EXEC SQL
    INSERT INTO GSTK.MOUVEMENTS_STOCK
        (MVT_ID, MVT_TYPE, MVT_SENS, MVT_ART_CODE, ...)
    VALUES
        (:HV-MVT-ID, :HV-MVT-TYPE, :HV-MVT-SENS, :HV-ART-CODE, ...)
END-EXEC.

IF SQLCODE = 0
    EXEC SQL
        UPDATE GSTK.ARTICLES
        SET ART_QTE_STOCK = ART_QTE_STOCK + :HV-QTE-ENT
        WHERE ART_CODE = :HV-ART-CODE
    END-EXEC

    IF SQLCODE = 0
        EXEC CICS SYNCPOINT END-EXEC         ← commit si tout est OK
        MOVE 'ENTREE ENREGISTREE' TO MSGRTRО IN GSTKxxxO
    ELSE
        EXEC CICS SYNCPOINT ROLLBACK END-EXEC ← annulation si UPDATE échoue
        MOVE 'ERREUR MAJ STOCK' TO MSGRTRО IN GSTKxxxO
    END-IF
ELSE
    EXEC CICS SYNCPOINT ROLLBACK END-EXEC
    MOVE 'ERREUR INSERT MOUVEMENT' TO MSGRTRО IN GSTKxxxO
END-IF.
```

---

## Pattern filtres SQL avec LIKE

```cobol
* Construire le filtre avec wildcard '%'
IF W-FIL-CODE = SPACES OR W-FIL-CODE = '%'
    MOVE '%' TO HV-FIL-CODE
ELSE
    STRING W-FIL-CODE(1:10) DELIMITED SPACE
           '%'              DELIMITED SIZE
           INTO HV-FIL-CODE
END-IF.

* Dans le SQL :
* WHERE ART_CODE LIKE :HV-FIL-CODE
* 'ART-001' → 'ART-001%' → filtre sur le code
* '%'       → '%'        → tout
```

---

## Pattern date écran → format DB2

```cobol
* L'opérateur saisit DD/MM/YYYY, DB2 attend YYYY-MM-DD
IF W-FIL-DATE-DEB = SPACES
    MOVE ' ' TO HV-DATE-DEB       ← espace = pas de filtre
ELSE
    MOVE W-FIL-DATE-DEB(7:4) TO HV-DATE-DEB(1:4)   ← YYYY
    MOVE '-'                  TO HV-DATE-DEB(5:1)
    MOVE W-FIL-DATE-DEB(4:2) TO HV-DATE-DEB(6:2)   ← MM
    MOVE '-'                  TO HV-DATE-DEB(8:1)
    MOVE W-FIL-DATE-DEB(1:2) TO HV-DATE-DEB(9:2)   ← DD
END-IF.
```

---

## Pattern date/heure système

```cobol
01 WS-ABSTIME    PIC S9(15) COMP-3.
01 WS-DATE       PIC X(10).
01 WS-TIME       PIC X(8).
01 W-DATE-ECRAN  PIC X(19).

* Récupérer date et heure système CICS
EXEC CICS ASKTIME ABSTIME(WS-ABSTIME) END-EXEC
EXEC CICS FORMATTIME
    ABSTIME(WS-ABSTIME)
    DATESEP('/')
    DDMMYYYY(WS-DATE)
    TIMESEP(':')
    HHMMSS(WS-TIME)
END-EXEC
STRING WS-DATE DELIMITED SIZE ' ' DELIMITED SIZE
       WS-TIME DELIMITED SIZE
       INTO W-DATE-ECRAN.
```

---

## Pattern barre ASCII (GSTK001)

```cobol
* Calculer la longueur de barre proportionnelle (max 20 chars)
* WL-MAX = quantité maximale dans la page courante
COMPUTE W-FILL-CNT ROUNDED =
    (WL-QTE(W-I) * 20) / WL-MAX(W-I).

MOVE SPACES TO WL-BARRE(W-I).
PERFORM VARYING W-J FROM 1 BY 1 UNTIL W-J > W-FILL-CNT
    MOVE '#' TO WL-BARRE(W-I)(W-J:1)
END-PERFORM.
```

---

## Nommage des champs BMS

### Règle de génération des noms COBOL depuis le BMS

Champ BMS défini comme `ARTCOD DFHMDF POS=... LENGTH=10` :
- Input  : `ARTCODI IN GSTKxxxI` (+ `ARTCODL` pour la longueur, `ARTCODA` pour l'attribut)
- Output : `ARTCODO IN GSTKxxxO`

**Piège courant :** si le champ s'appelle `MNTHTE`, l'output est `MNTHTEO` (7 chars = 6 + 'O'), **pas** `MNTHEO` (6 chars).

### Attributs DFHMDF courants

| Attribut         | Signification                         |
|------------------|---------------------------------------|
| `ASKIP,NORM`     | Non saisissable, intensité normale    |
| `ASKIP,BRT`      | Non saisissable, mis en évidence      |
| `UNPROT,NORM`    | Saisissable, intensité normale        |
| `UNPROT,BRT`     | Saisissable, mis en évidence          |
| `UNPROT,NUM`     | Saisissable, uniquement numérique     |
| `PROT,DRK`       | Protégé, non affiché (mot de passe)   |

### MDT (Modified Data Tag)

Un champ retourné avec `L > 0` signifie que l'opérateur l'a modifié.  
Toujours vérifier `IF FIELDxxxL IN GSTKxxxI > ZERO` avant de traiter la valeur.

Les champs UNPROT non modifiés par l'opérateur retournent `L = 0` même s'ils contiennent une valeur affichée. C'est pourquoi les filtres sont sauvegardés en COMMAREA FILLER et restaurés en début de tâche.

---

## Récupérer l'ID terminal

```cobol
*   Correct : EIBTRMID contient l'ID terminal courant (4 chars, fourni par CICS)
    MOVE EIBTRMID TO TERNAMO IN GSTKxxxO.

*   Incorrect — TERMINAL n'est pas une option valide de EXEC CICS ASSIGN :
*   EXEC CICS ASSIGN TERMINAL(TERNAMO IN GSTKxxxO) END-EXEC.  ← INVREQ/LENGERR
*   L'option correcte serait TERMID(ws-var) (variable de travail 4 chars), pas un champ BMS.
```

---

## Codes SQLCODE courants

| SQLCODE | Signification                              |
|---------|--------------------------------------------|
| 0       | Succès                                     |
| +100    | Aucune ligne trouvée (fin de curseur)      |
| -180    | Format de date/heure invalide              |
| -204    | Objet non trouvé (table, vue, séquence)    |
| -302    | Valeur trop grande pour la colonne         |
| -407    | Violation de NOT NULL                      |
| -408    | Type incompatible                          |
| -530    | Violation de clé étrangère                 |
| -803    | Violation d'unicité (clé dupliquée)        |
| -904    | Ressource non disponible                   |
| -922    | Autorisation refusée                       |

---

## Codes ABEND CICS courants

| Code    | Signification                                  | Solution                              |
|---------|------------------------------------------------|---------------------------------------|
| PGMIDERR| Programme non trouvé dans CICS                 | CEDA DEF PROGRAM + CEDA INSTALL       |
| MAPFAIL | Mapset non trouvé                              | Assembler le BMS + CEDA DEF MAPSET    |
| LENGERR | Longueur COMMAREA incorrecte                   | Vérifier LENGTH=263                   |
| NOTOPEN | Fichier ou connexion DB2 fermé(e)              | CEMT SET DATASET/DB2CONN OPEN         |
| INVREQ  | Requête invalide (mauvais contexte API CICS)   | Vérifier ordre des EXEC CICS          |
| IOERR   | Erreur d'entrée/sortie                         | Vérifier les DD dans le JCL CICS      |
| ASRA    | Program check (ABEND S0C7 = données numériques)| Débogage CEDF, vérifier host variables|
