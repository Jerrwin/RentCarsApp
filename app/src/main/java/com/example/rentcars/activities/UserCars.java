package com.example.rentcars.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentcars.R;
import com.example.rentcars.adapters.UserMyAdapter;
import com.example.rentcars.models.DataClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserCars extends AppCompatActivity {
    FloatingActionButton user_profile_btn, user_viewbook_btn, user_feedback_btn, user_logout_btn;
    private RecyclerView userrecyclerView;
    private ArrayList<DataClass> userdataList;
    private UserMyAdapter useradapter;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Cars");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cars);

        user_profile_btn = findViewById(R.id.user_profile_btn);
        user_viewbook_btn = findViewById(R.id.user_viewbook_btn);
        user_feedback_btn = findViewById(R.id.user_feedback_btn);
        user_logout_btn = findViewById(R.id.user_logout_btn);

        userrecyclerView = findViewById(R.id.userrecyclerView);
        userrecyclerView.setHasFixedSize(true);
        userrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userdataList = new ArrayList<>();

        // Retrieve the username from the Intent
        String username = getIntent().getStringExtra("username");

        // Pass the username to the adapter
        useradapter = new UserMyAdapter(this, userdataList, username);  // Add username as an argument
        userrecyclerView.setAdapter(useradapter);

        // Hide the status bar and navigation bar
        hideSystemUI();

        // Fetch data from Firebase and update RecyclerView
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userdataList.clear(); // Clear previous data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass userdataClass = dataSnapshot.getValue(DataClass.class);

                    // Check if the car is available before adding to the list
                    if (userdataClass != null && userdataClass.isAvailable()) {  // Assuming isAvailable() is a getter for the 'available' field
                        userdataList.add(userdataClass);
                    }
                }
                useradapter.notifyDataSetChanged(); // Notify the adapter of changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
                Toast.makeText(UserCars.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set onClickListener for the profile button
        user_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserCars.this, UserProfile.class);
                intent.putExtra("username", username); // Pass the username to the UserProfile activity
                startActivity(intent);
            }
        });

        user_viewbook_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserCars.this, UserViewBookings.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        user_feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserCars.this, UserFeedback.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        user_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a confirmation dialog
                new AlertDialog.Builder(UserCars.this)
                        .setTitle("Logout")
                        .setMessage("Do you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Go to the Login page
                                Intent intent = new Intent(UserCars.this, Login.class);
                                // Clear the back stack to prevent returning to UserProfile
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish(); // Finish UserProfile activity
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Dismiss dialog and stay on the current page
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

    }

    // Function to hide system UI
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
