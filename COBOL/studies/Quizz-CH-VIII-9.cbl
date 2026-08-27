      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   MISE A JOUR SEQUENTIELLE AVEC REWRITE
      *            Lecture stocks.txt en I-O, augmentation stock de 10
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. MAJ-STOCKS.

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT STOCKS-FILE
               ASSIGN TO "stocks.txt"
               ORGANIZATION IS LINE SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-FILE-STATUS.

       DATA DIVISION.
       FILE SECTION.

       FD STOCKS-FILE.
       01 STOCK-RECORD.
           05 SR-CODE               PIC X(5).
           05 FILLER                PIC X(1).
           05 SR-STOCK              PIC 9(5).

       WORKING-STORAGE SECTION.
       01 WS-FILE-STATUS            PIC XX.
       01 WS-EOF                    PIC 9 VALUE 0.
          88 EOF-STOCKS             VALUE 1.

       01 WS-CODE-CIBLE             PIC X(5).
       01 WS-TROUVE                 PIC 9 VALUE 0.
          88 ARTICLE-TROUVE         VALUE 1.
          88 ARTICLE-NON-TROUVE     VALUE 0.

       01 WS-STOCK-AVANT            PIC 9(5) VALUE ZEROES.
       01 WS-STOCK-APRES            PIC 9(5) VALUE ZEROES.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           PERFORM 1000-SAISIR-CODE-CIBLE

           OPEN I-O STOCKS-FILE
           IF WS-FILE-STATUS NOT = "00"
               DISPLAY "ERREUR OUVERTURE stocks.txt : " WS-FILE-STATUS
               STOP RUN
           END-IF

           PERFORM UNTIL EOF-STOCKS
               READ STOCKS-FILE
                   AT END
                       SET EOF-STOCKS TO TRUE
                   NOT AT END
                       PERFORM 2000-TRAITER-ARTICLE
               END-READ
           END-PERFORM

           CLOSE STOCKS-FILE

           PERFORM 3000-AFFICHER-RESULTAT
           STOP RUN.

      ******************************************************************
       1000-SAISIR-CODE-CIBLE.
           MOVE SPACES TO WS-CODE-CIBLE
           PERFORM UNTIL WS-CODE-CIBLE NOT = SPACES
               DISPLAY "Code article a mettre a jour : "
                   WITH NO ADVANCING
               ACCEPT WS-CODE-CIBLE
               IF WS-CODE-CIBLE = SPACES
                   DISPLAY "  --> Le code ne peut pas etre vide."
               END-IF
           END-PERFORM.

      ******************************************************************
       2000-TRAITER-ARTICLE.
           IF SR-CODE = WS-CODE-CIBLE
               MOVE SR-STOCK TO WS-STOCK-AVANT
               ADD 10 TO SR-STOCK
               MOVE SR-STOCK TO WS-STOCK-APRES

               REWRITE STOCK-RECORD
               IF WS-FILE-STATUS = "00"
                   SET ARTICLE-TROUVE TO TRUE
               ELSE
                   DISPLAY "ERREUR REWRITE article " SR-CODE
                       " : " WS-FILE-STATUS
               END-IF
           END-IF.

      ******************************************************************
       3000-AFFICHER-RESULTAT.
           DISPLAY SPACES
           DISPLAY "========================================"
           IF ARTICLE-TROUVE
               DISPLAY "  Article        : " WS-CODE-CIBLE
               DISPLAY "  Stock avant    : " WS-STOCK-AVANT
               DISPLAY "  Stock apres    : " WS-STOCK-APRES
               DISPLAY "  Mise a jour OK (+10)"
           ELSE
               DISPLAY "  Article " WS-CODE-CIBLE
                   " non trouve dans stocks.txt"
           END-IF
           DISPLAY "========================================".

       END PROGRAM MAJ-STOCKS.
