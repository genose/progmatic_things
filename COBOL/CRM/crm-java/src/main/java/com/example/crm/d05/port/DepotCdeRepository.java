package com.example.crm.d05.port;

import com.example.crm.d05.domain.CommandeDepot;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Port d'accès aux données pour D05_VERIF_CRM.
 *
 * findByDatebl  → résultat du curseur CURCDE (déjà filtré FLAG_CRM='N',
 *                 STATUT NOT IN ('THO','SIX','EIX'), cast date = W-DATEBL).
 * findStatencours → SELECT STATENCOURS FROM S.CDE_FAC LIMIT TO 1 ROW.
 *                   Empty si '02000' (not found).
 * updateStatcrm → UPDATE D.CDE SET STATCRM='', FLAG_CRM='O'
 *                 WHERE CODLAB=? AND NUMCDE=? AND NUMRAL=?   (pas de CODDEP — B-D05-04).
 */
public interface DepotCdeRepository {

    List<CommandeDepot> findByDatebl(String coddep, LocalDate datebl);

    Optional<String> findStatencours(String coddep, String codlab, int numcde, int numral);

    void updateStatcrm(String codlab, int numcde, int numral);

    /**
     * Valide la transaction READ WRITE (B-D05-06 : COMMIT unique après tous les UPDATEs).
     * Implémentation par défaut no-op (stubs de test non impactés).
     */
    default void commit() {}

    /**
     * Annule la transaction en cours (B-D05-07 : ROLLBACK conditionnel).
     * Implémentation par défaut no-op.
     */
    default void rollback() {}
}
