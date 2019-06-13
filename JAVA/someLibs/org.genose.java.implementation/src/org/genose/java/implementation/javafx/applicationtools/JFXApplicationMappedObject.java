/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationMappedObject extends HashMap<String, JFXApplicationObjectValue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public JFXApplicationMappedObject() {
		super();
		// TODO Auto-generated constructor stub
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

}
