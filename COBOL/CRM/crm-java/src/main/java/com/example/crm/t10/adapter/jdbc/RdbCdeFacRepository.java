package com.example.crm.t10.adapter.jdbc;

import com.example.crm.t10.port.CdeFacRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

/**
 * Adapter JDBC Oracle Rdb pour CdeFacRepository — T10_MAJ_DTLIVR_BDCRM.
 *
 * Base cible : BD_CRM, table E.CDE_FAC.
 *
 * ── Gestion transactionnelle (B-T10-07 : COMMIT par enregistrement) ──
 * Une connexion dédiée (txConn, auto-commit=false) est ouverte à la
 * première écriture et maintenue entre updateDtlivr() et commit().
 * commit() valide puis ferme cette connexion.
 * En cas d'erreur SQL (deadlock compris), rollback() est déclenché
 * en interne et une RuntimeException est levée → équivalent STOP RUN
 * (B-T10-10).
 *
 * Les lectures (findCdelab, existsCdeFac9994) utilisent des connexions
 * éphémères indépendantes de la transaction d'écriture — fidèle au
 * COBOL où TST-3628-9994 précède SET TRANSACTION READ WRITE.
 *
 * ── SQL Oracle Rdb spécifique ──
 * • LIMIT TO 1 ROW  : équivalent de FETCH FIRST 1 ROW ONLY
 * • SUBSTRING(col FROM 1 FOR 8) : syntaxe Oracle Rdb
 */
public class RdbCdeFacRepository implements CdeFacRepository {

    // Lecture CDELAB pour la cascade 3628→9994 (TST-3628-9994 étape 1)
    private static final String SQL_FIND_CDELAB =
            "SELECT SUBSTRING(CDELAB FROM 1 FOR 8) FROM E.CDE_FAC" +
            " WHERE CODDEP = ? AND CODLAB = ? AND NUMCDE = ? AND NUMRAL = ?" +
            " LIMIT TO 1 ROW";

    // Existence commande miroir 9994 (TST-3628-9994 étape 2 — B-T10-05)
    private static final String SQL_EXIST_9994 =
            "SELECT NUMCDE FROM E.CDE_FAC" +
            " WHERE CODDEP = 'CO' AND CODLAB = '9994' AND NUMCDE = ? AND NUMRAL = ?" +
            " LIMIT TO 1 ROW";

    // UPDATE principal : DTLIVR + FLAGLIV='O'
    private static final String SQL_UPDATE_DTLIVR =
            "UPDATE E.CDE_FAC SET DTLIVR = ?, FLAGLIV = 'O'" +
            " WHERE CODDEP = ? AND CODLAB = ? AND NUMCDE = ? AND NUMRAL = ?";

    // UPDATE miroir 9994 : CODDEP='CO' CODLAB='9994' hardcodés (B-T10-05)
    private static final String SQL_UPDATE_9994 =
            "UPDATE E.CDE_FAC SET DTLIVR = ?, FLAGLIV = 'O'" +
            " WHERE CODDEP = 'CO' AND CODLAB = '9994' AND NUMCDE = ? AND NUMRAL = ?";

    private final DataSource crmDs;

    /** Connexion READ WRITE maintenue pendant un cycle updateDtlivr → commit(). */
    private Connection txConn;

    public RdbCdeFacRepository(DataSource crmDs) {
        this.crmDs = crmDs;
    }

    // ── Lectures (connexions éphémères, hors transaction d'écriture) ──────────

    @Override
    public Optional<String> findCdelab(String coddep, String codlab, int numcde, int numral) {
        try (Connection conn = crmDs.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_CDELAB)) {
            ps.setString(1, coddep);
            ps.setString(2, codlab);
            ps.setInt(3, numcde);
            ps.setInt(4, numral);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString(1));
                }
                return Optional.empty(); // SQLSTATE '02000' — not found
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                "findCdelab(" + coddep + "," + codlab + "," + numcde + "," + numral + ")", e);
        }
    }

    @Override
    public boolean existsCdeFac9994(int numcde9994, int numral9994) {
        try (Connection conn = crmDs.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXIST_9994)) {
            ps.setInt(1, numcde9994);
            ps.setInt(2, numral9994);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                "existsCdeFac9994(" + numcde9994 + "," + numral9994 + ")", e);
        }
    }

    // ── Écritures (connexion READ WRITE partagée, commit() explicite) ─────────

    @Override
    public void updateDtlivr(String coddep, String codlab, int numcde, int numral, String datliv) {
        try {
            PreparedStatement ps = getTxConnection().prepareStatement(SQL_UPDATE_DTLIVR);
            try {
                ps.setTimestamp(1, VmsDateParser.parse(datliv));
                ps.setString(2, coddep);
                ps.setString(3, codlab);
                ps.setInt(4, numcde);
                ps.setInt(5, numral);
                ps.executeUpdate();
            } finally {
                ps.close();
            }
        } catch (SQLException e) {
            rollbackSilently();
            throw new RuntimeException(
                "updateDtlivr(" + coddep + "," + codlab + "," + numcde + "," + numral + ")", e);
        }
    }

    @Override
    public void updateDtlivr9994(int numcde9994, int numral9994, String datliv) {
        try {
            PreparedStatement ps = getTxConnection().prepareStatement(SQL_UPDATE_9994);
            try {
                ps.setTimestamp(1, VmsDateParser.parse(datliv));
                ps.setInt(2, numcde9994);
                ps.setInt(3, numral9994);
                ps.executeUpdate();
            } finally {
                ps.close();
            }
        } catch (SQLException e) {
            rollbackSilently();
            throw new RuntimeException(
                "updateDtlivr9994(" + numcde9994 + "," + numral9994 + ")", e);
        }
    }

    /**
     * COMMIT de la transaction en cours (B-T10-07 : par enregistrement).
     * Ferme la connexion READ WRITE après commit.
     */
    @Override
    public void commit() {
        if (txConn == null) return;
        try {
            txConn.commit();
        } catch (SQLException e) {
            rollbackSilently();
            throw new RuntimeException("commit T10", e);
        } finally {
            closeTxConn();
        }
    }

    // ── Gestion interne connexion ─────────────────────────────────────────────

    private Connection getTxConnection() throws SQLException {
        if (txConn == null || txConn.isClosed()) {
            txConn = crmDs.getConnection();
            txConn.setAutoCommit(false);
            // Oracle Rdb : SET TRANSACTION READ WRITE (implicite en auto-commit=false)
        }
        return txConn;
    }

    private void rollbackSilently() {
        if (txConn == null) return;
        try { txConn.rollback(); } catch (SQLException ignored) {}
        closeTxConn();
    }

    private void closeTxConn() {
        try {
            if (txConn != null && !txConn.isClosed()) txConn.close();
        } catch (SQLException ignored) {}
        txConn = null;
    }
}
