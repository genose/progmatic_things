package com.example.crm.common;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * DataSource minimaliste basé sur DriverManager.
 *
 * Permet d'instancier les adapters JDBC sans dépendance à un pool
 * de connexions (HikariCP, DBCP, etc.).
 *
 * Usage :
 *   DataSource ds = new DriverManagerDataSource(
 *       "oracle.rdb.jdbc.rdbThin.Driver",
 *       "jdbc:rdb://hostname/BD_DEPOT",
 *       "user", "password");
 *
 * Note : chaque appel à getConnection() crée une nouvelle connexion physique.
 * Adapter ce composant par un vrai pool en environnement de production.
 */
public class DriverManagerDataSource implements DataSource {

    private final String url;
    private final String username;
    private final String password;

    /**
     * @param driverClass  classe JDBC à charger (ex : "oracle.rdb.jdbc.rdbThin.Driver")
     * @param url          URL JDBC (ex : "jdbc:rdb://host/BD_DEPOT")
     * @param username     utilisateur base de données
     * @param password     mot de passe
     */
    public DriverManagerDataSource(String driverClass, String url,
                                   String username, String password) {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC introuvable : " + driverClass, e);
        }
        this.url      = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public Connection getConnection(String user, String pass) throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    // ── Méthodes de l'interface non utilisées ────────────────────────────────

    @Override public PrintWriter getLogWriter()                           { return null; }
    @Override public void setLogWriter(PrintWriter out)                   {}
    @Override public void setLoginTimeout(int seconds)                    {}
    @Override public int  getLoginTimeout()                               { return 0; }
    @Override public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
    @Override public <T> T unwrap(Class<T> iface) throws SQLException     { throw new SQLException("not a wrapper"); }
    @Override public boolean isWrapperFor(Class<?> iface)                 { return false; }
}
