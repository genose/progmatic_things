package org.genose.java.implementation.javafx.applicationtools.views;


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class JFXApplicationDialog {

	public static boolean showConfirmDialog(String sMessage) {
		Boolean bReturnValue = false;
		Alert aModalDialogAlert = new Alert(Alert.AlertType.CONFIRMATION);

		aModalDialogAlert.setTitle("Attention ...");
		aModalDialogAlert.setHeaderText("Confirmer ");
		aModalDialogAlert.setContentText(sMessage);

		// Set content for Dialog Pane
		aModalDialogAlert.getDialogPane().setContentText(sMessage);

		Optional<ButtonType> aOptionButtonSelected = aModalDialogAlert.showAndWait();

		if(!aOptionButtonSelected.isEmpty()) {
			bReturnValue = 	(aOptionButtonSelected.get() == ButtonType.OK) ;
		}

		System.out.println("Confirm return : " + bReturnValue);
		return bReturnValue;
	}

	public static boolean showAlertDialog(String sMessage) {
		Boolean bReturnValue = false;
		Alert aArticleDialogAlert = new Alert(Alert.AlertType.WARNING);

		aArticleDialogAlert.setTitle("Attention ...");
		aArticleDialogAlert.setHeaderText("Hoho ");
		aArticleDialogAlert.setContentText(sMessage);

		// Set content for Dialog Pane
		aArticleDialogAlert.getDialogPane().setContentText(sMessage);

		Optional<ButtonType> aOptionButtonSelected = aArticleDialogAlert.showAndWait();

		if(!aOptionButtonSelected.isEmpty()) {
			bReturnValue = 	(aOptionButtonSelected.get() == ButtonType.OK) ;
		}

		System.out.println("Alert return : " + bReturnValue);
		return bReturnValue;
		
	}
}
