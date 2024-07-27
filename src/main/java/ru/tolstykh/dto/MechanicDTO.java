package ru.tolstykh.dto;

import ru.tolstykh.entity.Mechanic;

public class MechanicDTO {
    private int id;
    private String name;


    // Геттеры и сеттеры

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


    public static MechanicDTO fromEntity(Mechanic mechanic) {
        MechanicDTO mechanicDTO = new MechanicDTO();
        mechanicDTO.setId(mechanic.getId());
        mechanicDTO.setName(mechanic.getName());

        return mechanicDTO;
    }

    public Mechanic toEntity() {
        Mechanic mechanic = new Mechanic();
        mechanic.setId(this.id);
        mechanic.setName(this.name);
        return mechanic;
    }

}




