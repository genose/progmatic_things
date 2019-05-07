/**
 * 
 */
package DBProjectJ2EE_103;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * @author 59013-03-13
 *
 */
@IdClass(AdressePK.class)
@Entity
@Table(name="Adresse")
public class Adresse implements Serializable {

	/**
	 * 
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY ) 
	private Integer idAdresse;
	@Id
	private Integer typeAddr;
	
	

	@Id
	private String rue;
	
	private String codePostal;
	
	private String ville;

	
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
	    
	    public String getEnumByString(String code){
	        for(typeAdresse e : typeAdresse.values()){
	            if(code == String.valueOf(value) ) return e.name();
	        }
	        return null;
	    }
	    
	    public String toString() {
	    	 for(typeAdresse e : typeAdresse.values()){
	             if(value == e.value) return e.name();
	         }
	    	 return "";
	    }
	}; 
	
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public Adresse() {
		// TODO Auto-generated constructor stub
		super();
		this.idAdresse = 0;
		this.rue = "";
		this.codePostal = "";
		this.ville = "";
		this.typeAddr = typeAdresse.ADRESSE_TYPE_INCONNU.getValue();
	}

	public Adresse(String rue, String codePostal, String ville) {
		super();
		this.idAdresse = 0;
		this.rue = rue;
		this.codePostal = codePostal;
		this.ville = ville;
		this.typeAddr = typeAdresse.ADRESSE_TYPE_INCONNU.getValue();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Adresse other = (Adresse) obj;
		if (codePostal == null) {
			if (other.codePostal != null)
				return false;
		} else if (!codePostal.equals(other.codePostal))
			return false;
		if (idAdresse != other.idAdresse)
			return false;
		if (rue == null) {
			if (other.rue != null)
				return false;
		} else if (!rue.equals(other.rue))
			return false;
		if (typeAddr == null) {
			if (other.typeAddr != null)
				return false;
		} else if (!typeAddr.equals(other.typeAddr))
			return false;
		if (ville == null) {
			if (other.ville != null)
				return false;
		} else if (!ville.equals(other.ville))
			return false;
		return true;
	}

	/**
	 * @param typeAddr the typeAddr to set
	 */
	public void setTypeAddr(Integer typeAddr) {
		this.typeAddr = typeAddr;
	}
	/**
	 * @return the typeAddr
	 */
	public Integer getTypeAddr() {
		return typeAddr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codePostal == null) ? 0 : codePostal.hashCode());
		result = prime * result + idAdresse;
		result = prime * result + ((rue == null) ? 0 : rue.hashCode());
		result = prime * result + ((typeAddr == null) ? 0 : typeAddr.hashCode());
		result = prime * result + ((ville == null) ? 0 : ville.hashCode());
		return result;
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
