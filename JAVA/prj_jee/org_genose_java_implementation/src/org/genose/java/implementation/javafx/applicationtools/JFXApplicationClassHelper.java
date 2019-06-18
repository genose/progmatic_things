/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationRuntimeException;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationClassHelper {

	/**
	 * 
	 */
	public JFXApplicationClassHelper() {
		throw new UnsupportedOperationException("Currently not to be instanciate ...");
	}

	/**
	 * 
	 * @param aClassToTest
	 * @param aMethodName
	 * @return true when method is found
	 */
	public static Boolean respondsTo(String aMethodName, Object aObjectToTest) {
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

	/**
	 * Find a method on generic object and invoke it
	 * 
	 * @param aObjectToIntrospect
	 * @param sInvokeMethodName
	 * @return Object return type(may be null or false or integer) or null if void
	 *         return type
	 */
	public static Object invokeMethod(Object aObjectToIntrospect, String sInvokeMethodName) {

		if ((aObjectToIntrospect == null) || (sInvokeMethodName == null)
				|| (String.valueOf(sInvokeMethodName).length() < 2)) {
			String sMessageToRaise = String.format("Can't call unnamed method (%s) on Object (%s)  ", sInvokeMethodName,
					((aObjectToIntrospect != null) ? aObjectToIntrospect.getClass() : "[NULL OBJECT]"));
			JFXApplicationException aThroweableException = new JFXApplicationException(sMessageToRaise, null,
					JFXApplicationHelper.getStackTrace());
			JFXApplicationException.raiseToFront(JFXApplicationHelper.class, aThroweableException, false);
			return null;
		}

		try {
			Method[] aMethodList = aObjectToIntrospect.getClass().getDeclaredMethods();
			List<Method> aArrayMethodList = Arrays.asList(aMethodList);

			JFXApplicationLogger.getLogger().logInfo(aObjectToIntrospect.getClass().getName() + " Got Method count : "
					+ String.valueOf(aMethodList.length));
			for (Method mMethodFound : aArrayMethodList) {
				JFXApplicationLogger.getLogger().logInfo(" Got Method : " + mMethodFound.getName());
				if (mMethodFound.getName().toLowerCase().compareToIgnoreCase(sInvokeMethodName) == 0) {
					try {

						Class<?> aReturnType = mMethodFound.getReturnType();
						if (mMethodFound.getReturnType() != null) {
							JFXApplicationLogger.getLogger()
									.logInfo(" Return Method Type : " + aReturnType.toGenericString());
							return mMethodFound.invoke(aObjectToIntrospect);
						} else {
							JFXApplicationLogger.getLogger().logInfo(" Return Method Type : is NULL ");
							mMethodFound.invoke(aObjectToIntrospect);
							return null;
						}

					} catch (IllegalAccessException | IllegalArgumentException
							| InvocationTargetException aThroweableException) {
						JFXApplicationException.raiseToFront(JFXApplicationClassHelper.class, aThroweableException,
								false);
					}

					return null;
				}
			}
		} catch (SecurityException evERRREFLECT) {
			JFXApplicationException.raiseToFront(JFXApplicationClassHelper.class, evERRREFLECT, false);

		}
		
		String sObjectIntrospectedClassName = "[NULL CLASS]";
		sObjectIntrospectedClassName = aObjectToIntrospect.getClass().getName();
	 

		throw new JFXApplicationRuntimeException(
				String.format("(%s) Does not respond to (%s)  ", String.valueOf(sObjectIntrospectedClassName), sInvokeMethodName));

	}

}
