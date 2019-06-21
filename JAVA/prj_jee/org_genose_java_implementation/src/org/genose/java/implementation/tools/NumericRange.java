/**
 * 
 */
package org.genose.java.implementation.tools;

/**
 * @author 59013-36-18
 *
 */
public class NumericRange
{
    private Double low;
    private Double high;

    public NumericRange(Double low, Double high){
        this.low = low;
        this.high = high;
    }
  public NumericRange(int low, int high){
        this.low = Double.valueOf(low);
        this.high = Double.valueOf(high);
    }
    public boolean contains(Double number){
    	return (number.compareTo(low) >= 0) && (number.compareTo(high) <= 0) ; 
    }
}