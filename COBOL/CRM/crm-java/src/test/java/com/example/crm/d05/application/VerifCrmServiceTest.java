package com.example.crm.d05.application;

import com.example.crm.d05.domain.CommandeDepot;
import com.example.crm.d05.domain.CommandeDiscordante;
import com.example.crm.d05.domain.VerifCrmResult;
import com.example.crm.d05.port.DepotCdeRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests de VerifCrmService.
 *
 * Stub manuel de DepotCdeRepository pour éviter tout accès base.
 * Chaque test construit les données via le stub directement.
 */
public class VerifCrmServiceTest {

    // ---- Stub du repository ----

    private static class StubRepo implements DepotCdeRepository {
        final List<CommandeDepot> commandes = new ArrayList<>();
        // clé = "coddep|codlab|numcde|numral"
        final Map<String, String> statencours = new HashMap<>();
        final List<String> updatesEffectues = new ArrayList<>();

        @Override
        public List<CommandeDepot> findByDatebl(String coddep, LocalDate datebl) {
            return commandes;
        }

        @Override
        public Optional<String> findStatencours(String coddep, String codlab,
                                                int numcde, int numral) {
            String key = coddep + "|" + codlab + "|" + numcde + "|" + numral;
            return Optional.ofNullable(statencours.get(key));
        }

        @Override
        public void updateStatcrm(String codlab, int numcde, int numral) {
            updatesEffectues.add(codlab + "|" + numcde + "|" + numral);
        }

        void ajouterCommande(int numcde, int numral, String statut, String codlab) {
            commandes.add(new CommandeDepot(numcde, numral, statut, codlab));
        }

        void ajouterStatencours(String coddep, String codlab, int numcde, int numral, String statut) {
            statencours.put(coddep + "|" + codlab + "|" + numcde + "|" + numral, statut);
        }
    }

    private StubRepo repo;
    private VerifCrmService service;

    @Before
    public void setUp() {
        repo    = new StubRepo();
        service = new VerifCrmService("CO", repo);
    }

    // ---- Tests de la phase de scan ----

    @Test
    public void aucuneCommande_retourneListeVide() {
        VerifCrmResult res = service.run("20241108", false);
        assertTrue(res.getDiscordantes().isEmpty());
        assertFalse(res.isTableOverflow());
        assertEquals(0, res.getNbMaj());
    }

    @Test
    public void statutsIdentiques_commandeEstOk_pasDeDiscordante() {
        repo.ajouterCommande(1000001, 0, "FAX", "0001");
        repo.ajouterStatencours("CO", "0001", 1000001, 0, "FAX");

        VerifCrmResult res = service.run("20241108", false);
        assertTrue(res.getDiscordantes().isEmpty());
    }

    @Test
    public void fat_fap_commandeEstOk_pasDeDiscordante() {
        repo.ajouterCommande(1000002, 0, "FAT", "0001");
        repo.ajouterStatencours("CO", "0001", 1000002, 0, "FAP");

        VerifCrmResult res = service.run("20241108", false);
        assertTrue(res.getDiscordantes().isEmpty());
    }

    @Test
    public void glt_glp_commandeEstOk_pasDeDiscordante() {
        repo.ajouterCommande(1000003, 0, "GLT", "0001");
        repo.ajouterStatencours("CO", "0001", 1000003, 0, "GLP");

        VerifCrmResult res = service.run("20241108", false);
        assertTrue(res.getDiscordantes().isEmpty());
    }

    @Test
    public void statutsDifferents_commandeEstDiscordante() {
        repo.ajouterCommande(1000004, 0, "FAT", "0001");
        repo.ajouterStatencours("CO", "0001", 1000004, 0, "GLT");

        VerifCrmResult res = service.run("20241108", false);
        assertEquals(1, res.getDiscordantes().size());
        CommandeDiscordante d = res.getDiscordantes().get(0);
        assertEquals(1000004, d.getNumcde());
        assertEquals(0, d.getNumral());
        assertEquals("FAT", d.getStatut());
        assertEquals("0001", d.getCodlab());
    }

    @Test
    public void commandeAbsenteDeCrm_estDiscordante() {
        // Pas d'entrée dans statencours → findStatencours retourne Empty
        repo.ajouterCommande(1000005, 0, "FAX", "0001");

        VerifCrmResult res = service.run("20241108", false);
        assertEquals(1, res.getDiscordantes().size());
    }

    @Test
    public void mixteOkEtNok_seulsNokSontAccumules() {
        repo.ajouterCommande(1000010, 0, "FAX", "0001"); // OK
        repo.ajouterStatencours("CO", "0001", 1000010, 0, "FAX");
        repo.ajouterCommande(1000011, 0, "FAT", "0001"); // NOK (absente)
        repo.ajouterCommande(1000012, 0, "GLT", "0001"); // OK
        repo.ajouterStatencours("CO", "0001", 1000012, 0, "GLP");

        VerifCrmResult res = service.run("20241108", false);
        assertEquals(1, res.getDiscordantes().size());
        assertEquals(1000011, res.getDiscordantes().get(0).getNumcde());
    }

    // ---- Tests de la phase de MAJ ----

    @Test
    public void majFalse_aucunUpdateEffectue_memeAvecDiscordantes() {
        repo.ajouterCommande(1000020, 0, "FAT", "0001");
        // absente → discordante

        VerifCrmResult res = service.run("20241108", false);
        assertEquals(1, res.getDiscordantes().size());
        assertTrue(repo.updatesEffectues.isEmpty());
        assertEquals(0, res.getNbMaj());
    }

    @Test
    public void majTrue_aucuneDiscordante_aucunUpdate() {
        repo.ajouterCommande(1000030, 0, "FAX", "0001");
        repo.ajouterStatencours("CO", "0001", 1000030, 0, "FAX"); // OK

        VerifCrmResult res = service.run("20241108", true);
        assertTrue(repo.updatesEffectues.isEmpty());
        assertEquals(0, res.getNbMaj());
    }

    @Test
    public void majTrue_avecDiscordantes_updateParDiscordante() {
        repo.ajouterCommande(1000040, 0, "FAT", "0001"); // NOK
        repo.ajouterCommande(1000041, 1, "GLT", "0002"); // NOK

        VerifCrmResult res = service.run("20241108", true);
        assertEquals(2, res.getNbMaj());
        assertTrue(repo.updatesEffectues.contains("0001|1000040|0"));
        assertTrue(repo.updatesEffectues.contains("0002|1000041|1"));
    }

    @Test
    public void updateN_utilise_codlab_numcde_numral_sansCODDEP() {
        // B-D05-04 : la clé UPDATE = (codlab, numcde, numral) sans coddep
        repo.ajouterCommande(1000050, 0, "FAT", "LABO"); // NOK

        service.run("20241108", true);

        // L'update ne doit pas contenir le coddep "CO"
        assertEquals(1, repo.updatesEffectues.size());
        assertEquals("LABO|1000050|0", repo.updatesEffectues.get(0));
    }

    // ---- Test overflow ----

    @Test
    public void overflow_9000_commandesNok_flagTableOverflow() {
        for (int i = 0; i < 9001; i++) {
            repo.ajouterCommande(i + 1, 0, "FAT", "0001");
            // pas de statencours → toutes discordantes
        }

        VerifCrmResult res = service.run("20241108", false);
        assertEquals(9000, res.getDiscordantes().size());
        assertTrue(res.isTableOverflow());
    }

    @Test
    public void exactement_9000_commandesNok_pasDeOverflow() {
        for (int i = 0; i < 9000; i++) {
            repo.ajouterCommande(i + 1, 0, "FAT", "0001");
        }

        VerifCrmResult res = service.run("20241108", false);
        assertEquals(9000, res.getDiscordantes().size());
        assertFalse(res.isTableOverflow());
    }

    // ---- Test conversion de date ----

    @Test
    public void parseDateBl_yyyymmdd_transmisAuRepo() {
        final List<LocalDate> datesRecues = new ArrayList<>();
        DepotCdeRepository repoCapture = new DepotCdeRepository() {
            @Override
            public List<CommandeDepot> findByDatebl(String coddep, LocalDate datebl) {
                datesRecues.add(datebl);
                return Collections.emptyList();
            }
            @Override public Optional<String> findStatencours(String c, String l, int n, int r) { return Optional.empty(); }
            @Override public void updateStatcrm(String l, int n, int r) {}
        };

        new VerifCrmService("CO", repoCapture).run("20241108", false);

        assertEquals(1, datesRecues.size());
        assertEquals(LocalDate.of(2024, 11, 8), datesRecues.get(0));
    }

    @Test
    public void coddepEstTransmisAuRepoPourfindStatencours() {
        final List<String> coddesParcours = new ArrayList<>();
        repo.ajouterCommande(1, 0, "FAT", "L001");

        DepotCdeRepository repoCapture = new DepotCdeRepository() {
            @Override public List<CommandeDepot> findByDatebl(String coddep, LocalDate d) { return repo.commandes; }
            @Override
            public Optional<String> findStatencours(String coddep, String codlab, int numcde, int numral) {
                coddesParcours.add(coddep);
                return Optional.empty();
            }
            @Override public void updateStatcrm(String l, int n, int r) {}
        };

        new VerifCrmService("MO", repoCapture).run("20241108", false);

        // coddep "MO" doit être passé au findStatencours (B-D05-02)
        assertEquals(1, coddesParcours.size());
        assertEquals("MO", coddesParcours.get(0));
    }
}
