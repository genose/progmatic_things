/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools.exceptionerror;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	public JFXApplicationRuntimeException() {
		super();
	}

	/**
	 * @param message
	 */
	public JFXApplicationRuntimeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public JFXApplicationRuntimeException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public JFXApplicationRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public JFXApplicationRuntimeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace); 
	}

}
