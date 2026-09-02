      *================================================================*
      * PROGRAMME  : CLASSES-NOTES                                     *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.0                                               *
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
           05 SC-CLASSE OCCURS 30 TIMES INDEXED BY WS-INDEX-CLASSE.
               10 SC-NOM-CLASSE PIC X(20) VALUE "CLASSE ".
               10 SC-ANNEE PIC 9(4) VALUE ZEROES.
               10 CL-ELEVES OCCURS 25 TIMES INDEXED BY WS-INDEX-ELEVE.
                   15 CL-NOM PIC X(20) VALUE "NOM ".
                        88 CL-FIN-ClASSE VALUE SPACE.
                   15 CL-PRENOM PIC X(20) VALUE "PRENOM ".
                   15 CL-AGE PIC 9(2) VALUE ZEROES.
                   15 CL-MOYENNE PIC 9(3)V99 VALUE ZEROES.
                   15 CL-NOTES OCCURS 5 TIMES INDEXED BY WS-INDEX-NOTE.
                      20 CL-MATIERE PIC X(20) VALUE "MATIERE ".
                      20 CL-COEFFICIENT PIC 9(2) VALUE ZEROES.
                      20 CL-SCORE PIC 9(3)V99 VALUE ZEROES.
                           88 ABSENCE-NOTE VALUE 0.
                           88 NOTE-VALIDE VALUE 1 THRU 20.
                           88 NOTE-EXCEPTIONNELLE VALUE 21 THRU 30.

       01 WS-NOTE-VALUE PIC 9(2) VALUE ZEROES.
       01 WS-NOTE-SOMME PIC 9(3) VALUE ZEROES.
       01 WS-NOTE-MOYENNE PIC 9(3)V99 VALUE ZEROES.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           SET WS-INDEX-CLASSE TO 1
           SET WS-INDEX-ELEVE TO 0
           SET WS-INDEX-NOTE TO 0

           PERFORM VARYING WS-INDEX FROM 1 BY 1
            UNTIL WS-INDEX > LEGNTH OF SC-CLASSE

                 MOVE "CLASSE " TO SC-NOM-CLASSE(WS-INDEX)
                 MOVE 2024 TO SC-ANNEE(WS-INDEX)
                 PERFORM VARYING WS-NOTE-INDEX FROM 1 BY 1 UNTIL WS-NOTE-INDEX > 25
                     MOVE "NOM " TO CL-NOM(WS-NOTE-INDEX)
                     MOVE "PRENOM " TO CL-PRENOM(WS-NOTE-INDEX)
                     MOVE 10 TO CL-AGE(WS-NOTE-INDEX)
                     PERFORM VARYING WS-NOTE-VALUE FROM 1 BY 1
                     UNTIL WS-NOTE-VALUE > LENGTH OF CL-NOTES
                         MOVE WS-NOTE-VALUE TO
                           CL-NOTE(WS-NOTE-VALUE)(WS-NOTE-INDEX)
                         ADD CL-NOTE(WS-NOTE-VALUE)(WS-NOTE-INDEX)
                           TO WS-NOTE-SOMME
                     END-PERFORM
                     COMPUTE CL-MOYENNE(WS-NOTE-INDEX)
                           = WS-NOTE-SOMME / LENGTH OF CL-NOTES
                     MOVE ZEROES TO WS-NOTE-MOYENNE
                     MOVE ZEROES TO WS-NOTE-SOMME


                 END-PERFORM
           END-PERFORM
            STOP RUN.
       END PROGRAM YOUR-PROGRAM-NAME.
