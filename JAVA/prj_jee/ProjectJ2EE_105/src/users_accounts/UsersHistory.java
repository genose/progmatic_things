package users_accounts;

import java.io.Serializable;


import java.lang.Integer;
import java.util.Date;

import javax.persistence.*;


/**
 * Entity implementation class for Entity: UsersHistory
 *
 */
@Entity(name="USERSHISTORY")
public class UsersHistory implements Serializable {


	/* ***************************** */
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY )
	private Integer historyId;

	/* ***************************** */   
	private String historyEvent;
	/* ***************************** */ 
	@Temporal(TemporalType.TIMESTAMP)
	private Date historydate;
	
	@Override
	public String toString() {
		return String.format("UsersHistory [historyId=%s, historyEvent=%s, historydate=%s, userHistoryInfo=%s]",
				historyId, historyEvent, historydate, userHistoryInfo);
	}
	/* ***************************** */
	/* ****   Foreign Keys  ***** */
	/* ***************************** */
	@ManyToOne
	private Users userHistoryInfo;
	/* ***************************** */
	/* ***************************** */
	
	/* ***************************** */ 
	private static final long serialVersionUID = 1L;
	/* ***************************** */ 
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
		result = prime * result + ((historyEvent == null) ? 0 : historyEvent.hashCode());
		result = prime * result + ((historyId == null) ? 0 : historyId.hashCode());
		result = prime * result + ((historydate == null) ? 0 : historydate.hashCode());
		result = prime * result + ((userHistoryInfo == null) ? 0 : userHistoryInfo.hashCode());
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
		if (historyEvent == null) {
			if (other.historyEvent != null)
				return false;
		} else if (!historyEvent.equals(other.historyEvent))
			return false;
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
		if (userHistoryInfo == null) {
			if (other.userHistoryInfo != null)
				return false;
		} else if (!userHistoryInfo.equals(other.userHistoryInfo))
			return false;
		return true;
	}
   
}
