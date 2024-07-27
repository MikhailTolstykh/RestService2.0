package ru.tolstykh.dto;

import org.junit.jupiter.api.Test;
import ru.tolstykh.entity.Mechanic;

import static org.junit.jupiter.api.Assertions.*;

class MechanicDTOTest {

    @Test
    void testGettersAndSetters() {
        MechanicDTO mechanicDTO = new MechanicDTO();
        mechanicDTO.setId(1);
        mechanicDTO.setName("John Doe");

        assertEquals(1, mechanicDTO.getId());
        assertEquals("John Doe", mechanicDTO.getName());
    }

    @Test
    void testFromEntity() {
        Mechanic mechanic = new Mechanic();
        mechanic.setId(1);
        mechanic.setName("John Doe");

        MechanicDTO mechanicDTO = MechanicDTO.fromEntity(mechanic);

        assertEquals(1, mechanicDTO.getId());
        assertEquals("John Doe", mechanicDTO.getName());
    }

    @Test
    void testToEntity() {
        MechanicDTO mechanicDTO = new MechanicDTO();
        mechanicDTO.setId(1);
        mechanicDTO.setName("John Doe");

        Mechanic mechanic = mechanicDTO.toEntity();

        assertEquals(1, mechanic.getId());
        assertEquals("John Doe", mechanic.getName());
    }

    @Test
    void testFromEntityWithNullValues() {
        Mechanic mechanic = new Mechanic();
        mechanic.setId(0);  // ID по умолчанию
        mechanic.setName(null);  // Имя по умолчанию

        MechanicDTO mechanicDTO = MechanicDTO.fromEntity(mechanic);

        assertEquals(0, mechanicDTO.getId());
        assertNull(mechanicDTO.getName());
    }

    @Test
    void testToEntityWithNullValues() {
        MechanicDTO mechanicDTO = new MechanicDTO();
        mechanicDTO.setId(0);  // ID по умолчанию
        mechanicDTO.setName(null);  // Имя по умолчанию

        Mechanic mechanic = mechanicDTO.toEntity();

        assertEquals(0, mechanic.getId());
        assertNull(mechanic.getName());
    }
}
