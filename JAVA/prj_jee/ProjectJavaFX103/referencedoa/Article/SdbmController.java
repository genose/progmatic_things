package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellDataFeatures;
import metier.Article;
import metier.Continent;
import metier.Couleur;
import metier.Fabricant;
import metier.Marque;
import metier.Pays;
import metier.Type;
import service.ServiceArticle;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ComboBox;

import javafx.scene.control.TableView;

public class SdbmController
{
	@FXML
	private TableView<Article> beersTable;
	@FXML
	private TableColumn<Article, Integer> columnId;
	@FXML
	private TableColumn<Article, String> columnArt;
	@FXML
	private TableColumn<Article, Marque> columnMarque;
	@FXML
	private TableColumn<Article, Float> columnTitrage;
	@FXML
	private TableColumn<Article, Float> columnPrix;
	@FXML
	private ComboBox<Fabricant> comboFabricant;
	@FXML
	private ComboBox<Pays> comboPays;
	@FXML
	private ComboBox<Continent> comboContinent;
	@FXML
	private ComboBox<?> comboVolume;
	@FXML
	private ComboBox<Marque> comboMarque;
	@FXML
	private Button supprButton;
	@FXML
	private Button modifierButton;
	@FXML
	private Button ajouterButton;
	@FXML
	private TextField searchField;
	@FXML
	private ComboBox<Couleur> comboCouleur;
	@FXML
	private ComboBox<Type> comboType;
	@FXML
	private Slider miniSlider;
	@FXML
	private Slider maxiSlider;

	@FXML
	public void initialize()
	{
		ServiceArticle serviceArticle = new ServiceArticle();
		comboCouleur.setItems(serviceArticle.getCouleurFiltre());
		comboContinent.setItems(serviceArticle.getContinentFiltre());
		comboPays.setItems(serviceArticle.getPaysFiltre());
		comboContinent.valueProperty().addListener((obs, oldValue, newValue) -> {
			comboPays.valueProperty().setValue(null);
			if (newValue.getId() == 0)
			{
				comboPays.setItems(serviceArticle.getPaysFiltre());
			}
			else
			{
				comboPays.setItems(FXCollections.observableArrayList(newValue.getListe()));
			}
		});
		comboPays.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue == null)
				return;
			comboMarque.valueProperty().setValue(null);
			if (newValue.getId() == 0)
			{
				comboMarque.setItems(serviceArticle.getMarqueFiltre());
//				comboContinent.setItems(serviceArticle.getContinentFiltre());
			}
			else
			{
//				System.out.println(comboContinent.getSelectionModel().getSelectedItem().getId());
				comboMarque.setItems(FXCollections.observableArrayList(newValue.getListeMarque()));
			}
		});
		comboMarque.setItems(serviceArticle.getMarqueFiltre());
		comboType.setItems(serviceArticle.getTypeFiltre());
		comboFabricant.setItems(serviceArticle.getFabricantFiltre());
		beersTable.setItems(serviceArticle.getArticleFiltre());
		columnId.setCellValueFactory(
				(CellDataFeatures<Article, Integer> feature) -> feature.getValue().idProperty().asObject());
		columnArt.setCellValueFactory(
				(CellDataFeatures<Article, String> feature) -> feature.getValue().nomProperty());
		columnMarque.setCellValueFactory(
				(CellDataFeatures<Article, Marque> feature) -> feature.getValue().marqueProperty());
		columnTitrage.setCellValueFactory(
				(CellDataFeatures<Article, Float> feature) -> feature.getValue().titrageProperty().asObject());
		columnPrix.setCellValueFactory(
				(CellDataFeatures<Article, Float> feature) -> feature.getValue().prixProperty().asObject());
	}

}
