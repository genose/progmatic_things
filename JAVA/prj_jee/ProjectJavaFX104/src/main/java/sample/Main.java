package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL fileURL = getClass().getResource(getClass().getResource(".")+"/sample.fxml");
        if(fileURL != null) {
            Parent root = FXMLLoader.load(fileURL);
            primaryStage.setTitle("Hello World");
            primaryStage.setScene(new Scene(root, 300, 275));
        }else{
            System.out.println( " Error : file not found ... in "+(getClass().getResource(".")));
        }
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
