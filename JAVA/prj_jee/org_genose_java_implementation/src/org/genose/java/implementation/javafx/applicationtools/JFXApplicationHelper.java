/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;



import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationHelper implements JFXApplicationDesignObjectLoad {

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
		try {
			Thread aThread = Thread.currentThread();
			if(aThread != null) {
				if(aThread.isDaemon())
					return aThread.getStackTrace();
				else
					System.out.println("ERROR CANT FETCH CURRENT THREAD WHILE FETCHING THREAD STACK ... ");
			}else {
				System.out.println("ERROR CANT FETCH CURRENT THREAD ... ");
			}
				
		} catch (Exception evERRSTACKTRACE) {
			System.out.println("ERROR WHILE FETCHING THREAD STACK ... "+evERRSTACKTRACE);
		}
		StackTraceElement[] aStackElement = null;
		aStackElement[0] = new StackTraceElement(JFXApplicationHelper.class.getName(), "getStackTrace", null, 1);
		return aStackElement;
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
	
	/**
	 * @param sApplicationPath
	 * @param aPathInBundle
	 * @return
	 */
	public static String resolveModulePathInsenstiveCase(String sApplicationPath, String sRequiredPathIn,
			String sAltPathIfFail) {
		String sBasePath = null;
		if ((sApplicationPath != null) && (sRequiredPathIn != null)) {
			Path aPathInBundle = Paths.get(sApplicationPath, sRequiredPathIn);

			sBasePath = ((JFXApplication.applicationPathExist(aPathInBundle.toString())) ? aPathInBundle.toString()
					: null);

			if (sBasePath == null) {
				aPathInBundle = Paths.get(sApplicationPath, String.valueOf(sRequiredPathIn).toLowerCase());

				sBasePath = ((JFXApplication.applicationPathExist(aPathInBundle.toString())) ? aPathInBundle.toString()
						: null);
			}

			if (sBasePath == null) {
				aPathInBundle = Paths.get(sApplicationPath, String.valueOf(sRequiredPathIn).toUpperCase());

				sBasePath = ((JFXApplication.applicationPathExist(aPathInBundle.toString())) ? aPathInBundle.toString()
						: null);
			}

		}

		if (((sAltPathIfFail != null) && String.valueOf(sAltPathIfFail).length() > 1) && (sBasePath == null)) {
			sBasePath = ((JFXApplication.applicationPathExist(sAltPathIfFail)) ? String.valueOf(sAltPathIfFail) : null);
		}

		return sBasePath;
	}
	
	
	public static String packageToPath( Object  aObjectClass) 
	{
		try {
			Class localClass = aObjectClass.getClass();

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

	public static Object create( Class<?>  aClass) {
		String sDesignFile = String.format("%s%s", aClass.getSimpleName(), JFXApplication.JFXFILETYPE.FILETYPE_FXML.getValue());
		Class aRefeneceClass = aClass.getClass();
		ClassLoader aClassLoader = aRefeneceClass.getClassLoader();
		java.net.URL aUrlDesignFile = aRefeneceClass.getResource(sDesignFile);
		
		
		return null;
	}

}
