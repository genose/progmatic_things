package org.genose.java.implementation.javafx.applicationtools;

public abstract interface JFXApplicationValuesAccessor {

	public Boolean isComplexObject();
	
	public Object getObjectValue();
	public void setObjectValue(Object aObjectValue );
	
	public Boolean getBoolean();
	public void setBoolean(Boolean aBooleanValue);
	
	public Integer getInteger();
	public void setInteger(Integer aIntegerValue);
	
	public Double getDouble();
	public void setDouble(Double aDoubleValue);
	
	public String getString();
	public void setString(String aStringValue);
	 
	public Object serialize();
	public Object unserialize();
	
	
	
}
