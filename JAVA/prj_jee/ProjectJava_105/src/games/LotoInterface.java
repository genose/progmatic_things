/**
 * 
 */
package games;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 59013-36-18
 *
 */
public interface LotoInterface {

	public void shuffle() throws Exception;

	public Map<String, Object> shuffle(List<Integer> usableNumber) throws Exception;
}
