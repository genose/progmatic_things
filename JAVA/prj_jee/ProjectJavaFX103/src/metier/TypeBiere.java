package metier;

import dao.objectInterface.DAOObject;

public class TypeBiere implements DAOObject {

	private Integer id_type = null;
	private String nom_type = null;
	/**
	 * @param id_type
	 * @param nom_type
	 */
	public TypeBiere(Integer id_type, String nom_type) {
		super();
		this.id_type = id_type;
		this.nom_type = nom_type;
	}
	@Override
	public Integer getId() {
		return id_type;
	}
	@Override
	public void setId(Integer idKey) {
		id_type  = idKey;
	}
	@Override
	public String getLibelle() {
		return nom_type;
	}
	@Override
	public Boolean setLibelle(String sLibelle) {
		return (nom_type = sLibelle) != null;
	}

	
}
