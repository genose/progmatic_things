package com.example.crm.d05intcde.adapter.file;

import com.example.crm.d05intcde.domain.CdeFacRecord;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Lecteur du fichier sequentiel RMS_CDEFAC.
 *
 * Le fichier est un enregistrement RMS OpenVMS a longueur fixe.
 * Chaque enregistrement correspond a ENR-CDEFAC du COBOL (140+ champs).
 * Cette implementation lit un fichier texte dont les champs sont alignes
 * en colonnes fixes (export RMS → CSV ou binaire plat selon l'environnement).
 *
 * TODO : adapter les positions de colonnes a l'export RMS reel.
 */
public final class CdeFacFileReader {

    /**
     * Lit tous les enregistrements du fichier.
     * Chaque ligne = un ENR-CDEFAC.
     *
     * @param cheminFichier chemin absolu du fichier RMS_CDEFAC
     * @return liste des enregistrements (vide si fichier absent ou vide)
     * @throws IOException en cas d'erreur lecture
     */
    public List<CdeFacRecord> lireTous(String cheminFichier) throws IOException {
        List<CdeFacRecord> records = new ArrayList<>();
        List<String> lignes = Files.readAllLines(Paths.get(cheminFichier));
        for (String ligne : lignes) {
            if (ligne.trim().isEmpty()) {
                continue;
            }
            CdeFacRecord rec = parseLigne(ligne);
            if (rec != null) {
                records.add(rec);
            }
        }
        return records;
    }

    /**
     * Parse une ligne en CdeFacRecord.
     * TODO : implementer le decodage des positions fixes reelles du fichier RMS.
     *
     * @param ligne une ligne du fichier RMS_CDEFAC
     * @return un CdeFacRecord ou null si la ligne ne peut pas etre parsee
     */
    CdeFacRecord parseLigne(String ligne) {
        // Implementation minimale — a completer avec les vraies positions RMS
        // Le fichier reel est un binaire RMS ou un CSV selon l'export utilise
        try {
            CdeFacRecord.Builder b = CdeFacRecord.builder();
            // TODO : extraire les champs aux positions fixes du fichier RMS export
            // Exemple (positions a ajuster selon la definition RMS reelle) :
            //   b.codlab = champ(ligne,  0,  4);
            //   b.coddep = champ(ligne,  4,  6);
            //   b.numcde = Long.parseLong(champ(ligne, 10, 18).trim());
            //   ...
            b.codlab  = "";
            b.coddep  = "";
            b.numcde  = 0L;
            b.numral  = 0;
            b.mtht    = BigDecimal.ZERO;
            b.escomp  = BigDecimal.ZERO;
            b.frgest1 = BigDecimal.ZERO;
            b.remisee = BigDecimal.ZERO;
            b.poids   = BigDecimal.ZERO;
            return b.build();
        } catch (Exception e) {
            return null;
        }
    }

}
