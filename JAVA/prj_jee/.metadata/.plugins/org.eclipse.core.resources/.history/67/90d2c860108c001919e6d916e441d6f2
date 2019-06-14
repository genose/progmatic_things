/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

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
//:: https://riptutorial.com/javafx/example/19339/loading-resource-bundle
	// :: https://riptutorial.com/Download/javafx.pdf
	
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

}
