package Main;

import java.net.URL;
import java.util.ArrayList;


import org.genose.java.implementation.javafx.applicationtools.ApplicationJFX;
import org.genose.java.implementation.javafx.applicationtools.ApplicationJFXScene;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends ApplicationJFX { 

    @Override
    public void start(Stage primaryStage) throws Exception{
    	
    	try {
    		System.out.println(" :: "+getApplicationPath());
    		
    		URL aUrlClassPath = getClass().getResource(".");
    		
        	super.start(primaryStage);
        	 

        	
            // Parent root = FXMLLoader.load(getClass().getResource("../MainWindow/MainWindow.fxml"));
            
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setOpacity(0.8);
            // primaryStage.setScene(new Scene(root, primaryStage.getHeight(), primaryStage.getWidth()));
        	
            primaryStage.setScene( new ApplicationJFXScene( "StartupScreens",null ) );
        	            
            primaryStage.show();
            primaryStage.setIconified(false);
            primaryStage.centerOnScreen();
        	
	
		} catch (Exception EV_ERR_INSTANTIATE) {
			// TODO: handle exception
			EV_ERR_INSTANTIATE.printStackTrace();
			throw EV_ERR_INSTANTIATE;
		}    	
    }


    public static void main(String[] args) {
        launch(args); 
    }

 
}