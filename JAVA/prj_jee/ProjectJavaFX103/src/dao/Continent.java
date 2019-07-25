package dao;

import java.util.ArrayList;

public class Continent implements DAOObject {

	private Integer idContinent = null;
	private String nomContinent = null;
	
	private ArrayList<Pays> aListePays = new ArrayList<>();

 
	/**
	 * @param idContinent
	 * @param nomContinent
	 * @param aListePays
	 */
	public Continent(Integer idContinent, String nomContinent, ArrayList<Pays> aListePays) {
		super();
		this.idContinent = idContinent;
		this.nomContinent = nomContinent;
		this.aListePays = aListePays;
	}


	/**
	 * @param idContinent
	 * @param nomContinent
	 */
	public Continent(Integer idContinent, String nomContinent) {
		super();
		this.idContinent = idContinent;
		this.nomContinent = nomContinent;
		this.aListePays =  new ArrayList<>();
	}
	
	/**
	 * @param idContinent
	 * @param nomContinent
	 */
	public Continent() {
		super();
		this.idContinent = 0;
		this.nomContinent = null;
		this.aListePays =  new ArrayList<>();
	}




	/**
	 * @return the idContinent
	 */
 
	public int getId() {
		return idContinent;
	}

	/**
	 * @param idContinent the idContinent to set
	 */
	
	public void setId(int idContinent) {
		this.idContinent = idContinent;
	}

	/**
	 * @return the nomContinent
	 */
	
	public String getLibelle() {
		return nomContinent;
	}


 
	public Boolean setLibelle(String sLibelle) {
		return this.nomContinent == null;
	}
 
	public ArrayList<Pays> getListe() {
		return this.aListePays;
	}

 
	public Boolean setListe(ArrayList<Pays> aListeObject) {
		return this.aListePays == aListeObject;
	}
 
	
}
