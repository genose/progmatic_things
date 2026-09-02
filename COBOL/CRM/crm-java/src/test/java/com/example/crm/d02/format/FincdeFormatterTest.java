package com.example.crm.d02.format;

import org.junit.Test;
import static org.junit.Assert.*;

/** Tests — format du FINCDE. */
public class FincdeFormatterTest {

    @Test
    public void recordIs197Characters() {
        assertEquals(197, new FincdeFormatter(0L, 0L).format().length());
    }

    @Test
    public void typmes_isFincde() {
        assertEquals("FINCDE", new FincdeFormatter(0L, 0L).format().substring(0, 6));
    }

    @Test
    public void sumqte_pos8To15() {
        // pos 8-15 (idx 7-14) — 8 chiffres
        String r = new FincdeFormatter(12345678L, 0L).format();
        assertEquals("12345678", r.substring(7, 15));
    }

    @Test
    public void sumqte_zeroPadded() {
        String r = new FincdeFormatter(42L, 0L).format();
        assertEquals("00000042", r.substring(7, 15));
    }

    @Test
    public void filler_pos16_isSpace() {
        assertEquals(' ', new FincdeFormatter(1L, 2L).format().charAt(15));
    }

    @Test
    public void nblig_pos17To24() {
        // pos 17-24 (idx 16-23) — 8 chiffres
        String r = new FincdeFormatter(0L, 99L).format();
        assertEquals("00000099", r.substring(16, 24));
    }

    @Test
    public void filler_pos25To121_areSpaces() {
        String r = new FincdeFormatter(1L, 2L).format();
        // pos 25-121 (idx 24-120)
        for (int i = 24; i < 121; i++) {
            assertEquals("Pos " + (i + 1), ' ', r.charAt(i));
        }
    }

    @Test
    public void pos122To197_areSpaces() {
        String r = new FincdeFormatter(1L, 2L).format();
        for (int i = 121; i < 197; i++) {
            assertEquals("Pos " + (i + 1), ' ', r.charAt(i));
        }
    }
}
