package dao;

import java.util.ArrayList;

public abstract interface DAOObject<T> {

	public abstract int getId();
	public abstract void setId(int idKey);
	public abstract String getLibelle() ;
	public abstract Boolean setLibelle(String sLibelle) ;
	public abstract ArrayList<T> getListe();
	public abstract Boolean setListe(ArrayList<T> aListeObject);
	
}
