package Application.Main;

import java.util.function.Function;

import javax.print.DocFlavor.URL;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationCallback;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationClassHelper;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationHelper;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationLogger;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationScene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Main extends JFXApplication {

	@Override
	public void start(Stage primaryStage) throws Exception {

		try {

			// setPrimaryStage ...
			super.start(primaryStage);
			// :: handler to call after loading super.setOnComplete(); ...
			JFXApplicationCallback aCallBackFunc = new JFXApplicationCallback(5000) {

				@Override
				public Object apply(Object aNode) {

					Object aInvokeableObject = JFXApplicationClassHelper.invokeMethod(aNode, "getRootController");
					if (aInvokeableObject != null
							&& JFXApplicationClassHelper.respondsTo(aInvokeableObject, "doStart")) {
						JFXApplicationClassHelper.invokeMethod(aInvokeableObject, "doStart");
					} else {
						String sMessageFailInvokeable = "Something went wrong when finding Method doStart ...";
						getLogger().logError(this.getClass(), sMessageFailInvokeable);
						JFXApplicationException.raiseToFront(this.getClass(), new JFXApplicationException(
								sMessageFailInvokeable, null, JFXApplicationHelper.getStackTrace()), true);
					}

					return aNode;
				}
				@Override
				public <V> Function<Object, V> andThen(Function<? super Object, ? extends V> after) {
					return super.andThen(after);
				}
			};

			super.setPrimaryScene(JFXApplicationScene.createScene("StartupScreens", null, aCallBackFunc));

			primaryStage.show();
			primaryStage.centerOnScreen();

		
			// super.setSecondaryScene(JFXApplicationScene.createScene("MainWindow", null,
			// null )); ...

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
			JFXApplicationException.raiseToFront(JFXApplication.class.getClass(), evERRMAIN, true);

		}
	}

}