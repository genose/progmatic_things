      *================================================================*
      * PROGRAMME  : GSTK003                                         *
      * AUTEUR     : Sebastien Cotillard                               *
      * DATE       : 2026-08-26                                       *
      * VERSION    : 1.0                                              *
      *----------------------------------------------------------------*
      * DESCRIPTION : SORTIE MARCHANDISE - SAISIE BON DE SORTIE      *
      *   Recherche article, saisie quantite/demandeur/infos,         *
      *   validation : INSERT mouvement + UPDATE stock article.        *
      *   Alerte si stock apres sortie < 0 (bloque).                  *
      *   Alerte si stock apres sortie < minimum (avertissement).     *
      *----------------------------------------------------------------*
      * TRANSID    : G003                                             *
      * MAPSET     : GSTK003M      MAP : GSTK003                     *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. GSTK003.
       AUTHOR. ETUDIANT-COBOL.
       DATE-WRITTEN. 2026-08-26.

       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       SPECIAL-NAMES.
           DECIMAL-POINT IS COMMA.

       DATA DIVISION.

      *================================================================*
       WORKING-STORAGE SECTION.

       01 W-PROG-ID         PIC X(8)  VALUE 'GSTK003 '.
       01 W-TRANS-ID        PIC X(4)  VALUE 'G003'.
       01 W-MAPSET          PIC X(8)  VALUE 'GSTK003M'.
       01 W-MAP             PIC X(7)  VALUE 'GSTK003'.
       01 W-CA-LEN          PIC S9(4) COMP VALUE 263.
       01 W-PROG-CIBLE      PIC X(8)  VALUE SPACES.
       01 W-RESP            PIC S9(8) COMP VALUE 0.
       01 W-RESP2           PIC S9(8) COMP VALUE 0.

      *--- DATE / HEURE -----------------------------------------------
       01 W-ABSTIME         PIC S9(15) COMP-3 VALUE 0.
       01 W-DATE-JOUR       PIC X(10) VALUE SPACES.
       01 W-HEURE-JOUR      PIC X(8)  VALUE SPACES.
       01 W-DATETIME-EDI.
          05 W-DT-DATE      PIC X(10).
          05 FILLER         PIC X     VALUE SPACE.
          05 W-DT-HEURE     PIC X(8).

      *--- INDICATEURS ------------------------------------------------
       01 W-ART-TROUVE      PIC X     VALUE 'N'.
          88 ARTICLE-TROUVE           VALUE 'O'.
          88 ARTICLE-ABSENT           VALUE 'N'.

      *--- DONNEES ARTICLE CHARGE -------------------------------------
       01 W-ART-CODE        PIC X(10) VALUE SPACES.
       01 W-ART-DESIG       PIC X(50) VALUE SPACES.
       01 W-ART-CATEG       PIC X(15) VALUE SPACES.
       01 W-ART-EMPL        PIC X(15) VALUE SPACES.
       01 W-ART-STAT        PIC X(10) VALUE SPACES.
       01 W-ART-QTE         PIC S9(10)V999  COMP-3 VALUE 0.
       01 W-ART-MIN         PIC S9(10)V999  COMP-3 VALUE 0.
       01 W-ART-MAX         PIC S9(10)V999  COMP-3 VALUE 0.
       01 W-ART-PVT         PIC S9(10)V9999 COMP-3 VALUE 0.

      *--- DONNEES MOUVEMENT ------------------------------------------
       01 W-QTE-SOR         PIC S9(10)V999  COMP-3 VALUE 0.
       01 W-MONTANT-HT      PIC S9(14)V99   COMP-3 VALUE 0.
       01 W-MVT-ID          PIC S9(12)      COMP-3 VALUE 0.
       01 W-QTE-APRES       PIC S9(10)V999  COMP-3 VALUE 0.

      *--- EDITIONS ---------------------------------------------------
       01 W-ED-STK          PIC Z(7),999.
       01 W-ED-MIN          PIC Z(7),999.
       01 W-ED-MAX          PIC Z(7),999.
       01 W-ED-PVT          PIC ZZZ9,9999.
       01 W-ED-QTE          PIC Z(7),999.
       01 W-ED-MONTANT      PIC Z(11),99.
       01 W-ED-APRES        PIC Z(7),999.

      *--- HOST VARIABLES SQL -----------------------------------------
       01 HV-ARTCOD         PIC X(10).
       01 HV-DESIG          PIC X(50).
       01 HV-CATEG          PIC X(15).
       01 HV-EMPL           PIC X(15).
       01 HV-STAT           PIC X(10).
       01 HV-QTE-STK        PIC S9(10)V999  COMP-3.
       01 HV-MIN            PIC S9(10)V999  COMP-3.
       01 HV-MAX            PIC S9(10)V999  COMP-3.
       01 HV-PVT            PIC S9(10)V9999 COMP-3.
       01 HV-MVT-ID         PIC S9(12)      COMP-3.
       01 HV-DATE-MVT       PIC X(10).
       01 HV-HEURE-MVT      PIC X(8).
       01 HV-QTE-SOR        PIC S9(10)V999  COMP-3.
       01 HV-MONTANT        PIC S9(14)V99   COMP-3.
       01 HV-QTE-AVANT      PIC S9(10)V999  COMP-3.
       01 HV-QTE-APRES      PIC S9(10)V999  COMP-3.
       01 HV-DEMAND         PIC X(60).
       01 HV-NUMBON         PIC X(12).
       01 HV-NUMCMD         PIC X(20).
       01 HV-CENTCO         PIC X(15).
       01 HV-MOTIF          PIC X(20).
       01 HV-COMMNT         PIC X(80).
       01 HV-OPERATEUR      PIC X(10).
       01 HV-TERMINAL       PIC X(10).

           EXEC SQL INCLUDE SQLCA END-EXEC.

       COPY GSTK003M.
       COPY DFHAID.
       COPY DFHBMSCA.
       COPY GSTKCOMM.

      *================================================================*
       LINKAGE SECTION.
       01 DFHCOMMAREA        PIC X(263).

      *================================================================*
       PROCEDURE DIVISION.
      *================================================================*

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
           MOVE 'G003    ' TO CA-TRAN-RETOUR.
           MOVE EIBTRMID   TO CA-TERMINAL.
           MOVE SPACES     TO CA-MSG-RETOUR.
           IF CA-ART-CODE-SELEC NOT = SPACES
               MOVE CA-ART-CODE-SELEC TO W-ART-CODE
               PERFORM 3000-RECHERCHER-ARTICLE
           END-IF.
           PERFORM 5000-AFFICHER-ECRAN.

      ******************************************************************
       2000-RETOUR-TRANSACTION.
      ******************************************************************
           MOVE SPACES TO CA-MSG-RETOUR.
           EXEC CICS RECEIVE MAP(W-MAP) MAPSET(W-MAPSET)
               INTO(GSTK003I)
               RESP(W-RESP) RESP2(W-RESP2)
           END-EXEC.
           IF W-RESP = DFHRESP(MAPFAIL)
               IF CA-ART-CODE-SELEC NOT = SPACES
                   MOVE CA-ART-CODE-SELEC TO W-ART-CODE
                   PERFORM 3000-RECHERCHER-ARTICLE
               END-IF
               PERFORM 5000-AFFICHER-ECRAN
               GO TO 2000-FIN
           END-IF.
           EVALUATE EIBAID
               WHEN DFHPF3
                   PERFORM 6000-RETOUR-MENU
               WHEN DFHPF5
                   PERFORM 2100-LIRE-CODE-ECRAN
                   IF W-ART-CODE = SPACES
                       MOVE 'SAISISSEZ UN CODE ARTICLE A RECHERCHER'
                           TO CA-MSG-RETOUR
                   ELSE
                       PERFORM 3000-RECHERCHER-ARTICLE
                   END-IF
                   PERFORM 5000-AFFICHER-ECRAN
               WHEN DFHPF6
                   PERFORM 2100-LIRE-CODE-ECRAN
                   PERFORM 2200-LIRE-SAISIE
                   PERFORM 4000-VALIDER-SORTIE
               WHEN DFHPF12
                   MOVE 'SAISIE ANNULEE' TO CA-MSG-RETOUR
                   PERFORM 6000-RETOUR-MENU
               WHEN DFHENTER
                   PERFORM 2100-LIRE-CODE-ECRAN
                   PERFORM 2200-LIRE-SAISIE
                   IF ARTICLE-TROUVE AND W-QTE-SOR > ZERO
                       COMPUTE W-MONTANT-HT = W-QTE-SOR * W-ART-PVT
                       COMPUTE W-QTE-APRES  = W-ART-QTE - W-QTE-SOR
                       MOVE 'MONTANT CALCULE - PF6 POUR VALIDER'
                           TO CA-MSG-RETOUR
                   ELSE
                       MOVE 'RECHERCHEZ ARTICLE (PF5) ET SAISISSEZ'
                           TO CA-MSG-RETOUR
                   END-IF
                   PERFORM 5000-AFFICHER-ECRAN
               WHEN OTHER
                   MOVE 'TOUCHE NON RECONNUE - PF5/PF6/PF3/PF12'
                       TO CA-MSG-RETOUR
                   PERFORM 5000-AFFICHER-ECRAN
           END-EVALUATE.
       2000-FIN.
           EXIT.

      ******************************************************************
       2100-LIRE-CODE-ECRAN.
      ******************************************************************
           IF ARTCODL IN GSTK003I > ZERO
           AND ARTCODI IN GSTK003I NOT = SPACES
               MOVE ARTCODI IN GSTK003I TO W-ART-CODE
           END-IF.

      ******************************************************************
       2200-LIRE-SAISIE.
      ******************************************************************
           IF QTESOL IN GSTK003I > ZERO
           AND QTESORI IN GSTK003I NOT = SPACES
               MOVE QTESORI IN GSTK003I TO W-QTE-SOR
           ELSE
               MOVE ZERO TO W-QTE-SOR
           END-IF.
           IF DEMANDL IN GSTK003I > ZERO
               MOVE DEMANDI IN GSTK003I TO HV-DEMAND
           ELSE
               MOVE SPACES TO HV-DEMAND
           END-IF.
           IF NUMBONL IN GSTK003I > ZERO
               MOVE NUMBONI IN GSTK003I TO HV-NUMBON
           ELSE
               MOVE SPACES TO HV-NUMBON
           END-IF.
           IF NUMCMDL IN GSTK003I > ZERO
               MOVE NUMCMDI IN GSTK003I TO HV-NUMCMD
           ELSE
               MOVE SPACES TO HV-NUMCMD
           END-IF.
           IF CENTCOL IN GSTK003I > ZERO
               MOVE CENTCOI IN GSTK003I TO HV-CENTCO
           ELSE
               MOVE SPACES TO HV-CENTCO
           END-IF.
           IF MOTIFXL IN GSTK003I > ZERO
               MOVE MOTIFXI IN GSTK003I TO HV-MOTIF
           ELSE
               MOVE SPACES TO HV-MOTIF
           END-IF.
           IF COMMNTI IN GSTK003I NOT = SPACES
               MOVE COMMNTI IN GSTK003I TO HV-COMMNT
           ELSE
               MOVE SPACES TO HV-COMMNT
           END-IF.

      ******************************************************************
       3000-RECHERCHER-ARTICLE.
      ******************************************************************
           MOVE 'N'        TO W-ART-TROUVE.
           MOVE W-ART-CODE TO HV-ARTCOD.
           EXEC SQL
               SELECT ART_CODE,
                      ART_DESIGNATION,
                      ART_CATEGORIE,
                      ART_EMPLACEMENT,
                      ART_STATUT,
                      ART_QTE_STOCK,
                      ART_QTE_MIN,
                      ART_QTE_MAX,
                      ART_PRIX_VENTE
               INTO   :HV-ARTCOD,  :HV-DESIG,   :HV-CATEG,
                      :HV-EMPL,    :HV-STAT,    :HV-QTE-STK,
                      :HV-MIN,     :HV-MAX,     :HV-PVT
               FROM   GSTK.ARTICLES
               WHERE  ART_CODE = :HV-ARTCOD
           END-EXEC.
           EVALUATE SQLCODE
               WHEN 0
                   MOVE 'O'        TO W-ART-TROUVE
                   MOVE HV-DESIG   TO W-ART-DESIG
                   MOVE HV-CATEG   TO W-ART-CATEG
                   MOVE HV-EMPL    TO W-ART-EMPL
                   MOVE HV-STAT    TO W-ART-STAT
                   MOVE HV-QTE-STK TO W-ART-QTE
                   MOVE HV-MIN     TO W-ART-MIN
                   MOVE HV-MAX     TO W-ART-MAX
                   MOVE HV-PVT     TO W-ART-PVT
                   MOVE 'ARTICLE TROUVE - SAISISSEZ QUANTITE / PF6'
                       TO CA-MSG-RETOUR
               WHEN +100
                   MOVE 'ARTICLE INTROUVABLE : '
                       TO CA-MSG-RETOUR
                   MOVE W-ART-CODE TO CA-MSG-RETOUR(24:10)
               WHEN OTHER
                   PERFORM 9100-ERREUR-SQL
           END-EVALUATE.

      ******************************************************************
       4000-VALIDER-SORTIE.
      ******************************************************************
           IF ARTICLE-ABSENT OR W-ART-CODE = SPACES
               MOVE 'RECHERCHEZ ET SELECTIONNEZ UN ARTICLE (PF5)'
                   TO CA-MSG-RETOUR
               PERFORM 5000-AFFICHER-ECRAN
               GO TO 4000-FIN
           END-IF.
           IF W-ART-STAT = 'ARCHIVE'
               MOVE 'ARTICLE ARCHIVE - SORTIE IMPOSSIBLE'
                   TO CA-MSG-RETOUR
               PERFORM 5000-AFFICHER-ECRAN
               GO TO 4000-FIN
           END-IF.
           IF W-QTE-SOR <= ZERO
               MOVE 'QUANTITE SORTIE DOIT ETRE SUPERIEURE A ZERO'
                   TO CA-MSG-RETOUR
               PERFORM 5000-AFFICHER-ECRAN
               GO TO 4000-FIN
           END-IF.
           COMPUTE W-QTE-APRES = W-ART-QTE - W-QTE-SOR.
           IF W-QTE-APRES < ZERO
               MOVE 'STOCK INSUFFISANT - SORTIE BLOQUEE'
                   TO CA-MSG-RETOUR
               COMPUTE W-QTE-APRES = W-ART-QTE - W-QTE-SOR
               MOVE W-QTE-APRES TO W-ED-APRES
               MOVE 'STOCK APRES SORTIE SERAIT : '
                   TO ALERTESO IN GSTK003O
               MOVE W-ED-APRES TO ALERTESO(29:10) IN GSTK003O
               PERFORM 5000-AFFICHER-ECRAN
               GO TO 4000-FIN
           END-IF.
      *    Mouvement valide : sauvegarder
           COMPUTE W-MONTANT-HT = W-QTE-SOR * W-ART-PVT.
           MOVE W-ART-QTE   TO HV-QTE-AVANT.
           MOVE W-QTE-APRES TO HV-QTE-APRES.
           MOVE W-ART-CODE  TO HV-ARTCOD.
           MOVE W-ART-DESIG TO HV-DESIG.
           MOVE W-QTE-SOR   TO HV-QTE-SOR.
           MOVE W-ART-PVT   TO HV-PVT.
           MOVE W-MONTANT-HT TO HV-MONTANT.
           MOVE CA-OPERATEUR TO HV-OPERATEUR.
           MOVE CA-TERMINAL  TO HV-TERMINAL.
           MOVE W-DATE-JOUR  TO HV-DATE-MVT.
           MOVE W-HEURE-JOUR TO HV-HEURE-MVT.
           EXEC SQL
               SELECT NEXT VALUE FOR GSTK.SEQ_MVT
               INTO   :HV-MVT-ID
               FROM   SYSIBM.SYSDUMMY1
           END-EXEC.
           IF SQLCODE NOT = 0
               PERFORM 9100-ERREUR-SQL
               PERFORM 5000-AFFICHER-ECRAN
               GO TO 4000-FIN
           END-IF.
           MOVE HV-MVT-ID TO W-MVT-ID.
           PERFORM 4100-SAUVER-MOUVEMENT.
           IF SQLCODE = 0
               PERFORM 4200-MAJ-STOCK
           END-IF.
           IF SQLCODE = 0
               EXEC CICS SYNCPOINT END-EXEC
               MOVE 'SORTIE ENREGISTREE - MVT N. '
                   TO CA-MSG-RETOUR
               MOVE W-MVT-ID TO CA-MSG-RETOUR(29:12)
               IF W-QTE-APRES < W-ART-MIN
                   MOVE 'ATTENTION : STOCK APRES SORTIE EST SOUS LE MIN'
                       TO ALERTESO IN GSTK003O
               END-IF
               MOVE 'VALIDE' TO ETATMVTO IN GSTK003O
               MOVE SPACES TO W-ART-CODE W-ART-DESIG
               MOVE 'N'    TO W-ART-TROUVE
               MOVE ZERO   TO W-QTE-SOR W-MONTANT-HT
           ELSE
               EXEC CICS SYNCPOINT ROLLBACK END-EXEC
               MOVE 'ERREUR SAUVEGARDE - OPERATION ANNULEE'
                   TO CA-MSG-RETOUR
           END-IF.
           PERFORM 5000-AFFICHER-ECRAN.
       4000-FIN.
           EXIT.

      ******************************************************************
       4100-SAUVER-MOUVEMENT.
      ******************************************************************
           EXEC SQL
               INSERT INTO GSTK.MOUVEMENTS_STOCK (
                   MVT_ID,        MVT_DATE,       MVT_HEURE,
                   MVT_TYPE,      MVT_SENS,       MVT_ART_CODE,
                   MVT_DESIGNATION, MVT_QUANTITE, MVT_PRIX_UNIT,
                   MVT_MONTANT_HT, MVT_STOCK_AVANT, MVT_STOCK_APRES,
                   MVT_NUM_BON,   MVT_NUM_LOT,    MVT_TIERS,
                   MVT_EMPLACEMENT, MVT_CENTRE_COUT, MVT_OPERATEUR,
                   MVT_POSTE,     MVT_COMMENTAIRE
               ) VALUES (
                   :HV-MVT-ID,   :HV-DATE-MVT,   :HV-HEURE-MVT,
                   'BON SORTIE', 'S',            :HV-ARTCOD,
                   :HV-DESIG,   :HV-QTE-SOR,    :HV-PVT,
                   :HV-MONTANT, :HV-QTE-AVANT,  :HV-QTE-APRES,
                   :HV-NUMBON,  SPACES,          :HV-DEMAND,
                   :HV-EMPL,    :HV-CENTCO,      :HV-OPERATEUR,
                   :HV-TERMINAL, :HV-COMMNT
               )
           END-EXEC.
           IF SQLCODE NOT = 0
               PERFORM 9100-ERREUR-SQL
           END-IF.

      ******************************************************************
       4200-MAJ-STOCK.
      ******************************************************************
           EXEC SQL
               UPDATE GSTK.ARTICLES
               SET    ART_QTE_STOCK = ART_QTE_STOCK - :HV-QTE-SOR,
                      ART_DATE_MAJ  = :HV-DATE-MVT,
                      ART_OPERATEUR = :HV-OPERATEUR
               WHERE  ART_CODE = :HV-ARTCOD
           END-EXEC.
           IF SQLCODE NOT = 0
               PERFORM 9100-ERREUR-SQL
           END-IF.

      ******************************************************************
       5000-AFFICHER-ECRAN.
      ******************************************************************
           MOVE LOW-VALUE TO GSTK003O.
           EXEC CICS ASKTIME ABSTIME(W-ABSTIME) END-EXEC.
           EXEC CICS FORMATTIME ABSTIME(W-ABSTIME)
               DDMMYYYY(W-DATE-JOUR)
               TIMESEP(':')
               TIME(W-HEURE-JOUR)
           END-EXEC.
           MOVE W-DATE-JOUR  TO W-DT-DATE.
           MOVE W-HEURE-JOUR TO W-DT-HEURE.
           MOVE W-DATETIME-EDI TO DATHRO  IN GSTK003O.
           MOVE CA-OPERATEUR   TO OPENAMO  IN GSTK003O.
           MOVE CA-TERMINAL    TO TERNAMO  IN GSTK003O.
           MOVE CA-SESSION-ID  TO SESNAMO  IN GSTK003O.
           IF W-ART-CODE NOT = SPACES
               MOVE W-ART-CODE TO ARTCODO IN GSTK003O
           END-IF.
           IF ARTICLE-TROUVE
               MOVE W-ART-DESIG(1:30) TO ARTDESO IN GSTK003O
               MOVE W-ART-STAT        TO ARTSTAO IN GSTK003O
               MOVE W-ART-CATEG       TO ARTCATO IN GSTK003O
               MOVE W-ART-EMPL        TO ARTEMPO IN GSTK003O
               MOVE W-ART-QTE         TO W-ED-STK
               MOVE W-ED-STK          TO ARTSTKO IN GSTK003O
               MOVE W-ART-MIN         TO W-ED-MIN
               MOVE W-ED-MIN          TO ARTMINO IN GSTK003O
               MOVE W-ART-MAX         TO W-ED-MAX
               MOVE W-ED-MAX          TO ARTMAXO IN GSTK003O
               MOVE W-ART-PVT         TO W-ED-PVT
               MOVE W-ED-PVT          TO ARTPVTO IN GSTK003O
           END-IF.
           IF W-QTE-SOR > ZERO
               MOVE W-QTE-SOR  TO W-ED-QTE
               MOVE W-ED-QTE   TO QTESORO  IN GSTK003O
           END-IF.
           IF W-MONTANT-HT > ZERO
               MOVE W-MONTANT-HT TO W-ED-MONTANT
               MOVE W-ED-MONTANT TO MNTHTEO  IN GSTK003O
           END-IF.
           IF CA-MSG-RETOUR NOT = SPACES
               MOVE CA-MSG-RETOUR TO MSGRTRO IN GSTK003O
           END-IF.
           MOVE -1 TO ARTCODL IN GSTK003I.
           EXEC CICS SEND MAP(W-MAP) MAPSET(W-MAPSET)
               FROM(GSTK003O)
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
           EXEC CICS XCTL PROGRAM('GSTK000 ')
               COMMAREA(GSTK-COMMAREA) LENGTH(W-CA-LEN)
           END-EXEC.

      ******************************************************************
       9000-ERREUR-CICS.
      ******************************************************************
           MOVE 'ERREUR CICS - CONTACTER SUPPORT'
               TO CA-MSG-RETOUR.
           EXEC CICS ABEND ABCODE('G003') NODUMP END-EXEC.

      ******************************************************************
       9100-ERREUR-SQL.
      ******************************************************************
           IF SQLCODE = +100
               MOVE 'AUCUNE DONNEE TROUVEE'
                   TO CA-MSG-RETOUR
           ELSE
               MOVE 'ERREUR SQL - CODE : '  TO CA-MSG-RETOUR
               MOVE SQLCODE                 TO CA-MSG-RETOUR(22:6)
           END-IF.
