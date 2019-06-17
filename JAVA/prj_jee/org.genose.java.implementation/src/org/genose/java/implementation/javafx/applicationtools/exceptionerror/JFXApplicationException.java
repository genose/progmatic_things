/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools.exceptionerror;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationLogger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public JFXApplicationException() {
		super();
	}

	/**
	 * @param message
	 */
	public JFXApplicationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public JFXApplicationException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public JFXApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public JFXApplicationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public static void raiseToFront(Class<?> fromClass, Throwable throwedEvent) {

// :: https://o7planning.org/fr/11529/tutoriel-javafx-alert-dialog#a10503960
		String aCauseMessage = String.format(
				"Error in frontend ... %n ***************** %n %s %n ***************** %n %s %n ***************** %n %s %n ***************** %n %s %n ***************** %n",
				fromClass, throwedEvent.getCause(), throwedEvent.getMessage(), throwedEvent.getStackTrace());

		if (!(fromClass.getClass().equals(JFXApplicationLogger.class))) {
			JFXApplicationLogger.getLogger().logError("Error while finding to forward error to frontend ...",
					throwedEvent);
			javafx.scene.control.Button aButton = new javafx.scene.control.Button();

			Stage aStageForError = new Stage(StageStyle.UNDECORATED);

			VBox root = new VBox();
			root.setPadding(new Insets(10));
			root.setSpacing(10);

			Button aQuitButton = new Button("Quit ... ");

			aQuitButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					Platform.exit();
				}

			});

			root.getChildren().addAll(aQuitButton);

			Scene scene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth()-20,
					Screen.getPrimary().getVisualBounds().getHeight()-80);
			aStageForError.setTitle("JavaFX Error Alert (o7planning.org)");
			aStageForError.setScene(scene);

			// aStageForError.show();
			/* ********************************* */
			/* ********************************* */
			/* ********************************* */
			Alert aFatalErrorAlert = new Alert(AlertType.ERROR);
			aFatalErrorAlert.setTitle("Fatal Error ...");
			aFatalErrorAlert.setHeaderText("Fatal Error : " + throwedEvent.getMessage());

			aFatalErrorAlert.setContentText(String.format(" Fatal Error \n %s", aCauseMessage));
			/* ********************************* */
			/* ********************************* */
			/* ********************************* */
			VBox dialogPaneContent = new VBox();
 
			Label aLabelCause = new Label("Cause :" + throwedEvent.getCause());
			Label aLabel = new Label("Stack Trace:");

			String stackTrace = JFXApplicationException.getStackTraceFormatted(throwedEvent);
			TextArea textArea = new TextArea();
			textArea.setText(stackTrace);

			dialogPaneContent.getChildren().addAll(aLabelCause, aLabel, textArea, aQuitButton);
			// Set content for Dialog Pane
			aFatalErrorAlert.getDialogPane().setContent(dialogPaneContent);
			aFatalErrorAlert.getDialogPane().setContentText("Text ...");

			aFatalErrorAlert.showAndWait();
			JFXApplication.notityDramaticQuit();
		} else {

			JFXApplicationLogger.getLogger().log(System.Logger.Level.ERROR, aCauseMessage);
			Platform.exit();
		}
	}

	private static String getStackTraceFormatted(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String s = sw.toString();
		return s;
	}

}
