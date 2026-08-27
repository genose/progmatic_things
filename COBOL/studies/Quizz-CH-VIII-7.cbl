      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   CREATION DYNAMIQUE D'UN FICHIER PAR SAISIE UTILISATEUR
      *            Saisie nom/prenom, ecriture dans contacts.txt
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. SAISIE-CONTACTS.

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT CONTACTS-FILE
               ASSIGN TO "contacts.txt"
               ORGANIZATION IS LINE SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-FILE-STATUS.

       DATA DIVISION.
       FILE SECTION.

       FD CONTACTS-FILE.
       01 CONTACT-RECORD            PIC X(62).

       WORKING-STORAGE SECTION.
       01 WS-FILE-STATUS            PIC XX.

       01 WS-SAISIE-NB              PIC X(3).
       01 WS-NB-ENREG               PIC 9(3) VALUE ZEROES.
       01 WS-COMPTEUR               PIC 9(3) VALUE ZEROES.
       01 WS-NB-ECRIT               PIC 9(3) VALUE ZEROES.

       01 WS-NOM                    PIC X(30).
       01 WS-PRENOM                 PIC X(30).

       01 WS-LIGNE-CONTACT.
           05 WL-NOM                PIC X(30).
           05 FILLER                PIC X(2)  VALUE ", ".
           05 WL-PRENOM             PIC X(30).

       01 WS-AFF-CPTR               PIC ZZ9.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           PERFORM 1000-DEMANDER-NB-ENREG

           OPEN OUTPUT CONTACTS-FILE
           IF WS-FILE-STATUS NOT = "00"
               DISPLAY "ERREUR OUVERTURE contacts.txt : " WS-FILE-STATUS
               STOP RUN
           END-IF

           PERFORM VARYING WS-COMPTEUR FROM 1 BY 1
               UNTIL WS-COMPTEUR > WS-NB-ENREG
               PERFORM 2000-SAISIR-CONTACT
           END-PERFORM

           CLOSE CONTACTS-FILE

           DISPLAY SPACES
           DISPLAY "========================================"
           DISPLAY "  Fichier contacts.txt cree."
           DISPLAY "  Enregistrements ecrits : " WS-NB-ECRIT
           DISPLAY "========================================"
           STOP RUN.

      ******************************************************************
       1000-DEMANDER-NB-ENREG.
           PERFORM UNTIL WS-NB-ENREG > 0 AND WS-NB-ENREG <= 999
               DISPLAY "Combien d enregistrements voulez-vous saisir ? "
                   WITH NO ADVANCING
               ACCEPT WS-SAISIE-NB
               MOVE FUNCTION NUMVAL(WS-SAISIE-NB) TO WS-NB-ENREG
               IF WS-NB-ENREG <= 0 OR WS-NB-ENREG > 999
                   DISPLAY "  --> Valeur invalide. Entrez un nombre"
                       " entre 1 et 999."
               END-IF
           END-PERFORM.

      ******************************************************************
       2000-SAISIR-CONTACT.
           MOVE WS-COMPTEUR TO WS-AFF-CPTR
           DISPLAY SPACES
           DISPLAY "--- Contact " WS-AFF-CPTR " ---"

           PERFORM 2100-SAISIR-NOM
           PERFORM 2200-SAISIR-PRENOM

           MOVE WS-NOM    TO WL-NOM
           MOVE WS-PRENOM TO WL-PRENOM
           WRITE CONTACT-RECORD FROM WS-LIGNE-CONTACT
           IF WS-FILE-STATUS NOT = "00"
               DISPLAY "ERREUR ECRITURE contact " WS-AFF-CPTR
                   " : " WS-FILE-STATUS
           ELSE
               ADD 1 TO WS-NB-ECRIT
           END-IF.

       2100-SAISIR-NOM.
           MOVE SPACES TO WS-NOM
           PERFORM UNTIL WS-NOM NOT = SPACES
               DISPLAY "  Nom     : " WITH NO ADVANCING
               ACCEPT WS-NOM
               IF WS-NOM = SPACES
                   DISPLAY "  --> Le nom ne peut pas etre vide."
               END-IF
           END-PERFORM.

       2200-SAISIR-PRENOM.
           MOVE SPACES TO WS-PRENOM
           PERFORM UNTIL WS-PRENOM NOT = SPACES
               DISPLAY "  Prenom  : " WITH NO ADVANCING
               ACCEPT WS-PRENOM
               IF WS-PRENOM = SPACES
                   DISPLAY "  --> Le prenom ne peut pas etre vide."
               END-IF
           END-PERFORM.

       END PROGRAM SAISIE-CONTACTS.
