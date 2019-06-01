package hellofx;


/**
 * @author 59013-36-18
 *
 */

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



public class Main extends Application { 

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../MainWindow/MainWindow.fxml"));
        
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setOpacity(0.5);
        primaryStage.setTitle("Jeu du pendu ... (Version consoleLog bridged)");
        primaryStage.setScene(new Scene(root, primaryStage.getHeight(), primaryStage.getWidth()));
        primaryStage.show();
        primaryStage.setIconified(false);
        primaryStage.centerOnScreen();
        
 
        
        
        
        primaryStage.getScene().setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent evMouse) {
            	
            	Scene eventSourceScene =  ((Scene)evMouse.getSource()) .getWindow().getScene();
            	
            	eventSourceScene.getWindow().setOpacity(0.8);
            	// System.out.println(" Event opacity : "+eventSourceScene.getWindow().getOpacity());
            	
            /*
                calling several oblects' methods and doing things
            */
            }
        });
        
        primaryStage.getScene().setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent evMouse) {
            	
            	Scene eventSourceScene =  ((Scene)evMouse.getSource()).getWindow().getScene();
            	
            	eventSourceScene.getWindow().setOpacity(0.5);
            	// System.out.println(" Event opacity : "+eventSourceScene.getWindow().getOpacity());
            	
            /*
                calling several oblects' methods and doing things
            */
            }
        });
        
    }


    public static void main(String[] args) {
        launch(args); 
    }

 
}