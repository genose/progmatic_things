package com.example.crm.t10.main;

import com.example.crm.common.DriverManagerDataSource;
import com.example.crm.t10.adapter.file.MajbdstatFileReader;
import com.example.crm.t10.adapter.jdbc.RdbCdeFacRepository;
import com.example.crm.t10.application.CascadeLabo3628;
import com.example.crm.t10.application.MajDtlivrService;
import com.example.crm.t10.domain.LivraisonRecord;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Point d'entrée du batch T10_MAJ_DTLIVR_BDCRM — migration Java 8.
 *
 * Équivalent de la procédure DCL OpenVMS qui invoque T10_MAJ_DTLIVR_BDCRM.EXE.
 *
 * En COBOL/VMS, les fichiers sont découverts via LIB$FIND_FILE sur le
 * pattern "DIRDAT:MAJBDSTAT*.DAT;*". En Java, on parcourt un répertoire
 * passé en argument et on filtre les fichiers correspondants.
 *
 * ── Paramètres (arguments positionnels) ──────────────────────────────────────
 *   args[0]  CRM_URL     URL JDBC BD_CRM     ex: jdbc:rdb://host/BD_CRM
 *   args[1]  DB_USER     utilisateur Oracle Rdb
 *   args[2]  DB_PASS     mot de passe
 *   args[3]  DIRDAT      répertoire contenant les fichiers MAJBDSTAT*.DAT
 *                        (équivalent du logique VMS DIRDAT:)
 *
 * ── Codes de sortie ──────────────────────────────────────────────────────────
 *   0  Terminaison normale
 *   1  Erreur de paramètres
 *   2  Erreur base de données ou fichier (FIN-ANORMALE, B-T10-11)
 *
 * ── Flux d'exécution ─────────────────────────────────────────────────────────
 *   Pour chaque fichier MAJBDSTAT*.DAT dans DIRDAT :
 *     Pour chaque enregistrement :
 *       1. FO→MO normalisation (B-T10-03)
 *       2. TST-3628-9994 : détection commande miroir si CODLAB='3628'
 *       3. UPDATE E.CDE_FAC SET DTLIVR=?, FLAGLIV='O'
 *       4. Si cascade : UPDATE 9994 avec même DTLIVR
 *       5. COMMIT (B-T10-07)
 *   DEADLOCK → RuntimeException → exit(2) = STOP RUN (B-T10-10)
 */
public class T10Main {

    private static final String DRIVER        = "oracle.rdb.jdbc.rdbThin.Driver";
    private static final String FILE_PREFIX   = "MAJBDSTAT";
    private static final String FILE_SUFFIX   = ".DAT";
    private static final Charset FILE_CHARSET = Charset.forName("ISO-8859-1");

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Usage: T10Main <crm_url> <user> <pass> <dirdat>");
            System.err.println("  ex : T10Main jdbc:rdb://srv/BD_CRM user pass /data/majbdstat");
            System.exit(1);
        }

        String crmUrl  = args[0];
        String user    = args[1];
        String pass    = args[2];
        File   dirdat  = new File(args[3]);

        System.out.println("T10_MAJ_DTLIVR_BDCRM — démarrage");
        System.out.println("  DIRDAT : " + dirdat.getAbsolutePath());

        if (!dirdat.isDirectory()) {
            System.err.println("ERREUR : répertoire inexistant : " + dirdat.getAbsolutePath());
            System.exit(1);
        }

        try {
            DataSource crmDs = new DriverManagerDataSource(DRIVER, crmUrl, user, pass);

            RdbCdeFacRepository repo    = new RdbCdeFacRepository(crmDs);
            CascadeLabo3628     cascade = new CascadeLabo3628(repo);
            MajDtlivrService    service = new MajDtlivrService(repo, cascade);

            List<File> fichiers = trouverFichiers(dirdat);
            if (fichiers.isEmpty()) {
                System.out.println("Aucun fichier " + FILE_PREFIX + "*" + FILE_SUFFIX
                        + " trouvé dans " + dirdat);
                System.exit(0);
            }

            int totalRecords = 0;
            int totalFichiers = 0;

            for (File fichier : fichiers) {
                System.out.println("Traitement : " + fichier.getName());
                List<LivraisonRecord> records = lireFichier(fichier);

                for (LivraisonRecord rec : records) {
                    service.traiterRecord(rec);
                    totalRecords++;
                }
                totalFichiers++;
            }

            System.out.println("T10_MAJ_DTLIVR_BDCRM — fin normale");
            System.out.println("  Fichiers traités    : " + totalFichiers);
            System.out.println("  Enregistrements MAJ : " + totalRecords);
            System.exit(0);

        } catch (Exception e) {
            // FIN-ANORMALE (B-T10-10) : STOP RUN
            System.err.println("T10_MAJ_DTLIVR_BDCRM — FIN ANORMALE : " + e.getMessage());
            e.printStackTrace(System.err);
            // Équivalent LIB$SET_LOGICAL ARRPRG=O (B-T10-11)
            System.err.println("ARRPRG=O");
            System.exit(2);
        }
    }

    /**
     * Découverte des fichiers MAJBDSTAT*.DAT dans le répertoire.
     * Équivalent de LIB$FIND_FILE("DIRDAT:MAJBDSTAT*.DAT;*").
     * Tri alphabétique pour un ordre déterministe.
     */
    private static List<File> trouverFichiers(File dir) {
        File[] tous = dir.listFiles((d, name) -> {
            String upper = name.toUpperCase();
            return upper.startsWith(FILE_PREFIX) && upper.endsWith(FILE_SUFFIX);
        });
        if (tous == null || tous.length == 0) return Collections.emptyList();
        List<File> liste = new ArrayList<File>();
        for (File f : tous) liste.add(f);
        Collections.sort(liste);
        return liste;
    }

    private static List<LivraisonRecord> lireFichier(File f) throws IOException {
        try (MajbdstatFileReader reader = new MajbdstatFileReader(f, FILE_CHARSET)) {
            return reader.readAll();
        }
    }
}
