package com.Application.Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationHelper;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        String fileName =  "sample";
       String appPath =  JFXApplicationHelper.packageToPath(Main.class)+""+String.valueOf(Main.class.getCanonicalName()).replaceAll("[\\.]", "/");
        String filePathSTR = "/views/"+fileName + ".fxml";

        filePathSTR = appPath+""+filePathSTR;
        System.out.println(" App :: "+getClass().getResource("."));
        System.out.println(" AppPath :: "+appPath);

        System.out.println(" :: "+getClass().getResource(filePathSTR ));

        System.out.println("File .... "+filePathSTR);

        URL fileURL = getClass().getResource(filePathSTR);

        System.out.println("Ressource .... "+fileURL);

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