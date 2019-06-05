package org.genose.java.implementation.javafx.applicationtools;


import org.genose.java.implementation.javafx.applicationtools.JFXApplicationException;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.Window;

public class JFXApplicationScene extends Scene {

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 */
	public JFXApplicationScene(Parent arg0, double arg1, double arg2, boolean arg3, SceneAntialiasing arg4) {
		super(arg0, arg1, arg2, arg3, arg4);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public JFXApplicationScene(Parent arg0, double arg1, double arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public JFXApplicationScene(Parent arg0, double arg1, double arg2, Paint arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public JFXApplicationScene(Parent arg0, double arg1, double arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public JFXApplicationScene(Parent arg0, Paint arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/*
	 * *****************************************************************************
	 * *
	 */
	/**
	 * @param arg0
	 */
	public JFXApplicationScene(Parent arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/*
	 * *****************************************************************************
	 * *
	 */
	/**
	 * 
	 * 
	 */
	public JFXApplicationScene() {
		super(null);
	}

	/*
	 * *****************************************************************************
	 * *
	 */
	/**
	 * Replace someting like this : 
	 *  Parent root = FXMLLoader.load(getClass().getResource("/MainWindow/MainWindow.fxml"));
        primaryStage.setScene(new Scene(root, primaryStage.getHeight(), primaryStage.getWidth()));
	 * 
	 * @param argModuleName
	 * @throws Exception
	 */
	 
	public JFXApplicationScene(String argModuleName, String argModuleNameFile) throws JFXApplicationException {

		super((new Parent() {

		}));
		String sApplicationPath = (JFXApplication.getApplicationBundlePath() + "/").replace("/./", "/");
		Boolean bModuleAsMVC = false;

		// formatting path as [APP_ROOT]/src/[ARGUMENT]/[MVC
		// STYLE(Controller;View;Ressources)]/[ARGUMENT].[FILEEXT]

		String sBasePath = ((JFXApplication.applicationPathExist(sApplicationPath + "" + argModuleName + "/"))
				? sApplicationPath + "" + argModuleName + "/"
				: "");

		sBasePath = ((sBasePath.length() <= 1) ? ((JFXApplication.applicationPathExist(
				sApplicationPath + "/" + JFXApplication.APPLICATION_MVC_DIRS.DIR_APPSRC.getValue() + "/"))
						? sApplicationPath + "/" + JFXApplication.APPLICATION_MVC_DIRS.DIR_APPSRC.getValue()
						: sBasePath)
				: sBasePath).replace("/./", "/");

		sBasePath = ((JFXApplication.applicationPathExist(sBasePath + "/" + argModuleName))
				? sBasePath + "/" + argModuleName
				: sBasePath).replace("/./", "/");

		sBasePath = ((JFXApplication
				.applicationPathExist(sBasePath + "/" + JFXApplication.APPLICATION_MVC_DIRS.DIR_ASSETS.getValue()))
						? sBasePath + "/" + JFXApplication.APPLICATION_MVC_DIRS.DIR_ASSETS.getValue()
						: sBasePath).replace("/./", "/").replace("//", "/");

 
		if (!JFXApplication.applicationPathExist(sBasePath)) {
			throw new JFXApplicationException( String.valueOf(getClass() + " can t determine path :: " + sBasePath));
		}

		String sRequestedScene = ((JFXApplication
				.applicationPathExist(sBasePath + "/" + JFXApplication.APPLICATION_MVC_DIRS.DIR_VIEWS.getValue()))
						? sBasePath + "/" + JFXApplication.APPLICATION_MVC_DIRS.DIR_VIEWS.getValue()
						: sBasePath);
		
		
		String sRequestedSceneAlt = (((sRequestedScene != null) && JFXApplication.applicationPathExist(sRequestedScene + "/" + argModuleName + ".fxml"))
										? sRequestedScene + "/" + argModuleName + ".fxml"
										: null);
		
		sRequestedScene = (((sRequestedScene != null) && ((argModuleNameFile != null) && argModuleNameFile.length() > 1)
				&& JFXApplication.applicationPathExist(sRequestedScene + "/" + argModuleNameFile + ".fxml"))
						? sRequestedScene + "/" + argModuleNameFile + ".fxml"
						: sRequestedSceneAlt);

		String sRequestedSceneCSS = ((JFXApplication
				.applicationPathExist(sBasePath + "/" + JFXApplication.APPLICATION_MVC_DIRS.DIR_RESSOURCES.getValue()))
						? sBasePath + "/" + JFXApplication.APPLICATION_MVC_DIRS.DIR_RESSOURCES.getValue()
						: sBasePath);
		
		String sRequestedSceneCSSAlt = (((sRequestedSceneCSS != null) && JFXApplication
								.applicationPathExist(sRequestedSceneCSS + "/" + argModuleName + ".css"))
										? sRequestedSceneCSS + "/" + argModuleName + ".css"
										: null);
		
		sRequestedSceneCSS = (((sRequestedSceneCSS != null)
				&& ((argModuleNameFile != null) && argModuleNameFile.length() > 1)
				&& JFXApplication.applicationPathExist(sRequestedSceneCSS + "/" + argModuleNameFile + ".css"))
						? sRequestedSceneCSS + "/" + argModuleNameFile + ".css"
						: sRequestedSceneCSSAlt);

		String sRequestedSceneIcon = ((JFXApplication
				.applicationPathExist(sBasePath + "/" + JFXApplication.APPLICATION_MVC_DIRS.DIR_RESSOURCES.getValue()))
						? sBasePath + "/" + JFXApplication.APPLICATION_MVC_DIRS.DIR_RESSOURCES.getValue()
						: null);
		
		String sRequestedSceneIconAlt = (((sRequestedSceneIcon != null) && JFXApplication
								.applicationPathExist(sRequestedSceneIcon + "/" + argModuleName + ".fxml"))
										? sRequestedSceneIcon + "/" + argModuleName + ".png"
										: null);
		
		sRequestedSceneIcon = (((sRequestedSceneIcon != null)
				&& ((argModuleNameFile != null) && argModuleNameFile.length() > 1)
				&& JFXApplication.applicationPathExist(sRequestedSceneIcon + "/" + argModuleNameFile + ".png"))
						? sRequestedSceneIcon + "/" + argModuleNameFile + ".png"
						: sRequestedSceneIconAlt);

		if (sRequestedScene == null) {
			throw new JFXApplicationException(getClass() + " can t find interface GUI componement :: " + argModuleName);
		}

		// :: https://stackoverflow.com/questions/10121991/javafx-application-icon
	 
		if (JFXApplication.getPrimaryStage() != null) {

			if (JFXApplication.applicationPathExist(sRequestedScene)) {
		 
				URL aUrlPath = getClass().getResource(sRequestedScene);
				
				if(aUrlPath != null) {
					Parent root;
					try {
						root = FXMLLoader.load(aUrlPath);
					} catch (IOException EV_ERR_LOAD_FXML ) {
						EV_ERR_LOAD_FXML.printStackTrace();
						throw new JFXApplicationException( EV_ERR_LOAD_FXML );
					}
					if (root != null) {
						this.setRoot(root);
					} else {
						throw new Error(" can't load " + sRequestedScene);
					}  
				}else {
					throw new Error(" can't load " + sRequestedScene);
				}

				// this.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				
			}
		} else {
			throw new JFXApplicationInvalidParameterException(" No Primary Stae for this Application ... ");
		}  

	}
	/*
	 * *****************************************************************************
	 * *
	 */
	/**
	 * 
	 * @return
	 */
	public Boolean detachTo(Object aSuperview) {
		aSuperview.getClass();
		return false;
	}
	/*
	 * *****************************************************************************
	 * *
	 */
	/**
	 * 
	 * @return
	 */
	public Boolean removeFromSuperview() {
		this.getRoot();
		return false;
	}
	/*
	 * *****************************************************************************
	 * *
	 */
	/**
	 * 
	 * @param aIconPath
	 * @return
	 */
	public Boolean setIcon(String aIconPath) {

		Window arootNode = this.getWindow(); 
		ObservableList<Image> aStageIcons = ((Stage) arootNode).getIcons();
		if(aStageIcons.size() >0) {
			return (aStageIcons.set( aStageIcons.size() -1 , new Image(aIconPath)) == null);
		}else {
			return aStageIcons.add( new Image(aIconPath));
		}
	}

	/*
	 * *****************************************************************************
	 * *
	 */
	/**
	 * 
	 * @param image
	 */
	public Boolean setIcon(Image aImage) {
		Parent rootStage = this.getRoot();
		aImage.getClass();
		return false;
	}

}
