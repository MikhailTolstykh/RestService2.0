package ru.tolstykh.util;

import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    public DatabaseConnection() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static java.sql.Connection getConnectionToDataBase() throws SQLException, ClassNotFoundException {
        // Использование DatabaseConfig для получения параметров конфигурации
        final String url = DatabaseConfig.getProperty("db.url");
        final String username = DatabaseConfig.getProperty("db.username");
        final String password = DatabaseConfig.getProperty("db.password");

        if (url == null || username == null || password == null) {
            throw new RuntimeException("Missing database configuration properties");
        }

        // Загрузка драйвера PostgreSQL
        Class.forName("org.postgresql.Driver");

        // Возвращение соединения с базой данных
        return DriverManager.getConnection(url, username, password);
    }
}
