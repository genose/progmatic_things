      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   FUSION DE DEUX FICHIERS EN UN TROISIEME
      *            clients_A.txt + clients_B.txt -> clients_merged.txt
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. FUSION-FICHIERS.

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT CLIENTS-A
               ASSIGN TO "clients_A.txt"
               ORGANIZATION IS SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-STATUS-A.

           SELECT CLIENTS-B
               ASSIGN TO "clients_B.txt"
               ORGANIZATION IS SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-STATUS-B.

           SELECT CLIENTS-MERGED
               ASSIGN TO "clients_merged.txt"
               ORGANIZATION IS SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-STATUS-OUT.

       DATA DIVISION.
       FILE SECTION.

       FD CLIENTS-A.
       01 CLIENT-A-RECORD           PIC X(80).

       FD CLIENTS-B.
       01 CLIENT-B-RECORD           PIC X(80).

       FD CLIENTS-MERGED.
       01 CLIENT-MERGED-RECORD      PIC X(80).

       WORKING-STORAGE SECTION.
       01 WS-STATUS-A               PIC XX.
       01 WS-STATUS-B               PIC XX.
       01 WS-STATUS-OUT             PIC XX.

       01 WS-EOF-A                  PIC 9 VALUE 0.
          88 EOF-A                  VALUE 1.
       01 WS-EOF-B                  PIC 9 VALUE 0.
          88 EOF-B                  VALUE 1.

       01 WS-NB-A                   PIC 9(6) VALUE ZEROES.
       01 WS-NB-B                   PIC 9(6) VALUE ZEROES.
       01 WS-NB-TOTAL               PIC 9(6) VALUE ZEROES.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           OPEN INPUT  CLIENTS-A
           IF WS-STATUS-A NOT = "00"
               DISPLAY "ERREUR OUVERTURE clients_A.txt : " WS-STATUS-A
               STOP RUN
           END-IF

           OPEN INPUT  CLIENTS-B
           IF WS-STATUS-B NOT = "00"
               DISPLAY "ERREUR OUVERTURE clients_B.txt : " WS-STATUS-B
               CLOSE CLIENTS-A
               STOP RUN
           END-IF

           OPEN OUTPUT CLIENTS-MERGED
           IF WS-STATUS-OUT NOT = "00"
               DISPLAY "ERREUR OUVERTURE clients_merged.txt : "
                   WS-STATUS-OUT
               CLOSE CLIENTS-A
               CLOSE CLIENTS-B
               STOP RUN
           END-IF

           PERFORM 1000-VIDER-FICHIER-A
           PERFORM 2000-VIDER-FICHIER-B

           CLOSE CLIENTS-A
           CLOSE CLIENTS-B
           CLOSE CLIENTS-MERGED

           PERFORM 3000-AFFICHER-BILAN
           STOP RUN.

      ******************************************************************
       1000-VIDER-FICHIER-A.
           PERFORM UNTIL EOF-A
               READ CLIENTS-A INTO CLIENT-A-RECORD
                   AT END
                       SET EOF-A TO TRUE
                   NOT AT END
                       WRITE CLIENT-MERGED-RECORD FROM CLIENT-A-RECORD
                       IF WS-STATUS-OUT = "00"
                           ADD 1 TO WS-NB-A
                           ADD 1 TO WS-NB-TOTAL
                       ELSE
                           DISPLAY "ERREUR ECRITURE (A) : "
                               WS-STATUS-OUT
                       END-IF
               END-READ
           END-PERFORM.

      ******************************************************************
       2000-VIDER-FICHIER-B.
           PERFORM UNTIL EOF-B
               READ CLIENTS-B INTO CLIENT-B-RECORD
                   AT END
                       SET EOF-B TO TRUE
                   NOT AT END
                       WRITE CLIENT-MERGED-RECORD FROM CLIENT-B-RECORD
                       IF WS-STATUS-OUT = "00"
                           ADD 1 TO WS-NB-B
                           ADD 1 TO WS-NB-TOTAL
                       ELSE
                           DISPLAY "ERREUR ECRITURE (B) : "
                               WS-STATUS-OUT
                       END-IF
               END-READ
           END-PERFORM.

      ******************************************************************
       3000-AFFICHER-BILAN.
           DISPLAY SPACES
           DISPLAY "========================================"
           DISPLAY "   BILAN DE LA FUSION                   "
           DISPLAY "========================================"
           DISPLAY "  Lignes de clients_A.txt  : " WS-NB-A
           DISPLAY "  Lignes de clients_B.txt  : " WS-NB-B
           DISPLAY "  Total ecrit dans merged  : " WS-NB-TOTAL.

       END PROGRAM FUSION-FICHIERS.
