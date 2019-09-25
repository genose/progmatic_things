package org.genose.java.implementation.javafx.applicationtools.views;

import org.genose.java.implementation.javafx.applicationtools.*;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.*;

import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.Window;
// https://www.turais.de/fxroot-construct-in-javafx-howto/
// https://stackoverflow.com/questions/23600926/how-to-understand-and-use-fxroot-in-javafx
// https://docs.oracle.com/javafx/2/fxml_get_started/custom_control.htm

// https://stackoverflow.com/questions/52457813/javafx-11-add-a-graphic-to-a-titledpane-on-the-right-side
// https://stackoverflow.com/questions/38525713/set-up-onetouchexpandable-button-on-javafx-splitpane
// https://stackoverflow.com/questions/31373618/how-to-add-a-titledpane-to-an-accordion-by-click-a-button
// https://docs.oracle.com/javafx/2/ui_controls/accordion-titledpane.htm
// https://o7planning.org/fr/11059/tutoriel-javafx-titledpane
// https://www.programcreek.com/java-api-examples/index.php?api=javafx.scene.layout.VBoxBuilder
//https://www.javatips.net/api/javafx.scene.parent
//https://stackoverflow.com/questions/34941411/how-to-get-controller-of-scene-in-javafx8

public class JFXApplicationScene extends Scene implements JFXApplicationDesignObjectLoad { 

	private String aSceneIdentifier = null;
	private Parent aSceneRootNode = null;
	private Object aSceneRootController = null;

	/* ************************************************ */
	/**
	 * 
	 * 
	 */
	public JFXApplicationScene() {
		super(null);
	}

	/* ************************************************ */
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 */
	public JFXApplicationScene(Parent arg0, double arg1, double arg2, boolean arg3, SceneAntialiasing arg4) {
		super(arg0, arg1, arg2, arg3, arg4);
	}

	/* ************************************************ */
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public JFXApplicationScene(Parent arg0, double arg1, double arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public JFXApplicationScene(Parent arg0, double arg1, double arg2, Paint arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/* ************************************************ */
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public JFXApplicationScene(Parent arg0, double arg1, double arg2) {
		super(arg0, arg1, arg2);
	}

	/* ************************************************ */
	/**
	 * @param arg0
	 * @param arg1
	 */
	public JFXApplicationScene(Parent arg0, Paint arg1) {
		super(arg0, arg1);

	}

	/* ************************************************ */
	/**
	 * @param arg0
	 */
	public JFXApplicationScene(Parent arg0) {
		super(arg0);
	}

	/**
	 * @return the aSceneRootController
	 */
	public Object getRootController() {
		return aSceneRootController;
	}

	/**
	 * @param aSceneRootController the aSceneRootController to set
	 */
	public void setRootController(Object aSceneRootController) {
		this.aSceneRootController = aSceneRootController;
	}

	/* ************************************************ */
	/*
	 * * Replace something like this : Parent root =
	 * FXMLLoader.load(getClass().getResource("/MainWindow/MainWindow.fxml"));
	 * primaryStage.setScene(new Scene(root, primaryStage.getHeight(),
	 * primaryStage.getWidth())); see
	 * https://docs.oracle.com/javafx/2/best_practices/jfxpub-best_practices.htm
	 * 
	 */
	public static JFXApplicationScene create(String argModuleName, String argModuleNameFile,
			JFXApplicationFunctionCallback aFuncCallback) throws JFXApplicationException {
		return JFXApplicationDesignObjectLoad.create(argModuleName, argModuleNameFile, aFuncCallback);
	}

 

	/*
	 * *****************************************************************************
	 * *
	 */
	/**
	 * 
	 * @return
	 */
	public Boolean detachTo(Object aSuperview) {
		aSuperview.getClass();
		return false;
	}

	/*
	 * *****************************************************************************
	 * *
	 */
	/**
	 * 
	 * @return
	 */
	public Boolean removeFromSuperview() {
		this.getRoot();
		return false;
	}

	/*
	 * *****************************************************************************
	 * *
	 */
	/**
	 * 
	 * @param aIconPath
	 * @return
	 */
	public Boolean setIcon(String aIconPath) {

		Window arootNode = this.getWindow();
		ObservableList<Image> aStageIcons = ((Stage) arootNode).getIcons();
		if (aStageIcons.size() > 0) {
			return (aStageIcons.set(aStageIcons.size() - 1, new Image(aIconPath)) == null);
		} else {
			return aStageIcons.add(new Image(aIconPath));
		}
	}

	/*
	 * *****************************************************************************
	 * *
	 */
	/**
	 * 
	 * @param image
	 */
	public Boolean setIcon(Image aImage) {

		return false;
	}

	public JFXApplicationScene getScene() {
		// TODO Auto-generated method stub
		return this;
	}

}
