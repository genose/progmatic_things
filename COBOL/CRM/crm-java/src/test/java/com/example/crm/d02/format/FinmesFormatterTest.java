package com.example.crm.d02.format;

import org.junit.Test;
import static org.junit.Assert.*;

/** Tests (T01 partiel) — format du FINMES. */
public class FinmesFormatterTest {

    @Test
    public void recordIs197Characters() {
        assertEquals(197, new FinmesFormatter(0L).format().length());
    }

    @Test
    public void typmes_isFinmes() {
        assertEquals("FINMES", new FinmesFormatter(0L).format().substring(0, 6));
    }

    @Test
    public void filler_pos7_isSpace() {
        assertEquals(' ', new FinmesFormatter(0L).format().charAt(6));
    }

    @Test
    public void nbcde_pos8To15_zeroPadded() {
        // pos 8-15 (idx 7-14) — 8 chiffres
        assertEquals("00000000", new FinmesFormatter(0L).format().substring(7, 15));
    }

    @Test
    public void nbcde_pos8To15_withValue() {
        // Test issu de T01/guide §11 : shouldWriteFinmesWithCorrectOrderCount
        String r = new FinmesFormatter(2L).format();
        assertEquals("00000002", r.substring(7, 15));
    }

    @Test
    public void nbcde_maxValue() {
        String r = new FinmesFormatter(99_999_999L).format();
        assertEquals("99999999", r.substring(7, 15));
    }

    @Test
    public void pos16To112_areSpaces() {
        String r = new FinmesFormatter(5L).format();
        // pos 16-112 (idx 15-111)
        for (int i = 15; i < 112; i++) {
            assertEquals("Pos " + (i + 1), ' ', r.charAt(i));
        }
    }

    @Test
    public void pos113To197_areSpaces() {
        String r = new FinmesFormatter(5L).format();
        for (int i = 112; i < 197; i++) {
            assertEquals("Pos " + (i + 1), ' ', r.charAt(i));
        }
    }
}
