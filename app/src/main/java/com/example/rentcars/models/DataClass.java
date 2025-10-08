package com.example.rentcars.models;

public class DataClass {
    private String uniqueId; // Unique ID for the car
    private String imageURL;
    private String model;
    private String type;
    private int capacity;
    private int ratePerKm;
    private boolean isAvailable;

    public DataClass() {
        // Default constructor required for calls to DataSnapshot.getValue(DataClass.class)
    }

    public DataClass(String uniqueId, String imageURL, String model, String type, int capacity, int ratePerKm, boolean isAvailable) {
        this.uniqueId = uniqueId; // Initialize the unique ID
        this.imageURL = imageURL;
        this.model = model;
        this.type = type;
        this.capacity = capacity;
        this.ratePerKm = ratePerKm;
        this.isAvailable = isAvailable;
    }

    // Getter and setter for unique ID
    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    // Getters and setters for other properties
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getRatePerKm() {
        return ratePerKm;
    }

    public void setRatePerKm(int ratePerKm) {
        this.ratePerKm = ratePerKm;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
