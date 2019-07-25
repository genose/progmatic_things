package dao;

import java.sql.Connection;

import dao.ArticleDAO;
import dao.ContinentDAO;
import dao.CouleurDAO;
import dao.FabricantDAO;
import dao.MarqueDAO;
import dao.PaysDAO;
import dao.SDBMConnect;
import dao.TypeBiereDAO;

public class DaoFactory
{
	private static final Connection connexion = SDBMConnect.getInstance();

	public static ContinentDAO getContinentDAO()
	{
		return new ContinentDAO(connexion);
	}

	public static CouleurDAO getCouleurDAO()
	{
		return new CouleurDAO(connexion);
	}

	public static PaysDAO getPaysDAO()
	{
		return new PaysDAO(connexion);
	}

	public static TypeBiereDAO getTypeDAO()
	{
		return new TypeBiereDAO(connexion);
	}

	public static MarqueDAO getMarqueDAO()
	{
		return new MarqueDAO(connexion);
	}

	public static FabricantDAO getFabricantDAO()
	{
		return new FabricantDAO(connexion);
	}
	
	public static ArticleDAO getArticleDAO()
	{
		return new ArticleDAO(connexion);
	}

}
