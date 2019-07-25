package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import dao.objectInterface.DAO;
import metier.Article;
import metier.Continent;
import metier.Couleur;
import metier.Fabricant;
import metier.Marque;
import metier.Pays;
import metier.TypeBiere;

public class ArticleDAO extends DAO<Article> {

	public ArticleDAO(Connection connexion) {
		super(connexion);
	}

	@Override
	public ArrayList<Article> getAll()
	{
		ResultSet rs = null;
		ArrayList<Article> liste = new ArrayList<Article>();
		try(Statement stmt = connexion.createStatement())
		{
 
			String strCmd = "select * from VueArticle";
			rs = stmt.executeQuery(strCmd);
			Article article;

			while (rs.next())
			{
				article = new Article(rs.getInt(1), rs.getString(2), rs.getFloat(5), rs.getFloat(3));
				article.setMarque(
						new Marque(rs.getInt(11), rs.getString(7),
							new Fabricant(rs.getInt(16), rs.getString(17)),
							new Pays(rs.getInt(12), rs.getString(8), new Continent(rs.getInt(13), rs.getString(9))
								)
						)
				);
				article.setType(new TypeBiere(rs.getInt(14),rs.getString(10))); 
				article.setCouleur(new Couleur(rs.getInt(15),rs.getString(6)));
				article.setVolume(rs.getInt(4));

				liste.add(article);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		} finally {
		
				try {
					if(rs != null)
						rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		}		
		return liste;
	}

	@Override
	public Article getByID(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Article> select(Article obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer insert(Article obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer update(Article obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer delete(Article obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
