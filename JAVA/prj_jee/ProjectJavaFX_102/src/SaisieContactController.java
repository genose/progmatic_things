



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
	 
	private boolean okClicked;
	
	@FXML
	private DatePicker datePickerNaissance;

	@FXML
	private TextField textFieldPrenom;

	@FXML
	private TextField textFieldNom;

	 
	@FXML
	void annuler()
	{
		okClicked = false;
		dialogStage.close();
	}
	@FXML
	void enregister()
	{
		 
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
