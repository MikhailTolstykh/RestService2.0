package repository;

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
//        try (InputStream input = Files.newInputStream(Paths.get("src/main/resources/database.properties"))) {
        try (InputStream input = Files.newInputStream(Paths.get(" C:\\Users\\User\\IdeaProjects\\RestService\\target\\classes\\database.property"))) {
            props.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String url = props.getProperty("url");
        final String username = props.getProperty("username");
        final String password = props.getProperty("password");
        Class.forName("org.postgresql.Driver");
//        final String url = "jdbc:postgresql://localhost:5432/aston-dev-test";
//        final String username = "postgres";
//        final String password = "password";
        return DriverManager.getConnection(url, username, password);
    }

}