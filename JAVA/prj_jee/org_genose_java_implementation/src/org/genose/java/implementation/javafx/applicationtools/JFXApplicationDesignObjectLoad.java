package org.genose.java.implementation.javafx.applicationtools;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import javafx.application.Application;
import javafx.stage.Stage;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationInvalidParameterException;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationRuntimeException;
import org.genose.java.implementation.javafx.applicationtools.threadstasks.JFXApplicationScheduledTask;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationScene;
import org.genose.java.implementation.javafx.applicationtools.views.customviewscontroller.JFXApplicationCustomControlComboxBoxAutoFill;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public abstract interface JFXApplicationDesignObjectLoad {

	public static JFXApplicationScene create(String argModuleName, String argModuleNameFile,
			JFXApplicationCallback aFuncCallback) throws JFXApplicationException {

		return (JFXApplicationScene) JFXApplicationDesignObjectLoad.create(argModuleName, argModuleNameFile,
				aFuncCallback, false);

	}

	public static <T> Object create(String argModuleName, String argModuleNameFile,
			JFXApplicationCallback aFuncCallback, Boolean bReturnOnlyDesignNode) throws JFXApplicationException {

		if(JFXApplicationHelper.getApplicationMain() == null ){
			throw new JFXApplicationException(" Unable to determine Main Class ....");
		}


		FXMLLoader aRootNodeLoader = null;
		Parent aRootNode = null;
		Object oFXMLRootNode = null;
		Object aParentRootNode = null;
		Object aController = null;

		JFXApplicationScene aSceneNode = null;

		Class aClassReference = JFXApplicationHelper.class.getClass();
		Application refApplication = null;
		if(JFXApplication.singletonInstanceExists())
		{
			refApplication = JFXApplication.getJFXApplicationSingleton();
            aClassReference = refApplication.getClass();
		}

		//System.out.println("Library using Ref class APPMain as JFXApplication::"+String.valueOf(refApplication));
		String sApplicationPath = JFXApplication.getApplicationBundlePath();
		String sApplicationAbsPath = JFXApplication.getApplicationRunnablePathAbsolute();
		Boolean bModuleAsMVC = false;

		/* ****************************************************************** */
		// formatting path as
		// [APP_ROOT]/src/[ARGUMENT]/[MVC_STYLE(Controller;View;Ressources)]/[ARGUMENT].[FILEEXT]
		/* ****************************************************************** */
		// check out entry point of this Module
		/* ****************************************************************** */

		String sBasePath = null;
		Path aPathInBundle = null;

		sBasePath = JFXApplicationHelper.resolveModulePathInsenstiveCase(sApplicationPath,
				JFXApplication.JFXFILETYPE.DIR_APPSRC.getValue(), sApplicationPath);

		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out module folder in this Module
		/* ****************************************************************** */
		sBasePath = JFXApplicationHelper.resolveModulePathInsenstiveCase(sBasePath, argModuleName,
				((argModuleName == null) ? sBasePath : null));

		/* ****************************************************************** */
		if (sBasePath == null) {
			throw new JFXApplicationException(String
					.valueOf(JFXApplicationScene.class.getName() + " can t determine path for :: " + argModuleName));
		}
		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out src folder in this Module ;; Module/src
		/* ****************************************************************** */
		String sBasePathModuleSrc = JFXApplicationHelper.resolveModulePathInsenstiveCase(sBasePath,
				JFXApplication.JFXFILETYPE.DIR_APPSRC.getValue(), null);
		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out assets folder in this Module ;; Module/assets
		/* ****************************************************************** */
		aPathInBundle = Paths.get(sBasePath, JFXApplication.JFXFILETYPE.DIR_ASSETS.getValue());
		String sBasePathModuleAsset = JFXApplicationHelper.resolveModulePathInsenstiveCase(sBasePath,
				JFXApplication.JFXFILETYPE.DIR_ASSETS.getValue(), null);

		System.out.println(" MVC "+JFXApplication.JFXFILETYPE.DIR_ASSETS.getValue()+" folder for ("+aPathInBundle+") "+JFXApplication.JFXFILETYPE.DIR_ASSETS.getValue()+" = "+String.valueOf(sBasePathModuleAsset));



		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out ressources folder in this Module ;; Module/Ressources
		/* ****************************************************************** */
		aPathInBundle = Paths.get(sBasePath, JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue());
		String sRequestedRessourcesDir = JFXApplicationHelper.resolveModulePathInsenstiveCase(sBasePathModuleAsset,
				JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue(), null);

		if(sRequestedRessourcesDir == null){
			aPathInBundle = Paths.get(sBasePath, JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue());
			sRequestedRessourcesDir = JFXApplicationHelper.resolveModulePathInsenstiveCase(aPathInBundle.toString(),
					JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue(), null);
		}

		System.out.println(" MVC "+JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue()+" folder for ("+aPathInBundle+" ) "+JFXApplication.JFXFILETYPE.DIR_RESSOURCES.getValue()+" = "+String.valueOf(sRequestedRessourcesDir)+") ");
		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out MVC View folder in this Module ;; Module/assets/views ... or ...
		// Module/views
		/* ****************************************************************** */
		aPathInBundle = Paths.get(sBasePath, JFXApplication.JFXFILETYPE.DIR_VIEWS.getValue());
		String sRequestedSceneViewsFolder = JFXApplicationHelper.resolveModulePathInsenstiveCase(
							sBasePathModuleAsset,
							JFXApplication.JFXFILETYPE.DIR_VIEWS.getValue(),
							null
		);

		if(sRequestedSceneViewsFolder == null){
			sRequestedSceneViewsFolder = JFXApplicationHelper.resolveModulePathInsenstiveCase( sBasePath,
					JFXApplication.JFXFILETYPE.DIR_VIEWS.getValue(),
					sBasePath);

		}

		System.out.println(" MVC "+JFXApplication.JFXFILETYPE.DIR_VIEWS.getValue()+" folder for ("+aPathInBundle+") "+JFXApplication.JFXFILETYPE.DIR_VIEWS.getValue()+" = "+String.valueOf(sRequestedSceneViewsFolder));
		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out specific FXML in this Module ;;
		// Module/[assets/]views/[ModuleName;ModuleNameFile].fmxl
		/* ****************************************************************** */

		String sRequestedSceneFile = ((argModuleNameFile != null)
				? argModuleNameFile + JFXApplication.JFXFILETYPE.FILETYPE_FXML.getValue()
				: argModuleName + JFXApplication.JFXFILETYPE.FILETYPE_FXML.getValue());

		sRequestedSceneFile = JFXApplicationHelper.resolveModulePathInsenstiveCase(sRequestedSceneViewsFolder,
				sRequestedSceneFile, null);

		System.out.println(" MVC "+JFXApplication.JFXFILETYPE.DIR_VIEWS.getValue()+" FILE for ("+String.valueOf(aPathInBundle)+")  = " +String.valueOf(sRequestedSceneFile));

		if (sRequestedSceneFile == null) {
			throw new JFXApplicationException(aClassReference.getName() + " can t find interface GUI componement ("
					+ String.valueOf(argModuleNameFile) + ") for " + argModuleName);
		}
		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out specific CSS in this Module ;;
		// Module/[assets/]Ressources/[ModuleName;ModuleNameFile].css
		/* ****************************************************************** */

		String sRequestedSceneCSS = ((argModuleNameFile != null)
				? argModuleNameFile + JFXApplication.JFXFILETYPE.FILETYPE_FCSS.getValue()
				: argModuleName + JFXApplication.JFXFILETYPE.FILETYPE_FCSS.getValue());
		sRequestedSceneCSS = JFXApplicationHelper.resolveModulePathInsenstiveCase(sRequestedRessourcesDir,
				sRequestedSceneFile, null);

		if(sRequestedSceneCSS == null){
			sRequestedSceneCSS = JFXApplicationHelper.resolveModulePathInsenstiveCase(sRequestedSceneViewsFolder,
					sRequestedSceneFile, null);
		}

		System.out.println(" MVC "+JFXApplication.JFXFILETYPE.FILETYPE_FCSS.getValue()+" FILE for ("+String.valueOf(aPathInBundle)+")  = " +String.valueOf(sRequestedSceneCSS));

		/* ****************************************************************** */
		/* ****************************************************************** */
		// check out for PNG icon ;;
		// Module/[assets/]Ressources/[ModuleName;ModuleNameFile].png
		/* ****************************************************************** */
		String sRequestedSceneIcon = ((argModuleNameFile != null)
				? argModuleNameFile + JFXApplication.JFXFILETYPE.FILETYPE_FPNG.getValue()
				: argModuleName + JFXApplication.JFXFILETYPE.FILETYPE_FPNG.getValue());
		sRequestedSceneIcon = JFXApplicationHelper.resolveModulePathInsenstiveCase(sRequestedRessourcesDir,
				sRequestedSceneIcon, null);

		if(sRequestedSceneCSS == null){
			sRequestedSceneCSS = JFXApplicationHelper.resolveModulePathInsenstiveCase(sRequestedSceneViewsFolder,
					sRequestedSceneFile, null);
		}

		System.out.println(" MVC "+JFXApplication.JFXFILETYPE.FILETYPE_FPNG.getValue()+" FILE for ("+String.valueOf(aPathInBundle)+")  = " +String.valueOf(sRequestedSceneIcon));

		/* ****************************************************************** */
		// :: https://stackoverflow.com/questions/10121991/javafx-application-icon
		// https://stackoverflow.com/questions/34941411/how-to-get-controller-of-scene-in-javafx8
		if (JFXApplication.getJFXApplicationSingleton().getPrimaryStage() != null) {

			try {
				URL aUrlRequestedDesignPath = aClassReference.getResource(sRequestedSceneFile);
				if (aUrlRequestedDesignPath != null) {
					System.out.println(" Try Loading : " + aUrlRequestedDesignPath);
					aRootNodeLoader = new FXMLLoader(aUrlRequestedDesignPath);

					oFXMLRootNode = aRootNodeLoader.getRoot();

					if (oFXMLRootNode == null) {
						System.out.println(" Root is Null  or Dynamic root ...");

						aController = aRootNodeLoader.getController();
						if (aController == null) {
							System.out.println(" Root Controller is Null  or Dynamic root ...");
						}
					}

					aRootNode = aRootNodeLoader.load();

				} else {
					throw new JFXApplicationException(" can't find / load " + sRequestedSceneFile);
				}
				/* **************************************** */
				if (aRootNode != null) {

					Object oControllerView = aRootNodeLoader.getController();

					if (oControllerView == null) {
						JFXApplicationLogger.getLogger().logError(aRootNode.getClass(),
								String.format("Warning : Loaded controler is null ... for (%s) of type (%s) :: (%s)",
										sRequestedSceneFile, aRootNode.getScene(), aRootNode.getClass()));
					}

					if (bReturnOnlyDesignNode) {

						aRootNode.setUserData(oControllerView);

					} else {
						aSceneNode = new JFXApplicationScene(aRootNode);
						aSceneNode.setRootController(oControllerView);
						aSceneNode.setUserData(aRootNodeLoader);
						// Setup icon
						if ((sRequestedSceneIcon != null)
								&& JFXApplicationClassHelper.respondsTo(aSceneNode, "setIcon")) {
							aSceneNode.setIcon(sRequestedSceneIcon);
						}
					}

					if (sRequestedSceneCSS != null) {
						String aURLforCSS = aClassReference.getResource("application.css").toExternalForm();
						if (bReturnOnlyDesignNode) {
							(aRootNode).getStylesheets().add(aURLforCSS);
						} else {
							((Scene) aSceneNode).getStylesheets().add(aURLforCSS);
						}
					}

				} else {
					JFXApplicationException.raiseToFront(JFXApplicationScene.class,
							new JFXApplicationException(" can't obtain to load " + sRequestedSceneFile), true);
				}

			} catch (Exception evERRLOADFXML) {
				String sFormattedErrorCause = String.format(
						"Unable to load requested file (%s : %s : %s) %n ;; cause of returned error ", argModuleName,
						argModuleNameFile, sRequestedSceneFile);

				JFXApplicationException.raiseToFront(JFXApplicationScene.class, evERRLOADFXML, true);
				throw new JFXApplicationException(evERRLOADFXML);
			}

		} else {
			JFXApplicationException.raiseToFront(JFXApplicationScene.class, new JFXApplicationInvalidParameterException(
					" No Primary Stage for this Application ... %n Ensure you called super(["+ Stage.class.getSimpleName() +".class]) on your "+ Objects.requireNonNullElse(JFXApplicationHelper.getApplicationMain(), Object.class).getClass().getSimpleName()+"() "),
					true);
		}
		/* ******************************** */
		try {

			if (aFuncCallback != null) {

				JFXApplicationScheduledTask aTimerTaskCallback = new JFXApplicationScheduledTask();

				aTimerTaskCallback.setCallback(aFuncCallback, ((bReturnOnlyDesignNode) ? aRootNode : aSceneNode));
				aTimerTaskCallback.setUserDatas(null);

				aTimerTaskCallback.schedule();

			}

		} catch (Exception evErrCallBack) {
			JFXApplication.getJFXApplicationSingleton().getLogger().logError(aClassReference, evErrCallBack);
			throw evErrCallBack;
		}
		return ((bReturnOnlyDesignNode) ? aRootNode : aSceneNode);
	}

	/**
	 * 
	 * @param <T>
	 * @return dynamic Object<T<Controller>> type in User Data ...
	 */
	public static <T> Parent create(Class<T> aObjectClassControllerWhichBeCreated) {
		try {

			String sCurrentExecutionClass = JFXApplicationDesignObjectLoad.class.getCanonicalName();

			StackTraceElement[] aStackTraceElementsList = JFXApplicationHelper.getStackTrace();

			String sEnclosingClassName = null;

			Object oFXMLRootNode = null;
			Object aParentRootNode = null;
			Object aController = null;
			Node aRootNode = null;

			if (aObjectClassControllerWhichBeCreated != null) {
				sEnclosingClassName = String.valueOf(JFXApplicationClassHelper
						.invokeMethod(aObjectClassControllerWhichBeCreated, "getCanonicalName"));
			} else {
				int iIndexStack = 0;
				for (StackTraceElement aStackTraceElement : aStackTraceElementsList) {

					if (aStackTraceElement.getClassName().compareToIgnoreCase(sCurrentExecutionClass) == 0) {
						// take another index, because index at n+0 is the method call and index n+1 is
						// the class element which call of current wizardy came from
						sEnclosingClassName = aStackTraceElementsList[(iIndexStack + 1)].getClassName();
						System.out.println(String.format("%d :: %s :: select (%s)", iIndexStack,
								aStackTraceElement.getClassName(), sEnclosingClassName));
						break;
					}
					iIndexStack++;
				}
			}

			Class<?> aRefeneceClass = Class.forName(sEnclosingClassName);
			Constructor<?>[] aConstructorList = aRefeneceClass.getConstructors();
			Object oObjectRef = null;
			if (aConstructorList.length > 0) {
				oObjectRef = aConstructorList[0].newInstance();
			} else {
				throw new JFXApplicationRuntimeException("Cant class constructor for " + sEnclosingClassName);
			}

			String sDesignFile = String.format("%s%s", oObjectRef.getClass().getSimpleName(),
					JFXApplication.JFXFILETYPE.FILETYPE_FXML.getValue());

			ClassLoader aClassLoader = oObjectRef.getClass().getClassLoader();
			if (aClassLoader == null) {
				throw new JFXApplicationRuntimeException(
						"Cant find class loader definition for " + sEnclosingClassName);
			}
			java.net.URL aUrlDesignFile = oObjectRef.getClass().getResource(sDesignFile);

			if (aUrlDesignFile != null) {
				FXMLLoader aRootNodeLoader = new FXMLLoader(aUrlDesignFile);

				if (aRootNodeLoader != null) {

					oFXMLRootNode = aRootNodeLoader.getRoot();

					if (oFXMLRootNode == null) {
						System.out.println(" Root is Null  or Dynamic root ...");

						aController = aRootNodeLoader.getController();
						if (aController == null) {
							aController = aRefeneceClass.getSuperclass();

							Object sclassnameforrootnode = JFXApplicationClassHelper.invokeMethod(aController,
									"getCanonicalName");
							Class<?> aRefeneceClassForRootNode = Class.forName(String.valueOf(sclassnameforrootnode));
							Constructor<?>[] aConstructorListForRootNode = aRefeneceClassForRootNode.getConstructors();

							if (aConstructorListForRootNode.length > 0) {

								for (Constructor<?> aConstructorFound : aConstructorListForRootNode) {
									if (aConstructorFound.getParameterCount() == 0) {
										aParentRootNode = aConstructorFound.newInstance();
										break;
									}
								}
							}
						}
						if (aParentRootNode != null) {
							aRootNodeLoader.setRoot(aParentRootNode);

						} else {
							throw new JFXApplicationRuntimeException(
									"Cant obtain dynamic (default) constructor definition for " + sEnclosingClassName);
						}

					}

					aRootNode = aRootNodeLoader.load();

					if ((aRootNode != null) && (aParentRootNode != null)) {
						((Parent) aParentRootNode).setUserData(aRootNodeLoader.getController());
					} else {
						throw new JFXApplicationRuntimeException("Cant load definition for " + sEnclosingClassName);
					}
				}

			} else {
				JFXApplicationLogger.getLogger().logError(aRefeneceClass.getClass(),
						new JFXApplicationRuntimeException(
								String.format("Cant obtain FXLOADER definition for %s  %n Expected Class %s",
										aUrlDesignFile, sEnclosingClassName)));
			}
			// may be NULL
			return ((Parent) aParentRootNode);
		} catch (Exception evERRObjectCreateLoad) {
			JFXApplicationLogger.getLogger().logError(JFXApplicationDesignObjectLoad.class, evERRObjectCreateLoad);
			throw new RuntimeException(evERRObjectCreateLoad);
		}

	}

	public static <T> Object createFromTypeController(Class<T> aclass, T tt) {
		Object aObject = new Object();
		return aObject;
	}

}