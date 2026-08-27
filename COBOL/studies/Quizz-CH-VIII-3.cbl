      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   COPIE D'UN FICHIER SEQUENTIEL VERS UN AUTRE
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. COPIE-FICHIER.

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT CLIENTS-IN
               ASSIGN TO "clients_in.txt"
               ORGANIZATION IS LINE SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-STATUS-IN.

           SELECT CLIENTS-OUT
               ASSIGN TO "clients_out.txt"
               ORGANIZATION IS LINE SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-STATUS-OUT.

       DATA DIVISION.
       FILE SECTION.

       FD CLIENTS-IN.
       01 CLIENT-IN-RECORD          PIC X(100).

       FD CLIENTS-OUT.
       01 CLIENT-OUT-RECORD         PIC X(100).

       WORKING-STORAGE SECTION.
       01 WS-STATUS-IN              PIC XX.
       01 WS-STATUS-OUT             PIC XX.
       01 WS-EOF                    PIC 9 VALUE 0.
          88 EOF-CLIENTS            VALUE 1.
       01 WS-COMPTEUR               PIC 9(6) VALUE ZEROES.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           OPEN INPUT  CLIENTS-IN
           IF WS-STATUS-IN NOT = "00"
               DISPLAY "ERREUR OUVERTURE clients_in.txt : "
                   WS-STATUS-IN
               STOP RUN
           END-IF

           OPEN OUTPUT CLIENTS-OUT
           IF WS-STATUS-OUT NOT = "00"
               DISPLAY "ERREUR OUVERTURE clients_out.txt : "
                   WS-STATUS-OUT
               CLOSE CLIENTS-IN
               STOP RUN
           END-IF

           PERFORM UNTIL EOF-CLIENTS
               READ CLIENTS-IN INTO CLIENT-IN-RECORD
                   AT END
                       SET EOF-CLIENTS TO TRUE
                   NOT AT END
                       WRITE CLIENT-OUT-RECORD FROM CLIENT-IN-RECORD
                       IF WS-STATUS-OUT NOT = "00"
                           DISPLAY "ERREUR ECRITURE ligne "
                               WS-COMPTEUR " : " WS-STATUS-OUT
                       ELSE
                           ADD 1 TO WS-COMPTEUR
                       END-IF
               END-READ
           END-PERFORM

           CLOSE CLIENTS-IN
           CLOSE CLIENTS-OUT

           DISPLAY "Copie terminee. Lignes traitees : " WS-COMPTEUR
           STOP RUN.

       END PROGRAM COPIE-FICHIER.
