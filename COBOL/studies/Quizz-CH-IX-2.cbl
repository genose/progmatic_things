      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   GESTION BIBLIOTHEQUE PAR SOUS-PROGRAMMES
      *            SP-AJOUTER-LIVRE   : ajouter un livre au catalogue
      *            SP-EMPRUNTER-LIVRE : enregistrer un emprunt
      *            SP-RETOURNER-LIVRE : retourner un livre emprunte
      *            SP-RECHERCHER-LIVRE: rechercher par titre/auteur
      *            SP-LISTE-EMPRUNTS  : lister les emprunts actifs
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. MAIN-BIBLIO.

       DATA DIVISION.
       WORKING-STORAGE SECTION.

      * Catalogue des livres (max 50)
       01 WS-CATALOGUE.
           05 WS-NB-LIVRES          PIC 9(3) VALUE 0.
           05 WS-LIVRE              OCCURS 50 TIMES.
               10 LIV-ID            PIC 9(5).
               10 LIV-TITRE         PIC X(40).
               10 LIV-AUTEUR        PIC X(30).
               10 LIV-DISPO         PIC 9.
               10 LIV-NB-EMPRUNTS   PIC 9(3).

      * Table des emprunts actifs (max 30)
       01 WS-EMPRUNTS.
           05 WS-NB-EMPRUNTS        PIC 9(3) VALUE 0.
           05 WS-EMPRUNT            OCCURS 30 TIMES.
               10 EMP-ID            PIC 9(5).
               10 EMP-LIVRE-ID      PIC 9(5).
               10 EMP-MEMBRE        PIC X(30).
               10 EMP-ACTIF         PIC 9.

       01 WS-PROCHAIN-LIV-ID        PIC 9(5) VALUE 1.
       01 WS-PROCHAIN-EMP-ID        PIC 9(5) VALUE 1.

      * Variables de saisie et de retour
       01 WS-STATUS                 PIC 9.
       01 WS-TITRE-SAISIE           PIC X(40).
       01 WS-AUTEUR-SAISIE          PIC X(30).
       01 WS-MEMBRE-SAISIE          PIC X(30).
       01 WS-ID-SAISIE              PIC X(6).
       01 WS-ID-NUM                 PIC 9(5).
       01 WS-MOTCLE-SAISIE          PIC X(30).
       01 WS-CHOIX                  PIC X.
       01 WS-CONTINUER              PIC 9 VALUE 1.
          88 QUITTER                VALUE 0.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           PERFORM 0000-INIT-CATALOGUE

           PERFORM UNTIL QUITTER
               DISPLAY SPACES
               DISPLAY "========================================"
               DISPLAY "         BIBLIOTHEQUE - MENU            "
               DISPLAY "========================================"
               DISPLAY "  1. Ajouter un livre"
               DISPLAY "  2. Emprunter un livre"
               DISPLAY "  3. Retourner un livre"
               DISPLAY "  4. Rechercher un livre"
               DISPLAY "  5. Liste des emprunts actifs"
               DISPLAY "  0. Quitter"
               DISPLAY "Votre choix : " WITH NO ADVANCING
               ACCEPT WS-CHOIX

               EVALUATE WS-CHOIX
                   WHEN "1"
                       DISPLAY "Titre  : " WITH NO ADVANCING
                       ACCEPT WS-TITRE-SAISIE
                       DISPLAY "Auteur : " WITH NO ADVANCING
                       ACCEPT WS-AUTEUR-SAISIE
                       CALL "SP-AJOUTER-LIVRE" USING
                           BY REFERENCE WS-CATALOGUE
                           BY REFERENCE WS-TITRE-SAISIE
                           BY REFERENCE WS-AUTEUR-SAISIE
                           BY REFERENCE WS-PROCHAIN-LIV-ID
                           BY REFERENCE WS-STATUS
                       END-CALL

                   WHEN "2"
                       DISPLAY "ID du livre   : " WITH NO ADVANCING
                       ACCEPT WS-ID-SAISIE
                       MOVE FUNCTION NUMVAL(WS-ID-SAISIE) TO WS-ID-NUM
                       DISPLAY "Nom du membre : " WITH NO ADVANCING
                       ACCEPT WS-MEMBRE-SAISIE
                       CALL "SP-EMPRUNTER-LIVRE" USING
                           BY REFERENCE WS-CATALOGUE
                           BY REFERENCE WS-EMPRUNTS
                           BY REFERENCE WS-ID-NUM
                           BY REFERENCE WS-MEMBRE-SAISIE
                           BY REFERENCE WS-PROCHAIN-EMP-ID
                           BY REFERENCE WS-STATUS
                       END-CALL

                   WHEN "3"
                       DISPLAY "ID emprunt a retourner : "
                           WITH NO ADVANCING
                       ACCEPT WS-ID-SAISIE
                       MOVE FUNCTION NUMVAL(WS-ID-SAISIE) TO WS-ID-NUM
                       CALL "SP-RETOURNER-LIVRE" USING
                           BY REFERENCE WS-CATALOGUE
                           BY REFERENCE WS-EMPRUNTS
                           BY REFERENCE WS-ID-NUM
                           BY REFERENCE WS-STATUS
                       END-CALL

                   WHEN "4"
                       DISPLAY "Mot-cle (titre ou auteur) : "
                           WITH NO ADVANCING
                       ACCEPT WS-MOTCLE-SAISIE
                       CALL "SP-RECHERCHER-LIVRE" USING
                           BY REFERENCE WS-CATALOGUE
                           BY REFERENCE WS-MOTCLE-SAISIE
                           BY REFERENCE WS-STATUS
                       END-CALL

                   WHEN "5"
                       CALL "SP-LISTE-EMPRUNTS" USING
                           BY REFERENCE WS-CATALOGUE
                           BY REFERENCE WS-EMPRUNTS
                       END-CALL

                   WHEN "0"
                       MOVE 0 TO WS-CONTINUER

                   WHEN OTHER
                       DISPLAY "  --> Choix invalide."
               END-EVALUATE
           END-PERFORM

           DISPLAY SPACES
           DISPLAY "Au revoir."
           STOP RUN.

      * Chargement de quelques livres par defaut
       0000-INIT-CATALOGUE.
           MOVE "Le Petit Prince"           TO WS-TITRE-SAISIE
           MOVE "Antoine de Saint-Exupery"  TO WS-AUTEUR-SAISIE
           CALL "SP-AJOUTER-LIVRE" USING BY REFERENCE WS-CATALOGUE
               WS-TITRE-SAISIE WS-AUTEUR-SAISIE
               WS-PROCHAIN-LIV-ID WS-STATUS END-CALL

           MOVE "1984"                      TO WS-TITRE-SAISIE
           MOVE "George Orwell"             TO WS-AUTEUR-SAISIE
           CALL "SP-AJOUTER-LIVRE" USING BY REFERENCE WS-CATALOGUE
               WS-TITRE-SAISIE WS-AUTEUR-SAISIE
               WS-PROCHAIN-LIV-ID WS-STATUS END-CALL

           MOVE "Les Miserables"            TO WS-TITRE-SAISIE
           MOVE "Victor Hugo"               TO WS-AUTEUR-SAISIE
           CALL "SP-AJOUTER-LIVRE" USING BY REFERENCE WS-CATALOGUE
               WS-TITRE-SAISIE WS-AUTEUR-SAISIE
               WS-PROCHAIN-LIV-ID WS-STATUS END-CALL

           MOVE "Algorithmes en COBOL"      TO WS-TITRE-SAISIE
           MOVE "Jean Dupont"               TO WS-AUTEUR-SAISIE
           CALL "SP-AJOUTER-LIVRE" USING BY REFERENCE WS-CATALOGUE
               WS-TITRE-SAISIE WS-AUTEUR-SAISIE
               WS-PROCHAIN-LIV-ID WS-STATUS END-CALL
           DISPLAY "Catalogue initialise (" WS-NB-LIVRES " livres)."
           MOVE 0 TO WS-STATUS.

       END PROGRAM MAIN-BIBLIO.

      ******************************************************************
      * SP-AJOUTER-LIVRE
      * Status : 0=OK, 1=catalogue plein, 2=titre ou auteur vide
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. SP-AJOUTER-LIVRE.

       DATA DIVISION.
       LINKAGE SECTION.
       01 LK-CATALOGUE.
           05 LK-NB-LIVRES          PIC 9(3).
           05 LK-LIVRE              OCCURS 50 TIMES.
               10 LIV-ID            PIC 9(5).
               10 LIV-TITRE         PIC X(40).
               10 LIV-AUTEUR        PIC X(30).
               10 LIV-DISPO         PIC 9.
               10 LIV-NB-EMPRUNTS   PIC 9(3).
       01 LK-TITRE                  PIC X(40).
       01 LK-AUTEUR                 PIC X(30).
       01 LK-PROCHAIN-ID            PIC 9(5).
       01 LK-STATUS                 PIC 9.

       PROCEDURE DIVISION USING
           LK-CATALOGUE LK-TITRE LK-AUTEUR LK-PROCHAIN-ID LK-STATUS.

           IF LK-TITRE = SPACES OR LK-AUTEUR = SPACES
               DISPLAY "  ERREUR : titre et auteur obligatoires."
               MOVE 2 TO LK-STATUS
           ELSE IF LK-NB-LIVRES >= 50
               DISPLAY "  ERREUR : catalogue plein (50 livres max)."
               MOVE 1 TO LK-STATUS
           ELSE
               ADD 1 TO LK-NB-LIVRES
               MOVE LK-PROCHAIN-ID    TO LIV-ID(LK-NB-LIVRES)
               MOVE LK-TITRE          TO LIV-TITRE(LK-NB-LIVRES)
               MOVE LK-AUTEUR         TO LIV-AUTEUR(LK-NB-LIVRES)
               MOVE 1                 TO LIV-DISPO(LK-NB-LIVRES)
               MOVE 0                 TO LIV-NB-EMPRUNTS(LK-NB-LIVRES)
               ADD 1 TO LK-PROCHAIN-ID
               DISPLAY "  Livre ajoute [ID:" LIV-ID(LK-NB-LIVRES)
                   "] " LK-TITRE
               MOVE 0 TO LK-STATUS
           END-IF
           GOBACK.

       END PROGRAM SP-AJOUTER-LIVRE.

      ******************************************************************
      * SP-EMPRUNTER-LIVRE
      * Status : 0=OK, 2=livre introuvable, 3=deja emprunte,
      *          4=emprunts pleins, 5=membre vide
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. SP-EMPRUNTER-LIVRE.

       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 WS-IDX                    PIC 9(3) VALUE 0.
       01 WS-POS-LIVRE              PIC 9(3) VALUE 0.

       LINKAGE SECTION.
       01 LK-CATALOGUE.
           05 LK-NB-LIVRES          PIC 9(3).
           05 LK-LIVRE              OCCURS 50 TIMES.
               10 LIV-ID            PIC 9(5).
               10 LIV-TITRE         PIC X(40).
               10 LIV-AUTEUR        PIC X(30).
               10 LIV-DISPO         PIC 9.
               10 LIV-NB-EMPRUNTS   PIC 9(3).
       01 LK-EMPRUNTS.
           05 LK-NB-EMPRUNTS        PIC 9(3).
           05 LK-EMPRUNT            OCCURS 30 TIMES.
               10 EMP-ID            PIC 9(5).
               10 EMP-LIVRE-ID      PIC 9(5).
               10 EMP-MEMBRE        PIC X(30).
               10 EMP-ACTIF         PIC 9.
       01 LK-LIVRE-ID               PIC 9(5).
       01 LK-MEMBRE                 PIC X(30).
       01 LK-PROCHAIN-EMP-ID        PIC 9(5).
       01 LK-STATUS                 PIC 9.

       PROCEDURE DIVISION USING LK-CATALOGUE LK-EMPRUNTS
           LK-LIVRE-ID LK-MEMBRE LK-PROCHAIN-EMP-ID LK-STATUS.

           IF LK-MEMBRE = SPACES
               DISPLAY "  ERREUR : nom du membre obligatoire."
               MOVE 5 TO LK-STATUS
               GOBACK
           END-IF

           MOVE 0 TO WS-POS-LIVRE
           PERFORM VARYING WS-IDX FROM 1 BY 1
               UNTIL WS-IDX > LK-NB-LIVRES
               IF LIV-ID(WS-IDX) = LK-LIVRE-ID
                   MOVE WS-IDX TO WS-POS-LIVRE
               END-IF
           END-PERFORM

           IF WS-POS-LIVRE = 0
               DISPLAY "  ERREUR : livre ID "
                   LK-LIVRE-ID " introuvable."
               MOVE 2 TO LK-STATUS
               GOBACK
           END-IF

           IF LIV-DISPO(WS-POS-LIVRE) = 0
               DISPLAY "  ERREUR : livre deja emprunte."
               MOVE 3 TO LK-STATUS
               GOBACK
           END-IF

           IF LK-NB-EMPRUNTS >= 30
               DISPLAY "  ERREUR : table des emprunts pleine."
               MOVE 4 TO LK-STATUS
               GOBACK
           END-IF

           ADD 1 TO LK-NB-EMPRUNTS
           MOVE LK-PROCHAIN-EMP-ID    TO EMP-ID(LK-NB-EMPRUNTS)
           MOVE LK-LIVRE-ID           TO EMP-LIVRE-ID(LK-NB-EMPRUNTS)
           MOVE LK-MEMBRE             TO EMP-MEMBRE(LK-NB-EMPRUNTS)
           MOVE 1                     TO EMP-ACTIF(LK-NB-EMPRUNTS)
           MOVE 0                     TO LIV-DISPO(WS-POS-LIVRE)
           ADD 1 TO LIV-NB-EMPRUNTS(WS-POS-LIVRE)
           ADD 1 TO LK-PROCHAIN-EMP-ID

           DISPLAY "  Emprunt [ID:" EMP-ID(LK-NB-EMPRUNTS)
               "] " LIV-TITRE(WS-POS-LIVRE)
               " -> " LK-MEMBRE
           MOVE 0 TO LK-STATUS
           GOBACK.

       END PROGRAM SP-EMPRUNTER-LIVRE.

      ******************************************************************
      * SP-RETOURNER-LIVRE
      * Status : 0=OK, 2=emprunt introuvable ou deja retourne
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. SP-RETOURNER-LIVRE.

       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 WS-IDX-E                  PIC 9(3) VALUE 0.
       01 WS-IDX-L                  PIC 9(3) VALUE 0.
       01 WS-POS-EMP                PIC 9(3) VALUE 0.
       01 WS-POS-LIV                PIC 9(3) VALUE 0.

       LINKAGE SECTION.
       01 LK-CATALOGUE.
           05 LK-NB-LIVRES          PIC 9(3).
           05 LK-LIVRE              OCCURS 50 TIMES.
               10 LIV-ID            PIC 9(5).
               10 LIV-TITRE         PIC X(40).
               10 LIV-AUTEUR        PIC X(30).
               10 LIV-DISPO         PIC 9.
               10 LIV-NB-EMPRUNTS   PIC 9(3).
       01 LK-EMPRUNTS.
           05 LK-NB-EMPRUNTS        PIC 9(3).
           05 LK-EMPRUNT            OCCURS 30 TIMES.
               10 EMP-ID            PIC 9(5).
               10 EMP-LIVRE-ID      PIC 9(5).
               10 EMP-MEMBRE        PIC X(30).
               10 EMP-ACTIF         PIC 9.
       01 LK-EMP-ID                 PIC 9(5).
       01 LK-STATUS                 PIC 9.

       PROCEDURE DIVISION USING
           LK-CATALOGUE LK-EMPRUNTS LK-EMP-ID LK-STATUS.

           MOVE 0 TO WS-POS-EMP
           PERFORM VARYING WS-IDX-E FROM 1 BY 1
               UNTIL WS-IDX-E > LK-NB-EMPRUNTS
               IF EMP-ID(WS-IDX-E) = LK-EMP-ID
                   AND EMP-ACTIF(WS-IDX-E) = 1
                   MOVE WS-IDX-E TO WS-POS-EMP
               END-IF
           END-PERFORM

           IF WS-POS-EMP = 0
               DISPLAY "  ERREUR : emprunt ID " LK-EMP-ID
                   " introuvable ou deja retourne."
               MOVE 2 TO LK-STATUS
               GOBACK
           END-IF

           MOVE 0 TO EMP-ACTIF(WS-POS-EMP)

           MOVE 0 TO WS-POS-LIV
           PERFORM VARYING WS-IDX-L FROM 1 BY 1
               UNTIL WS-IDX-L > LK-NB-LIVRES
               IF LIV-ID(WS-IDX-L) = EMP-LIVRE-ID(WS-POS-EMP)
                   MOVE WS-IDX-L TO WS-POS-LIV
               END-IF
           END-PERFORM

           IF WS-POS-LIV > 0
               MOVE 1 TO LIV-DISPO(WS-POS-LIV)
               DISPLAY "  Retour OK : " LIV-TITRE(WS-POS-LIV)
                   " (rendu par " EMP-MEMBRE(WS-POS-EMP) ")"
           END-IF
           MOVE 0 TO LK-STATUS
           GOBACK.

       END PROGRAM SP-RETOURNER-LIVRE.

      ******************************************************************
      * SP-RECHERCHER-LIVRE : recherche par mot-cle dans titre/auteur
      * Status : 0=resultats trouves, 2=aucun resultat
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. SP-RECHERCHER-LIVRE.

       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 WS-IDX                    PIC 9(3) VALUE 0.
       01 WS-NB-RESULTATS           PIC 9(3) VALUE 0.
       01 WS-MOTCLE-UP              PIC X(30).
       01 WS-TITRE-UP               PIC X(40).
       01 WS-AUTEUR-UP              PIC X(30).
       01 WS-DISPO-AFF              PIC X(12).

       LINKAGE SECTION.
       01 LK-CATALOGUE.
           05 LK-NB-LIVRES          PIC 9(3).
           05 LK-LIVRE              OCCURS 50 TIMES.
               10 LIV-ID            PIC 9(5).
               10 LIV-TITRE         PIC X(40).
               10 LIV-AUTEUR        PIC X(30).
               10 LIV-DISPO         PIC 9.
               10 LIV-NB-EMPRUNTS   PIC 9(3).
       01 LK-MOTCLE                 PIC X(30).
       01 LK-STATUS                 PIC 9.

       PROCEDURE DIVISION USING LK-CATALOGUE LK-MOTCLE LK-STATUS.
           MOVE 0 TO WS-NB-RESULTATS
           MOVE FUNCTION UPPER-CASE(LK-MOTCLE) TO WS-MOTCLE-UP

           PERFORM VARYING WS-IDX FROM 1 BY 1
               UNTIL WS-IDX > LK-NB-LIVRES
               MOVE FUNCTION UPPER-CASE(LIV-TITRE(WS-IDX))
                   TO WS-TITRE-UP
               MOVE FUNCTION UPPER-CASE(LIV-AUTEUR(WS-IDX))
                   TO WS-AUTEUR-UP

               IF FUNCTION TRIM(WS-MOTCLE-UP) =
                       FUNCTION TRIM(WS-MOTCLE-UP)
                   AND (WS-TITRE-UP(1:FUNCTION LENGTH(
                           FUNCTION TRIM(WS-MOTCLE-UP)))
                       = FUNCTION TRIM(WS-MOTCLE-UP)
                   OR WS-AUTEUR-UP(1:FUNCTION LENGTH(
                           FUNCTION TRIM(WS-MOTCLE-UP)))
                       = FUNCTION TRIM(WS-MOTCLE-UP))
                   ADD 1 TO WS-NB-RESULTATS
                   IF LIV-DISPO(WS-IDX) = 1
                       MOVE "Disponible  " TO WS-DISPO-AFF
                   ELSE
                       MOVE "Emprunte    " TO WS-DISPO-AFF
                   END-IF
                   DISPLAY "  [" LIV-ID(WS-IDX) "] "
                       LIV-TITRE(WS-IDX)
                   DISPLAY "       Auteur : " LIV-AUTEUR(WS-IDX)
                   DISPLAY "       Statut : " WS-DISPO-AFF
                       " | Emprunts total: "
                       LIV-NB-EMPRUNTS(WS-IDX)
               END-IF
           END-PERFORM

           IF WS-NB-RESULTATS = 0
               DISPLAY "  Aucun resultat pour : " LK-MOTCLE
               MOVE 2 TO LK-STATUS
           ELSE
               DISPLAY "  " WS-NB-RESULTATS " resultat(s) trouve(s)."
               MOVE 0 TO LK-STATUS
           END-IF
           GOBACK.

       END PROGRAM SP-RECHERCHER-LIVRE.

      ******************************************************************
      * SP-LISTE-EMPRUNTS : affiche tous les emprunts actifs
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. SP-LISTE-EMPRUNTS.

       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 WS-IDX-E                  PIC 9(3) VALUE 0.
       01 WS-IDX-L                  PIC 9(3) VALUE 0.
       01 WS-NB-ACTIFS              PIC 9(3) VALUE 0.
       01 WS-TITRE-EMP              PIC X(40) VALUE SPACES.

       LINKAGE SECTION.
       01 LK-CATALOGUE.
           05 LK-NB-LIVRES          PIC 9(3).
           05 LK-LIVRE              OCCURS 50 TIMES.
               10 LIV-ID            PIC 9(5).
               10 LIV-TITRE         PIC X(40).
               10 LIV-AUTEUR        PIC X(30).
               10 LIV-DISPO         PIC 9.
               10 LIV-NB-EMPRUNTS   PIC 9(3).
       01 LK-EMPRUNTS.
           05 LK-NB-EMPRUNTS        PIC 9(3).
           05 LK-EMPRUNT            OCCURS 30 TIMES.
               10 EMP-ID            PIC 9(5).
               10 EMP-LIVRE-ID      PIC 9(5).
               10 EMP-MEMBRE        PIC X(30).
               10 EMP-ACTIF         PIC 9.

       PROCEDURE DIVISION USING LK-CATALOGUE LK-EMPRUNTS.
           MOVE 0 TO WS-NB-ACTIFS
           DISPLAY "  ========================================"
           DISPLAY "          EMPRUNTS EN COURS               "
           DISPLAY "  ========================================"
           DISPLAY "  EMP#  LIV#  TITRE                MEMBRE"
           DISPLAY "  ----------------------------------------"

           PERFORM VARYING WS-IDX-E FROM 1 BY 1
               UNTIL WS-IDX-E > LK-NB-EMPRUNTS
               IF EMP-ACTIF(WS-IDX-E) = 1
                   ADD 1 TO WS-NB-ACTIFS
                   MOVE SPACES TO WS-TITRE-EMP
                   PERFORM VARYING WS-IDX-L FROM 1 BY 1
                       UNTIL WS-IDX-L > LK-NB-LIVRES
                       IF LIV-ID(WS-IDX-L) = EMP-LIVRE-ID(WS-IDX-E)
                           MOVE LIV-TITRE(WS-IDX-L) TO WS-TITRE-EMP
                       END-IF
                   END-PERFORM
                   DISPLAY "  " EMP-ID(WS-IDX-E)
                       "  " EMP-LIVRE-ID(WS-IDX-E)
                       "  " WS-TITRE-EMP(1:20)
                       "  " EMP-MEMBRE(WS-IDX-E)
               END-IF
           END-PERFORM

           IF WS-NB-ACTIFS = 0
               DISPLAY "  Aucun emprunt en cours."
           ELSE
               DISPLAY "  ----------------------------------------"
               DISPLAY "  Total emprunts actifs : " WS-NB-ACTIFS
           END-IF
           DISPLAY "  ========================================"
           GOBACK.

       END PROGRAM SP-LISTE-EMPRUNTS.
