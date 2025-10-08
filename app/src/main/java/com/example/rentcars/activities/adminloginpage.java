package com.example.rentcars.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rentcars.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class adminloginpage extends AppCompatActivity {

    TextInputLayout adminuname, adminpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adminloginpage);

        adminuname = findViewById(R.id.admin_username);
        adminpwd = findViewById(R.id.admin_password);

    }

    private Boolean validateUsername() {
        String val = adminuname.getEditText().getText().toString();
        if (val.isEmpty()) {
            adminuname.setError("Field cannot be empty");
            return false;
        } else {
            adminuname.setError(null);
            adminuname.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = adminpwd.getEditText().getText().toString();
        if (val.isEmpty()) {
            adminpwd.setError("Field cannot be empty");
            return false;
        } else {
            adminpwd.setError(null);
            adminpwd.setErrorEnabled(false);
            return true;
        }
    }

    public void loginAdmin(View view) {
        //Validate Login Info
        if (!validateUsername() | !validatePassword()) {
            return;
        } else {
            isAdmin();
        }
    }

    private void isAdmin() {

        final String userEnteredUsername = adminuname.getEditText().getText().toString().trim();
        final String userEnteredPassword = adminpwd.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String usernameFromDB = dataSnapshot.child("AdminUsername").getValue(String.class);
                    String passwordFromDB = dataSnapshot.child("AdminPassword").getValue(String.class);

                    if (usernameFromDB.equals(userEnteredUsername)) {
                        if (passwordFromDB.equals(userEnteredPassword)) {
                            // Admin credentials are correct, navigate to AdminPage
                            Intent intent = new Intent(getApplicationContext(), AdminCars.class);
                            startActivity(intent);
                        } else {
                            adminpwd.setError("Wrong Password");
                            adminpwd.requestFocus();
                        }
                    } else {
                        adminuname.setError("Invalid Admin Username");
                        adminuname.requestFocus();
                    }
                } else {
                    adminuname.setError("Admin data not found in database");
                    adminuname.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }


}