      *================================================================*
      * PROGRAMME  : TABLEAU-PUSH                                      *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.0                                               *
      *----------------------------------------------------------------*
      * DESCRIPTION: Push : ajout dynamique en fin de tableau          *
      *----------------------------------------------------------------*
      * GROUPE     : arrays                                            *
      * COMPILEUR  : GnuCOBOL â cobc -x                                *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. TABLEAU-PUSH.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
       01  WS-TABLEAU-PUSH.
            05 WS-VALEUR-PUSH PIC 9(3) VALUE ZEROES
               OCCURS 7 TIMES INDEXED BY WS-INDEX-PUSH.


       01  WS-INDEX-TMP-PUSH PIC 9(5) VALUE ZEROES.
       01  WS-VALEUR-TMP-LAST-PUSH PIC 9(3) VALUE ZEROES.

       01  WS-INDEX-OCCUR-PUSH PIC 9(3) VALUE ZEROES.

       PROCEDURE DIVISION.

       MAIN-PROCEDURE.
            DISPLAY "*********** PUSH DANS UN TABLEAU **********".
            COMPUTE WS-INDEX-OCCUR-PUSH =
             LENGTH OF WS-TABLEAU-PUSH / LENGTH OF WS-VALEUR-PUSH

           DISPLAY " Index PUSH :: " WS-INDEX-OCCUR-PUSH

            DISPLAY "*********** INITIALISATION DU TABLEAU **********".
            PERFORM VARYING WS-INDEX-PUSH FROM 1 BY 1
                 UNTIL WS-INDEX-PUSH > WS-INDEX-OCCUR-PUSH
                 COMPUTE WS-INDEX-TMP-PUSH = WS-INDEX-PUSH * 5
                 MOVE WS-INDEX-TMP-PUSH to WS-VALEUR-PUSH(WS-INDEX-PUSH)
                 DISPLAY "Valeur de l'ÃÂ©lÃÂ©ment " WS-INDEX-PUSH ": "
                  WS-VALEUR-PUSH(WS-INDEX-PUSH)
           END-PERFORM

           MOVE WS-VALEUR-PUSH(WS-INDEX-OCCUR-PUSH)
                TO WS-VALEUR-TMP-LAST-PUSH


           DISPLAY " Index PUSH :: " WS-INDEX-OCCUR-PUSH
           DISPLAY " ELEMENT A PUSHER DANS LE TABLEAU :: "
               WS-VALEUR-TMP-LAST-PUSH

      * ** BUFFER LIFE CYCLE ** *
           DISPLAY "*********** PUSH DANS LE TABLEAU **********".
           PERFORM VARYING WS-INDEX-PUSH FROM WS-INDEX-OCCUR-PUSH BY -1
                 UNTIL WS-INDEX-PUSH < 2

               COMPUTE WS-INDEX-TMP-PUSH = WS-INDEX-PUSH - 1
               DISPLAY "      ************************    "
               DISPLAY "-1- Valeur de l'Element "
               WS-INDEX-PUSH " FROM " WS-INDEX-TMP-PUSH
               " : OLD :" WS-VALEUR-PUSH(WS-INDEX-TMP-PUSH)
               " : NEW : " WS-VALEUR-PUSH(WS-INDEX-PUSH)

               MOVE WS-VALEUR-PUSH(WS-INDEX-TMP-PUSH)
                   TO  WS-VALEUR-PUSH(WS-INDEX-PUSH)

               DISPLAY "*2* Valeur de l'Element " WS-INDEX-PUSH
               " : OLD :" WS-VALEUR-PUSH(WS-INDEX-TMP-PUSH)
               " : NEW : " WS-VALEUR-PUSH(WS-INDEX-PUSH)

           END-PERFORM
           DISPLAY "*********** PUSH DE LA VALEUR **********".
           DISPLAY "Valeur de l'ÃÂ©lÃÂ©ment " WS-INDEX-PUSH ": "
                  WS-VALEUR-TMP-LAST-PUSH
           MOVE WS-VALEUR-TMP-LAST-PUSH TO WS-VALEUR-PUSH(1)
           DISPLAY "Valeur de l'ÃÂ©lÃÂ©ment 1: "
                  WS-VALEUR-PUSH(1)



           DISPLAY "*********** AFFICHAGE DES VALEURS ***********".
           PERFORM VARYING WS-INDEX-PUSH FROM 1 BY 1
                 UNTIL WS-INDEX-PUSH > WS-INDEX-OCCUR-PUSH
                 DISPLAY "Valeur de l'ÃÂ©lÃÂ©ment " WS-INDEX-PUSH ": "
                  WS-VALEUR-PUSH(WS-INDEX-PUSH)
           END-PERFORM

            STOP RUN.
       END PROGRAM YOUR-PROGRAM-NAME.
