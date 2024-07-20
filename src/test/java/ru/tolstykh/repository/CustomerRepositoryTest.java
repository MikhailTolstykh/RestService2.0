package ru.tolstykh.repository;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CustomerRepositoryTest {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/myDataBase";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";
    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        // Установление соединения с базой данных
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement statement = connection.createStatement();

        // Создание таблицы, если ее нет
        String createTableSQL = "CREATE TABLE IF NOT EXISTS car_service.customer1 ("
                + "id SERIAL PRIMARY KEY, "
                + "name VARCHAR(100), "
                + "email VARCHAR(100) UNIQUE);";
        statement.execute(createTableSQL);

        // Очистка таблицы перед тестами
        String truncateTableSQL = "TRUNCATE TABLE car_service.customer1 RESTART IDENTITY CASCADE;";
        statement.execute(truncateTableSQL);
    }

    @After
    public void tearDown() throws SQLException {
        // Очистка и закрытие соединения
        Statement statement = connection.createStatement();
        String dropTableSQL = "DROP TABLE IF EXISTS car_service.customer1;";
        statement.execute(dropTableSQL);
        connection.close();
    }

    @Test
    public void testAddCustomer() throws SQLException {
        // Данные для добавления
        String name = "John Doe";
        String email = "john.doe@example.com";

        // Добавление клиента
        String insertSQL = "INSERT INTO car_service.customer1 (name, email) VALUES (?, ?);";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        }

        // Проверка, что клиент был добавлен
        String selectSQL = "SELECT * FROM car_service.customer1 WHERE email = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(name, rs.getString("name"));
            assertEquals(email, rs.getString("email"));
        }
    }

    @Test
    public void testGetCustomerById() throws SQLException {
        // Данные для добавления
        String name = "Jane Doe";
        String email = "jane.doe@example.com";

        // Добавление клиента
        String insertSQL = "INSERT INTO car_service.customer1 (name, email) VALUES (?, ?) RETURNING id;";
        int id;
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            id = rs.getInt("id");
        }

        // Проверка получения клиента по ID
        String selectSQL = "SELECT * FROM car_service.customer1 WHERE id = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(name, rs.getString("name"));
            assertEquals(email, rs.getString("email"));
        }
    }

    @Test
    public void testGetCustomerByInvalidId() throws SQLException {
        // Проверка получения клиента по несуществующему ID
        String selectSQL = "SELECT * FROM car_service.customer1 WHERE id = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setInt(1, -1); // Не существующий ID
            ResultSet rs = pstmt.executeQuery();
            assertFalse(rs.next());
        }
    }

    @Test
    public void testUpdateCustomer() throws SQLException {
        // Данные для добавления
        String name = "Alice Smith";
        String email = "alice.smith@example.com";
        String newName = "Alice Johnson";

        // Добавление клиента
        String insertSQL = "INSERT INTO car_service.customer1 (name, email) VALUES (?, ?) RETURNING id;";
        int id;
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            id = rs.getInt("id");
        }

        // Обновление данных клиента
        String updateSQL = "UPDATE car_service.customer1 SET name = ? WHERE id = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }

        // Проверка обновленных данных
        String selectSQL = "SELECT * FROM car_service.customer1 WHERE id = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(newName, rs.getString("name"));
            assertEquals(email, rs.getString("email"));
        }
    }

    @Test
    public void testDeleteCustomer() throws SQLException {
        // Данные для добавления
        String name = "Bob Brown";
        String email = "bob.brown@example.com";

        // Добавление клиента
        String insertSQL = "INSERT INTO car_service.customer1 (name, email) VALUES (?, ?) RETURNING id;";
        int id;
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            id = rs.getInt("id");
        }

        // Удаление клиента
        String deleteSQL = "DELETE FROM car_service.customer1 WHERE id = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }

        // Проверка удаления клиента
        String selectSQL = "SELECT * FROM car_service.customer1 WHERE id = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            assertFalse(rs.next());
        }
    }

    @Test
    public void testAddCustomerWithDuplicateEmail() throws SQLException {
        // Данные для добавления
        String name1 = "Charlie Green";
        String email = "charlie.green@example.com";
        String name2 = "David Blue";

        // Добавление клиента
        String insertSQL = "INSERT INTO car_service.customer1 (name, email) VALUES (?, ?);";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, name1);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        }

        // Попытка добавить клиента с дублирующимся email
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, name2);
            pstmt.setString(2, email);
            pstmt.executeUpdate(); // Ожидается, что произойдет ошибка
            // Поскольку ожидание ошибки может быть специфичным для вашей реализации,
            // вы можете использовать проверку SQLException или специфичное поведение.
        } catch (SQLException e) {
            // Проверка на исключение из-за дублирующегося email
            assertTrue(e.getSQLState().startsWith("23505")); // Код ошибки для дублирующих записей в PostgreSQL
        }
    }
}
