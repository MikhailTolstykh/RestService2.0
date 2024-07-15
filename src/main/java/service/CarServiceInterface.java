package service;

import entity.Car;
import entity.Customer;

import java.sql.SQLException;
import java.util.List;

public interface CarServiceInterface {
    void addCar(Car car) throws SQLException;

    Car getCarById(int id) throws SQLException;

    void updateCar(Car car) throws SQLException;

    void deleteCar(int id) throws SQLException;

    List<Car> getAllCars() throws SQLException;
}
