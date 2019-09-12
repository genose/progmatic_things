package com.Application.Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationHelper;
import org.genose.java.implementation.net.GNSObjectSSHConnection;

import java.net.URL;

import static java.lang.Thread.sleep;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {

            /* ********************* */
           /* String fileName =  "sample";
            String appPath =  JFXApplicationHelper.packageToPathRoot(Main.class);
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
            }*/

           // JFXApplicationCustomControlSplitMenuHBox
          Application aApplicationRef =  JFXApplicationHelper.getApplicationMain();
System.out.println("Application "+aApplicationRef);
            primaryStage.show();
            GNSObjectSSHConnection objectSSHConnection = new GNSObjectSSHConnection("SGBDConnection",
                    "213.32.13.191", 0,
                    null, null,
                    "root", "AFPA_r0uba1x",
                    null, null,
                    null);

            //objectSSHConnection.execShell("pwd");

            objectSSHConnection.execSFTP("pwd");

            System.out.println(" ... Back to main ...");
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Waiting before closing connection ");
            Thread.sleep(10000);
            System.out.println(" ... Main :: awaked ...");
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Close connection ");
            System.out.println(" ... Main :: try close ...");
            objectSSHConnection.close();
            System.out.println(" ... main : done ...");


        } catch (Exception evErrExceptionApppMain) {
            System.out.println("Error :: " + evErrExceptionApppMain);
        }
    }


    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception evErrMain) {
            System.out.println("Error :: " + evErrMain);
        }
    }
}
