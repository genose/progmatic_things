/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

}
