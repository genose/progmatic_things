/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools.threadstasks;


import javafx.application.Platform;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationClassHelper;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationFunctionCallback;
import org.genose.java.implementation.streams.GNSObjectMappedLogger;
import org.genose.java.implementation.threadstasks.GNSObjectScheduledTask;

/**
 * @author xenon
 *
 */

public class JFXApplicationScheduledTask extends GNSObjectScheduledTask {



	/**
	 * 
	 */
	public JFXApplicationScheduledTask() {
		super();
	}

	/**
	 * 
	 */
	public JFXApplicationScheduledTask(int iDelayAfter) {
		super(iDelayAfter);

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
			GNSObjectMappedLogger.getLogger().logError(this.getClass(), "Cant run ... Runnable CallBack is null ... ");
			return;
		}
		// https://stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
		Platform.runLater(() -> {

			try {

				String sCallbackDescription = "[NULL DESCRIPTION]";
				try{
					if (JFXApplicationClassHelper.respondsTo(aFuncCallback, JFXApplicationFunctionCallback.CALLBACK_DESCRIPTION)) {
						Object aDescription = JFXApplicationClassHelper.invokeMethod(aFuncCallback,
								JFXApplicationFunctionCallback.CALLBACK_DESCRIPTION);

						sCallbackDescription = String.format("[%s]", (String) aDescription);
					}
				}catch (Exception evERRDESCRIPTION){
					;;
				}

				Object oCallBackResult = aFuncCallback.apply(oObjectArg0);

				GNSObjectMappedLogger.getLogger().logInfo(getClass(), String.format("Callback (%s) returned %s",
						sCallbackDescription, ((oCallBackResult == null) ? "[Null]" : oCallBackResult)));
			} catch (Exception evERRPlanifiedRunnable) {

				String sMessageFailInvokeable = "Something went wrong when running callback   ...";
				GNSObjectMappedLogger.getLogger().logError(this.getClass(), evERRPlanifiedRunnable,
						sMessageFailInvokeable);
				/*JFXApplicationException.raiseToFront(this.getClass(), new JFXApplicationException(
						sMessageFailInvokeable, evERRPlanifiedRunnable, JFXApplicationHelper.getStackTrace()), true); */

			}
		});

	}

}
