package dao;

import java.sql.Connection;
import java.util.Objects;

import dao.ArticleDAO;
import dao.ContinentDAO;
import dao.CouleurDAO;
import dao.FabricantDAO;
import dao.MarqueDAO;
import dao.PaysDAO;
import dao.SDBMConnect;
import dao.TypeBiereDAO;

public class DaoFactory implements DAOProvider
{
	private static final Connection aConnexion = SDBMConnect.getConnexion();

	public static ContinentDAO getContinentDAO()
	{
		Objects.requireNonNull(aConnexion, sERRMESSAGEDAONULLCONNECT);
		return new ContinentDAO(aConnexion);
	}

	public static CouleurDAO getCouleurDAO()
	{
		Objects.requireNonNull(aConnexion, sERRMESSAGEDAONULLCONNECT);
		return new CouleurDAO(aConnexion);
	}

	public static PaysDAO getPaysDAO()
	{
		Objects.requireNonNull(aConnexion, sERRMESSAGEDAONULLCONNECT);
		return new PaysDAO(aConnexion);
	}

	public static TypeBiereDAO getTypeDAO()
	{
		Objects.requireNonNull(aConnexion, sERRMESSAGEDAONULLCONNECT);
		return new TypeBiereDAO(aConnexion);
	}

	public static MarqueDAO getMarqueDAO()
	{
		return new MarqueDAO(aConnexion);
	}

	public static FabricantDAO getFabricantDAO()
	{
		Objects.requireNonNull(aConnexion, sERRMESSAGEDAONULLCONNECT);
		return new FabricantDAO(aConnexion);
	}
	
	public static ArticleDAO getArticleDAO()
	{
		Objects.requireNonNull(aConnexion, sERRMESSAGEDAONULLCONNECT);
		return new ArticleDAO(aConnexion);
	}

}
