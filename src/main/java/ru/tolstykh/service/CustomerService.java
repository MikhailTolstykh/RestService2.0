package ru.tolstykh.service;

import ru.tolstykh.entity.Customer;
import ru.tolstykh.repository.CustomerRepository;

import java.sql.SQLException;
import java.util.List;

public class CustomerService implements CustomerServiceInterface {

    private final CustomerRepository customerRepository;




    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }



    @Override
    public void addCustomer(Customer customer) throws SQLException {
        customerRepository.addCustomer(customer);
    }

    @Override
    public Customer getCustomerById(int id) throws SQLException {
        return customerRepository.getCustomerById(id);
    }

    @Override
    public void updateCustomer(Customer customer) throws SQLException {
        customerRepository.updateCustomer(customer);
    }

    @Override
    public void deleteCustomer(int id) throws SQLException {
        customerRepository.deleteCustomer(id);
    }

    @Override
    public List<Customer> getAllCustomers() throws SQLException {
        return customerRepository.getAllCustomers();
    }
}




