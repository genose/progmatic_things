package users_accounts;

import java.io.Serializable;

import java.lang.Integer;
import java.util.Date;

import javax.persistence.*;

import org.eclipse.persistence.jpa.jpql.parser.DateTime;

/**
 * Entity implementation class for Entity: account
 *
 */
@Entity
@Table(name="ACCOUNTS")
public class Account {
	
	/* ***************************** */
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY )
	private Integer 	idAccount;
	/* ***************************** */
	@Temporal(TemporalType.TIMESTAMP)
	private Date 	creationDate;

	/* ***************************** */
	private Integer accountType;
	
	/* ***************************** */
	/* ****   Foreign Keys  ***** */
	/* ***************************** */  
	@OneToOne(mappedBy = "accountInfo", targetEntity = Users.class)
	private Users 		userInfo;
	/* ***************************** */  
	private static final long serialVersionUID = 1L;

	public Account() {
		super();
		this.creationDate = new Date();
	}
	/**
	 * @return the user
	 */
	public Users getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(Users user) {
		this.user = user;
	}
   
	public Integer getIdAccount() {
		return this.idAccount;
	}

	public void setIdAccount(Integer idAccount) {
		this.idAccount = idAccount;
	}   
	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
   
}
