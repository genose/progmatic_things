package com.example.crm.d05intcde.application;

import com.example.crm.d05intcde.domain.CdeFacRecord;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Regle B-D05I-02 : statut terminal → skip, sauf exception CODLAB 3010.
 *
 * Statuts terminaux : FAT, GLT, FAP, GLP.
 * Exception : CODLAB='3010' AND cptfac > 20000 AND ws-cptfac = 0 → traitement continue.
 */
public final class StatutFilter {

    private static final Set<String> TERMINAUX = new HashSet<>(
        Arrays.asList("FAT", "GLT", "FAP", "GLP")
    );

    /**
     * @param wsStatencours statut actuel dans S.CDE_FAC (lu par verif-statut-final)
     * @param wsCptfac      cptfac actuel dans S.CDE_FAC
     * @param rec           enregistrement FIC-CDEFAC courant
     * @return true si l'enregistrement doit etre traite, false si skip
     */
    public boolean doitTraiter(String wsStatencours, long wsCptfac, CdeFacRecord rec) {
        if (!TERMINAUX.contains(wsStatencours)) {
            return true;
        }
        // statut terminal — exception CODLAB 3010
        return "3010".equals(rec.codlab)
            && rec.cptfac > 20000L
            && wsCptfac == 0L;
    }

    public static boolean estTerminal(String statut) {
        return TERMINAUX.contains(statut);
    }
}
