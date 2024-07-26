package ru.tolstykh.servlets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
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
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        // Параметр id отсутствует
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
        // Подготовка некорректного JSON (не закрытый JSON)
        String invalidJson = "{\"name\":\"John Doe\""; // Некорректный JSON
        BufferedReader reader = new BufferedReader(new StringReader(invalidJson));
        when(request.getReader()).thenReturn(reader);

        // Вызов метода
        mechanicServlet.doPost(request, response);

        // Проверка поведения
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST); // Ожидаемый статус 400
        verify(response).setContentType("application/json");
        verify(writer).write("{\"error\":\"Invalid JSON format\"}");
        verify(writer).flush();
    }
    @Test
    void testDoPutHandlesSQLException() throws Exception {
        // Given
        String mechanicJson = "{\"id\":1,\"name\":\"John Doe\"}";
        BufferedReader reader = new BufferedReader(new StringReader(mechanicJson));
        when(request.getReader()).thenReturn(reader);

        // Mock the conversion method
        doThrow(new SQLException("Database error")).when(mechanicService).updateMechanic(any(Mechanic.class));

        // When
        mechanicServlet.doPut(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(response).setContentType("application/json"); // Проверка установки типа контента
        verify(response.getWriter()).write("{\"error\":\"Database error\"}");
        verify(response.getWriter()).flush();
    }
    @Test
    void testJsonToMechanicDTOSuccess() throws IOException {

        String validJson = "{\"id\":1,\"name\":\"John Doe\"}";


        MechanicDTO mechanicDTO = mechanicServlet.jsonToMechanicDTO(validJson);


        assertNotNull(mechanicDTO);
        assertEquals(1, mechanicDTO.getId());
        assertEquals("John Doe", mechanicDTO.getName());
    }

    @Test
    void testJsonToMechanicDTOInvalidJson() {

        String invalidJson = "{\"id\":\"not-an-int\",\"name\":\"John Doe\"}"; // id не является числом


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            mechanicServlet.jsonToMechanicDTO(invalidJson);
        });


        assertTrue(exception.getCause() instanceof JsonMappingException);
        assertEquals("Invalid JSON format", exception.getMessage());
    }
    @Test
    void testInit() throws ServletException {
        // Устанавливаем реальные параметры для MechanicRepository
        String jdbcUrl = "jdbc:postgresql://localhost:5432/myDataBase";
        String username = "postgres";
        String password = "postgres";


        mechanicServlet.init();


        assertNotNull(mechanicServlet.mechanicService, "MechanicService should be initialized");


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


}










