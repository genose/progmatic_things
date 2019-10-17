package main.java.fr.jk.dao;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import java.sql.Connection;

public class MsSQLConnect
{
    // Declare the JDBC objects.
    private static Connection connexion;

    private MsSQLConnect()
    {
        try
        {
//			String dbURL = "jdbc:sqlserver://STA7400527:1433;database=sdbm";
//			String user = "javaSDBM";
//			String pass = "javaSDBM";
//			connexion = DriverManager.getConnection(dbURL, user, pass);

            SQLServerDataSource ds = new SQLServerDataSource();
            ds.setServerName("server name");
            ds.setPortNumber(1433);
            ds.setDatabaseName("nom base de données");
            ds.setIntegratedSecurity(false);
            ds.setUser("UserJava");
            ds.setPassword("mdp");
            connexion = ds.getConnection();
        }

        // Handle any errors that may have occurred.
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static synchronized Connection getInstance()
    {
        if (connexion == null)
            new MsSQLConnect();
        return connexion;
    }
    
    
}
