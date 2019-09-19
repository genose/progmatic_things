package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import org.genose.java.implementation.dataaccessobject.GNSObjectDAOConnexion;
import org.genose.java.implementation.dataaccessobject.GNSObjectDAOStrings;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

public class SDBMConnect extends GNSObjectDAOConnexion implements GNSObjectDAOStrings{
	// Declare the JDBC objects.
	// private static Connection aLServerConnexion = null;
	// private static SDBMConnect aServerInstanceConnexion = null;
	// private static String sConnectionHost = null;
	
	public SDBMConnect() {
		super();
		try {

//			String dbURL = "jdbc:sqlserver://DESKTOP-FCU3LC0:1433;databaseName=SDBM";
//			String user = "javaSDBM";
//			String pass = "javaSDBM";
//			connexion = DriverManager.getConnection(dbURL, user, pass);

			// Using as remoted serveur
			setRessourceRemotedService("SDBM");
			setHostRemotedService(getLocalHostname());
			setPortRemotedService(1433);

			setUserRemotedService("javasdbm");
			setPasswordRemotedService("javasdbm");

			System.out.println("try connection on " + getHostRemotedService()+"::"+getPortRemotedService()+" :: "+getUserRemotedService()+"::"+getPasswordRemotedService()+" :: "+getRessourceRemotedService());

			SQLServerDataSource ds = new SQLServerDataSource();

			ds.setServerName(getHostRemotedService());

			ds.setPortNumber(getPortRemotedService());

			ds.setDatabaseName(getRessourceRemotedService());

			ds.setIntegratedSecurity(false);

			ds.setUser(getUserRemotedService());

			ds.setPassword(getPasswordRemotedService());
			// aServerInstanceConnexion = this;
			//ds.getConnection();
			setServerConnexion( ds.getConnection());
			Objects.requireNonNull(getServerConnexion(), S_ERRMESSAGE_DAO_NULLCONNECT);
			
		}

		// Handle any errors that may have occurred.
		catch (Exception evERRSDBDCONNECTION) {
			evERRSDBDCONNECTION.printStackTrace();
			JFXApplicationException.raiseToFront(this.getClass(), evERRSDBDCONNECTION, true);
		}
	}

	public static synchronized SDBMConnect getInstance() {
		SDBMConnect aDAOObject = (SDBMConnect) SDBMConnect.getInstance(SDBMConnect.class);
		System.out.println(" +++++++++++++++++++ "+aDAOObject);
		return   aDAOObject;
	}



	public static void main(String[] args) {
		System.out.println("Test connexion ...");
		try {
			SDBMConnect aBDDConnexion = new SDBMConnect();

			Objects.requireNonNull(aBDDConnexion,
					"Connection au server " + SDBMConnect.getInstance());
			System.out.println(" Result : " + aBDDConnexion);
			Connection aServerConnexion = aBDDConnexion.getServerConnexion();
			System.out.println("Closed  ? " + aServerConnexion.isClosed());
			aServerConnexion.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(" Clear ... ");
	}

}
