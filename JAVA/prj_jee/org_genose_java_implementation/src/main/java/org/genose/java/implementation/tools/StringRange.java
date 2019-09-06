/**
 * 
 */
package org.genose.java.implementation.tools;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author 59013-36-18
 *
 */
public class StringRange extends HashMap<String, String> {

	/**
	 * 
	 */
	public StringRange(String... sStringsArgs) {
		super();
		for (String aStrArg : sStringsArgs) {
			put(aStrArg.toLowerCase(), aStrArg);
		}
	}

	@Override
	public String get(Object aKeyArg) {
		if(aKeyArg == null) {
			throw new RuntimeException(getClass()+" : key argument cannot be null ...");
		}
		
		if (
				!(aKeyArg instanceof String) ||
				!(aKeyArg instanceof Double) ||
				!(aKeyArg instanceof Integer) ||
				!(aKeyArg instanceof Float)
				) {
			throw new RuntimeException(getClass()+" : key argument cannot be non String Class compliant Descendant ...");
		}
		return super.get(String.valueOf(aKeyArg).toLowerCase());
	}
}
