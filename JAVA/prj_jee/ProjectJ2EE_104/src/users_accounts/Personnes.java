package users_accounts;
import users_accounts.*;

import java.io.Serializable;

import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.*;

import com.sun.jndi.cosnaming.IiopUrl.Address;



/**
 * Entity implementation class for Entity: Personne
 *
 */

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
@Table(name="personnes")

public class Personnes implements Serializable {

	/* ***************************** */   
	@Id 
	@GeneratedValue (strategy = GenerationType.IDENTITY )
	private Integer id;
	/* ***************************** */
	 
	@GeneratedValue (strategy = GenerationType.AUTO )
	private String nom;
	/* ***************************** */
	 
	@GeneratedValue (strategy = GenerationType.AUTO )
	private String prenom;
	/* ***************************** */
	private Integer age;
	/* ***************************** */
	 
	@GeneratedValue (strategy = GenerationType.AUTO )
	private String email;
	
	/* ***************************** */
	/* ****   Foreign Keys  ***** */
	/* ***************************** */  
	@OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE}, fetch=FetchType.EAGER)
	// JoinColumn( name="idAdresse", referencedColumnName="idAdresse", nullable=false )
	private Collection<Adresses> adresses;
	/* ***************************** */
	@OneToOne(mappedBy = "userIdentity")
	private Users userIdentityInfo;
	/* ***************************** */
	/* ***************************** */
	private static final long serialVersionUID = 1L;
	/* ***************************** */
	public Personnes() {
		super();
		this.id = 0;
		this.age = 22;
		this.nom = "Bourne";
		this.prenom = "Jason";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adresses == null) ? 0 : adresses.hashCode());
		result = prime * result + ((age == null) ? 0 : age.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((prenom == null) ? 0 : prenom.hashCode());
		result = prime * result + ((userIdentityInfo == null) ? 0 : userIdentityInfo.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Personnes other = (Personnes) obj;
		if (adresses == null) {
			if (other.adresses != null)
				return false;
		} else if (!adresses.equals(other.adresses))
			return false;
		if (age == null) {
			if (other.age != null)
				return false;
		} else if (!age.equals(other.age))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (prenom == null) {
			if (other.prenom != null)
				return false;
		} else if (!prenom.equals(other.prenom))
			return false;
		if (userIdentityInfo == null) {
			if (other.userIdentityInfo != null)
				return false;
		} else if (!userIdentityInfo.equals(other.userIdentityInfo))
			return false;
		return true;
	}
	public Personnes(String name, String firstname, int age) {
		super();
		this.id = 0;
		this.age = age;
		this.nom = name;
		this.prenom = firstname;
	}
	public Personnes(String email) {
		super();
		this.id = 0;
		this.age = null;
		this.nom = null;
		this.prenom = null;
		
	}
	@Override
	public String toString() {
		return String.format("Personnes [id=%s, nom=%s, prenom=%s, age=%s, email=%s, adresses=%s, userIdentityInfo=%s]",
				id, nom, prenom, age, email, adresses, userIdentityInfo);
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
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @param adresses the adresses to set
	 */
	public void setAdresses(Collection<Adresses> adresses) {
		this.adresses = adresses;
	}
	/**
	 * @return the adresses
	 */
	public List<Adresses> getAdresses() {
		return adresses;
	}
 
	public void removeAdresse(Adresses adresses) {
		if(this.adresses == null) {
			return;
		}else {
			this.adresses.remove(adresses);
		}
	}
	public boolean addAdresse(Adresses e) {
		return adresses.add(e);
	}
	public boolean contains(Object o) {
		return adresses.contains(o);
	}
	public boolean remove(Object o) {
		return adresses.remove(o);
	}
	
	 
 
	
   
}
