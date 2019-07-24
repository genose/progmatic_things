package dao;

import java.util.ArrayList;

public class Continent {

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
		this.aListePays =  new ArrayList<Pays>();
	}
	
	/**
	 * @param idContinent
	 * @param nomContinent
	 */
	public Continent() {
		super();
		this.idContinent = 0;
		this.nomContinent = null;
		this.aListePays =  new ArrayList<Pays>();
	}




	/**
	 * @return the idContinent
	 */
	public int getIdContinent() {
		return idContinent;
	}

	/**
	 * @param idContinent the idContinent to set
	 */
	public void setIdContinent(int idContinent) {
		this.idContinent = idContinent;
	}

	/**
	 * @return the nomContinent
	 */
	public String getNomContinent() {
		return nomContinent;
	}

	/**
	 * @param nomContinent the nomContinent to set
	 */
	public void setNomContinent(String nomContinent) {
		this.nomContinent = nomContinent;
	}

	/**
	 * @return the aListePays
	 */
	public ArrayList<Pays> getListePays() {
		return aListePays;
	}

	/**
	 * @param aListePays the aListePays to set
	 */
	public void setListePays(ArrayList<Pays> aListePays) {
		this.aListePays = aListePays;
	}
	
	
	
}
