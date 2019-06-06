package org.genose.java.implementation.javafx.applicationtools;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class JFXApplication extends Application {
	 /**
	  * ** Declare a singleton pattern thru a static reference to Application instance **
	  */
	private static JFXApplication 			JFXApplication_singleton = null;
	 /**
	  *  ** Declare static Stage **
	  */
	private static Stage 		aPrimaryStage;
	 /**
	  *  ** Declare a flag for if (class).start() was already called ... ** 
	  */
	private static Boolean  		bJFXApplicationIsStarted = false;
	
	
	
    public enum APPLICATION_MVC_DIRS  {
    		DIR_ASSETS("Assets"), DIR_VIEWS("Views"), DIR_CONTROLLERS("Controllers"), DIR_RESSOURCES("Ressources"), DIR_APPSRC("src");
    	
		private String value;
		private static java.util.HashMap<Object, Object> map = new java.util.HashMap<>();

		private APPLICATION_MVC_DIRS(String value) {
			this.value = value;
		}

		static {
			for (APPLICATION_MVC_DIRS addrType : APPLICATION_MVC_DIRS.values()) {
				map.put(addrType.value, addrType);
			}
		}

		public static APPLICATION_MVC_DIRS valueOf(int pageType) {
			return (APPLICATION_MVC_DIRS) map.get(pageType);
		}

		public String getValue() {
			return value;
		}

		public String getEnumByString(String code) {
			for (APPLICATION_MVC_DIRS e : APPLICATION_MVC_DIRS.values()) {
				if (code == String.valueOf(value))
					return e.name();
			}
			return null;
		}

		public String toString() {
			for (APPLICATION_MVC_DIRS e : APPLICATION_MVC_DIRS.values()) {
				if (value == e.value)
					return e.name();
			}
			return "";
		}

    	
    		
    };


	/**
	 * 
	 * 
	 */
	public JFXApplication() {
		super();
	    synchronized(JFXApplication.class){
	        if(JFXApplication_singleton != null) throw new UnsupportedOperationException(
	                getClass()+" is singleton but constructor called more than once");
	        JFXApplication_singleton = this;
	    }
	    System.out.println(getClass()+" : was instiated");
	}
	/* ****************************************************** */
	/**
	 * 
	 * @param stage
	 */
    public void setPrimaryStage(Stage stage) {
        JFXApplication.aPrimaryStage =  stage;
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
		synchronized(JFXApplication.class){
	        if(JFXApplication_singleton == null) JFXApplication_singleton = this;
		}
		setPrimaryStage(primaryStage);
	}

	public void start(JFXApplicationStage primaryStage) throws Exception {
		synchronized(JFXApplication.class){
	        if(JFXApplication_singleton == null) JFXApplication_singleton = this;
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
		
		/* ******************************************************* 
		 * https://stackoverflow.com/questions/10121991/javafx-application-icon
		 *  :: see : primaryStage.getIcons().add(new Image(getClass().getProtectionDomain().getCodeSource().getLocation()+"/stackoverflow.jpg"));
		 */
		Stage aPrimStage = JFXApplication.getActiveStage();
		if(aPrimStage != null) {
			JFXApplicationScene aScene = (JFXApplicationScene) ((aPrimStage).getScene());
						
			ObservableList<Image> aStageIcons = aPrimStage.getIcons();
			if(aStageIcons.size() >0)
				aStageIcons.set( aStageIcons.size() -1 , new Image(aIconPath));
			else
				aStageIcons.add( new Image(aIconPath));
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
		Class localClass = JFXApplication.getJFXApplicationSingleton().getClass();
		
		String packageNamed = localClass.getPackageName();
		String localPackagePath = ""+packageNamed.replaceAll("[.]", "/");
		String localRunnablePathRelative = String.valueOf("/"+localPackagePath.replaceAll("[^/]", "")).replaceAll("/", "../");
		URL aUrlClass = localClass.getResource(localRunnablePathRelative);
		if(aUrlClass != null) {
			return aUrlClass.getPath();
		}else {
			return "/";
		}
	}
	
	
	/* ****************************************************** */
	/**
	 * 
	 * @return bundle application path /
	 */
	public static String getApplicationBundlePath() {
				
		Class localClass = JFXApplication.getJFXApplicationSingleton().getClass();
		
		String packageNamed = localClass.getPackageName();
		String localPackagePath = ""+packageNamed.replaceAll("[.]", "/");
		String localRunnablePathRelative = String.valueOf("/"+localPackagePath.replaceAll("[^/]", "")).replaceAll("/", "../");
		/* ************************************************* */
		// :: path wil be resolved as-is : (.) inherited root path, package path of (Extends ApplicationJFX)
		// :: path wil be resolved as-is : (/) root runnable directory ;;
		/* ************************************************* */
		// :: absolutepath will fail with (.)
		// :: absolutepath will provide Application BasePath with (.) on windows
		// :: absolutepath will provide Application BasePath with (/) on MacOS
		/* ************************************************* */
		// :: localpath :: getpath() will provide  inherited root path, package path of (Extends ApplicationJFX) on windows
		// :: localpath :: getpath(.) will provide  inherited root path, package path of (Extends ApplicationJFX)
		// :: localpath :: getpath(/) will provide  root runnable directory ;; on MacOS
		/* ************************************************* */
 		File localClassPath = new java.io.File( "." );
		String localAbsolutePath = localClassPath.getAbsolutePath().replace(".", "");
		//localAbsolutePath = localAbsolutePath.substring(0, localAbsolutePath.length()-1);
		String localpath = localClassPath.getPath()+"";
		String localpathname = localClassPath.getName();
		System.out.println("");
		System.out.println(localClass+" 1 ;; class package path  \n ll >> "+localPackagePath);
		System.out.println("");
		System.out.println(localClass+" 1 ;; class abspath \n >> "+localAbsolutePath);
		System.out.println("");
		System.out.println(localClass+" 1 ;; class localpath \n >> "+localpath);
		System.out.println("");
		System.out.println(localClass+" 1 ;; class pathname \n >> "+localpathname);

		
		System.out.println("");
		System.out.println("");
		URL aUrlClassPath = localClass.getResource(localpath);
		System.out.println(localClass+" 2 ;; \n class getressource localpath \n >> "+aUrlClassPath+" \n origin  >> "+localpath );
		
		 
		System.out.println("");
		// get runnable dir
		// URL aUrlClass = ((localClass.getResource(localAbsolutePath) == null )? localClass.getResource(localRunnablePathRelative) : localClass.getResource(localAbsolutePath));
		URL aUrlClass = localClass.getResource(localRunnablePathRelative);
		URL aUrlClassPAth = localClass.getResource(aUrlClass.getPath());
		// URL aUrlClass = localClass.getResource("..\\C:\\Users\\59013-36-18\\Documents\\GitHub\\progmatic_things\\JAVA\\prj_jee\\ProjectJavaFX_102\\bin");
		System.out.println(localClass+" 3 ;; \n class getressource abspath \n >> "+aUrlClass+"\n"+aUrlClass.getPath()+" \n origin >> "+localAbsolutePath);
		boolean bPathExists = JFXApplication.applicationPathExist(localAbsolutePath);
		bPathExists = JFXApplication.applicationPathExist(aUrlClass.getPath()+"cc");
		return aUrlClassPath.getPath().replace("/", "");
	}

	private static JFXApplication getJFXApplicationSingleton() {
		// TODO Auto-generated method stub
		return JFXApplication_singleton;
	}
	/* ****************************************************** */
	/**
	 * 
	 * @param aPath
	 * @return true if path exists inside bundle app 
	 */
	public static boolean applicationPathExist(String aPath) {
		// TODO Auto-generated method stub
		try {
			File localClassPath = new java.io.File(String.valueOf(aPath).replace("/./", "/"));
			
			String documentPath = localClassPath.getAbsolutePath();
			System.out.println(" exixts :: "+documentPath+" :: "+String.valueOf(((localClassPath != null)? localClassPath.exists() : false)));
			
			return ((localClassPath != null)? localClassPath.exists() : false) ;
			
		} catch (Exception EV_ERR_FILEEXISTS) {
			EV_ERR_FILEEXISTS.printStackTrace();
		}
		return false;
		
	}
	

}
