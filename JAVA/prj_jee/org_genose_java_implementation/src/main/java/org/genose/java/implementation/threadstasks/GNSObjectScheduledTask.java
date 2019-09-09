package org.genose.java.implementation.threadstasks;

import org.genose.java.implementation.applicationtools.GNSObjectClassHelper;
import org.genose.java.implementation.applicationtools.GNSObjectFunctionCallback;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationClassHelper;
import org.genose.java.implementation.streams.GNSObjectMappedLogger;

public class GNSObjectScheduledTask extends java.util.TimerTask {
    protected Object pUserDatasObjectRef = null;
    protected Object oObjectArg0 = null;
    protected java.util.Timer aTimerCallBack = null;
    protected Integer iTimerDelayAfterScheduleLaunchCalled = 0;
    protected Integer iTimerRepeatDelay = 0;
    protected GNSObjectFunctionCallback aFuncCallback = null;

    private Thread aThread = null;

    /**
     *
     */
    public GNSObjectScheduledTask() {
        aTimerCallBack = new java.util.Timer();

    }

    /**
     *
     */
    public GNSObjectScheduledTask(int iDelayAfter) {
        aTimerCallBack = new java.util.Timer();
        iTimerDelayAfterScheduleLaunchCalled = ((iDelayAfter > 0) ? iDelayAfter : 0);

    }

    /**
     *
     */
    public GNSObjectScheduledTask(int iDelayAfter, int iDelayRepeat) {
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
        aThread = new Thread() {

            @Override
            public void run() {
                super.run();
                try {

                    String sCallbackDescription = "[NULL DESCRIPTION]";
                    try {
                        if (GNSObjectClassHelper.respondsTo(aFuncCallback, GNSObjectFunctionCallback.CALLBACK_DESCRIPTION)) {
                            Object aDescription = GNSObjectClassHelper.invokeMethod(aFuncCallback,
                                    GNSObjectFunctionCallback.CALLBACK_DESCRIPTION);

                            sCallbackDescription = String.format("[%s]", (String) aDescription);
                        }
                    } catch (Exception evERRDESCRIPTION) {
                        ;
                        ;
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
            }
        };

        aThread.run();

    }

    /**
     *
     */
    public void schedule() {
        if (this.aFuncCallback == null) {
            GNSObjectMappedLogger.getLogger().logError(this.getClass(),
                    "Cant schedule ... Runnable CallBack is null ... ");
            return;
        }
        this.aTimerCallBack.purge();
        setupDelayFromCallback();

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
     * @param iDelayAfterMilli
     */
    public void setDelayBeforeSchedule(int iDelayAfterMilli) {
        this.iTimerDelayAfterScheduleLaunchCalled = ((iDelayAfterMilli < 0) ? 0 : iDelayAfterMilli);
    }

    /**
     * Use MilliSeconds to schedule
     *
     * @param iDelayAfterMilli
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
    public GNSObjectFunctionCallback getCallback() {
        return aFuncCallback;
    }

    /**
     * @param aFuncCallback the aFuncCallback to set
     */
    public void setCallback(GNSObjectFunctionCallback aFuncCallback) {
        this.aFuncCallback = aFuncCallback;
    }

    /**
     * @param aFuncCallbackT
     * @param oObjectArg0T
     */
    public void setCallback(GNSObjectFunctionCallback aFuncCallbackT, Object oObjectArg0T) {
        this.aFuncCallback = aFuncCallbackT;
        this.oObjectArg0 = oObjectArg0T;

    }

    /**
     * @param oObjectArg0T
     */
    public void setCallBackArgument(Object oObjectArg0T) {
        this.oObjectArg0 = oObjectArg0T;
    }

    public void setupDelayFromCallback() {
        try {

            if (this.aFuncCallback != null) {

                setDelayBeforeSchedule(this.aFuncCallback.getCallbackDelayAfter());
                setDelayRepeatSchedule(this.aFuncCallback.getCallbackRepeatDelayAfter());

                if (GNSObjectClassHelper.respondsTo(this.aFuncCallback,
                        GNSObjectFunctionCallback.CALLBACK_DELAYAFTER_SETUP)) {
                    int iDelay = (Integer) GNSObjectClassHelper.invokeMethod(this.aFuncCallback,
                            GNSObjectFunctionCallback.CALLBACK_DELAYAFTER_SETUP);

                    setDelayBeforeSchedule(iDelay);
                }
                if (GNSObjectClassHelper.respondsTo(this.aFuncCallback,
                        GNSObjectFunctionCallback.CALLBACK_DELAYREPEATAFTER_SETUP)) {
                    int iDelay = (Integer) GNSObjectClassHelper.invokeMethod(this.aFuncCallback,
                            GNSObjectFunctionCallback.CALLBACK_DELAYREPEATAFTER_SETUP);
                    setDelayRepeatSchedule(iDelay);
                }
            }

        } catch (Exception evERRSETUPFROMCALLBACK) {
            GNSObjectMappedLogger.getLogger().logError(this.getClass(), evERRSETUPFROMCALLBACK);
        }

    }
}