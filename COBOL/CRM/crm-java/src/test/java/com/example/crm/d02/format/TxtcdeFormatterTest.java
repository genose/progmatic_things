package com.example.crm.d02.format;

import org.junit.Test;
import static org.junit.Assert.*;

/** Tests T10, T11 — format du TXTCDE. */
public class TxtcdeFormatterTest {

    @Test
    public void recordIs197Characters() {
        assertEquals(197, new TxtcdeFormatter("msg", "RT").format().length());
    }

    @Test
    public void typmes_isTxtcde() {
        assertEquals("TXTCDE", new TxtcdeFormatter("msg", "RT").format().substring(0, 6));
    }

    // T10 — article horaire QU00013 → TYPDOC = "RT"
    @Test
    public void typdoc_RT_pos89To90() {
        String r = new TxtcdeFormatter("Horaire 08h-18h", "RT").format();
        assertEquals("RT", r.substring(88, 90));
    }

    // T10 — article contact QU000132 → TYPDOC = "BL"
    @Test
    public void typdoc_BL_pos89To90() {
        String r = new TxtcdeFormatter("Contact: M. Martin", "BL").format();
        assertEquals("BL", r.substring(88, 90));
    }

    @Test
    public void message_pos8To87_leftPaddedTo80() {
        String msg = "Livraison entre 8h et 18h du lundi au vendredi";
        String r = new TxtcdeFormatter(msg, "RT").format();
        String field = r.substring(7, 87);
        assertEquals(80, field.length());
        assertTrue(field.startsWith(msg));
    }

    @Test
    public void filler_pos88_isSpace() {
        assertEquals(' ', new TxtcdeFormatter("x", "BL").format().charAt(87));
    }

    @Test
    public void pos91To197_areSpaces() {
        String r = new TxtcdeFormatter("msg", "RT").format();
        for (int i = 90; i < 197; i++) {
            assertEquals("Pos " + (i + 1), ' ', r.charAt(i));
        }
    }
}
