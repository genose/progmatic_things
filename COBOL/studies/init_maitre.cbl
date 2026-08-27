      ******************************************************************
      * Purpose: Initialise MAITRE.ETUDIANTS.DAT (indexe)
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. INIT-MAITRE.

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT MAITRE-FILE
               ASSIGN TO "MAITRE.ETUDIANTS.DAT"
               ORGANIZATION IS INDEXED
               ACCESS MODE IS SEQUENTIAL
               RECORD KEY IS NUMERO-ETUDIANT
               ALTERNATE RECORD KEY IS NOM-ETUDIANT
                   WITH DUPLICATES
               FILE STATUS IS WS-STATUS.

       DATA DIVISION.
       FILE SECTION.
       FD MAITRE-FILE.
       01 ENREGISTREMENT-ETUDIANT.
           05 NUMERO-ETUDIANT           PIC 9(7).
           05 NOM-ETUDIANT              PIC X(30).
           05 SEXE                      PIC X.
           05 CODE-COURS                PIC X(4).
           05 FRAIS-DUS                 PIC 9(4).
           05 MONTANT-PAYE              PIC 9(6)V99.
           05 COURS-INSCRITS            OCCURS 5 TIMES.
               10 ID-COURS              PIC X(10).

       WORKING-STORAGE SECTION.
       01 WS-STATUS                     PIC XX.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           OPEN OUTPUT MAITRE-FILE

           MOVE 0000001            TO NUMERO-ETUDIANT
           MOVE "DUPONT JEAN"      TO NOM-ETUDIANT
           MOVE "M"                TO SEXE
           MOVE "INFO"             TO CODE-COURS
           MOVE 1200               TO FRAIS-DUS
           MOVE 0                  TO MONTANT-PAYE
           MOVE "PROG101   "       TO ID-COURS(1)
           MOVE "WEB404    "       TO ID-COURS(2)
           MOVE "ALGO202   "       TO ID-COURS(3)
           MOVE SPACES             TO ID-COURS(4)
           MOVE SPACES             TO ID-COURS(5)
           WRITE ENREGISTREMENT-ETUDIANT

           MOVE 0000002            TO NUMERO-ETUDIANT
           MOVE "BERNARD LUCAS"    TO NOM-ETUDIANT
           MOVE "M"                TO SEXE
           MOVE "MATH"             TO CODE-COURS
           MOVE 2000               TO FRAIS-DUS
           MOVE 0                  TO MONTANT-PAYE
           MOVE "CALC101   "       TO ID-COURS(1)
           MOVE "STAT202   "       TO ID-COURS(2)
           MOVE "ANAL303   "       TO ID-COURS(3)
           MOVE SPACES             TO ID-COURS(4)
           MOVE SPACES             TO ID-COURS(5)
           WRITE ENREGISTREMENT-ETUDIANT

           MOVE 0000003            TO NUMERO-ETUDIANT
           MOVE "LEROY MARIE"      TO NOM-ETUDIANT
           MOVE "F"                TO SEXE
           MOVE "PHYS"             TO CODE-COURS
           MOVE 1800               TO FRAIS-DUS
           MOVE 0                  TO MONTANT-PAYE
           MOVE "MECA101   "       TO ID-COURS(1)
           MOVE "THER202   "       TO ID-COURS(2)
           MOVE "OPTI303   "       TO ID-COURS(3)
           MOVE SPACES             TO ID-COURS(4)
           MOVE SPACES             TO ID-COURS(5)
           WRITE ENREGISTREMENT-ETUDIANT

           MOVE 0000004            TO NUMERO-ETUDIANT
           MOVE "MARTIN SOPHIE"    TO NOM-ETUDIANT
           MOVE "F"                TO SEXE
           MOVE "INFO"             TO CODE-COURS
           MOVE 1500               TO FRAIS-DUS
           MOVE 0                  TO MONTANT-PAYE
           MOVE "PROG101   "       TO ID-COURS(1)
           MOVE "ALGO202   "       TO ID-COURS(2)
           MOVE "BD303     "       TO ID-COURS(3)
           MOVE "RESX404   "       TO ID-COURS(4)
           MOVE SPACES             TO ID-COURS(5)
           WRITE ENREGISTREMENT-ETUDIANT

           MOVE 0000005            TO NUMERO-ETUDIANT
           MOVE "DURAND PIERRE"    TO NOM-ETUDIANT
           MOVE "M"                TO SEXE
           MOVE "INFO"             TO CODE-COURS
           MOVE 1500               TO FRAIS-DUS
           MOVE 0                  TO MONTANT-PAYE
           MOVE "PROG101   "       TO ID-COURS(1)
           MOVE "BD303     "       TO ID-COURS(2)
           MOVE SPACES             TO ID-COURS(3)
           MOVE SPACES             TO ID-COURS(4)
           MOVE SPACES             TO ID-COURS(5)
           WRITE ENREGISTREMENT-ETUDIANT

           CLOSE MAITRE-FILE
           DISPLAY "MAITRE.ETUDIANTS.DAT cree (5 etudiants)."
           STOP RUN.

       END PROGRAM INIT-MAITRE.
