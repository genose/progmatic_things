/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools.exceptionerror;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationHelper;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationLogger;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationDialog;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
	public static final String ERROR_MESSAGE_DESIGNLOAD = "Something went wront with design consistantcy .... check FXML / Controller version ...";
	private Throwable aEncapsuledThrowable = null;
	private static Alert aFatalErrorAlert = null;
 
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

	/**
	 * 
	 * @param sMessageToRaise
	 * @param throwedCause
	 * @param stackTrace
	 */
	public JFXApplicationException(String sMessageToRaise, Throwable throwedCause, StackTraceElement[] stackTrace) {
		super(sMessageToRaise, throwedCause);
		super.setStackTrace(stackTrace);

	}

	/**
	 * 
	 * @return Throwable
	 */
	public Throwable getEncapsuledEventException() {

		return this.aEncapsuledThrowable;
	}

	/**
	 * 
	 * @param aThrowableToForward
	 */
	public void setEncapsuledEventException(Throwable aThrowableToForward) {

		this.aEncapsuledThrowable = aThrowableToForward;
	}

	/**
	 * 
	 * @param fromClass
	 * @param throwedEvent
	 * @param bFatalQuit
	 */
	public static void raiseToFront(Class<?> fromClass, Throwable throwedEvent, Boolean bFatalQuit) {

// :: https://o7planning.org/fr/11529/tutoriel-javafx-alert-dialog#a10503960
		String aCauseMessage = String.format(
				"Error in frontend ... %n ***************** %n %s %n ***************** %n ", String.valueOf(fromClass));

		if (throwedEvent != null) {
			aCauseMessage = String.format(
					"Error in frontend ... %n ***************** %n %s %n ***************** %n %s %n ***************** %n %s %n ***************** %n %s %n ***************** %n",
					fromClass, throwedEvent.getCause(), throwedEvent.getMessage(), throwedEvent.getStackTrace());
		}

		try {
			if (JFXApplicationException.aFatalErrorAlert == null) {
				
				
				if(throwedEvent == null) {
					 throwedEvent = new JFXApplicationException("raiseToFront was Called for a certain reason ..." , throwedEvent, JFXApplicationHelper.getStackTrace());
				}
				
				JFXApplicationLogger.getLogger().logError(JFXApplicationException.class, throwedEvent, "Error while finding to forward error to frontend ...");

				JFXApplicationException.aFatalErrorAlert = new Alert(AlertType.CONFIRMATION);
				/* ******** 
				javafx.scene.control.Button aButton = new javafx.scene.control.Button();

				Stage aStageForError = new Stage(StageStyle.UNDECORATED);

				VBox root = new VBox();
				root.setPadding(new Insets(10));
				root.setSpacing(10);

				Button aQuitButton = new Button("Quit ... ");

				aQuitButton.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) { 
						if (bFatalQuit) {
							Platform.exit();
						}
					}

				});

				root.getChildren().addAll(aQuitButton);

				Scene scene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth() - 20,
						Screen.getPrimary().getVisualBounds().getHeight() - 80);
				aStageForError.setTitle("JavaFX Error Alert (o7planning.org)");
				aStageForError.setScene(scene);

				 aStageForError.show();
				 
				 ******** */
				/* ********************************* */
				/* ********************************* */
				/* ********************************* */

				
				aFatalErrorAlert.setTitle("Fatal Error ...");
				aFatalErrorAlert.setHeaderText("Fatal Error : " + throwedEvent.getMessage());

				aFatalErrorAlert.setContentText(String.format(" Fatal Error %n %s", aCauseMessage));
				/* ********************************* */
				/* ********************************* */
				/* ********************************* */
				VBox dialogPaneContent = new VBox();

				Label aLabelCause = new Label("Cause :" + throwedEvent.getCause());
				Label aLabel = new Label("Stack Trace:");

				String stackTrace = JFXApplicationException.doFormattedStackTrace(throwedEvent);
				TextArea textArea = new TextArea();
				textArea.setText(stackTrace);

				dialogPaneContent.getChildren().addAll(aLabelCause, aLabel, textArea);
				// Set content for Dialog Pane
				aFatalErrorAlert.getDialogPane().setContent(dialogPaneContent);
				aFatalErrorAlert.getDialogPane().setContentText("Text ...");

				Optional<ButtonType> aOptionButtonSelected = aFatalErrorAlert.showAndWait();
				
				System.out.println(JFXApplicationException.class.getSimpleName()+" raisetofront : return : " + aOptionButtonSelected);
				
				JFXApplicationException.aFatalErrorAlert = null;

				if(!aOptionButtonSelected.isEmpty()) {
					if(aOptionButtonSelected.get() == ButtonType.OK) {
						if (bFatalQuit) {
							JFXApplicationDialog.showAlertDialog("Unrecoverable Error ... QUIT ");
							JFXApplication.notityDramaticQuit();
						}
					}else if(aOptionButtonSelected.get() == ButtonType.CANCEL) {
						if(JFXApplicationDialog.showConfirmDialog("Unrecoverable Error ... Should QUIT ")) {
							JFXApplication.notityDramaticQuit();
						}
						
					}
				}else if (bFatalQuit) {
					JFXApplicationDialog.showAlertDialog("Unrecoverable Error ... QUIT ");
					JFXApplication.notityDramaticQuit();
				}else if(JFXApplicationDialog.showConfirmDialog("Unrecoverable Error ... Should QUIT ")) {
					JFXApplication.notityDramaticQuit();
				}
			} else {

				JFXApplicationLogger.getLogger().logError(JFXApplicationException.class, aCauseMessage);
				Platform.exit(); 
			}
		} catch (Exception evERRRAISETOFRONT) {
			JFXApplicationException aEncapsulatedFatalError = new JFXApplicationException(
					"Exception when building front pane of Exception ...", evERRRAISETOFRONT,
					JFXApplicationHelper.getStackTrace());
			JFXApplicationLogger.getLogger().logError(JFXApplicationException.class, aEncapsulatedFatalError);
		}
	}

	public static String doFormattedStackTrace(Throwable eThrowableStackTrace) {
		if(eThrowableStackTrace == null) return ("[NULL]");
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		eThrowableStackTrace.printStackTrace(pw);
		return sw.toString();
	}

	public static String doFormattedStackTrace(StackTraceElement[] eStackTrace) {
		// ::  ...
		if(eStackTrace == null) return ("[NULL]");
		return Arrays.asList(eStackTrace).toString().replaceAll(",", "\n").replaceAll("\\[", "").replaceAll("\\]", "");
	}

	public static String doFormattedStackTrace(List<StackTraceElement> aStackTraceList) {
		if(aStackTraceList == null) return ("[NULL]");
		return aStackTraceList.toString().replaceAll(",", "\n").replaceAll("\\[", "").replaceAll("\\]", "");
	}

}
