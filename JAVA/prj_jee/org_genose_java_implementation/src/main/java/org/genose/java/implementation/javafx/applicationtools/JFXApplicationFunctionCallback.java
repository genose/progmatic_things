package org.genose.java.implementation.javafx.applicationtools;

import org.genose.java.implementation.applicationtools.GNSObjectFunctionCallback;

public class JFXApplicationFunctionCallback extends GNSObjectFunctionCallback {
    public JFXApplicationFunctionCallback() {
        super();
           }

    /**
     * Use MilliSeconds to schedule
     *
     * @param iCallbackDelayAfterMilli
     */
    public JFXApplicationFunctionCallback(int iCallbackDelayAfterMilli) {
        super(iCallbackDelayAfterMilli);
    }

    /**
     * Use MilliSeconds to schedule
     *
     * @param iCallbackDelayAfterMilli
     * @param iCallbackDelayRepeatMilli
     */
    public JFXApplicationFunctionCallback(int iCallbackDelayAfterMilli, int iCallbackDelayRepeatMilli) {
        super(iCallbackDelayAfterMilli, iCallbackDelayRepeatMilli);
    }
}
