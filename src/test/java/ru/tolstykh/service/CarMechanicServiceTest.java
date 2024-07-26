package ru.tolstykh.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tolstykh.repository.CarMechanicRepository;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarMechanicServiceTest {

    private CarMechanicRepository mockRepository;
    private Connection mockConnection;
    private CarMechanicService service;

    @BeforeEach
    public void setUp() {
        // Создаем моки для зависимостей
        mockRepository = mock(CarMechanicRepository.class);
        mockConnection = mock(Connection.class);
        // Инициализируем сервис
        service = new CarMechanicService(mockConnection, mockRepository);
    }

    @Test
    public void testConstructor_ThrowsException_WhenConnectionIsNull() {
        // Arrange & Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new CarMechanicService(null, mockRepository));
        assertEquals("Connection and repository cannot be null", thrown.getMessage());
    }

    @Test
    public void testConstructor_ThrowsException_WhenRepositoryIsNull() {
        // Arrange & Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new CarMechanicService(mockConnection, null));
        assertEquals("Connection and repository cannot be null", thrown.getMessage());
    }

    @Test
    public void testAddCarMechanic() throws SQLException {
        // Arrange
        int carId = 1;
        int mechanicId = 2;

        // Act
        service.addCarMechanic(carId, mechanicId);

        // Assert
        verify(mockRepository).addCarMechanic(carId, mechanicId);
    }

    @Test
    public void testRemoveCarMechanic() throws SQLException {
        // Arrange
        int carId = 1;
        int mechanicId = 2;

        // Act
        service.removeCarMechanic(carId, mechanicId);

        // Assert
        verify(mockRepository).removeCarMechanic(carId, mechanicId);
    }

    @Test
    public void testDeleteCarMechanics() throws SQLException {
        // Arrange
        int carId = 1;

        // Act
        service.deleteCarMechanics(carId);

        // Assert
        verify(mockRepository).deleteCarMechanics(carId);
    }

    @Test
    void shouldCloseConnectionWhenNotNullAndNotClosed() throws SQLException {
        // Arrange
        when(mockConnection.isClosed()).thenReturn(false);

        // Act
        service.close();

        // Assert
        verify(mockConnection).close();
    }

    @Test
    void shouldNotCloseConnectionWhenAlreadyClosed() throws SQLException {
        // Arrange
        when(mockConnection.isClosed()).thenReturn(true);

        // Act
        service.close();

        // Assert
        verify(mockConnection, never()).close();
    }

    @Test
    void shouldHandleSQLExceptionWhenClosingConnection() throws SQLException {
        // Arrange
        when(mockConnection.isClosed()).thenReturn(false);
        doThrow(new SQLException("Connection close error")).when(mockConnection).close();

        // Act & Assert
        SQLException thrown = assertThrows(SQLException.class, () -> service.close());
        assertEquals("Connection close error", thrown.getMessage());
    }



    @Test
    void shouldHandleSQLExceptionWhenClosingConnectionAndNotNull() throws SQLException {
        // Arrange
        when(mockConnection.isClosed()).thenReturn(false);
        doThrow(new SQLException("Connection close error")).when(mockConnection).close();

        // Act & Assert
        SQLException thrown = assertThrows(SQLException.class, () -> service.close());
        assertEquals("Connection close error", thrown.getMessage());
    }

    @Test
    void shouldNotThrowExceptionIfConnectionIsAlreadyClosedAndCloseFails() throws SQLException {
        // Arrange: setup mock behavior
        when(mockConnection.isClosed()).thenReturn(true);

        // Act & Assert: call the close method and ensure no exception is thrown
        service.close();

        // Verify that connection.close() was not called
        verify(mockConnection, never()).close();
    }

}

