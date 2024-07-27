package ru.tolstykh.service;

import ru.tolstykh.entity.Car;
import ru.tolstykh.repository.CarInterface;
import ru.tolstykh.repository.CarRepository;

import java.sql.SQLException;
import java.util.List;

public class CarService implements CarServiceInterface {
    private CarInterface carRepository;


    public CarService(CarInterface carRepository) {
        this.carRepository = carRepository;
    }

    public CarService() {

    }


    @Override
    public void addCar(Car car) throws SQLException {
        carRepository.addCar(car);
    }

    @Override
    public Car getCarById(int id) throws SQLException {
        return carRepository.getCarById(id);
    }

    @Override
    public void updateCar(Car car) throws SQLException {
        carRepository.updateCar(car);
    }

    @Override
    public void deleteCar(int id) throws SQLException {
        carRepository.deleteCar(id);
    }

    @Override
    public List<Car> getAllCars() throws SQLException {
        return carRepository.getAllCars();
    }
}

