package ru.tolstykh.servlets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tolstykh.dto.CarDTO;
import ru.tolstykh.entity.Car;
import ru.tolstykh.service.CarServiceInterface;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class CarServletTest {

    @InjectMocks
    private CarServlet carServlet;

    @Mock
    private CarServiceInterface carService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void testDoGetWithId() throws Exception {
        // Setup
        when(request.getParameter("id")).thenReturn("1");
        Car car = new Car();
        car.setId(1);
        car.setModel("Toyota");
        car.setCustomerId(2);
        when(carService.getCarById(1)).thenReturn(car);

        // Execute
        carServlet.doGet(request, response);

        // Verify
        verify(response).setContentType("application/json");
        verify(writer).write("{\"id\":1,\"model\":\"Toyota\",\"customerId\":2}"); // Убедитесь, что JSON формат соответствует
        verify(writer).flush();
    }

    @Test
    void testDoGetWithoutId() throws Exception {
        // Setup
        when(request.getParameter("id")).thenReturn(null);
        Car car = new Car();
        car.setId(1);
        car.setModel("Toyota");
        car.setCustomerId(2);
        List<Car> cars = Collections.singletonList(car);
        when(carService.getAllCars()).thenReturn(cars);

        // Execute
        carServlet.doGet(request, response);

        // Verify
        verify(response).setContentType("application/json");
        verify(writer).write("[{\"id\":1,\"model\":\"Toyota\",\"customerId\":2}]"); // Убедитесь, что JSON формат соответствует
        verify(writer).flush();
    }

    @Test
    void testDoPost() throws Exception {
        // Setup
        String carJson = "{\"id\":1,\"model\":\"Toyota\",\"customerId\":2}";
        CarDTO carDTO = new CarDTO();
        carDTO.setId(1);
        carDTO.setModel("Toyota");
        carDTO.setCustomerId(2);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(carJson)));

        // Execute
        carServlet.doPost(request, response);

        // Verify
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(writer).write("{\"message\":\"Car added successfully\"}");
        verify(carService).addCar(any(Car.class)); // Преобразование CarDTO в Car должно быть проверено
    }

    @Test
    void testDoPut() throws Exception {
        // Setup
        String carJson = "{\"id\":1,\"model\":\"Toyota\",\"customerId\":2}";
        CarDTO carDTO = new CarDTO();
        carDTO.setId(1);
        carDTO.setModel("Toyota");
        carDTO.setCustomerId(2);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(carJson)));

        // Execute
        carServlet.doPut(request, response);

        // Verify
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write("{\"message\":\"Car updated successfully\"}");
        verify(carService).updateCar(any(Car.class)); // Преобразование CarDTO в Car должно быть проверено
    }

    @Test
    void testDoDelete() throws Exception {
        // Setup
        when(request.getParameter("id")).thenReturn("1");

        // Execute
        carServlet.doDelete(request, response);

        // Verify
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write("{\"message\":\"Car deleted successfully\"}");
        verify(carService).deleteCar(1);
    }
}
