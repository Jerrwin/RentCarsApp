package com.example.rentcars.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentcars.R;
import com.example.rentcars.invoices.Invoice;
import com.example.rentcars.models.BookingDataClass;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserBookingAdapter extends RecyclerView.Adapter<UserBookingAdapter.BookingViewHolder> {

    private Context context;
    private ArrayList<BookingDataClass> bookingList;

    public UserBookingAdapter(Context context, ArrayList<BookingDataClass> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_booking_item, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingDataClass booking = bookingList.get(position);
        holder.bookingDate.setText("Date: " + booking.getBookedDate());
        holder.bookingTotalKm.setText("Total KM: " + booking.getTotalKm());
        holder.bookingTotalAmount.setText("Total Amount: ₹" + booking.getTotalAmount());

        // Set up the cancel button click listener
        holder.cancelButton.setOnClickListener(view -> cancelBooking(booking.getBookingId(), position));

        // Set up the "Get Invoice" button click listener
        holder.getInvoiceButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, Invoice.class);
            intent.putExtra("carName", holder.bookingCarName.getText().toString());
            intent.putExtra("bookingDate", booking.getBookedDate());
            intent.putExtra("totalKm", booking.getTotalKm());
            intent.putExtra("totalAmount", booking.getTotalAmount());
            intent.putExtra("seatingCapacity", holder.seatingcapacity.getText().toString());
            intent.putExtra("username", booking.getUsername());
            context.startActivity(intent);
        });

        // Fetch car name and seat capacity using carId
        fetchCarName(holder, booking.getCarId());
        fetchSeatCapacity(holder, booking.getCarId());
    }

    private void cancelBooking(String bookingId, int position) {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("Bookings").child(bookingId);
        bookingRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                bookingList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Booking canceled successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCarName(BookingViewHolder holder, String carId) {
        DatabaseReference carRef = FirebaseDatabase.getInstance().getReference("Cars").child(carId);
        carRef.child("model").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                String carName = dataSnapshot.exists() ? dataSnapshot.getValue(String.class) : "Not Found";
                holder.bookingCarName.setText("Car Name: " + carName);
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError databaseError) {
                holder.bookingCarName.setText("Failed to fetch car name");
            }
        });
    }

    private void fetchSeatCapacity(BookingViewHolder holder, String carId) {
        DatabaseReference seatRef = FirebaseDatabase.getInstance().getReference("Cars").child(carId);
        seatRef.child("capacity").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                String seatCapacity = dataSnapshot.exists() ? dataSnapshot.getValue(Long.class).toString() : "N/A";
                holder.seatingcapacity.setText("Seating Capacity: " + seatCapacity);
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError databaseError) {
                holder.seatingcapacity.setText("Failed to fetch seat capacity");
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView bookingDate, bookingTotalKm, bookingTotalAmount, bookingCarName, seatingcapacity;
        Button cancelButton, getInvoiceButton;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingDate = itemView.findViewById(R.id.userbookingDate);
            bookingTotalKm = itemView.findViewById(R.id.userbookingTotalKm);
            seatingcapacity = itemView.findViewById(R.id.userseatingcapacity);
            bookingTotalAmount = itemView.findViewById(R.id.userbookingTotalAmount);
            bookingCarName = itemView.findViewById(R.id.userbookingCarName);
            cancelButton = itemView.findViewById(R.id.user_cancel_button);
            getInvoiceButton = itemView.findViewById(R.id.user_invoice_button); // New Get Invoice button
        }
    }
}
