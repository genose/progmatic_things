package com.application.sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        String pathFXML = getClass().getResource("/")+String.valueOf(getClass().getPackageName()).replaceAll("[\\.]", "/");
        pathFXML = "design/sample.fxml";
        System.out.println( "path ::  "+getClass().getResource("."));
        System.out.println( "path :: !! "+pathFXML);
        URL fileDesign = getClass().getResource(pathFXML);
        System.out.println( "getressource :: !! "+fileDesign);
        Parent root = FXMLLoader.load(fileDesign);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
