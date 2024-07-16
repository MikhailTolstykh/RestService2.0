package service;

import entity.Car;
import repository.CarInterface;
import repository.CarRepository;

import java.sql.SQLException;
import java.util.List;

public class CarService implements CarServiceInterface {
    private final CarInterface carRepository;

    public CarService() {  this.carRepository = new CarRepository();
    }

    public CarService(CarInterface carRepository) {
        this.carRepository = carRepository;
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

