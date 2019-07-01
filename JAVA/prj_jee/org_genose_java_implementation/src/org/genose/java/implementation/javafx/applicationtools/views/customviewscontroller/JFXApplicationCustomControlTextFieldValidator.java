/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools.views.customviewscontroller;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationDesignObjectLoad;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationLogger;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.tools.NumericRange;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * @author xenon
 *
 */
public class JFXApplicationCustomControlTextFieldValidator extends AnchorPane
		implements Initializable, JFXApplicationDesignObjectLoad {

	/* ********************** */
	static final String EMPTY_FIELD_ERROR = "Veuillez Remplir ce Champ";
	static final String DEFAULT_LABEL_ERROR_PICTOGRAMME = "\u2107";
	// :: https://howtodoinjava.com/regex/java-regex-validate-email-address/
	static final String VALIDATION_REGEX_EMAIL = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

	public enum FIELDTYPEVALIDATOR {

		UPPERCASE(1), LOWERCASE(2), EMAIL(3), TELEPHONE(4), NIRFRNUMBER(128), NOVALIDATION(0);

		static final String REPRESENTATION_ABBREGED_ENUM = "typeAbbregedEnum";
		static final String REPRESENTATION_ABBREGED_STRING = "typeAbbreged";

		private Map<Object, Object> enumDefMap = new HashMap<>();
		private static Map<Object, Object> registredDefMap = new HashMap<>();

		private FIELDTYPEVALIDATOR(int typeAbbregedEnum) {

			enumDefMap.put(REPRESENTATION_ABBREGED_ENUM, typeAbbregedEnum);
		}

		/**
		 * 
		 */
		static {
			for (FIELDTYPEVALIDATOR definitiontype : FIELDTYPEVALIDATOR.values()) {
				registredDefMap.put(definitiontype.hashCode(), definitiontype);
			}
		}

		/* ************************************************************* */
		static public Boolean contains(Integer iIDRequested) {

			for (FIELDTYPEVALIDATOR eObjectVal : FIELDTYPEVALIDATOR.values()) {
				if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ABBREGED_ENUM))
						.compareToIgnoreCase(String.valueOf(iIDRequested)) == 0)
					return true;
			}

			return false;
		}

		/* ************************************************************* */
		static public Boolean contains(String sNameRequested) {

			for (FIELDTYPEVALIDATOR eObjectVal : FIELDTYPEVALIDATOR.values()) {

				if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ABBREGED_ENUM))
						.compareToIgnoreCase(sNameRequested) == 0)
					return true;
			}
			return false;
		}

		/* ************************************************************* */
		/**
		 * Whatever you looking for, we you the soluce value !!
		 * 
		 * @param String eObjectValCode ; else Cast your argument Integer with
		 *               String.valueOf( myIntegerArgument )
		 * @return
		 */
		public Enum<FIELDTYPEVALIDATOR> getEnumByString(String eObjectValCode) {
			for (FIELDTYPEVALIDATOR eObjectVal : FIELDTYPEVALIDATOR.values()) {

				if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ABBREGED_ENUM))
						.compareToIgnoreCase(eObjectValCode) == 0)
					return eObjectVal;

			}
			return null;
		}

		/**
		 * 
		 * @return
		 */
		public String getValue(String sKeyValue) {
			/* ************************************ */
			if (sKeyValue == null)
				return null;
			/* ************************************ */
			if (String.valueOf(sKeyValue).isEmpty())
				return null;
			/* ************************************ */
			if (String.valueOf(enumDefMap.get(sKeyValue)).compareToIgnoreCase(REPRESENTATION_ABBREGED_ENUM) == 0)
				return String.valueOf(enumDefMap.get(REPRESENTATION_ABBREGED_ENUM));
			/* ************************************ */

			return null;
		}

		public boolean equalsEnum(FIELDTYPEVALIDATOR aCompared) {

			if (String.valueOf(enumDefMap.get(REPRESENTATION_ABBREGED_ENUM))
					.compareToIgnoreCase(String.valueOf(aCompared.enumDefMap.get(REPRESENTATION_ABBREGED_ENUM))) == 0)
				return true;
			return false;
		}
	}

	/* ************************************************************ */
	/*
	 * ********************************************************** 1 >> 1 pour les
	 * hommes,2 pour les femmes, 3 ou 7 pour les personnes étrangères de sexe
	 * masculin en cours d'immatriculation en France, 4 ou 8 pour les personnes
	 * étrangères de sexe féminin en cours d'immatriculation en France 2 et 3 >>Deux
	 * derniers chiffres de l'année de naissance (ce qui donne l’année à un siècle
	 * près) 4 et 5 >> 01 (janvier) à 12 (décembre) 20 ou entre 30 et 42 ou entre 50
	 * et 99, pour un mois non connu 6 et 7 >> Département de naissance: 1 à 95 et
	 * 2A ou 2B pour la Corse (naissances après le 1er janvier 1976) 96 à 98 pour
	 * les naissances hors métropole. 99 pour les naissances à l’étranger 8, 9 et 10
	 * >> CodeINSEEde la commune de naissance Dernier chiffre du code DOM-TOM et 2
	 * chiffres du code INSEE de la commune pour les naissances hors métropole. Ou
	 * code du pays de naissance pour les naissances à l’étranger 11, 12 et 13 >>
	 * Numérod’ordre de la naissance dans le mois et la commune (ou le pays) 14 et
	 * 15 >> Complément à 97 du NIR modulo 97, ou l’on remplace pour la Corse 2A par
	 * 19 et 2B par 18 Exemples : (1 62 06 62 765 118 clef 08), (1 62 06 2A 765 118
	 * clef 05), (1 62 06 2B 765 118 clef 32)
	 */
	public enum NIRFRNUMBER_VALIDATOR_DESCRIPTION {

		NIR_POS_1(1), NIR_POS_2(2), NIR_POS_3(3), NIR_POS_4(4), NIR_POS_5(5), NIR_POS_6(6), NIR_POS_7(7), NIR_POS_8(8),
		NIR_POS_9(9), NIR_POS_10(10);

		static final String REPRESENTATION_ABBREGED_ENUM = "typeAbbregedEnum";
		static final String REPRESENTATION_ABBREGED_STRING = "typeAbbreged";

		private boolean bValidatedCondition = false;
		private Map<Object, Object> enumDefMap = new HashMap<>();
		private static Map<Object, Object> registredDefMap = new HashMap<>();

		private NIRFRNUMBER_VALIDATOR_DESCRIPTION(int typeAbbregedEnum) {

			enumDefMap.put(REPRESENTATION_ABBREGED_ENUM, typeAbbregedEnum);
		}

		/**
		 * 
		 */
		static {
			for (NIRFRNUMBER_VALIDATOR_DESCRIPTION definitiontype : NIRFRNUMBER_VALIDATOR_DESCRIPTION.values()) {
				registredDefMap.put(definitiontype.hashCode(), definitiontype);
			}
		}

		/* ************************************************************* */
		static public Boolean contains(Integer iIDRequested) {

			for (NIRFRNUMBER_VALIDATOR_DESCRIPTION eObjectVal : NIRFRNUMBER_VALIDATOR_DESCRIPTION.values()) {
				if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ABBREGED_ENUM))
						.compareToIgnoreCase(String.valueOf(iIDRequested)) == 0)
					return true;
			}

			return false;
		}

		/* ************************************************************* */
		static public Boolean contains(String sNameRequested) {

			for (NIRFRNUMBER_VALIDATOR_DESCRIPTION eObjectVal : NIRFRNUMBER_VALIDATOR_DESCRIPTION.values()) {

				if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ABBREGED_ENUM))
						.compareToIgnoreCase(sNameRequested) == 0)
					return true;
			}
			return false;
		}

		/* ************************************************************* */
		/**
		 * Whatever you looking for, we you the soluce value !!
		 * 
		 * @param String eObjectValCode ; else Cast your argument Integer with
		 *               String.valueOf( myIntegerArgument )
		 * @return
		 */
		public Enum<NIRFRNUMBER_VALIDATOR_DESCRIPTION> getEnumByString(String eObjectValCode) {
			for (NIRFRNUMBER_VALIDATOR_DESCRIPTION eObjectVal : NIRFRNUMBER_VALIDATOR_DESCRIPTION.values()) {

				if (String.valueOf(eObjectVal.enumDefMap.get(REPRESENTATION_ABBREGED_ENUM))
						.compareToIgnoreCase(eObjectValCode) == 0)
					return eObjectVal;

			}
			return null;
		}

		/**
		 * 
		 * @return
		 */
		public String getValue(String sKeyValue) {
			/* ************************************ */
			if (sKeyValue == null)
				return null;
			/* ************************************ */
			if (String.valueOf(sKeyValue).isEmpty())
				return null;
			/* ************************************ */
			if (String.valueOf(enumDefMap.get(sKeyValue)).compareToIgnoreCase(REPRESENTATION_ABBREGED_ENUM) == 0)
				return String.valueOf(enumDefMap.get(REPRESENTATION_ABBREGED_ENUM));
			/* ************************************ */

			return null;
		}

		public boolean equalsEnum(NIRFRNUMBER_VALIDATOR_DESCRIPTION aCompared) {

			if (String.valueOf(enumDefMap.get(REPRESENTATION_ABBREGED_ENUM))
					.compareToIgnoreCase(String.valueOf(aCompared.enumDefMap.get(REPRESENTATION_ABBREGED_ENUM))) == 0)
				return true;
			return false;
		}

		public boolean validateCondition() {
			return bValidatedCondition;
		}
	}

	/* ********************** */
	@FXML
	private TextField cControledTextField = null;
	private int iCarretPosition = 0;
	@FXML
	// label under textfield to explain whats wrong
	private Label cControledTextFieldLabel = null;
	@FXML
	// label under textfield to explain whats wrong
	private Label cLabelFormatInfo = null;
	@FXML
	// label under textfield to explain whats wrong
	private Label cLabelErrorExplainValidation = null;
	@FXML
	// label
	private Label cLabelValidationPictogramm = null;
	@FXML
	private Tooltip cTooltipValidationInfo = null;
	@FXML
	private Tooltip cTooltipTextField = null;

	private Enum<FIELDTYPEVALIDATOR> eValidatorType = FIELDTYPEVALIDATOR.NOVALIDATION;
	/* ********************** */
	// :: https://docs.oracle.com/javase/9/docs/api/java/util/regex/Pattern.html
	private java.util.regex.Pattern aRegexPatternFieldValidator = null;
	private NumericRange aRangeValidator = null;
	private Color aColorForTextFieldError = null;
	private Color aColorForTextFieldValidated = null;
	/* ********************** */

	HashMap<String, String> aValidationMap = new HashMap<>();
	private String sOldValueForControledTextField = "";

	private String sNewValueForControledTextField = "";

	private String sComboBoxFieldValidatorSelectedValue = "";

	private boolean bShouldUpdateControllerTextFieldValue = false;
	private boolean bShouldShowTooltipTextField = false;

	JFXApplicationCustomControlComboxBoxAutoFill<String> aComboBoxFieldValidator = null;

	@FXML
	public void initialize() {
		doInit();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		doInit();

	}

	private void doInit() {
		try {

			Objects.requireNonNull(cControledTextField, JFXApplicationException.ERROR_MESSAGE_DESIGNLOAD);
			Objects.requireNonNull(cLabelErrorExplainValidation, JFXApplicationException.ERROR_MESSAGE_DESIGNLOAD);
			Objects.requireNonNull(cLabelValidationPictogramm, JFXApplicationException.ERROR_MESSAGE_DESIGNLOAD);
			Objects.requireNonNull(cTooltipTextField, JFXApplicationException.ERROR_MESSAGE_DESIGNLOAD);
			Objects.requireNonNull(cTooltipValidationInfo, JFXApplicationException.ERROR_MESSAGE_DESIGNLOAD);

			cLabelValidationPictogramm.setVisible(false);
			cLabelErrorExplainValidation.setVisible(false);
			cLabelFormatInfo.setVisible(false);

			cControledTextField.setUserData(aValidationMap);

			// cControledTextField.getTooltip().setText("Entrer votre texte ...");
			cControledTextField.setTooltip(null);
			cTooltipTextField.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

			cTooltipTextField.setAutoHide(false);

			cTooltipTextField.setShowDelay(new Duration(0));
			cTooltipTextField.setHideDelay(new Duration(0));

			ObservableList<String> aListOfItems = FXCollections.observableArrayList();
			aListOfItems.add("0000");
			aListOfItems.add("4527");
			aListOfItems.add("99");
			aListOfItems.add("12");
			aListOfItems.add("34");
			Button aButtonValidateChoice = new Button("OK");
			aButtonValidateChoice.setPrefHeight(30.0);
			aButtonValidateChoice.setPrefWidth(60.0);

			AnchorPane aAnchorPane = new AnchorPane();
			aComboBoxFieldValidator = new JFXApplicationCustomControlComboxBoxAutoFill<String>();
			// aComboBox.setOpaqueInsets(new Insets(20, 10, 20, 10));
			aComboBoxFieldValidator.setOriginalItems(aListOfItems);
			aComboBoxFieldValidator.setEditable(false);
			// aComboBox.setValue("12");

			aComboBoxFieldValidator.valueProperty()
					.addListener((ChangeListener<? super String>) new ChangeListener<String>() {
						@Override
						public void changed(ObservableValue ov, String t, String sNewSelectedValue) {
							System.out.println("ov " + ov);
							System.out.println(" t : " + t);
							System.out.println(" t1 : " + sNewSelectedValue);
							if (sNewSelectedValue == null) {
								System.out.println(" newvalue is null ... ");
								return;
							}
							sComboBoxFieldValidatorSelectedValue = sNewSelectedValue;
							aComboBoxFieldValidator.setFilter("");
							aComboBoxFieldValidator.setValue(aComboBoxFieldValidator.getItems().get(0));
							// if (bShouldShowTooltipTextField) {
							tooltipHide();
							// bShouldShowTooltipTextField = (!bShouldShowTooltipTextField);
							sNewValueForControledTextField = "";
							cControledTextField.setText(sComboBoxFieldValidatorSelectedValue);
							cControledTextField.selectPositionCaret(sComboBoxFieldValidatorSelectedValue.length());
							// }

							cControledTextField.setEditable(true);
							cControledTextField.requestFocus();
						}
					});

			aAnchorPane.getChildren().add(aComboBoxFieldValidator);
			// aAnchorPane.getChildren().add(aButtonValidateChoice);

			aAnchorPane.setMaxHeight(120.0);

			cTooltipTextField.setGraphic(aAnchorPane);
			cTooltipTextField.setWidth(160.0);
			cTooltipTextField.setHeight(120.0);

			cTooltipTextField.setPrefWidth(160.0);
			cTooltipTextField.setPrefHeight(120.0);

			cControledTextField.setOnMouseEntered(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {

					/*
					 * if(!cTooltipTextField.isShowing() && bShouldShowTooltipTextField)
					 * tooltipShow();
					 */

				}
			});

			cTooltipTextField.setOnShowing(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent arg0) {
					/*
					 * if(!bShouldShowTooltipTextField) tooltipHide();
					 */

				}

			});

			cControledTextField.setOnMouseExited(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {

					/*
					 * if(cTooltipTextField.isShowing()&& bShouldShowTooltipTextField)
					 * tooltipShow();
					 */

				}
			});

			/* ************************************************************* */
			cControledTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent arg0) {
					iCarretPosition = cControledTextField.getCaretPosition();
					sOldValueForControledTextField = cControledTextField.getText();
					onKeyDownValidate(arg0);

				}
			});
			/* ************************************************************* */
			cControledTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent arg0) {

					if (bShouldUpdateControllerTextFieldValue) {
						cControledTextField.setText(sNewValueForControledTextField);
						cControledTextField.selectPositionCaret(iCarretPosition);
					}
					bShouldUpdateControllerTextFieldValue = false;
				}

			});

		} catch (Exception evERRDOINIT) {
			JFXApplicationLogger.getLogger().logError(this.getClass(), evERRDOINIT);
		}
	}

	/**
	 * 
	 */
	public JFXApplicationCustomControlTextFieldValidator() {
		super();

	}

	/**
	 * 
	 * @param aRegexString, a regex to use in validation process ...
	 * @return
	 */
	public boolean setValidationRegex(String aRegexString) {
		try {
			aRegexPatternFieldValidator = java.util.regex.Pattern.compile(aRegexString);
			return (aRegexPatternFieldValidator != null);
		} catch (PatternSyntaxException evERRREGEXCOMPILE) {
			JFXApplicationLogger.getLogger().logError(this.getClass(), evERRREGEXCOMPILE);
		}
		return false;
	}

	/**
	 * @return the bLabelVisible
	 */
	public Boolean getFieldNameVisible() {
		return cControledTextFieldLabel.isVisible();
	}

	/**
	 * @param bLabelVisible the bLabelVisible to set
	 */
	public void setLabelFieldNameVisible(Boolean labelVisible) {
		cControledTextFieldLabel.setVisible(labelVisible);
	}

	/**
	 * 
	 * @param sFieldName
	 */
	public void setLabelFieldNameValue(String sFieldName) {
		cControledTextFieldLabel.setText(sFieldName);
	}

	/**
	 * 
	 * @param eValidationType
	 */
	public void setValidationType(Enum<FIELDTYPEVALIDATOR> eValidationType) {
		this.eValidatorType = eValidationType;
	}

	/**
	 * 
	 * @param eValidationType
	 * @return
	 */
	public Enum<FIELDTYPEVALIDATOR> getValidationType(FIELDTYPEVALIDATOR eValidationType) {
		return this.eValidatorType;
	}

	/**
	 * @param aRegexPatternFieldValidator the aRegexPatternFieldValidator to set
	 */
	public void setRegexPatternFieldValidator(java.util.regex.Pattern aRegexPatternFieldValidator) {
		this.aRegexPatternFieldValidator = aRegexPatternFieldValidator;
	}

	/**
	 * @param aRangeValidator the aRangeValidator to set
	 */
	public void setRangeValidator(NumericRange aRangeValidator) {
		this.aRangeValidator = aRangeValidator;
	}

	/**
	 * @param aColorForTextFieldError the aColorForTextFieldError to set
	 */
	public void setColorForTextFieldError(Color colorForTextFieldError) {
		this.aColorForTextFieldError = colorForTextFieldError;
	}

	/**
	 * @param aColorForTextFieldValidated the aColorForTextFieldValidated to set
	 */
	public void setColorForTextFieldValidated(Color colorForTextFieldValidated) {
		this.aColorForTextFieldValidated = colorForTextFieldValidated;
	}

	@FXML
	public void onKeyDownValidate(KeyEvent ev) {

		/*
		 * if (ev.getText().length() == 0) { return; }
		 */
		// more than1 key was pressed ...
		/*
		 * if (ev.getCode().values().length > 1) { return; }
		 */

		if (((FIELDTYPEVALIDATOR) eValidatorType).equalsEnum(FIELDTYPEVALIDATOR.NOVALIDATION)) {
			// whetever, do default handle

			if (iCarretPosition >= sOldValueForControledTextField.length()) {
				sNewValueForControledTextField = String.format("%s%s", sOldValueForControledTextField, ev.getText());
				iCarretPosition = sNewValueForControledTextField.length();
			} else if (iCarretPosition > 0) {
				sNewValueForControledTextField = String.format("%s%s%s",
						sOldValueForControledTextField.substring(0, iCarretPosition), ev.getText(),
						sOldValueForControledTextField.substring(iCarretPosition,
								sOldValueForControledTextField.length()));

			} else {
				sNewValueForControledTextField = String.format("%s%s", ev.getText(), sOldValueForControledTextField);
				// take the same position
				iCarretPosition = 1;
			}
			tooltipShow();

		} else if (((FIELDTYPEVALIDATOR) eValidatorType).equalsEnum(FIELDTYPEVALIDATOR.NIRFRNUMBER)) {

			// cControledTextField.setTooltip(null);
			cTooltipTextField.setAutoHide(false);

			cControledTextField.setEditable(false);
			bShouldShowTooltipTextField = true;
			tooltipShow();
			aComboBoxFieldValidator.requestFocus();
			// aComboBoxFieldValidator.setFilter(ev.getText());
			aComboBoxFieldValidator.handleOnKeyPressed(ev);
			System.out.println(" return keyevent");
			/*
			 * if (validate()) { // tooltipHide(); sNewValueForcControledTextField =
			 * String.format("%s%s", sOldValueForcControledTextField, ev.getText()); } else
			 * { tooltipShow(); sNewValueForcControledTextField =
			 * sOldValueForcControledTextField; }
			 */
		} else if (((FIELDTYPEVALIDATOR) eValidatorType).equalsEnum(FIELDTYPEVALIDATOR.UPPERCASE)) {
			sNewValueForControledTextField = String.format("%s%s", sOldValueForControledTextField, ev.getText())
					.toUpperCase();
		} else if (((FIELDTYPEVALIDATOR) eValidatorType).equalsEnum(FIELDTYPEVALIDATOR.LOWERCASE)) {
			sNewValueForControledTextField = String.format("%s%s", sOldValueForControledTextField, ev.getText())
					.toLowerCase();
		} else if (((FIELDTYPEVALIDATOR) eValidatorType).equalsEnum(FIELDTYPEVALIDATOR.TELEPHONE)) {
			// whetever, do default handle
			sNewValueForControledTextField = sOldValueForControledTextField;
		} else if (((FIELDTYPEVALIDATOR) eValidatorType).equalsEnum(FIELDTYPEVALIDATOR.EMAIL)) {
			// whetever, do default handle
			sNewValueForControledTextField = sOldValueForControledTextField;
		}
		// cControledTextField.requestFocus();
		System.out.println(" ev getEventType : " + ev.getEventType().getClass() + " :: " + ev + " ;; "
				+ sOldValueForControledTextField);

	}

	/**
	 * force show tooltip ...
	 */
	private void tooltipInfoShow() {

		cTooltipValidationInfo.show(JFXApplication.getJFXApplicationSingleton().getPrimaryStage(),
				60.0 + cLabelValidationPictogramm.getScene().getX()
						+ cLabelValidationPictogramm.getScene().getWindow().getX(),
				20.0 + cLabelValidationPictogramm.getScene().getY()
						+ cLabelValidationPictogramm.getScene().getWindow().getY());
	}

	private void tooltipInfoHide() {
		cTooltipValidationInfo.hide();
	}

	/**
	 * force show tooltip ...
	 */
	private void tooltipShow() {

		Window stage = cControledTextField.getScene().getWindow();
		double posX = stage.getX() + cControledTextField.getBoundsInParent().getMinX() + cControledTextField.getWidth();
		double posY = stage.getY() + cControledTextField.getBoundsInParent().getMinY() + cControledTextField.getHeight()
				+ 20;

		cTooltipTextField.show(JFXApplication.getJFXApplicationSingleton().getPrimaryStage(), posX, posY);
	}

	private void tooltipHide() {
		cTooltipTextField.hide();
	}

	/**
	 * 
	 * @return true on primary rules validation success
	 */
	public boolean validate() {
		Boolean bRangeLengthValidate = true;
		Boolean bRegexValidate = true;

		/* ******************** */
		if (aRegexPatternFieldValidator != null) {
			Matcher regexMatcher = aRegexPatternFieldValidator.matcher(cControledTextField.getText());
			bRegexValidate = regexMatcher.matches();
		}
		/* ******************** */
		if (aRangeValidator != null) {
			int iTextLenght = cControledTextField.getText().length();
			bRangeLengthValidate = aRangeValidator.contains(iTextLenght);
		}
		/* ******************** */
		if (!bRegexValidate || !bRangeLengthValidate) {
			if (aColorForTextFieldError != null) {
				BorderStroke aBorderStrokeStyle = new BorderStroke(aColorForTextFieldError, BorderStrokeStyle.SOLID,
						null, new BorderWidths(5));
				Border aBorderColoredForField = new Border(aBorderStrokeStyle);
				cControledTextField.setBorder(aBorderColoredForField);
			}
			cLabelValidationPictogramm.setVisible(true);
			cLabelErrorExplainValidation.setVisible(true);

		} else {
			if (aColorForTextFieldValidated != null) {
				BorderStroke aBorderStrokeStyle = new BorderStroke(aColorForTextFieldValidated, BorderStrokeStyle.SOLID,
						null, new BorderWidths(5));
				Border aBorderColoredForField = new Border(aBorderStrokeStyle);
				cControledTextField.setBorder(aBorderColoredForField);
			}
			cLabelValidationPictogramm.setVisible(false);
			cLabelErrorExplainValidation.setVisible(false);
		}
		/* ******************** */
		return bRegexValidate && bRangeLengthValidate;
	}

}
