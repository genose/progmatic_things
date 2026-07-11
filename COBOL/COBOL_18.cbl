      ******************************************************************
      * Author: COTILLARD SEBASTIEN
      * Date: 03-07-2026
      * Purpose: FUSION DE DEUX TABLEAU DANS UN TROISIEME TABLEAU
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. TABLEAU-FUSION.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
       01 WS-TABLEAU-1 .
           05 WS-VALEUR-1 PIC 9(3) VALUE ZEROES
           OCCURS 5 TIMES INDEXED BY WS-INDEX-1.

       01 WS-TABLEAU-2.
           05 WS-VALEUR-2 PIC 9(3) VALUE ZEROES
            OCCURS 5 TIMES INDEXED BY WS-INDEX-2.

       01 WS-TABLEAU-FUSION.
           05 WS-VALEUR-FUSION PIC 9(3) VALUE ZEROES
           OCCURS 10 TIMES INDEXED BY WS-INDEX-FUSION.

       01  WS-INDEX-TMP PIC 9(5) VALUE ZEROES.
       01  WS-INDEX-OCCUR-1 PIC 9(3) VALUE ZEROES.
       01  WS-INDEX-OCCUR-2 PIC 9(3) VALUE ZEROES.
       01  WS-INDEX-OCCUR-FUSION PIC 9(3) VALUE ZEROES.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
            DISPLAY "********** FUSION DE DEUX TABLEAUX **********".
            COMPUTE WS-INDEX-OCCUR-1 =
             LENGTH OF WS-TABLEAU-1 / LENGTH OF WS-VALEUR-1

             DISPLAY " Index 1 :: " WS-INDEX-OCCUR-1

            COMPUTE WS-INDEX-OCCUR-2 =
             LENGTH OF WS-TABLEAU-2 / LENGTH OF WS-VALEUR-2

               DISPLAY " Index 2 :: " WS-INDEX-OCCUR-2

            COMPUTE WS-INDEX-OCCUR-FUSION =
             LENGTH OF WS-TABLEAU-FUSION / LENGTH OF WS-VALEUR-FUSION

              DISPLAY " Index FUSION :: " WS-INDEX-OCCUR-FUSION

            DISPLAY "*********** INITIALISATION DES TABLEAUX **********".
            PERFORM VARYING WS-INDEX-1 FROM 1 BY 1
                 UNTIL WS-INDEX-1 > WS-INDEX-OCCUR-1

                 COMPUTE WS-INDEX-TMP = WS-INDEX-1 * 5
                 MOVE WS-INDEX-TMP to WS-VALEUR-1(WS-INDEX-1)
            END-PERFORM

            PERFORM VARYING WS-INDEX-2 FROM 1 BY 1
                 UNTIL WS-INDEX-2 > WS-INDEX-OCCUR-2

                 COMPUTE WS-INDEX-TMP = WS-INDEX-2 * 10
                 MOVE WS-INDEX-TMP to WS-VALEUR-2(WS-INDEX-2)
            END-PERFORM
      
            DISPLAY " VISUALISATION DES TABLEAUX AVANT FUSION ".

            PERFORM VARYING WS-INDEX-1 FROM 1 BY 1
                 UNTIL WS-INDEX-1 > WS-INDEX-OCCUR-1
                 DISPLAY " ELEMENT TABLEAU 1: "
                 WS-VALEUR-1(WS-INDEX-1)
                 " ELEMENT TABLEAU 2: " WS-VALEUR-2(WS-INDEX-1)
            END-PERFORM
            
            DISPLAY "*********** FUSION DES TABLEAUX **********".

            DISPLAY " FUSION DU TABLEAU 1 DANS LE TABLEAU FUSION ".
            PERFORM VARYING WS-INDEX-1 FROM 1 BY 1
                 UNTIL WS-INDEX-1 > WS-INDEX-OCCUR-1
                 DISPLAY " INDEX 1 :: " WS-INDEX-1
                 DISPLAY " INDEX FUSION :: " WS-INDEX-1
                 DISPLAY " ELEMENT TABLEAU 1: "
                 WS-VALEUR-1(WS-INDEX-1)
                 MOVE WS-VALEUR-1(WS-INDEX-1) TO
                  WS-VALEUR-FUSION(WS-INDEX-1)
            END-PERFORM
            DISPLAY " FUSION DU TABLEAU 2 DANS LE TABLEAU FUSION ".
               PERFORM VARYING WS-INDEX-2 FROM 1 BY 1
                     UNTIL WS-INDEX-2 > WS-INDEX-OCCUR-2
                 COMPUTE WS-INDEX-TMP = WS-INDEX-OCCUR-1 + WS-INDEX-2
                DISPLAY " INDEX 2 :: " WS-INDEX-2     
                DISPLAY " INDEX FUSION :: " WS-INDEX-TMP
                DISPLAY " ELEMENT TABLEAU 2: " WS-VALEUR-2(WS-INDEX-2)

                     MOVE WS-VALEUR-2(WS-INDEX-2) TO
                    WS-VALEUR-FUSION(WS-INDEX-TMP)
               END-PERFORM


            DISPLAY "**** AFFICHAGE DES VALEURS FUSIONNEES ****".
            PERFORM VARYING WS-INDEX-FUSION FROM 1 BY 1
                 UNTIL WS-INDEX-FUSION > WS-INDEX-OCCUR-FUSION
                 DISPLAY "Valeur de l'élément " WS-INDEX-FUSION ": "
                  WS-VALEUR-FUSION(WS-INDEX-FUSION)
            END-PERFORM


            STOP RUN.
       END PROGRAM TABLEAU-FUSION.
