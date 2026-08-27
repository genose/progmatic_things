      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   VALEUR MAXIMALE D'UN TABLEAU - SAISIE ET RECHERCHE
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. MAX-TABLEAU.

       DATA DIVISION.
       WORKING-STORAGE SECTION.

       01 WS-TABLEAU.
           05 WS-VAL                PIC S9(6) VALUE ZEROES
               OCCURS 10 TIMES INDEXED BY WS-IDX.

       01 WS-SAISIE                 PIC X(7).
       01 WS-VAL-SAISIE             PIC S9(6).
       01 WS-MAX                    PIC S9(6) VALUE ZEROES.
       01 WS-POS-MAX                PIC 9(2)  VALUE 1.
       01 WS-AFF-IDX                PIC 99.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           DISPLAY "========================================"
           DISPLAY "   SAISIE DU TABLEAU (10 ENTIERS)       "
           DISPLAY "========================================"

           PERFORM VARYING WS-IDX FROM 1 BY 1
               UNTIL WS-IDX > 10
               MOVE WS-IDX TO WS-AFF-IDX
               DISPLAY "Valeur " WS-AFF-IDX " : "
                   WITH NO ADVANCING
               ACCEPT WS-SAISIE
               MOVE FUNCTION NUMVAL(WS-SAISIE) TO WS-VAL(WS-IDX)
           END-PERFORM

           PERFORM 1000-CHERCHER-MAX
           PERFORM 2000-AFFICHER-RESULTATS
           STOP RUN.

      ******************************************************************
       1000-CHERCHER-MAX.
           MOVE WS-VAL(1) TO WS-MAX
           MOVE 1         TO WS-POS-MAX

           PERFORM VARYING WS-IDX FROM 2 BY 1
               UNTIL WS-IDX > 10
               IF WS-VAL(WS-IDX) > WS-MAX
                   MOVE WS-VAL(WS-IDX) TO WS-MAX
                   MOVE WS-IDX         TO WS-POS-MAX
               END-IF
           END-PERFORM.

      ******************************************************************
       2000-AFFICHER-RESULTATS.
           DISPLAY SPACES
           DISPLAY "========================================"
           DISPLAY "          RESULTATS                     "
           DISPLAY "========================================"

           PERFORM VARYING WS-IDX FROM 1 BY 1
               UNTIL WS-IDX > 10
               MOVE WS-IDX TO WS-AFF-IDX
               IF WS-IDX = WS-POS-MAX
                   DISPLAY "  [" WS-AFF-IDX "] " WS-VAL(WS-IDX)
                       "  <-- MAXIMUM"
               ELSE
                   DISPLAY "  [" WS-AFF-IDX "] " WS-VAL(WS-IDX)
               END-IF
           END-PERFORM

           DISPLAY SPACES
           DISPLAY "  Valeur maximale : " WS-MAX
           DISPLAY "  Position        : " WS-POS-MAX.

       END PROGRAM MAX-TABLEAU.
