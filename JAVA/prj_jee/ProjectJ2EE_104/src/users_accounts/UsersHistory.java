package users_accounts;

import java.io.Serializable;

import java.lang.Integer;
import java.util.Date;

import javax.persistence.*;


/**
 * Entity implementation class for Entity: UsersHistory
 *
 */
@Entity
@Table(name="USERSHISTORY")
public class UsersHistory implements Serializable {


	/* ***************************** */
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY )
	private Integer historyId;
	// @PrimaryKeyJoinColumn("userId")
	private Integer historyUserId;
	/* ***************************** */   
	private String historyEvent;
	/* ***************************** */ 
	@Temporal(TemporalType.TIMESTAMP)
	private Date historydate;
	/* ***************************** */ 
	private static final long serialVersionUID = 1L;
	public UsersHistory() {
		super();
		this.historyEvent = "NULL EVENT";
		this.historydate = new Date();
	}
	/**
	 * @param historyEvent
	 * @param historydate
	 */
	public UsersHistory(String historyEvent) {
		super();
		this.historyEvent = historyEvent;
		this.historydate = new Date();
	}
	/**
	 * @param historyEvent
	 * @param historydate
	 */
	public UsersHistory(String historyEvent, Date historydate) {
		super();
		this.historyEvent = historyEvent;
		this.historydate = historydate;
	}
	public Integer getHistoryId() {
		return this.historyId;
	}

	public void setHistoryId(Integer historyId) {
		this.historyId = historyId;
	}   
	/**
	 * @return the historyEvent
	 */
	public String getHistoryEvent() {
		return historyEvent;
	}
	/**
	 * @param historyEvent the historyEvent to set
	 */
	public void setHistoryEvent(String historyEvent) {
		this.historyEvent = historyEvent;
	}
	public Date getHistorydate() {
		return this.historydate;
	}

	public void setHistorydate(Date historydate) {
		this.historydate = historydate;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((historyId == null) ? 0 : historyId.hashCode());
		result = prime * result + ((historydate == null) ? 0 : historydate.hashCode());
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
		UsersHistory other = (UsersHistory) obj;
		if (historyId == null) {
			if (other.historyId != null)
				return false;
		} else if (!historyId.equals(other.historyId))
			return false;
		if (historydate == null) {
			if (other.historydate != null)
				return false;
		} else if (!historydate.equals(other.historydate))
			return false;
		return true;
	}
   
}
