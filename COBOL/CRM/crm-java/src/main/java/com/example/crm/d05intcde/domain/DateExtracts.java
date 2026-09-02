package com.example.crm.d05intcde.domain;

/**
 * Annee (4 chars), mois (2 chars), trimestre (1 char '1'-'4')
 * extraits d'une date VMS ASCII "DD-MON-YYYY HH:MM:SS.CC".
 *
 * Regle B-D05I-07 : trimestre = 1 si mois < 04 ; 2 si < 07 ; 3 si < 10 ; 4 sinon.
 */
public final class DateExtracts {
    public final String annee;
    public final String mois;
    public final String trimestre;

    public DateExtracts(String annee, String mois, String trimestre) {
        this.annee     = annee;
        this.mois      = mois;
        this.trimestre = trimestre;
    }
}
