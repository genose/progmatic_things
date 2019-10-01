package metier;

import dao.objectInterface.DAOObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class Couleur implements DAOObject {

	public static final String fieldEntityName = "Couleur";
	
	public static final String fieldID = "id_couleur";
	public static final String fieldLibelle = "nom_couleur";


	
	private IntegerProperty id_couleur;
	private StringProperty nom_couleur;

	/**
	 *
	 */
	public Couleur( ) {
		super(); 
		this.id_couleur = new SimpleIntegerProperty();
		this.nom_couleur = new SimpleStringProperty();
		this.id_couleur.set(0);
	}

	/**
	 *
	 * @param id
	 * @param nom
	 */
	public Couleur(Integer id, String nom ) {
		super(); 
		this.id_couleur = new SimpleIntegerProperty();
		this.nom_couleur = new SimpleStringProperty();
		
		id_couleur.set( id);
		nom_couleur.set(nom);
	}

	/**
	 *
	 * @param idKey
	 * @param sLibelle
	 */
	public Couleur(IntegerProperty idKey, StringProperty sLibelle ) {
		super(); 
		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		this.id_couleur = new SimpleIntegerProperty();
		this.nom_couleur = new SimpleStringProperty();
		
		id_couleur.set( idKey.get());
		nom_couleur.set(sLibelle.get());
	}

	/**
	 *
	 * @return
	 */
	@Override
	public Integer getId() {
		return id_couleur.get();
	}

	/**
	 *
	 * @param idKey
	 */
	@Override
	public void setId(Integer idKey) {
		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		id_couleur.set(idKey);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public final String getLibelle() {
		return nom_couleur.get();
	}

	/**
	 *
	 * @param sLibelle
	 * @return
	 */
	@Override
	public Boolean setLibelle(String sLibelle) {
		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		nom_couleur.set(sLibelle);
		return ( nom_couleur.get() != null);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public IntegerProperty getPropertyId() {
		return id_couleur;
	}

	/**
	 *
	 * @param idKey
	 */
	@Override
	public void setPropertyId(IntegerProperty idKey) {

		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		id_couleur = idKey;
		
	}

	/**
	 *
	 * @return
	 */
	@Override
	public StringProperty getPropertyLibelle() {
		return nom_couleur;
	}

	/**
	 *
	 * @param sLibelle
	 * @return
	 */
	@Override
	public Boolean setPropertyLibelle(StringProperty sLibelle) {
		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(sLibelle.get(), sERRMESSAGEDAOOBJECT_PARAM);
		nom_couleur = sLibelle;
		return nom_couleur != null;
	}

}
