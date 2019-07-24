package dao;

public class Marque {
	private Integer id_Marque = null;
	private String nom_marque = null;
	private Pays id_pays = null;
	/**
	 * @param id_Marque
	 * @param id_pays
	 * @param nom_marque
	 */
	public Marque(Integer id_Marque, String nom_marque, Pays id_pays) {
		super();
		this.id_Marque = id_Marque;
		this.id_pays = id_pays;
		this.nom_marque = nom_marque;
	}
	/**
	 * @return the id_Marque
	 */
	public Integer getId_Marque() {
		return id_Marque;
	}
	/**
	 * @param id_Marque the id_Marque to set
	 */
	public void setId_Marque(Integer id_Marque) {
		this.id_Marque = id_Marque;
	}
	/**
	 * @return the nom_marque
	 */
	public String getNom_marque() {
		return nom_marque;
	}
	/**
	 * @param nom_marque the nom_marque to set
	 */
	public void setNom_marque(String nom_marque) {
		this.nom_marque = nom_marque;
	}
	/**
	 * @return the id_pays
	 */
	public Pays getId_pays() {
		return id_pays;
	}
	/**
	 * @param id_pays the id_pays to set
	 */
	public void setId_pays(Pays id_pays) {
		this.id_pays = id_pays;
	}

}
