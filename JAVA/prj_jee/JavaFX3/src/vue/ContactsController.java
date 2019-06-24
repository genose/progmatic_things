package vue;

import java.time.LocalDate;
import java.util.Optional;

import application.MenuApplication;
import model.Personne;
import outils.OutilsDate;
import service.RepertoireBean;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

public class ContactsController
{
	@FXML
	private TableView<Personne> contactsTable;
	@FXML
	private TableColumn<Personne, String> nomColumn;
	@FXML
	private TableColumn<Personne, String> prenomColumn;
	@FXML
	private TableColumn<Personne, String> dateDeNaissanceColumn;

	@FXML
	private TextField textFieldNomRecherche;
	@FXML
	private TextField textFieldPrenomRecherche;
	@FXML
	private Label detailNom;
	@FXML
	private Label detailPrenom;
	@FXML
	private Label detailDateDeNaissance;
	@FXML
	private Button buttonModifier;
	@FXML 
	private Button buttonSupprimer;
	@FXML
	private MenuItem modifierItem;
	@FXML
	private MenuItem supprimerItem;
	private MenuApplication mainApp;
	private RepertoireBean repertoire;

	@FXML
	private void initialize()
	{
		nomColumn.setCellValueFactory((CellDataFeatures<Personne, String> feature) -> feature.getValue().nomProperty());

		prenomColumn.setCellValueFactory(
				(CellDataFeatures<Personne, String> feature) -> feature.getValue().prenomProperty());

		dateDeNaissanceColumn.setCellValueFactory(
				(CellDataFeatures<Personne, String> feature) -> feature.getValue().dateNaissanceProperty());

		// Ajout d'un ecouteur sur les items
		contactsTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> afficherDetailContact(newValue));
		textFieldNomRecherche.textProperty()
				.addListener((observable, oldValue, newValue) -> repertoire.setNomSearched(newValue));
		textFieldPrenomRecherche.textProperty()
				.addListener((observable, oldValue, newValue) -> repertoire.setPrenomSearched(newValue));
	}

	public void setMainApp(MenuApplication menuApp)
	{
		this.mainApp = menuApp;
		repertoire = menuApp.getRepertoire();
		modifierSupprimerSetDisable(true);
		contactsTable.setItems(repertoire.getContacts());
		repertoire.getContacts().comparatorProperty().bind(contactsTable.comparatorProperty());
	}

	private void modifierSupprimerSetDisable(Boolean etat)
	{
		buttonModifier.setDisable(etat);
		buttonSupprimer.setDisable(etat);
		modifierItem.setDisable(etat);
		supprimerItem.setDisable(etat);
	}

	@FXML
	private void supprimer()
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Suppression d'un contacts");
		alert.setContentText("Etes-vous sûr de vouloir supprimer le contact :  " + repertoire.getPersonneSelected().toString());

		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK)
		{
			repertoire.supprimer();
			contactsTable.getSelectionModel().select(null);
		}
	}

	@FXML
	private void imprimer()
	{
		PrinterJob printerJob = PrinterJob.createPrinterJob();
		printerJob.showPrintDialog(mainApp.getPrimaryStage());
		boolean success = printerJob.printPage(contactsTable);
		if (success)
			printerJob.endJob();
		else
			printerJob.cancelJob();
	}

	@FXML
	private void modifier()
	{
		repertoire.setSaved(!mainApp.showModifContact());
	}

	@FXML
	private void ajouter()
	{
		repertoire.setPersonneSelected(new Personne("", "", OutilsDate.dateToString(LocalDate.now())));
		if (mainApp.showModifContact())
		{
			repertoire.ajouter();
			contactsTable.getSelectionModel().select(null);
		}
	}

	private void afficherDetailContact(Personne personne)
	{
		repertoire.setPersonneSelected(personne);
		if (personne != null)
		{
			detailNom.setText(personne.getNom());
			detailPrenom.setText(personne.getPrenom());
			detailDateDeNaissance.setText(personne.getDateNaissance());
			modifierSupprimerSetDisable(false);
		}
		else
		{
			detailNom.setText("");
			detailPrenom.setText("");
			detailDateDeNaissance.setText("");
			modifierSupprimerSetDisable(true);
		}
	}
}
