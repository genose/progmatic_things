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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
	private String sOldValueForcControledTextField = "";

	private String sNewValueForcControledTextField = "";
	private boolean bShouldUpdateControllerTextFieldValue = false;
	@FXML
	public void initialize() {
		doInit();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		doInit();

	}

	private void doInit() {
		Objects.requireNonNull(cControledTextField, JFXApplicationException.ERROR_MESSAGE_DESIGNLOAD);
		Objects.requireNonNull(cLabelErrorExplainValidation, JFXApplicationException.ERROR_MESSAGE_DESIGNLOAD);
		Objects.requireNonNull(cLabelValidationPictogramm, JFXApplicationException.ERROR_MESSAGE_DESIGNLOAD);
		Objects.requireNonNull(cTooltipTextField, JFXApplicationException.ERROR_MESSAGE_DESIGNLOAD);
		Objects.requireNonNull(cTooltipValidationInfo, JFXApplicationException.ERROR_MESSAGE_DESIGNLOAD);

		cLabelValidationPictogramm.setVisible(false);
		cLabelErrorExplainValidation.setVisible(false);
		cLabelFormatInfo.setVisible(false);

		cControledTextField.setUserData(aValidationMap);

		cControledTextField.getTooltip().setText("Entrer votre texte ...");
		
		cTooltipTextField.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		
		ObservableList<String> aListOfItems = FXCollections.emptyObservableList();
		aListOfItems.add("0000");
		aListOfItems.add("99");
 		AnchorPane aAnchorPane = new AnchorPane();
		ComboBox aComboBox = new ComboBox<>();
aComboBox.setEditable(true);
aComboBox.setValue("99");
aComboBox.setItems(aListOfItems);
		aAnchorPane.getChildren().add(aComboBox);
		cTooltipTextField.setGraphic( aAnchorPane );
		cTooltipTextField.setWidth(120.0);
		cTooltipTextField.setHeight(120.0);
		
		cControledTextField.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				tooltipShow();
				
			}
		});
		
		cControledTextField.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				tooltipShow();
				
			}
		});
		
		/* ************************************************************* */
		cControledTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent arg0) {
				arg0.consume();
				onKeyDownValidate(arg0);

			}
		});
		/* ************************************************************* */
		cControledTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent arg0) {
				arg0.consume();
				if(bShouldUpdateControllerTextFieldValue) {
				cControledTextField.setText(sNewValueForcControledTextField);
				cControledTextField.selectPositionCaret(iCarretPosition);
				}
				bShouldUpdateControllerTextFieldValue  = false;
			}

		});

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

		if(ev.getText().length() == 0)
		{
			return;
		}
		// more than1 key was pressed ...
		if(ev.getCode().values().length >1) {
			return;
		}
		iCarretPosition = cControledTextField.getCaretPosition();
		sOldValueForcControledTextField = cControledTextField.getText();

		if (((FIELDTYPEVALIDATOR) eValidatorType).equalsEnum(FIELDTYPEVALIDATOR.NOVALIDATION)) {
			// whetever, do default handle
		
			if(iCarretPosition >= sOldValueForcControledTextField.length()) {
				sNewValueForcControledTextField = String.format("%s%s", sOldValueForcControledTextField, ev.getText());
				iCarretPosition = sNewValueForcControledTextField.length();
			}else if(iCarretPosition >0) {
				sNewValueForcControledTextField = 
						String.format("%s%s%s", sOldValueForcControledTextField.substring(0, iCarretPosition),
				ev.getText(),
				sOldValueForcControledTextField.substring(iCarretPosition, sOldValueForcControledTextField.length()));
				
			}else {
				sNewValueForcControledTextField = String.format("%s%s", ev.getText(), sOldValueForcControledTextField );
				// take the same position 
				iCarretPosition= 1;
			}
		tooltipShow();
			
		

		} else if (((FIELDTYPEVALIDATOR) eValidatorType).equalsEnum(FIELDTYPEVALIDATOR.NIRFRNUMBER)) {

			if (validate()) {
				tooltipHide();
				sNewValueForcControledTextField = String.format("%s%s", sOldValueForcControledTextField, ev.getText());
			} else {
				tooltipShow();
				sNewValueForcControledTextField = sOldValueForcControledTextField;
			}
		} else if (((FIELDTYPEVALIDATOR) eValidatorType).equalsEnum(FIELDTYPEVALIDATOR.UPPERCASE)) {
			sNewValueForcControledTextField = String.format("%s%s", sOldValueForcControledTextField, ev.getText())
					.toUpperCase();
		} else if (((FIELDTYPEVALIDATOR) eValidatorType).equalsEnum(FIELDTYPEVALIDATOR.LOWERCASE)) {
			sNewValueForcControledTextField = String.format("%s%s", sOldValueForcControledTextField, ev.getText())
					.toLowerCase();
		} else if (((FIELDTYPEVALIDATOR) eValidatorType).equalsEnum(FIELDTYPEVALIDATOR.TELEPHONE)) {
			// whetever, do default handle
			sNewValueForcControledTextField = sOldValueForcControledTextField;
		} else if (((FIELDTYPEVALIDATOR) eValidatorType).equalsEnum(FIELDTYPEVALIDATOR.EMAIL)) {
			// whetever, do default handle
			sNewValueForcControledTextField = sOldValueForcControledTextField;
		}
		//cControledTextField.requestFocus();
		System.out.println(" ev getEventType : " + ev.getEventType().getClass() + " :: " + ev+ " ;; "
				+ sOldValueForcControledTextField);
bShouldUpdateControllerTextFieldValue = true;
	}

	/**
	 * force show tooltip ...
	 */
	private void tooltipInfoShow() {
		cTooltipValidationInfo.setAutoHide(false);
		//cTooltipValidationInfo.setHideDelay(new Duration(5.0));

		cTooltipValidationInfo.show(JFXApplication.getJFXApplicationSingleton().getPrimaryStage(),
				60.0 + cLabelValidationPictogramm.getScene().getX() + cLabelValidationPictogramm.getScene().getWindow().getX(),
				20.0 + cLabelValidationPictogramm.getScene().getY() + cLabelValidationPictogramm.getScene().getWindow().getY());
	}

	private void tooltipInfoHide() {
		cTooltipValidationInfo.hide();
	}
	/**
	 * force show tooltip ...
	 */
	private void tooltipShow() {
		cTooltipTextField.setAutoHide(false);
		//cTooltipValidationInfo.setHideDelay(new Duration(5.0));
		
		cTooltipTextField.show(JFXApplication.getJFXApplicationSingleton().getPrimaryStage(),
				60.0 + cControledTextField.getScene().getX() + cControledTextField.getScene().getWindow().getX(),
				20.0 + cControledTextField.getScene().getY() + cControledTextField.getScene().getWindow().getY());
	}
	
	private void tooltipHide() {
		cTooltipValidationInfo.hide();
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
