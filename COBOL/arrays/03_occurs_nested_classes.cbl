      *================================================================*
      * PROGRAMME  : CLASSES-NOTES                                     *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.1                                               *
      *----------------------------------------------------------------*
      * DESCRIPTION: OCCURS imbriques 3 niveaux : classes, eleves, notes
      *----------------------------------------------------------------*
      * GROUPE     : arrays                                            *
      * COMPILEUR  : GnuCOBOL - cobc -x                                *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. CLASSES-NOTES.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
       01 SC-ECOLE.
           05 SC-CLASSE OCCURS 3 TIMES INDEXED BY WS-INDEX-CLASSE.
               10 SC-NOM-CLASSE PIC X(20) VALUE "CLASSE ".
               10 SC-ANNEE PIC 9(4) VALUE ZEROES.
               10 CL-ELEVES OCCURS 5 TIMES INDEXED BY WS-INDEX-ELEVE.
                   15 CL-NOM PIC X(20) VALUE "NOM ".
                   15 CL-PRENOM PIC X(20) VALUE "PRENOM ".
                   15 CL-AGE PIC 9(2) VALUE ZEROES.
                   15 CL-MOYENNE PIC 9(3)V99 VALUE ZEROES.
                   15 CL-NOTES OCCURS 3 TIMES
                      INDEXED BY WS-INDEX-NOTE.
                      20 CL-MATIERE PIC X(20) VALUE "MATIERE ".
                      20 CL-SCORE PIC 9(3)V99 VALUE ZEROES.

       01 WS-NOTE-SOMME PIC 9(5)V99 VALUE ZEROES.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           PERFORM VARYING WS-INDEX-CLASSE FROM 1 BY 1
               UNTIL WS-INDEX-CLASSE > 3

               MOVE "CLASSE " TO
                   SC-NOM-CLASSE(WS-INDEX-CLASSE)
               MOVE 2024 TO SC-ANNEE(WS-INDEX-CLASSE)

               PERFORM VARYING WS-INDEX-ELEVE FROM 1 BY 1
                   UNTIL WS-INDEX-ELEVE > 5

                   MOVE "NOM " TO
                       CL-NOM(WS-INDEX-CLASSE, WS-INDEX-ELEVE)
                   MOVE "PRENOM " TO
                       CL-PRENOM(WS-INDEX-CLASSE, WS-INDEX-ELEVE)
                   MOVE 18 TO
                       CL-AGE(WS-INDEX-CLASSE, WS-INDEX-ELEVE)

                   MOVE ZEROES TO WS-NOTE-SOMME
                   PERFORM VARYING WS-INDEX-NOTE FROM 1 BY 1
                       UNTIL WS-INDEX-NOTE > 3
                       MOVE 10 TO CL-SCORE(WS-INDEX-CLASSE,
                           WS-INDEX-ELEVE, WS-INDEX-NOTE)
                       ADD CL-SCORE(WS-INDEX-CLASSE,
                           WS-INDEX-ELEVE, WS-INDEX-NOTE)
                           TO WS-NOTE-SOMME
                   END-PERFORM

                   COMPUTE CL-MOYENNE(WS-INDEX-CLASSE,
                       WS-INDEX-ELEVE) = WS-NOTE-SOMME / 3

               END-PERFORM
           END-PERFORM

           DISPLAY "CLASSE 1 ELEVE 1 MOYENNE: "
               CL-MOYENNE(1, 1)

           STOP RUN.
       END PROGRAM CLASSES-NOTES.
