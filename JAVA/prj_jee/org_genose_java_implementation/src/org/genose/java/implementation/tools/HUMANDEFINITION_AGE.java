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
// :: https://www.statcan.gc.ca/fra/concepts/definitions/age2
	AGE_BABY(new StringRange("BEBE", "POUPON"), new NumericRange(0.0, 0.11)),
	AGE_CHILD(new StringRange("ENFANT"), new NumericRange(1, 14)),
	AGE_ADOLESCENT(new StringRange("ADOLESCENT"), new NumericRange(14, 18)),
	AGE_ADULT(new StringRange("ADULTE"), new NumericRange(18, 60)),
	AGE_SENIOR(new StringRange("SENIOR"), new NumericRange(60, 160)),
	AGE_TALESOFTHEFUTUREPAST(new StringRange("HISTORY"), new NumericRange(-1.8e5, 0.0)),
	AGE_DINAUSAUR(new StringRange("DINOSAUR"), new NumericRange(-6e5, -1.8e5)),
	AGE_OMEGAPHY(new StringRange("DEUSEXDOMINUM"), new NumericRange(-10e10, 10e10));

	private NumericRange eAgeRange = new NumericRange(0, 0);
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

	/* ************************************************************* */
	/**
	 * 
	 * @param eObjectValCode
	 * @return
	 */
	public Enum<HUMANDEFINITION_AGE> getEnumByString(String eObjectValCode) {

		for (HUMANDEFINITION_AGE eObjectVal : HUMANDEFINITION_AGE.values()) {

			if (eObjectVal.eStringRange.containsKey(eObjectValCode))
				return eObjectVal;

			try {
				// find transformation ...
				Double dObjectValueFind = Double.valueOf(eObjectValCode);
				dObjectValueFind = transformDoubleToAge(dObjectValueFind);
				if (eObjectVal.eAgeRange.contains(dObjectValueFind))
					return eObjectVal;
			} catch (Exception e) {

			}

		}

		return getEnumByString(String.valueOf(eObjectValCode));
	}

	/* ************************************************************* */
	/**
	 * 
	 * @param eObjectValCode
	 * @return
	 */
	public Enum<HUMANDEFINITION_AGE> getEnumByAge(Double eObjectValCode) {

		eObjectValCode = transformDoubleToAge(eObjectValCode);

		for (HUMANDEFINITION_AGE eObjectVal : HUMANDEFINITION_AGE.values()) {

			if ((!eObjectVal.name().equals(AGE_TALESOFTHEFUTUREPAST.name()))
					&& (!eObjectVal.name().equals(AGE_DINAUSAUR.name()))
					&& (!eObjectVal.name().equals(AGE_OMEGAPHY.name()))
					&& (eObjectVal.eAgeRange.contains(eObjectValCode))) {
				return eObjectVal;
			}

		}
		// only energy matters --
		return AGE_OMEGAPHY;
	}

	/**
	 * 
	 * @param eObjectValCode
	 * @return
	 */
	public Double transformDoubleToAge(Double eObjectValCode) {
		// checkup overflowed value ... can t be 1.505 ... so it wil be Round(0.505 /
		// 0,12)
		Integer iOrdonaryValue = eObjectValCode.intValue();
		Double dExposantValue = (eObjectValCode - iOrdonaryValue);
		// got more than a year ... so let s parse
		if (dExposantValue >= 0.11) {

			dExposantValue = Double.valueOf((dExposantValue / 0.12));

			eObjectValCode = Double.valueOf((iOrdonaryValue.intValue() + dExposantValue.intValue()));
		}
		return eObjectValCode;
	}

}
