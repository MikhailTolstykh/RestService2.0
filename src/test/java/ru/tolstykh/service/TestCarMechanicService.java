package ru.tolstykh.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.tolstykh.repository.CarMechanicRepository;
import ru.tolstykh.service.CarMechanicService;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestCarMechanicService {

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
        try {
            new CarMechanicService(null, mockRepository);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Connection and repository cannot be null", e.getMessage());
        }
    }

    @Test
    public void testConstructor_ThrowsException_WhenRepositoryIsNull() {
        try {
            new CarMechanicService(mockConnection, null);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Connection and repository cannot be null", e.getMessage());
        }
    }

    @Test
    public void testAddCarMechanic() throws SQLException {
        int carId = 1;
        int mechanicId = 2;

        // Выполняем метод
        service.addCarMechanic(carId, mechanicId);

        // Проверяем, что метод репозитория был вызван с правильными аргументами
        verify(mockRepository).addCarMechanic(carId, mechanicId);
    }

    @Test
    public void testRemoveCarMechanic() throws SQLException {
        int carId = 1;
        int mechanicId = 2;

        // Выполняем метод
        service.removeCarMechanic(carId, mechanicId);

        // Проверяем, что метод репозитория был вызван с правильными аргументами
        verify(mockRepository).removeCarMechanic(carId, mechanicId);
    }

    @Test
    public void testDeleteCarMechanics() throws SQLException {
        int carId = 1;

        // Выполняем метод
        service.deleteCarMechanics(carId);

        // Проверяем, что метод репозитория был вызван с правильными аргументами
        verify(mockRepository).deleteCarMechanics(carId);
    }

    @Test
    public void testClose() throws SQLException {
        // Устанавливаем поведение мока для соединения
        when(mockConnection.isClosed()).thenReturn(false);

        // Выполняем метод
        service.close();

        // Проверяем, что соединение было закрыто
        verify(mockConnection).close();
    }

    @Test
    public void testClose_WhenConnectionIsAlreadyClosed() throws SQLException {
        // Устанавливаем поведение мока для соединения
        when(mockConnection.isClosed()).thenReturn(true);

        // Выполняем метод
        service.close();

        // Проверяем, что метод close у соединения не был вызван, так как соединение уже закрыто
        verify(mockConnection, never()).close();
    }
}
