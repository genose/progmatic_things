package com.example.crm.d05.application;

import com.example.crm.d05.domain.CommandeDepot;
import com.example.crm.d05.domain.CommandeDiscordante;
import com.example.crm.d05.domain.StatutEquivalence;
import com.example.crm.d05.domain.VerifCrmResult;
import com.example.crm.d05.port.DepotCdeRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Migration de TRAITEMENT-PRINCIPAL dans D05_VERIF_CRM.SCO.
 *
 * Phase 1 (READ ONLY) :
 *   - Parcourt toutes les commandes CURCDE pour la date demandée.
 *   - Compare chaque statut avec STATENCOURS dans S.CDE_FAC.
 *   - Accumule les discordantes dans tab-cde (max TABLE_MAX=9000).
 *
 * Phase 2 (READ WRITE, si maj=true et discordantes>0) :
 *   - UPDATE D.CDE SET STATCRM='', FLAG_CRM='O' pour chaque discordante.
 *   - Un seul COMMIT global implicite (géré par l'adapter, B-D05-06).
 *
 * La gestion transactionnelle (SET TRANSACTION READ ONLY / ROLLBACK /
 * SET TRANSACTION READ WRITE / COMMIT) est déléguée à l'adapter JDBC.
 */
public class VerifCrmService {

    private static final int TABLE_MAX = 9000;
    private static final DateTimeFormatter FMT_YYYYMMDD =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    private final String coddep;
    private final DepotCdeRepository repo;

    public VerifCrmService(String coddep, DepotCdeRepository repo) {
        this.coddep = coddep;
        this.repo   = repo;
    }

    /**
     * @param dateBl chaîne YYYYMMDD (= P-DATEBL dans COBOL)
     * @param maj    true = effectuer les UPDATEs (= P-MAJ='O')
     */
    public VerifCrmResult run(String dateBl, boolean maj) {
        LocalDate datebl = LocalDate.parse(dateBl, FMT_YYYYMMDD);

        // --- Phase 1 : scan READ ONLY ---
        List<CommandeDepot> commandes = repo.findByDatebl(coddep, datebl);

        List<CommandeDiscordante> discordantes = new ArrayList<>();
        boolean tableOverflow = false;

        for (CommandeDepot cmd : commandes) {
            Optional<String> statencours = repo.findStatencours(
                    coddep, cmd.getCodlab(), cmd.getNumcde(), cmd.getNumral());

            boolean ok = statencours.isPresent()
                    && StatutEquivalence.areEquivalent(cmd.getStatut(), statencours.get());

            if (!ok) {
                if (discordantes.size() < TABLE_MAX) {
                    discordantes.add(new CommandeDiscordante(
                            cmd.getNumcde(), cmd.getNumral(),
                            cmd.getStatut(), cmd.getCodlab()));
                } else {
                    tableOverflow = true;
                    // Comportement COBOL : display warning, traitement continue (B-D05-09)
                }
            }
        }

        // --- Phase 2 : mises à jour READ WRITE (si demandées) ---
        int nbMaj = 0;
        if (maj && !discordantes.isEmpty()) {
            for (CommandeDiscordante d : discordantes) {
                repo.updateStatcrm(d.getCodlab(), d.getNumcde(), d.getNumral());
                nbMaj++;
            }
            repo.commit(); // B-D05-06 : un seul COMMIT global après tous les UPDATEs
        }

        return new VerifCrmResult(discordantes, tableOverflow, nbMaj);
    }
}
