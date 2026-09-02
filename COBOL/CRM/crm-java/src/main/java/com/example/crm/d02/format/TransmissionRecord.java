package com.example.crm.d02.format;

/**
 * Contrat commun aux 7 variantes d'enregistrement de transmission.
 *
 * Chaque implémentation correspond à une variante COBOL REDEFINES de ENREG-TRANSMIT
 * (DEBCDE, REFCDE, LINTXT, TXTCDE, LINCDE, FINCDE, FINMES) et produit
 * exactement 197 caractères, en respectant les positions absolues définies
 * dans COBOL_VERS_JAVA8_TDD.md §4.
 */
public interface TransmissionRecord {
    /**
     * Retourne l'enregistrement formaté de 197 caractères.
     * Les octets non adressés par la variante sont des espaces
     * (équivalent de INITIALIZE ENREG-TRANSMIT en COBOL).
     */
    String format();
}
