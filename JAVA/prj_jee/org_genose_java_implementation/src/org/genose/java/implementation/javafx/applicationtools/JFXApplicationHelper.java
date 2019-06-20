/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
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

	
	/* ****************************************************** */
	/**
	 * 
	 * @return List<StackTraceElement>
	 */
	public static StackTraceElement[] getStackTrace() {
		return Thread.currentThread().getStackTrace();
		
	}
	/* ****************************************************** */
	private StackTraceElement getLastStackTrace()
	{
		StackTraceElement[] aStackTrace = Thread.currentThread().getStackTrace();
		return aStackTrace[aStackTrace.length -1];
	}
	/* ****************************************************** */
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
