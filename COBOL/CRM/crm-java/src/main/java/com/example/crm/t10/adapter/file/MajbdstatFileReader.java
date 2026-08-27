package com.example.crm.t10.adapter.file;

import com.example.crm.t10.domain.LivraisonRecord;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Lecteur du fichier MAJBDSTAT*.DAT pour T10_MAJ_DTLIVR_BDCRM.
 *
 * En COBOL/VMS, T10 découvre les fichiers via LIB$FIND_FILE sur le
 * pattern "DIRDAT:MAJBDSTAT*.DAT;*" et les lit avec READ MAJBDSTAT.
 * Ce lecteur Java prend en charge un fichier à la fois ; la découverte
 * de plusieurs fichiers est gérée par l'appelant.
 *
 * ── Format enregistrement (COPY DIRCOB:T10_DESC_FICMAJBDSTAT.LIB) ──
 *
 *   Pos  Lg  Champ          Description
 *   ───  ──  ─────────────  ─────────────────────────────────────────
 *     0   4  MAJBD-CODDEP   Code dépôt (ex : "CO  ", "MO  ", "FO  ")
 *     4   4  MAJBD-CODLAB   Code laboratoire (ex : "3628", "9994")
 *     8   7  MAJBD-NUMCDE   Numéro commande PIC 9(7)
 *    15   1  MAJBD-NUMRAL   Numéro ralliement PIC 9(1)
 *    16  23  MAJBD-DATLIV   Date VMS ASCII "DD-MON-YYYY HH:MM:SS.CC"
 *   ───────────────────────────────────────────────────────────────
 *   Total : 39 caractères par enregistrement
 *
 * Les enregistrements VMS sont séparés par CR+LF ou LF selon les
 * RMS file attributes. BufferedReader gère les deux cas.
 * Les lignes inférieures à 40 chars sont ignorées avec un message stderr.
 */
public class MajbdstatFileReader implements Closeable {

    static final int RECORD_LENGTH = 39;

    private final BufferedReader reader;

    public MajbdstatFileReader(File file, Charset charset) throws IOException {
        this.reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), charset));
    }

    public MajbdstatFileReader(InputStream in, Charset charset) {
        this.reader = new BufferedReader(new InputStreamReader(in, charset));
    }

    /**
     * Lit tous les enregistrements du fichier.
     *
     * @return liste de LivraisonRecord dans l'ordre du fichier
     * @throws IOException en cas d'erreur de lecture
     */
    public List<LivraisonRecord> readAll() throws IOException {
        List<LivraisonRecord> records = new ArrayList<LivraisonRecord>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) continue;
            if (line.length() < RECORD_LENGTH) {
                System.err.println("MAJBDSTAT: enregistrement trop court ("
                        + line.length() + " < " + RECORD_LENGTH + "), ignoré: [" + line + "]");
                continue;
            }
            records.add(parse(line));
        }
        return records;
    }

    /**
     * Parse un enregistrement de longueur >= 40.
     * Package-private pour les tests unitaires.
     */
    static LivraisonRecord parse(String line) {
        String coddep = line.substring(0, 4).trim();
        String codlab = line.substring(4, 8).trim();
        int    numcde = Integer.parseInt(line.substring(8, 15).trim());
        int    numral = Integer.parseInt(line.substring(15, 16).trim());
        String datliv = line.substring(16, 39); // "DD-MON-YYYY HH:MM:SS.CC" (23 chars)
        return new LivraisonRecord(coddep, codlab, numcde, numral, datliv);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
