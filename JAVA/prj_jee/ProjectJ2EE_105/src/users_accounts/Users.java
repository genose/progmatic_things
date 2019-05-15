package users_accounts;
import users_accounts.*;




import java.io.Serializable;

import java.lang.Integer;
import java.lang.String;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.List;

import javax.persistence.*;
import javax.persistence.criteria.CollectionJoin;

/**
 * Entity implementation class for Entity: Users
 *
 */

 
@Entity (name = "USERS")
public class Users implements Serializable {

	/* ***************************** */
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY )
	private Integer userId;
	/* ***************************** */
	private String userName;
	private String userEmail;
	private String userLogin;
	private String userPassword;
	/* ***************************** */
	
	/* ***************************** */
	/* ****   Foreign Keys  ***** */
	/* ***************************** */
    @OneToOne  // référence la relation dans la classe Account
    private Account accountInfo ;
	/* ***************************** */
	@OneToMany(mappedBy = "userHistoryInfo")
	private Collection<UsersHistory> connectionHistory;
	/* ***************************** */
	@OneToOne
	private Personnes userIdentity;
	/* ***************************** */
	private static final long serialVersionUID = 1L;
	/* ***************************** */
	public Users() {
		super();
	}
	/**
	 * @param login
	 * @param password
	 */
	public Users(String login, String password) {
		super();
		this.userLogin = login;
		this.userPassword = password;
	}
	 
	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}
	/**
	 * @param userEmail the userEmail to set
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	/**
	 * @return the userLogin
	 */
	public String getUserLogin() {
		return userLogin;
	}
	/**
	 * @param userLogin the userLogin to set
	 */
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
	/**
	 * @return the userPassword
	 */
	public String getUserPassword() {
		return userPassword;
	}
	/**
	 * @param userPassword the userPassword to set
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	@Override
	public String toString() {
		return String.format(
				"Users [userId=%s, userName=%s, userEmail=%s, userLogin=%s, userPassword=%s, accountInfo=%s, connectionHistory=%s, userIdentity=%s]",
				userId, userName, userEmail, userLogin, userPassword, accountInfo, connectionHistory, userIdentity);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userEmail == null) ? 0 : userEmail.hashCode());
		result = prime * result + ((userLogin == null) ? 0 : userLogin.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		result = prime * result + ((userPassword == null) ? 0 : userPassword.hashCode());
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
		Users other = (Users) obj;
		if (userEmail == null) {
			if (other.userEmail != null)
				return false;
		} else if (!userEmail.equals(other.userEmail))
			return false;
		if (userLogin == null) {
			if (other.userLogin != null)
				return false;
		} else if (!userLogin.equals(other.userLogin))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		if (userPassword == null) {
			if (other.userPassword != null)
				return false;
		} else if (!userPassword.equals(other.userPassword))
			return false;
		return true;
	} 
	
	 
   
}
