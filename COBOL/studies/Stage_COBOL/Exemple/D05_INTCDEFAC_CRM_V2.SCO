*---------------------------------------------------------------------------*
*    Nom du programme  : D05_INTCDEFAC_CRM_V2.SCO                           *
*    Auteur            : B. Caure                                           *
*    Langage           : SQL/COBOL                                          *
*    Date de creation  : 11/08/03                                           *
*    Date modification : 28/10/11                                           *
*== V2.000-3580 AP:EM  LIB:CREATION DU PROGRAMME                            *
*    Version           : 1.000                                              *
*    Fonctionnalites   : Intégration infos des fichiers DEPOT CDE           *
*                        ET FAC POUR ALIMENTER BASE BO                      *
*    Refer. externes   :                                                    *
*    - tables          :                                                    *
*    - sous-programmes : D00_MSGGES                                         *
*    - procedures      :                                                    *
*---------------------------------------------------------------------------*
IDENTIFICATION DIVISION.
PROGRAM-ID. D05_INTCDEFAC_CRM_V2.
ENVIRONMENT DIVISION.       
INPUT-OUTPUT SECTION.
	FILE-CONTROL.           
*
* fichier des libelles des messages a editer en cas d'anomalie         *
        SELECT FIC-MESSAGES  ASSIGN TO "RMS_MSG"   
               ORGANIZATION INDEXED
               ACCESS MODE DYNAMIC 
               RECORD KEY MSG-CLE
               FILE STATUS IS CODE-STATUS.
*
* fichier des anomalies detectes                                       *
        SELECT FIC-ANOMALIES ASSIGN TO "RMS_ANO" 
               FILE STATUS IS CODE-STATUS.
*
* fichier statistiques ventes realisees 
        SELECT FIC-CDEFAC ASSIGN TO "RMS_CDEFAC"
               FILE STATUS IS CODE-STATUS.

DATA DIVISION.
FILE SECTION.                                                 

FD  FIC-MESSAGES    
	EXTERNAL
        LABEL RECORD OMITTED.
    COPY "CDD$TOP.DEPOT.FICHIERS.D00_MSGTXT" FROM DICTIONARY.

FD  FIC-ANOMALIES
	EXTERNAL
        LABEL RECORD OMITTED.
    COPY "CDD$TOP.DEPOT.FICHIERS.D00_ANOMAL" FROM DICTIONARY.
*

FD  FIC-CDEFAC
        LABEL RECORD OMITTED.
    01  ENR-CDEFAC.
      02 ENR-CODLAB   PIC X(4).
      02 ENR-CODDEP   PIC X(2).
      02 ENR-SSLABO   PIC X(4).
      02 ENR-NUMCDE   PIC 9(8).
      02 ENR-NUMRAL   PIC 9.    
      02 ENR-MOYRGL   PIC X(2). 
      02 ENR-DELRGL   PIC X(3). 
      02 ENR-CODREP   PIC X(8).    
      02 ENR-DATCDE   PIC X(23).
      02 ENR-DATEBL   PIC X(23).
      02 ENR-ESCOMP   PIC 9(2)V9(4).
      02 ENR-FRGEST1  PIC S9(6)V9(2).
      02 ENR-FLAGCDE  PIC X.    
      02 ENR-FLAGLIV  PIC X.    
      02 ENR-FLAGFAC  PIC X.    
      02 ENR-FLAGBLO  PIC X.    
      02 ENR-FLAGSUP  PIC X.    
      02 ENR-MOTIFSUP PIC XXX.       
      02 ENR-FLAGDIF  PIC X.    
      02 ENR-FLAGRAL  PIC X.
      02 ENR-FLAGSTD  PIC X.
      02 ENR-FLAGDET  PIC X.
      02 ENR-GENCLI   PIC X.
      02 ENR-LIBGENCLI PIC X(40).
      02 ENR-CLILAB   PIC X(10).
      02 ENR-FLAGOCC  PIC X.
      02 ENR-CLICSP   PIC 9(8).
      02 ENR-NOMLIV   PIC X(40).
      02 ENR-RAISOCL  PIC X(40).
      02 ENR-ADR1L    PIC X(40).
      02 ENR-ADR2L    PIC X(40).
      02 ENR-VILLEL   PIC X(32).
      02 ENR-DEPARL   PIC XX.
      02 ENR-CPOSTL   PIC X(5).
      02 ENR-UGA      PIC X(3).
      02 ENR-UGA746   PIC X(5).
      02 ENR-CLIFAC   PIC 9(8).
      02 ENR-NOMFAC   PIC X(40).
      02 ENR-RAISOCF  PIC X(40).
      02 ENR-ADR1F    PIC X(40).
      02 ENR-ADR2F    PIC X(40).
      02 ENR-VILLEF   PIC X(32). 
      02 ENR-DEPARF   PIC X(2). 
      02 ENR-CPOSTF   PIC X(5).
      02 ENR-CLIPAY   PIC 9(8).
      02 ENR-NOMPAY   PIC X(40).
      02 ENR-RAISPAY  PIC X(40).
      02 ENR-CLIGRP   PIC 9(8). 
      02 ENR-NOMGRP   PIC X(40).
      02 ENR-RAISGRP  PIC X(40).
      02 ENR-TYPNFA   PIC X(2).      
      02 ENR-QUANTA   PIC X.         
      02 ENR-MOISFA   PIC XX.        
      02 ENR-CPTFAC   PIC 9(8).       
      02 ENR-DATFAC   PIC X(23).     
      02 ENR-CODREPN  PIC X(8).
      02 ENR-CODREPR  PIC X(8).
      02 ENR-REGFAC   PIC X.
      02 ENR-NOMREP   PIC X(40).
      02 ENR-NOMREPN   PIC X(40).	
      02 ENR-NOMREPR   PIC X(40).
      02 ENR-FLAGENCOURS PIC X.
      02 ENR-QTENCOURS PIC S9(8).
      02 ENR-STATENCOURS PIC X(3).
      02 ENR-REMISEE     PIC 9(6)V9(4).  
      02 ENR-MTHT        PIC S9(8)V9(2).
      02 ENR-DATSAI      PIC X(23).
      02 ENR-DATECH      PIC X(23).
      02 ENR-CIPPDV      PIC 9(9).
      02 ENR-GENCLIFAC   PIC X.
      02 ENR-LIBGENCLIFAC PIC X(40).
      02 ENR-CODREPC     PIC X(5).
      02 ENR-NOMREPC     PIC X(80).
      02 ENR-DTLIVS      PIC X(23).
      02 ENR-POIDS       PIC 9(7)V9(4).
      02 ENR-CDELAB	 PIC X(22).
      02 ENR-REFCDE      PIC X(35).
      02 ENR-DOSEXP      PIC X(10).
      02 ENR-VOLSTD      PIC 9(8).
      02 ENR-VOLDTL      PIC 9(8).
      02 ENR-LIBPAYS     PIC X(80).
      02 ENR-PROMOS      PIC X(4).
      02 ENR-NBCOLIS     PIC 9(6).
      02 ENR-NBPAL       PIC 9(6).
      02 ENR-DTLIVR      PIC X(23).
      02 ENR-FLAGEXP  PIC X.    


                   
WORKING-STORAGE SECTION.
COPY WORK_COM.
*
01 PARAM-RECU.
   05 P-CODDEP PIC X(2).

 
*---       longueur de la zone de reception de la date convertie    ---*
01  W-LENGTH                    PIC S9(4) COMP VALUE 23.
*---   flag = 0 date retournee sous format 01-JAN-1988 00:00:00.00  ---*
01  W-FLAG                      PIC S9(6) COMP VALUE ZERO. 
*
77  W-D-DATE-BIN                PIC S9(11)V9(7) COMP.
77  W-EDI-DATE                  PIC X(20).

77  ENR-DATCLOJOU               PIC 9(8).
77  W-ANNEECDE      PIC X(04).
77  W-TRIMESTRECDE  PIC X(01).
77  W-MOISCDE       PIC X(02).
77  W-ANNEEBL       PIC X(04).
77  W-TRIMESTREBL   PIC X(01).
77  W-MOISBL        PIC X(02).
77  W-ANNEEFAC      PIC X(04).
77  W-TRIMESTREFAC  PIC X(01).
77  W-MOISFAC       PIC X(02).
77  WS-NUMCDE       PIC 9(08).
77  WS-NUMRAL       PIC 9.
77  W-NOUVCDE       PIC 9.
77  W-ARTREMP       PIC X(10).

*---    date systeme sous forme ascii   ---*
01  W-D-DATE.
    02 W-D-JMA                       PIC X(12).
    02 W-D-HEURE                     PIC X(11).
01  W-DATE.
    02 W-DATE-JMA           PIC X(12).
    02 W-DATE-HEURE         PIC X(11).
77  W-DATE-FORMATAGE		PIC X(8).
77  W-DATCDE-BIN                PIC S9(11)V9(7) COMP.
77  W-DATSAI-BIN                PIC S9(11)V9(7) COMP.
77  W-DATEBL-BIN                PIC S9(11)V9(7) COMP.
77  W-DATFAC-BIN                PIC S9(11)V9(7) COMP.
77  W-DATECH-BIN                PIC S9(11)V9(7) COMP.
77  W-MOISN			PIC XX.             
77  W-DTLIVS-BIN                PIC S9(11)V9(7) COMP.
77  W-DTLIVR-BIN                PIC S9(11)V9(7) COMP.

*----------------- variables de travail -------------------------------*
*
77  W-FACTUR                PIC X.  
77  W-TYPNFA                PIC X(2).
77  W-QUANTA                PIC X.
77  W-MOISFA                PIC X(2).
77  W-CPTFAC                PIC 9(3).
77  W-CLICSP                PIC 9(8).
77  W-CIPPDV                PIC 9(9).
77  W-QTENCOURS             PIC 9(8).
77  IW-QTENCOURS            PIC S9(4) COMP.
77  W-STATENCOURS           PIC X(3).
77  WS-STATENCOURS          PIC X(3).
77  Ws-cptfac               PIC 9(9).
77  IW-STATENCOURS          PIC S9(4) COMP.

*------------------------ Variables de calcul -------------------------*
*
77  W-MONTBRUTCDE          PIC S9(8)V9(2).
77  W-MONTREM1CDE          PIC S9(6)V9(4).
77  W-MONTREM2CDE          PIC S9(6)V9(4).
77  W-MONTREM3CDE          PIC S9(6)V9(4).
77  W-MTREMICDE            PIC S9(6)V9(2).
77  W-MTHTCDE              PIC S9(8)V9(2).
77  W-MTHTARECDE           PIC S9(6)V9(2).
*
*
77  W-CPT-LIG              PIC 9(5).
77  W-LIGN-AO              PIC 9(5).
*
*----------------- zones de travail indépendantes ---------------------*
*
77  CODE-STATUS EXTERNAL      PIC XX.
*
*-------------  zones de gestion des erreur par module D00_MSGGES -----*     
COPY "CDD$TOP.DEPOT.FICHIERS.D00_MSGPAR" FROM DICTIONARY.
*
*----------------  flags conditions erreurs    ------------------------*     
01  FLAG-TROUVE                       PIC X VALUE "N".            
    88 TROUVE                               VALUE "O".
01  FLAG-ACREER                       PIC X VALUE "N".            
    88 ACREER                               VALUE "O".
01  FLAG-FINFIC                       PIC X VALUE "N".            
    88 FINFIC                               VALUE "O".
77  FLAG-DEBUG                        PIC X VALUE "N".
    88 DEBUG                                VALUE "O".

*----------------      debogage          ------------------------*
77  DEBUG1 PIC 9(8).
77  DEBUG2 PIC 9(8).
77  DEBUG3 PIC 9(8).

*----------------------------------------------------------------------*
*                DECLARATION DES BASES UTILISEES                       *
*----------------------------------------------------------------------*
EXEC SQL 
        DECLARE S ALIAS  FILENAME 'BD_CRM'
END-EXEC. 
EXEC SQL 
        DECLARE D ALIAS  FILENAME 'BD_DEPOT'
END-EXEC. 
*EXEC SQL
*        DECLARE ST ALIAS  FILENAME 'BD_STATS'
*END-EXEC.
***                      
*----------------------------------------------------------------------*
*                DECLARATION DU SQLCA                                  *
*----------------------------------------------------------------------*
01  SQLSTATE            PIC X(5).  
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
*----------------------------------------------------------------------*
*----------------------------------------------------------------------*
TRAITEMENT-PRINCIPAL.                
*----------------------------------------------------------------------*
              
COPY PROC-COM.
    MOVE LOGIC-NAME TO PARAM-RECU

*----------------------------------------------------------------------*
**DECLARATION DU TYPE DE TRANSACTION EFFECTUEE SUR LA BASE EXTRANET
*----------------------------------------------------------------------*
EXEC SQL 
    SET TRANSACTION READ WRITE
END-EXEC.

*--- Initialisation de la date systeme sous la forme jj-mmm-aaaa  ---*
    CALL "LIB$DATE_TIME" USING BY DESCRIPTOR W-D-DATE.
    CALL "D00_DATEDI" USING  W-D-DATE W-EDI-DATE.

*--- Date systeme en binaire
    CALL "SYS$BINTIM" USING BY DESCRIPTOR W-D-DATE
                            BY REFERENCE  W-D-DATE-BIN.

    PERFORM OUVERTURE-FICHIERS. 
    MOVE 0 TO DEBUG1.
    MOVE 0 TO DEBUG2.
    MOVE 0 TO DEBUG3.    
    move "N" to flag-finfic.
    MOVE 'O' TO FLAG-DEBUG.
    MOVE 0 TO WS-NUMCDE WS-NUMRAL.
    PERFORM LECTURE-FICCDEFAC THRU LECTURE-FICCDEFAC-FIN UNTIL FINFIC.
*    DISPLAY DEBUG1 " READ - " DEBUG2 " UPDATE - " DEBUG3 " CREATE".

EXEC SQL
    COMMIT
END-EXEC

    PERFORM FERMETURE-FICHIERS.
    STOP RUN.
 
*--------------------------------------------------------------------------*
LECTURE-FICCDEFAC.
*--------------------------------------------------------------------------*
    DISPLAY "READ"
    READ FIC-CDEFAC
	 AT END MOVE "O" TO FLAG-FINFIC.
    IF FINFIC 
       GO TO LECTURE-FICCDEFAC-FIN.
    IF ENR-NUMCDE=00000000  OR ENR-NUMCDE="0       " 
            display "numcde =0" 
	 GO TO LECTURE-FICCDEFAC.
*****Verif statut final
**?20/10/16    if enr-statencours = "FAT" AND NOT = "GLT" 
      move "N" to flag-acreer
      perform verif-statut-final
      if ws-statencours = "FAT" OR "GLT"  OR "FAP" OR "GLP"
         if enr-codlab = '3010' and enr-cptfac >   20000    and ws-cptfac =0
          display "commande mutu a  mettre  jour"  enr-numcde
         else
          display "commande deja en statut terminal => on ne fait rien " enr-numcde  
          GO TO LECTURE-FICCDEFAC
         end-if
      end-if.
*
    IF ENR-CODLAB = "9994" 
       display "*****ENR-CODLAB 9994>= maj base Depot"
       IF ENR-FLAGLIV = "O"
          PERFORM UPDATE-CDE-LIV
       END-IF	
       IF ENR-FLAGFAC = "O" 
          PERFORM UPDATE-CDE-FAC
       END-IF	
       IF ENR-FLAGBLO = "O" 
          PERFORM UPDATE-CDE-BLO
       END-IF	
       IF ENR-FLAGSUP = "O" AND ENR-FLAGFAC = "N"
          PERFORM UPDATE-CDE-SUP
       END-IF
    END-IF
*
    ADD 1 TO DEBUG1.

    MOVE ENR-CODDEP TO P-CODDEP.
    MOVE '' TO W-CLICSP.
    MOVE SPACES TO WS-STATENCOURS.
    PERFORM TRAITEMENT-DATES THRU TRAITEMENT-DATES-FIN.
    PERFORM RECHERCHE-CDEFAC
EXEC SQL
    COMMIT  
END-EXEC.
LECTURE-FICCDEFAC-FIN.
    EXIT.

UPDATE-CDE-LIV.
EXEC SQL 
    UPDATE D.CDE SET DTLIVR = :W-DTLIVR-BIN
    WHERE 
      NUMCDE=:ENR-NUMCDE AND 
      NUMRAL=:ENR-NUMRAL AND CODLAB ='9994'    
END-EXEC.
        MOVE "2003" TO NUM-MSG        
        MOVE "UPDATE CDE LIV" TO ANO_MESSAGE
        PERFORM GESTION-SQLSTATE.
*
UPDATE-CDE-FAC.
    display "********** UPDATE-CDE-FAC " ENR-NUMCDE ENR-NUMRAL ENR-CODLAB
EXEC SQL 
    UPDATE D.CDE SET DATEBL = :W-DATEBL-BIN , STATUT = 'FAT'
    WHERE 
      NUMCDE=:ENR-NUMCDE AND
      NUMRAL=:ENR-NUMRAL      AND CODLAB='9994'
END-EXEC.
        MOVE "2003" TO NUM-MSG        
        MOVE "UPDATE CDE FAC" TO ANO_MESSAGE
        PERFORM GESTION-SQLSTATE.

UPDATE-CDE-BLO.
EXEC SQL 
    UPDATE D.CDE SET  STATUT = 'BLO'
    WHERE 
      NUMCDE=:ENR-NUMCDE AND 
      NUMRAL=:ENR-NUMRAL AND CODLAB='9994'    
END-EXEC.
        MOVE "2003" TO NUM-MSG        
        MOVE "UPDATE CDE BLO" TO ANO_MESSAGE
        PERFORM GESTION-SQLSTATE.

UPDATE-CDE-SUP.
EXEC SQL 
    UPDATE D.CDE SET STATUT = 'SEI'
    WHERE 
      NUMCDE=:ENR-NUMCDE AND
      NUMRAL=:ENR-NUMRAL AND CODLAB='9994'    
END-EXEC.
        MOVE "2003" TO NUM-MSG        
        MOVE "UPDATE CDE SEI" TO ANO_MESSAGE
        PERFORM GESTION-SQLSTATE.

*------------------------------------------------------------------------------*
TRAITEMENT-DATES.
*------------------------------------------------------------------------------*
	DISPLAY ' TRAITEMENT-DATES ENR-DATCDE : ' ENR-DATCDE
    move enr-datcde to w-date
    CALL "SYS$BINTIM" USING BY DESCRIPTOR W-DATE  
                            BY REFERENCE  W-DATCDE-BIN.

    move enr-datebl to w-date
    CALL "SYS$BINTIM" USING BY DESCRIPTOR W-DATE  
                            BY REFERENCE  W-DATEBL-BIN.
    move enr-datsai to w-date
    CALL "SYS$BINTIM" USING BY DESCRIPTOR W-DATE  
                            BY REFERENCE  W-DATSAI-BIN.

    If enr-datfac = ''
       MOVE '17-NOV-1858' TO enr-datfac
    END-IF 
    move enr-datfac to w-date
    CALL "SYS$BINTIM" USING BY DESCRIPTOR W-DATE  
                            BY REFERENCE  W-DATFAC-BIN.
    If enr-datech = ''
       MOVE '17-NOV-1858' TO enr-datech
    END-IF 
    move enr-datech to w-date
    CALL "SYS$BINTIM" USING BY DESCRIPTOR W-DATE  
                            BY REFERENCE  W-DATECH-BIN.

    move ENR-DTLIVS to w-date
    CALL "SYS$BINTIM" USING BY DESCRIPTOR W-DATE  
                            BY REFERENCE  W-DTLIVS-BIN.

    move ENR-DTLIVR to w-date
    CALL "SYS$BINTIM" USING BY DESCRIPTOR W-DATE  
                            BY REFERENCE  W-DTLIVR-BIN.

TRAITEMENT-DATES-FIN.
    EXIT.

verif-statut-final.

       initialize   Ws-statencours ws-cptfac
EXEC SQL
	SELECT    statencours,cptfac  INTO :Ws-statencours,:ws-cptfac
	FROM S.CDE_FAC
	WHERE  CODDEP = :ENR-CODDEP
        AND   NUMCDE = :ENR-NUMCDE
        AND   NUMRAL = :ENR-NUMRAL
        AND   codlab = :ENR-codlab
        limit to 1 row
END-EXEC
        MOVE "2002" TO NUM-MSG        
        MOVE "verid statut final       " TO ANO_MESSAGE
        PERFORM GESTION-SQLSTATE.
        IF NOT TROUVE
           MOVE 'O' TO FLAG-ACREER or 
           display "== cde a créer dans Base CRM : "  enr-numcde enr-numral enr-codlab enr-cODDEP enr-statencours 
        else
           display "Base CRM : statut " Ws-statencours " fichier :"  enr-numcde enr-numral enr-codlab enr-cODDEP enr-statencours 
        END-IF.

*------------------------------------------------------------------------------*
RECHERCHE-CDEFAC.
*------------------------------------------------------------------------------*
*	display 'RECHERCHE-CDEFAC'
    PERFORM EXTRACTION-DATE THRU EXTRACTION-DATE-FIN.
    MOVE 0 TO W-NOUVCDE.
        display "info " ENR-CPTFAC " - " ENR-NUMCDE " - " ENR-NUMRAL   "-" enr-flagcde  "-" enr-flagfac
        display " - " ENR-REGFAC
    IF ACREER 
       PERFORM CREATION-CDEFAC THRU CREATION-CDEFAC-FIN
    ELSE
       PERFORM UPDATE-CDEFAC
    END-IF.
*
*------------------------------------------------------------------------------*
UPDATE-CDEFAC.
*------------------------------------------------------------------------------*
*    DISPLAY "UPDATE-CDEFAC".
        IF FLAG-DEBUG = 'O'
EXEC SQL 
    UPDATE S.CDE_FAC SET 
      CODLAB=:ENR-CODLAB,
      CODDEP=:ENR-CODDEP,
      SSLABO=:ENR-SSLABO,
      NUMCDE=:ENR-NUMCDE, 
      NUMRAL=:ENR-NUMRAL,     
      MOYRGL=:ENR-MOYRGL, 
      DELRGL=:ENR-DELRGL, 
      CODREP=:ENR-CODREP,    
      DATCDE=:W-DATCDE-BIN,
      DATEBL=:W-DATEBL-BIN,
      ESCOMP=:ENR-ESCOMP,  
      FRGEST1=:ENR-FRGEST1, 
      FLAGCDE=:ENR-FLAGCDE,    
      FLAGLIV=:ENR-FLAGLIV,    
      FLAGEXP=:ENR-FLAGEXP,    
      FLAGFAC=:ENR-FLAGFAC,    
      FLAGBLO=:ENR-FLAGBLO,    
      FLAGSUP=:ENR-FLAGSUP,    
      MOTIFSUP=:ENR-MOTIFSUP,       
      FLAGDIF=:ENR-FLAGDIF,    
      FLAGRAL=:ENR-FLAGRAL,
      GENCLI=:ENR-GENCLI ,
      LIBGENCLI=:ENR-LIBGENCLI,
      CLILAB=:ENR-CLILAB,
      FLAGOCC=:ENR-FLAGOCC,
      CLICSP=:ENR-CLICSP ,
      NOMLIV=:ENR-NOMLIV,
      RAISOCL=:ENR-RAISOCL,
      ADR1L=:ENR-ADR1L,
      ADR2L=:ENR-ADR2L,
      VILLEL=:ENR-VILLEL,
      DEPARL=:ENR-DEPARL,
      CPOSTL=:ENR-CPOSTL,
      UGA=:ENR-UGA ,
      UGA746=:ENR-UGA746,
      CLIFAC=:ENR-CLIFAC ,
      NOMFAC=:ENR-NOMFAC,
      RAISOCF=:ENR-RAISOCF,
      ADR1F=:ENR-ADR1F,
      ADR2F=:ENR-ADR2F,
      VILLEF=:ENR-VILLEF, 
      DEPARF=:ENR-DEPARF, 
      CPOSTF=:ENR-CPOSTF,
      CLIPAY=:ENR-CLIPAY ,
      NOMPAY=:ENR-NOMPAY,
      RAISPAY=:ENR-RAISPAY,
      CLIGRP=:ENR-CLIGRP , 
      NOMGRP=:ENR-NOMGRP,
      RAISGRP=:ENR-RAISGRP,
      TYPNFA=:ENR-TYPNFA,      
      QUANTA=:ENR-QUANTA ,         
      MOISFA=:ENR-MOISFA,        
      CPTFAC=:ENR-CPTFAC ,       
      DATFAC=:W-DATFAC-BIN,   
      CODREPN=:ENR-CODREPN,
      CODREPR=:ENR-CODREPR,
      REGFAC=:ENR-REGFAC,
      NOMREP=:ENR-NOMREP,
      NOMREPN=:ENR-NOMREPN,
      NOMREPR=:ENR-NOMREPR,
      FLAGENCOURS=:ENR-FLAGENCOURS,
      REMISEE=:ENR-REMISEE ,
      MTHTARE=:ENR-MTHT,         
      MTHTCDE=:ENR-MTHT,         
      DATSAI =:W-DATSAI-BIN,
      ANNEECDE =:W-ANNEECDE,
      MOISCDE =:W-MOISCDE,
      TRIMESTRECDE =:W-TRIMESTRECDE,
      ANNEEBL =:W-ANNEEBL,
      MOISBL =:W-MOISBL,
      TRIMESTREBL =:W-TRIMESTREBL,
      ANNEEFAC =:W-ANNEEFAC,
      MOISFAC =:W-MOISFAC,
      TRIMESTREFAC =:W-TRIMESTREFAC,
      DATECH=:W-DATECH-BIN,
      CIPPDV=:ENR-CIPPDV,
      GENCLIFAC=:ENR-GENCLIFAC ,
      LIBGENCLIFAC=:ENR-LIBGENCLIFAC,
      CODREPC=:ENR-CODREPC,
      NOMREPC=:ENR-NOMREPC,          
      DTLIVS=:W-DTLIVS-BIN,
      DTLIVR=:W-DTLIVR-BIN,
      POIDS=:ENR-POIDS,
      CDELAB=:ENR-CDELAB,
      REFCDE=:ENR-REFCDE,
      DOSEXP=:ENR-DOSEXP,
      VOLSTD=:ENR-VOLSTD,
      VOLDTL=:ENR-VOLDTL,
      LIBPAYS=:ENR-LIBPAYS,
      PROMOS=:ENR-PROMOS,
      NBPAL=:ENR-NBPAL,
      NBCOLIS = :ENR-NBCOLIS,
      TYPNFA  = :ENR-TYPNFA,
      QUANTA  = :ENR-QUANTA,
      MOISFA  = :ENR-MOISFA,
      CPTFAC  = :ENR-CPTFAC 
 
     WHERE   CODLAB = :ENR-CODLAB 
       AND   CODDEP = :ENR-CODDEP 
       AND   NUMCDE = :ENR-NUMCDE
       AND   NUMRAL = :ENR-NUMRAL  
END-EXEC.
        MOVE "2003" TO NUM-MSG        
        MOVE "UPDATE CDE_FAC" TO ANO_MESSAGE
        PERFORM GESTION-SQLSTATE.
UPDATE-CDEFAC-FIN.
    EXIT.        

*------------------------------------------------------------------------------*
CREATION-CDEFAC.
*------------------------------------------------------------------------------*

EXEC SQL
     INSERT INTO S.CDE_FAC (
      CODLAB,
      CODDEP,              
      SSLABO,
      NUMCDE,
      NUMRAL ,
      MOYRGL, 
      DELRGL, 
      CODREP,    
      DATCDE,
      DATEBL ,
      ESCOMP,  
      FRGEST1, 
      FLAGCDE,    
      FLAGLIV,  
      FLAGEXP,
      FLAGFAC,    
      FLAGBLO,    
      FLAGSUP,    
      MOTIFSUP,       
      FLAGDIF,    
      FLAGRAL,
      FLAGSTD,
      FLAGDET,
      GENCLI,
      LIBGENCLI,
      CLILAB,
      FLAGOCC,
      CLICSP,
      NOMLIV,
      RAISOCL,
      ADR1L,
      ADR2L,
      VILLEL,
      DEPARL,
      CPOSTL,
      UGA,
      UGA746,
      CLIFAC,
      NOMFAC,
      RAISOCF,
      ADR1F,
      ADR2F,
      VILLEF, 
      DEPARF, 
      CPOSTF,

      CLIPAY,
      NOMPAY,
      RAISPAY,
      CLIGRP, 
      NOMGRP,
      RAISGRP,
      CODREPN,
      CODREPR,
      REGFAC,
      NOMREP,
      NOMREPN,
      NOMREPR,
      FLAGENCOURS,
      STATENCOURS,
      REMISEE,
      DATSAI,
      ANNEECDE,
      MOISCDE,
      TRIMESTRECDE,
      ANNEEBL,
      MOISBL,
      TRIMESTREBL,
      ANNEEFAC,
      MOISFAC,
      TRIMESTREFAC,
      DATECH,
      CIPPDV,
      GENCLIFAC,
      LIBGENCLIFAC,
      CODREPC,
      NOMREPC,

      MONTBRUTCDE,
      MONTREM1CDE,
      MONTREM2CDE,
      MONTREM3CDE,
      MTREMICDE,

      DTLIVS,
      POIDS,
      CDELAB,
      REFCDE,
      DOSEXP,
      VOLSTD,
      VOLDTL,
      LIBPAYS,
      PROMOS,
      NBPAL,
      NBCOLIS,
      TYPNFA,
      QUANTA,
      MOISFA,
      CPTFAC,	
      DTLIVR,
      DATFAC,
      MTHTARE,
      MTHTCDE
        )
        VALUES
        (
      :ENR-CODLAB,
      :ENR-CODDEP,
      :ENR-SSLABO,
      :ENR-NUMCDE,
      :ENR-NUMRAL,
      :ENR-MOYRGL, 
      :ENR-DELRGL, 
      :ENR-CODREP,    
      :W-DATCDE-BIN,
      :W-DATEBL-BIN,
      :ENR-ESCOMP,  
      :ENR-FRGEST1, 
      :ENR-FLAGCDE,    
      :ENR-FLAGLIV,    
      :ENR-FLAGEXP,    
      :ENR-FLAGFAC,    
      :ENR-FLAGBLO,    
      :ENR-FLAGSUP,    
      :ENR-MOTIFSUP, 
      :ENR-FLAGDIF,    
      :ENR-FLAGRAL,
      :ENR-FLAGSTD,
      :ENR-FLAGDET,
      :ENR-GENCLI,
      :ENR-LIBGENCLI,

      :ENR-CLILAB,
      :ENR-FLAGOCC,
      :ENR-CLICSP,
      :ENR-NOMLIV,
      :ENR-RAISOCL,
      :ENR-ADR1L,
      :ENR-ADR2L,
      :ENR-VILLEL,
      :ENR-DEPARL,
      :ENR-CPOSTL,
      :ENR-UGA,
      :ENR-UGA746,
      :ENR-CLIFAC,
      :ENR-NOMFAC,
      :ENR-RAISOCF,
      :ENR-ADR1F,
      :ENR-ADR2F,
      :ENR-VILLEF, 
      :ENR-DEPARF, 
      :ENR-CPOSTF,
      :ENR-CLIPAY,
      :ENR-NOMPAY,
      :ENR-RAISPAY,
      :ENR-CLIGRP, 
      :ENR-NOMGRP,
      :ENR-RAISGRP,
      :ENR-CODREPN,
      :ENR-CODREPR,
      :ENR-REGFAC,
      :ENR-NOMREP,
      :ENR-NOMREPN,
      :ENR-NOMREPR,
      :ENR-FLAGENCOURS,
      :ENR-STATENCOURS,
      :ENR-REMISEE,
      :W-DATSAI-BIN,
      :W-ANNEECDE,
      :W-MOISCDE,
      :W-TRIMESTRECDE,
      :W-ANNEEBL,
      :W-MOISBL,
      :W-TRIMESTREBL,
      :W-ANNEEFAC,
      :W-MOISFAC,
      :W-TRIMESTREFAC,
      :W-DATECH-BIN,
      :ENR-CIPPDV,      
      :ENR-GENCLIFAC ,
      :ENR-LIBGENCLIFAC,
      :ENR-CODREPC,
      :ENR-NOMREPC,          

      :W-MONTBRUTCDE,
      :W-MONTREM1CDE,
      :W-MONTREM2CDE,
      :W-MONTREM3CDE,
      :W-MTREMICDE,

      :W-DTLIVS-BIN,
      :ENR-POIDS,
      :ENR-CDELAB,
      :ENR-REFCDE,
      :ENR-DOSEXP,
      :ENR-VOLSTD,
      :ENR-VOLDTL,
      :ENR-LIBPAYS,
      :ENR-PROMOS,
      :ENR-NBPAL,
      :ENR-NBCOLIS,
      :ENR-TYPNFA,
      :ENR-QUANTA,
      :ENR-MOISFA,
      :ENR-CPTFAC,
      :W-DTLIVR-BIN,
      :W-DATFAC-BIN,
      :ENR-MTHT,
      :ENR-MTHT
        )
END-EXEC.
        MOVE "2003" TO NUM-MSG        
        MOVE "INSERT CDE_FAC" TO ANO_MESSAGE
        PERFORM GESTION-SQLSTATE.
CREATION-CDEFAC-FIN.
    EXIT.

*------------------------------------------------------------------------*
* extraction du mois, de l'annee et du trimestre des dates CDE,FAC,BL
*------------------------------------------------------------------------*

EXTRACTION-DATE.

*--extraction de la date de commande
*       Display "W-DATCDE : " W-DATCDE.
       MOVE ENR-DATCDE(8:4) TO W-ANNEECDE.
       
       if  ENR-DATCDE(4:3) = 'JAN' move '01' to W-MOISCDE.
       if  ENR-DATCDE(4:3) = 'FEB' move '02' to W-MOISCDE.
       if  ENR-DATCDE(4:3) = 'MAR' move '03' to W-MOISCDE.
       if  ENR-DATCDE(4:3) = 'APR' move '04' to W-MOISCDE.
       if  ENR-DATCDE(4:3) = 'MAY' move '05' to W-MOISCDE.
       if  ENR-DATCDE(4:3) = 'JUN' move '06' to W-MOISCDE.
       if  ENR-DATCDE(4:3) = 'JUL' move '07' to W-MOISCDE.
       if  ENR-DATCDE(4:3) = 'AUG' move '08' to W-MOISCDE.
       if  ENR-DATCDE(4:3) = 'SEP' move '09' to W-MOISCDE.
       if  ENR-DATCDE(4:3) = 'OCT' move '10' to W-MOISCDE.
       if  ENR-DATCDE(4:3) = 'NOV' move '11' to W-MOISCDE.
       if  ENR-DATCDE(4:3) = 'DEC' move '12' to W-MOISCDE.
       If  W-MOISCDE  < '04'  
           MOVE '1' TO W-TRIMESTRECDE
       ELSE 
               If  W-MOISCDE  < '07'  
                 MOVE '2' TO W-TRIMESTRECDE
               ELSE
                 If  W-MOISCDE  < '10'  
                   MOVE '3' TO W-TRIMESTRECDE
                 ELSE
                   MOVE '4' TO W-TRIMESTRECDE
                 END-IF

               END-IF

       END-IF.

*--extraction de la date de BL
       MOVE ENR-DATEBL(8:4) TO W-ANNEEBL.
       if  ENR-DATEBL(4:3) = 'JAN' move '01' to W-MOISBL.
       if  ENR-DATEBL(4:3) = 'FEB' move '02' to W-MOISBL.
       if  ENR-DATEBL(4:3) = 'MAR' move '03' to W-MOISBL.
       if  ENR-DATEBL(4:3) = 'APR' move '04' to W-MOISBL.
       if  ENR-DATEBL(4:3) = 'MAY' move '05' to W-MOISBL.
       if  ENR-DATEBL(4:3) = 'JUN' move '06' to W-MOISBL.
       if  ENR-DATEBL(4:3) = 'JUL' move '07' to W-MOISBL.
       if  ENR-DATEBL(4:3) = 'AUG' move '08' to W-MOISBL.
       if  ENR-DATEBL(4:3) = 'SEP' move '09' to W-MOISBL.
       if  ENR-DATEBL(4:3) = 'OCT' move '10' to W-MOISBL.
       if  ENR-DATEBL(4:3) = 'NOV' move '11' to W-MOISBL.
       if  ENR-DATEBL(4:3) = 'DEC' move '12' to W-MOISBL.
       If  W-MOISBL  < '04'  
           MOVE '1' TO W-TRIMESTREBL
       ELSE 
           If  W-MOISBL  < '07'  
               MOVE '2' TO W-TRIMESTREBL
               ELSE
                 If  W-MOISBL  < '10'  
                   MOVE '3' TO W-TRIMESTREBL
                 ELSE
                   MOVE '4' TO W-TRIMESTREBL
                 END-IF

               END-IF

       END-IF.

*--extraction de la date de facture
       MOVE ENR-DATFAC(8:4) TO W-ANNEEFAC.
       if  ENR-DATFAC(4:3) = 'JAN' move '01' to W-MOISFAC.
       if  ENR-DATFAC(4:3) = 'FEB' move '02' to W-MOISFAC.
       if  ENR-DATFAC(4:3) = 'MAR' move '03' to W-MOISFAC.
       if  ENR-DATFAC(4:3) = 'APR' move '04' to W-MOISFAC.
       if  ENR-DATFAC(4:3) = 'MAY' move '05' to W-MOISFAC.
       if  ENR-DATFAC(4:3) = 'JUN' move '06' to W-MOISFAC.
       if  ENR-DATFAC(4:3) = 'JUL' move '07' to W-MOISFAC.
       if  ENR-DATFAC(4:3) = 'AUG' move '08' to W-MOISFAC.
       if  ENR-DATFAC(4:3) = 'SEP' move '09' to W-MOISFAC.
       if  ENR-DATFAC(4:3) = 'OCT' move '10' to W-MOISFAC.
       if  ENR-DATFAC(4:3) = 'NOV' move '11' to W-MOISFAC.
       if  ENR-DATFAC(4:3) = 'DEC' move '12' to W-MOISFAC.
       If  W-MOISFAC  < '04'  
           MOVE '1' TO W-TRIMESTREFAC
       ELSE 
           If  W-MOISFAC  < '07'  
               MOVE '2' TO W-TRIMESTREFAC
               ELSE
                 If  W-MOISFAC  < '10'  
                   MOVE '3' TO W-TRIMESTREFAC
                 ELSE
                   MOVE '4' TO W-TRIMESTREFAC
                 END-IF

               END-IF
       END-IF.


EXTRACTION-DATE-FIN.
	EXIT.

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
*   OUVERTURE DES FICHIERS                   
*----------------------------------------------------------------------*
OUVERTURE-FICHIERS.
    OPEN INPUT FIC-MESSAGES.
    IF CODE-STATUS NOT = "00"
       MOVE "1001" TO NUM-MSG
       MOVE "RMS_MSG" TO PARAM-01
       GO TO FIN-ANORMALE.

    OPEN OUTPUT FIC-ANOMALIES.
    IF CODE-STATUS NOT = "00"
       DISPLAY " Erreur d'ouverture du fichier RMS-ANO".

    OPEN INPUT FIC-CDEFAC.
    IF CODE-STATUS NOT = "00"
       MOVE "1001" TO NUM-MSG
       MOVE "RMS_CDEFAC" TO PARAM-01
       GO TO FIN-ANORMALE.


*----------------------------------------------------------------------*
* 	FERMETURE DES FICHIERS                                         *
*----------------------------------------------------------------------*
FERMETURE-FICHIERS.

    CLOSE FIC-ANOMALIES.
    IF CODE-STATUS NOT = "00"
       DISPLAY "Erreur de fermeture sur fichier RMS-ANO".

    CLOSE FIC-MESSAGES. 
    IF CODE-STATUS NOT = "00"
       MOVE "1004" TO NUM-MSG
       MOVE "RMS_MSG" TO PARAM-01
       GO TO FIN-ANOFERME.

    CLOSE FIC-CDEFAC. 
    IF CODE-STATUS NOT = "00"
       MOVE "1004" TO NUM-MSG
       MOVE "RMS_CDEFAC" TO PARAM-01
       GO TO FIN-ANOFERME.


*----------------------------------------------------------------------*
FIN-ANOFERME.
*----------------------------------------------------------------------*
        CALL "D00_MSGGES" USING ZONES-PARAMETRES.
        STOP RUN.  
   
*----------------------------------------------------------------------*
FIN-ANORMALE.
*----------------------------------------------------------------------*
        DISPLAY "SQLSTATE " SQLSTATE WITH CONVERSION
        IF NUM-MSG > "2000"
	   WRITE ANO-ENREG
        ELSE	
        CALL "D00_MSGGES" USING ZONES-PARAMETRES.
        CALL "SQL$GET_ERROR_TEXT" USING BY DESCRIPTOR GET-ERROR-BUFFER
	   			        BY REFERENCE GET-MSG-LEN.
        DISPLAY "GET-ERROR-BUFFER " GET-ERROR-BUFFER(1:GET-MSG-LEN) GET-MSG-LEN WITH CONVERSION
        MOVE GET-ERROR-BUFFER (1:80) TO ANO_MESSAGE.
        WRITE ANO-ENREG.
        IF GET-MSG-LEN >80
	   MOVE GET-ERROR-BUFFER (80:80) TO ANO_MESSAGE
	   WRITE ANO-ENREG
	   IF GET-MSG-LEN >160
              MOVE GET-ERROR-BUFFER (160:80) TO ANO_MESSAGE
	      WRITE ANO-ENREG
	      IF GET-MSG-LEN >240
	         MOVE GET-ERROR-BUFFER (240:60) TO ANO_MESSAGE
	         WRITE ANO-ENREG.
        PERFORM FERMETURE-FICHIERS.
EXEC SQL 
        ROLLBACK
END-EXEC.
        STOP RUN.     

END PROGRAM D05_INTCDEFAC_CRM_V2.
