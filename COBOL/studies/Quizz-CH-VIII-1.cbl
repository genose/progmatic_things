      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   LECTURE SEQUENTIELLE D'UN FICHIER TEXTE LIGNE PAR LIGNE
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. LECTURE-FICHIER.

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT TEXTE-FILE
               ASSIGN TO "fichier_test.txt"
               ORGANIZATION IS SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-FILE-STATUS.

       DATA DIVISION.
       FILE SECTION.

       FD TEXTE-FILE.
       01 TEXTE-RECORD              PIC X(50).

       WORKING-STORAGE SECTION.
       01 WS-FILE-STATUS            PIC XX.
       01 WS-EOF                    PIC 9 VALUE 0.
          88 EOF-FICHIER            VALUE 1.
       01 WS-COMPTEUR               PIC 9(4) VALUE ZEROES.

       01 WS-LIGNE-AFFICHEE.
           05 FILLER                PIC X(5)  VALUE "Nom ".
           05 WS-NUMERO             PIC ZZZ9.
           05 FILLER                PIC X(3)  VALUE " : ".
           05 WS-NOM                PIC X(50).

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           OPEN INPUT TEXTE-FILE
           IF WS-FILE-STATUS NOT = "00"
               DISPLAY "ERREUR OUVERTURE fichier_test.txt : "
                   WS-FILE-STATUS
               STOP RUN
           END-IF

           PERFORM UNTIL EOF-FICHIER
               READ TEXTE-FILE INTO TEXTE-RECORD
                   AT END
                       SET EOF-FICHIER TO TRUE
                   NOT AT END
                       ADD 1 TO WS-COMPTEUR
                       MOVE WS-COMPTEUR    TO WS-NUMERO
                       MOVE TEXTE-RECORD   TO WS-NOM
                       DISPLAY WS-LIGNE-AFFICHEE
               END-READ
           END-PERFORM

           DISPLAY SPACES
           DISPLAY "Nombre total de lignes lues : " WS-COMPTEUR

           CLOSE TEXTE-FILE
           STOP RUN.

       END PROGRAM LECTURE-FICHIER.
