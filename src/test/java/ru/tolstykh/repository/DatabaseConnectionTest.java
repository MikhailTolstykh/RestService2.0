package ru.tolstykh.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatabaseConnectionTest {


    @Test
    public void testGetConnectionToDataBase() throws SQLException, ClassNotFoundException {
        // Test the connection to the database
        try (Connection connection = DatabaseConnection.getConnectionToDataBase()) {
            assertNotNull(connection, "Connection should not be null");
            assertTrue(connection.isValid(2), "Connection should be valid");
        }
    }

}


