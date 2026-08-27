package com.example.crm.t10.adapter.file;

import com.example.crm.t10.domain.LivraisonRecord;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests unitaires de MajbdstatFileReader.
 *
 * Format attendu par enregistrement (40 chars) :
 *   0-3  : CODDEP (4 chars, padded)
 *   4-7  : CODLAB (4 chars)
 *   8-14 : NUMCDE (7 chiffres)
 *   15   : NUMRAL (1 chiffre)
 *   16-39: DATLIV "DD-MON-YYYY HH:MM:SS.CC"
 */
public class MajbdstatFileReaderTest {

    private static final String LIGNE_CO =
        "CO  36281234567008-AUG-2024 00:00:00.00";
    //   0123456789012345678901234567890123456789
    //   0         1         2         3
    // = "CO  " + "3628" + "1234567" + "0" + "08-AUG-2024 00:00:00.00"

    @Test
    public void parse_ligneNormale_tousChamps() {
        LivraisonRecord rec = MajbdstatFileReader.parse(LIGNE_CO);

        assertEquals("CO",     rec.getCoddep());
        assertEquals("3628",   rec.getCodlab());
        assertEquals(1234567,  rec.getNumcde());
        assertEquals(0,        rec.getNumral());
        assertEquals("08-AUG-2024 00:00:00.00", rec.getDatliv());
    }

    @Test
    public void parse_coddepPadded_trimme() {
        // CODDEP "MO" stocké sur 4 chars → "MO  "
        String ligne = "MO  9994" + "0000001" + "1" + "15-JAN-2024 14:30:00.00";
        LivraisonRecord rec = MajbdstatFileReader.parse(ligne);
        assertEquals("MO", rec.getCoddep());
        assertEquals("9994", rec.getCodlab());
        assertEquals(1, rec.getNumcde());
        assertEquals(1, rec.getNumral());
    }

    @Test
    public void readAll_deuxLignes_deuxRecords() throws IOException {
        String contenu = LIGNE_CO + "\n" +
                "MO  9994" + "9999999" + "1" + "25-DEC-2023 10:00:00.00" + "\n";

        try (MajbdstatFileReader reader = new MajbdstatFileReader(
                new ByteArrayInputStream(contenu.getBytes(StandardCharsets.ISO_8859_1)),
                StandardCharsets.ISO_8859_1)) {
            List<LivraisonRecord> records = reader.readAll();
            assertEquals(2, records.size());
            assertEquals("CO",   records.get(0).getCoddep());
            assertEquals("MO",   records.get(1).getCoddep());
            assertEquals(9999999, records.get(1).getNumcde());
        }
    }

    @Test
    public void readAll_ligneVide_ignoree() throws IOException {
        String contenu = LIGNE_CO + "\n\n";

        try (MajbdstatFileReader reader = new MajbdstatFileReader(
                new ByteArrayInputStream(contenu.getBytes(StandardCharsets.ISO_8859_1)),
                StandardCharsets.ISO_8859_1)) {
            List<LivraisonRecord> records = reader.readAll();
            assertEquals(1, records.size());
        }
    }

    @Test
    public void readAll_ligneTropCourte_ignoree() throws IOException {
        // Ligne de 10 chars seulement
        String contenu = "CO  3628\n" + LIGNE_CO + "\n";

        try (MajbdstatFileReader reader = new MajbdstatFileReader(
                new ByteArrayInputStream(contenu.getBytes(StandardCharsets.ISO_8859_1)),
                StandardCharsets.ISO_8859_1)) {
            List<LivraisonRecord> records = reader.readAll();
            assertEquals(1, records.size()); // la ligne courte est ignorée
            assertEquals("CO", records.get(0).getCoddep());
        }
    }

    @Test
    public void readAll_fichierVide_retourneListeVide() throws IOException {
        try (MajbdstatFileReader reader = new MajbdstatFileReader(
                new ByteArrayInputStream(new byte[0]),
                StandardCharsets.ISO_8859_1)) {
            List<LivraisonRecord> records = reader.readAll();
            assertTrue(records.isEmpty());
        }
    }

    @Test
    public void parse_crLf_ligneFonctionneComme_lf() throws IOException {
        // Séparateur CR+LF (style VMS / Windows)
        String contenu = LIGNE_CO + "\r\n" +
                "MO  9994" + "0000042" + "0" + "01-MAR-2024 08:00:00.00";

        try (MajbdstatFileReader reader = new MajbdstatFileReader(
                new ByteArrayInputStream(contenu.getBytes(StandardCharsets.ISO_8859_1)),
                StandardCharsets.ISO_8859_1)) {
            List<LivraisonRecord> records = reader.readAll();
            assertEquals(2, records.size());
            assertEquals("MO", records.get(1).getCoddep());
            assertEquals(42,   records.get(1).getNumcde());
        }
    }
}
