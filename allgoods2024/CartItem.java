package com.example.allgoods2024;

public class CartItem {
    private String cartId;
    private String userId;
    private String productId;
    private String productImageUrl;
    private String category;
    private String storeName;
    private String productName;
    private String price;
    private String type; // New field
    private int quantity; // New field
    private String deliveryMethod; // New field
    private String paymentMethod; // New field
    private String totalPrice;
    private String productType;

    // Default constructor required for calls to DataSnapshot.getValue(CartItem.class)
    public CartItem() {
    }

    // Constructor
    public CartItem(String cartId, String userId, String productId, String productImageUrl, String category, String storeName, String productName, String price, String type, int quantity, String deliveryMethod, String paymentMethod, String totalPrice, String productType) {
        this.cartId = cartId;
        this.userId = userId;
        this.productId = productId;
        this.productImageUrl = productImageUrl;
        this.category = category;
        this.storeName = storeName;
        this.productName = productName;
        this.price = price;
        this.type = type;
        this.quantity = quantity;
        this.deliveryMethod = deliveryMethod;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.productType = productType;
    }

    // Getters and setters
    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

}
