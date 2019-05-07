package DBProjectJ2EE_103;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;


public class ArticlePK implements Serializable{

	
	@Id
	private Integer idArticle;

}
