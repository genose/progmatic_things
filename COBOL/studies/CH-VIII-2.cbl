      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   GESTION D'UN LIVRET D'EPARGNE SUR UNE ANNEE
      *            Lecture LIVRET.DAT (D=depot, R=retrait)
      *            Calcul solde, totaux, interets 3%
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. LIVRET-EPARGNE.

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT LIVRET-FILE
               ASSIGN TO "LIVRET.DAT"
               ORGANIZATION IS SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-FILE-STATUS.

       DATA DIVISION.
       FILE SECTION.

       FD LIVRET-FILE.
       01 LIVRET-RECORD.
           05 LR-TYPE               PIC X(1).
               88 LR-DEPOT          VALUE 'D'.
               88 LR-RETRAIT        VALUE 'R'.
           05 LR-MONTANT            PIC 9(6)V99.

       WORKING-STORAGE SECTION.
       01 WS-FILE-STATUS            PIC XX.
       01 WS-EOF                    PIC 9 VALUE 0.
          88 EOF-LIVRET             VALUE 1.

       01 WS-SOLDE                  PIC S9(9)V99 VALUE ZEROES.
       01 WS-TOTAL-DEPOTS           PIC 9(9)V99  VALUE ZEROES.
       01 WS-TOTAL-RETRAITS         PIC 9(9)V99  VALUE ZEROES.
       01 WS-INTERETS               PIC 9(9)V99  VALUE ZEROES.
       01 WS-SOLDE-APRES-INT        PIC S9(9)V99 VALUE ZEROES.
       01 WS-NB-DEPOTS              PIC 9(5)     VALUE ZEROES.
       01 WS-NB-RETRAITS            PIC 9(5)     VALUE ZEROES.
       01 WS-NB-ERREURS             PIC 9(5)     VALUE ZEROES.

       77 TAUX-INTERET              PIC V99      VALUE .03.

      * Variables d'edition pour affichage
       01 WS-AFF-MONTANT            PIC ZZZ,ZZZ.99.
       01 WS-AFF-SOLDE              PIC -ZZZ,ZZZ,ZZ9.99.
       01 WS-AFF-INTERETS           PIC ZZZ,ZZZ.99.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           OPEN INPUT LIVRET-FILE
           IF WS-FILE-STATUS NOT = "00"
               DISPLAY "ERREUR OUVERTURE LIVRET.DAT : " WS-FILE-STATUS
               STOP RUN
           END-IF

           DISPLAY "========================================"
           DISPLAY "   TRAITEMENT DU LIVRET D EPARGNE       "
           DISPLAY "========================================"
           DISPLAY SPACES

           PERFORM UNTIL EOF-LIVRET
               READ LIVRET-FILE
                   AT END
                       SET EOF-LIVRET TO TRUE
                   NOT AT END
                       PERFORM 1000-TRAITER-TRANSACTION
               END-READ
           END-PERFORM

           CLOSE LIVRET-FILE

           PERFORM 2000-CALCULER-INTERETS
           PERFORM 3000-AFFICHER-RESUME
           STOP RUN.

      ******************************************************************
       1000-TRAITER-TRANSACTION.
           MOVE LR-MONTANT TO WS-AFF-MONTANT
           EVALUATE TRUE
               WHEN LR-DEPOT
                   ADD LR-MONTANT TO WS-SOLDE
                   ADD LR-MONTANT TO WS-TOTAL-DEPOTS
                   ADD 1          TO WS-NB-DEPOTS
                   DISPLAY "  DEPOT   + " WS-AFF-MONTANT
                       " EUR  | Solde : " WS-SOLDE

               WHEN LR-RETRAIT
                   IF LR-MONTANT > WS-SOLDE
                       DISPLAY "  RETRAIT REFUSE (fonds insuffisants)"
                           " : " WS-AFF-MONTANT " EUR"
                       ADD 1 TO WS-NB-ERREURS
                   ELSE
                       SUBTRACT LR-MONTANT FROM WS-SOLDE
                       ADD LR-MONTANT TO WS-TOTAL-RETRAITS
                       ADD 1          TO WS-NB-RETRAITS
                       DISPLAY "  RETRAIT - " WS-AFF-MONTANT
                           " EUR  | Solde : " WS-SOLDE
                   END-IF

               WHEN OTHER
                   DISPLAY "  TYPE INCONNU [" LR-TYPE
                       "] - transaction ignoree"
                   ADD 1 TO WS-NB-ERREURS
           END-EVALUATE.

      ******************************************************************
       2000-CALCULER-INTERETS.
           IF WS-SOLDE > ZEROES
               COMPUTE WS-INTERETS = WS-SOLDE * TAUX-INTERET
               COMPUTE WS-SOLDE-APRES-INT = WS-SOLDE + WS-INTERETS
           ELSE
               MOVE ZEROES TO WS-INTERETS
               MOVE WS-SOLDE TO WS-SOLDE-APRES-INT
           END-IF.

      ******************************************************************
       3000-AFFICHER-RESUME.
           DISPLAY SPACES
           DISPLAY "========================================"
           DISPLAY "   RESUME ANNUEL DU LIVRET D EPARGNE    "
           DISPLAY "========================================"

           MOVE WS-TOTAL-DEPOTS TO WS-AFF-MONTANT
           DISPLAY "  Nombre de depots        : " WS-NB-DEPOTS
           DISPLAY "  Total depots            : " WS-AFF-MONTANT " EUR"

           MOVE WS-TOTAL-RETRAITS TO WS-AFF-MONTANT
           DISPLAY "  Nombre de retraits      : " WS-NB-RETRAITS
           DISPLAY "  Total retraits          : " WS-AFF-MONTANT " EUR"

           DISPLAY "  Operations refusees     : " WS-NB-ERREURS

           DISPLAY "----------------------------------------"

           MOVE WS-SOLDE TO WS-AFF-SOLDE
           DISPLAY "  Solde final (av. int.)  : " WS-AFF-SOLDE " EUR"

           MOVE WS-INTERETS TO WS-AFF-INTERETS
           DISPLAY "  Interets annuels (3%)   : " WS-AFF-INTERETS " EUR"

           MOVE WS-SOLDE-APRES-INT TO WS-AFF-SOLDE
           DISPLAY "  Solde final (ap. int.)  : " WS-AFF-SOLDE " EUR"
           DISPLAY "========================================".

       END PROGRAM LIVRET-EPARGNE.
