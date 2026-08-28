      *================================================================*
      * PROGRAMME  : GSTK001                                         *
      * AUTEUR     : [NOM ETUDIANT]                                   *
      * DATE       : 2026-08-26                                       *
      * VERSION    : 1.0                                              *
      *----------------------------------------------------------------*
      * DESCRIPTION : CONSULTATION STOCK - LISTE AVEC FILTRES        *
      *   Transaction CICS pseudo-conversationnelle.                  *
      *   Affiche la liste des articles avec filtres de recherche,    *
      *   barre ASCII de niveau de stock, pagination 10 lignes/page.  *
      *----------------------------------------------------------------*
      * TRANSID    : G001                                             *
      * MAPSET     : GSTK001M      MAP : GSTK001                     *
      * COMMAREA   : GSTK-COMMAREA (263 octets, voir GSTKCPY)        *
      *   FILLER(209:12) = filtre code article                        *
      *   FILLER(221:22) = filtre libelle                             *
      *----------------------------------------------------------------*
      * TOUCHES :                                                      *
      *   ENTREE   -> Selectionner l'article pointe (SELxx)           *
      *   PF3      -> Retour menu (GSTK000)                           *
      *   PF5      -> Rafraichir avec les filtres saisis              *
      *   PF6      -> Sortie marchandise (GSTK003, art selectionne)   *
      *   PF7      -> Page precedente                                  *
      *   PF8      -> Page suivante                                    *
      *   PF9      -> Nouvel article (GSTK004)                        *
      *   PF12     -> Reinitialiser les filtres                       *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. GSTK001.
       AUTHOR. ETUDIANT-COBOL.
       DATE-WRITTEN. 2026-08-26.

       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       SPECIAL-NAMES.
           DECIMAL-POINT IS COMMA.

       DATA DIVISION.

      *================================================================*
       WORKING-STORAGE SECTION.

      *----------------------------------------------------------------*
      * CONSTANTES DU PROGRAMME                                        *
      *----------------------------------------------------------------*
       01 W-PROG-ID         PIC X(8)  VALUE 'GSTK001 '.
       01 W-TRANS-ID        PIC X(4)  VALUE 'G001'.
       01 W-MAPSET          PIC X(8)  VALUE 'GSTK001M'.
       01 W-MAP             PIC X(7)  VALUE 'GSTK001'.
       01 W-CA-LEN          PIC S9(4) COMP VALUE 263.
       01 W-PROG-CIBLE      PIC X(8)  VALUE SPACES.

      *----------------------------------------------------------------*
      * ZONES CICS                                                     *
      *----------------------------------------------------------------*
       01 W-RESP            PIC S9(8) COMP VALUE 0.
       01 W-RESP2           PIC S9(8) COMP VALUE 0.

      *----------------------------------------------------------------*
      * DATE ET HEURE                                                   *
      *----------------------------------------------------------------*
       01 W-ABSTIME         PIC S9(15) COMP-3 VALUE 0.
       01 W-DATE-JOUR       PIC X(10) VALUE SPACES.
       01 W-HEURE-JOUR      PIC X(8)  VALUE SPACES.
       01 W-DATETIME-EDI.
          05 W-DT-DATE      PIC X(10).
          05 FILLER         PIC X     VALUE SPACE.
          05 W-DT-HEURE     PIC X(8).

      *----------------------------------------------------------------*
      * FILTRES DE RECHERCHE (persistes dans COMMAREA FILLER)          *
      *   Offset 209: W-FIL-CODE (12 chars, format "PREFIXE%")        *
      *   Offset 221: W-FIL-LIBL (22 chars, format "MOT%")            *
      *----------------------------------------------------------------*
       01 W-FIL-CODE        PIC X(12) VALUE '%'.
       01 W-FIL-LIBL        PIC X(22) VALUE '%'.

      *----------------------------------------------------------------*
      * INDICATEURS                                                    *
      *----------------------------------------------------------------*
       01 W-FETCH-OK        PIC X     VALUE 'Y'.
          88 FETCH-OK                  VALUE 'Y'.
          88 FIN-FETCH                 VALUE 'N'.

      *----------------------------------------------------------------*
      * TABLE DES 10 LIGNES AFFICHEES                                  *
      *----------------------------------------------------------------*
       01 W-TABLE-ART.
          05 WL-ENT              OCCURS 10 TIMES.
             10 WL-CODE          PIC X(10).
             10 WL-DESIG         PIC X(24).
             10 WL-CATEG         PIC X(9).
             10 WL-QTE           PIC S9(10)V999  COMP-3.
             10 WL-MIN           PIC S9(10)V999  COMP-3.
             10 WL-MAX           PIC S9(10)V999  COMP-3.
             10 WL-PRIX          PIC S9(10)V9999 COMP-3.
             10 WL-VALEUR        PIC S9(14)V99   COMP-3.
             10 WL-STATUT        PIC X(8).
             10 WL-EMPL          PIC X(8).
             10 WL-BARRE         PIC X(20).
       01 W-NB-LUS          PIC S9(4) COMP VALUE 0.
       01 W-I               PIC S9(4) COMP VALUE 0.
       01 W-J               PIC S9(4) COMP VALUE 0.
       01 W-SKIP            PIC S9(4) COMP VALUE 0.
       01 W-LIGNE-NO        PIC 9(3)        VALUE 0.

      *----------------------------------------------------------------*
      * TOTAUX                                                         *
      *----------------------------------------------------------------*
       01 W-TOT-CNT         PIC S9(7)     COMP-3 VALUE 0.
       01 W-TOT-VLR         PIC S9(14)V99 COMP-3 VALUE 0.
       01 W-TOT-ALT         PIC S9(4)     COMP-3 VALUE 0.

      *----------------------------------------------------------------*
      * EDITIONS NUMERIQUES POUR AFFICHAGE                             *
      *----------------------------------------------------------------*
       01 W-ED-QTE          PIC Z(4)9.
       01 W-ED-MIN          PIC ZZZ9.
       01 W-ED-MAX          PIC Z(4)9.
       01 W-ED-PRIX         PIC ZZZ9,99.
       01 W-ED-VALEUR       PIC Z(7),99.
       01 W-ED-TOTCNT       PIC Z(4)9.
       01 W-ED-TOTVLR       PIC Z(12),99.
       01 W-ED-TOTALT       PIC ZZZ9.
       01 W-ED-PAGCUR       PIC ZZZ9.
       01 W-ED-PAGTOT       PIC ZZZ9.
       01 W-ED-LIGNO        PIC ZZ9.
       01 W-FILL-CNT        PIC S9(4)     COMP VALUE 0.
       01 W-BARRE-WK        PIC X(20)     VALUE SPACES.

      *----------------------------------------------------------------*
      * HOST VARIABLES SQL                                             *
      *----------------------------------------------------------------*
       01 HV-CODE           PIC X(10).
       01 HV-DESIG          PIC X(50).
       01 HV-CATEG          PIC X(15).
       01 HV-QTE            PIC S9(10)V999  COMP-3.
       01 HV-MIN            PIC S9(10)V999  COMP-3.
       01 HV-MAX            PIC S9(10)V999  COMP-3.
       01 HV-PRIX           PIC S9(10)V9999 COMP-3.
       01 HV-VALEUR         PIC S9(14)V99   COMP-3.
       01 HV-STATUT         PIC X(10).
       01 HV-EMPL           PIC X(15).
       01 HV-FIL-CODE       PIC X(12).
       01 HV-FIL-LIBL       PIC X(22).
       01 HV-FIL-CATEG      PIC X(17).
       01 HV-FIL-STAT       PIC X(12).
       01 HV-TOT-CNT        PIC S9(7)     COMP-3.
       01 HV-TOT-VLR        PIC S9(14)V99 COMP-3.
       01 HV-TOT-ALT        PIC S9(4)     COMP-3.

           EXEC SQL INCLUDE SQLCA END-EXEC.

       COPY GSTK001M.
       COPY DFHAID.
       COPY DFHBMSCA.
       COPY GSTKCOMM.

      *================================================================*
       LINKAGE SECTION.
       01 DFHCOMMAREA        PIC X(263).

      *================================================================*
       PROCEDURE DIVISION.
      *================================================================*

      *----------------------------------------------------------------*
      * DECLARATION DU CURSEUR SQL (traitement statique pre-compile)   *
      * Les valeurs de HV-FIL-* sont capturees a l'OPEN du curseur.   *
      *----------------------------------------------------------------*
           EXEC SQL
               DECLARE CURS-ART CURSOR FOR
               SELECT ART_CODE,
                      ART_DESIGNATION,
                      ART_CATEGORIE,
                      ART_QTE_STOCK,
                      ART_QTE_MIN,
                      ART_QTE_MAX,
                      ART_PRIX_VENTE,
                      ART_QTE_STOCK * ART_PRIX_VENTE,
                      ART_STATUT,
                      ART_EMPLACEMENT
               FROM   GSTK.ARTICLES
               WHERE  ART_CODE        LIKE :HV-FIL-CODE
               AND    ART_DESIGNATION  LIKE :HV-FIL-LIBL
               AND    ART_CATEGORIE    LIKE :HV-FIL-CATEG
               AND    ART_STATUT       LIKE :HV-FIL-STAT
               AND    ART_STATUT      <> 'ARCHIVE'
               ORDER BY ART_CODE
           END-EXEC.

      ******************************************************************
       0000-PRINCIPAL.
      ******************************************************************
           EVALUATE TRUE
               WHEN EIBCALEN = ZERO
                   PERFORM 1000-PREMIERE-ENTREE
               WHEN OTHER
                   MOVE DFHCOMMAREA TO GSTK-COMMAREA
                   PERFORM 2000-RETOUR-TRANSACTION
           END-EVALUATE.
           EXEC CICS RETURN END-EXEC.
           STOP RUN.

      ******************************************************************
       1000-PREMIERE-ENTREE.
      ******************************************************************
           MOVE SPACES     TO GSTK-COMMAREA.
           MOVE 'G001    ' TO CA-TRAN-RETOUR.
           MOVE EIBTRMID   TO CA-TERMINAL.
           MOVE 1          TO CA-PAGE-COURANTE.
           MOVE 1          TO CA-NB-PAGES.
           MOVE SPACES     TO CA-ART-CODE-SELEC.
           MOVE SPACES     TO CA-MSG-RETOUR.
           MOVE '%'        TO W-FIL-CODE.
           MOVE '%'        TO W-FIL-LIBL.
           PERFORM 3000-COMPTER-ARTICLES.
           PERFORM 4000-REQUETE-ARTICLES.
           PERFORM 5000-AFFICHER-ECRAN.

      ******************************************************************
       2000-RETOUR-TRANSACTION.
      ******************************************************************
      *    Restaurer filtres depuis FILLER de la COMMAREA
           MOVE GSTK-COMMAREA(209:12) TO W-FIL-CODE.
           MOVE GSTK-COMMAREA(221:22) TO W-FIL-LIBL.
           MOVE SPACES TO CA-MSG-RETOUR.

           EXEC CICS RECEIVE MAP(W-MAP) MAPSET(W-MAPSET)
               INTO(GSTK001I)
               RESP(W-RESP) RESP2(W-RESP2)
           END-EXEC.
           IF W-RESP = DFHRESP(MAPFAIL)
               PERFORM 4000-REQUETE-ARTICLES
               PERFORM 5000-AFFICHER-ECRAN
               GO TO 2000-FIN
           END-IF.

           EVALUATE EIBAID
               WHEN DFHPF3
                   PERFORM 6000-RETOUR-MENU
               WHEN DFHPF5
                   PERFORM 2100-MAJ-FILTRES
                   MOVE 1 TO CA-PAGE-COURANTE
                   PERFORM 3000-COMPTER-ARTICLES
                   PERFORM 4000-REQUETE-ARTICLES
                   PERFORM 5000-AFFICHER-ECRAN
               WHEN DFHPF6
                   PERFORM 4000-REQUETE-ARTICLES
                   PERFORM 2200-SELECTIONNER-ART
                   IF CA-ART-CODE-SELEC = SPACES
                       MOVE 'SELECTIONNEZ UN ARTICLE (colonne SEL)'
                           TO CA-MSG-RETOUR
                       PERFORM 5000-AFFICHER-ECRAN
                   ELSE
                       MOVE W-PROG-ID  TO CA-TRAN-RETOUR
                       MOVE 'GSTK003 ' TO W-PROG-CIBLE
                       EXEC CICS XCTL PROGRAM(W-PROG-CIBLE)
                           COMMAREA(GSTK-COMMAREA) LENGTH(W-CA-LEN)
                       END-EXEC
                   END-IF
               WHEN DFHPF7
                   IF CA-PAGE-COURANTE > 1
                       SUBTRACT 1 FROM CA-PAGE-COURANTE
                   ELSE
                       MOVE 'DEBUT DE LISTE - PF8 POUR PAGE SUIVANTE'
                           TO CA-MSG-RETOUR
                   END-IF
                   PERFORM 4000-REQUETE-ARTICLES
                   PERFORM 5000-AFFICHER-ECRAN
               WHEN DFHPF8
                   IF CA-PAGE-COURANTE < CA-NB-PAGES
                       ADD 1 TO CA-PAGE-COURANTE
                   ELSE
                       MOVE 'FIN DE LISTE - PF7 POUR PAGE PRECEDENTE'
                           TO CA-MSG-RETOUR
                   END-IF
                   PERFORM 4000-REQUETE-ARTICLES
                   PERFORM 5000-AFFICHER-ECRAN
               WHEN DFHPF9
                   MOVE SPACES     TO CA-ART-CODE-SELEC
                   MOVE W-PROG-ID  TO CA-TRAN-RETOUR
                   MOVE 'GSTK004 ' TO W-PROG-CIBLE
                   EXEC CICS XCTL PROGRAM(W-PROG-CIBLE)
                       COMMAREA(GSTK-COMMAREA) LENGTH(W-CA-LEN)
                   END-EXEC
               WHEN DFHPF12
                   MOVE '%'    TO W-FIL-CODE
                   MOVE '%'    TO W-FIL-LIBL
                   MOVE SPACES TO CA-FILTRE-CAT
                   MOVE SPACES TO CA-FILTRE-STATUT
                   MOVE SPACES TO CA-ART-CODE-SELEC
                   MOVE 1      TO CA-PAGE-COURANTE
                   PERFORM 3000-COMPTER-ARTICLES
                   PERFORM 4000-REQUETE-ARTICLES
                   PERFORM 5000-AFFICHER-ECRAN
               WHEN DFHENTER
                   PERFORM 4000-REQUETE-ARTICLES
                   PERFORM 2200-SELECTIONNER-ART
                   IF CA-ART-CODE-SELEC NOT = SPACES
                       MOVE 'ARTICLE SELECTIONNE : '
                           TO CA-MSG-RETOUR
                       MOVE CA-ART-CODE-SELEC
                           TO CA-MSG-RETOUR(23:10)
                   END-IF
                   PERFORM 5000-AFFICHER-ECRAN
               WHEN OTHER
                   MOVE 'TOUCHE NON RECONNUE - ENTER OU PF3..PF12'
                       TO CA-MSG-RETOUR
                   PERFORM 4000-REQUETE-ARTICLES
                   PERFORM 5000-AFFICHER-ECRAN
           END-EVALUATE.
       2000-FIN.
           EXIT.

      ******************************************************************
       2100-MAJ-FILTRES.
      ******************************************************************
      *    Lire les champs filtres depuis l'ecran recu
           IF FILCOL IN GSTK001I > ZERO
               IF FILCOI IN GSTK001I = SPACES
                   MOVE '%' TO W-FIL-CODE
               ELSE
                   STRING FILCOI IN GSTK001I DELIMITED SPACE
                          '%' DELIMITED SIZE
                          INTO W-FIL-CODE
               END-IF
           END-IF.
           IF FILLBL IN GSTK001I > ZERO
               IF FILLBI IN GSTK001I = SPACES
                   MOVE '%' TO W-FIL-LIBL
               ELSE
                   STRING FILLBI IN GSTK001I DELIMITED SPACE
                          '%' DELIMITED SIZE
                          INTO W-FIL-LIBL
               END-IF
           END-IF.
           IF FILCAL IN GSTK001I > ZERO
               IF FILCAI IN GSTK001I = SPACES
                   MOVE SPACES TO CA-FILTRE-CAT
               ELSE
                   MOVE FILCAI IN GSTK001I TO CA-FILTRE-CAT
               END-IF
           END-IF.
           IF FILSTL IN GSTK001I > ZERO
               IF FILSTI IN GSTK001I = SPACES
                   MOVE SPACES TO CA-FILTRE-STATUT
               ELSE
                   MOVE FILSTI IN GSTK001I TO CA-FILTRE-STATUT
               END-IF
           END-IF.

      ******************************************************************
       2200-SELECTIONNER-ART.
      ******************************************************************
      *    Detecter la premiere ligne dont le champ SELxx est saisi
           MOVE SPACES TO CA-ART-CODE-SELEC.
           IF SEL01L IN GSTK001I > ZERO AND W-NB-LUS >= 1
               MOVE WL-CODE(1) TO CA-ART-CODE-SELEC
           ELSE IF SEL02L IN GSTK001I > ZERO AND W-NB-LUS >= 2
               MOVE WL-CODE(2) TO CA-ART-CODE-SELEC
           ELSE IF SEL03L IN GSTK001I > ZERO AND W-NB-LUS >= 3
               MOVE WL-CODE(3) TO CA-ART-CODE-SELEC
           ELSE IF SEL04L IN GSTK001I > ZERO AND W-NB-LUS >= 4
               MOVE WL-CODE(4) TO CA-ART-CODE-SELEC
           ELSE IF SEL05L IN GSTK001I > ZERO AND W-NB-LUS >= 5
               MOVE WL-CODE(5) TO CA-ART-CODE-SELEC
           ELSE IF SEL06L IN GSTK001I > ZERO AND W-NB-LUS >= 6
               MOVE WL-CODE(6) TO CA-ART-CODE-SELEC
           ELSE IF SEL07L IN GSTK001I > ZERO AND W-NB-LUS >= 7
               MOVE WL-CODE(7) TO CA-ART-CODE-SELEC
           ELSE IF SEL08L IN GSTK001I > ZERO AND W-NB-LUS >= 8
               MOVE WL-CODE(8) TO CA-ART-CODE-SELEC
           ELSE IF SEL09L IN GSTK001I > ZERO AND W-NB-LUS >= 9
               MOVE WL-CODE(9) TO CA-ART-CODE-SELEC
           ELSE IF SEL10L IN GSTK001I > ZERO AND W-NB-LUS >= 10
               MOVE WL-CODE(10) TO CA-ART-CODE-SELEC
           END-IF.

      ******************************************************************
       3000-COMPTER-ARTICLES.
      ******************************************************************
      *    Calculer les totaux et le nombre de pages
           PERFORM 3100-PREPARER-FILTRES.
           EXEC SQL
               SELECT COUNT(*),
                      COALESCE(SUM(ART_QTE_STOCK * ART_PRIX_VENTE), 0),
                      SUM(CASE WHEN ART_QTE_STOCK < ART_QTE_MIN
                               THEN 1 ELSE 0 END)
               INTO   :HV-TOT-CNT,
                      :HV-TOT-VLR,
                      :HV-TOT-ALT
               FROM   GSTK.ARTICLES
               WHERE  ART_CODE        LIKE :HV-FIL-CODE
               AND    ART_DESIGNATION  LIKE :HV-FIL-LIBL
               AND    ART_CATEGORIE    LIKE :HV-FIL-CATEG
               AND    ART_STATUT       LIKE :HV-FIL-STAT
               AND    ART_STATUT      <> 'ARCHIVE'
           END-EXEC.
           IF SQLCODE = 0
               MOVE HV-TOT-CNT TO W-TOT-CNT
               MOVE HV-TOT-VLR TO W-TOT-VLR
               MOVE HV-TOT-ALT TO W-TOT-ALT
               COMPUTE CA-NB-PAGES = (W-TOT-CNT + 9) / 10
               IF CA-NB-PAGES = 0
                   MOVE 1 TO CA-NB-PAGES
               END-IF
               IF CA-PAGE-COURANTE > CA-NB-PAGES
                   MOVE CA-NB-PAGES TO CA-PAGE-COURANTE
               END-IF
           ELSE
               MOVE ZERO TO W-TOT-CNT W-TOT-VLR W-TOT-ALT
               MOVE 1    TO CA-NB-PAGES CA-PAGE-COURANTE
               PERFORM 9100-ERREUR-SQL
           END-IF.

      ******************************************************************
       3100-PREPARER-FILTRES.
      ******************************************************************
      *    Convertir les filtres WS en host variables SQL
           MOVE W-FIL-CODE TO HV-FIL-CODE.
           MOVE W-FIL-LIBL TO HV-FIL-LIBL.
           IF CA-FILTRE-CAT = SPACES
               MOVE '%' TO HV-FIL-CATEG
           ELSE
               STRING CA-FILTRE-CAT DELIMITED SPACE
                      '%' DELIMITED SIZE
                      INTO HV-FIL-CATEG
           END-IF.
           IF CA-FILTRE-STATUT = SPACES
               MOVE '%' TO HV-FIL-STAT
           ELSE
               MOVE CA-FILTRE-STATUT TO HV-FIL-STAT
           END-IF.

      ******************************************************************
       4000-REQUETE-ARTICLES.
      ******************************************************************
      *    Charger la page courante depuis DB2 via curseur
           PERFORM 3100-PREPARER-FILTRES.
           MOVE ZERO     TO W-NB-LUS.
           MOVE 'Y'      TO W-FETCH-OK.
           PERFORM VARYING W-I FROM 1 BY 1 UNTIL W-I > 10
               MOVE SPACES TO WL-CODE(W-I)
               MOVE SPACES TO WL-DESIG(W-I)
               MOVE SPACES TO WL-CATEG(W-I)
               MOVE ZERO   TO WL-QTE(W-I) WL-MIN(W-I) WL-MAX(W-I)
               MOVE ZERO   TO WL-PRIX(W-I) WL-VALEUR(W-I)
               MOVE SPACES TO WL-STATUT(W-I) WL-EMPL(W-I)
               MOVE SPACES TO WL-BARRE(W-I)
           END-PERFORM.

           EXEC SQL OPEN CURS-ART END-EXEC.
           IF SQLCODE NOT = 0
               PERFORM 9100-ERREUR-SQL
               GO TO 4000-FIN
           END-IF.

      *    Sauter les lignes des pages precedentes
           COMPUTE W-SKIP = (CA-PAGE-COURANTE - 1) * 10.
           PERFORM VARYING W-I FROM 1 BY 1
               UNTIL W-I > W-SKIP OR FIN-FETCH
               EXEC SQL FETCH CURS-ART INTO
                   :HV-CODE,   :HV-DESIG,  :HV-CATEG,
                   :HV-QTE,    :HV-MIN,    :HV-MAX,
                   :HV-PRIX,   :HV-VALEUR, :HV-STATUT, :HV-EMPL
               END-EXEC
               IF SQLCODE NOT = 0
                   MOVE 'N' TO W-FETCH-OK
               END-IF
           END-PERFORM.

      *    Charger les 10 lignes de la page courante
           PERFORM VARYING W-I FROM 1 BY 1
               UNTIL W-I > 10 OR FIN-FETCH
               EXEC SQL FETCH CURS-ART INTO
                   :HV-CODE,   :HV-DESIG,  :HV-CATEG,
                   :HV-QTE,    :HV-MIN,    :HV-MAX,
                   :HV-PRIX,   :HV-VALEUR, :HV-STATUT, :HV-EMPL
               END-EXEC
               IF SQLCODE NOT = 0
                   MOVE 'N' TO W-FETCH-OK
               ELSE
                   MOVE HV-CODE        TO WL-CODE(W-I)
                   MOVE HV-DESIG(1:24) TO WL-DESIG(W-I)
                   MOVE HV-CATEG(1:9)  TO WL-CATEG(W-I)
                   MOVE HV-QTE         TO WL-QTE(W-I)
                   MOVE HV-MIN         TO WL-MIN(W-I)
                   MOVE HV-MAX         TO WL-MAX(W-I)
                   MOVE HV-PRIX        TO WL-PRIX(W-I)
                   MOVE HV-VALEUR      TO WL-VALEUR(W-I)
                   MOVE HV-STATUT(1:8) TO WL-STATUT(W-I)
                   MOVE HV-EMPL(1:8)   TO WL-EMPL(W-I)
                   PERFORM 4100-BARRE-ASCII
                   ADD 1 TO W-NB-LUS
               END-IF
           END-PERFORM.

           EXEC SQL CLOSE CURS-ART END-EXEC.
       4000-FIN.
           EXIT.

      ******************************************************************
       4100-BARRE-ASCII.
      ******************************************************************
      *    Construire la barre ASCII proportionnelle au stock/max
      *    Rapport : (QTE * 20) / MAX  capped a 20 caracteres '#'
           MOVE SPACES TO WL-BARRE(W-I).
           IF WL-MAX(W-I) > ZERO
               COMPUTE W-FILL-CNT ROUNDED =
                   (WL-QTE(W-I) * 20) / WL-MAX(W-I)
               IF W-FILL-CNT > 20
                   MOVE 20 TO W-FILL-CNT
               END-IF
               IF W-FILL-CNT < 0
                   MOVE 0 TO W-FILL-CNT
               END-IF
               MOVE SPACES TO W-BARRE-WK
               PERFORM VARYING W-J FROM 1 BY 1
                   UNTIL W-J > W-FILL-CNT
                   MOVE '#' TO W-BARRE-WK(W-J:1)
               END-PERFORM
               MOVE W-BARRE-WK TO WL-BARRE(W-I)
           END-IF.

      ******************************************************************
       5000-AFFICHER-ECRAN.
      ******************************************************************
      *    Construire et envoyer la carte GSTK001
           MOVE LOW-VALUE TO GSTK001O.

      *    Date/heure courante
           EXEC CICS ASKTIME ABSTIME(W-ABSTIME) END-EXEC.
           EXEC CICS FORMATTIME ABSTIME(W-ABSTIME)
               DDMMYYYY(W-DATE-JOUR)
               TIMESEP(':')
               TIME(W-HEURE-JOUR)
           END-EXEC.
           MOVE W-DATE-JOUR  TO W-DT-DATE.
           MOVE W-HEURE-JOUR TO W-DT-HEURE.
           MOVE W-DATETIME-EDI TO DATHRO IN GSTK001O.

      *    Informations session
           MOVE CA-OPERATEUR  TO OPENAMO IN GSTK001O.
           MOVE CA-TERMINAL   TO TERNAMO IN GSTK001O.
           MOVE CA-SESSION-ID TO SESNAMO IN GSTK001O.

      *    Pagination
           MOVE CA-PAGE-COURANTE TO W-ED-PAGCUR.
           MOVE CA-NB-PAGES      TO W-ED-PAGTOT.
           MOVE W-ED-PAGCUR TO PAGCURO IN GSTK001O.
           MOVE W-ED-PAGTOT TO PAGTOTO IN GSTK001O.

      *    Totaux
           MOVE W-TOT-CNT TO W-ED-TOTCNT.
           MOVE W-ED-TOTCNT TO TOTCNTO IN GSTK001O.
           MOVE W-TOT-VLR  TO W-ED-TOTVLR.
           MOVE W-ED-TOTVLR TO TOTVLRO IN GSTK001O.
           MOVE W-TOT-ALT  TO W-ED-TOTALT.
           MOVE W-ED-TOTALT TO TOTALTO IN GSTK001O.

      *    Filtres courants sur l'ecran (pour information operateur)
           MOVE W-FIL-CODE(1:10)  TO FILCOO  IN GSTK001O.
           MOVE W-FIL-LIBL(1:20)  TO FILLBO  IN GSTK001O.
           MOVE CA-FILTRE-CAT     TO FILCAO  IN GSTK001O.
           MOVE CA-FILTRE-STATUT  TO FILSTO  IN GSTK001O.

      *    Message retour
           IF CA-MSG-RETOUR NOT = SPACES
               MOVE CA-MSG-RETOUR TO MSGRTRO IN GSTK001O
           END-IF.

      *    Remplir les 10 lignes de donnees
           COMPUTE W-LIGNE-NO = (CA-PAGE-COURANTE - 1) * 10 + 1.
           MOVE W-LIGNE-NO TO W-ED-LIGNO.
           IF W-NB-LUS >= 1
               MOVE W-ED-LIGNO    TO LN01O  IN GSTK001O
               MOVE WL-CODE(1)    TO AC01O  IN GSTK001O
               MOVE WL-DESIG(1)   TO DS01O  IN GSTK001O
               MOVE WL-CATEG(1)   TO CT01O  IN GSTK001O
               MOVE WL-QTE(1)     TO W-ED-QTE
               MOVE W-ED-QTE      TO QT01O  IN GSTK001O
               MOVE WL-MIN(1)     TO W-ED-MIN
               MOVE W-ED-MIN      TO MN01O  IN GSTK001O
               MOVE WL-MAX(1)     TO W-ED-MAX
               MOVE W-ED-MAX      TO MX01O  IN GSTK001O
               MOVE WL-PRIX(1)    TO W-ED-PRIX
               MOVE W-ED-PRIX     TO PU01O  IN GSTK001O
               MOVE WL-VALEUR(1)  TO W-ED-VALEUR
               MOVE W-ED-VALEUR   TO VL01O  IN GSTK001O
               MOVE WL-BARRE(1)   TO GR01O  IN GSTK001O
               MOVE WL-STATUT(1)  TO ST01O  IN GSTK001O
               MOVE WL-EMPL(1)    TO EM01O  IN GSTK001O
           END-IF.
           COMPUTE W-LIGNE-NO = (CA-PAGE-COURANTE - 1) * 10 + 2.
           MOVE W-LIGNE-NO TO W-ED-LIGNO.
           IF W-NB-LUS >= 2
               MOVE W-ED-LIGNO    TO LN02O  IN GSTK001O
               MOVE WL-CODE(2)    TO AC02O  IN GSTK001O
               MOVE WL-DESIG(2)   TO DS02O  IN GSTK001O
               MOVE WL-CATEG(2)   TO CT02O  IN GSTK001O
               MOVE WL-QTE(2)     TO W-ED-QTE
               MOVE W-ED-QTE      TO QT02O  IN GSTK001O
               MOVE WL-MIN(2)     TO W-ED-MIN
               MOVE W-ED-MIN      TO MN02O  IN GSTK001O
               MOVE WL-MAX(2)     TO W-ED-MAX
               MOVE W-ED-MAX      TO MX02O  IN GSTK001O
               MOVE WL-PRIX(2)    TO W-ED-PRIX
               MOVE W-ED-PRIX     TO PU02O  IN GSTK001O
               MOVE WL-VALEUR(2)  TO W-ED-VALEUR
               MOVE W-ED-VALEUR   TO VL02O  IN GSTK001O
               MOVE WL-BARRE(2)   TO GR02O  IN GSTK001O
               MOVE WL-STATUT(2)  TO ST02O  IN GSTK001O
               MOVE WL-EMPL(2)    TO EM02O  IN GSTK001O
           END-IF.
           COMPUTE W-LIGNE-NO = (CA-PAGE-COURANTE - 1) * 10 + 3.
           MOVE W-LIGNE-NO TO W-ED-LIGNO.
           IF W-NB-LUS >= 3
               MOVE W-ED-LIGNO    TO LN03O  IN GSTK001O
               MOVE WL-CODE(3)    TO AC03O  IN GSTK001O
               MOVE WL-DESIG(3)   TO DS03O  IN GSTK001O
               MOVE WL-CATEG(3)   TO CT03O  IN GSTK001O
               MOVE WL-QTE(3)     TO W-ED-QTE
               MOVE W-ED-QTE      TO QT03O  IN GSTK001O
               MOVE WL-MIN(3)     TO W-ED-MIN
               MOVE W-ED-MIN      TO MN03O  IN GSTK001O
               MOVE WL-MAX(3)     TO W-ED-MAX
               MOVE W-ED-MAX      TO MX03O  IN GSTK001O
               MOVE WL-PRIX(3)    TO W-ED-PRIX
               MOVE W-ED-PRIX     TO PU03O  IN GSTK001O
               MOVE WL-VALEUR(3)  TO W-ED-VALEUR
               MOVE W-ED-VALEUR   TO VL03O  IN GSTK001O
               MOVE WL-BARRE(3)   TO GR03O  IN GSTK001O
               MOVE WL-STATUT(3)  TO ST03O  IN GSTK001O
               MOVE WL-EMPL(3)    TO EM03O  IN GSTK001O
           END-IF.
           COMPUTE W-LIGNE-NO = (CA-PAGE-COURANTE - 1) * 10 + 4.
           MOVE W-LIGNE-NO TO W-ED-LIGNO.
           IF W-NB-LUS >= 4
               MOVE W-ED-LIGNO    TO LN04O  IN GSTK001O
               MOVE WL-CODE(4)    TO AC04O  IN GSTK001O
               MOVE WL-DESIG(4)   TO DS04O  IN GSTK001O
               MOVE WL-CATEG(4)   TO CT04O  IN GSTK001O
               MOVE WL-QTE(4)     TO W-ED-QTE
               MOVE W-ED-QTE      TO QT04O  IN GSTK001O
               MOVE WL-MIN(4)     TO W-ED-MIN
               MOVE W-ED-MIN      TO MN04O  IN GSTK001O
               MOVE WL-MAX(4)     TO W-ED-MAX
               MOVE W-ED-MAX      TO MX04O  IN GSTK001O
               MOVE WL-PRIX(4)    TO W-ED-PRIX
               MOVE W-ED-PRIX     TO PU04O  IN GSTK001O
               MOVE WL-VALEUR(4)  TO W-ED-VALEUR
               MOVE W-ED-VALEUR   TO VL04O  IN GSTK001O
               MOVE WL-BARRE(4)   TO GR04O  IN GSTK001O
               MOVE WL-STATUT(4)  TO ST04O  IN GSTK001O
               MOVE WL-EMPL(4)    TO EM04O  IN GSTK001O
           END-IF.
           COMPUTE W-LIGNE-NO = (CA-PAGE-COURANTE - 1) * 10 + 5.
           MOVE W-LIGNE-NO TO W-ED-LIGNO.
           IF W-NB-LUS >= 5
               MOVE W-ED-LIGNO    TO LN05O  IN GSTK001O
               MOVE WL-CODE(5)    TO AC05O  IN GSTK001O
               MOVE WL-DESIG(5)   TO DS05O  IN GSTK001O
               MOVE WL-CATEG(5)   TO CT05O  IN GSTK001O
               MOVE WL-QTE(5)     TO W-ED-QTE
               MOVE W-ED-QTE      TO QT05O  IN GSTK001O
               MOVE WL-MIN(5)     TO W-ED-MIN
               MOVE W-ED-MIN      TO MN05O  IN GSTK001O
               MOVE WL-MAX(5)     TO W-ED-MAX
               MOVE W-ED-MAX      TO MX05O  IN GSTK001O
               MOVE WL-PRIX(5)    TO W-ED-PRIX
               MOVE W-ED-PRIX     TO PU05O  IN GSTK001O
               MOVE WL-VALEUR(5)  TO W-ED-VALEUR
               MOVE W-ED-VALEUR   TO VL05O  IN GSTK001O
               MOVE WL-BARRE(5)   TO GR05O  IN GSTK001O
               MOVE WL-STATUT(5)  TO ST05O  IN GSTK001O
               MOVE WL-EMPL(5)    TO EM05O  IN GSTK001O
           END-IF.
           COMPUTE W-LIGNE-NO = (CA-PAGE-COURANTE - 1) * 10 + 6.
           MOVE W-LIGNE-NO TO W-ED-LIGNO.
           IF W-NB-LUS >= 6
               MOVE W-ED-LIGNO    TO LN06O  IN GSTK001O
               MOVE WL-CODE(6)    TO AC06O  IN GSTK001O
               MOVE WL-DESIG(6)   TO DS06O  IN GSTK001O
               MOVE WL-CATEG(6)   TO CT06O  IN GSTK001O
               MOVE WL-QTE(6)     TO W-ED-QTE
               MOVE W-ED-QTE      TO QT06O  IN GSTK001O
               MOVE WL-MIN(6)     TO W-ED-MIN
               MOVE W-ED-MIN      TO MN06O  IN GSTK001O
               MOVE WL-MAX(6)     TO W-ED-MAX
               MOVE W-ED-MAX      TO MX06O  IN GSTK001O
               MOVE WL-PRIX(6)    TO W-ED-PRIX
               MOVE W-ED-PRIX     TO PU06O  IN GSTK001O
               MOVE WL-VALEUR(6)  TO W-ED-VALEUR
               MOVE W-ED-VALEUR   TO VL06O  IN GSTK001O
               MOVE WL-BARRE(6)   TO GR06O  IN GSTK001O
               MOVE WL-STATUT(6)  TO ST06O  IN GSTK001O
               MOVE WL-EMPL(6)    TO EM06O  IN GSTK001O
           END-IF.
           COMPUTE W-LIGNE-NO = (CA-PAGE-COURANTE - 1) * 10 + 7.
           MOVE W-LIGNE-NO TO W-ED-LIGNO.
           IF W-NB-LUS >= 7
               MOVE W-ED-LIGNO    TO LN07O  IN GSTK001O
               MOVE WL-CODE(7)    TO AC07O  IN GSTK001O
               MOVE WL-DESIG(7)   TO DS07O  IN GSTK001O
               MOVE WL-CATEG(7)   TO CT07O  IN GSTK001O
               MOVE WL-QTE(7)     TO W-ED-QTE
               MOVE W-ED-QTE      TO QT07O  IN GSTK001O
               MOVE WL-MIN(7)     TO W-ED-MIN
               MOVE W-ED-MIN      TO MN07O  IN GSTK001O
               MOVE WL-MAX(7)     TO W-ED-MAX
               MOVE W-ED-MAX      TO MX07O  IN GSTK001O
               MOVE WL-PRIX(7)    TO W-ED-PRIX
               MOVE W-ED-PRIX     TO PU07O  IN GSTK001O
               MOVE WL-VALEUR(7)  TO W-ED-VALEUR
               MOVE W-ED-VALEUR   TO VL07O  IN GSTK001O
               MOVE WL-BARRE(7)   TO GR07O  IN GSTK001O
               MOVE WL-STATUT(7)  TO ST07O  IN GSTK001O
               MOVE WL-EMPL(7)    TO EM07O  IN GSTK001O
           END-IF.
           COMPUTE W-LIGNE-NO = (CA-PAGE-COURANTE - 1) * 10 + 8.
           MOVE W-LIGNE-NO TO W-ED-LIGNO.
           IF W-NB-LUS >= 8
               MOVE W-ED-LIGNO    TO LN08O  IN GSTK001O
               MOVE WL-CODE(8)    TO AC08O  IN GSTK001O
               MOVE WL-DESIG(8)   TO DS08O  IN GSTK001O
               MOVE WL-CATEG(8)   TO CT08O  IN GSTK001O
               MOVE WL-QTE(8)     TO W-ED-QTE
               MOVE W-ED-QTE      TO QT08O  IN GSTK001O
               MOVE WL-MIN(8)     TO W-ED-MIN
               MOVE W-ED-MIN      TO MN08O  IN GSTK001O
               MOVE WL-MAX(8)     TO W-ED-MAX
               MOVE W-ED-MAX      TO MX08O  IN GSTK001O
               MOVE WL-PRIX(8)    TO W-ED-PRIX
               MOVE W-ED-PRIX     TO PU08O  IN GSTK001O
               MOVE WL-VALEUR(8)  TO W-ED-VALEUR
               MOVE W-ED-VALEUR   TO VL08O  IN GSTK001O
               MOVE WL-BARRE(8)   TO GR08O  IN GSTK001O
               MOVE WL-STATUT(8)  TO ST08O  IN GSTK001O
               MOVE WL-EMPL(8)    TO EM08O  IN GSTK001O
           END-IF.
           COMPUTE W-LIGNE-NO = (CA-PAGE-COURANTE - 1) * 10 + 9.
           MOVE W-LIGNE-NO TO W-ED-LIGNO.
           IF W-NB-LUS >= 9
               MOVE W-ED-LIGNO    TO LN09O  IN GSTK001O
               MOVE WL-CODE(9)    TO AC09O  IN GSTK001O
               MOVE WL-DESIG(9)   TO DS09O  IN GSTK001O
               MOVE WL-CATEG(9)   TO CT09O  IN GSTK001O
               MOVE WL-QTE(9)     TO W-ED-QTE
               MOVE W-ED-QTE      TO QT09O  IN GSTK001O
               MOVE WL-MIN(9)     TO W-ED-MIN
               MOVE W-ED-MIN      TO MN09O  IN GSTK001O
               MOVE WL-MAX(9)     TO W-ED-MAX
               MOVE W-ED-MAX      TO MX09O  IN GSTK001O
               MOVE WL-PRIX(9)    TO W-ED-PRIX
               MOVE W-ED-PRIX     TO PU09O  IN GSTK001O
               MOVE WL-VALEUR(9)  TO W-ED-VALEUR
               MOVE W-ED-VALEUR   TO VL09O  IN GSTK001O
               MOVE WL-BARRE(9)   TO GR09O  IN GSTK001O
               MOVE WL-STATUT(9)  TO ST09O  IN GSTK001O
               MOVE WL-EMPL(9)    TO EM09O  IN GSTK001O
           END-IF.
           COMPUTE W-LIGNE-NO = (CA-PAGE-COURANTE - 1) * 10 + 10.
           MOVE W-LIGNE-NO TO W-ED-LIGNO.
           IF W-NB-LUS >= 10
               MOVE W-ED-LIGNO    TO LN10O  IN GSTK001O
               MOVE WL-CODE(10)   TO AC10O  IN GSTK001O
               MOVE WL-DESIG(10)  TO DS10O  IN GSTK001O
               MOVE WL-CATEG(10)  TO CT10O  IN GSTK001O
               MOVE WL-QTE(10)    TO W-ED-QTE
               MOVE W-ED-QTE      TO QT10O  IN GSTK001O
               MOVE WL-MIN(10)    TO W-ED-MIN
               MOVE W-ED-MIN      TO MN10O  IN GSTK001O
               MOVE WL-MAX(10)    TO W-ED-MAX
               MOVE W-ED-MAX      TO MX10O  IN GSTK001O
               MOVE WL-PRIX(10)   TO W-ED-PRIX
               MOVE W-ED-PRIX     TO PU10O  IN GSTK001O
               MOVE WL-VALEUR(10) TO W-ED-VALEUR
               MOVE W-ED-VALEUR   TO VL10O  IN GSTK001O
               MOVE WL-BARRE(10)  TO GR10O  IN GSTK001O
               MOVE WL-STATUT(10) TO ST10O  IN GSTK001O
               MOVE WL-EMPL(10)   TO EM10O  IN GSTK001O
           END-IF.

      *    Sauvegarder les filtres dans le FILLER de la COMMAREA
           MOVE W-FIL-CODE TO GSTK-COMMAREA(209:12).
           MOVE W-FIL-LIBL TO GSTK-COMMAREA(221:22).

      *    Envoyer l'ecran et suspendre en attendant la prochaine saisie
           EXEC CICS SEND MAP(W-MAP) MAPSET(W-MAPSET)
               FROM(GSTK001O)
               ERASE CURSOR
               RESP(W-RESP) RESP2(W-RESP2)
           END-EXEC.
           IF W-RESP NOT = DFHRESP(NORMAL)
               PERFORM 9000-ERREUR-CICS
           END-IF.
           EXEC CICS RETURN
               TRANSID(W-TRANS-ID)
               COMMAREA(GSTK-COMMAREA)
               LENGTH(W-CA-LEN)
           END-EXEC.

      ******************************************************************
       6000-RETOUR-MENU.
      ******************************************************************
           MOVE 'G000    ' TO CA-TRAN-RETOUR.
           MOVE W-PROG-ID  TO CA-MSG-RETOUR(1:8).
           EXEC CICS XCTL PROGRAM('GSTK000 ')
               COMMAREA(GSTK-COMMAREA) LENGTH(W-CA-LEN)
           END-EXEC.

      ******************************************************************
       9000-ERREUR-CICS.
      ******************************************************************
           MOVE 'ERREUR CICS - CONTACTER SUPPORT'
               TO CA-MSG-RETOUR.
           EXEC CICS ABEND ABCODE('G001') NODUMP END-EXEC.

      ******************************************************************
       9100-ERREUR-SQL.
      ******************************************************************
           IF SQLCODE = +100
               MOVE 'AUCUN ARTICLE TROUVE POUR CES CRITERES'
                   TO CA-MSG-RETOUR
               MOVE 0 TO W-TOT-CNT W-NB-LUS
               MOVE 1 TO CA-NB-PAGES CA-PAGE-COURANTE
           ELSE
               MOVE 'ERREUR SQL - CODE : '  TO CA-MSG-RETOUR
               MOVE SQLCODE                 TO CA-MSG-RETOUR(22:6)
           END-IF.
