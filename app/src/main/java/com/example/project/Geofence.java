package com.example.project;

public class Geofence {
    private String name;
    private double latitude;
    private double longitude;
    private int radius;

    // Constructor
    public Geofence(String name, double latitude, double longitude, int radius) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    // Getters and setters
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getRadius() { return radius; }

    @Override
    public String toString() {
        return name + " (" + latitude + ", " + longitude + ") - " + radius + "m";
    }
}
