/**
 * 
 */
package orm.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;

/**
 * @author 59013-03-13
 *
 */
public class test {

	/**
	 * 
	 */
	public test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		EntityManagerFactory 	entManagerFact 	= null;
		EntityManager 			entManager 		= null;
		EntityTransaction 		entTransac 		= null;
		
		try {
			entManagerFact = Persistence.createEntityManagerFactory("entreprise");
			entManager = entManagerFact.createEntityManager();
			Map<String, Object> listPersonnes = new HashMap<String, Object>();
			Object obj = new Object();
			listPersonnes = (Map<String, Object>) entManager.find(Personne.class,  0 , listPersonnes);
			// pers = ((pers == null ) ? new Personne(): pers);
			// System.out.println(String.valueOf(pers.getClass())+" :: "+String.valueOf(pers.toString()));
			System.out.println( ((listPersonnes != null)?listPersonnes: String.valueOf("null entity")) .toString());
		}catch(Exception EV_ERR_ORM) {
			EV_ERR_ORM.printStackTrace();
		}
	}

}
