package com.example.crm.d05intcde.application;

import com.example.crm.d05intcde.domain.DateExtracts;
import java.util.HashMap;
import java.util.Map;

/**
 * Extrait annee, mois (01-12) et trimestre (1-4) d'une date VMS ASCII.
 * Format source : "DD-MON-YYYY HH:MM:SS.CC"
 *
 * Regle B-D05I-07 : trimestre = 1 si mois < 04 ; 2 si < 07 ; 3 si < 10 ; 4 sinon.
 * Regle B-D05I-05 : champ vide → sentinel '17-NOV-1858' (epoch zero Oracle Rdb).
 */
public final class DateExtractor {

    public static final String SENTINEL_VMS = "17-NOV-1858 00:00:00.00";

    private static final Map<String, String> MOIS_MAP = new HashMap<>();
    static {
        MOIS_MAP.put("JAN", "01"); MOIS_MAP.put("FEB", "02"); MOIS_MAP.put("MAR", "03");
        MOIS_MAP.put("APR", "04"); MOIS_MAP.put("MAY", "05"); MOIS_MAP.put("JUN", "06");
        MOIS_MAP.put("JUL", "07"); MOIS_MAP.put("AUG", "08"); MOIS_MAP.put("SEP", "09");
        MOIS_MAP.put("OCT", "10"); MOIS_MAP.put("NOV", "11"); MOIS_MAP.put("DEC", "12");
    }

    /**
     * Extrait annee/mois/trimestre d'une date VMS "DD-MON-YYYY HH:MM:SS.CC".
     * Si la date est vide ou nulle, retourne les extraits de la sentinel 1858.
     */
    public DateExtracts extraire(String vmsDate) {
        String d = (vmsDate == null || vmsDate.trim().isEmpty()) ? SENTINEL_VMS : vmsDate;
        // "DD-MON-YYYY ..." → annee = chars 7-10 (index 7, longueur 4)
        String annee = d.length() >= 11 ? d.substring(7, 11) : "0000";
        // mois abreviation = chars 3-5 (index 3, longueur 3)
        String monAbbr = d.length() >= 6 ? d.substring(3, 6) : "JAN";
        String mois = MOIS_MAP.getOrDefault(monAbbr, "01");
        String trimestre = trimestreDe(mois);
        return new DateExtracts(annee, mois, trimestre);
    }

    /**
     * Substitue la sentinel Oracle Rdb si la date est vide (regle B-D05I-05).
     * Utilise pour DATFAC et DATECH avant passage a SYS$BINTIM / JDBC.
     */
    public String substituerSentinelle(String vmsDate) {
        if (vmsDate == null || vmsDate.trim().isEmpty()) {
            return SENTINEL_VMS;
        }
        return vmsDate;
    }

    private static String trimestreDe(String mois) {
        if (mois.compareTo("04") < 0) return "1";
        if (mois.compareTo("07") < 0) return "2";
        if (mois.compareTo("10") < 0) return "3";
        return "4";
    }
}
