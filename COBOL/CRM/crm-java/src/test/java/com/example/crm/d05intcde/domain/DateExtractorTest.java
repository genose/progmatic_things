package com.example.crm.d05intcde.domain;

import com.example.crm.d05intcde.application.DateExtractor;
import org.junit.Test;
import static org.junit.Assert.*;

public class DateExtractorTest {

    private final DateExtractor extractor = new DateExtractor();

    // --- extraire ---

    @Test
    public void extraire_jan_donne_mois01_et_trimestre1() {
        DateExtracts ex = extractor.extraire("15-JAN-2024 08:00:00.00");
        assertEquals("2024", ex.annee);
        assertEquals("01",   ex.mois);
        assertEquals("1",    ex.trimestre);
    }

    @Test
    public void extraire_mar_donne_trimestre1() {
        assertEquals("1", extractor.extraire("01-MAR-2024 00:00:00.00").trimestre);
    }

    @Test
    public void extraire_apr_donne_trimestre2() {
        assertEquals("2", extractor.extraire("01-APR-2024 00:00:00.00").trimestre);
    }

    @Test
    public void extraire_jun_donne_trimestre2() {
        assertEquals("2", extractor.extraire("30-JUN-2024 00:00:00.00").trimestre);
    }

    @Test
    public void extraire_jul_donne_trimestre3() {
        assertEquals("3", extractor.extraire("01-JUL-2024 00:00:00.00").trimestre);
    }

    @Test
    public void extraire_sep_donne_trimestre3() {
        assertEquals("3", extractor.extraire("30-SEP-2024 00:00:00.00").trimestre);
    }

    @Test
    public void extraire_oct_donne_trimestre4() {
        assertEquals("4", extractor.extraire("01-OCT-2024 00:00:00.00").trimestre);
    }

    @Test
    public void extraire_dec_donne_trimestre4() {
        assertEquals("4", extractor.extraire("31-DEC-2024 00:00:00.00").trimestre);
    }

    @Test
    public void extraire_annee_correcte() {
        assertEquals("1999", extractor.extraire("01-JAN-1999 00:00:00.00").annee);
    }

    // --- substituerSentinelle (B-D05I-05) ---

    @Test
    public void sentinelle_vide_substitue() {
        assertEquals(DateExtractor.SENTINEL_VMS, extractor.substituerSentinelle(""));
    }

    @Test
    public void sentinelle_null_substitue() {
        assertEquals(DateExtractor.SENTINEL_VMS, extractor.substituerSentinelle(null));
    }

    @Test
    public void sentinelle_espaces_substitue() {
        assertEquals(DateExtractor.SENTINEL_VMS, extractor.substituerSentinelle("   "));
    }

    @Test
    public void sentinelle_date_valide_inchangee() {
        String d = "15-JAN-2024 08:00:00.00";
        assertEquals(d, extractor.substituerSentinelle(d));
    }
}
