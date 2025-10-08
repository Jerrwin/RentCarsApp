package com.example.rentcars.models;

public class FeedbackDataClass {
    private String feedbackId;  // Add the feedbackId field
    private String username;
    private String feedback;
    private String datePosted;

    // Default constructor required for calls to DataSnapshot.getValue
    public FeedbackDataClass() {
    }

    // Constructor with feedbackId
    public FeedbackDataClass(String feedbackId, String username, String feedback, String datePosted) {
        this.feedbackId = feedbackId;
        this.username = username;
        this.feedback = feedback;
        this.datePosted = datePosted;
    }

    // Getters
    public String getFeedbackId() {
        return feedbackId;
    }

    public String getUsername() {
        return username;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getDatePosted() {
        return datePosted;
    }

    // Setters (optional, if needed for future functionality)
    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }
}
