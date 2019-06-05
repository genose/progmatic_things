package org.genose.java.implementation.javafx.applicationtools;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
 
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ApplicationJFX extends Application {
	private static Application instance;
	
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
	private static Stage aPrimaryStage; // **Declare static Stage**

	/**
	 * 
	 * 
	 */
	public ApplicationJFX() {
		super();
	    synchronized(ApplicationJFX.class){
	        if(instance != null) throw new UnsupportedOperationException(
	                getClass()+" is singleton but constructor called more than once");
	        instance = this;
	    }
	    System.out.println(getClass()+" : was instiated");
	}
	/* ****************************************************** */
	/**
	 * 
	 * @param stage
	 */
    private void setPrimaryStage(Stage stage) {
        ApplicationJFX.aPrimaryStage = stage;
    }

	/* ****************************************************** */
    /**
     * 
     * @return javafx.Stage
     */
    static public Stage getPrimaryStage() {
        return ApplicationJFX.aPrimaryStage;
    }
	
	/* ****************************************************** */
    /**
     * 
     * 
     */
	@Override
	public void start(Stage arg0) throws Exception {
		synchronized(ApplicationJFX.class){
	        if(instance == null) instance = this;
		}
		setPrimaryStage(arg0);
	}
	
	/* ****************************************************** */
	/**
	 * 
	 * @param aIconPath
	 * @return true when icon is set
	 */
	public static boolean setIcon(String aIconPath) {
		
		// :: https://stackoverflow.com/questions/10121991/javafx-application-icon
		// ::  primaryStage.getIcons().add(new Image(getClass().getProtectionDomain().getCodeSource().getLocation()+"/stackoverflow.jpg"));
		
		Stage aPrimStage = ApplicationJFX.getActiveStage();
		if(aPrimStage != null) {
			ApplicationJFXScene aScene = (ApplicationJFXScene) ((aPrimStage).getScene());
			((ApplicationJFXScene) aScene).setIcon( new Image(aIconPath) );
	        
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
	public static String getApplicationRunnablePath() {
	
		return "";
	}
	
	
	/* ****************************************************** */
	/**
	 * 
	 * @return bundle application path
	 */
	public static String getApplicationPath() {
				
		Class localClass = ApplicationJFX.getApplicationJFXSingleton().getClass();
		
		String packageNamed = localClass.getPackageName();
		String localPackagePath = ""+packageNamed.replaceAll("[.]", "/");
		/* ************************************************* */
		// :: path wil be resolved as-is : (.) inherited root path, package path of (Extends ApplicationJFX)
		// :: path wil be resolved as-is : (/) root runnable directory ;;
		/* ************************************************* */
		// :: absolutepath will fail with (.)
		// :: absolutepath will provide Application BasePath
		/* ************************************************* */
		// :: getpath(.) will provide  inherited root path, package path of (Extends ApplicationJFX)
		// :: getpath(/) will provide  root runnable directory ;;
		/* ************************************************* */
 		File localClassPath = new java.io.File( "/" );
		String localAbsolutePath = localClassPath.getAbsolutePath().replace(".", "");
		//localAbsolutePath = localAbsolutePath.substring(0, localAbsolutePath.length()-1);
		String localpath = localClassPath.getPath()+"";
		String localpathname = localClassPath.getName();
		System.out.println("");
		System.out.println(localClass+" 1 ;; class package path  \n ll >> "+localPackagePath);
		System.out.println("");
		System.out.println(localClass+" 1 ;; class abs \n >> "+localAbsolutePath);
		System.out.println("");
		System.out.println(localClass+" 1 ;; class localpath \n >> "+localpath);
		System.out.println("");
		System.out.println(localClass+" 1 ;; class pathname \n >> "+localpathname);

		
		System.out.println("");
		System.out.println("");
		URL aUrlClass = localClass.getResource(localAbsolutePath);
		System.out.println(localClass+" 2 ;; class getressource abspath doc \n >> "+aUrlClass+" \n >> "+localAbsolutePath);
		
		System.out.println("");
		URL aUrlClassPath = localClass.getResource(localpath);
		System.out.println(localClass+" 3 ;; class getressource local \n >> "+aUrlClassPath+" \n >> "+localpath );
		
		 
		return localpath;
	}

	private static Object getApplicationJFXSingleton() {
		// TODO Auto-generated method stub
		return instance;
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
			// TODO: handle exception
			EV_ERR_FILEEXISTS.printStackTrace();
		}
		return false;
		
	}

}
