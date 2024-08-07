/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools.arraysmapslists;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationMappedObject extends HashMap<String, JFXApplicationObjectValue> implements Map<String, JFXApplicationObjectValue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public JFXApplicationMappedObject() {
		super();
	}

	/**
	 * @param initialCapacity
	 * @param loadFactor
	 */
	public JFXApplicationMappedObject(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * @param initialCapacity
	 */
	public JFXApplicationMappedObject(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * @param m
	 */
	public JFXApplicationMappedObject(Map<? extends String, ? extends JFXApplicationObjectValue> m) {
		super(m);
	}
	
	/**
	 * 
	 * @return
	 */
	public Boolean isComplexObject()
	{
		return true;
	}
/**
 * 
 * @param key
 * @param aStringValue
 */
	public Object put(String key, String aStringValue) {
		return super.put(key, (new JFXApplicationObjectValue(aStringValue)) );
	}
	/**
	 * 
	 * @param key
	 * @param aStringValue
	 */
	public Object  put(Integer key, String aStringValue) {
		return super.put(String.valueOf(key), (new JFXApplicationObjectValue(aStringValue)) );
		
	}
/**
 * 
 * @param key
 * @param aMappedObject
 */
	public Object put(String key, JFXApplicationMappedObject aMappedObject) {
		return super.put(key, (new JFXApplicationObjectValue(aMappedObject)) );
		
	}

	/**
	 
		
		super.putAll(mMappedObject);
		for (Entry<String, JFXApplicationObjectValue> aNodeChildElementEntry : mMappedObject.entrySet()) {

			String aNodeChildElementName = aNodeChildElementEntry.getKey();
			JFXApplicationObjectValue aNodeChildElementValue = aNodeChildElementEntry.getValue();
			
			
		}
		
	} **/
}
