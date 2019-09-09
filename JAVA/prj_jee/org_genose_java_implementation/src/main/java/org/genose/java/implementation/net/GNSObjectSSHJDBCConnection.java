package org.genose.java.implementation.net;

import com.jcraft.jsch.Session;
import org.genose.java.implementation.streams.GNSObjectMappedLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class GNSObjectSSHJDBCConnection extends GNSObjectSSHConnection {

public final String CONNECTION_NAME = "SGBDCONNECTION";
private String sJDBCConnectionName = "";

    public GNSObjectSSHJDBCConnection(String sConnectionName, String sArgSSHHost, Integer iArgSSHPort, String sArgSSHHostForwardedService, Integer iArgSSHPortForwardedService, String sArgSSHUser, String sArgSSHPassword, String sArgRemoteServiceUSER, String sArgRemoteServicePassword, String sArgSSHPubliKeyFilePath) throws Exception {
        super( sConnectionName, sArgSSHHost, iArgSSHPort, sArgSSHHostForwardedService, iArgSSHPortForwardedService, sArgSSHUser, sArgSSHPassword,  sArgRemoteServiceUSER,  sArgRemoteServicePassword, sArgSSHPubliKeyFilePath);


        }

    @Override
    public Session open() throws Exception {


        if(openConnection() != null ){
            return null;
        }else{
            return null;
        }

    }

    private Connection openConnection(){

        try {
            super.open();
            //addSSHPortForwardind(sJDBCConnectionName, 0, getSSHHost(), iArgSSHPortForwardedService);


            Properties properties = new Properties();
            // properties.setProperty("user", user);
            // properties.setProperty("password", password);
            properties.setProperty("useUnicode", "true");
            properties.setProperty("characterEncoding", "UTF-8");
            // Class.forName("com.mysql.jdbc.Driver").newInstance();
            // Connection con = DriverManager.getConnection( "jdbc:mysql://" + url + ":3306/" + nameBD + "?autoReconnect=true", properties);
        }catch(Exception evERR_CONNECTION){
            GNSObjectMappedLogger.getLogger().logError(this.getClass(), evERR_CONNECTION);
        }
        return null;
    }

    private void addSSHPortForwardind() {
    }
}
