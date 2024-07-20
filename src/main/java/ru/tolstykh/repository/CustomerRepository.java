package ru.tolstykh.repository;

import ru.tolstykh.entity.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository implements CustomerInterface {

    private String URL;
    private String Username;
    private String Password;

    private static final String INSERT_CUSTOMERS_SQL = "INSERT INTO car_service.customer1 (name, email) VALUES (?, ?);";
    private static final String SELECT_CUSTOMER_BY_ID = "SELECT id, name, email FROM car_service.customer1 WHERE id = ?;";
    private static final String SELECT_ALL_CUSTOMERS = "SELECT * FROM car_service.customer1;";
    private static final String DELETE_CUSTOMERS_SQL = "DELETE FROM car_service.customer1 WHERE id = ?;";
    private static final String UPDATE_CUSTOMERS_SQL = "UPDATE car_service.customer1 SET name = ?, email = ? WHERE id = ?;";

    public CustomerRepository() {
    }

    // Конструктор по умолчанию
    public CustomerRepository(String jdbcUrl, String username, String password) {
        this.URL = DatabaseConfig.getProperty("db.url");
        this.Username = DatabaseConfig.getProperty("db.username");
        this.Password = DatabaseConfig.getProperty("db.password");





        // Проверка загруженных значений
        System.out.println("DB URL: " + URL);
        System.out.println("DB Username: " + Username);
        System.out.println("DB Password: " + Password);

        if (URL == null || Username == null || Password == null) {
            throw new RuntimeException("Database configuration is missing. URL, Username, or Password is null.");
        }
    }



    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, Username, Password);
    }

    public void addCustomer(Customer customer) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CUSTOMERS_SQL)) {
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getEmail());
            preparedStatement.executeUpdate(); // Убедитесь, что вызов executeUpdate()
        }
    }

    public Customer getCustomerById(int id) throws SQLException {
        Customer customer = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CUSTOMER_BY_ID)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    customer = new Customer(id, name, email);
                }
            }
        }
        return customer;
    }

    public void updateCustomer(Customer customer) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CUSTOMERS_SQL)) {
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getEmail());
            preparedStatement.setInt(3, customer.getId());
            preparedStatement.executeUpdate();
        }
    }

    public void deleteCustomer(int id) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CUSTOMERS_SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL_CUSTOMERS)) {

            while (resultSet.next()) {
                int customerId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                Customer customer = new Customer(customerId, name, email);
                customers.add(customer);
            }
        }
        return customers;
    }
}
