package com.example.crm.d05.domain;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests de StatutEquivalence (règles B-D05-03).
 *
 * Règles :
 *   - Statuts identiques → équivalent
 *   - FAT (DEPOT) / FAP (CRM) → équivalent
 *   - GLT (DEPOT) / GLP (CRM) → équivalent
 *   - Tout autre combinaison différente → NON équivalent
 *   - La comparaison est directionnelle (FAP/FAT n'est PAS équivalent)
 */
public class StatutEquivalenceTest {

    @Test
    public void statutsIdentiques_sontEquivalents() {
        assertTrue(StatutEquivalence.areEquivalent("FAX", "FAX"));
        assertTrue(StatutEquivalence.areEquivalent("GLT", "GLT"));
        assertTrue(StatutEquivalence.areEquivalent("FAT", "FAT"));
    }

    @Test
    public void fat_fap_estEquivalent() {
        assertTrue(StatutEquivalence.areEquivalent("FAT", "FAP"));
    }

    @Test
    public void glt_glp_estEquivalent() {
        assertTrue(StatutEquivalence.areEquivalent("GLT", "GLP"));
    }

    @Test
    public void statutsDifferents_sontNonEquivalents() {
        assertFalse(StatutEquivalence.areEquivalent("FAT", "GLT"));
        assertFalse(StatutEquivalence.areEquivalent("FAX", "FAP"));
        assertFalse(StatutEquivalence.areEquivalent("GLT", "FAP"));
    }

    @Test
    public void comparaisonEstDirectionnelle_fap_fat_estNonEquivalent() {
        // B-D05-03 : seul FAT(depot)/FAP(crm) est équivalent, pas l'inverse
        assertFalse(StatutEquivalence.areEquivalent("FAP", "FAT"));
    }

    @Test
    public void comparaisonEstDirectionnelle_glp_glt_estNonEquivalent() {
        assertFalse(StatutEquivalence.areEquivalent("GLP", "GLT"));
    }

    @Test
    public void statut_vide_avec_vide_estEquivalent() {
        assertTrue(StatutEquivalence.areEquivalent("", ""));
    }

    @Test
    public void statut_vide_avec_autreStatut_estNonEquivalent() {
        assertFalse(StatutEquivalence.areEquivalent("", "FAX"));
        assertFalse(StatutEquivalence.areEquivalent("FAX", ""));
    }
}
