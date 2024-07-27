package ru.tolstykh.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.tolstykh.entity.Mechanic;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MechanicRepositoryTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private Statement mockStatement;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private MechanicRepository mechanicRepository;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this); // Инициализация моков перед выполнением тестов
        mechanicRepository = new MechanicRepository(mockConnection);
        createTables(); // Создание таблиц в замокированном окружении
        cleanUp();
    }

    @BeforeEach
    void cleanUp() throws SQLException {
        Mockito.reset(mockConnection, mockStatement, mockPreparedStatement, mockResultSet); // Сброс состояния моков перед каждым тестом
    }

    private void createTables() throws SQLException {
        String createCustomerTableSQL = """
                CREATE TABLE IF NOT EXISTS customer (
                    id BIGSERIAL NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL
                );
                """;

        String createMechanicTableSQL = """
                CREATE TABLE IF NOT EXISTS mechanic (
                    id BIGSERIAL NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL
                );
                """;

        String createCarTableSQL = """
                CREATE TABLE IF NOT EXISTS car (
                    id BIGSERIAL NOT NULL PRIMARY KEY,
                    model TEXT NOT NULL,
                    customer_id BIGINT NOT NULL
                );
                """;

        String createCarMechanicTableSQL = """
                CREATE TABLE IF NOT EXISTS car_mechanic (
                    car_id BIGINT NOT NULL,
                    mechanic_id BIGINT NOT NULL,
                    PRIMARY KEY (car_id, mechanic_id),
                    FOREIGN KEY (car_id) REFERENCES car(id),
                    FOREIGN KEY (mechanic_id) REFERENCES mechanic(id)
                );
                """;

        // Мокирование создания Statement и выполнения SQL-запросов
        Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);
        Mockito.when(mockStatement.execute(anyString())).thenReturn(true); // Возвращаем true для успешного выполнения запросов

        try (Statement statement = mockConnection.createStatement()) {
            statement.execute(createCustomerTableSQL);
            statement.execute(createMechanicTableSQL);
            statement.execute(createCarTableSQL);
            statement.execute(createCarMechanicTableSQL);
        }
    }

    @Test
    void shouldGetAllMechanics() throws SQLException {
        Mechanic mechanic1 = new Mechanic(1, "John Doe");
        Mechanic mechanic2 = new Mechanic(2, "Jane Doe");

        // Мокирование создания Statement и выполнения запроса
        Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);
        Mockito.when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(true, true, false); // Определение последовательности результатов
        Mockito.when(mockResultSet.getInt("id")).thenReturn(mechanic1.getId(), mechanic2.getId()); // Возврат идентификаторов механиков
        Mockito.when(mockResultSet.getString("name")).thenReturn(mechanic1.getName(), mechanic2.getName()); // Возврат имен механиков

        // Выполнение тестируемого метода
        List<Mechanic> mechanics = mechanicRepository.getAllMechanics();

        // Проверка результатов
        assertEquals(2, mechanics.size());
        assertEquals("John Doe", mechanics.get(0).getName());
        assertEquals("Jane Doe", mechanics.get(1).getName());
    }
    @Test
    void testAddMechanic() throws SQLException {
        // Example test case
        Mechanic mechanic = new Mechanic();
        mechanic.setName("John Doe");

        when(mockConnection.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        mechanicRepository.addMechanic(mechanic);

        verify(mockPreparedStatement).setString(1, "John Doe");
        verify(mockPreparedStatement).executeUpdate();
        assertEquals(1, mechanic.getId());
    }

    @Test
    void shouldAddMechanic() throws SQLException {
        Mechanic mechanic = new Mechanic(0, "John Doe");


        ResultSet mockGeneratedKeys = Mockito.mock(ResultSet.class);
        Mockito.when(mockGeneratedKeys.next()).thenReturn(true);
        Mockito.when(mockGeneratedKeys.getInt(1)).thenReturn(1);


        Mockito.when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        Mockito.when(mockPreparedStatement.executeUpdate()).thenReturn(1);


        Mockito.when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);

        mechanicRepository.addMechanic(mechanic);


        assertEquals(1, mechanic.getId(), "Mechanic ID should be set to the generated key.");
    }

    @Test
    void shouldThrowSQLExceptionWhenMechanicNameIsNull() {
        Mechanic mechanic = new Mechanic(0, null);

        try {
            Mockito.when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                    .thenThrow(new SQLException("Mechanic name cannot be null"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        SQLException exception = assertThrows(SQLException.class, () -> mechanicRepository.addMechanic(mechanic));

        assertEquals("Mechanic name cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowSQLExceptionWhenConnectionFails() throws SQLException {
        Mechanic mechanic = new Mechanic(0, "John Doe");


        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenThrow(new SQLException("No suitable driver found"));


        SQLException exception = assertThrows(SQLException.class, () -> mechanicRepository.addMechanic(mechanic));

        assertTrue(exception.getMessage().contains("No suitable driver found"),
                "Expected SQLException with message containing 'No suitable driver found'");
    }

    @Test
    void shouldUpdateMechanic() throws SQLException {
        Mechanic mechanic = new Mechanic(1, "John Doe");

        Mockito.when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockPreparedStatement);
        Mockito.when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        mechanic.setName("Jane Doe");
        mechanicRepository.updateMechanic(mechanic);

        Mockito.verify(mockPreparedStatement).setString(1, "Jane Doe");
        Mockito.verify(mockPreparedStatement).setInt(2, 1);
    }

    @Test
    void shouldDeleteMechanic() throws SQLException {
        int mechanicId = 1;

        Mockito.when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockPreparedStatement);
        Mockito.when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        mechanicRepository.deleteMechanic(mechanicId);

        Mockito.verify(mockPreparedStatement).setInt(1, mechanicId);
    }


    @Test
    void shouldGetMechanicsByCarId() throws SQLException {
        Mechanic mechanic1 = new Mechanic(1, "John Doe");
        Mechanic mechanic2 = new Mechanic(2, "Jane Doe");

        Mockito.when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockPreparedStatement);
        Mockito.when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(true, true, false);
        Mockito.when(mockResultSet.getInt("id")).thenReturn(mechanic1.getId(), mechanic2.getId());
        Mockito.when(mockResultSet.getString("name")).thenReturn(mechanic1.getName(), mechanic2.getName());

        List<Mechanic> mechanics = mechanicRepository.getMechanicsByCarId(1);

        assertNotNull(mechanics);
        assertEquals(2, mechanics.size());
        assertEquals("John Doe", mechanics.get(0).getName());
        assertEquals("Jane Doe", mechanics.get(1).getName());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenDatabaseConfigIsMissing() {
        assertThrows(RuntimeException.class, () -> new MechanicRepository(null, null, null),
                "Database configuration is missing. URL, Username, or Password is null.");

        assertThrows(RuntimeException.class, () -> new MechanicRepository(null, "username", "password"),
                "Database configuration is missing. URL, Username, or Password is null.");

        assertThrows(RuntimeException.class, () -> new MechanicRepository("jdbcUrl", null, "password"),
                "Database configuration is missing. URL, Username, or Password is null.");

        assertThrows(RuntimeException.class, () -> new MechanicRepository("jdbcUrl", "username", null),
                "Database configuration is missing. URL, Username, or Password is null.");
    }

    @Test
    void shouldSetGeneratedIdWhenAddingMechanic() throws SQLException {
        Mechanic mechanic = new Mechanic(0, "John Doe");

        Mockito.when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(true);
        Mockito.when(mockResultSet.getInt(1)).thenReturn(1);

        Mockito.when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);

        mechanicRepository.addMechanic(mechanic);

        assertTrue(mechanic.getId() > 0);
    }

    @Test
    void shouldAddMechanicSuccessfully() throws SQLException {
        Mechanic mechanic = new Mechanic(0, "John Doe");

        Mockito.when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        Mockito.when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(true);
        Mockito.when(mockResultSet.getInt(1)).thenReturn(1);

        Mockito.when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);

        mechanicRepository.addMechanic(mechanic);


        Mockito.when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        Mockito.when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(true);
        Mockito.when(mockResultSet.getInt("id")).thenReturn(mechanic.getId());
        Mockito.when(mockResultSet.getString("name")).thenReturn(mechanic.getName());

        Mechanic fetchedMechanic = mechanicRepository.getMechanicById(mechanic.getId());
        assertNotNull(fetchedMechanic);
    }



    @Test
    void shouldThrowSQLExceptionWhenNameIsNull() {
        Mechanic mechanic = new Mechanic(0, null);

        try {
            Mockito.when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                    .thenThrow(new SQLException("Mechanic name cannot be null"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        SQLException thrown = assertThrows(SQLException.class, () -> mechanicRepository.addMechanic(mechanic));

        assertEquals("Mechanic name cannot be null", thrown.getMessage());
    }

    @Test
    void shouldSetMechanicIdWhenGeneratedKeysAvailable() throws SQLException {
        Mechanic mechanic = new Mechanic(0, "John Doe");

        Mockito.when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(true);
        Mockito.when(mockResultSet.getInt(1)).thenReturn(234);

        Mockito.when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);

        mechanicRepository.addMechanic(mechanic);

        assertEquals(234, mechanic.getId(), "Mechanic ID should be set to the generated key.");
    }

    @Test
    void shouldNotSetIdWhenNoGeneratedKeys() throws SQLException {

        Mechanic mechanic = new Mechanic(0, "John Doe");


        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);


        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);


        mechanicRepository.addMechanic(mechanic);


        assertEquals(0, mechanic.getId(), "Mechanic ID should not be set when no generated keys are returned.");
    }
    @Test
    void shouldReturnNullWhenNotFound() throws SQLException {
        int mechanicId = 1;


        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Результат отсутствует


        Mechanic mechanic = mechanicRepository.getMechanicById(mechanicId);


        assertNull(mechanic, "Mechanic should be null when not found.");
    }
}
