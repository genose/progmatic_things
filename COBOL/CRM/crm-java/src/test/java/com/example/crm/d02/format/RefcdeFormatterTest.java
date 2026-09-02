package com.example.crm.d02.format;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests T24, T25, T12 — format du REFCDE.
 * Vérifie les positions absolues et les comportements spéciaux.
 */
public class RefcdeFormatterTest {

    /** Construit un formatteur avec des valeurs minimales pour tester une propriété. */
    private RefcdeFormatter minimal() {
        return new RefcdeFormatter(
            "260820",                          // datcde  YYMMDD
            "CDELAB0000000000000000",           // cdelab  22 chars
            "REFCDE0000000000000000000000000",  // refcde  35 chars
            "OR",                              // typcde
            123L,                             // clicsp
            "COD",                            // codope
            "NUMDOC0001",                     // numdoc  10 chars
            "S",                              // cdesaisie
            "20-AUG-2026 10:30:00.00",        // datrec  23 chars
            "N",                              // trafic
            "20260820",                       // datebp  YYYYMMDD
            "CODSAI1",                        // codsai  7 chars
            "CSTR",                           // codstr  4 chars
            "SEXP",                           // strexp  4 chars
            "CODREP01"                        // codrep  8 chars
        );
    }

    @Test
    public void recordIs197Characters() {
        assertEquals(197, minimal().format().length());
    }

    @Test
    public void typmes_isRefcde() {
        assertEquals("REFCDE", minimal().format().substring(0, 6));
    }

    @Test
    public void filler_pos7_isSpace() {
        assertEquals(' ', minimal().format().charAt(6));
    }

    @Test
    public void datcde_pos8To13() {
        // pos 8-13 (idx 7-12)
        assertEquals("260820", minimal().format().substring(7, 13));
    }

    @Test
    public void filler_pos14_isSpace() {
        assertEquals(' ', minimal().format().charAt(13));
    }

    @Test
    public void cdelab_pos15To36() {
        // pos 15-36 (idx 14-35) — 22 chars
        assertEquals("CDELAB0000000000000000", minimal().format().substring(14, 36));
    }

    // T24 — astuce NOMLIV : CLICSP 123 → "123   " + 29 espaces
    @Test
    public void nomliv_pos84To118_clicsp123_givesShiftedValue() {
        // pos 84-118 (idx 83-117) — 35 chars
        String nomliv = minimal().format().substring(83, 118);
        assertEquals(35, nomliv.length());
        // "000123" → MOVE NOMLIV(4:6) TO NOMLIV → "123   " + 29 espaces
        assertEquals("123", nomliv.substring(0, 3));
        // Le reste doit être des espaces
        assertEquals("                                ", nomliv.substring(3));
    }

    @Test
    public void nomliv_clicsp0_givesZerosShifted() {
        RefcdeFormatter f = new RefcdeFormatter(
            "260820", "CDELAB0000000000000000",
            "REFCDE0000000000000000000000000", "OR", 0L,
            "COD", "NUMDOC0001", "S",
            "20-AUG-2026 10:30:00.00", "N", "20260820",
            "CODSAI1", "CSTR", "SEXP", "CODREP01");
        String nomliv = f.format().substring(83, 118);
        // "000000" → "000   " + 29 espaces
        assertEquals("000   ", nomliv.substring(0, 6));
    }

    // T25 — DATCDE doit être YYMMDD (6 chars), pas YYYYMMDD
    @Test
    public void datcde_isYyMmDd_notYyyyMmDd() {
        String datcde = minimal().format().substring(7, 13);
        assertEquals(6, datcde.length());
        assertEquals("260820", datcde); // "2026-08-20" → "260820"
    }

    // T12 — TRAFIC vide → 'N'
    @Test
    public void trafic_empty_givesN() {
        RefcdeFormatter f = new RefcdeFormatter(
            "260820", "CDELAB0000000000000000",
            "REFCDE0000000000000000000000000", "OR", 123L,
            "COD", "NUMDOC0001", "S",
            "20-AUG-2026 10:30:00.00", "",   // trafic vide
            "20260820", "CODSAI1", "CSTR", "SEXP", "CODREP01");
        // pos 161 (idx 160)
        assertEquals('N', f.format().charAt(160));
    }

    @Test
    public void trafic_null_givesN() {
        RefcdeFormatter f = new RefcdeFormatter(
            "260820", "CDELAB0000000000000000",
            "REFCDE0000000000000000000000000", "OR", 123L,
            "COD", "NUMDOC0001", "S",
            "20-AUG-2026 10:30:00.00", null,   // trafic null
            "20260820", "CODSAI1", "CSTR", "SEXP", "CODREP01");
        assertEquals('N', f.format().charAt(160));
    }

    @Test
    public void trafic_presentValue_isUsed() {
        RefcdeFormatter f = new RefcdeFormatter(
            "260820", "CDELAB0000000000000000",
            "REFCDE0000000000000000000000000", "OR", 123L,
            "COD", "NUMDOC0001", "S",
            "20-AUG-2026 10:30:00.00", "E",
            "20260820", "CODSAI1", "CSTR", "SEXP", "CODREP01");
        assertEquals('E', f.format().charAt(160));
    }

    @Test
    public void datrec_pos137To159() {
        // pos 137-159 (idx 136-158) — 23 chars
        assertEquals("20-AUG-2026 10:30:00.00", minimal().format().substring(136, 159));
    }

    @Test
    public void datebp_pos163To170() {
        // pos 163-170 (idx 162-169) — 8 chars YYYYMMDD
        assertEquals("20260820", minimal().format().substring(162, 170));
    }

    @Test
    public void codrep_pos190To197() {
        // pos 190-197 (idx 189-196) — 8 chars
        assertEquals("CODREP01", minimal().format().substring(189, 197));
    }

    @Test
    public void fillersBetweenFields_areSpaces() {
        String r = minimal().format();
        // Vérification des FILLER connus
        assertEquals(' ', r.charAt(13));  // pos 14
        assertEquals(' ', r.charAt(36));  // pos 37
        assertEquals(' ', r.charAt(72));  // pos 73
        assertEquals(' ', r.charAt(75));  // pos 76
        assertEquals(' ', r.charAt(82));  // pos 83
        assertEquals(' ', r.charAt(118)); // pos 119 ENR-REFCDE-FILLER
        assertEquals(' ', r.charAt(122)); // pos 123
        assertEquals(' ', r.charAt(133)); // pos 134
        assertEquals(' ', r.charAt(135)); // pos 136
        assertEquals(' ', r.charAt(159)); // pos 160
        assertEquals(' ', r.charAt(161)); // pos 162 — after TRAFIC
        assertEquals(' ', r.charAt(170)); // pos 171
        assertEquals(' ', r.charAt(178)); // pos 179
        assertEquals(' ', r.charAt(183)); // pos 184
        assertEquals(' ', r.charAt(188)); // pos 189
    }
}
