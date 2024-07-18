package ru.tolstykh.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class CarMechanicRepository  implements СarMechanicInterface {
    private Connection connection;

    public CarMechanicRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addCarMechanic(int carId, int mechanicId) throws SQLException {
        String sql = "INSERT INTO car_mechanic (car_id, mechanic_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, carId);
            statement.setInt(2, mechanicId);
            statement.executeUpdate();
        }
    }

    @Override
//удаляет все связи
    public void removeCarMechanic(int carId, int mechanicId) throws SQLException {
        String sql = "DELETE FROM car_mechanic WHERE car_id = ? AND mechanic_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, carId);
            statement.setInt(2, mechanicId);
            statement.executeUpdate();
        }
    }

     @Override
    // Удаляет конкретную запись
    public void deleteCarMechanics(int carId) throws SQLException {
        String sql = "DELETE FROM car_mechanic WHERE car_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, carId);
            preparedStatement.executeUpdate();
        }

    }

        @Override
        public void close () throws SQLException {
            if (connection != null) {
                connection.close();
            }
        }




}








