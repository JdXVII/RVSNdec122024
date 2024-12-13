package com.example.allgoods2024;

public class Receipt {
    private String userId;
    private double totalSales;
    private double totalCommission;
    private String date;

    public Receipt() {
        // Default constructor required for calls to DataSnapshot.getValue(Receipt.class)
    }

    public Receipt(String userId, double totalSales, double totalCommission, String date) {
        this.userId = userId;
        this.totalSales = totalSales;
        this.totalCommission = totalCommission;
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public double getTotalCommission() {
        return totalCommission;
    }

    public String getDate() {
        return date;
    }
}

