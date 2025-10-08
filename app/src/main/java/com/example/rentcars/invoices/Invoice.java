package com.example.rentcars.invoices;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.rentcars.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Invoice extends AppCompatActivity {

    private TextView invoiceUsername, invoiceCarName, invoiceSeatingCapacity,
            invoiceBookingDate, invoiceTotalKm, invoiceTotalAmount;
    private ImageView invoiceLogo;
    private Button downloadInvoiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        // Initialize views
        invoiceUsername = findViewById(R.id.invoice_username);
        invoiceCarName = findViewById(R.id.invoice_car_name);
        invoiceSeatingCapacity = findViewById(R.id.invoice_seating_capacity);
        invoiceBookingDate = findViewById(R.id.invoice_booking_date);
        invoiceTotalKm = findViewById(R.id.invoice_total_km);
        invoiceTotalAmount = findViewById(R.id.invoice_total_amount);
        invoiceLogo = findViewById(R.id.invoice_logo);
        //downloadInvoiceButton = findViewById(R.id.download_invoice_button);

        // Get data from intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String carName = intent.getStringExtra("carName");
        String seatingCapacity = intent.getStringExtra("seatingCapacity");
        String bookingDate = intent.getStringExtra("bookingDate");
        double totalKm = getIntent().getDoubleExtra("totalKm", 0.0);
        String totalAmount = intent.getStringExtra("totalAmount");

        // Set data to TextViews
        invoiceUsername.setText("Username: " + username);
        invoiceCarName.setText("Car Name: " + carName);
        invoiceSeatingCapacity.setText("Seating Capacity: " + seatingCapacity);
        invoiceBookingDate.setText("Booking Date: " + bookingDate);
        invoiceTotalKm.setText(String.format("Total KM: %.2f", totalKm));
        invoiceTotalAmount.setText("Total Amount: " + totalAmount);

        // Set download button listener
        /*downloadInvoiceButton.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                saveInvoiceAsImage();
            } else {
                requestStoragePermission();
            }
        });*/
    }

    private void saveInvoiceAsImage() {
        // Get the root view of the layout
        View content = findViewById(R.id.invoice_layout); // Ensure this is the correct ID of your root layout

        // Check if the layout has valid dimensions
        if (content.getWidth() == 0 || content.getHeight() == 0) {
            Toast.makeText(this, "Layout not yet laid out, please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a bitmap from the view
        Bitmap bitmap = Bitmap.createBitmap(content.getWidth(), content.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        content.layout(0, 0, content.getWidth(), content.getHeight());
        content.draw(canvas);

        // Define file path and name for saving image in external storage
        String fileName = "RentCars_Invoice.png";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Compress the bitmap to PNG format
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Toast.makeText(this, "Invoice saved as image in external storage: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving invoice: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveInvoiceAsImage();
            } else {
                Toast.makeText(this, "Permission denied to write to storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
