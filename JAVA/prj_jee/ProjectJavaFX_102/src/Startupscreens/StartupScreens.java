package StartupScreens;
 
import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationLogger;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationScene;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationViewInvokableInterface;

import TableViewDemo.TableViewDemo;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * @author xenon
 *
 */
public class StartupScreens implements org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationViewInvokableInterface {

	@FXML
	private Label aStartupText;
	/* ******************************************************* */
	/**
	 * 
	 * JavaFX Default init window
	 * you can t access to window / scene element until is not showed once 
	 * 
	 */
	@FXML
	public void initialize()
	{
		JFXApplication.getJFXApplicationSingleton().getPrimaryStage().initStyle(StageStyle.UNDECORATED);
		aStartupText.setText("Booting Up ...");
	}
	/* **************************************************************************** */
	
	/* **************************************************************************** */
	@FXML
	public void doStart() {
		try {
			JFXApplication.getJFXApplicationSingleton().getPrimaryStage().hide();
			//
		JFXApplication.getJFXApplicationSingleton().setPrimaryScene(JFXApplicationScene.createScene("MainWindow", null, null ));
			
			

			
			//JFXApplication.getJFXApplicationSingleton().getPrimaryStage().initStyle(StageStyle.DECORATED);
			//JFXApplication.getJFXApplicationSingleton().setPrimaryScene(new TableViewDemo());
			JFXApplication.getJFXApplicationSingleton().getPrimaryStage().show();
			
	
		} catch (JFXApplicationException evERRDOSTART) {
			JFXApplicationLogger.getLogger().logError(this.getClass(), evERRDOSTART);
			JFXApplication.getExceptionManagaer().raiseToFront(this.getClass(), evERRDOSTART, true);
		}

	}
	/* **************************************************************************** */
	@FXML
	public void doClose() {
		
	}
}
