package metier;

import dao.objectInterface.DAOObject;
import javafx.beans.property.*;

import java.util.Objects;


public class Pays implements DAOObject {
	
	public static final String fieldEntityName = "Pays";

	public static final String fieldID = "id_pays";
	public static final String fieldLibelle = "nom_pays";
	
	private IntegerProperty id_pays = null;
	private StringProperty nom_pays = null;
	private ObjectProperty<Continent> id_continent = null;
	
	/**
	 * @param idPays
	 * @param nomPays
	 * @param aContinent
	 */
	public Pays(IntegerProperty idPays, StringProperty nomPays, Continent aContinent) {
		super();
		Objects.requireNonNull(idPays, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(nomPays, sERRMESSAGEDAOOBJECT_PARAM);
		this.id_pays= new SimpleIntegerProperty();
		this.nom_pays= new SimpleStringProperty();
		this.id_continent = new SimpleObjectProperty<>();
		
		id_pays.set(idPays.get());
		nom_pays.set(nomPays.get());
		id_continent.set(aContinent);
	}
	/**
	 * @param idPays
	 * @param nomPays
	 */
	public Pays(IntegerProperty idPays, StringProperty nomPays) {
		super();
		Objects.requireNonNull(idPays, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(nomPays, sERRMESSAGEDAOOBJECT_PARAM);
		this.id_pays= new SimpleIntegerProperty();
		this.nom_pays= new SimpleStringProperty();
		this.id_continent = new SimpleObjectProperty<>();
		
		id_pays.set(idPays.get());
		nom_pays.set(nomPays.get());
	}
	
	/**
	 * @param idPays
	 * @param nomPays
	 * @param aContinent
	 */
	public Pays(Integer idPays, String nomPays, Continent aContinent) {
		super();
		this.id_pays= new SimpleIntegerProperty();
		this.nom_pays= new SimpleStringProperty();
		this.id_continent = new SimpleObjectProperty<>();
		
		id_pays.set(idPays);
		nom_pays.set(nomPays);
		id_continent.set(aContinent);
	}
	/**
	 * @param idPays
	 * @param nomPays
	 */
	public Pays(Integer idPays, String nomPays) {
		super();
		this.id_pays= new SimpleIntegerProperty();
		this.nom_pays= new SimpleStringProperty();
		this.id_continent = new SimpleObjectProperty<>();
		
		id_pays.set(idPays);
		nom_pays.set(nomPays);
	}

	/**
	 *
	 */
	public Pays() {
		super();
		this.id_pays= new SimpleIntegerProperty();
		this.nom_pays= new SimpleStringProperty();
		this.id_continent = new SimpleObjectProperty<>();
		
		id_pays.set(0);
		
	}

	/**
	 *
	 * @return
	 */
	@Override
	public Integer getId() {
		return id_pays.get();
	}

	/**
	 *
	 * @param idKey
	 */
	@Override
	public void setId(Integer idKey) {
		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		id_pays.set(idKey);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public final String getLibelle() {
		return nom_pays.get();
	}
	@Override
	public Boolean setLibelle(String sLibelle) {

		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		nom_pays.set(sLibelle);
		return (nom_pays != null);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public IntegerProperty getPropertyId() {

		return id_pays;
	}

	/**
	 *
	 * @param idKey
	 */
	@Override
	public void setPropertyId(IntegerProperty idKey) {

		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		id_pays = idKey;
		
	}

	/**
	 *
	 * @return
	 */
	@Override
	public StringProperty getPropertyLibelle() {
		return nom_pays;
	}

	/**
	 *
	 * @param sLibelle
	 * @return
	 */
	@Override
	public Boolean setPropertyLibelle(StringProperty sLibelle) {

		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		nom_pays = sLibelle;
		return nom_pays != null;
	}
	
	
}
