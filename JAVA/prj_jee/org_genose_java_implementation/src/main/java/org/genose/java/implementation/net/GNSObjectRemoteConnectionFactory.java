package org.genose.java.implementation.net;

import com.jcraft.jsch.JSch;
import com.pastdev.jsch.DefaultSessionFactory;
import org.genose.java.implementation.streams.GNSObjectMappedLogger;

import java.net.UnknownHostException;
import java.util.Objects;

public class GNSObjectRemoteConnectionFactory extends DefaultSessionFactory {

    /* ********************************************************************** */
    static final String ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER = "Invalid CONNECTION Parameter ";
    static final Integer SSH_DEFAULT_PORTNUMBER = 22;


    static GNSObjectSSHConnection aSSHConnection = null;

    /* ********************************************************************** */
    private static String sLocalhostName = null;
    /* ********************************************************************** */

    static {
        try {
            GNSObjectRemoteConnectionFactory.sLocalhostName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    /* ********************************************************************** */

    /* ********************************************************************** */
    private String sConnectionName = null;
    /* ********************************************************************** */
    private Integer iSSHConnectTimeOUT = 30;
    /* ********************************************************************** */
    private Integer iPortRemoteServiceLocalhost = 0; // localhost port public java.lang.Integer
    /* ********************************************************************** */
    private Integer iPortForwarded = 0; // resulted SSH Port forwardingpublic java.lang.Integer
    /* ********************************************************************** */
    private String sHostRemotedService = null;
    private Integer iPortRemotedService = null;

    /* ********************************************************************** */
    private String sUserRemotedService = null;
    private String sPasswordRemotedService = null;
    /* ********************************************************************** */
    private String sSSHPubliKeyFilePath = null;
    private String sRessourceRemotedService = null;
    private String sFilePathRemotedService = null;
    private String sPassword = null;
    private JSch aJSchProvider = null;
    /* ********************************************************************** */

    public GNSObjectRemoteConnectionFactory() {
        super(null, null, SSH_DEFAULT_PORTNUMBER);
        aJSchProvider = new JSch();
    }
    public GNSObjectRemoteConnectionFactory(String username, String hostname, Integer port) {
        super(username, hostname, port);
        aJSchProvider = new JSch();
    }

    /* ********************************************************************** */

    public static String getLocalhostName() {
        return sLocalhostName;
    }

    /* ********************************************************************** */
    public String getConnectionName() {
        return sConnectionName;
    }

    /* ********************************************************************** */
    public void setConnectionName(String sArgConnectionName) {
        this.sConnectionName = sArgConnectionName;
    }

    /* ********************************************************************** */
    public Integer getSSHConnectTimeOUT() {
        return iSSHConnectTimeOUT;
    }

    /* ********************************************************************** */
    @Override
    public String getHostname() {
        return super.getHostname();
    }

    /* ********************************************************************** */
    @Override
    public void setHostname(String sArgSSHHost) {
        super.setHostname(Objects.requireNonNullElse(sArgSSHHost, sLocalhostName));
    }

    /* ********************************************************************** */
    @Override
    public int getPort() {
        return super.getPort();
    }

    /* ********************************************************************** */
    public void setPort(Integer iArgSSHPort) {

        super.setPort( Objects.requireNonNullElse(iArgSSHPort, Integer.valueOf(SSH_DEFAULT_PORTNUMBER)));

    }

    /* ********************************************************************** */
    public int getPortRemotedServiceLocalhost() {
        return iPortRemoteServiceLocalhost;
    }

    /* ********************************************************************** */
    public void setPortRemotedServiceLocalhost(Integer iArgPortLocalhost) {
        this.iPortRemoteServiceLocalhost = iArgPortLocalhost;
    }
    /* ********************************************************************** */

    public Integer getPortForwarded() {
        return iPortForwarded;
    }
    /* ********************************************************************** */

    public void setPortForwarded(java.lang.Integer iSSHPortForwarded) {
        this.iPortForwarded = iSSHPortForwarded;
    }
    /* ********************************************************************** */

    public java.lang.String getHostRemotedService() {
        return sHostRemotedService;
    }
    /* ********************************************************************** */

    public void setHostRemotedService(java.lang.String sArgHostRemotedService) {
        this.sHostRemotedService = sArgHostRemotedService;
    }
    /* ********************************************************************** */

    public java.lang.Integer getPortRemotedService() {
        return iPortRemotedService;
    }
    /* ********************************************************************** */

    public void setPortRemotedService(java.lang.Integer iArgPortRemotedService) {
        this.iPortRemotedService = iArgPortRemotedService;
    }
    /* ********************************************************************** */
    @Override
    public String getUsername() {
        return super.getUsername();
    }
    /* ********************************************************************** */

    public void setsSSHUser(java.lang.String sArgSSHUser) {
        super.setUsername(Objects.requireNonNullElse(sArgSSHUser, String.valueOf("")));
    }

    public String getPassword() {
        return sPassword;
    }
    /* ********************************************************************** */

    public void setPassword(String sArgSSHPassword) {
        sPassword =Objects.requireNonNullElse(sArgSSHPassword, String.valueOf(""));
        super.setPassword(sPassword);
    }
    /* ********************************************************************** */

    public java.lang.String getSSHPubliKeyFilePath() {
        return sSSHPubliKeyFilePath;
    }
    /* ********************************************************************** */

    public void setSSHPubliKeyFilePath(java.lang.String sArgSSHPubliKeyFilePath) {
        this.sSSHPubliKeyFilePath = Objects.requireNonNullElse(sArgSSHPubliKeyFilePath, String.valueOf(""));
    }

    /**
     *
     * @param sArgRemoteConnectionName
     * @return null, if error or no connection
     */
    /* ********************************************************************** */
    public GNSObjectSSHConnection getSSHConnection(String sArgRemoteConnectionName) {
        try {
            setConnectionName(sArgRemoteConnectionName);
            aSSHConnection = Objects.requireNonNullElse(aSSHConnection,
                    (new GNSObjectSSHConnection(this)));
            return aSSHConnection;
        }catch(Exception evERRCONNECTION){
            GNSObjectMappedLogger.getLogger().logError(this.getClass(), evERRCONNECTION);
        }
        return null;
    }
    /* ********************************************************************** */
    public void setConnectionRemote(GNSObjectSSHConnection aArgConnection) {
        aSSHConnection = aArgConnection;
    }
    /* ********************************************************************** */

    public void setFilePathRemotedService(String sArgFilePathRemotedService) {
        this.sFilePathRemotedService = sArgFilePathRemotedService;
    }
    /* ********************************************************************** */


    public String getFilePathRemotedService() {
        return sFilePathRemotedService;
    }
    /* ********************************************************************** */

    public String getUserRemotedService() {
        return sUserRemotedService;
    }
    /* ********************************************************************** */

    public void setUserRemotedService(String sArgUserRemotedService) {
        this.sUserRemotedService = sUserRemotedService;
    }
    /* ********************************************************************** */

    public String getPasswordRemotedService() {
        return sPasswordRemotedService;
    }
    /* ********************************************************************** */

    public void setPasswordRemotedService(String sArgPasswordRemotedService) {
        this.sPasswordRemotedService = sPasswordRemotedService;
    }

    public String getRessourceRemotedService() {
        return sRessourceRemotedService;
    }

    public JSch getConnectionProvider() {
        return aJSchProvider;
    }
}