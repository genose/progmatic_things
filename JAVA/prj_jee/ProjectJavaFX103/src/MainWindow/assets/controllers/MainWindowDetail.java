package MainWindow.assets.controllers;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationDialog;
import org.genose.java.implementation.tools.refreshableObject;

import ArticleDialog.ArticleDialog;
import dao.DaoFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import metier.Article;
import metier.Couleur;
import metier.Marque;
import metier.TypeBiere;

public class MainWindowDetail implements refreshableObject<Article> {

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="tDetailView"
	private AnchorPane tDetailView; // Value injected by FXMLLoader

	@FXML // fx:id="tComboBoxType"
	private ComboBox<TypeBiere> tComboBoxType; // Value injected by FXMLLoader

	@FXML // fx:id="tComboBoxCouleur"
	private ComboBox<Couleur> tComboBoxCouleur; // Value injected by FXMLLoader

	@FXML // fx:id="tComboBoxMarque"
	private ComboBox<Marque> tComboBoxMarque; // Value injected by FXMLLoader

	@FXML // fx:id="tTextFieldNom"
	private TextField tTextFieldNom; // Value injected by FXMLLoader

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

	private Article aArticle = null;
	ArticleDialog aDialogArticle = null;
	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		Objects.requireNonNull( tDetailView, "fx:id=\"tDetailView\" was not injected: check your FXML file 'MainWindowDetail.fxml'.");
		Objects.requireNonNull( tTextFieldNom, "fx:id=\"tTextFieldNom\" was not injected: check your FXML file 'MainWindowDetail.fxml'.");
		Objects.requireNonNull( tComboBoxType, "fx:id=\"tComboBoxType\" was not injected: check your FXML file 'MainWindowDetail.fxml'.");
		Objects.requireNonNull( tComboBoxCouleur, "fx:id=\"tComboBoxCouleur\" was not injected: check your FXML file 'MainWindowDetail.fxml'.");
		Objects.requireNonNull( tComboBoxMarque, "fx:id=\"tComboBoxMarque\" was not injected: check your FXML file 'MainWindowDetail.fxml'.");
		Objects.requireNonNull( tLabelType, "fx:id=\"tLabelType\" was not injected: check your FXML file 'MainWindowDetail.fxml'.");
		Objects.requireNonNull( tLabelCouleur, "fx:id=\"tLabelCouleur\" was not injected: check your FXML file 'MainWindowDetail.fxml'.");
		Objects.requireNonNull( tLabelMarque, "fx:id=\"tLabelMarque\" was not injected: check your FXML file 'MainWindowDetail.fxml'.");
		Objects.requireNonNull( tToolbarDetail, "fx:id=\"tToolbarDetail\" was not injected: check your FXML file 'MainWindowDetail.fxml'.");
		Objects.requireNonNull( tMenuButtonAction, "fx:id=\"tMenuButtonAction\" was not injected: check your FXML file 'MainWindowDetail.fxml'.");
		Objects.requireNonNull( tMenuButtonEdit, "fx:id=\"tMenuButtonEdit\" was not injected: check your FXML file 'MainWindowDetail.fxml'.");
		Objects.requireNonNull( tMenuButtonPrint, "fx:id=\"tMenuButtonPrint\" was not injected: check your FXML file 'MainWindowDetail.fxml'.");
		Objects.requireNonNull( tToolbarButtonCancel, "fx:id=\"tToolbarButtonCancel\" was not injected: check your FXML file 'MainWindowDetail.fxml'.");
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
		if(bEditable) {
			
			tComboBoxCouleur.setItems(FXCollections.observableArrayList( DaoFactory.getCouleurDAO().getAll()) );
			tComboBoxType.setItems(FXCollections.observableArrayList( DaoFactory.getTypeDAO().getAll()) );
			tComboBoxMarque.setItems(FXCollections.observableArrayList( DaoFactory.getMarqueDAO().getAll()) );
			
		}
		
		

	}
	
	@FXML
	public void delete() {
		if(aArticle == null) JFXApplicationDialog.showAlertDialog("Veuillez selectionner un article ...");
		ArticleDialog aDialogArticle = new ArticleDialog();
		aDialogArticle.deleteConfirmDialog(aArticle);
	}
	@FXML
	public void add() {
		aArticle = new Article(0, "");
		ArticleDialog aDialogArticle = new ArticleDialog();
		aDialogArticle.update(aArticle);
	}
	
	@FXML
	public void edit() {
		 aDialogArticle = new ArticleDialog();
		aDialogArticle.update(aArticle);
		tComboBoxCouleur.getSelectionModel().select( aArticle.getCouleur() );
		tComboBoxType.getSelectionModel().select( aArticle.getType() );
		tComboBoxMarque.getSelectionModel().select( aArticle.getMarque() );
	}
	@Override
	public Boolean refresh(Article argArticle) {
		aArticle  = Objects.requireNonNullElse(argArticle, new Article(0, ""));

		tTextFieldNom.setText(aArticle.getLibelle());

		tLabelType.setText(Objects.requireNonNullElse(aArticle.getType(), new TypeBiere(0, "")).getLibelle());

		tLabelCouleur.setText(Objects.requireNonNullElse(aArticle.getType(), new Couleur(0, "")).getLibelle());

		tLabelMarque.setText(Objects.requireNonNullElse(aArticle.getType(), new Marque(0, "")).getLibelle());
		return true;
	}

	@Override
	public Boolean refresh() {
		return false;
	}
	public void cancel() {
		aDialogArticle.close();
	}
}
