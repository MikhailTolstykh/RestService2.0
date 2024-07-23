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

import ru.tolstykh.entity.Mechanic;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



@Testcontainers
public class MechanicRepositoryTest {
    public static MechanicRepository mechanicRepository;
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
        mechanicRepository = new MechanicRepository(postgresContainer.
                getJdbcUrl(),
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
            statement.execute("TRUNCATE TABLE car_mechanic RESTART IDENTITY CASCADE;");
            statement.execute("TRUNCATE TABLE car RESTART IDENTITY CASCADE;");
            statement.execute("TRUNCATE TABLE mechanic RESTART IDENTITY CASCADE;");
            statement.execute("TRUNCATE TABLE customer RESTART IDENTITY CASCADE;");
            System.out.println("записи удалились");


        }
    }


    private static void createTables() {


        String createCustomerTableSQL = """
                CREATE TABLE IF NOT EXISTS customer (
                    id BIGSERIAL NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL
                );
                """;


        String createMechanicTableSQL = """
                CREATE TABLE IF NOT EXISTS mechanic (
                    id BIGSERIAL NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL
                );
                """;

        String createCarTableSQL = """
                         CREATE TABLE IF NOT EXISTS car (
                             id BIGSERIAL NOT NULL PRIMARY KEY,
                             model TEXT NOT NULL,
                             customer_id BIGINT NOT NULL
                );
                         """;

        String createCarMechanicTableSQL = """
                CREATE TABLE IF NOT EXISTS car_mechanic (
                    car_id BIGINT NOT NULL,
                    mechanic_id BIGINT NOT NULL,
                    PRIMARY KEY (car_id, mechanic_id),
                    FOREIGN KEY (car_id) REFERENCES car(id),
                    FOREIGN KEY (mechanic_id) REFERENCES mechanic(id)
                );
                """;


        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createMechanicTableSQL);
            statement.execute(createCarTableSQL);
            statement.execute(createCarMechanicTableSQL);
            statement.execute(createCustomerTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при создании таблиц", e);
        }
    }

    @Test
    void shouldAddMechanic() throws SQLException {
        Mechanic mechanic = new Mechanic(0, "John Doe");
        mechanicRepository.addMechanic(mechanic);

        Mechanic fetchedMechanic = mechanicRepository.getMechanicById(mechanic.getId());
        assertNotNull(fetchedMechanic);
        assertEquals("John Doe", fetchedMechanic.getName());
    }

    @Test
    void shouldThrowSQLExceptionWhenConnectionFails() {
        MechanicRepository failingRepository = new MechanicRepository("invalid_url", "invalid_user", "invalid_password");
        Mechanic mechanic = new Mechanic(0, "John Doe");

        SQLException exception = assertThrows(SQLException.class, () -> {
            failingRepository.addMechanic(mechanic);
        });

        assertTrue(exception.getMessage().contains("No suitable driver found"), "Expected SQLException with message containing 'No suitable driver found'");
    }

    @Test
    void shouldUpdateMechanic() throws SQLException {
        Mechanic mechanic = new Mechanic(0, "John Doe");
        mechanicRepository.addMechanic(mechanic);

        mechanic.setName("Jane Doe");
        mechanicRepository.updateMechanic(mechanic);

        Mechanic updatedMechanic = mechanicRepository.getMechanicById(mechanic.getId());
        assertNotNull(updatedMechanic);
        assertEquals("Jane Doe", updatedMechanic.getName());
    }

    @Test
    void shouldDeleteMechanic() throws SQLException {
        Mechanic mechanic = new Mechanic(0, "John Doe");
        mechanicRepository.addMechanic(mechanic);

        mechanicRepository.deleteMechanic(mechanic.getId());
        Mechanic deletedMechanic = mechanicRepository.getMechanicById(mechanic.getId());
        assertNull(deletedMechanic);
    }

    @Test
    void shouldGetAllMechanics() throws SQLException {
        // Создание и добавление механиков
        Mechanic mechanic1 = new Mechanic(0, "John Doe");
        Mechanic mechanic2 = new Mechanic(0, "Jane Doe");
        mechanicRepository.addMechanic(mechanic1);
        mechanicRepository.addMechanic(mechanic2);

        // Проверка количества механиков
        List<Mechanic> mechanics = mechanicRepository.getAllMechanics();
        assertEquals(2, mechanics.size(), "Не удалось получить правильное количество механиков.");
        assertEquals("John Doe", mechanics.get(0).getName(), "Имя первого механика не совпадает.");
        assertEquals("Jane Doe", mechanics.get(1).getName(), "Имя второго механика не совпадает.");
    }
    @Test
    void shouldGetMechanicsByCarId() throws SQLException {

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("INSERT INTO mechanic (name) VALUES ('John Doe');");
            statement.execute("INSERT INTO mechanic (name) VALUES ('Jane Doe');");
            statement.execute("INSERT INTO car (model, customer_id) VALUES ('Model X', 1);");
            statement.execute("INSERT INTO car_mechanic (car_id, mechanic_id) VALUES (1, 1);");
            statement.execute("INSERT INTO car_mechanic (car_id, mechanic_id) VALUES (1, 2);");
        }


        List<Mechanic> mechanics = mechanicRepository.getMechanicsByCarId(1);


        assertNotNull(mechanics, "Список механиков не должен быть null.");
        assertEquals(2, mechanics.size(), "Неверное количество механиков.");
        assertEquals("John Doe", mechanics.get(0).getName(), "Имя первого механика не совпадает.");
        assertEquals("Jane Doe", mechanics.get(1).getName(), "Имя второго механика не совпадает.");
    }

    @Test
    void shouldThrowExceptionWhenDatabaseConfigIsMissing() {
        // Создаем репозиторий с отсутствующими параметрами для конфигурации базы данных
        Exception exception = assertThrows(RuntimeException.class, () -> {
            new MechanicRepository(null, null, null);
        });

        // Проверяем, что выбрасывается правильное исключение с правильным сообщением
        assertTrue(exception.getMessage().contains("Database configuration is missing. URL, Username, or Password is null."));
    }

    @Test
    void shouldSetGeneratedIdWhenAddingMechanic() throws SQLException {
        Mechanic mechanic = new Mechanic(0, "John Doe");
        mechanicRepository.addMechanic(mechanic);

        assertTrue(mechanic.getId() > 0, "Mechanic ID should be greater than zero after adding to database");
    }

    @Test
    void shouldThrowSQLExceptionWhenMechanicNameIsNull() throws SQLException {
        Mechanic mechanic = new Mechanic(0, null); // Имя механика null

        SQLException exception = assertThrows(SQLException.class, () -> {
            mechanicRepository.addMechanic(mechanic);
        });

        assertTrue(exception.getMessage().contains("name cannot be null"), "Expected SQLException for null mechanic name");
    }

}