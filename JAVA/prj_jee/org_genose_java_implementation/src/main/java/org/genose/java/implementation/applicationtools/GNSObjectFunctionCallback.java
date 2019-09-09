/**
 * 
 */
package org.genose.java.implementation.applicationtools;


import org.genose.java.implementation.javafx.applicationtools.JFXApplicationFunctionCallback;
import org.genose.java.implementation.javafx.applicationtools.threadstasks.JFXApplicationScheduledTask;
import org.genose.java.implementation.threadstasks.GNSObjectScheduledTask;

import java.util.function.Function;

/**
 * @author 59013-36-18
 *
 */
public class GNSObjectFunctionCallback implements Function<Object, Object> {

	private int iCallbackDelayAfter;
	private int iCallbackRepeatDelayAfter;
	private String sCallbackDescription;
	private Object aEventTarget;
	public static final String CALLBACK_DELAYAFTER_SETUP = "getCallbackDelayAfter";
	public static final String CALLBACK_DELAYREPEATAFTER_SETUP = "getCallbackRepeatDelayAfter";
	public static final String CALLBACK_DESCRIPTION = "getDescription";

	/**
	 * 
	 */
	public GNSObjectFunctionCallback() {
		super();
		this.setCallbackDelayAfter(0);
		this.setCallbackRepeatDelayAfter(0);

	}

	/**
	 * Use MilliSeconds to schedule
	 * 
	 * @param iCallbackDelayAfterMilli
	 */
	public GNSObjectFunctionCallback(int iCallbackDelayAfterMilli) {
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
	public GNSObjectFunctionCallback(int iCallbackDelayAfterMilli, int iCallbackDelayRepeatMilli) {
		super();
		this.setCallbackDelayAfter(((iCallbackDelayAfterMilli > 0) ? iCallbackDelayAfterMilli : (0)));
		this.setCallbackRepeatDelayAfter(((iCallbackDelayRepeatMilli > 0) ? iCallbackDelayRepeatMilli : (0)));
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
		this.sCallbackDescription = String.valueOf(sDescription);
	}
	@Override
	public <V> Function<Object, V> andThen(Function<? super Object, ? extends V> after) {
		System.out.println("Apply  function andthen");
		return (Function<Object, V>) apply(after);
	}
	@Override
	public Object apply(Object t) {
		/*JFXApplicationScheduledTask aTimerTaskCallback = new JFXApplicationScheduledTask();

				aTimerTaskCallback.setCallback(aFuncCallback, ((bReturnOnlyDesignNode) ? aRootNode : aSceneNode));
				aTimerTaskCallback.setUserDatas(null);

				aTimerTaskCallback.schedule();*/
		return this;
	}

	public String getDescription() {
		return ((this.sCallbackDescription == null) ? this.toString() : this.sCallbackDescription);
	}

	public void setCallbackObject(Object eventTarget) {
		aEventTarget = eventTarget;
	}

	/**
	 *
	 * @param arg0
	 * @return
	 */
	public Object applyDelayed(Object arg0) {
		aEventTarget = ((aEventTarget == null)?arg0: aEventTarget);
		schedule();
		return null;
	}

	/**
	 *
	 */
	public void schedule() {

		JFXApplicationScheduledTask aTimerTaskCallback = new JFXApplicationScheduledTask();

				aTimerTaskCallback.setCallback(this, aEventTarget);
				aTimerTaskCallback.setUserDatas(null);

				aTimerTaskCallback.schedule();
	}
}
