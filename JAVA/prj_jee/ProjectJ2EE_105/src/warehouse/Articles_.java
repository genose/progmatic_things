package warehouse;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-05-20T16:17:45.374+0200")
@StaticMetamodel(Articles.class)
public class Articles_ {
	public static volatile SingularAttribute<Articles, Integer> idArticle;
	public static volatile SingularAttribute<Articles, String> description;
	public static volatile SingularAttribute<Articles, Double> prix;
	public static volatile SingularAttribute<Articles, Commandes> articlesCommande;
	public static volatile CollectionAttribute<Articles, WarehousesArticlePositions> articlesPositions;
}
