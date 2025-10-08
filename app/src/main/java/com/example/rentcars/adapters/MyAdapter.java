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
import com.example.rentcars.activities.AdminViewCars;
import com.example.rentcars.models.DataClass;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<DataClass> dataList;
    private Context context;

    public MyAdapter(Context context, ArrayList<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item of the RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.admin_recycle_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Get the current car data
        DataClass carData = dataList.get(position);
        String carId = carData.getUniqueId(); // Assuming DataClass has a method to get the car ID

        // Load the car image using Glide
        Glide.with(context)
                .load(carData.getImageURL())
                .into(holder.recyclerImage);

        // Set car details to the respective TextViews
        holder.recyclerModel.setText(carData.getModel());
        holder.recyclerAvailability.setText(carData.isAvailable() ? "Available" : "Not Available");

        // Set the "View" button click listener
        holder.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log the carId
                Log.d("MyAdapter", "Car ID: " + carId); // Check what carId is being passed
                Intent intent = new Intent(context, AdminViewCars.class);
                intent.putExtra("carId", carId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        // Return the size of the data list
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Declare UI elements
        ImageView recyclerImage;
        TextView recyclerModel;
        TextView recyclerAvailability;
        Button viewButton; // Add a button to view car details

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize UI elements using their IDs
            recyclerImage = itemView.findViewById(R.id.recyclerImage);
            recyclerModel = itemView.findViewById(R.id.adminrecyclerCarModel);
            recyclerAvailability = itemView.findViewById(R.id.adminrecyclerAvailability);
            viewButton = itemView.findViewById(R.id.viewButton); // Assuming the button has this ID
        }
    }
}
