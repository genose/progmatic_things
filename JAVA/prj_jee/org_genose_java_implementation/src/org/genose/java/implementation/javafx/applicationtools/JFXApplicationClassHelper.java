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
	public static Boolean respondsTo(Object aObjectToTest, String aMethodName) {
		Method methodToFind = null;
		Class<?> aClassToTest = aObjectToTest.getClass();
		try {
			// *******************************************************
			methodToFind = aClassToTest.getMethod(aMethodName, (Class<?>[]) null);
		} catch (NoSuchMethodException | SecurityException evERRREFLECT) {
			try {
				// *******************************************************
				Method[] aDeclaredMethod = aClassToTest.getMethods();
				// *******************************************************
				for (int i = 0; i < aDeclaredMethod.length; i++) {
					int iStringCompare = String.valueOf(aDeclaredMethod[i]).compareToIgnoreCase(aMethodName);
					if (iStringCompare == 0) {
						return true;
					}
				}

			} catch (Exception evERRRESPONDSTO) {
				JFXApplicationLogger.getLogger().logError(JFXApplicationClassHelper.class, evERRRESPONDSTO);
				return false;
			}
		}
		return (methodToFind != null);
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
			Method[] aMethodList = aObjectToIntrospect.getClass().getMethods();
			List<Method> aArrayMethodList = Arrays.asList(aMethodList);
			Object aObjectToIntrospectSuperClass = aObjectToIntrospect.getClass().getSuperclass();
			Class aclassSuper = aObjectToIntrospectSuperClass.getClass();
			Method[] aMethodListSuperClass = aObjectToIntrospectSuperClass.getClass().getMethods();
			List<Method> aArrayMethodListSuperClass = Arrays.asList(aMethodListSuperClass);

			Boolean bIsEnclosedOrOverriten = (aObjectToIntrospect.getClass().getEnclosingClass() != null)
					&& (aObjectToIntrospect.getClass().getEnclosingMethod() != null)
					&& (aObjectToIntrospect.getClass().getEnclosingClass() != null);

			String sClassInfos = String.format(
					"*********************************************** " + "%n Should Invoke : %s " + "%n Class : %s %n "
							+ "Got Method count :   %s " + "%n IsEnclosed / Anonymous : %s " + "%n Super Class : %s"
							+ "%n Super Got Method count :   %s " + "%n IsEnclosed / Anonymous : %s "
							+ "%n *********************************************** %n",
					sInvokeMethodName, aObjectToIntrospect.toString(), String.valueOf(aMethodList.length),
					aObjectToIntrospect.getClass().getEnclosingClass() + " :: "
							+ aObjectToIntrospect.getClass().getEnclosingMethod() + " :: "
							+ aObjectToIntrospect.getClass().getEnclosingClass(),

					aObjectToIntrospectSuperClass, String.valueOf(aMethodListSuperClass.length),
					aObjectToIntrospectSuperClass.getClass().getEnclosingClass() + " :: "
							+ aObjectToIntrospectSuperClass.getClass().getEnclosingMethod() + " :: "
							+ aObjectToIntrospectSuperClass.getClass().getEnclosingClass());
			// sonarlint says extract to method ... i say NO !!
			try {
				Method aclassmethod = aObjectToIntrospect.getClass().getMethod(sInvokeMethodName);
				if (aclassmethod != null) {
					Class<?> aReturnType = aclassmethod.getReturnType();
					if (aclassmethod.getReturnType() != null) {
						JFXApplicationLogger.getLogger().logInfo("[Object getMethod] " + sInvokeMethodName
								+ " Return Method Type : " + aReturnType.toGenericString());
						return aclassmethod.invoke(aObjectToIntrospect);
					} else {
						JFXApplicationLogger.getLogger()
								.logInfo("[Object getMethod] " + sInvokeMethodName + " Return Method Type : is NULL ");
						aclassmethod.invoke(aObjectToIntrospect);
						return null;
					}
				}

			} catch (Exception throwedEvent) {
				JFXApplicationLogger.getLogger().logError(JFXApplicationHelper.class, throwedEvent,
						"Can t find [Object Method[" + sInvokeMethodName + "]] ... Retry with introspect ...");
			}
			// Following should rarely be reached ...
			// it find by Insensitive case methods name ...
			JFXApplicationLogger.getLogger().logInfo(sClassInfos);
			/* ********************************** */
			for (Method mMethodFound : aArrayMethodList) {

				int iCompreState = mMethodFound.getName().toLowerCase().compareToIgnoreCase(sInvokeMethodName);
				if (iCompreState == 0) {
					JFXApplicationLogger.getLogger().logInfo(
							"Class : " + aObjectToIntrospect.toString() + "%n Got Method : " + mMethodFound.getName());
					Class<?> aReturnType = mMethodFound.getReturnType();
					if (mMethodFound.getReturnType() != null) {
						JFXApplicationLogger.getLogger().logInfo("[Object Introspect] " + sInvokeMethodName
								+ " Return Method Type : " + aReturnType.toGenericString());
						return mMethodFound.invoke(aObjectToIntrospect);
					} else {
						JFXApplicationLogger.getLogger()
								.logInfo("[Object Introspect] " + sInvokeMethodName + " Return Method Type : is NULL ");
						mMethodFound.invoke(aObjectToIntrospect);
						return null;
					}

				}
			}
			/* ********************************** */
			if (bIsEnclosedOrOverriten) {
				for (Method mMethodFound : aMethodListSuperClass) {

					int iCompreState = mMethodFound.getName().toLowerCase().compareToIgnoreCase(sInvokeMethodName);
					if (iCompreState == 0) {
						JFXApplicationLogger.getLogger().logInfo("%n Super : " + aObjectToIntrospectSuperClass
								+ "%n Got Method : " + mMethodFound.getName());

						Class<?> aReturnType = mMethodFound.getReturnType();
						if (mMethodFound.getReturnType() != null) {
							JFXApplicationLogger.getLogger().logInfo("[Object.Super Introspect] " + sInvokeMethodName
									+ " Return Method Type : " + aReturnType.toGenericString());
							return mMethodFound.invoke(aObjectToIntrospect);
						} else {
							JFXApplicationLogger.getLogger().logInfo("[Object.Super Introspect] " + sInvokeMethodName
									+ " Return Method Type : is NULL ");
							mMethodFound.invoke(aObjectToIntrospect);
							return null;
						}

					}
				}
			}

		} catch (Exception evERRREFLECT) {
			JFXApplicationException.raiseToFront(JFXApplicationClassHelper.class, evERRREFLECT, false);

		}

		String sObjectIntrospectedClassName = "[NULL CLASS]";
		sObjectIntrospectedClassName = aObjectToIntrospect.getClass().getName();

		throw new JFXApplicationRuntimeException(String.format("(%s) Does not respond to (%s)  ",
				String.valueOf(sObjectIntrospectedClassName), sInvokeMethodName));

	}

}
