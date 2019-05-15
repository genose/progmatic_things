package warehouse;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: OrderHistory
 *
 */
@Entity

public class OrderHistory implements Serializable {

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY )
	private Integer orderHistoryId;
	
	@OneToOne
	private Commandes ordered;
	
	
	private static final long serialVersionUID = 1L;

	public OrderHistory() {
		super();
	}
   
}
