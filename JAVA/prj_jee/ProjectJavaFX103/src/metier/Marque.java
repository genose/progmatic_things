package metier;

import java.util.ArrayList;
import java.util.Objects;

import dao.objectInterface.DAOObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Marque implements DAOObject {

	public static final String fieldEntityName = "marque";

	public static final String fieldID = "id_marque";
	public static final String fieldLibelle = "nom_marque";

	private IntegerProperty id_Marque = null;
	private StringProperty nom_marque = null;
	private ObjectProperty<Fabricant> id_fabriquant = null;
	private ObjectProperty<Pays> id_pays = null;
	
	/**
	 * @param id_Marque
	 * @param id_pays
	 * @param nom_marque
	 */
	public Marque(IntegerProperty idMarque, StringProperty nomMarque) {
		super();
		Objects.requireNonNull(idMarque, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(nomMarque, sERRMESSAGEDAOOBJECT_PARAM);
		this.id_Marque= new SimpleIntegerProperty();
		this.nom_marque = new SimpleStringProperty();
		
		this.id_fabriquant = new SimpleObjectProperty<>();
		this.id_pays = new SimpleObjectProperty<>();
		
		this.id_Marque.set(idMarque.get());
		this.nom_marque.set(nomMarque.get());
		
	}
	
	/**
	 * @param id_Marque
	 * @param id_pays
	 * @param nom_marque
	 */
	public Marque(IntegerProperty idMarque, StringProperty nomMarque, Pays idPays) {
		super();
		Objects.requireNonNull(idMarque, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(nomMarque, sERRMESSAGEDAOOBJECT_PARAM);
		
		this.id_Marque= new SimpleIntegerProperty();
		this.nom_marque = new SimpleStringProperty();
		
		this.id_fabriquant = new SimpleObjectProperty<>();
		this.id_pays = new SimpleObjectProperty<>();
		
		this.id_Marque.set(idMarque.get());
		this.nom_marque.set(nomMarque.get());
		this.id_pays.set(idPays);
		
	}
	
	/**
	 * 
	 * @param id_Marque
	 * @param nom_marque
	 * @param id_fabricant
	 * @param id_pays
	 */
	public Marque(IntegerProperty idMarque, StringProperty nomMarque, Pays idPays, Fabricant idFabricant) {
		super();
		Objects.requireNonNull(idMarque, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(nomMarque, sERRMESSAGEDAOOBJECT_PARAM);
		
		this.id_Marque= new SimpleIntegerProperty();
		this.nom_marque = new SimpleStringProperty();
		
		this.id_fabriquant = new SimpleObjectProperty<>();
		this.id_pays = new SimpleObjectProperty<>();
		
		this.id_Marque.set(idMarque.get());
		this.nom_marque.set(nomMarque.get());
		this.id_pays.set(idPays);
		this.id_fabriquant.set(idFabricant);
		
	}

	/**
	 * @param id_Marque
	 * @param id_pays
	 * @param nom_marque
	 */
	public Marque(Integer idMarque, String nomMarque) {
		super();
		
		this.id_Marque= new SimpleIntegerProperty();
		this.nom_marque = new SimpleStringProperty();
		
		this.id_fabriquant = new SimpleObjectProperty<>();
		this.id_pays = new SimpleObjectProperty<>();
		
		this.id_Marque.set(idMarque);
		this.nom_marque.set(nomMarque);
		
	}

	/**
	 * @param id_Marque
	 * @param id_pays
	 * @param nom_marque
	 */
	public Marque(Integer idMarque, String nomMarque, Pays idPays) {
		super();
		
		this.id_Marque= new SimpleIntegerProperty();
		this.nom_marque = new SimpleStringProperty();
		
		this.id_fabriquant = new SimpleObjectProperty<>();
		this.id_pays = new SimpleObjectProperty<>();
		
		this.id_Marque.set(idMarque);
		this.nom_marque.set(nomMarque);
		this.id_pays.set(idPays);
		
	}

	/**
	 * 
	 * @param id_Marque
	 * @param nom_marque
	 * @param id_fabricant
	 * @param id_pays
	 */
	public Marque(Integer idMarque, String nomMarque, Pays idPays, Fabricant idFabricant) {
		super();
		
		this.id_Marque= new SimpleIntegerProperty();
		this.nom_marque = new SimpleStringProperty();
		
		this.id_fabriquant = new SimpleObjectProperty<>();
		this.id_pays = new SimpleObjectProperty<>();
		
		this.id_Marque.set(idMarque);
		this.nom_marque.set(nomMarque);
		this.id_pays.set(idPays);
		this.id_fabriquant.set(idFabricant);

	}

	public Marque() {
		this.id_Marque= new SimpleIntegerProperty();
		this.nom_marque = new SimpleStringProperty();
		
		this.id_fabriquant = new SimpleObjectProperty<>();
		this.id_pays = new SimpleObjectProperty<>();
		
		this.id_Marque.set(0);
	}

	@Override
	public Integer getId() {
		return id_Marque.get();
	}

	@Override
	public void setId(Integer idKey) {
		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		id_Marque.set(idKey);
	}

	@Override
	public final String getLibelle() {
		return nom_marque.get();
	}

	@Override
	public Boolean setLibelle(String sLibelle) {
		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		nom_marque.set(sLibelle);
		return (nom_marque.get() != null);
	}

	@Override
	public IntegerProperty getPropertyId() {
		return id_Marque;
	}

	@Override
	public void setPropertyId(IntegerProperty idKey) {
		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(idKey.get(), sERRMESSAGEDAOOBJECT_PARAM);
		id_Marque  = idKey;
		
	}

	@Override
	public StringProperty getPropertyLibelle() {
		return nom_marque;
	}

	@Override
	public Boolean setPropertyLibelle(StringProperty sLibelle) {
		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(sLibelle.get(), sERRMESSAGEDAOOBJECT_PARAM);
		nom_marque = sLibelle;
		return (getLibelle() != null);
	}

}
