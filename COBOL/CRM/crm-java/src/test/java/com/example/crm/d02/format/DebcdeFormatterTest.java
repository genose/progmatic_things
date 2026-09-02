package com.example.crm.d02.format;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests T01 (partiel) — format du DEBCDE.
 * Vérifie les positions absolues 1-197 selon le layout du guide §4.
 */
public class DebcdeFormatterTest {

    private String record;

    @Before
    public void setUp() {
        record = new DebcdeFormatter().format();
    }

    @Test
    public void recordIs197Characters() {
        assertEquals(197, record.length());
    }

    @Test
    public void typmes_isDebcde() {
        // pos 1-6 (idx 0-5)
        assertEquals("DEBCDE", record.substring(0, 6));
    }

    @Test
    public void filler_pos7_isSpace() {
        // pos 7 (idx 6)
        assertEquals(' ', record.charAt(6));
    }

    @Test
    public void emeteur_isLeftPaddedTo35() {
        // pos 8-42 (idx 7-41)
        String emeteur = record.substring(7, 42);
        assertEquals(35, emeteur.length());
        assertTrue("Doit commencer par '183 CSP'", emeteur.startsWith("183 CSP"));
        // compléter avec des espaces
        assertEquals("183 CSP                            ", emeteur);
    }

    @Test
    public void recepte_isLeftPaddedTo35() {
        // pos 43-77 (idx 42-76)
        String recepte = record.substring(42, 77);
        assertEquals(35, recepte.length());
        assertEquals("183 CSP                            ", recepte);
    }

    @Test
    public void test_isP() {
        // pos 78 (idx 77)
        assertEquals('P', record.charAt(77));
    }

    @Test
    public void remainingBytes_areSpaces() {
        // pos 79-197 (idx 78-196)
        for (int i = 78; i < 197; i++) {
            assertEquals("Position " + (i + 1) + " doit être espace", ' ', record.charAt(i));
        }
    }
}
