package Main;


import java.lang.System.Logger;


import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationScene;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationStage;
import org.genose.java.implementation.streams.ConsoleStream;

import javafx.stage.Stage;


public class Main extends JFXApplication { 

    @Override
    public void start(Stage primaryStage) throws Exception{
    	
    	try {

    		// setPrimaryStage ...
        	super.start(primaryStage);
        	
        	(System.getLogger(getClass().toString())).log(System.Logger.Level.OFF, "{string}", getClass()+" :: "+JFXApplication.getApplicationBundlePath());
    		
          /*  
Parent root = FXMLLoader.load(getClass().getResource("../MainWindow/MainWindow.fxml"));
    
primaryStage.initStyle(StageStyle.UNDECORATED);
primaryStage.setOpacity(0.8);
// primaryStage.setScene(new Scene(root, primaryStage.getHeight(), primaryStage.getWidth()));

primaryStage.setScene( new ApplicationJFXScene( "StartupScreens",null ) );
            
primaryStage.show();
primaryStage.setIconified(false);
primaryStage.centerOnScreen();
    */


		} catch (Exception EV_ERR_INSTANTIATE) { 
			(System.getLogger(getClass().toString())).log(System.Logger.Level.ERROR, EV_ERR_INSTANTIATE.getMessage());
			throw EV_ERR_INSTANTIATE;
		}    	
    }


    public static void main(String[] args) {
        launch(args); 
    }

 
}