package ru.tolstykh.servlets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tolstykh.dto.MechanicDTO;
import ru.tolstykh.entity.Mechanic;
import ru.tolstykh.service.MechanicServiceInterface;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class MechanicServletTest {
    private StringWriter stringWriter;

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

        mechanicServlet = new MechanicServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);

        MockitoAnnotations.openMocks(this);
        when(response.getWriter()).thenReturn(writer);

    }




    @Test
    void testDoDeleteSQLException() throws Exception {

        String id = "123";
        when(request.getParameter("id")).thenReturn(id);


        doThrow(new SQLException("Database error")).when(mechanicService).deleteMechanic(anyInt());


        mechanicServlet.doDelete(request, response);


        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(writer).write("{\"error\":\"Database error\"}");
        verify(writer).flush();
    }



    @Test
    void shouldReturnInternalServerErrorWhenSQLExceptionIsThrownInPost() throws IOException, SQLException {
        // Mocking the request JSON data
        String json = "{\"name\":\"John Doe\"}";
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        // Mocking the conversion from JSON to DTO and entity
        MechanicDTO mechanicDTO = new MechanicDTO();
        mechanicDTO.setName("John Doe");
        Mechanic mechanic = mechanicDTO.toEntity();

        // Mocking the behavior of the service to throw an SQLException
        doThrow(new SQLException("Database error")).when(mechanicService).addMechanic(any(Mechanic.class));

        try {
            mechanicServlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        // Verify the response status and error message
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(writer).write("{\"error\":\"Database error\"}");
    }

    @Test
    void testDoGetWithId() throws Exception {

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

        when(request.getParameter("id")).thenReturn(null);
        Mechanic mechanic = new Mechanic();
        mechanic.setId(1);
        mechanic.setName("John Doe");
        List<Mechanic> mechanics = Collections.singletonList(mechanic);
        when(mechanicService.getAllMechanics()).thenReturn(mechanics);


        mechanicServlet.doGet(request, response);


        verify(response).setContentType("application/json");
        verify(writer).write("[{\"id\":1,\"name\":\"John Doe\"}]");
        verify(writer).flush();
    }

    @Test
    void testDoPost() throws Exception {

        String mechanicJson = "{\"id\":1,\"name\":\"John Doe\"}";
        MechanicDTO mechanicDTO = new MechanicDTO();
        mechanicDTO.setId(1);
        mechanicDTO.setName("John Doe");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(mechanicJson)));


        mechanicServlet.doPost(request, response);


        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(writer).write("{\"message\":\"Mechanic added successfully\"}");
        verify(mechanicService).addMechanic(any(Mechanic.class));
    }

    @Test
    void testDoPut() throws Exception {

        String mechanicJson = "{\"id\":1,\"name\":\"John Doe\"}";
        MechanicDTO mechanicDTO = new MechanicDTO();
        mechanicDTO.setId(1);
        mechanicDTO.setName("John Doe");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(mechanicJson)));

        mechanicServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write("{\"message\":\"Mechanic updated successfully\"}");
        verify(mechanicService).updateMechanic(any(Mechanic.class));
    }

    @Test
    void testDoDelete() throws Exception {

        when(request.getParameter("id")).thenReturn("1");

        mechanicServlet.doDelete(request, response);


        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write("{\"message\":\"Mechanic deleted successfully\"}");
        verify(mechanicService).deleteMechanic(1);


    }

    @Test
    void testHandleBadRequest() throws IOException {
        // Arrange
        String errorMessage = "{\"error\":\"Mechanic ID is required\"}";

        // Act
        mechanicServlet.handleBadRequest(response, errorMessage);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("application/json"); // Проверяем установку типа контента
        verify(writer).write(errorMessage);
        verify(writer).flush(); // Проверяем, что flush() был вызван
    }

    @Test
    void testHandleInternalServerError() throws IOException {
        // Arrange
        String errorMessage = "Database error";

        // Act
        mechanicServlet.handleInternalServerError(response, errorMessage);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(response).setContentType("application/json"); // Проверяем установку типа контента
        verify(writer).write("{\"error\":\"" + errorMessage + "\"}");
        verify(writer).flush(); // Проверяем, что flush() был вызван
    }


    @Test
    void testDoGetWithSQLException() throws Exception {

        when(request.getParameter("id")).thenReturn("1");


        MechanicServiceInterface mechanicService = mock(MechanicServiceInterface.class);
        when(mechanicService.getMechanicById(anyInt())).thenThrow(new SQLException("Database error"));


        mechanicServlet.mechanicService = mechanicService;


        mechanicServlet.doGet(request, response);


        verify(writer).write("{\"error\":\"Database error\"}");
        verify(writer).flush();  // Проверяем, что flush() был вызван
    }

}