package com.example.rentcars.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentcars.adapters.AdminFeedbackAdapter;
import com.example.rentcars.models.FeedbackDataClass;
import com.example.rentcars.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminViewFeedback extends AppCompatActivity {

    FloatingActionButton admin_addcars_btn, admin_carspage_btn, admin_bookings_btn, admin_logout_btn;

    private RecyclerView adminFeedbackRecyclerView;
    private AdminFeedbackAdapter feedbackAdapter;
    private ArrayList<FeedbackDataClass> feedbackList;
    private DatabaseReference feedbackRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_feedback);

        adminFeedbackRecyclerView = findViewById(R.id.adminFeedbackRecyclerView);
        adminFeedbackRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        feedbackList = new ArrayList<>();
        feedbackAdapter = new AdminFeedbackAdapter(this, feedbackList);
        adminFeedbackRecyclerView.setAdapter(feedbackAdapter);

        admin_addcars_btn = findViewById(R.id.admin_addcars_btn);
        admin_carspage_btn = findViewById(R.id.admin_cars_btn);
        admin_bookings_btn = findViewById(R.id.admin_bookings_btn);
        admin_logout_btn = findViewById(R.id.admin_logout_btn);

        // Initialize Firebase reference
        feedbackRef = FirebaseDatabase.getInstance().getReference("Feedback");

        // Fetch feedbacks from the database
        fetchFeedbacks();

        admin_addcars_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminViewFeedback.this, CarUpload.class);
                startActivity(intent);
            }
        });

        admin_carspage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminViewFeedback.this, AdminCars.class);
                startActivity(intent);
            }
        });

        admin_bookings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminViewFeedback.this, AdminViewBookings.class);
                startActivity(intent);
            }
        });

        admin_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a confirmation dialog
                new AlertDialog.Builder(AdminViewFeedback.this)
                        .setTitle("Logout")
                        .setMessage("Do you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Go to the Login page
                                Intent intent = new Intent(AdminViewFeedback.this, Login.class);
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

    private void fetchFeedbacks() {
        feedbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                feedbackList.clear();
                for (DataSnapshot feedbackSnapshot : snapshot.getChildren()) {
                    FeedbackDataClass feedback = feedbackSnapshot.getValue(FeedbackDataClass.class);
                    feedbackList.add(feedback);
                }
                feedbackAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle potential errors
            }
        });
    }
}
