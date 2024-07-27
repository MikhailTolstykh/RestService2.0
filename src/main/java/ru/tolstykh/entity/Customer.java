package ru.tolstykh.entity;

import java.util.List;

public class Customer {
    private int id;
    private String name;
    private String email;
    private List<Car> cars;

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer(int id, String name, String email, List<Car> cars) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cars = cars;
    }

    public Customer() {

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

    public Customer(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;

    }

    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }


}
