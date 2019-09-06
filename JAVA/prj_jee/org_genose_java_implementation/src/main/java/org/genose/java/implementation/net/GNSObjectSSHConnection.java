package org.genose.java.implementation.net;

import com.jcraft.jsch.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class GNSObjectSSHConnection {
    /* ********************************************************************** */
    public static final String SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER = "Invalid CONNECTION Parameter ";
    public static final Integer SSH_DEFAULT_PORTNUMBER = 22;

    /* ********************************************************************** */
    String sConnectionName = null;
    /* ********************************************************************** */
    Integer iSSHConnectTimeOUT = 30;
    /* ********************************************************************** */

    private String sSSHHost = null;
    private Integer iSSHPort = 0;
    private Integer iSSHPortLocalhost = 0; // localhost port
    private Integer iSSHPortForwarded = 0; // resulted SSH Port forwarding
    /* ********************************************************************** */
    private String sSSHHostRemotedService = null;
    private Integer iSSHPortRemotedService = null;
    /* ********************************************************************** */
    private String sSSHUser = null;
    private String sSSHPassword = null;
    /* ********************************************************************** */
    private String sSSHPubliKeyFilePath = null;
    /* ********************************************************************** */
    private com.jcraft.jsch.JSch aSSHConnection = null;
    private com.jcraft.jsch.Session aSSHSession = null;
    private com.jcraft.jsch.ChannelSftp aSftpChannel = null;
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
        sshConnectionArrayList = Objects.requireNonNullElse(sshConnectionArrayList, new ArrayList<GNSObjectSSHConnection>());
        if (sArgSSHPassword.isEmpty() && sArgSSHPubliKeyFilePath.isEmpty()) {
            throw new InvalidParameterException(SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : SSH PUB FILE and Password cant be empty or null ");
        }
        try {
            if (!sArgSSHPubliKeyFilePath.isEmpty()) {
                System.out.println("Attach pub key [" + sArgSSHPubliKeyFilePath + "]");
                aSSHConnection.addIdentity(sArgSSHPubliKeyFilePath);

            }
        } catch (Exception evERREXCEPTION_SSH_PARAMETERS) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERREXCEPTION_SSH_PARAMETERS);

            throw new Exception(SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while attach pubkey file ", evERREXCEPTION_SSH_PARAMETERS);
        }
        /* ********************************************************************** */

        if (sshConnectionArrayList.size() > iSSHMAXConnection) {
            throw new Exception(SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR max connection management reached ...");
        }
        /* ********************************************************************** */

        sshConnectionArrayList.add(this);
        /* ********************************************************************** */
        sSSHHost = sArgSSHHost;
        iSSHPort = iArgSSHPort;
        iSSHPortLocalhost = 0; // localhost port
        iSSHPortForwarded = 0; // resulted SSH Port forwarding
        /* ********************************************************************** */
        sSSHHostRemotedService = sArgSSHHostForwardedService;
        iSSHPortRemotedService = iArgSSHPortForwardedService;
        /* ********************************************************************** */
        sSSHUser = sArgSSHUser;
        sSSHPassword = sArgSSHPassword;
        /* ********************************************************************** */
        sSSHPubliKeyFilePath = sArgSSHPubliKeyFilePath;
        /* ********************************************************************** */
        aSSHConnection = new JSch();
    }

    /**
     * @return
     */
    public Session open() throws Exception {
        try {
            // https://stackoverflow.com/questions/1968293/connect-to-remote-mysql-database-through-ssh-using-java
            /* ********************************************************************** */
            if (aSSHSession != null) {
                if (!aSSHSession.isConnected()) {
                    if (
                            (String.valueOf(aSSHSession.getUserName()).compareToIgnoreCase(sSSHUser) == 0)
                                    && (String.valueOf(aSSHSession.getHost()).compareToIgnoreCase(sSSHHost) == 0)
                                    && (aSSHSession.getPort() == iSSHPort)
                    ) {
                        aSSHSession.connect();
                    } else {

                        aSSHSession.delPortForwardingL(iSSHPortLocalhost);
                        aSSHSession.disconnect();
                    }
                } else {
                    return aSSHSession;
                }
            }
            // assumed null ....
            /* ********************************************************************** */
            java.util.Properties config = new java.util.Properties();
            /* ********************************************************************** */
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Trying create connection Session with (" + sSSHUser + ":" + sSSHHost + ":" + iSSHPort + " paswd:" + sSSHPassword + ")");
            aSSHSession = aSSHConnection.getSession(sSSHUser, sSSHHost, iSSHPort);
            if ((sSSHPassword != null) && !sSSHPassword.isEmpty())
                aSSHSession.setPassword(sSSHPassword);

            /* ********************************************************************** */
            if ((sSSHPubliKeyFilePath != null) && !sSSHPubliKeyFilePath.isEmpty())
                aSSHConnection.addIdentity(sSSHPubliKeyFilePath);
            /* ********************************************************************** */
            config.put("StrictHostKeyChecking", "no");
            config.put("ConnectionAttempts", "3");
            config.put("PreferredAuthentications", "publickey,password,keyboard-interactive");
            aSSHSession.setConfig(config);

            /* ********************************************************************** */
            aSSHSession.connect();// (iSSHConnectTimeOUT);
            /* ********************************************************************** */
            sSSHHostRemotedService = Objects.requireNonNullElse(sSSHHostRemotedService, String.valueOf(""));
            iSSHPortRemotedService = Objects.requireNonNullElse(iSSHPortRemotedService, Integer.valueOf(0));
            iSSHPortLocalhost = Objects.requireNonNullElse(iSSHPortLocalhost, Integer.valueOf(0));
            /* ********************************************************************** */
            if (sSSHHostRemotedService.isEmpty() && iSSHPortRemotedService != 0) {
                System.out.println("Port Forwarding init");
                iSSHPortForwarded = aSSHSession.setPortForwardingL(iSSHPortLocalhost, sSSHHostRemotedService, iSSHPortRemotedService);
                System.out.println("Port Forwarded");
                System.out.println("localhost:" + iSSHPortLocalhost + " -> " + sSSHHostRemotedService + ":" + iSSHPortRemotedService);

            } else {
                iSSHPortForwarded = 0;
            }

        } catch (Exception evERREXCEPTION_SSH_OPEN) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERREXCEPTION_SSH_OPEN);
            throw new Exception(SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while open connection ", evERREXCEPTION_SSH_OPEN);

        }
        return aSSHSession;
    }

    /**
     * @return
     */
    public boolean close() throws Exception {
        try {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Trying Close session ...");

            /* ********************************************************************** */
            if (aSSHSession == null) return true;
            if (aSSHSession.isConnected()) {
                aSSHSession.disconnect();
            }
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Close session ... done ...");

            return aSSHSession.isConnected();
            /* ********************************************************************** */
        } catch (Exception evERREXCEPTION_SSH_CLOSE) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, "ERROR : " + evERREXCEPTION_SSH_CLOSE);
            throw new Exception(SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while close connection ", evERREXCEPTION_SSH_CLOSE);

        }
    }

    public int execShell(String strCommand) throws Exception {
        try {
            open();
            Channel channel = aSSHSession.openChannel("exec");
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Trying exec command : " + strCommand);
            ((ChannelExec) channel).setCommand(strCommand);

            // X Forwarding
            // channel.setXForwarding(true);

            //channel.setInputStream(System.in);
            channel.setInputStream(null);

            //channel.setOutputStream(System.out);

            //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
            //((ChannelExec)channel).setErrStream(fos);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream inStream = channel.getInputStream();
            OutputStream osStream = channel.getOutputStream();

            channel.connect();
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Trying Read Stream exec command : " + strCommand);

            byte[] tmp = new byte[1024];
            boolean bClosedExecution = false;
            int iExecReturnCode = 0;

            try {
                do {
                    System.out.println(" .... ");

                    while (inStream.available() > 0) {
                        int i = inStream.read(tmp, 0, 1024);
                        if (i < 0) break;
                        System.out.println(" ccreaded : " + (new String(tmp, 0, i)));
                    }


                    if (channel.isClosed()) {
                        if (inStream.available() > 0) continue;
                        iExecReturnCode = channel.getExitStatus();
                        bClosedExecution = true;
                        System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Exit Status (" + String.valueOf(iExecReturnCode) + ") exec command : " + strCommand);

                        break;
                    }
                    System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Trying Sleep Reading Stream exec command : " + strCommand);
                    Thread.sleep(200);
                    System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Trying Awake Reading Stream exec command : " + strCommand);


                } while (!bClosedExecution);
            } catch (Exception ee) {
                System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, "***** Error Reading Stream exec command : " + strCommand + " :: " + ee);

            }
            if (!channel.isClosed()) {
                System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Trying Close exec command : " + strCommand);

                channel.disconnect();
            }
            System.out.println(" .... done  ");
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Cleared exec command : " + strCommand);

            return iExecReturnCode;
        } catch (Exception evERREXCEPTION_SSH_EXEC) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERREXCEPTION_SSH_EXEC);
            throw new Exception(SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while exec connection ", evERREXCEPTION_SSH_EXEC);

        }

    }

    public boolean execSFTP() throws Exception {

        Channel channel;
        OutputStream os;

        try {
            open();

            channel = aSSHSession.openChannel("sftp");
            channel.connect();
            aSftpChannel = (ChannelSftp) channel;
            os = aSftpChannel.getOutputStream();
            return true;
        } catch (Exception evERREXCEPTION_SSH_EXEC) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERREXCEPTION_SSH_EXEC);
            throw new Exception(SSH_ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while exec connection ", evERREXCEPTION_SSH_EXEC);

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
