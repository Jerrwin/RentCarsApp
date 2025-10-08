package com.example.rentcars.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;


import androidx.appcompat.app.AppCompatActivity;

import com.example.rentcars.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserFeedback extends AppCompatActivity {

    FloatingActionButton user_viewcars_btn, user_viewbook_btn, user_profile_btn, user_logout_btn;
    private EditText feedbackEditText;
    private Button submitFeedbackButton;
    private DatabaseReference feedbackRef;
    private String username; // Username should be passed from the previous activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);

        // Initialize Floating Action Buttons (FABs)
        user_profile_btn = findViewById(R.id.user_profile_btn);
        user_viewbook_btn = findViewById(R.id.user_viewbook_btn);
        user_viewcars_btn = findViewById(R.id.user_viewcars_btn);
        user_logout_btn = findViewById(R.id.user_logout_btn);

        // Initialize views
        feedbackEditText = findViewById(R.id.feedbackEditText);
        submitFeedbackButton = findViewById(R.id.submitFeedbackButton);

        // Initialize Firebase reference
        feedbackRef = FirebaseDatabase.getInstance().getReference("Feedback");

        // Get username from intent extras
        username = getIntent().getStringExtra("username");

        submitFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });

        // Navigate to User Profile
        user_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserFeedback.this, UserProfile.class);
                intent.putExtra("username", username); // Pass the username to the UserProfile activity
                startActivity(intent);
            }
        });

        // Navigate to View Bookings
        user_viewbook_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserFeedback.this, UserViewBookings.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        // Navigate to View Cars
        user_viewcars_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserFeedback.this, UserCars.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        user_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a confirmation dialog
                new AlertDialog.Builder(UserFeedback.this)
                        .setTitle("Logout")
                        .setMessage("Do you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Go to the Login page
                                Intent intent = new Intent(UserFeedback.this, Login.class);
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

    private void submitFeedback() {
        String feedback = feedbackEditText.getText().toString().trim();

        if (feedback.isEmpty()) {
            Toast.makeText(this, "Please write your feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique ID for the feedback
        String feedbackId = feedbackRef.push().getKey();
        if (feedbackId == null) {
            Toast.makeText(this, "Failed to generate feedback ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String datePosted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // Create a map to store feedback data including feedbackId
        Map<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("username", username);
        feedbackData.put("feedback", feedback);
        feedbackData.put("feedbackId", feedbackId);  // Store feedbackId explicitly
        feedbackData.put("datePosted", datePosted);

        // Store feedback in the database under the generated feedbackId
        feedbackRef.child(feedbackId).setValue(feedbackData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserFeedback.this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                        feedbackEditText.setText(""); // Clear the input field
                    } else {
                        Toast.makeText(UserFeedback.this, "Failed to submit feedback", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
