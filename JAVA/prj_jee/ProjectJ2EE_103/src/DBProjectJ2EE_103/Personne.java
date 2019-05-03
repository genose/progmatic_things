package DBProjectJ2EE_103;

import java.io.Serializable;

import java.lang.Integer;
import java.lang.String;
import javax.persistence.*;
import DBProjectJ2EE_103.*;

/**
 * Entity implementation class for Entity: Personne
 *
 */
@IdClass(PersonnePK.class)
@Entity 
@NamedQueries({
	@NamedQuery(
		name="findByNomPrenom",
		query="Select p FROM Personne p WHERE p.nom = :nom "
				+ "and p.prenom = :prenom"
),
@NamedQuery(
		name="findByNom",
		query="Select p FROM Personne p WHERE p.nom = :nom "
)
})
@Table(name="personne")
public class Personne implements Serializable {

	   
	
	@GeneratedValue (strategy = GenerationType.AUTO )
	private Integer id;
	@Id
	private String nom;
	@Id
	private String prenom;
	private Integer age;
	private static final long serialVersionUID = 1L;

	public Personne() {
		super();
		this.id =null;
		this.age = 22;
		this.nom = "Bourne";
		this.prenom = "Jason";
	}
	public Personne(String name, String firstname, int age) {
		super();
		this.id = null;
		this.age = age;
		this.nom = name;
		this.prenom = firstname;
	}
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}   
	public String getNom() {
		return this.nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}   
	public String getPrenom() {
		return this.prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}   
	public Integer getAge() {
		return this.age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
	@Override
	public String toString() {
		return "Personne [id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", age=" + age + "]";
	}
	
	public void main() {
		
	}
	
   
}
