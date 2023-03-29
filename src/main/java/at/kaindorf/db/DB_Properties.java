package at.kaindorf.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class DB_Properties {
    private static final Properties PROPERTIES = new Properties();

    static {
        Path filepath = Path.of(System.getProperty("user.dir"), "src", "main", "resources", "database.properties");
        try {
            FileInputStream fis = new FileInputStream(filepath.toFile());
            PROPERTIES.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }
}
