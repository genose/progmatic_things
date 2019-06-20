/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.util.function.Function;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationCallback implements Function<Object, Object> {

	private int iCallbackDelayAfter;
	private int iCallbackRepeatDelayAfter;
	private String sCallbackDescription;
	public static final String CALLBACK_DELAYAFTER_SETUP = "getCallbackDelayAfter";
	public static final String CALLBACK_DELAYREPEATAFTER_SETUP = "getCallbackRepeatDelayAfter";
	public static final String CALLBACK_DESCRIPTION = "getDescription";

	/**
	 * 
	 */
	public JFXApplicationCallback() {
		super();
		this.setCallbackDelayAfter(0);
		this.setCallbackRepeatDelayAfter(0);

	}

	/**
	 * Use MilliSeconds to schedule
	 * 
	 * @param iCallbackDelayAfterMilli
	 */
	public JFXApplicationCallback(int iCallbackDelayAfterMilli) {
		super();
		this.setCallbackDelayAfter(((iCallbackDelayAfterMilli > 0) ? iCallbackDelayAfterMilli : (0)));
		this.setCallbackRepeatDelayAfter(0);
	}

	/**
	 * Use MilliSeconds to schedule
	 * 
	 * @param iCallbackDelayAfterMilli
	 * @param iCallbackDelayRepeatMilli
	 */
	public JFXApplicationCallback(int iCallbackDelayAfterMilli, int iCallbackDelayRepeatMilli) {
		super();
		this.setCallbackDelayAfter(((iCallbackDelayAfterMilli > 0) ? iCallbackDelayAfterMilli : (0)));
		this.setCallbackRepeatDelayAfter(((iCallbackDelayRepeatMilli > 0) ? iCallbackDelayRepeatMilli : (0)));
	}

	public Object applyDelayed(Object arg0) {
		return null;
	}

	/**
	 * @return the iCallbackDelayAfter
	 */
	public int getCallbackDelayAfter() {
		return iCallbackDelayAfter;
	}

	/**
	 * @param iCallbackDelayAfter the iCallbackDelayAfter to set
	 */
	public void setCallbackDelayAfter(int iCallbackDelayAfter) {
		this.iCallbackDelayAfter = iCallbackDelayAfter;
	}

	/**
	 * @return the iCallbackRepeatDelayAfter
	 */
	public int getCallbackRepeatDelayAfter() {
		return iCallbackRepeatDelayAfter;
	}

	/**
	 * Use MilliSeconds to schedule
	 * 
	 * @param iCallbackRepeatDelayAfter the iCallbackRepeatDelayAfter to set
	 */
	public void setCallbackRepeatDelayAfter(int iCallbackRepeatDelayAfter) {
		this.iCallbackRepeatDelayAfter = iCallbackRepeatDelayAfter;
	}

	/**
	 * Use MilliSeconds to schedule
	 * 
	 * @param sDescription
	 */
	public void setDescription(String sDescription) {
		this.sCallbackDescription = sDescription;
	}
	@Override
	public <V> Function<Object, V> andThen(Function<? super Object, ? extends V> after) {
		System.out.println("Apply  function andthen");
		return (Function<Object, V>) apply(after);
	}
	@Override
	public Object apply(Object t) {
		// TODO Auto-generated method stub
		return null;
	}
	public String getDeScription() {
		return ((this.sCallbackDescription == null) ? this.toString() : this.sCallbackDescription);
	}

}
