package org.genose.java.implementation.net;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Objects;

public class GNSObjectSSHConnection {
    /* ********************************************************************** */
    public static final String SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER = "Invalid CONNECTION Parameter ";
    public static final Integer SSH_DEFAULT_PORTNUMBER = 22;

    /* ********************************************************************** */
    String sConnectionName = null;
    /* ********************************************************************** */
    Integer iSSHConnectTimeOUT = 30;
    /* ********************************************************************** */

    String sSSHHost = null;
    Integer iSSHPort = 0;
    Integer iSSHPortLocalhost = 0; // localhost port
    Integer iSSHPortForwarded = 0; // resulted SSH Port forwarding
    /* ********************************************************************** */
    private String sSSHHostRemotedService = null;
    private int iSSHPortRemotedService;
    /* ********************************************************************** */
    String sSSHUser = null;
    String sArgSSHPassword = null;
    /* ********************************************************************** */
    String sSSHPubliKeyFilePath = null;
    /* ********************************************************************** */
    private JSch aSSHConnection = null;
    private Session session = null;
    private static Integer iSSHMAXConnection = 10;
    private static ArrayList<Session> sessionArrayList = null;
    private static ArrayList<GNSObjectSSHConnection> sshConnectionArrayList = null;

    /**
     *
     */
    public GNSObjectSSHConnection() {
        throw new InvalidParameterException(SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER);
    }

    /**
     * @param sConnectionName
     * @param sArgSSHHost
     * @param iArgSSHPort
     * @param sArgSSHHostForwardedService
     * @param iArgSSHPortForwardedService
     * @param sArgSSHUser
     * @param sArgSSHPassword
     * @param sArgSSHPubliKeyFilePath
     */
    public GNSObjectSSHConnection(String sConnectionName, String sArgSSHHost, Integer iArgSSHPort, String sArgSSHHostForwardedService, Integer iArgSSHPortForwardedService, String sArgSSHUser, String sArgSSHPassword, String sArgSSHPubliKeyFilePath) throws Exception {

        Objects.requireNonNull(sArgSSHHost, SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER);
        Objects.requireNonNull(sArgSSHUser, SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER);

        iArgSSHPort = Objects.requireNonNullElse(iArgSSHPort, Integer.valueOf(0));
        sArgSSHPubliKeyFilePath = Objects.requireNonNullElse(sArgSSHPubliKeyFilePath, String.valueOf(""));

        if (iArgSSHPort == 0) {
            iArgSSHPort = SSH_DEFAULT_PORTNUMBER;
        }

        if (sArgSSHPassword.isEmpty() && sArgSSHPubliKeyFilePath.isEmpty()) {
            throw new InvalidParameterException(SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : SSH PUB FILE and Password cant be empty or null ");
        }
        try {
            if (!sArgSSHPubliKeyFilePath.isEmpty()) {
                aSSHConnection.addIdentity(sArgSSHPubliKeyFilePath);

            }
        } catch (Exception evERREXCEPTION_SSH_PARAMETERS) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERREXCEPTION_SSH_PARAMETERS);

            throw new Exception(SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER+" : ERROR while attach pubkey file ", evERREXCEPTION_SSH_PARAMETERS);
        }
        /* ********************************************************************** */

        if (sshConnectionArrayList.size() > iSSHMAXConnection) {
            throw new Exception(SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR max connection management reached ...");
        }
        /* ********************************************************************** */

        sshConnectionArrayList.add(this);
        /* ********************************************************************** */

    }

    /**
     * @return
     */
    public Session open() throws Exception {
        try {
            // https://stackoverflow.com/questions/1968293/connect-to-remote-mysql-database-through-ssh-using-java
            /* ********************************************************************** */
            if (session != null) close();
            if (session.isConnected()) return session;
            session = null;
            /* ********************************************************************** */
            java.util.Properties config = new java.util.Properties();
            /* ********************************************************************** */
            session = aSSHConnection.getSession(sSSHUser, sSSHHost, iSSHPort);
            /* ********************************************************************** */
            aSSHConnection.addIdentity(sSSHPubliKeyFilePath);
            /* ********************************************************************** */
            config.put("StrictHostKeyChecking", "no");
            config.put("ConnectionAttempts", "3");
            session.setConfig(config);

            /* ********************************************************************** */
            session.connect(iSSHConnectTimeOUT);
            /* ********************************************************************** */
            Objects.requireNonNullElse(sSSHHostRemotedService, String.valueOf(""));
            Objects.requireNonNullElse(iSSHPortRemotedService, Integer.valueOf(0));
            Objects.requireNonNullElse(iSSHPortLocalhost, Integer.valueOf(0));
            /* ********************************************************************** */
            if (sSSHHostRemotedService.isEmpty() && iSSHPortRemotedService != 0) {
                iSSHPortForwarded = session.setPortForwardingL(iSSHPortLocalhost, sSSHHostRemotedService, iSSHPortRemotedService);
                System.out.println("Port Forwarded");
                System.out.println("localhost:" + iSSHPortLocalhost + " -> " + sSSHHostRemotedService + ":" + iSSHPortRemotedService);

            } else {
                iSSHPortForwarded = 0;
            }

        } catch (Exception evERREXCEPTION_SSH_OPEN) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERREXCEPTION_SSH_OPEN);
            throw new Exception(SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER+" : ERROR while attach pubkey file ", evERREXCEPTION_SSH_OPEN);

        }
        return session;
    }

    /**
     * @return
     */
    public boolean close() throws Exception {
        try {
            /* ********************************************************************** */
            if (session == null) return true;
            if (session.isConnected()) session.disconnect();
            return session.isConnected();
            /* ********************************************************************** */
        } catch (Exception evERREXCEPTION_SSH_CLOSE) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERREXCEPTION_SSH_CLOSE);
            throw new Exception(SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER+" : ERROR while attach pubkey file ", evERREXCEPTION_SSH_CLOSE);

        }
    }

    /**
     * @param iArgLocalHostPort
     */
    public void setSSHPortLocalhost(Integer iArgLocalHostPort) {
        iSSHPortLocalhost = iArgLocalHostPort;
    }

    /**
     * @return
     */
    public Integer getSSHPortForwarded() {
        return iSSHPortForwarded;
    }
}
