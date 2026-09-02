package com.example.crm.d02.format;

import org.junit.Test;
import static org.junit.Assert.*;

/** Tests T07, T08, T09 — format du LINTXT. */
public class LintxtFormatterTest {

    @Test
    public void recordIs197Characters() {
        assertEquals(197, new LintxtFormatter("message").format().length());
    }

    @Test
    public void typmes_isLintxt() {
        assertEquals("LINTXT", new LintxtFormatter("").format().substring(0, 6));
    }

    // T09 — BL et RT absents → 114 espaces
    @Test
    public void emptyMessage_pos8To121_areSpaces() {
        String r = new LintxtFormatter(null).format();
        assertEquals("                                                                                                                  ",
            r.substring(7, 121)); // 114 espaces
        assertEquals(114, r.substring(7, 121).length());
    }

    // T07/T08 — message présent
    @Test
    public void message_pos8To121_leftPaddedTo114() {
        String msg = "Bon de livraison 12345";
        String r = new LintxtFormatter(msg).format();
        String field = r.substring(7, 121);
        assertEquals(114, field.length());
        assertTrue(field.startsWith(msg));
        // reste des espaces
        for (int i = msg.length(); i < 114; i++) {
            assertEquals(' ', field.charAt(i));
        }
    }

    @Test
    public void pos122To197_areSpaces() {
        String r = new LintxtFormatter("texte").format();
        for (int i = 121; i < 197; i++) {
            assertEquals("Pos " + (i + 1), ' ', r.charAt(i));
        }
    }
}
