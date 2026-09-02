package com.example.crm.d02.main;

/**
 * Point d'entree batch D02_EXTCDE_CRMCSP1.
 *
 * Usage :
 *   java -cp target/crm-d02-extcde-crmcsp1.jar:lib/rdb-jdbc.jar \
 *        com.example.crm.d02.main.D02Main \
 *        jdbc:rdb://host/BD_CRM  jdbc:rdb://host/BD_DEPOT \
 *        user pass  /data/sortie  CO 20241108
 *
 * Arguments :
 *   [0] urlCrm       — URL JDBC BD_CRM
 *   [1] urlDepot     — URL JDBC BD_DEPOT
 *   [2] user
 *   [3] pass
 *   [4] dirSortie    — repertoire de sortie des fichiers TRANSMIT
 *   [5] coddep       — code depot (CO ou MO)
 *   [6] datebl       — date bon de livraison YYYYMMDD
 */
public final class D02Main {

    public static void main(String[] args) {
        if (args.length < 7) {
            System.err.println(
                "Usage: D02Main <urlCrm> <urlDepot> <user> <pass> <dirSortie> <coddep> <datebl>");
            System.exit(1);
        }
        // TODO : instancier adapters JDBC + writer fichier 197 chars, injecter dans service D02
        System.out.println("D02_EXTCDE_CRMCSP1 — implementation en cours");
        System.exit(0);
    }
}
