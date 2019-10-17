#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package main.java.${package}.dao;

import java.sql.Connection;
import java.util.ArrayList;

public abstract class DAO<T>
{

    protected Connection connexion;

    public DAO(Connection connexion)
    {
        this.connexion = connexion;
    }

    public abstract T getByID(int id);
    public abstract ArrayList<T> getAll();
    public abstract boolean insert (T object);
    public abstract boolean delete(T object);
    public abstract boolean update(T object);

}