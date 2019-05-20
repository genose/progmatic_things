package users_accounts;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-05-20T16:17:45.374+0200")
@StaticMetamodel(Adresses.class)
public class Adresses_ {
	public static volatile SingularAttribute<Adresses, Integer> idAdresse;
	public static volatile SingularAttribute<Adresses, String> name;
	public static volatile SingularAttribute<Adresses, String> additionnalName;
	public static volatile SingularAttribute<Adresses, String> postcode;
	public static volatile SingularAttribute<Adresses, Integer> cityName;
	public static volatile SingularAttribute<Adresses, String> email;
	public static volatile SingularAttribute<Adresses, Integer> adresseType;
	public static volatile SingularAttribute<Adresses, Account> accountInfo;
	public static volatile SingularAttribute<Adresses, Personnes> identityInfo;
}
