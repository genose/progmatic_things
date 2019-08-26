package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

public class SDBMConnect implements DAOProvider {
	// Declare the JDBC objects.
	private static Connection aLServerConnexion = null;
	private static SDBMConnect aServerInstanceConnexion = null;
	private static String sConnectionHost = null;
	
	public SDBMConnect() {
		try {
			sConnectionHost = java.net.InetAddress.getLocalHost().getHostName();
//			String dbURL = "jdbc:sqlserver://DESKTOP-FCU3LC0:1433;databaseName=SDBM";
//			String user = "javaSDBM";
//			String pass = "javaSDBM";
//			connexion = DriverManager.getConnection(dbURL, user, pass);

			System.out.println("try connection on " + sConnectionHost);

			SQLServerDataSource ds = new SQLServerDataSource();

			ds.setServerName(sConnectionHost);

			ds.setPortNumber(1433);

			ds.setDatabaseName("SDBM");

			ds.setIntegratedSecurity(false);

			ds.setUser("javasdbm");

			ds.setPassword("javasdbm");
			aServerInstanceConnexion = this;
			
			aLServerConnexion = ds.getConnection();
			Objects.requireNonNull(aLServerConnexion, sERRMESSAGEDAONULLCONNECT);
			
		}

		// Handle any errors that may have occurred.
		catch (Exception evERRSDBDCONNECTION) {
			evERRSDBDCONNECTION.printStackTrace();
			JFXApplicationException.raiseToFront(aServerInstanceConnexion.getClass(), evERRSDBDCONNECTION, true);
		}
	}

	public static synchronized SDBMConnect getInstance() {
		if (aServerInstanceConnexion == null)
			aServerInstanceConnexion = new SDBMConnect();
		
		return aServerInstanceConnexion;
	}

	/**
	 * @return the connexion
	 */
	public static Connection getConnexion() {
		return (getInstance()).aLServerConnexion;
	}

	/**
	 * @param connexion the connexion to set
	 */
	public static void setConnexion(Connection connexion) {
		(getInstance()).aLServerConnexion = connexion;
	}

	/**
	 * @return the sConnectionHost
	 */
	public static String getsConnectionHost() {
		return sConnectionHost;
	}

	/**
	 * @param sConnectionHost the sConnectionHost to set
	 */
	public static void setsConnectionHost(String sConnectionHost) {
		(getInstance()).sConnectionHost = sConnectionHost;
	}

	public static void main(String[] args) {
		System.out.println("Test connexion ...");
		try {
			SDBMConnect aBDDConnexion = new SDBMConnect();

			Objects.requireNonNull(aBDDConnexion,
					"Connection au server " + SDBMConnect.sConnectionHost);
			System.out.println(" Result : " + aBDDConnexion);
			Connection aServerConnexion = getConnexion();
			System.out.println("Closed  ? " + aServerConnexion.isClosed());
			aServerConnexion.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(" Clear ... ");
	}

}
