      ******************************************************************
      * Author:
      * Date:
      * Purpose:
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. TABLEAU-ENTIER.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
       01  WS-TABLEAU-ENTIERS.
             10 WS-VALEUR PIC 9(3)  OCCURS 10 TIMES.

       01  WS-INDEX PIC 99 VALUE ZEROES.
       01  WS-INDEX-OCCUR PIC 9(3) VALUE ZEROES.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.

            COMPUTE WS-INDEX-OCCUR =
             LENGTH OF WS-TABLEAU-ENTIERS / LENGTH OF WS-VALEUR.

            Display "nombre d'occurences du tableau: " WS-INDEX-OCCUR
            DISPLAY "********** INITIALISATION DU TABLEAU **********".

            PERFORM VARYING WS-INDEX FROM 1 BY 1
                 UNTIL WS-INDEX > 10
                   move 5 to WS-VALEUR(WS-INDEX)
            END-PERFORM

            DISPLAY "*********** AFFICHAGE DES VALEURS ***********".
            PERFORM VARYING WS-INDEX FROM 1 BY 1
                  UNTIL WS-INDEX > 10
                 DISPLAY "Valeur de l'élément " WS-INDEX ": "
                  WS-VALEUR(WS-INDEX)
            END-PERFORM
            STOP RUN.
       END PROGRAM TABLEAU-ENTIER.
