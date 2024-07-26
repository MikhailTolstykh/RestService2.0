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

        mockRepository = mock(CarMechanicRepository.class);
        mockConnection = mock(Connection.class);

        service = new CarMechanicService(mockConnection, mockRepository);
    }

    @Test
    void testConstructor_ThrowsException_WhenConnectionIsNull() {

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new CarMechanicService(null, mockRepository));
        assertEquals("Connection and repository cannot be null", thrown.getMessage());
    }

    @Test
    void testConstructor_ThrowsException_WhenRepositoryIsNull() {

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new CarMechanicService(mockConnection, null));
        assertEquals("Connection and repository cannot be null", thrown.getMessage());
    }

    @Test
   void testAddCarMechanic() throws SQLException {
        // Arrange
        int carId = 1;
        int mechanicId = 2;


        service.addCarMechanic(carId, mechanicId);


        verify(mockRepository).addCarMechanic(carId, mechanicId);
    }

    @Test
     void testRemoveCarMechanic() throws SQLException {

        int carId = 1;
        int mechanicId = 2;

        service.removeCarMechanic(carId, mechanicId);

        verify(mockRepository).removeCarMechanic(carId, mechanicId);
    }

    @Test
     void testDeleteCarMechanics() throws SQLException {

        int carId = 1;


        service.deleteCarMechanics(carId);

        verify(mockRepository).deleteCarMechanics(carId);
    }

    @Test
    void shouldCloseConnectionWhenNotNullAndNotClosed() throws SQLException {

        when(mockConnection.isClosed()).thenReturn(false);


        service.close();


        verify(mockConnection).close();
    }

    @Test
    void shouldNotCloseConnectionWhenAlreadyClosed() throws SQLException {

        when(mockConnection.isClosed()).thenReturn(true);


        service.close();


        verify(mockConnection, never()).close();
    }

    @Test
    void shouldHandleSQLExceptionWhenClosingConnection() throws SQLException {

        when(mockConnection.isClosed()).thenReturn(false);
        doThrow(new SQLException("Connection close error")).when(mockConnection).close();


        SQLException thrown = assertThrows(SQLException.class, () -> service.close());
        assertEquals("Connection close error", thrown.getMessage());
    }



    @Test
    void dHandleSQLExceptionWhenClosingConnectionAndNotNull() throws SQLException {

        when(mockConnection.isClosed()).thenReturn(false);
        doThrow(new SQLException("Connection close error")).when(mockConnection).close();


        SQLException thrown = assertThrows(SQLException.class, () -> service.close());
        assertEquals("Connection close error", thrown.getMessage());
    }

    @Test
    void NotThrowExceptionIfConnectionIsAlreadyClosedAndCloseFails() throws SQLException {

        when(mockConnection.isClosed()).thenReturn(true);

        service.close();

        verify(mockConnection, never()).close();
    }

}

