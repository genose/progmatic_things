      *================================================================*
      * PROGRAMME  : TABLEAU-SEARCH                                    *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.0                                               *
      *----------------------------------------------------------------*
      * DESCRIPTION: Recherche dans tableau avec SEARCH / INDEXED BY   *
      *----------------------------------------------------------------*
      * GROUPE     : arrays                                            *
      * COMPILEUR  : GnuCOBOL - cobc -x                                *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. TABLEAU-SEARCH.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
       01 WS-TABLEAU-ENTIERS OCCURS 8 TIMES INDEXED BY WS-INDEX.
           05 WS-VALEUR PIC 9(3) VALUE ZEROES.
       01 WS-VALEUR-RECHERCHE PIC 9(3) VALUE ZEROES.

       01 WS-VALEUR-TROUVEE-STRUCT.
            05 WS-VALEUR-TROUVEE-INDEX PIC 9(2) VALUE ZEROES.
            05 WS-VALEUR-TROUVEE-FLAG pic 9.
                 88 WS-VALEUR-TROUVEE VALUE 1.
                 88 WS-VALEUR-NON-TROUVEE VALUE 0.

       01 WS-INDEX-TMP PIC 9(2) VALUE ZEROES.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.

            MOVE 0 TO WS-VALEUR-TROUVEE-FLAG.

            DISPLAY "********** RECHERCHE DANS UN TABLEAU **********".

            PERFORM VARYING WS-INDEX FROM 1 BY 1
                 UNTIL WS-INDEX > LENGTH OF WS-TABLEAU-ENTIERS
                 COMPUTE WS-VALEUR(WS-INDEX) = WS-INDEX * 10
               END-PERFORM

            DISPLAY "VALEUR A RECHERCHER:" WITH NO ADVANCING
            ACCEPT WS-VALEUR-RECHERCHE
            PERFORM VARYING WS-INDEX FROM 1 BY 1
                 UNTIL WS-INDEX > LENGTH OF WS-TABLEAU-ENTIERS
                 IF WS-VALEUR(WS-INDEX) = WS-VALEUR-RECHERCHE
                     MOVE WS-INDEX TO WS-VALEUR-TROUVEE-INDEX
                     SET WS-VALEUR-TROUVEE TO TRUE
                     EXIT PERFORM
                 END-IF
            END-PERFORM
            DISPLAY "*********** RESULTAT DE LA RECHERCHE **********".
            IF WS-VALEUR-TROUVEE
                DISPLAY "Valeur trouvAe   l'index: "
                WS-VALEUR-TROUVEE-INDEX
            ELSE
                DISPLAY "Valeur non trouvAe dans le tableau."
            END-IF
            DISPLAY "*********** AFFICHAGE DES VALEURS ***********".
            PERFORM VARYING WS-INDEX FROM 1 BY 1
                  UNTIL WS-INDEX > LENGTH OF WS-TABLEAU-ENTIERS
                 DISPLAY "Valeur de l'AlAment " WS-INDEX ": "
                  WS-VALEUR(WS-INDEX)
            END-PERFORM

            STOP RUN.
       END PROGRAM TABLEAU-ENTIER-SEARCH.
