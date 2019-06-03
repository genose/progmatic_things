package org.genose.java.implementation.javafx.applicationtools;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ApplicationJFXScene extends Scene {

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 */
	public ApplicationJFXScene(Parent arg0, double arg1, double arg2, boolean arg3, SceneAntialiasing arg4) {
		super(arg0, arg1, arg2, arg3, arg4);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public ApplicationJFXScene(Parent arg0, double arg1, double arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public ApplicationJFXScene(Parent arg0, double arg1, double arg2, Paint arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public ApplicationJFXScene(Parent arg0, double arg1, double arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ApplicationJFXScene(Parent arg0, Paint arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	/* ****************************************************************************** */
	/**
	 * @param arg0
	 */
	public ApplicationJFXScene(Parent arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	/* ****************************************************************************** */
	/**
	 * 
	 * 
	 */
	public ApplicationJFXScene() {
		super(null);
	}
	
	/* ****************************************************************************** */
	/**
	 * 
	 * @param argModuleName
	 * @throws Exception
	 */
	public ApplicationJFXScene(String argModuleName) throws Exception {
		
		super(null);
		String sApplicationPath = ApplicationJFX.getApplicationPath()+"/";
		Boolean bModuleAsMVC = false;
		
		// formatting path as [APP_ROOT]/src/[ARGUMENT]/[MVC STYLE(Controller;View;Ressources)]/[ARGUMENT].[FILEEXT]
		
		String sBasePath = argModuleName+"/"+((ApplicationJFX.applicationPathExist(sApplicationPath+""+argModuleName+"/"+ApplicationJFX.APPLICATION_MVC_DIRS.DIR_ASSETS))? ApplicationJFX.APPLICATION_MVC_DIRS.DIR_ASSETS : "" );
		String sRequestedScene = sBasePath+"/"+((ApplicationJFX.applicationPathExist(sApplicationPath+""+argModuleName+"/"+ApplicationJFX.APPLICATION_MVC_DIRS.DIR_VIEWS))? ApplicationJFX.APPLICATION_MVC_DIRS.DIR_VIEWS : "" );
		//+"/"+argModuleName+".fxml";
		String sRequestedSceneCSS = sBasePath+"/"+((ApplicationJFX.applicationPathExist(sApplicationPath+""+argModuleName+"/"+ApplicationJFX.APPLICATION_MVC_DIRS.DIR_VIEWS))? ApplicationJFX.APPLICATION_MVC_DIRS.DIR_VIEWS : "" );
		//+"/"+argModuleName+".css";
		
		String sRequestedSceneIcon = sBasePath+"/"+((ApplicationJFX.applicationPathExist(sApplicationPath+""+argModuleName+"/"+ApplicationJFX.APPLICATION_MVC_DIRS.DIR_RESSOURCES))? ApplicationJFX.APPLICATION_MVC_DIRS.DIR_RESSOURCES : "" );
		// +"/"+argModuleName+".png";
		
	
		

		// :: https://stackoverflow.com/questions/10121991/javafx-application-icon
		// ::  primaryStage.getIcons().add(new Image(getClass().getProtectionDomain().getCodeSource().getLocation()+"/stackoverflow.jpg"));
		if( ApplicationJFX.getPrimaryStage() != null ) {
			
			ApplicationJFX.getPrimaryStage();

			if(ApplicationJFX.applicationPathExist(sRequestedScene)) {
			
				Parent root = FXMLLoader.load( getClass().getResource(sRequestedScene) );
				 if(root != null ) {
					 setRoot( root );
				 }
				
				// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				// primaryStage.setScene(scene);
			}
		}else {
			throw new InvalidAlgorithmParameterException(" No Primary Stae for this Application ... ");
		}
		
        
	}
	/* ****************************************************************************** */
	/**
	 * 
	 * @param aIconPath
	 * @return
	 */
	public Boolean setIcon(String aIconPath) {
		
		Parent rootStage = this.getRoot();
		// .add(new Image(aIconPath));
        
		
		return false;
	}
	/* ****************************************************************************** */
	/**
	 * 
	 * @param image
	 */
	public Boolean setIcon(Image image) {
			Parent rootStage = this.getRoot();
		return false;
	}

	
	

}
