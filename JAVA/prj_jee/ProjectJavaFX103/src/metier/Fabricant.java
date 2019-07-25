/**
 * 
 */
package metier;

import java.util.ArrayList;

import dao.objectInterface.DAOObjectChained;

/**
 * @author 59013-36-18
 *
 */
public class Fabricant implements DAOObjectChained<Fabricant, Marque> {

	private Integer id_fabriquant = null;
	private String nom_fabriquant = null;
	private ArrayList<Marque> aListeMarque = new ArrayList<>();
	
	/**
	 * 
	 */
	public Fabricant() {
		id_fabriquant = 0;
		nom_fabriquant = null;
		aListeMarque = new ArrayList<>();
	}

	/**
	 * @param id_fabriquant
	 * @param nom_fabriquant
	 */
	public Fabricant(Integer id_fabriquant, String nom_fabriquant) {
		super();
		this.id_fabriquant = id_fabriquant;
		this.nom_fabriquant = nom_fabriquant;
	}

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public void setId(Integer idKey) {
		id_fabriquant = idKey;
		
	}

	@Override
	public String getLibelle() {
		return nom_fabriquant;
	}

	@Override
	public Boolean setLibelle(String sLibelle) {
		return (nom_fabriquant = sLibelle) != null;
	}

	@Override
	public ArrayList<Marque> getListe() {
		return aListeMarque;
	}

	@Override
	public Boolean setListe(ArrayList<Marque> aListeObject) {
		return (aListeMarque = aListeObject) != null;
	}

}
