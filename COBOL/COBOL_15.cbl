      ******************************************************************
      * Author: COTILLARD SEBASTIEN
      * Date: 30-06-2026
      * Purpose:
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. ADDITION-TABLEAU.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
       01 WS-TABLEAU .
           05 WS-VALEUR PIC 9(3) VALUE ZEROES
           OCCURS 5 TIMES INDEXED BY WS-INDEX.
       01 WS-INDEX-TMP PIC 9(5) VALUE ZEROES.
       01  WS-INDEX-OCCUR PIC 9(3) VALUE ZEROES.
       01 WS-SOMME-VALEURS PIC 9(3) VALUE ZEROES.
       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
            COMPUTE WS-INDEX-OCCUR =
             LENGTH OF WS-TABLEAU / LENGTH OF WS-VALEUR.

            DISPLAY "********** ADDITION-TABLEAU **********".
               PERFORM VARYING WS-INDEX FROM 1 BY 1
                     UNTIL WS-INDEX > WS-INDEX-OCCUR
                COMPUTE WS-INDEX-TMP = WS-INDEX * 10
                COMPUTE WS-SOMME-VALEURS =
                 WS-SOMME-VALEURS + WS-INDEX-TMP
                MOVE WS-INDEX-TMP TO WS-VALEUR(WS-INDEX)
                DISPLAY "." with no advancing
               END-PERFORM
           display "".
               DISPLAY "*********** AFFICHAGE DES VALEURS ***********".
               DISPLAY "Somme des valeurs: " WS-SOMME-VALEURS
               DISPLAY "Valeurs du tableau: "
               PERFORM VARYING WS-INDEX FROM 1 BY 1
                     UNTIL WS-INDEX > WS-INDEX-OCCUR
                DISPLAY "Valeur de l'élément " WS-INDEX ": "
                  WS-VALEUR(WS-INDEX)
               END-PERFORM
            STOP RUN.
       END PROGRAM ADDITION-TABLEAU.
