package dao;

import java.security.InvalidParameterException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;

import dao.objectInterface.DAO;
import dao.objectInterface.DAOObject;
import metier.Article;
import metier.Continent;
import metier.Couleur;
import metier.Fabricant;
import metier.Marque;
import metier.Pays;
import metier.TypeBiere;
import metier.VolumeBiere;
import service.ArticleSearch;

public class ArticleDAO extends DAO<Article> {

	public ArticleDAO(Connection connexion) {
		super(connexion);
	}

	@Override
	public ArrayList<Article> getAll() {
		ResultSet rs = null;
		ArrayList<Article> liste = new ArrayList<>();
		String strCmd = "select " + Article.fieldID + ", " + Article.fieldLibelle + ", " + Article.fieldPrix + ", "
				+ Article.fieldTitrage + ", " + TypeBiere.fieldID + ", " + TypeBiere.fieldLibelle + ", " +
				// *****************************************
				Couleur.fieldID + ", " + Couleur.fieldLibelle + ", " +
				// *****************************************
				Article.fieldVolume + ", " +
				// *****************************************
				Pays.fieldID + ", " + Pays.fieldLibelle + ", " + Continent.fieldID + ", " + Continent.fieldLibelle
				+ ", " + Fabricant.fieldID + ", " + Fabricant.fieldLibelle + ", " +

				Marque.fieldID + ", " + Marque.fieldLibelle + " from VueArticle";
		System.out.println(this.getClass().getSimpleName() + " :: " + strCmd);
		try (PreparedStatement stmt = connexion.prepareStatement(strCmd)) {

			rs = stmt.executeQuery();
			Article aResult = null;

			while (rs.next()) {
				aResult = new Article();
				aResult.setId(rs.getInt(Article.fieldID));
				aResult.setLibelle(rs.getString(Article.fieldLibelle));
				aResult.setPrix(rs.getFloat(Article.fieldPrix));
				aResult.setTitrage(rs.getFloat(Article.fieldTitrage));
				aResult.setType(new TypeBiere(rs.getInt(TypeBiere.fieldID), rs.getString(TypeBiere.fieldLibelle)));
				// *****************************************
				aResult.setCouleur(new Couleur(rs.getInt(Couleur.fieldID), rs.getString(Couleur.fieldLibelle)));
				// *****************************************
				aResult.setVolume(rs.getFloat(Article.fieldVolume));
				// *****************************************
				Pays aPays = new Pays(rs.getInt(Pays.fieldID), rs.getString(Pays.fieldLibelle),
						new Continent(rs.getInt(Continent.fieldID), rs.getString(Continent.fieldLibelle)));
				Fabricant aFabricant = new Fabricant(rs.getInt(Fabricant.fieldID),
						rs.getString(Fabricant.fieldLibelle));

				aResult.setMarque(
						new Marque(rs.getInt(Marque.fieldID), rs.getString(Marque.fieldLibelle), aPays, aFabricant));
				// *****************************************

				liste.add(aResult);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return liste;
	}

	@Override
	public Article getByID(int id) {

		ResultSet rs = null;
		Article aResult = new Article();
		String strCmd = "select * from VueArticle where id_article = ?";
		try (PreparedStatement pStmt = connexion.prepareStatement(strCmd)) {

			if (id > 0) {
				pStmt.setInt(1, id);
			} else {
				throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);

			}

			rs = pStmt.executeQuery();

			if (rs.next()) {

				aResult.setId(rs.getInt(Article.fieldID));
				aResult.setLibelle(rs.getString(Article.fieldLibelle));
				aResult.setPrix(rs.getFloat(Article.fieldPrix));
				aResult.setTitrage(rs.getFloat(Article.fieldTitrage));
				aResult.setType(new TypeBiere(rs.getInt(TypeBiere.fieldID), rs.getString(TypeBiere.fieldLibelle)));
				// *****************************************
				aResult.setCouleur(new Couleur(rs.getInt(Couleur.fieldID), rs.getString(Couleur.fieldLibelle)));
				// *****************************************
				aResult.setVolume(rs.getFloat(Article.fieldVolume));
				// *****************************************
				Pays aPays = new Pays(rs.getInt(Pays.fieldID), rs.getString(Pays.fieldLibelle),
						new Continent(rs.getInt(Continent.fieldID), rs.getString(Continent.fieldLibelle)));
				Fabricant aFabricant = new Fabricant(rs.getInt(Fabricant.fieldID),
						rs.getString(Fabricant.fieldLibelle));

				aResult.setMarque(
						new Marque(rs.getInt(Marque.fieldID), rs.getString(Marque.fieldLibelle), aPays, aFabricant));
				// *****************************************

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return aResult;
	}

	@Override
	public ArrayList<Article> select(Article obj) {
		Objects.requireNonNull(obj, sERRMESSAGEDAO_PARAM);
		ResultSet rs = null;
		ArrayList<Article> liste = new ArrayList<>();
		String strCmd = "{call sp_ArticleQBE(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) } ";
		try (CallableStatement cStmt = connexion.prepareCall(strCmd)) {

			// ******************************************
			if (obj.getId() > 0) {
				cStmt.setInt(1, obj.getId());
			} else {
				throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);
			}

			// ******************************************
			setOrNull(cStmt, 2, obj.getLibelle());
			// ******************************************
			setOrNull(cStmt, 3, obj.getPrix());
			// ******************************************
			setOrNull(cStmt, 4, obj.getVolume());
			// ******************************************
			setOrNull(cStmt, 5, obj.getTitrage());
			// ******************************************
			Objects.requireNonNull(obj.getMarque(), sERRMESSAGEDAO_PARAM);
			setOrNull(cStmt, 6, obj.getMarque().getId());
			// ******************************************
			Objects.requireNonNull(obj.getCouleur(), sERRMESSAGEDAO_PARAM);
			setOrNull(cStmt, 7, obj.getCouleur().getId());
			// ******************************************
			Objects.requireNonNull(obj.getType(), sERRMESSAGEDAO_PARAM);
			setOrNull(cStmt, 8, obj.getType().getId());
			// ******************************************

			rs = cStmt.executeQuery();
			if (rs.next()) {
				do {
					Article aResult = new Article();
					aResult.setId(rs.getInt(Article.fieldID));
					aResult.setLibelle(rs.getString(Article.fieldLibelle));
					aResult.setPrix(rs.getFloat(Article.fieldPrix));
					aResult.setTitrage(rs.getFloat(Article.fieldTitrage));
					aResult.setType(new TypeBiere(rs.getInt(TypeBiere.fieldID), rs.getString(TypeBiere.fieldLibelle)));
					// *****************************************
					aResult.setCouleur(new Couleur(rs.getInt(Couleur.fieldID), rs.getString(Couleur.fieldLibelle)));
					// *****************************************
					aResult.setVolume(rs.getFloat(Article.fieldVolume));
					// *****************************************
					Pays aPays = new Pays(rs.getInt(Pays.fieldID), rs.getString(Pays.fieldLibelle),
							new Continent(rs.getInt(Continent.fieldID), rs.getString(Continent.fieldLibelle)));

					Fabricant aFabricant = new Fabricant(rs.getInt(Fabricant.fieldID),
							rs.getString(Fabricant.fieldLibelle));

					aResult.setMarque(new Marque(rs.getInt(Marque.fieldID), rs.getString(Marque.fieldLibelle), aPays,
							aFabricant));
					// *****************************************

					liste.add(aResult);
				} while (rs.next());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return liste;
	}

	public ArticleSearch select(ArticleSearch obj) {
		Objects.requireNonNull(obj, sERRMESSAGEDAO_PARAM);
		ResultSet rs = null;

		String strCmd = "{call sp_ArticleQBE(?, ?, ?, ?, ?, ?, ?, ?,?,?,?) } ";
		try (CallableStatement cStmt = connexion.prepareCall(strCmd)) {
			HashMap<String, String> aMappingForJSON = new HashMap<>();
			setOrNull(cStmt, 1, obj.getId());
			// ******************************************
			setOrNull(cStmt, 2, obj.getLibelle());
			// ******************************************
			setOrNull(cStmt, 3, obj.getCriteriaPrixRange());
			// ******************************************
			aMappingForJSON.clear();
			aMappingForJSON.put(VolumeBiere.fieldID, VolumeBiere.accessorFieldID);
			aMappingForJSON.put(VolumeBiere.fieldLibelle, VolumeBiere.accessorFieldLibelle);
			
			setOrNull(cStmt, 4, obj.getCriteriaVolume(), aMappingForJSON);
			// ******************************************
			setOrNull(cStmt, 5, obj.getCriteriaTitrageRange());
			// ******************************************
			// Objects.requireNonNull(obj.getCouleur(), sERRMESSAGEDAO_PARAM);
			aMappingForJSON.clear();
			aMappingForJSON.put(Couleur.fieldID, Couleur.accessorFieldID);
			aMappingForJSON.put(Couleur.fieldLibelle, Couleur.accessorFieldLibelle);
			
			setOrNull(cStmt, 6, obj.getCriteriaCouleur(),aMappingForJSON);
			// ******************************************
			// Objects.requireNonNull(obj.getType(), sERRMESSAGEDAO_PARAM);
			aMappingForJSON.clear();
			aMappingForJSON.put(TypeBiere.fieldID, TypeBiere.accessorFieldID);
			aMappingForJSON.put(TypeBiere.fieldLibelle, TypeBiere.accessorFieldLibelle);
			
			setOrNull(cStmt, 7, obj.getCriteriaType(),aMappingForJSON);
			// ******************************************
			aMappingForJSON.clear();
			aMappingForJSON.put(Marque.fieldID, Marque.accessorFieldID);
			aMappingForJSON.put(Marque.fieldLibelle, Marque.accessorFieldLibelle);
			
			setOrNull(cStmt, 8, obj.getCriteriaMarque(), aMappingForJSON);
			// ******************************************
			aMappingForJSON.clear();
			aMappingForJSON.put(Fabricant.fieldID, Fabricant.accessorFieldID);
			aMappingForJSON.put(Fabricant.fieldLibelle, Fabricant.accessorFieldLibelle);
			
			setOrNull(cStmt, 9, obj.getCriteriaFabricant(), aMappingForJSON);
			// ******************************************
			aMappingForJSON.clear();
			aMappingForJSON.put(Pays.fieldID, Pays.accessorFieldID);
			aMappingForJSON.put(Pays.fieldLibelle, Pays.accessorFieldLibelle);
			
			setOrNull(cStmt, 10, obj.getCriteriaPays(), aMappingForJSON);
			// ******************************************	
		
			aMappingForJSON.clear();
			aMappingForJSON.put(Continent.fieldID, Continent.accessorFieldID);
			aMappingForJSON.put(Continent.fieldLibelle, Continent.accessorFieldLibelle);
			
			setOrNull(cStmt, 11, obj.getCriteriaContinent(), aMappingForJSON);
			// ******************************************

			rs = cStmt.executeQuery();
			obj.getResultArticle().clear();
			
			if (rs.next()) {
				do {
					Article aResult = new Article();
					aResult.setId(rs.getInt(Article.fieldID));
					aResult.setLibelle(rs.getString(Article.fieldLibelle));
					aResult.setPrix(rs.getFloat(Article.fieldPrix));
					aResult.setTitrage(rs.getFloat(Article.fieldTitrage));
					aResult.setType(new TypeBiere(rs.getInt(TypeBiere.fieldID), rs.getString(TypeBiere.fieldLibelle)));
					// *****************************************
					aResult.setCouleur(new Couleur(rs.getInt(Couleur.fieldID), rs.getString(Couleur.fieldLibelle)));
					// *****************************************
					aResult.setVolume(rs.getFloat(Article.fieldVolume));
					// *****************************************
					Continent aContinent = new Continent(rs.getInt(Continent.fieldID),
							rs.getString(Continent.fieldLibelle));
					Pays aPays = new Pays(rs.getInt(Pays.fieldID), rs.getString(Pays.fieldLibelle), aContinent);

					Fabricant aFabricant = new Fabricant(rs.getInt(Fabricant.fieldID),
							rs.getString(Fabricant.fieldLibelle));

					Marque aMarque = new Marque(rs.getInt(Marque.fieldID), rs.getString(Marque.fieldLibelle), aPays,
							aFabricant);

					aResult.setMarque(aMarque);
					// *****************************************

					obj.getResultArticle().add(aResult);
				} while (rs.next());
			}else {
				System.out.println(" no resultset ... ");
			}

		} catch (Exception e) {
			e.printStackTrace();
			JFXApplicationException.raiseToFront(this.getClass(), e, true);
		} finally {

			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

	@Override
	public Integer insert(Article obj) {
		Objects.requireNonNull(obj, sERRMESSAGEDAO_PARAM);
		ResultSet rs = null;
		String strCmd = "{call sp_createArticle(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) } ";
		try (CallableStatement cStmt = connexion.prepareCall(strCmd)) {

// ****************************************** 
			if (obj.getId() > 0) {
				cStmt.setInt(1, obj.getId());
			} else {
				throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);
			}

// ****************************************** 
			// ******************************************
			setOrNull(cStmt, 2, obj.getLibelle());
			// ******************************************
			setOrNull(cStmt, 3, obj.getPrix());
			// ******************************************
			setOrNull(cStmt, 4, obj.getVolume());
			// ******************************************
			setOrNull(cStmt, 5, obj.getTitrage());
			// ******************************************
			Objects.requireNonNull(obj.getMarque(), sERRMESSAGEDAO_PARAM);
			setOrNull(cStmt, 6, obj.getMarque().getId());
			// ******************************************
			Objects.requireNonNull(obj.getCouleur(), sERRMESSAGEDAO_PARAM);
			setOrNull(cStmt, 7, obj.getCouleur().getId());
			// ******************************************
			Objects.requireNonNull(obj.getType(), sERRMESSAGEDAO_PARAM);
			setOrNull(cStmt, 8, obj.getType().getId());
// ****************************************** 

			rs = cStmt.executeQuery();
			if (!rs.next()) {
				throw new SQLException("Creating (" + this.getClass().getSimpleName() + ") failed.");
			}
			try (ResultSet generatedKeys = cStmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException(
							"Creating (" + this.getClass().getSimpleName() + ") failed, no ID obtained.");
				}
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
		return 0;
	}

	@Override
	public Integer update(Article obj) {
		Objects.requireNonNull(obj, sERRMESSAGEDAO_PARAM);
		ResultSet rs = null;
		String strCmd = "{call sp_updateArticle(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) } ";
		try (CallableStatement cStmt = connexion.prepareCall(strCmd)) {

// ****************************************** 
			if (obj.getId() > 0) {
				cStmt.setInt(1, obj.getId());
			} else {
				throw new InvalidParameterException(sERRMESSAGEDAO_PARAM);
			}
			// ******************************************
			setOrNull(cStmt, 2, obj.getLibelle());
			// ******************************************
			setOrNull(cStmt, 3, obj.getPrix());
			// ******************************************
			setOrNull(cStmt, 4, obj.getVolume());
			// ******************************************
			setOrNull(cStmt, 5, obj.getTitrage());
			// ******************************************
			Objects.requireNonNull(obj.getMarque(), sERRMESSAGEDAO_PARAM);
			setOrNull(cStmt, 6, obj.getMarque().getId());
			// ******************************************
			Objects.requireNonNull(obj.getCouleur(), sERRMESSAGEDAO_PARAM);
			setOrNull(cStmt, 7, obj.getCouleur().getId());
			// ******************************************
			Objects.requireNonNull(obj.getType(), sERRMESSAGEDAO_PARAM);
			setOrNull(cStmt, 8, obj.getType().getId());
// ****************************************** 

			rs = cStmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
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
		return 0;
	}

	@Override
	public Integer delete(Article obj) {
		Objects.requireNonNull(obj, sERRMESSAGEDAO_PARAM);
		String strCmd = "delete from " + Article.fieldEntityName + " where " + Article.fieldID + " = ?";
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
