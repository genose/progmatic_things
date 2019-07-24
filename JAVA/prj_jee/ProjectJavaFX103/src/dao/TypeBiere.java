package dao;

public class TypeBiere {

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
	/**
	 * @return the id_type
	 */
	public Integer getId_type() {
		return id_type;
	}
	/**
	 * @param id_type the id_type to set
	 */
	public void setId_type(Integer id_type) {
		this.id_type = id_type;
	}
	/**
	 * @return the nom_type
	 */
	public String getNom_type() {
		return nom_type;
	}
	/**
	 * @param nom_type the nom_type to set
	 */
	public void setNom_type(String nom_type) {
		this.nom_type = nom_type;
	}
	
	
}
