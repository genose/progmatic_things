package com.example.crm.d05.domain;

/**
 * Entrée dans tab-cde : commande dont le statut DEPOT ≠ statut CRM.
 * Clé UPDATE : (codlab, numcde, numral) — CODDEP absent (B-D05-04).
 */
public final class CommandeDiscordante {

    private final int numcde;
    private final int numral;
    private final String statut;
    private final String codlab;

    public CommandeDiscordante(int numcde, int numral, String statut, String codlab) {
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
