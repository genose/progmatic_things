      *================================================================*
      * PROGRAMME  : TABLEAU-INIT                                      *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.0                                               *
      *----------------------------------------------------------------*
      * DESCRIPTION: Initialisation et parcours de tableau PERFORM     *
      *----------------------------------------------------------------*
      * GROUPE     : arrays                                            *
      * COMPILEUR  : GnuCOBOL - cobc -x                                *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. TABLEAU-INIT.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
       01 WS-TABLEAU OCCURS 5 TIMES.
           05 WS-VALEUR PIC 9(3) VALUE ZEROES.

       01   WS-INDEX PIC 9.
       01  WS-SOMME PIC 9(3)  VALUE ZEROES.
       01  WS-AVERAGE PIC Z(5).99  VALUE ZEROES.
       PROCEDURE DIVISION.
       MAIN-PROCEDURE.

      * ** Utilisez une boucle PERFORM VARYING
      * ** Pour parcourir le tableau. Pour chaque Iteration:
      * ** a. Affichez une Invite demandant a l'utilisateur
      * ** d'entrer une valeur a Inserer dans le tableau.
      * ** D. Lisez la valeur salsie par l'utilisateur
      * ** et stockez-la dans AA  l'indice
            PERFORM VARYING WS-INDEX FROM 1 BY 1 UNTIL WS-INDEX > 5
           DISPLAY "Entrez une valeur pour l'element " WITH NO ADVANCING
           DISPLAY WS-INDEX ": " WITH NO ADVANCING

            ACCEPT WS-VALEUR(WS-INDEX)
              COMPUTE WS-SOMME = WS-VALEUR(WS-INDEX) + WS-SOMME
            END-PERFORM

               COMPUTE WS-AVERAGE =  WS-SOMME / WS-INDEX .

               DISPLAY "La somme des valeurs est : " WS-SOMME
               DISPLAY "La moyenne des valeurs est : " WS-AVERAGE

            STOP RUN.
       END PROGRAM YOUR-PROGRAM-NAME.
