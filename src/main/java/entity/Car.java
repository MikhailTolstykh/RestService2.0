package entity;

import entity.Customer;

import java.util.List;

public class Car {
    private int id;
    private String model;
    private Customer customer;
    private int customerId;
    private List<Mechanic> mechanics;

    public List<Mechanic> getMechanics() {
        return mechanics;
    }

    public void setMechanics(List<Mechanic> mechanics) {
        this.mechanics = mechanics;
    }

    public Car() {

    }

    public Car(int id, String model, Customer customer, Integer customerId) {
        this.id = id;
        this.model = model;
        this.customer = customer;
        this.customerId = customerId;
    }

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


    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getCustomerId() {

        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

}