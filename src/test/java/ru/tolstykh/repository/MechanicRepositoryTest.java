package ru.tolstykh.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import static ru.tolstykh.repository.CarRepositoryTest.createTables;


@Testcontainers
public class MechanicRepositoryTest {
    private static MechanicRepository mechanicRepository;
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

}








