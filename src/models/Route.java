package models;

public class Route {
    private Long id;
    private String sourceCity;      // e.g., "New York"
    private String destinationCity; // e.g., "Boston"
    private double distanceKm;      // Used to calculate fuel or rough costs
    private String estimatedDuration; // e.g., "4 hours 30 mins"

    public Route(Long id, String sourceCity, String destinationCity, double distanceKm, String estimatedDuration) {
        this.id = id;
        this.sourceCity = sourceCity;
        this.destinationCity = destinationCity;
        this.distanceKm = distanceKm;
        this.estimatedDuration = estimatedDuration;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSourceCity() { return sourceCity; }
    public void setSourceCity(String sourceCity) { this.sourceCity = sourceCity; }

    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public String getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(String estimatedDuration) { this.estimatedDuration = estimatedDuration; }
}
