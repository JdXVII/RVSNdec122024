package com.example.allgoods2024;

public class DeclinedProduct {
    private String reason;
    private String userId;
    private String name;

    public DeclinedProduct() {}

    public DeclinedProduct(String reason, String userId, String name) {
        this.reason = reason;
        this.userId = userId;
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setProductName(String productName) {
        this.name = productName;
    }
}
