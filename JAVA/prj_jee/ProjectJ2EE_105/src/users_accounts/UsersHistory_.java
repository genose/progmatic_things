package users_accounts;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-05-21T14:38:36.138+0200")
@StaticMetamodel(UsersHistory.class)
public class UsersHistory_ {
	public static volatile SingularAttribute<UsersHistory, Integer> historyId;
	public static volatile SingularAttribute<UsersHistory, String> historyEvent;
	public static volatile SingularAttribute<UsersHistory, Date> historydate;
	public static volatile SingularAttribute<UsersHistory, Users> userHistoryInfo;
}
