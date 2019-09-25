package dao;

import org.genose.java.implementation.dataaccessobject.GNSObjectDAOStrings;

import java.sql.Connection;
import java.util.Objects;

public class DaoFactory implements GNSObjectDAOStrings
{
	private static Connection aConnexion = null; // SDBMConnect.getInstance().getServerConnexion();

	static {
		try {
			aConnexion = SDBMConnect.getInstance().getServerConnexion();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static ContinentDAO getContinentDAO()
	{
		Objects.requireNonNull(aConnexion, S_ERRMESSAGE_DAO_NULLCONNECT);
		return new ContinentDAO(aConnexion);
	}

	public static CouleurDAO getCouleurDAO()
	{
		Objects.requireNonNull(aConnexion, S_ERRMESSAGE_DAO_NULLCONNECT);
		return new CouleurDAO(aConnexion);
	}

	public static PaysDAO getPaysDAO()
	{
		Objects.requireNonNull(aConnexion, S_ERRMESSAGE_DAO_NULLCONNECT);
		return new PaysDAO(aConnexion);
	}

	public static TypeBiereDAO getTypeDAO()
	{
		Objects.requireNonNull(aConnexion, S_ERRMESSAGE_DAO_NULLCONNECT);
		return new TypeBiereDAO(aConnexion);
	}

	public static MarqueDAO getMarqueDAO()
	{
		return new MarqueDAO(aConnexion);
	}

	public static FabricantDAO getFabricantDAO()
	{
		Objects.requireNonNull(aConnexion, S_ERRMESSAGE_DAO_NULLCONNECT);
		return new FabricantDAO(aConnexion);
	}
	
	public static ArticleDAO getArticleDAO()
	{
		Objects.requireNonNull(aConnexion, S_ERRMESSAGE_DAO_NULLCONNECT);
		return new ArticleDAO(aConnexion);
	}

}
