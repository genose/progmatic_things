package org.genose.java.implementation.javafx.applicationtools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import org.genose.java.implementation.javafx.applicationtools.JFXApplicationHelper;
import org.genose.java.implementation.GNSJavaClassHelper;
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
	private Parent aSceneRootNode = null;

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
	/*
	 * * Replace something like this : Parent root =
	 * FXMLLoader.load(getClass().getResource("/MainWindow/MainWindow.fxml"));
	 * primaryStage.setScene(new Scene(root, primaryStage.getHeight(),
	 * primaryStage.getWidth()));
	 * 
	 */
	static public JFXApplicationScene createScene(String argModuleName, String argModuleNameFile,
			Function<Object, Boolean> aFuncCallback) throws JFXApplicationException {

		Parent aRootNode = null;
		JFXApplicationScene aSceneNode = null;
		
		Class aClassReference = JFXApplicationScene.class.getClass(); 
		
		String sApplicationPath = JFXApplication.getApplicationBundlePath();
		String sApplicationAbsPath = JFXApplication.getApplicationRunnablePathAbsolute();
		Boolean bModuleAsMVC = false;

		// formatting path as
		// [APP_ROOT]/src/[ARGUMENT]/[MVC_STYLE(Controller;View;Ressources)]/[ARGUMENT].[FILEEXT]

		// check out entry point of this Module
		Path aPathInBundle = Paths.get(sApplicationPath, argModuleName);
		
		String sBasePath = ((JFXApplication.applicationPathExist(aPathInBundle.toString())) ? aPathInBundle.toString() : sApplicationPath);

		if (!JFXApplication.applicationPathExist(sBasePath)) {
			throw new JFXApplicationException(String.valueOf(JFXApplicationScene.class.getName() + " can t determine path :: " + sBasePath));
		}
		
		// check out src folder in this Module
		aPathInBundle = Paths.get(sBasePath, JFXApplication.JFXFILETYPE.DIR_APPSRC.getValue());
		String sBasePathModuleSrc = ((JFXApplication.applicationPathExist(aPathInBundle.toString())) ? aPathInBundle.toString() : sBasePath);
		
		// check out assets folder in this Module
		aPathInBundle = Paths.get(sBasePath, JFXApplication.JFXFILETYPE.DIR_ASSETS.getValue());
		String sBasePathModuleAsset = ((JFXApplication.applicationPathExist(aPathInBundle.toString())) ? aPathInBundle.toString() : sBasePath);
		
		// check out ressources folder in this Module
		aPathInBundle = Paths.get(sBasePath, JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue());
		String sRequestedRessourcesDir = ((JFXApplication.applicationPathExist(aPathInBundle.toString())) ? aPathInBundle.toString() : sBasePath) ;
		
	
		// check out MVC View folder in this Module
		aPathInBundle = Paths.get(sBasePath, JFXApplication.JFXFILETYPE.DIR_VIEWS.getValue() );
		String sRequestedSceneViewsFolder = ((JFXApplication.applicationPathExist(aPathInBundle.toString())) ? aPathInBundle.toString() : sBasePath);

		// check out specific FXML in this Module
		aPathInBundle = Paths.get(sRequestedSceneViewsFolder, ((argModuleNameFile != null)? argModuleNameFile+JFXApplication.JFXFILETYPE.FILETYPE_FXML.getValue(): argModuleName+JFXApplication.JFXFILETYPE.FILETYPE_FXML.getValue())  );
		String sRequestedSceneFile = ((JFXApplication.applicationPathExist(aPathInBundle.toString())) ? aPathInBundle.toString() : null);
		
		if (sRequestedSceneFile == null) {
			throw new JFXApplicationException(aClassReference.getName() + " can t find interface GUI componement for " + argModuleName);
		}
		// check out specific CSS in this Module
		aPathInBundle = Paths.get(sRequestedRessourcesDir, ((argModuleNameFile != null)? argModuleNameFile+JFXApplication.JFXFILETYPE.FILETYPE_FCSS.getValue(): argModuleName+JFXApplication.JFXFILETYPE.FILETYPE_FCSS.getValue())  );
		String sRequestedSceneCSS = null;
		
		if(!JFXApplication.applicationPathExist(aPathInBundle.toString())) {
			// no CSS in this path, try another path
			aPathInBundle = Paths.get(sRequestedSceneViewsFolder, JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue(), ((argModuleNameFile != null)? argModuleNameFile+JFXApplication.JFXFILETYPE.FILETYPE_FCSS.getValue(): argModuleName+JFXApplication.JFXFILETYPE.FILETYPE_FCSS.getValue()) );
			if(JFXApplication.applicationPathExist(aPathInBundle.toString())) {
				sRequestedSceneCSS =  aPathInBundle.toString();
			}
		}else {
			sRequestedSceneCSS =  aPathInBundle.toString();
		}

		aPathInBundle = Paths.get(sRequestedRessourcesDir, ((argModuleNameFile != null)? argModuleNameFile+JFXApplication.JFXFILETYPE.FILETYPE_PNG.getValue(): argModuleName+JFXApplication.JFXFILETYPE.FILETYPE_PNG.getValue())  );
		
		
		String  sRequestedSceneIcon = null ;
		
		if(!JFXApplication.applicationPathExist(aPathInBundle.toString())) {
			// no CSS in this path, try another path
			aPathInBundle = Paths.get(sRequestedSceneViewsFolder, JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue(), ((argModuleNameFile != null)? argModuleNameFile+JFXApplication.JFXFILETYPE.FILETYPE_PNG.getValue(): argModuleName+JFXApplication.JFXFILETYPE.FILETYPE_PNG.getValue()) );
			if(JFXApplication.applicationPathExist(aPathInBundle.toString())) {
				sRequestedSceneIcon =  aPathInBundle.toString();
			}
		}else {
			sRequestedSceneIcon =  aPathInBundle.toString();
		}


		// :: https://stackoverflow.com/questions/10121991/javafx-application-icon

		if (JFXApplication.getPrimaryStage() != null) {
			
				File aScenePath = new java.io.File( sRequestedSceneFile );
				String scenepath = aScenePath.toPath().toString().replace(sApplicationAbsPath, "");
				URL aUrlPath = aClassReference.getResource(scenepath);

				if (aUrlPath != null) {
					
					try {
						aRootNode = FXMLLoader.load(aUrlPath);
						
						
					} catch (IOException evERRLOADFXML) {

						throw new JFXApplicationException(evERRLOADFXML);
					}
					
					if (aRootNode == null)  {
						throw new JFXApplicationException(" can't load " + sRequestedSceneFile);
					}else {
						aSceneNode = new JFXApplicationScene(aRootNode);
						 
						if (sRequestedSceneCSS != null) {
							String aURLforCSS = aClassReference.getResource("application.css").toExternalForm();
							((Scene)aSceneNode).getStylesheets().add(aURLforCSS);
						} 
						// Setup icon 
						if( (sRequestedSceneIcon != null) &&  GNSJavaClassHelper.respondsTo("setIcon",aSceneNode)) {
							aSceneNode.setIcon(sRequestedSceneIcon);
						}
					}
					
					
				} else {
					throw new JFXApplicationException(" can't load " + sRequestedSceneFile);
				}
			 
		} else {
			throw new JFXApplicationInvalidParameterException(" No Primary Stage for this Application ... %n Ensure you called super([Stage.class]) on your Main() ");
		}
		/* ******************************** */

		if (aFuncCallback != null) {
			Boolean bCallBackResult = aFuncCallback.apply(aSceneNode);
			JFXApplicationLogger.getLogger().logInfo(aSceneNode.getClass(), String.format("Callback returned %i", bCallBackResult));
		}
		
		return aSceneNode;
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
		if (aStageIcons.size() > 0) {
			return (aStageIcons.set(aStageIcons.size() - 1, new Image(aIconPath)) == null);
		} else {
			return aStageIcons.add(new Image(aIconPath));
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
