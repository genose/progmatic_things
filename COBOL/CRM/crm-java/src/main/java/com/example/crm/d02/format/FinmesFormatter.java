package com.example.crm.d02.format;

import java.util.Arrays;

/**
 * Enregistrement FINMES — pied de fichier, écrit une seule fois par fichier (CO et MO).
 *
 * Layout (positions absolues 1-197) :
 *   1-  6 : "FINMES"
 *      7   : espace
 *   8- 15  : NBCDE  9(8)   nombre total de commandes CO ou MO (NB-CDE-CO / NB-CDE-MO)
 *  16-112  : FILLER X(97)  espaces
 * 113-197  : non adressé — espaces (INITIALIZE)
 */
public final class FinmesFormatter implements TransmissionRecord {

    /** Nombre total de commandes traitées (NB-CDE-CO ou NB-CDE-MO). */
    private final long nbcde;

    public FinmesFormatter(long nbcde) {
        this.nbcde = nbcde;
    }

    @Override
    public String format() {
        char[] rec = new char[197];
        Arrays.fill(rec, ' ');

        FixedField.writeAlpha  (rec, 0, "FINMES", 6); // pos  1- 6
        // index 6 : espace

        FixedField.writeNumeric(rec, 7, nbcde,    8); // pos  8-15
        // FILLER pos 16-112 (index 15-111) : 97 espaces
        // non adressé pos 113-197 : espaces

        assert new String(rec).length() == 197;
        return new String(rec);
    }
}
