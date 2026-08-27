package com.example.crm.t10.port;

import java.util.Optional;

/**
 * Port d'accès à E.CDE_FAC (BD_CRM) pour T10_MAJ_DTLIVR_BDCRM.
 *
 * findCdelab  → SELECT SUBSTRING(CDELAB FROM 1 FOR 8) FROM E.CDE_FAC
 *               WHERE CODDEP=? AND CODLAB=? AND NUMCDE=? AND NUMRAL=?
 *               LIMIT TO 1 ROW
 *               Retourne Empty si '02000' (not found).
 *
 * existsCdeFac9994 → idem avec CODDEP='CO' CODLAB='9994' (B-T10-05).
 *
 * updateDtlivr → UPDATE E.CDE_FAC SET DTLIVR=:datliv, FLAGLIV='O'
 *                WHERE CODDEP=? AND CODLAB=? AND NUMCDE=? AND NUMRAL=?
 *                L'adapter gère SET TRANSACTION READ WRITE + COMMIT (B-T10-07).
 *
 * updateDtlivr9994 → même UPDATE avec CODDEP='CO' CODLAB='9994' (B-T10-09).
 *                    datliv identique à la commande principale.
 */
public interface CdeFacRepository {

    Optional<String> findCdelab(String coddep, String codlab, int numcde, int numral);

    boolean existsCdeFac9994(int numcde9994, int numral9994);

    void updateDtlivr(String coddep, String codlab, int numcde, int numral, String datliv);

    void updateDtlivr9994(int numcde9994, int numral9994, String datliv);

    /**
     * Valide la transaction en cours (B-T10-07 : COMMIT par enregistrement).
     * Implémentation par défaut no-op (stubs de test non impactés).
     */
    default void commit() {}
}
