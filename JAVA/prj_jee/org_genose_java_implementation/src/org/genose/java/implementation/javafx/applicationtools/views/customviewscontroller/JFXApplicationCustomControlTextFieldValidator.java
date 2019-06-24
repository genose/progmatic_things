/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools.views.customviewscontroller;

import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

import org.genose.java.implementation.javafx.applicationtools.JFXApplicationLogger;
import org.genose.java.implementation.tools.NumericRange;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;

/**
 * @author xenon
 *
 */
public class JFXApplicationCustomControlTextFieldValidator {

	/* ********************** */
	final static String EMPTY_FIELD_ERROR = "";
	final static String DEFAULT_LABEL_ERROR_PICTOGRAMME = "\u2107";
	// :: https://howtodoinjava.com/regex/java-regex-validate-email-address/
	final static String VALIDATION_REGEX_EMAIL = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

	/* ********************** */
	@FXML
	private TextField cControledTextField = null;
	// label under textfield to explain whats wrong
	private Label cLabelErrorExplainValidation = null;
	// label
	private Label cLabelValidationPictogramm = null;
	private Tooltip cTooltipTextField = null;

	/* ********************** */
	@FXML
	private Boolean bLabelVisible = false;;

	/* ********************** */
	// :: https://docs.oracle.com/javase/9/docs/api/java/util/regex/Pattern.html
	private java.util.regex.Pattern aRegexPatternFieldValidator = null;
	private NumericRange aRangeValidator = null;
	private Color aColorForTextFieldError = null;
	private Color aColorForTextFieldValidated = null;
	/* ********************** */

	@FXML
	public void initialize() {

		if ((cControledTextField == null) || (cLabelErrorExplainValidation == null)
				|| (cLabelValidationPictogramm == null) || (cTooltipTextField == null)) {
			throw new IllegalArgumentException(this.getClass() + " : Inconsistancy in design controller ... ");
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
	public Boolean getLabelVisible() {
		return bLabelVisible;
	}

	/**
	 * @param bLabelVisible the bLabelVisible to set
	 */
	public void setLabelVisible(Boolean labelVisible) {
		this.bLabelVisible = labelVisible;
	}

	/**
	 * @param aRegexPatternFieldValidator the aRegexPatternFieldValidator to set
	 */
	public void setaRegexPatternFieldValidator(java.util.regex.Pattern aRegexPatternFieldValidator) {
		this.aRegexPatternFieldValidator = aRegexPatternFieldValidator;
	}

	/**
	 * @param aRangeValidator the aRangeValidator to set
	 */
	public void setaRangeValidator(NumericRange aRangeValidator) {
		this.aRangeValidator = aRangeValidator;
	}

	/**
	 * @param aColorForTextFieldError the aColorForTextFieldError to set
	 */
	public void setaColorForTextFieldError(Color colorForTextFieldError) {
		this.aColorForTextFieldError = colorForTextFieldError;
	}

	/**
	 * @param aColorForTextFieldValidated the aColorForTextFieldValidated to set
	 */
	public void setaColorForTextFieldValidated(Color colorForTextFieldValidated) {
		this.aColorForTextFieldValidated = colorForTextFieldValidated;
	}

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
		if(!bRegexValidate || !bRangeLengthValidate) {
			if(aColorForTextFieldError != null) {
				BorderStroke aBorderStrokeStyle = new BorderStroke( aColorForTextFieldError, BorderStrokeStyle.SOLID, null, new BorderWidths(5));
				Border aBorderColoredForField = new Border(aBorderStrokeStyle);
				cControledTextField.setBorder(aBorderColoredForField);
			}
		}else {
			if(aColorForTextFieldValidated != null) {
				BorderStroke aBorderStrokeStyle = new BorderStroke( aColorForTextFieldValidated, BorderStrokeStyle.SOLID, null, new BorderWidths(5));
				Border aBorderColoredForField = new Border(aBorderStrokeStyle);
				cControledTextField.setBorder(aBorderColoredForField);
			}
		}
		/* ******************** */
		return bRegexValidate && bRangeLengthValidate;
	}

}
