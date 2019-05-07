package DBProjectJ2EE_103;

import DBProjectJ2EE_103.*;
import java.io.Serializable;

import java.lang.Double;
import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Article
 *
 */

@Entity
@IdClass(ArticlePK.class)
@Table(name="Article")
public class Article implements Serializable {

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY )
	private Integer idArticle;
	private String description;
	private Double prix;
	private Integer qtyStock;
	
	
	private static final long serialVersionUID = 1L;

	public Article() {
		super();
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

	public void setPrix(Double prix) {
		this.prix = prix;
	}   
	public Integer getQtyStock() {
		return this.qtyStock;
	}

	public void setQtyStock(Integer qtyStock) {
		this.qtyStock = qtyStock;
	}
   
}
