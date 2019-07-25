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
import metier.Couleur;

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

		String strCmd = "SELECT id_couleur ,nom_couleur from couleur  where id_couleur = ?";
		Couleur aCouleur = new Couleur();
		ResultSet rs = null;
		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd)) {
 
			pStmt.setInt(1, id);

			rs = pStmt.executeQuery();
			if (rs.next()) {
				aCouleur.setId(rs.getInt(1));
				aCouleur.setLibelle(rs.getString(2));
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
		return aCouleur;
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
		ArrayList<Couleur> liste = new ArrayList<Couleur>();
		String strCmd = "SELECT id_couleur ,nom_couleur from couleur order by nom_couleur";
		try (PreparedStatement stmt = connexion.prepareStatement(strCmd)) {
			rs = stmt.executeQuery(strCmd);

			while (rs.next()) {
				liste.add(new Couleur(rs.getInt(1), rs.getString(2)));
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
		ResultSet rs = null;
		String strCmd = "SELECT id_couleur ,nom_couleur from couleur where ";
		ArrayList<Couleur> liste = new ArrayList<>();
		
		if (obj.getId() != null && obj.getId() > 0)
			strCmd = String.format("%s%s", strCmd, " id_couleur = ? ");
		else if (obj.getLibelle() != null && !obj.getLibelle().isEmpty())
			strCmd = String.format("%s%s", strCmd, " nom_couleur like ? ");
		else
			throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);
		System.out.println(" Query : " + strCmd);
	
		try (	PreparedStatement pStmt = connexion.prepareStatement(strCmd)) {

			if (obj.getId() != null && (obj.getId() > 0))
				pStmt.setInt(1, ((Couleur) obj).getId());
			else
				pStmt.setString(1, "%" + ((Couleur) obj).getLibelle() + "%");

			rs = pStmt.executeQuery();

			while (rs.next()) {
				liste.add(new Couleur(rs.getInt(1), rs.getString(2)));
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
	public Integer insert(Couleur obj) {

		String strCmd = "insert into Couleur (nom_couleur) values ?";
		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd, Statement.RETURN_GENERATED_KEYS)) {
			
			pStmt.setString(1, obj.getLibelle());
			int affectedRows = pStmt.executeUpdate();
 
			try (ResultSet generatedKeys = pStmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	               return generatedKeys.getInt(1);
	            }
	            else {
	                throw new SQLException("Creating ("+this.getClass().getSimpleName()+") failed, no ID obtained.");
	            }
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return 0;
	}

	/**
	 * ***********
	 * 
	 * 
	 * 
	 */
	@Override
	public Integer update(Couleur obj) {
 
		String strCmd = "update Couleur set nom_couleur = ? where id_couleur = ? ";
		try ( PreparedStatement pStmt = connexion.prepareStatement(strCmd) ){

			pStmt.setString(1, obj.getLibelle());
			pStmt.setInt(2, obj.getId());

			pStmt.executeUpdate();

			return pStmt.getUpdateCount();

		} catch (Exception e) {
			e.printStackTrace();

		}  
		return 0;
	}

	/**
	 * ***********
	 * 
	 * 
	 * 
	 */
	@Override
	public Integer delete(Couleur obj) {

		String strCmd = "delete from Couleur where id_couleur = ? ";
		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd, Statement.RETURN_GENERATED_KEYS)) {
			pStmt.setInt(1, obj.getId());

			pStmt.executeUpdate();

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
				"Connection au server " + String.valueOf(SDBMConnect.getsConnectionHost()));
		System.out.println(" Result : " + aBDDConnexion);

		CouleurDAO aCouleurDAOTest = new CouleurDAO(aBDDConnexion.getInstance());

		Couleur aCouleur = new Couleur(0, "lon");
		ArrayList<Couleur> aCouleurList = aCouleurDAOTest.select(aCouleur);
		if (aCouleurList.isEmpty())
			System.out.println("Aucune couleur trouve pour (" + aCouleur + ")");
		else {
			for (Couleur aCouleur1 : aCouleurList) {
				System.out.println(" Couleur : " + aCouleur1.getLibelle());
			}
		}
		System.out.println("Clear ..CouleurDAO.");
	}
}
