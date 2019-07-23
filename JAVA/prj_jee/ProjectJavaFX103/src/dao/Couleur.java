package dao;

public class Couleur {

	private Integer idCouleur;
	private String nom;
	public Couleur( ) {
		super(); 
		this.idCouleur = null;
		this.nom = null;
	}
	/**
	 * 
	 */
	public Couleur(Integer id, String nom ) {
		super(); 
		this.idCouleur = id;
		this.nom = nom;
	}

	public Integer getId() {
		 
		return this.idCouleur;
	}

	public String getLibelle() {
		return this.nom;
	}

}
