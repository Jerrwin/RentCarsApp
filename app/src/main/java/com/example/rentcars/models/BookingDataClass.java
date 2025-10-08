package com.example.rentcars.models;

public class BookingDataClass {
    private String bookingId; // New field for Booking ID
    private String username;
    private String bookedDate;
    private double totalKm;
    private String totalAmount;
    private String phoneNo;  // New field for Phone Number
    private String carId;    // New field for Car ID

    public BookingDataClass() {
        // Default constructor required for calls to DataSnapshot.getValue(BookingDataClass.class)
    }

    public BookingDataClass(String bookingId, String username, String bookedDate, double totalKm, String totalAmount, String phoneNo, String carId) {
        this.bookingId = bookingId; // Initialize the booking ID
        this.username = username;
        this.bookedDate = bookedDate;
        this.totalKm = totalKm;
        this.totalAmount = totalAmount;
        this.phoneNo = phoneNo;  // Initialize the phone number
        this.carId = carId;      // Initialize the car ID
    }

    public String getBookingId() {
        return bookingId; // Getter for Booking ID
    }

    public String getUsername() {
        return username;
    }

    public String getBookedDate() {
        return bookedDate;
    }

    public double getTotalKm() {
        return totalKm;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getPhoneNo() {
        return phoneNo;  // Getter for Phone Number
    }

    public String getCarId() {
        return carId;    // Getter for Car ID
    }
}
