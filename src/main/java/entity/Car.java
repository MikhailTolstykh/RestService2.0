package entity;

import entity.Customer;

import java.util.List;

public class Car {
    private long id;
    private String model;
    private Customer customer;
    private long customerId;


    public Car() {

    }

    public Car(Long id, String model, Customer customer, Long customerId) {
        this.id = id;
        this.model = model;
        this.customer = customer;
        this.customerId = customerId;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getCustomerId() {

        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

}