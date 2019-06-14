package main;

import java.util.function.Function;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationLogger;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationScene;

import javafx.stage.Stage;

public class Main extends JFXApplication {

	@Override
	public void start(Stage primaryStage) throws Exception {

		try {

			// setPrimaryStage ...
			super.start(primaryStage);

			getLogger().log(System.Logger.Level.OFF, "{string}",
					getClass() + " :: " + JFXApplication.getApplicationBundlePath());
			
			{ 
				
				// JFXApplicationScene("MainWindow", { setAsPrimary(); });
			}
			
			super.setPrimaryScene(new JFXApplicationScene("StartupScreens", null, null ));
			// :: handler to call  after loading super.setOnComplete();
			super.setSecondaryScene(new JFXApplicationScene("mainwindow", null, null ));
			
// primaryStage.setScene(new Scene(root, primaryStage.getHeight(), primaryStage.getWidth()));

			primaryStage.setScene(super.getPrimaryScene());

			primaryStage.show();
			primaryStage.centerOnScreen();

		} catch (Exception evERRINSTANTIATE) {
			(System.getLogger(getClass().toString())).log(System.Logger.Level.ERROR, evERRINSTANTIATE.getMessage());
			throw evERRINSTANTIATE;
		}
	}

	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Exception evERRMAIN) {
			(new JFXApplicationLogger(Main.class.getName())).logError(Main.class.getClass(), evERRMAIN);

		}
	}

}