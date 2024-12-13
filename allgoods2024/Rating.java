package com.example.allgoods2024;

public class Rating {

    private int rating;
    private String buyerFirstName;

    // Default constructor required for calls to DataSnapshot.getValue(Rating.class)
    public Rating() {
    }

    // Constructor with parameters
    public Rating(int rating, String buyerFirstName) {
        this.rating = rating;
        this.buyerFirstName = buyerFirstName;
    }

    // Getter for rating
    public int getRating() {
        return rating;
    }

    // Setter for rating
    public void setRating(int rating) {
        this.rating = rating;
    }

    // Getter for buyer's first name
    public String getBuyerFirstName() {
        return buyerFirstName;
    }

    // Setter for buyer's first name
    public void setBuyerFirstName(String buyerFirstName) {
        this.buyerFirstName = buyerFirstName;
    }
}
