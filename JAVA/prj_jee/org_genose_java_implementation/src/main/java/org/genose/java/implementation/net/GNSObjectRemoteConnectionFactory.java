package org.genose.java.implementation.net;

import com.pastdev.jsch.DefaultSessionFactory;

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
    private String sSSHHost = null;
    /* ********************************************************************** */
    private Integer iSSHPort = 0;
    /* ********************************************************************** */
    private Integer iPortLocalhost = 0; // localhost portpublic java.lang.Integer
    /* ********************************************************************** */
    private Integer iPortForwarded = 0; // resulted SSH Port forwardingpublic java.lang.Integer
    /* ********************************************************************** */
    private java.lang.String sHostRemotedService = null;
    private java.lang.Integer iPortRemotedService = null;
    private java.lang.String sFilePathRemotedService = null;
    /* ********************************************************************** */
    private java.lang.String sSSHUser = null;
    private java.lang.String sSSHPassword = null;
    /* ********************************************************************** */
    private java.lang.String sUserRemotedService = null;
    private java.lang.String sPasswordRemotedService = null;
    /* ********************************************************************** */
    private java.lang.String sSSHPubliKeyFilePath = null;
    private String sRessourceRemotedService = null;
    /* ********************************************************************** */

    public GNSObjectRemoteConnectionFactory() {
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
    public String getSSHHost() {
        return sSSHHost;
    }

    /* ********************************************************************** */
    public void setSSHHost(String sArgSSHHost) {

        this.sSSHHost = Objects.requireNonNullElse(sArgSSHHost, sLocalhostName);
    }

    /* ********************************************************************** */
    public Integer getSSHPort() {
        return iSSHPort;
    }

    /* ********************************************************************** */
    public void setSSHPort(Integer iArgSSHPort) {

        this.iSSHPort = Objects.requireNonNullElse(iArgSSHPort, Integer.valueOf(SSH_DEFAULT_PORTNUMBER));

    }

    /* ********************************************************************** */
    public int getSSHPortLocalhost() {
        return iPortLocalhost;
    }

    /* ********************************************************************** */
    public void setiSSHPortLocalhost(Integer iArgPortLocalhost) {
        this.iPortLocalhost = iArgPortLocalhost;
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

    public void setiSSHPortRemotedService(java.lang.Integer iArgPortRemotedService) {
        this.iPortRemotedService = iArgPortRemotedService;
    }
    /* ********************************************************************** */

    public java.lang.String getSSHUser() {
        return sSSHUser;
    }
    /* ********************************************************************** */

    public void setsSSHUser(java.lang.String sArgSSHUser) {
        this.sSSHUser = Objects.requireNonNullElse(sArgSSHUser, String.valueOf(""));
    }

    public java.lang.String getSSHPassword() {
        return sSSHPassword;
    }
    /* ********************************************************************** */

    public void setsSSHPassword(java.lang.String sArgSSHPassword) {
        this.sSSHPassword = Objects.requireNonNullElse(sArgSSHPassword, String.valueOf(""));
    }
    /* ********************************************************************** */

    public java.lang.String getSSHPubliKeyFilePath() {
        return sSSHPubliKeyFilePath;
    }
    /* ********************************************************************** */

    public void setSSHPubliKeyFilePath(java.lang.String sArgSSHPubliKeyFilePath) {
        this.sSSHPubliKeyFilePath = Objects.requireNonNullElse(sArgSSHPubliKeyFilePath, String.valueOf(""));
    }

    /* ********************************************************************** */
    public GNSObjectSSHConnection getSSHConnection(String sArgRemoteConnectionName) {

        aSSHConnection = Objects.requireNonNullElse(aSSHConnection, (new GNSObjectSSHConnection(this)));
        return aSSHConnection;
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
}