package com.example.crm.d05intcde.application;

import com.example.crm.d05intcde.domain.CdeFacRecord;
import org.junit.Test;
import static org.junit.Assert.*;

public class StatutFilterTest {

    private final StatutFilter filter = new StatutFilter();

    private CdeFacRecord rec(String codlab, long cptfac) {
        CdeFacRecord.Builder b = CdeFacRecord.builder();
        b.codlab = codlab;
        b.cptfac = cptfac;
        return b.build();
    }

    // --- statuts non terminaux ---

    @Test
    public void statut_vide_doit_traiter() {
        assertTrue(filter.doitTraiter("", 0, rec("XXXX", 0)));
    }

    @Test
    public void statut_CRV_doit_traiter() {
        assertTrue(filter.doitTraiter("CRV", 0, rec("XXXX", 0)));
    }

    // --- statuts terminaux → skip ---

    @Test
    public void statut_FAT_est_terminal_skip() {
        assertFalse(filter.doitTraiter("FAT", 0, rec("XXXX", 0)));
    }

    @Test
    public void statut_GLT_est_terminal_skip() {
        assertFalse(filter.doitTraiter("GLT", 0, rec("XXXX", 0)));
    }

    @Test
    public void statut_FAP_est_terminal_skip() {
        assertFalse(filter.doitTraiter("FAP", 0, rec("XXXX", 0)));
    }

    @Test
    public void statut_GLP_est_terminal_skip() {
        assertFalse(filter.doitTraiter("GLP", 0, rec("XXXX", 0)));
    }

    // --- exception codlab 3010 (B-D05I-02) ---

    @Test
    public void codlab3010_cptfacElevee_wsZero_passe_malgre_terminal() {
        assertTrue(filter.doitTraiter("FAT", 0L, rec("3010", 20001L)));
    }

    @Test
    public void codlab3010_cptfacElevee_wsNonZero_skip() {
        assertFalse(filter.doitTraiter("FAT", 1L, rec("3010", 20001L)));
    }

    @Test
    public void codlab3010_cptfacExact20000_skip() {
        assertFalse(filter.doitTraiter("FAT", 0L, rec("3010", 20000L)));
    }

    @Test
    public void autreCodelab_statut_terminal_skip() {
        assertFalse(filter.doitTraiter("FAT", 0L, rec("1234", 99999L)));
    }
}
