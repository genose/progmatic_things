/**
 * 
 */
package DBProjectJ2EE_103;

import java.io.Serializable;

import javax.persistence.Id;
import DBProjectJ2EE_103.*;

/**
 * @author 59013-03-13
 *
 */
public class PersonnePK implements Serializable {

	/**
	 * 
	 */
	@Id
	private String nom;
	@Id
	private String prenom;
	
	/**
	 * @return the nom
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @param nom the nom to set
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 * @return the prenom
	 */
	public String getPrenom() {
		return prenom;
	}

	/**
	 * @param prenom the prenom to set
	 */
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public PersonnePK() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
