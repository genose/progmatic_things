package org.genose.java.implementation.net;

import com.jcraft.jsch.*;
import org.genose.java.implementation.streams.GNSObjectMappedLogger;

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
    private static HashMap<String, Channel> channelHashMap = null;

    /* ********************************************************************** */
    private static Map<String, GNSObjectSSHConnection> sshConnectionMap = null;
    private org.genose.java.implementation.net.GNSObjectRemoteConnectionFactory aConnectionParametersFactory = null;
    /* ********************************************************************** */
    private com.jcraft.jsch.JSch aSSHConnection = null;
    private com.jcraft.jsch.Session aSSHSession = null;
    /* ********************************************************************** */
    java.util.Properties aSessionConfigProperties = null;

    /**
     *
     */
    public GNSObjectSSHConnection() {
        throw new InvalidParameterException(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER);
    }

    /**
     * @param sConnectionName
     * @param sArgHost
     * @param iArgPort
     * @param sArgSSHHostRemotedService
     * @param iArgSSHPortRemotedService
     * @param sArgSSHUser
     * @param sArgSSHPassword
     * @param sArgRemoteServiceUSER
     * @param sArgRemoteServicePassword
     * @param sArgSSHPubliKeyFilePath
     * @throws Exception
     */

    public GNSObjectSSHConnection(String sConnectionName,
                                  String sArgHost, Integer iArgPort,
                                  String sArgSSHHostRemotedService, Integer iArgSSHPortRemotedService,
                                  String sArgSSHUser, String sArgSSHPassword,
                                  String sArgRemoteServiceUSER, String sArgRemoteServicePassword,
                                  String sArgSSHPubliKeyFilePath
    ) throws Exception {
        /* ********************************************************************** */
        sConnectionName = Objects.requireNonNullElse(sConnectionName, this.toString());

        /* ********************************************************************** */
        Objects.requireNonNull(sArgHost, ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER);
        Objects.requireNonNull(sArgSSHUser, ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER);
        /* ********************************************************************** */

        sArgSSHHostRemotedService = Objects.requireNonNullElse(sArgSSHHostRemotedService, "");
        iArgSSHPortRemotedService = Objects.requireNonNullElse(iArgSSHPortRemotedService, Integer.valueOf(0));
        /* ********************************************************************** */

        sArgSSHUser = Objects.requireNonNullElse(sArgSSHUser, "");
        sArgSSHPassword = Objects.requireNonNullElse(sArgSSHPassword, "");
        /* ********************************************************************** */

        sArgRemoteServiceUSER = Objects.requireNonNullElse(sArgRemoteServiceUSER, "");
        sArgRemoteServicePassword = Objects.requireNonNullElse(sArgRemoteServicePassword, "");
        /* ********************************************************************** */

        sArgSSHPubliKeyFilePath = Objects.requireNonNullElse(sArgSSHPubliKeyFilePath, "");

        /* ********************************************************************** */
        /* ********************************************************************** */
        if (sArgSSHPassword.isEmpty() && sArgSSHPubliKeyFilePath.isEmpty()) {
            throw new InvalidParameterException(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : SSH PUB FILE and Password cant be empty or null ");
        }
        /* ********************************************************************** */
        /* ********************************************************************** */

        sshConnectionMap = Objects.requireNonNullElse(sshConnectionMap, new HashMap<>());

        /* ********************************************************************** */
        /* ********************************************************************** */
        if (sshConnectionMap.size() > iSSHMAXConnection) {
            throw new Exception(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR max connection management reached ...");
        }
        /* ********************************************************************** */
        /* ********************************************************************** */
        sshConnectionMap.put(sConnectionName, this);
        /* ********************************************************************** */
        /* ********************************************************************** */
        aConnectionParametersFactory = new GNSObjectRemoteConnectionFactory();
        /* ********************************************************************** */
        aConnectionParametersFactory.setConnectionName(sConnectionName);
        /* ********************************************************************** */
        aConnectionParametersFactory.setHostname(sArgHost);
        // aConnectionParametersFactory.setPort(iArgPort);
        aConnectionParametersFactory.setPortRemotedServiceLocalhost(0); // localhost port
        aConnectionParametersFactory.setPortRemotedService(0); // resulted SSH Port forwarding
        /* ********************************************************************** */
        aConnectionParametersFactory.setHostRemotedService(sArgSSHHostRemotedService);
        aConnectionParametersFactory.setPortRemotedService(iArgSSHPortRemotedService);
        /* ********************************************************************** */
        aConnectionParametersFactory.setUsername(sArgSSHUser);
        aConnectionParametersFactory.setPassword(sArgSSHPassword);
        /* ********************************************************************** */
        aConnectionParametersFactory.setSSHPubliKeyFilePath(sArgSSHPubliKeyFilePath);
        /* ********************************************************************** */
        // aSSHSession = aConnectionParametersFactory.newSession();
        // aConnectionFactory.setConnectionRemote(this);
        aSSHConnection = aConnectionParametersFactory.getConnectionProvider();
    }

    public GNSObjectSSHConnection(GNSObjectRemoteConnectionFactory aArgRemoteConnectionFactory) throws Exception {
        Objects.requireNonNull(aArgRemoteConnectionFactory, ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER);

        /* ********************************************************************** */
        Objects.requireNonNull(aArgRemoteConnectionFactory.getHostname(), ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER);
        Objects.requireNonNull(aArgRemoteConnectionFactory.getUsername(), ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER);
        /* ********************************************************************** */

        Objects.requireNonNullElse(aArgRemoteConnectionFactory.getHostRemotedService(), "");
        Objects.requireNonNullElse(aArgRemoteConnectionFactory.getPortRemotedService(), Integer.valueOf(0));
        /* ********************************************************************** */

        Objects.requireNonNullElse(aArgRemoteConnectionFactory.getUserRemotedService(), "");
        Objects.requireNonNullElse(aArgRemoteConnectionFactory.getPasswordRemotedService(), "");
        /* ********************************************************************** */

        Objects.requireNonNullElse(aArgRemoteConnectionFactory.getRessourceRemotedService(), "");
        Objects.requireNonNullElse(aArgRemoteConnectionFactory.getFilePathRemotedService(), "");
        /* ********************************************************************** */

        Objects.requireNonNullElse(aArgRemoteConnectionFactory.getSSHPubliKeyFilePath(), "");

        /* ********************************************************************** */
        /* ********************************************************************** */
        if (aArgRemoteConnectionFactory.getPassword().isEmpty() && aArgRemoteConnectionFactory.getSSHPubliKeyFilePath().isEmpty()) {
            throw new InvalidParameterException(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : SSH PUB FILE and Password cant be empty or null ");
        }
        /* ********************************************************************** */
        /* ********************************************************************** */

        sshConnectionMap = Objects.requireNonNullElse(sshConnectionMap, new HashMap<>());

        /* ********************************************************************** */
        /* ********************************************************************** */
        if (sshConnectionMap.size() > iSSHMAXConnection) {
            throw new Exception(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR max connection management reached ...");
        }
        /* ********************************************************************** */
        /* ********************************************************************** */
        sshConnectionMap.put(aArgRemoteConnectionFactory.getConnectionName(), this);
        /* ********************************************************************** */
        /* ********************************************************************** */

        aConnectionParametersFactory = aArgRemoteConnectionFactory;
        /* ********************************************************************** */
        //aSSHSession = aConnectionParametersFactory.newSession();
        /* ********************************************************************** */
        aSSHConnection = aConnectionParametersFactory.getConnectionProvider();
        /* ********************************************************************** */
    }

    protected GNSObjectRemoteConnectionFactory getConnectionFactory() {
        return aConnectionParametersFactory;
    }

    protected Session getSSHSession() throws Exception {
        try {
            if (aSSHSession != null) {
                GNSObjectMappedLogger.getLogger().logInfo(this.getClass(), "Reuse previous [" + aConnectionParametersFactory.getConnectionProvider() + "]");
                if (!aSSHSession.isConnected()) {
                    GNSObjectMappedLogger.getLogger().logInfo(this.getClass(), "No connected [" + aConnectionParametersFactory.getConnectionProvider() + "]");

                    if (
                            (String.valueOf(aSSHSession.getUserName()).compareToIgnoreCase(aConnectionParametersFactory.getUsername()) == 0)
                                    && (String.valueOf(aSSHSession.getHost()).compareToIgnoreCase(aConnectionParametersFactory.getHostname()) == 0)
                                    && (aSSHSession.getPort() == aConnectionParametersFactory.getPort())
                    ) {
                        GNSObjectMappedLogger.getLogger().logInfo(this.getClass(), "Connect previous [" + aConnectionParametersFactory.getConnectionProvider() + "]");

                    } else {
/*                    if (aConnectionParametersFactory.getPortRemotedServiceLocalhost() != 0) {
                        aSSHSession.delPortForwardingL(aConnectionParametersFactory.getPortRemotedServiceLocalhost());
                    }*/
                        GNSObjectMappedLogger.getLogger().logInfo(this.getClass(), "Close previous [" + aConnectionParametersFactory.getConnectionProvider() + "]");

                    }
                } else {
                    GNSObjectMappedLogger.getLogger().logInfo(this.getClass(), "Already connected previous [" + aConnectionParametersFactory.getConnectionProvider() + "]");


                }
            }
            return aSSHSession;
        } catch (Exception evERREXCEPTION_SSH_SESSION_CHECK) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERREXCEPTION_SSH_SESSION_CHECK);
            throw new Exception(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while check session connection ", evERREXCEPTION_SSH_SESSION_CHECK);

        }

    }

    /**
     * @return
     */
    protected Session createSession() throws Exception {
        try {
            // https://stackoverflow.com/questions/1968293/connect-to-remote-mysql-database-through-ssh-using-java
            /* ********************************************************************** */

            Objects.requireNonNull(aConnectionParametersFactory, ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER);
            /* ********************************************************************** */

            if (getSSHSession() != null) {
                if (getSSHSession().isConnected()) {
                    return getSSHSession();
                }
            }
            /* ********************************************************************** */
            // assumed null from here ....
            /* ********************************************************************** */
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Trying create connection Session with ("
                    + aConnectionParametersFactory.getUsername() + ":"
                    + aConnectionParametersFactory.getHostname() + ":" + aConnectionParametersFactory.getPort()
                    + " paswd:" + aConnectionParametersFactory.getPassword()
                    + " timeout:" + aConnectionParametersFactory.getSSHConnectTimeOUT() + ")");
            /* ********************************************************************** */

            aSSHSession = aSSHConnection.getSession(aConnectionParametersFactory.getUsername(),
                    aConnectionParametersFactory.getHostname(),
                    aConnectionParametersFactory.getPort());
            /* ********************************************************************** */

            if ((aConnectionParametersFactory.getPassword() != null) && !aConnectionParametersFactory.getPassword().isEmpty())
                aSSHSession.setPassword(aConnectionParametersFactory.getPassword());

            /* ********************************************************************** */
            try {
                if ((aConnectionParametersFactory.getSSHPubliKeyFilePath() != null) && !aConnectionParametersFactory.getSSHPubliKeyFilePath().isEmpty()) {
                    System.out.println("Attach pub key [" + aConnectionParametersFactory.getSSHPubliKeyFilePath() + "]");
                    aSSHConnection.addIdentity(aConnectionParametersFactory.getSSHPubliKeyFilePath());

                }
            } catch (Exception evERREXCEPTION_SSH_PARAMETERS) {
                System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERREXCEPTION_SSH_PARAMETERS);

                //  throw new Exception(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while attach pubkey file ", evERREXCEPTION_SSH_PARAMETERS);
            }

            aSessionConfigProperties = Objects.requireNonNullElse(aSessionConfigProperties, (new java.util.Properties()));
            aSessionConfigProperties.clear();
            /* ********************************************************************** */
            aSessionConfigProperties.put("StrictHostKeyChecking", "no");
            aSessionConfigProperties.put("ConnectionAttempts", "3");
            aSessionConfigProperties.put("PreferredAuthentications", "publickey,password,keyboard-interactive");


            HostKey[] arrayHostKey = aSSHConnection.getHostKeyRepository().getHostKey();
            for (int i = 0; i < (arrayHostKey.length - 1); i++) {
                System.out.println("host : " + arrayHostKey[i].getHost());
                System.out.println("hostkey : " + arrayHostKey[i].getKey());

                if (arrayHostKey[i].getHost().equalsIgnoreCase(getConnectionFactory().getHostname())) {
                    aSSHSession.setConfig("server_host_type", arrayHostKey[i].getType());
                } else {
                    System.out.println("hosttype : " + arrayHostKey[i].getType());
                }
            }
            GNSObjectMappedLogger.getLogger().logInfo("sftp session connected without using proxy..." + aSSHSession.isConnected());


            aSSHSession.setDaemonThread(true);
            aSSHSession.setConfig(aSessionConfigProperties);

            /* ********************************************************************** */
            // aSSHSession.connect(aConnectionParametersFactory.getSSHConnectTimeOUT() * 1000);
            GNSObjectMappedLogger.getLogger().logInfo("session connected...." + aSSHSession.isConnected());

        } catch (Exception evERREXCEPTION_SSH_CREATESESSION) {
            GNSObjectMappedLogger.getLogger().logError(this.getClass(), evERREXCEPTION_SSH_CREATESESSION);
            throw new Exception(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while open connection ", evERREXCEPTION_SSH_CREATESESSION);

        }
        return aSSHSession;
    }

    /**
     * @return
     */
    public boolean openSession() throws Exception {
        try {

            /* ********************************************************************** */
            aSSHSession.connect(aConnectionParametersFactory.getSSHConnectTimeOUT() * 1000);
            GNSObjectMappedLogger.getLogger().logInfo("session connected...." + aSSHSession.isConnected());

        } catch (Exception evERROpenSession) {
            System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.ERROR, evERROpenSession);
            throw new Exception(ERROR_MESSAGE_INVALID_CONNECTIONPARAMETER + " : ERROR while open connection ", evERROpenSession);
        }
        return false;
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
     */
    public boolean closeSessionChannels() {
        try {


            channelHashMap.forEach((s, channel) -> {
                try {
                    if ((channel != null)) {
                        channel.disconnect();
                    }
                } catch (Exception EVERRCLOSE_CHANNEL) {
                    GNSObjectMappedLogger.getLogger().logError(this.getClass(), EVERRCLOSE_CHANNEL);
                }
            });

            channelHashMap.clear();
            return true;
        } catch (Exception EVERRClear_CHANNEL) {
            GNSObjectMappedLogger.getLogger().logError(this.getClass(), EVERRClear_CHANNEL);
        }
        return false;
    }

    /**
     * @param channel
     * @param inStream
     * @param osStream
     * @param strCommand
     * @return
     */
    public int channelStreamPrint(Channel channel, InputStream inStream, OutputStream osStream, String strCommand) {

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

        System.out.println(" .... done  ");
        System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Cleared exec command : " + strCommand);
        return iExecReturnCode;
    }

    /**
     * @return true, if session is connected
     */
    public boolean openXGUISession() {
        try {
            createSession();
            aSSHSession.setX11Host(getConnectionFactory().getHostRemotedService());
            aSSHSession.setX11Port(getConnectionFactory().getPortRemotedService());
            openSession();

            Channel channel = aSSHSession.openChannel("shell");

            channel.setXForwarding(true);

            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);

            channel.connect();
            return aSSHSession.isConnected();
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }


    /**
     * @return
     * @throws Exception
     */
    public boolean addSSHTunnel(boolean bRemoteAFromRemoteB) throws Exception {
        try {
            if (openSession() ) {

                /* ********************************************************************** */
                aConnectionParametersFactory.setHostRemotedService(Objects.requireNonNullElse(aConnectionParametersFactory.getHostRemotedService(), String.valueOf("")));
                aConnectionParametersFactory.setPortRemotedService(Objects.requireNonNullElse(aConnectionParametersFactory.getPortRemotedService(), Integer.valueOf(0)));
                aConnectionParametersFactory.setPortRemotedServiceLocalhost(Objects.requireNonNullElse(aConnectionParametersFactory.getPortRemotedServiceLocalhost(), Integer.valueOf(0)));
                /* ********************************************************************** */
                if (aConnectionParametersFactory.getHostRemotedService().isEmpty() && aConnectionParametersFactory.getPortRemotedService() != 0) {
                    System.out.println("Local to Remote Port Forwarding init");
                    aConnectionParametersFactory.setPortRemotedService(
                            aSSHSession.setPortForwardingL(
                                    aConnectionParametersFactory.getPortRemotedServiceLocalhost(),
                                    ((!bRemoteAFromRemoteB)? aConnectionParametersFactory.getLocalHostname():aConnectionParametersFactory.getHostRemotedService()),
                                    aConnectionParametersFactory.getPortRemotedService()
                            )
                    );
                    System.out.println("Port Remoted");
                    System.out.println("localhost:" + aConnectionParametersFactory.getPortRemotedServiceLocalhost() + " -> " + aConnectionParametersFactory.getHostRemotedService() + ":" + aConnectionParametersFactory.getPortRemotedService());

                } else {
                    aConnectionParametersFactory.setPortRemotedService(0);
                }

                return aConnectionParametersFactory.getPortRemotedService() != 0;
            } else {
                return false;
            }
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

                    aSSHSession.delPortForwardingL(aConnectionParametersFactory.getPortRemotedServiceLocalhost());
                    aSSHSession.delPortForwardingR(aConnectionParametersFactory.getPortRemotedService());
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
    // http://www.jcraft.com/jsch/examples/X11Forwarding.java
    public int execShell(String strCommand) throws Exception {
        try {
            if (openSession()) {
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

                int iExecReturn = channelStreamPrint(channel, inStream, osStream, strCommand);
                if (!channel.isClosed()) {
                    System.getLogger(getClass().getSimpleName()).log(System.Logger.Level.INFO, "***** Trying Close exec command : " + strCommand);

                    channel.disconnect();
                }
                return iExecReturn;
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
            int iExecReturn = channelStreamPrint(channel, null, os, null);
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
            environment.put("defaultSessionFactory", aConnectionParametersFactory);
            environment.put("watchservice.inotify", true);
            URI uri = new URI(scheme + "://"
                    + aConnectionParametersFactory.getUsername()
                    + "@" + aConnectionParametersFactory.getHostname()
                    + ":" + aConnectionParametersFactory.getPort()
                    + aConnectionParametersFactory.getFilePathRemotedService());
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
