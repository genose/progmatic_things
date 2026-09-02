      *================================================================*
      * PROGRAMME  : CONDITIONS-IF                                     *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.0                                               *
      *----------------------------------------------------------------*
      * DESCRIPTION: IF/ELSE imbriques - conditions selon genre et age *
      *----------------------------------------------------------------*
      * GROUPE     : basics                                            *
      * COMPILEUR  : GnuCOBOL - cobc -x                                *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. CONDITIONS-IF.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
       01 WS-DEFAULT-MASQUE PIC X VALUE "*".
       01 WS-GENRE PIC X.
           88 FEMME VALUE 'F'.
           88 HOMME VALUE 'H'.
       PROCEDURE DIVISION.
      *	   SET HOMME TO TRUE
           IF FEMME
               DISPLAY 'Vous tes une femme'
           ELSE IF HOMME
               DISPLAY 'Vous tes un homme'
           ELSE
               DISPLAY 'NE SAIS PAS '
           END-IF.

           DISPLAY  WS-GENRE


           STOP RUN.
       END PROGRAM YOUR-PROGRAM-NAME.
