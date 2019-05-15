package warehouse;
import warehouse.*;




import java.io.Serializable;

import javax.persistence.*; 


/**
 * Entity implementation class for Entity: OrderHistory
 *
 */
@Entity (name = "ORDER_HISTORY")
public class OrderHistory implements Serializable {
 
	@GeneratedValue (strategy = GenerationType.AUTO )
	@Id
	private Integer orderHistoryId;
	
	
	private static final long serialVersionUID = 1L;

	public OrderHistory() {
		super();
	}

	@Override
	public String toString() {
		return String.format("OrderHistory [orderHistoryId=%s]", orderHistoryId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((orderHistoryId == null) ? 0 : orderHistoryId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderHistory other = (OrderHistory) obj;
		if (orderHistoryId == null) {
			if (other.orderHistoryId != null)
				return false;
		} else if (!orderHistoryId.equals(other.orderHistoryId))
			return false;
		return true;
	}
   
}
