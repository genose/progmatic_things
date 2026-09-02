package com.example.crm.d02.format;

import java.util.Arrays;

/**
 * Enregistrement DEBCDE — tête de fichier, écrit une seule fois.
 *
 * Layout (positions absolues 1-197) :
 *   1-  6 : "DEBCDE"
 *      7   : espace (ENR-FILLER)
 *   8- 42  : ENR-DEBCDE-EMETEUR  PIC X(35)  "183 CSP" + 28 espaces
 *  43- 77  : ENR-DEBCDE-RECEPTE  PIC X(35)  "183 CSP" + 28 espaces
 *     78   : ENR-DEBCDE-TEST     PIC X      "P"
 *  79-178  : ENR-DEBCDE-FILLER   PIC X(100) espaces
 * 179-197  : FILLER              PIC X(19)  espaces
 */
public final class DebcdeFormatter implements TransmissionRecord {

    private static final String TYPMES  = "DEBCDE";
    private static final String EMETEUR = "183 CSP";
    private static final String RECEPTE = "183 CSP";
    private static final String TEST    = "P";

    @Override
    public String format() {
        char[] rec = new char[197];
        Arrays.fill(rec, ' ');

        // ENR-TYPMES pos 1-6 (index 0-5)
        FixedField.writeAlpha(rec, 0, TYPMES, 6);
        // ENR-FILLER pos 7 (index 6) : espace — déjà fait par Arrays.fill

        // Corps — positions absolues 8-197 (index 7-196)
        FixedField.writeAlpha(rec,  7, EMETEUR, 35); // pos  8-42
        FixedField.writeAlpha(rec, 42, RECEPTE, 35); // pos 43-77
        rec[77] = TEST.charAt(0);                     // pos 78

        // FILLER pos 79-178 et 179-197 : déjà espaces

        assert new String(rec).length() == 197;
        return new String(rec);
    }
}
