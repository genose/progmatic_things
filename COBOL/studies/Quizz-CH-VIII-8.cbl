      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   SYNTHESE DES VENTES - CALCUL DES TOTAUX PAR PRODUIT
      *            Lecture ventes.txt, ecriture ventes_synthese.txt
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. SYNTHESE-VENTES.

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT VENTES-IN
               ASSIGN TO "ventes.txt"
               ORGANIZATION IS SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-STATUS-IN.

           SELECT SYNTHESE-OUT
               ASSIGN TO "ventes_synthese.txt"
               ORGANIZATION IS SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-STATUS-OUT.

       DATA DIVISION.
       FILE SECTION.

       FD VENTES-IN.
       01 VENTE-IN-RECORD.
           05 VIN-ID-PRODUIT         PIC X(5).
           05 FILLER                 PIC X(1).
           05 VIN-QUANTITE           PIC 9(5).
           05 FILLER                 PIC X(1).
           05 VIN-PRIX-UNITAIRE      PIC 9(9)V99.

       FD SYNTHESE-OUT.
       01 SYNTHESE-RECORD.
           05 VOUT-ID-PRODUIT        PIC X(5).
           05 FILLER                 PIC X(3) VALUE " | ".
           05 VOUT-TOTAL-ED          PIC ZZZ,ZZZ,ZZ9.99.

       WORKING-STORAGE SECTION.
       01 WS-STATUS-IN               PIC XX.
       01 WS-STATUS-OUT              PIC XX.
       01 WS-EOF                     PIC 9 VALUE 0.
          88 EOF-VENTES              VALUE 1.

       01 WS-TOTAL-LIGNE             PIC 9(11)V99 VALUE ZEROES.
       01 WS-CA-GLOBAL               PIC 9(13)V99 VALUE ZEROES.
       01 WS-NB-LIGNES               PIC 9(6)     VALUE ZEROES.

       01 WS-AFF-CA                  PIC ZZZ,ZZZ,ZZZ,ZZ9.99.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           OPEN INPUT VENTES-IN
           IF WS-STATUS-IN NOT = "00"
               DISPLAY "ERREUR OUVERTURE ventes.txt : " WS-STATUS-IN
               STOP RUN
           END-IF

           OPEN OUTPUT SYNTHESE-OUT
           IF WS-STATUS-OUT NOT = "00"
               DISPLAY "ERREUR OUVERTURE ventes_synthese.txt : "
                   WS-STATUS-OUT
               CLOSE VENTES-IN
               STOP RUN
           END-IF

           PERFORM 1000-ECRIRE-ENTETE

           PERFORM UNTIL EOF-VENTES
               READ VENTES-IN
                   AT END
                       SET EOF-VENTES TO TRUE
                   NOT AT END
                       PERFORM 2000-TRAITER-VENTE
               END-READ
           END-PERFORM

           CLOSE VENTES-IN
           CLOSE SYNTHESE-OUT

           PERFORM 3000-AFFICHER-BILAN
           STOP RUN.

      ******************************************************************
       1000-ECRIRE-ENTETE.
           MOVE "PROD." TO VOUT-ID-PRODUIT
           MOVE 0       TO VOUT-TOTAL-ED
           MOVE "ID    " TO VOUT-ID-PRODUIT
           MOVE "TOTAL (quantite x prix)" TO VOUT-TOTAL-ED
           WRITE SYNTHESE-RECORD
               FROM "ID    | TOTAL (quantite x prix)      "
           WRITE SYNTHESE-RECORD
               FROM "------+------------------------------".

      ******************************************************************
       2000-TRAITER-VENTE.
           COMPUTE WS-TOTAL-LIGNE =
               VIN-QUANTITE * VIN-PRIX-UNITAIRE

           ADD WS-TOTAL-LIGNE TO WS-CA-GLOBAL
           ADD 1 TO WS-NB-LIGNES

           MOVE VIN-ID-PRODUIT  TO VOUT-ID-PRODUIT
           MOVE WS-TOTAL-LIGNE  TO VOUT-TOTAL-ED
           WRITE SYNTHESE-RECORD
           IF WS-STATUS-OUT NOT = "00"
               DISPLAY "ERREUR ECRITURE produit " VIN-ID-PRODUIT
                   " : " WS-STATUS-OUT
           END-IF.

      ******************************************************************
       3000-AFFICHER-BILAN.
           MOVE WS-CA-GLOBAL TO WS-AFF-CA
           DISPLAY SPACES
           DISPLAY "========================================"
           DISPLAY "   SYNTHESE DES VENTES                  "
           DISPLAY "========================================"
           DISPLAY "  Lignes traitees         : " WS-NB-LIGNES
           DISPLAY "  Chiffre d affaires      : " WS-AFF-CA.

       END PROGRAM SYNTHESE-VENTES.
