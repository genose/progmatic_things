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
    private int low;
    private int high;

    public NumericRange(int low, int high){
        this.low = low;
        this.high = high;
    }

    public boolean contains(int number){
        return (number >= low && number <= high);
    }
};
