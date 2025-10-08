package com.example.rentcars.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rentcars.R;
import com.example.rentcars.activities.UserViewCars;
import com.example.rentcars.models.DataClass;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserMyAdapter extends RecyclerView.Adapter<UserMyAdapter.MyViewHolder>{
    private ArrayList<DataClass> userdataList;
    private Context context;
    private String username;  // Add a field for the username

    // Update the constructor to accept the username
    public UserMyAdapter(Context context, ArrayList<DataClass> userdataList, String username) {
        this.context = context;
        this.userdataList = userdataList;
        this.username = username;  // Initialize the username
    }

    @NonNull
    @Override
    public UserMyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item of the RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.user_recycle_item, parent, false);
        return new UserMyAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserMyAdapter.MyViewHolder holder, int position) {
        // Get the current car data
        DataClass carData = userdataList.get(position);
        String carId = carData.getUniqueId(); // Assuming DataClass has a method to get the car ID

        // Load the car image using Glide
        Glide.with(context)
                .load(carData.getImageURL())
                .into(holder.recyclerImage);

        // Set car details to the respective TextViews
        holder.recyclerModel.setText(carData.getModel());
        //holder.recyclerAvailability.setText(carData.isAvailable() ? "Available" : "Not Available");
        holder.recyclerAvailability.setText(String.valueOf(carData.getCapacity()));

        // Set the "View" button click listener
        holder.userviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log the carId
                Log.d("UserMyAdapter", "Car ID: " + carId); // Check what carId is being passed
                Intent intent = new Intent(context, UserViewCars.class);
                intent.putExtra("carId", carId);  // Pass car ID to the next activity
                intent.putExtra("username", username);  // Pass the username to the next activity
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the size of the data list
        return userdataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Declare UI elements
        ImageView recyclerImage;
        TextView recyclerModel;
        TextView recyclerAvailability;
        Button userviewButton; // Add a button to view car details

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize UI elements using their IDs
            recyclerImage = itemView.findViewById(R.id.userrecyclerImage);
            recyclerModel = itemView.findViewById(R.id.userrecyclerCarModel);
            recyclerAvailability = itemView.findViewById(R.id.userrecyclerAvailability);
            userviewButton = itemView.findViewById(R.id.userviewButton); // Assuming the button has this ID
        }
    }
}
