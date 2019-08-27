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
import metier.Pays;

public class PaysDAO extends DAO<Pays>{

	public PaysDAO(Connection connexion) {
		super(connexion);
	}

	/**
	 * ***********
	 * 
	 * 
	 * 
	 */
	@Override
	public Pays getByID(int id) {

		String strCmd = "SELECT "+Pays.fieldID+" ,"+Pays.fieldLibelle+" from "+Pays.fieldEntityName+"  where "+Pays.fieldID+" = ?";
		Pays aResult = new Pays();
		ResultSet rs = null;
		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd)) {
 

			if (id > 0) {
				pStmt.setInt(1, id);
			} else {
				throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);
			
			}


			rs = pStmt.executeQuery();
			if (rs.next()) {
				aResult.setId(rs.getInt(Pays.fieldID));
				aResult.setLibelle(rs.getString(Pays.fieldLibelle));
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
	public ArrayList<Pays> getAll() {
		ResultSet rs = null;
		ArrayList<Pays> liste = new ArrayList<>();
		String strCmd = "SELECT "+Pays.fieldID+", "+Pays.fieldLibelle+" from "+Pays.fieldEntityName+" order by "+Pays.fieldLibelle;
		try (PreparedStatement stmt = connexion.prepareStatement(strCmd)) {
			rs = stmt.executeQuery();

			while (rs.next()) {
				liste.add(new Pays(rs.getInt(Pays.fieldID), rs.getString(Pays.fieldLibelle)));
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
	public ArrayList<Pays> select(Pays obj) {
		ResultSet rs = null;
		String strCmd = "SELECT "+Pays.fieldID+" ,"+Pays.fieldLibelle+" from "+Pays.fieldEntityName+" where ";
		ArrayList<Pays> liste = new ArrayList<>();
		
		if (obj.getId() != null && obj.getId() > 0)
			strCmd = String.format("%s%s", strCmd, " "+Pays.fieldID+" = ? ");
		else if (obj.getLibelle() != null && !obj.getLibelle().isEmpty())
			strCmd = String.format("%s%s", strCmd, " "+Pays.fieldLibelle+" like ? ");
		else
			throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);
		System.out.println(" Query : " + strCmd);
	
		try (	PreparedStatement pStmt = connexion.prepareStatement(strCmd)) {

			if (obj.getId() != null && (obj.getId() > 0))
				pStmt.setInt(1, obj.getId());
			else
				pStmt.setString(1, "%" +  obj.getLibelle() + "%");

			rs = pStmt.executeQuery();

			while (rs.next()) {
				liste.add(new Pays(rs.getInt(Pays.fieldID), rs.getString(Pays.fieldLibelle)));
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
	public Integer insert(Pays obj) {
		
		Objects.requireNonNull(obj,sERRMESSAGEDAO_PARAM);
		Objects.requireNonNull(obj.getLibelle(),sERRMESSAGEDAO_PARAM);
		
		String strCmd = "insert into " + Pays.fieldEntityName + " (" + Pays.fieldLibelle + ")   values ?";
		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd, Statement.RETURN_GENERATED_KEYS)) {

			pStmt.setString(1, obj.getLibelle());
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
	public Integer update(Pays obj) {
		
		Objects.requireNonNull(obj,sERRMESSAGEDAO_PARAM);
		Objects.requireNonNull(obj.getLibelle(),sERRMESSAGEDAO_PARAM);
		
		String strCmd = "update " + Pays.fieldEntityName + " set " + Pays.fieldLibelle + " = ? where "
				+ Pays.fieldID + " = ?";
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
	public Integer delete(Pays obj) {
		Objects.requireNonNull(obj,sERRMESSAGEDAO_PARAM);
		String strCmd = "delete from "+Pays.fieldEntityName+" where "+Pays.fieldID+" = ?";
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
