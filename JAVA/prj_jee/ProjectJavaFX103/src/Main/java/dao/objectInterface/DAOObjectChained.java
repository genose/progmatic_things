package dao.objectInterface;

import java.util.ArrayList;

public interface DAOObjectChained<T, S> extends DAOObject {
 
	ArrayList<S> getListe();

	Boolean setListe(ArrayList<S> aListeObject);

}