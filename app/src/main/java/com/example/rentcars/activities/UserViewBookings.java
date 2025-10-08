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
import com.example.rentcars.adapters.UserBookingAdapter;
import com.example.rentcars.models.BookingDataClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserViewBookings extends AppCompatActivity {

    FloatingActionButton user_viewcars_btn, user_profile_btn, user_feedback_btn, user_logout_btn;
    private RecyclerView bookingsRecyclerView;
    private ArrayList<BookingDataClass> bookingsList;
    private UserBookingAdapter bookingAdapter;
    private DatabaseReference bookingsDatabaseReference;
    private String currentUsername; // Store the logged-in user's username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_bookings);

        bookingsRecyclerView = findViewById(R.id.userbookingsRecyclerView);
        bookingsRecyclerView.setHasFixedSize(true);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        user_viewcars_btn = findViewById(R.id.user_viewcars_btn);
        user_profile_btn = findViewById(R.id.user_profile_btn);
        user_feedback_btn = findViewById(R.id.user_feedback_btn);
        user_logout_btn = findViewById(R.id.user_logout_btn);

        // Get current logged-in user's username (from intent)
        currentUsername = getIntent().getStringExtra("username");

        bookingsList = new ArrayList<>();
        bookingAdapter = new UserBookingAdapter(this, bookingsList);
        bookingsRecyclerView.setAdapter(bookingAdapter);

        // Initialize Firebase reference
        bookingsDatabaseReference = FirebaseDatabase.getInstance().getReference("Bookings");

        // Fetch booking data for the current user
        fetchUserBookingsFromFirebase();

        user_viewcars_btn.setOnClickListener(view -> {
            Intent intent = new Intent(UserViewBookings.this, UserCars.class);
            intent.putExtra("username", currentUsername); // Pass the username to the UserCars activity
            startActivity(intent);
        });

        user_profile_btn.setOnClickListener(view -> {
            Intent intent = new Intent(UserViewBookings.this, UserProfile.class);
            intent.putExtra("username", currentUsername); // Pass the username to the UserProfile activity
            startActivity(intent);
        });

        user_feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserViewBookings.this, UserFeedback.class);
                intent.putExtra("username", currentUsername);
                startActivity(intent);
            }
        });

        user_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a confirmation dialog
                new AlertDialog.Builder(UserViewBookings.this)
                        .setTitle("Logout")
                        .setMessage("Do you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Go to the Login page
                                Intent intent = new Intent(UserViewBookings.this, Login.class);
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

    private void fetchUserBookingsFromFirebase() {
        bookingsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingsList.clear(); // Clear the previous data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BookingDataClass bookingData = dataSnapshot.getValue(BookingDataClass.class);

                    // Ensure bookingData is not null and filter by current username
                    if (bookingData != null && bookingData.getUsername() != null && bookingData.getUsername().equals(currentUsername)) {
                        bookingsList.add(bookingData);  // Add the booking to the list
                    }
                }
                bookingAdapter.notifyDataSetChanged();  // Notify the adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserViewBookings.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
