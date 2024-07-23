package ru.tolstykh.dto;

import ru.tolstykh.entity.Car;

public class CarDTO {

    private int id;
    private String model;
    private int customerId;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    // Метод для преобразования сущности Car в DTO CarDTO
    public static CarDTO fromEntity(Car car) {
        CarDTO carDTO = new CarDTO();
        carDTO.setId(car.getId());
        carDTO.setModel(car.getModel());
        carDTO.setCustomerId(car.getCustomerId());
        return carDTO;
    }

    // Метод для преобразования DTO CarDTO в сущность Car
    public Car toEntity() {
        Car car = new Car();
        car.setId(this.id);
        car.setModel(this.model);
        car.setCustomerId(this.customerId);
        return car;
    }
}



