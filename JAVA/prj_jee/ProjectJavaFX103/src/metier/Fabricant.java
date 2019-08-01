/**
 * 
 */
package metier;

import java.util.ArrayList;
import java.util.Objects;

import dao.objectInterface.DAOObjectChained;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author 59013-36-18
 *
 */
public class Fabricant implements DAOObjectChained<Fabricant, Marque> {

	public static final String fieldEntityName = "Fabricant";

	public static final String fieldID = "id_Fabricant";
	public static final String fieldLibelle = "nom_Fabricant";


	private IntegerProperty id_fabriquant = null;
	private StringProperty nom_fabriquant = null;
	private ArrayList<Marque> aListeMarque = new ArrayList<>();

	/**
	 * 
	 */
	public Fabricant() {
		this.id_fabriquant = new SimpleIntegerProperty();
		this.nom_fabriquant = new SimpleStringProperty();
		this.id_fabriquant.set(0);
		aListeMarque = new ArrayList<>();
	}

	/**
	 * @param id_fabriquant
	 * @param nom_fabriquant
	 */
	public Fabricant(Integer idFabriquant, String nomFabriquant) {
		super();
		this.id_fabriquant = new SimpleIntegerProperty();
		this.nom_fabriquant = new SimpleStringProperty();

		this.id_fabriquant.set(idFabriquant);
		this.nom_fabriquant.set(nomFabriquant);
	}
	
	/**
	 * @param id_fabriquant
	 * @param nom_fabriquant
	 */
	public Fabricant(IntegerProperty idFabriquant, StringProperty nomFabriquant) {
		super();
		Objects.requireNonNull(idFabriquant, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(nomFabriquant, sERRMESSAGEDAOOBJECT_PARAM);
		this.id_fabriquant = new SimpleIntegerProperty();
		this.nom_fabriquant = new SimpleStringProperty();

		this.id_fabriquant.set(idFabriquant.get());
		this.nom_fabriquant.set(nomFabriquant.get());
	}

	@Override
	public Integer getId() {
		return id_fabriquant.get();
	}

	@Override
	public void setId(Integer idKey) {

		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);

		id_fabriquant.set(idKey);

	}

	@Override
	public String getLibelle() {
		return nom_fabriquant.get();
	}

	@Override
	public Boolean setLibelle(String sLibelle) {

		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		nom_fabriquant.set(sLibelle);
		return (nom_fabriquant != null);
	}

	@Override
	public ArrayList<Marque> getListe() {
		return aListeMarque;
	}

	@Override
	public Boolean setListe(ArrayList<Marque> aListeObject) {

		Objects.requireNonNull(aListeObject, sERRMESSAGEDAOOBJECT_PARAM);
		aListeMarque = aListeObject;
		return (aListeMarque != null);
	}

	@Override
	public IntegerProperty getPropertyId() {
		return id_fabriquant;
	}

	@Override
	public void setPropertyId(IntegerProperty idKey) {

		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		id_fabriquant = idKey;

	}

	@Override
	public StringProperty getPropertyLibelle() {

		return nom_fabriquant;
	}

	@Override
	public Boolean setPropertyLibelle(StringProperty sLibelle) {

		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		nom_fabriquant = sLibelle;
		return nom_fabriquant != null;
	}

}
