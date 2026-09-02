      *================================================================*
      * PROGRAMME  : INSPECT-TALLY                                     *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.0                                               *
      *----------------------------------------------------------------*
      * DESCRIPTION: INSPECT TALLYING : comptage d occurrences de car. *
      *----------------------------------------------------------------*
      * GROUPE     : strings                                           *
      * COMPILEUR  : GnuCOBOL - cobc -x                                *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. INSPECT-TALLY.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
       01 CHAINE-SOURCE PIC X(32) VALUE "aie, aie, j'' mal au poing".

       01 LISTE-CARACTERE.
           05 CARACTERE-A PIC X VALUE "a".
           05 CARACTERE-E PIC X VALUE "e".
           05 CARACTERE-I PIC X VALUE "i".
           05 CARACTERE-O PIC X VALUE "O".
           05 CARACTERE-U PIC X VALUE "U".

       01 LISTE-COMPTEUR.
           05 COMPTEUR-A PIC 99 VALUE ZERO.
           05 COMPTEUR-E PIC 99 VALUE ZERO.
           05 COMPTEUR-I PIC 99 VALUE ZERO.
           05 COMPTEUR-O PIC 99 VALUE ZERO.
           05 COMPTEUR-U PIC 99 VALUE ZERO.


       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
            DISPLAY "Hello world"


           INSPECT CHAINE-SOURCE TALLYING COMPTEUR-A FOR ALL CARACTERE-A.
           INSPECT CHAINE-SOURCE TALLYING COMPTEUR-E FOR ALL CARACTERE-E.
           INSPECT CHAINE-SOURCE TALLYING COMPTEUR-I FOR ALL CARACTERE-I.
           INSPECT CHAINE-SOURCE TALLYING COMPTEUR-O FOR ALL CARACTERE-O.
           INSPECT CHAINE-SOURCE TALLYING COMPTEUR-U FOR ALL CARACTERE-U.


            DISPLAY "COMPTEUR A " COMPTEUR-A.
            DISPLAY "COMPTEUR E " COMPTEUR-E.
            DISPLAY "COMPTEUR I " COMPTEUR-I.
            DISPLAY "COMPTEUR O " COMPTEUR-O.
            DISPLAY "COMPTEUR U " COMPTEUR-U.
            STOP RUN.
       END PROGRAM YOUR-PROGRAM-NAME.
