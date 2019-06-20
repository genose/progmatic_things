/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools.threadstasks;

import org.genose.java.implementation.javafx.applicationtools.JFXApplicationCallback;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationClassHelper;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationLogger;

import javafx.application.Platform;

/**
 * @author xenon
 *
 */

public class JFXApplicationScheduledTask extends java.util.TimerTask {
	private Object pUserDatasObjectRef = null;
	private Object oObjectArg0 = null;
	private java.util.Timer aTimerCallBack = null;
	private Integer iTimerDelayAfterScheduleLaunchCalled = 0;
	private Integer iTimerRepeatDelay = 0;
	private JFXApplicationCallback aFuncCallback = null;

	/**
	 * 
	 */
	public JFXApplicationScheduledTask() {
		aTimerCallBack = new java.util.Timer();

	}

	/**
	 * 
	 */
	public JFXApplicationScheduledTask(int iDelayAfter) {
		aTimerCallBack = new java.util.Timer();
		iTimerDelayAfterScheduleLaunchCalled = ((iDelayAfter > 0) ? iDelayAfter : 0);

	}

	/**
	 * 
	 */
	public JFXApplicationScheduledTask(int iDelayAfter, int iDelayRepeat) {
		aTimerCallBack = new java.util.Timer();
		this.iTimerDelayAfterScheduleLaunchCalled = ((iDelayAfter > 0) ? iDelayAfter : 0);
		this.iTimerRepeatDelay = ((iDelayRepeat > 0) ? iDelayRepeat : 0);

	}

	/**
	 * 
	 */
	@Override
	public void run() {

		if (this.aFuncCallback == null) {
			JFXApplicationLogger.getLogger().logError(this.getClass(), "Can run ... Runnable CallBack is null ... ");
			return;
		}
		// https://stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
		Platform.runLater(() -> {
			Object oCallBackResult = aFuncCallback.apply(oObjectArg0);
			String sCallbackDescription = "[NULL DESCRIPTION]";
			if (JFXApplicationClassHelper.respondsTo(aFuncCallback, JFXApplicationCallback.CALLBACK_DESCRIPTION)) {
				sCallbackDescription = String.format("[%s]", (String) JFXApplicationClassHelper
						.invokeMethod(aFuncCallback, JFXApplicationCallback.CALLBACK_DESCRIPTION));
			}

			JFXApplicationLogger.getLogger().logInfo(getClass(), String.format("Callback (%s) returned %s",
					sCallbackDescription, ((oCallBackResult == null) ? "[Null]" : oCallBackResult)));
		});

	}

	/**
	 * 
	 */
	public void schedule() {
		if (this.aFuncCallback == null) {
			JFXApplicationLogger.getLogger().logError(this.getClass(),
					"Cant schedule ... Runnable CallBack is null ... ");
			return;
		}
		this.aTimerCallBack.purge();
		if (iTimerRepeatDelay > 50) {
			this.aTimerCallBack.schedule(this, iTimerDelayAfterScheduleLaunchCalled, iTimerRepeatDelay);

		} else {
			this.aTimerCallBack.schedule(this, iTimerDelayAfterScheduleLaunchCalled);
		}
	}

	/**
	 * 
	 */
	@Override
	public boolean cancel() {
		this.aTimerCallBack.cancel();
		Boolean bCancelled = super.cancel();
		this.aTimerCallBack.purge();
		return bCancelled;
	}

	/**
	 * Use MilliSeconds to schedule
	 * 
	 * @param iDelay
	 */
	public void setDelayBeforeSchedule(int iDelayAfterMilli) {
		this.iTimerDelayAfterScheduleLaunchCalled = ((iDelayAfterMilli < 0) ? 0 : iDelayAfterMilli);
	}

	/**
	 * Use MilliSeconds to schedule
	 * 
	 * @param iDelay
	 */
	public void setDelayRepeatSchedule(int iDelayAfterMilli) {
		this.iTimerRepeatDelay = ((iDelayAfterMilli < 0) ? 0 : iDelayAfterMilli);
	}

	/**
	 * 
	 */
	public int getDelayRepeatSchedule() {
		return this.iTimerRepeatDelay;
	}

	/**
	 * @return the pObjectRef
	 */
	public synchronized final Object getUserDatas() {
		return this.pUserDatasObjectRef;
	}

	/**
	 * @param pObjectRef the pObjectRef to set
	 */
	public synchronized final void setUserDatas(Object pObjectRef) {
		this.pUserDatasObjectRef = pObjectRef;
	}

	/**
	 * @return the aFuncCallback
	 */
	public JFXApplicationCallback getCallback() {
		return aFuncCallback;
	}

	/**
	 * @param aFuncCallback the aFuncCallback to set
	 */
	public void setCallback(JFXApplicationCallback aFuncCallback) {
		this.aFuncCallback = aFuncCallback;
	}

	/**
	 * 
	 * @param aFuncCallback
	 * @param oObjectArg0
	 */
	public void setCallback(JFXApplicationCallback aFuncCallbackT, Object oObjectArg0T) {
		this.aFuncCallback = aFuncCallbackT;
		this.oObjectArg0 = oObjectArg0T;

	}

	/**
	 * 
	 * @param oObjectArg0
	 */
	public void setCallBackArgument(Object oObjectArg0T) {
		this.oObjectArg0 = oObjectArg0T;
	}

	public void setupDelayFromCallback() {
		try {

			if (this.aFuncCallback != null) {
				
				setDelayBeforeSchedule( this.aFuncCallback.getCallbackDelayAfter());
				setDelayRepeatSchedule( this.aFuncCallback.getCallbackRepeatDelayAfter());
				
				if (JFXApplicationClassHelper.respondsTo(this.aFuncCallback,
						JFXApplicationCallback.CALLBACK_DELAYAFTER_SETUP)) {
					int iDelay = (Integer) JFXApplicationClassHelper.invokeMethod(this.aFuncCallback,
							JFXApplicationCallback.CALLBACK_DELAYAFTER_SETUP);

					setDelayBeforeSchedule(iDelay);
				}
				if (JFXApplicationClassHelper.respondsTo(this.aFuncCallback,
						JFXApplicationCallback.CALLBACK_DELAYREPEATAFTER_SETUP)) {
					int iDelay = (Integer) JFXApplicationClassHelper.invokeMethod(this.aFuncCallback,
							JFXApplicationCallback.CALLBACK_DELAYREPEATAFTER_SETUP); 
					setDelayRepeatSchedule(iDelay);
				}
			}

		} catch (Exception evERRSETUPFROMCALLBACK) {
			JFXApplicationLogger.getLogger().logError(this.getClass(), evERRSETUPFROMCALLBACK);
		}

	}
}
