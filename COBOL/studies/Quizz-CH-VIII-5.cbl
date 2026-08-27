      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   FILTRAGE - COPIE DES FACTURES > 1000
      *            Lecture factures.txt, ecriture factures_grandes.txt
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. FILTRE-FACTURES.

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT FACTURES-IN
               ASSIGN TO "factures.txt"
               ORGANIZATION IS LINE SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-STATUS-IN.

           SELECT FACTURES-OUT
               ASSIGN TO "factures_grandes.txt"
               ORGANIZATION IS LINE SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-STATUS-OUT.

       DATA DIVISION.
       FILE SECTION.

       FD FACTURES-IN.
       01 FACTURE-IN-RECORD.
           05 FIN-NUMERO            PIC X(6).
           05 FILLER                PIC X(1).
           05 FIN-CLIENT            PIC X(20).
           05 FILLER                PIC X(1).
           05 FIN-MONTANT           PIC 9(9)V99.

       FD FACTURES-OUT.
       01 FACTURE-OUT-RECORD.
           05 FOUT-NUMERO           PIC X(6).
           05 FILLER                PIC X(1) VALUE SPACE.
           05 FOUT-CLIENT           PIC X(20).
           05 FILLER                PIC X(1) VALUE SPACE.
           05 FOUT-MONTANT          PIC 9(9)V99.

       WORKING-STORAGE SECTION.
       01 WS-STATUS-IN              PIC XX.
       01 WS-STATUS-OUT             PIC XX.
       01 WS-EOF                    PIC 9 VALUE 0.
          88 EOF-FACTURES           VALUE 1.

       01 WS-NB-ECRITES             PIC 9(6) VALUE ZEROES.
       01 WS-NB-IGNOREES            PIC 9(6) VALUE ZEROES.
       01 WS-NB-TOTAL               PIC 9(6) VALUE ZEROES.

       01 WS-AFF-MONTANT            PIC ZZZ,ZZZ,ZZ9.99.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           OPEN INPUT FACTURES-IN
           IF WS-STATUS-IN NOT = "00"
               DISPLAY "ERREUR OUVERTURE factures.txt : " WS-STATUS-IN
               STOP RUN
           END-IF

           OPEN OUTPUT FACTURES-OUT
           IF WS-STATUS-OUT NOT = "00"
               DISPLAY "ERREUR OUVERTURE factures_grandes.txt : "
                   WS-STATUS-OUT
               CLOSE FACTURES-IN
               STOP RUN
           END-IF

           PERFORM UNTIL EOF-FACTURES
               READ FACTURES-IN
                   AT END
                       SET EOF-FACTURES TO TRUE
                   NOT AT END
                       PERFORM 1000-FILTRER-FACTURE
               END-READ
           END-PERFORM

           CLOSE FACTURES-IN
           CLOSE FACTURES-OUT

           PERFORM 2000-AFFICHER-BILAN
           STOP RUN.

      ******************************************************************
       1000-FILTRER-FACTURE.
           ADD 1 TO WS-NB-TOTAL
           IF FIN-MONTANT > 1000
               MOVE FIN-NUMERO  TO FOUT-NUMERO
               MOVE FIN-CLIENT  TO FOUT-CLIENT
               MOVE FIN-MONTANT TO FOUT-MONTANT
               WRITE FACTURE-OUT-RECORD
               IF WS-STATUS-OUT NOT = "00"
                   DISPLAY "ERREUR ECRITURE facture " FIN-NUMERO
                       " : " WS-STATUS-OUT
               ELSE
                   ADD 1 TO WS-NB-ECRITES
               END-IF
           ELSE
               ADD 1 TO WS-NB-IGNOREES
           END-IF.

      ******************************************************************
       2000-AFFICHER-BILAN.
           DISPLAY SPACES
           DISPLAY "========================================"
           DISPLAY "   BILAN DU FILTRAGE                    "
           DISPLAY "========================================"
           DISPLAY "  Factures lues     : " WS-NB-TOTAL
           DISPLAY "  Factures ecrites  : " WS-NB-ECRITES
               " (montant > 1000)"
           DISPLAY "  Factures ignorees : " WS-NB-IGNOREES
               " (montant <= 1000)".

       END PROGRAM FILTRE-FACTURES.
