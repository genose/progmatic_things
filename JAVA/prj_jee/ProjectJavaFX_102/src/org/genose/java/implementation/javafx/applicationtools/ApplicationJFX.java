package org.genose.java.implementation.javafx.applicationtools;


import java.io.File;
import java.nio.file.Path;

import org.genose.java.implementation.games.PenduGame.GAMESTATUS;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ApplicationJFX extends Application {

	
    public enum APPLICATION_MVC_DIRS  {
    		DIR_ASSETS("Assets"), DIR_VIEWS("Views"), DIR_CONTROLLERS("Controllers"), DIR_RESSOURCES("Ressources");
    	
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

    private void setPrimaryStage(Stage stage) {
        ApplicationJFX.aPrimaryStage = stage;
    }

    static public Stage getPrimaryStage() {
        return ApplicationJFX.aPrimaryStage;
    }
	
	
	public ApplicationJFX() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start(Stage arg0) throws Exception {
		setPrimaryStage(arg0);
	}
	
	
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

	private static Stage getActiveStage() {
		// TODO Auto-generated method stub
		return getPrimaryStage();
	}

	public static String getApplicationPath() {
		// TODO Auto-generated method stub
		File localClassPath = new java.io.File(".");
		String documentPath = localClassPath.getAbsolutePath();
		
		// ApplicationJFX.getClass().getProtectionDomain().getCodeSource().getLocation();
		
		return documentPath;
	}

	public static boolean applicationPathExist(String string) {
		// TODO Auto-generated method stub
		return false;
	}

}
