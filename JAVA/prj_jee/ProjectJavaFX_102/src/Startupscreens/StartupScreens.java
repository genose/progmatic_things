package StartupScreens;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationCallback;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationLogger;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationScene;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationViewInvokableInterface;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.StageStyle;

/**
 * @author xenon
 *
 */
public class StartupScreens implements JFXApplicationViewInvokableInterface {

	@FXML
	private Label aStartupText;

	/* ******************************************************* */
	/**
	 * 
	 * JavaFX Default init window you can t access to window / scene element until
	 * is not showed once
	 * 
	 */
	@FXML
	public void initialize() {
		JFXApplication.getJFXApplicationSingleton().getPrimaryStage().initStyle(StageStyle.UNDECORATED);
		aStartupText.setText("Booting Up ...");
	}
	/*
	 * ****************************************************************************
	 */

	/*
	 * ****************************************************************************
	 */
	@FXML
	public void doStart() {
	//	try {
		
			JFXApplicationCallback aCallback=	new JFXApplicationCallback() {
				
				@Override
				public Object apply(Object arg0) {
					this.setDescription("Close Startup Screen and Call new Stage to show up MainWindow ...");
					JFXApplication.getJFXApplicationSingleton().getPrimaryStage().hide();
					JFXApplication.getJFXApplicationSingleton().setPrimaryScene((JFXApplicationScene)arg0);
					JFXApplication.getJFXApplicationSingleton().getPrimaryStage().show();
					return arg0;
				}
				@Override
				public String getDescription() {
					return "Nobody somebody...";
				}
			};
			
		//	JFXApplicationScene.create("MainWindow", null, aCallback );
			
 
JFXApplicationLogger.getLogger().logInfo(this.getClass(), String.valueOf(getClass().getEnclosingMethod()));

/*
		} catch (JFXApplicationException evERRDOSTART) {
			JFXApplicationLogger.getLogger().logError(this.getClass(), evERRDOSTART);

			JFXApplicationException.raiseToFront(this.getClass(), evERRDOSTART, true);
		}*/

	}

	/*
	 * ****************************************************************************
	 */
	@FXML
	public void doClose() {
		JFXApplicationLogger.getLogger().logInfo(" DoClose ...");
	}
}
