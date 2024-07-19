package ru.tolstykh.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ru.tolstykh.entity.Car;
import ru.tolstykh.entity.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerDTOTest {

    @Test
    void testFromEntity() {

        Car car1 = new Car();
        car1.setId(1);
        car1.setModel("Tesla Model S");
        car1.setCustomerId(101);

        Car car2 = new Car();
        car2.setId(2);
        car2.setModel("Audi Q7");
        car2.setCustomerId(101);

        List<Car> cars = new ArrayList<>();
        cars.add(car1);
        cars.add(car2);

        // Создание тестовой сущности Customer
        Customer customer = new Customer();
        customer.setId(1);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setCars(cars);

        // Преобразование Customer в CustomerDTO
        CustomerDTO dto = CustomerDTO.fromEntity(customer);

        // Проверка значений в CustomerDTO
        assertEquals(1, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("john.doe@example.com", dto.getEmail());
        assertNotNull(dto.getCars());
        assertEquals(2, dto.getCars().size());

        // Проверка значений в CarDTO внутри CustomerDTO
        CarDTO carDTO1 = dto.getCars().get(0);
        assertEquals(1, carDTO1.getId());
        assertEquals("Tesla Model S", carDTO1.getModel());
        assertEquals(101, carDTO1.getCustomerId());

        CarDTO carDTO2 = dto.getCars().get(1);
        assertEquals(2, carDTO2.getId());
        assertEquals("Audi Q7", carDTO2.getModel());
        assertEquals(101, carDTO2.getCustomerId());
    }

    @Test
    void testToEntity() {
        // Создание тестовых CarDTO
        CarDTO carDTO1 = new CarDTO();
        carDTO1.setId(1);
        carDTO1.setModel("Tesla Model S");
        carDTO1.setCustomerId(101);

        CarDTO carDTO2 = new CarDTO();
        carDTO2.setId(2);
        carDTO2.setModel("Audi Q7");
        carDTO2.setCustomerId(101);

        List<CarDTO> carDTOs = new ArrayList<>();
        carDTOs.add(carDTO1);
        carDTOs.add(carDTO2);

        // Создание тестового CustomerDTO
        CustomerDTO dto = new CustomerDTO();
        dto.setId(1);
        dto.setName("John Doe");
        dto.setEmail("john.doe@example.com");
        dto.setCars(carDTOs);

        // Преобразование CustomerDTO в Customer
        Customer customer = CustomerDTO.toEntity(dto);

        // Проверка значений в Customer
        assertEquals(1, customer.getId());
        assertEquals("John Doe", customer.getName());
        assertEquals("john.doe@example.com", customer.getEmail());
        assertNotNull(customer.getCars());
        assertEquals(2, customer.getCars().size());

        // Проверка значений в Car внутри Customer
        Car car1 = customer.getCars().get(0);
        assertEquals(1, car1.getId());
        assertEquals("Tesla Model S", car1.getModel());
        assertEquals(101, car1.getCustomerId());

        Car car2 = customer.getCars().get(1);
        assertEquals(2, car2.getId());
        assertEquals("Audi Q7", car2.getModel());
        assertEquals(101, car2.getCustomerId());
    }

    @Test
    void testGettersAndSetters() {
        // Создание тестового CustomerDTO
        CustomerDTO dto = new CustomerDTO();
        dto.setId(1);
        dto.setName("John Doe");
        dto.setEmail("john.doe@example.com");

        // Создание тестовых CarDTO
        CarDTO carDTO1 = new CarDTO();
        carDTO1.setId(1);
        carDTO1.setModel("Tesla Model S");
        carDTO1.setCustomerId(101);

        CarDTO carDTO2 = new CarDTO();
        carDTO2.setId(2);
        carDTO2.setModel("Audi Q7");
        carDTO2.setCustomerId(101);

        List<CarDTO> carDTOs = new ArrayList<>();
        carDTOs.add(carDTO1);
        carDTOs.add(carDTO2);

        dto.setCars(carDTOs);

        // Проверка значений через геттеры
        assertEquals(1, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("john.doe@example.com", dto.getEmail());
        assertNotNull(dto.getCars());
        assertEquals(2, dto.getCars().size());

        CarDTO car1 = dto.getCars().get(0);
        assertEquals(1, car1.getId());
        assertEquals("Tesla Model S", car1.getModel());
        assertEquals(101, car1.getCustomerId());

        CarDTO car2 = dto.getCars().get(1);
        assertEquals(2, car2.getId());
        assertEquals("Audi Q7", car2.getModel());
        assertEquals(101, car2.getCustomerId());
    }
}
