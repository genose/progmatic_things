      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   GESTION DES FRAIS ETUDIANTS
      *            - Lecture des paiements (STUDPAY.DAT, sequentiel)
      *            - MAJ du fichier maitre (STUDMAST.DAT, indexe)
      *            - Rapport des impayes (REPORT.DAT, trie par nom)
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. GESTION-FRAIS-ETU.

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.

           SELECT PAY-FILE
               ASSIGN TO "STUDPAY.DAT"
               ORGANIZATION IS LINE SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-PAY-STATUS.

           SELECT MAST-FILE
               ASSIGN TO "STUDMAST.DAT"
               ORGANIZATION IS INDEXED
               ACCESS MODE IS DYNAMIC
               RECORD KEY IS MAST-STUDENT-NUM
               ALTERNATE RECORD KEY IS MAST-STUDENT-NAME
                   WITH DUPLICATES
               FILE STATUS IS WS-MAST-STATUS.

           SELECT REPORT-FILE
               ASSIGN TO "REPORT.DAT"
               ORGANIZATION IS LINE SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-RPT-STATUS.

       DATA DIVISION.
       FILE SECTION.

       FD PAY-FILE.
       01 PAY-RECORD.
           05 PAY-STUDENT-NUM       PIC X(6).
           05 PAY-AMOUNT            PIC 9(5)V99.

       FD MAST-FILE.
       01 MAST-RECORD.
           05 MAST-STUDENT-NUM      PIC X(6).
           05 MAST-STUDENT-NAME     PIC X(30).
           05 MAST-FEES-DUE         PIC 9(5)V99.
           05 MAST-FEES-PAID        PIC 9(5)V99.
           05 MAST-COURSES          OCCURS 5 TIMES.
               10 MAST-COURSE-CODE  PIC X(10).

       FD REPORT-FILE.
       01 REPORT-LINE               PIC X(80).

       WORKING-STORAGE SECTION.
       01 WS-PAY-STATUS             PIC XX.
       01 WS-MAST-STATUS            PIC XX.
       01 WS-RPT-STATUS             PIC XX.

       01 WS-EOF-PAY                PIC 9 VALUE 0.
          88 EOF-PAY                VALUE 1.

       01 WS-TOTAL-UNPAID           PIC 9(7)V99 VALUE ZEROES.
       01 WS-BALANCE                PIC 9(5)V99 VALUE ZEROES.
       01 WS-ERR-COUNT              PIC 9(4)  VALUE ZEROES.
       01 WS-IDX                    PIC 9(2)  VALUE ZEROES.

      * Lignes de rapport formatees
       01 WS-TITLE-LINE.
           05 FILLER PIC X(30) VALUE "RAPPORT DES FRAIS IMPAYES".
           05 FILLER PIC X(50) VALUE SPACES.

       01 WS-SEP-LINE               PIC X(80) VALUE ALL "-".

       01 WS-HDR-LINE.
           05 FILLER PIC X(6)  VALUE "NUM".
           05 FILLER PIC X(2)  VALUE SPACES.
           05 FILLER PIC X(30) VALUE "NOM".
           05 FILLER PIC X(10) VALUE "DU".
           05 FILLER PIC X(10) VALUE "PAYE".
           05 FILLER PIC X(10) VALUE "SOLDE".
           05 FILLER PIC X(12) VALUE "COURS".

       01 WS-DETAIL-LINE.
           05 WS-DL-NUM             PIC X(6).
           05 FILLER                PIC X(2)  VALUE SPACES.
           05 WS-DL-NAME            PIC X(30).
           05 WS-DL-DUE             PIC ZZ,ZZZ.99.
           05 FILLER                PIC X(2)  VALUE SPACES.
           05 WS-DL-PAID            PIC ZZ,ZZZ.99.
           05 FILLER                PIC X(2)  VALUE SPACES.
           05 WS-DL-BALANCE         PIC ZZ,ZZZ.99.

       01 WS-COURSE-LINE.
           05 FILLER                PIC X(40) VALUE SPACES.
           05 FILLER                PIC X(8)  VALUE "  Cours:".
           05 WS-CL-CODE            PIC X(10).
           05 FILLER                PIC X(22) VALUE SPACES.

       01 WS-TOTAL-LINE.
           05 FILLER PIC X(40) VALUE SPACES.
           05 FILLER PIC X(18) VALUE "TOTAL IMPAYES   : ".
           05 WS-TL-TOTAL           PIC ZZZ,ZZZ.99.
           05 FILLER                PIC X(14) VALUE SPACES.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           PERFORM 1000-OPEN-FILES
           PERFORM 2000-WRITE-REPORT-HEADER
           PERFORM 3000-PROCESS-PAYMENTS UNTIL EOF-PAY
           PERFORM 4000-GENERATE-REPORT
           PERFORM 5000-CLOSE-FILES
           STOP RUN.

      ******************************************************************
       1000-OPEN-FILES.
           OPEN INPUT  PAY-FILE
           IF WS-PAY-STATUS NOT = "00"
               DISPLAY "ERREUR OUVERTURE STUDPAY.DAT : " WS-PAY-STATUS
               STOP RUN
           END-IF

           OPEN I-O MAST-FILE
           IF WS-MAST-STATUS NOT = "00"
               DISPLAY "ERREUR OUVERTURE STUDMAST.DAT: " WS-MAST-STATUS
               STOP RUN
           END-IF

           OPEN OUTPUT REPORT-FILE
           IF WS-RPT-STATUS NOT = "00"
               DISPLAY "ERREUR OUVERTURE REPORT.DAT  : " WS-RPT-STATUS
               STOP RUN
           END-IF

           PERFORM 3100-READ-PAYMENT.

      ******************************************************************
       2000-WRITE-REPORT-HEADER.
           WRITE REPORT-LINE FROM WS-SEP-LINE
           WRITE REPORT-LINE FROM WS-TITLE-LINE
           WRITE REPORT-LINE FROM WS-SEP-LINE
           WRITE REPORT-LINE FROM WS-HDR-LINE
           WRITE REPORT-LINE FROM WS-SEP-LINE.

      ******************************************************************
       3000-PROCESS-PAYMENTS.
           PERFORM 3200-VALIDATE-PAYMENT
           IF WS-PAY-STATUS = "00"
               PERFORM 3300-UPDATE-MASTER
           END-IF
           PERFORM 3100-READ-PAYMENT.

       3100-READ-PAYMENT.
           READ PAY-FILE INTO PAY-RECORD
               AT END SET EOF-PAY TO TRUE
           END-READ.

       3200-VALIDATE-PAYMENT.
           IF PAY-AMOUNT < 0.01 OR PAY-AMOUNT > 9999.99
               DISPLAY "PAIEMENT INVALIDE (hors plage 0.01-9999.99) "
                   "Etudiant: " PAY-STUDENT-NUM
                   " Montant: " PAY-AMOUNT
               ADD 1 TO WS-ERR-COUNT
               MOVE "ERR" TO WS-PAY-STATUS
           ELSE
               MOVE "00" TO WS-PAY-STATUS
           END-IF.

       3300-UPDATE-MASTER.
           MOVE PAY-STUDENT-NUM TO MAST-STUDENT-NUM
           READ MAST-FILE
           IF WS-MAST-STATUS = "00"
               ADD PAY-AMOUNT TO MAST-FEES-PAID
               REWRITE MAST-RECORD
               IF WS-MAST-STATUS NOT = "00"
                   DISPLAY "ERREUR REWRITE etudiant: " MAST-STUDENT-NUM
                       " Status: " WS-MAST-STATUS
               END-IF
           ELSE
               DISPLAY "ETUDIANT INEXISTANT: " PAY-STUDENT-NUM
                   " Status: " WS-MAST-STATUS
               ADD 1 TO WS-ERR-COUNT
           END-IF.

      ******************************************************************
       4000-GENERATE-REPORT.
           MOVE SPACES TO MAST-STUDENT-NAME
           START MAST-FILE KEY IS GREATER THAN OR EQUAL TO
               MAST-STUDENT-NAME
           IF WS-MAST-STATUS NOT = "00" AND "10"
               DISPLAY "ERREUR START MAST-FILE: " WS-MAST-STATUS
               STOP RUN
           END-IF

           PERFORM 4100-READ-MASTER-BY-NAME
           PERFORM UNTIL WS-MAST-STATUS = "10"
               COMPUTE WS-BALANCE =
                   MAST-FEES-DUE - MAST-FEES-PAID
               IF WS-BALANCE > ZEROES
                   PERFORM 4200-WRITE-DETAIL
                   ADD WS-BALANCE TO WS-TOTAL-UNPAID
               END-IF
               PERFORM 4100-READ-MASTER-BY-NAME
           END-PERFORM

           WRITE REPORT-LINE FROM WS-SEP-LINE
           MOVE WS-TOTAL-UNPAID TO WS-TL-TOTAL
           WRITE REPORT-LINE FROM WS-TOTAL-LINE
           WRITE REPORT-LINE FROM WS-SEP-LINE

           DISPLAY "Rapport genere. Erreurs : " WS-ERR-COUNT
           DISPLAY "Total impayes  : " WS-TOTAL-UNPAID.

       4100-READ-MASTER-BY-NAME.
           READ MAST-FILE NEXT
               KEY IS MAST-STUDENT-NAME
           END-READ.

       4200-WRITE-DETAIL.
           MOVE MAST-STUDENT-NUM  TO WS-DL-NUM
           MOVE MAST-STUDENT-NAME TO WS-DL-NAME
           MOVE MAST-FEES-DUE     TO WS-DL-DUE
           MOVE MAST-FEES-PAID    TO WS-DL-PAID
           MOVE WS-BALANCE        TO WS-DL-BALANCE
           WRITE REPORT-LINE FROM WS-DETAIL-LINE

           PERFORM VARYING WS-IDX FROM 1 BY 1
               UNTIL WS-IDX > 5
               IF MAST-COURSE-CODE(WS-IDX) NOT = SPACES
                   MOVE MAST-COURSE-CODE(WS-IDX) TO WS-CL-CODE
                   WRITE REPORT-LINE FROM WS-COURSE-LINE
               END-IF
           END-PERFORM.

      ******************************************************************
       5000-CLOSE-FILES.
           CLOSE PAY-FILE
           CLOSE MAST-FILE
           CLOSE REPORT-FILE.

       END PROGRAM GESTION-FRAIS-ETU.
