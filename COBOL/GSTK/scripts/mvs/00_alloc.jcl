//GSTKALLC JOB (ACCT),'GSTK ALLOC DATASETS',CLASS=A,MSGCLASS=X,
//             MSGLEVEL=(1,1),NOTIFY=&SYSUID,
//             USER=&SYSUID,PASSWORD=&TSO_PASS
//*
//*=============================================================*
//* JOB : GSTKALLC - ALLOCATION DES DATASETS MVS POUR GSTK     *
//* A SOUMETTRE EN PREMIER avant tout upload ou compilation     *
//*                                                             *
//* Adapter HLQ selon votre userid (ex: HERC01)                *
//*=============================================================*
//         SET HLQ=HERC02
//*
//*-------------------------------------------------------------*
//* SOURCE : programmes COBOL (FB 80)
//*-------------------------------------------------------------*
//ALLOCSRC EXEC PGM=IEFBR14
//GSTKSRC  DD DSN=&HLQ..GSTK.SOURCE,
//             DISP=(NEW,CATLG,DELETE),
//             UNIT=SYSDA,
//             SPACE=(TRK,(50,10,50)),
//             DCB=(RECFM=FB,LRECL=80,BLKSIZE=3120,DSORG=PO)
//*
//*-------------------------------------------------------------*
//* BMS : mapsets BMS (FB 80)
//*-------------------------------------------------------------*
//ALLOCBMS EXEC PGM=IEFBR14
//GSTKBMS  DD DSN=&HLQ..GSTK.BMS,
//             DISP=(NEW,CATLG,DELETE),
//             UNIT=SYSDA,
//             SPACE=(TRK,(20,5,20)),
//             DCB=(RECFM=FB,LRECL=80,BLKSIZE=3120,DSORG=PO)
//*
//*-------------------------------------------------------------*
//* COPYLIB : copybooks (FB 80)
//*-------------------------------------------------------------*
//ALLOCCPY EXEC PGM=IEFBR14
//GSTKCPY  DD DSN=&HLQ..GSTK.COPYLIB,
//             DISP=(NEW,CATLG,DELETE),
//             UNIT=SYSDA,
//             SPACE=(TRK,(5,2,10)),
//             DCB=(RECFM=FB,LRECL=80,BLKSIZE=3120,DSORG=PO)
//*
//*-------------------------------------------------------------*
//* JCL : jobs de compilation (FB 80)
//*-------------------------------------------------------------*
//ALLOCJCL EXEC PGM=IEFBR14
//GSTKJCL  DD DSN=&HLQ..GSTK.JCL,
//             DISP=(NEW,CATLG,DELETE),
//             UNIT=SYSDA,
//             SPACE=(TRK,(5,2,10)),
//             DCB=(RECFM=FB,LRECL=80,BLKSIZE=3120,DSORG=PO)
//*
//*-------------------------------------------------------------*
//* LOADLIB : load modules compilés (U)
//*-------------------------------------------------------------*
//ALLOCLOD EXEC PGM=IEFBR14
//GSTKLOD  DD DSN=&HLQ..GSTK.LOADLIB,
//             DISP=(NEW,CATLG,DELETE),
//             UNIT=SYSDA,
//             SPACE=(CYL,(5,2,30)),
//             DCB=(RECFM=U,BLKSIZE=32760,DSORG=PO)
//*
//*-------------------------------------------------------------*
//* DBRMLIB : DB2 Database Request Modules (FB 80)
//*-------------------------------------------------------------*
//ALLOCDBR EXEC PGM=IEFBR14
//GSTKDBR  DD DSN=&HLQ..GSTK.DBRMLIB,
//             DISP=(NEW,CATLG,DELETE),
//             UNIT=SYSDA,
//             SPACE=(TRK,(10,5,20)),
//             DCB=(RECFM=FB,LRECL=80,BLKSIZE=3120,DSORG=PO)
//*
//* FIN GSTKALLC
