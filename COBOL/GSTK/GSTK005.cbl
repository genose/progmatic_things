      *================================================================*
      * PROGRAMME  : GSTK005                                         *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-08-26                                       *
      * VERSION    : 1.0                                              *
      *----------------------------------------------------------------*
      * DESCRIPTION : RAPPORTS STOCK PAR CATEGORIE                    *
      *   Affiche les statistiques agglomeres par categorie :         *
      *   nb articles, qte totale, valeur, alertes, % du stock total. *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. GSTK005.
       AUTHOR. ETUDIANT-COBOL.
       DATE-WRITTEN. 2026-08-26.

       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       SPECIAL-NAMES.
           DECIMAL-POINT IS COMMA.

       DATA DIVISION.

      *================================================================*
       WORKING-STORAGE SECTION.

       01 W-PROG-ID         PIC X(8)  VALUE 'GSTK005 '.
       01 W-TRANS-ID        PIC X(4)  VALUE 'G005'.
       01 W-MAPSET          PIC X(8)  VALUE 'GSTK005M'.
       01 W-MAP             PIC X(7)  VALUE 'GSTK005'.
       01 W-CA-LEN          PIC S9(4) COMP VALUE 263.
       01 W-RESP            PIC S9(8) COMP VALUE 0.
       01 W-RESP2           PIC S9(8) COMP VALUE 0.

       01 W-ABSTIME         PIC S9(15) COMP-3 VALUE 0.
       01 W-DATE-JOUR       PIC X(10) VALUE SPACES.
       01 W-HEURE-JOUR      PIC X(8)  VALUE SPACES.
       01 W-DATETIME-EDI.
          05 W-DT-DATE      PIC X(10).
          05 FILLER         PIC X     VALUE SPACE.
          05 W-DT-HEURE     PIC X(8).

       01 W-FETCH-OK        PIC X     VALUE 'Y'.
          88 FETCH-OK                  VALUE 'Y'.
          88 FIN-FETCH                 VALUE 'N'.

      *--- STATISTIQUES GLOBALES -------------------------------------
       01 W-TOT-ACTIFS      PIC S9(7)     COMP-3 VALUE 0.
       01 W-TOT-VLR         PIC S9(14)V99 COMP-3 VALUE 0.
       01 W-TOT-ALT         PIC S9(5)     COMP-3 VALUE 0.
       01 W-TOT-MVT         PIC S9(7)     COMP-3 VALUE 0.

      *--- TABLE 10 LIGNES CATEGORIE ---------------------------------
       01 W-TABLE-CAT.
          05 WC-LIG             OCCURS 10 TIMES.
             10 WC-CAT          PIC X(15).
             10 WC-NA           PIC S9(7)     COMP-3.
             10 WC-QT           PIC S9(12)V999 COMP-3.
             10 WC-VL           PIC S9(14)V99 COMP-3.
             10 WC-AL           PIC S9(5)     COMP-3.
       01 W-NB-LUS          PIC S9(4) COMP VALUE 0.
       01 W-I               PIC S9(4) COMP VALUE 0.
       01 W-SKIP            PIC S9(4) COMP VALUE 0.
       01 W-PCT             PIC S9(5)V99 COMP-3 VALUE 0.

      *--- EDITIONS --------------------------------------------------
       01 W-ED-ACTIFS       PIC Z(5)9.
       01 W-ED-VLR          PIC Z(12),99.
       01 W-ED-ALT          PIC Z(4)9.
       01 W-ED-MVT          PIC Z(5)9.
       01 W-ED-NA           PIC Z(5)9.
       01 W-ED-QT           PIC Z(9),999.
       01 W-ED-VL           PIC Z(12),99.
       01 W-ED-AL           PIC ZZZZ9.
       01 W-ED-PC           PIC ZZ9,99.
       01 W-ED-PAGCUR       PIC ZZZ9.
       01 W-ED-PAGTOT       PIC ZZZ9.

      *--- HOST VARIABLES SQL ----------------------------------------
       01 HV-TOT-ACT        PIC S9(7)     COMP-3.
       01 HV-TOT-VLR        PIC S9(14)V99 COMP-3.
       01 HV-TOT-ALT        PIC S9(5)     COMP-3.
       01 HV-TOT-MVT        PIC S9(7)     COMP-3.
       01 HV-CAT            PIC X(15).
       01 HV-NA             PIC S9(7)     COMP-3.
       01 HV-QT             PIC S9(12)V999 COMP-3.
       01 HV-VL             PIC S9(14)V99 COMP-3.
       01 HV-AL             PIC S9(5)     COMP-3.

           EXEC SQL INCLUDE SQLCA END-EXEC.

       COPY GSTK005M.
       COPY DFHAID.
       COPY DFHBMSCA.
       COPY GSTKCOMM.

      *================================================================*
       LINKAGE SECTION.
       01 DFHCOMMAREA        PIC X(263).

      *================================================================*
       PROCEDURE DIVISION.
      *================================================================*

           EXEC SQL
               DECLARE CURS-CAT CURSOR FOR
               SELECT ART_CATEGORIE,
                      COUNT(*),
                      COALESCE(SUM(ART_QTE_STOCK), 0),
                      COALESCE(SUM(ART_QTE_STOCK * ART_PRIX_VENTE), 0),
                      SUM(CASE WHEN ART_QTE_STOCK < ART_QTE_MIN
                               THEN 1 ELSE 0 END)
               FROM   GSTK.ARTICLES
               WHERE  ART_STATUT <> 'ARCHIVE'
               GROUP BY ART_CATEGORIE
               ORDER BY ART_CATEGORIE
           END-EXEC.

      ******************************************************************
       0000-PRINCIPAL.
      ******************************************************************
           EVALUATE TRUE
               WHEN EIBCALEN = ZERO
                   PERFORM 1000-PREMIERE-ENTREE
               WHEN OTHER
                   MOVE DFHCOMMAREA TO GSTK-COMMAREA
                   PERFORM 2000-RETOUR-TRANSACTION
           END-EVALUATE.
           EXEC CICS RETURN END-EXEC.
           STOP RUN.

      ******************************************************************
       1000-PREMIERE-ENTREE.
      ******************************************************************
           MOVE SPACES     TO GSTK-COMMAREA.
           MOVE 'G005    ' TO CA-TRAN-RETOUR.
           MOVE EIBTRMID   TO CA-TERMINAL.
           MOVE SPACES     TO CA-MSG-RETOUR.
           MOVE 1          TO CA-PAGE-COURANTE.
           PERFORM 3000-REQUETE-GLOBAL.
           PERFORM 4000-REQUETE-CATEGORIES.
           PERFORM 5000-AFFICHER-ECRAN.

      ******************************************************************
       2000-RETOUR-TRANSACTION.
      ******************************************************************
           MOVE SPACES TO CA-MSG-RETOUR.
           EVALUATE EIBAID
               WHEN DFHPF3
                   PERFORM 6000-RETOUR-MENU
               WHEN DFHPF7
                   IF CA-PAGE-COURANTE > 1
                       SUBTRACT 1 FROM CA-PAGE-COURANTE
                   ELSE
                       MOVE 'DEBUT DE LISTE' TO CA-MSG-RETOUR
                   END-IF
                   PERFORM 4000-REQUETE-CATEGORIES
                   PERFORM 5000-AFFICHER-ECRAN
               WHEN DFHPF8
                   IF CA-PAGE-COURANTE < CA-NB-PAGES
                       ADD 1 TO CA-PAGE-COURANTE
                   ELSE
                       MOVE 'FIN DE LISTE' TO CA-MSG-RETOUR
                   END-IF
                   PERFORM 4000-REQUETE-CATEGORIES
                   PERFORM 5000-AFFICHER-ECRAN
               WHEN OTHER
                   PERFORM 4000-REQUETE-CATEGORIES
                   PERFORM 5000-AFFICHER-ECRAN
           END-EVALUATE.

      ******************************************************************
       3000-REQUETE-GLOBAL.
      ******************************************************************
           EXEC SQL
               SELECT COUNT(*),
                      COALESCE(SUM(ART_QTE_STOCK * ART_PRIX_VENTE),0),
                      SUM(CASE WHEN ART_QTE_STOCK < ART_QTE_MIN
                               THEN 1 ELSE 0 END)
               INTO   :HV-TOT-ACT, :HV-TOT-VLR, :HV-TOT-ALT
               FROM   GSTK.ARTICLES
               WHERE  ART_STATUT = 'ACTIF'
           END-EXEC.
           IF SQLCODE = 0
               MOVE HV-TOT-ACT TO W-TOT-ACTIFS
               MOVE HV-TOT-VLR TO W-TOT-VLR
               MOVE HV-TOT-ALT TO W-TOT-ALT
           END-IF.
           EXEC SQL
               SELECT COUNT(*)
               INTO   :HV-TOT-MVT
               FROM   GSTK.MOUVEMENTS_STOCK
               WHERE  DATE(MVT_TIMESTAMP) = CURRENT_DATE
           END-EXEC.
           IF SQLCODE = 0
               MOVE HV-TOT-MVT TO W-TOT-MVT
           END-IF.
      *    Compter categories pour pagination
           EXEC SQL
               SELECT COUNT(DISTINCT ART_CATEGORIE)
               INTO   :HV-NA
               FROM   GSTK.ARTICLES
               WHERE  ART_STATUT <> 'ARCHIVE'
           END-EXEC.
           IF SQLCODE = 0
               COMPUTE CA-NB-PAGES = (HV-NA + 9) / 10
               IF CA-NB-PAGES = 0
                   MOVE 1 TO CA-NB-PAGES
               END-IF
           END-IF.

      ******************************************************************
       4000-REQUETE-CATEGORIES.
      ******************************************************************
           MOVE ZERO     TO W-NB-LUS.
           MOVE 'Y'      TO W-FETCH-OK.
           PERFORM VARYING W-I FROM 1 BY 1 UNTIL W-I > 10
               MOVE SPACES TO WC-CAT(W-I)
               MOVE ZERO   TO WC-NA(W-I) WC-QT(W-I)
               MOVE ZERO   TO WC-VL(W-I) WC-AL(W-I)
           END-PERFORM.
           EXEC SQL OPEN CURS-CAT END-EXEC.
           IF SQLCODE NOT = 0
               PERFORM 9100-ERREUR-SQL
               GO TO 4000-FIN
           END-IF.
           COMPUTE W-SKIP = (CA-PAGE-COURANTE - 1) * 10.
           PERFORM VARYING W-I FROM 1 BY 1
               UNTIL W-I > W-SKIP OR FIN-FETCH
               EXEC SQL FETCH CURS-CAT INTO
                   :HV-CAT, :HV-NA, :HV-QT, :HV-VL, :HV-AL
               END-EXEC
               IF SQLCODE NOT = 0
                   MOVE 'N' TO W-FETCH-OK
               END-IF
           END-PERFORM.
           PERFORM VARYING W-I FROM 1 BY 1
               UNTIL W-I > 10 OR FIN-FETCH
               EXEC SQL FETCH CURS-CAT INTO
                   :HV-CAT, :HV-NA, :HV-QT, :HV-VL, :HV-AL
               END-EXEC
               IF SQLCODE NOT = 0
                   MOVE 'N' TO W-FETCH-OK
               ELSE
                   MOVE HV-CAT TO WC-CAT(W-I)
                   MOVE HV-NA  TO WC-NA(W-I)
                   MOVE HV-QT  TO WC-QT(W-I)
                   MOVE HV-VL  TO WC-VL(W-I)
                   MOVE HV-AL  TO WC-AL(W-I)
                   ADD 1 TO W-NB-LUS
               END-IF
           END-PERFORM.
           EXEC SQL CLOSE CURS-CAT END-EXEC.
       4000-FIN.
           EXIT.

      ******************************************************************
       5000-AFFICHER-ECRAN.
      ******************************************************************
           MOVE LOW-VALUE TO GSTK005O.
           EXEC CICS ASKTIME ABSTIME(W-ABSTIME) END-EXEC.
           EXEC CICS FORMATTIME ABSTIME(W-ABSTIME)
               DDMMYYYY(W-DATE-JOUR)
               TIMESEP(':')
               TIME(W-HEURE-JOUR)
           END-EXEC.
           MOVE W-DATE-JOUR  TO W-DT-DATE.
           MOVE W-HEURE-JOUR TO W-DT-HEURE.
           MOVE W-DATETIME-EDI TO DATHRO  IN GSTK005O.
           MOVE CA-OPERATEUR   TO OPENAMO  IN GSTK005O.
           MOVE CA-TERMINAL    TO TERNAMO  IN GSTK005O.
           MOVE CA-PAGE-COURANTE TO W-ED-PAGCUR.
           MOVE CA-NB-PAGES      TO W-ED-PAGTOT.
           MOVE W-ED-PAGCUR TO PAGCURO  IN GSTK005O.
           MOVE W-ED-PAGTOT TO PAGTOTO  IN GSTK005O.
           MOVE W-TOT-ACTIFS TO W-ED-ACTIFS.
           MOVE W-ED-ACTIFS  TO GAACTO   IN GSTK005O.
           MOVE W-TOT-VLR    TO W-ED-VLR.
           MOVE W-ED-VLR     TO GAVLRO   IN GSTK005O.
           MOVE W-TOT-ALT    TO W-ED-ALT.
           MOVE W-ED-ALT     TO GAALTO   IN GSTK005O.
           MOVE W-TOT-MVT    TO W-ED-MVT.
           MOVE W-ED-MVT     TO GAMVTO   IN GSTK005O.
           IF CA-MSG-RETOUR NOT = SPACES
               MOVE CA-MSG-RETOUR TO MSGRTRO IN GSTK005O
           END-IF.
      *--- Ligne 1
           IF W-NB-LUS >= 1
               MOVE WC-CAT(1) TO CT01O IN GSTK005O
               MOVE WC-NA(1)  TO W-ED-NA
               MOVE W-ED-NA   TO NA01O IN GSTK005O
               MOVE WC-QT(1)  TO W-ED-QT
               MOVE W-ED-QT   TO QT01O IN GSTK005O
               MOVE WC-VL(1)  TO W-ED-VL
               MOVE W-ED-VL   TO VL01O IN GSTK005O
               MOVE WC-AL(1)  TO W-ED-AL
               MOVE W-ED-AL   TO AL01O IN GSTK005O
               IF W-TOT-VLR > ZERO
                   COMPUTE W-PCT = (WC-VL(1) * 100) / W-TOT-VLR
                   MOVE W-PCT TO W-ED-PC
                   MOVE W-ED-PC TO PC01O IN GSTK005O
               END-IF
           END-IF.
      *--- Ligne 2
           IF W-NB-LUS >= 2
               MOVE WC-CAT(2) TO CT02O IN GSTK005O
               MOVE WC-NA(2)  TO W-ED-NA
               MOVE W-ED-NA   TO NA02O IN GSTK005O
               MOVE WC-QT(2)  TO W-ED-QT
               MOVE W-ED-QT   TO QT02O IN GSTK005O
               MOVE WC-VL(2)  TO W-ED-VL
               MOVE W-ED-VL   TO VL02O IN GSTK005O
               MOVE WC-AL(2)  TO W-ED-AL
               MOVE W-ED-AL   TO AL02O IN GSTK005O
               IF W-TOT-VLR > ZERO
                   COMPUTE W-PCT = (WC-VL(2) * 100) / W-TOT-VLR
                   MOVE W-PCT TO W-ED-PC
                   MOVE W-ED-PC TO PC02O IN GSTK005O
               END-IF
           END-IF.
      *--- Ligne 3
           IF W-NB-LUS >= 3
               MOVE WC-CAT(3) TO CT03O IN GSTK005O
               MOVE WC-NA(3)  TO W-ED-NA
               MOVE W-ED-NA   TO NA03O IN GSTK005O
               MOVE WC-QT(3)  TO W-ED-QT
               MOVE W-ED-QT   TO QT03O IN GSTK005O
               MOVE WC-VL(3)  TO W-ED-VL
               MOVE W-ED-VL   TO VL03O IN GSTK005O
               MOVE WC-AL(3)  TO W-ED-AL
               MOVE W-ED-AL   TO AL03O IN GSTK005O
               IF W-TOT-VLR > ZERO
                   COMPUTE W-PCT = (WC-VL(3) * 100) / W-TOT-VLR
                   MOVE W-PCT TO W-ED-PC
                   MOVE W-ED-PC TO PC03O IN GSTK005O
               END-IF
           END-IF.
      *--- Ligne 4
           IF W-NB-LUS >= 4
               MOVE WC-CAT(4) TO CT04O IN GSTK005O
               MOVE WC-NA(4)  TO W-ED-NA
               MOVE W-ED-NA   TO NA04O IN GSTK005O
               MOVE WC-QT(4)  TO W-ED-QT
               MOVE W-ED-QT   TO QT04O IN GSTK005O
               MOVE WC-VL(4)  TO W-ED-VL
               MOVE W-ED-VL   TO VL04O IN GSTK005O
               MOVE WC-AL(4)  TO W-ED-AL
               MOVE W-ED-AL   TO AL04O IN GSTK005O
               IF W-TOT-VLR > ZERO
                   COMPUTE W-PCT = (WC-VL(4) * 100) / W-TOT-VLR
                   MOVE W-PCT TO W-ED-PC
                   MOVE W-ED-PC TO PC04O IN GSTK005O
               END-IF
           END-IF.
      *--- Ligne 5
           IF W-NB-LUS >= 5
               MOVE WC-CAT(5) TO CT05O IN GSTK005O
               MOVE WC-NA(5)  TO W-ED-NA
               MOVE W-ED-NA   TO NA05O IN GSTK005O
               MOVE WC-QT(5)  TO W-ED-QT
               MOVE W-ED-QT   TO QT05O IN GSTK005O
               MOVE WC-VL(5)  TO W-ED-VL
               MOVE W-ED-VL   TO VL05O IN GSTK005O
               MOVE WC-AL(5)  TO W-ED-AL
               MOVE W-ED-AL   TO AL05O IN GSTK005O
               IF W-TOT-VLR > ZERO
                   COMPUTE W-PCT = (WC-VL(5) * 100) / W-TOT-VLR
                   MOVE W-PCT TO W-ED-PC
                   MOVE W-ED-PC TO PC05O IN GSTK005O
               END-IF
           END-IF.
      *--- Ligne 6
           IF W-NB-LUS >= 6
               MOVE WC-CAT(6) TO CT06O IN GSTK005O
               MOVE WC-NA(6)  TO W-ED-NA
               MOVE W-ED-NA   TO NA06O IN GSTK005O
               MOVE WC-QT(6)  TO W-ED-QT
               MOVE W-ED-QT   TO QT06O IN GSTK005O
               MOVE WC-VL(6)  TO W-ED-VL
               MOVE W-ED-VL   TO VL06O IN GSTK005O
               MOVE WC-AL(6)  TO W-ED-AL
               MOVE W-ED-AL   TO AL06O IN GSTK005O
               IF W-TOT-VLR > ZERO
                   COMPUTE W-PCT = (WC-VL(6) * 100) / W-TOT-VLR
                   MOVE W-PCT TO W-ED-PC
                   MOVE W-ED-PC TO PC06O IN GSTK005O
               END-IF
           END-IF.
      *--- Ligne 7
           IF W-NB-LUS >= 7
               MOVE WC-CAT(7) TO CT07O IN GSTK005O
               MOVE WC-NA(7)  TO W-ED-NA
               MOVE W-ED-NA   TO NA07O IN GSTK005O
               MOVE WC-QT(7)  TO W-ED-QT
               MOVE W-ED-QT   TO QT07O IN GSTK005O
               MOVE WC-VL(7)  TO W-ED-VL
               MOVE W-ED-VL   TO VL07O IN GSTK005O
               MOVE WC-AL(7)  TO W-ED-AL
               MOVE W-ED-AL   TO AL07O IN GSTK005O
               IF W-TOT-VLR > ZERO
                   COMPUTE W-PCT = (WC-VL(7) * 100) / W-TOT-VLR
                   MOVE W-PCT TO W-ED-PC
                   MOVE W-ED-PC TO PC07O IN GSTK005O
               END-IF
           END-IF.
      *--- Ligne 8
           IF W-NB-LUS >= 8
               MOVE WC-CAT(8) TO CT08O IN GSTK005O
               MOVE WC-NA(8)  TO W-ED-NA
               MOVE W-ED-NA   TO NA08O IN GSTK005O
               MOVE WC-QT(8)  TO W-ED-QT
               MOVE W-ED-QT   TO QT08O IN GSTK005O
               MOVE WC-VL(8)  TO W-ED-VL
               MOVE W-ED-VL   TO VL08O IN GSTK005O
               MOVE WC-AL(8)  TO W-ED-AL
               MOVE W-ED-AL   TO AL08O IN GSTK005O
               IF W-TOT-VLR > ZERO
                   COMPUTE W-PCT = (WC-VL(8) * 100) / W-TOT-VLR
                   MOVE W-PCT TO W-ED-PC
                   MOVE W-ED-PC TO PC08O IN GSTK005O
               END-IF
           END-IF.
      *--- Ligne 9
           IF W-NB-LUS >= 9
               MOVE WC-CAT(9) TO CT09O IN GSTK005O
               MOVE WC-NA(9)  TO W-ED-NA
               MOVE W-ED-NA   TO NA09O IN GSTK005O
               MOVE WC-QT(9)  TO W-ED-QT
               MOVE W-ED-QT   TO QT09O IN GSTK005O
               MOVE WC-VL(9)  TO W-ED-VL
               MOVE W-ED-VL   TO VL09O IN GSTK005O
               MOVE WC-AL(9)  TO W-ED-AL
               MOVE W-ED-AL   TO AL09O IN GSTK005O
               IF W-TOT-VLR > ZERO
                   COMPUTE W-PCT = (WC-VL(9) * 100) / W-TOT-VLR
                   MOVE W-PCT TO W-ED-PC
                   MOVE W-ED-PC TO PC09O IN GSTK005O
               END-IF
           END-IF.
      *--- Ligne 10
           IF W-NB-LUS >= 10
               MOVE WC-CAT(10) TO CT10O IN GSTK005O
               MOVE WC-NA(10)  TO W-ED-NA
               MOVE W-ED-NA    TO NA10O IN GSTK005O
               MOVE WC-QT(10)  TO W-ED-QT
               MOVE W-ED-QT    TO QT10O IN GSTK005O
               MOVE WC-VL(10)  TO W-ED-VL
               MOVE W-ED-VL    TO VL10O IN GSTK005O
               MOVE WC-AL(10)  TO W-ED-AL
               MOVE W-ED-AL    TO AL10O IN GSTK005O
               IF W-TOT-VLR > ZERO
                   COMPUTE W-PCT = (WC-VL(10) * 100) / W-TOT-VLR
                   MOVE W-PCT TO W-ED-PC
                   MOVE W-ED-PC TO PC10O IN GSTK005O
               END-IF
           END-IF.
      *--- Totaux
           MOVE W-TOT-ACTIFS TO W-ED-NA
           MOVE W-ED-NA      TO TOTNAO  IN GSTK005O.
           MOVE W-TOT-VLR    TO W-ED-VL
           MOVE W-ED-VL      TO TOTVLO  IN GSTK005O.
           MOVE W-TOT-ALT    TO W-ED-AL
           MOVE W-ED-AL      TO TOTALO  IN GSTK005O.
           EXEC CICS SEND MAP(W-MAP) MAPSET(W-MAPSET)
               FROM(GSTK005O)
               ERASE CURSOR
               RESP(W-RESP) RESP2(W-RESP2)
           END-EXEC.
           IF W-RESP NOT = DFHRESP(NORMAL)
               PERFORM 9000-ERREUR-CICS
           END-IF.
           EXEC CICS RETURN
               TRANSID(W-TRANS-ID)
               COMMAREA(GSTK-COMMAREA)
               LENGTH(W-CA-LEN)
           END-EXEC.

      ******************************************************************
       6000-RETOUR-MENU.
      ******************************************************************
           MOVE 'G000    ' TO CA-TRAN-RETOUR.
           EXEC CICS XCTL PROGRAM('GSTK000 ')
               COMMAREA(GSTK-COMMAREA) LENGTH(W-CA-LEN)
           END-EXEC.

      ******************************************************************
       9000-ERREUR-CICS.
      ******************************************************************
           MOVE 'ERREUR CICS' TO CA-MSG-RETOUR.
           EXEC CICS ABEND ABCODE('G005') NODUMP END-EXEC.

      ******************************************************************
       9100-ERREUR-SQL.
      ******************************************************************
           IF SQLCODE = +100
               MOVE 'AUCUNE CATEGORIE TROUVEE' TO CA-MSG-RETOUR
           ELSE
               MOVE 'ERREUR SQL - CODE : '  TO CA-MSG-RETOUR
               MOVE SQLCODE                 TO CA-MSG-RETOUR(22:6)
           END-IF.
