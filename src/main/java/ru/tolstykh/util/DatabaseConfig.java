package ru.tolstykh.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("File database.properties not found in classpath.");
            } else {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database.properties", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static void loadFromFile(String filePath) {
        try (InputStream input = Files.newInputStream(Paths.get(filePath))) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database.properties from file: " + filePath, e);
        }
    }
}
