package com.example.crm.d05.domain;

/**
 * Représente une ligne retournée par CURCDE (BD_DEPOT.CDE).
 * Champs: numcde(9), numral(1), statut(3), codlab(4).
 */
public final class CommandeDepot {

    private final int numcde;
    private final int numral;
    private final String statut;
    private final String codlab;

    public CommandeDepot(int numcde, int numral, String statut, String codlab) {
        this.numcde = numcde;
        this.numral = numral;
        this.statut = statut;
        this.codlab = codlab;
    }

    public int getNumcde()    { return numcde; }
    public int getNumral()    { return numral; }
    public String getStatut() { return statut; }
    public String getCodlab() { return codlab; }
}
