/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;

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

		Parent root = FXMLLoader.load(getClass().getClassLoader()
		                                  .getResource("ui/main.fxml"), bundle);
	}

	public static void invoke(Object aObjectToIntrospect, String sInvokeMethodName) {
		
		if((aObjectToIntrospect != null) && (sInvokeMethodName != null)) {
			
			if(String.valueOf(sInvokeMethodName).length() < 2) {
				String sMessageToRaise = String.format("Can't call unnamed method (%s) on Object (%s)  ", sInvokeMethodName, aObjectToIntrospect.getClass() );
				throw JFXApplicationException.raiseToFront( JFXApplicationHelper.class, new JFXApplicationException(sMessageToRaise, null, JFXApplicationHelper.getStackTrace() ));
			}
			Method [] aMethodList = aObjectToIntrospect.getClass().getDeclaredMethods();
			List<Method>aArrayMethodList = Arrays.asList(aMethodList);
			for (Method mMethodFound : aArrayMethodList) {
				if(mMethodFound.getName().toLowerCase().compareToIgnoreCase(sInvokeMethodName) == 0) {
					
					return;
				}
			}
		}
		
		
	}
	
	public static StackTraceElement[] getStackTrace() {
		StackTraceElement[] aStackTrace = Thread.currentThread().getStackTrace();
		return aStackTrace;
	}
	

}
