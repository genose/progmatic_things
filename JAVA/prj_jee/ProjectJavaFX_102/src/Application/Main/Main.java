package Application.Main;

import java.lang.reflect.Method;
import java.util.function.Function;

import javax.print.DocFlavor.URL;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationCallback;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationClassHelper;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationHelper;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationLogger;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationScene;
import org.genose.java.implementation.javafx.applicationtools.views.customviewscontroller.JFXApplicationCustomControlSplitMenuHBox;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Main extends JFXApplication {

	@Override
	public void start(Stage primaryStage) throws Exception {

		try {

			// setPrimaryStage ...
			super.start(primaryStage);
			// :: handler to call after loading super.setOnComplete(); ...
			// as declared here, this instance of callback is a new extended class from
			// JFXApplicationCallback
			JFXApplicationCallback aCallBackFunc = new JFXApplicationCallback(2000) {

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
				public String getDescription() {
					return "Call new Stage to show up MainWindow ...";
				}

			};
 
			//super.setPrimaryScene(JFXApplicationScene.create("StartupScreens", null, aCallBackFunc));

			Parent aSplitMenu = JFXApplicationCustomControlSplitMenuHBox.create();
			
			JFXApplicationScene aScene = new JFXApplicationScene(aSplitMenu); 
			primaryStage.setHeight(480);
			primaryStage.setWidth(640);
			setPrimaryScene(aScene);
			primaryStage.show();
			primaryStage.centerOnScreen();
			JFXApplicationCustomControlSplitMenuHBox asplitMenuUserData = (JFXApplicationCustomControlSplitMenuHBox)aSplitMenu.getUserData();
			System.out.println("Data :: "+String.valueOf(asplitMenuUserData));
			asplitMenuUserData.setSlideMenuOnLeft(true);

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