package ru.tolstykh.servlets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tolstykh.dto.MechanicDTO;
import ru.tolstykh.entity.Mechanic;
import ru.tolstykh.service.MechanicServiceInterface;

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

class MechanicServletTest {

    @InjectMocks
    private MechanicServlet mechanicServlet;

    @Mock
    private MechanicServiceInterface mechanicService;

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
        Mechanic mechanic = new Mechanic();
        mechanic.setId(1);
        mechanic.setName("John Doe");
        when(mechanicService.getMechanicById(1)).thenReturn(mechanic);

        // Execute
        mechanicServlet.doGet(request, response);

        // Verify
        verify(response).setContentType("application/json");
        verify(writer).write("{\"id\":1,\"name\":\"John Doe\"}");
        verify(writer).flush();
    }

    @Test
    void testDoGetWithoutId() throws Exception {
        // Setup
        when(request.getParameter("id")).thenReturn(null);
        Mechanic mechanic = new Mechanic();
        mechanic.setId(1);
        mechanic.setName("John Doe");
        List<Mechanic> mechanics = Collections.singletonList(mechanic);
        when(mechanicService.getAllMechanics()).thenReturn(mechanics);

        // Execute
        mechanicServlet.doGet(request, response);

        // Verify
        verify(response).setContentType("application/json");
        verify(writer).write("[{\"id\":1,\"name\":\"John Doe\"}]");
        verify(writer).flush();
    }

    @Test
    void testDoPost() throws Exception {
        // Setup
        String mechanicJson = "{\"id\":1,\"name\":\"John Doe\"}";
        MechanicDTO mechanicDTO = new MechanicDTO();
        mechanicDTO.setId(1);
        mechanicDTO.setName("John Doe");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(mechanicJson)));

        // Execute
        mechanicServlet.doPost(request, response);

        // Verify
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(writer).write("{\"message\":\"Mechanic added successfully\"}");
        verify(mechanicService).addMechanic(any(Mechanic.class));
    }

    @Test
    void testDoPut() throws Exception {
        // Setup
        String mechanicJson = "{\"id\":1,\"name\":\"John Doe\"}";
        MechanicDTO mechanicDTO = new MechanicDTO();
        mechanicDTO.setId(1);
        mechanicDTO.setName("John Doe");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(mechanicJson)));

        // Execute
        mechanicServlet.doPut(request, response);

        // Verify
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write("{\"message\":\"Mechanic updated successfully\"}");
        verify(mechanicService).updateMechanic(any(Mechanic.class));
    }

    @Test
    void testDoDelete() throws Exception {
        // Setup
        when(request.getParameter("id")).thenReturn("1");

        // Execute
        mechanicServlet.doDelete(request, response);

        // Verify
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write("{\"message\":\"Mechanic deleted successfully\"}");
        verify(mechanicService).deleteMechanic(1);
    }
}
