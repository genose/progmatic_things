package Main;


import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationDesignObjectLoad;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationFunctionCallback;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationHelper;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationMappedLogger;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationDialog;
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


 				JFXApplicationFunctionCallback aCallback = new JFXApplicationFunctionCallback(){

				@Override
				public Object apply(Object t) {

					JFXApplicationMappedLogger.getLogger().logInfo(this.getClass(), " +++++++++++++++++++++++++++++ ");
					JFXApplicationHelper.getApplicationMain().getPrimaryStage().show();
					/* ************************************************ */

					/* ************************************************ */
					JFXApplicationFunctionCallback aCallbackb = new JFXApplicationFunctionCallback(2000){
						@Override
						public Object apply(Object t) {

							JFXApplicationFunctionCallback aCallbackc = new JFXApplicationFunctionCallback(2000){

								@Override
								public Object apply(Object t) {
									JFXApplicationDialog.showAlertDialog("HelloWorld");
									JFXApplicationHelper.getApplicationMain().getPrimaryStage().show();
									return null;
								}
							};

							try {
								//JFXApplicationHelper.getApplicationMain().getPrimaryStage().hide();
								JFXApplicationScene aSceneb = JFXApplicationDesignObjectLoad.create("ArticleDialog", null, aCallbackc);
								JFXApplicationHelper.getApplicationMain().setPrimaryScene(aSceneb);
							} catch (JFXApplicationException e) {
								e.printStackTrace();
							}


							return null;

						}
					};



					return this.andThen(aCallbackb);
				}
			};

			//JFXApplicationDialog.showAlertDialog("HelloWorld");
// 			JFXApplicationScene  aScene = JFXApplicationDesignObjectLoad.create("Main", "MainWindow", null);


			super.setPrimaryScene( JFXApplicationDesignObjectLoad.create("MainWindow", null,aCallback) );

		} catch (Exception evERRINSTANTIATE) {
			JFXApplicationMappedLogger.getLogger().logError(this.getClass(), evERRINSTANTIATE);
			throw evERRINSTANTIATE;
		}
	}

	public static void main(String[] args) {
		try {

			// JFXApplicationException.raiseToFront(Main.class, null, true);
			 launch(args);
		} catch (Exception evERRMAIN) {
			//JFXApplicationLogger.getLogger().logError(Main.class.getClass(), evERRMAIN);
			System.out.println("Main::FATAL ERROR : "+evERRMAIN);
			// JFXApplicationException.raiseToFront(Main.class.getClass(), evERRMAIN, true);

		}
	}
}
