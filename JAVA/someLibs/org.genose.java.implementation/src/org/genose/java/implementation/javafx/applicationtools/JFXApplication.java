package org.genose.java.implementation.javafx.applicationtools;

import java.io.File;
import java.lang.System.Logger;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class JFXApplication extends Application {
	/**
	 * ** Declare a singleton pattern thru a static reference to Application
	 * instance **
	 */
	private static JFXApplication pJFXApplicationSingleton = null;
	/**
	 * ** Declare static Stage **
	 */
	private static Stage aPrimaryStage;
	/**
	 * ** Declare a flag for if (class).start() was already called ... **
	 */
	private static Boolean bJFXApplicationIsStarted = false;
	/**
	 * ** Declare a Log wrapper ... **
	 */
	private JFXApplicationLogger aLogger = null;

	public enum JFXFILETYPE {
		DIR_ASSETS("Assets"), DIR_VIEWS("Views"), DIR_CONTROLLERS("Controllers"), DIR_RESSOURCES("Ressources"),
		DIR_APPSRC("src"), FILETYPE_READEABLE_TEXT(".txt;.csv"), FILETYPE_FXML(".fxml"), FILETYPE_FCSS(".fcss"),
		FILETYPE_PNG(".png");

		private String strValue;
		private static java.util.HashMap<Object, Object> map = new java.util.HashMap<>();

		/* ******************************************************* */
		/**
		 * 
		 * @param aStrValue
		 */
		private JFXFILETYPE(String aStrValue) {
			this.strValue = aStrValue;
		}

		/* ******************************************************* */
		/**
		 * 
		 */
		static {
			for (JFXFILETYPE aType : JFXFILETYPE.values()) {
				map.put(aType.strValue, aType);
			}
		}

		/* ******************************************************* */
		/**
		 * 
		 * @param pageType
		 * @return
		 */
		public static JFXFILETYPE valueOf(int pageType) {
			return (JFXFILETYPE) map.get(pageType);
		}

		/* ******************************************************* */
		/**
		 * 
		 * @return
		 */
		public String getValue() {
			return strValue;
		}

		/* ******************************************************* */
		/**
		 * 
		 * @param strCode
		 * @return
		 */
		public String getEnumByString(String strCode) {
			for (JFXFILETYPE eType : JFXFILETYPE.values()) {
				if (strCode.toUpperCase().compareTo(String.valueOf(strValue.toUpperCase())) == 0)
					return eType.name();
			}
			return null;
		}

		/* ******************************************************* */
		@Override
		public String toString() {
			for (JFXFILETYPE eType : JFXFILETYPE.values()) {
				if (strValue.compareTo(eType.strValue) == 0)
					return eType.name();
			}
			return "";
		}

		/* ******************************************************* */
		/**
		 * 
		 * @return
		 */
		public List<String> split() {
			ArrayList<String> aSplittedValues = new ArrayList<>();
			String[] arrayStringOfValues = strValue.split(";");
			for (int i = 0; i < arrayStringOfValues.length; i++) {
				aSplittedValues.add(arrayStringOfValues[i]);
			}
			return aSplittedValues;
		}

	};

	/**
	 * 
	 */
	public JFXApplication() {
		super();
		singletonInstanceCreate();
		aLogger = new JFXApplicationLogger(getClass().getName());
		getLogger().logInfo(getClass(), "was instiated");
	}

	/*
	 * *****************************************************************************
	 */
	/**
	 * create a singleton
	 */
	private void singletonInstanceCreate() {
		synchronized (JFXApplication.class) {
			if (pJFXApplicationSingleton != null)
				throw new UnsupportedOperationException(
						getClass() + " is singleton but constructor called more than once");
			pJFXApplicationSingleton = this;
		}
	}

	/* ****************************************************** */
	/**
	 * 
	 * @return {@link JFXApplicationLogger}
	 */
	public JFXApplicationLogger getLogger() {
		return aLogger;
	}

	/* ****************************************************** */
	/**
	 * 
	 * @param {{@link javafx.stage}
	 */
	public void setPrimaryStage(Stage stage) {
		JFXApplication.aPrimaryStage = stage;
	}

	/* ****************************************************** */
	/**
	 * 
	 * @return javafx.Stage (JFXApplicationStage)
	 */
	static public Stage getPrimaryStage() {
		return JFXApplication.aPrimaryStage;
	}

	/* ****************************************************** */
	/**
	 * 
	 * 
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		synchronized (JFXApplication.class) {
			if (pJFXApplicationSingleton == null)
				pJFXApplicationSingleton = this;
		}
		setPrimaryStage(primaryStage);
	}

	public void start(JFXApplicationStage primaryStage) throws Exception {
		synchronized (JFXApplication.class) {
			if (pJFXApplicationSingleton == null)
				pJFXApplicationSingleton = this;
		}
		setPrimaryStage(primaryStage);

	}

	/* ****************************************************** */
	/**
	 * 
	 * @param aIconPath
	 * @return true when icon is set
	 */
	public static boolean setIcon(String aIconPath) {

		/*
		 * *******************************************************
		 * https://stackoverflow.com/questions/10121991/javafx-application-icon :: see :
		 * primaryStage.getIcons().add(new
		 * Image(getClass().getProtectionDomain().getCodeSource().getLocation()+
		 * "/stackoverflow.jpg"));
		 */
		Stage aPrimStage = JFXApplication.getActiveStage();
		if (aPrimStage != null) {
			Scene aScene = ((aPrimStage).getScene());

			ObservableList<Image> aStageIcons = aPrimStage.getIcons();
			if (aStageIcons.isEmpty())
				aStageIcons.set(aStageIcons.size() - 1, new Image(aIconPath));
			else
				aStageIcons.add(new Image(aIconPath));
			aScene.getRoot();
		}

		return false;
	}

	/* ****************************************************** */
	/**
	 * 
	 * @return
	 */
	private static Stage getActiveStage() {
		// TODO Auto-generated method stub
		return getPrimaryStage();
	}

	/* ****************************************************** */
	/**
	 * 
	 * @return bundle application path
	 */
	public static String getApplicationRunnablePathAbsolute() {

		String systemPathSeparator = String.valueOf(File.separatorChar);
		try {

			Class localClass = JFXApplication.getJFXApplicationSingleton().getClass();

			String packageNamed = localClass.getPackageName();
			String localPackagePath = String.valueOf(packageNamed);

			String localRunnablePathRelative = String.valueOf(String.valueOf("." + localPackagePath)
					.replaceAll("[.]", "\\" + systemPathSeparator).replaceAll("[^\\" + systemPathSeparator + "]", ""));

			localRunnablePathRelative = localRunnablePathRelative.replaceAll("[\\" + systemPathSeparator + "]", "../");
			URL aUrlClass = localClass.getResource(localRunnablePathRelative);
			if (aUrlClass != null) {
				return aUrlClass.getPath().replaceFirst("[/]", "").replaceFirst(localPackagePath, "").replaceAll("[//]",
						"/");
			}

		} catch (Exception evErrPath) {
			JFXApplicationLogger.getLogger().logError(JFXApplication.class.getClass(), evErrPath);
		}

		return "/";

	}

	/* ****************************************************** */
	/**
	 * 
	 * @return bundle application path /
	 */
	public static String getApplicationBundlePath() {
		try {
			Class localClass = JFXApplication.getJFXApplicationSingleton().getClass();

			String systemPathSeparator = String.valueOf(File.separatorChar);
			String packageNamed = localClass.getPackageName();
			String localPackagePath = String.valueOf(packageNamed);
			String localRunnablePathRelative = String.valueOf("." + packageNamed).replaceAll("[.]", "*")
					.replaceAll("[^\\*]", "");

			localRunnablePathRelative = localRunnablePathRelative.replaceAll("[\\*]", "..\\" + systemPathSeparator);
			/* ************************************************* */
			// :: path wil be resolved as-is : (.) inherited root path, package path of
			// (Extends ApplicationJFX)
			// :: path wil be resolved as-is : (/) root runnable directory ;;
			/* ************************************************* */
			// :: absolutepath will fail with (.)
			// :: absolutepath will provide Application BasePath with (.) on windows
			// :: absolutepath will provide Application BasePath with (/) on MacOS
			/* ************************************************* */
			// :: localpath :: getpath() will provide inherited root path, package path of
			// (Extends ApplicationJFX) on windows
			// :: localpath :: getpath(.) will provide inherited root path, package path of
			// (Extends ApplicationJFX)
			// :: localpath :: getpath(/) will provide root runnable directory ;; on MacOS
			/* ************************************************* */
			/**
			 * File localClassPath = new java.io.File("."); String localAbsolutePath =
			 * localClassPath.getAbsolutePath().replace(".", ""); // localAbsolutePath =
			 * localAbsolutePath.substring(0, // localAbsolutePath.length()-1); String
			 * localpath = localClassPath.getPath(); String localpathname =
			 * localClassPath.getName(); URL aUrlClassPath =
			 * localClass.getResource(localpath); String sFinalAppPath =
			 * aUrlClassPath.getPath().replaceFirst("[/]", "") .replaceFirst(
			 * localPackagePath , "").replaceAll("[//]", "/");
			 * 
			 * JFXApplicationLogger.getLogger() .logInfo(localClass + " 1 ;; class package
			 * path \n ll >> " + localPackagePath);
			 * 
			 * JFXApplicationLogger.getLogger().logInfo(localClass + " 1 ;; class abspath \n
			 * >> " + localAbsolutePath);
			 * 
			 * JFXApplicationLogger.getLogger().logInfo(localClass + " 1 ;; class localpath
			 * \n >> " + localpath);
			 * 
			 * JFXApplicationLogger.getLogger().logInfo(localClass + " 1 ;; class pathname
			 * \n >> " + localpathname);
			 * 
			 * 
			 * JFXApplicationLogger.getLogger().logInfo(localClass + " 2 ;; \n class
			 * getressource localpath \n >> " + aUrlClassPath + " \n origin >> " +
			 * localpath);
			 */
			// get runnable dir
			// URL aUrlClass = ((localClass.getResource(localAbsolutePath) == null )?
			// localClass.getResource(localRunnablePathRelative) :
			// localClass.getResource(localAbsolutePath));
			// URL aUrlClass = localClass.getResource(localRunnablePathRelative);

			return localRunnablePathRelative;
		} catch (Exception evErrPath) {
			JFXApplicationLogger.getLogger().logError(JFXApplication.class.getClass(), evErrPath);
		}
		return null;
	}

	public static JFXApplication getJFXApplicationSingleton() {
		synchronized (JFXApplication.class) {
			if (pJFXApplicationSingleton == null)
				throw new UnsupportedOperationException(" singleton is null");
		}
		return pJFXApplicationSingleton;
	}

	/* ****************************************************** */
	/**
	 * Intend to be used with (class).getRessource(String);
	 * 
	 * @param aPath
	 * @return true if path exists inside bundle app
	 */
	public static boolean applicationPathExist(String aPath) {
		try {
			File localClassPath = null;
			if (aPath != null) {
				Class localClass = JFXApplication.getJFXApplicationSingleton().getClass();
				String systemPathSeparator = String.valueOf(File.separatorChar);
				localClassPath = new java.io.File(String.valueOf(aPath));

				String documentPath = localClassPath.getAbsolutePath();
				String documentPathRelative = localClassPath.getPath();
				// .replaceAll("[..\\"+systemPathSeparator+"]", "");

				/*
				 * if(documentPathRelative.length() != localClassPath.length()) {
				 * documentPathRelative = String.format(""+systemPathSeparator+"%s",
				 * documentPathRelative); }
				 */
				URL aPathUrl = localClass.getResource(documentPathRelative);
				URL aPathUrlAlt = localClass.getResource(aPath);

				boolean bPathIsValid = ((aPathUrl != null) && (aPathUrlAlt != null));

				boolean bPathExist = localClassPath.exists();
				boolean bPathUrlValid = (aPathUrl != null);
				JFXApplicationLogger.getLogger().logInfo(null,
						((bPathIsValid) ? "" : "Not") + " exist :: " + documentPath + " :: ");

				return bPathIsValid;

			}
		} catch (Exception evERRFILEEXISTS) {
			JFXApplicationLogger.getLogger().logError(JFXApplication.getJFXApplicationSingleton().getClass(),
					evERRFILEEXISTS);
		}

		return false;

	}

	/**
	 * Quit Application
	 */
	public void notifyQuit() {
		Platform.exit();
	}

	public void setPrimaryScene(JFXApplicationScene jfxApplicationScene) {
		aPrimaryStage.setScene(jfxApplicationScene);
	}

	public void setSecondaryScene(JFXApplicationScene jfxApplicationScene) {

	}

	public Scene getPrimaryScene() {
		// TODO Auto-generated method stub
		return null;
	}

}
