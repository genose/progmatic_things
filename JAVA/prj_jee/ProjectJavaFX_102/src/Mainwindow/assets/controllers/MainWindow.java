/**
 * 
 */
package MainWindow.assets.controllers;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationStage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * @author 59013-36-18
 *
 */
public class MainWindow extends JFXApplicationStage {

	@FXML
	private Button tirroirButton;
	@FXML
	public void initialize() {
		tirroirButton.setText("X");
		
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
