package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

public class SDBMConnect {
	// Declare the JDBC objects.
	private static Connection connexion = null;
	private static String sConnectionHost = null;
	public final String sERRMESSAGEDAO = "ERROR : Impossible d'utiliser une connexion NULL ";

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

			connexion = ds.getConnection();
			Objects.requireNonNull(connexion, sERRMESSAGEDAO);
		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized Connection getInstance() {
		if (connexion == null)
			new SDBMConnect();
		return connexion;
	}

	/**
	 * @return the connexion
	 */
	public static Connection getConnexion() {
		return connexion;
	}

	/**
	 * @param connexion the connexion to set
	 */
	public static void setConnexion(Connection connexion) {
		SDBMConnect.connexion = connexion;
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
		SDBMConnect.sConnectionHost = sConnectionHost;
	}

	public static void main(String[] args) {
		System.out.println("Test connexion ...");
		try {
			SDBMConnect aBDDConnexion = new SDBMConnect();

			Objects.requireNonNull(aBDDConnexion,
					"Connection au server " + String.valueOf(SDBMConnect.sConnectionHost));
			System.out.println(" Result : " + aBDDConnexion);
			Connection aServerConnexion = aBDDConnexion.getInstance();
			System.out.println("Closed  ? " + aServerConnexion.isClosed());
			aServerConnexion.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(" Clear ... ");
	}

}
