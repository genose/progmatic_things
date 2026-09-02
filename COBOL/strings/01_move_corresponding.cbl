      *================================================================*
      * PROGRAMME  : MOVE-CORRESP                                      *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.0                                               *
      *----------------------------------------------------------------*
      * DESCRIPTION: MOVE CORRESPONDING entre groupes de donnees       *
      *----------------------------------------------------------------*
      * GROUPE     : strings                                           *
      * COMPILEUR  : GnuCOBOL - cobc -x                                *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. MOVE-CORRESP.
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
