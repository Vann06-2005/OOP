package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Schedule {
    private Long id;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal ticketPrice;
    private int availableSeats; // Decreases as people book

    // Relationships
    private Bus bus;
    private Route route;

    public Schedule(Long id, Bus bus, Route route, LocalDateTime departureTime, LocalDateTime arrivalTime, BigDecimal ticketPrice) {
        this.id = id;
        this.bus = bus;
        this.route = route;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.ticketPrice = ticketPrice;
        this.availableSeats = bus.getTotalSeats(); // Initially, all seats are free
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public BigDecimal getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public Bus getBus() { return bus; }
    public void setBus(Bus bus) { this.bus = bus; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }
}