package models;

public class Bus {
    private Long id;
    private String busNumber; // License plate, e.g., "ABC-1234"
    private int totalSeats;
    private String type; // e.g., "AC Sleeper", "Non-AC Seater", "Volvo"
    private boolean isOperational; // True if bus is running, False if in maintenance

    public Bus(Long id, String busNumber, int totalSeats, String type) {
        this.id = id;
        this.busNumber = busNumber;
        this.totalSeats = totalSeats;
        this.type = type;
        this.isOperational = true; // Default to active
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isOperational() { return isOperational; }
    public void setOperational(boolean operational) { isOperational = operational; }
}