package dao;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import dao.objectInterface.DAO;
import metier.Continent;
import metier.Pays;

public class ContinentDAO extends DAO<Continent> {

	public ContinentDAO(Connection connexion) {
		super(connexion);
	}

	@Override
	public Continent getByID(int id) {

		String strCmd = "SELECT id_continent ,nom_continent from continent where id_continent = ?";
		Continent aContinent = new Continent();
		ResultSet rs = null;
		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd)) {
 
			pStmt.setInt(1, id);

			rs = pStmt.executeQuery();
			if (rs.next()) {
				aContinent.setId(rs.getInt(1));
				aContinent.setLibelle(rs.getString(2));
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
		return aContinent;
	}

	@Override
	public ArrayList<Continent> getAll() {
		ResultSet rs = null;
		ArrayList<Continent> liste = new ArrayList<>();
		String strCmd = "select continent.id_continent, nom_continent, id_pays,nom_pays from CONTINENT "
				+ "join PAYS on PAYS.ID_CONTINENT = CONTINENT.ID_CONTINENT " + "order by NOM_CONTINENT, NOM_PAYS";
		try (PreparedStatement stmt = connexion.prepareStatement(strCmd)) {

			rs = stmt.executeQuery(strCmd);
			if (rs.next()) {
				// ajoute un continent avant de pouvoir l'utiliser ...
				liste.add(new Continent(rs.getInt(1), rs.getString(2)));
				Continent continentLu = liste.get(liste.size() - 1);
				// ajoute les pays et continent
				do {

					if (continentLu.getId() != rs.getInt(1)) {

						liste.add(new Continent(rs.getInt(1), rs.getString(2)));
						continentLu = liste.get(liste.size() - 1);
					}

					continentLu.getListe().add(new Pays(rs.getInt(3), rs.getString(4)));

				} while (rs.next());
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
	public ArrayList<Continent> select(Continent obj) {
		ResultSet rs = null;
		ArrayList<Continent> liste = new ArrayList<>();
		String strCmd = "select continent.id_continent, nom_continent, id_pays,nom_pays from CONTINENT "
				+ "join PAYS on PAYS.ID_CONTINENT = CONTINENT.ID_CONTINENT " + "order by NOM_CONTINENT, NOM_PAYS";

		if (obj.getId() != null && obj.getId() > 0)
			strCmd = String.format("%s%s", strCmd, " id_continent = ? ");
		else if (obj.getLibelle() != null && !obj.getLibelle().isEmpty())
			strCmd = String.format("%s%s", strCmd, " nom_continent like ? ");
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
				liste.add(new Continent(rs.getInt(1), rs.getString(2)));
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
	public Integer insert(Continent obj) {
		String strCmd = "insert into Continent values ?";
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
	public Integer update(Continent obj) {
		String strCmd = "update CONTINENT set nom_continent = ? where id_continent = ?";
		try (PreparedStatement stmt = connexion.prepareStatement(strCmd, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, obj.getLibelle());
			stmt.setInt(2, obj.getId());

			stmt.executeUpdate();

			return stmt.getUpdateCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public Integer delete(Continent obj) {

		String strCmd = "delete from CONTINENT where id_continent = ?";
		try (PreparedStatement stmt = connexion.prepareStatement(strCmd, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setInt(1, obj.getId());

			stmt.executeUpdate(strCmd);

			return stmt.getUpdateCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
