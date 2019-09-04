package MainWindow.assets.controllers;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import org.genose.java.implementation.tools.refreshableObject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;
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
	private ComboBox<TypeBiere> tComboBoxType; // Value injected by FXMLLoader

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
		Objects.requireNonNull( tComboBoxType, "fx:id=\"tComboxBoxType\" was not injected: check your FXML file 'MainWindowRecherche.fxml'.");
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
		refresh();	
		/* ********************************************************************* */
		tTextFieldNom.setText("");
		/* ********************************************************************* */
tSliderRangePrix.setValue(0);
tSliderRangePrix.setMax(aServiceArticle.getArticleSearch().getCriteriaPrixRange().max());
tSliderRangePrix.setMin(aServiceArticle.getArticleSearch().getCriteriaPrixRange().min());
/* ********************************************************************* */
String sTextRangePrix = String.format("%5.02f-%5.02f", tSliderRangePrix.getMin(), tSliderRangePrix.getMax());
tTextFieldRangePrix.setText(sTextRangePrix);
/* ********************************************************************* */

tSliderRangeTitrage.setValue(0);
tSliderRangeTitrage.setMax(aServiceArticle.getArticleSearch().getCriteriaTitrageRange().max());
tSliderRangeTitrage.setMin(aServiceArticle.getArticleSearch().getCriteriaTitrageRange().min());
/* ********************************************************************* */
tTextFieldRangeTitrage.setText( String.format("%5.02f-%5.02f", tSliderRangeTitrage.getMin(), tSliderRangeTitrage.getMax()));
/* ********************************************************************* */
		/*
		 * *****************************************************************************
		 * ***************
		 */
		tComboBoxCouleur.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				if (arg0 == null)
					return;
				ComboBox<Couleur> aComboBox = (ComboBox<Couleur>)arg0.getSource();
				Couleur objSelected = aComboBox.getSelectionModel().getSelectedItem();
				if(objSelected == null )return;
				System.out.println(" :: "+objSelected);
				 
					aServiceArticle.getArticleSearch().getCriteriaCouleur().clear();
					aServiceArticle.getArticleSearch().getCriteriaCouleur().add(objSelected);
					aServiceArticle.search();
				 
		 
			}
		});
		 
		
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
            public Couleur fromString(String arg0) {
                return null;
            }
        });
        /* ********************************************************************* */
		/*
		 * *****************************************************************************
		 * ***************
		 */
		tComboBoxType.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (arg0 == null)
					return;
				ComboBox<TypeBiere> aComboBox = (ComboBox<TypeBiere>)arg0.getSource();
				TypeBiere objSelected = aComboBox.getSelectionModel().getSelectedItem();
				if(objSelected == null )return;
				System.out.println(" :: "+objSelected);
				 
					aServiceArticle.getArticleSearch().getCriteriaType().clear();
					aServiceArticle.getArticleSearch().getCriteriaType().add(objSelected);
					aServiceArticle.search();
		 
		 
			}
		});


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
		/*
		 * *****************************************************************************
		 * ***************
		 */
		tComboBoxContinent.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				if (arg0 == null)
					return;
				ComboBox<Continent> aComboBox = (ComboBox<Continent>)arg0.getSource();
				Continent objSelected = aComboBox.getSelectionModel().getSelectedItem();
				if(objSelected == null )return;
				System.out.println(" :: "+objSelected);
				 
					aServiceArticle.getArticleSearch().getCriteriaContinent().clear();
					aServiceArticle.getArticleSearch().getCriteriaContinent().add(objSelected);
					aServiceArticle.search();
			 
		 
			}
		});


		tComboBoxContinent.setCellFactory(new Callback<ListView<Continent>, ListCell<Continent>>() {
            @Override
            public ListCell<Continent> call(ListView<Continent> l){
                return new ListCell<Continent>(){
                    @Override
                    protected void updateItem(Continent item, boolean empty) {
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
        tComboBoxContinent.setConverter(new StringConverter<Continent>() {
              @Override
              public String toString(Continent arg0) {
                if (arg0 == null){
                  return null;
                } else {
                  return arg0.getLibelle();
                }
              }

            @Override
            public Continent fromString(String arg0) {
                return null;
            }
        });
        /* ********************************************************************* */
		/*
		 * *****************************************************************************
		 * ***************
		 */
		tComboBoxPays.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				if (arg0 == null)
					return;
				ComboBox<Pays> aComboBox = (ComboBox<Pays>)arg0.getSource();
				Pays objSelected = aComboBox.getSelectionModel().getSelectedItem();
				if(objSelected == null )return;
				System.out.println(" :: "+objSelected);
				 
					aServiceArticle.getArticleSearch().getCriteriaPays().clear();
					aServiceArticle.getArticleSearch().getCriteriaPays().add(objSelected);
					aServiceArticle.search();
			 
		 
			}
		});


		tComboBoxPays.setCellFactory(new Callback<ListView<Pays>, ListCell<Pays>>() {
            @Override
            public ListCell<Pays> call(ListView<Pays> l){
                return new ListCell<Pays>(){
                    @Override
                    protected void updateItem(Pays item, boolean empty) {
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
        tComboBoxPays.setConverter(new StringConverter<Pays>() {
              @Override
              public String toString(Pays arg0) {
                if (arg0 == null){
                  return null;
                } else {
                  return arg0.getLibelle();
                }
              }

            @Override
            public Pays fromString(String arg0) {
                return null;
            }
        });
        /* ********************************************************************* */

		/*
		 * *****************************************************************************
		 * ***************
		 */
		tComboBoxMarque.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				if (arg0 == null)
					return;
				ComboBox<Marque> aComboBox = (ComboBox<Marque>)arg0.getSource();
				Marque objSelected = aComboBox.getSelectionModel().getSelectedItem();
				if(objSelected == null )return;
				System.out.println(" :: "+objSelected);
				 
					aServiceArticle.getArticleSearch().getCriteriaMarque().clear();
					aServiceArticle.getArticleSearch().getCriteriaMarque().add(objSelected);
					aServiceArticle.search();
			 
		 
			}
		});

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
		/*
		 * *****************************************************************************
		 * ***************
		 */
		tComboBoxFabricant.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				if (arg0 == null)
					return;
				ComboBox<Fabricant> aComboBox = (ComboBox<Fabricant>)arg0.getSource();
				Fabricant objSelected = aComboBox.getSelectionModel().getSelectedItem();
				if(objSelected == null )return;
				System.out.println(" :: "+objSelected);
				 
					aServiceArticle.getArticleSearch().getCriteriaFabricant().clear();
					aServiceArticle.getArticleSearch().getCriteriaFabricant().add(objSelected);
					aServiceArticle.search();
					 
		 
			}
		});
		tComboBoxFabricant.setCellFactory(new Callback<ListView<Fabricant>, ListCell<Fabricant>>() {
            @Override
            public ListCell<Fabricant> call(ListView<Fabricant> l){
                return new ListCell<Fabricant>(){
                    @Override
                    protected void updateItem(Fabricant item, boolean empty) {
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
        tComboBoxFabricant.setConverter(new StringConverter<Fabricant>() {
              @Override
              public String toString(Fabricant arg0) {
                if (arg0 == null){
                  return null;
                } else {
                  return arg0.getLibelle();
                }
              }

            @Override
            public Fabricant fromString(String arg0) {
                return null;
            }
        });
        /* ********************************************************************* */

	}

	@Override
	public Boolean refresh() {

		tComboBoxCouleur.valueProperty().setValue(null);
		tComboBoxType.valueProperty().setValue(null);

		tComboBoxContinent.valueProperty().setValue(null);
		tComboBoxPays.valueProperty().setValue(null);

		tComboBoxMarque.valueProperty().setValue(null);
		tComboBoxFabricant.valueProperty().setValue(null);

		/* ***************************************************************** */
		
		tComboBoxCouleur.setItems(aServiceArticle.getCouleurFiltre());
		tComboBoxCouleur.getSelectionModel().selectFirst();
		
		tComboBoxType.setItems(aServiceArticle.getTypeFiltre());
		tComboBoxCouleur.getSelectionModel().selectFirst();

		tComboBoxContinent.setItems(aServiceArticle.getContinentFiltre());
		tComboBoxCouleur.getSelectionModel().selectFirst();
		tComboBoxPays.setItems(aServiceArticle.getPaysFiltre());
		tComboBoxCouleur.getSelectionModel().selectFirst();

		tComboBoxMarque.setItems(aServiceArticle.getMarqueFiltre());
		tComboBoxCouleur.getSelectionModel().selectFirst();
		tComboBoxFabricant.setItems(aServiceArticle.getFabricantFiltre());
		tComboBoxCouleur.getSelectionModel().selectFirst();

		/* ***************************************************************** */
		tSliderRangePrix.setMin(aServiceArticle.getPrixFiltre().min());
		tSliderRangePrix.setMax(aServiceArticle.getPrixFiltre().max());

		tSliderRangeTitrage.setMin(aServiceArticle.getTitrageFiltre().min());
		tSliderRangeTitrage.setMax(aServiceArticle.getTitrageFiltre().max());

		/* ***************************************************************** */
/* 
		tComboBoxCouleur.getItems().add(0, new Couleur(0, "Couleur"));
		tComboBoxType.getItems().add(0, new TypeBiere(0, "Type"));

		tComboBoxContinent.getItems().add(0, new Continent(0, "Continent"));
		tComboBoxPays.getItems().add(0, new Pays(0, "Pays"));

		tComboBoxMarque.getItems().add(0, new Marque(0, "Marque"));
		tComboBoxFabricant.getItems().add(0, new Fabricant(0, "Fabricant"));
		*/

		/* ***************************************************************** */

		return false;
	}

	@Override
	public Boolean refresh(Article arg0) {
		return false;
	}
}
