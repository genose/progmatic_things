package com.example.crm.t10.domain;

/**
 * Résultat de la détection d'une commande miroir labo 9994 (TST-3628-9994).
 *
 * numcde9994 = W-CDELAB(1:7) converti en entier  (B-T10-06)
 * numral9994 = W-CDELAB(8:1) converti en entier
 * La mise à jour 9994 utilise toujours CODDEP='CO' (hardcodé — B-T10-05).
 */
public final class Livraison9994 {

    private final int numcde9994;
    private final int numral9994;

    public Livraison9994(int numcde9994, int numral9994) {
        this.numcde9994 = numcde9994;
        this.numral9994 = numral9994;
    }

    public int getNumcde9994() { return numcde9994; }
    public int getNumral9994() { return numral9994; }
}
