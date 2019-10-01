/**
 * 
 */
package metier;

import dao.objectInterface.DAOObjectChained;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Objects;

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
	 *
	 * @param idFabriquant
	 * @param nomFabriquant
	 */
	public Fabricant(Integer idFabriquant, String nomFabriquant) {
		super();
		this.id_fabriquant = new SimpleIntegerProperty();
		this.nom_fabriquant = new SimpleStringProperty();

		this.id_fabriquant.set(idFabriquant);
		this.nom_fabriquant.set(nomFabriquant);
	}

	/**
	 *
	 * @param idFabriquant
	 * @param nomFabriquant
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

	/**
	 *
	 * @return
	 */
	@Override
	public Integer getId() {
		return id_fabriquant.get();
	}

	/**
	 *
	 * @param idKey
	 */
	@Override
	public void setId(Integer idKey) {

		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);

		id_fabriquant.set(idKey);

	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getLibelle() {
		return nom_fabriquant.get();
	}

	/**
	 *
	 * @param sLibelle
	 * @return
	 */
	@Override
	public Boolean setLibelle(String sLibelle) {

		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		nom_fabriquant.set(sLibelle);
		return (nom_fabriquant != null);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public ArrayList<Marque> getListe() {
		return aListeMarque;
	}

	/**
	 *
	 * @param aListeObject
	 * @return
	 */
	@Override
	public Boolean setListe(ArrayList<Marque> aListeObject) {

		Objects.requireNonNull(aListeObject, sERRMESSAGEDAOOBJECT_PARAM);
		aListeMarque = aListeObject;
		return (aListeMarque != null);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public IntegerProperty getPropertyId() {
		return id_fabriquant;
	}

	/**
	 *
	 * @param idKey
	 */
	@Override
	public void setPropertyId(IntegerProperty idKey) {

		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		id_fabriquant = idKey;

	}

	/**
	 *
	 * @return
	 */
	@Override
	public StringProperty getPropertyLibelle() {

		return nom_fabriquant;
	}

	/**
	 *
	 * @param sLibelle
	 * @return
	 */
	@Override
	public Boolean setPropertyLibelle(StringProperty sLibelle) {

		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		nom_fabriquant = sLibelle;
		return nom_fabriquant != null;
	}

}
