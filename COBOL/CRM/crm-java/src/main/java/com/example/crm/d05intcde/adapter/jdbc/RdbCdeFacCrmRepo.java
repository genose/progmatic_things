package com.example.crm.d05intcde.adapter.jdbc;

import com.example.crm.d05intcde.domain.CdeFacKey;
import com.example.crm.d05intcde.domain.CdeFacRecord;
import com.example.crm.d05intcde.domain.DateExtracts;
import com.example.crm.d05intcde.port.CdeFacRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Adaptateur JDBC Oracle Rdb pour BD_CRM.S.CDE_FAC.
 *
 * Implemente CdeFacRepository (port d05intcde) :
 *   - selectStatut : SELECT STATENCOURS, CPT_FAC WHERE (CODLAB, CODDEP, NUMCDE, NUMRAL)
 *   - insert       : INSERT INTO S.CDE_FAC (80+ colonnes) — CREATION-CDEFAC
 *   - update       : UPDATE S.CDE_FAC (70+ colonnes)     — UPDATE-CDEFAC (B-D05I-10)
 *   - commit / rollback
 *
 * TODO : completer les requetes SQL avec les 70-80 colonnes reelles de S.CDE_FAC.
 */
public final class RdbCdeFacCrmRepo implements CdeFacRepository {

    private final Connection conn;

    public RdbCdeFacCrmRepo(Connection conn) {
        this.conn = conn;
    }

    @Override
    public StatutResult selectStatut(CdeFacKey key) {
        // B-D05I-06 : verif-statut-final — SQLSTATE '02000' → absent=true
        String sql = "SELECT STATENCOURS, CPT_FAC FROM S.CDE_FAC"
                   + " WHERE CODLAB=? AND CODDEP=? AND NUMCDE=? AND NUMRAL=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key.codlab);
            ps.setString(2, key.coddep);
            ps.setLong  (3, key.numcde);
            ps.setInt   (4, key.numral);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return new StatutResult(true, "", 0L);
                }
                String statencours = rs.getString(1);
                long   cptfac      = rs.getLong(2);
                return new StatutResult(false,
                        statencours == null ? "" : statencours.trim(),
                        cptfac);
            }
        } catch (SQLException e) {
            throw new RuntimeException("selectStatut " + key, e);
        }
    }

    @Override
    public void insert(CdeFacRecord rec, DateExtracts extCde, DateExtracts extBl,
                       DateExtracts extFac,
                       Timestamp tsCde, Timestamp tsBl, Timestamp tsSai,
                       Timestamp tsFac, Timestamp tsEch,
                       Timestamp tsDtlivs, Timestamp tsDtlivr) {
        // TODO : INSERT INTO S.CDE_FAC avec les 80+ colonnes (FLAGSTD, FLAGDET inclus)
        // Respecter B-D05I-10 : FLAGSTD et FLAGDET presents en INSERT seulement
        throw new UnsupportedOperationException(
            "RdbCdeFacCrmRepo.insert — a implementer avec les colonnes S.CDE_FAC reelles");
    }

    @Override
    public void update(CdeFacRecord rec, DateExtracts extCde, DateExtracts extBl,
                       DateExtracts extFac,
                       Timestamp tsCde, Timestamp tsBl, Timestamp tsSai,
                       Timestamp tsFac, Timestamp tsEch,
                       Timestamp tsDtlivs, Timestamp tsDtlivr) {
        // TODO : UPDATE S.CDE_FAC avec les 70+ colonnes (sans FLAGSTD ni FLAGDET — B-D05I-10)
        throw new UnsupportedOperationException(
            "RdbCdeFacCrmRepo.update — a implementer avec les colonnes S.CDE_FAC reelles");
    }

    @Override
    public void commit() {
        try {
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("commit", e);
        }
    }

    @Override
    public void rollback() {
        try {
            conn.rollback();
        } catch (SQLException e) {
            throw new RuntimeException("rollback", e);
        }
    }
}
