package com.example.crm.d05intcde.adapter.jdbc;

import com.example.crm.d05intcde.port.BdDepotCdePort;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Adaptateur JDBC Oracle Rdb pour BD_DEPOT.D.CDE.
 *
 * Implemente BdDepotCdePort (regle B-D05I-03) :
 * mises a jour conditionnelles sur D.CDE quand CODLAB='9994'.
 *
 * Toutes les requetes filtrent sur CODLAB='9994' (hardcode COBOL).
 */
public final class RdbBdDepotCdeRepo implements BdDepotCdePort {

    private final Connection conn;

    public RdbBdDepotCdeRepo(Connection conn) {
        this.conn = conn;
    }

    /** UPDATE D.CDE SET DTLIVR=? WHERE NUMCDE=? AND NUMRAL=? AND CODLAB='9994' */
    @Override
    public void updateCdeLiv(long numcde, int numral, Timestamp dtlivr) {
        String sql = "UPDATE D.CDE SET DTLIVR=? WHERE NUMCDE=? AND NUMRAL=? AND CODLAB='9994'";
        execUpdate(sql, dtlivr, numcde, numral);
    }

    /** UPDATE D.CDE SET DATEBL=?, STATUT='FAT' WHERE NUMCDE=? AND NUMRAL=? AND CODLAB='9994' */
    @Override
    public void updateCdeFac(long numcde, int numral, Timestamp datebl) {
        String sql = "UPDATE D.CDE SET DATEBL=?, STATUT='FAT'"
                   + " WHERE NUMCDE=? AND NUMRAL=? AND CODLAB='9994'";
        execUpdate(sql, datebl, numcde, numral);
    }

    /** UPDATE D.CDE SET STATUT='BLO' WHERE NUMCDE=? AND NUMRAL=? AND CODLAB='9994' */
    @Override
    public void updateCdeBlo(long numcde, int numral) {
        String sql = "UPDATE D.CDE SET STATUT='BLO'"
                   + " WHERE NUMCDE=? AND NUMRAL=? AND CODLAB='9994'";
        execUpdateNoTs(sql, numcde, numral);
    }

    /** UPDATE D.CDE SET STATUT='SEI' WHERE NUMCDE=? AND NUMRAL=? AND CODLAB='9994' */
    @Override
    public void updateCdeSup(long numcde, int numral) {
        String sql = "UPDATE D.CDE SET STATUT='SEI'"
                   + " WHERE NUMCDE=? AND NUMRAL=? AND CODLAB='9994'";
        execUpdateNoTs(sql, numcde, numral);
    }

    /** Execute UPDATE avec un Timestamp en premier parametre, puis numcde + numral. */
    private void execUpdate(String sql, Timestamp ts, long numcde, int numral) {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, ts);
            ps.setLong     (2, numcde);
            ps.setInt      (3, numral);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("execUpdate D.CDE CODLAB=9994", e);
        }
    }

    /** Execute UPDATE sans Timestamp (STATUT seulement). */
    private void execUpdateNoTs(String sql, long numcde, int numral) {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, numcde);
            ps.setInt (2, numral);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("execUpdateNoTs D.CDE CODLAB=9994", e);
        }
    }
}
