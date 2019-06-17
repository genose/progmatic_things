/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationRuntimeException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationHelper {

	/**
	 * 
	 */
	public JFXApplicationHelper() {
		// TODO Auto-generated constructor stub
	}

	void loadBundleRessource() throws IOException {
		Locale locale = new Locale("en", "UK");
		ResourceBundle bundle = ResourceBundle.getBundle("strings", locale);

		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("ui/main.fxml"), bundle);
	}

	public static Object invokeMethod(Object aObjectToIntrospect, String sInvokeMethodName) {

		
		if ( (aObjectToIntrospect == null) || (sInvokeMethodName == null) || (String.valueOf(sInvokeMethodName).length() < 2) )  {
			String sMessageToRaise = String.format("Can't call unnamed method (%s) on Object (%s)  ",
					sInvokeMethodName, ((aObjectToIntrospect != null)? aObjectToIntrospect.getClass() : "[NULL OBJECT]"));
			JFXApplicationException aThroweableException = new JFXApplicationException(sMessageToRaise, null,
					JFXApplicationHelper.getStackTrace());
			JFXApplicationException.raiseToFront(JFXApplicationHelper.class, aThroweableException, false);
			return null;
		}
		 
			
			Method[] aMethodList = aObjectToIntrospect.getClass().getDeclaredMethods();
			List<Method> aArrayMethodList = Arrays.asList(aMethodList);
			
			
			JFXApplicationLogger.getLogger().logInfo(aObjectToIntrospect.getClass().getName()+" Got Method count : "+String.valueOf(aMethodList.length));
			for (Method mMethodFound : aArrayMethodList) {
					JFXApplicationLogger.getLogger().logInfo(" Got Method : "+mMethodFound.getName());
				if (mMethodFound.getName().toLowerCase().compareToIgnoreCase(sInvokeMethodName) == 0) {
					try {
					
						Class<?> aReturnType = mMethodFound.getReturnType();
						if(mMethodFound.getReturnType() != null) {
							JFXApplicationLogger.getLogger().logInfo(" Return Method Type : "+aReturnType.toGenericString());
							return mMethodFound.invoke(aObjectToIntrospect, null);
						}else {
							JFXApplicationLogger.getLogger().logInfo(" Return Method Type : is NULL ");
							mMethodFound.invoke(aObjectToIntrospect, null);
							return null;
						}
						
						
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException aThroweableException) {
						JFXApplicationException.raiseToFront(JFXApplicationHelper.class, aThroweableException, false);
					}
					
					return null;
				}
			}
	 

		throw new JFXApplicationRuntimeException(String.format("(%s) Does not respond to (%s)  ",
				((aObjectToIntrospect != null) ? aObjectToIntrospect : Object.class).getClass(), sInvokeMethodName));

	}

	public static StackTraceElement[] getStackTrace() {
		StackTraceElement[] aStackTrace = Thread.currentThread().getStackTrace();
		return aStackTrace;
	}

	public static String getApplicationBundlePath() {
		try {
			Class localClass = JFXApplicationHelper.class.getClass();

			String systemPathSeparator = String.valueOf(File.separatorChar);
			String packageNamed = localClass.getPackageName();
			String localPackagePath = String.valueOf(packageNamed);
			String localRunnablePathRelative = String.valueOf("." + packageNamed).replaceAll("[.]", "*")
					.replaceAll("[^\\*]", "");

			localRunnablePathRelative = localRunnablePathRelative.replaceAll("[\\*]", "..\\" + systemPathSeparator);
			 

			return localRunnablePathRelative;
		} catch (Exception evErrPath) {
			JFXApplicationLogger.getLogger().logError(JFXApplication.class.getClass(), evErrPath);
		}
		return null;
	}

}
