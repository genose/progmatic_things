package warehouse;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import users_accounts.Account;
import users_accounts.Adresses;

@Generated(value="Dali", date="2019-05-21T14:48:49.429+0200")
@StaticMetamodel(Commandes.class)
public class Commandes_ {
	public static volatile SingularAttribute<Commandes, Integer> idCommande;
	public static volatile SingularAttribute<Commandes, Integer> etatCommande;
	public static volatile SingularAttribute<Commandes, Adresses> adresseLivraison;
	public static volatile SingularAttribute<Commandes, Adresses> adresseFacturation;
	public static volatile SingularAttribute<Commandes, Adresses> adresseWarehouseReturn;
	public static volatile CollectionAttribute<Commandes, Articles> listeArticles;
	public static volatile SingularAttribute<Commandes, Account> accountInfo;
}
