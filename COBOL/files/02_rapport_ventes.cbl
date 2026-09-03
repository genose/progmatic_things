      *================================================================*
      * PROGRAMME  : RAPPORT-VENTES                                    *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.0                                               *
      *----------------------------------------------------------------*
      * DESCRIPTION: Rapport de ventes - VENTES-LOGIQUE.dat            *
      *----------------------------------------------------------------*
      * GROUPE     : files                                             *
      * COMPILEUR  : GnuCOBOL - cobc -x                                *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. RAPPORT-VENTES.
       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT FICHIER-VENTES ASSIGN TO VENTES-LOGIQUE
           ORGANIZATION IS LINE SEQUENTIAL.

       DATA DIVISION.
       FILE SECTION.
       FD FICHIER-VENTES.
       01 ENREGISTREMENT-VENTE.
           05 DATE-VENTE PIC 9(08).
           05 MONTANT-VENTE PIC 9(05).


       WORKING-STORAGE SECTION.
       01 DATE-PRECEDENTE PIC 9(08) VALUE ZEROS.
       01 TOTAL-JOUR PIC 9(07) VALUE ZERO.
       01 FIN-DE-FICHIER PIC X VALUE 'N'.


       PROCEDURE DIVISION.
       DEBUT-PROGRAMME.
           OPEN INPUT FICHIER-VENTES
           READ FICHIER-VENTES
           AT END MOVE 'Y' TO FIN-DE-FICHIER
           END-READ
           PERFORM UNTIL FIN-DE-FICHIER = 'Y'
           IF DATE-VENTE NOT = DATE-PRECEDENTE AND
               DATE-PRECEDENTE NOT = ZEROS
              DISPLAY "Total des ventes pour ",
              DATE-PRECEDENTE, ": ", TOTAL-JOUR
              MOVE ZEROS TO TOTAL-JOUR
           END-IF
           ADD MONTANT-VENTE TO TOTAL-JOUR
           MOVE DATE-VENTE TO DATE-PRECEDENTE
           READ FICHIER-VENTES
           AT END MOVE 'Y' TO FIN-DE-FICHIER
           END-READ
           END-PERFORM
           DISPLAY "Total des ventes pour ",
           DATE-PRECEDENTE, ": ", TOTAL-JOUR
           CLOSE FICHIER-VENTES
           STOP RUN.
       END PROGRAM RAPPORT-VENTES.
