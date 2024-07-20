package ru.tolstykh.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ru.tolstykh.entity.Car;

class CarDTOTest {

    @Test
    void testFromEntity() {
        // Создание тестовой сущности Car
        Car car = new Car();
        car.setId(1);
        car.setModel("Tesla Model S");
        car.setCustomerId(123);

        // Преобразование Car в CarDTO
        CarDTO carDTO = CarDTO.fromEntity(car);

        // Проверка значений в CarDTO
        assertEquals(1, carDTO.getId());
        assertEquals("Tesla Model S", carDTO.getModel());
        assertEquals(123, carDTO.getCustomerId());
    }

    @Test
    void testToEntity() {
        // Создание тестового DTO CarDTO
        CarDTO carDTO = new CarDTO();
        carDTO.setId(1);
        carDTO.setModel("Tesla Model S");
        carDTO.setCustomerId(123);

        // Преобразование CarDTO в Car
        Car car = carDTO.toEntity();

        // Проверка значений в Car
        assertEquals(1, car.getId());
        assertEquals("Tesla Model S", car.getModel());
        assertEquals(123, car.getCustomerId());
    }

    @Test
    void testGettersAndSetters() {
        // Создание тестового DTO CarDTO
        CarDTO carDTO = new CarDTO();
        carDTO.setId(1);
        carDTO.setModel("Tesla Model S");
        carDTO.setCustomerId(123);

        // Проверка значений через геттеры
        assertEquals(1, carDTO.getId());
        assertEquals("Tesla Model S", carDTO.getModel());
        assertEquals(123, carDTO.getCustomerId());
    }
}
