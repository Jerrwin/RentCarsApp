package com.example.rentcars.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentcars.models.FeedbackDataClass;
import com.example.rentcars.R;

import java.util.ArrayList;

public class AdminFeedbackAdapter extends RecyclerView.Adapter<AdminFeedbackAdapter.FeedbackViewHolder> {

    private Context context;
    private ArrayList<FeedbackDataClass> feedbackList;

    public AdminFeedbackAdapter(Context context, ArrayList<FeedbackDataClass> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_feedback_item, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        FeedbackDataClass feedback = feedbackList.get(position);
        holder.username.setText("Username: " + feedback.getUsername());
        holder.feedbackText.setText("Feedback: " + feedback.getFeedback());
        holder.datePosted.setText("Date: " + feedback.getDatePosted());
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView username, feedbackText, datePosted;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.adminFeedbackUsername);
            feedbackText = itemView.findViewById(R.id.adminFeedbackText);
            datePosted = itemView.findViewById(R.id.adminFeedbackDate);
        }
    }
}
