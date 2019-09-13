package org.genose.java.implementation.net;

import com.jcraft.jsch.*;
import com.pastdev.jsch.DefaultSessionFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Thread.sleep;
import static org.genose.java.implementation.net.GNSObjectRemoteConnectionFactory.ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER;

public class GNSObjectSSHConnection implements Closeable {
    /* ********************************************************************** */

    // ::   private com.jcraft.jsch.ChannelSftp aSftpChannel = null;
    private static Integer iSSHMAXConnection = 10;
    private static ArrayList<Session> sessionArrayList = null;

    /* ********************************************************************** */
    private static Map<String, GNSObjectSSHConnection> sshConnectionArrayList = null;
    private org.genose.java.implementation.net.GNSObjectRemoteConnectionFactory aConnectionFactory = null;
    /* ********************************************************************** */
    private com.jcraft.jsch.JSch aSSHConnection = null;
    private com.jcraft.jsch.Session aSSHSession = null;

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

    public GNSObjectSSHConnection(String sConnectionName,
                                  String sArgSSHHost, Integer iArgSSHPort,
                                  String sArgSSHHostForwardedService, Integer iArgSSHPortForwardedService,
                                  String sArgSSHUser, String sArgSSHPassword,
                                  String sArgRemoteServiceUSER, String sArgRemoteServicePassword,
                                  String sArgSSHPubliKeyFilePath
    ) throws Exception {
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
        aConnectionFactory.setPortForwarded(0); // resulted SSH Port forwarding
        /* ********************************************************************** */
        aConnectionFactory.setHostRemotedService(sArgSSHHostForwardedService);
        aConnectionFactory.setiSSHPortRemotedService(iArgSSHPortForwardedService);
        /* ********************************************************************** */
        aConnectionFactory.setsSSHUser(sArgSSHUser);
        aConnectionFactory.setsSSHPassword(sArgSSHPassword);
        /* ********************************************************************** */
        aConnectionFactory.setSSHPubliKeyFilePath(sArgSSHPubliKeyFilePath);
        /* ********************************************************************** */
        aSSHSession = aConnectionFactory.newSession();
        // aConnectionFactory.setConnectionRemote(this);
    }

    public GNSObjectSSHConnection(org.genose.java.implementation.net.GNSObjectRemoteConnectionFactory aArgRemoteConnectionFactory) {
        Objects.requireNonNull(aArgRemoteConnectionFactory, ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER);
        sshConnectionArrayList.put(aArgRemoteConnectionFactory.getConnectionName(), this);
        aConnectionFactory = aArgRemoteConnectionFactory;
        aSSHConnection = new JSch();
    }

    protected GNSObjectRemoteConnectionFactory getConnectionFactory() {
        return aConnectionFactory;
    }

    /**
     * @return
     */
    protected Session openSession() throws Exception {
        try {
            // https://stackoverflow.com/questions/1968293/connect-to-remote-mysql-database-through-ssh-using-java
            /* ********************************************************************** */

            Objects.requireNonNull(aConnectionFactory, ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER);
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
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Trying create connection Session with (" + aConnectionFactory.getSSHUser() + ":" + aConnectionFactory.getSSHHost() + ":" + aConnectionFactory.getSSHPort() + " paswd:" + aConnectionFactory.getSSHPassword() + " timeout:" + aConnectionFactory.getSSHConnectTimeOUT() + ")");
            /* ********************************************************************** */

            aSSHSession = aSSHConnection.getSession(aConnectionFactory.getSSHUser(), aConnectionFactory.getSSHHost(), aConnectionFactory.getSSHPort());
            /* ********************************************************************** */

            if ((aConnectionFactory.getSSHPassword() != null) && !aConnectionFactory.getSSHPassword().isEmpty())
                aSSHSession.setPassword(aConnectionFactory.getSSHPassword());

            /* ********************************************************************** */
            try {
                if ((aConnectionFactory.getSSHPubliKeyFilePath() != null) && !aConnectionFactory.getSSHPubliKeyFilePath().isEmpty()) {
                    System.out.println("Attach pub key [" + aConnectionFactory.getSSHPubliKeyFilePath() + "]");
                    aSSHConnection.addIdentity(aConnectionFactory.getSSHPubliKeyFilePath());

                }
            } catch (Exception evERREXCEPTION_SSH_PARAMETERS) {
                System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERREXCEPTION_SSH_PARAMETERS);

                //  throw new Exception(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while attach pubkey file ", evERREXCEPTION_SSH_PARAMETERS);
            }

            /* ********************************************************************** */
            config.put("StrictHostKeyChecking", "no");
            config.put("ConnectionAttempts", "3");
            config.put("PreferredAuthentications", "publickey,password,keyboard-interactive");
            aSSHSession.setConfig(config);

            /* ********************************************************************** */
            aSSHSession.connect(aConnectionFactory.getSSHConnectTimeOUT() * 1000);


        } catch (Exception evERREXCEPTION_SSH_OPEN) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERREXCEPTION_SSH_OPEN);
            throw new Exception(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while open connection ", evERREXCEPTION_SSH_OPEN);

        }
        return aSSHSession;
    }

    /**
     * @return
     */
    public boolean closeSession() throws Exception {
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

    /**
     * @return
     * @throws Exception
     */
    public boolean addSSHTunnel() throws Exception {
        try {
            openSession();

            /* ********************************************************************** */
            aConnectionFactory.setHostRemotedService(Objects.requireNonNullElse(aConnectionFactory.getHostRemotedService(), String.valueOf("")));
            aConnectionFactory.setiSSHPortRemotedService(Objects.requireNonNullElse(aConnectionFactory.getPortRemotedService(), Integer.valueOf(0)));
            aConnectionFactory.setiSSHPortLocalhost(Objects.requireNonNullElse(aConnectionFactory.getSSHPortLocalhost(), Integer.valueOf(0)));
            /* ********************************************************************** */
            if (aConnectionFactory.getHostRemotedService().isEmpty() && aConnectionFactory.getPortRemotedService() != 0) {
                System.out.println("Port Forwarding init");
                aConnectionFactory.setPortForwarded(aSSHSession.setPortForwardingL(aConnectionFactory.getSSHPortLocalhost(), aConnectionFactory.getHostRemotedService(), aConnectionFactory.getPortRemotedService()));
                System.out.println("Port Forwarded");
                System.out.println("localhost:" + aConnectionFactory.getSSHPortLocalhost() + " -> " + aConnectionFactory.getHostRemotedService() + ":" + aConnectionFactory.getPortRemotedService());

            } else {
                aConnectionFactory.setPortForwarded(0);
            }

            return aConnectionFactory.getPortForwarded() != 0;

        } catch (Exception evERRInitTunnel) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, "ERROR : " + evERRInitTunnel);
            throw new Exception(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while close connection ", evERRInitTunnel);
        }
    }

    /**
     * @return
     * @throws Exception
     */
    public boolean delSSHTunnel() throws Exception {
        try {

            if (aSSHSession != null) {
                if (aSSHSession.isConnected()) {

                    aSSHSession.delPortForwardingL(aConnectionFactory.getSSHPortLocalhost());
                    aSSHSession.delPortForwardingR(aConnectionFactory.getPortForwarded());
                }

                return aSSHSession.isConnected();
            }

            return false;
        } catch (Exception evERRInitTunnel) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, "ERROR : " + evERRInitTunnel);
            throw new Exception(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while close connection ", evERRInitTunnel);
        }
    }

    /**
     * @param strCommand
     * @return
     * @throws Exception
     */
    public int execShell(String strCommand) throws Exception {
        try {
            if (openSession() != null) {
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

    public boolean execSFTP(String sFTPCommand) throws Exception {

        Channel channel;
        OutputStream os;
        ChannelSftp aSftpChannel;
        try {
            openSession();

            channel = aSSHSession.openChannel("sftp");
            channel.connect();
            aSftpChannel = (ChannelSftp) channel;
            os = aSftpChannel.getOutputStream();
            aSftpChannel.disconnect();
            channel.disconnect();
            return true;
        } catch (Exception evERREXCEPTION_SSH_EXEC) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERREXCEPTION_SSH_EXEC);
            throw new Exception(GNSObjectRemoteConnectionFactory.ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while exec connection ", evERREXCEPTION_SSH_EXEC);

        }
    }

    public boolean execMountSSHFSRemote(String sLocalDestPath, String sRemoteOriginPath) {
        try {

            // defaultSessionFactory.setKnownHosts(knownHosts);
            // defaultSessionFactory.setIdentityFromPrivateKey(privateKey);
            String scheme = "";

            Map<String, Object> environment = new HashMap<String, Object>();
            environment.put("defaultSessionFactory", aConnectionFactory);
            environment.put("watchservice.inotify", true);
            URI uri = new URI(scheme + "://" + aConnectionFactory.getSSHUser() + "@" + aConnectionFactory.getSSHHost() + ":" + aConnectionFactory.getSSHPort() + aConnectionFactory.getFilePathRemotedService());
            FileSystem fileSystem = FileSystems.newFileSystem(uri, environment);

        } catch (Exception evERRSSHFSMOUNT) {
            //  Assume.assumeNoException(evERRSSHFSMOUNT);
        }
        return false;
    }



    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {

    }
}
