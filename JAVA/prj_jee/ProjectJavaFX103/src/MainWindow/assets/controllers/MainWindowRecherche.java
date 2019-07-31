package MainWindow.assets.controllers;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import org.genose.java.implementation.tools.refreshableObject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import metier.Article;
import metier.Continent;
import metier.Couleur;
import metier.Fabricant;
import metier.Marque;
import metier.Pays;
import metier.TypeBiere;
import service.ServiceArticle;

public class MainWindowRecherche implements refreshableObject<Article> {

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="tComboBoxCouleur"
	private ComboBox<Couleur> tComboBoxCouleur; // Value injected by FXMLLoader
	@FXML // fx:id="tComboxBoxType"
	private ComboBox<TypeBiere> tComboxBoxType; // Value injected by FXMLLoader

	@FXML // fx:id="tComboBoxPays"
	private ComboBox<Pays> tComboBoxPays; // Value injected by FXMLLoader

	@FXML // fx:id="tComboBoxContinent"
	private ComboBox<Continent> tComboBoxContinent; // Value injected by FXMLLoader

	@FXML // fx:id="tComboBoxPays"
	private ComboBox<Marque> tComboBoxMarque; // Value injected by FXMLLoader

	@FXML // fx:id="tComboBoxContinent"
	private ComboBox<Fabricant> tComboBoxFabricant; // Value injected by FXMLLoader

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

	private ServiceArticle aServiceArticle = null;

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		Objects.requireNonNull( tComboBoxFabricant, "fx:id=\"tComboBoxFabricant\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.");
		Objects.requireNonNull( tComboBoxMarque, "fx:id=\"tComboBoxMarque\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.");
		Objects.requireNonNull( tComboBoxCouleur, "fx:id=\"tComboBoxCouleur\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.");
		Objects.requireNonNull( tComboBoxPays, "fx:id=\"tComboBoxPays\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.");
		Objects.requireNonNull( tComboBoxContinent, "fx:id=\"tComboBoxContinent\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.");
		Objects.requireNonNull( tComboxBoxType, "fx:id=\"tComboxBoxType\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.");
		Objects.requireNonNull( tSliderRangeTitrage, "fx:id=\"tSliderRangeTitrage\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.");
		Objects.requireNonNull( tTextFieldNom, "fx:id=\"tTextFieldNom\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.");
		Objects.requireNonNull( tTextFieldRangeTitrage, "fx:id=\"tTextFieldRangeTitrage\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.");
		Objects.requireNonNull( tSliderRangePrix, "fx:id=\"tSliderRangePrix\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.");
		Objects.requireNonNull( tTextFieldRangePrix, "fx:id=\"tTextFieldRangePrix\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.");

		aServiceArticle = ServiceArticle.getServiceArticleSingleton();
 
		/* ***************** initialise search GUI ************************* */

		/*
		 * ***************** initialise Event responder on search GUI
		 * *************************
		 */

		/*
		 * *****************************************************************************
		 * ***************
		 */
		tComboBoxCouleur.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends Couleur> arg0, Couleur arg1, Couleur arg2) -> {

					if (arg2 == null)
						return;

					System.out.println(" Values : " + arg2.getClass());
					System.out.println(" Values : " + arg0);
					System.out.println(" Values : 1" + arg1);
					System.out.println(" Values : 2" + arg2);

					aServiceArticle.getArticleSearch().getCriteriaCouleur().clear();
					aServiceArticle.getArticleSearch().getCriteriaCouleur().add(arg2);
					aServiceArticle.search();
					refresh();
				});

		/*
		 * *****************************************************************************
		 * ***************
		 */
		tComboxBoxType.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends TypeBiere> arg0, TypeBiere arg1, TypeBiere arg2) -> {

					if (arg2 == null)
						return;

					System.out.println(" Values : " + arg2.getClass());
					System.out.println(" Values : " + arg0);
					System.out.println(" Values : 1" + arg1);
					System.out.println(" Values : 2" + arg2);

					aServiceArticle.getArticleSearch().getCriteriaType().clear();
					aServiceArticle.getArticleSearch().getCriteriaType().add(arg2);
					aServiceArticle.search();
					refresh();
				});

		/*
		 * *****************************************************************************
		 * ***************
		 */
		tComboBoxContinent.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends Continent> arg0, Continent arg1, Continent arg2) -> {

					if (arg2 == null)
						return;

					System.out.println(" Values : " + arg2.getClass());
					System.out.println(" Values : " + arg0);
					System.out.println(" Values : 1" + arg1);
					System.out.println(" Values : 2" + arg2);

					aServiceArticle.getArticleSearch().getCriteriaContinent().clear();
					aServiceArticle.getArticleSearch().getCriteriaContinent().add(arg2);
					aServiceArticle.search();
					refresh();
				});

		/*
		 * *****************************************************************************
		 * ***************
		 */
		tComboBoxPays.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends Pays> arg0, Pays arg1, Pays arg2) -> {

					if (arg2 == null)
						return;

					System.out.println(" Values : " + arg2.getClass());
					System.out.println(" Values : " + arg0);
					System.out.println(" Values : 1" + arg1);
					System.out.println(" Values : 2" + arg2);

					aServiceArticle.getArticleSearch().getCriteriaPays().clear();
					aServiceArticle.getArticleSearch().getCriteriaPays().add(arg2);
					aServiceArticle.search();
					refresh();
				});

	 

		/*
		 * *****************************************************************************
		 * ***************
		 */
		tComboBoxMarque.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends Marque> arg0, Marque arg1, Marque arg2) -> {

					if (arg2 == null)
						return;

					System.out.println(" Values : " + arg2.getClass());
					System.out.println(" Values : " + arg0);
					System.out.println(" Values : 1" + arg1);
					System.out.println(" Values : 2" + arg2);

					aServiceArticle.getArticleSearch().getCriteriaMarque().clear();
					aServiceArticle.getArticleSearch().getCriteriaMarque().add(arg2);
					aServiceArticle.search();
					refresh();
				});

		/*
		 * *****************************************************************************
		 * ***************
		 */
		tComboBoxFabricant.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends Fabricant> arg0, Fabricant arg1, Fabricant arg2) -> {

					if (arg2 == null)
						return;

					System.out.println(" Values : " + arg2.getClass());
					System.out.println(" Values : " + arg0);
					System.out.println(" Values : 1" + arg1);
					System.out.println(" Values : 2" + arg2);

					aServiceArticle.getArticleSearch().getCriteriaFabricant().clear();
					aServiceArticle.getArticleSearch().getCriteriaFabricant().add(arg2);
					aServiceArticle.search();
					refresh();
				});

		refresh();
	}

	@Override
	public Boolean refresh() {

		tComboBoxCouleur.valueProperty().setValue(null);
		tComboxBoxType.valueProperty().setValue(null);

		tComboBoxContinent.valueProperty().setValue(null);
		tComboBoxPays.valueProperty().setValue(null);

		tComboBoxMarque.valueProperty().setValue(null);
		tComboBoxFabricant.valueProperty().setValue(null);

		/* ***************************************************************** */
		tComboBoxCouleur.setItems(aServiceArticle.getCouleurFiltre());
		tComboxBoxType.setItems(aServiceArticle.getTypeFiltre());

		tComboBoxContinent.setItems(aServiceArticle.getContinentFiltre());
		tComboBoxPays.setItems(aServiceArticle.getPaysFiltre());

		tComboBoxMarque.setItems(aServiceArticle.getMarqueFiltre());
		tComboBoxFabricant.setItems(aServiceArticle.getFabricantFiltre());

		/* ***************************************************************** */
		tSliderRangePrix.setMin(aServiceArticle.getPrixFiltre().min());
		tSliderRangePrix.setMax(aServiceArticle.getPrixFiltre().max());

		tSliderRangeTitrage.setMin(aServiceArticle.getTitrageFiltre().min());
		tSliderRangeTitrage.setMax(aServiceArticle.getTitrageFiltre().max());

		/* ***************************************************************** */

		tComboBoxCouleur.getItems().add(0, new Couleur(0, "Couleur"));
		tComboxBoxType.getItems().add(0, new TypeBiere(0, "Type"));

		tComboBoxContinent.getItems().add(0, new Continent(0, "Continent"));
		tComboBoxPays.getItems().add(0, new Pays(0, "Pays"));

		tComboBoxMarque.getItems().add(0, new Marque(0, "Marque"));
		tComboBoxFabricant.getItems().add(0, new Fabricant(0, "Fabricant"));

		/* ***************************************************************** */

		return false;
	}

	@Override
	public Boolean refresh(Article arg0) {
		return false;
	}
}
