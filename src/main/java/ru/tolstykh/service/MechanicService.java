package ru.tolstykh.service;

import ru.tolstykh.entity.Car;
import ru.tolstykh.entity.Mechanic;
import ru.tolstykh.repository.MechanicInterface;

import java.sql.SQLException;
import java.util.List;

public class MechanicService implements MechanicServiceInterface {
    private final MechanicInterface mechanicRepository;



    public MechanicService(MechanicInterface mechanicRepository) {
        this.mechanicRepository = mechanicRepository;
    }

    @Override
    public void addMechanic(Mechanic mechanic) throws SQLException {
        mechanicRepository.addMechanic(mechanic);
    }

    @Override
    public Mechanic getMechanicById(int id) throws SQLException {
        Mechanic mechanic = mechanicRepository.getMechanicById(id);
        if (mechanic != null) {
            List<Car> cars = mechanicRepository.getCarsByMechanicId(id);
            mechanic.setCars(cars);
        }
        return mechanic;
    }

    @Override
    public void updateMechanic(Mechanic mechanic) throws SQLException {
        mechanicRepository.updateMechanic(mechanic);
    }

    @Override
    public void deleteMechanic(int id) throws SQLException {
        mechanicRepository.deleteMechanic(id);
    }

    @Override
    public List<Mechanic> getAllMechanics() throws SQLException {
        List<Mechanic> mechanics = mechanicRepository.getAllMechanics();
        for (Mechanic mechanic : mechanics) {
            List<Car> cars = mechanicRepository.getCarsByMechanicId(mechanic.getId());
            mechanic.setCars(cars);
        }
        return mechanics;
    }

    @Override
    public List<Car> getCarsByMechanicId(int mechanicId) throws SQLException {
        return mechanicRepository.getCarsByMechanicId(mechanicId);
    }


    @Override
    public List<Mechanic> getMechanicsByCarId(int carId) throws SQLException {
        return mechanicRepository.getMechanicsByCarId(carId);
    }
}









