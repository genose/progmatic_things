package MainWindow.assets.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import dao.Couleur;
import dao.Marque;
import dao.TypeBiere;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;

public class MainWindowDetail {


    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="tDetailView"
    private AnchorPane tDetailView; // Value injected by FXMLLoader

    @FXML // fx:id="tTextFieldNom"
    private TextField tTextFieldNom; // Value injected by FXMLLoader

    @FXML // fx:id="tComboBoxType"
    private ComboBox<?> tComboBoxType; // Value injected by FXMLLoader

    @FXML // fx:id="tComboBoxCouleur"
    private ComboBox<?> tComboBoxCouleur; // Value injected by FXMLLoader

    @FXML // fx:id="tComboBoxMarque"
    private ComboBox<?> tComboBoxMarque; // Value injected by FXMLLoader

    @FXML // fx:id="tLabelType"
    private Label tLabelType; // Value injected by FXMLLoader

    @FXML // fx:id="tLabelCouleur"
    private Label tLabelCouleur; // Value injected by FXMLLoader

    @FXML // fx:id="tLabelMarque"
    private Label tLabelMarque; // Value injected by FXMLLoader

    @FXML // fx:id="tToolbarDetail"
    private ToolBar tToolbarDetail; // Value injected by FXMLLoader

    @FXML // fx:id="tMenuButtonAction"
    private MenuButton tMenuButtonAction; // Value injected by FXMLLoader

    @FXML // fx:id="tMenuButtonEdit"
    private MenuItem tMenuButtonEdit; // Value injected by FXMLLoader

    @FXML // fx:id="tMenuButtonPrint"
    private MenuItem tMenuButtonPrint; // Value injected by FXMLLoader

    @FXML // fx:id="tToolbarButtonCancel"
    private Button tToolbarButtonCancel; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert tDetailView != null : "fx:id=\"tDetailView\" was not injected: check your FXML file 'MainWindowDetail.fxml'.";
        assert tTextFieldNom != null : "fx:id=\"tTextFieldNom\" was not injected: check your FXML file 'MainWindowDetail.fxml'.";
        assert tComboBoxType != null : "fx:id=\"tComboBoxType\" was not injected: check your FXML file 'MainWindowDetail.fxml'.";
        assert tComboBoxCouleur != null : "fx:id=\"tComboBoxCouleur\" was not injected: check your FXML file 'MainWindowDetail.fxml'.";
        assert tComboBoxMarque != null : "fx:id=\"tComboBoxMarque\" was not injected: check your FXML file 'MainWindowDetail.fxml'.";
        assert tLabelType != null : "fx:id=\"tLabelType\" was not injected: check your FXML file 'MainWindowDetail.fxml'.";
        assert tLabelCouleur != null : "fx:id=\"tLabelCouleur\" was not injected: check your FXML file 'MainWindowDetail.fxml'.";
        assert tLabelMarque != null : "fx:id=\"tLabelMarque\" was not injected: check your FXML file 'MainWindowDetail.fxml'.";
        assert tToolbarDetail != null : "fx:id=\"tToolbarDetail\" was not injected: check your FXML file 'MainWindowDetail.fxml'.";
        assert tMenuButtonAction != null : "fx:id=\"tMenuButtonAction\" was not injected: check your FXML file 'MainWindowDetail.fxml'.";
        assert tMenuButtonEdit != null : "fx:id=\"tMenuButtonEdit\" was not injected: check your FXML file 'MainWindowDetail.fxml'.";
        assert tMenuButtonPrint != null : "fx:id=\"tMenuButtonPrint\" was not injected: check your FXML file 'MainWindowDetail.fxml'.";
        assert tToolbarButtonCancel != null : "fx:id=\"tToolbarButtonCancel\" was not injected: check your FXML file 'MainWindowDetail.fxml'.";
        setEditable(false);
	}

	public void setEditable(Boolean bEditable) {
		/* ************************************************ */
		tTextFieldNom.setDisable(!bEditable);
		tTextFieldNom.setEditable(bEditable);
		/* ************************************************ */
		/* ************************************************ */
		tComboBoxType.setDisable(!bEditable);
		tComboBoxType.setVisible(bEditable);
		tComboBoxType.setEditable(bEditable);
		/* ************************************************ */
		/* ************************************************ */
		tComboBoxCouleur.setDisable(!bEditable);
		tComboBoxCouleur.setVisible(bEditable);
		tComboBoxCouleur.setEditable(bEditable);
		/* ************************************************ */
		/* ************************************************ */
		tComboBoxMarque.setDisable(!bEditable);
		tComboBoxMarque.setVisible(bEditable);
		tComboBoxMarque.setEditable(bEditable);
		/* ************************************************ */
		/* ************************************************ */
		tToolbarButtonCancel.setDisable(!bEditable);
		tToolbarButtonCancel.setVisible(bEditable);
		/* ************************************************ */
		/* ************************************************ */
		tLabelCouleur.setVisible(!bEditable);
		/* ************************************************ */
		/* ************************************************ */
		tLabelType.setVisible(!bEditable);
		/* ************************************************ */
		/* ************************************************ */
		tLabelMarque.setVisible(!bEditable);
		/* ************************************************ */

	}
}
