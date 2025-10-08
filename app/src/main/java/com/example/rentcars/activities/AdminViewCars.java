package com.example.rentcars.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rentcars.models.DataClass;
import com.example.rentcars.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminViewCars extends AppCompatActivity {

    private ImageView carImageView;
    private TextInputLayout carModelEditText, carTypeEditText, seatingCapacityEditText, ratePerKmEditText;
    private CheckBox availabilityStatusCheckBox;
    private Button backButton, deleteButton, updateButton;
    private String carId;

    private DatabaseReference databaseReference;
    private DataClass carData;  // Declare carData as a class-level variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_cars);

        // Initialize UI elements
        carImageView = findViewById(R.id.carImageView);
        carModelEditText = findViewById(R.id.car_model);
        carTypeEditText = findViewById(R.id.car_type);
        seatingCapacityEditText = findViewById(R.id.seating_capacity);
        ratePerKmEditText = findViewById(R.id.rate_per_km);
        availabilityStatusCheckBox = findViewById(R.id.availability_status);

        backButton = findViewById(R.id.backButton);
        deleteButton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.update_car_details);

        // Get the car ID from the Intent
        Intent intent = getIntent();
        carId = intent.getStringExtra("carId");

        // Log the received carId
        Log.d("AdminViewCars", "Received Car ID: " + carId);

        // Check for null carId
        if (carId == null) {
            Toast.makeText(this, "No car ID provided", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no car ID
            return;
        }

        // Reference to the "Cars" node in Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Cars").child(carId);

        // Load car details from Firebase
        loadCarDetails();

        // Set up the back button click listener
        backButton.setOnClickListener(view -> finish());

        // Set up delete button click listener
        deleteButton.setOnClickListener(view -> deleteCar());

        // Set up update button click listener
        updateButton.setOnClickListener(view -> updateCarDetails());
    }

    private void loadCarDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    carData = dataSnapshot.getValue(DataClass.class);  // Assign the data to the class-level carData

                    if (carData != null) {
                        // Populate the UI with car data
                        Glide.with(AdminViewCars.this)
                                .load(carData.getImageURL())
                                .into(carImageView);

                        carModelEditText.getEditText().setText(carData.getModel());
                        carTypeEditText.getEditText().setText(carData.getType());
                        seatingCapacityEditText.getEditText().setText(String.valueOf(carData.getCapacity()));
                        ratePerKmEditText.getEditText().setText(String.valueOf(carData.getRatePerKm()));
                        availabilityStatusCheckBox.setChecked(carData.isAvailable()); // Set CheckBox based on availability
                    }
                } else {
                    Toast.makeText(AdminViewCars.this, "Car details not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError databaseError) {
                Toast.makeText(AdminViewCars.this, "Failed to load car details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AdminViewCars", "Database error: " + databaseError.getMessage());
            }
        });
    }


    private void deleteCar() {
        // Remove the car from the Firebase database
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AdminViewCars.this, "Car deleted successfully", Toast.LENGTH_SHORT).show();
                finish(); // Go back after deletion
            } else {
                Toast.makeText(AdminViewCars.this, "Failed to delete car", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCarDetails() {
        // Ensure carData is loaded before proceeding
        if (carData == null) {
            Toast.makeText(AdminViewCars.this, "Failed to load car data. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the updated details from the input fields
        String model = carModelEditText.getEditText().getText().toString().trim();
        String type = carTypeEditText.getEditText().getText().toString().trim();
        int capacity = Integer.parseInt(seatingCapacityEditText.getEditText().getText().toString().trim());
        double ratePerKm = Double.parseDouble(ratePerKmEditText.getEditText().getText().toString().trim());
        boolean isAvailable = availabilityStatusCheckBox.isChecked(); // Get availability from CheckBox

        // Check for valid inputs (Optional)
        if (model.isEmpty() || type.isEmpty() || capacity <= 0 || ratePerKm <= 0) {
            Toast.makeText(AdminViewCars.this, "Please enter valid car details", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the existing image URL from carData (since the image hasn't been changed)
        String imageURL = carData.getImageURL();  // Get the current image URL
        String uniqueId = carData.getUniqueId();  // Get the unique ID of the car

        // Create a new DataClass object with updated details
        DataClass updatedCarData = new DataClass(uniqueId, imageURL, model, type, capacity, (int) ratePerKm, isAvailable);

        // Update car details in Firebase
        databaseReference.setValue(updatedCarData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AdminViewCars.this, "Car details updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AdminViewCars.this, "Failed to update car details", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
