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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class CarRepositoryTest {
    protected static CarRepository carRepository;
    private static DataSource dataSource;

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
        carRepository = new CarRepository(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword());
        System.out.println("Репозиторий подключился");

        createTables();
    }

    @BeforeEach
    void cleanUp() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            // Удаление всех записей из таблиц
            statement.execute("TRUNCATE TABLE car RESTART IDENTITY CASCADE;");
            statement.execute("TRUNCATE TABLE customer RESTART IDENTITY CASCADE;");
        }
    }

    static void createTables() {
        String createCustomerTableSQL = """
                CREATE TABLE IF NOT EXISTS customer
                (
                id BIGSERIAL NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                email TEXT NOT NULL
                );
                """;

        String createCarTableSQL = """
                CREATE TABLE IF NOT EXISTS car
                (
                id BIGSERIAL NOT NULL PRIMARY KEY,
                model TEXT NOT NULL,
                customer_id BIGINT NOT NULL REFERENCES customer(id)
                );
                """;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createCustomerTableSQL);
            statement.execute(createCarTableSQL);
            System.out.println("Таблицы созданы");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при создании таблиц", e);
        }
    }

    @Test
    void shouldAddCar() throws SQLException {
        // Добавляем клиента для связи с машиной
        String insertCustomerSQL = "INSERT INTO customer (name, email) VALUES ('John Doe', 'john.doe@example.com') RETURNING id;";
        int customerId;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(insertCustomerSQL);
            if (resultSet.next()) {
                customerId = resultSet.getInt("id");
            } else {
                throw new RuntimeException("Не удалось получить ID клиента после вставки");
            }
        }


        // Добавляем машину
        Car car = new Car("Toyota Camry", customerId);
        carRepository.addCar(car);

        // Проверяем, что машина добавлена
        Car fetchedCar = carRepository.getCarById(1);
        assertNotNull(fetchedCar);
        assertEquals("Toyota Camry", fetchedCar.getModel());
        assertEquals(customerId, fetchedCar.getCustomerId());
    }



    @Test
    void shouldGetCarById() throws SQLException {
        // Добавляем клиента для связи с машиной
        String insertCustomerSQL = "INSERT INTO customer (name, email) VALUES ('Jane Doe', 'jane.doe@example.com') RETURNING id;";
        int customerId;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(insertCustomerSQL);
            if (resultSet.next()) {
                customerId = resultSet.getInt("id");
            } else {
                throw new RuntimeException("Не удалось получить ID клиента после вставки");
            }
        }

        // Добавляем машину
        Car car = new Car("Honda Accord", customerId);
        carRepository.addCar(car);

        // Проверяем, что машина возвращается корректно
        Car fetchedCar = carRepository.getCarById(1);
        assertNotNull(fetchedCar);
        assertEquals("Honda Accord", fetchedCar.getModel());
        assertEquals(customerId, fetchedCar.getCustomerId());
    }

    @Test
    void shouldUpdateCar() throws SQLException {
        // Добавляем клиента для связи с машиной
        String insertCustomerSQL = "INSERT INTO customer (name, email) VALUES ('Jane Doe', 'jane.doe@example.com') RETURNING id;";
        int customerId;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(insertCustomerSQL);
            if (resultSet.next()) {
                customerId = resultSet.getInt("id");
            } else {
                throw new RuntimeException("Не удалось получить ID клиента после вставки");
            }
        }

        // Добавляем машину
        Car car = new Car("Nissan Altima", customerId);
        carRepository.addCar(car);

        // Обновляем машину
        Car updatedCar = new Car(1, "Nissan Maxima", customerId);
        carRepository.updateCar(updatedCar);

        // Проверяем, что машина обновлена
        Car fetchedCar = carRepository.getCarById(1);
        assertNotNull(fetchedCar);
        assertEquals("Nissan Maxima", fetchedCar.getModel());
        assertEquals(customerId, fetchedCar.getCustomerId());
    }

    @Test
    void shouldDeleteCar() throws SQLException {
        // Добавляем клиента для связи с машиной
        String insertCustomerSQL = "INSERT INTO customer (name, email) VALUES ('Jack Doe', 'jack.doe@example.com') RETURNING id;";
        int customerId;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(insertCustomerSQL);
            if (resultSet.next()) {
                customerId = resultSet.getInt("id");
            } else {
                throw new RuntimeException("Не удалось получить ID клиента после вставки");
            }
        }

        // Добавляем машину
        Car car = new Car("Ford Mustang", customerId);
        carRepository.addCar(car);

        // Удаляем машину
        carRepository.deleteCar(1);

        // Проверяем, что машина удалена
        Car fetchedCar = carRepository.getCarById(1);
        assertNull(fetchedCar);
    }

    @Test
    void shouldGetAllCars() throws SQLException {
        // Добавляем клиента для связи с машинами
        String insertCustomerSQL = "INSERT INTO customer (name, email) VALUES ('Alice Doe', 'alice.doe@example.com') RETURNING id;";
        int customerId;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(insertCustomerSQL);
            if (resultSet.next()) {
                customerId = resultSet.getInt("id");
            } else {
                throw new RuntimeException("Не удалось получить ID клиента после вставки");
            }
        }

        // Добавляем машины
        Car car1 = new Car("Chevrolet Malibu", customerId);
        Car car2 = new Car("Subaru Legacy", customerId);
        carRepository.addCar(car1);
        carRepository.addCar(car2);

        // Проверяем, что все машины возвращаются корректно
        List<Car> cars = carRepository.getAllCars();
        assertNotNull(cars);
        assertEquals(2, cars.size(), "Expected 2 cars in the list.");
    }
}
