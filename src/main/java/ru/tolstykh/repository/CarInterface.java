package ru.tolstykh.repository;

import ru.tolstykh.entity.Car;
import ru.tolstykh.entity.Mechanic;

import java.sql.SQLException;
import java.util.List;

public interface CarInterface {
    void addCar(Car car) throws SQLException;

    Car getCarById(int id) throws SQLException;

    void updateCar(Car car) throws SQLException;

    void deleteCar(int id) throws SQLException;

    List<Car> getAllCars() throws SQLException;

    List<Mechanic> getMechanicsByCarId(int carId) throws SQLException;


}
