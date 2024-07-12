package entity;

import entity.Mechanic;

import java.util.List;

public class RepairOrder {
    private long id;
    private String description;
    private long carId;
    private List<Mechanic> mechanics;

    public List<Mechanic> getMechanics() {
        return mechanics;
    }

    public void setMechanics(List<Mechanic> mechanics) {
        this.mechanics = mechanics;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }





    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public RepairOrder(long id, String description, long carId, List<Mechanic> mechanics) {
        this.id = id;
        this.description = description;
        this.carId = carId;
        this.mechanics = mechanics;
    }
}
