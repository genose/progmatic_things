package com.example.crm.d02.format;

import java.util.Arrays;

/**
 * Enregistrement LINTXT — commentaire BL ou RT issu de D.MES.COMMENT.
 * Toujours écrit, même si le message est vide (114 espaces dans ce cas).
 *
 * Layout (positions absolues 1-197) :
 *   1-  6 : "LINTXT"
 *      7   : espace
 *   8-121  : MESSAGE  X(114)  D.MES.COMMENT  (via W-COMMENT)
 * 122-197  : non adressé — espaces (INITIALIZE)
 */
public final class LintxtFormatter implements TransmissionRecord {

    /** D.MES.COMMENT (114 chars max), ou null/vide si absent. */
    private final String message;

    public LintxtFormatter(String message) {
        this.message = message;
    }

    @Override
    public String format() {
        char[] rec = new char[197];
        Arrays.fill(rec, ' ');

        FixedField.writeAlpha(rec, 0, "LINTXT", 6); // pos 1-6
        // index 6 : espace

        FixedField.writeAlpha(rec, 7, message, 114); // pos 8-121
        // pos 122-197 (index 121-196) : espaces

        assert new String(rec).length() == 197;
        return new String(rec);
    }
}
