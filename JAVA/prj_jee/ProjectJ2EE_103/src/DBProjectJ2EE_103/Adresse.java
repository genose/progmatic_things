/**
 * 
 */
package DBProjectJ2EE_103;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author 59013-03-13
 *
 */
@Entity
@Table(name="Adresse")
public class Adresse implements Serializable {

	/**
	 * 
	 */
	@Id
	private int idAdresse;
	private String rue;
	private String codePostal;
	private String ville;
	private Integer type;
	
	public enum typeAdresse { 
		ADRESSE_TYPE_INCONNU(0) ,
		ADRESSE_PRINCIPAL(1) ,
		ADDRESSE_LIVRAISON(2),
		ADRESSE_FACTURATION(3),
		ADRESSE_AUXILIAIRE(4);

	    private int value;
	    private static Map map = new HashMap<>();

	    private typeAdresse(int value) {
	        this.value = value;
	    }

	    static {
	        for (typeAdresse addrType : typeAdresse.values()) {
	            map.put(addrType.value, addrType);
	        }
	    }

	    public static typeAdresse valueOf(int pageType) {
	        return (typeAdresse) map.get(pageType);
	    }

	    public int getValue() {
	        return value;
	    }
	}; 
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public Adresse() {
		// TODO Auto-generated constructor stub
		super();
	}

	public Adresse(String rue, String codePostal, String ville) {
		super();
		this.rue = rue;
		this.codePostal = codePostal;
		this.ville = ville;
		this.type = typeAdresse.ADRESSE_TYPE_INCONNU.getValue();
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
	 * @return the codePostal
	 */
	public String getCodePostal() {
		return codePostal;
	}

	/**
	 * @param codePostal the codePostal to set
	 */
	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
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

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
