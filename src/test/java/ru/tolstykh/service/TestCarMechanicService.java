package ru.tolstykh.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.tolstykh.repository.CarMechanicRepository;

import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

class TestCarMechanicService {

    private CarMechanicRepository mockRepository;
    private Connection mockConnection;
    private CarMechanicService carMechanicService;

    @BeforeEach
    void setUp() {
        // Создаем замокированные объекты
        mockConnection = mock(Connection.class);
        mockRepository = mock(CarMechanicRepository.class);

        // Создаем экземпляр CarMechanicService с замокированным репозиторием
        carMechanicService = new CarMechanicService(mockConnection, mockRepository);
    }

    @Test
    void testAddCarMechanic() throws SQLException {
        // Выполняем метод
        carMechanicService.addCarMechanic(1, 2);

        // Проверяем, что метод был вызван с правильными аргументами
        verify(mockRepository).addCarMechanic(1, 2);
    }

    @Test
    void testRemoveCarMechanic() throws SQLException {
        // Выполняем метод
        carMechanicService.removeCarMechanic(1, 2);

        // Проверяем, что метод был вызван с правильными аргументами
        verify(mockRepository).removeCarMechanic(1, 2);
    }

    @Test
    void testDeleteCarMechanics() throws SQLException {
        // Выполняем метод
        carMechanicService.deleteCarMechanics(1);

        // Проверяем, что метод был вызван с правильными аргументами
        verify(mockRepository).deleteCarMechanics(1);
    }

    @Test
    void testClose() throws SQLException {
        // Выполняем метод
        carMechanicService.close();

        // Проверяем, что метод close() был вызван на mockConnection
        verify(mockConnection).close();
    }
}
