package org.genose.java.implementation.dataaccessobject.objectinterface;

import java.util.ArrayList;

public interface GNSObjectDAOObjectChained<T, S> extends GNSObjectDAOObject {
 
	ArrayList<S> getListe();

	Boolean setListe(ArrayList<S> aListeObject);

}