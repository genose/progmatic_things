package Application.Main;

import java.net.URL;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationHelper;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationLogger;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.views.*;

import TableViewDemo.TableViewDemo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends JFXApplication {

	@Override
	public void start(Stage primaryStage) throws Exception {

		try {

			// setPrimaryStage ...
			super.start(primaryStage);

			// getLogger().log(System.Logger.Level.OFF, "{string}", getClass() + " :: " +
			// JFXApplication.getApplicationBundlePath());

			{

				// JFXApplicationScene("MainWindow", { setAsPrimary(); });
			}

			// URL aressource =
			// getClass().getResource("../../StartupScreens/StartupScreens.fxml");
			// Parent root = FXMLLoader.load(aressource);

			super.setPrimaryScene(JFXApplicationScene.createScene("StartupScreens", null, null));

// primaryStage.setScene(new Scene(root, primaryStage.getHeight(), primaryStage.getWidth()));

			// primaryStage.setScene(super.getPrimaryScene());

			primaryStage.show();
			primaryStage.centerOnScreen();

			Object aInvokeableObject = JFXApplicationHelper.invokeMethod((super.getPrimaryScene()),
					"getRootController");
			if (aInvokeableObject != null) {
				JFXApplicationHelper.invokeMethod(aInvokeableObject, "doStart");
			} else {
				String sMessageFailInvokeable = "Something went wrong when finding Method doStart ...";
				getLogger().logError(this.getClass(), sMessageFailInvokeable);
				JFXApplicationException.raiseToFront(this.getClass(),
						new JFXApplicationException(sMessageFailInvokeable, null, JFXApplicationHelper.getStackTrace()),
						true);
			}

			// :: handler to call after loading super.setOnComplete();
			// super.setSecondaryScene(JFXApplicationScene.createScene("MainWindow", null,
			// null ));

		} catch (Exception evERRINSTANTIATE) {
			JFXApplicationLogger.getLogger().logError(this.getClass(), evERRINSTANTIATE);
			throw evERRINSTANTIATE;
		}
	}

	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Exception evERRMAIN) {
			JFXApplicationLogger.getLogger().logError(Main.class.getClass(), evERRMAIN);

		}
	}

}