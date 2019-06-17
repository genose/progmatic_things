/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools.views.customviewscontroller;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationCustomControl extends Control {

	
	@FXML
	private AnchorPane vRootPane = null;
	
	@FXML
	private AnchorPane vLeftChildPane = null;
	
	@FXML
	private AnchorPane vRightChildPane = null;
	
	@FXML
	private Accordion vAccordionChildPane = null;
	
	
	
	@FXML
	private Button vButtonSlider = null;
	
	/**
	 * 
	 */
	@FXML
	public void initialize() {
		TitledPane aTiltledPane = new TitledPane();
		aTiltledPane.setText("TitlePane Default ...");
		vAccordionChildPane.getPanes().addAll(aTiltledPane );
	}
	public JFXApplicationCustomControl() {
		super();
	}
	

}
