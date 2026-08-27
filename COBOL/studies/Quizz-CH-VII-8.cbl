      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   TABLEAU DE FREQUENCES - COMPTAGE DES VALEURS 1 A 5
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. FREQ-TABLEAU.

       DATA DIVISION.
       WORKING-STORAGE SECTION.

       01 WS-DONNEES.
           05 WS-VAL PIC 9 VALUE 0
               OCCURS 15 TIMES INDEXED BY WS-IDX-D.

       01 WS-FREQUENCES.
           05 WS-FREQ PIC 9(3) VALUE ZEROES
               OCCURS 5 TIMES INDEXED BY WS-IDX-F.

       01 WS-VALEUR-COURANTE        PIC 9 VALUE 0.
       01 WS-IDX-TMP                PIC 9(2) VALUE 0.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           PERFORM 1000-INIT-DONNEES
           PERFORM 2000-COMPTER-FREQUENCES
           PERFORM 3000-AFFICHER-RESULTATS
           STOP RUN.

      ******************************************************************
       1000-INIT-DONNEES.
           MOVE 3 TO WS-VAL(1)
           MOVE 1 TO WS-VAL(2)
           MOVE 4 TO WS-VAL(3)
           MOVE 1 TO WS-VAL(4)
           MOVE 5 TO WS-VAL(5)
           MOVE 2 TO WS-VAL(6)
           MOVE 3 TO WS-VAL(7)
           MOVE 5 TO WS-VAL(8)
           MOVE 2 TO WS-VAL(9)
           MOVE 1 TO WS-VAL(10)
           MOVE 4 TO WS-VAL(11)
           MOVE 3 TO WS-VAL(12)
           MOVE 2 TO WS-VAL(13)
           MOVE 5 TO WS-VAL(14)
           MOVE 1 TO WS-VAL(15).

      ******************************************************************
       2000-COMPTER-FREQUENCES.
           PERFORM VARYING WS-IDX-D FROM 1 BY 1
               UNTIL WS-IDX-D > 15
               MOVE WS-VAL(WS-IDX-D) TO WS-IDX-TMP
               ADD 1 TO WS-FREQ(WS-IDX-TMP)
           END-PERFORM.

      ******************************************************************
       3000-AFFICHER-RESULTATS.
           DISPLAY "================================"
           DISPLAY " TABLEAU DE FREQUENCES          "
           DISPLAY "================================"
           DISPLAY " Valeur | Frequence | Graphique "
           DISPLAY "--------------------------------"

           PERFORM VARYING WS-IDX-F FROM 1 BY 1
               UNTIL WS-IDX-F > 5
               MOVE WS-IDX-F TO WS-VALEUR-COURANTE
               DISPLAY "   " WS-VALEUR-COURANTE
                   "    |     " WS-FREQ(WS-IDX-F)
                   "     | "
                   PERFORM WS-FREQ(WS-IDX-F) TIMES
                       DISPLAY "*" WITH NO ADVANCING
                   END-PERFORM
                   DISPLAY SPACES
           END-PERFORM

           DISPLAY "================================".

       END PROGRAM FREQ-TABLEAU.
