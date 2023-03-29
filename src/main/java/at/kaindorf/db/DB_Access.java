package at.kaindorf.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DB_Access {
    private static DB_Access theInstance;
    private DB_Database database = DB_Database.getInstance();
    private Connection connection = database.getConnection();

    private DB_Access() throws SQLException, ClassNotFoundException {
    }

    public static DB_Access getInstance() throws SQLException, ClassNotFoundException {
        if (theInstance == null) {
            theInstance = new DB_Access();
        }
        return theInstance;
    }


}
