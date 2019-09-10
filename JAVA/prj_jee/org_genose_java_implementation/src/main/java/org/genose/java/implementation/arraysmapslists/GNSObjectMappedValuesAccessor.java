package org.genose.java.implementation.arraysmapslists;

public interface GNSObjectMappedValuesAccessor {

	final static String sWarningComplexType = " ERROR : Can't fetch value because This Type (%s) is a Complex Type (Map, Array, or List ...) "; 
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
