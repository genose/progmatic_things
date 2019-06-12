package org.genose.java.implementation.javafx.applicationtools;



import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.function.Function;

import org.genose.java.implementation.javafx.applicationtools.JFXApplicationHelper;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationBundle;

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

	private String aSceneIdentifier = null;
	/* ************************************************ */
	/**
	 * 
	 * 
	 */
	public JFXApplicationScene() {
		super(null);
	}
	
	/* ************************************************ */
	/**
	 * Replace something like this : 
	 *  Parent root = FXMLLoader.load(getClass().getResource("/MainWindow/MainWindow.fxml"));
        primaryStage.setScene(new Scene(root, primaryStage.getHeight(), primaryStage.getWidth()));
	 * 
	 * @param argModuleName
	 * @throws JFXApplicationException  
	 * @throws Exception
	 */
	 
	public JFXApplicationScene(String argModuleName, String argModuleNameFile, Function<Object, Boolean> aFuncCallback ) throws JFXApplicationException {

		super((new Parent() {

		}));
		
		createScene(argModuleName, argModuleNameFile);

		
		if(aFuncCallback != null) { 
			Boolean bCallBackResult = aFuncCallback.apply(null);
		}
	}
	/* ************************************************ */
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
/* ************************************************ */
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
	/* ************************************************ */
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public JFXApplicationScene(Parent arg0, double arg1, double arg2) {
		super(arg0, arg1, arg2);
	}
	/* ************************************************ */
	/**
	 * @param arg0
	 * @param arg1
	 */
	public JFXApplicationScene(Parent arg0, Paint arg1) {
		super(arg0, arg1);
		
	}
	/* ************************************************ */
	/**
	 * @param arg0
	 */
	public JFXApplicationScene(Parent arg0) {
		super(arg0);
	}


	/* ************************************************ */
	/**
	 * @param argModuleName
	 * @param argModuleNameFile
	 * @param argCallBack
	 * @throws JFXApplicationException
	 * 
	 */
	protected boolean createScene(String argModuleName, String argModuleNameFile)
			throws JFXApplicationException {
		String sApplicationPath = (JFXApplication.getApplicationBundlePath() + File.pathSeparator).replace(File.pathSeparator+"."+File.pathSeparator, File.pathSeparator);
		Boolean bModuleAsMVC = false;

		// formatting path as [APP_ROOT]/src/[ARGUMENT]/[MVC_STYLE(Controller;View;Ressources)]/[ARGUMENT].[FILEEXT]

		String sBasePath = ((JFXApplication.applicationPathExist(sApplicationPath + "" + argModuleName + File.pathSeparator))
				? sApplicationPath + "" + argModuleName + File.pathSeparator
				: "");

		
		String sBasePathAlt = ((JFXApplication.applicationPathExist(
				sApplicationPath + File.pathSeparator + JFXApplication.JFXFILETYPE.DIR_APPSRC.getValue() + File.pathSeparator))
						? sApplicationPath + File.pathSeparator + JFXApplication.JFXFILETYPE.DIR_APPSRC.getValue()
						: sBasePath);
				
		
		sBasePath = ((sBasePath.length() <= 1) ? sBasePathAlt: sBasePath).replace(File.pathSeparator+"."+File.pathSeparator, File.pathSeparator);

		sBasePath = ((JFXApplication.applicationPathExist(sBasePath + File.pathSeparator + argModuleName))
				? sBasePath + File.pathSeparator + argModuleName
				: sBasePath).replace("/./", File.pathSeparator);

		sBasePath = ((JFXApplication
				.applicationPathExist(sBasePath + File.pathSeparator + JFXApplication.JFXFILETYPE.DIR_ASSETS.getValue()))
						? sBasePath + File.pathSeparator + JFXApplication.JFXFILETYPE.DIR_ASSETS.getValue()
						: sBasePath).replace(File.pathSeparator+"."+File.pathSeparator, File.pathSeparator).replace(File.pathSeparator+""+File.pathSeparator, File.pathSeparator);

 
		if (!JFXApplication.applicationPathExist(sBasePath)) {
			throw new JFXApplicationException( String.valueOf(getClass() + " can t determine path :: " + sBasePath));
		}

		String sRequestedScene = ((JFXApplication
				.applicationPathExist(sBasePath + File.pathSeparator + JFXApplication.JFXFILETYPE.DIR_VIEWS.getValue()))
						? sBasePath + File.pathSeparator + JFXApplication.JFXFILETYPE.DIR_VIEWS.getValue()
						: sBasePath);
		
		
		String sRequestedSceneAlt = (((sRequestedScene != null) && JFXApplication.applicationPathExist(sRequestedScene + File.pathSeparator + argModuleName + JFXApplication.JFXFILETYPE.FILETYPE_FXML.toString() ))
										? sRequestedScene + File.pathSeparator + argModuleName + JFXApplication.JFXFILETYPE.FILETYPE_FXML.toString()
										: null);
		
		sRequestedScene = (((sRequestedScene != null) && ((argModuleNameFile != null) && argModuleNameFile.length() > 1)
				&& JFXApplication.applicationPathExist(sRequestedScene + File.pathSeparator + argModuleNameFile + JFXApplication.JFXFILETYPE.FILETYPE_FXML.toString()))
						? sRequestedScene + File.pathSeparator + argModuleNameFile + JFXApplication.JFXFILETYPE.FILETYPE_FXML.toString()
						: sRequestedSceneAlt);

		String sRequestedSceneCSS = ((JFXApplication
				.applicationPathExist(sBasePath + File.pathSeparator + JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue()))
						? sBasePath + File.pathSeparator + JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue()
						: sBasePath);
		
		String sRequestedSceneCSSAlt = (((sRequestedSceneCSS != null) && JFXApplication
								.applicationPathExist(sRequestedSceneCSS + File.pathSeparator + argModuleName + JFXApplication.JFXFILETYPE.FILETYPE_FCSS.toString()))
										? sRequestedSceneCSS + File.pathSeparator + argModuleName + JFXApplication.JFXFILETYPE.FILETYPE_FCSS.toString()
										: null);
		
		sRequestedSceneCSS = (((sRequestedSceneCSS != null)
				&& ((argModuleNameFile != null) && argModuleNameFile.length() > 1)
				&& JFXApplication.applicationPathExist(sRequestedSceneCSS + File.pathSeparator + argModuleNameFile + JFXApplication.JFXFILETYPE.FILETYPE_FCSS.toString()))
						? sRequestedSceneCSS + File.pathSeparator + argModuleNameFile + JFXApplication.JFXFILETYPE.FILETYPE_FCSS.toString()
						: sRequestedSceneCSSAlt);

		String sRequestedSceneIcon = ((JFXApplication
				.applicationPathExist(sBasePath + File.pathSeparator + JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue()))
						? sBasePath + File.pathSeparator + JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue()
						: null);
		
		String sRequestedSceneIconAlt = (((sRequestedSceneIcon != null) && JFXApplication
								.applicationPathExist(sRequestedSceneIcon + File.pathSeparator + argModuleName + JFXApplication.JFXFILETYPE.FILETYPE_FXML.toString()))
										? sRequestedSceneIcon + File.pathSeparator + argModuleName + JFXApplication.JFXFILETYPE.FILETYPE_PNG.toString()
										: null);
		
		sRequestedSceneIcon = (((sRequestedSceneIcon != null)
				&& ((argModuleNameFile != null) && argModuleNameFile.length() > 1)
				&& JFXApplication.applicationPathExist(sRequestedSceneIcon + File.pathSeparator + argModuleNameFile + JFXApplication.JFXFILETYPE.FILETYPE_PNG.toString()))
						? sRequestedSceneIcon + File.pathSeparator + argModuleNameFile + JFXApplication.JFXFILETYPE.FILETYPE_PNG.toString()
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
						
						throw new JFXApplicationException( evERRLOADFXML );
					}
					if (root != null) {
						this.setRoot(root);
					} else {
						throw new JFXApplicationException(" can't load " + sRequestedScene);
					}  
					
					if(JFXApplicationBundle.fileExist()) {
						String aURLforCSS = getClass().getResource("application.css").toExternalForm();
						this.getStylesheets().add(aURLforCSS);
					}
					

				}else {
					throw new JFXApplicationException(" can't load " + sRequestedScene);
				}
			}
		} else {
			throw new JFXApplicationInvalidParameterException(" No Primary Stae for this Application ... ");
		}
		/* ******************************** */
		return true;
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
