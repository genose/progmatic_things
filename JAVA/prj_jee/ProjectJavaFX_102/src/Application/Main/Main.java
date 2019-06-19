package Application.Main;
 
import org.genose.java.implementation.javafx.applicationtools.*;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.views.*;
  
import javafx.stage.Stage;

public class Main extends JFXApplication { 

	@Override
	public void start(Stage primaryStage) throws Exception {

		try {

			// setPrimaryStage ...
			super.start(primaryStage);
  
			super.setPrimaryScene(JFXApplicationScene.createScene("StartupScreens", null, null));
  
			primaryStage.show();
			primaryStage.centerOnScreen();

			Object aInvokeableObject = JFXApplicationClassHelper.invokeMethod((super.getPrimaryScene()),
					"getRootController");
			if (aInvokeableObject != null) {
				JFXApplicationClassHelper.invokeMethod(aInvokeableObject, "doStart");
			} else {
				String sMessageFailInvokeable = "Something went wrong when finding Method doStart ...";
				getLogger().logError(this.getClass(), sMessageFailInvokeable);
				JFXApplicationException.raiseToFront(this.getClass(),
						new JFXApplicationException(sMessageFailInvokeable, null, JFXApplicationHelper.getStackTrace()),
						true);
			}

			// :: handler to call after loading super.setOnComplete(); ...
			// super.setSecondaryScene(JFXApplicationScene.createScene("MainWindow", null,  null )); ...

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