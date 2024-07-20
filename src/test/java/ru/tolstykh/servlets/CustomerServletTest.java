package ru.tolstykh.servlets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.tolstykh.dto.CustomerDTO;
import ru.tolstykh.entity.Customer;
import ru.tolstykh.service.CustomerServiceInterface;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServletTest {

    private CustomerServlet customerServlet;
    private CustomerServiceInterface customerService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws ServletException {
        customerService = mock(CustomerServiceInterface.class);
        customerServlet = new CustomerServlet();
        customerServlet.init();
        customerServlet.customerService = customerService;
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
    }

    @Test
    void testDoGetWithId() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        Customer customer = new Customer(1, "John Doe", "john.doe@example.com");
        when(customerService.getCustomerById(1)).thenReturn(customer);

        customerServlet.doGet(request, response);

        verify(customerService, times(1)).getCustomerById(1);
        String expectedJson = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
        assertEquals(expectedJson, stringWriter.toString().trim());
    }

    @Test
    void testDoGetAll() throws Exception {
        when(request.getParameter("id")).thenReturn(null);
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        List<Customer> customers = Arrays.asList(
                new Customer(1, "John Doe", "john.doe@example.com"),
                new Customer(2, "Jane Doe", "jane.doe@example.com")
        );
        when(customerService.getAllCustomers()).thenReturn(customers);

        customerServlet.doGet(request, response);

        verify(customerService, times(1)).getAllCustomers();
        String expectedJson = "[{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"},{\"id\":2,\"name\":\"Jane Doe\",\"email\":\"jane.doe@example.com\"}]";
        assertEquals(expectedJson, stringWriter.toString().trim());
    }

    @Test
    void testDoPost() throws Exception {
        String json = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        customerServlet.doPost(request, response);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerService, times(1)).addCustomer(customerCaptor.capture());
        Customer customer = customerCaptor.getValue();
        assertEquals("John Doe", customer.getName());
        assertEquals("john.doe@example.com", customer.getEmail());

        assertEquals("{\"message\":\"Customer added successfully\"}", stringWriter.toString().trim());
    }

    @Test
    void testDoPut() throws Exception {
        String json = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        customerServlet.doPut(request, response);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerService, times(1)).updateCustomer(customerCaptor.capture());
        Customer customer = customerCaptor.getValue();
        assertEquals(1, customer.getId());
        assertEquals("John Doe", customer.getName());
        assertEquals("john.doe@example.com", customer.getEmail());

        assertEquals("{\"message\":\"Customer updated successfully\"}", stringWriter.toString().trim());
    }

    @Test
    void testDoDelete() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        customerServlet.doDelete(request, response);

        verify(customerService, times(1)).deleteCustomer(1);
        assertEquals("{\"message\":\"Customer deleted successfully\"}", stringWriter.toString().trim());
    }

    @Test
    void testDoDeleteWithoutId() throws Exception {
        when(request.getParameter("id")).thenReturn(null);
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        customerServlet.doDelete(request, response);

        verify(customerService, times(0)).deleteCustomer(anyInt());
        assertEquals("{\"error\":\"Customer ID is required\"}", stringWriter.toString().trim());
    }
}
