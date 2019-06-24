package vue;

import model.Personne;
import outils.OutilsDate;

import java.time.DayOfWeek;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DateCell;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SaisieContactController
{
	private Stage dialogStage;
	private Personne personne;
	private boolean okClicked;
	
	@FXML
	private DatePicker datePickerNaissance;

	@FXML
	private TextField textFieldPrenom;

	@FXML
	private TextField textFieldNom;

	
		
	public void setPersonne(Personne personne)
	{
		this.personne = personne;
		textFieldNom.setText(personne.getNom());
		textFieldPrenom.setText(personne.getPrenom());
		datePickerNaissance.setValue(OutilsDate.stringToDate(personne.getDateNaissance()));
	    datePickerNaissance.setDayCellFactory(d -> 
        new DateCell() {
            @Override public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.isAfter(LocalDate.now()));
                setTooltip(new Tooltip(OutilsDate.dateToLitteral(item)));
            }});	   
	}
		

	@FXML
	void annuler()
	{
		okClicked = false;
		dialogStage.close();
	}
	@FXML
	void enregister()
	{
		if (textFieldNom.getText().equals("") || textFieldPrenom.getText().equals("")
				|| datePickerNaissance.getValue() == null)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur de saisie");
			alert.setContentText("Veuillez compléter l'ensemble des champs de saisie");
			alert.showAndWait();
		} else
		{
			okClicked = !personne.getNom().equals(textFieldNom.getText())
					|| !personne.getPrenom().equals(textFieldPrenom.getText())
					|| !personne.getDateNaissance().equals(OutilsDate.dateToString(datePickerNaissance.getValue()));
			personne.setAll(textFieldNom.getText(), textFieldPrenom.getText(), datePickerNaissance.getValue());
			dialogStage.close();
		}
	}
	public void setDialogStage(Stage dialogStage)
	{
		this.dialogStage = dialogStage;
		okClicked = false;
	}
	public boolean isOkClicked()
	{
		return okClicked;
	}
}
