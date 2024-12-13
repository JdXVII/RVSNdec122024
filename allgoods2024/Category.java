package com.example.allgoods2024;

public class Category {
    private String firstName;
    private String lastName;
    private String storeName;
    private String userId;

    public Category() {
        // Default constructor required for calls to DataSnapshot.getValue(Category.class)
    }

    public Category(String firstName, String lastName, String storeName, String userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.storeName = storeName;
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getUserId() {
        return userId;
    }
}
