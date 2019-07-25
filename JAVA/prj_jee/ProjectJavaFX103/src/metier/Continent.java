package metier;

import java.util.ArrayList;

import dao.objectInterface.DAOObjectChained;

public class Continent implements DAOObjectChained<Continent, Pays> {

	private Integer id_continent = null;
	private String nom_continent = null;
	
	private ArrayList<Pays> aListePays = new ArrayList<>();

	/**
	 * @param idContinent
	 * @param nomContinent
	 */
	public Continent(Integer idContinent, String nomContinent) {
		super();
		this.id_continent = idContinent;
		this.nom_continent = nomContinent;
		this.aListePays =  new ArrayList<>();
	}
	
	/**
	 * @param id_continent
	 * @param nom_continent
	 */
	public Continent() {
		super();
		this.id_continent = 0;
		this.nom_continent = null;
		this.aListePays =  new ArrayList<>();
	}


	@Override
	public Integer getId() {
		return id_continent;
	}


	@Override
	public void setId(Integer idKey) {
		id_continent = idKey;
	}


	@Override
	public String getLibelle() {
		return nom_continent;
	}


	@Override
	public Boolean setLibelle(String sLibelle) {
		return (nom_continent = sLibelle) != null;
	}


	@Override
	public ArrayList<Pays> getListe() {
		return aListePays;
	}


	@Override
	public Boolean setListe(ArrayList<Pays> aListeObject) {
		return (aListePays = aListeObject) != null;
	}
 
	
}
