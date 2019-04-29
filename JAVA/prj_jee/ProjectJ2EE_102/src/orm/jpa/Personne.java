/**
 * 
 */
package orm.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

// mapping name SQL = java class
@Entity @Table(name="personne")
/**
 * @author 59013-03-13
 *
 */
public class Personne {

	@Id
	private int idPersonne;
	private String prenom;
	private String nom;
	private int age;
	/**
	 * 
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public String toString() {
		return "Personne [idPersonne=" + idPersonne + ", prenom=" + prenom + ", nom=" + nom + ", age=" + age + "]";
	}

	/**
	 * @return the idPersonne
	 */
	public int getIdPersonne() {
		return idPersonne;
	}

	/**
	 * @param idPersonne the idPersonne to set
	 */
	public void setIdPersonne(int idPersonne) {
		this.idPersonne = idPersonne;
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
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	public Personne() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
