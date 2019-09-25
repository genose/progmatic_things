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
// https://github.com/mcka-dev/javafx-internationalization-maven-example/tree/master/src/main
	// https://github.com/mcka-dev/javafx-internationalization-maven-example
	// https://stackoverflow.com/questions/10143392/javafx-2-and-internationalization
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
