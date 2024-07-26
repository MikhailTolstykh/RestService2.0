package ru.tolstykh.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.tolstykh.entity.Car;
import ru.tolstykh.entity.Mechanic;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
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
                car_id BIGINT NOT NULL REFERENCES car(id),
                mechanic_id BIGINT NOT NULL REFERENCES mechanic(id),
                PRIMARY KEY (car_id, mechanic_id)
                );
                """;


        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createCustomerTableSQL);
            statement.execute(createCarTableSQL);
            statement.execute(createMechanicTableSQL);
            statement.execute(createCarMechanicTableSQL);
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
    void shouldHandleInvalidCustomerIdWhenAddingCar() {
        // Создаем машину с некорректным customerId (0)
        Car car = new Car("Ford Fiesta", 0);

        // Ожидаем, что метод addCar выбросит исключение
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            carRepository.addCar(car);
        });

        // Проверяем, что сообщение исключения содержит ожидаемое
        assertTrue(thrown.getMessage().contains("Customer ID must be a positive integer and cannot be null"));
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


        Car car = new Car("Ford Mustang", customerId);
        carRepository.addCar(car);


        carRepository.deleteCar(1);

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


        Car car1 = new Car("Chevrolet Malibu", customerId);
        Car car2 = new Car("Subaru Legacy", customerId);
        carRepository.addCar(car1);
        carRepository.addCar(car2);


        List<Car> cars = carRepository.getAllCars();
        assertNotNull(cars);
        assertEquals(2, cars.size(), "Expected 2 cars in the list.");
    }


    @Test
    void shouldGetMechanicsByCarId() throws SQLException {

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


        Car car = new Car("Toyota Camry", customerId);
        carRepository.addCar(car);


        String insertMechanic1SQL = "INSERT INTO mechanic (name) VALUES ('Mechanic One') RETURNING id;";
        String insertMechanic2SQL = "INSERT INTO mechanic (name) VALUES ('Mechanic Two') RETURNING id;";
        int mechanicId1;
        int mechanicId2;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            var resultSet1 = statement.executeQuery(insertMechanic1SQL);
            if (resultSet1.next()) {
                mechanicId1 = resultSet1.getInt("id");
            } else {
                throw new RuntimeException("Не удалось получить ID механика после вставки");
            }

            var resultSet2 = statement.executeQuery(insertMechanic2SQL);
            if (resultSet2.next()) {
                mechanicId2 = resultSet2.getInt("id");
            } else {
                throw new RuntimeException("Не удалось получить ID механика после вставки");
            }
        }


        String insertCarMechanic1SQL = "INSERT INTO car_mechanic (car_id, mechanic_id) VALUES (1, ?);";
        String insertCarMechanic2SQL = "INSERT INTO car_mechanic (car_id, mechanic_id) VALUES (1, ?);";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement1 = connection.prepareStatement(insertCarMechanic1SQL);
             PreparedStatement preparedStatement2 = connection.prepareStatement(insertCarMechanic2SQL)) {
            preparedStatement1.setInt(1, mechanicId1);
            preparedStatement1.executeUpdate();
            preparedStatement2.setInt(1, mechanicId2);
            preparedStatement2.executeUpdate();
        }


        List<Mechanic> mechanics = carRepository.getMechanicsByCarId(1);
        assertNotNull(mechanics);
        assertEquals(2, mechanics.size(), "Expected 2 mechanics in the list.");
        assertTrue(mechanics.stream().anyMatch(mechanic -> mechanic.getName().equals("Mechanic One")), "Expected 'Mechanic One' in the list.");
        assertTrue(mechanics.stream().anyMatch(mechanic -> mechanic.getName().equals("Mechanic Two")), "Expected 'Mechanic Two' in the list.");
    }

    @Test
    void shouldHandleSQLExceptionInGetMechanicsByCarId() throws SQLException {

        CarRepository mockCarRepository = Mockito.spy(new CarRepository(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword()));
        Connection mockConnection = Mockito.mock(Connection.class);
        PreparedStatement mockPreparedStatement = Mockito.mock(PreparedStatement.class);


        Mockito.doReturn(mockConnection).when(mockCarRepository).getConnection();
        Mockito.when(mockConnection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException("Test SQL Exception"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            mockCarRepository.getMechanicsByCarId(1);
        });

        String expectedMessage = "Ошибка при выполнении запроса getMechanicsByCarId";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Expected exception message to contain: " + expectedMessage);
    }

    @Test
    void shouldUpdateCarWithValidData() throws SQLException {

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


        Car car = new Car("Toyota Corolla", customerId);
        carRepository.addCar(car);


        Car updatedCar = new Car(1, "Toyota Camry", customerId);
        carRepository.updateCar(updatedCar);


        Car fetchedCar = carRepository.getCarById(1);
        assertNotNull(fetchedCar);
        assertEquals("Toyota Camry", fetchedCar.getModel());
        assertEquals(customerId, fetchedCar.getCustomerId());
    }

    @Test
    void shouldUpdateCarWithZeroCustomerId() throws SQLException {

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


    }


    @Test
    void shouldHandleEmptyModel() throws SQLException {

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

        Car car = new Car("Toyota Corolla", customerId);
        carRepository.addCar(car);


        Car updatedCar = new Car(1, "", customerId);
        carRepository.updateCar(updatedCar);


        Car fetchedCar = carRepository.getCarById(1);
        assertNotNull(fetchedCar);
        assertEquals("", fetchedCar.getModel());
        assertEquals(customerId, fetchedCar.getCustomerId());
    }


    @Test
    void shouldHandleAddingCarWithExistingCustomer() throws SQLException {
        // Добавляем клиента
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

        // Добавляем машину с существующим клиентом
        Car car = new Car("Chevrolet Impala", customerId);
        carRepository.addCar(car);

        // Проверяем, что машина добавлена
        Car fetchedCar = carRepository.getCarById(1);
        assertNotNull(fetchedCar);
        assertEquals("Chevrolet Impala", fetchedCar.getModel());
        assertEquals(customerId, fetchedCar.getCustomerId());
    }



    @Test
    void shouldAddAndRetrieveMechanicsByCarId() throws SQLException {

        String insertCustomerSQL = "INSERT INTO customer (name, email) VALUES ('Clara Johnson', 'clara.johnson@example.com') RETURNING id;";
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


        Car car = new Car("Honda Accord", customerId);
        carRepository.addCar(car);


        String insertMechanic1SQL = "INSERT INTO mechanic (name) VALUES ('Mechanic One') RETURNING id;";
        String insertMechanic2SQL = "INSERT INTO mechanic (name) VALUES ('Mechanic Two') RETURNING id;";
        int mechanicId1;
        int mechanicId2;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            var resultSet1 = statement.executeQuery(insertMechanic1SQL);
            if (resultSet1.next()) {
                mechanicId1 = resultSet1.getInt("id");
            } else {
                throw new RuntimeException("Не удалось получить ID механика после вставки");
            }

            var resultSet2 = statement.executeQuery(insertMechanic2SQL);
            if (resultSet2.next()) {
                mechanicId2 = resultSet2.getInt("id");
            } else {
                throw new RuntimeException("Не удалось получить ID механика после вставки");
            }
        }


        String insertCarMechanic1SQL = "INSERT INTO car_mechanic (car_id, mechanic_id) VALUES (?, ?);";
        String insertCarMechanic2SQL = "INSERT INTO car_mechanic (car_id, mechanic_id) VALUES (?, ?);";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement1 = connection.prepareStatement(insertCarMechanic1SQL);
             PreparedStatement preparedStatement2 = connection.prepareStatement(insertCarMechanic2SQL)) {
            preparedStatement1.setInt(1, 1);
            preparedStatement1.setInt(2, mechanicId1);
            preparedStatement1.executeUpdate();
            preparedStatement2.setInt(1, 1);
            preparedStatement2.setInt(2, mechanicId2);
            preparedStatement2.executeUpdate();
        }

        List<Mechanic> mechanics = carRepository.getMechanicsByCarId(1);
        assertNotNull(mechanics);
        assertEquals(2, mechanics.size(), "Expected 2 mechanics in the list.");
        assertTrue(mechanics.stream().anyMatch(mechanic -> mechanic.getName().equals("Mechanic One")), "Expected 'Mechanic One' in the list.");
        assertTrue(mechanics.stream().anyMatch(mechanic -> mechanic.getName().equals("Mechanic Two")), "Expected 'Mechanic Two' in the list.");
    }

    @Test
    void shouldThrowExceptionForInvalidCustomerId() {

        Car car = new Car("Some Model", 0);


        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            carRepository.updateCar(car);
        });

        assertTrue(thrown.getMessage().contains("Invalid customerId"));
    }

    @Test
    void shouldHandleUpdateForNonExistentCar() throws SQLException {

        String insertCustomerSQL = "INSERT INTO customer (name, email) VALUES ('Bob Doe', 'bob.doe@example.com') RETURNING id;";
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

        Car nonExistentCar = new Car(999, "Nonexistent Model", customerId);

        assertDoesNotThrow(() -> carRepository.updateCar(nonExistentCar));


        Car fetchedCar = carRepository.getCarById(999);
        assertNull(fetchedCar, "Car should not exist");
    }
    @Test
    void shouldThrowRuntimeExceptionWhenDatabaseConfigIsMissing() {
        // Проверка, когда все параметры равны null
        assertThrows(RuntimeException.class, () -> {
            new CustomerRepository(null, null, null);
        }, "Database configuration is missing. URL, Username, or Password is null.");


        assertThrows(RuntimeException.class, () -> {
            new CustomerRepository(null, "username", "password");
        }, "Database configuration is missing. URL, Username, or Password is null.");


        assertThrows(RuntimeException.class, () -> {
            new CustomerRepository("jdbcUrl", null, "password");
        }, "Database configuration is missing. URL, Username, or Password is null.");


        assertThrows(RuntimeException.class, () -> {
            new CustomerRepository("jdbcUrl", "username", null);
        }, "Database configuration is missing. URL, Username, or Password is null.");



    }



    @Test
    void shouldHandleClassNotFoundException() {

        DatabaseConnection dbConn = new DatabaseConnection("jdbc:postgresql://localhost:5432/mydb", "user", "password") {
            @Override
            protected void loadDriver() throws ClassNotFoundException {
                // Пробрасываем исключение
                throw new ClassNotFoundException("Driver class not found");
            }
        };


        assertThrows(RuntimeException.class, dbConn::getConnection);
    }

    static class DatabaseConnection {
        private final String url;
        private final String username;
        private final String password;

        public DatabaseConnection(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }

        protected Connection getConnection() {
            Connection connection = null;
            try {
                loadDriver();
                connection = DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle SQLException
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return connection;
        }


        protected void loadDriver() throws ClassNotFoundException {
            Class.forName("org.postgresql.Driver");
        }
    }
    @Test
    void shouldReturnEmptyListWhenNoCarsInDatabase() throws SQLException {

        List<Car> cars = carRepository.getAllCars();
        assertNotNull(cars, "The result should not be null.");
        assertTrue(cars.isEmpty(), "The list should be empty when no cars are present.");
    }
    @Test
    void shouldReturnMultipleCarsWhenMultipleCarsExist() throws SQLException {

        String insertCustomerSQL = "INSERT INTO customer (name, email) VALUES ('Bob Smith', 'bob.smith@example.com') RETURNING id;";
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


        Car car1 = new Car("Ford Focus", customerId);
        Car car2 = new Car("Honda Civic", customerId);
        carRepository.addCar(car1);
        carRepository.addCar(car2);


        List<Car> cars = carRepository.getAllCars();
        assertNotNull(cars, "The result should not be null.");
        assertEquals(2, cars.size(), "The list should contain exactly two cars.");
        assertTrue(cars.stream().anyMatch(car -> car.getModel().equals("Ford Focus")), "The list should contain 'Ford Focus'.");
        assertTrue(cars.stream().anyMatch(car -> car.getModel().equals("Honda Civic")), "The list should contain 'Honda Civic'.");
    }
    @Test
    void testConstructorSetsConnection() {

        assertNotNull(carRepository, "CarRepository instance should not be null");

        assertDoesNotThrow(() -> {

            carRepository.getAllCars();
        });
    }
    @Test
    void shouldInitializeCarRepositoryWithConnection() {
        try (Connection connection = dataSource.getConnection()) {
            CarRepository carRepository = new CarRepository(connection);
            assertNotNull(carRepository, "CarRepository should be initialized");
        } catch (SQLException e) {
            fail("Failed to obtain connection or initialize CarRepository", e);
        }
    }
}


