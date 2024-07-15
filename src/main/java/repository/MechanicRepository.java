package repository;

import entity.Car;
import entity.Mechanic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MechanicRepository implements MechanicInterface {
    private String URL = DatabaseConfig.getProperty("db.url");
    private String Username = DatabaseConfig.getProperty("db.username");
    private String Password = DatabaseConfig.getProperty("db.password");

    private static final String INSERT_MECHANIC_SQL = "INSERT INTO mechanics (name) VALUES (?)";
    private static final String SELECT_MECHANIC_BY_ID_SQL = "SELECT * FROM mechanics WHERE id = ?";
    private static final String UPDATE_MECHANIC_SQL = "UPDATE mechanics SET name = ? WHERE id = ?";
    private static final String DELETE_MECHANIC_SQL = "DELETE FROM mechanics WHERE id = ?";
    private static final String SELECT_ALL_MECHANICS_SQL = "SELECT * FROM mechanics";
    private static final String SELECT_CARS_BY_MECHANIC_ID_SQL = "SELECT c.id, c.model, c.customer_id FROM cars c " +
            "INNER JOIN mechanic_car mc ON c.id = mc.car_id " +
            "WHERE mc.mechanic_id = ?";
    private static final String INSERT_MECHANIC_CAR_SQL = "INSERT INTO mechanic_car (mechanic_id, car_id) VALUES (?, ?)";
    private static final String DELETE_MECHANIC_CAR_SQL = "DELETE FROM mechanic_car WHERE mechanic_id = ? AND car_id = ?";
    private static final String SELECT_MECHANICS_BY_CAR_ID_SQL = "SELECT m.id, m.name " +   "FROM mechanic m " +

                    "JOIN car_mechanic cm ON m.id = cm.mechanic_id " +
                    "WHERE cm.car_id = ?";
    @Override
    public void addMechanic(Mechanic mechanic) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_MECHANIC_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, mechanic.getName());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                mechanic.setId(generatedKeys.getInt(1));
            }
        }
    }

    @Override
    public Mechanic getMechanicById(int id) throws SQLException {
        Mechanic mechanic = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MECHANIC_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                mechanic = new Mechanic(id, name); // cars will be fetched separately
            }
        }
        return mechanic;
    }

    @Override
    public void updateMechanic(Mechanic mechanic) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MECHANIC_SQL)) {
            preparedStatement.setString(1, mechanic.getName());
            preparedStatement.setInt(2, mechanic.getId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteMechanic(int id) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_MECHANIC_SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<Mechanic> getAllMechanics() throws SQLException {
        List<Mechanic> mechanics = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL_MECHANICS_SQL)) {
            while (resultSet.next()) {
                int mechanicId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                Mechanic mechanic = new Mechanic(mechanicId, name); // cars will be fetched separately
                mechanics.add(mechanic);
            }
        }
        return mechanics;
    }

    @Override
    public List<Car> getCarsByMechanicId(int mechanicId) throws SQLException {
        List<Car> cars = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CARS_BY_MECHANIC_ID_SQL)) {
            preparedStatement.setInt(1, mechanicId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int carId = resultSet.getInt("id");
                String model = resultSet.getString("model");

                // Создание объекта Car без указания customer и mechanics
                Car car = new Car(carId, model, null, null);
                cars.add(car);
            }
        }
        return cars;
    }

    @Override
    public void connectCarToMechanic(int mechanicId, int carId) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_MECHANIC_CAR_SQL)) {
            preparedStatement.setInt(1, mechanicId);
            preparedStatement.setInt(2, carId);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteConnectCarFromMechanic(int mechanicId, int carId) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_MECHANIC_CAR_SQL)) {
            preparedStatement.setInt(1, mechanicId);
            preparedStatement.setInt(2, carId);
            preparedStatement.executeUpdate();
        }
    }
    public List<Mechanic> getMechanicsByCarId(int carId) throws SQLException {
        List<Mechanic> mechanics = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MECHANICS_BY_CAR_ID_SQL)) {
            preparedStatement.setInt(1, carId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int mechanicId = resultSet.getInt("id");
                String name = resultSet.getString("name");

                Mechanic mechanic = new Mechanic(mechanicId, name);
                mechanics.add(mechanic);
            }
        }
        return mechanics;
    }
    protected Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, Username, Password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}









