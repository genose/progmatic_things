*----------------------------------------------------------------------*
*    Nom du programme  : T10_MAJ_DTLIVR_BDCRM.SCO                      *
*    Auteur            : JM BELVAL                                     *
*    Langage           : RDB/COBOL                                     *
*    Date de creation  : 22/02/2011                                    *
*    Date modification : 22/02/2011  recherche auto des fichiers RT    *
*    Version           : 1.000                                         *
*== V3.001-5813 AP:JMB LIB:NOUVEAU PROGRAMME                           *
*== V3.001-3913 AP:FC  LIB:PASSAGE V3.000 (NO COLIS SUR 8)             *
*    Fonctionnalites   : MAJ DTLIVR DANS BDEXTRANET                    *
*    Refer. externes   :                                               *
*    - tables          :                                               *
*    - sous-programmes :                                               *
*    - procedures      :                                               *
*----------------------------------------------------------------------*
IDENTIFICATION DIVISION.
*=======================
PROGRAM-ID. T10_MAJ_DTLIVR_BDCRM.

ENVIRONMENT DIVISION.       
*====================

CONFIGURATION SECTION.
*---------------------
SPECIAL-NAMES.
	DECIMAL-POINT IS COMMA.

INPUT-OUTPUT SECTION.
*--------------------
FILE-CONTROL.

* fichier contenant les libelles des messages d'erreurs
    SELECT FIC-MESSAGES ASSIGN TO "RMS_MSG" ORGANIZATION INDEXED ACCESS MODE DYNAMIC RECORD KEY MSG-CLE FILE STATUS IS CODE-STATUS. 

* fichier des anomalies détectées
    SELECT FIC-ANOMALIES ASSIGN TO "RMS_ANO" FILE STATUS IS CODE-STATUS. 

    SELECT FIC-TELE ASSIGN TO FOUND-FILESPEC FILE STATUS IS CODE-STATUS. 

DATA DIVISION.                                    
*=============
FILE SECTION.                                   
*------------
FD FIC-MESSAGES EXTERNAL LABEL RECORD OMITTED.
   COPY "CDD$TOP.DEPOT.FICHIERS.D00_MSGTXT" FROM DICTIONARY.

FD FIC-ANOMALIES EXTERNAL LABEL RECORD OMITTED.
   COPY "CDD$TOP.DEPOT.FICHIERS.D00_ANOMAL" FROM DICTIONARY.

FD FIC-TELE VALUE OF ID FOUND-FILESPEC LABEL RECORD OMITTED.
   COPY "DIRCOB:T10_DESC_FICMAJBDSTAT.LIB".


WORKING-STORAGE SECTION.       
*-----------------------                              

*-------------  zones de gestion des erreur par module D00_MSGGES -----*     
COPY "CDD$TOP.DEPOT.FICHIERS.D00_MSGPAR" FROM DICTIONARY.

*-------------------- Noms des fichiers de données des RT -------------*
01 FICHIER01            PIC X(400) VALUE "DIRDAT:MAJBDSTAT*.DAT;*".

*--- Champs indicateurs pour routines contrôle existence fichier (find-file)---*
01 FILESPEC             PIC X(255).
01 FOUND-FILESPEC       PIC X(255).
01 FILEARCHI            PIC X(255).
01 FOUND-FILEARCHI      PIC X(255).
01 SAVED-FILESPEC       PIC X(255).
01 STRING-LENGTH        PIC 999.
01 CONTEXT              PIC S9(9) COMP.
01 STATUS-VALUE         PIC S9(9) COMP.
01 STATUS-RESULT        PIC S9(9) COMP.
01 RMS$_NMF             PIC S9(9) COMP VALUE EXTERNAL RMS$_NMF.
01 RMS$_FNF             PIC S9(9) COMP VALUE EXTERNAL RMS$_FNF.
01 RMS$_NORMAL          PIC S9(9) COMP VALUE EXTERNAL RMS$_NORMAL.
01 SS$_NORMAL           PIC S9(9) COMP VALUE EXTERNAL SS$_NORMAL.

*----------------  flags conditions erreurs    ------------------------*     
01 FLAG-ERREUR          PIC X VALUE "N".            
   88 ERREUR                  VALUE "O".
01 FLAG-FINFIC          PIC X VALUE "N".            
   88 FINFIC                  VALUE "O".
01 FLAG-CDE9994         PIC X VALUE "N".            
   88 CDE9994                 VALUE "O".
01 FLAG-TROUVE          PIC X VALUE "N".            
   88 TROUVE                  VALUE "O".
01 FLAG-DEADLOCK        PIC X VALUE "N".            
   88 DEADLOCK                VALUE "O".
01 FLAG-EXIST-STR       PIC X VALUE "N".
   88 EXIST-STR               VALUE "O".
01 FLAG-EXIST-RTR       PIC X VALUE "N".
   88 EXIST-RTR               VALUE "O".
01 FLAG-EXIST-COLIS     PIC X VALUE "N".
   88 EXIST-COLIS             VALUE "O".
01 FLAG-EXIST-COLISLIG  PIC X VALUE "N".
   88 EXIST-COLISLIG          VALUE "O".
01 FLAG-RGP             PIC X VALUE "N".
   88 RGP                     VALUE "O".
01 FLAG-TRANSACTION-OK  PIC X VALUE "N".
   88 TRANSACTION-OK          VALUE "O".
01 FLAG-CREAT-OK        PIC X VALUE "N".
   88 CREAT-OK                VALUE "O".
01 FLAG-RET-EXIST       PIC X VALUE "N".
   88 RET-EXIST               VALUE "O".
01 FLAG-CLIENT          PIC X VALUE "N".
   88 CLIENT                  VALUE "O".
01 FLAG-PAR-DEPOT       PIC X VALUE "N".
   88 PAR-DEPOT               VALUE "O".
01 FLAG-LECTCCL         PIC X VALUE "N".
   88 LECTCCL                 VALUE "O".
01 FLAG-FIC-TROUVE      PIC X VALUE "N".
   88 FIC-TROUVE              VALUE "O".
01 FLAG-DATE-OK         PIC X VALUE "N".
   88 DATE-OK                 VALUE "O".
01 FLAG-CODCSEM-OK      PIC X VALUE "N".
   88 CODCSEM-OK              VALUE "O".
01 FLAG-EMPTY-FILE      PIC X VALUE "N".
   88 EMPTY-FILE              VALUE "O".
01 FLAG-WRONGID         PIC X VALUE "N".
   88 WRONGID                 VALUE "O".
01 FLAG-NOREAD          PIC X VALUE "N".
   88 NOREAD                  VALUE "O".

*--------------------   codes exceptions   -----------------------------*     
01 RDB$_DEADLOCK        PIC S9(9) COMP VALUE IS EXTERNAL RDB$_DEADLOCK.
01 RDB$_LOCK_CONFLICT   PIC S9(9) COMP VALUE IS EXTERNAL RDB$_LOCK_CONFLICT.
*---       longueur de la zone de reception de la date convertie    ---*
01 W-LENGTH             PIC S9(4) COMP VALUE 23.
*---   flag = 0 date retournee sous format 01-JAN-1988 00:00:00.00  ---*
01 W-FLAG               PIC S9(6) COMP VALUE ZERO. 
*--------------------   statut d'erreur   -----------------------------*     
77 CODE-STATUS EXTERNAL PIC XX.  
77 W-STATUT             PIC X(3).
77 RDB-DBKEY            PIC X(8).
77 NBR-TRIES            PIC 9(3).
77 MAX-TRIES            PIC 9(3) VALUE 999.

*---- INDICATEURS POUR CONTROLE SQL CHAMPS NULS
77 I1                   PIC S9(4).
77 I2                   PIC S9(4).
*--- Zones d'entete ---------------------------------------------------*
01 W-USERNAME           PIC X(12).
01 W-IDENTMES           PIC X(6).
01 W-DATEEXP            PIC 9(6).
01 W-HEUREEXP           PIC 9(4).
01 W-ORGATL             PIC X(10).
01 W-PERATL             PIC X(10).
01 W-ORGATLD            PIC X(10).
01 W-PERATLD            PIC X(10).
01 W-NOGRPBG            PIC 9(6).

01 W-ORIFIC             PIC X(2).
01 W-TYPFIC             PIC 9(2).
*--- Zones de travail -------------------------------------------------*
01 NUM-LIGNE            PIC 9(2).
01 MAX-LIGNES           PIC 9(2).
01 NUM-PAGE             PIC 9(3).
*--- Zones pour suivi intégration -------------------------------------*
01 NB-FILES             PIC 9(4).
01 NB-RTR               PIC 9(4).
01 NB-RTR-COL           PIC 9(4).
01 NB-PAL-RT            PIC 9(4).
01 NB-COLIS-RT          PIC 9(4).
01 NB-COLIS             PIC 9(4).
01 NB-COLISLIG          PIC 9(4).
01 NB-FILES-TOT         PIC 9(4).
01 NB-RTR-TOT           PIC 9(4).
01 NB-COLIS-TOT         PIC 9(4).
01 NB-COLISLIG-TOT      PIC 9(4).
01 TAXEVD-PAR           PIC S99V99 COMP.
01 TAXECR-PAR           PIC S99V99 COMP.
01 SAUVVD-PAR           PIC S99V99 COMP.
01 SAUVCR-PAR           PIC S99V99 COMP.
01 EDIT-CLI             PIC X.
01 W-OLDTYPE            PIC XX.
01 W-CSEMT              PIC XX.
01 W-CSREC              PIC XX.
01 W-GENCLI             PIC X.
01 W-VOITRI             PIC XXX.
01 W-FONCT              PIC X(40).
01 W-ARGUM              PIC X(30).
01 MEM-CODSTR           PIC X(4).
01 W-CODECLIM           PIC X(4).
01 W-INTCOLTRP          PIC X.
01 W-ETICOLTRP          PIC X.
01 W-DEPT               PIC 9(5).
01 W-NORT               PIC 9(8).
01 D-NORT               PIC 9(8).
01 W-NORT-EDI           PIC 9(8).
01 W-NORTREPASC         PIC X(15).
01 W-NORTENLEV          PIC 9(8).
01 W-NORTRELIVR         PIC 9(8).
01 W-NOCOLI             PIC 9(8).
01 W-NUMCDE             PIC 9(7).
01 W-NUMRAL             PIC 9(1).
01 W-NUMCDE-9994        PIC 9(7).
01 W-NUMRAL-9994        PIC 9(1).
01 W-CDELAB             PIC X(8).
*
01 W-NUMLIG             PIC 9(8).
01 W-ARTCIP             PIC X(7).
01 W-LIBART             PIC X(30).
01 W-QTELIV             PIC 9(6).
01 W-CODCSEM-RGP        PIC X(2).
01 W-CODDEP             PIC X(4).
01 W-CODLAB             PIC X(4).
01 W-CODLAB-DEFAUT      PIC X(4).
01 W-CODSTR             PIC X(4).
01 W-CODSTR-FIC         PIC X(4).
01 W-VILLE              PIC X(32).
01 W-NBCLICIP           PIC 9(6).
01 W-CODECLI            PIC X(4).
01 W-CODCLILAB          PIC X(10).
01 RT-ENREG             PIC X VALUE "N".
01 W-CLICIP             PIC 9(7).
01 W-CLICSP             PIC 9(6).
01 W-CODSTR-CCL         PIC X(4).
01 W-NUMREC             PIC X(6).
01 W-CABDEP             PIC X(28).
01 W-CABSTR             PIC X(32).
01 W-CABSTR-REC         PIC X(32).
01 W-CABLAB             PIC X(22).
01 PTR-STRING           PIC 9(3).
01 W-CODSITE            PIC X(2).
01 W-DPTSITE            PIC X(2).

01 W-BUFFIC-SAV         PIC X(512).
01 W-BUFFIC-SVG         PIC X(512).

77 LOGNAM               PIC X(6) VALUE "ARRPRG".
77 VALLOG               PIC X.

01 W-CSPAR1             PIC XX.
01 W-CSPAR2             PIC XX.

*----------------------------------------------------------------------*
* POUR PASSAGE EN A4
*----------------------------------------------------------------------*
77 ESC-FORMAT-A4        PIC X(14)  VALUE "&l1O&k4S".
77 ESC-FORMAT-RAZ       PIC X(14)  VALUE "&l0O&k0S".
77 ESCAPE-DEBUT         PIC X(14)  VALUE "[4;74!s [6w".       
77 ESCAPE-FIN           PIC X(14)  VALUE "[4;74!s [4w".  
77 SETUP-DEBUT          PIC X(14)  VALUE SPACES.
77 SETUP-FIN            PIC X(14)  VALUE SPACES.
*--------------------  zones dates        -----------------------------*     
77 W-DATE-RDB           PIC S9(11)V9(7) COMP.        
77 W-DATE-ZERO          PIC S9(11)V9(7) COMP.        
77 W-DATSYS             PIC S9(11)V9(7) COMP.
77 W-DATJOU             PIC S9(11)V9(7) COMP.
77 W-P-DATE             PIC S9(11)V9(7) COMP.
77 W-RT-DATE            PIC S9(11)V9(7) COMP.              
77 W-DTAFFECT           PIC S9(11)V9(7) COMP.
77 W-DATBIN             PIC S9(11)V9(7) COMP.
77 W-DATASC             PIC X(23).
77 W-EDI-DATE           PIC X(10).
*---    date systeme sous forme ascii   ---*
01 W-D-DATE.
   02 W-D-JMA           PIC X(12).
   02 W-D-HEURE         PIC X(11).
01 W-DATE-ASCII.
   02 W-JOUR            PIC X(11).
   02 FILLER            PIC X.
   02 W-HEURE           PIC X(11).
*--- Zones conversion dates
01 W-MONTH-ASC          PIC X(3).
01 W-MONTH-NUM          PIC X(2).
*--- Zones pour calcul date affectation
01  W-DATE-VMS          PIC S9(11)V9(7) COMP.         
77  ONE-DAY-ASCII       PIC X(16) VALUE "0001 00:00:00.00".
77  ONE-DAY-INTERVAL    PIC S9(11)V9(7) COMP. 
77  NO-JOUR             PIC 9(9) COMP.
77  LIBELLE-JOUR        PIC X(8).

77  LIMITE-ASCII        PIC X(16) VALUE "0060 00:00:00.00".
77  LIMITE-INTERVAL     PIC S9(11)V9(7) COMP. 
77  W-DATLIM            PIC S9(11)V9(7) COMP.


*----------------------------------------------------------------------*
*            declaration de la base utilisée                           *
*----------------------------------------------------------------------*
    EXEC SQL
    DECLARE E ALIAS FILENAME 'BD_CRM'
    END-EXEC.

*----------------------------------------------------------------------*
*                DECLARATION DU SQLCA                                  *
*----------------------------------------------------------------------*
01 SQLSTATE             PIC X(5).
   EXEC SQL
   INCLUDE SQLCA
   END-EXEC.

01 GETMSGVARS.
   02 MSG-ID            PIC 9(9) COMP.
   02 MSG-LEN           PIC 9(9) COMP.
   02 MSG-TXT           PIC X(132).
01 SQLGETERRVARS.
   02 GET-ERROR-BUFFER  PIC X(300).
   02 GET-MSG-LEN       PIC S9(9) COMP.

*----------------------------------------------------------------------*
PROCEDURE DIVISION.
*==================
DECLARATIVES.    

* traitement des erreurs d'entrees-sorties sur les fichiers RMS
FILE-PROBLEM-001 SECTION.
    USE AFTER STANDARD EXCEPTION PROCEDURE ON OUTPUT.
FILE-PROBLEM-001-ACTION. 
*   en cas d'erreur ecriture d'un message dans le fichier RMS   

FILE-PROBLEM-002 SECTION.
    USE AFTER STANDARD EXCEPTION PROCEDURE ON INPUT.
FILE-PROBLEM-002-ACTION. 
*   en cas d'erreur ecriture d'un message dans le fichier RMS  
 
END DECLARATIVES.

*-----------------
PROGRAMME SECTION.
*-----------------
TRAITEMENT-PRINCIPAL.                        
*====================
    EXEC SQL 
    SET TRANSACTION READ ONLY
    END-EXEC.
    PERFORM INITIALISATION.
    EXEC SQL
    COMMIT
    END-EXEC.
    PERFORM OPEN-FILES.
    MOVE "O" TO FLAG-FIC-TROUVE.
    MOVE FICHIER01 TO FILESPEC.
    PERFORM TRT-FILES UNTIL NOT FIC-TROUVE.
    DISPLAY "PLUS DE FICHIER RI A INTEGRER".
    PERFORM CLOSE-FILES.
    STOP RUN.

*-------------------------------------------------------------------------*
TRT-FILES.
*-------------------------------------------------------------------------*
    DISPLAY "RECHERCHE FICHIER " FILESPEC.
    CALL 'LIB$FIND_FILE' USING
         BY DESCRIPTOR FILESPEC
                       FOUND-FILESPEC
         BY REFERENCE  CONTEXT
                       OMITTED
                       OMITTED
         BY REFERENCE  STATUS_VALUE
                       OMITTED
         GIVING STATUS_RESULT.
    EVALUATE STATUS_RESULT
    WHEN RMS$_NORMAL MOVE "O" TO FLAG-FIC-TROUVE
    WHEN RMS$_NMF    MOVE "N" TO FLAG-FIC-TROUVE
    WHEN RMS$_FNF    MOVE "N" TO FLAG-FIC-TROUVE
    END-EVALUATE.
    IF FIC-TROUVE
       DISPLAY "FICHIER TROUVE : " FOUND-FILESPEC
       ADD 1 TO NB-FILES
       PERFORM TRT-FICHIER
*       PERFORM RENAME-FICHIER
    ELSE
       CALL 'LIB$FIND_FILE_END' USING BY REFERENCE CONTEXT GIVING STATUS-RESULT
    END-IF.

*-------------------------------------------------------------------------*
INITIALISATION.
*-------------------------------------------------------------------------*
*   Init. compteurs
    MOVE 0 TO NUM-PAGE.
    MOVE 0 TO NUM-LIGNE.
    MOVE 0 TO NB-FILES NB-RTR NB-RTR-COL NB-COLIS-RT NB-PAL-RT NB-COLIS.
    MOVE 0 TO NB-FILES-TOT NB-RTR-TOT NB-COLIS-TOT.
*   Format A4
    MOVE ESC-FORMAT-A4 TO SETUP-DEBUT
    MOVE ESC-FORMAT-RAZ TO SETUP-FIN
    MOVE 72 TO MAX-LIGNES
*   Obtention de la date
    MOVE  '17-NOV-1858 ' TO  W-D-JMA.
    MOVE  '00:00:00.00'  TO  W-D-HEURE.
    CALL "SYS$BINTIM" USING BY DESCRIPTOR W-D-DATE BY REFERENCE W-DATE-ZERO.
    CALL "SYS$GETTIM" USING BY REFERENCE  W-DATSYS.
    CALL "SYS$ASCTIM" USING BY REFERENCE  W-LENGTH BY DESCRIPTOR W-D-DATE
                            BY REFERENCE  W-DATSYS BY VALUE      W-FLAG.
    MOVE  '00:00:00.00'  TO  W-D-HEURE.
    CALL "SYS$BINTIM" USING BY DESCRIPTOR W-D-DATE BY REFERENCE W-DATJOU.

*-- Intervalle 1 jour pour calcul date affectation
    CALL "SYS$BINTIM" USING BY DESCRIPTOR ONE-DAY-ASCII BY REFERENCE ONE-DAY-INTERVAL.
*-- Intervalle 1 jour pour calcul date LIMITE COLIS
    CALL "SYS$BINTIM" USING BY DESCRIPTOR LIMITE-ASCII BY REFERENCE LIMITE-INTERVAL.
    CALL "LIB$SUB_TIMES" USING BY REFERENCE W-DATSYS BY REFERENCE LIMITE-INTERVAL BY REFERENCE W-DATLIM.

*-------------------------------------------------------------------------*
TRT-FICHIER.
*-------------------------------------------------------------------------*
    PERFORM OUVERTURE-FICHIERS.
    MOVE "N" TO FLAG-FINFIC.
    MOVE "N" TO FLAG-NOREAD.
    PERFORM TRAITEMENT-INFOS UNTIL FINFIC.
    PERFORM FERMETURE-FICHIERS.

*------------------------------------------------------------------------*
TRAITEMENT-INFOS.
*------------------------------------------------------------------------*    
    INITIALIZE ENR-MAJDTLIVR.
    READ FIC-TELE AT END MOVE "O" TO FLAG-FINFIC.
    IF NOT FINFIC
       MOVE "N" TO FLAG-DEADLOCK
       MOVE "N" TO FLAG-CREAT-OK
       MOVE MAJBD-CODDEP TO W-CODDEP
       MOVE MAJBD-CODLAB TO W-CODLAB
       MOVE MAJBD-NUMCDE TO W-NUMCDE
       MOVE MAJBD-NUMRAL TO W-NUMRAL
       IF W-CODDEP = "FO"
          MOVE "MO" TO W-CODDEP
       END-IF
*
       MOVE 'N' TO FLAG-CDE9994
       IF W-CODLAB='3628' 
          PERFORM TST-3628-9994
       END-IF
*
       MOVE MAJBD-DATLIV TO W-DATASC
       CALL "SYS$BINTIM" USING BY DESCRIPTOR W-DATASC BY REFERENCE W-DATBIN
       DISPLAY "MAJ DTLIVR CDE BDCRM      " W-CODDEP " " W-CODLAB " " W-NUMCDE "/" W-NUMRAL " " MAJBD-DATLIV

       PERFORM MAJ-DTLIVR-BDSTATSEXTRANET UNTIL CREAT-OK

       MOVE "N" TO FLAG-CREAT-OK
       IF CDE9994
          PERFORM MAJ-DTLIVR-BDSTATSEXTRANET-9994 UNTIL CREAT-OK
       END-IF
    END-IF.

*---------------------------------------------------------------------*
RENAME-FICHIER.
*---------------------------------------------------------------------*
    MOVE "DIRSAV:*.STASAV" TO SAVED-FILESPEC.
    CALL 'LIB$RENAME_FILE' USING BY DESCRIPTOR FOUND-FILESPEC SAVED_FILESPEC GIVING STATUS_RESULT.
    IF STATUS_RESULT NOT = SS$_NORMAL
       DISPLAY "ECHEC SAUVEGARDE FICHIER " FOUND-FILESPEC " " STATUS_RESULT
    END-IF.

TST-3628-9994.
*
    EXEC SQL
    SELECT SUBSTRING(CDELAB FROM 1 FOR 8) INTO :W-CDELAB FROM  E.CDE_FAC 
     WHERE CODDEP=:W-CODDEP AND CODLAB=:W-CODLAB AND NUMCDE=:W-NUMCDE AND NUMRAL=:W-NUMRAL
     LIMIT TO 1 ROW
    END-EXEC.
    MOVE "2003" TO NUM-MSG        
    MOVE "SELECT CDE_FAC cde fac" TO ANO_MESSAGE
    PERFORM GESTION-SQLSTATE
    IF TROUVE
       IF W-CDELAB IS NUMERIC
          MOVE W-CDELAB(1:7) TO W-NUMCDE-9994
          MOVE W-CDELAB(8:1) TO W-NUMRAL-9994
          EXEC SQL
          SELECT SUBSTRING(CDELAB FROM 1 FOR 8) INTO :W-CDELAB FROM  E.CDE_FAC 
           WHERE CODDEP='CO' AND CODLAB='9994' AND NUMCDE=:W-NUMCDE-9994 AND NUMRAL=:W-NUMRAL-9994
           LIMIT TO 1 ROW
          END-EXEC
          MOVE "2003" TO NUM-MSG        
          MOVE "SELECT CDE_FAC cde fac 9994" TO ANO_MESSAGE
          PERFORM GESTION-SQLSTATE
          IF TROUVE
             DISPLAY "commande 3628 ==> Commande  9994 TROUVE " W-CDELAB 
             MOVE 'O' TO FLAG-CDE9994
          END-IF
       END-IF
    END-IF.

*----------------------------------------------------------------------*
MAJ-DTLIVR-BDSTATSEXTRANET.
*----------------------------------------------------------------------*
*
    MOVE "N" TO FLAG-CREAT-OK

    IF DEADLOCK
       EXEC SQL
       ROLLBACK
       END-EXEC
    END-IF.
*
*
    EXEC SQL
       SET TRANSACTION READ WRITE
    END-EXEC.

    EXEC SQL
    UPDATE E.CDE_FAC SET DTLIVR = :W-DATBIN,  FLAGLIV='O'
      WHERE CODDEP=:W-CODDEP AND CODLAB=:W-CODLAB AND NUMCDE=:W-NUMCDE AND NUMRAL=:W-NUMRAL
    END-EXEC.

    MOVE '2009' TO NUM-MSG.
    MOVE 'MAJ DTLIVR CDE_FAC' TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.
    IF NOT DEADLOCK
       MOVE 'O' TO FLAG-CREAT-OK
       EXEC SQL
       COMMIT
       END-EXEC
    END-IF.
*

*----------------------------------------------------------------------*
MAJ-DTLIVR-BDSTATSEXTRANET-9994.
*----------------------------------------------------------------------*
*
    display "MAJ-DTLIVR-BDSTATSEXTRANET-9994"
*
    MOVE "N" TO FLAG-CREAT-OK

    IF DEADLOCK
       EXEC SQL
       ROLLBACK
       END-EXEC
    END-IF.
*
    EXEC SQL
       SET TRANSACTION READ WRITE
    END-EXEC.

    EXEC SQL
    UPDATE E.CDE_FAC SET DTLIVR = :W-DATBIN,  FLAGLIV='O'
      WHERE CODDEP='CO' AND CODLAB='9994' AND NUMCDE=:W-NUMCDE-9994 AND NUMRAL=:W-NUMRAL-9994
    END-EXEC.

    MOVE '2009' TO NUM-MSG.
    MOVE 'MAJ DTLIVR CDE_FAC 9994' TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.
    IF NOT DEADLOCK
       MOVE 'O' TO FLAG-CREAT-OK
       EXEC SQL
       COMMIT
       END-EXEC
    END-IF.

*
*----------------------------------------------------------------------*
CVT-MONTH-ASC-NUM.
*----------------------------------------------------------------------*
    EVALUATE W-MONTH-ASC
    WHEN  "JAN" MOVE "01" TO W-MONTH-NUM
    WHEN  "FEB" MOVE "02" TO W-MONTH-NUM
    WHEN  "MAR" MOVE "03" TO W-MONTH-NUM
    WHEN  "APR" MOVE "04" TO W-MONTH-NUM
    WHEN  "MAY" MOVE "05" TO W-MONTH-NUM
    WHEN  "JUN" MOVE "06" TO W-MONTH-NUM
    WHEN  "JUL" MOVE "07" TO W-MONTH-NUM
    WHEN  "AUG" MOVE "08" TO W-MONTH-NUM
    WHEN  "SEP" MOVE "09" TO W-MONTH-NUM
    WHEN  "OCT" MOVE "10" TO W-MONTH-NUM
    WHEN  "NOV" MOVE "11" TO W-MONTH-NUM
    WHEN  "DEC" MOVE "12" TO W-MONTH-NUM
    END-EVALUATE.

*----------------------------------------------------------------------*
CVT-MONTH-NUM-ASC.
*----------------------------------------------------------------------*
    EVALUATE W-MONTH-NUM
    WHEN  '01' MOVE "JAN" TO W-MONTH-ASC
    WHEN  '02' MOVE "FEB" TO W-MONTH-ASC
    WHEN  '03' MOVE "MAR" TO W-MONTH-ASC
    WHEN  '04' MOVE "APR" TO W-MONTH-ASC
    WHEN  '05' MOVE "MAY" TO W-MONTH-ASC
    WHEN  '06' MOVE "JUN" TO W-MONTH-ASC
    WHEN  '07' MOVE "JUL" TO W-MONTH-ASC
    WHEN  '08' MOVE "AUG" TO W-MONTH-ASC
    WHEN  '09' MOVE "SEP" TO W-MONTH-ASC
    WHEN  '10' MOVE "OCT" TO W-MONTH-ASC
    WHEN  '11' MOVE "NOV" TO W-MONTH-ASC
    WHEN  '12' MOVE "DEC" TO W-MONTH-ASC
    END-EVALUATE.

*----------------------------------------------------------------------*
GESTION-SQLSTATE.
*----------------------------------------------------------------------*
     MOVE 'N' TO FLAG-DEADLOCK.
     EVALUATE SQLSTATE
     WHEN '02000'              MOVE 'N' TO FLAG-TROUVE
     WHEN '22002'              MOVE 'O' TO FLAG-TROUVE
     WHEN 'R1001'              MOVE 'O' TO FLAG-DEADLOCK
     WHEN 'R1002'              MOVE 'O' TO FLAG-DEADLOCK
     WHEN '00000' THRU '01999' MOVE 'O' TO FLAG-TROUVE  
     WHEN '02001' THRU '22001' PERFORM FIN-ANORMALE
     WHEN '22003' THRU 'R1000' PERFORM FIN-ANORMALE
     WHEN 'R1003' THRU 'S9999' PERFORM FIN-ANORMALE
     END-EVALUATE.
     IF DEADLOCK
        DISPLAY "DEADLOCK !!!!!"
        MOVE "N" TO FLAG-TROUVE
        PERFORM FIN-ANORMALE
     END-IF.

*----------------------------------------------------------------------*
FIN-ANORMALE.
*----------------------------------------------------------------------*
    CALL "SYS$PUTMSG" USING RDB$MESSAGE_VECTOR.
    DISPLAY "FIN ANORMALE".
    DISPLAY "SQLSTATE " SQLSTATE WITH CONVERSION
    DISPLAY NUM-MSG " " ANO_MESSAGE.
    IF NUM-MSG > "2000"
       WRITE ANO-ENREG
    ELSE
       CALL "D00_MSGGES" USING ZONES-PARAMETRES
    END-IF.
    CALL "SQL$GET_ERROR_TEXT" USING BY DESCRIPTOR GET-ERROR-BUFFER
                                    BY REFERENCE GET-MSG-LEN.
    DISPLAY "GET-ERROR-BUFFER " GET-ERROR-BUFFER(1:GET-MSG-LEN)

    MOVE GET-ERROR-BUFFER (1:80) TO ANO_MESSAGE
    WRITE ANO-ENREG
    IF GET-MSG-LEN >80
       MOVE GET-ERROR-BUFFER (80:80) TO ANO_MESSAGE
       WRITE ANO-ENREG
       IF GET-MSG-LEN >160
          MOVE GET-ERROR-BUFFER (160:80) TO ANO_MESSAGE
          WRITE ANO-ENREG
          WRITE ANO-ENREG
          IF GET-MSG-LEN >240
             MOVE GET-ERROR-BUFFER (240:60) TO ANO_MESSAGE
             WRITE ANO-ENREG
          END-IF
       END-IF
    END-IF.
    DISPLAY "AVANT STOP RUN".
    PERFORM FERMETURE-FICHIERS.
    EXEC SQL
    ROLLBACK
    END-EXEC.

    MOVE "O" TO VALLOG.
    CALL "LIB$SET_LOGICAL" USING BY DESCRIPTOR LOGNAM BY DESCRIPTOR VALLOG.

    STOP RUN.

*----------------------------------------------------------------------*
FIN-ANOFERME.
*----------------------------------------------------------------------*
    CALL "D00_MSGGES" USING ZONES-PARAMETRES.
    STOP RUN.

*----------------------------------------------------------------------*
OUVERTURE-FICHIERS.                                                           
*----------------------------------------------------------------------*
    OPEN INPUT FIC-TELE.
    IF CODE-STATUS NOT = "00"
        MOVE "1001" TO NUM-MSG
        MOVE FOUND-FILESPEC TO PARAM-01
        GO TO FIN-ANORMALE
    END-IF.  

*----------------------------------------------------------------------*
OPEN-FILES.
*----------------------------------------------------------------------*
    OPEN INPUT FIC-MESSAGES.
    IF CODE-STATUS NOT = "00"
       MOVE "1001" TO NUM-MSG        
       MOVE "RMS_MSG" TO PARAM-01
       GO TO FIN-ANORMALE
    END-IF.
    OPEN OUTPUT FIC-ANOMALIES.
    IF CODE-STATUS NOT = "00"
       MOVE "1001" TO NUM-MSG        
       MOVE "RMS_ANO" TO PARAM-01
       GO TO FIN-ANORMALE
    END-IF.

*----------------------------------------------------------------------*
FERMETURE-FICHIERS.
*----------------------------------------------------------------------*
    CLOSE FIC-TELE.
    IF CODE-STATUS NOT = "00"
       MOVE "1004" TO NUM-MSG
       MOVE FOUND-FILESPEC TO PARAM-01
       GO TO FIN-ANOFERME
    END-IF.

*----------------------------------------------------------------------*
CLOSE-FILES.
*----------------------------------------------------------------------*
    CLOSE FIC-ANOMALIES.   
    IF CODE-STATUS NOT = "00"
       MOVE "1004" TO NUM-MSG        
       MOVE "RMS_ANO" TO PARAM-01
       GO TO FIN-ANOFERME 
    END-IF.
    CLOSE FIC-MESSAGES.
    IF CODE-STATUS NOT = "00"
       MOVE "1004" TO NUM-MSG        
       MOVE "RMS_MSG" TO PARAM-01
       GO TO FIN-ANOFERME
    END-IF.

END PROGRAM T10_MAJ_DTLIVR_BDCRM.
