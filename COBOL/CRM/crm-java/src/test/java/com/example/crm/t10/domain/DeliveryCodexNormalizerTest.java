package com.example.crm.t10.domain;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests de DeliveryCodexNormalizer (règle B-T10-03).
 *
 * Règle : CODDEP="FO" → "MO", tout autre code reste inchangé.
 */
public class DeliveryCodexNormalizerTest {

    @Test
    public void fo_estRemplaceParMo() {
        assertEquals("MO", DeliveryCodexNormalizer.normalize("FO"));
    }

    @Test
    public void co_estInchange() {
        assertEquals("CO", DeliveryCodexNormalizer.normalize("CO"));
    }

    @Test
    public void mo_estInchange() {
        assertEquals("MO", DeliveryCodexNormalizer.normalize("MO"));
    }

    @Test
    public void autreCode_estInchange() {
        assertEquals("XX", DeliveryCodexNormalizer.normalize("XX"));
        assertEquals("DP", DeliveryCodexNormalizer.normalize("DP"));
    }

    @Test
    public void codeVide_estInchange() {
        assertEquals("", DeliveryCodexNormalizer.normalize(""));
    }

    @Test
    public void fo_minuscule_nestPasRemappe() {
        // La règle COBOL est case-sensitive : "fo" != "FO"
        assertEquals("fo", DeliveryCodexNormalizer.normalize("fo"));
    }
}
