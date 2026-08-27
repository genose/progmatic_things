      *================================================================*
      * PROGRAMME  : GSTK004                                         *
      * AUTEUR     : [NOM ETUDIANT]                                   *
      * DATE       : 2026-08-26                                       *
      * VERSION    : 1.0                                              *
      *----------------------------------------------------------------*
      * DESCRIPTION : CREATION / MODIFICATION ARTICLE                 *
      *   Si CA-ART-CODE-SELEC vide -> mode CREATION                  *
      *   Si CA-ART-CODE-SELEC rempli -> mode MODIFICATION            *
      *   PF5 : charger article par code saisi                        *
      *   PF6 : enregistrer (INSERT ou UPDATE)                        *
      *   PF9 : archiver l article (UPDATE STATUT=ARCHIVE)            *
      *================================================================*
       IDENTIFICATION DIVISION.
       PROGRAM-ID. GSTK004.
       AUTHOR. ETUDIANT-COBOL.
       DATE-WRITTEN. 2026-08-26.

       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       SPECIAL-NAMES.
           DECIMAL-POINT IS COMMA.

       DATA DIVISION.

      *================================================================*
       WORKING-STORAGE SECTION.

       01 W-PROG-ID         PIC X(8)  VALUE 'GSTK004 '.
       01 W-TRANS-ID        PIC X(4)  VALUE 'G004'.
       01 W-MAPSET          PIC X(8)  VALUE 'GSTK004M'.
       01 W-MAP             PIC X(7)  VALUE 'GSTK004'.
       01 W-CA-LEN          PIC S9(4) COMP VALUE 263.
       01 W-RESP            PIC S9(8) COMP VALUE 0.
       01 W-RESP2           PIC S9(8) COMP VALUE 0.

       01 W-MODE            PIC X     VALUE 'C'.
          88 MODE-CREATION            VALUE 'C'.
          88 MODE-MODIF               VALUE 'M'.

      *--- DATE / HEURE -----------------------------------------------
       01 W-ABSTIME         PIC S9(15) COMP-3 VALUE 0.
       01 W-DATE-JOUR       PIC X(10) VALUE SPACES.
       01 W-HEURE-JOUR      PIC X(8)  VALUE SPACES.
       01 W-DATETIME-EDI.
          05 W-DT-DATE      PIC X(10).
          05 FILLER         PIC X     VALUE SPACE.
          05 W-DT-HEURE     PIC X(8).

      *--- ARTICLE COURANT --------------------------------------------
       01 W-ART-CODE        PIC X(10) VALUE SPACES.
       01 W-ART-DESIG       PIC X(50) VALUE SPACES.
       01 W-ART-DESC        PIC X(80) VALUE SPACES.
       01 W-ART-CAT         PIC X(15) VALUE SPACES.
       01 W-ART-SCT         PIC X(15) VALUE SPACES.
       01 W-ART-UNI         PIC X(10) VALUE SPACES.
       01 W-ART-EMPL        PIC X(15) VALUE SPACES.
       01 W-ART-FRN         PIC X(10) VALUE SPACES.
       01 W-ART-CDB         PIC X(13) VALUE SPACES.
       01 W-ART-STAT        PIC X(10) VALUE 'ACTIF'.
       01 W-ART-QTE         PIC S9(10)V999  COMP-3 VALUE 0.
       01 W-ART-QMN         PIC S9(10)V999  COMP-3 VALUE 0.
       01 W-ART-QMX         PIC S9(10)V999  COMP-3 VALUE 0.
       01 W-ART-PAC         PIC S9(10)V9999 COMP-3 VALUE 0.
       01 W-ART-PVT         PIC S9(10)V9999 COMP-3 VALUE 0.
       01 W-ART-TVA         PIC S9(3)V99    COMP-3 VALUE 0.
       01 W-ART-DLV         PIC 9(3)               VALUE 0.
       01 W-ART-DTC         PIC X(10) VALUE SPACES.
       01 W-ART-DTM         PIC X(10) VALUE SPACES.
       01 W-ART-OPE         PIC X(10) VALUE SPACES.
       01 W-ART-VALEUR      PIC S9(14)V99   COMP-3 VALUE 0.

      *--- EDITIONS ---------------------------------------------------
       01 W-ED-QTE          PIC Z(9),999.
       01 W-ED-QMN          PIC Z(9),999.
       01 W-ED-QMX          PIC Z(9),999.
       01 W-ED-PAC          PIC ZZZ9,9999.
       01 W-ED-PVT          PIC ZZZ9,9999.
       01 W-ED-TVA          PIC ZZ9,99.
       01 W-ED-DLV          PIC ZZZ9.
       01 W-ED-VLR          PIC Z(11),99.

      *--- HOST VARIABLES SQL -----------------------------------------
       01 HV-CODE           PIC X(10).
       01 HV-DESIG          PIC X(50).
       01 HV-DESC           PIC X(80).
       01 HV-CAT            PIC X(15).
       01 HV-SCT            PIC X(15).
       01 HV-UNI            PIC X(10).
       01 HV-EMPL           PIC X(15).
       01 HV-FRN            PIC X(10).
       01 HV-CDB            PIC X(13).
       01 HV-STAT           PIC X(10).
       01 HV-QTE            PIC S9(10)V999  COMP-3.
       01 HV-QMN            PIC S9(10)V999  COMP-3.
       01 HV-QMX            PIC S9(10)V999  COMP-3.
       01 HV-PAC            PIC S9(10)V9999 COMP-3.
       01 HV-PVT            PIC S9(10)V9999 COMP-3.
       01 HV-TVA            PIC S9(3)V99    COMP-3.
       01 HV-DLV            PIC 9(3).
       01 HV-DATE           PIC X(10).
       01 HV-OPERATEUR      PIC X(10).

           EXEC SQL INCLUDE SQLCA END-EXEC.

       COPY GSTK004M.
       COPY DFHAID.
       COPY DFHBMSCA.
       COPY GSTKCPY.

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
           MOVE 'G004    ' TO CA-TRAN-RETOUR.
           MOVE EIBTRMID   TO CA-TERMINAL.
           MOVE SPACES     TO CA-MSG-RETOUR.
           IF CA-ART-CODE-SELEC NOT = SPACES
               MOVE 'M' TO W-MODE
               MOVE CA-ART-CODE-SELEC TO W-ART-CODE
               PERFORM 3000-CHARGER-ARTICLE
           ELSE
               MOVE 'C' TO W-MODE
               MOVE 'ACTIF' TO W-ART-STAT
           END-IF.
           PERFORM 5000-AFFICHER-ECRAN.

      ******************************************************************
       2000-RETOUR-TRANSACTION.
      ******************************************************************
      *    Restaurer mode depuis COMMAREA FILLER (octet 209)
           MOVE GSTK-COMMAREA(209:1) TO W-MODE.
           MOVE GSTK-COMMAREA(210:10) TO W-ART-CODE.
           MOVE SPACES TO CA-MSG-RETOUR.
           EXEC CICS RECEIVE MAP(W-MAP) MAPSET(W-MAPSET)
               INTO(GSTK004I)
               RESP(W-RESP) RESP2(W-RESP2)
           END-EXEC.
           IF W-RESP = DFHRESP(MAPFAIL)
               PERFORM 5000-AFFICHER-ECRAN
               GO TO 2000-FIN
           END-IF.
           EVALUATE EIBAID
               WHEN DFHPF3
                   PERFORM 6000-RETOUR-MENU
               WHEN DFHPF5
                   PERFORM 2100-LIRE-SAISIE-CODE
                   IF W-ART-CODE = SPACES
                       MOVE 'SAISISSEZ UN CODE ARTICLE'
                           TO CA-MSG-RETOUR
                   ELSE
                       MOVE 'M' TO W-MODE
                       PERFORM 3000-CHARGER-ARTICLE
                   END-IF
                   PERFORM 5000-AFFICHER-ECRAN
               WHEN DFHPF6
                   PERFORM 2200-LIRE-TOUS-CHAMPS
                   PERFORM 4000-ENREGISTRER-ARTICLE
               WHEN DFHPF9
                   PERFORM 2100-LIRE-SAISIE-CODE
                   IF W-ART-CODE NOT = SPACES
                       PERFORM 4300-ARCHIVER-ARTICLE
                   ELSE
                       MOVE 'CHARGEZ UN ARTICLE AVANT D ARCHIVER'
                           TO CA-MSG-RETOUR
                       PERFORM 5000-AFFICHER-ECRAN
                   END-IF
               WHEN DFHPF12
                   MOVE 'OPERATION ANNULEE' TO CA-MSG-RETOUR
                   PERFORM 6000-RETOUR-MENU
               WHEN DFHENTER
                   PERFORM 5000-AFFICHER-ECRAN
               WHEN OTHER
                   MOVE 'PF3/PF5/PF6/PF9/PF12'
                       TO CA-MSG-RETOUR
                   PERFORM 5000-AFFICHER-ECRAN
           END-EVALUATE.
       2000-FIN.
           EXIT.

      ******************************************************************
       2100-LIRE-SAISIE-CODE.
      ******************************************************************
           IF ARTCODL IN GSTK004I > ZERO
           AND ARTCODI IN GSTK004I NOT = SPACES
               MOVE ARTCODI IN GSTK004I TO W-ART-CODE
           END-IF.

      ******************************************************************
       2200-LIRE-TOUS-CHAMPS.
      ******************************************************************
           PERFORM 2100-LIRE-SAISIE-CODE.
           IF ARTDESL IN GSTK004I > ZERO
               MOVE ARTDESI IN GSTK004I TO W-ART-DESIG
           END-IF.
           IF ARTDSCL IN GSTK004I > ZERO
               MOVE ARTDSCI IN GSTK004I TO W-ART-DESC
           END-IF.
           IF ARTCATL IN GSTK004I > ZERO
               MOVE ARTCATI IN GSTK004I TO W-ART-CAT
           END-IF.
           IF ARTSCTL IN GSTK004I > ZERO
               MOVE ARTSCTI IN GSTK004I TO W-ART-SCT
           END-IF.
           IF ARTUNIL IN GSTK004I > ZERO
               MOVE ARTUNII IN GSTK004I TO W-ART-UNI
           END-IF.
           IF ARTEMPL IN GSTK004I > ZERO
               MOVE ARTEMPI IN GSTK004I TO W-ART-EMPL
           END-IF.
           IF ARTFRNL IN GSTK004I > ZERO
               MOVE ARTFRNI IN GSTK004I TO W-ART-FRN
           END-IF.
           IF ARTCDBL IN GSTK004I > ZERO
               MOVE ARTCDBI IN GSTK004I TO W-ART-CDB
           END-IF.
           IF ARTSTAL IN GSTK004I > ZERO
           AND ARTSTAI IN GSTK004I NOT = SPACES
               MOVE ARTSTAI IN GSTK004I TO W-ART-STAT
           END-IF.
           IF ARTQMNL IN GSTK004I > ZERO
           AND ARTQMNI IN GSTK004I NOT = SPACES
               MOVE ARTQMNI IN GSTK004I TO W-ART-QMN
           END-IF.
           IF ARTQMXL IN GSTK004I > ZERO
           AND ARTQMXI IN GSTK004I NOT = SPACES
               MOVE ARTQMXI IN GSTK004I TO W-ART-QMX
           END-IF.
           IF ARTPACL IN GSTK004I > ZERO
           AND ARTPACI IN GSTK004I NOT = SPACES
               MOVE ARTPACI IN GSTK004I TO W-ART-PAC
           END-IF.
           IF ARTPVTL IN GSTK004I > ZERO
           AND ARTPVTI IN GSTK004I NOT = SPACES
               MOVE ARTPVTI IN GSTK004I TO W-ART-PVT
           END-IF.
           IF ARTTVAL IN GSTK004I > ZERO
           AND ARTTVAI IN GSTK004I NOT = SPACES
               MOVE ARTTVAI IN GSTK004I TO W-ART-TVA
           END-IF.
           IF ARTDLVL IN GSTK004I > ZERO
           AND ARTDLVI IN GSTK004I NOT = SPACES
               MOVE ARTDLVI IN GSTK004I TO W-ART-DLV
           END-IF.

      ******************************************************************
       3000-CHARGER-ARTICLE.
      ******************************************************************
           MOVE W-ART-CODE TO HV-CODE.
           EXEC SQL
               SELECT ART_CODE,       ART_DESIGNATION, ART_DESCRIPTION,
                      ART_CATEGORIE,  ART_SOUS_CAT,    ART_UNITE,
                      ART_EMPLACEMENT, ART_FRN_CODE,   ART_CODE_BARRE,
                      ART_STATUT,     ART_QTE_STOCK,   ART_QTE_MIN,
                      ART_QTE_MAX,    ART_PRIX_ACHAT,  ART_PRIX_VENTE,
                      ART_TVA_TAUX,   ART_DELAI_APPRO,
                      ART_DATE_CREATION, ART_DATE_MAJ, ART_OPERATEUR
               INTO   :HV-CODE,  :HV-DESIG, :HV-DESC,
                      :HV-CAT,   :HV-SCT,   :HV-UNI,
                      :HV-EMPL,  :HV-FRN,   :HV-CDB,
                      :HV-STAT,  :HV-QTE,   :HV-QMN,
                      :HV-QMX,   :HV-PAC,   :HV-PVT,
                      :HV-TVA,   :HV-DLV,
                      :W-ART-DTC, :W-ART-DTM, :W-ART-OPE
               FROM   GSTK.ARTICLES
               WHERE  ART_CODE = :HV-CODE
           END-EXEC.
           EVALUATE SQLCODE
               WHEN 0
                   MOVE HV-DESIG   TO W-ART-DESIG
                   MOVE HV-DESC    TO W-ART-DESC
                   MOVE HV-CAT     TO W-ART-CAT
                   MOVE HV-SCT     TO W-ART-SCT
                   MOVE HV-UNI     TO W-ART-UNI
                   MOVE HV-EMPL    TO W-ART-EMPL
                   MOVE HV-FRN     TO W-ART-FRN
                   MOVE HV-CDB     TO W-ART-CDB
                   MOVE HV-STAT    TO W-ART-STAT
                   MOVE HV-QTE     TO W-ART-QTE
                   MOVE HV-QMN     TO W-ART-QMN
                   MOVE HV-QMX     TO W-ART-QMX
                   MOVE HV-PAC     TO W-ART-PAC
                   MOVE HV-PVT     TO W-ART-PVT
                   MOVE HV-TVA     TO W-ART-TVA
                   MOVE HV-DLV     TO W-ART-DLV
                   MOVE 'M'        TO W-MODE
                   MOVE 'ARTICLE CHARGE - MODIFIEZ ET APPUYEZ PF6'
                       TO CA-MSG-RETOUR
               WHEN +100
                   IF MODE-CREATION
                       MOVE 'NOUVEAU CODE DISPONIBLE - SAISISSEZ LES'
                           TO CA-MSG-RETOUR
                   ELSE
                       MOVE 'ARTICLE INTROUVABLE'
                           TO CA-MSG-RETOUR
                       MOVE 'C' TO W-MODE
                   END-IF
               WHEN OTHER
                   PERFORM 9100-ERREUR-SQL
           END-EVALUATE.

      ******************************************************************
       4000-ENREGISTRER-ARTICLE.
      ******************************************************************
      *    Controles de base
           IF W-ART-CODE = SPACES
               MOVE 'CODE ARTICLE OBLIGATOIRE' TO CA-MSG-RETOUR
               PERFORM 5000-AFFICHER-ECRAN
               GO TO 4000-FIN
           END-IF.
           IF W-ART-DESIG = SPACES
               MOVE 'DESIGNATION OBLIGATOIRE' TO CA-MSG-RETOUR
               PERFORM 5000-AFFICHER-ECRAN
               GO TO 4000-FIN
           END-IF.
           MOVE W-ART-CODE  TO HV-CODE.
           MOVE W-ART-DESIG TO HV-DESIG.
           MOVE W-ART-DESC  TO HV-DESC.
           MOVE W-ART-CAT   TO HV-CAT.
           MOVE W-ART-SCT   TO HV-SCT.
           MOVE W-ART-UNI   TO HV-UNI.
           MOVE W-ART-EMPL  TO HV-EMPL.
           MOVE W-ART-FRN   TO HV-FRN.
           MOVE W-ART-CDB   TO HV-CDB.
           MOVE W-ART-STAT  TO HV-STAT.
           MOVE W-ART-QMN   TO HV-QMN.
           MOVE W-ART-QMX   TO HV-QMX.
           MOVE W-ART-PAC   TO HV-PAC.
           MOVE W-ART-PVT   TO HV-PVT.
           MOVE W-ART-TVA   TO HV-TVA.
           MOVE W-ART-DLV   TO HV-DLV.
           MOVE CA-OPERATEUR TO HV-OPERATEUR.
           MOVE W-DATE-JOUR  TO HV-DATE.
           IF MODE-CREATION
               PERFORM 4100-INSERER-ARTICLE
           ELSE
               PERFORM 4200-MODIFIER-ARTICLE
           END-IF.
           IF SQLCODE = 0
               EXEC CICS SYNCPOINT END-EXEC
               IF MODE-CREATION
                   MOVE 'ARTICLE CREE AVEC SUCCES'
                       TO CA-MSG-RETOUR
               ELSE
                   MOVE 'ARTICLE MIS A JOUR AVEC SUCCES'
                       TO CA-MSG-RETOUR
               END-IF
               MOVE 'M' TO W-MODE
               PERFORM 3000-CHARGER-ARTICLE
           ELSE
               EXEC CICS SYNCPOINT ROLLBACK END-EXEC
               MOVE 'ERREUR SAUVEGARDE - VERIFIEZ LES DONNEES'
                   TO CA-MSG-RETOUR
           END-IF.
           PERFORM 5000-AFFICHER-ECRAN.
       4000-FIN.
           EXIT.

      ******************************************************************
       4100-INSERER-ARTICLE.
      ******************************************************************
           EXEC SQL
               INSERT INTO GSTK.ARTICLES (
                   ART_CODE,       ART_DESIGNATION, ART_DESCRIPTION,
                   ART_CATEGORIE,  ART_SOUS_CAT,    ART_UNITE,
                   ART_QTE_STOCK,  ART_QTE_MIN,     ART_QTE_MAX,
                   ART_PRIX_ACHAT, ART_PRIX_VENTE,  ART_TVA_TAUX,
                   ART_EMPLACEMENT, ART_FRN_CODE,   ART_CODE_BARRE,
                   ART_STATUT,     ART_DELAI_APPRO,
                   ART_DATE_CREATION, ART_DATE_MAJ, ART_OPERATEUR
               ) VALUES (
                   :HV-CODE,  :HV-DESIG, :HV-DESC,
                   :HV-CAT,   :HV-SCT,   :HV-UNI,
                   0,         :HV-QMN,   :HV-QMX,
                   :HV-PAC,   :HV-PVT,   :HV-TVA,
                   :HV-EMPL,  :HV-FRN,   :HV-CDB,
                   :HV-STAT,  :HV-DLV,
                   :HV-DATE,  :HV-DATE,  :HV-OPERATEUR
               )
           END-EXEC.
           IF SQLCODE NOT = 0
               PERFORM 9100-ERREUR-SQL
           END-IF.

      ******************************************************************
       4200-MODIFIER-ARTICLE.
      ******************************************************************
           EXEC SQL
               UPDATE GSTK.ARTICLES
               SET    ART_DESIGNATION = :HV-DESIG,
                      ART_DESCRIPTION = :HV-DESC,
                      ART_CATEGORIE   = :HV-CAT,
                      ART_SOUS_CAT    = :HV-SCT,
                      ART_UNITE       = :HV-UNI,
                      ART_QTE_MIN     = :HV-QMN,
                      ART_QTE_MAX     = :HV-QMX,
                      ART_PRIX_ACHAT  = :HV-PAC,
                      ART_PRIX_VENTE  = :HV-PVT,
                      ART_TVA_TAUX    = :HV-TVA,
                      ART_EMPLACEMENT = :HV-EMPL,
                      ART_FRN_CODE    = :HV-FRN,
                      ART_CODE_BARRE  = :HV-CDB,
                      ART_STATUT      = :HV-STAT,
                      ART_DELAI_APPRO = :HV-DLV,
                      ART_DATE_MAJ    = :HV-DATE,
                      ART_OPERATEUR   = :HV-OPERATEUR
               WHERE  ART_CODE = :HV-CODE
           END-EXEC.
           IF SQLCODE NOT = 0
               PERFORM 9100-ERREUR-SQL
           END-IF.

      ******************************************************************
       4300-ARCHIVER-ARTICLE.
      ******************************************************************
           MOVE W-ART-CODE  TO HV-CODE.
           MOVE 'ARCHIVE'   TO HV-STAT.
           MOVE CA-OPERATEUR TO HV-OPERATEUR.
           MOVE W-DATE-JOUR  TO HV-DATE.
           EXEC SQL
               UPDATE GSTK.ARTICLES
               SET    ART_STATUT    = :HV-STAT,
                      ART_DATE_MAJ  = :HV-DATE,
                      ART_OPERATEUR = :HV-OPERATEUR
               WHERE  ART_CODE = :HV-CODE
           END-EXEC.
           IF SQLCODE = 0
               EXEC CICS SYNCPOINT END-EXEC
               MOVE 'ARTICLE ARCHIVE'  TO CA-MSG-RETOUR
               MOVE 'ARCHIVE'          TO W-ART-STAT
           ELSE
               EXEC CICS SYNCPOINT ROLLBACK END-EXEC
               PERFORM 9100-ERREUR-SQL
           END-IF.
           PERFORM 5000-AFFICHER-ECRAN.

      ******************************************************************
       5000-AFFICHER-ECRAN.
      ******************************************************************
           MOVE LOW-VALUE TO GSTK004O.
           EXEC CICS ASKTIME ABSTIME(W-ABSTIME) END-EXEC.
           EXEC CICS FORMATTIME ABSTIME(W-ABSTIME)
               DDMMYYYY(W-DATE-JOUR)
               TIMESEP(':')
               TIME(W-HEURE-JOUR)
           END-EXEC.
           MOVE W-DATE-JOUR  TO W-DT-DATE.
           MOVE W-HEURE-JOUR TO W-DT-HEURE.
           MOVE W-DATETIME-EDI TO DATHRO  IN GSTK004O.
           MOVE CA-OPERATEUR   TO OPENAMO  IN GSTK004O.
           MOVE CA-TERMINAL    TO TERNAMO  IN GSTK004O.
           MOVE CA-SESSION-ID  TO SESNAMO  IN GSTK004O.
      *    Indicateur de mode
           IF MODE-CREATION
               MOVE '=== MODE CREATION ===' TO MODINDO IN GSTK004O
           ELSE
               MOVE '=== MODE MODIFICATION ===' TO MODINDO IN GSTK004O
           END-IF.
      *    Champs article
           MOVE W-ART-CODE       TO ARTCODO IN GSTK004O.
           MOVE W-ART-DESIG(1:50)  TO ARTDESO IN GSTK004O.
           MOVE W-ART-DESC         TO ARTDSCO IN GSTK004O.
           MOVE W-ART-CAT          TO ARTCATO IN GSTK004O.
           MOVE W-ART-SCT          TO ARTSCTO IN GSTK004O.
           MOVE W-ART-UNI          TO ARTUNIO IN GSTK004O.
           MOVE W-ART-EMPL         TO ARTEMPO IN GSTK004O.
           MOVE W-ART-FRN          TO ARTFRNO IN GSTK004O.
           MOVE W-ART-CDB          TO ARTCDBO IN GSTK004O.
           MOVE W-ART-STAT         TO ARTSTAO IN GSTK004O.
           MOVE W-ART-QTE          TO W-ED-QTE
           MOVE W-ED-QTE           TO ARTSTKO IN GSTK004O.
           MOVE W-ART-QMN          TO W-ED-QMN
           MOVE W-ED-QMN           TO ARTQMNO IN GSTK004O.
           MOVE W-ART-QMX          TO W-ED-QMX
           MOVE W-ED-QMX           TO ARTQMXO IN GSTK004O.
           MOVE W-ART-PAC          TO W-ED-PAC
           MOVE W-ED-PAC           TO ARTPACO IN GSTK004O.
           MOVE W-ART-PVT          TO W-ED-PVT
           MOVE W-ED-PVT           TO ARTPVTO IN GSTK004O.
           MOVE W-ART-TVA          TO W-ED-TVA
           MOVE W-ED-TVA           TO ARTTVAO IN GSTK004O.
           MOVE W-ART-DLV          TO W-ED-DLV
           MOVE W-ED-DLV           TO ARTDLVO IN GSTK004O.
      *    Valeur stock calculee
           COMPUTE W-ART-VALEUR = W-ART-QTE * W-ART-PVT.
           MOVE W-ART-VALEUR TO W-ED-VLR.
           MOVE W-ED-VLR     TO STKVLRO IN GSTK004O.
      *    Dates
           IF W-ART-DTC NOT = SPACES
               MOVE W-ART-DTC TO ARTDTCO IN GSTK004O
           END-IF.
           IF W-ART-DTM NOT = SPACES
               MOVE W-ART-DTM TO ARTDTMO IN GSTK004O
           END-IF.
           IF W-ART-OPE NOT = SPACES
               MOVE W-ART-OPE TO ARTOPEO IN GSTK004O
           END-IF.
           IF CA-MSG-RETOUR NOT = SPACES
               MOVE CA-MSG-RETOUR TO MSGRTRO IN GSTK004O
           END-IF.
      *    Sauvegarder mode + code dans COMMAREA FILLER
           MOVE W-MODE     TO GSTK-COMMAREA(209:1).
           MOVE W-ART-CODE TO GSTK-COMMAREA(210:10).
           MOVE -1 TO ARTCODL IN GSTK004I.
           EXEC CICS SEND MAP(W-MAP) MAPSET(W-MAPSET)
               FROM(GSTK004O)
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
           EXEC CICS ABEND ABCODE('G004') NODUMP END-EXEC.

      ******************************************************************
       9100-ERREUR-SQL.
      ******************************************************************
           IF SQLCODE = +100
               MOVE 'AUCUNE DONNEE TROUVEE' TO CA-MSG-RETOUR
           ELSE
               MOVE 'ERREUR SQL - CODE : '  TO CA-MSG-RETOUR
               MOVE SQLCODE                 TO CA-MSG-RETOUR(22:6)
           END-IF.
