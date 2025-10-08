package com.example.rentcars.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentcars.models.BookingDataClass;
import com.example.rentcars.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private ArrayList<BookingDataClass> bookingList;

    public BookingAdapter(Context context, ArrayList<BookingDataClass> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.booking_item, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingDataClass booking = bookingList.get(position);
        holder.bookingUsername.setText("Username: " + booking.getUsername());
        holder.bookingDate.setText("Date: " + booking.getBookedDate());
        holder.bookingTotalKm.setText("Total KM: " + booking.getTotalKm());
        holder.bookingTotalAmount.setText("Total Amount: ₹" + booking.getTotalAmount());
        holder.bookingPhoneNo.setText("Phone No: " + booking.getPhoneNo());

        // Fetch car name using carId
        fetchCarName(holder, booking.getCarId());
    }

    private void fetchCarName(BookingViewHolder holder, String carId) {
        DatabaseReference carRef = FirebaseDatabase.getInstance().getReference("Cars").child(carId);
        carRef.child("model").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String carName = dataSnapshot.getValue(String.class);
                    holder.bookingCarName.setText("Car Name: " + carName);
                } else {
                    holder.bookingCarName.setText("Car Name: Not Found");
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError databaseError) {
                holder.bookingCarName.setText("Failed to fetch car name");
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView bookingUsername, bookingDate, bookingTotalKm, bookingTotalAmount, bookingPhoneNo, bookingCarName;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingUsername = itemView.findViewById(R.id.bookingUsername);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            bookingTotalKm = itemView.findViewById(R.id.bookingTotalKm);
            bookingTotalAmount = itemView.findViewById(R.id.bookingTotalAmount);
            bookingPhoneNo = itemView.findViewById(R.id.bookingPhoneNo); // New TextView for Phone No
            bookingCarName = itemView.findViewById(R.id.bookingCarName);   // New TextView for Car Name
        }
    }
}
