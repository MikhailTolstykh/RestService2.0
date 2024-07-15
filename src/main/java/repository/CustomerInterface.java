package repository;

import entity.Customer;

import java.sql.SQLException;
import java.util.List;

public interface CustomerInterface {


        void addCustomer(Customer customer) throws SQLException;

        Customer getCustomerById(int id) throws SQLException;

        void updateCustomer(Customer customer) throws SQLException;

        void deleteCustomer(int id) throws SQLException;

        List<Customer> getAllCustomers() throws SQLException;
    }

