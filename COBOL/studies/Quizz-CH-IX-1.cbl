      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   GESTION DES FRAIS ETUDIANTS - TRAITEMENT BATCH
      *            - Lecture PAIEMENTS.ETUDIANTS.DAT (sequentiel)
      *            - MAJ MAITRE.ETUDIANTS.DAT (indexe, cle alt. nom)
      *            - Rapport RAPPORT.DAT (impayes tries par nom)
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. FRAIS-ETUDIANTS.

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.

           SELECT PAIEMENTS-FILE
               ASSIGN TO "PAIEMENTS.ETUDIANTS.DAT"
               ORGANIZATION IS SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-STATUS-PAY.

           SELECT MAITRE-FILE
               ASSIGN TO "MAITRE.ETUDIANTS.DAT"
               ORGANIZATION IS INDEXED
               ACCESS MODE IS DYNAMIC
               RECORD KEY IS NUMERO-ETUDIANT
               ALTERNATE RECORD KEY IS NOM-ETUDIANT
                   WITH DUPLICATES
               FILE STATUS IS WS-STATUS-MAI.

           SELECT RAPPORT-FILE
               ASSIGN TO "RAPPORT.DAT"
               ORGANIZATION IS SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-STATUS-RPT.

       DATA DIVISION.
       FILE SECTION.

       FD PAIEMENTS-FILE.
       01 ENREGISTREMENT-PAIEMENT.
           05 NUMERO-ETUDIANT-PAIEMENT  PIC 9(7).
           05 MONTANT-PAIEMENT          PIC 9(6)V99.

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

       FD RAPPORT-FILE.
       01 LIGNE-RAPPORT                 PIC X(132).

       WORKING-STORAGE SECTION.
       01 WS-STATUS-PAY                 PIC XX.
       01 WS-STATUS-MAI                 PIC XX.
       01 WS-STATUS-RPT                 PIC XX.

       01 WS-EOF-PAY                    PIC 9 VALUE 0.
          88 EOF-PAIEMENTS              VALUE 1.

       01 WS-TOTAL-IMPAYES              PIC 9(9)V99  VALUE ZEROES.
       01 WS-SOLDE-ETUDIANT             PIC S9(6)V99 VALUE ZEROES.
       01 WS-NB-PAIE-OK                 PIC 9(5)     VALUE ZEROES.
       01 WS-NB-PAIE-ERR                PIC 9(5)     VALUE ZEROES.
       01 WS-IDX                        PIC 9(2)     VALUE ZEROES.

      * Lignes de rapport
       01 WS-ENTETE-1.
           05 FILLER PIC X(50) VALUE SPACES.
           05 FILLER PIC X(32)
               VALUE "RAPPORT DES FRAIS IMPAYES".
           05 FILLER PIC X(50) VALUE SPACES.

       01 WS-ENTETE-2.
           05 FILLER PIC X(7)  VALUE "NUMERO".
           05 FILLER PIC X(2)  VALUE SPACES.
           05 FILLER PIC X(30) VALUE "NOM".
           05 FILLER PIC X(1)  VALUE SPACES.
           05 FILLER PIC X(4)  VALUE "SEXE".
           05 FILLER PIC X(2)  VALUE SPACES.
           05 FILLER PIC X(8)  VALUE "FRAIS DU".
           05 FILLER PIC X(2)  VALUE SPACES.
           05 FILLER PIC X(11) VALUE "MONTANT PAY".
           05 FILLER PIC X(2)  VALUE SPACES.
           05 FILLER PIC X(8)  VALUE "SOLDE DU".
           05 FILLER PIC X(56) VALUE SPACES.

       01 WS-SEP-LINE                   PIC X(132) VALUE ALL "-".

       01 WS-DETAIL-LINE.
           05 WDL-NUMERO                PIC 9(7).
           05 FILLER                    PIC X(2)  VALUE SPACES.
           05 WDL-NOM                   PIC X(30).
           05 FILLER                    PIC X(1)  VALUE SPACES.
           05 WDL-SEXE                  PIC X(1).
           05 FILLER                    PIC X(4)  VALUE SPACES.
           05 WDL-FRAIS-DUS             PIC Z,ZZ9.
           05 FILLER                    PIC X(4)  VALUE SPACES.
           05 WDL-MONTANT-PAYE          PIC ZZ,ZZZ.99.
           05 FILLER                    PIC X(2)  VALUE SPACES.
           05 WDL-SOLDE                 PIC ZZ,ZZZ.99.
           05 FILLER                    PIC X(54) VALUE SPACES.

       01 WS-COURS-LINE.
           05 FILLER                    PIC X(42) VALUE SPACES.
           05 FILLER                    PIC X(9)  VALUE "  Cours: ".
           05 WCL-ID-COURS              PIC X(10).
           05 FILLER                    PIC X(71) VALUE SPACES.

       01 WS-TOTAL-LINE.
           05 FILLER                    PIC X(75) VALUE SPACES.
           05 FILLER                    PIC X(21)
               VALUE "TOTAL IMPAYES      : ".
           05 WTL-TOTAL                 PIC ZZZ,ZZZ.99.
           05 FILLER                    PIC X(29) VALUE SPACES.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           PERFORM 1000-OUVRIR-FICHIERS
           PERFORM 2000-ECRIRE-ENTETES
           PERFORM 3000-TRAITER-PAIEMENTS UNTIL EOF-PAIEMENTS
           PERFORM 4000-GENERER-RAPPORT
           PERFORM 5000-FERMER-FICHIERS
           DISPLAY SPACES
           DISPLAY "Paiements appliques : " WS-NB-PAIE-OK
           DISPLAY "Paiements en erreur : " WS-NB-PAIE-ERR
           STOP RUN.

      ******************************************************************
       1000-OUVRIR-FICHIERS.
           OPEN INPUT PAIEMENTS-FILE
           IF WS-STATUS-PAY NOT = "00"
               DISPLAY "ERREUR PAIEMENTS.ETUDIANTS.DAT : "
                   WS-STATUS-PAY
               STOP RUN
           END-IF

           OPEN I-O MAITRE-FILE
           IF WS-STATUS-MAI NOT = "00"
               DISPLAY "ERREUR MAITRE.ETUDIANTS.DAT : " WS-STATUS-MAI
               STOP RUN
           END-IF

           OPEN OUTPUT RAPPORT-FILE
           IF WS-STATUS-RPT NOT = "00"
               DISPLAY "ERREUR RAPPORT.DAT : " WS-STATUS-RPT
               STOP RUN
           END-IF

           PERFORM 3100-LIRE-PAIEMENT.

      ******************************************************************
       2000-ECRIRE-ENTETES.
           WRITE LIGNE-RAPPORT FROM WS-SEP-LINE
           WRITE LIGNE-RAPPORT FROM WS-ENTETE-1
           WRITE LIGNE-RAPPORT FROM WS-SEP-LINE
           WRITE LIGNE-RAPPORT FROM WS-ENTETE-2
           WRITE LIGNE-RAPPORT FROM WS-SEP-LINE.

      ******************************************************************
       3000-TRAITER-PAIEMENTS.
           PERFORM 3200-VALIDER-PAIEMENT
           IF WS-STATUS-PAY = "00"
               PERFORM 3300-METTRE-A-JOUR-MAITRE
           END-IF
           PERFORM 3100-LIRE-PAIEMENT.

       3100-LIRE-PAIEMENT.
           READ PAIEMENTS-FILE INTO ENREGISTREMENT-PAIEMENT
               AT END SET EOF-PAIEMENTS TO TRUE
           END-READ.

       3200-VALIDER-PAIEMENT.
           IF MONTANT-PAIEMENT < 0.01 OR MONTANT-PAIEMENT > 9999.99
               DISPLAY "PAIEMENT INVALIDE - Etudiant: "
                   NUMERO-ETUDIANT-PAIEMENT
                   " Montant: " MONTANT-PAIEMENT
                   " (hors plage 0.01-9999.99)"
               ADD 1 TO WS-NB-PAIE-ERR
               MOVE "ER" TO WS-STATUS-PAY
           ELSE
               MOVE "00" TO WS-STATUS-PAY
           END-IF.

       3300-METTRE-A-JOUR-MAITRE.
           MOVE NUMERO-ETUDIANT-PAIEMENT TO NUMERO-ETUDIANT
           READ MAITRE-FILE
           IF WS-STATUS-MAI = "00"
               ADD MONTANT-PAIEMENT TO MONTANT-PAYE
               REWRITE ENREGISTREMENT-ETUDIANT
               IF WS-STATUS-MAI NOT = "00"
                   DISPLAY "ERREUR REWRITE etudiant: "
                       NUMERO-ETUDIANT " : " WS-STATUS-MAI
                   ADD 1 TO WS-NB-PAIE-ERR
               ELSE
                   ADD 1 TO WS-NB-PAIE-OK
               END-IF
           ELSE
               DISPLAY "ETUDIANT INEXISTANT: "
                   NUMERO-ETUDIANT-PAIEMENT
                   " Status: " WS-STATUS-MAI
               ADD 1 TO WS-NB-PAIE-ERR
           END-IF.

      ******************************************************************
       4000-GENERER-RAPPORT.
           MOVE SPACES TO NOM-ETUDIANT
           START MAITRE-FILE KEY IS GREATER THAN OR EQUAL TO
               NOM-ETUDIANT
           IF WS-STATUS-MAI NOT = "00" AND "10"
               DISPLAY "ERREUR START MAITRE: " WS-STATUS-MAI
               STOP RUN
           END-IF

           PERFORM 4100-LIRE-MAITRE-PAR-NOM
           PERFORM UNTIL WS-STATUS-MAI = "10"
               COMPUTE WS-SOLDE-ETUDIANT =
                   FRAIS-DUS - MONTANT-PAYE
               IF WS-SOLDE-ETUDIANT > ZEROES
                   PERFORM 4200-ECRIRE-DETAIL
                   ADD WS-SOLDE-ETUDIANT TO WS-TOTAL-IMPAYES
               END-IF
               PERFORM 4100-LIRE-MAITRE-PAR-NOM
           END-PERFORM

           WRITE LIGNE-RAPPORT FROM WS-SEP-LINE
           MOVE WS-TOTAL-IMPAYES TO WTL-TOTAL
           WRITE LIGNE-RAPPORT FROM WS-TOTAL-LINE
           WRITE LIGNE-RAPPORT FROM WS-SEP-LINE.

       4100-LIRE-MAITRE-PAR-NOM.
           READ MAITRE-FILE NEXT
           END-READ.

       4200-ECRIRE-DETAIL.
           MOVE NUMERO-ETUDIANT    TO WDL-NUMERO
           MOVE NOM-ETUDIANT       TO WDL-NOM
           MOVE SEXE               TO WDL-SEXE
           MOVE FRAIS-DUS          TO WDL-FRAIS-DUS
           MOVE MONTANT-PAYE       TO WDL-MONTANT-PAYE
           MOVE WS-SOLDE-ETUDIANT  TO WDL-SOLDE
           WRITE LIGNE-RAPPORT FROM WS-DETAIL-LINE

           PERFORM VARYING WS-IDX FROM 1 BY 1
               UNTIL WS-IDX > 5
               IF ID-COURS(WS-IDX) NOT = SPACES
                   MOVE ID-COURS(WS-IDX) TO WCL-ID-COURS
                   WRITE LIGNE-RAPPORT FROM WS-COURS-LINE
               END-IF
           END-PERFORM.

      ******************************************************************
       5000-FERMER-FICHIERS.
           CLOSE PAIEMENTS-FILE
           CLOSE MAITRE-FILE
           CLOSE RAPPORT-FILE.

       END PROGRAM FRAIS-ETUDIANTS.
