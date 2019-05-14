/**
 * 
 */
package warehouse;
import warehouse.*;



import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * @author 59013-03-13
 *
 */
@Entity
@Table(name = "WAREHOUSES_ARTICLE_POSITIONS")
public class WarehousesArticlePositions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private Integer positionId;
	private String  positionDescrition;
	private Integer positionQty;
	private Integer positionQtyDate;
	private Integer positionQtyLastDate;
	private Integer positionReapproAwait;
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date 	positionReapproAwaitDate;
	
	/* ***************************** */
	/* ****   Foreign Keys  ***** */
	/* ***************************** */
	@ManyToOne
	private Articles articlesStocksPositions;
	/* ***************************** */

	/**
	 * @return the positionId
	 */
	public Integer getPositionId() {
		return positionId;
	}

	/**
	 * @param positionId the positionId to set
	 */
	public void setPositionId(Integer positionId) {
		this.positionId = positionId;
	}

	/**
	 * @return the positionDescrition
	 */
	public String getPositionDescrition() {
		return positionDescrition;
	}

	/**
	 * @param positionDescrition the positionDescrition to set
	 */
	public void setPositionDescrition(String positionDescrition) {
		this.positionDescrition = positionDescrition;
	}

	/**
	 * @return the positionQty
	 */
	public Integer getPositionQty() {
		return positionQty;
	}

	/**
	 * @param positionQty the positionQty to set
	 */
	public void setPositionQty(Integer positionQty) {
		this.positionQty = positionQty;
	}

	/**
	 * @return the positionQtyDate
	 */
	public Integer getPositionQtyDate() {
		return positionQtyDate;
	}

	/**
	 * @param positionQtyDate the positionQtyDate to set
	 */
	public void setPositionQtyDate(Integer positionQtyDate) {
		this.positionQtyDate = positionQtyDate;
	}

	/**
	 * @return the positionQtyLastDate
	 */
	public Integer getPositionQtyLastDate() {
		return positionQtyLastDate;
	}

	/**
	 * @param positionQtyLastDate the positionQtyLastDate to set
	 */
	public void setPositionQtyLastDate(Integer positionQtyLastDate) {
		this.positionQtyLastDate = positionQtyLastDate;
	}

	/**
	 * @return the positionReapproAwait
	 */
	public Integer getPositionReapproAwait() {
		return positionReapproAwait;
	}

	/**
	 * @param positionReapproAwait the positionReapproAwait to set
	 */
	public void setPositionReapproAwait(Integer positionReapproAwait) {
		this.positionReapproAwait = positionReapproAwait;
	}

	/**
	 * @return the positionReapproAwaitDate
	 */
	public Date getPositionReapproAwaitDate() {
		return positionReapproAwaitDate;
	}

	/**
	 * @param positionReapproAwaitDate the positionReapproAwaitDate to set
	 */
	public void setPositionReapproAwaitDate(Date positionReapproAwaitDate) {
		this.positionReapproAwaitDate = positionReapproAwaitDate;
	}

	/**
	 * @return the articlesStocksPositions
	 */
	public Articles getArticlesStocksPositions() {
		return articlesStocksPositions;
	}

	/**
	 * @param articlesStocksPositions the articlesStocksPositions to set
	 */
	public void setArticlesStocksPositions(Articles articlesStocksPositions) {
		this.articlesStocksPositions = articlesStocksPositions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((positionId == null) ? 0 : positionId.hashCode());
		result = prime * result + ((positionQty == null) ? 0 : positionQty.hashCode());
		result = prime * result + ((positionQtyDate == null) ? 0 : positionQtyDate.hashCode());
		result = prime * result + ((positionQtyLastDate == null) ? 0 : positionQtyLastDate.hashCode());
		result = prime * result + ((positionReapproAwait == null) ? 0 : positionReapproAwait.hashCode());
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
		WarehousesArticlePositions other = (WarehousesArticlePositions) obj;
		if (positionId == null) {
			if (other.positionId != null)
				return false;
		} else if (!positionId.equals(other.positionId))
			return false;
		if (positionQty == null) {
			if (other.positionQty != null)
				return false;
		} else if (!positionQty.equals(other.positionQty))
			return false;
		if (positionQtyDate == null) {
			if (other.positionQtyDate != null)
				return false;
		} else if (!positionQtyDate.equals(other.positionQtyDate))
			return false;
		if (positionQtyLastDate == null) {
			if (other.positionQtyLastDate != null)
				return false;
		} else if (!positionQtyLastDate.equals(other.positionQtyLastDate))
			return false;
		if (positionReapproAwait == null) {
			if (other.positionReapproAwait != null)
				return false;
		} else if (!positionReapproAwait.equals(other.positionReapproAwait))
			return false;
		return true;
	}
	
}
