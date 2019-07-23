package MainWindow.assets.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.genose.java.implementation.javafx.applicationtools.JFXApplicationDesignObjectLoad;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationScene;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

public class MainWindow {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane rSearchPanel;

    @FXML
    void initialize() {
        assert rSearchPanel != null : "fx:id=\"rSearchPanel\" was not injected: check your FXML file 'MainWindow.fxml'.";
        try {
			JFXApplicationScene aSearchPanelContent =  JFXApplicationDesignObjectLoad.create(this.getClass().getSimpleName(), this.getClass().getSimpleName()+"."+"Recherche" , null);
		} catch (JFXApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
       // rSearchPanel.getChildren().add( aSearchPanelContent );
    }
}