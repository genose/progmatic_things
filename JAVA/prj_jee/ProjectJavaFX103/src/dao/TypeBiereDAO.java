package dao;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

import dao.objectInterface.DAO;
import metier.TypeBiere;

public class TypeBiereDAO extends DAO<TypeBiere> {

	public TypeBiereDAO(Connection connexion) {
		super(connexion);
	}

	@Override
	public TypeBiere getByID(int id) {

		String strCmd = "SELECT " + TypeBiere.fieldID + " ," + TypeBiere.fieldLibelle + " from "
				+ TypeBiere.fieldEntityName + "  where " + TypeBiere.fieldID + " = ?";
		TypeBiere aResult = new TypeBiere();
		ResultSet rs = null;
		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd)) {
			
		
			if (id > 0) {
				pStmt.setInt(1, id);
			} else {
				throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);
			
			}

			rs = pStmt.executeQuery();
			if (rs.next()) {
				aResult.setId(rs.getInt(TypeBiere.fieldID));
				aResult.setLibelle(rs.getString(TypeBiere.fieldLibelle));
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return aResult;
	}

	/**
	 * ***********
	 * 
	 * 
	 * 
	 */
	@Override
	public ArrayList<TypeBiere> getAll() {
		ResultSet rs = null;
		ArrayList<TypeBiere> liste = new ArrayList<>();
		String strCmd = "SELECT " + TypeBiere.fieldID + ", " + TypeBiere.fieldLibelle + " from "
				+ TypeBiere.fieldEntityName + " order by " + TypeBiere.fieldLibelle;
		try (PreparedStatement stmt = connexion.prepareStatement(strCmd)) {
			rs = stmt.executeQuery();

			while (rs.next()) {
				liste.add(new TypeBiere(rs.getInt(TypeBiere.fieldID), rs.getString(TypeBiere.fieldLibelle)));
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return liste;
	}

	/**
	 * ***********
	 * 
	 * 
	 * 
	 */
	@Override
	public ArrayList<TypeBiere> select(TypeBiere obj) {
		
		Objects.requireNonNull(obj,sERRMESSAGEDAO_PARAM);
		
		ResultSet rs = null;
		String strCmd = "SELECT " + TypeBiere.fieldID + " ," + TypeBiere.fieldLibelle + " from "
				+ TypeBiere.fieldEntityName + " where ";
		ArrayList<TypeBiere> liste = new ArrayList<>();

		if (obj.getId() != null && obj.getId() > 0)
			strCmd = String.format("%s%s", strCmd, " " + TypeBiere.fieldID + " = ? ");
		else if (obj.getLibelle() != null && !obj.getLibelle().isEmpty())
			strCmd = String.format("%s%s", strCmd, " " + TypeBiere.fieldLibelle + " like ? ");
		else
			throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);
		System.out.println(" Query : " + strCmd);

		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd)) {

			if (obj.getId() != null && (obj.getId() > 0))
				pStmt.setInt(1, obj.getId());
			else
				pStmt.setString(1, "%" + obj.getLibelle() + "%");

			rs = pStmt.executeQuery();

			while (rs.next()) {
				liste.add(new TypeBiere(rs.getInt(TypeBiere.fieldID), rs.getString(TypeBiere.fieldLibelle)));
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

		return liste;
	}

	@Override
	public Integer insert(TypeBiere obj) {
		
		Objects.requireNonNull(obj, sERRMESSAGEDAO_PARAM);
		Objects.requireNonNull(obj.getLibelle(), sERRMESSAGEDAO_PARAM);
		
		String strCmd = "insert into " + TypeBiere.fieldEntityName + " (" + TypeBiere.fieldLibelle + ")   values ?";
		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd, Statement.RETURN_GENERATED_KEYS)) {

			setOrNull(pStmt,1, Objects.toString(obj.getLibelle(), "NULL Libelle"));
			int affectedRows = pStmt.executeUpdate();

			try (ResultSet generatedKeys = pStmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException(
							"Creating (" + this.getClass().getSimpleName() + ") failed, no ID obtained.");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return 0;
	}

	@Override
	public Integer update(TypeBiere obj) {
		
		Objects.requireNonNull(obj, sERRMESSAGEDAO_PARAM);
		Objects.requireNonNull(obj.getLibelle(),sERRMESSAGEDAO_PARAM);
		
		String strCmd = "update " + TypeBiere.fieldEntityName + " set " + TypeBiere.fieldLibelle + " = ? where "
				+ TypeBiere.fieldID + " = ?";
		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd, Statement.RETURN_GENERATED_KEYS)) {

			if (obj.getId() > 0) {
				pStmt.setInt(2, obj.getId());
			} else {
				throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);
			}

			setOrNull(pStmt,1, Objects.toString(obj.getLibelle(), "NULL Libelle"));
			pStmt.executeUpdate();

			return pStmt.getUpdateCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public Integer delete(TypeBiere obj) {
		
		Objects.requireNonNull(obj, sERRMESSAGEDAO_PARAM);
		
		String strCmd = "delete from " + TypeBiere.fieldEntityName + " where " + TypeBiere.fieldID + " = ?";
		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd, Statement.RETURN_GENERATED_KEYS)) {

			if (obj.getId() > 0) {
				pStmt.setInt(1, obj.getId());
			} else {
				throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);
			}
			pStmt.executeUpdate(strCmd);

			return pStmt.getUpdateCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
