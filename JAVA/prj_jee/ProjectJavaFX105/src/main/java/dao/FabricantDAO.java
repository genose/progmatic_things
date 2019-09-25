package dao;

import dao.objectInterface.DAO;
import metier.Fabricant;

import java.security.InvalidParameterException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;


public class FabricantDAO extends DAO<Fabricant> {

	public FabricantDAO(Connection connexion) {
		super(connexion);
	}

	/**
	 * ***********
	 * 
	 * 
	 * 
	 */
	@Override
	public Fabricant getByID(int id) {

		String strCmd = "SELECT "+Fabricant.fieldID+" ,"+Fabricant.fieldLibelle+" from "+Fabricant.fieldEntityName+"  where "+Fabricant.fieldID+" = ?";
		Fabricant aResult = new Fabricant();
		ResultSet rs = null;
		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd)) {
 

			if (id > 0) {
				pStmt.setInt(1, id);
			} else {
				throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);
			
			}

			rs = pStmt.executeQuery();
			if (rs.next()) {
				aResult.setId(rs.getInt(Fabricant.fieldID));
				aResult.setLibelle(rs.getString(Fabricant.fieldLibelle));
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
	public ArrayList<Fabricant> getAll() {
		
		ResultSet rs = null;
		ArrayList<Fabricant> liste = new ArrayList<>();
		String strCmd = "SELECT "+Fabricant.fieldID+", "+Fabricant.fieldLibelle+" from "+Fabricant.fieldEntityName+" order by "+Fabricant.fieldLibelle;
		try (PreparedStatement stmt = connexion.prepareStatement(strCmd)) {
			rs = stmt.executeQuery();

			while (rs.next()) {
				liste.add(new Fabricant(rs.getInt(Fabricant.fieldID), rs.getString(Fabricant.fieldLibelle)));
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
	public ArrayList<Fabricant> select(Fabricant obj) {
		Objects.requireNonNull(obj,sERRMESSAGEDAO_PARAM);
		ResultSet rs = null;
		String strCmd = "SELECT "+Fabricant.fieldID+" ,"+Fabricant.fieldLibelle+" from "+Fabricant.fieldEntityName+" where ";
		ArrayList<Fabricant> liste = new ArrayList<>();
		
		if (obj.getId() != null && obj.getId() > 0)
			strCmd = String.format("%s%s", strCmd, " "+Fabricant.fieldID+" = ? ");
		else if (obj.getLibelle() != null && !obj.getLibelle().isEmpty())
			strCmd = String.format("%s%s", strCmd, " "+Fabricant.fieldLibelle+" like ? ");
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
				liste.add(new Fabricant(rs.getInt(Fabricant.fieldID), rs.getString(Fabricant.fieldLibelle)));
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
	public Integer insert(Fabricant obj) {

		Objects.requireNonNull(obj,sERRMESSAGEDAO_PARAM);
		Objects.requireNonNull(obj.getLibelle(),sERRMESSAGEDAO_PARAM);
		
		String strCmd = "insert into " + Fabricant.fieldEntityName + " (" + Fabricant.fieldLibelle + ")   values ?";
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
	public Integer update(Fabricant obj) {
		
		Objects.requireNonNull(obj,sERRMESSAGEDAO_PARAM);
		Objects.requireNonNull(obj.getLibelle(),sERRMESSAGEDAO_PARAM);
		
		String strCmd = "update " + Fabricant.fieldEntityName + " set " + Fabricant.fieldLibelle + " = ? where "
				+ Fabricant.fieldID + " = ?";
		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd, Statement.RETURN_GENERATED_KEYS)) {

			if (obj.getId() > 0) {
				pStmt.setInt(2, obj.getId());
			} else
				throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);

			setOrNull(pStmt,1, Objects.toString(obj.getLibelle(), "NULL Libelle"));
			pStmt.executeUpdate();

			return pStmt.getUpdateCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public Integer delete(Fabricant obj) {
		Objects.requireNonNull(obj,sERRMESSAGEDAO_PARAM);
		String strCmd = "delete from "+Fabricant.fieldEntityName+" where "+Fabricant.fieldID+" = ?";
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
