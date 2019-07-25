package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ContinentDAO extends DAO {

	public ContinentDAO(Connection connexion) {
		super(connexion);
	}

	public ArrayList<Continent> getAll() {
		ResultSet rs = null;
		ArrayList<Continent> liste = new ArrayList<>();
		try {
			Statement stmt = connexion.createStatement();
			String strCmd = "select continent.id_continent, nom_continent, id_pays,nom_pays from CONTINENT "
					+ "join PAYS on PAYS.ID_CONTINENT = CONTINENT.ID_CONTINENT " + "order by NOM_CONTINENT, NOM_PAYS";
			rs = stmt.executeQuery(strCmd);
 
			do{
				Continent continentLu  = liste.get( liste.size() -1 );
				if (continentLu.getId() != rs.getInt(1)) {
					 ;
					liste.add(new Continent(rs.getInt(1), rs.getString(2)));
				}
				continentLu.getListe().add(new Pays(rs.getInt(3), rs.getString(4)));
			}while (rs.next()) ;

			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return liste;
	}

 
	public boolean insert(Continent obj) {
		try {
			String strCmd = "insert into Continent values ?";
			PreparedStatement pStmt = connexion.prepareStatement(strCmd);
			pStmt.setString(1, obj.getLibelle());
			pStmt.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

 
	public Integer update(Continent obj) {
		try {
			Statement stmt = connexion.createStatement();
			String strCmd = "update CONTINENT set nom_continent = " + obj.getLibelle() + " where id_continent = "
					+ obj.getId();
			stmt.execute(strCmd);
			return stmt.getUpdateCount();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
 
 
}
