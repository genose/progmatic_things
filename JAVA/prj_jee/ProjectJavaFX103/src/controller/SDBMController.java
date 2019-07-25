package controller;

import dao.Article;
import dao.Continent;
import dao.Pays;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.scene.control.Slider;

import javafx.scene.control.ComboBox;

import javafx.scene.control.TableView;

public class SDBMController {
	@FXML
	private TableView<Article> beersTable;
	@FXML
	private ComboBox<?> comboFabricant;
	@FXML
	private ComboBox<Pays> comboPays;
	@FXML
	private ComboBox<Continent> comboContinent;
	@FXML
	private ComboBox<?> comboVolume;
	@FXML
	private Button supprButton;
	@FXML
	private Button modifierButton;
	@FXML
	private Button ajouterButton;
	@FXML
	private TextField searchField;
	@FXML
	private ComboBox comboCouleur;
	@FXML
	private ComboBox<?> comboType;
	@FXML
	private Slider miniSlider;
	@FXML
	private Slider maxiSlider;

	@FXML
	public void initialize() {
		ServiceArticle serviceArticle = new ServiceArticle();
		comboCouleur.setItems(serviceArticle.getCouleurFiltre());
		comboContinent.setItems(serviceArticle.getContinentFiltre());
		comboPays.setItems(serviceArticle.getPaysFiltre());
		comboContinent.valueProperty().addListener((obs, oldValue, newValue) -> {
			comboPays.valueProperty().setValue(null);
			if (newValue.getId() == 0) {
				comboPays.setItems(serviceArticle.getPaysFiltre());
			} else {
// comboPays.getSelectionModel().select(null);
				comboPays.setItems(FXCollections.observableArrayList(newValue.getListe()));
			}
		});

	}

}