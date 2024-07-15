package repository;

import entity.Car;
import entity.Mechanic;

import java.sql.SQLException;
import java.util.List;

public interface CarInterface {
    void addCar(Car car) throws SQLException;

    Car getCarById(int id) throws SQLException;

    void updateCar(Car car) throws SQLException;

    void deleteCar(int id) throws SQLException;

    List<Car> getAllCars() throws SQLException;

    List<Mechanic> getMechanicsByCarId(int carId) throws SQLException;

    void addCarMechanic(int carId, int mechanicId) throws SQLException;

    void deleteCarMechanics(int carId) throws SQLException;

}
