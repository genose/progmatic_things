package com.example.crm.d02.format;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests T26, T30 — format du LINCDE.
 * Points critiques :
 *   T26 — QTCCDE (pos 19-25) reçoit QTELIV (quantité livrée), pas la quantité commandée.
 *   T30 — LIBELL (pos 35-69) est TOUJOURS 35 espaces.
 */
public class LincdeFormatterTest {

    private LincdeFormatter build(String codart, long qteliv, long qtcgrt, String codlablab) {
        return new LincdeFormatter(codart, qteliv, qtcgrt, codlablab);
    }

    @Test
    public void recordIs197Characters() {
        assertEquals(197, build("ART0000001", 5L, 2L, "1234").format().length());
    }

    @Test
    public void typmes_isLincde() {
        assertEquals("LINCDE", build("ART0000001", 5L, 2L, "1234").format().substring(0, 6));
    }

    @Test
    public void codart_pos8To17() {
        // pos 8-17 (idx 7-16) — 10 chars
        String r = build("ABCDEFGHIJ", 1L, 0L, "LAB1").format();
        assertEquals("ABCDEFGHIJ", r.substring(7, 17));
    }

    @Test
    public void codart_shorterThan10_isPaddedWithSpaces() {
        String r = build("ART01", 1L, 0L, "LAB1").format();
        assertEquals("ART01     ", r.substring(7, 17));
    }

    // T26 — ENR-LINCDE-QTCCDE reçoit QTELIV, pas QTCCDE
    @Test
    public void qtccde_pos19To25_containsQteliv() {
        String r = build("ART0000001", 1234567L, 0L, "1234").format();
        // pos 19-25 (idx 18-24) — 7 chiffres
        assertEquals("1234567", r.substring(18, 25));
    }

    @Test
    public void qtcgrt_pos27To33() {
        String r = build("ART0000001", 0L, 42L, "1234").format();
        // pos 27-33 (idx 26-32) — 7 chiffres
        assertEquals("0000042", r.substring(26, 33));
    }

    // T30 — LIBELL TOUJOURS 35 espaces
    @Test
    public void libell_pos35To69_isAlwaysSpaces() {
        String r = build("ART0000001", 5L, 1L, "1234").format();
        // pos 35-69 (idx 34-68) — 35 chars
        String libell = r.substring(34, 69);
        assertEquals(35, libell.length());
        assertEquals("                                   ", libell);
    }

    @Test
    public void codlablab_pos71To74() {
        String r = build("ART0000001", 5L, 1L, "ABCD").format();
        // pos 71-74 (idx 70-73) — 4 chars
        assertEquals("ABCD", r.substring(70, 74));
    }

    @Test
    public void filler_pos75To112_areSpaces() {
        String r = build("ART0000001", 5L, 1L, "ABCD").format();
        // pos 75-112 (idx 74-111) — 38 espaces
        for (int i = 74; i < 112; i++) {
            assertEquals("Pos " + (i + 1), ' ', r.charAt(i));
        }
    }

    @Test
    public void pos113To197_areSpaces() {
        String r = build("ART0000001", 5L, 1L, "ABCD").format();
        for (int i = 112; i < 197; i++) {
            assertEquals("Pos " + (i + 1), ' ', r.charAt(i));
        }
    }

    @Test
    public void filler_pos18_isSpace() {
        assertEquals(' ', build("ART01", 1L, 0L, "LAB1").format().charAt(17));
    }

    @Test
    public void filler_pos26_isSpace() {
        assertEquals(' ', build("ART01", 1L, 0L, "LAB1").format().charAt(25));
    }

    @Test
    public void filler_pos34_isSpace() {
        assertEquals(' ', build("ART01", 1L, 0L, "LAB1").format().charAt(33));
    }

    @Test
    public void filler_pos70_isSpace() {
        assertEquals(' ', build("ART01", 1L, 0L, "LAB1").format().charAt(69));
    }
}
