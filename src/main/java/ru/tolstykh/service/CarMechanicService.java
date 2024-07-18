package ru.tolstykh.service;

import ru.tolstykh.repository.CarMechanicRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class CarMechanicService implements CarMechanicServiceInterface {

    private CarMechanicRepository repository;

    public CarMechanicService(Connection connection) {
        this.repository = new CarMechanicRepository(connection);
    }
    @Override
    public void addCarMechanic(int carId, int mechanicId) throws SQLException {
        repository.addCarMechanic(carId, mechanicId);

    }

    @Override
    public void removeCarMechanic(int carId, int mechanicId) throws SQLException {
        repository.removeCarMechanic(carId, mechanicId);


    }

    @Override
    public void deleteCarMechanics(int carId) throws SQLException {

        repository.deleteCarMechanics(carId);

    }

     @Override
     public void close () throws SQLException{
        repository.close();
     }


}
