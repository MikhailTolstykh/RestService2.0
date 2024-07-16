package servlets;

import dto.MechanicDTO;
import entity.Mechanic;
import repository.MechanicRepository;
import service.MechanicService;
import service.MechanicServiceInterface;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class MechanicServlet extends HttpServlet {
    private MechanicServiceInterface mechanicService;

    @Override
    public void init() throws ServletException {

            MechanicRepository mechanicRepository = new MechanicRepository();
            mechanicService = new MechanicService(mechanicRepository);

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
                out.write("[");
                for (int i = 0; i < mechanics.size(); i++) {
                    MechanicDTO mechanicDTO = MechanicDTO.fromEntity(mechanics.get(i));
                    out.write(mechanicDTOToJson(mechanicDTO));
                    if (i < mechanics.size() - 1) {
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
        MechanicDTO mechanicDTO = jsonToMechanicDTO(request.getReader().lines().collect(Collectors.joining()));

        try {
            Mechanic mechanic = mechanicDTO.toEntity(); // Вызов метода toEntity() через экземпляр объекта
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
        MechanicDTO mechanicDTO = jsonToMechanicDTO(request.getReader().lines().collect(Collectors.joining()));

        try {
            Mechanic mechanic = mechanicDTO.toEntity(); // Вызов метода toEntity() через экземпляр объекта
            mechanicService.updateMechanic(mechanic);
            response.getWriter().write("{\"message\":\"Mechanic updated successfully\"}");
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
                int mechanicId = Integer.parseInt(id);
                mechanicService.deleteMechanic(mechanicId);
                response.getWriter().write("{\"message\":\"Mechanic deleted successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Mechanic ID is required\"}");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
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
