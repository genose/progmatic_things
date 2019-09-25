package dao;

import dao.objectInterface.DAO;
import metier.Couleur;

import java.security.InvalidParameterException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;


public class CouleurDAO extends DAO<Couleur> {


	/**
	 * ***********
	 * 
	 * 
	 * 
	 */
	public CouleurDAO(Connection connexion) {
		super(connexion);
	}

	/**
	 * ***********
	 * 
	 * 
	 * 
	 */
	@Override
	public Couleur getByID(int id) {

		String strCmd = "SELECT "+Couleur.fieldID+" ,"+Couleur.fieldLibelle+" from "+Couleur.fieldEntityName+"  where "+Couleur.fieldID+" = ?";
		Couleur aResult = new Couleur();
		ResultSet rs = null;
		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd)) {
 

			if (id > 0) {
				pStmt.setInt(1, id);
			} else {
				throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);
			
			}


			rs = pStmt.executeQuery();
			if (rs.next()) {
				aResult.setId(rs.getInt(Couleur.fieldID));
				aResult.setLibelle(rs.getString(Couleur.fieldLibelle));
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
	public ArrayList<Couleur> getAll() {
		ResultSet rs = null;
		ArrayList<Couleur> liste = new ArrayList<>();
		String strCmd = "SELECT "+Couleur.fieldID+", "+Couleur.fieldLibelle+" from "+Couleur.fieldEntityName+" order by "+Couleur.fieldLibelle;
		try (PreparedStatement stmt = connexion.prepareStatement(strCmd)) {
			rs = stmt.executeQuery();

			while (rs.next()) {
				liste.add(new Couleur(rs.getInt(Couleur.fieldID), rs.getString(Couleur.fieldLibelle)));
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
	public ArrayList<Couleur> select(Couleur obj) {
	
		Objects.requireNonNull(obj, sERRMESSAGEDAO_PARAM);
		
		ResultSet rs = null;
		String strCmd = "SELECT "+Couleur.fieldID+" ,"+Couleur.fieldLibelle+" from "+Couleur.fieldEntityName+" where ";
		ArrayList<Couleur> liste = new ArrayList<>();
		
		if (obj.getId() != null && obj.getId() > 0)
			strCmd = String.format("%s%s", strCmd, " "+Couleur.fieldID+" = ? ");
		else if (obj.getLibelle() != null && !obj.getLibelle().isEmpty())
			strCmd = String.format("%s%s", strCmd, " "+Couleur.fieldLibelle+" like ? ");
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
				liste.add(new Couleur(rs.getInt(Couleur.fieldID), rs.getString(Couleur.fieldLibelle)));
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
	public Integer insert(Couleur obj) {
		
		Objects.requireNonNull(obj,sERRMESSAGEDAO_PARAM);
		Objects.requireNonNull(obj.getLibelle(),sERRMESSAGEDAO_PARAM);
		
		String strCmd = "insert into " + Couleur.fieldEntityName + " (" + Couleur.fieldLibelle + ")   values ?";
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
	public Integer update(Couleur obj) {
		
		Objects.requireNonNull(obj,sERRMESSAGEDAO_PARAM);
		Objects.requireNonNull(obj.getLibelle(),sERRMESSAGEDAO_PARAM);
		
		String strCmd = "update " + Couleur.fieldEntityName + " set " + Couleur.fieldLibelle + " = ? where "
				+ Couleur.fieldID + " = ?";
		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd, Statement.RETURN_GENERATED_KEYS)) {

			if (obj.getId() > 0) {
				pStmt.setInt(1, obj.getId());
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
	public Integer delete(Couleur obj) {
		
		Objects.requireNonNull(obj,sERRMESSAGEDAO_PARAM);
		
		String strCmd = "delete from "+Couleur.fieldEntityName+" where "+Couleur.fieldID+" = ?";
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

	public static void main(String[] args) {
		System.out.println(" Test Couleur ...");
		SDBMConnect aBDDConnexion = new SDBMConnect();

		Objects.requireNonNull(aBDDConnexion,
				"Connection au server " + aBDDConnexion.getHostname());
		System.out.println(" Result : " + aBDDConnexion);

		CouleurDAO aCouleurDAOTest = new CouleurDAO(SDBMConnect.getInstance().getServerConnexion());

		Couleur aCouleur = new Couleur(0, "lon");
		ArrayList<Couleur> aCouleurList = aCouleurDAOTest.select(aCouleur);
		if (aCouleurList.isEmpty()) {
			System.out.println("Aucune couleur trouve pour (" + aCouleur + ")");
		}else {
			for (Couleur aCouleur1 : aCouleurList) {
				System.out.println(" Couleur : " + aCouleur1.getLibelle());
			}
		}
		System.out.println("Clear ..CouleurDAO.");
	}
}
