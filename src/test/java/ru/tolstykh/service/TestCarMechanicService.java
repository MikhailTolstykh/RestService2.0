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

        mockConnection = mock(Connection.class);
        mockRepository = mock(CarMechanicRepository.class);


        carMechanicService = new CarMechanicService(mockConnection, mockRepository);
    }

    @Test
    void testAddCarMechanic() throws SQLException {

        carMechanicService.addCarMechanic(1, 2);


        verify(mockRepository).addCarMechanic(1, 2);
    }

    @Test
    void testRemoveCarMechanic() throws SQLException {

        carMechanicService.removeCarMechanic(1, 2);

        verify(mockRepository).removeCarMechanic(1, 2);
    }

    @Test
    void testDeleteCarMechanics() throws SQLException {

        carMechanicService.deleteCarMechanics(1);

        verify(mockRepository).deleteCarMechanics(1);
    }

    @Test
    void testClose() throws SQLException {

        carMechanicService.close();

        verify(mockConnection).close();
    }
}
