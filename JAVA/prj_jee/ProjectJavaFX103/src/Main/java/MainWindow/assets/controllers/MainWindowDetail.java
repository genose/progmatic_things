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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
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

	        /* ********************************************************************* */
			tComboBoxCouleur.setItems(FXCollections.observableArrayList( DaoFactory.getCouleurDAO().getAll()) );
			tComboBoxCouleur.getItems().add(0, new Couleur(0, Couleur.sDEFAULTSELECTCOMBOXLIBELLE) );
			tComboBoxCouleur.getSelectionModel().select(0);
			tComboBoxCouleur.setCellFactory(new Callback<ListView<Couleur>, ListCell<Couleur>>() {
	            @Override
	            public ListCell<Couleur> call(ListView<Couleur> l){
	                return new ListCell<Couleur>(){
	                    @Override
	                    protected void updateItem(Couleur item, boolean empty) {
	                        super.updateItem(item, empty);
	                        if (item == null || empty) {
	                            setGraphic(null);
	                        } else {
	                            setText(item.getLibelle());
	                        }
	                    }
	                } ;
	            }
	        });
	        //selected value showed in combo box
	        tComboBoxCouleur.setConverter(new StringConverter<Couleur>() {
	              @Override
	              public String toString(Couleur arg0) {
	                if (arg0 == null){
	                  return null;
	                } else {
	                  return arg0.getLibelle();
	                }
	              }

	            @Override
	            public Couleur fromString(String userId) {
	                return null;
	            }
	        });
	        /* ********************************************************************* */
			tComboBoxType.setItems(FXCollections.observableArrayList( DaoFactory.getTypeDAO().getAll()) );
			tComboBoxType.getItems().add(0, new TypeBiere(0, Couleur.sDEFAULTSELECTCOMBOXLIBELLE) );
			tComboBoxType.getSelectionModel().select(0);
			tComboBoxType.setCellFactory(new Callback<ListView<TypeBiere>, ListCell<TypeBiere>>() {
	            @Override
	            public ListCell<TypeBiere> call(ListView<TypeBiere> l){
	                return new ListCell<TypeBiere>(){
	                    @Override
	                    protected void updateItem(TypeBiere item, boolean empty) {
	                        super.updateItem(item, empty);
	                        if (item == null || empty) {
	                            setGraphic(null);
	                        } else {
	                            setText(item.getLibelle());
	                        }
	                    }
	                } ;
	            }
	        });
	        //selected value showed in combo box
	        tComboBoxType.setConverter(new StringConverter<TypeBiere>() {
	              @Override
	              public String toString(TypeBiere arg0) {
	                if (arg0 == null){
	                  return null;
	                } else {
	                  return arg0.getLibelle();
	                }
	              }

	            @Override
	            public TypeBiere fromString(String arg0) {
	                return null;
	            }
	        });
	        /* ********************************************************************* */
			tComboBoxMarque.setItems(FXCollections.observableArrayList( DaoFactory.getMarqueDAO().getAll()) );
			tComboBoxMarque.getItems().add(0, new Marque(0, Marque.sDEFAULTSELECTCOMBOXLIBELLE) );
			tComboBoxMarque.getSelectionModel().select(0);
			tComboBoxMarque.setCellFactory(new Callback<ListView<Marque>, ListCell<Marque>>() {
	            @Override
	            public ListCell<Marque> call(ListView<Marque> l){
	                return new ListCell<Marque>(){
	                    @Override
	                    protected void updateItem(Marque item, boolean empty) {
	                        super.updateItem(item, empty);
	                        if (item == null || empty) {
	                            setGraphic(null);
	                        } else {
	                            setText(item.getLibelle());
	                        }
	                    }
	                } ;
	            }
	        });
	        //selected value showed in combo box
	        tComboBoxMarque.setConverter(new StringConverter<Marque>() {
	              @Override
	              public String toString(Marque arg0) {
	                if (arg0 == null){
	                  return null;
	                } else {
	                  return arg0.getLibelle();
	                }
	              }

	            @Override
	            public Marque fromString(String arg0) {
	                return null;
	            }
	        });
	        /* ********************************************************************* */
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
		aArticle = new Article();
		aDialogArticle = new ArticleDialog();
		aDialogArticle.update(aArticle);
	}
	
	@FXML
	public void edit() {
		aArticle  = Objects.requireNonNullElse(aArticle, new Article());
		 aDialogArticle = new ArticleDialog();
		aDialogArticle.update(aArticle);
		tComboBoxCouleur.getSelectionModel().select( aArticle.getCouleur() );
		tComboBoxType.getSelectionModel().select( aArticle.getType() );
		tComboBoxMarque.getSelectionModel().select( aArticle.getMarque() );
	}
	@Override
	public Boolean refresh(Article argArticle) {
		aArticle  = Objects.requireNonNullElse(argArticle, new Article());

		tTextFieldNom.setText(aArticle.getLibelle());

		tLabelType.setText(Objects.requireNonNullElse(aArticle.getType(), new TypeBiere()).getLibelle());

		tLabelCouleur.setText(Objects.requireNonNullElse(aArticle.getCouleur(), new Couleur()).getLibelle());

		tLabelMarque.setText(Objects.requireNonNullElse(aArticle.getMarque(), new Marque()).getLibelle());
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
