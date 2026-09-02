package com.example.crm.d02.port;

import java.time.LocalDateTime;

/**
 * Port d'accès à la date/heure courante.
 * Rend la date système déterministe dans les tests (comportement B05 et B12).
 *
 * En production : {@code () -> LocalDateTime.now()}.
 * En test       : retourner une date fixe connue.
 */
public interface ClockPort {
    LocalDateTime now();
}
