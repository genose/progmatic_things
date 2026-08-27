package com.example.crm.t10.adapter.jdbc;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Convertit une date VMS ASCII en java.sql.Timestamp pour Oracle Rdb JDBC.
 *
 * Format attendu : "DD-MON-YYYY HH:MM:SS.CC" (24 chars)
 *   ex : "08-AUG-2024 00:00:00.00"
 *
 * Dans COBOL T10, SYS$BINTIM convertit cette même chaîne en quadword VMS
 * binaire (100 ns depuis 17-NOV-1858), qui est ensuite stocké dans DTLIVR
 * via SQL (B-T10-01). L'adapter Java délègue la conversion de type au
 * driver Oracle Rdb JDBC en passant un java.sql.Timestamp.
 */
public final class VmsDateParser {

    private static final Map<String, Integer> MONTHS = new HashMap<String, Integer>();
    static {
        MONTHS.put("JAN",  1); MONTHS.put("FEB",  2); MONTHS.put("MAR",  3);
        MONTHS.put("APR",  4); MONTHS.put("MAY",  5); MONTHS.put("JUN",  6);
        MONTHS.put("JUL",  7); MONTHS.put("AUG",  8); MONTHS.put("SEP",  9);
        MONTHS.put("OCT", 10); MONTHS.put("NOV", 11); MONTHS.put("DEC", 12);
    }

    private VmsDateParser() {}

    /**
     * @param vmsDate chaîne "DD-MON-YYYY HH:MM:SS.CC" (24 chars)
     * @return java.sql.Timestamp correspondant
     * @throws IllegalArgumentException si le format est invalide
     */
    public static Timestamp parse(String vmsDate) {
        if (vmsDate == null || vmsDate.length() < 23) {
            throw new IllegalArgumentException("Date VMS invalide : [" + vmsDate + "]");
        }
        // "DD-MON-YYYY HH:MM:SS.CC"
        //  0123456789012345678901 23
        int    day   = Integer.parseInt(vmsDate.substring(0, 2).trim());
        String mon   = vmsDate.substring(3, 6).toUpperCase();
        int    year  = Integer.parseInt(vmsDate.substring(7, 11));
        int    hour  = Integer.parseInt(vmsDate.substring(12, 14));
        int    min   = Integer.parseInt(vmsDate.substring(15, 17));
        int    sec   = Integer.parseInt(vmsDate.substring(18, 20));
        int    hund  = Integer.parseInt(vmsDate.substring(21, 23)); // centièmes

        Integer month = MONTHS.get(mon);
        if (month == null) {
            throw new IllegalArgumentException("Mois VMS inconnu : " + mon);
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,         year);
        cal.set(Calendar.MONTH,        month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY,  hour);
        cal.set(Calendar.MINUTE,       min);
        cal.set(Calendar.SECOND,       sec);
        cal.set(Calendar.MILLISECOND,  hund * 10);
        return new Timestamp(cal.getTimeInMillis());
    }
}
