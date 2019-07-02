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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
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

//this.setTooltip(this.aTooltipAutoFillIndication );
		//
		this.setTooltip(null);
		this.setPrefWidth(120.0);
		this.setPrefHeight(20.0);
		this.autosize();
		this.setVisibleRowCount(20);

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
		this.promptTextProperty().addListener((ChangeListener<String>) new ChangeListener<T>() {
			@Override
			public void changed(ObservableValue ov, T t, T sNewSelectedValue) {
				System.out.println(" combobox ov " + ov);
				System.out.println(" t : " + t);
				System.out.println(" t1 : " + sNewSelectedValue);

			}
		});
	}

	public void handleOnKeyPressed(KeyEvent e) {

		ObservableList<T> filteredList = FXCollections.observableArrayList();
		KeyCode code = e.getCode();
		if (code.isModifierKey() || code.isArrowKey())
			return;
		e.consume();
		
		String sCurrentText = this.getEditor().getText();
		System.out.println(" text "+sCurrentText);
		if (code.isLetterKey() || code.isDigitKey()) {
			sFilter =  sCurrentText; // :: += e.getText();
		}
		if (code == KeyCode.BACK_SPACE && sFilter.length() > 0) {
			sFilter = sFilter.substring(0, sFilter.length() - 1);
			this.getItems().setAll(originalItems);
		}
		if (code == KeyCode.ESCAPE) {
			sFilter = "";
		}
		//this.getEditor().setText(sFilter);
		if (sFilter.length() == 0) {
			filteredList.addAll(originalItems);

			this.aTooltipAutoFillIndication
					.setText(String.format(" (%d) match avail %n Precise your search ...  ", originalItems.size()));
			if (originalItems.size() < 20) {
				this.show();
			}
		} else {
			Stream<T> itens = originalItems.stream();
			String txtUsr = sFilter.toLowerCase();

			itens.filter(el -> el.toString().toLowerCase().contains(txtUsr)).forEach(filteredList::add);

			this.getParent().requestFocus();
			this.requestFocus();

			this.hide();
			this.aTooltipAutoFillIndication.hide();

			if (filteredList.isEmpty()) {

				this.aTooltipAutoFillIndication
						.setText(String.format("No match for : (%s) %n Correct your search to continue ... ", sFilter));

			} else {
				this.aTooltipAutoFillIndication.setText(String
						.format(" (%d) match for : (%s) %n Select a value in the list ", filteredList.size(), sFilter));
				// show the list ...
				this.show();

			}

		}

		Window stage = this.getScene().getWindow();
		double posX = stage.getX() + this.getBoundsInParent().getMinX() + this.getWidth() + 40;
		double posY = stage.getY() + this.getBoundsInParent().getMinY();
		this.aTooltipAutoFillIndication.show(stage, posX, posY);

		System.out.println(" ******** \n filter :(" + sFilter + ") elements .... " + filteredList);
		if (!this.getItems().equals(filteredList)) {
			// System.out.println(" >>>> Update::Clear combo list ... \n ******** \n ");
			// this.getItems().clear();
			System.out.println(" >>>> Update::SetAll combo list ... \n ******** \n ");
			this.getItems().setAll(filteredList);
		}

		// this.getSelectionModel().clearSelection();
		// this.getSelectionModel().select(null);
		if ((filteredList.size() == 1) && ((sFilter.length() == 0) || (code == KeyCode.ENTER))) {
			System.out.println(" >>>> Update::First combo list ... \n ******** \n ");
			this.getSelectionModel().selectFirst();
		} else if ((filteredList.size() > 1) && (this.getSelectionModel().getSelectedItem() != null)
				&& (code == KeyCode.ENTER)) {
			int iSelectedIndex = this.getSelectionModel().getSelectedIndex();
			System.out.println(" >>>> Update::Select once  combo list ... \n ******** \n ");
			this.getSelectionModel().select(null);
			this.getSelectionModel().select(iSelectedIndex);
		}
		if (isEditable()) {

			this.getEditor().setText(sFilter);
			this.getEditor().selectPositionCaret(sFilter.length());
		}

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
