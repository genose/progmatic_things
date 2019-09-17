package org.genose.java.implementation.net;

import com.jcraft.jsch.Session;
import org.genose.java.implementation.exceptionerror.GNSObjectException;
import org.genose.java.implementation.streams.GNSObjectMappedLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class GNSObjectSSHJDBCConnection extends GNSObjectSSHConnection {

    public final String CONNECTION_NAME = "SGBDCONNECTION";
    private String sJDBCConnectionName = "";
    private String sJDBCConnectionProtocolName = "mysql";
    private Connection aJDBCConnection = null;

    // https://stackoverflow.com/questions/1968293/connect-to-remote-mysql-database-through-ssh-using-java
    public GNSObjectSSHJDBCConnection(String sConnectionName, String sArgSSHHost, Integer iArgSSHPort, String sArgSSHHostForwardedService, Integer iArgSSHPortForwardedService, String sArgSSHUser, String sArgSSHPassword, String sArgRemoteServiceUSER, String sArgRemoteServicePassword, String sArgSSHPubliKeyFilePath) throws Exception {
        super(sConnectionName, sArgSSHHost, iArgSSHPort, sArgSSHHostForwardedService, iArgSSHPortForwardedService, sArgSSHUser, sArgSSHPassword, sArgRemoteServiceUSER, sArgRemoteServicePassword, sArgSSHPubliKeyFilePath);


    }


    private Connection open(Boolean bTunnledForwardConnection) {

        try {

            super.close();
            if ((aJDBCConnection != null) &&
                    !aJDBCConnection.isClosed()) {
                aJDBCConnection.close();
            }

            if (super.openSession() == null) {
                return null;
            }
            //addSSHPortForwardind(sJDBCConnectionName, 0, getSSHHost(), iArgSSHPortForwardedService);


            Properties properties = new Properties();
            properties.setProperty("user", getConnectionFactory().getUserRemotedService());
            properties.setProperty("password", getConnectionFactory().getPasswordRemotedService());
            properties.setProperty("useUnicode", "true");
            properties.setProperty("characterEncoding", "UTF-8");
            // Class.forName("com.mysql.jdbc.Driver").newInstance();
            if (bTunnledForwardConnection && (getConnectionFactory().getPortForwarded() == 0)) {
                if( ! addSSHTunnel()){
                    throw new GNSObjectException("Error or Timeout in obtain SSHTunnel Forwarding Connection ...");
                }
            }

            aJDBCConnection = DriverManager.getConnection("jdbc:" + sJDBCConnectionProtocolName + "://" + getConnectionFactory().getHostRemotedService() + ":" + getConnectionFactory().getPortRemotedService() + "/" + getConnectionFactory().getRessourceRemotedService() + "?autoReconnect=true", properties);
            if ((aJDBCConnection != null) && (aJDBCConnection.isValid(30))) {
                return aJDBCConnection;
            } else {
                if (aJDBCConnection != null) {
                    aJDBCConnection.close();
                }
                throw new GNSObjectException("Error or Timeout in obtain valid Connection ...");
            }

        } catch (Exception evERR_CONNECTION) {
            GNSObjectMappedLogger.getLogger().logError(this.getClass(), evERR_CONNECTION);
        }
        return null;
    }

    private void addSSHPortForwardind() {
    }
}
