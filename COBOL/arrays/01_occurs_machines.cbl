      *================================================================*
      * PROGRAMME  : MACHINE                                           *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.0                                               *
      *----------------------------------------------------------------*
      * DESCRIPTION: OCCURS 5 TIMES + PERFORM VARYING â tableau machines
      *----------------------------------------------------------------*
      * GROUPE     : arrays                                            *
      * COMPILEUR  : GnuCOBOL â cobc -x                                *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. MACHINE.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
       01 ATELIER.
           05 MACHINE OCCURS 5 TIMES.
               10 MACHINE-NAME PIC X(9) VALUE "MACHINE ".
               10 MACHINE-NUMERO PIC 9 VALUE ZEROES.
               10 MACHINE-ETAT PIC X VALUE "A".
               10 MACHINE-TEMPERATURE PIC 9(3)V99 VALUE ZEROES.
               10 MACHINE-POIDS PIC 9(3)V99 VALUE ZEROES.
               10 MACHINE-HAUTEUR PIC 9(3)V99 VALUE ZEROES.
               10 MACHINE-LARGEUR PIC 9(3)V99 VALUE ZEROES.
               10 MACHINE-PROFONDEUR PIC 9(3)V99 VALUE ZEROES.
               10 MACHINE-COULEUR PIC X(9) VALUE "COULEUR ".
               10 MACHINE-MARQUE PIC X(9) VALUE "MARQUE ".
               10 MACHINE-MODELE PIC X(9) VALUE "MODELE ".
               10 MACHINE-ANNEE PIC 9(4) VALUE ZEROES.

       01 WS-INDEX PIC 9 VALUE ZEROES.
       01 WS-TEMPERATURE-CONSIGNE PIC 9(3)v99 VALUE 35.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
              PERFORM VARYING WS-INDEX FROM 1 BY 1 UNTIL WS-INDEX > 5

                    MOVE WS-INDEX TO MACHINE-NUMERO(WS-INDEX)
                    MOVE "A" TO MACHINE-ETAT(WS-INDEX)
                    MOVE WS-TEMPERATURE-CONSIGNE TO
                       MACHINE-TEMPERATURE(WS-INDEX)
                   ADD 5 to WS-TEMPERATURE-CONSIGNE GIVING
                           WS-TEMPERATURE-CONSIGNE
               END-PERFORM


      * **         MOVE 35 TO  MACHINE-TEMPERATURE(1)
      * **         MOVE 75 TO MACHINE-TEMPERATURE(2)
               MOVE 65 TO MACHINE-TEMPERATURE(3)
               MOVE 50 TO MACHINE-TEMPERATURE(4)
               MOVE 55 TO MACHINE-TEMPERATURE(5)

               MOVE "H" TO MACHINE-ETAT(2)

               DISPLAY "Etat des machines :"
               PERFORM VARYING WS-INDEX FROM 1 BY 1 UNTIL WS-INDEX > 5
                DISPLAY "Machine : " MACHINE-NAME(WS-INDEX)
                  with NO ADVANCING
                DISPLAY "Numero : " MACHINE-NUMERO (WS-INDEX)
                DISPLAY "Etat : " MACHINE-ETAT(WS-INDEX)
                DISPLAY "Temperature : " MACHINE-TEMPERATURE (WS-INDEX)

               IF MACHINE-ETAT(WS-INDEX)  = "H"
                DISPLAY "La machine est en panne."
               ELSE
                DISPLAY "La machine fonctionne normalement."
               END-IF
               DISPLAY "-----------------------------"

               IF MACHINE-TEMPERATURE (WS-INDEX)  > 70
                DISPLAY "La machine est en surchauffe."
               ELSE
                DISPLAY
                "La machine fonctionne a une temperature normale."
               END-IF
               DISPLAY "-----------------------------"


               END-PERFORM





            STOP RUN.
       END PROGRAM MACHINE.
