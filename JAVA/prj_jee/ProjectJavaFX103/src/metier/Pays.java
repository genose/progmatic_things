package metier;

import dao.objectInterface.DAOObject;
import metier.Continent;

public class Pays implements DAOObject {

	
	private Integer id_pays = null;
	private String nom_pays = null;
	private Continent id_continent = null;
	
	/**
	 * @param idPays
	 * @param nomPays
	 * @param aContinent
	 */
	public Pays(Integer idPays, String nomPays, Continent aContinent) {
		super();
		id_pays = idPays;
		nom_pays = nomPays;
		id_continent = aContinent;
	}
	/**
	 * @param idPays
	 * @param nomPays
	 */
	public Pays(Integer idPays, String nomPays) {
		super();
		id_pays = idPays;
		nom_pays = nomPays;
	}
	@Override
	public Integer getId() {
		return id_pays;
	}
	@Override
	public void setId(Integer idKey) {
		id_pays = idKey;
	}
	@Override
	public String getLibelle() {
		return nom_pays;
	}
	@Override
	public Boolean setLibelle(String sLibelle) {
		return (nom_pays) != null;
	}
	
	
}
