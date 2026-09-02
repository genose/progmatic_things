package com.example.crm.d02.adapter;

import com.example.crm.d02.adapter.vmsdate.VmsDateCodec;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Tests de conversion des dates VMS (LegacyDateCodec).
 * Vérifie la parité avec le comportement COBOL SYS$ASCTIM / SYS$BINTIM.
 */
public class VmsDateCodecTest {

    private VmsDateCodec codec;

    @Before
    public void setUp() {
        codec = new VmsDateCodec();
    }

    // ── toVmsAscii ────────────────────────────────────────────────────────────

    @Test
    public void toVmsAscii_formatsCorrectly() {
        LocalDateTime dt = LocalDateTime.of(2026, 8, 20, 10, 30, 5, 70_000_000);
        assertEquals("20-AUG-2026 10:30:05.07", codec.toVmsAscii(dt));
    }

    @Test
    public void toVmsAscii_paddsDayWithZero() {
        LocalDateTime dt = LocalDateTime.of(2026, 1, 5, 0, 0, 0, 0);
        String result = codec.toVmsAscii(dt);
        assertTrue("Doit commencer par '05-JAN'", result.startsWith("05-JAN"));
    }

    @Test
    public void toVmsAscii_allMonths() {
        String[] months = {"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
        for (int m = 1; m <= 12; m++) {
            LocalDateTime dt = LocalDateTime.of(2026, m, 1, 0, 0, 0, 0);
            String result = codec.toVmsAscii(dt);
            assertEquals("Mois " + m, months[m - 1], result.substring(3, 6));
        }
    }

    @Test
    public void toVmsAscii_length_is23() {
        LocalDateTime dt = LocalDateTime.of(2026, 8, 20, 10, 30, 0, 0);
        assertEquals(23, codec.toVmsAscii(dt).length());
    }

    // ── fromVmsAscii ──────────────────────────────────────────────────────────

    @Test
    public void fromVmsAscii_parsesCorrectly() {
        LocalDateTime result = codec.fromVmsAscii("20-AUG-2026 10:30:05.07");
        assertEquals(2026, result.getYear());
        assertEquals(8,    result.getMonthValue());
        assertEquals(20,   result.getDayOfMonth());
        assertEquals(10,   result.getHour());
        assertEquals(30,   result.getMinute());
        assertEquals(5,    result.getSecond());
        assertEquals(70_000_000, result.getNano());
    }

    @Test
    public void fromVmsAscii_roundTrip() {
        LocalDateTime original = LocalDateTime.of(2025, 12, 31, 23, 59, 59, 990_000_000);
        String ascii = codec.toVmsAscii(original);
        LocalDateTime parsed = codec.fromVmsAscii(ascii);
        assertEquals(original.getYear(),       parsed.getYear());
        assertEquals(original.getMonthValue(), parsed.getMonthValue());
        assertEquals(original.getDayOfMonth(), parsed.getDayOfMonth());
        assertEquals(original.getHour(),       parsed.getHour());
        assertEquals(original.getMinute(),     parsed.getMinute());
        assertEquals(original.getSecond(),     parsed.getSecond());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromVmsAscii_invalidMonth_throwsException() {
        codec.fromVmsAscii("20-XXX-2026 10:30:00.00");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromVmsAscii_null_throwsException() {
        codec.fromVmsAscii(null);
    }

    // ── toYyyyMmDd ───────────────────────────────────────────────────────────

    @Test
    public void toYyyyMmDd_formatsCorrectly() {
        LocalDateTime dt = LocalDateTime.of(2026, 8, 20, 10, 30, 0);
        assertEquals("20260820", codec.toYyyyMmDd(dt));
    }

    @Test
    public void toYyyyMmDd_paddsDayAndMonth() {
        LocalDateTime dt = LocalDateTime.of(2026, 1, 5, 0, 0, 0);
        assertEquals("20260105", codec.toYyyyMmDd(dt));
    }

    @Test
    public void toYyyyMmDd_length_is8() {
        assertEquals(8, codec.toYyyyMmDd(LocalDateTime.of(2026, 8, 20, 0, 0, 0)).length());
    }

    // ── toYyMmDd ─────────────────────────────────────────────────────────────

    @Test
    public void toYyMmDd_dropsCentury_B10() {
        // Comportement B10 : "20260820" → "260820" (substring(2,8))
        LocalDateTime dt = LocalDateTime.of(2026, 8, 20, 10, 30, 0);
        assertEquals("260820", codec.toYyMmDd(dt));
    }

    @Test
    public void toYyMmDd_length_is6() {
        assertEquals(6, codec.toYyMmDd(LocalDateTime.of(2026, 8, 20, 0, 0, 0)).length());
    }

    @Test
    public void toYyMmDd_year2000_gives00() {
        LocalDateTime dt = LocalDateTime.of(2000, 3, 15, 0, 0, 0);
        assertEquals("000315", codec.toYyMmDd(dt));
    }

    @Test
    public void toYyMmDd_year1999_gives99() {
        LocalDateTime dt = LocalDateTime.of(1999, 6, 1, 0, 0, 0);
        assertEquals("990601", codec.toYyMmDd(dt));
    }
}
