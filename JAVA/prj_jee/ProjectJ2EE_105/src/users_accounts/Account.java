package users_accounts;

import warehouse.*;
import FetchableEntity.*;

import java.io.Serializable;

import java.lang.Integer;
import java.util.Collection;
import java.util.Date;

import javax.annotation.ManagedBean;
import javax.persistence.*;
import javax.persistence.EntityManager;

import org.eclipse.persistence.jpa.jpql.parser.DateTime;

/**
 * Entity implementation class for Entity: account
 *
 */
@Entity(name = "ACCOUNTS")
@ManagedBean
public class Account extends FetchableClassSerializableEntity implements Serializable {

	/* ***************************** */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAccount", insertable = true, nullable = true, unique = true, updatable = false)
	private Integer idAccount;
	/* ***************************** */
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	/* ***************************** */
	@Column(name = "account_type", nullable = false, updatable = true)
	private Integer accountType;

	/* ***************************** */
	/* **** Foreign Keys ***** */
	/* ***************************** */
	@OneToOne(mappedBy = "accountInfo", targetEntity = Users.class, fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, optional = false)
	private Users userInfo;
	/* ***************************** */
	@OneToMany(mappedBy = "accountInfo", targetEntity = Adresses.class, fetch = FetchType.EAGER, cascade = {CascadeType.ALL} )
	private Collection<Adresses> accountAdresses;
	/* ***************************** */
	@OneToMany(mappedBy = "accountInfo" ,targetEntity = Commandes.class, fetch = FetchType.LAZY, cascade = {CascadeType.DETACH} )
	private Collection<Commandes> accountCommandes;
	/* ***************************** */

	private static final long serialVersionUID = 1L;

	public Account() {
		super();
		this.creationDate = new Date();
	}

	public Account(String loginAccount, String passwordAccount) {
		// TODO Auto-generated constructor stub
		userInfo = new Users(loginAccount, passwordAccount);
	}

	@Override
	public String toString() {
		return String.format("Account [idAccount=%s, creationDate=%s, accountType=%s, userInfo=%s, accountAdresses=%s]",
				idAccount, creationDate, accountType, userInfo, accountAdresses);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountAdresses == null) ? 0 : accountAdresses.hashCode());
		result = prime * result + ((accountType == null) ? 0 : accountType.hashCode());
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((idAccount == null) ? 0 : idAccount.hashCode());
		result = prime * result + ((userInfo == null) ? 0 : userInfo.hashCode());
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
		Account other = (Account) obj;
		if (accountAdresses == null) {
			if (other.accountAdresses != null)
				return false;
		} else if (!accountAdresses.equals(other.accountAdresses))
			return false;
		if (accountType == null) {
			if (other.accountType != null)
				return false;
		} else if (!accountType.equals(other.accountType))
			return false;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (idAccount == null) {
			if (other.idAccount != null)
				return false;
		} else if (!idAccount.equals(other.idAccount))
			return false;
		if (userInfo == null) {
			if (other.userInfo != null)
				return false;
		} else if (!userInfo.equals(other.userInfo))
			return false;
		return true;
	}

	/**
	 * @return the user
	 */
	public Users getUser() {
		return userInfo;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(Users user) {
		this.userInfo = user;
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
	/**
	 * 
	 * @param findLogin
	 * @param findPassword
	 * @return
	 * @throws Exception
	 */
	public static final Account find(String findLogin, String findPassword) throws Exception {
		Account entFind = new Account(findLogin, findPassword);
		
		return (Account) entFind.find();
	}
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Account find() throws Exception {
		/* ****************************************** */
		super.entManagerFactoryInit("ProjectJ2EE_105" );
		/* ****************************************** */
		getEntTransaction().begin();
		/* ****************************************** */
		Account entFind = null;
		/* ****************************************** */
		if(getEntTransaction().isActive()) {
			try {
				// *******************************
				entFind = this.find(this);
				// this = ((entFind == null)? null : entFind);
				// *******************************
				System.out.println("*******************\n  result : "+entFind);
				System.out.println("*******************\n");
			} catch (Exception EV_ERR_FINDENT) {
				System.out.println("******* ERROR ******\n"+EV_ERR_FINDENT);
				throw new Exception("ERROR While retrieve entity ... "+EV_ERR_FINDENT);
			} finally { 
				getEntManager().close();
				getEntManagerFactory().close(); 
			}
		}else {
			/* ****************************************** */
			getEntManager().close();
			getEntManagerFactory().close();
			/* ****************************************** */
			throw new Exception("Unable to obtain sustainble transaction ... ");
		}
		/* ****************************************** */
		return (Account) entFind;
	}

}
