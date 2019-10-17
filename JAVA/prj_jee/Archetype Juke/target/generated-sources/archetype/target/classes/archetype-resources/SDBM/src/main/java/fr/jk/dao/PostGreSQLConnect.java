#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package main.java.${package}.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class PostGreSQLConnect
{
    // Declare the JDBC objects.
    private static Connection connexion;

    private PostGreSQLConnect()
    {
        try
        {
            Class.forName("org.postgresql.Driver");
            String dbURL = "jdbc:postgresql://localhost:5432/progica";
            String user = "Julien";
            String passwd = "filrouge";
            connexion = DriverManager.getConnection(dbURL, user, passwd);

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
            new PostGreSQLConnect();
        return connexion;
    }

}
