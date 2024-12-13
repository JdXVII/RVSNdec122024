package com.example.allgoods2024;

public class ProductSalesData {
    private int quantitySold;
    private int totalPrice;

    public ProductSalesData(int quantitySold, int totalPrice) {
        this.quantitySold = quantitySold;
        this.totalPrice = totalPrice;
    }

    // Add more sales data (for products sold multiple times)
    public void addSales(int quantity, int price) {
        this.quantitySold += quantity;
        this.totalPrice += price;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public int getTotalPrice() {
        return totalPrice;
    }
}
