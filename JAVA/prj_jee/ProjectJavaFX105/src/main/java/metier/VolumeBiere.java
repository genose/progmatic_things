/**
 * 
 */
package metier;

import dao.objectInterface.DAOObject;
import javafx.beans.property.*;

/**
 * @author 59013-36-18
 *
 */
public class VolumeBiere implements DAOObject {

	public static final String fieldEntityName = "volumes";
	public static final String fieldID = "id_volume";
	public static final String fieldLibelle = "nom_volume";

	private IntegerProperty id_volume = null;
	private StringProperty nom_volume = null;
	private FloatProperty volume = null;

	/**
	 * 
	 */
	public VolumeBiere() {
		id_volume = new SimpleIntegerProperty();
		nom_volume = new SimpleStringProperty();
		volume = new SimpleFloatProperty();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public Integer getId() {
		return id_volume.get();
	}

	/**
	 *
	 * @param idKey
	 */
	@Override
	public void setId(Integer idKey) {
		id_volume.set(idKey);

	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getLibelle() {
		return nom_volume.get();
	}

	/**
	 *
	 * @param sLibelle
	 * @return
	 */
	@Override
	public Boolean setLibelle(String sLibelle) {
		nom_volume.set(sLibelle);
		return nom_volume.get() != null;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public IntegerProperty getPropertyId() {
		return id_volume;
	}

	/**
	 *
	 * @param idKey
	 */
	@Override
	public void setPropertyId(IntegerProperty idKey) {
		idKey.set(idKey.get());

	}

	/**
	 *
	 * @return
	 */
	@Override
	public StringProperty getPropertyLibelle() {
		return nom_volume;
	}

	/**
	 *
	 * @param sLibelle
	 * @return
	 */
	@Override
	public Boolean setPropertyLibelle(StringProperty sLibelle) {
		nom_volume.set(sLibelle.get());
		return nom_volume.get() != null;
	}

}
