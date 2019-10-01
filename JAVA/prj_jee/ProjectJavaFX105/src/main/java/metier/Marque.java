package metier;

import dao.objectInterface.DAOObject;
import javafx.beans.property.*;

import java.util.Objects;

public class Marque implements DAOObject {

	public static final String fieldEntityName = "marque";

	public static final String fieldID = "id_marque";
	public static final String fieldLibelle = "nom_marque";

	private IntegerProperty id_Marque = null;
	private StringProperty nom_marque = null;
	private ObjectProperty<Fabricant> id_fabriquant = null;
	private ObjectProperty<Pays> id_pays = null;

	/**
	 *
	 * @param idMarque
	 * @param nomMarque
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
	 *
	 * @param idMarque
	 * @param nomMarque
	 * @param idPays
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
	 * @param idMarque
	 * @param nomMarque
	 * @param idPays
	 * @param idFabricant
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
	 *
	 * @param idMarque
	 * @param nomMarque
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
	 *
	 * @param idMarque
	 * @param nomMarque
	 * @param idPays
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
	 * @param idMarque
	 * @param nomMarque
	 * @param idPays
	 * @param idFabricant
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

	/**
	 *
	 */
	public Marque() {
		this.id_Marque= new SimpleIntegerProperty();
		this.nom_marque = new SimpleStringProperty();
		
		this.id_fabriquant = new SimpleObjectProperty<>();
		this.id_pays = new SimpleObjectProperty<>();
		
		this.id_Marque.set(0);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public Integer getId() {
		return id_Marque.get();
	}

	/**
	 *
	 * @param idKey
	 */
	@Override
	public void setId(Integer idKey) {
		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		id_Marque.set(idKey);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public final String getLibelle() {
		return nom_marque.get();
	}

	/**
	 *
	 * @param sLibelle
	 * @return
	 */
	@Override
	public Boolean setLibelle(String sLibelle) {
		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		nom_marque.set(sLibelle);
		return (nom_marque.get() != null);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public IntegerProperty getPropertyId() {
		return id_Marque;
	}

	/**
	 *
	 * @param idKey
	 */
	@Override
	public void setPropertyId(IntegerProperty idKey) {
		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(idKey.get(), sERRMESSAGEDAOOBJECT_PARAM);
		id_Marque  = idKey;
		
	}

	/**
	 *
	 * @return
	 */
	@Override
	public StringProperty getPropertyLibelle() {
		return nom_marque;
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
		nom_marque = sLibelle;
		return (getLibelle() != null);
	}

}
