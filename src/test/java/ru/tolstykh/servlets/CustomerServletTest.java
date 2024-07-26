package ru.tolstykh.servlets;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tolstykh.dto.CustomerDTO;
import ru.tolstykh.entity.Customer;
import ru.tolstykh.repository.CustomerRepository;
import ru.tolstykh.service.CustomerService;
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


    @InjectMocks
    private CustomerServlet customerServlet;

    @Mock
    private CustomerServiceInterface customerService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PrintWriter writer;
    @Mock
    private StringWriter stringWriter;




    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        stringWriter = new StringWriter();
        // Используем StringWriter для создания PrintWriter
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
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

    @Test
    void testDoPutWithInvalidJson() throws Exception {

        String invalidJson = "{\"id\":\"abc\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}"; // Invalid JSON
        BufferedReader reader = new BufferedReader(new StringReader(invalidJson));
        when(request.getReader()).thenReturn(reader);


        customerServlet.doPut(request, response);


        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        assertEquals("{\"error\":\"Invalid data\"}", stringWriter.toString().trim());
    }

    @Test
    void testDoPutWithInvalidData() throws Exception {

        String customerJson = "{\"id\":1,\"name\":\"\",\"email\":\"john.doe@example.com\"}"; // Invalid data
        BufferedReader reader = new BufferedReader(new StringReader(customerJson));
        when(request.getReader()).thenReturn(reader);


        customerServlet.doPut(request, response);


        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        assertEquals("{\"error\":\"Invalid data\"}", stringWriter.toString().trim());
    }



    @Test
     void testDoPut_CatchBlock() throws Exception {

        String customerJson = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
        BufferedReader reader = new BufferedReader(new StringReader(customerJson));
        when(request.getReader()).thenReturn(reader);
        doThrow(new SQLException("Database error")).when(customerService).updateCustomer(any());

        customerServlet.doPut(request, response);


        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(response).setContentType("application/json");
        assertEquals("{\"error\":\"Database error\"}", stringWriter.toString().trim());
    }
    @Test
    public void testDoPost_CatchBlock() throws Exception {

        String customerJson = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
        BufferedReader reader = new BufferedReader(new StringReader(customerJson));
        when(request.getReader()).thenReturn(reader);
        doThrow(new SQLException("Database error")).when(customerService).addCustomer(any());


        customerServlet.doPost(request, response);


        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertEquals("{\"error\":\"Database error\"}", stringWriter.toString().trim());
    }
    @Test
    public void testDoDelete_CatchBlock() throws Exception {

        String customerId = "1";
        when(request.getParameter("id")).thenReturn(customerId);
        doThrow(new SQLException("Database error")).when(customerService).deleteCustomer(anyInt());


        customerServlet.doDelete(request, response);


        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertEquals("{\"error\":\"Database error\"}", stringWriter.toString().trim());
    }
    @Test
    void testInit() throws ServletException {

        String jdbcUrl = "jdbc:postgresql://localhost:5432/myDataBase";
        String username = "postgres";
        String password = "postgres";


        customerServlet.init();


        assertNotNull(customerServlet.customerService, "CustomerService should be initialized");


    }
}









