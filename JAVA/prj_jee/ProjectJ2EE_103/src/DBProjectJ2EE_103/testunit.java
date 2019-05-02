/**
 * 
 */
package DBProjectJ2EE_103;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.transaction.TransactionRequiredException;

/**
 * @author 59013-03-13
 *
 */
public class testunit {

	/**
	 * @throws Exception
	 * @return
	 * 
	 */
	public testunit() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws TransactionRequiredException 
	 */
	public static void main(String[] args) throws TransactionRequiredException {
		// TODO Auto-generated method stub
		
		System.out.println("*****************************");
		PersonnePK pers = null;
		EntityManagerFactory entManFactory = Persistence.createEntityManagerFactory("ProjectJ2EE_103");
		EntityManager entManager = entManFactory.createEntityManager();
		EntityTransaction entTransac = entManager.getTransaction();
		
		
		entTransac.begin();
		
		if(entTransac.isActive()) {
			 
			pers = entManager.find(PersonnePK.class,1);
			pers = ((pers == null)? new Personne() : pers);
			System.out.println(" result : "+pers);
			entManager.persist(pers);
			
			String sql = "Select * from personne";
			Query queryResult = entManager.createNativeQuery(sql, Personne.class);
			List<Personne> listPersonnes = queryResult.getResultList();
			for ( Personne persIdent : listPersonnes) {
				System.out.println("PersonneIdent : "+persIdent.toString());
			}
			
			
		}else {
			throw new TransactionRequiredException("Transaction is not avail ...");
		}
		
		entTransac.commit();
		
		

	}

}
