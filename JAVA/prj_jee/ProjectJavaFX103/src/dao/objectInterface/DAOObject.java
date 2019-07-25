package dao.objectInterface;

public abstract interface DAOObject
{

	public abstract Integer getId();
	public abstract void setId(Integer idKey);
	public abstract String getLibelle() ;
	public abstract Boolean setLibelle(String sLibelle) ;
	
	
}
