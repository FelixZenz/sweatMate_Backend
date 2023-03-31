package at.kaindorf.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_Database {
    private static DB_Database theInstance;
    private Connection connection;
    private DB_CachedConnection cachedConnection;

    public static DB_Database getInstance() throws SQLException, ClassNotFoundException {
        if (theInstance == null) {
            theInstance = new DB_Database();
        }
        return theInstance;
    }

    private String db_url = DB_Properties.getProperty("db_url");
    private String db_driver = DB_Properties.getProperty("db_driver");
    private String db_username = DB_Properties.getProperty("db_username");
    private String db_password = DB_Properties.getProperty("db_password");

    private DB_Database() throws SQLException, ClassNotFoundException {
        Class.forName(db_driver);
        connect();
        cachedConnection = new DB_CachedConnection(connection);
    }

    private void connect() throws SQLException {
        disconnect();
        connection = DriverManager.getConnection(db_url, db_username, db_password);
    }

    private void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() throws SQLException {
        return cachedConnection.getStatement();
    }

    public void releaseStatement(Statement statement) {
        cachedConnection.releaseStatement(statement);
    }
}
