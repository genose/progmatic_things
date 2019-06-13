/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.util.Map;
import java.util.Objects;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationObjectValue implements JFXApplicationValuesAccessor {
	private Object pPrivateValue = null;
	
	/**
	 * 
	 */
	public JFXApplicationObjectValue() {
		super();
	}

	@Override
	public Boolean isComplexObject() {
		if(this.pPrivateValue instanceof Map<?, ?>) {
			return true;
		}
		return false;
	}

	@Override
	public Object getObjectValue() {
		
		return pPrivateValue;
	}

	@Override
	public void setObjectValue(Object aObjectValue) {
		this.pPrivateValue =  aObjectValue;
	}

	@Override
	public Boolean getBoolean() {
		if(isComplexObject()) {
			throw new JFXApplicationRuntimeException("This Type ("+getObjectValue()+") is ");
		}else {
			return ((Boolean)this.pPrivateValue).booleanValue();
		}
	}

	@Override
	public void setBoolean(Boolean aBooleanValue) {
		this.pPrivateValue = aBooleanValue;
	}

	@Override
	public Integer getInteger() {
		return ((Integer)this.pPrivateValue).intValue();
	}

	@Override
	public void setInteger(Integer aIntegerValue) {
		this.pPrivateValue = aIntegerValue;
	}

	@Override
	public Double getDouble() {
		return ((Double)this.pPrivateValue).doubleValue();
	}

	@Override
	public void setDouble(Double aDoubleValue) {
		this.pPrivateValue = aDoubleValue;
		
	}

	@Override
	public String getString() {
		
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
