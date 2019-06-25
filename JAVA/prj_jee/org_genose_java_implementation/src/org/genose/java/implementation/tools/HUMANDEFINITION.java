/**
 * 
 */
package org.genose.java.implementation.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 59013-36-18
 *
 */
public enum HUMANDEFINITION {

	HOMME(1, "M", "MASCULIN", "MONSIEUR", "GARCON"), FEMME(2, "F", "FEMININ", "MADAME", "FILLE"),
	UNBINARY999(999, "P", "POLYTETRAHEDRE", "POLYPHASED", "POLY");

	static final  String REPRESENTATION_ABBREGED_ENUM = "typeAbbregedEnum";
	static final String REPRESENTATION_ABBREGED_STRING = "typeAbbreged";
	static final String REPRESENTATION_ADMINISTRATIVE_NAME = "typeAdministrativeName";
	static final String REPRESENTATION_ADMINISTRATIVE_STYLE = "typeAdministrativeStyle";
	static final String REPRESENTATION_ADMINISTRATIVE_SEX = "typeAdministrativeSex";
	static final String REPRESENTATION_ADMINISTRATIVE_AGE = "typeAdministrativeAge";

	private Map<Object, Object> enumDefMap = new HashMap<>();
	private static Map<Object, Object> registredDefMap = new HashMap<>();

	private HUMANDEFINITION(int typeAbbregedEnum, String typeAbbreged, String typeAdministrativeName,
			String typeAdministrativeStyle, String typeAdministrativeSex) {

		enumDefMap.put(REPRESENTATION_ABBREGED_ENUM, typeAbbregedEnum);
		enumDefMap.put(REPRESENTATION_ABBREGED_STRING, typeAbbreged);
		enumDefMap.put(REPRESENTATION_ADMINISTRATIVE_NAME, typeAdministrativeName);
		enumDefMap.put(REPRESENTATION_ADMINISTRATIVE_STYLE, typeAdministrativeStyle);
		// enumDefMap.put(REPRESENTATION_ADMINISTRATIVE_AGE, typeAdministrativeAge);
		enumDefMap.put(REPRESENTATION_ADMINISTRATIVE_SEX, typeAdministrativeSex);
	}

	/**
	 * 
	 */
	static {
		for (HUMANDEFINITION definitiontype : HUMANDEFINITION.values()) {
			registredDefMap.put(definitiontype.hashCode(), definitiontype);
		}
	}

	/* ************************************************************* */
	static public Boolean contains(Integer iIDRequested) {

		for (HUMANDEFINITION eObjectVal : HUMANDEFINITION.values()) {
			if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ABBREGED_ENUM))
					.compareToIgnoreCase(String.valueOf(iIDRequested)) == 0)
				return true;
		}

		return false;
	}

	/* ************************************************************* */
	static public Boolean contains(String sNameRequested) {

		for (HUMANDEFINITION eObjectVal : HUMANDEFINITION.values()) {

			if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ABBREGED_ENUM))
					.compareToIgnoreCase(sNameRequested) == 0)
				return true;
			if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ABBREGED_STRING))
					.compareToIgnoreCase(sNameRequested) == 0)
				return true;
			if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ADMINISTRATIVE_NAME))
					.compareToIgnoreCase(sNameRequested) == 0)
				return true;
			if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ADMINISTRATIVE_STYLE))
					.compareToIgnoreCase(sNameRequested) == 0)
				return true;
			if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ADMINISTRATIVE_SEX))
					.compareToIgnoreCase(sNameRequested) == 0)
				return true;

		}
		return false;
	}

	/* ************************************************************* */
	/**
	 * Whatever you looking for, we you the soluce value !!
	 * @param String eObjectValCode ; else Cast your argument Integer with String.valueOf( myIntegerArgument )
	 * @return
	 */
	public Enum<HUMANDEFINITION> getEnumByString(String eObjectValCode) {
		for (HUMANDEFINITION eObjectVal : HUMANDEFINITION.values()) {

			if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ABBREGED_ENUM))
					.compareToIgnoreCase(eObjectValCode) == 0)
				return eObjectVal;
			if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ABBREGED_STRING))
					.compareToIgnoreCase(eObjectValCode) == 0)
				return eObjectVal;
			if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ADMINISTRATIVE_NAME))
					.compareToIgnoreCase(eObjectValCode) == 0)
				return eObjectVal;
			if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ADMINISTRATIVE_STYLE))
					.compareToIgnoreCase(eObjectValCode) == 0)
				return eObjectVal;
			if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ADMINISTRATIVE_SEX))
					.compareToIgnoreCase(eObjectValCode) == 0)
				return eObjectVal;
		}
		return null;
	}
	/**
	 * 
	 * @return
	 */
	public String getValue(String sKeyValue) {
			/* ************************************ */
			if(sKeyValue == null) return null;
			/* ************************************ */
			if(String.valueOf(sKeyValue).isEmpty()) return null;
			/* ************************************ */
			if (String.valueOf(enumDefMap.get(sKeyValue))
					.compareToIgnoreCase(REPRESENTATION_ABBREGED_ENUM) == 0)
				return String.valueOf(enumDefMap.get(REPRESENTATION_ABBREGED_ENUM));
			/* ************************************ */
			if (String.valueOf(sKeyValue)
					.compareToIgnoreCase(REPRESENTATION_ABBREGED_STRING) == 0)
				return String.valueOf(enumDefMap.get(REPRESENTATION_ABBREGED_STRING));
			/* ************************************ */
			if (String.valueOf(sKeyValue)
					.compareToIgnoreCase(REPRESENTATION_ABBREGED_ENUM) == 0)
				return String.valueOf(enumDefMap.get(REPRESENTATION_ABBREGED_ENUM));
			/* ************************************ */
			if (String.valueOf(sKeyValue)
					.compareToIgnoreCase(REPRESENTATION_ADMINISTRATIVE_STYLE) == 0)
				return String.valueOf(enumDefMap.get(REPRESENTATION_ADMINISTRATIVE_STYLE));
			/* ************************************ */
			if (String.valueOf(sKeyValue)
					.compareToIgnoreCase(REPRESENTATION_ADMINISTRATIVE_SEX) == 0)
				return String.valueOf(enumDefMap.get(REPRESENTATION_ADMINISTRATIVE_SEX));
			/* ************************************ */
		return null;
	}
}
