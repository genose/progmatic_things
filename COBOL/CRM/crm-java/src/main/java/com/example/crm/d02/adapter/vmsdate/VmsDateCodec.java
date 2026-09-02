package com.example.crm.d02.adapter.vmsdate;

import com.example.crm.d02.port.LegacyDateCodec;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Implémentation de LegacyDateCodec reproduisant le comportement des fonctions
 * VMS SYS$ASCTIM / SYS$BINTIM et du code COBOL de conversion de dates.
 *
 * Format VMS ASCII : "DD-MON-YYYY HH:MI:SS.CC"  (longueur exacte 23 caractères)
 *
 * Mois VMS (identiques à ceux de SYS$ASCTIM) :
 *   JAN FEB MAR APR MAY JUN JUL AUG SEP OCT NOV DEC
 */
public final class VmsDateCodec implements LegacyDateCodec {

    private static final String[] MONTHS_VMS = {
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
        "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    };

    private static final Map<String, Integer> MONTH_TO_NUM;
    static {
        MONTH_TO_NUM = new HashMap<>();
        for (int i = 0; i < MONTHS_VMS.length; i++) {
            MONTH_TO_NUM.put(MONTHS_VMS[i], i + 1);
        }
    }

    /**
     * LocalDateTime → "DD-MON-YYYY HH:MI:SS.CC"
     * Exemple : 2026-08-20T10:30:05.070000000 → "20-AUG-2026 10:30:05.07"
     */
    @Override
    public String toVmsAscii(LocalDateTime dt) {
        int cc = dt.getNano() / 10_000_000; // centisecondes
        return String.format("%02d-%s-%04d %02d:%02d:%02d.%02d",
            dt.getDayOfMonth(),
            MONTHS_VMS[dt.getMonthValue() - 1],
            dt.getYear(),
            dt.getHour(),
            dt.getMinute(),
            dt.getSecond(),
            cc);
    }

    /**
     * "DD-MON-YYYY HH:MI:SS.CC" → LocalDateTime
     * Exemple : "20-AUG-2026 10:30:05.07" → LocalDateTime(2026, 8, 20, 10, 30, 5, 70_000_000)
     */
    @Override
    public LocalDateTime fromVmsAscii(String vmsAscii) {
        if (vmsAscii == null || vmsAscii.length() < 23) {
            throw new IllegalArgumentException("Date VMS invalide : " + vmsAscii);
        }
        int    day  = Integer.parseInt(vmsAscii.substring( 0,  2).trim());
        String mon  = vmsAscii.substring( 3,  6);
        int    year = Integer.parseInt(vmsAscii.substring( 7, 11));
        int    hour = Integer.parseInt(vmsAscii.substring(12, 14));
        int    min  = Integer.parseInt(vmsAscii.substring(15, 17));
        int    sec  = Integer.parseInt(vmsAscii.substring(18, 20));
        int    cc   = Integer.parseInt(vmsAscii.substring(21, 23));

        Integer month = MONTH_TO_NUM.get(mon.toUpperCase());
        if (month == null) {
            throw new IllegalArgumentException("Mois VMS inconnu : " + mon);
        }
        return LocalDateTime.of(year, month, day, hour, min, sec, cc * 10_000_000);
    }

    /**
     * Retourne "YYYYMMDD" depuis un LocalDateTime.
     *
     * Reproduit la logique COBOL :
     *   SYS$ASCTIM → W-DATE-ASCII "DD-MON-YYYY ..."
     *   YYYY (chars 8-11 COBOL = idx 7-10) → positions 1-4 résultat
     *   MON  (chars 4-6  COBOL = idx 3-5)  → table → 2 chiffres → positions 5-6
     *   DD   (chars 1-2  COBOL = idx 0-1)  → positions 7-8
     *   INSPECT : remplacer espaces par '0' (ex : jour ' 5' → '05')
     */
    @Override
    public String toYyyyMmDd(LocalDateTime dt) {
        return String.format("%04d%02d%02d",
            dt.getYear(),
            dt.getMonthValue(),
            dt.getDayOfMonth());
    }

    /**
     * Retourne "YYMMDD" (6 caractères, siècle ignoré).
     * Correspond à ficDatecde.substring(2, 8) en Java.
     * Utilisé pour ENR-REFCDE-DATCDE (comportement B10).
     */
    @Override
    public String toYyMmDd(LocalDateTime dt) {
        return toYyyyMmDd(dt).substring(2); // "YYYYMMDD" → "YYMMDD"
    }
}
