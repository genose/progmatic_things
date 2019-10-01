package MainWindow.assets.controllers;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import org.genose.java.implementation.javafx.applicationtools.JFXApplicationDesignObjectLoad;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.tools.refreshableObject;

import metier.Article;
import metier.Couleur;
import metier.Marque;
import metier.TypeBiere;
import service.ServiceArticle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MainWindow extends BorderPane implements refreshableObject<Article> {

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
	private TableColumn<Article, Integer> tTableViewListeArticleColumnId;
	@FXML
	private TableColumn<Article, String> tTableViewListeArticleColumnLibelle;
	@FXML
	private TableColumn<Article, Couleur> tTableViewListeArticleColumnCouleur;
	@FXML
	private TableColumn<Article, TypeBiere> tTableViewListeArticleColumnTypeBiere;
	@FXML
	private TableColumn<Article, Marque> tTableViewListeArticleColumnMarque;
	@FXML
	private TableColumn<Article, Float> tTableViewListeArticleColumnTitrage;
	@FXML
	private TableColumn<Article, Float> tTableViewListeArticleColumnPrix;

	private ServiceArticle serviceArticle = null;

	@FXML
	void initialize() {
		Objects.requireNonNull( tDetailArticle, "fx:id=\"tDetailArticle\" was not injected: check your FXML file 'MainWindow.fxml'.");
		Objects.requireNonNull( rSearchPanel, "fx:id=\"rSearchPanel\" was not injected: check your FXML file 'MainWindow.fxml'.");
		Objects.requireNonNull( tTableViewListeArticle, "fx:id=\"tTableViewListeArticle\" was not injected: check your FXML file 'MainWindow.fxml'.");
		Objects.requireNonNull( tTableViewListeArticleColumnId, "fx:id=\"tTableViewListeArticleColumnId\" was not injected: check your FXML file 'MainWindow.fxml'.");
		Objects.requireNonNull( tTableViewListeArticleColumnLibelle, "fx:id=\"tTableViewListeArticleColumnLibelle\" was not injected: check your FXML file 'MainWindow.fxml'.");
		Objects.requireNonNull( tTableViewListeArticleColumnCouleur, "fx:id=\"tTableViewListeArticleColumnCouleur\" was not injected: check your FXML file 'MainWindow.fxml'.");
		Objects.requireNonNull( tTableViewListeArticleColumnMarque, "fx:id=\"tTableViewListeArticleColumnMarque\" was not injected: check your FXML file 'MainWindow.fxml'.");
		Objects.requireNonNull( tTableViewListeArticleColumnPrix, "fx:id=\"tTableViewListeArticleColumnPrix\" was not injected: check your FXML file 'MainWindow.fxml'.");
		Objects.requireNonNull( tTableViewListeArticleColumnTitrage, "fx:id=\"tTableViewListeArticleColumnTitrage\" was not injected: check your FXML file 'MainWindow.fxml'.");

		serviceArticle = new ServiceArticle();
		try {
			Node aSearchPanelContent = (Node) JFXApplicationDesignObjectLoad.create(this.getClass().getSimpleName(),
					this.getClass().getSimpleName() + "Recherche", null, true);
			Node aDetailPanelContent = (Node) JFXApplicationDesignObjectLoad.create(this.getClass().getSimpleName(),
					this.getClass().getSimpleName() + "Detail", null, true);

			rSearchPanel.setContent(aSearchPanelContent);
			tDetailArticle.getChildren().add(aDetailPanelContent);

		} catch (JFXApplicationException e) {
			e.printStackTrace();
		}

		tTableViewListeArticle.setItems(serviceArticle.getArticleSorted());

		tTableViewListeArticleColumnId.setCellValueFactory(
				(CellDataFeatures<Article, Integer> feature) -> feature.getValue().getPropertyId().asObject());
		tTableViewListeArticleColumnLibelle.setCellValueFactory(
				(CellDataFeatures<Article, String> feature) -> feature.getValue().getPropertyLibelle());
		tTableViewListeArticleColumnCouleur.setCellValueFactory(
				(CellDataFeatures<Article, Couleur> feature) ->
						feature.getValue().getPropertyCouleur()
		);
		tTableViewListeArticleColumnCouleur.set
		tTableViewListeArticleColumnTypeBiere.setCellValueFactory(
				(CellDataFeatures<Article, TypeBiere> feature) -> feature.getValue().getPropertyTypeBiere());
		tTableViewListeArticleColumnMarque.setCellValueFactory(
				(CellDataFeatures<Article, Marque> feature) -> feature.getValue().getPropertyMarque());
		tTableViewListeArticleColumnTitrage.setCellValueFactory(
				(CellDataFeatures<Article, Float> feature) -> feature.getValue().getPropertyTitrage().asObject());
		tTableViewListeArticleColumnPrix.setCellValueFactory(
				(CellDataFeatures<Article, Float> feature) -> feature.getValue().getPropertyPrix().asObject());

		tTableViewListeArticle.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Article>() {

			@Override
			public void changed(ObservableValue<? extends Article> arg0, Article arg1, Article arg2) {
				System.out.println(" ******** %n Selectitem :: " + arg2);
				System.out.println("  ******* %n Detail controller :: " + tDetailArticle.getChildren().get(0).getUserData());
				((MainWindowDetail)tDetailArticle.getChildren().get(0).getUserData()).refresh(arg2);

			}
		});

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
	public TitledPane getSearchPanel() {
		return rSearchPanel;
	}

	/**
	 * @param rSearchPanel the rSearchPanel to set
	 */
	public void setSearchPanel(TitledPane rSearchPanel) {
		this.rSearchPanel = rSearchPanel;
	}

	@Override
	public Boolean refresh(Article arg0) {
		return false;
	}

	@Override
	public Boolean refresh() {

		tTableViewListeArticle.setItems(serviceArticle.getArticleSorted());

		return true;
	}
}