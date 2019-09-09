package org.genose.java.implementation.exceptionerror;

import org.genose.java.implementation.javafx.applicationtools.JFXApplicationHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class GNSObjectException extends Exception {

    private Throwable aEncapsuledThrowable = null;

    public GNSObjectException() {
        super();
    }

    /**
     * @param message
     */
    public GNSObjectException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public GNSObjectException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public GNSObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public GNSObjectException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     *
     * @param sMessageToRaise
     * @param throwedCause
     * @param stackTrace
     */
    public GNSObjectException(String sMessageToRaise, Throwable throwedCause, StackTraceElement[] stackTrace) {
        super(sMessageToRaise, throwedCause);
        super.setStackTrace(stackTrace);

    }


    public static String doFormattedStackTrace(Throwable eThrowableStackTrace) {
        if (eThrowableStackTrace == null) return ("[NULL]");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        eThrowableStackTrace.printStackTrace(pw);
        return sw.toString();
    }

    public static String doFormattedStackTrace(StackTraceElement[] eStackTrace) {
        // ::  ...
        if (eStackTrace == null) return ("[NULL]");
        return Arrays.asList(eStackTrace).toString().replaceAll(",", "\n").replaceAll("\\[", "").replaceAll("\\]", "");
    }

    public static String doFormattedStackTrace(List<StackTraceElement> aStackTraceList) {
        if (aStackTraceList == null) return ("[NULL]");
        return aStackTraceList.toString().replaceAll(",", "\n").replaceAll("\\[", "").replaceAll("\\]", "");
    }

    public static void raiseToFront(Class<?> jfxApplicationHelperClass, Exception aThroweableException, boolean b) {

    }

    /**
     *
     * @return Throwable
     */
    public Throwable getEncapsuledEventException() {

        return this.aEncapsuledThrowable;
    }

    /**
     *
     * @param aThrowableToForward
     */
    public void setEncapsuledEventException(Throwable aThrowableToForward) {

        this.aEncapsuledThrowable = aThrowableToForward;
    }

}
