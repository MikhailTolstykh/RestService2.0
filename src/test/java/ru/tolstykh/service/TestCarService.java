package ru.tolstykh.service;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tolstykh.entity.Car;
import ru.tolstykh.repository.CarInterface;
import ru.tolstykh.service.CarService;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

 class CarServiceTest {

    private CarService carService;
    private CarInterface carRepositoryMock;

    @BeforeEach
    void setUp() {
        carRepositoryMock = mock(CarInterface.class);
        carService = new CarService(carRepositoryMock);
    }

    @Test
    void shouldAddCar() throws SQLException {
        Car car = new Car(1, "Model X", 1);

        // Выполнение действия в сервисе
        carService.addCar(car);

        // Проверяем, что метод addCar был вызван на мок-объекте carRepositoryMock
        verify(carRepositoryMock, times(1)).addCar(car);
    }

    @Test
    void shouldGetCarById() throws SQLException {
        Car car = new Car(1, "Model X", 1);
        when(carRepositoryMock.getCarById(1)).thenReturn(car);

        Car fetchedCar = carService.getCarById(1);

        verify(carRepositoryMock, times(1)).getCarById(1);
        assertNotNull(fetchedCar);
        assertEquals("Model X", fetchedCar.getModel());
    }

    @Test
    void shouldUpdateCar() throws SQLException {
        Car car = new Car(1, "Model X", 1);

        // Выполнение действия в сервисе
        carService.updateCar(car);

        // Проверяем, что метод updateCar был вызван на мок-объекте carRepositoryMock
        verify(carRepositoryMock, times(1)).updateCar(car);
    }

    @Test
    void shouldDeleteCar() throws SQLException {
        // Выполнение действия в сервисе
        carService.deleteCar(1);

        // Проверяем, что метод deleteCar был вызван на мок-объекте carRepositoryMock
        verify(carRepositoryMock, times(1)).deleteCar(1);
    }

    @Test
    void shouldGetAllCars() throws SQLException {
        Car car = new Car(1, "Model X", 1);
        when(carRepositoryMock.getAllCars()).thenReturn(Collections.singletonList(car));

        List<Car> cars = carService.getAllCars();

        verify(carRepositoryMock, times(1)).getAllCars();
        assertFalse(cars.isEmpty());
        assertEquals("Model X", cars.get(0).getModel());
    }


}
