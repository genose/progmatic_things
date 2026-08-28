      *================================================================*
      * PROGRAMME  : GSTK000                                         *
      * AUTEUR     : [NOM ETUDIANT]                                   *
      * DATE       : 2025-06-15                                       *
      * VERSION    : 1.0                                              *
      *----------------------------------------------------------------*
      * DESCRIPTION : MENU PRINCIPAL - SYSTEME DE GESTION DE STOCK   *
      *   Transaction CICS pseudo-conversationnelle.                  *
      *   Affiche le menu principal et le tableau de bord KPI.        *
      *   Route vers les sous-transactions GSTK001 a GSTK007.        *
      *----------------------------------------------------------------*
      * TRANSID    : G000                                             *
      * MAPSET     : GSTK000M      MAP : GSTK000                     *
      * COMMAREA   : GSTK-COMMAREA (263 octets, voir GSTKCPY)        *
      *----------------------------------------------------------------*
      * TOUCHES :                                                      *
      *   ENTREE   -> Valider le choix et router                      *
      *   PF1      -> Rafraichir l'ecran                              *
      *   PF2/CLEAR-> Fin de session                                  *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. GSTK000.
       AUTHOR. ETUDIANT-COBOL.
       DATE-WRITTEN. 2025-06-15.

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
       01 W-PROG-ID         PIC X(8)  VALUE 'GSTK000 '.
       01 W-TRANS-ID        PIC X(4)  VALUE 'G000'.
       01 W-MAPSET          PIC X(8)  VALUE 'GSTK000M'.
       01 W-MAP             PIC X(7)  VALUE 'GSTK000'.
      *    Taille reelle de la COMMAREA (calculee depuis GSTKCPY)
       01 W-CA-LEN          PIC S9(4) COMP VALUE 263.

      *    Noms des programmes cibles (8 chars, paddes avec espaces)
       01 W-PROG-CIBLE      PIC X(8)  VALUE SPACES.

      *----------------------------------------------------------------*
      * ZONES DE TRAVAIL CICS                                          *
      *----------------------------------------------------------------*
       01 W-RESP            PIC S9(8) COMP VALUE 0.
       01 W-RESP2           PIC S9(8) COMP VALUE 0.
       01 W-ABEND-CD        PIC X(4)  VALUE 'G000'.

      *----------------------------------------------------------------*
      * DATE ET HEURE                                                   *
      *----------------------------------------------------------------*
       01 W-ABSTIME         PIC S9(15) COMP-3 VALUE 0.
       01 W-DATE-JOUR       PIC X(10) VALUE SPACES.
       01 W-HEURE-JOUR      PIC X(8)  VALUE SPACES.

      *    Zone affichee sur l'ecran : JJ/MM/AAAA HH:MM:SS
       01 W-DATETIME-EDI.
          05 W-DT-DATE      PIC X(10).
          05 FILLER         PIC X     VALUE SPACE.
          05 W-DT-HEURE     PIC X(8).

      *----------------------------------------------------------------*
      * INDICATEURS LOGIQUES                                           *
      *----------------------------------------------------------------*
       01 W-FLAG-ERREUR     PIC X     VALUE 'N'.
          88 ERREUR-SAISIE             VALUE 'O'.
          88 PAS-ERREUR                VALUE 'N'.

      *----------------------------------------------------------------*
      * VALEUR DU CHOIX OPERATEUR                                      *
      *----------------------------------------------------------------*
       01 W-CHOIX           PIC 9     VALUE 0.

      *----------------------------------------------------------------*
      * DONNEES KPI - TABLEAU DE BORD (requetes DB2)                  *
      *----------------------------------------------------------------*
       01 W-NB-ARTICLES     PIC S9(5) COMP-3 VALUE 0.
       01 W-NB-ALERTES      PIC S9(4) COMP-3 VALUE 0.
       01 W-NB-MVTS         PIC S9(5) COMP-3 VALUE 0.
       01 W-VALEUR-STOCK    PIC S9(12)V99 COMP-3 VALUE 0.

      *    Zones d'edition pour affichage (DECIMAL-POINT IS COMMA)
       01 W-ARTCNT-EDI      PIC ZZ.ZZ9.
       01 W-ALTCNT-EDI      PIC ZZZ9.
       01 W-MVTCNT-EDI      PIC ZZ.ZZ9.
      *    48.297,62 -> format francais via DECIMAL-POINT IS COMMA
       01 W-STKVLR-EDI      PIC ZZZ.ZZZ.ZZ9,99.

      *----------------------------------------------------------------*
      * ZONE MESSAGE ERREUR SQL                                        *
      *----------------------------------------------------------------*
       01 W-SQLCODE-EDI     PIC -9(5).
       01 W-MSG-SQL         PIC X(79) VALUE SPACES.

      *----------------------------------------------------------------*
      * MAP I/O - GENERE PAR L'ASSEMBLEUR BMS A PARTIR DE GSTK000M   *
      *----------------------------------------------------------------*
           COPY GSTK000M.

      *----------------------------------------------------------------*
      * TOUCHES DE FONCTION CICS                                       *
      *----------------------------------------------------------------*
           COPY DFHAID.

      *----------------------------------------------------------------*
      * CONSTANTES ATTRIBUTS BMS (DFHBMPRO, DFHBRT, DFHNORM...)      *
      *----------------------------------------------------------------*
           COPY DFHBMSCA.

      *----------------------------------------------------------------*
      * STRUCTURES COMMUNES COMMAREA + RECORDS (GSTKCPY.cbl)          *
      *----------------------------------------------------------------*
           COPY GSTKCOMM.

      *----------------------------------------------------------------*
      * SQLCA - ZONE DE COMMUNICATION SQL DB2                          *
      *----------------------------------------------------------------*
           EXEC SQL
               INCLUDE SQLCA
           END-EXEC.

      *================================================================*
       LINKAGE SECTION.

      *    COMMAREA recue de la transaction precedente
       01 DFHCOMMAREA       PIC X(263).

      *================================================================*
       PROCEDURE DIVISION.

      *----------------------------------------------------------------*
       0000-PRINCIPAL.
      *----------------------------------------------------------------*
      * Point d'entree unique du programme.                            *
      * EIBCALEN = 0 : premiere entree (nouvelle transaction).         *
      * EIBCALEN > 0 : retour apres interaction operateur.             *
      *----------------------------------------------------------------*
           EVALUATE TRUE
               WHEN EIBCALEN = ZERO
                   PERFORM 1000-PREMIERE-ENTREE

               WHEN OTHER
                   MOVE DFHCOMMAREA TO GSTK-COMMAREA
                   PERFORM 2000-RETOUR-TRANSACTION
           END-EVALUATE.

           EXEC CICS RETURN END-EXEC.

      *----------------------------------------------------------------*
       1000-PREMIERE-ENTREE.
      *----------------------------------------------------------------*
      * Initialise la COMMAREA, recupere le contexte CICS,            *
      * interroge DB2 pour les KPI et affiche l'ecran.                *
      *----------------------------------------------------------------*
           INITIALIZE GSTK-COMMAREA.

      *    Recuperer le terminal depuis le bloc EIB
           MOVE EIBTRMID          TO CA-TERMINAL.

      *    Horodatage de la session
           EXEC CICS ASKTIME
               ABSTIME(W-ABSTIME)
               RESP(W-RESP)
               RESP2(W-RESP2)
           END-EXEC.

           EXEC CICS FORMATTIME
               ABSTIME(W-ABSTIME)
               DDMMYYYY(W-DATE-JOUR)
               DATESEP('/')
               TIME(W-HEURE-JOUR)
               TIMESEP(':')
               RESP(W-RESP)
               RESP2(W-RESP2)
           END-EXEC.

           MOVE W-DATE-JOUR       TO CA-DATE-SAISIE.
           MOVE W-HEURE-JOUR      TO CA-HEURE-SAISIE.

      *    ID de session : SES-JJ/MM/AAAA-TERM
           STRING 'SES-' DELIMITED BY SIZE
                  W-DATE-JOUR(1:10) DELIMITED BY SIZE
                  '-' DELIMITED BY SIZE
                  EIBTRMID DELIMITED BY SIZE
               INTO CA-SESSION-ID.

      *    Operateur connecte (ID fourni par CICS sign-on)
           EXEC CICS ASSIGN
               USERID(CA-OPERATEUR)
               RESP(W-RESP)
               RESP2(W-RESP2)
           END-EXEC.
           IF W-RESP NOT = DFHRESP(NORMAL)
               MOVE 'INCONNU   ' TO CA-OPERATEUR
           END-IF.

           PERFORM 3000-REQUETES-KPI.
           PERFORM 4000-AFFICHER-ECRAN.

      *----------------------------------------------------------------*
       2000-RETOUR-TRANSACTION.
      *----------------------------------------------------------------*
      * Analyse la touche de fonction (AID key) et dirige le traitement*
      *----------------------------------------------------------------*
           EVALUATE EIBAID
               WHEN DFHPF2
               WHEN DFHCLEAR
                   PERFORM 5000-FIN-SESSION

               WHEN DFHPF1
      *            Rafraichissement simple (mise a jour KPI)
                   MOVE SPACES        TO CA-MSG-RETOUR
                   MOVE 'N'           TO W-FLAG-ERREUR
                   PERFORM 3000-REQUETES-KPI
                   PERFORM 4000-AFFICHER-ECRAN

               WHEN DFHENTER
                   PERFORM 2100-TRAITER-SAISIE

               WHEN OTHER
                   MOVE
                   'TOUCHE NON RECONNUE - UTILISER ENTREE OU PF2.'
                       TO CA-MSG-RETOUR
                   MOVE 'O'           TO W-FLAG-ERREUR
                   PERFORM 3000-REQUETES-KPI
                   PERFORM 4000-AFFICHER-ECRAN
           END-EVALUATE.

      *----------------------------------------------------------------*
       2100-TRAITER-SAISIE.
      *----------------------------------------------------------------*
      * Recoit la map, valide le choix et route vers le bon programme. *
      *----------------------------------------------------------------*
           EXEC CICS RECEIVE
               MAP(W-MAP)
               MAPSET(W-MAPSET)
               INTO(GSTK000I)
               RESP(W-RESP)
               RESP2(W-RESP2)
           END-EXEC.

           IF W-RESP NOT = DFHRESP(NORMAL)
               PERFORM 9000-ERREUR-CICS
           END-IF.

           MOVE 'N'   TO W-FLAG-ERREUR.
           MOVE SPACES TO CA-MSG-RETOUR.

      *    Verifier que quelque chose a ete saisi
           IF CHOIXL IN GSTK000I = ZERO
               MOVE 'AUCUN CHOIX SAISI - ENTRER UN CHIFFRE DE 0 A 7.'
                   TO CA-MSG-RETOUR
               MOVE 'O' TO W-FLAG-ERREUR
           ELSE
               MOVE CHOIXI IN GSTK000I TO W-CHOIX
               IF W-CHOIX > 7
                   MOVE
               'CHOIX INVALIDE - ENTRER UN CHIFFRE DE 0 A 7.'
                       TO CA-MSG-RETOUR
                   MOVE 'O' TO W-FLAG-ERREUR
               END-IF
           END-IF.

           IF ERREUR-SAISIE
               PERFORM 3000-REQUETES-KPI
               PERFORM 4000-AFFICHER-ECRAN
           ELSE
               PERFORM 2200-ROUTER
           END-IF.

      *----------------------------------------------------------------*
       2200-ROUTER.
      *----------------------------------------------------------------*
      * Transfert de controle (XCTL) vers le programme cible.         *
      * La COMMAREA est transmise integralement.                       *
      *----------------------------------------------------------------*
           EVALUATE W-CHOIX
               WHEN 0
                   PERFORM 5000-FIN-SESSION

               WHEN 1
                   MOVE 'GSTK001 ' TO W-PROG-CIBLE
               WHEN 2
                   MOVE 'GSTK002 ' TO W-PROG-CIBLE
               WHEN 3
                   MOVE 'GSTK003 ' TO W-PROG-CIBLE
               WHEN 4
                   MOVE 'GSTK004 ' TO W-PROG-CIBLE
               WHEN 5
                   MOVE 'GSTK005 ' TO W-PROG-CIBLE
               WHEN 6
                   MOVE 'GSTK006 ' TO W-PROG-CIBLE
               WHEN 7
                   MOVE 'GSTK007 ' TO W-PROG-CIBLE
           END-EVALUATE.

           IF W-CHOIX > 0
               MOVE W-TRANS-ID TO CA-TRAN-RETOUR
               EXEC CICS XCTL
                   PROGRAM(W-PROG-CIBLE)
                   COMMAREA(GSTK-COMMAREA)
                   LENGTH(W-CA-LEN)
                   RESP(W-RESP)
                   RESP2(W-RESP2)
               END-EXEC
               IF W-RESP NOT = DFHRESP(NORMAL)
                   PERFORM 9000-ERREUR-CICS
               END-IF
           END-IF.

      *----------------------------------------------------------------*
       3000-REQUETES-KPI.
      *----------------------------------------------------------------*
      * Interroge DB2 pour alimenter le tableau de bord.              *
      * En cas d'erreur SQL, on affiche 0 (pas d'arret programme).    *
      *----------------------------------------------------------------*

      *    KPI 1 : nombre d'articles actifs
           EXEC SQL
               SELECT COUNT(*)
                 INTO :W-NB-ARTICLES
                 FROM GSTK.ARTICLES
                WHERE ART_STATUT = 'ACTIF'
           END-EXEC.
           IF SQLCODE NOT = 0
               MOVE 0 TO W-NB-ARTICLES
           END-IF.

      *    KPI 2 : valeur totale du stock (prix vente * quantite)
           EXEC SQL
               SELECT COALESCE(
                          SUM(ART_QTE_STOCK * ART_PRIX_VENTE), 0)
                 INTO :W-VALEUR-STOCK
                 FROM GSTK.ARTICLES
                WHERE ART_STATUT = 'ACTIF'
           END-EXEC.
           IF SQLCODE NOT = 0
               MOVE 0 TO W-VALEUR-STOCK
           END-IF.

      *    KPI 3 : nombre d'alertes stock actives
           EXEC SQL
               SELECT COUNT(*)
                 INTO :W-NB-ALERTES
                 FROM GSTK.ALERTES_STOCK
                WHERE ALT_STATUT = 'ACTIVE'
           END-EXEC.
           IF SQLCODE NOT = 0
               MOVE 0 TO W-NB-ALERTES
           END-IF.

      *    KPI 4 : mouvements de stock du jour
           EXEC SQL
               SELECT COUNT(*)
                 INTO :W-NB-MVTS
                 FROM GSTK.MOUVEMENTS_STOCK
                WHERE DATE(MVT_TIMESTAMP) = CURRENT_DATE
           END-EXEC.
           IF SQLCODE NOT = 0
               MOVE 0 TO W-NB-MVTS
           END-IF.

      *----------------------------------------------------------------*
       4000-AFFICHER-ECRAN.
      *----------------------------------------------------------------*
      * Charge les champs de la map et envoie l'ecran.                *
      * Se termine par un RETURN pseudo-conversationnel.              *
      *----------------------------------------------------------------*
           INITIALIZE GSTK000O.

      *    Mettre a jour date/heure (peut avoir change depuis 1000)
           EXEC CICS ASKTIME
               ABSTIME(W-ABSTIME)
           END-EXEC.
           EXEC CICS FORMATTIME
               ABSTIME(W-ABSTIME)
               DDMMYYYY(W-DATE-JOUR)
               DATESEP('/')
               TIME(W-HEURE-JOUR)
               TIMESEP(':')
           END-EXEC.

           MOVE W-DATE-JOUR TO W-DT-DATE.
           MOVE W-HEURE-JOUR TO W-DT-HEURE.
           MOVE W-DATETIME-EDI    TO DATHRO  IN GSTK000O.

      *    Infos session
           MOVE CA-OPERATEUR      TO OPENAMO IN GSTK000O.
           MOVE CA-TERMINAL       TO TERNAMO IN GSTK000O.
           MOVE CA-SESSION-ID     TO SESNAMO IN GSTK000O.

      *    KPI editions numeriques (DECIMAL-POINT IS COMMA actif)
           MOVE W-NB-ARTICLES     TO W-ARTCNT-EDI.
           MOVE W-ARTCNT-EDI      TO ARTCNTO IN GSTK000O.

           MOVE W-VALEUR-STOCK    TO W-STKVLR-EDI.
           MOVE W-STKVLR-EDI      TO STKVLRO IN GSTK000O.

           MOVE W-NB-ALERTES      TO W-ALTCNT-EDI.
           MOVE W-ALTCNT-EDI      TO ALTCNTO IN GSTK000O.

           MOVE W-NB-MVTS         TO W-MVTCNT-EDI.
           MOVE W-MVTCNT-EDI      TO MVTCNTO IN GSTK000O.

      *    Message retour : rouge si erreur, normal sinon
           IF CA-MSG-RETOUR NOT = SPACES
               MOVE CA-MSG-RETOUR TO MSGRTRO IN GSTK000O
               IF ERREUR-SAISIE
                   MOVE DFHBRT    TO MSGRTRA IN GSTK000O
               END-IF
           END-IF.

      *    Positionner curseur sur le champ de saisie
           MOVE -1                TO CHOIXL  IN GSTK000I.

      *    Envoi de la map (ERASE efface l'ecran, CURSOR suit le -1)
           EXEC CICS SEND
               MAP(W-MAP)
               MAPSET(W-MAPSET)
               FROM(GSTK000O)
               ERASE
               CURSOR
               RESP(W-RESP)
               RESP2(W-RESP2)
           END-EXEC.

           IF W-RESP NOT = DFHRESP(NORMAL)
               PERFORM 9000-ERREUR-CICS
           END-IF.

      *    Retour pseudo-conversationnel : CICS suspend et attend
      *    la prochaine interaction (AID key) de l'operateur.
           EXEC CICS RETURN
               TRANSID(W-TRANS-ID)
               COMMAREA(GSTK-COMMAREA)
               LENGTH(W-CA-LEN)
           END-EXEC.

      *----------------------------------------------------------------*
       5000-FIN-SESSION.
      *----------------------------------------------------------------*
      * Termine la session proprement sans relancer de transaction.    *
      *----------------------------------------------------------------*
           EXEC CICS SEND TEXT
               FROM('AU REVOIR - FIN DE SESSION GSTK            ')
               LENGTH(44)
               ERASE
               FREEKB
           END-EXEC.

           EXEC CICS RETURN END-EXEC.

      *----------------------------------------------------------------*
       9000-ERREUR-CICS.
      *----------------------------------------------------------------*
      * Gestionnaire d'erreurs CICS.                                   *
      * Declenche un ABEND avec le code de retour CICS.               *
      *----------------------------------------------------------------*
           MOVE W-RESP TO W-ABEND-CD.

           EXEC CICS SEND TEXT
               FROM(W-ABEND-CD)
               LENGTH(4)
               ERASE
           END-EXEC.

           EXEC CICS ABEND
               ABCODE(W-ABEND-CD)
           END-EXEC.

       END PROGRAM GSTK000.
