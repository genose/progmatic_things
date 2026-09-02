      *================================================================*
      * PROGRAMME  : INVENTAIRE                                        *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-09-02                                        *
      * VERSION    : 1.0                                               *
      *----------------------------------------------------------------*
      * DESCRIPTION: OCCURS + affichage formate d un inventaire produits
      *----------------------------------------------------------------*
      * GROUPE     : arrays                                            *
      * COMPILEUR  : GnuCOBOL â cobc -x                                *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. INVENTAIRE.
       DATA DIVISION.
       FILE SECTION.
       WORKING-STORAGE SECTION.
       01 INVENTAIRE-PRODUITS.
           05 PRODUIT OCCURS 2 TIMES INDEXED BY WS-INDEX-PRODUIT.
               10 PROD-ID PIC 9(6) VALUE ZEROES.
               10 PROD-NOM PIC X(20) VALUE "PRODUIT ".
               10 PROD-QUANTITE PIC 9(4) VALUE ZEROES.

       01 WS-PRIX-TOTAL PIC 9(7)V99 VALUE ZEROES.
       01 WS-QUANTITE-TOTALE PIC 9(5) VALUE ZEROES.
       01 WS-STOCK-TOTAL PIC 9(5) VALUE ZEROES.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
            DISPLAY "** INVENTAIRE DES PRODUITS **".
            PERFORM VARYING WS-INDEX-PRODUIT FROM 1 BY 1
                UNTIL WS-INDEX-PRODUIT > LENGTH OF PRODUIT
                MOVE WS-INDEX-PRODUIT TO PROD-ID(WS-INDEX-PRODUIT)
                MOVE "PRODUIT " TO PROD-NOM(WS-INDEX-PRODUIT)
                MOVE 10 TO PROD-QUANTITE(WS-INDEX-PRODUIT)
                ADD PROD-QUANTITE(WS-INDEX-PRODUIT) TO WS-STOCK-TOTAL
                DISPLAY "*********************************************".
                DISPLAY "*********** SAISIE DU PRODUIT ***************".
                DISPLAY "Produit: " PROD-NOM  " ID: " PROD-ID .
                DISPLAY " SAISIR LA QUANTITE: " WITH NO ADVANCING
                ACCEPT PROD-QUANTITE(WS-INDEX-PRODUIT)
                ADD PROD-QUANTITE(WS-INDEX-PRODUIT) TO WS-STOCK-TOTAL
                DISPLAY "************************************".
                DISPLAY "*********** RESULTAT ***************".
                DISPLAY "Produit: " PROD-NOM
                " ID: " PROD-ID
                " QuantitÃÂ©: " PROD-QUANTITE.

            END-PERFORM
            DISPLAY "************************************".
            DISPLAY "STOCK TOTAL: " WS-STOCK-TOTAL
            DISPLAY "FIN DE L'INVENTAIRE".
            STOP RUN.
       END PROGRAM YOUR-PROGRAM-NAME.

