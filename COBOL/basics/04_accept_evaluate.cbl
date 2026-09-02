      *================================================================*
      * PROGRAMME  : ACCPTST                                           *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.0                                               *
      *----------------------------------------------------------------*
      * DESCRIPTION: ACCEPT + EVALUATE : retraite par age et genre     *
      *----------------------------------------------------------------*
      * GROUPE     : basics                                            *
      * COMPILEUR  : GnuCOBOL - cobc -x                                *
      *================================================================*
       IDENTIFICATION DIVISION.
        PROGRAM-ID. ACCPTST.
        ENVIRONMENT DIVISION.
        DATA DIVISION.
        WORKING-STORAGE SECTION.
        01 AGE                       PIC 9(3).
        01 GENDER                    PIC X(1).
        PROCEDURE DIVISION.
            ACCEPT AGE.
            ACCEPT GENDER.
            EVALUATE TRUE ALSO TRUE
               WHEN AGE > 60 ALSO GENDER = 'M'
                    DISPLAY 'THE MAN IS RETIRED   '
               WHEN AGE > 60 ALSO GENDER = 'F'
                    DISPLAY 'THE WOMAN IS RETIRED  '
               WHEN AGE <= 60 ALSO GENDER = 'M'
                    DISPLAY 'THE MAN IS NOT RETIRED   '
               WHEN AGE <= 60 ALSO GENDER = 'F'
                    DISPLAY 'THE WOMAN IS NOT RETIRED  '
               WHEN OTHER
                    DISPLAY 'INVALID INPUT      '
                    DISPLAY 'AGE =' AGE ' and GENDER =' GENDER
            END-EVALUATE.
            STOP RUN.
