package dto;

import entity.Car;
import entity.Customer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerDTO {

    private int id;
    private String name;
    private String email;
    private List<CarDTO> cars;

    // Геттеры и сеттеры для полей
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<CarDTO> getCars() {
        return cars;
    }

    public void setCars(List<CarDTO> cars) {
        this.cars = cars;
    }

    // Метод для преобразования сущности в DTO
    public static CustomerDTO fromEntity(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());

        List<CarDTO> carDTOs = new ArrayList<>();
        if (customer.getCars() != null) {
            for (Car car : customer.getCars()) {
                carDTOs.add(CarDTO.fromEntity(car));
            }
        }
        dto.setCars(carDTOs);

        return dto;
    }

    // Метод для преобразования DTO в сущность
    public static Customer toEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());

        List<Car> cars = new ArrayList<>();
        if (dto.getCars() != null) {
            for (CarDTO carDTO : dto.getCars()) {
                cars.add(carDTO.toEntity()); // Используем метод toEntity класса CarDTO
            }
        }
        customer.setCars(cars);

        return customer;
    }
}




