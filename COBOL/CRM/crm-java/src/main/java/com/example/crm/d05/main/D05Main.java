package com.example.crm.d05.main;

import com.example.crm.common.DriverManagerDataSource;
import com.example.crm.d05.adapter.jdbc.RdbDepotCdeRepository;
import com.example.crm.d05.application.VerifCrmService;
import com.example.crm.d05.domain.CommandeDiscordante;
import com.example.crm.d05.domain.VerifCrmResult;

import javax.sql.DataSource;

/**
 * Point d'entrée du batch D05_VERIF_CRM — migration Java 8.
 *
 * Équivalent de la procédure DCL OpenVMS qui invoque D05_VERIF_CRM.EXE.
 *
 * ── Paramètres (arguments positionnels) ──────────────────────────────────────
 *   args[0]  DEPOT_URL   URL JDBC BD_DEPOT   ex: jdbc:rdb://host/BD_DEPOT
 *   args[1]  CRM_URL     URL JDBC BD_CRM     ex: jdbc:rdb://host/BD_CRM
 *   args[2]  DB_USER     utilisateur Oracle Rdb
 *   args[3]  DB_PASS     mot de passe
 *   args[4]  P-CODDEP    code dépôt           ex: CO
 *   args[5]  P-DATEBL    date YYYYMMDD         ex: 20241108
 *   args[6]  P-MAJ       'O' = effectuer les mises à jour, autre = lecture seule
 *
 * ── Codes de sortie ──────────────────────────────────────────────────────────
 *   0  Terminaison normale
 *   1  Erreur de paramètres
 *   2  Erreur base de données (exception propagée)
 *
 * ── Flux d'exécution ─────────────────────────────────────────────────────────
 *   1. Connexion BD_DEPOT (DataSource) + BD_CRM (DataSource)
 *   2. VerifCrmService.run(datebl, maj)
 *      a. Phase scan READ ONLY : CURCDE → compare STATUT vs STATENCOURS
 *      b. Phase MAJ READ WRITE (si P-MAJ='O') : UPDATE + COMMIT unique
 *   3. Affichage résultats sur stdout
 */
public class D05Main {

    private static final String DRIVER = "oracle.rdb.jdbc.rdbThin.Driver";

    public static void main(String[] args) {
        if (args.length < 7) {
            System.err.println("Usage: D05Main <depot_url> <crm_url> <user> <pass>"
                    + " <coddep> <datebl_YYYYMMDD> <maj_O_ou_N>");
            System.err.println("  ex : D05Main jdbc:rdb://srv/BD_DEPOT"
                    + " jdbc:rdb://srv/BD_CRM user pass CO 20241108 O");
            System.exit(1);
        }

        String depotUrl = args[0];
        String crmUrl   = args[1];
        String user     = args[2];
        String pass     = args[3];
        String coddep   = args[4];
        String datebl   = args[5];
        boolean maj     = "O".equalsIgnoreCase(args[6]);

        System.out.println("D05_VERIF_CRM — démarrage");
        System.out.println("  CODDEP  : " + coddep);
        System.out.println("  DATEBL  : " + datebl);
        System.out.println("  MAJ     : " + (maj ? "O (mise à jour)" : "N (lecture seule)"));

        try {
            DataSource depotDs = new DriverManagerDataSource(DRIVER, depotUrl, user, pass);
            DataSource crmDs   = new DriverManagerDataSource(DRIVER, crmUrl,   user, pass);

            RdbDepotCdeRepository repo = new RdbDepotCdeRepository(depotDs, crmDs);
            VerifCrmService service    = new VerifCrmService(coddep, repo);

            VerifCrmResult result = service.run(datebl, maj);

            // ── Restitution (équivalent DISPLAY COBOL) ──────────────────────
            System.out.println("Commandes discordantes : " + result.getDiscordantes().size());

            if (result.isTableOverflow()) {
                System.out.println("WARN: tableau interne overflow — seules les 9000"
                        + " premières discordantes ont été traitées (B-D05-09)");
            }

            for (CommandeDiscordante d : result.getDiscordantes()) {
                System.out.printf("  CODLAB=%-4s NUMCDE=%07d NUMRAL=%d STATUT=%s%n",
                        d.getCodlab(), d.getNumcde(), d.getNumral(), d.getStatut());
            }

            System.out.println("Mises à jour effectuées : " + result.getNbMaj());
            System.out.println("D05_VERIF_CRM — fin normale");
            System.exit(0);

        } catch (Exception e) {
            System.err.println("D05_VERIF_CRM — ERREUR : " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(2);
        }
    }
}
