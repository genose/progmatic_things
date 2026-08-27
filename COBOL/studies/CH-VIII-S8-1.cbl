      ******************************************************************
      * Author:    COTILLARD SEBASTIEN
      * Date:      12-08-2026
      * Purpose:   GESTION INVENTAIRE, VENTES MENSUELLES, RECHERCHE
      *            Partie 1 : Tableau de produits (OCCURS 2)
      *            Partie 2 : Ventes mensuelles (OCCURS 6)
      *            Partie 3 : Recherche dans un tableau (OCCURS 5)
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. GESTION-INVENTAIRE.

       DATA DIVISION.
       WORKING-STORAGE SECTION.

      *----------------------------------------------------------------*
      * PARTIE 1 - INVENTAIRE PRODUITS
      *----------------------------------------------------------------*
       01 INVENTAIRE-PRODUITS.
           05 PRODUIT               OCCURS 2 TIMES INDEXED BY IDX-PROD.
               10 PROD-ID           PIC 9(6).
               10 PROD-NOM          PIC X(20).
               10 PROD-QUANTITE     PIC 9(4).

      *----------------------------------------------------------------*
      * PARTIE 2 - VENTES MENSUELLES
      *----------------------------------------------------------------*
       01 VENTES-MENSUELLES.
           05 VENTE-MONTANT         PIC 9(5)V99 VALUE ZEROES
               OCCURS 6 TIMES INDEXED BY IDX-MOIS.

       01 WS-TOTAL-VENTES           PIC 9(7)V99 VALUE ZEROES.
       01 WS-AFF-TOTAL              PIC ZZZ,ZZZ.99.
       01 WS-AFF-MONTANT            PIC ZZ,ZZZ.99.
       01 WS-SAISIE-MONTANT         PIC X(8).

      *----------------------------------------------------------------*
      * PARTIE 3 - TABLEAU DE RECHERCHE
      *----------------------------------------------------------------*
       01 TABLEAU-RECHERCHE.
           05 TR-VALEUR             PIC 9(4) VALUE ZEROES
               OCCURS 5 TIMES INDEXED BY IDX-RCH.

       01 WS-VALEUR-CHERCHEE        PIC 9(4) VALUE ZEROES.
       01 WS-SAISIE-VAL             PIC X(5).
       01 WS-TROUVE                 PIC 9     VALUE 0.
          88 VALEUR-TROUVEE         VALUE 1.
          88 VALEUR-NON-TROUVEE     VALUE 0.
       01 WS-POS-TROUVEE            PIC 9(2)  VALUE ZEROES.

      * Index generique pour affichage
       01 WS-IDX-AFF                PIC 99.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           PERFORM 1000-GESTION-INVENTAIRE
           PERFORM 2000-VENTES-MENSUELLES
           PERFORM 3000-RECHERCHE-TABLEAU
           STOP RUN.

      ******************************************************************
      * PARTIE 1 : INVENTAIRE PRODUITS
      ******************************************************************
       1000-GESTION-INVENTAIRE.
           DISPLAY "========================================"
           DISPLAY "   PARTIE 1 : INVENTAIRE PRODUITS       "
           DISPLAY "========================================"

           PERFORM 1100-INIT-PRODUITS
           PERFORM 1200-SAISIE-QUANTITES
           PERFORM 1300-AFFICHER-INVENTAIRE.

       1100-INIT-PRODUITS.
           MOVE 100001          TO PROD-ID(1)
           MOVE "Clavier USB"   TO PROD-NOM(1)
           MOVE 0               TO PROD-QUANTITE(1)

           MOVE 100002          TO PROD-ID(2)
           MOVE "Souris sans fil" TO PROD-NOM(2)
           MOVE 0               TO PROD-QUANTITE(2).

       1200-SAISIE-QUANTITES.
           PERFORM VARYING IDX-PROD FROM 1 BY 1
               UNTIL IDX-PROD > 2
               MOVE IDX-PROD TO WS-IDX-AFF
               DISPLAY "Quantite en stock pour ["
                   PROD-NOM(IDX-PROD) "] : "
                   WITH NO ADVANCING
               ACCEPT PROD-QUANTITE(IDX-PROD)
           END-PERFORM.

       1300-AFFICHER-INVENTAIRE.
           DISPLAY SPACES
           DISPLAY "--- Inventaire actuel ---"
           PERFORM VARYING IDX-PROD FROM 1 BY 1
               UNTIL IDX-PROD > 2
               DISPLAY "Produit : " PROD-NOM(IDX-PROD)
                   "  ID : " PROD-ID(IDX-PROD)
                   "  Quantite : " PROD-QUANTITE(IDX-PROD)
           END-PERFORM
           DISPLAY SPACES.

      ******************************************************************
      * PARTIE 2 : VENTES MENSUELLES
      ******************************************************************
       2000-VENTES-MENSUELLES.
           DISPLAY "========================================"
           DISPLAY "   PARTIE 2 : VENTES MENSUELLES (M1-M6) "
           DISPLAY "========================================"

           PERFORM 2100-SAISIE-VENTES
           PERFORM 2200-CALCULER-TOTAL
           PERFORM 2300-AFFICHER-VENTES.

       2100-SAISIE-VENTES.
           PERFORM VARYING IDX-MOIS FROM 1 BY 1
               UNTIL IDX-MOIS > 6
               MOVE IDX-MOIS TO WS-IDX-AFF
               DISPLAY "Ventes mois " WS-IDX-AFF " (ex: 1250.50) : "
                   WITH NO ADVANCING
               ACCEPT WS-SAISIE-MONTANT
               MOVE FUNCTION NUMVAL(WS-SAISIE-MONTANT)
                   TO VENTE-MONTANT(IDX-MOIS)
           END-PERFORM.

       2200-CALCULER-TOTAL.
           MOVE ZEROES TO WS-TOTAL-VENTES
           PERFORM VARYING IDX-MOIS FROM 1 BY 1
               UNTIL IDX-MOIS > 6
               ADD VENTE-MONTANT(IDX-MOIS) TO WS-TOTAL-VENTES
           END-PERFORM.

       2300-AFFICHER-VENTES.
           DISPLAY SPACES
           DISPLAY "--- Detail des ventes ---"
           PERFORM VARYING IDX-MOIS FROM 1 BY 1
               UNTIL IDX-MOIS > 6
               MOVE IDX-MOIS TO WS-IDX-AFF
               MOVE VENTE-MONTANT(IDX-MOIS) TO WS-AFF-MONTANT
               DISPLAY "  Mois " WS-IDX-AFF " : " WS-AFF-MONTANT
           END-PERFORM
           MOVE WS-TOTAL-VENTES TO WS-AFF-TOTAL
           DISPLAY "  Total 6 mois : " WS-AFF-TOTAL
           DISPLAY SPACES.

      ******************************************************************
      * PARTIE 3 : RECHERCHE DANS UN TABLEAU
      ******************************************************************
       3000-RECHERCHE-TABLEAU.
           DISPLAY "========================================"
           DISPLAY "   PARTIE 3 : RECHERCHE DANS TABLEAU    "
           DISPLAY "========================================"

           PERFORM 3100-INIT-TABLEAU
           PERFORM 3200-AFFICHER-TABLEAU
           PERFORM 3300-SAISIR-VALEUR-CHERCHEE
           PERFORM 3400-RECHERCHER
           PERFORM 3500-AFFICHER-RESULTAT.

       3100-INIT-TABLEAU.
           MOVE 1042 TO TR-VALEUR(1)
           MOVE 3875 TO TR-VALEUR(2)
           MOVE 0210 TO TR-VALEUR(3)
           MOVE 7654 TO TR-VALEUR(4)
           MOVE 5001 TO TR-VALEUR(5).

       3200-AFFICHER-TABLEAU.
           DISPLAY "Valeurs du tableau : "
           PERFORM VARYING IDX-RCH FROM 1 BY 1
               UNTIL IDX-RCH > 5
               MOVE IDX-RCH TO WS-IDX-AFF
               DISPLAY "  [" WS-IDX-AFF "] " TR-VALEUR(IDX-RCH)
           END-PERFORM.

       3300-SAISIR-VALEUR-CHERCHEE.
           DISPLAY "Valeur a rechercher : " WITH NO ADVANCING
           ACCEPT WS-SAISIE-VAL
           MOVE FUNCTION NUMVAL(WS-SAISIE-VAL)
               TO WS-VALEUR-CHERCHEE.

       3400-RECHERCHER.
           SET VALEUR-NON-TROUVEE TO TRUE
           PERFORM VARYING IDX-RCH FROM 1 BY 1
               UNTIL IDX-RCH > 5 OR VALEUR-TROUVEE
               IF TR-VALEUR(IDX-RCH) = WS-VALEUR-CHERCHEE
                   SET VALEUR-TROUVEE TO TRUE
                   MOVE IDX-RCH TO WS-POS-TROUVEE
               END-IF
           END-PERFORM.

       3500-AFFICHER-RESULTAT.
           DISPLAY SPACES
           IF VALEUR-TROUVEE
               DISPLAY "  Valeur " WS-VALEUR-CHERCHEE
                   " trouvee en position " WS-POS-TROUVEE
           ELSE
               DISPLAY "  Valeur " WS-VALEUR-CHERCHEE
                   " non trouvee dans le tableau."
           END-IF.

       END PROGRAM GESTION-INVENTAIRE.
