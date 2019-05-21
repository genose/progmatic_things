package users_accounts;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-05-21T14:38:36.136+0200")
@StaticMetamodel(Personnes.class)
public class Personnes_ {
	public static volatile SingularAttribute<Personnes, Integer> id;
	public static volatile SingularAttribute<Personnes, String> nom;
	public static volatile SingularAttribute<Personnes, String> prenom;
	public static volatile SingularAttribute<Personnes, Integer> age;
	public static volatile SingularAttribute<Personnes, String> email;
	public static volatile CollectionAttribute<Personnes, Adresses> adresses;
	public static volatile SingularAttribute<Personnes, Users> userIdentityInfo;
}
