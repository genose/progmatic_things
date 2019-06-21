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

	
	HOMME(1, "M", "MASCULIN", "MONSIEUR", "GARCON",HUMANDEFINITION_AG),
	FEMME(2, "F", "FEMININ", "MADAME", "FILLE" ),
	UNBINARY999(999, "P", "POLYTETRAHEDRE", "POLYPHASED", "POLY");

	final static String REPRESENTATION_ABBREGED_ENUM = "typeAbbregedEnum";
	final static String REPRESENTATION_ABBREGED_STRING = "typeAbbreged";
	final static String REPRESENTATION_ADMINISTRATIVE_NAME = "typeAdministrativeName";
	final static String REPRESENTATION_ADMINISTRATIVE_STYLE = "typeAdministrativeStyle";
	final static String REPRESENTATION_ADMINISTRATIVE_SEX = "typeAdministrativeSex";
	final static String REPRESENTATION_ADMINISTRATIVE_AGE = "typeAdministrativeAge";

	
	private Map<Object, Object> enumDefMap = new HashMap<>();
	private static Map<Object, Object> registredDefMap = new HashMap<>();

	private HUMANDEFINITION(int typeAbbregedEnum, String typeAbbreged, String typeAdministrativeName, String typeAdministrativeStyle, String typeAdministrativeSex, Enum<HUMANDEFINITION_AGE> typeAdministrativeAge) {

		enumDefMap.put(REPRESENTATION_ABBREGED_ENUM, typeAbbregedEnum);
		enumDefMap.put(REPRESENTATION_ABBREGED_STRING, typeAbbreged);
		enumDefMap.put(REPRESENTATION_ADMINISTRATIVE_NAME, typeAdministrativeName);
		enumDefMap.put(REPRESENTATION_ADMINISTRATIVE_STYLE, typeAdministrativeStyle);
		enumDefMap.put(REPRESENTATION_ADMINISTRATIVE_AGE, typeAdministrativeAge);
		enumDefMap.put(REPRESENTATION_ADMINISTRATIVE_SEX, typeAdministrativeSex);
	}

}
