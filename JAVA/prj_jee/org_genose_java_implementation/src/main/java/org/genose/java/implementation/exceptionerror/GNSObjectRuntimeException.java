/**
 * 
 */
package org.genose.java.implementation.exceptionerror;

/**
 * @author 59013-36-18
 *
 */
public class GNSObjectRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public GNSObjectRuntimeException() {
		super();
	}

	/**
	 * @param message
	 */
	public GNSObjectRuntimeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public GNSObjectRuntimeException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public GNSObjectRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 *
	 * @param from
	 * @param message
	 */
	public GNSObjectRuntimeException(Class from, String message) {
		super(message, new Throwable(message));
	}
	/**
	 *
	 * @param from
	 * @param message
	 * @param cause
	 */
	public GNSObjectRuntimeException(Class from, String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public GNSObjectRuntimeException(String message, Throwable cause, boolean enableSuppression,
									 boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace); 
	}

}
