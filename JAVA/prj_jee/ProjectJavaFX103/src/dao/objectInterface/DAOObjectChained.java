package dao.objectInterface;

import java.util.ArrayList;

public interface DAOObjectChained<T, S> extends DAOObject {
 
	public abstract ArrayList<S> getListe();

	public abstract Boolean setListe(ArrayList<S> aListeObject);

}