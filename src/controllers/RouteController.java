package controllers;

import db.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Route;

public class RouteController {

    private static RouteController instance;

    private RouteController() {}

    public static RouteController getInstance() {
        if (instance == null) instance = new RouteController();
        return instance;
    }

    public void addRoute(Route route) {
        String sql = "INSERT INTO routes (source_city, destination_city, distance_km, estimated_duration) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, route.getSourceCity());
            stmt.setString(2, route.getDestinationCity());
            stmt.setDouble(3, route.getDistanceKm());
            stmt.setString(4, route.getEstimatedDuration());
            
            stmt.executeUpdate();
            System.out.println("Route added: " + route.getSourceCity() + " -> " + route.getDestinationCity());
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT * FROM routes";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                routes.add(new Route(
                    rs.getLong("id"),
                    rs.getString("source_city"),
                    rs.getString("destination_city"),
                    rs.getDouble("distance_km"),
                    rs.getString("estimated_duration")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }
}