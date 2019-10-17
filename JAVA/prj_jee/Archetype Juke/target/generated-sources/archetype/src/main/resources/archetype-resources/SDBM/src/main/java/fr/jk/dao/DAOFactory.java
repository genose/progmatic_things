#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package main.java.${package}.dao;

import java.sql.Connection;

public class DAOFactory
{

    private static Connection connexionMsSQL = MsSQLConnect.getInstance();

    //Connections aux DAO a définir

    private static Connection connexionPGSQL = PostGreSQLConnect.getInstance();
}