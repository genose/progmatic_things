package dao.objectInterface;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Objects;

public abstract class DAO<T> {
 public static final String sERRMESSAGEDAO = "ERROR : Impossible d'utiliser une connexion NULL ";
 public static final String sERRMESSAGEDAO_PARAM = "ERROR : Parametre invalide ";
	public DAO(Connection argConnexion) {
		
		Objects.requireNonNull(argConnexion, sERRMESSAGEDAO);
		this.connexion = argConnexion;
	}

	protected Connection connexion;
	public String sDOADefinition = null;
	public abstract T  getByID(int id);
	public abstract ArrayList<T>  getAll();
	public abstract ArrayList<T>  select(T obj);
	public abstract Integer insert(T obj);
	public abstract Integer update(T obj);
	public abstract Integer delete(T obj);
	public boolean setOrNull( PreparedStatement aStatementArg, int iArgFieldPosition, String sValue) {
		try {
			if ((sValue == null ) || (sValue .isEmpty()) ) {
			
				aStatementArg.setNull(iArgFieldPosition, Types.VARCHAR);
		
		} else {
			aStatementArg.setString(iArgFieldPosition, sValue);
		}
			return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return false;
	};
	public boolean setOrNull( PreparedStatement aStatementArg, int iArgFieldPosition, Integer iValue) {
		try {
			if ((iValue == null ) || (iValue <= 0) ) {
				
				aStatementArg.setNull(iArgFieldPosition, Types.INTEGER);
				
			} else {
				aStatementArg.setInt(iArgFieldPosition, iValue);
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	};
	public boolean setOrNull( PreparedStatement aStatementArg, int iArgFieldPosition, Float fValue) {
		try {
			if ((fValue == null ) || (fValue <= 0) ) {
				
				aStatementArg.setNull( iArgFieldPosition, Types.FLOAT);
				
			} else {
				aStatementArg.setFloat(iArgFieldPosition, fValue);
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	};
 
	}
