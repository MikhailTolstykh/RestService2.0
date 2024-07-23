package ru.tolstykh.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tolstykh.entity.Car;
import ru.tolstykh.entity.Mechanic;
import ru.tolstykh.repository.MechanicInterface;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestMechanicService{

    @Mock
    private MechanicInterface mechanicRepository;

    @InjectMocks
    private MechanicService mechanicService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddMechanic() throws SQLException {
        Mechanic mechanic = new Mechanic();
        mechanic.setName("John Doe");

        mechanicService.addMechanic(mechanic);

        verify(mechanicRepository, times(1)).addMechanic(mechanic);
    }

    @Test
    void testGetMechanicById() throws SQLException {
        int mechanicId = 1;
        Mechanic mechanic = new Mechanic(mechanicId, "John Doe");
        List<Car> cars = Arrays.asList(new Car(1, "Toyota", 1), new Car(2, "Honda", 2));

        when(mechanicRepository.getMechanicById(mechanicId)).thenReturn(mechanic);
        when(mechanicRepository.getCarsByMechanicId(mechanicId)).thenReturn(cars);

        Mechanic result = mechanicService.getMechanicById(mechanicId);

        assertEquals(mechanicId, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals(cars, result.getCars());

        verify(mechanicRepository, times(1)).getMechanicById(mechanicId);
        verify(mechanicRepository, times(1)).getCarsByMechanicId(mechanicId);
    }

    @Test
    void testUpdateMechanic() throws SQLException {
        Mechanic mechanic = new Mechanic();
        mechanic.setId(1);
        mechanic.setName("John Doe Updated");

        mechanicService.updateMechanic(mechanic);

        verify(mechanicRepository, times(1)).updateMechanic(mechanic);
    }

    @Test
    void testDeleteMechanic() throws SQLException {
        int mechanicId = 1;

        mechanicService.deleteMechanic(mechanicId);

        verify(mechanicRepository, times(1)).deleteMechanic(mechanicId);
    }

    @Test
    void testGetAllMechanics() throws SQLException {
        Mechanic mechanic1 = new Mechanic(1, "John Doe");
        Mechanic mechanic2 = new Mechanic(2, "Jane Doe");
        List<Mechanic> mechanics = Arrays.asList(mechanic1, mechanic2);
        List<Car> cars1 = Arrays.asList(new Car(1, "Toyota", 1));
        List<Car> cars2 = Arrays.asList(new Car(2, "Honda", 2));

        when(mechanicRepository.getAllMechanics()).thenReturn(mechanics);
        when(mechanicRepository.getCarsByMechanicId(1)).thenReturn(cars1);
        when(mechanicRepository.getCarsByMechanicId(2)).thenReturn(cars2);

        List<Mechanic> result = mechanicService.getAllMechanics();

        assertEquals(2, result.size());
        assertEquals(cars1, result.get(0).getCars());
        assertEquals(cars2, result.get(1).getCars());

        verify(mechanicRepository, times(1)).getAllMechanics();
        verify(mechanicRepository, times(1)).getCarsByMechanicId(1);
        verify(mechanicRepository, times(1)).getCarsByMechanicId(2);
    }

    @Test
    void testGetCarsByMechanicId() throws SQLException {
        int mechanicId = 1;
        List<Car> cars = Arrays.asList(new Car(1, "Toyota", 1), new Car(2, "Honda", 2));

        when(mechanicRepository.getCarsByMechanicId(mechanicId)).thenReturn(cars);

        List<Car> result = mechanicService.getCarsByMechanicId(mechanicId);

        assertEquals(cars, result);

        verify(mechanicRepository, times(1)).getCarsByMechanicId(mechanicId);
    }

    @Test
    void testGetMechanicsByCarId() throws SQLException {
        int carId = 1;
        Mechanic mechanic1 = new Mechanic(1, "John Doe");
        Mechanic mechanic2 = new Mechanic(2, "Jane Doe");
        List<Mechanic> mechanics = Arrays.asList(mechanic1, mechanic2);

        when(mechanicRepository.getMechanicsByCarId(carId)).thenReturn(mechanics);

        List<Mechanic> result = mechanicService.getMechanicsByCarId(carId);

        assertEquals(mechanics, result);

        verify(mechanicRepository, times(1)).getMechanicsByCarId(carId);
    }

    @Test
    void testGetMechanicsByCarIdThrowsSQLException() throws SQLException {
        int carId = 1;


        when(mechanicRepository.getMechanicsByCarId(carId)).thenThrow(new SQLException("Database error"));


        SQLException thrown = assertThrows(SQLException.class, () -> {
            mechanicService.getMechanicsByCarId(carId);
        });


        assertEquals("Database error", thrown.getMessage());


        verify(mechanicRepository, times(1)).getMechanicsByCarId(carId);
    }


}
