package com.example.rentcars.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rentcars.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {

    FloatingActionButton user_viewcars_btn, user_viewbook_btn, user_feedback_btn, user_logout_btn;

    TextInputLayout fullName, email, phoneNo, password;
    TextView username;

    DatabaseReference reference;
    String user_username, user_name, user_email, user_phoneNo, user_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        reference = FirebaseDatabase.getInstance().getReference("users");

        // Hooks
        fullName = findViewById(R.id.pro_name);
        email = findViewById(R.id.pro_email);
        phoneNo = findViewById(R.id.pro_num);
        password = findViewById(R.id.pro_password);
        username = findViewById(R.id.pro_username);

        user_viewcars_btn = findViewById(R.id.user_viewcars_btn);
        user_viewbook_btn = findViewById(R.id.user_viewbook_btn);
        user_feedback_btn = findViewById(R.id.user_feedback_btn);
        user_logout_btn = findViewById(R.id.user_logout_btn);

        // Get the username from Intent
        Intent intent = getIntent();
        user_username = intent.getStringExtra("username");
        username.setText(user_username);

        // Fetch and display user data from Firebase
        fetchAndDisplayUserData();

        user_viewcars_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, UserCars.class);
                intent.putExtra("username", user_username);
                startActivity(intent);
            }
        });

        user_viewbook_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, UserViewBookings.class);
                intent.putExtra("username", user_username);
                startActivity(intent);
            }
        });

        user_feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, UserFeedback.class);
                intent.putExtra("username", user_username);
                startActivity(intent);
            }
        });

        user_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a confirmation dialog
                new AlertDialog.Builder(UserProfile.this)
                        .setTitle("Logout")
                        .setMessage("Do you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Go to the Login page
                                Intent intent = new Intent(UserProfile.this, Login.class);
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

    private void fetchAndDisplayUserData() {
        reference.child(user_username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user_name = dataSnapshot.child("name").getValue(String.class);
                    user_email = dataSnapshot.child("email").getValue(String.class);
                    user_phoneNo = dataSnapshot.child("phoneno").getValue(String.class);
                    user_password = dataSnapshot.child("password").getValue(String.class);

                    // Set the fetched data in the input fields
                    fullName.getEditText().setText(user_name);
                    email.getEditText().setText(user_email);
                    phoneNo.getEditText().setText(user_phoneNo);
                    password.getEditText().setText(user_password);
                } else {
                    Toast.makeText(UserProfile.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserProfile.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void update(View view) {
        boolean isUpdated = false;

        // Validate and update fields only if they are valid
        if (validateName() && isNameChanged()) {
            isUpdated = true;
        }

        if (validateEmail() && isEmailChanged()) {
            isUpdated = true;
        }

        if (validatePhoneNo() && isPhoneNoChanged()) {
            isUpdated = true;
        }

        if (validatePassword() && isPasswordChanged()) {
            isUpdated = true;
        }

        // Show appropriate toast message
        if (isUpdated) {
            Toast.makeText(this, "Data has been updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No changes detected", Toast.LENGTH_SHORT).show();
        }
    }

    // The rest of the code for validation and checking changes will remain the same as before

    private Boolean validateName() {
        String val = fullName.getEditText().getText().toString();
        if (val.isEmpty()) {
            fullName.setError("Field cannot be empty");
            return false;
        } else {
            fullName.setError(null);
            fullName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean isNameChanged() {
        String newName = fullName.getEditText().getText().toString();
        if (!user_name.equals(newName)) {
            reference.child(user_username).child("name").setValue(newName);
            user_name = newName;
            return true;
        } else {
            return false;
        }
    }

    private Boolean validateEmail() {
        String val = email.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            email.setError("Invalid email address");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean isEmailChanged() {
        String newEmail = email.getEditText().getText().toString();

        if (!user_email.equals(newEmail)) {
            reference.child(user_username).child("email").setValue(newEmail);
            user_email = newEmail;
            return true;
        } else {
            return false;
        }
    }

    private Boolean validatePhoneNo() {
        String val = phoneNo.getEditText().getText().toString();
        if (val.isEmpty()) {
            phoneNo.setError("Field cannot be empty");
            return false;
        } else if (val.length() != 10) {
            phoneNo.setError("Phone number must be 10 digits");
            return false;
        } else {
            phoneNo.setError(null);
            phoneNo.setErrorEnabled(false);
            return true;
        }
    }

    private boolean isPhoneNoChanged() {
        String newPhoneNo = phoneNo.getEditText().getText().toString();

        if (!user_phoneNo.equals(newPhoneNo)) {
            reference.child(user_username).child("phoneno").setValue(newPhoneNo);
            user_phoneNo = newPhoneNo;
            return true;
        } else {
            return false;
        }
    }

    private boolean validatePassword() {
        String newPassword = password.getEditText().getText().toString();
        String passwordVal = "^" +
                "(?=.*[a-zA-Z])" +
                "(?=.*[@#$%^&+=])" +
                "(?=\\S+$)" +
                ".{4,}" +
                "$";

        if (newPassword.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else if (!newPassword.matches(passwordVal)) {
            password.setError("Password is too weak");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean isPasswordChanged() {
        String newPassword = password.getEditText().getText().toString();

        if (!user_password.equals(newPassword)) {
            reference.child(user_username).child("password").setValue(newPassword);
            user_password = newPassword;
            return true;
        } else {
            return false;
        }
    }
}
