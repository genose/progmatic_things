      *================================================================*
      * PROGRAMME  : TEST-MULTIPLY                                     *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.0                                               *
      *----------------------------------------------------------------*
      * DESCRIPTION: COMPUTE GIVING - multiplication, total HT et TVA  *
      *----------------------------------------------------------------*
      * GROUPE     : basics                                            *
      * COMPILEUR  : GnuCOBOL - cobc -x                                *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. TEST-MULTIPLY.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
       01 NOMBRE-A PIC 9(3) VALUE 127.
       01 NOMBRE-B PIC 9(3) VALUES 58.
       01 NOMBRE-RESULTAT PIC X9(3).
       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
            DISPLAY "***** TEST MULTIPLY *****"

            COMPUTE NOMBRE-RESULTAT = NOMBRE-A * NOMBRE-B.



            STOP RUN.


       END PROGRAM TEST-MULTIPLY.
