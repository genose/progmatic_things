package warehouse;

import warehouse.*;


import java.io.Serializable;

import java.lang.Double;
import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Article
 *
 */

@Entity (name="ARTICLES")
public class Articles implements Serializable {

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY )
	private Integer idArticle;
	private String description;
	private Double prix;
	// private Integer qtyStock;
	
	
	/* ***************************** */
	/* ****   Foreign Keys  ***** */
	/* ***************************** */
	@ManyToOne
	private Commandes articlesCommande;
	/* ***************************** */
	@OneToMany(mappedBy = "articlesStocksPositions")
	private Collection<WarehousesArticlePositions> articlesPositions; // more than one storage position in warehouse is possible ... EG large QTY, Car Park in different place
	/* ***************************** */
	
	private static final long serialVersionUID = 1L;

	public Articles() {
		super();
	}

	@Override
	public String toString() {
		return String.format("Articles [idArticle=%s, description=%s, prix=%s, articlesCommande=%s]", idArticle,
				description, prix, articlesCommande);
	}

	public Integer getIdArticle() {
		return this.idArticle;
	}

	public void setIdArticle(Integer idArticle) {
		this.idArticle = idArticle;
	}   
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}   
	public Double getPrix() {
		return this.prix;
	}

 

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((articlesCommande == null) ? 0 : articlesCommande.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((idArticle == null) ? 0 : idArticle.hashCode());
		result = prime * result + ((prix == null) ? 0 : prix.hashCode());
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
		Articles other = (Articles) obj;
		if (articlesCommande == null) {
			if (other.articlesCommande != null)
				return false;
		} else if (!articlesCommande.equals(other.articlesCommande))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (idArticle == null) {
			if (other.idArticle != null)
				return false;
		} else if (!idArticle.equals(other.idArticle))
			return false;
		if (prix == null) {
			if (other.prix != null)
				return false;
		} else if (!prix.equals(other.prix))
			return false;
		return true;
	}

}
