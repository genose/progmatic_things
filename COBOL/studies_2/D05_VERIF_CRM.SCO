*----------------------------------------------------------------------*
*    Nom du programme  : D05_VERIF_CRM                                 *
*    Auteur            : C.Rodier 	                               *
*    Langage           : SQL/COBOL                                     *
*    Date de creation  : 19/10/16                                      *
*    Date modification : 19/10/16                                      *
*== V2.000-3580 AP:EM  LIB:CREATION DU PROGRAMME                       *
*    Version           : 1.000                                         *
*    Fonctionnalites   : Verif cde DEPOT et CRM en phase pour 1 date   *
*    - tables          :                                               *
*    - sous-programmes :   			                       *
*    - procedures      :                                               *
*----------------------------------------------------------------------*
IDENTIFICATION DIVISION.
PROGRAM-ID. D05_VERIF_CRM.
ENVIRONMENT DIVISION.       

INPUT-OUTPUT SECTION.
*--------------------
FILE-CONTROL.

* fichier des anomalies detectes 
  SELECT FIC-ANOMALIES ASSIGN TO "RMS_ANO" 
         FILE STATUS IS CODE-STATUS.
*
DATA DIVISION.                                                             
*
FILE SECTION.                                   
*------------
FD  FIC-ANOMALIES LABEL RECORD OMITTED.
    COPY "CDD$TOP.DEPOT.FICHIERS.D00_ANOMAL" FROM DICTIONARY.
*
WORKING-STORAGE SECTION.     
*----------------------------------------------------------------------*
*              DECLARATION DES VARIABLES DU PROGRAMME                  *
*----------------------------------------------------------------------*
*
01  W-D-DATE1.
    02 W-D-JMA1      PIC X(12).
    02 W-D-HEURE1    PIC X(11).
01  W-DATSYS        PIC S9(11)V9(7) COMP.
01  ED-HEURE    PIC X(11).
01  W-CPT PIC 9(6).
*
01 tab-cde.
   02 t-occ       occurs 9000 .
   03   t-NUMCDE PIC 9(9).
   03   t-NUMRAL PIC 9.
   03   t-statut PIC xxx.
   03   t-codlab PIC x(4).
01 cpt-cde              pic 9(4). 
01 ws-cde.
   03   w-NUMCDE PIC 9(9).
   03   w-NUMRAL PIC 9.
   03   w-statut PIC xxx.
   03   w-codlab PIC x(4).
01   w-statencours PIC xxx.
*
*** DATE SYSTEME ***
01  W-D-DATSYS.
    02 FILLER       PIC X(12).
    02 W-EDI-HEURE  PIC X(11).
01 W-EDI-DATE.
   05 EDI-JJ pic XX.
   05 FILLER PIC X.
   05 EDI-MM PIC XX.
   05 FILLER PIC XXX.
   05 EDI-AA PIC XX.
   05 FILLER PIC X(10).
*
01 DATE-EDIT   PIC X(10) VALUE "00/00/0000".
01 HEURE-EDIT  PIC X(5)  VALUE "00:00".
01 W-DATE-ASCII PIC X(23).
01 W-DATE-RDB   PIC S9(11)V9(7) COMP.
01 W-DATE-ZERO  PIC S9(11)V9(7) COMP.
01 W-DATRAS     PIC S9(11)V9(7) COMP.
01 W-DATJOU     PIC S9(11)V9(7) COMP.
01 W-DATSYS     PIC S9(11)V9(7) COMP.
01 W-DATBIN     PIC S9(11)V9(7) COMP.
01 W-DATBINC     PIC S9(11)V9(7) COMP.
01 W-DATBINF     PIC S9(11)V9(7) COMP.
01 W-DATDEB     PIC S9(11)V9(7) COMP.
01 W-DATFIN     PIC S9(11)V9(7) COMP.
01 W-DATEBL     PIC S9(11)V9(7) COMP.
01 W-YYYYMMDD   PIC X(8).
01 W-PYYYYMMDD   PIC X(8).
01 W-ANNEE      PIC 9999.
01 W-JOUR       PIC 99.
01 W-MOIS       PIC 99.
01 W-MOIS-UK    PIC XXX.
01 W-MOIS-FR    PIC XXX.

01  WFONCT      PIC X(22).
01  W-FONCT      PIC X(30).
01  W-FONCTI.
    02  W-JJ    PIC 99.
    02  TIRET1  PIC X     VALUE '-'.
    02  W-MM    PIC 99.
    02  TIRET2  PIC X     VALUE '-'.
    02  W-SI    PIC 99.
    02  W-AA    PIC 99.
    02  W-HEURE PIC X(12).
01 MAB                  PIC X(80) VALUE SPACES. 
*---    date systeme sous forme ascii   ---*
01  W-D-DATE.
    02 W-D-JMA                       PIC X(12).
    02 W-D-HEURE                     PIC X(11).
01  W-D-DATE-BIN                     PIC S9(11)V9(7) COMP.

*-------------  zones de gestion des erreur par module D00_MSGGES -----*
COPY "CDD$TOP.DEPOT.FICHIERS.D00_MSGPAR" FROM DICTIONARY.

*----- flag de conditions d'erreurs -----*
01 FLAG-TROUVE                    PIC X VALUE "N".
   88 TROUVE                            VALUE "O".
01 FLAG-FINCDE                    PIC X VALUE "N".
   88 FINCDE                            VALUE "O".
01 FLAG-DEADLOCK                  PIC X VALUE "N".
   88 DEADLOCK                          VALUE "O".
01 FLAG-REJET                     PIC X VALUE "N".
   88 REJET                             VALUE "O".
01 FLAG-REJET-GLO                 PIC X VALUE "N".
   88 REJET-GLO                         VALUE "O".
*
*-- codes exceptions RDB
01 RDB$_DEADLOCK        PIC S9(9) COMP VALUE IS EXTERNAL RDB$_DEADLOCK.
01 RDB$_LOCK_CONFLICT   PIC S9(9) COMP VALUE IS EXTERNAL RDB$_LOCK_CONFLICT.
01 RDB$_INTEG_FAIL      PIC S9(9) COMP VALUE IS EXTERNAL RDB$_INTEG_FAIL. 
01 EXCEPT               PIC S9(9) COMP.                  
01 W-LENGTH             PIC S9(11)V9(7) COMP.
01 W-FLAG               PIC S9(6) COMP VALUE ZERO.
01 W-RESULT             PIC S9(5) COMP.
01 CODE-STATUS EXTERNAL PIC XX.
*
*----------------------------------------------------------------------*
*                DECLARATION DE LA BASE UTILISEE                       *
*----------------------------------------------------------------------*
EXEC SQL 
        DECLARE S ALIAS  FILENAME 'BD_CRM'
END-EXEC. 
EXEC SQL 
        DECLARE D ALIAS  FILENAME 'BD_DEPOT'
END-EXEC. 
EXEC SQL
        INCLUDE FROM DICTIONARY 'CDD$DEFAULT.DBDEPOT.RDB$RELATIONS.CDE' 
END-EXEC.
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
*
*-- parametres 

COPY WORK_COM.
*
01 PARAM.
   02 P-CODDEP  PIC XX.
   02 P-DATEBL. 
      03  P-AA	PIC 9999.
      03  P-MM 	PIC 99.
      03  P-JJ 	PIC 99.
   02 P-MAJ     PIC X.
*
*----------------------------------------------------------------------*
PROCEDURE DIVISION.
*----------------------------------------------------------------------*
DECLARATIVES.
* traitement des erreurs d'entrées-sorties sur les fichiers RMS        *
FILE-PROBLEM-001 SECTION.
	USE AFTER STANDARD EXCEPTION PROCEDURE ON OUTPUT.
FILE-PROBLEM-001-ACTION.
* en cas d'erreur d'écriture d'un message dans le fichier ANO          *

FILE-PROBLEM-002  SECTION.
	USE AFTER STANDARD EXCEPTION PROCEDURE ON INPUT.
FILE-PROBLEM-002-ACTION.         
* en cas d'erreur d'écriture d'un message dans le fichier ANO          *
END DECLARATIVES.

*----------------------------------------------------------------------*
PROGRAMME SECTION.           
*----------------------------------------------------------------------*
DECLARE-CURSEUR-CDE.
    EXEC SQL
      DECLARE CURCDE    CURSOR FOR 
       SELECT numcde,numral,statut,codlab                                     
         FROM D.CDE
        WHERE  cast( cast(datebl as date ansi) as date vms) = :W-DATEBL
          AND FLAG_CRM = 'N'
          AND STATUT <> 'THO'
          AND STATUT <> 'THO'
          AND STATUT <> 'SIX'
          AND STATUT <> 'EIX'
    END-EXEC.
    MOVE "2006" TO NUM-MSG.
    MOVE "CURSEUR CDE" TO ANO_MESSAGE
    PERFORM GESTION-SQLSTATE.
*
TRAITEMENT-PRINCIPAL.                        
*
    COPY PROC_COM.
    MOVE LOGIC-NAME TO PARAM.
    DISPLAY "PARAMETRES RECUS : " PARAM.
*--- Date systeme en binaire
    CALL 'LIB$DATE_TIME' USING BY DESCRIPTOR W-D-DATSYS.
    CALL 'D00_DATEDI' USING W-D-DATSYS W-D-DATE.  
    MOVE  W-EDI-HEURE TO  ED-HEURE. 
*
    PERFORM TRANSFO-DATEBL.
    PERFORM OPEN-FILES.
*
EXEC SQL 
     SET TRANSACTION READ ONLY
END-EXEC.
*
    initialize tab-cde.
    move 0 to cpt-cde.
    MOVE  "N" TO FLAG-FINCDE
    PERFORM OPEN-CURCDE
    PERFORM TRT-CDE         UNTIL FINCDE OR DEADLOCK
    PERFORM CLOSE-CURCDE
*
    display "Nb commande a maj " cpt-cde
    IF P-MAJ = 'O'
       IF cpt-cde > 0 
      	  EXEC SQL 
	     ROLLBACK 
 	  END-EXEC
	  EXEC SQL 
	     SET TRANSACTION READ WRITE           RESERVING D.CDE    FOR SHARED WRITE
 	  END-EXEC
       END-IF
*
     move 0 to cpt-cde
     perform varying cpt-cde from 1 by 1 until cpt-cde > 9000 or 
			t-codlab(cpt-cde) =spaces 
           perform decharge-tableau
           perform trt-tabcde
     end-perform
*
      IF cpt-cde > 0 
	EXEC SQL 
          COMMIT
	END-EXEC
      END-IF
    END-IF.
*
    PERFORM CLOSE-FILES.
    STOP RUN.
*
TRT-CDE.
    INITIALIZE WS-CDE.             
    MOVE "N" TO FLAG-REJET.
*
    EXEC SQL
    FETCH CURcde
       INTO :W-NUMCDE,
	    :W-NUMRAL,
            :W-STATUT,
            :W-CODLAB
    END-EXEC.
    MOVE "2001" TO NUM-MSG.
    MOVE "LECTURE TABLE CDE  " TO ANO_MESSAGE.
    PERFORM GESTION-SQLSTATE
    IF TROUVE
       PERFORM GET-CDE-CRM
***       PERFORM GET-CDE-CRM-CPT
    ELSE
       MOVE 'O' TO FLAG-FINCDE
    END-IF.
*
CHARGE-TABLEAU.
*
    add 1 to cpt-cde.
    if cpt-cde> 9000
	 display "table trop petite" 
    end-if.
    move W-CODLAB to t-CODLAB(cpt-cde).
    move W-NUMCDE to t-NUMCDE(cpt-cde).
    move W-NUMral to t-NUMral(cpt-cde).
    move W-STATUT to t-statut(cpt-cde).

DECHARGE-TABLEAU.
    move  t-CODLAB(cpt-cde) to W-CODLAB .
    move  t-NUMCDE(cpt-cde) to W-NUMCDE .
    move  t-NUMral(cpt-cde) to W-NUMral.
    move  t-STATUT(cpt-cde) to W-STATUT.
*
TRT-TABCDE.
*
    display "UPDATE CDE" W-NUMCDE W-NUMRAL
EXEC SQL
    UPDATE D.CDE
            SET STATCRM = '', FLAG_CRM = 'O'
    WHERE
	        CODLAB =  :W-CODLAB
           AND  NUMCDE =  :W-NUMCDE
           AND  NUMRAL =  :W-NUMRAL
END-EXEC
     MOVE "2008" TO NUM-MSG
     MOVE "PB UPDATE CDE         " TO ANO_MESSAGE
     PERFORM GESTION-SQLSTATE.
*
GET-CDE-CRM.
*
    initialize W-STATENCOURS
EXEC SQL
    SELECT STATENCOURS INTO :W-STATENCOURS
    FROM S.CDE_FAC
    WHERE  	CODDEP =  :P-CODDEP
	   AND  CODLAB =  :W-CODLAB
           AND  NUMCDE =  :W-NUMCDE
           AND  NUMRAL =  :W-NUMRAL
    LIMIT TO 1 ROW
END-EXEC
     MOVE "2008" TO NUM-MSG
     MOVE "PB LECTURE CDE_FAC CRM" TO ANO_MESSAGE
     PERFORM GESTION-SQLSTATE
     IF TROUVE  
	IF    ( ( W-STATUT = W-STATENCOURS ) 
	             OR ( W-STATUT = 'FAT' AND W-STATENCOURS = 'FAP')
	             OR ( W-STATUT = 'GLT' AND W-STATENCOURS = 'GLP')
		      )
           display "cde " W-NUMCDE " " W-NUMRAL "/" w-codlab "***OK***"
        ELSE
           display "cde " W-NUMCDE " " W-NUMRAL "/" w-codlab "statut depot :" w-statut "statut crm : " w-statencours " ===NOK==="
           PERFORM CHARGE-TABLEAU
        END-IF
     ELSE
        display "cde " W-NUMCDE " " W-NUMRAL "/" w-codlab "statut depot :" w-statut "statut crm : --- ===NOK===Inexistante dans CRM"
        PERFORM CHARGE-TABLEAU
     END-IF.
*
GET-CDE-CRM-CPT.
*
    MOVE 0 TO W-CPT.
EXEC SQL
    SELECT COUNT(*)    INTO :W-CPT            
    FROM S.CDE_FAC
    WHERE  	CODDEP =  :P-CODDEP
	   AND  CODLAB =  :W-CODLAB
           AND  NUMCDE =  :W-NUMCDE
           AND  NUMRAL =  :W-NUMRAL
END-EXEC
     MOVE "2008" TO NUM-MSG
     MOVE "PB LECTURE CDE_FAC CRM" TO ANO_MESSAGE
     PERFORM GESTION-SQLSTATE
     IF TROUVE  AND W-CPT>1
        display "cde " W-NUMCDE " " W-NUMRAL "/" w-codlab "statut depot :" w-statut "statut crm : " w-statencours "===NOK+++lig==="
**        PERFORM CHARGE-TABLEAU
     END-IF.
*----------------------------------------------------------------------*
TRANSFO-DATEBL.
*----------------------------------------------------------------------*
* initialisation date zero 
    MOVE "17-NOV-1858 " TO W-DATE-ASCII(01:12).
    CALL "SYS$BINTIM"  USING  BY DESCRIPTOR W-DATE-ASCII
                              BY REFERENCE  W-DATE-ZERO. 

    MOVE W-DATE-ZERO TO W-DATEBL.
    MOVE P-DATEBL(7:2) TO W-DATE-ASCII(1:2)
    MOVE P-DATEBL(1:4) TO W-DATE-ASCII(8:4)
    MOVE P-DATEBL(5:2) TO W-MOIS-FR
    PERFORM CVT-MOIS-FR-UK
    MOVE W-MOIS-UK TO W-DATE-ASCII(4:3)
    DISPLAY "DATE DEBUT : " W-DATE-ASCII
    CALL "SYS$BINTIM"  USING  BY DESCRIPTOR W-DATE-ASCII
                              BY REFERENCE  W-DATDEB
    MOVE P-DATEBL(7:2) TO W-DATE-ASCII(1:2)
    MOVE P-DATEBL(1:4) TO W-DATE-ASCII(8:4)
    MOVE P-DATEBL(5:2) TO W-MOIS-FR
    PERFORM CVT-MOIS-FR-UK
    MOVE W-MOIS-UK TO W-DATE-ASCII(4:3)
    MOVE "00:00:00.00" to W-DATE-ASCII(13:11)
    DISPLAY "DATE BL        : " W-DATE-ASCII
    CALL "SYS$BINTIM"  USING  BY DESCRIPTOR W-DATE-ASCII
                              BY REFERENCE  W-DATEBL.

*----------------------------------------------------------------------*
CVT-MOIS-FR-UK.
*----------------------------------------------------------------------*
    EVALUATE W-MOIS-FR
    WHEN "01" MOVE "JAN" TO W-MOIS-UK
    WHEN "02" MOVE "FEB" TO W-MOIS-UK
    WHEN "03" MOVE "MAR" TO W-MOIS-UK
    WHEN "04" MOVE "APR" TO W-MOIS-UK
    WHEN "05" MOVE "MAY" TO W-MOIS-UK
    WHEN "06" MOVE "JUN" TO W-MOIS-UK
    WHEN "07" MOVE "JUL" TO W-MOIS-UK
    WHEN "08" MOVE "AUG" TO W-MOIS-UK
    WHEN "09" MOVE "SEP" TO W-MOIS-UK
    WHEN "10" MOVE "OCT" TO W-MOIS-UK
    WHEN "11" MOVE "NOV" TO W-MOIS-UK
    WHEN "12" MOVE "DEC" TO W-MOIS-UK
    END-EVALUATE.
*
*      
*----------------------------------------------------------------------*
OPEN-FILES.
*----------------------------------------------------------------------*
    OPEN OUTPUT FIC-ANOMALIES.
    IF CODE-STATUS NOT = "00"
       DISPLAY " Erreur d'ouverture du fichier RMS-ANO"
       MOVE "1001" TO NUM-MSG
       MOVE "RMS_ANO" TO PARAM-01
       GO TO FIN-ANOOPEN
    END-IF.
*
*----------------------------------------------------------------------*
CLOSE-FILES.
*----------------------------------------------------------------------*
    CLOSE FIC-ANOMALIES.
    IF CODE-STATUS NOT = "00"
       MOVE "1004" TO NUM-MSG
       MOVE "RMS_ANO" TO PARAM-01
       GO TO FIN-ANOFERME
    END-IF.
*
*----------------------------------------------------------------------*
OPEN-CURCDE.
*----------------------------------------------------------------------*
EXEC SQL
        OPEN CURCDE    
END-EXEC.
    MOVE "2006" TO NUM-MSG.
    MOVE "OUVERTURE CURCDE  " TO ANO_MESSAGE
    PERFORM GESTION-SQLSTATE.

*----------------------------------------------------------------------*
CLOSE-CURCDE.   
*----------------------------------------------------------------------*
EXEC SQL
        CLOSE CURCDE   
END-EXEC.
    MOVE "2006" TO NUM-MSG.
    MOVE "FERMETURE CURCDE   " TO ANO_MESSAGE
    PERFORM GESTION-SQLSTATE.
*
*
*----------------------------------------------------------------------*
GESTION-SQLSTATE.
*----------------------------------------------------------------------*
    MOVE 'N' TO FLAG-TROUVE.
    MOVE 'N' TO FLAG-DEADLOCK.
    EVALUATE SQLSTATE
    WHEN '02000'              MOVE 'N' TO FLAG-TROUVE
    WHEN '22002'              MOVE 'O' TO FLAG-TROUVE
    WHEN '00000' THRU '01999' MOVE 'O' TO FLAG-TROUVE
    WHEN '02001' THRU '22001' PERFORM FIN-ANORMALE
    WHEN '22003' THRU 'R1000' PERFORM FIN-ANORMALE
    WHEN 'R1001'              MOVE 'O' TO FLAG-DEADLOCK
    WHEN 'R1002'              MOVE 'O' TO FLAG-DEADLOCK
    WHEN 'R1003' THRU 'S9999' PERFORM FIN-ANORMALE
    END-EVALUATE.
    IF DEADLOCK
       DISPLAY "DEADLOCK " NUM-MSG " " ANO_MESSAGE
*       MOVE "DEADLOCK" TO WS-MES
*       generate detail-line
       MOVE 'O' TO FLAG-REJET
    END-IF.
    
*----------------------------------------------------------------------*
FIN-ANOFERME.
*----------------------------------------------------------------------*
    DISPLAY "FIN-ANOFERME".
    CALL "D00_MSGGES" USING ZONES-PARAMETRES.
    STOP RUN.  

*----------------------------------------------------------------------*
FIN-ANOOPEN.
*----------------------------------------------------------------------*
    DISPLAY "FIN-ANOOPEN".
    CALL "D00_MSGGES" USING ZONES-PARAMETRES.
    STOP RUN.  

*----------------------------------------------------------------------*
FIN-ANORMALE.
*----------------------------------------------------------------------*
    CALL "SYS$PUTMSG" USING RDB$MESSAGE_VECTOR.
    IF NUM-MSG > "2000"
       DISPLAY NUM-MSG " " ANO_MESSAGE " SQLSTATE " SQLSTATE WITH CONVERSION
       WRITE ANO-ENREG
       PERFORM TRT-MSG-SQL
    ELSE	
       DISPLAY NUM-MSG " " ANO_MESSAGE
       CALL "D00_MSGGES" USING ZONES-PARAMETRES
    END-IF.
    PERFORM CLOSE-FILES.
    EXEC SQL 
    ROLLBACK
    END-EXEC.
    STOP RUN.     

*----------------------------------------------------------------------*
TRT-MSG-SQL.
*----------------------------------------------------------------------*
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
 
