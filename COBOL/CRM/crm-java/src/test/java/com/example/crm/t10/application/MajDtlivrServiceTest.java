package com.example.crm.t10.application;

import com.example.crm.t10.domain.Livraison9994;
import com.example.crm.t10.domain.LivraisonRecord;
import com.example.crm.t10.port.CdeFacRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests de MajDtlivrService (TRAITEMENT-INFOS par enregistrement).
 *
 * Stratégie :
 *   - Stub de CdeFacRepository pour capturer les appels.
 *   - Stub de CascadeLabo3628 injecté pour contrôler la détection 9994.
 */
public class MajDtlivrServiceTest {

    // ---- Stubs ----

    private static class StubRepo implements CdeFacRepository {
        final List<String> updatesCalls    = new ArrayList<>();
        final List<String> updates9994     = new ArrayList<>();

        @Override public Optional<String> findCdelab(String c, String l, int n, int r) { return Optional.empty(); }
        @Override public boolean existsCdeFac9994(int n, int r) { return false; }

        @Override
        public void updateDtlivr(String coddep, String codlab, int numcde, int numral, String datliv) {
            updatesCalls.add(coddep + "|" + codlab + "|" + numcde + "|" + numral + "|" + datliv);
        }

        @Override
        public void updateDtlivr9994(int numcde9994, int numral9994, String datliv) {
            updates9994.add(numcde9994 + "|" + numral9994 + "|" + datliv);
        }
    }

    /** CascadeLabo3628 contrôlable : retourne une valeur fixe. */
    private static class StubCascade extends CascadeLabo3628 {
        private final Optional<Livraison9994> valeur;

        StubCascade(CdeFacRepository repo, Optional<Livraison9994> valeur) {
            super(repo);
            this.valeur = valeur;
        }

        @Override
        public Optional<Livraison9994> detecter(String coddep, int numcde, int numral) {
            return valeur;
        }
    }

    private StubRepo       repo;
    private MajDtlivrService serviceSans9994;  // cascade retourne empty
    private MajDtlivrService serviceAvec9994;  // cascade retourne une livraison 9994

    @Before
    public void setUp() {
        repo = new StubRepo();
        serviceSans9994 = new MajDtlivrService(repo,
                new StubCascade(repo, Optional.<Livraison9994>empty()));
        serviceAvec9994 = new MajDtlivrService(repo,
                new StubCascade(repo, Optional.of(new Livraison9994(9876543, 1))));
    }

    // ---- Tests de normalisation FO→MO ----

    @Test
    public void coddep_fo_estRemappeEnMo_avantUpdate() {
        LivraisonRecord rec = new LivraisonRecord("FO", "0001", 1111111, 0,
                "08-AUG-2024 00:00:00.00");
        serviceSans9994.traiterRecord(rec);

        assertEquals(1, repo.updatesCalls.size());
        assertTrue("coddep doit être MO",
                repo.updatesCalls.get(0).startsWith("MO|"));
    }

    @Test
    public void coddep_co_estInchange() {
        LivraisonRecord rec = new LivraisonRecord("CO", "0001", 2222222, 0,
                "08-AUG-2024 00:00:00.00");
        serviceSans9994.traiterRecord(rec);

        assertTrue(repo.updatesCalls.get(0).startsWith("CO|"));
    }

    // ---- Tests update principal ----

    @Test
    public void update_principal_utilise_bons_parametres() {
        LivraisonRecord rec = new LivraisonRecord("CO", "LABO", 3333333, 1,
                "15-JAN-2024 14:30:00.00");
        serviceSans9994.traiterRecord(rec);

        assertEquals(1, repo.updatesCalls.size());
        assertEquals("CO|LABO|3333333|1|15-JAN-2024 14:30:00.00",
                repo.updatesCalls.get(0));
    }

    @Test
    public void nonLabo3628_aucunUpdate9994() {
        LivraisonRecord rec = new LivraisonRecord("CO", "0001", 4444444, 0,
                "08-AUG-2024 00:00:00.00");
        // serviceSans9994 : cascade retourne empty de toute façon
        serviceSans9994.traiterRecord(rec);

        assertTrue(repo.updates9994.isEmpty());
    }

    // ---- Tests cascade 3628/9994 ----

    @Test
    public void labo3628_sans9994_seulUpdatePrincipal() {
        LivraisonRecord rec = new LivraisonRecord("CO", "3628", 5555555, 0,
                "08-AUG-2024 00:00:00.00");
        serviceSans9994.traiterRecord(rec);

        assertEquals(1, repo.updatesCalls.size());
        assertTrue(repo.updates9994.isEmpty());
    }

    @Test
    public void labo3628_avec9994_deuxUpdates() {
        LivraisonRecord rec = new LivraisonRecord("CO", "3628", 6666666, 0,
                "20-MAR-2024 00:00:00.00");
        serviceAvec9994.traiterRecord(rec);

        // Update principal
        assertEquals(1, repo.updatesCalls.size());
        assertTrue(repo.updatesCalls.get(0).startsWith("CO|3628|6666666|0|"));

        // Update 9994 (B-T10-05 : CODDEP='CO' CODLAB='9994' hardcodés dans adapter)
        assertEquals(1, repo.updates9994.size());
        // numcde9994=9876543, numral9994=1 (définis dans StubCascade)
        assertEquals("9876543|1|20-MAR-2024 00:00:00.00", repo.updates9994.get(0));
    }

    @Test
    public void update9994_utilise_memeDatliv_queUpdatePrincipal() {
        // B-T10-09 : même W-DATBIN pour les deux updates
        String datliv = "25-DEC-2023 10:00:00.00";
        LivraisonRecord rec = new LivraisonRecord("CO", "3628", 7777777, 0, datliv);
        serviceAvec9994.traiterRecord(rec);

        assertTrue("datliv principal", repo.updatesCalls.get(0).endsWith(datliv));
        assertTrue("datliv 9994", repo.updates9994.get(0).endsWith(datliv));
    }

    @Test
    public void fo_plus_labo3628_remappage_avant_cascade() {
        // B-T10-03 : FO→MO avant TST-3628-9994
        // Le stub cascade reçoit "MO" (pas "FO")
        final List<String> codeDepotRecu = new ArrayList<>();
        CascadeLabo3628 cascadeCapture = new CascadeLabo3628(repo) {
            @Override
            public Optional<Livraison9994> detecter(String coddep, int numcde, int numral) {
                codeDepotRecu.add(coddep);
                return Optional.empty();
            }
        };

        MajDtlivrService svc = new MajDtlivrService(repo, cascadeCapture);
        svc.traiterRecord(new LivraisonRecord("FO", "3628", 8888888, 0,
                "08-AUG-2024 00:00:00.00"));

        assertEquals(1, codeDepotRecu.size());
        assertEquals("MO", codeDepotRecu.get(0));  // FO normalisé en MO
    }

    @Test
    public void cascade_seulementAppeleePourLabo3628() {
        final List<String> cascadeAppels = new ArrayList<>();
        CascadeLabo3628 cascadeCapture = new CascadeLabo3628(repo) {
            @Override
            public Optional<Livraison9994> detecter(String coddep, int numcde, int numral) {
                cascadeAppels.add("called");
                return Optional.empty();
            }
        };

        MajDtlivrService svc = new MajDtlivrService(repo, cascadeCapture);
        // Codlab différent de 3628 → cascade non appelée
        svc.traiterRecord(new LivraisonRecord("CO", "9999", 1234567, 0,
                "08-AUG-2024 00:00:00.00"));

        assertTrue(cascadeAppels.isEmpty());
    }
}
