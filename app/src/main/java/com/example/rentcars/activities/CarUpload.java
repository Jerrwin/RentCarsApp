package com.example.rentcars.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rentcars.models.DataClass;
import com.example.rentcars.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CarUpload extends AppCompatActivity {

    // Declare your UI elements and Firebase references
    private MaterialButton uploadButton;
    private ImageView uploadImage;
    private TextInputLayout carModel, carType, seatingCapacity, rate;
    private CheckBox checkboxAvailable;
    private ProgressBar progressBar;
    private Uri imageUri;
    FloatingActionButton admin_carspage_btn, admin_bookings_btn, admin_feedback_btn, admin_logout_btn;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Cars");
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_upload);

        // Initialize your UI elements
        uploadButton = findViewById(R.id.uploadButton);
        uploadImage = findViewById(R.id.uploadImage);
        progressBar = findViewById(R.id.progressBar);
        carModel = findViewById(R.id.carmodel);
        carType = findViewById(R.id.cartype);
        seatingCapacity = findViewById(R.id.noofseat);
        rate = findViewById(R.id.rate);
        checkboxAvailable = findViewById(R.id.checkbox_available);

        admin_carspage_btn = findViewById(R.id.admin_carspage_btn);
        admin_bookings_btn = findViewById(R.id.admin_bookings_btn);
        admin_feedback_btn = findViewById(R.id.admin_feedback_btn);
        admin_logout_btn = findViewById(R.id.admin_logout_btn);

        progressBar.setVisibility(View.INVISIBLE);

        // Activity result launcher for image selection
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            imageUri = data != null ? data.getData() : null;
                            if (imageUri != null) {
                                uploadImage.setImageURI(imageUri);
                            }
                        } else {
                            Toast.makeText(CarUpload.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(CarUpload.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        admin_carspage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CarUpload.this, AdminCars.class);
                startActivity(intent);
            }
        });

        admin_bookings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CarUpload.this, AdminViewBookings.class);
                startActivity(intent);
            }
        });

        admin_feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CarUpload.this, AdminViewFeedback.class);
                startActivity(intent);
            }
        });

        admin_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a confirmation dialog
                new AlertDialog.Builder(CarUpload.this)
                        .setTitle("Logout")
                        .setMessage("Do you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Go to the Login page
                                Intent intent = new Intent(CarUpload.this, Login.class);
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

    private void uploadToFirebase(Uri uri) {
        // Get values from input fields
        String model = carModel.getEditText().getText().toString().trim();
        String type = carType.getEditText().getText().toString().trim();
        String seating = seatingCapacity.getEditText().getText().toString().trim();
        String rateValue = rate.getEditText().getText().toString().trim();

        // Check for empty fields
        if (model.isEmpty() || type.isEmpty() || seating.isEmpty() || rateValue.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int capacity;
        int ratePerKm;

        try {
            capacity = Integer.parseInt(seating);
            ratePerKm = Integer.parseInt(rateValue);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for seating capacity and rate", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check availability status
        boolean isAvailable = checkboxAvailable.isChecked();

        // Generate a unique file name for the image
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));

        progressBar.setVisibility(View.VISIBLE);

        // Upload image to Firebase Storage
        imageReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                // Create a new DataClass object
                                String uniqueId = databaseReference.push().getKey(); // Generate a unique key
                                if (uniqueId != null) {
                                    DataClass dataClass = new DataClass(
                                            uniqueId, // Pass the unique ID here
                                            downloadUri.toString(),
                                            model,
                                            type,
                                            capacity,
                                            ratePerKm,
                                            isAvailable
                                    );

                                    // Save to the database
                                    databaseReference.child(uniqueId).setValue(dataClass)
                                            .addOnSuccessListener(aVoid -> {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(CarUpload.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(CarUpload.this, AdminCars.class));
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(CarUpload.this, "Failed to upload data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(CarUpload.this, "Failed to get a unique key", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(CarUpload.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}
