      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   TRAITEMENT DES NOTES DE 10 ETUDIANTS
      *            Saisie, moyenne, comptage au-dessus de la moyenne
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. NOTES-ETUDIANTS.

       DATA DIVISION.
       WORKING-STORAGE SECTION.

       01 WS-NOTES.
           05 WS-NOTE               PIC 9(2)V9 VALUE ZEROES
               OCCURS 10 TIMES INDEXED BY WS-IDX.

       01 WS-SAISIE                 PIC X(4).
       01 WS-NOTE-SAISIE            PIC 9(2)V9.
       01 WS-SOMME                  PIC 9(4)V9 VALUE ZEROES.
       01 WS-MOYENNE                PIC 9(2)V99 VALUE ZEROES.
       01 WS-NB-DESSUS              PIC 9(2)   VALUE ZEROES.
       01 WS-SAISIE-VALIDE          PIC 9      VALUE 0.
          88 SAISIE-OK              VALUE 1.
          88 SAISIE-KO              VALUE 0.

      * Variables d'edition pour affichage
       01 WS-AFF-MOYENNE            PIC Z9.99.
       01 WS-AFF-IDX                PIC 99.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           DISPLAY "========================================"
           DISPLAY "   SAISIE DES NOTES DE LA CLASSE        "
           DISPLAY "========================================"

           PERFORM VARYING WS-IDX FROM 1 BY 1
               UNTIL WS-IDX > 10
               PERFORM 1000-SAISIR-NOTE
           END-PERFORM

           PERFORM 2000-CALCULER-MOYENNE
           PERFORM 3000-COMPTER-DESSUS-MOYENNE
           PERFORM 4000-AFFICHER-RESULTATS
           STOP RUN.

      ******************************************************************
       1000-SAISIR-NOTE.
           MOVE 0 TO WS-SAISIE-VALIDE
           MOVE WS-IDX TO WS-AFF-IDX

           PERFORM UNTIL SAISIE-OK
               DISPLAY "Note etudiant " WS-AFF-IDX
                   " (0 a 20) : " WITH NO ADVANCING
               ACCEPT WS-SAISIE

               MOVE FUNCTION NUMVAL(WS-SAISIE) TO WS-NOTE-SAISIE

               IF WS-NOTE-SAISIE >= 0 AND WS-NOTE-SAISIE <= 20
                   MOVE WS-NOTE-SAISIE TO WS-NOTE(WS-IDX)
                   SET SAISIE-OK TO TRUE
               ELSE
                   DISPLAY "  --> Valeur invalide."
                       " Saisissez un nombre entre 0 et 20."
               END-IF
           END-PERFORM.

      ******************************************************************
       2000-CALCULER-MOYENNE.
           MOVE ZEROES TO WS-SOMME
           PERFORM VARYING WS-IDX FROM 1 BY 1
               UNTIL WS-IDX > 10
               ADD WS-NOTE(WS-IDX) TO WS-SOMME
           END-PERFORM
           COMPUTE WS-MOYENNE = WS-SOMME / 10.

      ******************************************************************
       3000-COMPTER-DESSUS-MOYENNE.
           MOVE ZEROES TO WS-NB-DESSUS
           PERFORM VARYING WS-IDX FROM 1 BY 1
               UNTIL WS-IDX > 10
               IF WS-NOTE(WS-IDX) > WS-MOYENNE
                   ADD 1 TO WS-NB-DESSUS
               END-IF
           END-PERFORM.

      ******************************************************************
       4000-AFFICHER-RESULTATS.
           MOVE WS-MOYENNE TO WS-AFF-MOYENNE
           DISPLAY SPACES
           DISPLAY "========================================"
           DISPLAY "          RESULTATS DE LA CLASSE        "
           DISPLAY "========================================"
           DISPLAY SPACES

           PERFORM VARYING WS-IDX FROM 1 BY 1
               UNTIL WS-IDX > 10
               MOVE WS-IDX TO WS-AFF-IDX
               DISPLAY "  Etudiant " WS-AFF-IDX
                   " : " WS-NOTE(WS-IDX)
           END-PERFORM

           DISPLAY SPACES
           DISPLAY "  Moyenne de la classe     : "
               WS-AFF-MOYENNE
           DISPLAY "  Etudiants > moyenne      : "
               WS-NB-DESSUS " / 10".

       END PROGRAM NOTES-ETUDIANTS.
