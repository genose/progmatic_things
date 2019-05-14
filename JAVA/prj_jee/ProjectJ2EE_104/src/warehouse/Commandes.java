package warehouse;

import warehouse.*;

import java.io.Serializable;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.*;

import users_accounts.Account;
import users_accounts.Adresses;


/**
 * Entity implementation class for Entity: Commande
 *
 */
@Entity
@Table(name="COMMANDES")
public class Commandes implements Serializable {

	   
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY )
	private Integer idCommande;   
	@Column(name = "commandestatus") 
	private Integer etatCommande;
	
	private Adresses adresseLivraison;
	
	private Adresses adresseFacturation;
	
	private Adresses adresseWarehouseReturn; // origin storage
	
	
	
	/* ***************************** */
	/* ****   Foreign Keys  ***** */
	/* ***************************** */
	@OneToMany(mappedBy = "articlesCommande", cascade={CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.EAGER)
	private Collection<Articles> listeArticles;
	/* ***************************** */
	@ManyToOne
	private Account accountInfo;
		/* ***************************** */
	private static final long serialVersionUID = 1L;

	public Commandes() {
		super();
	}   
	public Integer getIdCommande() {
		return this.idCommande;
	}

	public void setIdCommande(Integer idCommande) {
		this.idCommande = idCommande;
	}   
	public Integer getEtatCommande() {
		return this.etatCommande;
	}

	public void setEtatCommande(Integer etatCommande) {
		this.etatCommande = etatCommande;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adresseFacturation == null) ? 0 : adresseFacturation.hashCode());
		result = prime * result + ((adresseLivraison == null) ? 0 : adresseLivraison.hashCode());
		result = prime * result + ((adresseWarehouseReturn == null) ? 0 : adresseWarehouseReturn.hashCode());
		result = prime * result + ((etatCommande == null) ? 0 : etatCommande.hashCode());
		result = prime * result + ((idCommande == null) ? 0 : idCommande.hashCode());
		result = prime * result + ((listeArticles == null) ? 0 : listeArticles.hashCode());
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
		Commandes other = (Commandes) obj;
		if (adresseFacturation == null) {
			if (other.adresseFacturation != null)
				return false;
		} else if (!adresseFacturation.equals(other.adresseFacturation))
			return false;
		if (adresseLivraison == null) {
			if (other.adresseLivraison != null)
				return false;
		} else if (!adresseLivraison.equals(other.adresseLivraison))
			return false;
		if (adresseWarehouseReturn == null) {
			if (other.adresseWarehouseReturn != null)
				return false;
		} else if (!adresseWarehouseReturn.equals(other.adresseWarehouseReturn))
			return false;
		if (etatCommande == null) {
			if (other.etatCommande != null)
				return false;
		} else if (!etatCommande.equals(other.etatCommande))
			return false;
		if (idCommande == null) {
			if (other.idCommande != null)
				return false;
		} else if (!idCommande.equals(other.idCommande))
			return false;
		if (listeArticles == null) {
			if (other.listeArticles != null)
				return false;
		} else if (!listeArticles.equals(other.listeArticles))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return String.format(
				"Commandes [idCommande=%s, etatCommande=%s, adresseLivraison=%s, adresseFacturation=%s, adresseWarehouseReturn=%s, listeArticles=%s]",
				idCommande, etatCommande, adresseLivraison, adresseFacturation, adresseWarehouseReturn, listeArticles);
	}
   
}
