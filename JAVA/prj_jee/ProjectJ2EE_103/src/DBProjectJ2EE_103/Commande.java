package DBProjectJ2EE_103;

import DBProjectJ2EE_103.*;
import java.io.Serializable;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Commande
 *
 */
@Entity
@IdClass(CommandePK.class)
@Table(name="Commande")
public class Commande implements Serializable {

	   
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY )
	private Integer idCommande;   
	@Id
	private Integer etatCommande;
	@ManyToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE}, fetch=FetchType.EAGER)
	private List<Article> listeArticle = new ArrayList<Article>();
	
	private static final long serialVersionUID = 1L;

	public Commande() {
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
   
}
