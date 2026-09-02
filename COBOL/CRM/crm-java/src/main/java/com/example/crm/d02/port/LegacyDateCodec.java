package com.example.crm.d02.port;

import java.time.LocalDateTime;

/**
 * Port de conversion des dates VMS / COBOL.
 *
 * Le programme COBOL utilise des dates binaires VMS (PIC S9(11)V9(7) COMP)
 * et les fonctions SYS$BINTIM / SYS$ASCTIM pour les conversions.
 * Cette interface isole ces conversions derrière un contrat testable.
 *
 * Format VMS ASCII : "DD-MON-YYYY HH:MI:SS.CC"
 *   DD  = jour 01-31
 *   MON = mois abrégé 3 lettres majuscules (JAN FEB MAR APR MAY JUN JUL AUG SEP OCT NOV DEC)
 *   YYYY = année 4 chiffres
 *   HH:MI:SS.CC = heure:minute:seconde.centisecondes
 *
 * Ne pas disperser ces conversions dans les repositories et les formatteurs.
 */
public interface LegacyDateCodec {

    /**
     * Convertit un LocalDateTime en chaîne ASCII VMS 23 caractères.
     * Exemple : LocalDateTime(2026, 8, 20, 10, 30, 0) → "20-AUG-2026 10:30:00.00"
     */
    String toVmsAscii(LocalDateTime dt);

    /**
     * Analyse une chaîne ASCII VMS 23 caractères et retourne le LocalDateTime correspondant.
     * Les centisecondes (CC) sont converties en nanoseconde × 10 000 000.
     */
    LocalDateTime fromVmsAscii(String vmsAscii);

    /**
     * Retourne la date au format YYYYMMDD (8 caractères).
     * Exemple : LocalDateTime(2026, 8, 20, ...) → "20260820"
     * Conversion COBOL : SYS$ASCTIM → recomposition YYYYMMDD + INSPECT espaces→'0'.
     */
    String toYyyyMmDd(LocalDateTime dt);

    /**
     * Retourne la date au format YYMMDD (6 caractères, siècle ignoré).
     * Exemple : LocalDateTime(2026, 8, 20, ...) → "260820"
     * Correspond à ficDatecde.substring(2, 8) en Java.
     * Utilisé pour ENR-REFCDE-DATCDE (comportement B10).
     */
    String toYyMmDd(LocalDateTime dt);
}
