package ru.tolstykh.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


    public final class DatabaseConnection {

        private DatabaseConnection() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }

        public static java.sql.Connection getConnectionToDataBase() throws SQLException, ClassNotFoundException {
            Properties props = new Properties();


            try (InputStream input = Files.newInputStream(Paths.get("C:\\Users\\User\\IdeaProjects\\RestService2.0\\src\\main\\resources\\database.properties"))) {
                props.load(input);
            } catch (Exception e) {
                System.out.println(e.getMessage()+e.getStackTrace());
                throw new RuntimeException("ошибка загрузки файла", e);
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

