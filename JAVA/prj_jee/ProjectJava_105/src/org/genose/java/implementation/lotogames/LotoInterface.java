/**
 * 
 */
package org.genose.java.implementation.lotogames;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 59013-36-18
 *
 */
public interface LotoInterface {

	public void shuffle(Boolean bDoNotify) throws Exception;

	public Map<String, Object> shuffle(List<Integer> userUsableNumber, Boolean bDoNotify)  throws Exception;
}
