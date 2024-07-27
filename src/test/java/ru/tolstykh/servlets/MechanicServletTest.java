package ru.tolstykh.servlets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonMappingException;
import ru.tolstykh.dto.MechanicDTO;
import ru.tolstykh.entity.Mechanic;
import ru.tolstykh.repository.MechanicRepository;
import ru.tolstykh.service.MechanicService;
import ru.tolstykh.service.MechanicServiceInterface;
import ru.tolstykh.util.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

@PrepareForTest(DatabaseConnection.class)
@MockitoSettings(strictness = Strictness.LENIENT)

@ExtendWith(MockitoExtension.class)
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
        MockitoAnnotations.openMocks(this);
        mechanicServlet = new MechanicServlet();
        mechanicServlet.mechanicService = mechanicService;


        when(response.getWriter()).thenReturn(writer);
    }


    @Test
    void testInit() throws Exception {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = mockStatic(DatabaseConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            mockedDatabaseConnection.when(DatabaseConnection::getConnectionToDataBase).thenReturn(mockConnection);

            MechanicRepository mockRepository = mock(MechanicRepository.class);
            MechanicService mockService = mock(MechanicService.class);

            mechanicServlet.init();

            assertNotNull(mechanicServlet.mechanicService, "MechanicService should be initialized");
        } catch (Exception e) {
            fail("Initialization failed with exception: " + e.getMessage());
        }
    }


    @Test
    void shouldThrowRuntimeExceptionWhenSQLExceptionOccurs() throws Exception {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = Mockito.mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnectionToDataBase)
                    .thenThrow(new SQLException("Database error"));

            assertThrows(RuntimeException.class, () -> mechanicServlet.init(), "Expected RuntimeException to be thrown");
        }
    }

    @Test
    void shouldThrowRuntimeExceptionWhenClassNotFoundExceptionOccurs() throws Exception {
        try (MockedStatic<DatabaseConnection> mockedDatabaseConnection = Mockito.mockStatic(DatabaseConnection.class)) {
            mockedDatabaseConnection.when(DatabaseConnection::getConnectionToDataBase)
                    .thenThrow(new ClassNotFoundException("Class not found"));

            assertThrows(RuntimeException.class, () -> mechanicServlet.init(), "Expected RuntimeException to be thrown");
        }
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
        String json = "{\"name\":\"John Doe\"}";
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);

        MechanicDTO mechanicDTO = new MechanicDTO();
        mechanicDTO.setName("John Doe");
        Mechanic mechanic = mechanicDTO.toEntity();

        doThrow(new SQLException("Database error")).when(mechanicService).addMechanic(any(Mechanic.class));

        try {
            mechanicServlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

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

        mechanicServlet.doGet(request, response);

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
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(mechanicJson)));

        mechanicServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(writer).write("{\"message\":\"Mechanic added successfully\"}");
        verify(mechanicService).addMechanic(any(Mechanic.class));
    }

    @Test
    void testDoPut() throws Exception {
        String mechanicJson = "{\"id\":1,\"name\":\"John Doe\"}";
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
        String errorMessage = "{\"error\":\"Mechanic ID is required\"}";

        mechanicServlet.handleBadRequest(response, errorMessage);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("application/json");
        verify(writer).write(errorMessage);
        verify(writer).flush();
    }

    @Test
    void testHandleInternalServerError() throws IOException {
        String errorMessage = "Database error";

        mechanicServlet.handleInternalServerError(response, errorMessage);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(response).setContentType("application/json");
        verify(writer).write("{\"error\":\"" + errorMessage + "\"}");
        verify(writer).flush();
    }

    @Test
    void testDoGetWithSQLException() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        when(mechanicService.getMechanicById(anyInt())).thenThrow(new SQLException("Database error"));

        mechanicServlet.doGet(request, response);

        verify(writer).write("{\"error\":\"Database error\"}");
        verify(writer).flush();
    }

    @Test
    void testDoDeleteWithNonExistentMechanic() throws Exception {
        when(request.getParameter("id")).thenReturn("999");
        doThrow(new SQLException("Mechanic not found")).when(mechanicService).deleteMechanic(anyInt());

        mechanicServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(writer).write("{\"error\":\"Mechanic not found\"}");
    }

    @Test
    void testDoDeleteWithoutId() throws Exception {
        when(request.getParameter("id")).thenReturn(null);

        mechanicServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("application/json");
        verify(writer).write("{\"error\":\"Mechanic ID is required\"}");
        verify(writer).flush();
    }

    @Test
    void testDoDeleteWithValidId() throws Exception {
        when(request.getParameter("id")).thenReturn("1");

        mechanicServlet.doDelete(request, response);

        verify(mechanicService).deleteMechanic(1);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write("{\"message\":\"Mechanic deleted successfully\"}");
        verify(writer).flush();
    }


    @Test
    void testDoPostWithInvalidJson() throws Exception {
        String invalidJson = "{\"invalidField\":\"value\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(invalidJson)));

        mechanicServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("application/json");
        verify(writer).write("{\"error\":\"Invalid JSON format\"}");
        verify(writer).flush();
    }
}