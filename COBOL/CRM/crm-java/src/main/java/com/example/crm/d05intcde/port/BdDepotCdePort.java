package com.example.crm.d05intcde.port;

import java.sql.Timestamp;

/**
 * Port acces BD_DEPOT.D.CDE pour les mises a jour conditionnelles CODLAB 9994.
 * Regle B-D05I-03.
 */
public interface BdDepotCdePort {

    /** UPDATE D.CDE SET DTLIVR=ts WHERE NUMCDE=n AND NUMRAL=r AND CODLAB='9994'. */
    void updateCdeLiv(long numcde, int numral, Timestamp dtlivr);

    /** UPDATE D.CDE SET DATEBL=ts, STATUT='FAT' WHERE NUMCDE=n AND NUMRAL=r AND CODLAB='9994'. */
    void updateCdeFac(long numcde, int numral, Timestamp datebl);

    /** UPDATE D.CDE SET STATUT='BLO' WHERE NUMCDE=n AND NUMRAL=r AND CODLAB='9994'. */
    void updateCdeBlo(long numcde, int numral);

    /** UPDATE D.CDE SET STATUT='SEI' WHERE NUMCDE=n AND NUMRAL=r AND CODLAB='9994'. */
    void updateCdeSup(long numcde, int numral);
}
