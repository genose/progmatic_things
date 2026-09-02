package com.example.crm.d05intcde.application;

import com.example.crm.d05intcde.domain.CdeFacKey;
import com.example.crm.d05intcde.domain.CdeFacRecord;
import com.example.crm.d05intcde.domain.DateExtracts;
import com.example.crm.d05intcde.port.BdDepotCdePort;
import com.example.crm.d05intcde.port.CdeFacRepository;
import com.example.crm.d05intcde.port.CdeFacRepository.StatutResult;

import com.example.crm.d02.adapter.vmsdate.VmsDateCodec;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Orchestration principale de D05_INTCDEFAC_CRM_V2.
 *
 * Regles preservees :
 *   B-D05I-01 : NUMCDE=0 → skip
 *   B-D05I-02 : statut terminal → skip (sauf exception 3010)
 *   B-D05I-03 : CODLAB='9994' → updates D.CDE conditionnels
 *   B-D05I-04 : COMMIT par enregistrement
 *   B-D05I-05 : DATFAC/DATECH vide → sentinel 1858-11-17
 *   B-D05I-06 : UPSERT via select puis insert ou update
 */
public final class IntCdeFacService {

    private final CdeFacRepository crmRepo;
    private final BdDepotCdePort   depotPort;
    private final DateExtractor    dateExtractor;
    private final StatutFilter     statutFilter;

    public IntCdeFacService(CdeFacRepository crmRepo, BdDepotCdePort depotPort) {
        this.crmRepo       = crmRepo;
        this.depotPort     = depotPort;
        this.dateExtractor = new DateExtractor();
        this.statutFilter  = new StatutFilter();
    }

    /**
     * Traite la liste des enregistrements FIC-CDEFAC.
     * Equivalent de la boucle LECTURE-FICCDEFAC UNTIL FINFIC.
     */
    public void traiter(List<CdeFacRecord> records) {
        for (CdeFacRecord rec : records) {
            traiterRecord(rec);
        }
        crmRepo.commit(); // COMMIT final
    }

    void traiterRecord(CdeFacRecord rec) {
        // B-D05I-01 : NUMCDE = 0 → skip silencieux
        if (rec.numcde == 0L) {
            return;
        }

        // verif-statut-final
        CdeFacKey key = new CdeFacKey(rec.codlab, rec.coddep, rec.numcde, rec.numral);
        StatutResult statut = crmRepo.selectStatut(key);

        // B-D05I-02 : statut terminal → skip sauf exception 3010
        if (!statut.absent && !statutFilter.doitTraiter(statut.wsStatencours, statut.wsCptfac, rec)) {
            return;
        }

        // B-D05I-03 : CODLAB='9994' → updates conditionnels D.CDE
        if ("9994".equals(rec.codlab)) {
            traiterCodlab9994(rec);
        }

        // TRAITEMENT-DATES : conversion VMS ASCII → Timestamp Java
        String datfacEffectif = dateExtractor.substituerSentinelle(rec.datfac);   // B-D05I-05
        String datechEffectif = dateExtractor.substituerSentinelle(rec.datech);   // B-D05I-05

        Timestamp tsCde   = parseVms(rec.datcde);
        Timestamp tsBl    = parseVms(rec.datebl);
        Timestamp tsSai   = parseVms(rec.datsai);
        Timestamp tsFac   = parseVms(datfacEffectif);
        Timestamp tsEch   = parseVms(datechEffectif);
        Timestamp tsDtlivs = parseVms(rec.dtlivs);
        Timestamp tsDtlivr = parseVms(rec.dtlivr);

        // EXTRACTION-DATE
        DateExtracts extCde = dateExtractor.extraire(rec.datcde);
        DateExtracts extBl  = dateExtractor.extraire(rec.datebl);
        DateExtracts extFac = dateExtractor.extraire(datfacEffectif);

        // B-D05I-06 : UPSERT
        if (statut.absent) {
            crmRepo.insert(rec, extCde, extBl, extFac, tsCde, tsBl, tsSai, tsFac, tsEch, tsDtlivs, tsDtlivr);
        } else {
            crmRepo.update(rec, extCde, extBl, extFac, tsCde, tsBl, tsSai, tsFac, tsEch, tsDtlivs, tsDtlivr);
        }

        crmRepo.commit(); // B-D05I-04 : commit par enregistrement
    }

    private void traiterCodlab9994(CdeFacRecord rec) {
        Timestamp tsDtlivr = parseVms(rec.dtlivr);
        Timestamp tsDatebl = parseVms(rec.datebl);

        if ("O".equals(rec.flagliv)) {
            depotPort.updateCdeLiv(rec.numcde, rec.numral, tsDtlivr);
        }
        if ("O".equals(rec.flagfac)) {
            depotPort.updateCdeFac(rec.numcde, rec.numral, tsDatebl);
        }
        if ("O".equals(rec.flagblo)) {
            depotPort.updateCdeBlo(rec.numcde, rec.numral);
        }
        if ("O".equals(rec.flagsup) && "N".equals(rec.flagfac)) {
            depotPort.updateCdeSup(rec.numcde, rec.numral);
        }
    }

    private static final VmsDateCodec VMS_CODEC = new VmsDateCodec();

    /**
     * Conversion VMS ASCII "DD-MON-YYYY HH:MM:SS.CC" → java.sql.Timestamp.
     * Reutilise VmsDateCodec depuis d02.
     */
    private Timestamp parseVms(String vmsDate) {
        if (vmsDate == null || vmsDate.trim().isEmpty()) {
            return Timestamp.valueOf("1858-11-17 00:00:00");
        }
        try {
            LocalDateTime ldt = VMS_CODEC.fromVmsAscii(vmsDate);
            return Timestamp.valueOf(ldt);
        } catch (Exception e) {
            return Timestamp.valueOf("1858-11-17 00:00:00");
        }
    }
}
