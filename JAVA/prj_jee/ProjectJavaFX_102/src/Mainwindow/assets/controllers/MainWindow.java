/**
 * 
 */
package MainWindow.assets.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationStage;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationViewsHierarchy;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * @author 59013-36-18
 *
 */
public class MainWindow implements JFXApplicationViewsHierarchy {

	@FXML
	private Button tirroirButton;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

		tirroirButton.setText("*");
		 
		
	}
	 @FXML
	 public void quitApp() {
		 JFXApplication.getJFXApplicationSingleton().notifyQuit();
		
	 }
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
}
