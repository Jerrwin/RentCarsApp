package com.example.rentcars.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rentcars.R;
import com.example.rentcars.helpers.UserHelperClass;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignUp extends AppCompatActivity {

    TextInputLayout regname, reguname, regemail, regpno, regpwd;
    Button regbtn, regTologin;

    FirebaseDatabase rootnode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        regname = findViewById(R.id.name);
        reguname = findViewById(R.id.username);
        regemail = findViewById(R.id.email);
        regpno = findViewById(R.id.phoneno);
        regpwd = findViewById(R.id.password);
        regbtn = findViewById(R.id.signupbtn);
        regTologin = findViewById(R.id.tologin);

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser(view);
            }
        });

    }

    private Boolean validateName() {
        String val = regname.getEditText().getText().toString();
        if (val.isEmpty()) {
            regname.setError("Field cannot be empty");
            return false;
        } else {
            regname.setError(null);
            regname.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateUsername() {
        String val = reguname.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";
        if (val.isEmpty()) {
            reguname.setError("Field cannot be empty");
            return false;
        } else if (val.length() >= 15) {
            reguname.setError("Username too long");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            reguname.setError("White Spaces are not allowed");
            return false;
        } else {
            reguname.setError(null);
            reguname.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = regemail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            regemail.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            regemail.setError("Invalid email address");
            return false;
        } else {
            regemail.setError(null);
            regemail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePhoneNo() {
        String val = regpno.getEditText().getText().toString();
        if (val.isEmpty()) {
            regpno.setError("Field cannot be empty");
            return false;
        } else if (val.length() != 10) {
            regpno.setError("Phone number must be 10 digits");
            return false;
        } else {
            regpno.setError(null);
            regpno.setErrorEnabled(false);
            return true;
        }
    }


    private Boolean validatePassword() {
        String val = regpwd.getEditText().getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digitai
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";
        if (val.isEmpty()) {
            regpwd.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            regpwd.setError("Password is too weak");
            return false;
        } else {
            regpwd.setError(null);
            regpwd.setErrorEnabled(false);
            return true;
        }
    }

    private void checkUsernameExists(final String username, final UserHelperClass helperClass) {
        reference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reguname.setError("Username already exists");
                    reguname.requestFocus();
                } else {
                    reference.child(username).setValue(helperClass).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUp.this, "Signed up successfully!", Toast.LENGTH_SHORT).show();
                            navigateToLogin();
                        } else {
                            Toast.makeText(SignUp.this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SignUp", "Error checking username", databaseError.toException());
                Toast.makeText(SignUp.this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void registerUser(View view) {
        if (!validateName() | !validatePassword() | !validatePhoneNo() | !validateEmail() | !validateUsername()) {
            Log.d("SignUp", "Validation failed");
            return;
        }

        // Initialize Firebase
        rootnode = FirebaseDatabase.getInstance();
        reference = rootnode.getReference("users");

        // Get all values
        String name = regname.getEditText().getText().toString();
        String username = reguname.getEditText().getText().toString();
        String email = regemail.getEditText().getText().toString();
        String phoneno = regpno.getEditText().getText().toString();
        String password = regpwd.getEditText().getText().toString();

        // Create helper class
        UserHelperClass helperClass = new UserHelperClass(name, username, email, phoneno, password);

        // Check if username exists before registering
        checkUsernameExists(username, helperClass);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SignUp.this, Login.class);
        startActivity(intent);
        finish(); // Optional: call finish() to close the current activity
    }

}