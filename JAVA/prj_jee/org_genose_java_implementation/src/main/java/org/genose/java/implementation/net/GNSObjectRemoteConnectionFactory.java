package org.genose.java.implementation.net;

import java.net.UnknownHostException;
import java.util.Objects;

public class GNSObjectRemoteConnectionFactory {

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

    public static String getLocalhostName() {
        return sLocalhostName;
    }

    /* ********************************************************************** */
    private String sConnectionName = null;

    /* ********************************************************************** */
    public String getConnectionName() {
        return sConnectionName;
    }

    /* ********************************************************************** */
    public void setConnectionName(String sArgConnectionName) {
        this.sConnectionName = sArgConnectionName;
    }

    /* ********************************************************************** */
    private Integer iSSHConnectTimeOUT = 30;

    /* ********************************************************************** */
    public Integer getSSHConnectTimeOUT() {
        return iSSHConnectTimeOUT;
    }

    /* ********************************************************************** */
    private String sSSHHost = null;

    /* ********************************************************************** */
    public String getSSHHost() {
        return sSSHHost;
    }

    /* ********************************************************************** */
    public void setSSHHost(String sArgSSHHost) {

        this.sSSHHost = Objects.requireNonNullElse(sArgSSHHost,sLocalhostName);
    }

    /* ********************************************************************** */
    private Integer iSSHPort = 0;

    /* ********************************************************************** */
    public Integer getSSHPort() {
        return iSSHPort;
    }

    /* ********************************************************************** */
    public void setSSHPort(Integer iArgSSHPort) {

        this.iSSHPort = Objects.requireNonNullElse(iArgSSHPort, Integer.valueOf(SSH_DEFAULT_PORTNUMBER));

    }

    /* ********************************************************************** */
    private Integer iSSHPortLocalhost = 0; // localhost portpublic java.lang.Integer

    /* ********************************************************************** */
    public int getSSHPortLocalhost() {
        return iSSHPortLocalhost;
    }

    /* ********************************************************************** */
    public void setiSSHPortLocalhost(Integer iSSHPortLocalhost) {
        this.iSSHPortLocalhost = iSSHPortLocalhost;
    }

    /* ********************************************************************** */
    private Integer iSSHPortForwarded = 0; // resulted SSH Port forwardingpublic java.lang.Integer

    public Integer getiSSHPortForwarded() {
        return iSSHPortForwarded;
    }
    /* ********************************************************************** */

    public void setiSSHPortForwarded(java.lang.Integer iSSHPortForwarded) {
        this.iSSHPortForwarded = iSSHPortForwarded;
    }

    /* ********************************************************************** */
    private java.lang.String sSSHHostRemotedService = null;

    public java.lang.String getsSSHHostRemotedService() {
        return sSSHHostRemotedService;
    }
    /* ********************************************************************** */

    public void setsSSHHostRemotedService(java.lang.String sSSHHostRemotedService) {
        this.sSSHHostRemotedService = sSSHHostRemotedService;
    }
    /* ********************************************************************** */

    private java.lang.Integer iSSHPortRemotedService = null;

    public java.lang.Integer getiSSHPortRemotedService() {
        return iSSHPortRemotedService;
    }
    /* ********************************************************************** */

    public void setiSSHPortRemotedService(java.lang.Integer iSSHPortRemotedService) {
        this.iSSHPortRemotedService = iSSHPortRemotedService;
    }

    /* ********************************************************************** */
    private java.lang.String sSSHUser = null;
    /* ********************************************************************** */

    public java.lang.String getSSHUser() {
        return sSSHUser;
    }
    /* ********************************************************************** */

    public void setsSSHUser(java.lang.String sArgSSHUser) {
        this.sSSHUser = Objects.requireNonNullElse(sArgSSHUser, String.valueOf(""));
    }
    /* ********************************************************************** */

    private java.lang.String sSSHPassword = null;
    /* ********************************************************************** */

    public java.lang.String getSSHPassword() {
        return sSSHPassword;
    }
    /* ********************************************************************** */

    public void setsSSHPassword(java.lang.String sArgSSHPassword) {
        this.sSSHPassword = Objects.requireNonNullElse(sArgSSHPassword, String.valueOf(""));
    }

    /* ********************************************************************** */
    private java.lang.String sSSHPubliKeyFilePath = null;
    /* ********************************************************************** */

    public java.lang.String getSSHPubliKeyFilePath() {
        return sSSHPubliKeyFilePath;
    }
    /* ********************************************************************** */

    public void setSSHPubliKeyFilePath(java.lang.String sArgSSHPubliKeyFilePath) {
        this.sSSHPubliKeyFilePath = Objects.requireNonNullElse(sArgSSHPubliKeyFilePath, String.valueOf(""));
    }
    /* ********************************************************************** */

    public GNSObjectRemoteConnectionFactory() {
    }

    /* ********************************************************************** */
    public GNSObjectSSHConnection getSSHConnection(){

        aSSHConnection = Objects.requireNonNullElse( aSSHConnection,( new GNSObjectSSHConnection(this) ));
        return aSSHConnection;
    }


    public void setConnectionRemote(GNSObjectSSHConnection aArgConnection ) {
        aSSHConnection = aArgConnection;
    }
}