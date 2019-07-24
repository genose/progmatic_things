package dao;

public class Pays {

	
	private Integer idPays = null;
	private String nomPays = null;
	private Continent aContinent = null;
	/**
	 * @param idPays
	 * @param nomPays
	 * @param aContinent
	 */
	public Pays(Integer idPays, String nomPays, Continent aContinent) {
		super();
		this.idPays = idPays;
		this.nomPays = nomPays;
		this.aContinent = aContinent;
	}
	/**
	 * @param idPays
	 * @param nomPays
	 */
	public Pays(Integer idPays, String nomPays) {
		super();
		this.idPays = idPays;
		this.nomPays = nomPays;
	}
	
	
}
