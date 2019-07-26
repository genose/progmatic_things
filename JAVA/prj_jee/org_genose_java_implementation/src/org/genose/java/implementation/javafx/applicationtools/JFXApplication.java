package org.genose.java.implementation.javafx.applicationtools;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationScene;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationStage;

import javafx.application.Application; 
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/*
 * https://stackoverflow.com/questions/53453212/how-to-deploy-a-javafx-11-desktop-application-with-a-jre
 * https://medium.com/azulsystems/using-jlink-to-build-java-runtimes-for-non-modular-applications-9568c5e70ef4
 *  JAVAFX12_OUTPUTNAME="javafx12.0.1_jdk"
 * 
 *  PATH_JAVAFX_JMODS="/c/java/javafx-jmods-12.0.1/"
 * 
 * JAVA_HOME="/c/java/jdk-12.0.1/"
 * JAVA_JREMODULE_LIST="java.se"
 * 
 * JAVAFX12_MODS_LIST="javafx.base.jmod,javafx.controls.jmod,javafx.fxml.jmod,javafx.graphics.jmod,javafx.media.jmod,javafx.swing.jmod,javafx.web.jmod"
 * JAVAFX12_MODS_LIST="javafx.fxml,javafx.web,javafx.media,javafx.swing"
 * 
 *  $JAVA_HOME/bin/jlink --module-path $PATH_JAVAFX_JMODS --add-modules $JAVA_JREMODULE_LIST,$JAVAFX12_MODS_LIST --bind-service --output $JAVAFX12_OUTPUTNAME
 */

public class JFXApplication extends Application {
	/**
	 * ** Declare a singleton pattern thru a static reference to Application
	 * instance **
	 */
	private static JFXApplication pJFXApplicationSingleton = null;
	/**
	 * ** Declare static Stage **
	 */
	private Stage aPrimaryStage;
	/**
	 * ** Declare a flag for if (class).start() was already called ... **
	 */
	private static Boolean bJFXApplicationIsStarted = false;
	/**
	 * ** Declare a Log wrapper ... **
	 */
	private static JFXApplicationLogger aLogger = null;
	private static JFXApplicationException aExceptionManager;

	public enum JFXFILETYPE {
		DIR_ASSETS("Assets"), DIR_VIEWS("Views"), DIR_CONTROLLERS("Controllers"), DIR_RESSOURCES("Ressources"),
		// ::
		// https://stackoverflow.com/questions/10143392/javafx-2-and-internationalization
		DIR_LOCALI18N("locali18n"), DIR_APPSRC("src"), FILETYPE_READEABLE_TEXT(".txt;.csv"), FILETYPE_FXML(".fxml"),
		FILETYPE_FCSS(".fcss"), FILETYPE_PNG(".png");

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
		aExceptionManager = new JFXApplicationException();
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

	/**
	 * 
	 */
	private static synchronized void singletonInstanceCheck() {
		synchronized (JFXApplication.class) {
			if (pJFXApplicationSingleton == null)
				throw new UnsupportedOperationException(" singleton is null");
		}
	}

	/* ****************************************************** */
	/**
	 * 
	 * @return {@link JFXApplicationLogger}
	 */
	public JFXApplicationLogger getLogger() {
		singletonInstanceCheck();
		return JFXApplication.aLogger;
	}

	/* ****************************************************** */
	/**
	 * 
	 * @param {{@link javafx.stage}
	 */
	public void setPrimaryStage(Stage primaryStage) {
		singletonInstanceCheck();
		aPrimaryStage = primaryStage;
	}

	/* ****************************************************** */
	/**
	 * 
	 * @return javafx.Stage (JFXApplicationStage)
	 */
	public Stage getPrimaryStage() {
		singletonInstanceCheck();
		return (Stage)aPrimaryStage;
	}

	/* ****************************************************** */
	/**
	 * @throws Exception
	 * 
	 * 
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		singletonInstanceCheck();
		setPrimaryStage(primaryStage);

	}

	/* ****************************************************** */
	/**
	 * 
	 * @param primaryStage
	 * @throws Exception
	 */
	public void start(JFXApplicationStage primaryStage) throws Exception {
		singletonInstanceCheck();
		setPrimaryStage(primaryStage);

	}

	/* ****************************************************** */
	/**
	 * 
	 * @return
	 */
	public static JFXApplication getJFXApplicationSingleton() {
		singletonInstanceCheck();
		return pJFXApplicationSingleton;
	}

	/* ****************************************************** */
	/**
	 * 
	 * @param aIconPath
	 * @return true when icon is set
	 */
	public static boolean setIcon(String aIconPath) {
		singletonInstanceCheck();
		/*
		 * *******************************************************
		 * https://stackoverflow.com/questions/10121991/javafx-application-icon :: see :
		 * primaryStage.getIcons().add(new
		 * Image(getClass().getProtectionDomain().getCodeSource().getLocation()+
		 * "/stackoverflow.jpg")); ...
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
		singletonInstanceCheck();
		return JFXApplication.getJFXApplicationSingleton().getPrimaryStage();
	}

	/* ****************************************************** */
	/**
	 * 
	 * @return
	 */
	private static Stage getActivePrimaryStage() {
		singletonInstanceCheck();
		return JFXApplication.getJFXApplicationSingleton().getPrimaryStage();
	}

	/* ****************************************************** */
	/**
	 * 
	 * @return bundle application path
	 */
	public static String getApplicationRunnablePathAbsolute() {
		singletonInstanceCheck();
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
				return aUrlClass.getPath()/* .replaceFirst("[/]", "")*/.replaceFirst(localPackagePath, "").replaceAll("[//]",
						"/");
			}else {
				 aUrlClass = localClass.getResource(systemPathSeparator);
				 if (aUrlClass != null) {
						return aUrlClass.getPath()/*.replaceFirst("[/]", "")*/.replaceFirst(localPackagePath, "").replaceAll("[//]",
								"/");
					}
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
		singletonInstanceCheck();
		try {
			Class localClass = JFXApplication.getJFXApplicationSingleton().getClass();

			String systemPathSeparator = String.valueOf(File.separatorChar);
			String sLocalClassPackageName = localClass.getPackageName();

			Boolean bAnnonPackageName = String.valueOf(sLocalClassPackageName).isEmpty();
			
			String localPackagePath = String.valueOf(sLocalClassPackageName);
			String localRunnablePathRelative = ((bAnnonPackageName) ? "." : String.valueOf("." + sLocalClassPackageName).replaceAll("[.]", "*")
					.replaceAll("[^\\*]", ""));

			localRunnablePathRelative = localRunnablePathRelative.replaceAll("[\\*]", "..\\" + systemPathSeparator);
			URL aUrlClass = localClass.getResource(localRunnablePathRelative);
			if(aUrlClass != null && !bAnnonPackageName ) { 
				return localRunnablePathRelative;
			}else {
				aUrlClass = localClass.getResource("");
				String slocalPathUnamedModule = aUrlClass.getPath();
				slocalPathUnamedModule = slocalPathUnamedModule.substring(0, slocalPathUnamedModule.lastIndexOf("/") );
				 if (aUrlClass != null) {
					 slocalPathUnamedModule =  ".." + systemPathSeparator+slocalPathUnamedModule.substring(slocalPathUnamedModule.lastIndexOf("/"),slocalPathUnamedModule.length())+ "" + systemPathSeparator;
					 slocalPathUnamedModule = slocalPathUnamedModule.replaceAll("//", "/");
						return slocalPathUnamedModule;
					}
				
			}
		} catch (Exception evErrPath) {
			JFXApplicationLogger.getLogger().logError(JFXApplication.class.getClass(), evErrPath);
		}
		return null;
	}

	/* ****************************************************** */
	/**
	 * Intend to be used with (class).getRessource(String);
	 * 
	 * @param aPath
	 * @return true if path exists inside bundle app
	 */
	public static boolean applicationPathExist(String aPath) {
		singletonInstanceCheck();
		try {
			File localClassPath = null;
			if (aPath != null) {
				Class localClass = JFXApplication.getJFXApplicationSingleton().getClass();
				String sLocalClassPackageName = localClass.getPackageName();
				Boolean bAnnonPackageName = String.valueOf(sLocalClassPackageName).isEmpty();
				String systemPathSeparator = String.valueOf(File.separatorChar);
				localClassPath = new java.io.File(String.valueOf(aPath));

				String documentPath = localClassPath.getAbsolutePath();
				String documentPathRelative = localClassPath.getPath();

				URL aPathUrl = localClass.getResource(documentPathRelative);
				URL aPathUrlAlt = localClass.getResource(aPath);

				boolean bPathIsValid = ((aPathUrl != null) && (aPathUrlAlt != null));

				boolean bPathExist = localClassPath.exists();
				boolean bPathUrlValid = (aPathUrl != null);
				/*
				 * JFXApplicationLogger.getLogger().logInfo(null, ((bPathIsValid) ? "" : "Not")
				 * + " exist :: " + documentPath + " :: ");
				 */
				return bPathIsValid;

			}
		} catch (Exception evERRFILEEXISTS) {
			JFXApplicationLogger.getLogger().logError(JFXApplication.getJFXApplicationSingleton().getClass(),
					evERRFILEEXISTS);
		}

		return false;

	}

	/**
	 * 
	 * @param jfxApplicationScene
	 */
	public JFXApplication setPrimaryScene(JFXApplicationScene jfxApplicationScene) {
		singletonInstanceCheck();
		synchronized (JFXApplication.class) {
			if (aPrimaryStage != null) {
				if(jfxApplicationScene != null) {
					aPrimaryStage.setScene(jfxApplicationScene);
				}else {
					JFXApplicationException.raiseToFront(this.getClass(),
							new JFXApplicationException("Try to Set Null Scene to Primary stage window ..."), true);
				}
			} else {
				JFXApplicationException.raiseToFront(this.getClass(),
						new JFXApplicationException("Primary stage window is null ..."), true);
			}
		}
		return this;
	}

	/**
	 * 
	 * @param ajfxApplicationScene
	 */
	public void setSecondaryScene(JFXApplicationScene ajfxApplicationScene) {
		getLogger().logInfo("setSecondary() ... ");
		JFXApplicationException.raiseToFront(this.getClass(),
				new JFXApplicationException("Unimplemented ..."), true);
	}

	/**
	 * 
	 * @return
	 */
	public JFXApplicationScene getPrimaryScene() {
		synchronized (JFXApplication.class) {

			if (aPrimaryStage == null) {

				JFXApplicationException.raiseToFront(this.getClass(),
						new JFXApplicationException("Primary stage window is null ..."), false);
			} else {

				return (JFXApplicationScene) aPrimaryStage.getScene();
			}
		}
		return null;
	}

	/**
	 * 
	 * @return
	 * @throws JFXApplicationException
	 */
	public static JFXApplicationLogger getApplicationLogger() throws JFXApplicationException {
		singletonInstanceCheck();
		synchronized (JFXApplication.class) {
			if (pJFXApplicationSingleton == null)
				throw new JFXApplicationException(" Application is not intanciated ... ");
		}
		return JFXApplication.getJFXApplicationSingleton().getLogger();

	}

	/**
	 * 
	 * @return
	 */
	public static JFXApplicationException getExceptionManagaer() {
		synchronized (JFXApplication.class) {
			if (pJFXApplicationSingleton == null) {
				JFXApplicationException.raiseToFront(JFXApplication.class.getClass(),
						new JFXApplicationException(" Application is not intanciated ... "), true);
			}
		}
		return JFXApplication.aExceptionManager;
	}

	/* ****************************************************** */
	/**
	 * Quit Application
	 */
	public void notifyQuit() {
		Platform.exit();
	}

	/* ****************************************************** */
	/*
	 * 
	 */
	public static void notityDramaticQuit() {

		Platform.exit();
	}

}
