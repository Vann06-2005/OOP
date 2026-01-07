package controllers;

import db.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Bus;
public class BusController {

    private static BusController instance;

    private BusController() {}

    public static BusController getInstance() {
        if (instance == null) instance = new BusController();
        return instance;
    }

    public void addBus(Bus bus) {
        String sql = "INSERT INTO buses (bus_number, total_seats, type, is_operational) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bus.getBusNumber());
            stmt.setInt(2, bus.getTotalSeats());
            stmt.setString(3, bus.getType());
            stmt.setBoolean(4, bus.isOperational());
            
            stmt.executeUpdate();
            System.out.println("Bus added: " + bus.getBusNumber());
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Bus> getAllBuses() {
        List<Bus> buses = new ArrayList<>();
        String sql = "SELECT * FROM buses";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Bus bus = new Bus(
                    rs.getLong("id"),
                    rs.getString("bus_number"),
                    rs.getInt("total_seats"),
                    rs.getString("type")
                );
                bus.setOperational(rs.getBoolean("is_operational"));
                buses.add(bus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buses;
    }

    public Bus findBusByNumber(String busNumber) {
        String sql = "SELECT * FROM buses WHERE bus_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, busNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Bus bus = new Bus(
                    rs.getLong("id"),
                    rs.getString("bus_number"),
                    rs.getInt("total_seats"),
                    rs.getString("type")
                );
                bus.setOperational(rs.getBoolean("is_operational"));
                return bus;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}