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

import users_accounts.Personne;

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
		
		System.out.println("Begin *****************************");
		
		EntityManagerFactory entManFactory = Persistence.createEntityManagerFactory("ProjectJ2EE_103");
		EntityManager entManager = entManFactory.createEntityManager();
		EntityTransaction entTransac = entManager.getTransaction();
		
		int testCase = 3;
		
		if(testCase == 0) {		
				entTransac.begin();
				
				if(entTransac.isActive()) {
					PersonnePK pk = new PersonnePK("Bourne", "Jason");
					Personne pers =  null;
					pers = entManager.find(Personne.class,pk);
					pers = ((pers == null)? new Personne("**", "**", 0) : pers);
					System.out.println(" result : "+pers);
					if(pers.getId() == 0) 
					{
						entManager.persist(pers);
					}
					
		//			String sql = "Select * from personne";
		//			Query queryResult = entManager.createNativeQuery(sql, Personne.class);
		//			List<Personne> listPersonnes = queryResult.getResultList();
		//			System.out.println("Found Results : "+String.valueOf(listPersonnes.size()) );
		//			for ( Personne persIdent : listPersonnes) {
		//				System.out.println("PersonneIdent : "+persIdent.toString());
		//			}
					
					Query queryResult = entManager.createNamedQuery("findByNomPrenom");
					queryResult.setParameter("nom", "Bourne");
					queryResult.setParameter("prenom", "Jason");
					
					List<Personne> listPersonnes = queryResult.getResultList();
					System.out.println("Found Results : "+String.valueOf(listPersonnes.size()) );
					for ( Personne persIdent : listPersonnes) {
						System.out.println("PersonneIdent : "+persIdent.toString());
					}
					entTransac.commit();
				}else {
					throw new TransactionRequiredException("Transaction is not avail ...");
				}
			}else if(testCase == 1) {
				// *******************************
				PersonnePK pk = new PersonnePK("Bourne", "Jason");
				Personne pers =  null;
				// *******************************
				pers = entManager.find(Personne.class,pk);
				pers = ((pers == null)? new Personne("**", "**", 0) : pers);
				// *******************************
				System.out.println("*******************\n  result : "+pers);
				System.out.println("*******************\n");  
				// *******************************
				pers.setAge(39);
				// *******************************			
				System.out.println("*******************\n 1. personne  :: "+ pers.toString());
				System.out.println("*******************\n");
				
				// *******************************
				System.out.println("*******************\n  Refresh from Database");
				entManager.refresh(pers);
				
				System.out.println("*******************\n");
				pers = entManager.find(Personne.class,pk);
				// *******************************				
				System.out.println("*******************\n 3. personne  :: "+ pers.toString());
				System.out.println("*******************\n");
				// *******************************
				entManager.close();
				entManFactory.close();
			}else if(testCase == 2) {
				// *******************************
				PersonnePK pk = new PersonnePK("Bourne", "Jason");
				Personne pers =  null;
				// *******************************
				pers = entManager.find(Personne.class,pk);
				pers = ((pers == null)? new Personne("**", "**", 0) : pers);
				// *******************************
				System.out.println("*******************\n  result : "+pers);
				System.out.println("*******************\n");  
				// *******************************
				pers.setNom("BourneShell");
				// *******************************
				entManager.detach(pers);
				// *******************************
				entManager.getTransaction().begin();
				// *******************************
				entManager.flush();
				entManager.getTransaction().commit();
				// *******************************				
				System.out.println("*******************\n 1. personne  :: "+ pers.toString());
				System.out.println("*******************\n");
				// *******************************		
				pers = entManager.find(Personne.class,pk);
				// *******************************				
				System.out.println("*******************\n 2. personne  :: "+ pers.toString());
				System.out.println("*******************\n");
				// *******************************
				System.out.println("*******************\n  Refresh from Database");
				entManager.refresh(pers);
				
				System.out.println("*******************\n");
				pers = entManager.find(Personne.class,pk);
				// *******************************				
				System.out.println("*******************\n 3. personne  :: "+ pers.toString());
				System.out.println("*******************\n");
				// *******************************
				entManager.close();
				entManFactory.close();
				// *******************************
			}else if(testCase == 3){

				// *******************************
				PersonnePK pk = new PersonnePK("Bourne", "Jason");
				Personne pers =  null;
				// *******************************
				pers = entManager.find(Personne.class,pk);
				pers = ((pers == null)? new Personne("**", "**", 0) : pers);
				// *******************************
				System.out.println("*******************\n  result : "+pers);
				System.out.println("*******************\n");
				if(pers.getId() == 0) {
					
					pers.setNom("Bourne");
					pers.setPrenom("Jason");
					pers.setAge(38);
					Adresse addr = new Adresse("Los Angeles Avenue", "AU", "Melbourne");
					Adresse addr2 = new Adresse("Los Spentos Avenue", "AU", "Melbourne");
					pers.addAdresse(addr);
					pers.addAdresse(addr2);
					
					entTransac.begin();
					
					System.out.println("*******************\n 2. Create personne  :: "+ pers.toString());
					System.out.println("*******************\n");
					entManager.persist(pers);
					entManager.flush();
					entTransac.commit();
					
					
				}
				pers = entManager.find(Personne.class,pk);
				// *******************************			
				pers = ((pers == null)? new Personne("**NOPE**", "**NOPE**", 0) : pers);
				System.out.println("*******************\n 3. personne  :: "+ pers.toString());
				System.out.println("*******************\n");
				// *******************************
			}
			
		
		
		System.out.println("Finish **************************");
		

	}

}
