package dao;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

public class CouleurDAO extends DAO<Couleur> {

	private ResultSet rs = null;
	private PreparedStatement pStmt = null;

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
	public ArrayList<Couleur> getAll() {
		ArrayList<Couleur> liste = new ArrayList<Couleur>();
		try {

			Statement stmt = connexion.createStatement();

			String strCmd = "SELECT id_couleur ,nom_couleur from couleur order by nom_couleur";
			rs = stmt.executeQuery(strCmd);

			while (rs.next()) {
				liste.add(new Couleur(rs.getInt(1), rs.getString(2)));
			}

			return liste;

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}

				if (pStmt != null) {
					pStmt.close();
					pStmt = null;
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return (new ArrayList<Couleur>());
	}

	/**
	 * ***********
	 * 
	 * 
	 * 
	 */
	@Override
	public Couleur getByID(int id) {

		try {

			String strCmd = "SELECT id_couleur ,nom_couleur from couleur order by nom_couleur where id_couleur = ?";

			PreparedStatement pStmt = connexion.prepareStatement(strCmd);

			pStmt.setInt(1, id);

			rs = pStmt.executeQuery();
			if (rs.next()) {
				return new Couleur(rs.getInt(1), rs.getString(2));
			} else {
				return (new Couleur());
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}

				if (pStmt != null) {
					pStmt.close();
					pStmt = null;
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return (new Couleur());
	}

	/**
	 * ***********
	 * 
	 * 
	 * 
	 */
	@Override
	public ArrayList<Couleur> select(Couleur obj) {
		try {

			ArrayList<Couleur> liste = new ArrayList<Couleur>();

			String strCmd = "SELECT id_couleur ,nom_couleur from couleur where ";

			if (obj.getId() != null && obj.getId() > 0)
				strCmd = String.format("%s%s", strCmd, " id_couleur = ? ");
			else if (obj.getLibelle() != null && !obj.getLibelle().isEmpty())
				strCmd = String.format("%s%s", strCmd, " nom_couleur like ? ");
			else
				throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);
			System.out.println(" Query : " + strCmd);

			strCmd = String.format("%s%s", strCmd, "order by nom_couleur ");
			System.out.println(" Query : " + strCmd);
			PreparedStatement pStmt = connexion.prepareStatement(strCmd);

			if (obj.getId() != null && (obj.getId() > 0))
				pStmt.setInt(1, ((Couleur) obj).getId());
			else 
				pStmt.setString(1, "%"+((Couleur) obj).getLibelle()+"%");

			rs = pStmt.executeQuery();

			while (rs.next()) {
				liste.add(new Couleur(rs.getInt(1), rs.getString(2)));
			}

			return liste;

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}

				if (pStmt != null) {
					pStmt.close();
					pStmt = null;
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

		return (new ArrayList<Couleur>());
	}

	/**
	 * ***********
	 * 
	 * 
	 * 
	 */
	@Override
	public Integer insert(Couleur obj) {

		try {
			String strCmd = "insert into Couleur (nom_couleur) values ?";

			PreparedStatement pStmt = connexion.prepareStatement(strCmd);

			pStmt.setString(1, ((Couleur) obj).getLibelle());
			rs = pStmt.executeQuery();

			return rs.getRow();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}

				if (pStmt != null) {
					pStmt.close();
					pStmt = null;
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}
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
	public boolean delete(Couleur obj) {

		try {

			String strCmd = "delete from Couleur where id_couleur = ? ";
			PreparedStatement pStmt = connexion.prepareStatement(strCmd);

			pStmt.setInt(1, ((Couleur) obj).getId());

			rs = pStmt.executeQuery();

			return pStmt.getUpdateCount() > 0;

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}

				if (pStmt != null) {
					pStmt.close();
					pStmt = null;
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return false;

	}

	/**
	 * ***********
	 * 
	 * 
	 * 
	 */
	@Override
	public Integer update(Couleur obj) {

		try {

			String strCmd = "update Couleur set nom_couleur = ? where id_couleur = ? ";
			PreparedStatement pStmt = connexion.prepareStatement(strCmd);

			pStmt.setString(1, ((Couleur) obj).getLibelle());
			pStmt.setInt(2, ((Couleur) obj).getId());

			pStmt.executeQuery();

			return pStmt.getUpdateCount();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}

				if (pStmt != null) {
					pStmt.close();
					pStmt = null;
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * 
	 * @param args
	 */
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
