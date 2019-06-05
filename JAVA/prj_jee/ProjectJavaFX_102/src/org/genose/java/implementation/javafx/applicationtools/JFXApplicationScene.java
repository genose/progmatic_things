package org.genose.java.implementation.javafx.applicationtools;

import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.util.Observable;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

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
	 * 
	 * @param argModuleName
	 * @throws Exception
	 */
	 
	public JFXApplicationScene(String argModuleName, String argModuleNameFile) throws JFXApplicationException {

		super((new Parent() {

		}));
		String sApplicationPath = (ApplicationJFX.getApplicationBundlePath() + "/").replace("/./", "/");
		Boolean bModuleAsMVC = false;

		// formatting path as [APP_ROOT]/src/[ARGUMENT]/[MVC
		// STYLE(Controller;View;Ressources)]/[ARGUMENT].[FILEEXT]

		String sBasePath = ((ApplicationJFX.applicationPathExist(sApplicationPath + "" + argModuleName + "/"))
				? sApplicationPath + "" + argModuleName + "/"
				: "");

		sBasePath = ((sBasePath.length() <= 1) ? ((ApplicationJFX.applicationPathExist(
				sApplicationPath + "/" + ApplicationJFX.APPLICATION_MVC_DIRS.DIR_APPSRC.getValue() + "/"))
						? sApplicationPath + "/" + ApplicationJFX.APPLICATION_MVC_DIRS.DIR_APPSRC.getValue()
						: sBasePath)
				: sBasePath).replace("/./", "/");

		sBasePath = ((ApplicationJFX.applicationPathExist(sBasePath + "/" + argModuleName))
				? sBasePath + "/" + argModuleName
				: sBasePath).replace("/./", "/");

		sBasePath = ((ApplicationJFX
				.applicationPathExist(sBasePath + "/" + ApplicationJFX.APPLICATION_MVC_DIRS.DIR_ASSETS.getValue()))
						? sBasePath + "/" + ApplicationJFX.APPLICATION_MVC_DIRS.DIR_ASSETS.getValue()
						: sBasePath).replace("/./", "/").replace("//", "/");

 
		if (!ApplicationJFX.applicationPathExist(sBasePath)) {
			throw new JFXApplicationException(getClass() + " can t determine path :: " + sBasePath);
		}

		String sRequestedScene = ((ApplicationJFX
				.applicationPathExist(sBasePath + "/" + ApplicationJFX.APPLICATION_MVC_DIRS.DIR_VIEWS.getValue()))
						? sBasePath + "/" + ApplicationJFX.APPLICATION_MVC_DIRS.DIR_VIEWS.getValue()
						: sBasePath);

		sRequestedScene = (((sRequestedScene != null) && ((argModuleNameFile != null) && argModuleNameFile.length() > 1)
				&& ApplicationJFX.applicationPathExist(sRequestedScene + "/" + argModuleNameFile + ".fxml"))
						? sRequestedScene + "/" + argModuleNameFile + ".fxml"
						: (((sRequestedScene != null)
								&& ApplicationJFX.applicationPathExist(sRequestedScene + "/" + argModuleName + ".fxml"))
										? sRequestedScene + "/" + argModuleName + ".fxml"
										: null));

		String sRequestedSceneCSS = ((ApplicationJFX
				.applicationPathExist(sBasePath + "/" + ApplicationJFX.APPLICATION_MVC_DIRS.DIR_RESSOURCES.getValue()))
						? sBasePath + "/" + ApplicationJFX.APPLICATION_MVC_DIRS.DIR_RESSOURCES.getValue()
						: sBasePath);
		sRequestedSceneCSS = (((sRequestedSceneCSS != null)
				&& ((argModuleNameFile != null) && argModuleNameFile.length() > 1)
				&& ApplicationJFX.applicationPathExist(sRequestedSceneCSS + "/" + argModuleNameFile + ".css"))
						? sRequestedSceneCSS + "/" + argModuleNameFile + ".css"
						: (((sRequestedSceneCSS != null) && ApplicationJFX
								.applicationPathExist(sRequestedSceneCSS + "/" + argModuleName + ".css"))
										? sRequestedSceneCSS + "/" + argModuleName + ".css"
										: null));

		String sRequestedSceneIcon = ((ApplicationJFX
				.applicationPathExist(sBasePath + "/" + ApplicationJFX.APPLICATION_MVC_DIRS.DIR_RESSOURCES.getValue()))
						? sBasePath + "/" + ApplicationJFX.APPLICATION_MVC_DIRS.DIR_RESSOURCES.getValue()
						: sBasePath);
		sRequestedSceneIcon = (((sRequestedSceneIcon != null)
				&& ((argModuleNameFile != null) && argModuleNameFile.length() > 1)
				&& ApplicationJFX.applicationPathExist(sRequestedSceneIcon + "/" + argModuleNameFile + ".png"))
						? sRequestedSceneIcon + "/" + argModuleNameFile + ".png"
						: (((sRequestedSceneIcon != null) && ApplicationJFX
								.applicationPathExist(sRequestedSceneIcon + "/" + argModuleName + ".fxml"))
										? sRequestedSceneIcon + "/" + argModuleName + ".png"
										: null));

		if (sRequestedScene == null) {
			throw new Exception(getClass() + " can t find interface GUI componement :: " + argModuleName);
		}

		// :: https://stackoverflow.com/questions/10121991/javafx-application-icon
		// :: primaryStage.getIcons().add(new
		// Image(getClass().getProtectionDomain().getCodeSource().getLocation()+"/stackoverflow.jpg"));
		if (ApplicationJFX.getPrimaryStage() != null) {

			ApplicationJFX.getPrimaryStage();

			if (ApplicationJFX.applicationPathExist(sRequestedScene)) {
				String surlrequested = ( sRequestedScene).replace("src/", "bin/").replace("//", "/");
				String aUrlClassPath = new URL("file:/"+surlrequested).getPath();// getClass().getResource("/");
		 
				URL aUrlPath = getClass().getResource(aUrlClassPath);
				
				
				if(aUrlClassPath != null) {
					Parent root = FXMLLoader.load(new URL("file:/"+surlrequested));
					if (root != null) {
						this.setRoot(root);
					} else {
						throw new Error(" can't load " + sRequestedScene);
					}  
				}else {
					throw new Error(" can't load " + sRequestedScene);
				}

				// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				// primaryStage.setScene(scene);
			}
		} else {
			throw new InvalidAlgorithmParameterException(" No Primary Stae for this Application ... ");
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

		Parent aparentNode = this.getRoot();
		Parent arootNode = aparentNode.getParent();
		
		// .add(new Image(aIconPath));
		String newpath = aIconPath.strip();
		
		ObservableList<Image> aStageIcons = ((Stage)arootNode).getIcons();
		if(aStageIcons.size() >0)
			aStageIcons.set( aStageIcons.size() -1 , new Image(aIconPath));
		else
			((Stage)arootNode).add( new Image(aIconPath));
		
		return false;
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
