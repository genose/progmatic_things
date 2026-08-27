package com.example.crm.d05.adapter.jdbc;

import com.example.crm.d05.domain.CommandeDepot;
import com.example.crm.d05.port.DepotCdeRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adapter JDBC Oracle Rdb pour DepotCdeRepository — D05_VERIF_CRM.
 *
 * Accès à deux bases distinctes :
 *   depotDs → BD_DEPOT, table D.CDE  : findByDatebl, updateStatcrm
 *   crmDs   → BD_CRM,   table S.CDE_FAC : findStatencours
 *
 * ── Gestion transactionnelle ──
 * Phase 1 (scan READ ONLY) :
 *   findByDatebl()    : connexion éphémère, read-only, auto-commit
 *   findStatencours() : connexion éphémère, read-only, auto-commit
 *
 * Phase 2 (MAJ READ WRITE) :
 *   updateStatcrm() : connexion rwConn, auto-commit=false, accumulée
 *   commit()        : COMMIT unique (B-D05-06) puis fermeture de rwConn
 *   rollback()      : ROLLBACK conditionnel (B-D05-07)
 *
 * Le commit est déclenché par VerifCrmService après la boucle d'updates.
 *
 * ── SQL Oracle Rdb spécifique ──
 * • LIMIT TO 1 ROW   : syntaxe Oracle Rdb
 * • DATEBL = ?       : le driver JDBC convertit java.sql.Date vers DATE VMS
 *   (Original COBOL : CAST(CAST(DATEBL AS DATE ANSI) AS DATE VMS) = :W-DATEBL)
 */
public class RdbDepotCdeRepository implements DepotCdeRepository {

    // Curseur CURCDE — filtres du COBOL D05 (FLAG_CRM='N', THO/SIX/EIX exclus)
    private static final String SQL_CURCDE =
            "SELECT NUMCDE, NUMRAL, STATUT, CODLAB FROM D.CDE" +
            " WHERE CODDEP = ?" +
            "   AND DATEBL = ?" +
            "   AND FLAG_CRM = 'N'" +
            "   AND STATUT <> 'THO'" +
            "   AND STATUT <> 'SIX'" +
            "   AND STATUT <> 'EIX'";
    // Note : le COBOL utilise CAST(CAST(DATEBL AS DATE ANSI) AS DATE VMS) = :W-DATEBL
    // car W-DATEBL est un quadword VMS. Avec JDBC, java.sql.Date est converti par le
    // driver Oracle Rdb vers le type DATE de la colonne.

    // GET-CDE-CRM — lecture STATENCOURS dans BD_CRM
    private static final String SQL_STATENCOURS =
            "SELECT STATENCOURS FROM S.CDE_FAC" +
            " WHERE CODDEP = ? AND CODLAB = ? AND NUMCDE = ? AND NUMRAL = ?" +
            " LIMIT TO 1 ROW";

    // MAJ-CDE — UPDATE sans CODDEP dans la clé (B-D05-04)
    private static final String SQL_UPDATE_STATCRM =
            "UPDATE D.CDE SET STATCRM = '', FLAG_CRM = 'O'" +
            " WHERE CODLAB = ? AND NUMCDE = ? AND NUMRAL = ?";

    private final DataSource depotDs;
    private final DataSource crmDs;

    /** Connexion READ WRITE maintenue pendant la phase de MAJ. */
    private Connection rwConn;

    public RdbDepotCdeRepository(DataSource depotDs, DataSource crmDs) {
        this.depotDs = depotDs;
        this.crmDs   = crmDs;
    }

    // ── Phase 1 : lectures READ ONLY ──────────────────────────────────────────

    @Override
    public List<CommandeDepot> findByDatebl(String coddep, LocalDate datebl) {
        List<CommandeDepot> result = new ArrayList<CommandeDepot>();
        try (Connection conn = depotDs.getConnection()) {
            conn.setReadOnly(true);
            try (PreparedStatement ps = conn.prepareStatement(SQL_CURCDE)) {
                ps.setString(1, coddep);
                ps.setDate(2, Date.valueOf(datebl));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(new CommandeDepot(
                                rs.getInt("NUMCDE"),
                                rs.getInt("NUMRAL"),
                                rs.getString("STATUT").trim(),
                                rs.getString("CODLAB").trim()
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByDatebl(" + coddep + "," + datebl + ")", e);
        }
        return result;
    }

    @Override
    public Optional<String> findStatencours(String coddep, String codlab, int numcde, int numral) {
        try (Connection conn = crmDs.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_STATENCOURS)) {
            ps.setString(1, coddep);
            ps.setString(2, codlab);
            ps.setInt(3, numcde);
            ps.setInt(4, numral);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String val = rs.getString(1);
                    // SQLSTATE '22002' (NULL success) : Oracle Rdb retourne null pour
                    // une colonne nullable vide — traité comme présent mais vide
                    return Optional.of(val == null ? "" : val.trim());
                }
                return Optional.empty(); // SQLSTATE '02000' — not found
            }
        } catch (SQLException e) {
            // DEADLOCK (B-D05-08) : propagé → service sort de la boucle CURCDE
            throw new RuntimeException(
                "findStatencours(" + coddep + "," + codlab + "," + numcde + "," + numral + ")", e);
        }
    }

    // ── Phase 2 : écritures READ WRITE ───────────────────────────────────────

    @Override
    public void updateStatcrm(String codlab, int numcde, int numral) {
        // Clé : (CODLAB, NUMCDE, NUMRAL) — CODDEP absent (B-D05-04)
        try {
            PreparedStatement ps = getRwConnection().prepareStatement(SQL_UPDATE_STATCRM);
            try {
                ps.setString(1, codlab);
                ps.setInt(2, numcde);
                ps.setInt(3, numral);
                ps.executeUpdate();
            } finally {
                ps.close();
            }
        } catch (SQLException e) {
            rollbackSilently();
            throw new RuntimeException(
                "updateStatcrm(" + codlab + "," + numcde + "," + numral + ")", e);
        }
    }

    /**
     * COMMIT unique après tous les UPDATEs (B-D05-06).
     * Appelé par VerifCrmService après la boucle updateStatcrm().
     */
    @Override
    public void commit() {
        if (rwConn == null) return;
        try {
            rwConn.commit();
        } catch (SQLException e) {
            rollbackSilently();
            throw new RuntimeException("commit D05", e);
        } finally {
            closeRwConn();
        }
    }

    /**
     * ROLLBACK conditionnel (B-D05-07 : seulement si P-MAJ='O' ET cpt-cde > 0).
     * La condition est vérifiée par le service avant d'appeler cette méthode.
     */
    @Override
    public void rollback() {
        rollbackSilently();
    }

    // ── Gestion interne connexion READ WRITE ──────────────────────────────────

    private Connection getRwConnection() throws SQLException {
        if (rwConn == null || rwConn.isClosed()) {
            rwConn = depotDs.getConnection();
            rwConn.setAutoCommit(false);
            // Oracle Rdb : SET TRANSACTION READ WRITE (implicite via auto-commit=false)
        }
        return rwConn;
    }

    private void rollbackSilently() {
        if (rwConn == null) return;
        try { rwConn.rollback(); } catch (SQLException ignored) {}
        closeRwConn();
    }

    private void closeRwConn() {
        try {
            if (rwConn != null && !rwConn.isClosed()) rwConn.close();
        } catch (SQLException ignored) {}
        rwConn = null;
    }
}
