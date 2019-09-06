/**
 * 
 */
package org.genose.java.implementation.tools;

/**
 * @author 59013-36-18
 *
 */
public class NumericRange {
	private Double low;
	private Double high;

	public NumericRange(Double low, Double high) {
		this.low = low;
		this.high = high;
	}

	public NumericRange(int low, int high) {
		this.low = Double.valueOf(low);
		this.high = Double.valueOf(high);
	}

	public Double min()
	{
		return this.low;
	}
	
	public void setMin(Double dMinValue)
	{
		this.low = dMinValue;
	}
	
	public Double max()
	{
		return this.high;
	}
	
	public void setMax(Double dMaxValue)
	{
		this.high = dMaxValue;
	}
	
	public boolean contains(Integer number) {
		return (Double.valueOf(number).compareTo(low) >= 0) && (Double.valueOf(number).compareTo(high) <= 0);
	}
	
	public boolean contains(Double number) {
		return (number.compareTo(low) >= 0) && (number.compareTo(high) <= 0);
	}


	public Boolean isEmpty() {
		return (low == 0) && (high == 0);
		
	}
}