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

import com.example.rentcars.adapters.BookingAdapter;
import com.example.rentcars.models.BookingDataClass;
import com.example.rentcars.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminViewBookings extends AppCompatActivity {

    FloatingActionButton admin_addcars_btn, admin_carspage_btn, admin_feedback_btn, admin_logout_btn;
    private RecyclerView bookingsRecyclerView;
    private ArrayList<BookingDataClass> bookingsList;
    private BookingAdapter bookingAdapter;
    private DatabaseReference bookingsDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_bookings);

        bookingsRecyclerView = findViewById(R.id.bookingsRecyclerView);
        bookingsRecyclerView.setHasFixedSize(true);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        admin_addcars_btn = findViewById(R.id.admin_addcars_btn);
        admin_carspage_btn = findViewById(R.id.admin_carspage_btn);
        admin_feedback_btn = findViewById(R.id.admin_feedback_btn);
        admin_logout_btn = findViewById(R.id.admin_logout_btn);

        bookingsList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(this, bookingsList);
        bookingsRecyclerView.setAdapter(bookingAdapter);

        // Initialize Firebase reference
        bookingsDatabaseReference = FirebaseDatabase.getInstance().getReference("Bookings");

        // Fetch booking data from Firebase
        fetchBookingsFromFirebase();

        admin_addcars_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminViewBookings.this, CarUpload.class);
                startActivity(intent);
            }
        });

        admin_carspage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminViewBookings.this, AdminCars.class);
                startActivity(intent);
            }
        });

        admin_feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminViewBookings.this, AdminViewFeedback.class);
                startActivity(intent);
            }
        });

        admin_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a confirmation dialog
                new AlertDialog.Builder(AdminViewBookings.this)
                        .setTitle("Logout")
                        .setMessage("Do you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Go to the Login page
                                Intent intent = new Intent(AdminViewBookings.this, Login.class);
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

    private void fetchBookingsFromFirebase() {
        bookingsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BookingDataClass bookingData = dataSnapshot.getValue(BookingDataClass.class);
                    bookingsList.add(bookingData);  // Add the booking to the list
                }
                bookingAdapter.notifyDataSetChanged();  // Notify the adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminViewBookings.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
