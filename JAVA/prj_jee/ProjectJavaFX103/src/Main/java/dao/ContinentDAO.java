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
				aContinent.setId(rs.getInt(Continent.fieldID));
				aContinent.setLibelle(rs.getString(Continent.fieldLibelle));
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
		String strCmd = "select " + "conti." + Continent.fieldID + ", " + "conti." + Continent.fieldLibelle + ", " + " "
				+ Pays.fieldID + " ," + " " + Pays.fieldLibelle + " from  " + Continent.fieldEntityName + " conti  join "
				+ Pays.fieldEntityName + " pay on pay." + Pays.fieldID + " = conti." + Continent.fieldID + " "
				+ "order by " + Continent.fieldLibelle + ", " + Pays.fieldLibelle;
		System.out.println(this.getClass().getSimpleName() + " :: "+strCmd);
		try (PreparedStatement stmt = connexion.prepareStatement(strCmd)) {

			rs = stmt.executeQuery();
			if (rs.next()) {
				// ajoute un continent avant de pouvoir l'utiliser ...
				liste.add(new Continent(rs.getInt(Continent.fieldID), rs.getString(Continent.fieldLibelle)));
				Continent continentLu = liste.get(liste.size() - 1);
				// ajoute les pays et continent
				do {

					if (continentLu.getId() != rs.getInt(Continent.fieldID)) {

						liste.add(new Continent(rs.getInt(Continent.fieldID), rs.getString(Continent.fieldLibelle)));
						continentLu = liste.get(liste.size() - 1);
					}

					continentLu.getListe().add(new Pays(rs.getInt(Pays.fieldID), rs.getString(Pays.fieldID)));

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
		Objects.requireNonNull(obj,sERRMESSAGEDAO_PARAM);
		ResultSet rs = null;
		ArrayList<Continent> liste = new ArrayList<>();
		String strCmd = "select " + "conti." + Continent.fieldID + "," + "conti." + Continent.fieldLibelle + "," + " "
				+ Pays.fieldID + " ," + " " + Pays.fieldLibelle + " from  " + Continent.fieldEntityName + "join "
				+ Pays.fieldEntityName + " pay on pay." + Pays.fieldID + " = conti." + Continent.fieldID + " ";

		if (obj.getId() != null && obj.getId() > 0)
			strCmd = String.format("%s%s", strCmd, " " + Continent.fieldID + " = ? ");
		else if (obj.getLibelle() != null && !obj.getLibelle().isEmpty())
			strCmd = String.format("%s%s", strCmd, " " + Continent.fieldLibelle + " like ? ");
		else
			throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);
		strCmd = String.format("%s%s", strCmd, "order by " + Continent.fieldLibelle + ", " + Pays.fieldLibelle);
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
		
		Objects.requireNonNull(obj,sERRMESSAGEDAO_PARAM);
		Objects.requireNonNull(obj.getLibelle(),sERRMESSAGEDAO_PARAM);
		
		String strCmd = "insert into " + Continent.fieldEntityName + " (" + Continent.fieldLibelle + ")   values ?";
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
	public Integer update(Continent obj) {
		
		Objects.requireNonNull(obj,sERRMESSAGEDAO_PARAM);
		Objects.requireNonNull(obj.getLibelle(),sERRMESSAGEDAO_PARAM);
		
		Objects.toString(obj.getLibelle(), "NULL");
		String strCmd = "update " + Continent.fieldEntityName + " set " + Continent.fieldLibelle + " = ? where "
				+ Continent.fieldID + " = ?";
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
	public Integer delete(Continent obj) {
		Objects.requireNonNull(obj,sERRMESSAGEDAO_PARAM);
		String strCmd = "delete from "+Continent.fieldEntityName+" where "+Continent.fieldID+" = ?";
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
