/**
 * 
 */
package MainWindow.Assets.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.Window;

/**
 * @author 59013-36-18
 *
 */
public class MainWindow extends Window {

	 @FXML
	 public void quitApp() {
		 Platform.exit();
	 }
}
