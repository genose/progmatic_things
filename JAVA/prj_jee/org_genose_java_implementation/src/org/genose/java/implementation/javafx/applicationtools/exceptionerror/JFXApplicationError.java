/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools.exceptionerror;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public JFXApplicationError() {

	}

	/**
	 * @param message
	 */
	public JFXApplicationError(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public JFXApplicationError(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public JFXApplicationError(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public JFXApplicationError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
