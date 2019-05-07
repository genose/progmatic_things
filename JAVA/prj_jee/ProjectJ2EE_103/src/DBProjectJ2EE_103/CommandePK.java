package DBProjectJ2EE_103;

import java.io.Serializable;
import java.lang.Integer;

import javax.persistence.Id;
import javax.persistence.JoinTable;

/**
 * ID class for entity: Commande
 *
 */ 
public class CommandePK  implements Serializable {   
   
	@Id         
	private Integer idCommande;

	
	private static final long serialVersionUID = 1L;

	public CommandePK() {}

	

	public Integer getIdCommande() {
		return this.idCommande;
	}

	public void setIdCommande(Integer idCommande) {
		this.idCommande = idCommande;
	}
 
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CommandePK other = (CommandePK) obj;
		if (idCommande == null) {
			if (other.idCommande != null)
				return false;
		} else if (!idCommande.equals(other.idCommande))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idCommande == null) ? 0 : idCommande.hashCode());
		return result;
	}
   
   
}
