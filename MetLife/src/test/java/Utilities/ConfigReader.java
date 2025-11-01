package Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
public class ConfigReader {

    private static Properties properties;

    static {
        try {
            // Load the properties file once when the class is loaded
        	String rootPath = System.getProperty("user.dir");
            FileInputStream fis = new FileInputStream(rootPath+File.separator+"Config"+File.separator+"config.properties");
            properties = new Properties();
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load config.properties file.");
        }
    }

    // Utility method to get property value
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}

