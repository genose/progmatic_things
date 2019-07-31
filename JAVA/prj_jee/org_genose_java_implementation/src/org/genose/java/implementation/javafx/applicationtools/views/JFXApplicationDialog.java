package org.genose.java.implementation.javafx.applicationtools.views;

import java.awt.event.ActionEvent;
import java.util.Optional;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JFXApplicationDialog {

	public static boolean showConfirmDialog(String sMessage) {
		Boolean bReturnValue = false;
		Alert aArticleDialogAlert = new Alert(AlertType.CONFIRMATION);

		aArticleDialogAlert.setTitle("Attention ...");
		aArticleDialogAlert.setHeaderText("Confirmer ");
		aArticleDialogAlert.setContentText(sMessage);

		// Set content for Dialog Pane
		aArticleDialogAlert.getDialogPane().setContentText(sMessage);

		Optional<ButtonType> aOptionButtonSelected = aArticleDialogAlert.showAndWait();

		if(!aOptionButtonSelected.isEmpty()) {
			bReturnValue = 	(aOptionButtonSelected.get() == ButtonType.OK) ;
		}

		System.out.println("Confirm return : " + bReturnValue);
		return bReturnValue;
	}

	public static boolean showAlertDialog(String sMessage) {
		Boolean bReturnValue = false;
		Alert aArticleDialogAlert = new Alert(AlertType.WARNING);

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
