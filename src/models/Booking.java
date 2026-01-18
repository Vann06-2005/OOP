package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Booking {
    private Long id;
    private LocalDateTime bookingDate;
    private String seatNumber; // e.g., "A1", "B4"
    private String status; // "CONFIRMED", "CANCELLED", "PENDING"
    private BigDecimal totalAmount;

    // Relationships
    private User customer;
    private Schedule schedule; // Links to the specific trip

    public Booking(Long id, User customer, Schedule schedule, String seatNumber, BigDecimal totalAmount) {
        this.id = id;
        this.customer = customer;
        this.schedule = schedule;
        this.seatNumber = seatNumber;
        this.totalAmount = totalAmount;
        this.bookingDate = LocalDateTime.now();
        this.status = "CONFIRMED";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    public Schedule getSchedule() { return schedule; }
    public void setSchedule(Schedule schedule) { this.schedule = schedule; }
}