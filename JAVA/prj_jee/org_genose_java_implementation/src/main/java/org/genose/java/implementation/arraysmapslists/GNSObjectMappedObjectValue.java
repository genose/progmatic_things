/**
 * 
 */
package org.genose.java.implementation.arraysmapslists;

import org.genose.java.implementation.exceptionerror.GNSObjectRuntimeException;

/**
 * @author 59013-36-18
 *
 */
public class GNSObjectMappedObjectValue implements GNSObjectMappedValuesAccessor {
	private Object pPrivateValue = null;

	/**
	 * 
	 */
	public GNSObjectMappedObjectValue() {
		super();
	}
	
	/**
	 * 
	 * @param aObjectValue
	 */
	public GNSObjectMappedObjectValue(Object aObjectValue) {
		super();
		this.pPrivateValue = aObjectValue;
	}
	/**
	 * 
	 * @param aStringValue
	 */
	public GNSObjectMappedObjectValue(String aStringValue) {
		super();
		this.pPrivateValue = aStringValue;
	}
	/**
	 * 
	 * @param aIntegerValue
	 */
	public GNSObjectMappedObjectValue(Integer aIntegerValue) {
		super();
		pPrivateValue = aIntegerValue;
	}
	/**
	 * 
	 * @param aDoubleValue
	 */
	public GNSObjectMappedObjectValue(Double aDoubleValue) {
		super();
		pPrivateValue = aDoubleValue;
	}
	/**
	 * 
	 * @param aBooleanValue
	 */
	public GNSObjectMappedObjectValue(Boolean aBooleanValue) {
		super();
		pPrivateValue = aBooleanValue;
	}
	

	/**
	 * @return false if not a String or Double or Integer
	 */
	@Override
	public Boolean isComplexObject() {
		return !((this.pPrivateValue == null) || (this.pPrivateValue instanceof String)
				|| (this.pPrivateValue instanceof Integer) || (this.pPrivateValue instanceof Double));
	}

	@Override
	public Object getObjectValue() {

		return this.pPrivateValue;
	}

	@Override
	public void setObjectValue(Object aObjectValue) {
		this.pPrivateValue = aObjectValue;
	}

	@Override
	public Boolean getBoolean() {
		if (isComplexObject()) {
			throw new GNSObjectRuntimeException(String.format(sWarningComplexType, pPrivateValue.getClass()));
		}

		return ((Boolean) this.pPrivateValue).booleanValue();

	}

	@Override
	public void setBoolean(Boolean aBooleanValue) {
		this.pPrivateValue = aBooleanValue;
	}

	@Override
	public Integer getInteger() {

		if (isComplexObject()) {
			throw new GNSObjectRuntimeException(String.format(sWarningComplexType, pPrivateValue.getClass()));
		}

		return ((Integer) this.pPrivateValue).intValue();
	}

	@Override
	public void setInteger(Integer aIntegerValue) {
		this.pPrivateValue = aIntegerValue;
	}

	@Override
	public Double getDouble() {

		if (isComplexObject()) {
			throw new GNSObjectRuntimeException(String.format(sWarningComplexType, pPrivateValue.getClass()));
		}

		return ((Double) this.pPrivateValue).doubleValue();
	}

	@Override
	public void setDouble(Double aDoubleValue) {
		this.pPrivateValue = aDoubleValue;

	}

	@Override
	public String getString() {

		if (isComplexObject()) {
			throw new GNSObjectRuntimeException(String.format(sWarningComplexType, pPrivateValue.getClass()));
		}

		return String.valueOf(this.pPrivateValue);
	}

	@Override
	public void setString(String aStringValue) {
		this.pPrivateValue = aStringValue;

	}

	@Override
	public Object serialize() {

		return null;
	}

	@Override
	public Object unserialize() {
		return null;
	}

}
