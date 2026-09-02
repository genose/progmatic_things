*-------------------------------------------------------------------------*
*    Nom du programme  : D02_EXTCDE_CRMCSP1.SCO                           *
*    Auteur            : AG.DUROURE                                       *
*    Langage           : RDB/COBOL                                        *
*    Date de creation  : 16/06/2015                                       *
*    Date modification : 16/06/2015                                       *
*== V3.001-6420 AP:AGD LIB:Creation a partir du D02_EXTCDE_HOPITAUX1      *
*    Fonctionnalites   : CREATION FICHIERS TRANSMIS AU LABORATOIRE        *   
*                      :           FICHIER CONFIRMATION EXPEDITION        *
*                        pour dotation (typcde='D')                       *
*                         ( Gestion Multi-Labos)                          *
*    Refer. externes   :                                                  *
*    - base            : DEPOT                                            *
*    - tables          : PAR CDE CDL ART CLL CLE                          *
*    - sous-programmes : D00_MSGGES                                       *
*-------------------------------------------------------------------------*
IDENTIFICATION DIVISION.
PROGRAM-ID. D02_EXTCDE_CRMCSP1. 

ENVIRONMENT DIVISION.       
INPUT-OUTPUT SECTION.
FILE-CONTROL.           

* fichier des libelles des messages a editer en cas d'anomalie
        SELECT FIC-MESSAGES  ASSIGN TO "RMS_MSG".

* fichier des anomalies detectes                             
        SELECT FIC-ANOMALIES ASSIGN TO "RMS_ANO" 
               FILE STATUS IS CODE-STATUS.

* fichier a transmettre (depot CO)
        SELECT FIC-TRANSMITCO ASSIGN TO "RMS_TRANSCO"
               FILE STATUS IS CODE-STATUS.

* fichier a transmettre (depot MO)
        SELECT FIC-TRANSMITMO ASSIGN TO "RMS_TRANSMO"
               FILE STATUS IS CODE-STATUS.

* fichier sortie des commandes a mettre a jour (datext)
        SELECT FIC-MAJ        ASSIGN TO "RMS_MAJ"
               FILE STATUS IS CODE-STATUS.


DATA DIVISION.
*=============
FILE SECTION.                                                 
*------------
FD  FIC-MESSAGES LABEL RECORD OMITTED.
    COPY "CDD$TOP.DEPOT.FICHIERS.D00_MSGTXT" FROM DICTIONARY.

FD  FIC-ANOMALIES LABEL RECORD OMITTED.
    COPY "CDD$TOP.DEPOT.FICHIERS.D00_ANOMAL" FROM DICTIONARY.

FD  FIC-MAJ.
01  LIGNE-MAJ              PIC X(23).


FD  FIC-TRANSMITCO.
01  ENREG-TRANSMITCO PIC X(197).

FD  FIC-TRANSMITMO.
01  ENREG-TRANSMITMO PIC X(197).


*
WORKING-STORAGE SECTION.                                     
*-----------------------

01  ENREG-TRANSMIT.
   02 ENR-TYPMES PIC X(6).
   02 ENR-FILLER PIC X.
   02 ENR-DEBCDE.
      03 ENR-DEBCDE-EMETEUR  PIC X(35).
      03 ENR-DEBCDE-RECEPTE  PIC X(35).
*bt : flag test/prod
      03 ENR-DEBCDE-TEST     PIC X(1).
      03 ENR-DEBCDE-FILLER   PIC X(100).
      03 FILLER              PIC X(19).

   02 ENR-REFCDE REDEFINES ENR-DEBCDE.
      03 ENR-REFCDE-DATCDE   PIC X(06).          
      03 FILLER              PIC X.
      03 ENR-REFCDE-CDELAB   PIC X(22).
      03 FILLER              PIC X.
      03 ENR-REFCDE-REFCDE   PIC X(35).
      03 FILLER              PIC X.
      03 ENR-REFCDE-TYPCDE   PIC X(02).
      03 FILLER              PIC X.
*      03 ENR-REFCDE-CLICIP   PIC X(06).
      03 ENR-REFCDE-CLICSP   PIC 9(06).
      03 FILLER              PIC X.
      03 ENR-REFCDE-NOMLIV   PIC X(35).
      03 ENR-REFCDE-FILLER   PIC X.
      03 ENR-REFCDE-CODOPE   PIC X(3).
      03 FILLER              PIC X.
      03 ENR-REFCDE-NUMDOC   PIC X(10).
      03 FILLER              PIC X.
      03 ENR-REFCDE-CDESAISIE PIC X.
      03 FILLER              PIC X.
      03 ENR-REFCDE-DATREC   PIC X(23).
      03 FILLER              PIC X.
      03 ENR-REFCDE-TRAFIC   PIC X.
      03 FILLER              PIC X.
      03 ENR-REFCDE-DATEBP   PIC X(8).
      03 FILLER              PIC X.
      03 ENR-REFCDE-CODSAI   PIC X(7).
      03 FILLER              PIC X.
      03 ENR-REFCDE-CODSTR   PIC X(4).
      03 FILLER              PIC X.
      03 ENR-REFCDE-STREXP   PIC X(4).
      03 FILLER              PIC X.
      03 ENR-REFCDE-CODREP   PIC X(8).
   02 ENR-LINTXT REDEFINES ENR-DEBCDE.
      03 ENR-LINTXT-MESSAGE  PIC X(114).
   02 ENR-TXTCDE REDEFINES ENR-DEBCDE.
      03 ENR-TXTCDE-MESSAGE  PIC X(80).
      03 FILLER              PIC X.
      03 ENR-TXTCDE-TYPDOC   PIC X(02).
   02 ENR-LINCDE REDEFINES ENR-DEBCDE.
      03 ENR-LINCDE-CODART   PIC X(10).
      03 FILLER              PIC X.
      03 ENR-LINCDE-QTCCDE   PIC 9(07).
      03 FILLER              PIC X.
      03 ENR-LINCDE-QTCGRT   PIC 9(07).
      03 FILLER              PIC X.
      03 ENR-LINCDE-LIBELL   PIC X(35).
      03 FILLER              PIC X.
      03 ENR-LINCDE-CODLABLAB PIC X(4).
      03 FILLER              PIC X(38).

   02 ENR-FINCDE REDEFINES ENR-DEBCDE.
      03 ENR-FINCDE-SUMQTE   PIC 9(08).
      03 FILLER              PIC X.
      03 ENR-FINCDE-NBLIG    PIC 9(08).
      03 FILLER              PIC X(97).

   02 ENR-FINMES REDEFINES ENR-DEBCDE.
      03 ENR-FINMES-NBCDE    PIC 9(08).
      03 FILLER              PIC X(97).

* donnees FIC-xxx pas utilisees 
01 OLD-ENREG-TRANSMIT.
    02 ENTETE-TRANSMIT.    
       03 FIC-ENVOI    PIC 9(6).
       03 FIC-TYPENR   PIC X.
       03 FIC-CODLAB   PIC X(4).
       03 FIC-NUMCDE   PIC 9(7).  
       03 FIC-NUMRAL   PIC 9(1).
*  ENTETE commande FIC-TYPENR = 0 
    02 FIC-TYPE1.
       03 FIC-CDELAB   PIC X(10).
       03 FIC-STATUT   PIC X(3).			
       03 FIC-DATEBL   PIC X(8).			
       03 FIC-NBCOL    PIC 9(6).  			
       03 FIC-PDS      PIC 9(6)V99.			
       03 FIC-UNITE    PIC XX.
       03 FIC-VOL      PIC 9(6)V99.			
       03 FIC-CLILAB   PIC 9(8).
       03 FIC-CLILAB-x redefines FIC-CLILAB pic x(8).
       03 FIC-RAISOCL  PIC X(30).
       03 FIC-NOMLIV   PIC X(25).
       03 FIC-ADR1L    PIC X(30).
       03 FIC-ADR2L    PIC X(30).
       03 FIC-VILLEL   PIC X(32).
       03 FIC-CPOSTL   PIC X(05).
       03 fic-telep    pic x(30).
       03 FIC-TYPCDE   PIC X.
       03 FIC-NUMFAC   PIC X(10).
       03 FIC-CLILAB2   PIC X(10).
       03 FIC-CODREP    PIC X(8).
       03 FIC-DATCDE   PIC X(8).			
       03 FIC-DATSAI   PIC X(8).			
       03 FIC-DATREC   PIC X(23).
       03 FIC-DATEBP   PIC X(8).			
       03 FIC-CODSTR   PIC X(4).
       03 FIC-STREXP   PIC X(4).
*   ligne commande detail FIC-TYPENR = 2
    02 FIC-TYPE2 REDEFINES FIC-TYPE1.
       03 FIC-REFLIG   PIC 9(4).
       03 FIC-ARTLAB   PIC X(15).
       03 FIC-QTCCDE   pic 9(6).
       03 FIC-QTLCDE   PIC 9(6).
       03 F            PIC X(30). 
       03 fic-numret   pic 9(9).
       03 F            PIC X(194). 
*   ligne enreg lots livres FIC-TYPENR = 3
    02 FIC-TYPE3 REDEFINES FIC-TYPE1.
       03 FIC-REFLIG3  PIC 9(4).
       03 FIC-ARTLAB3  PIC X(15). 
       03 FIC-QTLCDE3   PIC 9(6).
       03 FIC-LOTFAB   PIC X(12).	
       03 F            PIC X(227).


01 NOM-FICHIER EXTERNAL.
     02 FIC-DEV  PIC X(7).
     02 FIC-NOM1 PIC X(15).

01  ENREG_MAJ.   
	03  ENREG_MAJ_DAT. 
*    	    05  ENREG-DATEXT		 	PIC X(23).
    	    05  ENREG-DATPOR		 	PIC X(23).
	03  ENREG_MAJ_CDE REDEFINES ENREG_MAJ_DAT. 
    	    05  ENREG-NUMCDE		 	PIC 9(7).
    	    05  ENREG-NUMRAL		 	PIC 9.
            05  FILLER                          PIC X(5).
            05  ENREG-CDELAB                    PIC X(10).
*
01  inumret  pic s9(4) comp.
01  inoapv   pic s9(4) comp.
01  iclicsp  pic s9(4) comp.
01  inumlig  pic s9(4) comp.
01  icodart  pic s9(4) comp.
01  ilotfab  pic s9(4) comp.
01  iqteval  pic s9(4) comp.
01  inort    pic s9(4) comp.
01  ipoids   pic s9(4) comp.
01  inbcolis pic s9(4) comp.
01  idatmaj  pic s9(4) comp.
01  icdelab pic s9(4) comp.
01  inumdos pic s9(4) comp.
*
01  rte-clicsp pic 9(8).
01  rte-noapv  pic 9(8).
01  rte-numret pic 9(8).
01  rte-datmaj PIC S9(11)V9(7) COMP.
01  rte-numdos PIC 9(8).                
*
01  rtl-numlig pic 9(8).
01  rtl-codart pic x(10).
01  rtl-lotfab pic x(12).
01  rtl-qteval pic 9(8). 
01  rtl-VQUALI pic X(1). 
*
01  cde-cdelab pic x(10).
01  rec-nort   pic 9(8).
01  rec-poids  pic 9(8).
01  rec-nbcolis   pic 9(8).
*
01  ret-refexp pic x(12).
*
01 w-nort      pic x(8).
01 ws-nort     pic x(8).
01 T1          pic 9.
01 T2          pic 9.
01 ws-numcde pic 9(7).
01 ws-numral pic 9.
*
01  WFONCT2        PIC X(30).
01  WFONCT         PIC X(30).
01  WFONCT-LABLAB  PIC X(30).
01  WARGUM-TYPLAB  PIC X(30).
01  w-nbcol        pic 9(6).
01  w-pds          pic 9(6)v99.
01  w-vol          pic 9(6)v99.
01  w-clicip       pic x(6).
01  W-ADELI        PIC X(11).
01  W-CIPPDV       pic 9(7).
01  w-cptfac       pic 9(5).
01  W-FONCT.
    02  W-JJ    PIC 99.
    02  TIRET1  PIC X     VALUE '-'.
    02  W-MM    PIC 99.
    02  TIRET2  PIC X     VALUE '-'.
    02  W-SI    PIC 99.
    02  W-AA    PIC 99.
    02  W-HEURE PIC X(12).
01  W-NUMDOC     PIC X(10).

01  WARGUM-DEPLAB.
    02  W-DEPLAB-CODLAB PIC X(4).
    02  W-DEPLAB-DEPART PIC X(2).


01  W-FONCTION.
    02  FONCT-JJ    PIC 99.
    02  W-TIRET1 PIC X VALUE "-".
    02  FONCT-MM    PIC X(3).
    02  W-TIRET2 PIC X VALUE "-".
    02  FONCT-SI    PIC 99.
    02  FONCT-AA    PIC 99.
    02  FONCT-HEURE PIC X(12).


01 PARAM-DATES.
   02 PARAM-DATE-BIN	PIC S9(11)V9(7) COMP.
   02 PARAM-DATE-TXT.
      03 DAT-AA		PIC XXXX.
      03 DAT-MM		PIC XX.
      03 DAT-JJ		PIC XX.

01 W-D-DATE 			PIC X(23).
01 W-DATE-ASCII PIC X(23).
01 W-DATE-ZERO  PIC X(23) VALUE "17-NOV-1858 00:00:00.00".

01 W-LIB-HORAIRE PIC X(35).
01 W-LIB-CONTACT PIC X(35).

77  W-PB-CDE    PIC 9.
77  I1           PIC S9(4) COMP.
77  IDATEBL     PIC S9(4) COMP.
77  IDATEBP     PIC S9(4) COMP.
77  ICODSTR     PIC S9(4) COMP.
77  ITYPNFA     PIC S9(4) COMP.
77  IQUANTA     PIC S9(4) COMP.
77  IMOISFA     PIC S9(4) COMP.
77  ICPTFAC     PIC S9(4) COMP.
77  IVQUALI     PIC S9(4) COMP.
77  ICODREP     PIC S9(4) COMP.
77  IDATREC     PIC S9(4) COMP.
77  ICIPPDV     PIC S9(4) COMP.
77  ICODSAI     PIC S9(4) COMP.
77  ISTREXP     PIC S9(4) COMP.
77  ICODLABLAB  PIC S9(4) COMP.
77  W-DATDEB        PIC S9(11)V9(7) COMP.
77  W-DATFIN        PIC S9(11)V9(7) COMP.
77  W-DATBIN        PIC S9(11)V9(7) COMP.
77  W-DATBINAIRE    PIC S9(11)V9(7) COMP.
01 W-LAST-DATE  PIC S9(11)V9(7) COMP.
01 W-DATE-VMS   PIC S9(11)V9(7) COMP.
01 W-DATE-RDB   PIC S9(11)V9(7) COMP.
01 W-DATE-NULL  PIC S9(11)V9(7) COMP.
77 W-MOIS-NUM   PIC X(2).
77 W-MOTIF   PIC X(2).
77 W-STATUT  PIC X(3).
77 W-COUNT   PIC 9(2).
77 W-POSDEB  PIC 9.
77 W-TAILLE  PIC 9.
77 W-CLILAB  PIC X(10).
77 W-CODREP  PIC X(8).
77 W-COMMENT PIC X(35).

77 W-CLIPAY  PIC 9(06).
77 W-CLICSP  PIC 9(06).
77 W-CLIPAY-CLICIP  PIC X(06).
77 W-CLIPAY-CIPPDV  PIC X(07).

01 W-DATE-SYS   PIC S9(11)V9(7) COMP.
01 W-DATE-HEURE.
   02  W-JMA    PIC X(12).
   02  W-HH     PIC X(11).
01 W-DATE-STD   PIC X(10).

01 W-LENGTH PIC S9(11)V9(7) COMP.
01 W-FLAG   PIC S9(6) COMP VALUE ZERO.
01 W-RESULT PIC S9(5) COMP.
01  W-RAL               PIC 99.
01  IND                 PIC 99.

*-- variables  de travail du programme

77 W-CODDEP PIC X(2).
77 W-CODLAB PIC X(4).
77 W-CODLABLAB PIC X(4).
77 PREV-CODDEP PIC X(2).
77 PREV-CODLAB PIC X(4).
77 W-CDL-CODDEP PIC X(2).
77 W-CDL-CODLAB PIC X(4).
77 W-LABLAB PIC X(4).

77 W-CODSTR PIC X(4).
77 W-NUMCDE PIC 9(7).
77 W-DATPER PIC X(8).
77 W-NUMRAL PIC 9(6).
77 W-NUMRALMAX PIC 9(6).
77 W-NUMRALMAXLAB PIC 9(6).
77 W-REFCDE PIC X(35).
77 W-CDELAB PIC X(15).
77 W-NUMEXP PIC X(9).
77 W-NUMRAF PIC 9(5).
77 W-DATEBL PIC X(8).
77 W-DATEBP PIC X(8).
77 W-TYPNFA PIC X(2).
77 W-REFLIG PIC X(15).
77 W-QTCCDE PIC S9(9).
77 W-QTLCDE PIC S9(9).
77 W-QTLCDET PIC S9(9).
77 W-QTERALM PIC 9(8).
77 W-VERIFQTE-CDE PIC 9(8).
77 W-VERIFQTE-CLL PIC 9(8).
77 W-SUMNBE   PIC 9(6).
77 W-SUMQTE   PIC 9(10).
77 W-SUMQTE-CO   PIC 9(10).
77 W-SUMQTE-MO   PIC 9(10).
77 W-SUMNBL   PIC 9(6).
77 W-SUMNBO   PIC 9(6).
77 W-SUMNBELO PIC 9(6).
77 W-SUMQTCDE PIC 9(6).
77 W-RESVIS   PIC 9(8).
77 W-TYPCDELAB PIC X(3).
77 W-TOTLIV  PIC 9(8).
77 WA-QTLGRT PIC 9(8).
77 WA-QTLCDE PIC 9(8).
77 WA-QTLECH PIC 9(8).
77 W-DIFF   PIC S9V9999.  
77 CDL-NUMLIG PIC X(4).
77 CDL-CODART PIC X(10).
77 W-CDELIVTOT PIC X.
77 W-CDLLIVTOT PIC X.

77 W-CPTCLL   PIC 9(5).
01  W-NUMLIGX.
    10  W-NUMLIGN PIC 9(4).
77 W-NUMLIG PIC X(4).
77 W-CODART PIC X(10).
77 W-ARTLAB PIC X(15).
77 W-ARTSAI PIC X(07).
77 W-ARTCIP PIC X(7). 
77 W-UNITE  PIC X(2).
77 W-MENSPE PIC X(3).
77 W-LOTFAB PIC X(12).
77 W-RUPLOT PIC X(12).
77 W-QTELIV PIC 9(8).
77 W-QTELIVT PIC 9(8).
77 W-QTELIVC PIC 9(8).
77 WS-QTELIV PIC 9(8).
77 W-QTCCDEL PIC 9(8).
77 W-QTETHE PIC 9(8).
77 W-QTETOT PIC 9(8).
77 W-QTCGRT PIC 9(8).
77 W-QTERAL PIC 9(8).
77 WA-QTERAL PIC 9(8).
77 W-QTECDE PIC 9(8).
77 W-QTRCDE PIC 9(8).
77 W-TYPTRS PIC X(1).
77 W-GESLOT PIC X(1).

77 W-CODENT PIC X(3).
77 W-FONCT1 PIC X(30).
77 W-DATCRE PIC S9(11)V9(7) COMP.
77 W-DATMAJ PIC S9(11)V9(7) COMP.
77 W-CODOPE PIC X(3).
77 W-CHAMPS PIC X(6).
77 W-ARGUM  PIC X(4).
77 WS-ARGUM PIC X(6).
77 WS-FONCT PIC X(2).
77 QTLCDETP PIC 9(8).

01 WARGUM.
   02 WCODLAB   PIC X(4).
   02 WTYPE     PIC X VALUE "N".
01 WWARGUM PIC X(5).
01 WARGUMRAL.
   02 WRALCODLAB      PIC X(4).
   02 WRALGENCLILAB   PIC X(3).
01 WWARGUMRAL PIC X(7).
*-- tableau pour stockage des lignes commandes d'une commande
01 NB-CDL  PIC 9999.
01 W-NB-CDL-CO  PIC 9999.
01 W-NB-CDL-MO  PIC 9999.
01 NB-CDE  PIC 9999.
01 NB-CDE-CO  PIC 9999.
01 NB-CDE-MO  PIC 9999.
01 NB-CDL-NO  PIC 9999.
01 IND-CDL PIC 9999.
01 IND-CDL-NO PIC 9999.
01 MAX-CDL PIC 9999 VALUE 1000.
01 TABLE-CDL.
   02 TAB-CDL OCCURS 1000.
      03 T-CDL.
         04 TCDL-NUMLIG   PIC 9(4).
         04 TCDL-CODART   PIC X(10).
         04 TCDL-ARTLAB   PIC X(15).
         04 TCDL-ARTSAI   PIC X(07).
         04 TCDL-LIBELL   PIC X(35).
         04 TCDL-artcip   PIC X(7). 
         04 TCDL-LOTFAB   PIC X(12).
         04 TCDL-QTELIV   PIC 9(8).
         04 TCDL-QTCGRT   PIC 9(8).
         04 TCDL-QTECDE   PIC 9(8).
         04 TCDL-QTLCDE   PIC 9(8).
         04 TCDL-CODENR   PIC X.
         04 TCDL-CODLAB   PIC X(4).
         04 TCDL-CODDEP   PIC X(2).
         04 TCDL-CODLABLAB PIC X(4).

*----------------- zones de travail independantes ---------------------*
77  CODE-STATUS EXTERNAL  PIC XX.

*-- zones de gestion des erreur par module D00_MSGGES 
COPY "CDD$TOP.DEPOT.FICHIERS.D00_MSGPAR" FROM DICTIONARY.
*
*-- flags indicateurs booleens 
01  FLAG-TROUVE       PIC X VALUE "N".            
    88 TROUVE               VALUE "O".
01  FLAG-TROUVE-LOT   PIC X VALUE "N".            
    88 TROUVE-LOT           VALUE "O".
01  FLAG-NOPAR        PIC X VALUE "N".            
    88 NOPAR                VALUE "O".
01  FLAG-ERREUR       PIC X VALUE "N".            
    88 ERREUR               VALUE "O".
01  FLAG-FIN-CDE      PIC X VALUE "N".            
    88 FIN-CDE              VALUE "O".
01  FLAG-FIN-CDL      PIC X VALUE "N".            
    88 FIN-CDL              VALUE "O".
01  FLAG-FIN-RAL      PIC X VALUE "N".            
    88 FIN-RAL              VALUE "O".
01  FLAG-FIN-PAR      PIC X VALUE "N".            
    88 FIN-PAR              VALUE "O".
01  FLAG-FIN-RTE      PIC X VALUE "N".            
    88 FIN-RTE              VALUE "O".
01  FLAG-FIN-RTL      PIC X VALUE "N".            
    88 FIN-RTL              VALUE "O".
01  FLAG-FINFIC       PIC X VALUE "N".            
    88 FINFIC               VALUE "O".
01  FLAG-CDE-NONFAC   PIC X VALUE "N".
    88 CDE-NONFAC           VALUE "O".
01  FLAG-FIN-RECHERCHE-DIV  PIC X VALUE "N".
    88 FIN-RECHERCHE-DIV          VALUE "O".

01  FLAG-BL-TROUVE    PIC X VALUE "N".
    88 BL-TROUVE            VALUE "O".
01  FLAG-RT-TROUVE    PIC X VALUE "N".
    88 RT-TROUVE            VALUE "O".

*-- codes exceptions RDB
01  RDB$_DEADLOCK                     PIC S9(9) COMP 
    VALUE IS EXTERNAL RDB$_DEADLOCK.
01  RDB$_LOCK_CONFLICT                PIC S9(9) COMP 
    VALUE IS EXTERNAL RDB$_LOCK_CONFLICT.
01  RDB$_INTEG_FAIL 		      PIC S9(9) COMP 
    VALUE IS EXTERNAL RDB$_INTEG_FAIL. 
01  EXCEPT                            PIC S9(9) COMP.                  

*--- copie des descriptions entites tables utilisees 
    EXEC SQL
    INCLUDE FROM DICTIONARY 'CDD$DEFAULT.DBDEPOT.RDB$RELATIONS.MVT' 
    END-EXEC.
    EXEC SQL
    INCLUDE FROM DICTIONARY 'CDD$DEFAULT.DBDEPOT.RDB$RELATIONS.MES' 
    END-EXEC.
    EXEC SQL
    INCLUDE FROM DICTIONARY 'CDD$DEFAULT.DBDEPOT.RDB$RELATIONS.CDE' 
    END-EXEC.
    EXEC SQL
    INCLUDE FROM DICTIONARY 'CDD$DEFAULT.DBDEPOT.RDB$RELATIONS.CDL' 
    END-EXEC.
    EXEC SQL
    INCLUDE FROM DICTIONARY 'CDD$DEFAULT.DBDEPOT.RDB$RELATIONS.ART' 
    END-EXEC.
    EXEC SQL
    INCLUDE FROM DICTIONARY 'CDD$DEFAULT.DBDEPOT.RDB$RELATIONS.CLI' 
    END-EXEC.
    EXEC SQL
    INCLUDE FROM DICTIONARY 'CDD$DEFAULT.DBDEPOT.RDB$RELATIONS.CDO' 
    END-EXEC.
    EXEC SQL
    INCLUDE FROM DICTIONARY 'CDD$DEFAULT.DBDEPOT.RDB$RELATIONS.CCL' 
    END-EXEC.
    EXEC SQL
    INCLUDE FROM DICTIONARY 'CDD$DEFAULT.DBDEPOT.RDB$RELATIONS.PAR' 
    END-EXEC.
    EXEC SQL
    INCLUDE FROM DICTIONARY 'CDD$DEFAULT.DBDEPOT.RDB$RELATIONS.RTE' 
    END-EXEC.
    EXEC SQL
    INCLUDE FROM DICTIONARY 'CDD$DEFAULT.DBDEPOT.RDB$RELATIONS.RTL' 
    END-EXEC.
    EXEC SQL
    INCLUDE FROM DICTIONARY 'CDD$TOP.TRANSP.TRANSPORT.RDB$RELATIONS.STR'
    END-EXEC.
    EXEC SQL
    INCLUDE FROM DICTIONARY 'CDD$TOP.TRANSP.TRANSPORT.RDB$RELATIONS.REC'
    END-EXEC.
*----------------------------------------------------------------------*

*--- Declarations des bases de donnees utilisees
    EXEC SQL 
    DECLARE D ALIAS  FILENAME 'BD_DEPOT' DBKEY SCOPE IS ATTACH
    END-EXEC. 

    EXEC SQL 
        DECLARE T ALIAS FILENAME 'BD_TRANSPORT'
    END-EXEC. 

    EXEC SQL 
        DECLARE S ALIAS FILENAME 'BD_STATS'
    END-EXEC. 

    EXEC SQL 
    DECLARE P ALIAS  FILENAME 'BD_PDF'
    END-EXEC. 

*----------------------------------------------------------------------*

*----------------------------------------------------------------------*
*                DECLARATION DU SQLCA                                  *
*----------------------------------------------------------------------*
01 SQLSTATE            PIC X(5).  
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

*-- parametres 
COPY WORK_COM.
01 PARAM.
   02 P-CODDEP  PIC XX.
   02 P-CODLAB  PIC XXXX.
   02 P-CODREP  PIC X.
   02 P-DATDEB  PIC X(23).
   02 P-DATFIN  PIC X(23).
   02 P-QUERETOUR PIC X.

01  PARAM-BASNUMCDE.
    03  WP-NUMCDE          PIC 9(7).
    03  WP-NUMRAL          PIC 9(1).
    03  WP-NUMEXP          PIC X(9).
    03  W-MSG              PIC 9(3).

*----------------------------------------------------------------------*

PROCEDURE DIVISION.
*==================

DECLARATIVES.

* traitement des erreurs d'entrees-sorties sur les fichiers RMS

FILE-PROBLEM-001 SECTION.
     USE AFTER STANDARD EXCEPTION PROCEDURE ON OUTPUT.
FILE-PROBLEM-001-ACTION.

FILE-PROBLEM-002  SECTION.
	USE AFTER STANDARD EXCEPTION PROCEDURE ON INPUT.
FILE-PROBLEM-002-ACTION.         

END DECLARATIVES.

PROGRAMME SECTION.           
*=================
*-----------------------------------*
* DECLARATION DES CURSEURS 
*-----------------------------------*
DECLARE-CURSEUR-CDE.
    display "w-last-date   " w-last-date with conversion.
    EXEC SQL
    DECLARE CURCDE CURSOR
    FOR SELECT distinct cde.NUMCDE,cde.NUMRAL,STATUT,TYPNFA,QUANTA,MOISFA,CPTFAC,
                        CPTFAC,GENCLILAB,
		   	CLICSP,TYPCDE,cde.CDELAB,NBSTD,NBDTL,LIVTOT,PDSSTD,PDSDTL,
                        VOLSTD,VOLDTL,DATCDE,DATEBL,refcde,RESVIS,CLILAB,
                        TYPCDELAB ,CODSTR,typnfa,quanta,moisfa,cptfac,CODREP,
                         RAISOCL, NOMLIV, ADR1L, ADR2L, VILLEL, CPOSTL,DATSAI
                        ,cde.codope, CDE.ADELI, CDE.CDESAISIE,
                        CDE.DATREC,TRAFIC, CDE.DATEBP, CDE.CODSAI, CDE.CODMES,
                        CDE.STREXP,CDE.CODLABLAB
    FROM D.CDE JOIN D.CDO on (cde.NUMCDE = cdo.numcde  and cde.NUMRAL=cdo.numral)
    WHERE CODLAB=:W-CODLAB AND (STATUT = 'CRV') 
             AND  DATPOR IS NULL
             AND :P-CODREP <> 'R'
 
    END-EXEC.
    MOVE "2001" TO NUM-MSG.
    MOVE "DECLARE CURSOR CDE" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

DECLARE-CURSEUR-CDE-R.
    display "w-last-date   " w-last-date with conversion.
    EXEC SQL
    DECLARE CURCDE_R CURSOR
    FOR SELECT distinct cde.NUMCDE,cde.NUMRAL,STATUT,TYPNFA,QUANTA,MOISFA,CPTFAC,
                        CPTFAC,GENCLILAB,
		   	CLICSP,TYPCDE,cde.CDELAB,NBSTD,NBDTL,LIVTOT,PDSSTD,PDSDTL,
                        VOLSTD,VOLDTL,DATCDE,DATEBL,refcde,RESVIS,CLILAB,
                        TYPCDELAB ,CODSTR,typnfa,quanta,moisfa,cptfac,CODREP,
                         RAISOCL, NOMLIV, ADR1L, ADR2L, VILLEL, CPOSTL,DATSAI,
                        cde.codope, CDE.ADELI, CDE.CDESAISIE, CDE.DATREC,TRAFIC, CDE.DATEBP, CDE.CODSAI, CDE.CODMES,
                        CDE.STREXP,CDE.CODLABLAB
    FROM D.CDE JOIN D.CDO on (cde.NUMCDE = cdo.numcde  and cde.NUMRAL=cdo.numral)
    WHERE CODLAB=:W-CODLAB AND (STATUT = 'CRV' )
             AND DATEBL > :W-DATDEB AND DATEBL < :W-DATFIN
    END-EXEC.
    MOVE "2001" TO NUM-MSG.
    MOVE "DECLARE CURSOR CDE" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

DECLARE-CURSEUR-CDL.
    EXEC SQL
    DECLARE CURCDL CURSOR
    FOR SELECT L.NUMCDE, L.NUMRAL,codart, L.NUMLIG, L.REFLIG, L.LOTFAB,
	       L.QTCCDE, L.QTCECH, L.QTCGRT,
	       L.QTLCDE, L.QTLECH, L.QTLGRT,L.LIVTOT, L.MENSPE, L.ARTSAI,L.CODDEP,L.CODLAB
    FROM D.CDL L JOIN D.ART A USING(CODART)
    WHERE (L.NUMCDE = :W-NUMCDE AND L.NUMRAL = :W-NUMRAL)
    AND A.CODLAB = :W-CODLAB
    AND CODSSS = '' AND QTCCDE > 0
    AND CODDEP <> ''
    ORDER BY L.CODDEP, L.CODLAB
    END-EXEC.
    MOVE "2002" TO NUM-MSG.
    MOVE "DECLARE CURSOR CDL" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

 DECLARE-CURSEUR-PAR.
 EXEC SQL
     DECLARE CURPAR CURSOR
     FOR SELECT ARGUM, FONCT
     FROM D.PAR
     WHERE CODENT = 'LAB'
       AND CHAMPS = 'NONFAC'
       AND ARGUM STARTING WITH :P-CODLAB
 END-EXEC.
     MOVE "2004" TO NUM-MSG.
     MOVE "DECLARE CURSOR PAR" TO ANO_MESSAGE.
     PERFORM GESTION-SQLSTATE.
*
 DECLARE-CURSEUR-DIV.
*----------------------------------------------------------------------*
 EXEC SQL
        DECLARE CURDIV CURSOR FOR SELECT CODLAB FROM D.LAB
        WHERE CODLAB = :P-CODLAB
 END-EXEC.
        EVALUATE SQLSTATE
           WHEN  '02001' THRU 'S9999'
              MOVE "2018" TO NUM-MSG        
              MOVE "DECLARATION DU CURSEUR CURDIV SUR TABLE LAB" TO ANO_MESSAGE
              PERFORM FIN-ANORMALE
        END-EVALUATE.
*
*
*----------------------------------------------------------------------*
* PROCEDURE PRINCIPALE DU PROGRAMME
*----------------------------------------------------------------------*
TRAITEMENT-PRINCIPAL.                
    MOVE SPACES TO PARAM.
    COPY PROC_COM.
    MOVE LOGIC-NAME TO PARAM.

    DISPLAY "DEBUT PROGRAMME AVEC : " PARAM.

    IF P-CODREP = "R" AND (P-DATDEB = SPACES OR P-DATFIN = SPACES)
       DISPLAY "UNE DATE DEBUT ET UNE DATE FIN DOIVENT ETRE PASSEES EN PARAM" 
       GO TO FIN_TRT.

    MOVE P-CODDEP TO W-CODDEP.
    MOVE P-CODLAB TO W-CODLAB.

*-- initialisation de la date systeme sous la forme jj-mmm-aaaa
    CALL "LIB$DATE_TIME" USING BY DESCRIPTOR W-DATE-ASCII.
    CALL "D00_DATEDI"    USING W-DATE-ASCII W-DATE-STD.
    MOVE W-DATE-ASCII TO W-D-DATE

*-- acquisition de la date du jour 
    CALL  "LIB$DATE_TIME"  USING  BY DESCRIPTOR W-D-DATE.
    CALL  "SYS$BINTIM"  USING  BY DESCRIPTOR W-D-DATE
                               BY REFERENCE  W-DATE-SYS. 

*-- initiaLIlisation de la date sous la forme binaire    
    CALL "SYS$BINTIM" USING BY DESCRIPTOR W-DATE-ASCII
                            BY REFERENCE  W-DATE-VMS
			    GIVING W-RESULT.

*-- initialisation de la date ZERO sous la forme binaire    
    CALL "SYS$BINTIM" USING BY DESCRIPTOR W-DATE-ZERO
                            BY REFERENCE  W-DATE-NULL
			    GIVING W-RESULT.

*-- initialisation de la date sous la forme jj/mm/aaaa
    MOVE W-DATE-VMS TO PARAM-DATE-BIN.
    CALL "D00_YYYYMMDD" USING PARAM-DATE-BIN PARAM-DATE-TXT.

    EXEC SQL 
       SET TRANSACTION READ ONLY
    END-EXEC.


    PERFORM OPEN-FILES.

    IF P-CODREP = "R"
       CALL "SYS$BINTIM" USING BY DESCRIPTOR P-DATDEB
                               BY REFERENCE  W-DATDEB
                               GIVING W-RESULT
       CALL "SYS$BINTIM" USING BY DESCRIPTOR P-DATFIN
                               BY REFERENCE  W-DATFIN
                               GIVING W-RESULT
    ELSE
*------ recherche de la date debut selection commandes 
       PERFORM FIND-LAST-DATE

       MOVE W-DATBIN           TO W-LAST-DATE
       display " W-LAST-DATE : " W-LAST-DATE   WITH CONVERSION
    END-IF.



    PERFORM TRAITEMENT.

    PERFORM CLOSE-FILES.


    EXEC SQL
    	ROLLBACK
    END-EXEC.
*
FIN_TRT.
    STOP RUN.



TRAITEMENT.
* --- date trt
    DISPLAY "TRAITEMENT".
    MOVE W-D-DATE  TO ENREG-DATPOR
    MOVE ENREG_MAJ TO LIGNE-MAJ
    WRITE LIGNE-MAJ

* ---------- commandes en statut FAT ------------------------------


* FORMATAGE ORDERS

    INITIALIZE ENREG-TRANSMIT.
    MOVE "DEBCDE" TO ENR-TYPMES.
    MOVE SPACES   TO ENR-FILLER.
    MOVE "183 CSP                           " to ENR-DEBCDE-EMETEUR.
    MOVE "183 CSP                           " to ENR-DEBCDE-RECEPTE.
*bt : flag test/prod
    MOVE "P"      TO ENR-DEBCDE-TEST.
    MOVE SPACES   TO ENR-DEBCDE-FILLER.
    MOVE  ENREG-TRANSMIT TO ENREG-TRANSMITCO ENREG-TRANSMITMO.
    WRITE ENREG-TRANSMITCO
    WRITE ENREG-TRANSMITMO


    MOVE 0 TO NB-CDE
    DISPLAY "TIME OPEN CURDIV : "
    CALL "LIB$SPAWN" USING BY DESCRIPTOR "SH TIME"
    MOVE "N" TO FLAG-FIN-RECHERCHE-DIV.
    DISPLAY "P-CODLAB : " P-CODLAB.
    PERFORM OPEN-CURSOR-DIV.
    PERFORM RECHERCHE-DIV THRU RECHERCHE-DIV-FIN UNTIL FIN-RECHERCHE-DIV.
    PERFORM CLOSE-CURSOR-DIV.


* FORMATAGE ORDERS

    INITIALIZE ENREG-TRANSMIT.
    MOVE "FINMES" TO ENR-TYPMES.
    MOVE SPACES   TO ENR-FILLER.
    MOVE NB-CDE-CO    TO ENR-FINMES-NBCDE.
    MOVE  ENREG-TRANSMIT TO ENREG-TRANSMITCO.
    WRITE ENREG-TRANSMITCO

    MOVE NB-CDE-MO    TO ENR-FINMES-NBCDE.
    MOVE  ENREG-TRANSMIT TO ENREG-TRANSMITMO.
    WRITE ENREG-TRANSMITMO.


RECHERCHE-DIV.
    DISPLAY "RECHERCHE-DIV:" W-CODLAB
EXEC SQL
       FETCH CURDIV INTO :W-CODLAB
END-EXEC.           
           DISPLAY "W-CODLAB : " W-CODLAB
           EVALUATE SQLSTATE
	   WHEN  '02000' 
	      MOVE "O" TO FLAG-FIN-RECHERCHE-DIV
	   WHEN  '00000' THRU '01999'
                 PERFORM LECTURE-LAB
*                PERFORM LECTURE-LABLAB
                 MOVE "N" TO FLAG-FIN-CDE

    DISPLAY "TIME OPEN CURCDE : "
    CALL "LIB$SPAWN" USING BY DESCRIPTOR "SH TIME"
                 IF P-CODREP = "R"
                    PERFORM OPEN-CURSOR-CDE-R
                    PERFORM LECTURE-DES-CDES-R UNTIL FIN-CDE
                    PERFORM CLOSE-CURSOR-CDE-R
                 ELSE
                    PERFORM OPEN-CURSOR-CDE
                    PERFORM LECTURE-DES-CDES UNTIL FIN-CDE
                    PERFORM CLOSE-CURSOR-CDE
                 END-IF
    	   WHEN  '22002'    
                 PERFORM LECTURE-LAB
*                PERFORM LECTURE-LABLAB
                 MOVE "N" TO FLAG-FIN-CDE
    DISPLAY "TIME OPEN CURCDE : "
    CALL "LIB$SPAWN" USING BY DESCRIPTOR "SH TIME"
                 IF P-CODREP = "R"
                    PERFORM OPEN-CURSOR-CDE-R
                    PERFORM LECTURE-DES-CDES-R UNTIL FIN-CDE
                    PERFORM CLOSE-CURSOR-CDE-R
                 ELSE
                    PERFORM OPEN-CURSOR-CDE
                    PERFORM LECTURE-DES-CDES UNTIL FIN-CDE
                    PERFORM CLOSE-CURSOR-CDE
                 END-IF
           WHEN  '02001' THRU '22001'
              MOVE "2019" TO NUM-MSG        
              MOVE "FETCH DU CURSEUR CURDIV" TO ANO_MESSAGE
              PERFORM FIN-ANORMALE           
           WHEN  '22003' THRU 'S9999'
              MOVE "2019" TO NUM-MSG        
              MOVE "FETCH DU CURSEUR CURDIV" TO ANO_MESSAGE
              PERFORM FIN-ANORMALE           
        END-EVALUATE.

RECHERCHE-DIV-FIN.       
	EXIT.
*


*
*
* ACQUISITION DE LA DATE DERNIERE TRANSMISSION.
*----------------------------------------------------------------------*

FIND-LAST-DATE.
    DISPLAY "FIND-LAST-DATE".
    MOVE "N" TO FLAG-TROUVE.
    MOVE P-CODLAB TO WCODLAB.
    MOVE WARGUM TO WWARGUM.
    EXEC SQL
    SELECT FONCT INTO :WFONCT FROM D.PAR
           WHERE CODENT = 'LAB'    AND
                 CHAMPS = 'DATRAS' AND ARGUM = :WWARGUM
    END-EXEC.
    MOVE '2004' TO NUM-MSG.
    MOVE 'ERREUR LECTURE PAR FIND-LAST-DATE' TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

           IF TROUVE
                 MOVE WFONCT TO W-FONCT 
                 MOVE W-JJ TO FONCT-JJ
                 MOVE W-SI TO FONCT-SI
                 MOVE W-AA TO FONCT-AA
                 EVALUATE W-MM
                    WHEN 1
                      MOVE 'JAN' TO FONCT-MM
                    WHEN 2
                      MOVE 'FEB' TO FONCT-MM
                    WHEN 3
                      MOVE 'MAR' TO FONCT-MM
                    WHEN 4
                      MOVE 'APR' TO FONCT-MM
                    WHEN 5
                      MOVE 'MAY' TO FONCT-MM
                    WHEN 6
                      MOVE 'JUN' TO FONCT-MM
                    WHEN 7
                      MOVE 'JUL' TO FONCT-MM
                    WHEN 8
                      MOVE 'AUG' TO FONCT-MM
                    WHEN 9
                      MOVE 'SEP' TO FONCT-MM
                    WHEN 10
                      MOVE 'OCT' TO FONCT-MM
                    WHEN 11
                      MOVE 'NOV' TO FONCT-MM
                    WHEN 12
                      MOVE 'DEC' TO FONCT-MM
                 END-EVALUATE
	         MOVE W-HEURE TO FONCT-HEURE
                 DISPLAY "TRT RAS FONCTION = " W-FONCTION 
                 CALL "SYS$BINTIM" USING BY DESCRIPTOR W-FONCTION
                                         BY REFERENCE  W-DATBIN
                                         GIVING W-RESULT
                 IF CODE-STATUS NOT = "00"      
		   display "RMS-MAJ " code-status
		   MOVE "1003" TO NUM-MSG
	           MOVE "RMS_MAJ" TO PARAM-01
                   GO TO FIN-ANOFERME
                 END-IF
           ELSE
              DISPLAY "PAS DE DATRAS DANS PAR POUR LE LABO " P-CODLAB
	      PERFORM CREAT-DATRAS
           END-IF.
           
CREAT-DATRAS.
    DISPLAY "CREAT-DATRAS".
    EXEC SQL
    	ROLLBACK
    END-EXEC.
    EXEC SQL 
       SET TRANSACTION READ WRITE 
           RESERVING D.PAR FOR SHARED WRITE
    END-EXEC.
    MOVE "LAB"    TO W-CODENT.
    MOVE "DATRAS" TO W-CHAMPS.
    MOVE "SYS"    TO W-CODOPE.
    MOVE P-CODLAB TO WCODLAB.
    MOVE WARGUM  TO WWARGUM.
    MOVE W-DATE-SYS TO W-DATCRE.
    MOVE W-DATE-SYS TO W-DATMAJ.
    MOVE W-d-date   TO W-FONCTION.
    CALL "SYS$BINTIM" USING BY DESCRIPTOR W-FONCTION
			   BY REFERENCE  W-DATBIN
			   GIVING W-RESULT.
    MOVE FONCT-JJ TO W-JJ
    MOVE FONCT-SI TO W-SI
    MOVE FONCT-AA TO W-AA
    MOVE FONCT-HEURE TO W-HEURE
    EVALUATE FONCT-MM
      WHEN 'JAN'
        MOVE 01 TO W-MM
      WHEN 'FEB'
        MOVE 02 TO W-MM
      WHEN 'MAR'
        MOVE 03 TO W-MM
      WHEN 'APR'
        MOVE 04 TO W-MM
      WHEN 'MAY'
        MOVE 05 TO W-MM
      WHEN 'JUN'
        MOVE 06 TO W-MM
      WHEN 'JUL'
        MOVE 07 TO W-MM
      WHEN 'AUG'
        MOVE 08 TO W-MM
      WHEN 'SEP'
        MOVE 09 TO W-MM
      WHEN 'OCT'
        MOVE 10 TO W-MM
      WHEN 'NOV'
        MOVE 11 TO W-MM
      WHEN 'DEC'
        MOVE 12 TO W-MM
     END-EVALUATE.
     MOVE '-' TO TIRET1 TIRET2
     MOVE W-FONCT TO W-FONCT1
*     DISPLAY "MODIFY  " WFONCT


    EXEC SQL
    INSERT INTO  D.PAR 
    (    CODENT,    FONCT,    DATCRE,    DATMAJ,    CODOPE,
         CHAMPS,    ARGUM )
    VALUES
    ( :W-CODENT, 
      :W-FONCT1, :W-DATCRE, :W-DATMAJ, :W-CODOPE, 
      :W-CHAMPS,  :WWARGUM  )
    END-EXEC.
    MOVE "2026" TO NUM-MSG.
    MOVE "PB INSERT  SUR TABLE PAR" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.
    EXEC SQL
    	COMMIT
    END-EXEC.
    EXEC SQL 
       SET TRANSACTION READ ONLY
    END-EXEC.




*----------------------------------------------------------------------*
* TRAITEMENT DES COMMANDES
*----------------------------------------------------------------------*
LECTURE-DES-CDES.
    DISPLAY "LECTURE-DES-CDES".
    EXEC SQL
    FETCH CURCDE INTO :CDE.NUMCDE,:CDE.NUMRAL,:W-STATUT,
		      :CDE.TYPNFA,
		      :CDE.QUANTA,
		      :CDE.MOISFA,
		      :CDE.CPTFAC,
		      :w-CPTFAC,
                      :CDE.GENCLILAB indicator :i1,
		      :CDE.CLICSP,                     
		      :CDE.TYPCDE,                     
		      :CDE.CDELAB,                     
                      :CDE.NBSTD,:CDE.NBDTL, 
                      :CDE.LIVTOT, 
                      :CDE.PDSSTD,:CDE.PDSDTL,  
                      :CDE.VOLSTD,:CDE.VOLDTL,
                      :CDE.DATCDE,
                      :CDE.DATEBL INDICATOR :IDATEBL  ,
                      :w-refcde, :CDE.RESVIS indicator :i1,
                      :CDE.CLILAB, :CDE.TYPCDELAB indicator :i1,
                      :CDE.CODSTR INDICATOR :ICODSTR,
                      :CDE.TYPNFA INDICATOR :ITYPNFA,
                      :CDE.QUANTA INDICATOR :IQUANTA,
                      :CDE.MOISFA INDICATOR :IMOISFA,
                      :CDE.CPTFAC INDICATOR :ICPTFAC,
                      :CDE.CODREP INDICATOR :ICODREP,
                      :CDO.RAISOCL,
                      :CDO.NOMLIV,
                      :CDO.ADR1L,
                      :CDO.ADR2L,
                      :CDO.VILLEL,
                      :CDO.CPOSTL,
                      :CDE.DATSAI ,
                      :CDE.CODOPE ,
                      :CDE.ADELI,
                      :CDE.CDESAISIE,
                      :CDE.DATREC INDICATOR :IDATREC,
                      :CDE.TRAFIC,
                      :CDE.DATEBP INDICATOR :IDATEBP,
                      :CDE.CODSAI INDICATOR :ICODSAI,
                      :CDE.CODMES,
                      :CDE.STREXP INDICATOR :ISTREXP,
                      :CDE.CODLABLAB INDICATOR :ICODLABLAB

    END-EXEC.
    MOVE "2006" TO NUM-MSG.
    MOVE "FETCH CURSOR CDE" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.
    DISPLAY "NUMERO CDE " NUMCDE IN CDE with conversion
    IF TROUVE
       PERFORM TRT-TROUVE
      ELSE
       MOVE "O" TO FLAG-FIN-CDE
    END-IF.

*----------------------------------------------------------------------*
* TRAITEMENT DES COMMANDES
*----------------------------------------------------------------------*
LECTURE-DES-CDES-R.
    EXEC SQL
    FETCH CURCDE_R INTO :CDE.NUMCDE,:CDE.NUMRAL,:W-STATUT,
		      :CDE.TYPNFA,
		      :CDE.QUANTA,
		      :CDE.MOISFA,
		      :CDE.CPTFAC,
		      :w-CPTFAC,
                      :CDE.GENCLILAB indicator :i1,
		      :CDE.CLICSP,                     
		      :CDE.TYPCDE,                     
		      :CDE.CDELAB,                     
                      :CDE.NBSTD,:CDE.NBDTL, 
                      :CDE.LIVTOT, 
                      :CDE.PDSSTD,:CDE.PDSDTL,  
                      :CDE.VOLSTD,:CDE.VOLDTL,
                      :CDE.DATCDE,
                      :CDE.DATEBL INDICATOR :IDATEBL  ,
                      :w-refcde, :CDE.RESVIS indicator :i1,
                      :CDE.CLILAB, :CDE.TYPCDELAB indicator :i1,
                      :CDE.CODSTR INDICATOR :ICODSTR,
                      :CDE.TYPNFA INDICATOR :ITYPNFA,
                      :CDE.QUANTA INDICATOR :IQUANTA,
                      :CDE.MOISFA INDICATOR :IMOISFA,
                      :CDE.CPTFAC INDICATOR :ICPTFAC,
                      :CDE.CODREP INDICATOR :ICODREP,
                      :CDO.RAISOCL,
                      :CDO.NOMLIV,
                      :CDO.ADR1L,
                      :CDO.ADR2L,
                      :CDO.VILLEL,
                      :CDO.CPOSTL,
                      :CDE.DATSAI,
                      :CDE.CODOPE,
                      :CDE.ADELI,
                      :CDE.CDESAISIE,
                      :CDE.DATREC,
                      :CDE.TRAFIC,
                      :CDE.DATEBP INDICATOR :IDATEBP,
                      :CDE.CODSAI INDICATOR :ICODSAI,
                      :CDE.CODMES,
                      :CDE.STREXP INDICATOR :ISTREXP,
                      :CDE.CODLABLAB INDICATOR :ICODLABLAB

    END-EXEC.
    MOVE "2006" TO NUM-MSG.
    MOVE "FETCH CURSOR CDE" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.
    DISPLAY "NUMERO CDE " NUMCDE IN CDE with conversion
    IF TROUVE
       PERFORM TRT-TROUVE
      ELSE
       MOVE "O" TO FLAG-FIN-CDE
    END-IF.


TRT-TROUVE.
       DISPLAY "TRT-TROUVE".
       MOVE CLILAB IN CDE TO W-CLILAB

       MOVE CODREP IN CDE TO W-CODREP
       MOVE CODLABLAB IN CDE TO W-CODLABLAB

*       display "---> Commande "W-NUMCDE W-NUMRAL
*       display "**w-clilab " w-clilab 
       MOVE NUMCDE IN CDE TO W-NUMCDE
       MOVE CDELAB IN CDE TO W-CDELAB
       MOVE NUMRAL IN CDE TO W-NUMRAL
       MOVE RESVIS IN CDE TO W-RESVIS
       MOVE TYPCDELAB IN CDE TO W-TYPCDELAB

         MOVE NUMCDE IN CDE TO WP-NUMCDE
         MOVE NUMRAL IN CDE TO WP-NUMRAL
         MOVE 0             TO W-MSG
         MOVE SPACES        TO WP-NUMEXP
*         display "traitement commande avant : " PARAM-BASNUMCDE
         CALL "D00_NUMEXP" USING PARAM-BASNUMCDE
         IF W-MSG = 0
            MOVE WP-NUMEXP     TO W-NUMEXP
         END-IF

       PERFORM LECTURE-CLI
       PERFORM RECHERCHE-MES-BL-RT.

       PERFORM TRAIT-FAT.


RECHERCHE-HORAIRE.
    MOVE SPACES TO W-LIB-HORAIRE
    EXEC SQL
      SELECT LIBELL INTO :W-LIB-HORAIRE
      FROM D.CDL
       WHERE NUMCDE = :W-NUMCDE 
       AND   NUMRAL = :W-NUMRAL
       AND   CODART = 'QU00013'
    END-EXEC.
    MOVE "2006" TO NUM-MSG.
    MOVE "LECTURE CDL HORAIRE" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

RECHERCHE-CONTACT.
    MOVE SPACES TO W-LIB-CONTACT
    EXEC SQL
      SELECT LIBELL INTO :W-LIB-CONTACT
      FROM D.CDL
       WHERE NUMCDE = :W-NUMCDE 
       AND   NUMRAL = :W-NUMRAL
       AND   CODART = 'QU000132'
    END-EXEC.
    MOVE "2006" TO NUM-MSG.
    MOVE "LECTURE CDL HORAIRE" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.


*----------------------------------------------------------------------*
* RECHERCHE MES BL/RT                                                  *
*----------------------------------------------------------------------*
RECHERCHE-MES-BL-RT.
    INITIALIZE MES.
    MOVE "O" TO FLAG-TROUVE.
    MOVE "N" TO FLAG-BL-TROUVE.
    MOVE "N" TO FLAG-RT-TROUVE.
    MOVE SPACES TO W-COMMENT.
    EXEC SQL
    SELECT    CODMES,      CODENT,      TYPDOC,      COMMENT 
    INTO :MES.CODMES, :MES.CODENT, :MES.TYPDOC, :MES.COMMENT
    FROM D.MES
    WHERE TYPDOC = 'BL' AND CODMES = :CDE.CODMES	
    END-EXEC.
    EVALUATE SQLSTATE
       WHEN  '02000' 
          MOVE "N" TO FLAG-TROUVE
       WHEN  '02001' THRU '22001'
          MOVE "2003" TO NUM-MSG        
          MOVE "PB RECHERCHE SUR MES" TO ANO_MESSAGE
          PERFORM FIN-ANORMALE           
       WHEN  '22003' THRU 'S9999'
          MOVE "2003" TO NUM-MSG        
          MOVE "PB RECHERCHE SUR MES" TO ANO_MESSAGE
          PERFORM FIN-ANORMALE           
    END-EVALUATE.
    IF TROUVE MOVE "O" TO FLAG-BL-TROUVE.
    IF TROUVE 
       MOVE COMMENT IN MES TO W-COMMENT
    END-IF
    MOVE "O" TO FLAG-TROUVE.
    EXEC SQL
    SELECT    CODMES,      CODENT,      TYPDOC,      COMMENT 
    INTO :MES.CODMES, :MES.CODENT, :MES.TYPDOC, :MES.COMMENT
    FROM D.MES
    WHERE TYPDOC = 'RT' AND CODMES = :CDE.CODMES 
    ORDER BY NUMLIG LIMIT 1	
    END-EXEC.
    EVALUATE SQLSTATE
       WHEN  '02000' 
          MOVE "N" TO FLAG-TROUVE
       WHEN  '02001' THRU '22001'
          MOVE "2003" TO NUM-MSG        
          MOVE "PB RECHERCHE SUR MES" TO ANO_MESSAGE
          PERFORM FIN-ANORMALE           
       WHEN  '22003' THRU 'S9999'
          MOVE "2003" TO NUM-MSG        
          MOVE "PB RECHERCHE SUR MES" TO ANO_MESSAGE
          PERFORM FIN-ANORMALE           
    END-EVALUATE.
    IF TROUVE AND NOT BL-TROUVE
       MOVE COMMENT IN MES TO W-COMMENT
    END-IF.

RECHERCHE-MES-BL-RT-FIN.       
    EXIT.

*----------------------------------------------------------------------*
* TRAITEMENT DES LIGNES COMMANDES
*----------------------------------------------------------------------*
LECTURE-DES-CDL.
*    display " LECTURE-DES-CDL"
    EXEC SQL
    FETCH CURCDL INTO :CDL.NUMCDE, :CDL.NUMRAL,
		      :CDL.codart, :CDL.NUMLIG, :CDL.REFLIG, :CDL.LOTFAB,
	              :CDL.QTCCDE, :CDL.QTCECH, :CDL.QTCGRT,
	              :CDL.QTLCDE, :CDL.QTLECH, :CDL.QTLGRT, :CDL.LIVTOT,
                      :CDL.MENSPE,:CDL.ARTSAI,:CDL.CODDEP,:CDL.CODLAB
    END-EXEC.
    MOVE "2006" TO NUM-MSG.
    MOVE "FETCH CURSOR CDL" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.
    IF TROUVE
       PERFORM TRT-TROUVE-CDL
    ELSE
       PERFORM TRT-NOT-TROUVE-CDL
    END-IF.



TRT-TROUVE-CDL.


* GESTION VIATRIS
       IF CODLAB IN CDL = "2951"
          PERFORM LECTURE-DEPLAB
       END-IF



       IF CODDEP IN CDL = "MO"
          ADD 1 TO W-NB-CDL-MO
       END-IF
       IF CODDEP IN CDL = "CO"
          ADD 1 TO W-NB-CDL-CO
       END-IF

       MOVE CODART IN CDL TO CDL-CODART
       MOVE NUMLIG IN CDL TO CDL-NUMLIG

       IF W-NUMLIG NOT = CDL-NUMLIG OR W-CODART NOT = CDL-CODART
          IF W-NUMLIG NOT = SPACES OR W-CODART NOT = SPACES
             PERFORM TRT-CDL-CUMULEE
          END-IF
          PERFORM INIT-QTE
          MOVE QTCCDE IN CDL TO W-QTCCDEL
          MOVE CDL-CODART TO W-CODART
          MOVE CDL-NUMLIG TO W-NUMLIG
          MOVE LIVTOT IN CDL TO W-CDLLIVTOT
          MOVE REFLIG IN CDL TO W-REFLIG
          MOVE MENSPE IN CDL TO W-MENSPE
          MOVE LOTFAB IN CDL TO W-LOTFAB
          MOVE QTLCDE IN CDL TO W-QTLCDE
          MOVE ARTSAI IN CDL TO W-ARTSAI
          MOVE CODDEP IN CDL TO W-CDL-CODDEP
          MOVE CODLAB IN CDL TO W-CDL-CODLAB
       END-IF
       COMPUTE W-QTECDE = W-QTECDE + QTCCDE IN CDL  
       COMPUTE W-QTETOT = W-QTETOT + QTLCDE IN CDL 
       COMPUTE W-QTRCDE = W-QTRCDE + QTRCDE IN CDL

       COMPUTE W-QTCGRT = W-QTCGRT + QTCGRT IN CDL+ QTCECH IN CDL.


TRT-NOT-TROUVE-CDL.

       MOVE CDL-CODART TO W-CODART        
       MOVE CDL-NUMLIG TO W-NUMLIG
       MOVE QTCCDE IN CDL TO W-QTCCDEL
       MOVE REFLIG IN CDL TO W-REFLIG
       MOVE MENSPE IN CDL TO W-MENSPE
       MOVE LOTFAB IN CDL TO W-LOTFAB
       MOVE ARTSAI IN CDL TO W-ARTSAI
       MOVE CODDEP IN CDL TO W-CDL-CODDEP
       MOVE CODLAB IN CDL TO W-CDL-CODLAB
       PERFORM TRT-CDL-CUMULEE
       MOVE "O" TO FLAG-FIN-CDL.



TRT-CDL-CUMULEE.
    PERFORM LECTURE-ART.
       ADD 1 TO NB-CDL
       MOVE "1"                       TO      TCDL-CODENR (NB-CDL).
       MOVE W-ARTlab                  TO      TCDL-artlab (NB-CDL).
       MOVE LIBELL IN ART             TO      TCDL-LIBELL (NB-CDL).
       MOVE W-CODART                  TO      TCDL-codart (NB-CDL).
       MOVE W-ARTcip                  TO      TCDL-ARTCIP (NB-CDL).
       MOVE SPACES                    TO      TCDL-LOTFAB (NB-CDL)
       IF W-REFLIG(1:4) NOT = SPACES
          MOVE W-REFLIG(1:4)             TO      TCDL-NUMLIG (NB-CDL)
       ELSE 
          MOVE W-NUMLIG                TO       TCDL-NUMLIG (NB-CDL)
       END-IF
       MOVE W-QTETOT                  TO      TCDL-QTELIV (NB-CDL)
       MOVE W-QTETOT                  TO      W-VERIFQTE-CDE.         
       MOVE W-QTECDE                  TO      TCDL-QTECDE (NB-CDL)

       MOVE W-QTCGRT                  TO      TCDL-QTCGRT (NB-CDL)

       MOVE W-LOTFAB                  TO      TCDL-LOTFAB (NB-CDL)
       MOVE W-ARTSAI                  TO      TCDL-ARTSAI (NB-CDL)
       MOVE W-CDL-CODLAB              TO      TCDL-CODLAB (NB-CDL)
       MOVE W-CDL-CODDEP              TO      TCDL-CODDEP (NB-CDL)
       MOVE 0                         TO      W-VERIFQTE-CLL.

       ADD W-QTETOT TO W-QTELIVT W-QTELIVC.

       PERFORM TRAITEMENT-SANS-LOT.

INIT-QTE.
    MOVE 0 TO W-QTERAL.
    MOVE 0 TO W-QTECDE.
    MOVE 0 TO W-QTRCDE.
    MOVE 0 TO W-QTETOT.
    MOVE 0 TO W-QTCGRT.
    MOVE 0 TO W-QTELIV.
    MOVE 0 TO W-QTERALM.



*----------------------------------------------------------------------*
TRAIT-FAT.
*----------------------------------------------------------------------*
       MOVE CDELAB IN CDE TO W-CDELAB.
       MOVE "N" TO FLAG-FIN-CDL
       MOVE 0 TO NB-CDL
       MOVE SPACES TO W-NUMLIG
       MOVE SPACES TO W-CODART
       MOVE 0 TO W-NB-CDL-CO  W-NB-CDL-MO
          PERFORM OPEN-CURSOR-CDL
          PERFORM LECTURE-DES-CDL UNTIL FIN-CDL
          PERFORM CLOSE-CURSOR-CDL
       PERFORM RECHERCHE-HORAIRE
       PERFORM RECHERCHE-CONTACT
       PERFORM STOCK-FIC-DESADV.


TRAITEMENT-SANS-LOT.
    IF W-GESLOT NOT = "4" AND W-GESLOT NOT = "5"
       MOVE SPACES TO W-LOTFAB
    END-IF.
    ADD 1 TO NB-CDL.
    MOVE W-REFLIG(1:4) TO TCDL-NUMLIG(NB-CDL).
    IF W-REFLIG(1:4) NOT = SPACES 
          MOVE W-REFLIG(1:4) TO TCDL-NUMLIG(NB-CDL) 
    ELSE
          MOVE W-NUMLIG TO W-REFLIG(1:4)
          MOVE W-REFLIG(1:4) TO TCDL-NUMLIG(NB-CDL) 
    END-IF
    MOVE W-ARTLAB      TO TCDL-ARTLAB(NB-CDL).
    MOVE LIBELL IN ART TO TCDL-LIBELL(NB-CDL).
    MOVE W-CODART      TO TCDL-CODART(NB-CDL).
    MOVE W-ARTCIP      TO TCDL-ARTCIP(NB-CDL).
    MOVE W-LOTFAB      TO TCDL-LOTFAB(NB-CDL).
    MOVE W-ARTSAI      TO TCDL-ARTSAI(NB-CDL).
    MOVE W-CDL-CODDEP  TO TCDL-CODDEP(NB-CDL).
    MOVE W-CDL-CODLAB  TO TCDL-CODLAB(NB-CDL).
    MOVE W-QTETOT      TO TCDL-QTELIV(NB-CDL).
    MOVE W-CODLABLAB   TO TCDL-CODLABLAB(NB-CDL).
    MOVE W-QTETOT      TO WS-QTELIV.

    MOVE W-QTCGRT      TO TCDL-QTCGRT(NB-CDL).

    MOVE "3"           TO TCDL-CODENR (NB-CDL)
    ADD W-QTETOT       TO W-VERIFQTE-CLL. 



*-------------------------------------------------------------------------*
* ECRITURE DES DONNEES DANS LE FICHIER DE TRANSMISSION
*-------------------------------------------------------------------------*
STOCK-FIC-DESADV.
        MOVE SPACES        TO ENREG_MAJ_CDE
        MOVE NUMCDE IN CDE TO ENREG-NUMCDE 
        MOVE NUMRAL IN CDE TO ENREG-NUMRAL
        MOVE CDELAB IN CDE TO ENREG-CDELAB
        MOVE ENREG_MAJ TO LIGNE-MAJ
        WRITE LIGNE-MAJ
	IF CODE-STATUS NOT = "00"
		display "RMS-MAJ " code-status
		MOVE "1003" TO NUM-MSG
		MOVE "RMS_MAJ" TO PARAM-01
		GO TO FIN-ANOFERME.




    MOVE ALL "0" TO FIC-ENVOI.    
    INITIALIZE FIC-TYPE1.
    MOVE "0"  TO FIC-TYPENR
    MOVE NUMCDE in cde TO FIC-NUMCDE
    MOVE NUMRAL in cde TO FIC-NUMRAL
    MOVE DATCDE  IN CDE TO W-DATE-RDB 
    CALL "SYS$ASCTIM" USING
                      BY REFERENCE  W-LENGTH
                      BY DESCRIPTOR W-DATE-ASCII
                      BY REFERENCE  W-DATE-RDB
                      BY VALUE      W-FLAG.

    MOVE W-DATE-ASCII(8:4) TO FIC-DATCDE(1:4) 
    MOVE W-DATE-ASCII(1:2) TO FIC-DATCDE(7:2) 

    PERFORM CVT-MOIS 
    MOVE W-MOIS-NUM        TO FIC-DATCDE(5:2).
    INSPECT FIC-DATCDE REPLACING ALL SPACES BY "0". 


    MOVE DATSAI  IN CDE TO W-DATE-RDB 
    CALL "SYS$ASCTIM" USING
                      BY REFERENCE  W-LENGTH
                      BY DESCRIPTOR W-DATE-ASCII
                      BY REFERENCE  W-DATE-RDB
                      BY VALUE      W-FLAG.

    MOVE W-DATE-ASCII(8:4) TO FIC-DATSAI(1:4) 
    MOVE W-DATE-ASCII(1:2) TO FIC-DATSAI(7:2) 

    PERFORM CVT-MOIS 
    MOVE W-MOIS-NUM        TO FIC-DATSAI(5:2).
    INSPECT FIC-DATSAI REPLACING ALL SPACES BY "0". 

    MOVE DATREC  IN CDE TO W-DATE-RDB.
    CALL "SYS$ASCTIM" USING
                      BY REFERENCE  W-LENGTH
                      BY DESCRIPTOR W-DATE-ASCII
                      BY REFERENCE  W-DATE-RDB
                      BY VALUE      W-FLAG.

    MOVE W-DATE-ASCII TO FIC-DATREC.
    DISPLAY "DATREC " W-DATE-ASCII
    DISPLAY "FIC-DATREC  " FIC-DATREC

    MOVE W-NUMCDE TO FIC-NUMCDE
    MOVE W-NUMRAL TO FIC-NUMRAL
    MOVE W-CODLAB TO FIC-CODLAB. 

    IF IDATEBL = -1 
       MOVE '00000000' TO FIC-DATEBL
       MOVE PARAM-DATE-TXT TO FIC-DATEBL
    ELSE
       MOVE DATEBL  IN CDE TO W-DATE-RDB 
       CALL "SYS$ASCTIM" USING
                      BY REFERENCE  W-LENGTH
                      BY DESCRIPTOR W-DATE-ASCII
                      BY REFERENCE  W-DATE-RDB
                      BY VALUE      W-FLAG
       MOVE W-DATE-ASCII(8:4) TO FIC-DATEBL(1:4) 
       MOVE W-DATE-ASCII(1:2) TO FIC-DATEBL(7:2) 
       PERFORM CVT-MOIS 
       MOVE W-MOIS-NUM        TO FIC-DATEBL(5:2)
       INSPECT FIC-DATEBL REPLACING ALL SPACES BY "0"
    END-IF.


    IF IDATEBP = -1 
       MOVE '00000000' TO FIC-DATEBP
       MOVE PARAM-DATE-TXT TO FIC-DATEBP
    ELSE
       MOVE DATEBP  IN CDE TO W-DATE-RDB 
       CALL "SYS$ASCTIM" USING
                      BY REFERENCE  W-LENGTH
                      BY DESCRIPTOR W-DATE-ASCII
                      BY REFERENCE  W-DATE-RDB
                      BY VALUE      W-FLAG
       MOVE W-DATE-ASCII(8:4) TO FIC-DATEBP(1:4) 
       MOVE W-DATE-ASCII(1:2) TO FIC-DATEBP(7:2) 
       PERFORM CVT-MOIS 
       MOVE W-MOIS-NUM        TO FIC-DATEBP(5:2)
       INSPECT FIC-DATEBP REPLACING ALL SPACES BY "0"
    END-IF.

    move typcde in cde          to fic-typcde.
    move typnfa in cde          to fic-numfac(1:2).
    move quanta in cde          to fic-numfac(3:1).
    move moisfa in cde          to fic-numfac(4:2).
    move w-cptfac               to fic-numfac(6:5).
    iNSPECT fic-numfac  REPLACING ALL SPACE BY ZERO.

    MOVE CDELAB in cde TO FIC-CDELAB.

    IF W-NUMCDE = FIC-CDELAB(1:7)
       MOVE SPACE    TO FIC-CODREP 
    ELSE
       MOVE W-CODREP TO FIC-CODREP
    END-IF.

    move zero to w-nbcol
    add nbstd in cde nbdtl in cde to w-nbcol
    move  w-nbcol  to fic-nbcol
*
    move zero to w-pds   
    add pdsstd in cde pdsdtl in cde to w-pds
    move  w-pds    to fic-pds
*
    move 'KG' to fic-unite
    move w-statut to fic-statut
*
    move zero to w-vol   
    add volstd in cde voldtl in cde to w-vol
    move  w-vol    to fic-vol
*
    perform lecture-ccl.
    move clilab in ccl to fic-clilab2.
*    display "**clilab in ccl** " fic-clilab2.
*
    move clicsp in cde to fic-clilab.
    inspect fic-clilab-x replacing all spaces by '0'. 
*
    perform lecture-cdo.
    move raisocl in cdo to fic-raisocl.
    move nomliv in cdo to fic-nomliv.
    move adr1l  in cdo to fic-adr1l. 
    move adr2l  in cdo to fic-adr2l. 
    move villel in cdo to fic-villel.
    move cpostl in cdo to fic-cpostl.
    perform get-cli-cde.
    perform get-cli-pay.
    move telep in cli  to fic-telep.



* formatage ORDERS :

    IF W-NB-CDL-CO > 0
       ADD 1 TO NB-CDE-CO    
    END-IF
    IF W-NB-CDL-MO > 0
       ADD 1 TO NB-CDE-MO  
    END-IF
    MOVE SPACES TO PREV-CODLAB
    MOVE  0  TO IND-CDL W-SUMQTE.
    PERFORM STOCK-LIG-DESADV UNTIL IND-CDL = NB-CDL .

    PERFORM WRITE-FINCDE.

WRITE-REFCDE.
    INITIALIZE ENREG-TRANSMIT.
    MOVE "REFCDE" TO ENR-TYPMES.
    MOVE SPACES   TO ENR-FILLER.
    MOVE FIC-DATCDE(3:6) TO ENR-REFCDE-DATCDE.
*    MOVE FIC-NUMCDE      TO ENR-REFCDE-REFCDE(1:7).
*    MOVE FIC-NUMRAL      TO ENR-REFCDE-REFCDE(8:1).
    MOVE W-REFCDE         TO ENR-REFCDE-REFCDE. 
    MOVE CDELAB IN CDE    TO ENR-REFCDE-CDELAB.
    DISPLAY "REFCDE : " ENR-REFCDE-REFCDE.
    DISPLAY "CDELAB : " ENR-REFCDE-CDELAB.

    MOVE CODSAI IN CDE TO ENR-REFCDE-CODSAI.

    MOVE CODSTR IN CDE TO ENR-REFCDE-CODSTR.
    MOVE STREXP IN CDE TO ENR-REFCDE-STREXP.

    MOVE FIC-DATREC       TO ENR-REFCDE-DATREC.
    MOVE FIC-DATEBP       TO ENR-REFCDE-DATEBP.
    DISPLAY "ENR-REFCDE-DATREC " ENR-REFCDE-DATREC.
*    IF ADELI IN CDE NOT = SPACE
*       MOVE ADELI IN CDE TO W-ADELI
*       MOVE W-ADELI(1:6) TO ENR-REFCDE-CLICIP
*    ELSE
*       MOVE W-CLICIP TO ENR-REFCDE-CLICIP
*    END-IF

    MOVE CLICSP IN CDE TO ENR-REFCDE-CLICSP.

    MOVE CLICSP IN CDE TO ENR-REFCDE-NOMLIV.

    MOVE CODOPE IN CDE TO ENR-REFCDE-CODOPE.
    MOVE CODREP IN CDE TO ENR-REFCDE-CODREP.
    MOVE CDESAISIE IN CDE TO ENR-REFCDE-CDESAISIE
    MOVE ENR-REFCDE-NOMLIV(4:6) TO ENR-REFCDE-NOMLIV.
*    MOVE SPACES     TO ENR-REFCDE-TYPCDE.
    MOVE TYPCDE IN CDE TO ENR-REFCDE-TYPCDE.
    DISPLAY "ENR-REFCDE-TYPCDE : "ENR-REFCDE-TYPCDE.

    IF TRAFIC IN CDE = SPACES 
       MOVE "N" TO ENR-REFCDE-TRAFIC
    ELSE
       MOVE TRAFIC IN CDE TO ENR-REFCDE-TRAFIC
    END-IF.
    DISPLAY "ENR-REFCDE-TRAFIC :*" ENR-REFCDE-TRAFIC "*".

* recherche dans la table DOCENT de BD_PDF le NUMDOC    
    PERFORM RECH-NUMDOC.
    IF TROUVE 
        MOVE W-NUMDOC TO ENR-REFCDE-NUMDOC
    ELSE
        MOVE SPACES   TO ENR-REFCDE-NUMDOC
    END-IF.
    DISPLAY "ENR-REFCDE-NUMDOC :" ENR-REFCDE-NUMDOC.

    MOVE SPACES     TO ENR-REFCDE-FILLER.

    MOVE  ENREG-TRANSMIT TO ENREG-TRANSMITCO ENREG-TRANSMITMO.

    IF TCDL-CODDEP(IND-CDL) = "CO"
       DISPLAY "EVO ENREG-TRANSMITCO :"ENREG-TRANSMITCO
       WRITE ENREG-TRANSMITCO
    END-IF
    IF TCDL-CODDEP(IND-CDL) = "MO"
       DISPLAY "EVO ENREG-TRANSMITMO :"ENREG-TRANSMITMO
       WRITE ENREG-TRANSMITMO
    END-IF.


WRITE-LINTXT.
    INITIALIZE ENREG-TRANSMIT.
    MOVE "LINTXT" TO ENR-TYPMES.
    MOVE SPACES   TO ENR-FILLER.
    MOVE W-COMMENT TO ENR-LINTXT-MESSAGE
    MOVE  ENREG-TRANSMIT TO ENREG-TRANSMITCO ENREG-TRANSMITMO.
    IF TCDL-CODDEP(IND-CDL) = "CO"
       WRITE ENREG-TRANSMITCO
    END-IF
    IF TCDL-CODDEP(IND-CDL) = "MO"
       WRITE ENREG-TRANSMITMO
    END-IF.

WRITE-TXTHOR.
    INITIALIZE ENREG-TRANSMIT.
    MOVE "TXTCDE" TO ENR-TYPMES.
    MOVE SPACES   TO ENR-FILLER.
    MOVE W-LIB-HORAIRE TO ENR-TXTCDE-MESSAGE
    MOVE "RT"     TO ENR-TXTCDE-TYPDOC
    MOVE  ENREG-TRANSMIT TO ENREG-TRANSMITCO ENREG-TRANSMITMO.
    IF TCDL-CODDEP(IND-CDL) = "CO"
       WRITE ENREG-TRANSMITCO
    END-IF
    IF TCDL-CODDEP(IND-CDL) = "MO"
       WRITE ENREG-TRANSMITMO
    END-IF.

WRITE-TXTCON.
    INITIALIZE ENREG-TRANSMIT.
    MOVE "TXTCDE" TO ENR-TYPMES.
    MOVE SPACES   TO ENR-FILLER.
    MOVE W-LIB-CONTACT TO ENR-TXTCDE-MESSAGE
    MOVE "BL"     TO ENR-TXTCDE-TYPDOC
    MOVE  ENREG-TRANSMIT TO ENREG-TRANSMITCO ENREG-TRANSMITMO.
    IF TCDL-CODDEP(IND-CDL) = "CO"
       WRITE ENREG-TRANSMITCO
    END-IF
    IF TCDL-CODDEP(IND-CDL) = "MO"
       WRITE ENREG-TRANSMITMO
    END-IF.

WRITE-FINCDE.
    INITIALIZE ENREG-TRANSMIT.
    MOVE "FINCDE" TO ENR-TYPMES.
    MOVE SPACES   TO ENR-FILLER.
    MOVE W-SUMQTE-CO  TO ENR-FINCDE-SUMQTE
    MOVE W-NB-CDL-CO   TO ENR-FINCDE-NBLIG
    MOVE  ENREG-TRANSMIT TO ENREG-TRANSMITCO.

    IF PREV-CODDEP = "CO"
       WRITE ENREG-TRANSMITCO
    END-IF

    MOVE W-SUMQTE-MO  TO ENR-FINCDE-SUMQTE
    MOVE W-NB-CDL-MO   TO ENR-FINCDE-NBLIG
    MOVE  ENREG-TRANSMIT TO ENREG-TRANSMITMO.
    IF PREV-CODDEP = "MO"
       WRITE ENREG-TRANSMITMO
    END-IF.


STOCK-LIG-DESADV.
    MOVE ALL "0" TO FIC-ENVOI. 

    ADD 1 TO IND-CDL.


    IF TCDL-CODLAB(IND-CDL) NOT= PREV-CODLAB

      IF IND-CDL > 1
         PERFORM WRITE-FINCDE
      END-IF

      MOVE 0 TO W-SUMQTE-CO,  W-SUMQTE-MO
      MOVE 0 TO W-NB-CDL-CO,  W-NB-CDL-MO
      
      PERFORM WRITE-REFCDE
      PERFORM WRITE-LINTXT
      IF W-LIB-HORAIRE NOT = SPACE
         PERFORM WRITE-TXTHOR
      END-IF
      IF W-LIB-CONTACT NOT = SPACE
         PERFORM WRITE-TXTCON
      END-IF
    END-IF


    IF TCDL-CODENR (IND-CDL) = "1"
       MOVE ALL "0" TO FIC-ENVOI    
       INITIALIZE FIC-TYPE2 
       MOVE "1" TO FIC-TYPENR
       MOVE TCDL-NUMLIG(IND-CDL)     TO FIC-REFLIG
       MOVE TCDL-ARTLAB(IND-CDL)     TO FIC-ARTLAB
       MOVE TCDL-QTELIV (IND-CDL)    TO FIC-QTlcde
       MOVE TCDL-QTecde (IND-CDL)    TO FIC-QTccde
    ELSE
       MOVE ALL "0" TO FIC-ENVOI    
       INITIALIZE FIC-TYPE3 
       MOVE "3"                     TO FIC-TYPENR
       MOVE TCDL-NUMLIG(IND-CDL)    TO FIC-REFLIG3
       move TCDL-ARTLAB(IND-CDL) to FIC-ARTLAB3
       MOVE TCDL-QTELIV(IND-CDL) TO QTLCDETP
       MOVE QTLCDETP                TO FIC-QTLCDE3
       MOVE TCDL-LOTFAB(IND-CDL)    TO FIC-LOTFAB  W-LOTFAB
       MOVE TCDL-CODART(IND-CDL)    TO   W-CODART
       MOVE TCDL-CODLABLAB(IND-CDL) TO  W-CODLABLAB

       perform lecture-mvt
       move datper in mvt to w-datper (3:6)
       move "20" to   w-datper (1:2)

       PERFORM FORMAT-ORDERS-CDL

       IF TCDL-CODDEP(IND-CDL) ="CO"
          ADD 1 TO W-NB-CDL-CO
       END-IF
       IF TCDL-CODDEP(IND-CDL) ="MO"
          ADD 1 TO W-NB-CDL-MO
       END-IF

    END-IF
    MOVE TCDL-CODLAB(IND-CDL)    TO PREV-CODLAB.
    MOVE TCDL-CODDEP(IND-CDL)    TO PREV-CODDEP.


FORMAT-ORDERS-CDL.

* formatage ORDERS :


    INITIALIZE ENREG-TRANSMIT.
    MOVE "LINCDE" TO ENR-TYPMES.
    MOVE SPACES   TO ENR-FILLER.
*    MOVE TCDL-ARTSAI(IND-CDL) TO ENR-LINCDE-ARTCIP.
    MOVE TCDL-CODART(IND-CDL) TO ENR-LINCDE-CODART.
    MOVE TCDL-QTELIV(IND-CDL) TO ENR-LINCDE-QTCCDE.

    MOVE TCDL-QTCGRT(IND-CDL) TO ENR-LINCDE-QTCGRT.

    MOVE TCDL-CODLABLAB(IND-CDL) TO ENR-LINCDE-CODLABLAB.

    MOVE SPACES               TO ENR-LINCDE-LIBELL.
    ADD TCDL-QTELIV(IND-CDL) TO W-SUMQTE.

    MOVE  ENREG-TRANSMIT TO ENREG-TRANSMITCO ENREG-TRANSMITMO.
    IF TCDL-CODDEP(IND-CDL) = "CO"
       WRITE ENREG-TRANSMITCO
       ADD TCDL-QTELIV(IND-CDL) TO W-SUMQTE-CO
    END-IF
    IF TCDL-CODDEP(IND-CDL) = "MO"
       WRITE ENREG-TRANSMITMO
       ADD TCDL-QTELIV(IND-CDL) TO W-SUMQTE-MO
    END-IF.

          

*-------------------------------------------------------------------------*
* CONVERSION MOIS DE LETTRE EN NUMERIQUE
*-------------------------------------------------------------------------*
CVT-MOIS.
    EVALUATE W-DATE-ASCII(4:3)
    WHEN "JAN" MOVE "01" TO W-MOIS-NUM
    WHEN "FEB" MOVE "02" TO W-MOIS-NUM
    WHEN "MAR" MOVE "03" TO W-MOIS-NUM
    WHEN "APR" MOVE "04" TO W-MOIS-NUM
    WHEN "MAY" MOVE "05" TO W-MOIS-NUM
    WHEN "JUN" MOVE "06" TO W-MOIS-NUM
    WHEN "JUL" MOVE "07" TO W-MOIS-NUM
    WHEN "AUG" MOVE "08" TO W-MOIS-NUM
    WHEN "SEP" MOVE "09" TO W-MOIS-NUM
    WHEN "OCT" MOVE "10" TO W-MOIS-NUM
    WHEN "NOV" MOVE "11" TO W-MOIS-NUM
    WHEN "DEC" MOVE "12" TO W-MOIS-NUM
    END-EVALUATE.

*-------------------------------------------------------------------------*
* LECTURE DE LA TABLE SOUS-TRAITANT (TRANSPORT)
*-------------------------------------------------------------------------*
LECTURE-STR.
    initialize nom in str W-CODSTR.
*    display "codstr " codstr in cde.
    MOVE CODSTR IN CDE  TO W-codstr.
    EXEC SQL
    SELECT       NOM           INTO STR.NOM                      
    FROM T.STR 
    WHERE CODSTR = :W-CODSTR
    END-EXEC.
    MOVE "2005" TO NUM-MSG.
    MOVE "LECTURE TABLE STR (Transport)" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.
*-------------------------------------------------------------------------*
* CDO *
*-------------------------------------------------------------------------*
LECTURE-cdo.
    initialize cdo.
    EXEC SQL
    SELECT  raisocl,nomliv,adr1l,adr2l,villel,cpostl
      INTO cdo.raisocl,cdo.nomliv,
           cdo.adr1l,cdo.adr2l,cdo.villel,cdo.cpostl
    FROM   d.cdo
    WHERE numcde = :cde.numcde and
          numral = :cde.numral 
    END-EXEC.
    MOVE "2005" TO NUM-MSG.
    MOVE "LECTURE TABLE cdo" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.
*
get-cli.
    initialize cli.
    EXEC SQL
    SELECT  raisoc ,nom   ,adres1 ,adres2 ,ville ,cpost , telep, CIPPDV,pays,CLIPAY,CLICIP,CLICSP
      INTO cli.raisoc ,cli.nom   ,
           cli.adres1 ,cli.adres2 ,cli.ville ,cli.cpost,cli.telep, :CLI.CIPPDV INDICATOR :ICIPPDV,
           :CLI.PAYS,:W-CLIPAY,:CLI.CLICIP,:CLI.CLICSP
    FROM   d.cli
    WHERE clicsp = :rte-clicsp          
    END-EXEC.
    MOVE "2005" TO NUM-MSG.
    MOVE "LECTURE TABLE cli" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

    IF ICIPPDV < 0
       MOVE 0 TO CIPPDV IN CLI   
    END-IF.


get-cli-cde.
    initialize cli.
    EXEC SQL
    SELECT  raisoc ,nom   ,adres1 ,adres2 ,ville ,cpost , telep, CIPPDV,pays, clipay,CLICIP,CLICSP
      INTO cli.raisoc ,cli.nom   ,
           cli.adres1 ,cli.adres2 ,cli.ville ,cli.cpost,cli.telep, :CLI.CIPPDV INDICATOR :ICIPPDV,
           :cli.pays, :W-CLIPAY,:CLI.CLICIP, :CLI.CLICSP
    FROM   d.cli
    WHERE clicsp = :cde.clicsp          
    END-EXEC.
    MOVE "2005" TO NUM-MSG.
    MOVE "LECTURE TABLE cli cde" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

    IF ICIPPDV < 0
       MOVE 0 TO CIPPDV IN CLI   
    END-IF.


get-cli-pay.
    EXEC SQL
    SELECT  CLICIP, CIPPDV
      INTO :W-CLIPAY-CLICIP, :W-CLIPAY-CIPPDV INDICATOR :ICIPPDV
    FROM   d.cli
    WHERE clicsp = :W-CLIPAY
    END-EXEC.
    MOVE "2005" TO NUM-MSG.
    MOVE "LECTURE TABLE cli" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

    IF ICIPPDV < 0
       MOVE 0 TO W-CLIPAY-CIPPDV
    END-IF.


*-------------------------------------------------------------------------*
* CCL *
*-------------------------------------------------------------------------*
LECTURE-ccl.
    initialize ccl.
    EXEC SQL
    SELECT  clilab
      INTO ccl.clilab
    FROM   d.ccl
    WHERE  codlab= :p-CODLAB
      AND  clicsp= :cde.clicsp
    END-EXEC.
    MOVE "2005" TO NUM-MSG.
    MOVE "LECTURE TABLE cdo" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

*-------------------------------------------------------------------------*
* LECTURE MVT pour recup datper                     
*-------------------------------------------------------------------------*
LECTURE-MVT.
*
    initialize w-datper mvt. 
*    display "**codart" w-codart " lotfab " w-lotfab .
    EXEC SQL
    SELECT  datper    INTO mvt.datper                      
    FROM d.mvt
    WHERE CODdep = :P-CODDEP  and
          CODLAB = :W-CODLAB  and
          CODART = :W-CODART  and
          LOTFAB = :W-LOTFAB  and 
          CODMVT IN ('070','071','072') and
          cast(datmvt as date ansi) = cast(:cde.datebl as date ansi)  and
          REFDOC STARTING WITH :W-NUMCDE
    LIMIT TO 1 ROW
    END-EXEC.
    MOVE "2005" TO NUM-MSG.
    MOVE "LECTURE TABLE MVT " TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.
*
*-------------------------------------------------------------------------*
* LECTURE DE LA TABLE LABORATOIRE
*-------------------------------------------------------------------------*
LECTURE-LAB.
    EXEC SQL
    SELECT       TYPTRS,NUMRAL INTO :W-TYPTRS, :W-NUMRALMAXLAB 
    FROM D.LAB 
    WHERE CODLAB = :W-CODLAB
    END-EXEC.
    MOVE "2005" TO NUM-MSG.
    MOVE "LECTURE TABLE LAB" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.



*-------------------------------------------------------------------------*
* LECTURE DE LA TABLE ARTICLE
*-------------------------------------------------------------------------*
LECTURE-ART.
    EXEC SQL
    SELECT ARTLAB, GESLOT, ARTCIP, libell   
    INTO :W-ARTLAB, :W-GESLOT, :W-ARTCIP, :ART.LIBELL  
    FROM D.ART 
    WHERE CODLAB = :W-CODLAB AND CODART = :W-CODART
    END-EXEC.
    MOVE "2005" TO NUM-MSG.
    MOVE "LECTURE TABLE ART" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

*-------------------------------------------------------------------------*
* LECTURE DE LA TABLE CLIENT
*-------------------------------------------------------------------------*
LECTURE-CLI.
    INITIALIZE CLI.
    EXEC SQL
    SELECT GENCLI, DEPART,clicip , CIPPDV, PAYS
    INTO :CLI.GENCLI, :CLI.DEPART, :w-clicip, :CLI.CIPPDV INDICATOR :ICIPPDV, :CLI.PAYS
    FROM D.CLI 
    WHERE CLICSP = :CDE.CLICSP
    END-EXEC.
    MOVE "2005" TO NUM-MSG.
    MOVE "LECTURE TABLE CLI" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

    IF ICIPPDV < 0
       MOVE 0 TO CIPPDV IN CLI   
    END-IF.



LECTURE-PAR-LABLAB.
*-----------------------------------*
* RECHERCHE code labo du labo                     
*-----------------------------------*
    INITIALIZE WFONCT-LABLAB.
    EXEC SQL
    SELECT FONCT INTO :WFONCT-LABLAB FROM D.PAR
           WHERE CODENT = 'LAB'    AND
                 CHAMPS = 'LABLAB' AND  
                 ARGUM STARTING WITH :W-CODLAB
    LIMIT TO 1 ROW
    END-EXEC. 
    MOVE '2004' TO NUM-MSG.
    MOVE 'ERREUR LECTURE PAR LECTURE-PAR-LABLAB' TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

LECTURE-PAR-TYPLAB.
*-----------------------------------*
* RECHERCHE code ADV                              
*-----------------------------------*
    INITIALIZE WARGUM-TYPLAB.
    EXEC SQL
    SELECT ARGUM INTO :WARGUM-TYPLAB FROM D.PAR
           WHERE CODENT = 'LAB'                   AND
                 CHAMPS = 'TYPLAB'                AND  
                 ARGUM STARTING WITH :W-CODLAB    AND 
                 FONCT STARTING WITH :CDE.TYPCDE
    LIMIT TO 1 ROW
    END-EXEC. 
    DISPLAY 'wargum typlab '  WARGUM-TYPLAB.
    MOVE '2004' TO NUM-MSG.
    MOVE 'ERREUR LECTURE PAR LECTURE-PAR-TYPLAB' TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

LECTURE-DEPLAB.     
    MOVE "N" TO FLAG-TROUVE
    INITIALIZE WARGUM-DEPLAB.
    MOVE CODLAB IN CDL TO W-DEPLAB-CODLAB
    MOVE DEPART IN CLI TO W-DEPLAB-DEPART
    MOVE WARGUM-DEPLAB TO WS-ARGUM

    EXEC SQL
    SELECT FONCT INTO :WFONCT       FROM D.PAR
           WHERE CODENT = 'LAB'                   AND
                 CHAMPS = 'DEPLAB'                AND  
                 ARGUM = :WS-ARGUM
    LIMIT TO 1 ROW
    END-EXEC. 
    MOVE '2004' TO NUM-MSG.
    MOVE 'ERREUR LECTURE PAR LECTURE-PAR-TYPLAB' TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.
    IF TROUVE
       MOVE WFONCT(1:2) TO CODDEP IN CDL
    END-IF.


*-----------------------------------*
* RECHERCHE SI CDE NON FACTURE                    
*----------------------------------------------------------------------*
LECTURE-PAR.
EXEC SQL
    FETCH CURPAR INTO :WS-ARGUM, :WS-FONCT
END-EXEC.
    MOVE "2006" TO NUM-MSG.
    MOVE "FETCH CURSOR PAR" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.
    IF TROUVE
       IF DEPART IN CLI NOT < WS-FONCT(1:2)
          IF WS-ARGUM(5:2) = SPACES
             MOVE "O" TO FLAG-CDE-NONFAC
          ELSE
             IF WS-ARGUM(5:1) = SPACE
              AND WS-ARGUM(6:1) = TYPCDE IN CDE
                MOVE "O" TO FLAG-CDE-NONFAC
             ELSE
                IF WS-ARGUM(5:1) = GENCLI IN CLI
                 AND WS-ARGUM(6:1) = SPACE
                   MOVE "O" TO FLAG-CDE-NONFAC
                ELSE
                   IF WS-ARGUM(5:1) = GENCLI IN CLI
                    AND WS-ARGUM(6:1) = TYPCDE IN CDE
                      MOVE "O" TO FLAG-CDE-NONFAC
                   END-IF
                END-IF
             END-IF
          END-IF
       END-IF
    ELSE
       MOVE "O" TO FLAG-FIN-PAR
    END-IF.

*-----------------------------------*
* RECH-NUMDOC                  
*-----------------------------------*
RECH-NUMDOC.
    DISPLAY "RECHERCHE NUMDOC DANS DOCENT: " W-CODLAB"/"W-NUMCDE"/"W-NUMRAL"/"
    EXEC SQL
        SELECT NUMDOC INTO :W-NUMDOC 
        FROM P.DOCENT
        WHERE CODLAB = :W-CODLAB 
        AND NUMCDE = :W-NUMCDE
        AND NUMRAL = :W-NUMRAL
        LIMIT TO 1 ROW
    END-EXEC. 
    MOVE '2004' TO NUM-MSG.
    MOVE 'ERREUR LECTURE DOCENT' TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

*-------------------------------------------------------------------------*
* OUVERTURE DU CURSEUR POUR LECTURE DES LABOS     
*-------------------------------------------------------------------------*
OPEN-CURSOR-DIV.
    EXEC SQL
    OPEN CURDIV
    END-EXEC.
    MOVE "2005" TO NUM-MSG.
    MOVE "OPEN CURSOR DIV sur LAB" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

*-------------------------------------------------------------------------*
* FERMETURE DU CURSEUR POUR LECTURE DES LABOS
*-------------------------------------------------------------------------*
CLOSE-CURSOR-DIV.
    EXEC SQL
    CLOSE CURDIV   
    END-EXEC.
    MOVE "2006" TO NUM-MSG.
    MOVE "CLOSE CURSOR DIV" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.


*-------------------------------------------------------------------------*
* OUVERTURE DU CURSEUR POUR LECTURE DES COMMANDES 
*-------------------------------------------------------------------------*
OPEN-CURSOR-CDE.
    EXEC SQL
    OPEN CURCDE
    END-EXEC.
    MOVE "2005" TO NUM-MSG.
    MOVE "OPEN CURSOR CDE" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

*-------------------------------------------------------------------------*
* FERMETURE DU CURSEUR POUR LECTURE DES COMMANDES 
*-------------------------------------------------------------------------*
CLOSE-CURSOR-CDE.
    EXEC SQL
    CLOSE CURCDE
    END-EXEC.
    MOVE "2006" TO NUM-MSG.
    MOVE "CLOSE CURSOR CDE" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.


*-------------------------------------------------------------------------*
* OUVERTURE DU CURSEUR POUR LECTURE DES COMMANDES 
*-------------------------------------------------------------------------*
OPEN-CURSOR-CDE-R.
    EXEC SQL
    OPEN CURCDE_R
    END-EXEC.
    MOVE "2005" TO NUM-MSG.
    MOVE "OPEN CURSOR CDE" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

*-------------------------------------------------------------------------*
* FERMETURE DU CURSEUR POUR LECTURE DES COMMANDES 
*-------------------------------------------------------------------------*
CLOSE-CURSOR-CDE-R.
    EXEC SQL
    CLOSE CURCDE_R
    END-EXEC.
    MOVE "2006" TO NUM-MSG.
    MOVE "CLOSE CURSOR CDE" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

*-------------------------------------------------------------------------*
* OUVERTURE DU CURSEUR POUR LECTURE DES LIGNES COMMANDE
*-------------------------------------------------------------------------*
OPEN-CURSOR-CDL.
    EXEC SQL
    OPEN CURCDL
    END-EXEC.
    MOVE "2005" TO NUM-MSG.
    MOVE "OPEN CURSOR CDL" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

*-------------------------------------------------------------------------*
* FERMETURE DU CURSEUR POUR LECTURE DES LIGNES COMMANDE
*-------------------------------------------------------------------------*
CLOSE-CURSOR-CDL.
    EXEC SQL
    CLOSE CURCDL
    END-EXEC.
    MOVE "2006" TO NUM-MSG.
    MOVE "CLOSE CURSOR CDL" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.



*-------------------------------------------------------------------------*
* OUVERTURE DU CURSEUR POUR LECTURE DE PAR         
*-------------------------------------------------------------------------*
OPEN-CURSOR-PAR.
    EXEC SQL
    OPEN CURPAR
    END-EXEC.
    MOVE "2005" TO NUM-MSG.
    MOVE "OPEN CURSOR PAR" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

*-------------------------------------------------------------------------*
* FERMETURE DU CURSEUR POUR LECTURE DE PAR
*-------------------------------------------------------------------------*
CLOSE-CURSOR-PAR.
    EXEC SQL
    CLOSE CURPAR
    END-EXEC.
    MOVE "2006" TO NUM-MSG.
    MOVE "CLOSE CURSOR PAR" TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE.

*----------------------------------------------------------------------*
* OUVERTURE DES FICHIERS           
*----------------------------------------------------------------------*
OPEN-FILES.
    OPEN INPUT FIC-MESSAGES.
    IF CODE-STATUS NOT = "00"
       MOVE "1001" TO NUM-MSG
       MOVE "RMS_MSG" TO PARAM-01
       GO TO FIN-ANOOPEN
    END-IF.
    OPEN OUTPUT FIC-ANOMALIES.
    IF CODE-STATUS NOT = "00"
       MOVE "1001" TO NUM-MSG
       MOVE "RMS_ANO" TO PARAM-01
       GO TO FIN-ANOOPEN
    END-IF.

    OPEN OUTPUT FIC-TRANSMITCO.
    IF CODE-STATUS NOT = "00"
       MOVE "1001" TO NUM-MSG
       MOVE "TRANSMIT" TO PARAM-01
       GO TO FIN-ANOOPEN
    END-IF.

    OPEN OUTPUT FIC-TRANSMITMO.
    IF CODE-STATUS NOT = "00"
       MOVE "1001" TO NUM-MSG
       MOVE "TRANSMIT" TO PARAM-01
       GO TO FIN-ANOOPEN
    END-IF.

	OPEN Output FIC-MAJ.
	IF CODE-STATUS NOT = "00"
		MOVE "1001" TO NUM-MSG
 		MOVE "FIC-REJ" TO PARAM-01
		GO TO FIN-ANORMALE.



*----------------------------------------------------------------------*
* FERMETURE DES FICHIERS                                         *
*----------------------------------------------------------------------*
CLOSE-FILES.
    CLOSE FIC-TRANSMITCO.
    IF CODE-STATUS NOT = "00"
       MOVE "1004" TO NUM-MSG
       MOVE "TRANSMIT" TO PARAM-01
       GO TO FIN-ANOFERME
    END-IF.

    CLOSE FIC-TRANSMITMO.
    IF CODE-STATUS NOT = "00"
       MOVE "1004" TO NUM-MSG
       MOVE "TRANSMIT" TO PARAM-01
       GO TO FIN-ANOFERME
    END-IF.

	CLOSE       FIC-MAJ .
	IF CODE-STATUS NOT = "00"
		MOVE "1004" TO NUM-MSG
		MOVE "FIC-REJ " TO PARAM-01
		GO TO FIN-ANOFERME.


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


*----------------------------------------------------------------------*
GESTION-SQLSTATE.
*----------------------------------------------------------------------*
     EVALUATE SQLSTATE
     WHEN '02000'              MOVE 'N' TO FLAG-TROUVE
     WHEN '22002'              MOVE 'O' TO FLAG-TROUVE
     WHEN '00000' THRU '01999' MOVE 'O' TO FLAG-TROUVE  
     WHEN '02001' THRU '22001' PERFORM FIN-ANORMALE
     WHEN '22003' THRU 'S9999' PERFORM FIN-ANORMALE
     END-EVALUATE.

*----------------------------------------------------------------------*
FIN-ANOOPEN.
*----------------------------------------------------------------------*
    DISPLAY "FIN-ANOOPEN".
    CALL "D00_MSGGES" USING ZONES-PARAMETRES.
    STOP RUN.  
*----------------------------------------------------------------------*
FIN-ANOFERME.
*----------------------------------------------------------------------*
    DISPLAY "FIN-ANOFERME".
    CALL "D00_MSGGES" USING ZONES-PARAMETRES.
    STOP RUN.  

*----------------------------------------------------------------------*
FIN-ANORMALE.
*----------------------------------------------------------------------*
    CALL "sys$putmsg" using rdb$message_vector.
    DISPLAY NUM-MSG " " ANO_MESSAGE.
    DISPLAY "FIN-ANORMALE".
    DISPLAY "SQLSTATE " SQLSTATE WITH CONVERSION.
    WRITE ANO-ENREG.
    IF NUM-MSG < "2000" CALL "D00_MSGGES" USING ZONES-PARAMETRES.
    CALL "SQL$GET_ERROR_TEXT" USING BY DESCRIPTOR GET-ERROR-BUFFER 
                                    BY REFERENCE GET-MSG-LEN.
    DISPLAY GET-ERROR-BUFFER(1:GET-MSG-LEN) GET-MSG-LEN WITH CONVERSION.
    MOVE GET-ERROR-BUFFER (1:80) TO ANO_MESSAGE.
    WRITE ANO-ENREG.
    IF GET-MSG-LEN >80
       MOVE GET-ERROR-BUFFER (80:80) TO ANO_MESSAGE
       WRITE ANO-ENREG
    END-IF.
    IF GET-MSG-LEN >160
       MOVE GET-ERROR-BUFFER (160:80) TO ANO_MESSAGE
       WRITE ANO-ENREG
    END-IF.
    IF GET-MSG-LEN >240
       MOVE GET-ERROR-BUFFER (240:60) TO ANO_MESSAGE
       WRITE ANO-ENREG
    END-IF.
    DISPLAY "AVANT STOP RUN".
    PERFORM CLOSE-FILES.
    EXEC SQL 
    ROLLBACK
    END-EXEC.
    STOP RUN.     

END PROGRAM D02_EXTCDE_CRMCSP1. 
