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
import ru.tolstykh.entity.Customer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class CustomerRepositoryTest {
    private static CustomerRepository customerRepository;
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

        System.out.println("Настроили переменные");

        dataSource = new HikariDataSource(config);
        customerRepository = new CustomerRepository(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword());
        System.out.println("репозиторий подключился");

        createTables();
    }


    @BeforeEach
    void cleanUp() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            // Удаление всех записей из таблицы
            statement.execute("TRUNCATE TABLE customer RESTART IDENTITY CASCADE;");
        }
    }





    private static void createTables() {

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
                (id BIGSERIAL NOT NULL PRIMARY KEY,
                model TEXT NOT NULL,
                customer_id BIGINT NOT NULL);
                """;

        String insertCustomerTableSQL = "INSERT INTO customer (name,email) VALUES ('lada', 'example@example.com')";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createCarTableSQL);
            statement.execute(createCustomerTableSQL);
            statement.execute(insertCustomerTableSQL);
            System.out.println("Таблицы созданы");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при создании таблиц", e);
        }
    }
    @Test
    void shouldAddCustomer() throws SQLException {
        Customer customer = new Customer("John Doe", "john.doe@example.com");
        customerRepository.addCustomer(customer);

        Customer fetchedCustomer = customerRepository.getCustomerById(1);
        assertNotNull(fetchedCustomer);
        assertEquals("John Doe", fetchedCustomer.getName());
        assertEquals("john.doe@example.com", fetchedCustomer.getEmail());
    }

    @Test
    void shouldGetCustomerById() throws SQLException {
        Customer customer = new Customer("Jane Doe", "jane.doe@example.com");
        customerRepository.addCustomer(customer);

        Customer fetchedCustomer = customerRepository.getCustomerById(1);
        assertNotNull(fetchedCustomer);
        assertEquals("Jane Doe", fetchedCustomer.getName());
        assertEquals("jane.doe@example.com", fetchedCustomer.getEmail());
    }

    @Test
    void shouldUpdateCustomer() throws SQLException {
        Customer customer = new Customer("Initial Name", "initial@example.com");
        customerRepository.addCustomer(customer);

        Customer updatedCustomer = new Customer(1, "Updated Name", "updated@example.com");
        customerRepository.updateCustomer(updatedCustomer);

        Customer fetchedCustomer = customerRepository.getCustomerById(1);
        assertNotNull(fetchedCustomer);
        assertEquals("Updated Name", fetchedCustomer.getName());
        assertEquals("updated@example.com", fetchedCustomer.getEmail());
    }

    @Test
    void shouldDeleteCustomer() throws SQLException {
        Customer customer = new Customer("To Be Deleted", "tobedeleted@example.com");
        customerRepository.addCustomer(customer);

        customerRepository.deleteCustomer(1);

        Customer fetchedCustomer = customerRepository.getCustomerById(1);
        assertNull(fetchedCustomer);
    }

    @Test
    void shouldGetAllCustomers() throws SQLException {
        Customer customer1 = new Customer("Customer One", "customer1@example.com");
        Customer customer2 = new Customer("Customer Two", "customer2@example.com");
        customerRepository.addCustomer(customer1);
        customerRepository.addCustomer(customer2);

        List<Customer> customers = customerRepository.getAllCustomers();
        assertNotNull(customers);
        assertEquals(2, customers.size());
    }
}