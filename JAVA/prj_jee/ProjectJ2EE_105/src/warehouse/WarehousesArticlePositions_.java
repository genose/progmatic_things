package warehouse;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-05-21T14:38:45.568+0200")
@StaticMetamodel(WarehousesArticlePositions.class)
public class WarehousesArticlePositions_ {
	public static volatile SingularAttribute<WarehousesArticlePositions, Integer> positionId;
	public static volatile SingularAttribute<WarehousesArticlePositions, String> positionDescrition;
	public static volatile SingularAttribute<WarehousesArticlePositions, Integer> positionQty;
	public static volatile SingularAttribute<WarehousesArticlePositions, Integer> positionQtyDate;
	public static volatile SingularAttribute<WarehousesArticlePositions, Integer> positionQtyLastDate;
	public static volatile SingularAttribute<WarehousesArticlePositions, Integer> positionReapproAwait;
	public static volatile SingularAttribute<WarehousesArticlePositions, Date> positionReapproAwaitDate;
	public static volatile SingularAttribute<WarehousesArticlePositions, Articles> articlesStocksPositions;
}
