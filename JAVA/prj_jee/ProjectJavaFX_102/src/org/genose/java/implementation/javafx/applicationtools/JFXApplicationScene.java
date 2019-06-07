package org.genose.java.implementation.javafx.applicationtools;



import java.io.IOException;
import java.net.URL;

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
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public JFXApplicationScene(Parent arg0, double arg1, double arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public JFXApplicationScene(Parent arg0, double arg1, double arg2, Paint arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public JFXApplicationScene(Parent arg0, double arg1, double arg2) {
		super(arg0, arg1, arg2);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public JFXApplicationScene(Parent arg0, Paint arg1) {
		super(arg0, arg1);
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

		
		String sBasePathAlt = ((JFXApplication.applicationPathExist(
				sApplicationPath + "/" + JFXApplication.JFXFILETYPE.DIR_APPSRC.getValue() + "/"))
						? sApplicationPath + "/" + JFXApplication.JFXFILETYPE.DIR_APPSRC.getValue()
						: sBasePath);
				
		
		sBasePath = ((sBasePath.length() <= 1) ? sBasePathAlt: sBasePath).replace("/./", "/");

		sBasePath = ((JFXApplication.applicationPathExist(sBasePath + "/" + argModuleName))
				? sBasePath + "/" + argModuleName
				: sBasePath).replace("/./", "/");

		sBasePath = ((JFXApplication
				.applicationPathExist(sBasePath + "/" + JFXApplication.JFXFILETYPE.DIR_ASSETS.getValue()))
						? sBasePath + "/" + JFXApplication.JFXFILETYPE.DIR_ASSETS.getValue()
						: sBasePath).replace("/./", "/").replace("//", "/");

 
		if (!JFXApplication.applicationPathExist(sBasePath)) {
			throw new JFXApplicationException( String.valueOf(getClass() + " can t determine path :: " + sBasePath));
		}

		String sRequestedScene = ((JFXApplication
				.applicationPathExist(sBasePath + "/" + JFXApplication.JFXFILETYPE.DIR_VIEWS.getValue()))
						? sBasePath + "/" + JFXApplication.JFXFILETYPE.DIR_VIEWS.getValue()
						: sBasePath);
		
		
		String sRequestedSceneAlt = (((sRequestedScene != null) && JFXApplication.applicationPathExist(sRequestedScene + "/" + argModuleName + ".fxml"))
										? sRequestedScene + "/" + argModuleName + ".fxml"
										: null);
		
		sRequestedScene = (((sRequestedScene != null) && ((argModuleNameFile != null) && argModuleNameFile.length() > 1)
				&& JFXApplication.applicationPathExist(sRequestedScene + "/" + argModuleNameFile + ".fxml"))
						? sRequestedScene + "/" + argModuleNameFile + ".fxml"
						: sRequestedSceneAlt);

		String sRequestedSceneCSS = ((JFXApplication
				.applicationPathExist(sBasePath + "/" + JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue()))
						? sBasePath + "/" + JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue()
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
				.applicationPathExist(sBasePath + "/" + JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue()))
						? sBasePath + "/" + JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue()
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
					} catch (IOException evERRLOADFXML ) {
						evERRLOADFXML.printStackTrace();
						throw new JFXApplicationException( evERRLOADFXML );
					}
					if (root != null) {
						this.setRoot(root);
					} else {
						throw new JFXApplicationException(" can't load " + sRequestedScene);
					}  
				}else {
					throw new JFXApplicationException(" can't load " + sRequestedScene);
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
	 
		return false;
	}

}
