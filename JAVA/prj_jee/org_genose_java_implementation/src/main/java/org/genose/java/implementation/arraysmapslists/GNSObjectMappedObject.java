/**
 * 
 */
package org.genose.java.implementation.arraysmapslists;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author 59013-36-18
 *
 */
public class GNSObjectMappedObject extends HashMap<String, GNSObjectMappedObjectValue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public GNSObjectMappedObject() {
		super();
	}

	/**
	 * @param initialCapacity
	 * @param loadFactor
	 */
	public GNSObjectMappedObject(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * @param initialCapacity
	 */
	public GNSObjectMappedObject(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * @param m
	 */
	public GNSObjectMappedObject(Map<? extends String, ? extends GNSObjectMappedObjectValue> m) {
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
	public GNSObjectMappedObjectValue get(Object key) {
		return super.get(key);
	}

	public GNSObjectMappedObjectValue get(Integer key) {
		return super.get(String.valueOf(key));
	}

	@Override
	public Collection<GNSObjectMappedObjectValue> values() {
		return super.values();
	}

	/**
	 * 
	 * @param key
	 * @param aStringValue
	 */
	public Object put(String key, String aStringValue) {
		return super.put(key, (new GNSObjectMappedObjectValue(aStringValue)));
	}

	/**
	 * 
	 * @param key
	 * @param aStringValue
	 */
	public Object put(Integer key, String aStringValue) {
		return super.put(String.valueOf(key), (new GNSObjectMappedObjectValue(aStringValue)));

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

	public Function<GNSObjectMappedObject, GNSObjectMappedObject> toAsString = (
			GNSObjectMappedObject aCollection) -> {

		this.forEach((String e, GNSObjectMappedObjectValue a) -> System.out.println(" == " + e + "::" + a.getString()));
		return this;

	};

	/**
	 * 
	 * @param key
	 * @param aMappedObject
	 */
	public Object put(String key, GNSObjectMappedObject aMappedObject) {
		return super.put(key, (new GNSObjectMappedObjectValue(aMappedObject)));

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
