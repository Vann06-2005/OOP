package models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private String fullName;
    private String password;
    
    private String role; 

    // Relationship: One user can have multiple bookings history
    private List<Booking> bookings;

    public User(Long id, String fullName,String password, String role) {
        this.id = id;
        this.fullName = fullName;
        this.password = password;
        this.role = role;
        this.bookings = new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
}