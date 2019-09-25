package org.genose.java.implementation.net.secure;

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
    // https://mediatemple.net/community/products/dv/204403884/tunnel-local-mysql-server-through-ssh
    // https://stackoverflow.com/questions/1968293/connect-to-remote-mysql-database-through-ssh-using-java

    /**
     * @param sConnectionName
     * @param sArgSSHHost
     * @param iArgSSHPort
     * @param sArgSSHHostForwardedService
     * @param iArgSSHPortForwardedService
     * @param sArgSSHUser
     * @param sArgSSHPassword
     * @param sArgRemoteServiceUSER
     * @param sArgRemoteServicePassword
     * @param sArgSSHPubliKeyFilePath
     * @throws Exception
     */
    public GNSObjectSSHJDBCConnection(String sConnectionName, String sArgSSHHost, Integer iArgSSHPort, String sArgSSHHostForwardedService, Integer iArgSSHPortForwardedService, String sArgSSHUser, String sArgSSHPassword, String sArgRemoteServiceUSER, String sArgRemoteServicePassword, String sArgSSHPubliKeyFilePath) throws Exception {
        super(sConnectionName, sArgSSHHost, iArgSSHPort, sArgSSHHostForwardedService, iArgSSHPortForwardedService, sArgSSHUser, sArgSSHPassword, sArgRemoteServiceUSER, sArgRemoteServicePassword, sArgSSHPubliKeyFilePath);


    }

    /**
     * @param bUseTunnledForwardedConnection
     * @return
     */
    private Connection open(Boolean bUseTunnledForwardedConnection) {

        try {

            super.close();
            if ((aJDBCConnection != null) &&
                    !aJDBCConnection.isClosed()) {
                aJDBCConnection.close();
            }

            if (!super.openSession()) {
                return null;
            }
            //addSSHPortForwardind(sJDBCConnectionName, 0, getSSHHost(), iArgSSHPortForwardedService);


            Properties properties = new Properties();
            properties.setProperty("user", getConnectionFactory().getUserRemotedService());

            if ((getConnectionFactory().getPasswordRemotedService() != null) && (!getConnectionFactory().getPasswordRemotedService().isEmpty())) {
                properties.setProperty("password", getConnectionFactory().getPasswordRemotedService());
            }

            properties.setProperty("useUnicode", "true");
            properties.setProperty("characterEncoding", "UTF-8");
            // Class.forName("com.mysql.jdbc.Driver").newInstance();
            if (bUseTunnledForwardedConnection && (getConnectionFactory().getPortForwardedRemotedService() == 0)) {
                if (!addSSHTunnel(false)) {
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
}
