      ******************************************************************
      * Author:
      * Date:
      * Purpose:
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. YOUR-PROGRAM-NAME.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 GROUPE-SOURCE.
           05 DONNEE-SOURCE PIC X(25) VALUE "DONNEE A DEPLACER".

       01 GROUPE-DESTINATION.
           05 DONNEE-DESTINATION PIC X(25) VALUE SPACES.

       PROCEDURE DIVISION.
           DISPLAY 'Avant MOVE CORRESPONDING:' GROUPE-DESTINATION
           MOVE GROUPE-SOURCE TO GROUPE-DESTINATION
           DISPLAY 'Apres MOVE CORRESPONDING:"' GROUPE-DESTINATION "'"
           STOP RUN.
       END PROGRAM YOUR-PROGRAM-NAME.
