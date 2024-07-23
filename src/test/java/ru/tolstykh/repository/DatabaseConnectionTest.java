package ru.tolstykh.repository;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class DatabaseConnectionTest {



    @Test
    public void testGetConnectionToDataBase() throws SQLException, ClassNotFoundException {

        try (Connection connection = ru.tolstykh.util.DatabaseConnection.getConnectionToDataBase()) {
            assertNotNull(connection, "Connection should not be null");
            assertTrue(connection.isValid(2), "Connection should be valid");
        }
    }



}




