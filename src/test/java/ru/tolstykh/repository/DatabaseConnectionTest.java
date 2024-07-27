package ru.tolstykh.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tolstykh.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class DatabaseConnectionTest {

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    @BeforeAll
    public static void setUp() {

        System.setProperty("db.url", postgresContainer.getJdbcUrl());
        System.setProperty("db.username", postgresContainer.getUsername());
        System.setProperty("db.password", postgresContainer.getPassword());
    }

    @AfterAll
    public static void tearDown() {

        System.clearProperty("db.url");
        System.clearProperty("db.username");
        System.clearProperty("db.password");
    }

    @Test
    public void testGetConnectionToDataBase() throws SQLException, ClassNotFoundException {
        try (Connection connection = DatabaseConnection.getConnectionToDataBase()) {
            assertNotNull(connection, "Connection should not be null");
            assertTrue(connection.isValid(2), "Connection should be valid");
        }
    }
}
