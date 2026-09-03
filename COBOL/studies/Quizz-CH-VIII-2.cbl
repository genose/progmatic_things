      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   ECRITURE DE DONNEES STATIQUES DANS UN FICHIER SEQUENTIEL
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. ECRITURE-EMPLOYES.

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT EMPLOYES-FILE
               ASSIGN TO "employes.txt"
               ORGANIZATION IS SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-FILE-STATUS.

       DATA DIVISION.
       FILE SECTION.

       FD EMPLOYES-FILE.
       01 EMPLOYE-RECORD.
           05 EMP-ID                PIC 9(5).
           05 FILLER                PIC X(1) VALUE SPACE.
           05 EMP-NOM               PIC X(20).
           05 FILLER                PIC X(1) VALUE SPACE.
           05 EMP-SALAIRE           PIC 9(9)V99.

       WORKING-STORAGE SECTION.
       01 WS-FILE-STATUS            PIC XX.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           OPEN OUTPUT EMPLOYES-FILE
           IF WS-FILE-STATUS NOT = "00"
               DISPLAY "ERREUR OUVERTURE employes.txt : " WS-FILE-STATUS
               STOP RUN
           END-IF

           MOVE 10001           TO EMP-ID
           MOVE "Dupont Jean"   TO EMP-NOM
           MOVE 285000.00       TO EMP-SALAIRE
           WRITE EMPLOYE-RECORD
           IF WS-FILE-STATUS NOT = "00"
               DISPLAY "ERREUR ECRITURE enreg 1 : " WS-FILE-STATUS
           END-IF

           MOVE 10002           TO EMP-ID
           MOVE "Martin Sophie" TO EMP-NOM
           MOVE 320050.75       TO EMP-SALAIRE
           WRITE EMPLOYE-RECORD
           IF WS-FILE-STATUS NOT = "00"
               DISPLAY "ERREUR ECRITURE enreg 2 : " WS-FILE-STATUS
           END-IF

           MOVE 10003           TO EMP-ID
           MOVE "Bernard Lucas" TO EMP-NOM
           MOVE 198500.50       TO EMP-SALAIRE
           WRITE EMPLOYE-RECORD
           IF WS-FILE-STATUS NOT = "00"
               DISPLAY "ERREUR ECRITURE enreg 3 : " WS-FILE-STATUS
           END-IF

           CLOSE EMPLOYES-FILE

           DISPLAY "Fichier employes.txt cree avec 3 enregistrements."
           STOP RUN.

       END PROGRAM ECRITURE-EMPLOYES.
