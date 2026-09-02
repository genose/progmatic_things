      *================================================================*
      * PROGRAMME  : HELLO-VARS                                        *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.0                                               *
      *----------------------------------------------------------------*
      * DESCRIPTION: Variables numeriques/alphanumeriques et DISPLAY   *
      *----------------------------------------------------------------*
      * GROUPE     : basics                                            *
      * COMPILEUR  : GnuCOBOL - cobc -x                                *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. HELLO-VARS.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
           01 WS-ART1 PIC 9(3)V9(3) VALUE 21.
           01 WS-ART2 PIC 9(2)V9(3) VALUE 14.
           01 WS-REMISE PIC 9(2)V99 VALUE 1.30.
           01 WS-RESULTAT PIC 9(3)V9(3) VALUE 0.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
            DISPLAY "Hello world"

           DIVIDE WS-ART1 BY WS-REMISE GIVING WS-RESULTAT.
            DISPLAY WS-RESULTAT.

           DIVIDE WS-ART2 BY WS-REMISE GIVING WS-RESULTAT.
            DISPLAY WS-RESULTAT.

            STOP RUN.
       END PROGRAM YOUR-PROGRAM-NAME.
