package metier;

import java.util.Objects;

import dao.objectInterface.DAOObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Couleur implements DAOObject {

	public static final String fieldEntityName = "Couleur";
	
	public static final String fieldID = "id_couleur";
	public static final String fieldLibelle = "nom_couleur";
	
	private IntegerProperty id_couleur;
	private StringProperty nom_couleur;
	
	public Couleur( ) {
		super(); 
		this.id_couleur = new SimpleIntegerProperty();
		this.nom_couleur = new SimpleStringProperty();
		this.id_couleur.set(0);
	}
	/**
	 * 
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
	@Override
	public Integer getId() {
		return id_couleur.get();
	}
	@Override
	public void setId(Integer idKey) {
		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		id_couleur.set(idKey);
	}
	@Override
	public final String getLibelle() {
		return nom_couleur.get();
	}
	@Override
	public Boolean setLibelle(String sLibelle) {
		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		nom_couleur.set(sLibelle);
		return ( nom_couleur.get() != null);
	}
	@Override
	public IntegerProperty getPropertyId() {
		return id_couleur;
	}
	@Override
	public void setPropertyId(IntegerProperty idKey) {

		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		id_couleur = idKey;
		
	}
	@Override
	public StringProperty getPropertyLibelle() {
		return nom_couleur;
	}
	@Override
	public Boolean setPropertyLibelle(StringProperty sLibelle) {
		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(sLibelle.get(), sERRMESSAGEDAOOBJECT_PARAM);
		nom_couleur = sLibelle;
		return nom_couleur != null;
	}
 
 

}
