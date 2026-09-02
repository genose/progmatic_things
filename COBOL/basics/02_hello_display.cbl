      *================================================================*
      * PROGRAMME  : HELLO-DISPLAY                                     *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.0                                               *
      *----------------------------------------------------------------*
      * DESCRIPTION: DISPLAY simple â affichage de messages formates   *
      *----------------------------------------------------------------*
      * GROUPE     : basics                                            *
      * COMPILEUR  : GnuCOBOL â cobc -x                                *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. HELLO-DISPLAY.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
           01  WS-COMPTEUR PIC 9(3) COMP-3 VALUE 0.
           01  WS-EXEMPLE PIC X(10) VALUE "EXEMPLE".

           01  WS-NOMBRE PIC S9(6)V99 VALUE -130.51.

           01 WS-EMPLOYEE.
               05    NOM PIC X(10) VALUE LOW-VALUE.
               05    PRENOM  PIC X(10) VALUE LOW-VALUE.
               05    AGE PIC 9(2) VALUE LOW-VALUE.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.

                      move ALL '*' TO  PRENOM.

           INITIALIZE WS-EMPLOYEE
               REPLACING ALPHANUMERIC BY LOW-VALUE.




            DISPLAY "Hello world"
            STOP RUN.
       END PROGRAM YOUR-PROGRAM-NAME.
