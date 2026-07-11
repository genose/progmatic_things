      ******************************************************************
      * Author: COTILLARD SEBASTIEN
      * Date: 2026-06-19
      * Purpose: JEU DU PENDU
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. JEU-DU-PENDU-COBOL.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
      * 2023096

      * ** PARTIE HAUTE DU PENDU
       01 WS-MASQUE-STEP-POUTRE-HAUT-NO.
          05 PIC X(32) VALUE SPACES.

          05 PIC X(32) VALUE "_________   ".
          05 PIC X(32) VALUE "=========   ".
          05 PIC X(32) VALUE "||//    |   ".
       01 WS-MASQUE-STEP-POUTRE-HAUT-YES.
          05 PIC X(32) VALUE SPACES.
          05 PIC X(32) VALUE SPACES.
          05 PIC X(32) VALUE SPACES.
          05 PIC X(32) VALUE SPACES.
          05 PIC X(32) VALUE SPACES.
      * ** PARTIE DU MILLIEU DU PENDU
       01 WS-MASQUE-STEP-MIDDLE-NO-1.
          05 PIC X(32) VALUE "||/".
          05 PIC X(32) VALUE "||".
          05 PIC X(32) VALUE "||".
          05 PIC X(32) VALUE "||".
          05 PIC X(32) VALUE "||".
          05 PIC X(32) VALUE "||".
          05 PIC X(32) VALUE "||".
          05 PIC X(32) VALUE "||".
          05 PIC X(32) VALUE "||".
          05 PIC X(32) VALUE "||".

       01 WS-MASQUE-STEP-MIDDLE-NO-2.
          05 PIC X(32) VALUE "||/    \\|//   ".
          05 PIC X(32) VALUE "||     x | x   ".
          05 PIC X(32) VALUE "||      /-\  ".
          05 PIC X(32) VALUE "||       |   ".
          05 PIC X(32) VALUE "||     /| |\ ".
          05 PIC X(32) VALUE "||    | | | | ".
          05 PIC X(32) VALUE "||    ' | | ' ".
          05 PIC X(32) VALUE "||     / | \ ".
          05 PIC X(32) VALUE "||     |   | ".
          05 PIC X(32) VALUE "||     /   \ ".

       01 WS-MASQUE-STEP-MIDDLE-YES.
            05   PIC X(32) VALUE ".".
            05   PIC X(32) VALUE ".".
            05   PIC X(32) VALUE ".".
            05   PIC X(32) VALUE ".".
            05   PIC X(32) VALUE ".".
            05   PIC X(32) VALUE ".".
            05   PIC X(32) VALUE ".".
            05   PIC X(32) VALUE ".".
            05   PIC X(32) VALUE ".".
            05   PIC X(32) VALUE ".".

      * ** PARTIE BASSE DU PENDU
       01 WS-MASQUE-STEP-POUTRE-BAS-NO.
           05 PIC X(32) VALUE "||    _____________".
           05 PIC X(32) VALUE "||   / ________  /|".
           05 PIC X(32) VALUE "||  / / / / / / / /".
           05 PIC X(32) VALUE "|| / /_/_/_/_/ / /!".
           05 PIC X(32) VALUE "||/___________/ /!!".
           05 PIC X(32) VALUE "||____________|/".
           05 PIC X(32) VALUE "!! !!        !!".

       01 WS-MASQUE-STEP-POUTRE-BAS-YES.
           05 PIC X(32) VALUE SPACES.
           05 PIC X(32) VALUE SPACES.
           05 PIC X(32) VALUE SPACES.
           05 PIC X(32) VALUE SPACES.
           05 PIC X(32) VALUE SPACES.
           05 PIC X(32) VALUE SPACES.
           05 PIC X(32) VALUE SPACES.

      * ** Creation de la variable stockant les etapes du pendu
       01 WS-GAME-PENDU.
           05 WS-MASQUE-POUTRE-HAUT       PIC 9     VALUE 1.
               88 WS-MASQUE-POUTRE-HAUT-YES         VALUE 1.
               88 WS-MASQUE-POUTRE-HAUT-NO          VALUE 0.
           05 WS-MASQUE-MIDDLE           PIC 9     VALUE 1.
               88 WS-MASQUE-MIDDLE-YES              VALUE 1.
               88 WS-MASQUE-MIDDLE-NO               VALUE 0.
           05 WS-MASQUE-POUTRE-BAS        PIC 9     VALUE 1.
               88 WS-MASQUE-POUTRE-BAS-YES          VALUE 1.
               88 WS-MASQUE-POUTRE-BAS-NO           VALUE 0.
           05 WS-STEP-GAME-ETAT-PERDU              VALUE 6.
               10 WS-STEP-GAME-ETAT-HAUT  PIC 9     VALUE 1.
               10 WS-STEP-GAME-ETAT-BAS   PIC 9     VALUE 2.
               10 WS-STEP-GAME-ETAT-MID   PIC 9     VALUE 3.
           05 WS-STEP-NB-TRY             PIC 9     VALUE ZEROES.
           05 WS-STEP-NB-TRY-RESTANT     PIC 9     VALUE ZEROES.
           05 WS-STEP-NB-TRY-MAX         PIC 9     VALUE 5.
       01 WS-RESUMES.
          05 WS-RESUME-HAUT PIC X(160) VALUE SPACES.
          05 WS-RESUME-MID PIC X(320) VALUE SPACES.
          05 WS-RESUME-BAS  PIC X(224) VALUE SPACES.

       01 WS-SCREEN-RESUME PIC X(800) VALUE SPACES.
       01 WS-SCREEN-RESUME-LINE PIC X(32) VALUE SPACES.
      * ** ****************************
       01 WS-ENTER-LETTRE               PIC X     VALUE SPACES.
      * ** ****************************
       01 WS-ENTER-LETTRE-VALID-FOUND   PIC 9     VALUE ZERO.
      * ** ****************************
       01 WS-LETTRE-MISTERE-TROUVE      PIC X(32) VALUE LOW-VALUE.
      * ** ****************************
      * ** MOT MISTERE A TROUVER ******
       01 WS-LETTRE-MISTERE             PIC X(32)
                                                  VALUE
             "**COBOL IS HIGH VALUE LANGUAGE**".
       01 WS-LETTRE-MISTERE-FOUND       PIC 9     VALUE ZEROES.
      * ** ****************************
       77 WS-INDEX                      PIC 9(4)  VALUE ZEROES.
       77 WS-INDEX-CHAR                 PIC 9(4)  VALUE ZEROES.
       77 WS-INDEX-LIGNE                PIC 9(4)  VALUE ZEROES.
      * ** ****************************


      * ** SCREEN SECTION.
       01 CLEAR-SCREEN .
            05 CLEAR-SCREEN-LIGNE OCCURS 20 TIMES.
                   10 CLEAR-SCREEN-COLS OCCURS 80 TIMES.
                         15 SPERATEUR-1 PIC X VALUE "*".

       01 SCREEN-RESUME.
            05 SCREEN-RESUME-LIGNE OCCURS 20 TIMES
               PIC X(80) VALUE SPACES.
      * ** ****************************
       01 WS-INDEX-RESUME-LIGNE         PIC 9(4)  VALUE ZEROES.
       01 WS-INDEX-RESUME-CHAR          PIC 9(4)  VALUE ZEROES.
      * ** ****************************
       PROCEDURE DIVISION.
            INITIALIZE WS-LETTRE-MISTERE-TROUVE.
            INITIALIZE WS-RESUME-HAUT.
            INITIALIZE WS-RESUME-MID.
            INITIALIZE WS-RESUME-BAS.
            INITIALIZE WS-SCREEN-RESUME.
            INITIALIZE CLEAR-SCREEN.
            INITIALIZE WS-ENTER-LETTRE.
            INITIALIZE WS-ENTER-LETTRE-VALID-FOUND.
            INITIALIZE WS-STEP-NB-TRY.
            INITIALIZE WS-STEP-NB-TRY-RESTANT.
            INITIALIZE WS-MASQUE-POUTRE-BAS.
            INITIALIZE WS-MASQUE-MIDDLE.
            INITIALIZE WS-MASQUE-POUTRE-HAUT.
            DISPLAY CLEAR-SCREEN.

            MOVE 1 TO WS-MASQUE-POUTRE-HAUT.
            MOVE 1 TO WS-MASQUE-MIDDLE.
            MOVE 1 TO WS-MASQUE-POUTRE-BAS.
            MOVE 6 TO WS-STEP-GAME-ETAT-PERDU.
            move 1 TO WS-STEP-GAME-ETAT-HAUT.
            move 2 TO WS-STEP-GAME-ETAT-BAS.
            MOVE 3 TO WS-STEP-GAME-ETAT-MID.
            MOVE 5 TO WS-STEP-NB-TRY-MAX.
            MOVE 5 TO WS-STEP-NB-TRY-RESTANT.
            MOVE 0 TO WS-STEP-NB-TRY.
            DISPLAY "-------------------------------------------"
            DISPLAY "** jeux du pendu **".
            DISPLAY '** LETTRE "+" ou "-" pour quitter le jeu **'.
            DISPLAY "-------------------------------------------"

      * **     PERFORM CLEAR-SCREEN-PROCEDURE.


            PERFORM MAIN-PROCEDURE.
            DISPLAY "Fin du programme".
            STOP RUN.
       CLEAR-SCREEN-PROCEDURE.
             MOVE 0 TO WS-INDEX.
             MOVE 0 TO WS-INDEX-CHAR.
             MOVE 0 TO WS-INDEX-LIGNE.

             PERFORM VARYING WS-INDEX FROM 1 BY 1
             UNTIL WS-INDEX GREATER THAN 20
              DISPLAY '-'
      * **        perform VARYING WS-INDEX-CHAR FROM 1 BY 1
      * **        UNTIL WS-INDEX-CHAR  GREATER THAN 80
      * **
      * **             DISPLAY SPERATEUR-1(WS-INDEX,WS-INDEX-CHAR)
      * **                   WITH NO ADVANCING
      * **       END-PERFORM
      * **       MOVE 0 TO WS-INDEX-CHAR

             END-PERFORM
       GOBACK.

       MAIN-PROCEDURE.
            DISPLAY CLEAR-SCREEN.

            MOVE SPACES TO WS-ENTER-LETTRE
            MOVE ZEROES TO WS-ENTER-LETTRE-VALID-FOUND
            DISPLAY CLEAR-SCREEN.
            DISPLAY "-------------------------------------------"
            DISPLAY "-------------------------------------------"
            DISPLAY " ******************************************"
            DISPLAY " ********* TROUVER LE MOT MYSTERE *********"
            DISPLAY " ******************************************"
            DISPLAY "-------------------------------------------"
            DISPLAY "-------------------------------------------"
      * **  WITH NO ADVANCING.
            DISPLAY "Veuillez saisir une lettre : "
            ACCEPT WS-ENTER-LETTRE

      * ** TEST DE LA VALIDITE DE LA LETTRE SAISIE
            IF WS-ENTER-LETTRE IS NUMERIC
      * **        OR WS-ENTER-LETTRE EQUAL SPACES

              DISPLAY "La lettre saisie n'est pas valide"
      * **        MOVE 0 TO WS-ENTER-LETTRE-VALID-FOUND
               GO TO  MAIN-PROCEDURE
            END-IF

            IF WS-ENTER-LETTRE IS ALPHABETIC-LOWER
               MOVE FUNCTION UPPER-CASE(WS-ENTER-LETTRE)
                  TO WS-ENTER-LETTRE
            END-IF

            IF WS-ENTER-LETTRE EQUAL "+" OR WS-ENTER-LETTRE EQUAL "-"
               DISPLAY " .... EXIT ...."
               GO TO  PROCEDURE-EXIT
            END-IF
      * ** COMPTAGE DU NOMBRE DE LETTRE VALIDE DANS LE MOT MYSTERE
            INSPECT WS-LETTRE-MISTERE
              TALLYING WS-ENTER-LETTRE-VALID-FOUND
              FOR ALL WS-ENTER-LETTRE.


            DISPLAY " ++ WS-STEP-GAME-ETAT-MID : " WS-STEP-GAME-ETAT-MID
                      " : " WS-MASQUE-MIDDLE
      * ** TEST DE LA VALIDITE DE LA LETTRE SAISIE
            IF WS-ENTER-LETTRE-VALID-FOUND EQUAL ZEROES

              ADD 1 TO WS-STEP-NB-TRY

              EVALUATE TRUE
              WHEN WS-STEP-NB-TRY-RESTANT EQUAL 5
                   MOVE 0 TO WS-MASQUE-POUTRE-BAS
                   move 0 to WS-STEP-GAME-ETAT-BAS
              WHEN WS-STEP-NB-TRY-RESTANT EQUAL 4
                 OR WS-STEP-NB-TRY-RESTANT EQUAL 3
                 or WS-STEP-NB-TRY-RESTANT EQUAL 1

                   IF WS-STEP-GAME-ETAT-MID GREATER THAN ZEROES
                      SUBTRACT 1 FROM WS-STEP-GAME-ETAT-MID
      * **      DISPLAY " -- WS-STEP-GAME-ETAT-MID : " WS-STEP-GAME-ETAT-MID
      * **                " : " WS-MASQUE-MIDDLE
                   END-IF

                 MOVE 0 TO WS-MASQUE-MIDDLE
            DISPLAY " -- WS-STEP-GAME-ETAT-MID : " WS-STEP-GAME-ETAT-MID
              " : " WS-MASQUE-MIDDLE

              WHEN WS-STEP-NB-TRY-RESTANT EQUAL 2
                   MOVE 0 TO WS-MASQUE-POUTRE-HAUT
                   move 0 to WS-STEP-GAME-ETAT-HAUT
              WHEN OTHER
                   DISPLAY "Vous avez perdu !"
                   PERFORM PROCEDURE-EXIT
              END-EVALUATE
            END-IF
      * ** CREATION DE L'AFFICHAGE MOT MYSTERE
            MOVE 0 TO WS-INDEX.
      * **      MOVE SPACES TO WS-LETTRE-MISTERE-TROUVE
            PERFORM VARYING WS-INDEX FROM 1 BY 1 UNTIL WS-INDEX
              GREATER THAN LENGTH OF WS-LETTRE-MISTERE

                   IF WS-LETTRE-MISTERE(WS-INDEX:1) EQUAL
                      WS-ENTER-LETTRE
                      MOVE WS-ENTER-LETTRE TO
                         WS-LETTRE-MISTERE-TROUVE(WS-INDEX:1)
                   ELSE
                      IF WS-LETTRE-MISTERE-TROUVE(WS-INDEX:1)
                         EQUAL SPACES
                         MOVE "@" TO WS-LETTRE-MISTERE-TROUVE(WS-INDEX:1
                            )
                      END-IF
                   END-IF
            END-PERFORM

      * ** Affichage du pendu selon les etapes
            DISPLAY " NB DE LETTRE TROUVE : "WS-ENTER-LETTRE-VALID-FOUND
            DISPLAY " >>>>> " WS-LETTRE-MISTERE-TROUVE " <<<<<"
            DISPLAY "-------------------------------------------"


            IF WS-ENTER-LETTRE-VALID-FOUND EQUAL ZEROES
               DISPLAY "La lettre saisie n'est pas dans le mot mystere"
      * **      ELSE
      * **         DISPLAY "La lettre saisie est dans le mot mystere"
            END-IF
      * **      DISPLAY "Vous avez saisi : " WS-ENTER-LETTRE
            DISPLAY "Vous avez encore " WS-STEP-NB-TRY-RESTANT " essais"


            DISPLAY "-------------------------------------------"

      * **     MOVE SPACES TO WS-RESUME

      * ** AFFICHAGE PARTIE HAUTE DU PENDU
            IF WS-MASQUE-POUTRE-HAUT EQUAL ZEROES
                 MOVE WS-MASQUE-STEP-POUTRE-HAUT-NO TO WS-RESUME-HAUT
            ELSE
                 MOVE WS-MASQUE-STEP-POUTRE-HAUT-YES TO WS-RESUME-HAUT
            END-IF
      * **  AFFICHAGE DE LA PARTIE CENTRAL DU PENDU
            IF WS-MASQUE-MIDDLE EQUAL ZEROES

               IF WS-STEP-GAME-ETAT-MID EQUAL ZEROES
                 MOVE WS-MASQUE-STEP-MIDDLE-NO-2 TO WS-RESUME-MID
                 MOVE 0 TO WS-STEP-GAME-ETAT-PERDU
               ELSE
                 MOVE  WS-MASQUE-STEP-MIDDLE-NO-1 TO WS-RESUME-MID
               END-IF
            ELSE
      * **     DISPLAY "WS-STEP-GAME-ETAT-MID : " WS-STEP-GAME-ETAT-MID
      * **           " : " WS-MASQUE-MIDDLE
                 MOVE WS-MASQUE-STEP-MIDDLE-YES TO WS-RESUME-MID
      * **          DISPLAY" --- " WS-MASQUE-STEP-MIDDLE-YES
      * **           DISPLAY "WS-RESUME-MID : " WS-RESUME-MID

            END-IF
      * ** AFFICHAGE DE LA PARTIE BASSE DU PENDU
            IF WS-MASQUE-POUTRE-BAS EQUAL 0
                 MOVE WS-MASQUE-STEP-POUTRE-BAS-NO TO WS-RESUME-BAS
            ELSE
                 MOVE WS-MASQUE-STEP-POUTRE-BAS-YES TO WS-RESUME-BAS
            END-IF
      * ** AFFICHAGE DU PENDU SUR UNE SEULE LIGNE METHODE 1

            STRING WS-RESUME-HAUT DELIMITED BY SIZE
               WS-RESUME-MID DELIMITED BY SIZE
               WS-RESUME-BAS DELIMITED BY SIZE
               INTO WS-SCREEN-RESUME
            END-STRING.
      * **      DISPLAY "-------------------------------------------"
      * **     DISPLAY "----" WS-SCREEN-RESUME
      * **     DISPLAY "-------------------------------------------"
      * ** AFFICHAGE DU PENDU SUR PLUSIEURS LIGNES METHODE 2
            MOVE 0 TO WS-INDEX.
            MOVE 1 TO WS-INDEX-CHAR.
            MOVE 1 TO WS-INDEX-LIGNE.

            COMPUTE WS-INDEX =  (LENGTH OF WS-SCREEN-RESUME / 32)
      * **      DISPLAY "LENGTH    : " LENGTH OF WS-SCREEN-RESUME
      * **      DISPLAY "NB LIGNES : " WS-INDEX

            PERFORM  WS-INDEX TIMES

            MOVE WS-SCREEN-RESUME(WS-INDEX-CHAR:32)
             TO WS-SCREEN-RESUME-LINE

                 DISPLAY " LIGNE : " WS-INDEX-LIGNE "@" WS-INDEX-CHAR
                 ">> " WS-SCREEN-RESUME-LINE
                 ADD 32 TO WS-INDEX-CHAR
                 ADD 1 TO WS-INDEX-LIGNE

            END-PERFORM

      * ** TEST DE LA FIN DU JEU
             COMPUTE WS-STEP-NB-TRY-RESTANT =
             WS-STEP-NB-TRY-MAX - WS-STEP-NB-TRY

            DISPLAY " Etat : " WS-STEP-GAME-ETAT-PERDU
                 " : " WS-STEP-NB-TRY-RESTANT
            IF WS-STEP-GAME-ETAT-PERDU EQUAL ZEROES
               DISPLAY "Vous avez perdu ! ... " WS-STEP-GAME-ETAT-PERDU
               PERFORM PROCEDURE-EXIT
            END-IF
      * ** TEST DE LA FIN DU JEU

            IF WS-STEP-NB-TRY-RESTANT EQUAL ZEROES
               DISPLAY "Vous avez perdu ! *** " WS-STEP-NB-TRY-RESTANT
               PERFORM PROCEDURE-EXIT
            END-IF

            GO TO MAIN-PROCEDURE.
       PROCEDURE-EXIT.
           DISPLAY "Fin du programme"
           STOP RUN.
       END PROGRAM JEU-DU-PENDU-COBOL.
