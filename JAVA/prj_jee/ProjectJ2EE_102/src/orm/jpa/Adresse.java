/**
 * 
 */
package orm.jpa;

import java.util.HashMap;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Id;
import javax.persistence.Persistence;
import javax.persistence.Table;

/**
 * @author 59013-03-13
 *
 */
@Entity @Table(name="adresses")
public class Adresse {

	/**
	 * 
	 */
	@Id
	private Integer idadresses;

	private String rue;
	private String numero;
	private Integer codepostal;
	private String ville;
	
	public Adresse() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the idadresses
	 */
	public Integer getIdadresses() {
		return idadresses;
	}

	/**
	 * @param idadresses the idadresses to set
	 */
	public void setIdadresses(Integer idadresses) {
		this.idadresses = idadresses;
	}

	/**
	 * @return the rue
	 */
	public String getRue() {
		return rue;
	}

	/**
	 * @param rue the rue to set
	 */
	public void setRue(String rue) {
		this.rue = rue;
	}

	/**
	 * @return the numero
	 */
	public String getNumero() {
		return numero;
	}

	/**
	 * @param numero the numero to set
	 */
	public void setNumero(String numero) {
		this.numero = numero;
	}

	/**
	 * @return the codepostal
	 */
	public Integer getCodepostal() {
		return codepostal;
	}

	/**
	 * @param codepostal the codepostal to set
	 */
	public void setCodepostal(Integer codepostal) {
		this.codepostal = codepostal;
	}

	/**
	 * @return the ville
	 */
	public String getVille() {
		return ville;
	}

	/**
	 * @param ville the ville to set
	 */
	public void setVille(String ville) {
		this.ville = ville;
	}

	@Override
	public String toString() {
		return "adresse [idadresses=" + idadresses + ", rue=" + rue + ", numero=" + numero + ", codepostal="
				+ codepostal + ", ville=" + ville + "]";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EntityManagerFactory 	entManagerFact 	= null;
		EntityManager 			entManager 		= null;
		EntityTransaction 		entTransac 		= null;
		
		try {
			entManagerFact = Persistence.createEntityManagerFactory("entreprise");
			entManager = entManagerFact.createEntityManager();
			Map<String, Object> listPersonnes = new HashMap<String, Object>();
			Object obj = new Object();
			Map<String, Object> mappingFields = new HashMap<String, Object>();
			Map relationMapping = new HashMap<String, String>();
			relationMapping.put( "id", "> 1" );
			
			mappingFields.put(Personne.class.getSimpleName(), "id>1" );
			listPersonnes = (Map<String, Object>) entManager.find(Personne.class,  0 , listPersonnes);
			// pers = ((pers == null ) ? new Personne(): pers);
			System.out.println(String.valueOf(Personne.class.getClass())+" :: "+String.valueOf(mappingFields.toString()));
			System.out.println( ((listPersonnes != null)?listPersonnes: String.valueOf("null entity")) .toString());
			
			
			Personne pers = entManager.find(Personne.class, 1);
			
			
			
			System.out.println( ((pers != null)?pers: String.valueOf("null entity")) .toString()+" :: "+entManager.getMetamodel().getManagedTypes().toString());
			
			
		}catch(Exception EV_ERR_ORM) {
			EV_ERR_ORM.printStackTrace();
		}
	}

}
