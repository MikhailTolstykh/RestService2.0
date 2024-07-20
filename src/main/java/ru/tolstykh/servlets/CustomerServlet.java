package ru.tolstykh.servlets;

import ru.tolstykh.dto.CustomerDTO;
import ru.tolstykh.entity.Customer;
import ru.tolstykh.repository.CustomerRepository;
import ru.tolstykh.service.CustomerService;
import ru.tolstykh.service.CustomerServiceInterface;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerServlet extends HttpServlet {
    protected CustomerServiceInterface customerService;

    @Override
    public void init() throws ServletException {
        CustomerRepository customerRepository = new CustomerRepository();
        customerService = new CustomerService(customerRepository);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("doGet called");
        String id = request.getParameter("id");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            if (id != null) {
                int customerId = Integer.parseInt(id);
                Customer customer = customerService.getCustomerById(customerId);
                CustomerDTO customerDTO = CustomerDTO.fromEntity(customer);
                out.write(customerDTOToJson(customerDTO));
            } else {
                List<Customer> customers = customerService.getAllCustomers();
                out.write("[");
                for (int i = 0; i < customers.size(); i++) {
                    CustomerDTO customerDTO = CustomerDTO.fromEntity(customers.get(i));
                    out.write(customerDTOToJson(customerDTO));
                    if (i < customers.size() - 1) {
                        out.write(",");
                    }
                }
                out.write("]");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CustomerDTO customerDTO = jsonToCustomerDTO(request.getReader().lines().collect(Collectors.joining()));

        try {
            Customer customer = CustomerDTO.toEntity(customerDTO); // Используем статический метод toEntity
            customerService.addCustomer(customer);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"message\":\"Customer added successfully\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CustomerDTO customerDTO = jsonToCustomerDTO(request.getReader().lines().collect(Collectors.joining()));

        try {
            Customer customer = CustomerDTO.toEntity(customerDTO); // Используем статический метод toEntity
            customerService.updateCustomer(customer);
            response.getWriter().write("{\"message\":\"Customer updated successfully\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        try {
            if (id != null) {
                int customerId = Integer.parseInt(id);
                customerService.deleteCustomer(customerId);
                response.getWriter().write("{\"message\":\"Customer deleted successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Customer ID is required\"}");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private String customerDTOToJson(CustomerDTO customerDTO) {
        return String.format("{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\"}", customerDTO.getId(), customerDTO.getName(), customerDTO.getEmail());
    }

    private CustomerDTO jsonToCustomerDTO(String json) {
        CustomerDTO customerDTO = new CustomerDTO();
        json = json.replace("{", "").replace("}", "").replace("\"", "");
        String[] fields = json.split(",");
        for (String field : fields) {
            String[] keyValue = field.split(":");
            switch (keyValue[0]) {
                case "id":
                    customerDTO.setId(Integer.parseInt(keyValue[1]));
                    break;
                case "name":
                    customerDTO.setName(keyValue[1]);
                    break;
                case "email":
                    customerDTO.setEmail(keyValue[1]);
                    break;
            }
        }
        return customerDTO;
    }
}