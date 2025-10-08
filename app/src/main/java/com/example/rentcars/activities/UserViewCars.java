package com.example.rentcars.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rentcars.R;
import com.example.rentcars.models.DataClass;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class UserViewCars extends AppCompatActivity {

    private ImageView usercarImageView;
    private TextView usercarModelTextView, usercarTypeTextView, userseatingCapacityTextView, userratePerKmTextView;
    private TextView selectedDateText;  // TextView to display the selected date
    private Button userbackButton, userbookButton, selectBookingDateButton;
    private String carId;

    private DatabaseReference databaseReference;
    private DataClass carData;  // Declare carData as a class-level variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_cars);

        // Initialize UI elements
        usercarImageView = findViewById(R.id.usercarImageView);
        usercarModelTextView = findViewById(R.id.user_car_model);
        usercarTypeTextView = findViewById(R.id.user_car_type);
        userseatingCapacityTextView = findViewById(R.id.user_seating_capacity);
        userratePerKmTextView = findViewById(R.id.user_rate_per_km);
        selectedDateText = findViewById(R.id.selected_date_text);
        selectBookingDateButton = findViewById(R.id.select_booking_date_btn);

        userbackButton = findViewById(R.id.user_backButton);
        userbookButton = findViewById(R.id.user_book_car_btn);

        TextView totalAmountText = findViewById(R.id.total_amount_text);
        EditText totalKmInput = findViewById(R.id.total_km_input);

        // Set a TextWatcher to listen for changes in the totalKmInput EditText
        totalKmInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && carData != null) {
                    try {
                        double totalKm = Double.parseDouble(s.toString());
                        double totalAmount = totalKm * carData.getRatePerKm();
                        totalAmountText.setText("₹ " + String.format("%.2f", totalAmount));
                    } catch (NumberFormatException e) {
                        totalAmountText.setText("Total Amount: ₹0.00");
                    }
                } else {
                    totalAmountText.setText("Total Amount: ₹0.00");
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Get the car ID from the Intent
        Intent intent = getIntent();
        carId = intent.getStringExtra("carId");

        // Log the received carId
        Log.d("UserViewCars", "Received Car ID: " + carId);

        // Check if carId is valid
        if (carId == null || carId.isEmpty()) {
            Toast.makeText(this, "No car ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Cars").child(carId);

        // Load car details
        loadCarDetails();

        // Set up the listeners
        userbackButton.setOnClickListener(view -> finish());
        userbookButton.setOnClickListener(view -> BookCars());
        selectBookingDateButton.setOnClickListener(view -> openDatePicker());
    }



    private void loadCarDetails() {
        // Check if the database reference is initialized
        if (databaseReference == null) {
            Toast.makeText(this, "Database reference is null", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    carData = dataSnapshot.getValue(DataClass.class);

                    if (carData != null) {
                        // Populate the UI with car data
                        Glide.with(UserViewCars.this)
                                .load(carData.getImageURL())
                                .into(usercarImageView);

                        usercarModelTextView.setText("Model: " + carData.getModel());
                        usercarTypeTextView.setText("Type: " + carData.getType());
                        userseatingCapacityTextView.setText("Seating Capacity: " + carData.getCapacity());
                        userratePerKmTextView.setText("Rate Per Km: " + carData.getRatePerKm());
                    }
                } else {
                    Toast.makeText(UserViewCars.this, "Car details not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError databaseError) {
                Toast.makeText(UserViewCars.this, "Failed to load car details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UserViewCars", "Database error: " + databaseError.getMessage());
            }
        });
    }


    private void openDatePicker() {
        // Get the current date to set the default date in DatePickerDialog
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and show it
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                UserViewCars.this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Format the selected date and display it in the TextView
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    selectedDateText.setText("Selected Date: " + selectedDate);
                },
                year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }


    private void BookCars() {
        // Get the entered total KM
        EditText totalKmInput = findViewById(R.id.total_km_input);
        String totalKmStr = totalKmInput.getText().toString();

        if (totalKmStr.isEmpty()) {
            Toast.makeText(this, "Please enter the total kilometers", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalKm = Double.parseDouble(totalKmStr);

        // Ensure kilometers are greater than zero
        if (totalKm <= 0) {
            Toast.makeText(this, "Total kilometers must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the selected date
        String selectedDate = selectedDateText.getText().toString().replace("Selected Date: ", "");
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a booking date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure carData is not null before proceeding
        if (carData == null) {
            Toast.makeText(this, "Car data is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate total amount
        double totalAmount = totalKm * carData.getRatePerKm();
        String formattedAmount = String.format("%.2f", totalAmount);

        // Get the username (assuming it was passed via Intent)
        String username = getIntent().getStringExtra("username");

        // Reference to the user data in Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(username);

        // Fetch the user's phone number
        userRef.child("phoneno").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String phoneNo = task.getResult().getValue(String.class);

                // Generate a unique booking ID
                DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("Bookings").push();
                String bookingId = bookingRef.getKey(); // Store the unique booking ID

                // Prepare the booking data to be saved
                HashMap<String, Object> bookingData = new HashMap<>();
                bookingData.put("bookingId", bookingId); // Add booking ID to the data
                bookingData.put("username", username);
                bookingData.put("carId", carId);
                bookingData.put("bookedDate", selectedDate);
                bookingData.put("totalKm", totalKm);
                bookingData.put("totalAmount", formattedAmount);
                bookingData.put("phoneNo", phoneNo);  // Add the phone number to the booking data

                // Save the booking data to Firebase
                bookingRef.setValue(bookingData).addOnCompleteListener(saveTask -> {
                    if (saveTask.isSuccessful()) {
                        Toast.makeText(UserViewCars.this, "Car booked successfully!", Toast.LENGTH_SHORT).show();
                        finish();  // Optionally close the activity after successful booking
                    } else {
                        Toast.makeText(UserViewCars.this, "Booking failed: " + saveTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(UserViewCars.this, "Failed to fetch user phone number", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
