package at.kaindorf.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

public class DB_Properties {
    private static final Properties PROPERTIES = new Properties();

   /* db_url = jdbc:postgresql://localhost:5432/sweatmatedb
    db_driver = org.postgresql.Driver
            db_username = postgres
    db_password = postgres*/
    static {
        PROPERTIES.setProperty("db_url", "jdbc:postgresql://localhost:5432/sweatmatedb");
        PROPERTIES.setProperty("db_driver", "org.postgresql.Driver");
        PROPERTIES.setProperty("db_username", "postgres");
        PROPERTIES.setProperty("db_password", "postgres");
   }

    /*
    static {
       // Path filepath = Path.of(System.getProperty("user.dir"), "src", "main", "resources", "database.properties");
        InputStream is =ClassLoader.class.getResourceAsStream("database.properties");
        try {
            FileInputStream fis = new FileInputStream(is.toString());
            PROPERTIES.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    */


    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }
}
