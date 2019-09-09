/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationRuntimeException;
import org.genose.java.implementation.streams.GNSObjectMappedLogger;

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
		Class<?> aClassToTestSuperclass = aObjectToTest.getClass().getSuperclass();
		boolean bMethodCanReachObject = false;
		/*
		 * try { // ******************************************************* methodToFind
		 * = aClassToTest.getMethod(aMethodName, (Class<?>[]) null);
		 * 
		 * } catch (NoSuchMethodException | SecurityException evERRREFLECT) {
		 */
		try {
			// *******************************************************
			Method[] aDeclaredMethod = aClassToTest.getMethods();
			
			// *******************************************************
			for (int i = 0; i < aDeclaredMethod.length; i++) {
				String sCurrentFoundMethod = aDeclaredMethod[i].getName();
				int iStringCompare = String.valueOf(sCurrentFoundMethod).compareToIgnoreCase(aMethodName);
				if (iStringCompare == 0) {
					bMethodCanReachObject =  aDeclaredMethod[i].canAccess(aObjectToTest);
				//	JFXApplicationLogger.getLogger().logInfo("[Object ("+aObjectToTest+")%n findind ("+aMethodName+") %nrespondsTo Got list %n(" + Arrays.toString(aDeclaredMethod).replaceAll(",", "\n") + ")]");
					
					
				//	JFXApplicationLogger.getLogger().logInfo("[Object  ("+aObjectToTest+") %n respondsTo (" + aMethodName + ")] %n canAccess : "+bMethodCanReachObject);
					
					if (!bMethodCanReachObject) {
						GNSObjectMappedLogger.getLogger().logError(JFXApplicationClassHelper.class,
								"This context Can t reach method : " + aMethodName+"%n on Object : "+aObjectToTest+"%n class : "+aClassToTest+"%n super : "+aClassToTestSuperclass);
					}
					
					return bMethodCanReachObject;
				}
			}

		} catch (Exception evERRRESPONDSTO) {
			GNSObjectMappedLogger.getLogger().logError(JFXApplicationClassHelper.class, evERRRESPONDSTO);
			return false;
		}
		/* } */
		if (methodToFind != null)
			bMethodCanReachObject = methodToFind.canAccess(aClassToTest);
		if (!bMethodCanReachObject) {
			GNSObjectMappedLogger.getLogger().logError(JFXApplicationClassHelper.class,
					"This context Can t reach method : " + aMethodName);
			return false;
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

		boolean aMethodCanReachObject = false;
		String sEnclosureType = "";

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
			sEnclosureType = (bIsEnclosedOrOverriten) ? "Enclosed Object" : "";
			String sClassInfos = String.format(
					"*********************************************** " + "%n Should Invoke : %s " + "%n Class : %s "
							+ "%n Got Method count :   %s " + "%n IsEnclosed / Anonymous : %b %n %s %n   "
							+ "%n Super Class : %s" + "%n Super Got Method count :   %s "
							+ "%n IsEnclosed / Anonymous : %b %n %s "
							+ "%n *********************************************** %n",
					sInvokeMethodName, aObjectToIntrospect.toString(), String.valueOf(aMethodList.length),
					bIsEnclosedOrOverriten,
					String.format(
							" ****************************************** %n  enclosed by class : %s %n enclosed in method : %s %n ******************************************",
							aObjectToIntrospect.getClass().getEnclosingClass(),
							aObjectToIntrospect.getClass().getEnclosingMethod()),

					aObjectToIntrospectSuperClass, String.valueOf(aMethodListSuperClass.length), false,
					String.format(
							" ****************************************** %n  enclosed by class : %s %n enclosed in method : %s %n ******************************************",
							aObjectToIntrospectSuperClass.getClass().getEnclosingClass(),
							aObjectToIntrospectSuperClass.getClass().getEnclosingMethod())

			);
			// JFXApplicationLogger.getLogger().logInfo(sClassInfos);
			// sonarlint says extract to method ... i say NO !!
			try {
				Method aclassmethod = aObjectToIntrospect.getClass().getMethod(sInvokeMethodName);
				if (aclassmethod != null) {
					Class<?> aReturnType = aclassmethod.getReturnType();
					aMethodCanReachObject = aclassmethod.canAccess(aObjectToIntrospect);
					if (aMethodCanReachObject) {
						if (aReturnType != null && (aReturnType.toGenericString().equalsIgnoreCase("void"))) {
							GNSObjectMappedLogger.getLogger()
									.logInfo("[Object getMethod (" + sEnclosureType + ":"
											+ (aObjectToIntrospect.getClass()) + ")] " + sInvokeMethodName
											+ " Return Method Type :  void :: " + aReturnType.toGenericString());
							aclassmethod.invoke(aObjectToIntrospect);
							return null;
						} else if (aReturnType != null) {
							GNSObjectMappedLogger.getLogger()
									.logInfo("[Object getMethod (" + sEnclosureType + ":"
											+ (aObjectToIntrospect.getClass()) + ")] " + sInvokeMethodName
											+ "%n Return Method Type : " + aReturnType.toGenericString());
							return aclassmethod.invoke(aObjectToIntrospect);
						} else {
							GNSObjectMappedLogger.getLogger()
									.logInfo("[Object getMethod (" + sEnclosureType + ":"
											+ (aObjectToIntrospect.getClass()) + ")] " + sInvokeMethodName
											+ "%n Return Method Type : is NULL ");
							aclassmethod.invoke(aObjectToIntrospect);
							return null;
						}
					} else {
						GNSObjectMappedLogger.getLogger().logError(JFXApplicationClassHelper.class,
								"Unreacheable Method Call (" + aclassmethod.getName() + ")");
						return null;
					}
				}

			} catch (Exception throwedEvent) {
				/* JFXApplicationLogger.getLogger().logError(JFXApplicationHelper.class, throwedEvent,
						" WARNING : Can t find [" + sEnclosureType + ":" + "Object Method[" + sInvokeMethodName
								+ "]] ... Retry with introspect ..."); */
			}

			// Following should rarely be reached ...
			// it find by Insensitive case methods name ...

			/* ********************************** */
			for (Method mMethodFound : aArrayMethodList) {

				int iCompreState = mMethodFound.getName().toLowerCase().compareToIgnoreCase(sInvokeMethodName);
				if (iCompreState == 0) {
					GNSObjectMappedLogger.getLogger().logInfo(
							"Class : " + aObjectToIntrospect.toString() + "%n Got Method : " + mMethodFound.getName());
					Class<?> aReturnType = mMethodFound.getReturnType();
					aMethodCanReachObject = mMethodFound.canAccess(aObjectToIntrospect);
					GNSObjectMappedLogger.getLogger()
							.logInfo(String.format("[Object Introspect] canreach object : %b", aMethodCanReachObject));

					if (!aMethodCanReachObject) {
						throw new JFXApplicationException("Unreacheable Method Call (" + mMethodFound.getName() + ")");
					}

					if (aReturnType != null && aReturnType.toGenericString().equalsIgnoreCase("void")) {
						GNSObjectMappedLogger.getLogger().logInfo("[Object Introspect] " + sInvokeMethodName
								+ " Return Method Type : " + aReturnType.toGenericString());
						mMethodFound.invoke(aObjectToIntrospect);
						return null;
					} else if (aReturnType != null) {
						GNSObjectMappedLogger.getLogger().logInfo("[Object Introspect] " + sInvokeMethodName
								+ " Return Method Type : " + aReturnType.toGenericString());
						return mMethodFound.invoke(aObjectToIntrospect);
					} else {
						GNSObjectMappedLogger.getLogger()
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
						GNSObjectMappedLogger.getLogger().logInfo("%n Super : " + aObjectToIntrospectSuperClass
								+ "%n Got Method : " + mMethodFound.getName());

						Class<?> aReturnType = mMethodFound.getReturnType();
						if (mMethodFound.getReturnType() != null) {
							GNSObjectMappedLogger.getLogger().logInfo("[Object.Super Introspect] " + sInvokeMethodName
									+ " Return Method Type : " + aReturnType.toGenericString());
							return mMethodFound.invoke(aObjectToIntrospect);
						} else {
							GNSObjectMappedLogger.getLogger().logInfo("[Object.Super Introspect] " + sInvokeMethodName
									+ " Return Method Type : is NULL ");
							mMethodFound.invoke(aObjectToIntrospect);
							return null;
						}

					}
				}
			}

		} catch (Exception evERRREFLECT) {
			JFXApplicationException.raiseToFront(JFXApplicationClassHelper.class, evERRREFLECT, true);

		}

		String sObjectIntrospectedClassName = "[NULL CLASS]";
		sObjectIntrospectedClassName = aObjectToIntrospect.getClass().getName();

		throw new JFXApplicationRuntimeException(String.format("(%s) Does not respond to (%s)  ",
				String.valueOf(sObjectIntrospectedClassName), sInvokeMethodName));

	}

}
