      *================================================================*
      * PROGRAMME  : TABLEAU-POP                                       *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.0                                               *
      *----------------------------------------------------------------*
      * DESCRIPTION: Suppression et extraction d element par index (pop)
      *----------------------------------------------------------------*
      * GROUPE     : arrays                                            *
      * COMPILEUR  : GnuCOBOL - cobc -x                                *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. TABLEAU-POP.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
       01  WS-TABLEAU-DELETE.
            05 WS-VALEUR PIC 9(3) VALUE ZEROES
               OCCURS 6 TIMES INDEXED BY WS-INDEX.

       01  WS-READ-TMP PIC 9(3) VALUE ZEROES.
       01  WS-INDEX-TMP-DELETE PIC 9(5) VALUE ZEROES.
       01  WS-INDEX-TO-DELETE PIC 9(3) VALUE ZEROES.
       01  WS-INDEX-OCCUR PIC 9(3) VALUE ZEROES.
       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
            DISPLAY "*** DELETE INDEX AND POP DANS UN TABLEAU ***".

      * **      PERFORM 10000-INITIALISATION-TABLEAU.
            PERFORM 50000-AFFICHAGE-VALEUR.
            PERFORM 60000-SAISIE-INDEX-TO-DELETE.
            PERFORM 70000-DELETE-INDEX.
            PERFORM 50000-AFFICHAGE-VALEUR.
            PERFORM 111111-EXIT-PROGRAM.

       111111-EXIT-PROGRAM.
            DISPLAY "*********** EXIT PROGRAM **********".
            STOP RUN.
       10000-INITIALISATION-TABLEAU.

            COMPUTE WS-INDEX-OCCUR =
                 LENGTH OF WS-TABLEAU-DELETE
                 / LENGTH OF WS-VALEUR.
            DISPLAY " Index OCCUR :: " WS-INDEX-OCCUR
            DISPLAY "************ INITIALISATION DU TABLEAU **********".
            PERFORM VARYING WS-INDEX FROM 1 BY 1
              UNTIL WS-INDEX > WS-INDEX-OCCUR
                DISPLAY " ****** "
                DISPLAY " Saisir la valeur de l'Element "
                  WS-INDEX ": "
                ACCEPT WS-READ-TMP
                MOVE WS-READ-TMP TO WS-VALEUR(WS-INDEX)
                DISPLAY "Valeur de l'AlAment " WS-INDEX ": "
                 WS-VALEUR(WS-INDEX)

           END-PERFORM
           GOBACK.

       50000-AFFICHAGE-VALEUR.
            DISPLAY "*********** AFFICHAGE DU TABLEAU **********".
            PERFORM VARYING WS-INDEX FROM 1 BY 1
                  UNTIL WS-INDEX > WS-INDEX-OCCUR
                DISPLAY "      ************************    "
                DISPLAY "-1- Valeur de l'Element "
                WS-INDEX " : "
                WS-VALEUR(WS-INDEX)
            END-PERFORM
            GOBACK.

       60000-SAISIE-INDEX-TO-DELETE.
            DISPLAY "*********** SAISIE DE L'INDEX A DELETE **********".
            DISPLAY "Saisir l'index AA  supprimer : "
            ACCEPT WS-INDEX-TO-DELETE
            GOBACK.

       70000-DELETE-INDEX.
            DISPLAY "**** DELETE INDEX AND POP DANS LE TABLEAU ***"

      * ** BUFFER LIFE CYCLE ** *
           DISPLAY "*********** DELETE INDEX DANS LE TABLEAU **********".
           PERFORM VARYING WS-INDEX FROM WS-INDEX-TO-DELETE BY -1
                 UNTIL WS-INDEX > 1
               DISPLAY "      ************************    "
               DISPLAY "-1- Valeur de l'Element "
               WS-INDEX " << " WS-INDEX-TMP-DELETE
               " OLD : " WS-VALEUR(WS-INDEX)
               " NEW : " WS-VALEUR(WS-INDEX-TMP-DELETE)

               COMPUTE WS-INDEX-TMP-DELETE = WS-INDEX + 1
               MOVE WS-VALEUR(WS-INDEX-TMP-DELETE)
                   TO  WS-VALEUR(WS-INDEX)
               DISPLAY "*2* Valeur de l'Element " WS-INDEX
               " : OLD :" WS-VALEUR(WS-INDEX-TMP-DELETE)
               " : NEW : " WS-VALEUR(WS-INDEX)
           END-PERFORM
           GOBACK.

       END PROGRAM YOUR-PROGRAM-NAME.
