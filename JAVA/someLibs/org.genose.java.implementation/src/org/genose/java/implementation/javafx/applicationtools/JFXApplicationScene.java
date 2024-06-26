package org.genose.java.implementation.javafx.applicationtools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import org.genose.java.implementation.javafx.applicationtools.JFXApplicationHelper;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationInvalidParameterException;
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
	 * primaryStage.getWidth())); see
	 * https://docs.oracle.com/javafx/2/best_practices/jfxpub-best_practices.htm
	 * 
	 */
	static public JFXApplicationScene createScene(String argModuleName, String argModuleNameFile,
			Function<Object, Boolean> aFuncCallback) throws JFXApplicationException {

		Parent aRootNode = null;
		JFXApplicationScene aSceneNode = null;

		Class aClassReference = JFXApplication.getJFXApplicationSingleton().getClass();
		String sApplicationPath = JFXApplication.getApplicationBundlePath();
		String sApplicationAbsPath = JFXApplication.getApplicationRunnablePathAbsolute();
		Boolean bModuleAsMVC = false;
		String sFilePath = String.valueOf(
				"../../" + argModuleName + "/" + argModuleName + JFXApplication.JFXFILETYPE.FILETYPE_FXML.getValue());
		String sFilePathb = String.valueOf("../../" + argModuleName);

		String sFilePathc = String.valueOf(
				"/" + argModuleName + "/" + argModuleName + JFXApplication.JFXFILETYPE.FILETYPE_FXML.getValue());

		File localClassPath = new java.io.File(sFilePath);

		File localClassPathb = new java.io.File(sFilePathb);
		File localClassPathc = new java.io.File(sFilePathc);

		Boolean bExist = localClassPath.exists();
		URL aPathUrl = aClassReference.getResource(sFilePath);
		URL aPathUrlAlt = aClassReference.getResource(localClassPath.getPath());
		URL aPathUrlAltb = aClassReference.getResource(localClassPath.getAbsolutePath());

		System.out
				.println("JFXApplicationScene.createScene() : " + aPathUrl + "\n" + aPathUrlAlt + "\n" + aPathUrlAltb);

		Boolean bExistb = localClassPathb.exists();
		URL aPathUrlb = aClassReference.getResource(sFilePathb);

		Boolean bExistc = localClassPathc.exists();
		URL aPathUrlc = aClassReference.getResource(sFilePathc);

		/* ****************************************************************** */
		// formatting path as
		// [APP_ROOT]/src/[ARGUMENT]/[MVC_STYLE(Controller;View;Ressources)]/[ARGUMENT].[FILEEXT]
		/* ****************************************************************** */
		// check out entry point of this Module
		/* ****************************************************************** */
		Path aPathInBundle = null;

		String sBasePath = null;

		sBasePath = resolveModulePathInsenstiveCase(sApplicationPath, JFXApplication.JFXFILETYPE.DIR_APPSRC.getValue(),
				sApplicationPath);

		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out module folder in this Module
		/* ****************************************************************** */
		sBasePath = resolveModulePathInsenstiveCase(sBasePath, argModuleName, null);

		/* ****************************************************************** */
		if (sBasePath == null) {
			throw new JFXApplicationException(String
					.valueOf(JFXApplicationScene.class.getName() + " can t determine path for :: " + argModuleName));
		}
		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out src folder in this Module ;; Module/src
		/* ****************************************************************** */
		String sBasePathModuleSrc = resolveModulePathInsenstiveCase(sBasePath,
				JFXApplication.JFXFILETYPE.DIR_APPSRC.getValue(), null);
		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out assets folder in this Module ;; Module/assets
		/* ****************************************************************** */
		String sBasePathModuleAsset = resolveModulePathInsenstiveCase(sBasePath,
				JFXApplication.JFXFILETYPE.DIR_ASSETS.getValue(), null);
		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out ressources folder in this Module ;; Module/Ressources
		/* ****************************************************************** */
		aPathInBundle = Paths.get(sBasePath, JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue());
		String sRequestedRessourcesDir = resolveModulePathInsenstiveCase(sBasePath,
				JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue(), null);

		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out MVC View folder in this Module ;; Module/assets/views ... or ...
		// Module/views
		/* ****************************************************************** */
		String sRequestedSceneViewsFolder = resolveModulePathInsenstiveCase(sBasePathModuleAsset,
				JFXApplication.JFXFILETYPE.DIR_VIEWS.getValue(),
				resolveModulePathInsenstiveCase(sBasePath, JFXApplication.JFXFILETYPE.DIR_VIEWS.getValue(), sBasePath));
		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out specific FXML in this Module ;;
		// Module/[assets/]views/[ModuleName;ModuleNameFile].fmxl
		/* ****************************************************************** */
		String sRequestedSceneFile = ((argModuleNameFile != null)
				? argModuleNameFile + JFXApplication.JFXFILETYPE.FILETYPE_FXML.getValue()
				: argModuleName + JFXApplication.JFXFILETYPE.FILETYPE_FXML.getValue());
		sRequestedSceneFile = resolveModulePathInsenstiveCase(sRequestedSceneViewsFolder, sRequestedSceneFile, null);

		if (sRequestedSceneFile == null) {
			throw new JFXApplicationException(
					aClassReference.getName() + " can t find interface GUI componement for " + argModuleName);
		}
		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out specific CSS in this Module ;;
		// Module/[assets/]Ressources/[ModuleName;ModuleNameFile].css
		/* ****************************************************************** */

		String sRequestedSceneCSS = ((argModuleNameFile != null)
				? argModuleNameFile + JFXApplication.JFXFILETYPE.FILETYPE_FCSS.getValue()
				: argModuleName + JFXApplication.JFXFILETYPE.FILETYPE_FCSS.getValue());
		sRequestedSceneCSS = resolveModulePathInsenstiveCase(sRequestedRessourcesDir, sRequestedSceneFile, null);
		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out for PNG icon ;;
		// Module/[assets/]Ressources/[ModuleName;ModuleNameFile].png
		/* ****************************************************************** */
		String sRequestedSceneIcon = ((argModuleNameFile != null)
				? argModuleNameFile + JFXApplication.JFXFILETYPE.FILETYPE_PNG.getValue()
				: argModuleName + JFXApplication.JFXFILETYPE.FILETYPE_PNG.getValue());
		sRequestedSceneIcon = resolveModulePathInsenstiveCase(sRequestedRessourcesDir, sRequestedSceneIcon, null);
		/* ****************************************************************** */
		// :: https://stackoverflow.com/questions/10121991/javafx-application-icon

		if (JFXApplication.getPrimaryStage() != null) {

			try {
				URL aUrlRequestedScenePath = aClassReference.getResource(sFilePath);
				if (aUrlRequestedScenePath != null) {
					aRootNode = FXMLLoader.load(aUrlRequestedScenePath);
				} else {
					throw new JFXApplicationException(" can't load " + sRequestedSceneFile);
				}

				if (aRootNode != null) {
					aSceneNode = new JFXApplicationScene(aRootNode);

					if (sRequestedSceneCSS != null) {
						String aURLforCSS = aClassReference.getResource("application.css").toExternalForm();
						((Scene) aSceneNode).getStylesheets().add(aURLforCSS);
					}
					// Setup icon
					if ((sRequestedSceneIcon != null) && GNSJavaClassHelper.respondsTo("setIcon", aSceneNode)) {
						aSceneNode.setIcon(sRequestedSceneIcon);
					}

				} else {
					throw new JFXApplicationException(" can't load " + sRequestedSceneFile);
				}

			} catch (IOException evERRLOADFXML) {
				JFXApplicationLogger.getLogger().logError(
						String.format("Unable to load requested file (%s) %n ;; cause of returned error ",sRequestedSceneFile), evERRLOADFXML);
				throw new JFXApplicationException(evERRLOADFXML);
			}

		} else {
			throw new JFXApplicationInvalidParameterException(
					" No Primary Stage for this Application ... %n Ensure you called super([Stage.class]) on your Main() ");
		}
		/* ******************************** */
		try {

			if (aFuncCallback != null) {
				Boolean bCallBackResult = aFuncCallback.apply(aSceneNode);
				JFXApplicationLogger.getLogger().logInfo(aSceneNode.getClass(),
						String.format("Callback returned %i", bCallBackResult));
			}

		} catch (Exception evErrCallBack) {
			JFXApplication.getJFXApplicationSingleton().getLogger().logError(aClassReference, evErrCallBack);
			throw evErrCallBack;
		}
		return aSceneNode;
	}

	/**
	 * @param sApplicationPath
	 * @param aPathInBundle
	 * @return
	 */
	private static String resolveModulePathInsenstiveCase(String sApplicationPath, String sRequiredPathIn,
			String sAltPathIfFail) {
		String sBasePath = null;
		if ((sApplicationPath != null) && (sRequiredPathIn != null)) {
			Path aPathInBundle = Paths.get(sApplicationPath, sRequiredPathIn);

			sBasePath = ((JFXApplication.applicationPathExist(aPathInBundle.toString())) ? aPathInBundle.toString()
					: null);

			if (sBasePath == null) {
				aPathInBundle = Paths.get(sApplicationPath, String.valueOf(sRequiredPathIn).toLowerCase());

				sBasePath = ((JFXApplication.applicationPathExist(aPathInBundle.toString())) ? aPathInBundle.toString()
						: null);
			}

			if (sBasePath == null) {
				aPathInBundle = Paths.get(sApplicationPath, String.valueOf(sRequiredPathIn).toUpperCase());

				sBasePath = ((JFXApplication.applicationPathExist(aPathInBundle.toString())) ? aPathInBundle.toString()
						: null);
			}

		}

		if (((sAltPathIfFail != null) && String.valueOf(sAltPathIfFail).length() > 1) && (sBasePath == null)) {
			sBasePath = ((JFXApplication.applicationPathExist(sAltPathIfFail)) ? String.valueOf(sAltPathIfFail) : null);
		}

		return sBasePath;
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
