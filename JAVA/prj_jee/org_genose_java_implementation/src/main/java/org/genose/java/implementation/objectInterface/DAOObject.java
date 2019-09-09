package org.genose.java.implementation.objectInterface;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public interface DAOObject
{
	 String sERRMESSAGEDAOOBJECT_PARAM = "ERROR : Parametre invalide ";
	String sDEFAULTSELECTCOMBOXLIBELLE = "Choisir ...";
	
	String fieldEntityName = "tablename";
	String fieldID = "id";
	String fieldLibelle = "libelle";
	
	String accessorFieldID = "getid";
	String accessorFieldLibelle = "getLibelle";
	
	Integer getId();
	void setId(Integer idKey);
	String getLibelle() ;
	Boolean setLibelle(String sLibelle) ;
	
	IntegerProperty getPropertyId();
	void setPropertyId(IntegerProperty idKey);
	StringProperty getPropertyLibelle() ;
	Boolean setPropertyLibelle(StringProperty sLibelle) ;
	
	
}
