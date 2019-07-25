package metier;

import dao.objectInterface.DAOObject;

public class Couleur implements DAOObject {

	private Integer id_couleur;
	private String nom_couleur;
	public Couleur( ) {
		super(); 
		this.id_couleur = 0;
		this.nom_couleur = null;
	}
	/**
	 * 
	 */
	public Couleur(Integer id, String nom ) {
		super(); 
		id_couleur = id;
		nom_couleur = nom;
	}
	@Override
	public Integer getId() {
		return id_couleur;
	}
	@Override
	public void setId(Integer idKey) {
		id_couleur = idKey;
	}
	@Override
	public String getLibelle() {
		return nom_couleur;
	}
	@Override
	public Boolean setLibelle(String sLibelle) {
		return (nom_couleur = sLibelle) == null;
	}
 
 

}
