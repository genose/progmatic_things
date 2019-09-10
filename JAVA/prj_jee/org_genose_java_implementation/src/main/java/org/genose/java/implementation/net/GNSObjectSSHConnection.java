package org.genose.java.implementation.net;

import com.jcraft.jsch.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Thread.sleep;
import static org.genose.java.implementation.net.GNSObjectRemoteConnectionFactory.ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER;

public class GNSObjectSSHConnection {
    /* ********************************************************************** */

    private org.genose.java.implementation.net.GNSObjectRemoteConnectionFactory aConnectionFactory = null;

    /* ********************************************************************** */

    /* ********************************************************************** */
    private com.jcraft.jsch.JSch aSSHConnection = null;
    private com.jcraft.jsch.Session aSSHSession = null;
    private com.jcraft.jsch.ChannelSftp aSftpChannel = null;
    private static Integer iSSHMAXConnection = 10;
    private static ArrayList<Session> sessionArrayList = null;
    private static Map<String, GNSObjectSSHConnection> sshConnectionArrayList = null;

    /**
     *
     */
    public GNSObjectSSHConnection() {
        throw new InvalidParameterException(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER);
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
        /* ********************************************************************** */
        Objects.requireNonNull(sArgSSHHost, ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER);
        Objects.requireNonNull(sArgSSHUser, ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER);

        /* ********************************************************************** */
        iArgSSHPort = Objects.requireNonNullElse(iArgSSHPort, Integer.valueOf(0));
        sArgSSHPubliKeyFilePath = Objects.requireNonNullElse(sArgSSHPubliKeyFilePath, String.valueOf(""));
        sConnectionName = Objects.requireNonNullElse(sConnectionName, this.toString());
        /* ********************************************************************** */
        sshConnectionArrayList = Objects.requireNonNullElse(sshConnectionArrayList, new HashMap<>());
        /* ********************************************************************** */
        if (iArgSSHPort == 0) {
            iArgSSHPort = GNSObjectRemoteConnectionFactory.SSH_DEFAULT_PORTNUMBER;
        }
        /* ********************************************************************** */
        if (sArgSSHPassword.isEmpty() && sArgSSHPubliKeyFilePath.isEmpty()) {
            throw new InvalidParameterException(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : SSH PUB FILE and Password cant be empty or null ");
        }
        /* ********************************************************************** */
        try {
            if (!sArgSSHPubliKeyFilePath.isEmpty()) {
                System.out.println("Attach pub key [" + sArgSSHPubliKeyFilePath + "]");
                aSSHConnection.addIdentity(sArgSSHPubliKeyFilePath);

            }
        } catch (Exception evERREXCEPTION_SSH_PARAMETERS) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERREXCEPTION_SSH_PARAMETERS);

            throw new Exception(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while attach pubkey file ", evERREXCEPTION_SSH_PARAMETERS);
        }
        /* ********************************************************************** */

        if (sshConnectionArrayList.size() > iSSHMAXConnection) {
            throw new Exception(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR max connection management reached ...");
        }
        /* ********************************************************************** */

        sshConnectionArrayList.put(sConnectionName, this);
        /* ********************************************************************** */

        aConnectionFactory = new GNSObjectRemoteConnectionFactory();
        aConnectionFactory.setSSHHost(sArgSSHHost);
        aConnectionFactory.setSSHPort(iArgSSHPort);
        aConnectionFactory.setiSSHPortLocalhost(0); // localhost port
        aConnectionFactory.setiSSHPortForwarded(0); // resulted SSH Port forwarding
        /* ********************************************************************** */
        aConnectionFactory.setsSSHHostRemotedService(sArgSSHHostForwardedService);
        aConnectionFactory.setiSSHPortRemotedService(iArgSSHPortForwardedService);
        /* ********************************************************************** */
        aConnectionFactory.setsSSHUser(sArgSSHUser);
        aConnectionFactory.setsSSHPassword(sArgSSHPassword);
        /* ********************************************************************** */
        aConnectionFactory.setSSHPubliKeyFilePath(sArgSSHPubliKeyFilePath);
        /* ********************************************************************** */
        aSSHConnection = new JSch();
        aConnectionFactory.setConnectionRemote(this);
    }

    public GNSObjectSSHConnection(org.genose.java.implementation.net.GNSObjectRemoteConnectionFactory aArgRemoteConnectionFactory) {
        aConnectionFactory = aArgRemoteConnectionFactory;
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
                            (String.valueOf(aSSHSession.getUserName()).compareToIgnoreCase(aConnectionFactory.getSSHUser()) == 0)
                                    && (String.valueOf(aSSHSession.getHost()).compareToIgnoreCase(aConnectionFactory.getSSHHost()) == 0)
                                    && (aSSHSession.getPort() == aConnectionFactory.getSSHPort())
                    ) {
                        aSSHSession.connect();
                    } else {

                        aSSHSession.delPortForwardingL(aConnectionFactory.getSSHPortLocalhost());
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
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Trying create connection Session with (" + aConnectionFactory.getSSHUser() + ":" + aConnectionFactory.getSSHHost() + ":" + aConnectionFactory.getSSHPort() + " paswd:" + aConnectionFactory.getSSHPassword() + " timeout:"+aConnectionFactory.getSSHConnectTimeOUT()+")");
            aSSHSession = aSSHConnection.getSession(aConnectionFactory.getSSHUser(), aConnectionFactory.getSSHHost(), aConnectionFactory.getSSHPort());
            if ((aConnectionFactory.getSSHPassword() != null) && !aConnectionFactory.getSSHPassword().isEmpty())
                aSSHSession.setPassword(aConnectionFactory.getSSHPassword());

            /* ********************************************************************** */
            if ((aConnectionFactory.getSSHPubliKeyFilePath() != null) && !aConnectionFactory.getSSHPubliKeyFilePath().isEmpty())
                aSSHConnection.addIdentity(aConnectionFactory.getSSHPubliKeyFilePath());
            /* ********************************************************************** */
            config.put("StrictHostKeyChecking", "no");
            config.put("ConnectionAttempts", "3");
            config.put("PreferredAuthentications", "publickey,password,keyboard-interactive");
            aSSHSession.setConfig(config);

            /* ********************************************************************** */
            aSSHSession.connect(aConnectionFactory.getSSHConnectTimeOUT()*1000);
            /* ********************************************************************** */
            aConnectionFactory.setsSSHHostRemotedService(Objects.requireNonNullElse(aConnectionFactory.getsSSHHostRemotedService(), String.valueOf("")));
            aConnectionFactory.setiSSHPortRemotedService(Objects.requireNonNullElse(aConnectionFactory.getiSSHPortRemotedService(), Integer.valueOf(0)));
            aConnectionFactory.setiSSHPortLocalhost(Objects.requireNonNullElse(aConnectionFactory.getSSHPortLocalhost(), Integer.valueOf(0)));
            /* ********************************************************************** */
            if (aConnectionFactory.getsSSHHostRemotedService().isEmpty() && aConnectionFactory.getiSSHPortRemotedService() != 0) {
                System.out.println("Port Forwarding init");
                aConnectionFactory.setiSSHPortForwarded(aSSHSession.setPortForwardingL(aConnectionFactory.getSSHPortLocalhost(), aConnectionFactory.getsSSHHostRemotedService(), aConnectionFactory.getiSSHPortRemotedService()));
                System.out.println("Port Forwarded");
                System.out.println("localhost:" + aConnectionFactory.getSSHPortLocalhost() + " -> " + aConnectionFactory.getsSSHHostRemotedService() + ":" + aConnectionFactory.getiSSHPortRemotedService());

            } else {
                aConnectionFactory.setiSSHPortForwarded(0);
            }

        } catch (Exception evERREXCEPTION_SSH_OPEN) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERREXCEPTION_SSH_OPEN);
            throw new Exception(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while open connection ", evERREXCEPTION_SSH_OPEN);

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
            throw new Exception(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while close connection ", evERREXCEPTION_SSH_CLOSE);

        }
    }

    public int execShell(String strCommand) throws Exception {
        try {
            if (open() != null) {
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

            } else {
                return -1;
            }
        } catch (
                Exception evERREXCEPTION_SSH_EXEC) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERREXCEPTION_SSH_EXEC);
            throw new Exception(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while exec connection ", evERREXCEPTION_SSH_EXEC);

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
            throw new Exception(GNSObjectRemoteConnectionFactory.ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while exec connection ", evERREXCEPTION_SSH_EXEC);

        }
    }

    /**
     * @param iArgLocalHostPort
     */
    public void setSSHPortLocalhost(Integer iArgLocalHostPort) {
        aConnectionFactory.setiSSHPortLocalhost(iArgLocalHostPort);
    }

    /**
     * @return
     */
    public Integer getSSHPortForwarded() {
        return aConnectionFactory.getiSSHPortForwarded();
    }
}
