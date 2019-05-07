/**
 * 
 */
package DBProjectJ2EE_103;

import java.io.Serializable;
import javax.persistence.Id;

/**
 * @author 59013-03-13
 *
 */
public class AdressePK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	private Integer idAdresse;
	@Id
	private Integer typeAddr;
	@Id
	private String rue;
 
	
	public AdressePK() {
		// TODO Auto-generated constructor stub
		super();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idAdresse;
		result = prime * result + ((rue == null) ? 0 : rue.hashCode());
		result = prime * result + ((typeAddr == null) ? 0 : typeAddr.hashCode());
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
		AdressePK other = (AdressePK) obj;
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
		return true;
	}
	/**
	 * @return the idAdresse
	 */
	public int getIdAdresse() {
		return idAdresse;
	}

	/**
	 * @param idAdresse the idAdresse to set
	 */
	public void setIdAdresse(int idAdresse) {
		this.idAdresse = idAdresse;
	}

	/**
	 * @return the type
	 */
	public Integer getTypeAddr() {
		return typeAddr;
	}

	/**
	 * @param type the type to set
	 */
	public void setTypeAddr(Adresse.typeAdresse type) {
		this.typeAddr = type.getValue();
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

	 

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
