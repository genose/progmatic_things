package com.example.crm.d05intcde.domain;

/** Cle primaire de S.CDE_FAC et D.CDE : (codlab, coddep, numcde, numral). */
public final class CdeFacKey {
    public final String codlab;
    public final String coddep;
    public final long   numcde;
    public final int    numral;

    public CdeFacKey(String codlab, String coddep, long numcde, int numral) {
        this.codlab = codlab;
        this.coddep = coddep;
        this.numcde = numcde;
        this.numral = numral;
    }
}
