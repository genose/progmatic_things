package org.genose.java.implementation.net;

import org.genose.java.implementation.tools.GNSObjectSingletonStrings;

import java.util.HashMap;

public class GNSObjectServerStateMonitor implements GNSObjectSingletonStrings, GNSObjectsingletonUtils {
    /*
    Intended to monitor the server through HTTP or SSH and provide connection feedback to
    @Class GNSObjectSSHConnection , @Class

     */
    private HashMap<String, Object> opObjectsToFeedBack = null;
    private static GNSObjectsingletonUtils opsSingletonGNServerStateMonitor= null;
    /*
     * *****************************************************************************
     */
    /**
     * create a singleton
     */
    private void singletonInstanceCreate() {
        synchronized (GNSObjectServerStateMonitor.class) {
            if (opsSingletonGNServerStateMonitor != null) {
                throw new UnsupportedOperationException(
                        getClass() + ERRMESSAGE_SINGLETON_INSTANCAITE_TWICE );
            }

            opsSingletonGNServerStateMonitor = this;
        }
    }
    /**
     *
     * @return True if singleton is Valid ( not null )
     */
    public static synchronized boolean singletonInstanceExists(){
        synchronized (GNSObjectServerStateMonitor.class) {
            return (opsSingletonGNServerStateMonitor != null);

        }
    }

    /**
     *
     */
    private static synchronized void singletonInstanceCheck() {
        synchronized (GNSObjectServerStateMonitor.class) {
            if (opsSingletonGNServerStateMonitor == null) {
                throw new UnsupportedOperationException(ERRMESSAGE_SINGLETON_INVALIDNULL);
            }
        }
    }


}
