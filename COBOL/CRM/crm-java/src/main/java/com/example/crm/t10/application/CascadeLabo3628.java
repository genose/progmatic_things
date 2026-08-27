package com.example.crm.t10.application;

import com.example.crm.t10.domain.Livraison9994;
import com.example.crm.t10.port.CdeFacRepository;

import java.util.Optional;

/**
 * Détection de la commande miroir labo 9994 (TST-3628-9994 dans T10).
 *
 * Algorithme :
 * 1. SELECT CDELAB(1:8) FROM E.CDE_FAC WHERE CODDEP=coddep AND CODLAB='3628'
 *    AND NUMCDE=numcde AND NUMRAL=numral LIMIT TO 1 ROW.
 * 2. Si trouvé ET W-CDELAB IS NUMERIC (tous chiffres) :
 *    numcde9994 = CDELAB[0..6] (7 chars), numral9994 = CDELAB[7] (1 char).
 * 3. Vérifier existence dans E.CDE_FAC avec CODDEP='CO' CODLAB='9994'.
 * 4. Si trouvé → retourner Optional<Livraison9994>.
 *
 * Si CDELAB non numérique → Optional.empty() (ignoré silencieusement — B-T10-04).
 */
public class CascadeLabo3628 {

    private final CdeFacRepository repo;

    public CascadeLabo3628(CdeFacRepository repo) {
        this.repo = repo;
    }

    /**
     * @param coddep coddep déjà normalisé (FO→MO appliqué en amont)
     * @param numcde numéro de commande (7 chiffres dans T10)
     * @param numral numéro de ralliement
     * @return Livraison9994 si une commande miroir CO/9994 existe, sinon empty
     */
    public Optional<Livraison9994> detecter(String coddep, int numcde, int numral) {
        Optional<String> cdelabOpt = repo.findCdelab(coddep, "3628", numcde, numral);
        if (!cdelabOpt.isPresent()) {
            return Optional.empty();
        }

        String cdelab = cdelabOpt.get();
        if (!isNumeric(cdelab)) {
            // B-T10-04 : CDELAB non numérique → lien 9994 ignoré silencieusement
            return Optional.empty();
        }

        int numcde9994 = Integer.parseInt(cdelab.substring(0, 7));
        int numral9994 = Integer.parseInt(cdelab.substring(7, 8));

        if (repo.existsCdeFac9994(numcde9994, numral9994)) {
            return Optional.of(new Livraison9994(numcde9994, numral9994));
        }
        return Optional.empty();
    }

    private static boolean isNumeric(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }
}
