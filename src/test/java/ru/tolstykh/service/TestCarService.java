package ru.tolstykh.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import ru.tolstykh.entity.Car;
import ru.tolstykh.repository.CarInterface;


class TestCarService {

    @Mock
    private CarInterface carRepository;

    @InjectMocks
    private CarService carService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testAddCar() throws SQLException {
        Car car = new Car();
        carService.addCar(car);
        verify(carRepository, times(1)).addCar(car);
    }

    @Test
     void testGetCarById() throws SQLException {
        Car car = new Car();
        when(carRepository.getCarById(1)).thenReturn(car);

        Car result = carService.getCarById(1);
        assertEquals(car, result);
    }

    @Test
     void testUpdateCar() throws SQLException {
        Car car = new Car();
        carService.updateCar(car);
        verify(carRepository, times(1)).updateCar(car);
    }

    @Test
    void testDeleteCar() throws SQLException {
        carService.deleteCar(1);
        verify(carRepository, times(1)).deleteCar(1);
    }

    @Test
     void testGetAllCars() throws SQLException {
        Car car1 = new Car();
        Car car2 = new Car();
        List<Car> cars = Arrays.asList(car1, car2);
        when(carRepository.getAllCars()).thenReturn(cars);

        List<Car> result = carService.getAllCars();
        assertEquals(2, result.size());
        assertEquals(car1, result.get(0));
        assertEquals(car2, result.get(1));
    }
}
