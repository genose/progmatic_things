      *================================================================*
      * PROGRAMME  : GSTK007                                         *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                       *
      * VERSION    : 1.0                                              *
      *----------------------------------------------------------------*
      * DESCRIPTION : HISTORIQUE DES MOUVEMENTS DE STOCK             *
      *   Affiche l'historique des entrees/sorties de stock.          *
      *   Filtrage par article, date, type de mouvement.              *
      *----------------------------------------------------------------*
      * TRANSID    : G007                                             *
      * MAPSET     : GSTK007M      MAP : GSTK007                     *
      * COMMAREA   : GSTK-COMMAREA (voir GSTKCOMM)                   *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. GSTK007.

       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       SPECIAL-NAMES.
           DECIMAL-POINT IS COMMA.

       DATA DIVISION.
       WORKING-STORAGE SECTION.

      *----------------------------------------------------------------*
      * COPY MAPSET ET COMMAREA
      *----------------------------------------------------------------*
           COPY GSTK007M.
           COPY GSTKCOMM.
           COPY DFHAID.

      *----------------------------------------------------------------*
      * SQLCA
      *----------------------------------------------------------------*
           EXEC SQL INCLUDE SQLCA END-EXEC.

      *----------------------------------------------------------------*
      * HOST VARIABLES SQL
      *----------------------------------------------------------------*
       01  HV-VARIABLES.
           05  HV-ART-CODE        PIC X(10).
           05  HV-DATE-DEB        PIC X(10).
           05  HV-DATE-FIN        PIC X(10).
           05  HV-MVT-TYPE        PIC X(20).
           05  HV-MVT-DATE        PIC X(10).
           05  HV-MVT-HEURE       PIC X(8).
           05  HV-MVT-TYPX        PIC X(20).
           05  HV-MVT-SENS        PIC X(1).
           05  HV-MVT-ACODE       PIC X(10).
           05  HV-MVT-DESIG       PIC X(40).
           05  HV-MVT-QTE         PIC S9(9)    COMP-3.
           05  HV-MVT-MHT         PIC S9(13)V99 COMP-3.
           05  HV-MVT-OPER        PIC X(8).
           05  HV-TOT-CNT         PIC S9(7)    COMP-3.
           05  HV-TOT-MNT         PIC S9(13)V99 COMP-3.

      *----------------------------------------------------------------*
      * TABLE DE TRAVAIL 10 LIGNES
      *----------------------------------------------------------------*
       01  W-TABLE.
           05  WL-LIG             OCCURS 10 TIMES.
               10  WL-DATE        PIC X(10).
               10  WL-HEURE       PIC X(8).
               10  WL-TYPE        PIC X(20).
               10  WL-SENS        PIC X(1).
               10  WL-CODE        PIC X(10).
               10  WL-DESIG       PIC X(40).
               10  WL-QTE         PIC S9(9)    COMP-3.
               10  WL-MHT         PIC S9(13)V99 COMP-3.
               10  WL-OPER        PIC X(8).
               10  WL-ACTIF       PIC X         VALUE '0'.
                   88  LIG-ACTIVE                VALUE '1'.

      *----------------------------------------------------------------*
      * FILTRES COURANTS
      *----------------------------------------------------------------*
       01  W-FILTRES.
           05  W-FIL-CODE         PIC X(12)     VALUE '%'.
           05  W-FIL-DATE-DEB     PIC X(10)     VALUE SPACES.
           05  W-FIL-DATE-FIN     PIC X(10)     VALUE SPACES.
           05  W-FIL-TYPE         PIC X(22)     VALUE '%'.

      *----------------------------------------------------------------*
      * PAGINATION
      *----------------------------------------------------------------*
       01  W-PAGINATION.
           05  W-PAGE-CUR         PIC S9(4)     COMP VALUE 1.
           05  W-PAGE-TOT         PIC S9(4)     COMP VALUE 1.
           05  W-NB-MVT           PIC S9(7)     COMP VALUE 0.
           05  W-SKIP             PIC S9(7)     COMP VALUE 0.

      *----------------------------------------------------------------*
      * DRAPEAUX ET VARIABLES TRAVAIL
      *----------------------------------------------------------------*
       01  W-FLAGS.
           05  W-FETCH-OK         PIC X         VALUE 'Y'.
               88  FETCH-OK                      VALUE 'Y'.
               88  FIN-FETCH                     VALUE 'N'.

       01  W-WORK.
           05  W-I                PIC S9(4)     COMP VALUE 0.
           05  W-CNT              PIC S9(7)     COMP VALUE 0.
           05  W-DATE-ECRAN       PIC X(19).
           05  W-DATE-DB2         PIC X(10).
           05  WS-ABSTIME         PIC S9(15)    COMP-3.
           05  WS-DATE            PIC X(10).
           05  WS-TIME            PIC X(8).

      *    Zones d'édition numérique
       01  W-EDIT.
           05  W-QTE-EDIT         PIC -ZZZZ9.
           05  W-MHT-EDIT         PIC -ZZZ.ZZZ.ZZ9,99.
           05  W-CNT-EDIT         PIC ZZZZZZZ.
           05  W-MNT-EDIT         PIC -ZZZ.ZZZ.ZZ9,99.
           05  W-PAG-EDIT         PIC ZZZ9.

       01  W-PROG-CIBLE           PIC X(8)      VALUE SPACES.

      *----------------------------------------------------------------*
      * CONSTANTES
      *----------------------------------------------------------------*
       01  C-MAPSET               PIC X(8)      VALUE 'GSTK007M'.
       01  C-MAP                  PIC X(8)      VALUE 'GSTK007'.
       01  C-TRANS                PIC X(4)      VALUE 'G007'.

       PROCEDURE DIVISION.

      *================================================================*
      * DECLARATION CURSEUR (EN TETE PROCEDURE DIVISION - OBLIGATOIRE)*
      *================================================================*
           EXEC SQL
               DECLARE CURS-MVT CURSOR FOR
               SELECT MVT_DATE,
                      MVT_HEURE,
                      MVT_TYPE,
                      MVT_SENS,
                      MVT_ART_CODE,
                      MVT_DESIGNATION,
                      MVT_QUANTITE,
                      MVT_MONTANT_HT,
                      MVT_OPERATEUR
               FROM   GSTK.MOUVEMENTS_STOCK
               WHERE  MVT_ART_CODE LIKE :HV-ART-CODE
               AND    MVT_TYPE     LIKE :HV-MVT-TYPE
               AND    (:HV-DATE-DEB = ' ' OR
                       MVT_DATE >= :HV-DATE-DEB)
               AND    (:HV-DATE-FIN = ' ' OR
                       MVT_DATE <= :HV-DATE-FIN)
               ORDER BY MVT_DATE DESC, MVT_HEURE DESC
           END-EXEC.

      *================================================================*
      * 0000-MAIN                                                      *
      *================================================================*
       0000-MAIN.
           EVALUATE TRUE
               WHEN EIBCALEN = ZERO
                   PERFORM 1000-PREMIERE-ENTREE
               WHEN OTHER
                   PERFORM 2000-RETOUR-TRANSACTION
           END-EVALUATE
           STOP RUN.

      *================================================================*
      * 1000-PREMIERE-ENTREE                                           *
      *================================================================*
       1000-PREMIERE-ENTREE.
           INITIALIZE GSTK-COMMAREA.
           EXEC CICS SEND MAP(C-MAP)
               MAPSET(C-MAPSET)
               MAPONLY ERASE
           END-EXEC
           EXEC CICS RETURN
               TRANSID(C-TRANS)
               COMMAREA(GSTK-COMMAREA)
               LENGTH(263)
           END-EXEC.

      *================================================================*
      * 2000-RETOUR-TRANSACTION                                        *
      *================================================================*
       2000-RETOUR-TRANSACTION.
           MOVE DFHCOMMAREA TO GSTK-COMMAREA

      *    Restaurer filtres depuis COMMAREA FILLER
           MOVE GSTK-COMMAREA(209:12) TO W-FIL-CODE
           MOVE GSTK-COMMAREA(221:10) TO W-FIL-DATE-DEB
           MOVE GSTK-COMMAREA(231:10) TO W-FIL-DATE-FIN
           MOVE GSTK-COMMAREA(241:22) TO W-FIL-TYPE

           EXEC CICS RECEIVE MAP(C-MAP)
               MAPSET(C-MAPSET)
               INTO(GSTK007I)
           END-EXEC

      *    Mettre à jour filtres si l'opérateur a saisi quelque chose
           IF FILCOL IN GSTK007I > ZERO
               MOVE SPACES TO W-FIL-CODE
               MOVE FILCOI IN GSTK007I TO W-FIL-CODE(1:10)
           END-IF
           IF FILDEL IN GSTK007I > ZERO
               MOVE FILDEI IN GSTK007I TO W-FIL-DATE-DEB
           END-IF
           IF FILDFL IN GSTK007I > ZERO
               MOVE FILDFI IN GSTK007I TO W-FIL-DATE-FIN
           END-IF
           IF FILTPL IN GSTK007I > ZERO
               MOVE SPACES TO W-FIL-TYPE
               MOVE FILTPI IN GSTK007I TO W-FIL-TYPE(1:10)
           END-IF

           EVALUATE EIBAID
               WHEN DFHPF3
                   PERFORM 6000-RETOUR-MENU

               WHEN DFHPF5
      *            Filtrer : repartir page 1
                   MOVE 1 TO W-PAGE-CUR
                   PERFORM 3000-COMPTER
                   PERFORM 4000-REQUETE-MOUVEMENTS
                   MOVE SPACES TO MSGRTRO IN GSTK007O
                   PERFORM 5000-AFFICHER-ECRAN

               WHEN DFHPF7
      *            Page précédente
                   IF W-PAGE-CUR > 1
                       SUBTRACT 1 FROM W-PAGE-CUR
                   ELSE
                       MOVE 'DEBUT DE LISTE' TO MSGRTRO IN GSTK007O
                   END-IF
                   PERFORM 3000-COMPTER
                   PERFORM 4000-REQUETE-MOUVEMENTS
                   PERFORM 5000-AFFICHER-ECRAN

               WHEN DFHPF8
      *            Page suivante
                   IF W-PAGE-CUR < W-PAGE-TOT
                       ADD 1 TO W-PAGE-CUR
                   ELSE
                       MOVE 'FIN DE LISTE' TO MSGRTRO IN GSTK007O
                   END-IF
                   PERFORM 3000-COMPTER
                   PERFORM 4000-REQUETE-MOUVEMENTS
                   PERFORM 5000-AFFICHER-ECRAN

               WHEN DFHPF12
      *            RAZ filtres
                   MOVE '%'    TO W-FIL-CODE
                   MOVE SPACES TO W-FIL-DATE-DEB
                   MOVE SPACES TO W-FIL-DATE-FIN
                   MOVE '%'    TO W-FIL-TYPE
                   MOVE 1      TO W-PAGE-CUR
                   PERFORM 3000-COMPTER
                   PERFORM 4000-REQUETE-MOUVEMENTS
                   MOVE 'FILTRES REINITIALISES' TO MSGRTRO IN GSTK007O
                   PERFORM 5000-AFFICHER-ECRAN

               WHEN OTHER
      *            ENTER ou autre : afficher résultats courants
                   PERFORM 3000-COMPTER
                   PERFORM 4000-REQUETE-MOUVEMENTS
                   MOVE SPACES TO MSGRTRO IN GSTK007O
                   PERFORM 5000-AFFICHER-ECRAN
           END-EVALUATE.

      *================================================================*
      * 3000-COMPTER : TOTAL MOUVEMENTS + MONTANT TOTAL PERIODE       *
      *================================================================*
       3000-COMPTER.
           PERFORM 3100-PREPARER-FILTRES-SQL

           EXEC SQL
               SELECT COUNT(*),
                      COALESCE(SUM(MVT_MONTANT_HT), 0)
               INTO   :HV-TOT-CNT,
                      :HV-TOT-MNT
               FROM   GSTK.MOUVEMENTS_STOCK
               WHERE  MVT_ART_CODE LIKE :HV-ART-CODE
               AND    MVT_TYPE     LIKE :HV-MVT-TYPE
               AND    (:HV-DATE-DEB = ' ' OR
                       MVT_DATE >= :HV-DATE-DEB)
               AND    (:HV-DATE-FIN = ' ' OR
                       MVT_DATE <= :HV-DATE-FIN)
           END-EXEC

           IF SQLCODE = 0
               MOVE HV-TOT-CNT TO W-NB-MVT
               COMPUTE W-PAGE-TOT = (W-NB-MVT + 9) / 10
               IF W-PAGE-TOT < 1
                   MOVE 1 TO W-PAGE-TOT
               END-IF
               IF W-PAGE-CUR > W-PAGE-TOT
                   MOVE W-PAGE-TOT TO W-PAGE-CUR
               END-IF
           ELSE
               MOVE 0 TO W-NB-MVT
               MOVE 0 TO HV-TOT-MNT
               MOVE 1 TO W-PAGE-TOT
               MOVE 1 TO W-PAGE-CUR
           END-IF.

      *================================================================*
      * 3100-PREPARER-FILTRES-SQL                                      *
      *================================================================*
       3100-PREPARER-FILTRES-SQL.
      *    Code article : ajouter wildcard si non vide
           IF W-FIL-CODE = SPACES OR W-FIL-CODE = '%'
               MOVE '%' TO HV-ART-CODE
           ELSE
               STRING W-FIL-CODE(1:10) DELIMITED SPACE
                      '%'              DELIMITED SIZE
                      INTO HV-ART-CODE
           END-IF

      *    Type mouvement
           IF W-FIL-TYPE = SPACES OR W-FIL-TYPE = '%'
               MOVE '%' TO HV-MVT-TYPE
           ELSE
               STRING W-FIL-TYPE(1:10) DELIMITED SPACE
                      '%'              DELIMITED SIZE
                      INTO HV-MVT-TYPE
           END-IF

      *    Date début : DD/MM/YYYY -> YYYY-MM-DD (format DB2)
           IF W-FIL-DATE-DEB = SPACES
               MOVE ' ' TO HV-DATE-DEB
           ELSE
               MOVE W-FIL-DATE-DEB(7:4) TO HV-DATE-DEB(1:4)
               MOVE '-'                  TO HV-DATE-DEB(5:1)
               MOVE W-FIL-DATE-DEB(4:2) TO HV-DATE-DEB(6:2)
               MOVE '-'                  TO HV-DATE-DEB(8:1)
               MOVE W-FIL-DATE-DEB(1:2) TO HV-DATE-DEB(9:2)
           END-IF

      *    Date fin : DD/MM/YYYY -> YYYY-MM-DD
           IF W-FIL-DATE-FIN = SPACES
               MOVE ' ' TO HV-DATE-FIN
           ELSE
               MOVE W-FIL-DATE-FIN(7:4) TO HV-DATE-FIN(1:4)
               MOVE '-'                  TO HV-DATE-FIN(5:1)
               MOVE W-FIL-DATE-FIN(4:2) TO HV-DATE-FIN(6:2)
               MOVE '-'                  TO HV-DATE-FIN(8:1)
               MOVE W-FIL-DATE-FIN(1:2) TO HV-DATE-FIN(9:2)
           END-IF.

      *================================================================*
      * 4000-REQUETE-MOUVEMENTS                                        *
      *================================================================*
       4000-REQUETE-MOUVEMENTS.
      *    Initialiser la table
           PERFORM VARYING W-I FROM 1 BY 1 UNTIL W-I > 10
               MOVE SPACES TO WL-DATE(W-I)
               MOVE SPACES TO WL-HEURE(W-I)
               MOVE SPACES TO WL-TYPE(W-I)
               MOVE SPACES TO WL-SENS(W-I)
               MOVE SPACES TO WL-CODE(W-I)
               MOVE SPACES TO WL-DESIG(W-I)
               MOVE ZERO   TO WL-QTE(W-I)
               MOVE ZERO   TO WL-MHT(W-I)
               MOVE SPACES TO WL-OPER(W-I)
               MOVE '0'    TO WL-ACTIF(W-I)
           END-PERFORM

           EXEC SQL OPEN CURS-MVT END-EXEC
           IF SQLCODE NOT = 0
               MOVE 'ERREUR ACCES MOUVEMENTS (OPEN)' TO
                   MSGRTRO IN GSTK007O
               GO TO 4000-EXIT
           END-IF

      *    Sauter les lignes des pages précédentes
           COMPUTE W-SKIP = (W-PAGE-CUR - 1) * 10
           MOVE 'Y' TO W-FETCH-OK

           IF W-SKIP > 0
               PERFORM VARYING W-CNT FROM 1 BY 1
                   UNTIL W-CNT > W-SKIP OR FIN-FETCH
                   EXEC SQL FETCH CURS-MVT
                       INTO :HV-MVT-DATE,  :HV-MVT-HEURE,
                            :HV-MVT-TYPX,  :HV-MVT-SENS,
                            :HV-MVT-ACODE, :HV-MVT-DESIG,
                            :HV-MVT-QTE,   :HV-MVT-MHT,
                            :HV-MVT-OPER
                   END-EXEC
                   IF SQLCODE NOT = 0
                       MOVE 'N' TO W-FETCH-OK
                   END-IF
               END-PERFORM
           END-IF

      *    Charger les 10 lignes affichables
           PERFORM VARYING W-I FROM 1 BY 1
               UNTIL W-I > 10 OR FIN-FETCH
               EXEC SQL FETCH CURS-MVT
                   INTO :HV-MVT-DATE,  :HV-MVT-HEURE,
                        :HV-MVT-TYPX,  :HV-MVT-SENS,
                        :HV-MVT-ACODE, :HV-MVT-DESIG,
                        :HV-MVT-QTE,   :HV-MVT-MHT,
                        :HV-MVT-OPER
               END-EXEC
               IF SQLCODE = 0
                   MOVE HV-MVT-DATE  TO WL-DATE(W-I)
                   MOVE HV-MVT-HEURE TO WL-HEURE(W-I)
                   MOVE HV-MVT-TYPX  TO WL-TYPE(W-I)
                   MOVE HV-MVT-SENS  TO WL-SENS(W-I)
                   MOVE HV-MVT-ACODE TO WL-CODE(W-I)
                   MOVE HV-MVT-DESIG TO WL-DESIG(W-I)
                   MOVE HV-MVT-QTE   TO WL-QTE(W-I)
                   MOVE HV-MVT-MHT   TO WL-MHT(W-I)
                   MOVE HV-MVT-OPER  TO WL-OPER(W-I)
                   MOVE '1'          TO WL-ACTIF(W-I)
               ELSE
                   MOVE 'N' TO W-FETCH-OK
               END-IF
           END-PERFORM

           EXEC SQL CLOSE CURS-MVT END-EXEC.
       4000-EXIT.
           EXIT.

      *================================================================*
      * 5000-AFFICHER-ECRAN                                            *
      *================================================================*
       5000-AFFICHER-ECRAN.
      *    Date/heure système
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
                  INTO W-DATE-ECRAN
           MOVE W-DATE-ECRAN TO DATHRO IN GSTK007O

      *    Filtres
           MOVE W-FIL-CODE(1:10)  TO FILCOO  IN GSTK007O
           MOVE W-FIL-DATE-DEB    TO FILDEO  IN GSTK007O
           MOVE W-FIL-DATE-FIN    TO FILDFO  IN GSTK007O
           MOVE W-FIL-TYPE(1:10)  TO FILTPO  IN GSTK007O

      *    Opérateur et terminal
           MOVE CA-OPERATEUR      TO OPENAMO IN GSTK007O
           MOVE EIBTRMID TO TERNAMO IN GSTK007O

      *    Pagination
           MOVE W-PAGE-CUR        TO W-PAG-EDIT
           MOVE W-PAG-EDIT        TO PAGCURO IN GSTK007O
           MOVE W-PAGE-TOT        TO W-PAG-EDIT
           MOVE W-PAG-EDIT        TO PAGTOTO IN GSTK007O

      *    Totaux période
           MOVE W-NB-MVT          TO W-CNT-EDIT
           MOVE W-CNT-EDIT        TO TOTCNTO IN GSTK007O
           MOVE HV-TOT-MNT        TO W-MNT-EDIT
           MOVE W-MNT-EDIT        TO TOTMNTO IN GSTK007O

      *    Ligne 1
           IF LIG-ACTIVE(1)
               MOVE WL-DATE(1)(1:10)    TO DT01O IN GSTK007O
               MOVE WL-HEURE(1)(1:8)    TO HE01O IN GSTK007O
               MOVE WL-TYPE(1)(1:10)    TO TP01O IN GSTK007O
               MOVE WL-SENS(1)          TO SN01O IN GSTK007O
               MOVE WL-CODE(1)          TO AC01O IN GSTK007O
               MOVE WL-DESIG(1)(1:20)   TO DS01O IN GSTK007O
               MOVE WL-QTE(1)           TO W-QTE-EDIT
               MOVE W-QTE-EDIT          TO QT01O IN GSTK007O
               MOVE WL-MHT(1)           TO W-MHT-EDIT
               MOVE W-MHT-EDIT          TO MH01O IN GSTK007O
               MOVE WL-OPER(1)          TO OP01O IN GSTK007O
           ELSE
               MOVE SPACES TO DT01O IN GSTK007O
               MOVE SPACES TO HE01O IN GSTK007O
               MOVE SPACES TO TP01O IN GSTK007O
               MOVE SPACES TO SN01O IN GSTK007O
               MOVE SPACES TO AC01O IN GSTK007O
               MOVE SPACES TO DS01O IN GSTK007O
               MOVE SPACES TO QT01O IN GSTK007O
               MOVE SPACES TO MH01O IN GSTK007O
               MOVE SPACES TO OP01O IN GSTK007O
           END-IF

      *    Ligne 2
           IF LIG-ACTIVE(2)
               MOVE WL-DATE(2)(1:10)    TO DT02O IN GSTK007O
               MOVE WL-HEURE(2)(1:8)    TO HE02O IN GSTK007O
               MOVE WL-TYPE(2)(1:10)    TO TP02O IN GSTK007O
               MOVE WL-SENS(2)          TO SN02O IN GSTK007O
               MOVE WL-CODE(2)          TO AC02O IN GSTK007O
               MOVE WL-DESIG(2)(1:20)   TO DS02O IN GSTK007O
               MOVE WL-QTE(2)           TO W-QTE-EDIT
               MOVE W-QTE-EDIT          TO QT02O IN GSTK007O
               MOVE WL-MHT(2)           TO W-MHT-EDIT
               MOVE W-MHT-EDIT          TO MH02O IN GSTK007O
               MOVE WL-OPER(2)          TO OP02O IN GSTK007O
           ELSE
               MOVE SPACES TO DT02O IN GSTK007O
               MOVE SPACES TO HE02O IN GSTK007O
               MOVE SPACES TO TP02O IN GSTK007O
               MOVE SPACES TO SN02O IN GSTK007O
               MOVE SPACES TO AC02O IN GSTK007O
               MOVE SPACES TO DS02O IN GSTK007O
               MOVE SPACES TO QT02O IN GSTK007O
               MOVE SPACES TO MH02O IN GSTK007O
               MOVE SPACES TO OP02O IN GSTK007O
           END-IF

      *    Ligne 3
           IF LIG-ACTIVE(3)
               MOVE WL-DATE(3)(1:10)    TO DT03O IN GSTK007O
               MOVE WL-HEURE(3)(1:8)    TO HE03O IN GSTK007O
               MOVE WL-TYPE(3)(1:10)    TO TP03O IN GSTK007O
               MOVE WL-SENS(3)          TO SN03O IN GSTK007O
               MOVE WL-CODE(3)          TO AC03O IN GSTK007O
               MOVE WL-DESIG(3)(1:20)   TO DS03O IN GSTK007O
               MOVE WL-QTE(3)           TO W-QTE-EDIT
               MOVE W-QTE-EDIT          TO QT03O IN GSTK007O
               MOVE WL-MHT(3)           TO W-MHT-EDIT
               MOVE W-MHT-EDIT          TO MH03O IN GSTK007O
               MOVE WL-OPER(3)          TO OP03O IN GSTK007O
           ELSE
               MOVE SPACES TO DT03O IN GSTK007O
               MOVE SPACES TO HE03O IN GSTK007O
               MOVE SPACES TO TP03O IN GSTK007O
               MOVE SPACES TO SN03O IN GSTK007O
               MOVE SPACES TO AC03O IN GSTK007O
               MOVE SPACES TO DS03O IN GSTK007O
               MOVE SPACES TO QT03O IN GSTK007O
               MOVE SPACES TO MH03O IN GSTK007O
               MOVE SPACES TO OP03O IN GSTK007O
           END-IF

      *    Ligne 4
           IF LIG-ACTIVE(4)
               MOVE WL-DATE(4)(1:10)    TO DT04O IN GSTK007O
               MOVE WL-HEURE(4)(1:8)    TO HE04O IN GSTK007O
               MOVE WL-TYPE(4)(1:10)    TO TP04O IN GSTK007O
               MOVE WL-SENS(4)          TO SN04O IN GSTK007O
               MOVE WL-CODE(4)          TO AC04O IN GSTK007O
               MOVE WL-DESIG(4)(1:20)   TO DS04O IN GSTK007O
               MOVE WL-QTE(4)           TO W-QTE-EDIT
               MOVE W-QTE-EDIT          TO QT04O IN GSTK007O
               MOVE WL-MHT(4)           TO W-MHT-EDIT
               MOVE W-MHT-EDIT          TO MH04O IN GSTK007O
               MOVE WL-OPER(4)          TO OP04O IN GSTK007O
           ELSE
               MOVE SPACES TO DT04O IN GSTK007O
               MOVE SPACES TO HE04O IN GSTK007O
               MOVE SPACES TO TP04O IN GSTK007O
               MOVE SPACES TO SN04O IN GSTK007O
               MOVE SPACES TO AC04O IN GSTK007O
               MOVE SPACES TO DS04O IN GSTK007O
               MOVE SPACES TO QT04O IN GSTK007O
               MOVE SPACES TO MH04O IN GSTK007O
               MOVE SPACES TO OP04O IN GSTK007O
           END-IF

      *    Ligne 5
           IF LIG-ACTIVE(5)
               MOVE WL-DATE(5)(1:10)    TO DT05O IN GSTK007O
               MOVE WL-HEURE(5)(1:8)    TO HE05O IN GSTK007O
               MOVE WL-TYPE(5)(1:10)    TO TP05O IN GSTK007O
               MOVE WL-SENS(5)          TO SN05O IN GSTK007O
               MOVE WL-CODE(5)          TO AC05O IN GSTK007O
               MOVE WL-DESIG(5)(1:20)   TO DS05O IN GSTK007O
               MOVE WL-QTE(5)           TO W-QTE-EDIT
               MOVE W-QTE-EDIT          TO QT05O IN GSTK007O
               MOVE WL-MHT(5)           TO W-MHT-EDIT
               MOVE W-MHT-EDIT          TO MH05O IN GSTK007O
               MOVE WL-OPER(5)          TO OP05O IN GSTK007O
           ELSE
               MOVE SPACES TO DT05O IN GSTK007O
               MOVE SPACES TO HE05O IN GSTK007O
               MOVE SPACES TO TP05O IN GSTK007O
               MOVE SPACES TO SN05O IN GSTK007O
               MOVE SPACES TO AC05O IN GSTK007O
               MOVE SPACES TO DS05O IN GSTK007O
               MOVE SPACES TO QT05O IN GSTK007O
               MOVE SPACES TO MH05O IN GSTK007O
               MOVE SPACES TO OP05O IN GSTK007O
           END-IF

      *    Ligne 6
           IF LIG-ACTIVE(6)
               MOVE WL-DATE(6)(1:10)    TO DT06O IN GSTK007O
               MOVE WL-HEURE(6)(1:8)    TO HE06O IN GSTK007O
               MOVE WL-TYPE(6)(1:10)    TO TP06O IN GSTK007O
               MOVE WL-SENS(6)          TO SN06O IN GSTK007O
               MOVE WL-CODE(6)          TO AC06O IN GSTK007O
               MOVE WL-DESIG(6)(1:20)   TO DS06O IN GSTK007O
               MOVE WL-QTE(6)           TO W-QTE-EDIT
               MOVE W-QTE-EDIT          TO QT06O IN GSTK007O
               MOVE WL-MHT(6)           TO W-MHT-EDIT
               MOVE W-MHT-EDIT          TO MH06O IN GSTK007O
               MOVE WL-OPER(6)          TO OP06O IN GSTK007O
           ELSE
               MOVE SPACES TO DT06O IN GSTK007O
               MOVE SPACES TO HE06O IN GSTK007O
               MOVE SPACES TO TP06O IN GSTK007O
               MOVE SPACES TO SN06O IN GSTK007O
               MOVE SPACES TO AC06O IN GSTK007O
               MOVE SPACES TO DS06O IN GSTK007O
               MOVE SPACES TO QT06O IN GSTK007O
               MOVE SPACES TO MH06O IN GSTK007O
               MOVE SPACES TO OP06O IN GSTK007O
           END-IF

      *    Ligne 7
           IF LIG-ACTIVE(7)
               MOVE WL-DATE(7)(1:10)    TO DT07O IN GSTK007O
               MOVE WL-HEURE(7)(1:8)    TO HE07O IN GSTK007O
               MOVE WL-TYPE(7)(1:10)    TO TP07O IN GSTK007O
               MOVE WL-SENS(7)          TO SN07O IN GSTK007O
               MOVE WL-CODE(7)          TO AC07O IN GSTK007O
               MOVE WL-DESIG(7)(1:20)   TO DS07O IN GSTK007O
               MOVE WL-QTE(7)           TO W-QTE-EDIT
               MOVE W-QTE-EDIT          TO QT07O IN GSTK007O
               MOVE WL-MHT(7)           TO W-MHT-EDIT
               MOVE W-MHT-EDIT          TO MH07O IN GSTK007O
               MOVE WL-OPER(7)          TO OP07O IN GSTK007O
           ELSE
               MOVE SPACES TO DT07O IN GSTK007O
               MOVE SPACES TO HE07O IN GSTK007O
               MOVE SPACES TO TP07O IN GSTK007O
               MOVE SPACES TO SN07O IN GSTK007O
               MOVE SPACES TO AC07O IN GSTK007O
               MOVE SPACES TO DS07O IN GSTK007O
               MOVE SPACES TO QT07O IN GSTK007O
               MOVE SPACES TO MH07O IN GSTK007O
               MOVE SPACES TO OP07O IN GSTK007O
           END-IF

      *    Ligne 8
           IF LIG-ACTIVE(8)
               MOVE WL-DATE(8)(1:10)    TO DT08O IN GSTK007O
               MOVE WL-HEURE(8)(1:8)    TO HE08O IN GSTK007O
               MOVE WL-TYPE(8)(1:10)    TO TP08O IN GSTK007O
               MOVE WL-SENS(8)          TO SN08O IN GSTK007O
               MOVE WL-CODE(8)          TO AC08O IN GSTK007O
               MOVE WL-DESIG(8)(1:20)   TO DS08O IN GSTK007O
               MOVE WL-QTE(8)           TO W-QTE-EDIT
               MOVE W-QTE-EDIT          TO QT08O IN GSTK007O
               MOVE WL-MHT(8)           TO W-MHT-EDIT
               MOVE W-MHT-EDIT          TO MH08O IN GSTK007O
               MOVE WL-OPER(8)          TO OP08O IN GSTK007O
           ELSE
               MOVE SPACES TO DT08O IN GSTK007O
               MOVE SPACES TO HE08O IN GSTK007O
               MOVE SPACES TO TP08O IN GSTK007O
               MOVE SPACES TO SN08O IN GSTK007O
               MOVE SPACES TO AC08O IN GSTK007O
               MOVE SPACES TO DS08O IN GSTK007O
               MOVE SPACES TO QT08O IN GSTK007O
               MOVE SPACES TO MH08O IN GSTK007O
               MOVE SPACES TO OP08O IN GSTK007O
           END-IF

      *    Ligne 9
           IF LIG-ACTIVE(9)
               MOVE WL-DATE(9)(1:10)    TO DT09O IN GSTK007O
               MOVE WL-HEURE(9)(1:8)    TO HE09O IN GSTK007O
               MOVE WL-TYPE(9)(1:10)    TO TP09O IN GSTK007O
               MOVE WL-SENS(9)          TO SN09O IN GSTK007O
               MOVE WL-CODE(9)          TO AC09O IN GSTK007O
               MOVE WL-DESIG(9)(1:20)   TO DS09O IN GSTK007O
               MOVE WL-QTE(9)           TO W-QTE-EDIT
               MOVE W-QTE-EDIT          TO QT09O IN GSTK007O
               MOVE WL-MHT(9)           TO W-MHT-EDIT
               MOVE W-MHT-EDIT          TO MH09O IN GSTK007O
               MOVE WL-OPER(9)          TO OP09O IN GSTK007O
           ELSE
               MOVE SPACES TO DT09O IN GSTK007O
               MOVE SPACES TO HE09O IN GSTK007O
               MOVE SPACES TO TP09O IN GSTK007O
               MOVE SPACES TO SN09O IN GSTK007O
               MOVE SPACES TO AC09O IN GSTK007O
               MOVE SPACES TO DS09O IN GSTK007O
               MOVE SPACES TO QT09O IN GSTK007O
               MOVE SPACES TO MH09O IN GSTK007O
               MOVE SPACES TO OP09O IN GSTK007O
           END-IF

      *    Ligne 10
           IF LIG-ACTIVE(10)
               MOVE WL-DATE(10)(1:10)   TO DT10O IN GSTK007O
               MOVE WL-HEURE(10)(1:8)   TO HE10O IN GSTK007O
               MOVE WL-TYPE(10)(1:10)   TO TP10O IN GSTK007O
               MOVE WL-SENS(10)         TO SN10O IN GSTK007O
               MOVE WL-CODE(10)         TO AC10O IN GSTK007O
               MOVE WL-DESIG(10)(1:20)  TO DS10O IN GSTK007O
               MOVE WL-QTE(10)          TO W-QTE-EDIT
               MOVE W-QTE-EDIT          TO QT10O IN GSTK007O
               MOVE WL-MHT(10)          TO W-MHT-EDIT
               MOVE W-MHT-EDIT          TO MH10O IN GSTK007O
               MOVE WL-OPER(10)         TO OP10O IN GSTK007O
           ELSE
               MOVE SPACES TO DT10O IN GSTK007O
               MOVE SPACES TO HE10O IN GSTK007O
               MOVE SPACES TO TP10O IN GSTK007O
               MOVE SPACES TO SN10O IN GSTK007O
               MOVE SPACES TO AC10O IN GSTK007O
               MOVE SPACES TO DS10O IN GSTK007O
               MOVE SPACES TO QT10O IN GSTK007O
               MOVE SPACES TO MH10O IN GSTK007O
               MOVE SPACES TO OP10O IN GSTK007O
           END-IF

      *    Sauvegarder filtres dans COMMAREA FILLER avant retour
           MOVE W-FIL-CODE        TO GSTK-COMMAREA(209:12)
           MOVE W-FIL-DATE-DEB    TO GSTK-COMMAREA(221:10)
           MOVE W-FIL-DATE-FIN    TO GSTK-COMMAREA(231:10)
           MOVE W-FIL-TYPE        TO GSTK-COMMAREA(241:22)

           EXEC CICS SEND MAP(C-MAP)
               MAPSET(C-MAPSET)
               FROM(GSTK007O)
               ERASE CURSOR
           END-EXEC

           EXEC CICS RETURN
               TRANSID(C-TRANS)
               COMMAREA(GSTK-COMMAREA)
               LENGTH(263)
           END-EXEC.

      *================================================================*
      * 6000-RETOUR-MENU                                               *
      *================================================================*
       6000-RETOUR-MENU.
           MOVE 'GSTK000' TO W-PROG-CIBLE
           EXEC CICS XCTL
               PROGRAM(W-PROG-CIBLE)
               COMMAREA(GSTK-COMMAREA)
               LENGTH(263)
           END-EXEC.
