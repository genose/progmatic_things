package com.application.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        String filePathSTR =  getClass().getResource(".")+"/views/sample.fxml";
        URL fileURL = getClass().getResource(filePathSTR);
        System.out.println("File .... "+fileURL);
        if(fileURL != null) {
            Parent root = FXMLLoader.load(fileURL);
            primaryStage.setTitle("Hello World");
            primaryStage.setScene(new Scene(root, 300, 275));
        }else{
            System.out.println( " Error : file not found ... in "+(filePathSTR));
        }
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
