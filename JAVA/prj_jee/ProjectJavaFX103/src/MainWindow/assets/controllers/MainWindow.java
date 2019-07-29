package MainWindow.assets.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.TitlePaneLayout;

import org.genose.java.implementation.javafx.applicationtools.JFXApplicationDesignObjectLoad;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationScene;

import metier.Article;
import metier.Marque;
import service.ServiceArticle;
import MainWindow.assets.dao.MainWindowDAO;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class MainWindow {
    private MainWindowDAO aMainWindowDAO = null;
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane tDetailArticle;

    @FXML
    private TitledPane rSearchPanel;

    @FXML
    private TableView<Article> tTableViewListeArticle;
 
	@FXML
	private TableColumn<Article, Integer> tTableViewColumnId;
	@FXML
	private TableColumn<Article, String> tTableViewColumnArt;
	@FXML
	private TableColumn<Article, Marque> tTableViewColumnMarque;
	@FXML
	private TableColumn<Article, Float> tTableViewColumnTitrage;
	@FXML
	private TableColumn<Article, Float> tTableViewColumnPrix;

	private ServiceArticle serviceArticle = null;

    @FXML
    void initialize() {
        assert tDetailArticle != null : "fx:id=\"tDetailArticle\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert rSearchPanel != null : "fx:id=\"rSearchPanel\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert tTableViewListeArticle != null : "fx:id=\"tTableViewListeArticle\" was not injected: check your FXML file 'MainWindow.fxml'.";

       
        aMainWindowDAO = new MainWindowDAO();
        serviceArticle = new ServiceArticle();
        try {
			Node aSearchPanelContent =  (Node) JFXApplicationDesignObjectLoad.create(this.getClass().getSimpleName(), this.getClass().getSimpleName()+"Recherche" , null, true);
			Node aDetailPanelContent =  (Node) JFXApplicationDesignObjectLoad.create(this.getClass().getSimpleName(), this.getClass().getSimpleName()+"Detail" , null, true);
			  
			
			rSearchPanel.setContent(aSearchPanelContent );
			tDetailArticle.getChildren().add( aDetailPanelContent );
        
			
			
        } catch (JFXApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        tTableViewListeArticle.setItems(serviceArticle.getArticleFiltre());
        tTableViewColumnId.setCellValueFactory(
				(CellDataFeatures<Article, Integer> feature) -> feature.getValue().getPropertyId().asObject());
        tTableViewColumnArt.setCellValueFactory(
				(CellDataFeatures<Article, String> feature) -> feature.getValue().getPropertyLibelle());
        tTableViewColumnMarque.setCellValueFactory(
				(CellDataFeatures<Article, Marque> feature) -> feature.getValue().getPropertyMarque());
        tTableViewColumnTitrage.setCellValueFactory(
				(CellDataFeatures<Article, Float> feature) -> feature.getValue().getPropertyTitrage().asObject());
        tTableViewColumnPrix.setCellValueFactory(
				(CellDataFeatures<Article, Float> feature) -> feature.getValue().getPropertyPrix().asObject());
        
        
    }

	/**
	 * @return the resources
	 */
	public ResourceBundle getResources() {
		return resources;
	}

	/**
	 * @param resources the resources to set
	 */
	public void setResources(ResourceBundle resources) {
		this.resources = resources;
	}

	/**
	 * @return the location
	 */
	public URL getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(URL location) {
		this.location = location;
	}

	/**
	 * @return the rSearchPanel
	 */
	public TitledPane getrSearchPanel() {
		return rSearchPanel;
	}

	/**
	 * @param rSearchPanel the rSearchPanel to set
	 */
	public void setrSearchPanel(TitledPane rSearchPanel) {
		this.rSearchPanel = rSearchPanel;
	}

	/**
	 * @return the aMainWindowDAO
	 */
	public MainWindowDAO getaMainWindowDAO() {
		return aMainWindowDAO;
	}

	/**
	 * @param aMainWindowDAO the aMainWindowDAO to set
	 */
	public void setaMainWindowDAO(MainWindowDAO aMainWindowDAO) {
		this.aMainWindowDAO = aMainWindowDAO;
	}
}