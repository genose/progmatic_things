      ******************************************************************
      * Author:
      * Date:
      * Purpose:
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. YOUR-PROGRAM-NAME.
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
               DISPLAY 'Vous êtes une femme'
           ELSE IF HOMME
               DISPLAY 'Vous êtes un homme'
           ELSE
               DISPLAY 'NE SAIS PAS '
           END-IF.

           DISPLAY  WS-GENRE


           STOP RUN.
       END PROGRAM YOUR-PROGRAM-NAME.
