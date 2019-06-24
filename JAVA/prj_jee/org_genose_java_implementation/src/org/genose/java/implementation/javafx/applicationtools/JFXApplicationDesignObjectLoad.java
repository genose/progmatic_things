package org.genose.java.implementation.javafx.applicationtools;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public interface JFXApplicationDesignObjectLoad {

	public static Object create( Class<?>  aClass) {
		java.net.URL aUrlDesignFile = aClass.getClass().getResource(String.format("%s%s", aClass.getName(), JFXApplication.JFXFILETYPE.FILETYPE_FXML));
			try {
				FXMLLoader aRootNodeLoader = new FXMLLoader(aUrlDesignFile);

	
			Node aRootNode = aRootNodeLoader.load();
		} catch (Exception evERRObjectCreateLoad) {
			JFXApplicationLogger.getLogger().logError(JFXApplicationDesignObjectLoad.class, evERRObjectCreateLoad);
		}
		return null;
	}
	
}