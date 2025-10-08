package com.example.rentcars.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Pair;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rentcars.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    Button callsignup, login_btn, adminpage;
    ImageView img;
    TextView logotext, slogantext;
    TextInputLayout uname, pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callsignup = findViewById(R.id.login_slbtn);
        img = findViewById(R.id.login_logo_img);
        logotext = findViewById(R.id.login_logo_name);
        slogantext = findViewById(R.id.login_logo_signin);
        uname = findViewById(R.id.login_username);
        pwd = findViewById(R.id.login_password);
        login_btn = findViewById(R.id.login_signinbtn);
        adminpage = findViewById(R.id.toadminpage);

        callsignup.setOnClickListener((view -> {
            Intent intent = new Intent(Login.this, SignUp.class);

            Pair[] pairs = new Pair[7];

            pairs[0] = new Pair<View, String>(img, "logo_trans");
            pairs[1] = new Pair<View, String>(logotext, "text_trans");
            pairs[2] = new Pair<View, String>(slogantext, "logo_desc");
            pairs[3] = new Pair<View, String>(uname, "uname_trans");
            pairs[4] = new Pair<View, String>(pwd, "pwd_trans");
            pairs[5] = new Pair<View, String>(login_btn, "btn_trans");
            pairs[6] = new Pair<View, String>(callsignup, "slbtn_trans");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
            startActivity(intent, options.toBundle());

        }));

        adminpage.setOnClickListener((view -> {
            Intent intent = new Intent(Login.this, adminloginpage.class);

            startActivity(intent);

        }));

    }

    private Boolean validateUsername() {
        String val = uname.getEditText().getText().toString();
        if (val.isEmpty()) {
            uname.setError("Field cannot be empty");
            return false;
        } else {
            uname.setError(null);
            uname.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = pwd.getEditText().getText().toString();
        if (val.isEmpty()) {
            pwd.setError("Field cannot be empty");
            return false;
        } else {
            pwd.setError(null);
            pwd.setErrorEnabled(false);
            return true;
        }
    }

    public void loginUser(View view) {
        //Validate Login Info
        if (!validateUsername() | !validatePassword()) {
            return;
        } else {
            isUser();
        }
    }

    private void isUser() {

        final String userEnteredUsername = uname.getEditText().getText().toString().trim();
        final String userEnteredPassword = pwd.getEditText().getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    uname.setError(null);
                    uname.setErrorEnabled(false);
                    String passwordFromDB = dataSnapshot.child(userEnteredUsername).child("password").getValue(String.class);
                    if (passwordFromDB.equals(userEnteredPassword)) {
                        uname.setError(null);
                        uname.setErrorEnabled(false);
                        String nameFromDB = dataSnapshot.child(userEnteredUsername).child("name").getValue(String.class);
                        String usernameFromDB = dataSnapshot.child(userEnteredUsername).child("username").getValue(String.class);
                        String phoneNoFromDB = dataSnapshot.child(userEnteredUsername).child("phoneno").getValue(String.class);
                        String emailFromDB = dataSnapshot.child(userEnteredUsername).child("email").getValue(String.class);
                        Intent intent = new Intent(getApplicationContext(), UserCars.class);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("phoneNo", phoneNoFromDB);
                        intent.putExtra("password", passwordFromDB);
                        startActivity(intent);
                    } else {
                        pwd.setError("Wrong Password");
                        pwd.requestFocus();
                    }
                } else {
                    uname.setError("No such User exist");
                    uname.requestFocus();
                }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}