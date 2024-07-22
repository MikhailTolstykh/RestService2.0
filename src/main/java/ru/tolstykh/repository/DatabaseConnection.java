package ru.tolstykh.repository;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {

    private DatabaseConnection() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Connection getConnectionToDataBase() throws SQLException, ClassNotFoundException {
        Properties props = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get("src/main/resources/database.properties"))) {
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database properties file", e);
        }

        final String url = props.getProperty("db.url");
        final String username = props.getProperty("db.username");
        final String password = props.getProperty("db.password");

        if (url == null || username == null || password == null) {
            throw new RuntimeException("Missing database configuration properties");
        }

        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(url, username, password);
    }
}