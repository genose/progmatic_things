package com.example.crm.d02.format;

import java.util.Arrays;

/**
 * Enregistrement FINCDE — pied de groupe laboratoire.
 * Routé vers CO ou MO selon PREV-CODDEP (le dépôt du dernier enregistrement
 * traité dans le groupe, pas le dépôt courant — comportement B03).
 *
 * Layout (positions absolues 1-197) :
 *   1-  6 : "FINCDE"
 *      7   : espace
 *   8- 15  : SUMQTE  9(8)   somme quantités livrées pour ce dépôt (W-SUMQTE-CO/MO)
 *     16   : espace
 *  17- 24  : NBLIG   9(8)   nb de lignes LINCDE écrites pour ce dépôt
 *  25-121  : FILLER  X(97)  espaces
 * 122-197  : non adressé — espaces (INITIALIZE)
 */
public final class FincdeFormatter implements TransmissionRecord {

    /** Somme des quantités livrées pour ce groupe (W-SUMQTE-CO ou W-SUMQTE-MO). */
    private final long sumqte;
    /** Nombre de lignes LINCDE écrites pour ce groupe (W-NB-CDL-CO ou W-NB-CDL-MO). */
    private final long nblig;

    public FincdeFormatter(long sumqte, long nblig) {
        this.sumqte = sumqte;
        this.nblig  = nblig;
    }

    @Override
    public String format() {
        char[] rec = new char[197];
        Arrays.fill(rec, ' ');

        FixedField.writeAlpha  (rec,  0, "FINCDE", 6); // pos  1- 6
        // index 6 : espace

        FixedField.writeNumeric(rec,  7, sumqte,   8); // pos  8-15
        // index 15 : espace
        FixedField.writeNumeric(rec, 16, nblig,    8); // pos 17-24
        // FILLER pos 25-121 (index 24-120) : 97 espaces
        // non adressé pos 122-197 : espaces

        assert new String(rec).length() == 197;
        return new String(rec);
    }
}
