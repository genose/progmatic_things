      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   TRANSFORMATION DES DONNEES - DOUBLEMENT DES PRIX
      *            Lecture produits.txt, ecriture produits_nouveaux.txt
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. TRANSFO-PRODUITS.

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT PRODUITS-IN
               ASSIGN TO "produits.txt"
               ORGANIZATION IS SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-STATUS-IN.

           SELECT PRODUITS-OUT
               ASSIGN TO "produits_nouveaux.txt"
               ORGANIZATION IS SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-STATUS-OUT.

       DATA DIVISION.
       FILE SECTION.

       FD PRODUITS-IN.
       01 PRODUIT-IN-RECORD.
           05 PIN-ID                PIC 9(5).
           05 FILLER                PIC X(1).
           05 PIN-NOM               PIC X(20).
           05 FILLER                PIC X(1).
           05 PIN-PRIX              PIC 9(9)V99.

       FD PRODUITS-OUT.
       01 PRODUIT-OUT-RECORD.
           05 POUT-ID               PIC 9(5).
           05 FILLER                PIC X(1) VALUE SPACE.
           05 POUT-NOM              PIC X(20).
           05 FILLER                PIC X(1) VALUE SPACE.
           05 POUT-PRIX             PIC 9(9)V99.

       WORKING-STORAGE SECTION.
       01 WS-STATUS-IN              PIC XX.
       01 WS-STATUS-OUT             PIC XX.
       01 WS-EOF                    PIC 9 VALUE 0.
          88 EOF-PRODUITS           VALUE 1.

       01 WS-TOTAL-INITIAL          PIC 9(11)V99 VALUE ZEROES.
       01 WS-TOTAL-DOUBLE           PIC 9(11)V99 VALUE ZEROES.
       01 WS-PRIX-DOUBLE            PIC 9(9)V99  VALUE ZEROES.
       01 WS-COMPTEUR               PIC 9(6)     VALUE ZEROES.

      * Variables d'edition pour l'affichage final
       01 WS-AFF-INITIAL            PIC ZZZ,ZZZ,ZZ9.99.
       01 WS-AFF-DOUBLE             PIC ZZZ,ZZZ,ZZ9.99.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           OPEN INPUT  PRODUITS-IN
           IF WS-STATUS-IN NOT = "00"
               DISPLAY "ERREUR OUVERTURE produits.txt : " WS-STATUS-IN
               STOP RUN
           END-IF

           OPEN OUTPUT PRODUITS-OUT
           IF WS-STATUS-OUT NOT = "00"
               DISPLAY "ERREUR OUVERTURE produits_nouveaux.txt : "
                   WS-STATUS-OUT
               CLOSE PRODUITS-IN
               STOP RUN
           END-IF

           PERFORM UNTIL EOF-PRODUITS
               READ PRODUITS-IN
                   AT END
                       SET EOF-PRODUITS TO TRUE
                   NOT AT END
                       PERFORM 1000-TRAITER-PRODUIT
               END-READ
           END-PERFORM

           CLOSE PRODUITS-IN
           CLOSE PRODUITS-OUT

           PERFORM 2000-AFFICHER-TOTAUX
           STOP RUN.

      ******************************************************************
       1000-TRAITER-PRODUIT.
           ADD PIN-PRIX TO WS-TOTAL-INITIAL

           COMPUTE WS-PRIX-DOUBLE = PIN-PRIX * 2
           ADD WS-PRIX-DOUBLE TO WS-TOTAL-DOUBLE

           MOVE PIN-ID          TO POUT-ID
           MOVE PIN-NOM         TO POUT-NOM
           MOVE WS-PRIX-DOUBLE  TO POUT-PRIX
           WRITE PRODUIT-OUT-RECORD
           IF WS-STATUS-OUT NOT = "00"
               DISPLAY "ERREUR ECRITURE produit " PIN-ID
                   " : " WS-STATUS-OUT
           ELSE
               ADD 1 TO WS-COMPTEUR
           END-IF.

      ******************************************************************
       2000-AFFICHER-TOTAUX.
           MOVE WS-TOTAL-INITIAL TO WS-AFF-INITIAL
           MOVE WS-TOTAL-DOUBLE  TO WS-AFF-DOUBLE
           DISPLAY SPACES
           DISPLAY "Produits traites          : " WS-COMPTEUR
           DISPLAY "Total prix initiaux       : " WS-AFF-INITIAL
           DISPLAY "Total prix doubles        : " WS-AFF-DOUBLE.

       END PROGRAM TRANSFO-PRODUITS.
