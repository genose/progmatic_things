package dao.objectInterface;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public abstract interface DAOObject
{
	 public static final String sERRMESSAGEDAOOBJECT_PARAM = "ERROR : Parametre invalide ";
	public static final String sDEFAULTSELECTCOMBOXLIBELLE = "Choisir ...";
	
	public static final String fieldEntityName = "tablename";
	public static final String fieldID = "id";
	public static final String fieldLibelle = "libelle";
	
	public static final String accessorFieldID = "getid";
	public static final String accessorFieldLibelle = "getLibelle";
	
	public abstract Integer getId();
	public abstract void setId(Integer idKey);
	public abstract String getLibelle() ;
	public abstract Boolean setLibelle(String sLibelle) ;
	
	public abstract IntegerProperty getPropertyId();
	public abstract void setPropertyId(IntegerProperty idKey);
	public abstract StringProperty getPropertyLibelle() ;
	public abstract Boolean setPropertyLibelle(StringProperty sLibelle) ;
	
	
}
