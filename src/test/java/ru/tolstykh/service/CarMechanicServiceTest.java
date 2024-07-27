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


    @Test
    void shouldCloseConnection() throws SQLException {
        // Создаем мок для CarMechanicRepository
        CarMechanicRepository repository = mock(CarMechanicRepository.class);

        // Создаем мок для Connection и для его метода close
        Connection connection = mock(Connection.class);
        doNothing().when(connection).close();

        // Создаем экземпляр CarMechanicService
        CarMechanicService service = new CarMechanicService(connection, repository);

        // Закрываем соединение с помощью сервиса
        service.close();

        // Проверяем, что метод close был вызван
        verify(connection, times(1)).close();
    }

    @Test
    void shouldNotThrowExceptionWhenCloseIsCalledTwice() throws SQLException {

        CarMechanicRepository repository = mock(CarMechanicRepository.class);


        Connection connection = mock(Connection.class);
        doNothing().when(connection).close();

        CarMechanicService service = new CarMechanicService(connection, repository);





        service.close();


        verify(connection, times(1)).close();
    }

    @Test
    void shouldThrowExceptionWhenConnectionIsNull() {

        CarMechanicRepository repository = mock(CarMechanicRepository.class);


        Exception exception = assertThrows(IllegalArgumentException.class, () -> new CarMechanicService(null, repository));
        assertEquals("Connection and repository cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRepositoryIsNull() throws SQLException {

        Connection connection = mock(Connection.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new CarMechanicService(connection, null));
        assertEquals("Connection and repository cannot be null", exception.getMessage());
    }


    @Test
    void shouldCloseConnectionWhenNotNullAndNotClosed() throws SQLException {

        when(mockConnection.isClosed()).thenReturn(false);
        service = new CarMechanicService(mockConnection,mockRepository );


        service.close();


        verify(mockConnection, times(1)).close();
    }

    @Test
    void shouldNotCloseConnectionWhenNotNullAndClosed() throws SQLException {

        when(mockConnection.isClosed()).thenReturn(true);
        service = new CarMechanicService(mockConnection,mockRepository );


        service.close();


        verify(mockConnection, times(0)).close();
    }
}




