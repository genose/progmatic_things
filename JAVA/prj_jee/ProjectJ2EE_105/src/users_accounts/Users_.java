package users_accounts;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-05-20T16:17:45.374+0200")
@StaticMetamodel(Users.class)
public class Users_ {
	public static volatile SingularAttribute<Users, Integer> userId;
	public static volatile SingularAttribute<Users, String> userName;
	public static volatile SingularAttribute<Users, String> userEmail;
	public static volatile SingularAttribute<Users, String> userLogin;
	public static volatile SingularAttribute<Users, String> userPassword;
	public static volatile SingularAttribute<Users, Account> accountInfo;
	public static volatile CollectionAttribute<Users, UsersHistory> connectionHistory;
	public static volatile SingularAttribute<Users, Personnes> userIdentity;
}
