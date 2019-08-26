package Main;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationDesignObjectLoad;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationHelper;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationLogger;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationScene;
import org.json.JSONArray; 

import javafx.stage.Stage;

public class Main extends JFXApplication {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			super.start(primaryStage);
 System.out.println("Main :: "+ JFXApplicationHelper.getApplicationBundlePath());
 System.out.println("path :: "+getClass().getResource("."));
			JFXApplicationScene  aScene = JFXApplicationDesignObjectLoad.create("MainWindow", null, null);
			//super.setPrimaryScene(aScene);

			primaryStage.show();
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
			JFXApplicationException.raiseToFront(Main.class.getClass(), evERRMAIN, true);

		}
	}
}
