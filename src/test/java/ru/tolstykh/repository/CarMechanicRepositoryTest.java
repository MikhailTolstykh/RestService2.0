package ru.tolstykh.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.tolstykh.entity.Car;
import ru.tolstykh.entity.Mechanic;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class CarMechanicRepositoryTest {

    private static DataSource dataSource;
    private static CarMechanicRepository carMechanicRepository;
    private static MechanicRepository mechanicRepository;
    private static CarRepository carRepository;

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withExposedPorts(5432);




    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgresContainer.getJdbcUrl());
        config.setUsername(postgresContainer.getUsername());
        config.setPassword(postgresContainer.getPassword());
        config.setDriverClassName("org.postgresql.Driver");

        dataSource = new HikariDataSource(config);
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            carMechanicRepository = new CarMechanicRepository(connection);
            mechanicRepository = new MechanicRepository(postgresContainer.getJdbcUrl(),
                    postgresContainer.getUsername(),
                    postgresContainer.getPassword());
            createTables(connection);
            cleanUp(connection);
        }
    }

    void cleanUp(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            carRepository = new CarRepository();
            statement.execute("TRUNCATE TABLE car_mechanic RESTART IDENTITY CASCADE;");
            statement.execute("TRUNCATE TABLE car RESTART IDENTITY CASCADE;");
            statement.execute("TRUNCATE TABLE mechanic RESTART IDENTITY CASCADE;");
        }
    }

    private static void createTables(Connection connection) throws SQLException {
        String createCarTableSQL = """
                CREATE TABLE IF NOT EXISTS car
                (
                    id BIGSERIAL NOT NULL PRIMARY KEY,
                    model TEXT NOT NULL,
                    customer_id BIGINT NOT NULL
                );
                """;

        String createMechanicTableSQL = """
                CREATE TABLE IF NOT EXISTS mechanic
                (
                    id BIGSERIAL NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL
                );
                """;

        String createCarMechanicTableSQL = """
                CREATE TABLE IF NOT EXISTS car_mechanic
                (
                    car_id BIGINT NOT NULL,
                    mechanic_id BIGINT NOT NULL,
                    PRIMARY KEY (car_id, mechanic_id)
                );
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(createCarTableSQL);
            statement.execute(createMechanicTableSQL);
            statement.execute(createCarMechanicTableSQL);
        }
    }

    @Test
    void GetCarsByMechanicId() throws SQLException {

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO car (model, customer_id) VALUES ('Model S', 1), ('Model 3', 2);");
            statement.execute("INSERT INTO mechanic (name) VALUES ('John Doe'), ('Jane Doe');");
            statement.execute("INSERT INTO car_mechanic (car_id, mechanic_id) VALUES (1, 1), (2, 1);");
        }


        try (Connection connection = dataSource.getConnection()) {
            carMechanicRepository = new CarMechanicRepository(connection);


            List<Car> cars = mechanicRepository.getCarsByMechanicId(1);

            assertNotNull(cars);
            assertEquals(2, cars.size());
            assertEquals("Model S", cars.get(0).getModel());
            assertEquals("Model 3", cars.get(1).getModel());
        }
    }
    @Test
    void shouldDeleteAllMechanicsForCar() throws SQLException {
        // Вставка тестовых данных
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            // Вставляем автомобиль
            statement.execute("INSERT INTO car (model, customer_id) VALUES ('Model Y', 1);");
            // Вставляем механиков
            statement.execute("INSERT INTO mechanic (name) VALUES ('Alice'), ('Bob');");
            // Создаем связи между автомобилем и механиками
            statement.execute("INSERT INTO car_mechanic (car_id, mechanic_id) VALUES (1, 1), (1, 2);");
        }

        // Проверяем, что механики были добавлены
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS count FROM car_mechanic WHERE car_id = 1")) {
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                assertEquals(2, count, "Expected 2 mechanics for car_id 1 before deletion.");
            }
        }

        // Удаляем все механики для автомобиля
        try (Connection connection = dataSource.getConnection()) {
            carMechanicRepository = new CarMechanicRepository(connection);
            carMechanicRepository.deleteCarMechanics(1);
        }

        // Проверяем, что механики были удалены
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS count FROM car_mechanic WHERE car_id = 1")) {
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                assertEquals(0, count, "Expected 0 mechanics for car_id 1 after deletion.");
            }
        }
    }
}