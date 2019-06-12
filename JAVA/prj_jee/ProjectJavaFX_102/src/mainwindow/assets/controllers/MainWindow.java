/**
 * 
 */
package MainWindow.Assets.Controllers;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationStage;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.Window;

/**
 * @author 59013-36-18
 *
 */
public class MainWindow extends JFXApplicationStage {

	 @FXML
	 public void quitApp() {
		 JFXApplication.getJFXApplicationSingleton().notifyQuit();
		
	 }
}
