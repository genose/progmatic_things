/**
 * 
 */
package org.genose.java.implementation;

import java.lang.reflect.Method;

/**
 * @author 59013-36-18
 *
 */
public class GNSJavaClassHelper {

	/**
	 * 
	 */
	public GNSJavaClassHelper() {
		throw new UnsupportedOperationException("Currently not to be instanciate ...");
	}

	/**
	 * 
	 * @param aClassToTest
	 * @param aMethodName
	 * @return true when method is found
	 */
	public static Boolean respondsTo( String aMethodName, Object aObjectToTest) {
		Method methodToFind = null;
		Class<?> aClassToTest = aObjectToTest.getClass();
		try {
			// *******************************************************
			methodToFind = aClassToTest.getMethod(aMethodName, (Class<?>[]) null);
		} catch (NoSuchMethodException | SecurityException evERRREFLECT) {
			try {
				// *******************************************************
				Method[] aDeclaredMethod = aClassToTest.getDeclaredMethods();
				// *******************************************************
				for (int i = 0; i < aDeclaredMethod.length; i++) {
					if (String.valueOf(aDeclaredMethod[i]).compareToIgnoreCase(aMethodName) == 0) {
						return true;
					}
				}

			} catch (SecurityException e) {
				return false;
			}
		}
		return (methodToFind == null);
	}
}
