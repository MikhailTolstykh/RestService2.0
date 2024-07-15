package repository;

import entity.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CustomerRepository implements CustomerInterface {

    private String URL = DatabaseConfig.getProperty("db.url");
    private String Username = DatabaseConfig.getProperty("db.username");
    private String Password = DatabaseConfig.getProperty("db.password");


    private static final String INSERT_CUSTOMERS_SQL = "INSERT INTO customers (name, email) VALUES (?, ?);";
    private static final String SELECT_CUSTOMER_BY_ID = "SELECT id, name, email FROM customers WHERE id = ?;";
    private static final String SELECT_ALL_CUSTOMERS = "SELECT * FROM customers;";
    private static final String DELETE_CUSTOMERS_SQL = "DELETE FROM customers WHERE id = ?;";
    private static final String UPDATE_CUSTOMERS_SQL = "UPDATE customers SET name = ?, email = ? WHERE id = ?;";

    protected Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, Username, Password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void addCustomer(Customer customer) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CUSTOMERS_SQL)) {
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getEmail());
        }
    }

    public Customer getCustomerById(int id) throws SQLException {
        Customer customer = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CUSTOMER_BY_ID)) {
            preparedStatement.setInt(1, customer.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");

                customer = new Customer(id, name, email);
            }
            return customer;
        }
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
                int customerId = resultSet.getInt("customer_id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");

                Customer customer = new Customer(customerId, name, email); // Предполагается, что у вас есть конструктор Customer
                customers.add(customer);
            }
        }

        return customers;
    }


}

