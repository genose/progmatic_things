/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools.views.customviewscontroller;

import java.beans.DesignMode;
import java.io.Serializable;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationDesignObjectLoad;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * @author xenon
 * @param <T>
 *
 */
public class JFXApplicationCustomControlComboxBoxAutoFill<T> extends ComboBox<T>
		implements Initializable, JFXApplicationDesignObjectLoad {

	@FXML
	private Button aButtonValidateChoice = null;

	private Tooltip aTooltipAutoFillIndication = null;

	private ComboBox<T> aComboxBoxAutofilleable = null;
	private ObservableList<T> originalItems = FXCollections.observableArrayList();
	String sFilter = "";

	KeyCode aKeyCode = null;

	public JFXApplicationCustomControlComboxBoxAutoFill() {
		super();

		doInit();
	}

	public void setOriginalItems(ObservableList<T> originalItemsList) {

		this.originalItems.clear();
		this.originalItems.addAll(originalItemsList);

		this.getItems().clear();
		this.getItems().addAll(this.originalItems);

	}

	@FXML
	public void initialize() {
		// doInit();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// doInit();

	}

	private void doInit() {

		/*
		 * for (String el : aListOfItems) { this.getItems().add((T) el.toString());
		 * originalItems.add((T) el); }
		 */

		this.aTooltipAutoFillIndication = new Tooltip();
		this.aTooltipAutoFillIndication.setHeight(20.0);
		this.aTooltipAutoFillIndication.setWidth(120.0);

		this.aTooltipAutoFillIndication.setContentDisplay(ContentDisplay.TEXT_ONLY);

		this.aTooltipAutoFillIndication.setAutoHide(false);

		this.aTooltipAutoFillIndication.setShowDelay(new Duration(0));
		this.aTooltipAutoFillIndication.setHideDelay(new Duration(0));

		this.aComboxBoxAutofilleable = this;
		this.aComboxBoxAutofilleable.getSelectionModel().selectedItemProperty().addListener( (ObservableValue<? extends T> arg0, T arg1, T arg2) -> {
				// TODO Auto-generated method stub
				System.out.println(" Values : "+arg0.getClass());
				System.out.println(" Values : "+arg0);
				System.out.println(" Values : 1"+arg1);
				System.out.println(" Values : 2"+arg2);
			} );
//this.setTooltip(this.aTooltipAutoFillIndication );
		//
		this.setTooltip(null);
		this.setPrefWidth(120.0);
		this.setPrefHeight(20.0);
		this.autosize();
		this.setVisibleRowCount(20);

		this.getEditor().setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent evKeyEvent) {

				System.out.println(" ********  \n anchored Combox keyevent " + evKeyEvent);
				handleOnKeyPressed(evKeyEvent);

			}
		});
		this.getEditor().setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent arg0) {

				// System.out.println(" anchor Combox keyevent "+arg0);
				// aComboBoxFieldValidator.requestFocus();
				//
				handleOnKeyReleased(arg0);

			}
		});
		/*
		 * aButtonValidateChoice.setOnMouseClicked(new EventHandler<MouseEvent>() {
		 * 
		 * @Override public void handle(MouseEvent arg0) {
		 * System.out.println("Selected value "+aComboxBoxAutofilleable.
		 * getSelectionModel().selectedItemProperty());
		 * 
		 * } });
		 */

		/* ************************************************************* */
		/*
		 * this.setOnKeyReleased(new EventHandler<KeyEvent>() {
		 * 
		 * @Override public void handle(KeyEvent arg0) {
		 * 
		 * handleOnKeyPressed(arg0);
		 * 
		 * } });
		 */

	}

	public void handleOnKeyPressed(KeyEvent evKeyEvent) {

		// this.requestFocus();

		aKeyCode = evKeyEvent.getCode();

		if (aKeyCode.isModifierKey() || aKeyCode.isArrowKey())
			return;

		if (aKeyCode == KeyCode.BACK_SPACE && sFilter.length() > 0) {
			sFilter = sFilter.substring(0, sFilter.length() - 1);
		} else if (aKeyCode == KeyCode.ESCAPE) {
			sFilter = "";
			getEditor().setText(sFilter);
		} else if (aKeyCode.isLetterKey() || aKeyCode.isDigitKey()) {
			sFilter += evKeyEvent.getText();
		}

	}

	private void handleOnKeyReleased(KeyEvent e) {

		ObservableList<T> oFilteredList = FXCollections.observableArrayList();

		e.consume();

		String sCurrentText = this.getEditor().getText();
		System.out.println(" text " + sCurrentText + " :: " + e.getText());

		if (sFilter.length() == 0) {
			oFilteredList.addAll(originalItems);

			this.aTooltipAutoFillIndication
					.setText(String.format(" (%d) match avail %n Precise your search ...  ", originalItems.size()));
			if (originalItems.size() < 20) {
				this.show();
			}
		} else {
			Stream<T> itens = originalItems.stream();
			String sFilterLowerCaseReference = sFilter.toLowerCase();

			itens.filter(el -> el.toString().toLowerCase().contains(sFilterLowerCaseReference))
					.forEach(oFilteredList::add);

			//this.getParent().requestFocus();
			//this.requestFocus();

			this.hide();
			this.aTooltipAutoFillIndication.hide();

			if (oFilteredList.isEmpty()) {

				this.aTooltipAutoFillIndication
						.setText(String.format("No match for : (%s) %n Correct your search to continue ... ", sFilter));

			} else {
				this.aTooltipAutoFillIndication.setText(String.format(
						" (%d) match for : (%s) %n Select a value in the list ", oFilteredList.size(), sFilter));
				// show the list ...
				this.show();

			}

		}

		Window stage = this.getScene().getWindow();
		double posX = stage.getX() + this.getBoundsInParent().getMinX() + this.getWidth() + 40;
		double posY = stage.getY() + this.getBoundsInParent().getMinY();
		this.aTooltipAutoFillIndication.show(stage, posX, posY);

		System.out.println(" ******** \n filter :(" + sFilter + ") elements .... " + oFilteredList);
		if (!this.getItems().equals(oFilteredList)) {
			// System.out.println(" >>>> Update::Clear combo list ... \n ******** \n ");
			// this.getItems().clear();
			System.out.println(" >>>> Update::SetAll combo list ... \n ******** \n ");
			this.getItems().setAll(oFilteredList);
		}

		// this.getSelectionModel().clearSelection();
		// this.getSelectionModel().select(null);
		if ((oFilteredList.size() == 1) && ((sFilter.length() == 0) || (aKeyCode == KeyCode.ENTER))) {
			System.out.println(" >>>> Update::First combo list ... \n ******** \n ");
			this.getSelectionModel().selectFirst();
		} else if ((oFilteredList.size() > 1) && (this.getSelectionModel().getSelectedItem() != null)
				&& (aKeyCode == KeyCode.ENTER)) {
			int iSelectedIndex = this.getSelectionModel().getSelectedIndex();
			System.out.println(" >>>> Update::Select once  combo list ... \n ******** \n ");
			this.getSelectionModel().select(null);
			this.getSelectionModel().select(iSelectedIndex);
		}	// 
		this.getEditor().setText(sFilter);
		if (isEditable() && (aKeyCode.isArrowKey())) {

		
			this.getEditor().selectPositionCaret(sFilter.length());

		}else

		System.out.println(" >>>> END :: Update combo list ... \n ******** \n ");
	}

	public void handleOnHiding(Event e) {
		sFilter = "";
		// getTooltip().hide();
		T s = getSelectionModel().getSelectedItem();
		getItems().setAll(originalItems);
		getSelectionModel().select(s);
	}

	public static <T> JFXApplicationCustomControlComboxBoxAutoFill<?> create() {
		return (JFXApplicationCustomControlComboxBoxAutoFill<?>) new JFXApplicationCustomControlComboxBoxAutoFill<>(); // ::
																														// JFXApplicationDesignObjectLoad.create(JFXApplicationCustomControlComboxBoxAutoFill.class).getUserData();
	}

	public void setFilter(String text) {
		sFilter = text;

	}

}
