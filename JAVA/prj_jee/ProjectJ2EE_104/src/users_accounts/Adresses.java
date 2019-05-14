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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountInfo == null) ? 0 : accountInfo.hashCode());
		result = prime * result + ((additionnalName == null) ? 0 : additionnalName.hashCode());
		result = prime * result + ((adresseType == null) ? 0 : adresseType.hashCode());
		result = prime * result + ((cityName == null) ? 0 : cityName.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((idAdresse == null) ? 0 : idAdresse.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((postcode == null) ? 0 : postcode.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Adresses other = (Adresses) obj;
		if (accountInfo == null) {
			if (other.accountInfo != null)
				return false;
		} else if (!accountInfo.equals(other.accountInfo))
			return false;
		if (additionnalName == null) {
			if (other.additionnalName != null)
				return false;
		} else if (!additionnalName.equals(other.additionnalName))
			return false;
		if (adresseType == null) {
			if (other.adresseType != null)
				return false;
		} else if (!adresseType.equals(other.adresseType))
			return false;
		if (cityName == null) {
			if (other.cityName != null)
				return false;
		} else if (!cityName.equals(other.cityName))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (idAdresse == null) {
			if (other.idAdresse != null)
				return false;
		} else if (!idAdresse.equals(other.idAdresse))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (postcode == null) {
			if (other.postcode != null)
				return false;
		} else if (!postcode.equals(other.postcode))
			return false;
		return true;
	}
	/* ***************************** */
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY )
	private Integer idAdresse;
	/* ***************************** */
	private String name;
	private String additionnalName;
	private String postcode;
	private Integer cityName;
	private String email;
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
	@ManyToOne   // référence la relation dans la classe Account
	private Account accountInfo;
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


	@Override
	public String toString() {
		return String.format(
				"Adresses [idAdresse=%s, name=%s, additionnalName=%s, postcode=%s, cityName=%s, email=%s, adresseType=%s, accountInfo=%s]",
				idAdresse, name, additionnalName, postcode, cityName, email, adresseType, accountInfo);
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
