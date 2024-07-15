package repository;

import entity.Car;
import entity.Mechanic;

import java.sql.SQLException;
import java.util.List;

public interface MechanicInterface {

    void addMechanic(Mechanic mechanic) throws SQLException;

    Mechanic getMechanicById(int id) throws SQLException;

    void updateMechanic(Mechanic mechanic) throws SQLException;

    void deleteMechanic(int id) throws SQLException;

    List<Mechanic> getAllMechanics() throws SQLException;

    List<Car> getCarsByMechanicId(int mechanicId) throws SQLException;

    public List<Mechanic> getMechanicsByCarId(int CarId) throws SQLException;

}
