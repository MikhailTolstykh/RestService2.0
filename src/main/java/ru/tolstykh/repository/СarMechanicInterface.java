package ru.tolstykh.repository;

import java.sql.SQLException;

public interface СarMechanicInterface {

    void addCarMechanic(int carId, int mechanicId) throws SQLException;

    public void removeCarMechanic(int carId, int mechanicId) throws SQLException;


    public void deleteCarMechanics(int carId) throws SQLException ;

    public void close () throws SQLException;


}
