/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools.arraysmapslists;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

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
	public Boolean isComplexObject() {
		return true;
	}

	@Override
	public JFXApplicationObjectValue get(Object key) {
		return super.get(key);
	}

	public JFXApplicationObjectValue get(Integer key) {
		return super.get(String.valueOf(key));
	}

	@Override
	public Collection<JFXApplicationObjectValue> values() {
		return super.values();
	}

	/**
	 * 
	 * @param key
	 * @param aStringValue
	 */
	public Object put(String key, String aStringValue) {
		return super.put(key, (new JFXApplicationObjectValue(aStringValue)));
	}

	/**
	 * 
	 * @param key
	 * @param aStringValue
	 */
	public Object put(Integer key, String aStringValue) {
		return super.put(String.valueOf(key), (new JFXApplicationObjectValue(aStringValue)));

	}

	/*
	 * https://stackoverflow.com/questions/45840136/java-8-function-class-addthen-
	 * default-method
	 * https://www.deadcoderising.com/2015-09-07-java-8-functional-composition-using
	 * -compose-and-andthen/
	 * https://stackoverflow.com/questions/43849066/java-8-functions-compose-and-
	 * andthen
	 * 
	 * ******** default <V> Function<T, V> andThen(Function<? super R, ? extends V>
	 * after) { Objects.requireNonNull(after); return (T t) ->
	 * after.apply(apply(t)); }
	 */

	public Function<JFXApplicationMappedObject, JFXApplicationMappedObject> toAsString = (
			JFXApplicationMappedObject aCollection) -> {

		this.forEach((String e, JFXApplicationObjectValue a) -> System.out.println(" == " + e + "::" + a.getString()));
		return this;

	};

	/**
	 * 
	 * @param key
	 * @param aMappedObject
	 */
	public Object put(String key, JFXApplicationMappedObject aMappedObject) {
		return super.put(key, (new JFXApplicationObjectValue(aMappedObject)));

	}

	/**
	 * 
	 * 
	 * super.putAll(mMappedObject); for (Entry<String, JFXApplicationObjectValue>
	 * aNodeChildElementEntry : mMappedObject.entrySet()) {
	 * 
	 * String aNodeChildElementName = aNodeChildElementEntry.getKey();
	 * JFXApplicationObjectValue aNodeChildElementValue =
	 * aNodeChildElementEntry.getValue();
	 * 
	 * 
	 * }
	 * 
	 * }
	 **/
}
