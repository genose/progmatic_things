package com.example.crm.t10.application;

import com.example.crm.t10.domain.Livraison9994;
import com.example.crm.t10.port.CdeFacRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests de CascadeLabo3628 (TST-3628-9994).
 *
 * Règles :
 *   - CDELAB trouvé + numérique → vérifier existence 9994
 *   - CDELAB trouvé + non numérique → empty silencieux (B-T10-04)
 *   - CDELAB not found → empty
 *   - 9994 non existant → empty
 *   - CODDEP='CO' hardcodé pour la recherche 9994 (B-T10-05)
 */
public class CascadeLabo3628Test {

    private static class StubRepo implements CdeFacRepository {
        // clé = "coddep|codlab|numcde|numral" → CDELAB
        final Map<String, String> cdelabs = new HashMap<>();
        // ensemble (numcde9994, numral9994) qui existent
        final Map<String, Boolean> exist9994 = new HashMap<>();

        @Override
        public Optional<String> findCdelab(String coddep, String codlab, int numcde, int numral) {
            return Optional.ofNullable(cdelabs.get(coddep + "|" + codlab + "|" + numcde + "|" + numral));
        }

        @Override
        public boolean existsCdeFac9994(int numcde9994, int numral9994) {
            return Boolean.TRUE.equals(exist9994.get(numcde9994 + "|" + numral9994));
        }

        @Override public void updateDtlivr(String c, String l, int n, int r, String d) {}
        @Override public void updateDtlivr9994(int n, int r, String d) {}

        void addCdelab(String coddep, String codlab, int numcde, int numral, String cdelab) {
            cdelabs.put(coddep + "|" + codlab + "|" + numcde + "|" + numral, cdelab);
        }

        void addExist9994(int numcde9994, int numral9994) {
            exist9994.put(numcde9994 + "|" + numral9994, true);
        }
    }

    private StubRepo       repo;
    private CascadeLabo3628 cascade;

    @Before
    public void setUp() {
        repo    = new StubRepo();
        cascade = new CascadeLabo3628(repo);
    }

    @Test
    public void cdelab_numerique_9994Existe_retournelivraison9994() {
        // CDELAB = "12345671" → numcde9994=1234567, numral9994=1
        repo.addCdelab("MO", "3628", 9990001, 0, "12345671");
        repo.addExist9994(1234567, 1);

        Optional<Livraison9994> result = cascade.detecter("MO", 9990001, 0);

        assertTrue(result.isPresent());
        assertEquals(1234567, result.get().getNumcde9994());
        assertEquals(1, result.get().getNumral9994());
    }

    @Test
    public void cdelab_numerique_9994Absent_retourneEmpty() {
        repo.addCdelab("MO", "3628", 9990002, 0, "12345670");
        // 9994 non ajouté → existsCdeFac9994 = false

        Optional<Livraison9994> result = cascade.detecter("MO", 9990002, 0);

        assertFalse(result.isPresent());
    }

    @Test
    public void cdelab_nonNumerique_retourneEmptySilencieusement() {
        // B-T10-04 : CDELAB avec chars non numériques → ignoré
        repo.addCdelab("MO", "3628", 9990003, 0, "ABC12345");

        Optional<Livraison9994> result = cascade.detecter("MO", 9990003, 0);

        assertFalse(result.isPresent());
    }

    @Test
    public void cdelab_avecEspace_nonNumerique_retourneEmpty() {
        repo.addCdelab("MO", "3628", 9990004, 0, "1234567 ");

        Optional<Livraison9994> result = cascade.detecter("MO", 9990004, 0);

        assertFalse(result.isPresent());
    }

    @Test
    public void commandeAbsente_de3628_retourneEmpty() {
        // findCdelab retourne empty (pas dans le stub)
        Optional<Livraison9994> result = cascade.detecter("MO", 9990099, 0);

        assertFalse(result.isPresent());
    }

    @Test
    public void coddepTransmisTelQuelFindCdelab() {
        // Le service utilise le coddep reçu (déjà normalisé FO→MO en amont)
        repo.addCdelab("MO", "3628", 1111111, 0, "22222220");
        repo.addExist9994(2222222, 0);

        // Appel avec "MO" → trouvé
        assertTrue(cascade.detecter("MO", 1111111, 0).isPresent());
        // Appel avec "FO" → non trouvé (normalisation non faite ici)
        assertFalse(cascade.detecter("FO", 1111111, 0).isPresent());
    }

    @Test
    public void extraction_numcde9994_et_numral9994_depuis_cdelab() {
        // "98765432" → numcde9994=9876543, numral9994=2
        repo.addCdelab("CO", "3628", 5555555, 1, "98765432");
        repo.addExist9994(9876543, 2);

        Optional<Livraison9994> result = cascade.detecter("CO", 5555555, 1);

        assertTrue(result.isPresent());
        assertEquals(9876543, result.get().getNumcde9994());
        assertEquals(2, result.get().getNumral9994());
    }
}
