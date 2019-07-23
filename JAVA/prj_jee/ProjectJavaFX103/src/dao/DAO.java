package dao;

import java.sql.Connection;
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

	public abstract T  getByID(int id);
	public abstract ArrayList<T>  select(T obj);
	public abstract Integer insert(T obj);
	public abstract Integer update(T obj);
	public abstract boolean delete(T obj);

 
	}
