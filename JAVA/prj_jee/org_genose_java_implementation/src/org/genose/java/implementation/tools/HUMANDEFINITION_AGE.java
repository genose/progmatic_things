/**
 * 
 */
package org.genose.java.implementation.tools;

import org.genose.java.implementation.tools.NumericRange;
import org.genose.java.implementation.tools.StringRange;
/**
 * @author 59013-36-18
 *
 */
public enum HUMANDEFINITION_AGE {

	

	 AGE_BABY( new StringRange("BEBE", "POUPON"), new NumericRange(0,1)),
	AGE_CHILD( new StringRange("ENFANT"), new NumericRange(1,14)),
	AGE_ADOLESCENT(new StringRange("ADOLESCENT"), new NumericRange( 14, 18)),
	AGE_ADULT(new StringRange("ADULTE"),  new NumericRange(18,60)),
	AGE_SENIOR(new StringRange("SENIOR"), new NumericRange(60,160));
	
	private NumericRange eAgeRange = new NumericRange(0,0);
	private StringRange eStringRange = new StringRange();
	
	private HUMANDEFINITION_AGE(StringRange aTypeNameRange, NumericRange aAgeRange) {
		eAgeRange = aAgeRange;
		eStringRange = aTypeNameRange;
	}

	/**
	 * @return the eAgeRange
	 */
	public NumericRange getAgeRange() {
		return eAgeRange;
	}
	/**
	 * @return the eStringRange
	 */
	public StringRange getStringRange() {
		return eStringRange;
	}

}
