package com.example.crm.d05intcde.port;

import com.example.crm.d05intcde.domain.CdeFacKey;
import com.example.crm.d05intcde.domain.CdeFacRecord;
import com.example.crm.d05intcde.domain.DateExtracts;
import java.sql.Timestamp;

/**
 * Port acces BD_CRM.S.CDE_FAC.
 *
 * - selectStatut : verif-statut-final (determine ACREER ou UPDATE)
 * - insert       : CREATION-CDEFAC
 * - update       : UPDATE-CDEFAC
 */
public interface CdeFacRepository {

    /**
     * Resultat de verif-statut-final.
     * absent=true si SQLSTATE='02000' (NOT FOUND).
     */
    final class StatutResult {
        public final boolean absent;
        public final String  wsStatencours;
        public final long    wsCptfac;

        public StatutResult(boolean absent, String wsStatencours, long wsCptfac) {
            this.absent        = absent;
            this.wsStatencours = wsStatencours;
            this.wsCptfac      = wsCptfac;
        }
    }

    StatutResult selectStatut(CdeFacKey key);

    /** INSERT INTO S.CDE_FAC (80+ colonnes, dont FLAGSTD, FLAGDET, montants a zero). */
    void insert(CdeFacRecord rec, DateExtracts extCde, DateExtracts extBl, DateExtracts extFac,
                Timestamp tsCde, Timestamp tsBl, Timestamp tsSai,
                Timestamp tsFac, Timestamp tsEch, Timestamp tsDtlivs, Timestamp tsDtlivr);

    /** UPDATE S.CDE_FAC (70+ colonnes, sans FLAGSTD ni FLAGDET — regle B-D05I-10). */
    void update(CdeFacRecord rec, DateExtracts extCde, DateExtracts extBl, DateExtracts extFac,
                Timestamp tsCde, Timestamp tsBl, Timestamp tsSai,
                Timestamp tsFac, Timestamp tsEch, Timestamp tsDtlivs, Timestamp tsDtlivr);

    void commit();
    void rollback();
}
