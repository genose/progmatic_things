      *================================================================*
      * PROGRAMME  : GSTK002                                         *
      * AUTEUR     : [NOM ETUDIANT]                                   *
      * DATE       : 2026-08-26                                       *
      * VERSION    : 1.0                                              *
      *----------------------------------------------------------------*
      * DESCRIPTION : ENTREE MARCHANDISE - SAISIE BON DE RECEPTION   *
      *   Recherche article par code, saisie quantite/prix/infos,     *
      *   validation : INSERT mouvement + UPDATE stock article.        *
      *----------------------------------------------------------------*
      * TRANSID    : G002                                             *
      * MAPSET     : GSTK002M      MAP : GSTK002                     *
      * COMMAREA   : GSTK-COMMAREA (263 octets)                      *
      *----------------------------------------------------------------*
      * TOUCHES :                                                      *
      *   ENTREE   -> Calculer montant HT (apercu avant validation)   *
      *   PF3      -> Retour menu (GSTK000)                           *
      *   PF5      -> Rechercher article par code saisi               *
      *   PF6      -> Valider l entree (INSERT + UPDATE)              *
      *   PF12     -> Annuler (retour menu sans sauvegarde)           *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. GSTK002.
       AUTHOR. ETUDIANT-COBOL.
       DATE-WRITTEN. 2026-08-26.

       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       SPECIAL-NAMES.
           DECIMAL-POINT IS COMMA.

       DATA DIVISION.

      *================================================================*
       WORKING-STORAGE SECTION.

       01 W-PROG-ID         PIC X(8)  VALUE 'GSTK002 '.
       01 W-TRANS-ID        PIC X(4)  VALUE 'G002'.
       01 W-MAPSET          PIC X(8)  VALUE 'GSTK002M'.
       01 W-MAP             PIC X(7)  VALUE 'GSTK002'.
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
       01 W-SAISIE-OK       PIC X     VALUE 'N'.
          88 SAISIE-VALIDE            VALUE 'O'.

      *--- DONNEES ARTICLE CHARGE -------------------------------------
       01 W-ART-CODE        PIC X(10) VALUE SPACES.
       01 W-ART-DESIG       PIC X(50) VALUE SPACES.
       01 W-ART-CATEG       PIC X(15) VALUE SPACES.
       01 W-ART-EMPL        PIC X(15) VALUE SPACES.
       01 W-ART-STAT        PIC X(10) VALUE SPACES.
       01 W-ART-QTE         PIC S9(10)V999  COMP-3 VALUE 0.
       01 W-ART-MIN         PIC S9(10)V999  COMP-3 VALUE 0.
       01 W-ART-MAX         PIC S9(10)V999  COMP-3 VALUE 0.
       01 W-ART-PUA         PIC S9(10)V9999 COMP-3 VALUE 0.
       01 W-ART-FRN         PIC X(10) VALUE SPACES.
       01 W-ART-FRNNOM      PIC X(40) VALUE SPACES.

      *--- DONNEES MOUVEMENT -----------------------------------------
       01 W-QTE-ENT         PIC S9(10)V999  COMP-3 VALUE 0.
       01 W-PRIX-ACH        PIC S9(10)V9999 COMP-3 VALUE 0.
       01 W-MONTANT-HT      PIC S9(14)V99   COMP-3 VALUE 0.
       01 W-MVT-ID          PIC S9(12)      COMP-3 VALUE 0.
       01 W-QTE-APRES       PIC S9(10)V999  COMP-3 VALUE 0.

      *--- EDITIONS ---------------------------------------------------
       01 W-ED-STK          PIC Z(7),999.
       01 W-ED-MIN          PIC Z(7),999.
       01 W-ED-MAX          PIC Z(7),999.
       01 W-ED-PUA          PIC ZZZ9,9999.
       01 W-ED-QTE          PIC Z(7),999.
       01 W-ED-PRIX         PIC ZZZ9,9999.
       01 W-ED-MONTANT      PIC Z(11),99.

      *--- HOST VARIABLES SQL -----------------------------------------
       01 HV-ARTCOD         PIC X(10).
       01 HV-DESIG          PIC X(50).
       01 HV-CATEG          PIC X(15).
       01 HV-EMPL           PIC X(15).
       01 HV-STAT           PIC X(10).
       01 HV-QTE-STK        PIC S9(10)V999  COMP-3.
       01 HV-MIN            PIC S9(10)V999  COMP-3.
       01 HV-MAX            PIC S9(10)V999  COMP-3.
       01 HV-PUA            PIC S9(10)V9999 COMP-3.
       01 HV-FRN-CODE       PIC X(10).
       01 HV-FRN-NOM        PIC X(40).
       01 HV-MVT-ID         PIC S9(12)      COMP-3.
       01 HV-DATE-MVT       PIC X(10).
       01 HV-HEURE-MVT      PIC X(8).
       01 HV-QTE-ENT        PIC S9(10)V999  COMP-3.
       01 HV-PRIX-ACH       PIC S9(10)V9999 COMP-3.
       01 HV-MONTANT        PIC S9(14)V99   COMP-3.
       01 HV-QTE-AVANT      PIC S9(10)V999  COMP-3.
       01 HV-QTE-APRES      PIC S9(10)V999  COMP-3.
       01 HV-NUMBON         PIC X(12).
       01 HV-NUMLOT         PIC X(30).
       01 HV-NOMFRN         PIC X(60).
       01 HV-REFFRN         PIC X(30).
       01 HV-NUMCMD         PIC X(20).
       01 HV-COMMNT         PIC X(80).
       01 HV-OPERATEUR      PIC X(10).
       01 HV-TERMINAL       PIC X(10).

           EXEC SQL INCLUDE SQLCA END-EXEC.

       COPY GSTK002M.
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
           MOVE SPACES TO GSTK-COMMAREA.
           MOVE 'G002    ' TO CA-TRAN-RETOUR.
           MOVE EIBTRMID   TO CA-TERMINAL.
           MOVE SPACES     TO CA-MSG-RETOUR.
      *    Si arrive depuis GSTK001 avec article selectionne
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
               INTO(GSTK002I)
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
                   PERFORM 4000-VALIDER-ENTREE
               WHEN DFHPF12
                   MOVE 'SAISIE ANNULEE' TO CA-MSG-RETOUR
                   PERFORM 6000-RETOUR-MENU
               WHEN DFHENTER
                   PERFORM 2100-LIRE-CODE-ECRAN
                   PERFORM 2200-LIRE-SAISIE
                   IF W-ART-CODE NOT = SPACES AND ARTICLE-TROUVE
                       COMPUTE W-MONTANT-HT = W-QTE-ENT * W-PRIX-ACH
                       MOVE 'MONTANT CALCULE - PF6 POUR VALIDER'
                           TO CA-MSG-RETOUR
                   ELSE
                       MOVE 'RECHERCHEZ ARTICLE (PF5) AVANT DE SAISIR'
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
           IF ARTCODL IN GSTK002I > ZERO
           AND ARTCODI IN GSTK002I NOT = SPACES
               MOVE ARTCODI IN GSTK002I TO W-ART-CODE
           END-IF.

      ******************************************************************
       2200-LIRE-SAISIE.
      ******************************************************************
      *    Lire les champs de saisie depuis la carte recue
           IF QTEENTL IN GSTK002I > ZERO
           AND QTEENTI IN GSTK002I NOT = SPACES
               MOVE QTEENTI IN GSTK002I TO W-QTE-ENT
           ELSE
               MOVE ZERO TO W-QTE-ENT
           END-IF.
           IF PRIACHL IN GSTK002I > ZERO
           AND PRIACHI IN GSTK002I NOT = SPACES
               MOVE PRIACHI IN GSTK002I TO W-PRIX-ACH
           ELSE
               MOVE ZERO TO W-PRIX-ACH
           END-IF.
           IF NUMBON  IN GSTK002I NOT = SPACES
               MOVE NUMBONI IN GSTK002I TO HV-NUMBON
           ELSE
               MOVE SPACES TO HV-NUMBON
           END-IF.
           IF NUMLOTI IN GSTK002I NOT = SPACES
               MOVE NUMLOTI IN GSTK002I TO HV-NUMLOT
           ELSE
               MOVE SPACES TO HV-NUMLOT
           END-IF.
           IF NOMFRNI IN GSTK002I NOT = SPACES
               MOVE NOMFRNI IN GSTK002I TO HV-NOMFRN
           ELSE
               MOVE W-ART-FRNNOM TO HV-NOMFRN
           END-IF.
           IF REFFRNI IN GSTK002I NOT = SPACES
               MOVE REFFRNI IN GSTK002I TO HV-REFFRN
           ELSE
               MOVE SPACES TO HV-REFFRN
           END-IF.
           IF NUMCMDI IN GSTK002I NOT = SPACES
               MOVE NUMCMDI IN GSTK002I TO HV-NUMCMD
           ELSE
               MOVE SPACES TO HV-NUMCMD
           END-IF.
           IF COMMNTI IN GSTK002I NOT = SPACES
               MOVE COMMNTI IN GSTK002I TO HV-COMMNT
           ELSE
               MOVE SPACES TO HV-COMMNT
           END-IF.

      ******************************************************************
       3000-RECHERCHER-ARTICLE.
      ******************************************************************
           MOVE 'N'       TO W-ART-TROUVE.
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
                      ART_PRIX_ACHAT,
                      ART_FRN_CODE,
                      ART_FRN_NOM
               INTO   :HV-ARTCOD,   :HV-DESIG,  :HV-CATEG,
                      :HV-EMPL,     :HV-STAT,   :HV-QTE-STK,
                      :HV-MIN,      :HV-MAX,    :HV-PUA,
                      :HV-FRN-CODE, :HV-FRN-NOM
               FROM   GSTK.ARTICLES
               WHERE  ART_CODE = :HV-ARTCOD
           END-EXEC.
           EVALUATE SQLCODE
               WHEN 0
                   MOVE 'O'          TO W-ART-TROUVE
                   MOVE HV-DESIG     TO W-ART-DESIG
                   MOVE HV-CATEG     TO W-ART-CATEG
                   MOVE HV-EMPL      TO W-ART-EMPL
                   MOVE HV-STAT      TO W-ART-STAT
                   MOVE HV-QTE-STK   TO W-ART-QTE
                   MOVE HV-MIN       TO W-ART-MIN
                   MOVE HV-MAX       TO W-ART-MAX
                   MOVE HV-PUA       TO W-ART-PUA
                   MOVE HV-FRN-CODE  TO W-ART-FRN
                   MOVE HV-FRN-NOM   TO W-ART-FRNNOM
                   MOVE W-ART-PUA    TO W-PRIX-ACH
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
       4000-VALIDER-ENTREE.
      ******************************************************************
      *    Controles de validite avant sauvegarde
           MOVE 'N' TO W-SAISIE-OK.
           IF W-ART-CODE = SPACES OR ARTICLE-ABSENT
               MOVE 'RECHERCHEZ ET SELECTIONNEZ UN ARTICLE (PF5)'
                   TO CA-MSG-RETOUR
               PERFORM 5000-AFFICHER-ECRAN
               GO TO 4000-FIN
           END-IF.
           IF W-ART-STAT = 'ARCHIVE'
               MOVE 'ARTICLE ARCHIVE - ENTREE IMPOSSIBLE'
                   TO CA-MSG-RETOUR
               PERFORM 5000-AFFICHER-ECRAN
               GO TO 4000-FIN
           END-IF.
           IF W-QTE-ENT <= ZERO
               MOVE 'QUANTITE ENTREE DOIT ETRE SUPERIEURE A ZERO'
                   TO CA-MSG-RETOUR
               PERFORM 5000-AFFICHER-ECRAN
               GO TO 4000-FIN
           END-IF.
           IF W-PRIX-ACH < ZERO
               MOVE 'PRIX ACHAT NE PEUT PAS ETRE NEGATIF'
                   TO CA-MSG-RETOUR
               PERFORM 5000-AFFICHER-ECRAN
               GO TO 4000-FIN
           END-IF.
           MOVE 'O' TO W-SAISIE-OK.
      *    Preparer les donnees du mouvement
           COMPUTE W-MONTANT-HT = W-QTE-ENT * W-PRIX-ACH.
           COMPUTE W-QTE-APRES  = W-ART-QTE + W-QTE-ENT.
           MOVE W-ART-QTE   TO HV-QTE-AVANT.
           MOVE W-QTE-APRES TO HV-QTE-APRES.
           MOVE W-ART-CODE  TO HV-ARTCOD.
           MOVE W-QTE-ENT   TO HV-QTE-ENT.
           MOVE W-PRIX-ACH  TO HV-PRIX-ACH.
           MOVE W-MONTANT-HT TO HV-MONTANT.
           MOVE CA-OPERATEUR TO HV-OPERATEUR.
           MOVE CA-TERMINAL  TO HV-TERMINAL.
           MOVE W-DATE-JOUR  TO HV-DATE-MVT.
           MOVE W-HEURE-JOUR TO HV-HEURE-MVT.
      *    Obtenir le prochain identifiant de mouvement
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
               MOVE 'ENTREE ENREGISTREE - MVT N. '
                   TO CA-MSG-RETOUR
               MOVE W-MVT-ID TO CA-MSG-RETOUR(30:12)
               MOVE 'VALIDE' TO ETATMVTO IN GSTK002O
      *        Reinitialiser pour nouvelle saisie
               MOVE SPACES TO W-ART-CODE W-ART-DESIG
               MOVE 'N'    TO W-ART-TROUVE
               MOVE ZERO   TO W-QTE-ENT W-PRIX-ACH W-MONTANT-HT
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
                   :HV-MVT-ID,    :HV-DATE-MVT,   :HV-HEURE-MVT,
                   'BON ENTREE',  'E',            :HV-ARTCOD,
                   :HV-DESIG,    :HV-QTE-ENT,    :HV-PRIX-ACH,
                   :HV-MONTANT,  :HV-QTE-AVANT,  :HV-QTE-APRES,
                   :HV-NUMBON,   :HV-NUMLOT,     :HV-NOMFRN,
                   :HV-EMPL,     SPACES,         :HV-OPERATEUR,
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
               SET    ART_QTE_STOCK = ART_QTE_STOCK + :HV-QTE-ENT,
                      ART_PRIX_ACHAT = :HV-PRIX-ACH,
                      ART_DATE_MAJ   = :HV-DATE-MVT,
                      ART_OPERATEUR  = :HV-OPERATEUR
               WHERE  ART_CODE = :HV-ARTCOD
           END-EXEC.
           IF SQLCODE NOT = 0
               PERFORM 9100-ERREUR-SQL
           END-IF.

      ******************************************************************
       5000-AFFICHER-ECRAN.
      ******************************************************************
           MOVE LOW-VALUE TO GSTK002O.
      *    Date/heure
           EXEC CICS ASKTIME ABSTIME(W-ABSTIME) END-EXEC.
           EXEC CICS FORMATTIME ABSTIME(W-ABSTIME)
               DDMMYYYY(W-DATE-JOUR)
               TIMESEP(':')
               TIME(W-HEURE-JOUR)
           END-EXEC.
           MOVE W-DATE-JOUR  TO W-DT-DATE.
           MOVE W-HEURE-JOUR TO W-DT-HEURE.
           MOVE W-DATETIME-EDI TO DATHRO  IN GSTK002O.
      *    Session
           MOVE CA-OPERATEUR  TO OPENAMO  IN GSTK002O.
           MOVE CA-TERMINAL   TO TERNAMO  IN GSTK002O.
           MOVE CA-SESSION-ID TO SESNAMO  IN GSTK002O.
      *    Code article saisi
           IF W-ART-CODE NOT = SPACES
               MOVE W-ART-CODE TO ARTCODO IN GSTK002O
           END-IF.
      *    Infos article si trouve
           IF ARTICLE-TROUVE
               MOVE W-ART-DESIG(1:30) TO ARTDESO IN GSTK002O
               MOVE W-ART-STAT        TO ARTSTAO IN GSTK002O
               MOVE W-ART-CATEG       TO ARTCATO IN GSTK002O
               MOVE W-ART-EMPL        TO ARTEMPO IN GSTK002O
               MOVE W-ART-FRN         TO ARTFRNO IN GSTK002O
               MOVE W-ART-QTE         TO W-ED-STK
               MOVE W-ED-STK          TO ARTSTKO IN GSTK002O
               MOVE W-ART-MIN         TO W-ED-MIN
               MOVE W-ED-MIN          TO ARTMINO IN GSTK002O
               MOVE W-ART-MAX         TO W-ED-MAX
               MOVE W-ED-MAX          TO ARTMAXO IN GSTK002O
               MOVE W-ART-PUA         TO W-ED-PUA
               MOVE W-ED-PUA          TO ARTPUAO IN GSTK002O
           END-IF.
      *    Saisie en cours
           IF W-QTE-ENT > ZERO
               MOVE W-QTE-ENT  TO W-ED-QTE
               MOVE W-ED-QTE   TO QTEENTO IN GSTK002O
           END-IF.
           IF W-PRIX-ACH > ZERO
               MOVE W-PRIX-ACH TO W-ED-PRIX
               MOVE W-ED-PRIX  TO PRIACHO  IN GSTK002O
           END-IF.
      *    Montant HT calcule
           IF W-MONTANT-HT > ZERO
               MOVE W-MONTANT-HT TO W-ED-MONTANT
               MOVE W-ED-MONTANT TO MNTHTEO  IN GSTK002O
           END-IF.
      *    Message
           IF CA-MSG-RETOUR NOT = SPACES
               MOVE CA-MSG-RETOUR TO MSGRTRO IN GSTK002O
           END-IF.
      *    Positionner le curseur sur le code article
           MOVE -1 TO ARTCODL IN GSTK002I.
           EXEC CICS SEND MAP(W-MAP) MAPSET(W-MAPSET)
               FROM(GSTK002O)
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
           EXEC CICS ABEND ABCODE('G002') NODUMP END-EXEC.

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
