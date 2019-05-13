package users_accounts;

import java.io.Serializable;

import java.lang.Integer;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*; 

 

/**
 * Entity implementation class for Entity: Adresses
 *
 */
@Entity
@Table(name="ADRESSES")
public class Adresses implements Serializable {

	/* ***************************** */
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY )
	private Integer idAdresse;
	/* ***************************** */
	private String name;
	private String additionnalName;
	private String postcode;
	private Integer cityName;
	/* ***************************** */
	
	

	
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
	
	
	/* ***************************** */
	private Integer adresseType;
	/* ***************************** */
	private static final long serialVersionUID = 1L;
	/* ***************************** */
	public Adresses() {
		super();
	}
	
	
	/**
	 * @param name
	 * @param additionnalName
	 * @param postcode
	 * @param cityName
	 */
	public Adresses(String name, String additionnalName, String postcode, Integer cityName) {
		super();
		this.name = name;
		this.additionnalName = additionnalName;
		this.postcode = postcode;
		this.cityName = cityName;
		this.adresseType = typeAdresse.ADRESSE_TYPE_INCONNU.getValue();
	}


	/**
	 * @param name
	 * @param additionnalName
	 * @param postcode
	 * @param cityName
	 * @param adresseType
	 */
	public Adresses(String name, String additionnalName, String postcode, Integer cityName, typeAdresse adresseType) {
		super();
		this.name = name;
		this.additionnalName = additionnalName;
		this.postcode = postcode;
		this.cityName = cityName;
		this.adresseType = adresseType.getValue();
	}


	/* ***************************** */
	public Integer getIdAdresse() {
		return this.idAdresse;
	}
	/* ***************************** */
	public void setIdAdresse(Integer idAdresse) {
		this.idAdresse = idAdresse;
	}   
	public String getName() {
		return this.name;
	}
	/* ***************************** */
	public void setName(String name) {
		this.name = name;
	}
	/* ***************************** */
	public String getPostcode() {
		return this.postcode;
	}
	/* ***************************** */
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	/* ***************************** */
	public Integer getCityName() {
		return this.cityName;
	}
	/* ***************************** */
	public void setCityName(Integer cityName) {
		this.cityName = cityName;
	}
	/* ***************************** */
	public String getAdditionnalName() {
		return this.additionnalName;
	}
	/* ***************************** */
	public void setAdditionnalName(String additionnalName) {
		this.additionnalName = additionnalName;
	}
	/* ***************************** */
}
