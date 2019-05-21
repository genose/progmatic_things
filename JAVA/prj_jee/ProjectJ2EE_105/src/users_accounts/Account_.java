package users_accounts;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import warehouse.Commandes;

@Generated(value="Dali", date="2019-05-21T14:47:17.071+0200")
@StaticMetamodel(Account.class)
public class Account_ {
	public static volatile SingularAttribute<Account, Integer> idAccount;
	public static volatile SingularAttribute<Account, Date> creationDate;
	public static volatile SingularAttribute<Account, Integer> accountType;
	public static volatile SingularAttribute<Account, Users> userInfo;
	public static volatile CollectionAttribute<Account, Adresses> accountAdresses;
	public static volatile CollectionAttribute<Account, Commandes> accountCommandes;
}
