package dao.objectInterface;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public abstract interface DAOObject
{
	 public static final String sERRMESSAGEDAOOBJECT_PARAM = "ERROR : Parametre invalide ";
	public static final String sDEFAULTSELECTCOMBOXLIBELLE = "Choisir ...";
	public abstract Integer getId();
	public abstract void setId(Integer idKey);
	public abstract String getLibelle() ;
	public abstract Boolean setLibelle(String sLibelle) ;
	
	public abstract IntegerProperty getPropertyId();
	public abstract void setPropertyId(IntegerProperty idKey);
	public abstract StringProperty getPropertyLibelle() ;
	public abstract Boolean setPropertyLibelle(StringProperty sLibelle) ;
	
	
}
