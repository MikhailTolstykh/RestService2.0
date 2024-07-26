package ru.tolstykh.service;
import ru.tolstykh.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tolstykh.repository.CustomerRepository;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


 class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCustomer() throws SQLException {
        Customer customer = new Customer();
        customerService.addCustomer(customer);
        verify(customerRepository, times(1)).addCustomer(customer);
    }

    @Test
   void testGetCustomerById() throws SQLException {
        Customer customer = new Customer(1, "John Doe", "john.doe@example.com");
        when(customerRepository.getCustomerById(1)).thenReturn(customer);

        Customer result = customerService.getCustomerById(1);
        assertEquals(customer, result);
    }

    @Test
    void testUpdateCustomer() throws SQLException {
        Customer customer = new Customer();
        customerService.updateCustomer(customer);
        verify(customerRepository, times(1)).updateCustomer(customer);
    }

    @Test
    void testDeleteCustomer() throws SQLException {
        customerService.deleteCustomer(1);
        verify(customerRepository, times(1)).deleteCustomer(1);
    }

    @Test
     void testGetAllCustomers() throws SQLException {
        Customer customer1 = new Customer(1, "John Doe", "john.doe@example.com");
        Customer customer2 = new Customer(2, "Jane Doe", "jane.doe@example.com");
        List<Customer> customers = Arrays.asList(customer1, customer2);
        when(customerRepository.getAllCustomers()).thenReturn(customers);

        List<Customer> result = customerService.getAllCustomers();
        assertEquals(2, result.size());
        assertEquals(customer1, result.get(0));
        assertEquals(customer2, result.get(1));
    }
}

