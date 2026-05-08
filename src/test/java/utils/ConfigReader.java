package utils;

import java.io.FileInputStream;
import java.util.Properties;

// Reads values from src/test/resources/config.properties
public class ConfigReader {

    private static Properties props = new Properties();

    // This block runs once when the class is first used. It loads the file.
    static {
        try {
            FileInputStream file = new FileInputStream("src/test/resources/config.properties");
            props.load(file);
            file.close();
        } catch (Exception e) {
            throw new RuntimeException("Could not load config.properties: " + e.getMessage());
        }
    }

    // Use this anywhere: ConfigReader.get("url")
    public static String get(String key) {
        return props.getProperty(key);
    }
}
