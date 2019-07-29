package metier;

import java.util.Objects;

import dao.objectInterface.DAOObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TypeBiere implements DAOObject {

	public static final String fieldEntityName = "Type";

	public static final String fieldID = "id_type";
	public static final String fieldLibelle = "nom_type";

	private IntegerProperty id_type = null;
	private StringProperty nom_type = null;
	
	/**
	 * @param id_type
	 * @param nom_type
	 */
	public TypeBiere(IntegerProperty idType, StringProperty nomType) {
		super();
		Objects.requireNonNull(idType, sERRMESSAGEDAOOBJECT_PARAM);
		Objects.requireNonNull(nomType, sERRMESSAGEDAOOBJECT_PARAM);
		this.id_type = new SimpleIntegerProperty();
		this.nom_type = new SimpleStringProperty();
		
		this.id_type.set(idType.get());
		this.nom_type.set(nomType.get());
	}

	/**
	 * @param id_type
	 * @param nom_type
	 */
	public TypeBiere(Integer idType, String nomType) {
		super();
		
		this.id_type = new SimpleIntegerProperty();
		this.nom_type = new SimpleStringProperty();
		
		this.id_type.set(idType);
		this.nom_type.set(nomType);
	}

	public TypeBiere() {

		this.id_type = new SimpleIntegerProperty();
		this.nom_type = new SimpleStringProperty();
		
		this.id_type.set(0);
	}

	@Override
	public Integer getId() {
		return id_type.get();
	}

	@Override
	public void setId(Integer idKey) {
		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		id_type.set(idKey);
	}

	@Override
	public final String getLibelle() {
		return nom_type.get();
	}

	@Override
	public Boolean setLibelle(String sLibelle) {
		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		nom_type.set(sLibelle);
		return (nom_type != null);
	}

	@Override
	public IntegerProperty getPropertyId() {
		return id_type ;
	}

	@Override
	public void setPropertyId(IntegerProperty idKey) {
		Objects.requireNonNull(idKey, sERRMESSAGEDAOOBJECT_PARAM);
		id_type = idKey;
	}

	@Override
	public StringProperty getPropertyLibelle() {

		return nom_type;
	}

	@Override
	public Boolean setPropertyLibelle(StringProperty sLibelle) {
		Objects.requireNonNull(sLibelle, sERRMESSAGEDAOOBJECT_PARAM);
		nom_type = sLibelle;
		return nom_type != null;
	}

}
