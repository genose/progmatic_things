package metier;

import java.util.ArrayList;

import dao.objectInterface.DAOObject;

public class Marque implements DAOObject {
	private Integer id_Marque = null;
	private String nom_marque = null;
	private Fabricant id_fabriquant = null;
	private Pays id_pays = null;
	/**
	 * @param id_Marque
	 * @param id_pays
	 * @param nom_marque
	 */
	public Marque(Integer id_Marque, String nom_marque) {
		super();
		this.id_Marque = id_Marque;
		this.id_pays = null;
		this.nom_marque = nom_marque;
	}
	/**
	 * @param id_Marque
	 * @param id_pays
	 * @param nom_marque
	 */
	public Marque(Integer id_Marque, String nom_marque, Pays id_pays) {
		super();
		this.id_Marque = id_Marque;
		this.nom_marque = nom_marque;
		this.id_pays = id_pays;
	}
	/**
	 * 
	 * @param id_Marque
	 * @param nom_marque
	 * @param id_fabricant
	 * @param id_pays
	 */
	public Marque(Integer id_Marque, String nom_marque, Fabricant id_fabricant, Pays id_pays) {
		super();
		this.id_Marque = id_Marque;
		this.nom_marque = nom_marque;
		this.id_fabriquant = id_fabriquant;
		this.id_pays = id_pays;
	}
	@Override
	public Integer getId() {
		return id_Marque;
	}
	@Override
	public void setId(Integer idKey) {
		id_Marque = idKey;	
	}
	@Override
	public String getLibelle() {
		return nom_marque;
	}
	@Override
	public Boolean setLibelle(String sLibelle) {
		return (nom_marque = sLibelle)  != null;
	}

	
	
}
