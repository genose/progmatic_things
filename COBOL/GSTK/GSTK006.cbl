      *================================================================*
      * PROGRAMME  : GSTK006                                         *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-08-26                                       *
      * VERSION    : 1.0                                              *
      *----------------------------------------------------------------*
      * DESCRIPTION : ALERTES STOCK CRITIQUE                          *
      *   Liste des articles dont ART_QTE_STOCK < ART_QTE_MIN,       *
      *   tries par niveau de criticite (ratio qte/min croissant).   *
      *   PF6 -> ENTREE MARCHANDISE pour le reapprovisionnement.      *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. GSTK006.
       AUTHOR. ETUDIANT-COBOL.
       DATE-WRITTEN. 2026-08-26.

       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       SPECIAL-NAMES.
           DECIMAL-POINT IS COMMA.

       DATA DIVISION.

      *================================================================*
       WORKING-STORAGE SECTION.

       01 W-PROG-ID         PIC X(8)  VALUE 'GSTK006 '.
       01 W-TRANS-ID        PIC X(4)  VALUE 'G006'.
       01 W-MAPSET          PIC X(8)  VALUE 'GSTK006M'.
       01 W-MAP             PIC X(7)  VALUE 'GSTK006'.
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

       01 W-FIL-CODE        PIC X(12) VALUE '%'.
       01 W-FIL-CATEG       PIC X(17) VALUE '%'.

      *--- TABLE 10 LIGNES -------------------------------------------
       01 W-TABLE-ALT.
          05 WA-LIG             OCCURS 10 TIMES.
             10 WA-CODE         PIC X(10).
             10 WA-DESIG        PIC X(24).
             10 WA-CATEG        PIC X(9).
             10 WA-QTE          PIC S9(10)V999 COMP-3.
             10 WA-MIN          PIC S9(10)V999 COMP-3.
             10 WA-MANQ         PIC S9(10)V999 COMP-3.
             10 WA-DLV          PIC 9(3).
             10 WA-STAT         PIC X(8).
       01 W-NB-LUS          PIC S9(4) COMP VALUE 0.
       01 W-TOT-ALT         PIC S9(5) COMP-3 VALUE 0.
       01 W-I               PIC S9(4) COMP VALUE 0.
       01 W-SKIP            PIC S9(4) COMP VALUE 0.
       01 W-PROG-CIBLE      PIC X(8)  VALUE SPACES.

      *--- EDITIONS --------------------------------------------------
       01 W-ED-QTE          PIC Z(5),999.
       01 W-ED-MIN          PIC Z(5),999.
       01 W-ED-MQ           PIC Z(5),999.
       01 W-ED-DL           PIC ZZZ9.
       01 W-ED-TOT          PIC Z(4)9.
       01 W-ED-PAG          PIC ZZZ9.

      *--- HOST VARIABLES SQL ----------------------------------------
       01 HV-CODE           PIC X(10).
       01 HV-DESIG          PIC X(50).
       01 HV-CATEG          PIC X(15).
       01 HV-QTE            PIC S9(10)V999 COMP-3.
       01 HV-MIN            PIC S9(10)V999 COMP-3.
       01 HV-MANQ           PIC S9(10)V999 COMP-3.
       01 HV-DLV            PIC 9(3).
       01 HV-STAT           PIC X(10).
       01 HV-FIL-CODE       PIC X(12).
       01 HV-FIL-CATEG      PIC X(17).
       01 HV-TOT            PIC S9(5)     COMP-3.

           EXEC SQL INCLUDE SQLCA END-EXEC.

       COPY GSTK006M.
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
               DECLARE CURS-ALT CURSOR FOR
               SELECT ART_CODE,
                      ART_DESIGNATION,
                      ART_CATEGORIE,
                      ART_QTE_STOCK,
                      ART_QTE_MIN,
                      ART_QTE_MIN - ART_QTE_STOCK,
                      ART_DELAI_APPRO,
                      ART_STATUT
               FROM   GSTK.ARTICLES
               WHERE  ART_QTE_STOCK < ART_QTE_MIN
               AND    ART_STATUT     = 'ACTIF'
               AND    ART_CODE       LIKE :HV-FIL-CODE
               AND    ART_CATEGORIE  LIKE :HV-FIL-CATEG
               ORDER BY (ART_QTE_STOCK / NULLIF(ART_QTE_MIN, 0)) ASC
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
           MOVE 'G006    ' TO CA-TRAN-RETOUR.
           MOVE EIBTRMID   TO CA-TERMINAL.
           MOVE SPACES     TO CA-MSG-RETOUR.
           MOVE 1          TO CA-PAGE-COURANTE.
           MOVE '%'        TO W-FIL-CODE W-FIL-CATEG.
           PERFORM 3000-COMPTER.
           PERFORM 4000-REQUETE.
           PERFORM 5000-AFFICHER-ECRAN.

      ******************************************************************
       2000-RETOUR-TRANSACTION.
      ******************************************************************
           MOVE GSTK-COMMAREA(209:12) TO W-FIL-CODE.
           MOVE GSTK-COMMAREA(221:17) TO W-FIL-CATEG.
           MOVE SPACES TO CA-MSG-RETOUR.
           EXEC CICS RECEIVE MAP(W-MAP) MAPSET(W-MAPSET)
               INTO(GSTK006I)
               RESP(W-RESP) RESP2(W-RESP2)
           END-EXEC.
           IF W-RESP = DFHRESP(MAPFAIL)
               PERFORM 4000-REQUETE
               PERFORM 5000-AFFICHER-ECRAN
               GO TO 2000-FIN
           END-IF.
           EVALUATE EIBAID
               WHEN DFHPF3
                   PERFORM 6000-RETOUR-MENU
               WHEN DFHPF5
                   PERFORM 2100-MAJ-FILTRES
                   MOVE 1 TO CA-PAGE-COURANTE
                   PERFORM 3000-COMPTER
                   PERFORM 4000-REQUETE
                   PERFORM 5000-AFFICHER-ECRAN
               WHEN DFHPF6
                   PERFORM 2200-SELECTIONNER
                   MOVE W-PROG-ID  TO CA-TRAN-RETOUR
                   MOVE 'GSTK002 ' TO W-PROG-CIBLE
                   EXEC CICS XCTL PROGRAM(W-PROG-CIBLE)
                       COMMAREA(GSTK-COMMAREA) LENGTH(W-CA-LEN)
                   END-EXEC
               WHEN DFHPF7
                   IF CA-PAGE-COURANTE > 1
                       SUBTRACT 1 FROM CA-PAGE-COURANTE
                   ELSE
                       MOVE 'DEBUT DE LISTE' TO CA-MSG-RETOUR
                   END-IF
                   PERFORM 4000-REQUETE
                   PERFORM 5000-AFFICHER-ECRAN
               WHEN DFHPF8
                   IF CA-PAGE-COURANTE < CA-NB-PAGES
                       ADD 1 TO CA-PAGE-COURANTE
                   ELSE
                       MOVE 'FIN DE LISTE' TO CA-MSG-RETOUR
                   END-IF
                   PERFORM 4000-REQUETE
                   PERFORM 5000-AFFICHER-ECRAN
               WHEN OTHER
                   PERFORM 4000-REQUETE
                   PERFORM 5000-AFFICHER-ECRAN
           END-EVALUATE.
       2000-FIN.
           EXIT.

      ******************************************************************
       2100-MAJ-FILTRES.
      ******************************************************************
           IF FILCOL IN GSTK006I > ZERO
               IF FILCOI IN GSTK006I = SPACES
                   MOVE '%' TO W-FIL-CODE
               ELSE
                   STRING FILCOI IN GSTK006I DELIMITED SPACE
                          '%' DELIMITED SIZE INTO W-FIL-CODE
               END-IF
           END-IF.
           IF FILCAL IN GSTK006I > ZERO
               IF FILCAI IN GSTK006I = SPACES
                   MOVE '%' TO W-FIL-CATEG
               ELSE
                   STRING FILCAI IN GSTK006I DELIMITED SPACE
                          '%' DELIMITED SIZE INTO W-FIL-CATEG
               END-IF
           END-IF.

      ******************************************************************
       2200-SELECTIONNER.
      ******************************************************************
           MOVE SPACES TO CA-ART-CODE-SELEC.
           IF SEL01L IN GSTK006I > ZERO AND W-NB-LUS >= 1
               MOVE WA-CODE(1) TO CA-ART-CODE-SELEC
           ELSE IF SEL02L IN GSTK006I > ZERO AND W-NB-LUS >= 2
               MOVE WA-CODE(2) TO CA-ART-CODE-SELEC
           ELSE IF SEL03L IN GSTK006I > ZERO AND W-NB-LUS >= 3
               MOVE WA-CODE(3) TO CA-ART-CODE-SELEC
           ELSE IF SEL04L IN GSTK006I > ZERO AND W-NB-LUS >= 4
               MOVE WA-CODE(4) TO CA-ART-CODE-SELEC
           ELSE IF SEL05L IN GSTK006I > ZERO AND W-NB-LUS >= 5
               MOVE WA-CODE(5) TO CA-ART-CODE-SELEC
           ELSE IF SEL06L IN GSTK006I > ZERO AND W-NB-LUS >= 6
               MOVE WA-CODE(6) TO CA-ART-CODE-SELEC
           ELSE IF SEL07L IN GSTK006I > ZERO AND W-NB-LUS >= 7
               MOVE WA-CODE(7) TO CA-ART-CODE-SELEC
           ELSE IF SEL08L IN GSTK006I > ZERO AND W-NB-LUS >= 8
               MOVE WA-CODE(8) TO CA-ART-CODE-SELEC
           ELSE IF SEL09L IN GSTK006I > ZERO AND W-NB-LUS >= 9
               MOVE WA-CODE(9) TO CA-ART-CODE-SELEC
           ELSE IF SEL10L IN GSTK006I > ZERO AND W-NB-LUS >= 10
               MOVE WA-CODE(10) TO CA-ART-CODE-SELEC
           END-IF.

      ******************************************************************
       3000-COMPTER.
      ******************************************************************
           MOVE W-FIL-CODE  TO HV-FIL-CODE.
           MOVE W-FIL-CATEG TO HV-FIL-CATEG.
           EXEC SQL
               SELECT COUNT(*)
               INTO   :HV-TOT
               FROM   GSTK.ARTICLES
               WHERE  ART_QTE_STOCK < ART_QTE_MIN
               AND    ART_STATUT     = 'ACTIF'
               AND    ART_CODE       LIKE :HV-FIL-CODE
               AND    ART_CATEGORIE  LIKE :HV-FIL-CATEG
           END-EXEC.
           IF SQLCODE = 0
               MOVE HV-TOT TO W-TOT-ALT
               COMPUTE CA-NB-PAGES = (W-TOT-ALT + 9) / 10
               IF CA-NB-PAGES = 0
                   MOVE 1 TO CA-NB-PAGES
               END-IF
           END-IF.

      ******************************************************************
       4000-REQUETE.
      ******************************************************************
           MOVE W-FIL-CODE  TO HV-FIL-CODE.
           MOVE W-FIL-CATEG TO HV-FIL-CATEG.
           MOVE ZERO TO W-NB-LUS.
           MOVE 'Y'  TO W-FETCH-OK.
           PERFORM VARYING W-I FROM 1 BY 1 UNTIL W-I > 10
               MOVE SPACES TO WA-CODE(W-I) WA-DESIG(W-I)
               MOVE SPACES TO WA-CATEG(W-I) WA-STAT(W-I)
               MOVE ZERO   TO WA-QTE(W-I) WA-MIN(W-I)
               MOVE ZERO   TO WA-MANQ(W-I) WA-DLV(W-I)
           END-PERFORM.
           EXEC SQL OPEN CURS-ALT END-EXEC.
           IF SQLCODE NOT = 0
               PERFORM 9100-ERREUR-SQL
               GO TO 4000-FIN
           END-IF.
           COMPUTE W-SKIP = (CA-PAGE-COURANTE - 1) * 10.
           PERFORM VARYING W-I FROM 1 BY 1
               UNTIL W-I > W-SKIP OR FIN-FETCH
               EXEC SQL FETCH CURS-ALT INTO
                   :HV-CODE,  :HV-DESIG, :HV-CATEG,
                   :HV-QTE,   :HV-MIN,   :HV-MANQ,
                   :HV-DLV,   :HV-STAT
               END-EXEC
               IF SQLCODE NOT = 0
                   MOVE 'N' TO W-FETCH-OK
               END-IF
           END-PERFORM.
           PERFORM VARYING W-I FROM 1 BY 1
               UNTIL W-I > 10 OR FIN-FETCH
               EXEC SQL FETCH CURS-ALT INTO
                   :HV-CODE,  :HV-DESIG, :HV-CATEG,
                   :HV-QTE,   :HV-MIN,   :HV-MANQ,
                   :HV-DLV,   :HV-STAT
               END-EXEC
               IF SQLCODE NOT = 0
                   MOVE 'N' TO W-FETCH-OK
               ELSE
                   MOVE HV-CODE        TO WA-CODE(W-I)
                   MOVE HV-DESIG(1:24) TO WA-DESIG(W-I)
                   MOVE HV-CATEG(1:9)  TO WA-CATEG(W-I)
                   MOVE HV-QTE         TO WA-QTE(W-I)
                   MOVE HV-MIN         TO WA-MIN(W-I)
                   MOVE HV-MANQ        TO WA-MANQ(W-I)
                   MOVE HV-DLV         TO WA-DLV(W-I)
                   MOVE HV-STAT(1:8)   TO WA-STAT(W-I)
                   ADD 1 TO W-NB-LUS
               END-IF
           END-PERFORM.
           EXEC SQL CLOSE CURS-ALT END-EXEC.
       4000-FIN.
           EXIT.

      ******************************************************************
       5000-AFFICHER-ECRAN.
      ******************************************************************
           MOVE LOW-VALUE TO GSTK006O.
           EXEC CICS ASKTIME ABSTIME(W-ABSTIME) END-EXEC.
           EXEC CICS FORMATTIME ABSTIME(W-ABSTIME)
               DDMMYYYY(W-DATE-JOUR)
               TIMESEP(':')
               TIME(W-HEURE-JOUR)
           END-EXEC.
           MOVE W-DATE-JOUR  TO W-DT-DATE.
           MOVE W-HEURE-JOUR TO W-DT-HEURE.
           MOVE W-DATETIME-EDI TO DATHRO  IN GSTK006O.
           MOVE CA-OPERATEUR   TO OPENAMO  IN GSTK006O.
           MOVE CA-TERMINAL    TO TERNAMO  IN GSTK006O.
           MOVE CA-PAGE-COURANTE TO W-ED-PAG.
           MOVE W-ED-PAG TO PAGCURO IN GSTK006O.
           MOVE CA-NB-PAGES TO W-ED-PAG.
           MOVE W-ED-PAG TO PAGTOTO IN GSTK006O.
           MOVE W-TOT-ALT TO W-ED-TOT.
           MOVE W-ED-TOT  TO TOTALTO IN GSTK006O.
           IF CA-MSG-RETOUR NOT = SPACES
               MOVE CA-MSG-RETOUR TO MSGRTRO IN GSTK006O
           END-IF.
      *--- 10 lignes
           IF W-NB-LUS >= 1
               MOVE WA-CODE(1)  TO AC01O IN GSTK006O
               MOVE WA-DESIG(1) TO DS01O IN GSTK006O
               MOVE WA-CATEG(1) TO CT01O IN GSTK006O
               MOVE WA-QTE(1)   TO W-ED-QTE
               MOVE W-ED-QTE    TO QT01O IN GSTK006O
               MOVE WA-MIN(1)   TO W-ED-MIN
               MOVE W-ED-MIN    TO MN01O IN GSTK006O
               MOVE WA-MANQ(1)  TO W-ED-MQ
               MOVE W-ED-MQ     TO MQ01O IN GSTK006O
               MOVE WA-DLV(1)   TO W-ED-DL
               MOVE W-ED-DL     TO DL01O IN GSTK006O
               MOVE WA-STAT(1)  TO ST01O IN GSTK006O
           END-IF.
           IF W-NB-LUS >= 2
               MOVE WA-CODE(2)  TO AC02O IN GSTK006O
               MOVE WA-DESIG(2) TO DS02O IN GSTK006O
               MOVE WA-CATEG(2) TO CT02O IN GSTK006O
               MOVE WA-QTE(2)   TO W-ED-QTE
               MOVE W-ED-QTE    TO QT02O IN GSTK006O
               MOVE WA-MIN(2)   TO W-ED-MIN
               MOVE W-ED-MIN    TO MN02O IN GSTK006O
               MOVE WA-MANQ(2)  TO W-ED-MQ
               MOVE W-ED-MQ     TO MQ02O IN GSTK006O
               MOVE WA-DLV(2)   TO W-ED-DL
               MOVE W-ED-DL     TO DL02O IN GSTK006O
               MOVE WA-STAT(2)  TO ST02O IN GSTK006O
           END-IF.
           IF W-NB-LUS >= 3
               MOVE WA-CODE(3)  TO AC03O IN GSTK006O
               MOVE WA-DESIG(3) TO DS03O IN GSTK006O
               MOVE WA-CATEG(3) TO CT03O IN GSTK006O
               MOVE WA-QTE(3)   TO W-ED-QTE
               MOVE W-ED-QTE    TO QT03O IN GSTK006O
               MOVE WA-MIN(3)   TO W-ED-MIN
               MOVE W-ED-MIN    TO MN03O IN GSTK006O
               MOVE WA-MANQ(3)  TO W-ED-MQ
               MOVE W-ED-MQ     TO MQ03O IN GSTK006O
               MOVE WA-DLV(3)   TO W-ED-DL
               MOVE W-ED-DL     TO DL03O IN GSTK006O
               MOVE WA-STAT(3)  TO ST03O IN GSTK006O
           END-IF.
           IF W-NB-LUS >= 4
               MOVE WA-CODE(4)  TO AC04O IN GSTK006O
               MOVE WA-DESIG(4) TO DS04O IN GSTK006O
               MOVE WA-CATEG(4) TO CT04O IN GSTK006O
               MOVE WA-QTE(4)   TO W-ED-QTE
               MOVE W-ED-QTE    TO QT04O IN GSTK006O
               MOVE WA-MIN(4)   TO W-ED-MIN
               MOVE W-ED-MIN    TO MN04O IN GSTK006O
               MOVE WA-MANQ(4)  TO W-ED-MQ
               MOVE W-ED-MQ     TO MQ04O IN GSTK006O
               MOVE WA-DLV(4)   TO W-ED-DL
               MOVE W-ED-DL     TO DL04O IN GSTK006O
               MOVE WA-STAT(4)  TO ST04O IN GSTK006O
           END-IF.
           IF W-NB-LUS >= 5
               MOVE WA-CODE(5)  TO AC05O IN GSTK006O
               MOVE WA-DESIG(5) TO DS05O IN GSTK006O
               MOVE WA-CATEG(5) TO CT05O IN GSTK006O
               MOVE WA-QTE(5)   TO W-ED-QTE
               MOVE W-ED-QTE    TO QT05O IN GSTK006O
               MOVE WA-MIN(5)   TO W-ED-MIN
               MOVE W-ED-MIN    TO MN05O IN GSTK006O
               MOVE WA-MANQ(5)  TO W-ED-MQ
               MOVE W-ED-MQ     TO MQ05O IN GSTK006O
               MOVE WA-DLV(5)   TO W-ED-DL
               MOVE W-ED-DL     TO DL05O IN GSTK006O
               MOVE WA-STAT(5)  TO ST05O IN GSTK006O
           END-IF.
           IF W-NB-LUS >= 6
               MOVE WA-CODE(6)  TO AC06O IN GSTK006O
               MOVE WA-DESIG(6) TO DS06O IN GSTK006O
               MOVE WA-CATEG(6) TO CT06O IN GSTK006O
               MOVE WA-QTE(6)   TO W-ED-QTE
               MOVE W-ED-QTE    TO QT06O IN GSTK006O
               MOVE WA-MIN(6)   TO W-ED-MIN
               MOVE W-ED-MIN    TO MN06O IN GSTK006O
               MOVE WA-MANQ(6)  TO W-ED-MQ
               MOVE W-ED-MQ     TO MQ06O IN GSTK006O
               MOVE WA-DLV(6)   TO W-ED-DL
               MOVE W-ED-DL     TO DL06O IN GSTK006O
               MOVE WA-STAT(6)  TO ST06O IN GSTK006O
           END-IF.
           IF W-NB-LUS >= 7
               MOVE WA-CODE(7)  TO AC07O IN GSTK006O
               MOVE WA-DESIG(7) TO DS07O IN GSTK006O
               MOVE WA-CATEG(7) TO CT07O IN GSTK006O
               MOVE WA-QTE(7)   TO W-ED-QTE
               MOVE W-ED-QTE    TO QT07O IN GSTK006O
               MOVE WA-MIN(7)   TO W-ED-MIN
               MOVE W-ED-MIN    TO MN07O IN GSTK006O
               MOVE WA-MANQ(7)  TO W-ED-MQ
               MOVE W-ED-MQ     TO MQ07O IN GSTK006O
               MOVE WA-DLV(7)   TO W-ED-DL
               MOVE W-ED-DL     TO DL07O IN GSTK006O
               MOVE WA-STAT(7)  TO ST07O IN GSTK006O
           END-IF.
           IF W-NB-LUS >= 8
               MOVE WA-CODE(8)  TO AC08O IN GSTK006O
               MOVE WA-DESIG(8) TO DS08O IN GSTK006O
               MOVE WA-CATEG(8) TO CT08O IN GSTK006O
               MOVE WA-QTE(8)   TO W-ED-QTE
               MOVE W-ED-QTE    TO QT08O IN GSTK006O
               MOVE WA-MIN(8)   TO W-ED-MIN
               MOVE W-ED-MIN    TO MN08O IN GSTK006O
               MOVE WA-MANQ(8)  TO W-ED-MQ
               MOVE W-ED-MQ     TO MQ08O IN GSTK006O
               MOVE WA-DLV(8)   TO W-ED-DL
               MOVE W-ED-DL     TO DL08O IN GSTK006O
               MOVE WA-STAT(8)  TO ST08O IN GSTK006O
           END-IF.
           IF W-NB-LUS >= 9
               MOVE WA-CODE(9)  TO AC09O IN GSTK006O
               MOVE WA-DESIG(9) TO DS09O IN GSTK006O
               MOVE WA-CATEG(9) TO CT09O IN GSTK006O
               MOVE WA-QTE(9)   TO W-ED-QTE
               MOVE W-ED-QTE    TO QT09O IN GSTK006O
               MOVE WA-MIN(9)   TO W-ED-MIN
               MOVE W-ED-MIN    TO MN09O IN GSTK006O
               MOVE WA-MANQ(9)  TO W-ED-MQ
               MOVE W-ED-MQ     TO MQ09O IN GSTK006O
               MOVE WA-DLV(9)   TO W-ED-DL
               MOVE W-ED-DL     TO DL09O IN GSTK006O
               MOVE WA-STAT(9)  TO ST09O IN GSTK006O
           END-IF.
           IF W-NB-LUS >= 10
               MOVE WA-CODE(10)  TO AC10O IN GSTK006O
               MOVE WA-DESIG(10) TO DS10O IN GSTK006O
               MOVE WA-CATEG(10) TO CT10O IN GSTK006O
               MOVE WA-QTE(10)   TO W-ED-QTE
               MOVE W-ED-QTE     TO QT10O IN GSTK006O
               MOVE WA-MIN(10)   TO W-ED-MIN
               MOVE W-ED-MIN     TO MN10O IN GSTK006O
               MOVE WA-MANQ(10)  TO W-ED-MQ
               MOVE W-ED-MQ      TO MQ10O IN GSTK006O
               MOVE WA-DLV(10)   TO W-ED-DL
               MOVE W-ED-DL      TO DL10O IN GSTK006O
               MOVE WA-STAT(10)  TO ST10O IN GSTK006O
           END-IF.
           MOVE W-FIL-CODE  TO GSTK-COMMAREA(209:12).
           MOVE W-FIL-CATEG TO GSTK-COMMAREA(221:17).
           EXEC CICS SEND MAP(W-MAP) MAPSET(W-MAPSET)
               FROM(GSTK006O)
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
           EXEC CICS ABEND ABCODE('G006') NODUMP END-EXEC.

      ******************************************************************
       9100-ERREUR-SQL.
      ******************************************************************
           IF SQLCODE = +100
               MOVE 'AUCUNE ALERTE ACTIVE'  TO CA-MSG-RETOUR
           ELSE
               MOVE 'ERREUR SQL - CODE : '  TO CA-MSG-RETOUR
               MOVE SQLCODE                 TO CA-MSG-RETOUR(22:6)
           END-IF.
