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

import com.example.rentcars.models.DataClass;
import com.example.rentcars.adapters.MyAdapter;
import com.example.rentcars.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminCars extends AppCompatActivity {
    FloatingActionButton admin_addcars_btn, admin_bookings_btn, admin_feedback_btn, admin_logout_btn;
    private RecyclerView recyclerView;
    private ArrayList<DataClass> dataList;
    private MyAdapter adapter;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Cars");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_cars);

        admin_addcars_btn = findViewById(R.id.admin_addcars_btn);
        admin_bookings_btn = findViewById(R.id.admin_bookings_btn);
        admin_feedback_btn = findViewById(R.id.admin_feedback_btn);
        admin_logout_btn = findViewById(R.id.admin_logout_btn);

        recyclerView = findViewById(R.id.adminrecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();
        adapter = new MyAdapter(this, dataList);
        recyclerView.setAdapter(adapter);

        // Hide the status bar and navigation bar
        hideSystemUI();



        // Fetch data from Firebase and update RecyclerView
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear(); // Clear previous data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
                Toast.makeText(AdminCars.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        admin_addcars_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCars.this, CarUpload.class);
                startActivity(intent);
            }
        });

        admin_bookings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCars.this, AdminViewBookings.class);
                startActivity(intent);
            }
        });

        admin_feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCars.this, AdminViewFeedback.class);
                startActivity(intent);
            }
        });

        admin_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a confirmation dialog
                new AlertDialog.Builder(AdminCars.this)
                        .setTitle("Logout")
                        .setMessage("Do you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Go to the Login page
                                Intent intent = new Intent(AdminCars.this, Login.class);
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