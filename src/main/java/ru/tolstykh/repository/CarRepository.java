package ru.tolstykh.repository;

import ru.tolstykh.entity.Car;
import ru.tolstykh.entity.Customer;
import ru.tolstykh.entity.Mechanic;
import ru.tolstykh.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarRepository implements CarInterface{

    private String URL = DatabaseConfig.getProperty("db.url");
    private String Username = DatabaseConfig.getProperty("db.username");
    private String Password = DatabaseConfig.getProperty("db.password");

    public CarRepository() {
    }

    private Connection connection;

    public CarRepository(Connection connection) {
        this.connection = connection;
    }
    public CarRepository(String URL, String username, String password) {
        this.URL = URL;
        Username = username;
        Password = password;
    }

    private static final String INSERT_CAR_SQL = "INSERT INTO car (model, customer_id) VALUES (?, ?);";
    private static final String SELECT_CAR_BY_ID = "SELECT id, model, customer_id FROM car WHERE id = ?;";
    private static final String SELECT_ALL_CARS = "SELECT * FROM car;";
    private static final String DELETE_CAR_SQL = "DELETE FROM car WHERE id = ?;";
    private static final String UPDATE_CAR_SQL = "UPDATE car SET model = ?, customer_id = ? WHERE id = ?;";

    private static final String INSERT_CAR_MECHANIC_SQL = "INSERT INTO car_mechanic (car_id, mechanic_id) VALUES (?, ?);";
    private static final String DELETE_CAR_MECHANICS_SQL = "DELETE FROM car_mechanic WHERE car_id = ?;";
    private static final String SELECT_MECHANICS_BY_CAR_ID = "SELECT mechanic_id FROM car_mechanic WHERE car_id = ?;";


    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, Username, Password);
    }

    @Override
    public void addCar(Car car) throws SQLException {
        if (car.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Customer ID must be a positive integer and cannot be null");
        }

        String INSERT_CAR_SQL = "INSERT INTO car (model, customer_id) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CAR_SQL)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setInt(2, car.getCustomerId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Car getCarById(int id) throws SQLException {
        Car car = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CAR_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String model = resultSet.getString("model");
                int customerId = resultSet.getInt("customer_id");

                CustomerRepository customerRepository = new CustomerRepository(URL, Username, Password);
                Customer customer = customerRepository.getCustomerById(customerId);

                MechanicRepository mechanicRepository = new MechanicRepository(getConnection());
                List<Mechanic> mechanics = mechanicRepository.getMechanicsByCarId(id);

                car = new Car(id, model, customerId);
                car.setMechanics(mechanics);
            }
        }
        return car;
    }



    @Override
    public void updateCar(Car car) throws SQLException {
        if (car.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Invalid customerId: " + car.getCustomerId());
        }

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CAR_SQL)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setInt(2, car.getCustomerId());
            preparedStatement.setInt(3, car.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteCar(int id) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CAR_SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<Car> getAllCars() throws SQLException {
        List<Car> cars = new ArrayList<>();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL_CARS)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String model = resultSet.getString("model");
                int customerId = resultSet.getInt("customer_id");

                Customer customer = null;
                CustomerRepository customerRepository = new CustomerRepository(URL, Username, Password);
                customer = customerRepository.getCustomerById(customerId);

                MechanicRepository mechanicRepository = new MechanicRepository(getConnection());
                List<Mechanic> mechanics = mechanicRepository.getMechanicsByCarId(id);

                Car car = new Car(id, model, customerId);
                car.setMechanics(mechanics);
                cars.add(car);
            }
        }

        return cars;
    }

    @Override
    public List<Mechanic> getMechanicsByCarId(int carId) throws SQLException {
        List<Mechanic> mechanics = new ArrayList<>();
        String sql = "SELECT m.id, m.name FROM mechanic m " +
                "INNER JOIN car_mechanic cm ON m.id = cm.mechanic_id " +
                "WHERE cm.car_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, carId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Mechanic mechanic = new Mechanic();
                    mechanic.setId(resultSet.getInt("id"));
                    mechanic.setName(resultSet.getString("name"));
                    mechanics.add(mechanic);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при выполнении запроса getMechanicsByCarId", e);
        }
        return mechanics;
    }


}