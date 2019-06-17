/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.io.File;

/**
 * @author xenon
 *
 */
public class JFXApplicationBundle extends JFXApplicationHelper {

	// :: https://riptutorial.com/javafx/example/19339/loading-resource-bundle
	// :: https://riptutorial.com/Download/javafx.pdf
	/**
	 * 
	 */
	public JFXApplicationBundle() {
		// nothing to initialize
	}

	public static boolean fileExist(String aFilePath) {
		if(aFilePath == null) return false;
		return (new File(aFilePath)).exists();
	}
	
	public JFXApplicationBundle(String aBundlePath) {
		
	}
}
