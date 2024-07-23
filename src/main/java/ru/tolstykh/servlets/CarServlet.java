package ru.tolstykh.servlets;

import ru.tolstykh.dto.CarDTO;
import ru.tolstykh.entity.Car;
import ru.tolstykh.service.CarService;
import ru.tolstykh.service.CarServiceInterface;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name="CarServlet",urlPatterns = "/car/*")
public class CarServlet extends HttpServlet {
    private CarServiceInterface carService;
    public CarServlet() {
        super();
    }


    @Override
    public void init() throws ServletException {
        carService = new CarService(); // Инициализируем CarService
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Received GET request");
        String id = request.getParameter("id");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            if (id != null) {
                int carId = Integer.parseInt(id);
                Car car = carService.getCarById(carId);
                CarDTO carDTO = CarDTO.fromEntity(car);
                out.write(carDTOToJson(carDTO));
            } else {
                List<Car> cars = carService.getAllCars();
                StringBuilder jsonResult = new StringBuilder("[");
                for (int i = 0; i < cars.size(); i++) {
                    CarDTO carDTO = CarDTO.fromEntity(cars.get(i));
                    jsonResult.append(carDTOToJson(carDTO));
                    if (i < cars.size() - 1) {
                        jsonResult.append(",");
                    }
                }
                jsonResult.append("]");
                out.write(jsonResult.toString());
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"" + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CarDTO carDTO = jsonToCarDTO(request.getReader().lines().collect(Collectors.joining()));

        try {
            Car car = carDTO.toEntity();
            carService.addCar(car);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"message\":\"Car added successfully\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CarDTO carDTO = jsonToCarDTO(request.getReader().lines().collect(Collectors.joining()));

        try {
            Car car = carDTO.toEntity();
            carService.updateCar(car);
            response.setStatus(HttpServletResponse.SC_OK); // Добавьте эту строку
            response.getWriter().write("{\"message\":\"Car updated successfully\"}");
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
                int carId = Integer.parseInt(id);
                carService.deleteCar(carId);
                response.setStatus(HttpServletResponse.SC_OK); // Добавьте эту строку
                response.getWriter().write("{\"message\":\"Car deleted successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Car ID is required\"}");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }


    private String carDTOToJson(CarDTO carDTO) {
        return String.format("{\"id\":%d,\"model\":\"%s\",\"customerId\":%d}", carDTO.getId(), carDTO.getModel(), carDTO.getCustomerId());
    }

    private CarDTO jsonToCarDTO(String json) {
        CarDTO carDTO = new CarDTO();
        json = json.replace("{", "").replace("}", "").replace("\"", "");
        String[] fields = json.split(",");
        for (String field : fields) {
            String[] keyValue = field.split(":");
            switch (keyValue[0]) {
                case "id":
                    carDTO.setId(Integer.parseInt(keyValue[1]));
                    break;
                case "model":
                    carDTO.setModel(keyValue[1]);
                    break;
                case "customerId":
                    carDTO.setCustomerId(Integer.parseInt(keyValue[1]));
                    break;
            }
        }
        return carDTO;
    }
}