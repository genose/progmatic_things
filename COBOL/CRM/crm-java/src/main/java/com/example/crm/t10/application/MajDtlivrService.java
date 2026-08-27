package com.example.crm.t10.application;

import com.example.crm.t10.domain.DeliveryCodexNormalizer;
import com.example.crm.t10.domain.Livraison9994;
import com.example.crm.t10.domain.LivraisonRecord;
import com.example.crm.t10.port.CdeFacRepository;

import java.util.Optional;

/**
 * Migration de TRAITEMENT-INFOS dans T10_MAJ_DTLIVR_BDCRM.COB.
 *
 * Par enregistrement :
 *   1. Normaliser coddep (FO→MO) — B-T10-03.
 *   2. Si codlab='3628' → détecter commande miroir 9994.
 *   3. UPDATE E.CDE_FAC SET DTLIVR=datliv, FLAGLIV='O'
 *      avec COMMIT par enregistrement (B-T10-07).
 *   4. Si CDE9994 → UPDATE 9994 avec même datliv (B-T10-09).
 *
 * Deadlock : délégué à l'adapter JDBC. En cas de deadlock,
 * l'adapter doit lever une RuntimeException (→ équivalent STOP RUN, B-T10-10).
 * Le service ne gère pas de retry.
 */
public class MajDtlivrService {

    private final CdeFacRepository repo;
    private final CascadeLabo3628  cascade;

    public MajDtlivrService(CdeFacRepository repo) {
        this.repo    = repo;
        this.cascade = new CascadeLabo3628(repo);
    }

    /** Constructeur pour injection du cascade (tests). */
    public MajDtlivrService(CdeFacRepository repo, CascadeLabo3628 cascade) {
        this.repo    = repo;
        this.cascade = cascade;
    }

    /**
     * Traite un enregistrement de livraison.
     *
     * @param record enregistrement brut du fichier MAJBDSTAT
     */
    public void traiterRecord(LivraisonRecord record) {
        // Étape 1 : normalisation coddep FO→MO (B-T10-03)
        String coddep = DeliveryCodexNormalizer.normalize(record.getCoddep());
        String codlab = record.getCodlab();
        int    numcde = record.getNumcde();
        int    numral = record.getNumral();
        String datliv = record.getDatliv();

        // Étape 2 : détection cascade 3628/9994
        Optional<Livraison9994> livraison9994 = Optional.empty();
        if ("3628".equals(codlab)) {
            livraison9994 = cascade.detecter(coddep, numcde, numral);
        }

        // Étape 3 : UPDATE principal
        repo.updateDtlivr(coddep, codlab, numcde, numral, datliv);

        // Étape 4 : UPDATE 9994 si cascade détectée (même datliv — B-T10-09)
        if (livraison9994.isPresent()) {
            Livraison9994 l = livraison9994.get();
            repo.updateDtlivr9994(l.getNumcde9994(), l.getNumral9994(), datliv);
        }

        // COMMIT par enregistrement (B-T10-07) — no-op pour les stubs de test
        repo.commit();
    }
}
