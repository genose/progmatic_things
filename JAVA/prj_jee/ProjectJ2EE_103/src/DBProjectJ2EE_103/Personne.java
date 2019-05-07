package DBProjectJ2EE_103;

import java.io.Serializable;

import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.sun.jndi.cosnaming.IiopUrl.Address;

import DBProjectJ2EE_103.*;

/**
 * Entity implementation class for Entity: Personne
 *
 */

@Entity
@IdClass(PersonnePK.class)
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

	   
	 
	@GeneratedValue (strategy = GenerationType.IDENTITY )
	private Integer id;
	@Id
	private String nom;
	@Id
	private String prenom;
	private Integer age;
	private static final long serialVersionUID = 1L;
	@OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE}, fetch=FetchType.EAGER)
	// JoinColumn( name="idAdresse", referencedColumnName="idAdresse", nullable=false )
	private List<Adresse> adresses = new ArrayList<Adresse>();
	
	public Personne() {
		super();
		this.id = 0;
		this.age = 22;
		this.nom = "Bourne";
		this.prenom = "Jason";
	}
	public Personne(String name, String firstname, int age) {
		super();
		this.id = 0;
		this.age = age;
		this.nom = name;
		this.prenom = firstname;
	}
	@Override
	public String toString() {
		return String.format(
				"Personne [getId()=%s,\n getNom()=%s,\n getPrenom()=%s,\n getAge()=%s,\n hashCode()=%s,\n adresses(%s)]",
				getId(), getNom(), getPrenom(), getAge(), hashCode(), ((adresses != null)? adresses: new Adresse()) .toString());
	}

	public Integer getId() {
		return this.id ;
	}

	public void setId(Integer id) {
		this.id = id;
	}   
	public String getNom() {
		return String.valueOf(this.nom);
	}

	public void setNom(String nom) {
		this.nom = nom;
	}   
	public String getPrenom() {
		return String.valueOf(this.prenom);
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}   
	public Integer getAge() {
		return Integer.valueOf(this.age) | 0;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
	/**
	 * @return the adresses
	 */
	public List<Adresse> getAdresses() {
		return adresses;
	}
 
	public void removeAdresse(Adresse adresses) {
		if(this.adresses == null) {
			return;
		}else {
			this.adresses.remove(adresses);
		}
	}
	public boolean addAdresse(Adresse e) {
		return adresses.add(e);
	}
	public boolean contains(Object o) {
		return adresses.contains(o);
	}
	public boolean remove(Object o) {
		return adresses.remove(o);
	}
	
	 
 
	
   
}
