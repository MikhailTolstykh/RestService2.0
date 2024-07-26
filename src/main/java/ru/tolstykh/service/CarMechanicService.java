package ru.tolstykh.service;

import ru.tolstykh.repository.CarMechanicRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class CarMechanicService implements CarMechanicServiceInterface {

    private final CarMechanicRepository repository;
    private final Connection connection;

    public CarMechanicService(Connection connection, CarMechanicRepository repository) {
        if (connection == null || repository == null) {
            throw new IllegalArgumentException("Connection and repository cannot be null");
        }
        this.connection = connection;
        this.repository = repository;
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
    public void close() throws SQLException {
        if ( !connection.isClosed()) {
            connection.close();
        }
    }
}
