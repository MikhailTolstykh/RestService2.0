package ru.tolstykh.servlets;

import ru.tolstykh.dto.MechanicDTO;
import ru.tolstykh.entity.Mechanic;
import ru.tolstykh.repository.MechanicRepository;
import ru.tolstykh.service.MechanicService;
import ru.tolstykh.service.MechanicServiceInterface;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
@WebServlet(name="MechanicServlet ",urlPatterns = "/mechanic/*")
public class MechanicServlet extends HttpServlet {

    MechanicServiceInterface mechanicService;

    @Override
    public void init() throws ServletException {

        mechanicService = new MechanicService(new MechanicRepository());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            if (id != null) {
                int mechanicId = Integer.parseInt(id);
                Mechanic mechanic = mechanicService.getMechanicById(mechanicId);
                MechanicDTO mechanicDTO = MechanicDTO.fromEntity(mechanic);
                out.write(mechanicDTOToJson(mechanicDTO));
            } else {
                List<Mechanic> mechanics = mechanicService.getAllMechanics();
                String jsonArray = mechanics.stream()
                        .map(MechanicDTO::fromEntity)
                        .map(this::mechanicDTOToJson)
                        .collect(Collectors.joining(",", "[", "]"));
                out.write(jsonArray);
            }
            out.flush();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"" + e.getMessage() + "\"}");
            out.flush();  // И здесь тоже
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = new BufferedReader(request.getReader()).lines().collect(Collectors.joining());

        MechanicDTO mechanicDTO;
        try {
            mechanicDTO = jsonToMechanicDTO(json);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid JSON format\"}");
            response.getWriter().flush();
            return;
        }

        try {
            Mechanic mechanic = mechanicDTO.toEntity();
            mechanicService.addMechanic(mechanic);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"message\":\"Mechanic added successfully\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = new BufferedReader(request.getReader()).lines().collect(Collectors.joining());
        MechanicDTO mechanicDTO = jsonToMechanicDTO(json);

        try {
            Mechanic mechanic = mechanicDTO.toEntity();
            mechanicService.updateMechanic(mechanic);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\":\"Mechanic updated successfully\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        if (id == null) {
            handleBadRequest(response, "{\"error\":\"Mechanic ID is required\"}");
            return; // Ранний выход, если ID отсутствует
        }

        try {
            int mechanicId = Integer.parseInt(id);
            mechanicService.deleteMechanic(mechanicId);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\":\"Mechanic deleted successfully\"}");
            response.getWriter().flush();
        } catch (SQLException e) {
            handleInternalServerError(response, e.getMessage());

        }
    }

    void handleBadRequest(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json"); // Устанавливаем тип контента
        response.getWriter().write(message);
        response.getWriter().flush();
    }

    void handleInternalServerError(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json"); // Устанавливаем тип контента
        response.getWriter().write("{\"error\":\"" + errorMessage + "\"}");
        response.getWriter().flush();
    }

    private String mechanicDTOToJson(MechanicDTO mechanicDTO) {
        return String.format("{\"id\":%d,\"name\":\"%s\"}", mechanicDTO.getId(), mechanicDTO.getName());
    }

    private MechanicDTO jsonToMechanicDTO(String json) {
        MechanicDTO mechanicDTO = new MechanicDTO();
        json = json.replace("{", "").replace("}", "").replace("\"", "");
        String[] fields = json.split(",");
        for (String field : fields) {
            String[] keyValue = field.split(":");
            switch (keyValue[0]) {
                case "id":
                    mechanicDTO.setId(Integer.parseInt(keyValue[1]));
                    break;
                case "name":
                    mechanicDTO.setName(keyValue[1]);
                    break;
            }
        }
        return mechanicDTO;
    }
}