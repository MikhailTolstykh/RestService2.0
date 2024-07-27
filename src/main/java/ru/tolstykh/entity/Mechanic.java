package ru.tolstykh.entity;

import java.util.List;

public class Mechanic {
    private int id;
    private String name;
    private List<Car> cars;

    public Mechanic(String name) {
        this.name = name;
    }

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

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }


    public Mechanic() {
    }

    public Mechanic(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
