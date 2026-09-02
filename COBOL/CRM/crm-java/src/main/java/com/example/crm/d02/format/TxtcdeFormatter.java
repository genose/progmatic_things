package com.example.crm.d02.format;

import java.util.Arrays;

/**
 * Enregistrement TXTCDE — texte horaire (RT) ou texte contact (BL).
 * Écrit uniquement si l'article QU00013 (RT) ou QU000132 (BL) est présent.
 *
 * Layout (positions absolues 1-197) :
 *   1-  6 : "TXTCDE"
 *      7   : espace
 *   8- 87  : MESSAGE  X(80)  W-LIB-HORAIRE ou W-LIB-CONTACT (D.CDL.LIBELL)
 *     88   : espace
 *  89- 90  : TYPDOC   X(2)   "RT" (horaire) ou "BL" (contact)
 *  91-197  : non adressé — espaces (INITIALIZE)
 */
public final class TxtcdeFormatter implements TransmissionRecord {

    /** Libellé de l'article QU00013 ou QU000132 (80 chars max). */
    private final String message;
    /**
     * Type de document : "RT" pour l'article horaire QU00013,
     *                    "BL" pour l'article contact QU000132.
     */
    private final String typdoc;

    public TxtcdeFormatter(String message, String typdoc) {
        this.message = message;
        this.typdoc  = typdoc;
    }

    @Override
    public String format() {
        char[] rec = new char[197];
        Arrays.fill(rec, ' ');

        FixedField.writeAlpha(rec, 0, "TXTCDE", 6); // pos 1-6
        // index 6 : espace

        FixedField.writeAlpha(rec,  7, message, 80); // pos  8-87
        // index 87 : espace
        FixedField.writeAlpha(rec, 88, typdoc,   2); // pos 89-90
        // pos 91-197 : espaces

        assert new String(rec).length() == 197;
        return new String(rec);
    }
}
