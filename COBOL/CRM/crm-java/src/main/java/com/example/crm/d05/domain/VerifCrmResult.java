package com.example.crm.d05.domain;

import java.util.Collections;
import java.util.List;

/**
 * Résultat de VerifCrmService.run().
 *
 * discordantes : commandes dont le statut DEPOT ≠ CRM (contenu de tab-cde).
 * tableOverflow : true si plus de 9000 discordantes détectées (B-D05-09).
 * nbMaj : nombre d'UPDATEs effectivement exécutés (0 si maj=false ou liste vide).
 */
public final class VerifCrmResult {

    private final List<CommandeDiscordante> discordantes;
    private final boolean tableOverflow;
    private final int nbMaj;

    public VerifCrmResult(List<CommandeDiscordante> discordantes,
                          boolean tableOverflow,
                          int nbMaj) {
        this.discordantes  = Collections.unmodifiableList(discordantes);
        this.tableOverflow = tableOverflow;
        this.nbMaj         = nbMaj;
    }

    public List<CommandeDiscordante> getDiscordantes() { return discordantes; }
    public boolean isTableOverflow()                   { return tableOverflow; }
    public int getNbMaj()                              { return nbMaj; }
}
