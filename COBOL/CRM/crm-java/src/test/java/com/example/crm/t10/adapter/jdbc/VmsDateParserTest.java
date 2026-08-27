package com.example.crm.t10.adapter.jdbc;

import org.junit.Test;

import java.sql.Timestamp;
import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Tests unitaires de VmsDateParser.
 *
 * Vérifie la conversion du format VMS ASCII "DD-MON-YYYY HH:MM:SS.CC"
 * vers java.sql.Timestamp (B-T10-01).
 */
public class VmsDateParserTest {

    @Test
    public void parse_dateNormale_anneeMoisJourExacts() {
        Timestamp ts = VmsDateParser.parse("08-AUG-2024 00:00:00.00");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());

        assertEquals(2024, cal.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, cal.get(Calendar.MONTH));
        assertEquals(8, cal.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void parse_heureMinuteSecondeCentiemes_exacts() {
        Timestamp ts = VmsDateParser.parse("15-JAN-2024 14:30:45.75");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());

        assertEquals(14, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, cal.get(Calendar.MINUTE));
        assertEquals(45, cal.get(Calendar.SECOND));
        // 75 centièmes = 750 ms
        assertEquals(750, cal.get(Calendar.MILLISECOND));
    }

    @Test
    public void parse_tousMois_acceptes() {
        String[] mois = {"JAN","FEB","MAR","APR","MAY","JUN",
                         "JUL","AUG","SEP","OCT","NOV","DEC"};
        int[] numerosAttendus = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        for (int i = 0; i < mois.length; i++) {
            Timestamp ts = VmsDateParser.parse("01-" + mois[i] + "-2024 00:00:00.00");
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(ts.getTime());
            assertEquals("Mois " + mois[i], numerosAttendus[i] - 1, cal.get(Calendar.MONTH));
        }
    }

    @Test
    public void parse_centieresZero_millisecondesZero() {
        Timestamp ts = VmsDateParser.parse("25-DEC-2023 10:00:00.00");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());
        assertEquals(0, cal.get(Calendar.MILLISECOND));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_chaineNull_leveException() {
        VmsDateParser.parse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_chaineTropCourte_leveException() {
        VmsDateParser.parse("08-AUG-2024");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_moisInconnu_leveException() {
        VmsDateParser.parse("01-XXX-2024 00:00:00.00");
    }
}
