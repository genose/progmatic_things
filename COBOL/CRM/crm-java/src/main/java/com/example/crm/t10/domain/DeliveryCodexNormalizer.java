package com.example.crm.t10.domain;

/**
 * Normalisation du code dépôt (B-T10-03).
 *
 * Règle : si CODDEP = "FO" → remplacer par "MO".
 * S'applique avant toute requête SQL, y compris le check 3628/9994.
 */
public final class DeliveryCodexNormalizer {

    private DeliveryCodexNormalizer() {}

    public static String normalize(String coddep) {
        return "FO".equals(coddep) ? "MO" : coddep;
    }
}
