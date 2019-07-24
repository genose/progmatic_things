package MainWindow.assets.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import dao.Continent;
import dao.Couleur;
import dao.Pays;
import dao.TypeBiere;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class MainWindowRecherche {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="tComboBoxCouleur"
    private ComboBox<Couleur> tComboBoxCouleur; // Value injected by FXMLLoader

    @FXML // fx:id="tComboBoxPays"
    private ComboBox<Pays> tComboBoxPays; // Value injected by FXMLLoader

    @FXML // fx:id="tComboBoxContinent"
    private ComboBox<Continent> tComboBoxContinent; // Value injected by FXMLLoader

    @FXML // fx:id="tComboxBoxType"
    private ComboBox<TypeBiere> tComboxBoxType; // Value injected by FXMLLoader

    @FXML // fx:id="tSliderRangeTitrage"
    private Slider tSliderRangeTitrage; // Value injected by FXMLLoader

    @FXML // fx:id="tTextFieldNom"
    private TextField tTextFieldNom; // Value injected by FXMLLoader

    @FXML // fx:id="tTextFieldRangeTitrage"
    private TextField tTextFieldRangeTitrage; // Value injected by FXMLLoader

    @FXML // fx:id="tSliderRangePrix"
    private Slider tSliderRangePrix; // Value injected by FXMLLoader

    @FXML // fx:id="tTextFieldRangePrix"
    private TextField tTextFieldRangePrix; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert tComboBoxCouleur != null : "fx:id=\"tComboBoxCouleur\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.";
        assert tComboBoxPays != null : "fx:id=\"tComboBoxPays\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.";
        assert tComboBoxContinent != null : "fx:id=\"tComboBoxContinent\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.";
        assert tComboxBoxType != null : "fx:id=\"tComboxBoxType\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.";
        assert tSliderRangeTitrage != null : "fx:id=\"tSliderRangeTitrage\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.";
        assert tTextFieldNom != null : "fx:id=\"tTextFieldNom\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.";
        assert tTextFieldRangeTitrage != null : "fx:id=\"tTextFieldRangeTitrage\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.";
        assert tSliderRangePrix != null : "fx:id=\"tSliderRangePrix\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.";
        assert tTextFieldRangePrix != null : "fx:id=\"tTextFieldRangePrix\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.";

    }
}
