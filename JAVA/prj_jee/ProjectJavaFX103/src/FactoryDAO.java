import java.util.ArrayList;

import dao.Continent;
import dao.DAO;
import dao.SDBMConnect;

public class FactoryDAO<T> {

	private static SDBMConnect aConnexion = null;
	
	public FactoryDAO(SDBMConnect argConnexion) {
		this.aConnexion = argConnexion;
	}
	
	public T getFactory(T aFactory){
		
		
		return aFactory;
		
	}
	

}
