package service;

import entity.Car;
import entity.Mechanic;

import java.sql.SQLException;
import java.util.List;

public interface MechanicServiceInterface {
    void addMechanic(Mechanic mechanic) throws SQLException;
    Mechanic getMechanicById(int id) throws SQLException;
    void updateMechanic(Mechanic mechanic) throws SQLException;
    void deleteMechanic(int id) throws SQLException;
    List<Mechanic> getAllMechanics() throws SQLException;
    List<Car> getCarsByMechanicId(int mechanicId) throws SQLException;
    void connectCarToMechanic(int mechanicId, int carId) throws SQLException;
    void deleteConnectCarFromMechanic(int mechanicId, int carId) throws SQLException;
    List<Mechanic> getMechanicsByCarId(int carId) throws SQLException;



}
