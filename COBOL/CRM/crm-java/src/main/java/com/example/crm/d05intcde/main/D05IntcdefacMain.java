package com.example.crm.d05intcde.main;

/**
 * Point d'entree batch D05_INTCDEFAC_CRM_V2.
 *
 * Usage :
 *   java -cp target/d05-intcdefac.jar:lib/rdb-jdbc.jar \
 *        com.example.crm.d05intcde.main.D05IntcdefacMain \
 *        jdbc:rdb://host/BD_CRM  jdbc:rdb://host/BD_DEPOT \
 *        user pass  /data/RMS_CDEFAC  CO
 *
 * Arguments :
 *   [0] urlCrm     — URL JDBC BD_CRM
 *   [1] urlDepot   — URL JDBC BD_DEPOT
 *   [2] user
 *   [3] pass
 *   [4] fichierCdefac — chemin fichier RMS_CDEFAC
 *   [5] coddep     — parametre P-CODDEP
 */
public final class D05IntcdefacMain {

    public static void main(String[] args) {
        if (args.length < 6) {
            System.err.println("Usage: D05IntcdefacMain <urlCrm> <urlDepot> <user> <pass> <fichierCdefac> <coddep>");
            System.exit(1);
        }
        // TODO : instancier adapters JDBC + file reader, injecter dans IntCdeFacService
        System.out.println("D05_INTCDEFAC_CRM_V2 — implementation en cours");
        System.exit(0);
    }
}
