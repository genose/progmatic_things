/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools.views.customviewscontroller;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import org.genose.java.implementation.javafx.applicationtools.JFXApplicationDesignObjectLoad;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationCustomControlSplitMenuHBox extends AnchorPane implements Initializable, JFXApplicationDesignObjectLoad {

	@FXML
	private AnchorPane vRootAnchorPanel = null;

	@FXML
	private SplitPane vSplitRootPanel = null;

	@FXML
	private AnchorPane vLeftChildPanel = null;

	@FXML
	private AnchorPane vRightChildPanel = null;

	@FXML
	private Accordion vAccordionChildPanel = null;

	@FXML
	private Button vButtonSlider = null;
	@FXML
	private Button vButtonSliderMode = null;

	private boolean bSlideMenuOnLeft = false;

	private ObservableList<TitledPane> vMenuPanels = FXCollections.emptyObservableList();

	private static final String message = JFXApplicationException.ERROR_MESSAGE_DESIGNLOAD;

	/**
	 * 
	 */
	@FXML
	public void initialize() {
		System.out.println(" init called ....");
		doInit();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		System.out.println(" init called with url \n " + arg0 + "\n" + arg1);
		doInit();
	}

	/**
	 * 
	 */
	public JFXApplicationCustomControlSplitMenuHBox() {
		super();
	}

	public void doInit() {

		Objects.requireNonNull(vRootAnchorPanel, message);
		Objects.requireNonNull(vSplitRootPanel, message);
		Objects.requireNonNull(vButtonSlider, message);
		Objects.requireNonNull(vAccordionChildPanel, message);
		Objects.requireNonNull(vLeftChildPanel, message);
		Objects.requireNonNull(vRightChildPanel, message);
		vLeftChildPanel.setUserData(Integer.valueOf(0));
		vRightChildPanel.setUserData(Integer.valueOf(1));
		vRightChildPanel.setMaxWidth(vRightChildPanel.getMinWidth());
		vButtonSlider.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				toggleMenuPanel();

			}
		});
		vButtonSliderMode.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				setSlideMenuOnLeft(!bSlideMenuOnLeft);

			}
		});
	}

	/**
	 * Show / hide panel on the rigth
	 */
	@FXML
	void toggleMenuPanel() {
		AnchorPane aRefToAnchorPaneSlider = ((bSlideMenuOnLeft) ? vLeftChildPanel : vRightChildPanel);
		Double dPaneWidth = aRefToAnchorPaneSlider.getWidth();

		if (dPaneWidth > aRefToAnchorPaneSlider.getMinWidth()) {

			aRefToAnchorPaneSlider.setMaxWidth(aRefToAnchorPaneSlider.getMinWidth());

			vButtonSlider.setText("=");
			vButtonSlider.getTooltip().setText("Ouvrir le panel");
		} else {

			aRefToAnchorPaneSlider.setMaxWidth(vRootAnchorPanel.getMaxWidth() - vRootAnchorPanel.getMinWidth());

			vButtonSlider.setText("X");
			vButtonSlider.getTooltip().setText("Fermer le panel");

		}

	}

	/**
	 * 
	 * @param aName
	 * @param aUserData
	 * @return
	 */
	public int addMenuTitledPanel(String aName, Object aUserData) {
		AnchorPane oNewPanelContent = new AnchorPane();
		TitledPane oNewTitledPane = new TitledPane(aName, oNewPanelContent);
		oNewTitledPane.setUserData(aUserData);
		vAccordionChildPanel.getPanes().add(oNewTitledPane);
		vMenuPanels.add(oNewTitledPane);
		return vAccordionChildPanel.getChildrenUnmodifiable().size();
	}

	/**
	 * 
	 * @param iMenuPanelIndex
	 * @param aNodeElement
	 * @return
	 */
	public boolean addContentToMenuPanel(int iMenuPanelIndex, Node aNodeElement) {
		if (iMenuPanelIndex < vMenuPanels.size()) {
			TitledPane oTitledPane = vMenuPanels.get(iMenuPanelIndex);
			AnchorPane oChildContentPane = (AnchorPane) oTitledPane.getContent();
			return (boolean) (oChildContentPane).getChildren().add(aNodeElement);
		} else {
			throw new RuntimeException(String.format("%s :  Index (%d) is out of bounds (%d) ", this.getClass(),
					iMenuPanelIndex, vMenuPanels.size()));
		}

	}

	public Node getContentMenuPanel(int iMenuPanelIndex) {
		if (iMenuPanelIndex < vMenuPanels.size()) {
			TitledPane oTitledPane = vMenuPanels.get(iMenuPanelIndex);
			return oTitledPane.getContent();
		} else {
			throw new RuntimeException(String.format("%s :  Index (%d) is out of bounds (%d) ", this.getClass(),
					iMenuPanelIndex, vMenuPanels.size()));
		}
	}

	/**
	 * 
	 * @param bSlideMenuToLeft , set the disposition of toggle menu
	 */
	public void setSlideMenuOnLeft(boolean bSlideMenuToLeft) {

		bSlideMenuOnLeft = bSlideMenuToLeft;

		Objects.requireNonNull(vRootAnchorPanel, message);
		Objects.requireNonNull(vSplitRootPanel, message);
		Objects.requireNonNull(vButtonSlider, message);
		Objects.requireNonNull(vAccordionChildPanel, message);
		Objects.requireNonNull(vLeftChildPanel, message);
		Objects.requireNonNull(vRightChildPanel, message);

		if (bSlideMenuOnLeft) {

			ObservableList<Node> aNodeList = vSplitRootPanel.getItems();

			if (((Integer) vSplitRootPanel.getItems().get(0).getUserData()).intValue() != 1) {

				// the tab have a dynamic max via toggle
				Double dMinRigth = vRightChildPanel.getMinWidth();
				// fixed value ... so we need to store it
				Double dMaxLeft = vLeftChildPanel.getMaxWidth();
				Double dMinLeft = vLeftChildPanel.getMinWidth();

				vLeftChildPanel = (AnchorPane) vSplitRootPanel.getItems().get(1);
				// this is button toggle
				vRightChildPanel = (AnchorPane) vSplitRootPanel.getItems().get(0);

				// swap value once we swapped refs ...
				vLeftChildPanel.setMinWidth(dMinRigth);
				vRightChildPanel.setMinWidth(dMinLeft);
				vRightChildPanel.setMaxWidth(dMaxLeft);

				vSplitRootPanel.getItems().clear();
				vSplitRootPanel.getItems().add(vLeftChildPanel);
				vSplitRootPanel.getItems().add(vRightChildPanel);
			}

		} else {
			if (((Integer) vSplitRootPanel.getItems().get(0).getUserData()).intValue() != 0) {

				// the tab have a dynamic max via toggle
				Double dMinRigth = vRightChildPanel.getMinWidth();
				// fixed value ... so we need to store it
				Double dMaxLeft = vLeftChildPanel.getMaxWidth();
				Double dMinLeft = vLeftChildPanel.getMinWidth();

				// this is button toggle
				vLeftChildPanel = (AnchorPane) vSplitRootPanel.getItems().get(1);

				vRightChildPanel = (AnchorPane) vSplitRootPanel.getItems().get(0);

				// swap value once we swapped refs ...
				vLeftChildPanel.setMinWidth(dMinRigth);
				vRightChildPanel.setMinWidth(dMinLeft);
				vRightChildPanel.setMaxWidth(dMaxLeft);

				vSplitRootPanel.getItems().clear();
				vSplitRootPanel.getItems().add(vLeftChildPanel);
				vSplitRootPanel.getItems().add(vRightChildPanel);
			}
		}

	}
	/**
	 * Dynamicly create a class controller .... and load associated FXML File ...
	 * @return
	 */

	public static Parent create() {
		return JFXApplicationDesignObjectLoad.create(null);
	}


}
