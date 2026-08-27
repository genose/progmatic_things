package com.example.crm.d05.domain;

/**
 * Règles d'équivalence de statuts entre BD_DEPOT et BD_CRM (B-D05-03).
 *
 * Statuts équivalents (considérés comme synchronisés) :
 *   - Tout statut identique (FAX == FAX, etc.)
 *   - FAT (DEPOT) == FAP (CRM)
 *   - GLT (DEPOT) == GLP (CRM)
 *
 * La comparaison est directionnelle : statutDepot est la valeur COBOL,
 * statutCrm est STATENCOURS lu dans S.CDE_FAC.
 */
public final class StatutEquivalence {

    private StatutEquivalence() {}

    public static boolean areEquivalent(String statutDepot, String statutCrm) {
        if (statutDepot.equals(statutCrm)) return true;
        if ("FAT".equals(statutDepot) && "FAP".equals(statutCrm)) return true;
        if ("GLT".equals(statutDepot) && "GLP".equals(statutCrm)) return true;
        return false;
    }
}
