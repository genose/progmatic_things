package metier;

import dao.objectInterface.DAOObjectChained;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Objects;

public class Continent implements DAOObjectChained<Continent, Pays> {

	public static final String fieldEntityName = "Continent";
	
	public static final String fieldID = "id_continent";
	public static final String fieldLibelle = "nom_continent";
	
	private IntegerProperty id_continent = null;
	private StringProperty nom_continent = null;
	
	private ArrayList<Pays> aListePays = new ArrayList<>();

	/**
	 * @param idContinent
	 * @param nomContinent
	 */
	public Continent(IntegerProperty idContinent, StringProperty nomContinent) {
		super();
		Objects.requireNonNull(idContinent, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(nomContinent, sERRMESSAGEDAOOBJECT_PARAM);
		this.id_continent = new SimpleIntegerProperty();
		this.nom_continent = new SimpleStringProperty();
		
		this.id_continent.set(idContinent.get());
		this.nom_continent.set(nomContinent.get());
		
		this.aListePays =  new ArrayList<>();
	}
	/**
	 * @param idContinent
	 * @param nomContinent
	 */
	public Continent(Integer idContinent, String nomContinent) {
		super();
		this.id_continent = new SimpleIntegerProperty();
		this.nom_continent = new SimpleStringProperty();
		
		this.id_continent.set(idContinent);
		this.nom_continent.set(nomContinent);
		
		this.aListePays =  new ArrayList<>();
	}

	/**
	 *
	 */
	public Continent() {
		super();
		this.id_continent = new SimpleIntegerProperty();
		this.nom_continent = new SimpleStringProperty();
		
		this.id_continent.set(0);
		this.aListePays =  new ArrayList<>();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public Integer getId() {
		return id_continent.get();
	}

	/**
	 *
	 * @param idKey
	 */
	@Override
	public void setId(Integer idKey) {
		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		id_continent.set(idKey);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getLibelle() {
		return nom_continent.get();
	}

	/**
	 *
	 * @param sLibelle
	 * @return
	 */

	@Override
	public Boolean setLibelle(String sLibelle) {
		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		nom_continent.set(sLibelle);
		return (nom_continent.get() != null);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public ArrayList<Pays> getListe() {
		return aListePays;
	}

	/**
	 *
	 * @param aListeObject
	 * @return
	 */
	@Override
	public Boolean setListe(ArrayList<Pays> aListeObject) {
		return (aListePays = aListeObject) != null;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public IntegerProperty getPropertyId() {
		return id_continent;
	}

	/**
	 *
	 * @param idKey
	 */
	@Override
	public void setPropertyId(IntegerProperty idKey) {
		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		id_continent = idKey;
		
	}

	/**
	 *
	 * @return
	 */
	@Override
	public StringProperty getPropertyLibelle() {
		
		return nom_continent;
	}

	/**
	 *
	 * @param sLibelle
	 * @return
	 */
	@Override
	public Boolean setPropertyLibelle(StringProperty sLibelle) {
		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		nom_continent = sLibelle;
		return (nom_continent != null);
	}
 
	
}
