package entity;

import entity.Customer;

import java.util.List;

public class Car {
    private long id;
    private String model;
    private Customer customer;
    private long customerId;
    private List<RepairOrder> repairOrders;

    public Car() {

    }

    public Car(Long id, String model, Customer customer, Long customerId, List<RepairOrder> repairOrders) {
        this.id = id;
        this.model = model;
        this.customer = customer;
        this.customerId = customerId;
        this.repairOrders = repairOrders;
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

    public List<RepairOrder> getRepairOrders() {
        return repairOrders;
    }

    public void setRepairOrders(List<RepairOrder> repairOrders) {
        this.repairOrders = repairOrders;
    }
}
