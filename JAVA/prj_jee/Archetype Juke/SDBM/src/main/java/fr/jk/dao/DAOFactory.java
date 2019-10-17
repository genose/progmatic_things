package main.java.fr.jk.dao;

import java.sql.Connection;

public class DAOFactory
{

    private static Connection connexionMsSQL = MsSQLConnect.getInstance();

    //Connections aux DAO a définir

    private static Connection connexionPGSQL = PostGreSQLConnect.getInstance();
}