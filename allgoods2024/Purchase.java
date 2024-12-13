package com.example.allgoods2024;

public class Purchase {
    private String storeName;
    private String productName;
    private int quantity;
    private String deliveryMethod;
    private String paymentMethod;
    private String totalPrice;
    private String price;
    private String type;
    private String productId;
    private String userId;
    private String firstName;
    private String lastName;
    private String province;
    private String city;
    private String barangay;
    private String zipCode;
    private String zone;
    private String status;
    private String purchaseId;
    private String productImageUrl;
    private String category;
    private String purchaseDate;
    private double deliveryPayment;
    private double total;
    private double fee; // Add fee field
    private Boolean isRead = false;
    private String productType; // Added productType
    private String timestamp;

    // Default constructor required for calls to DataSnapshot.getValue(Purchase.class)
    public Purchase() {
    }

    public Purchase(String storeName, String productName, int quantity, String deliveryMethod, String paymentMethod, String totalPrice, String price, String type, String productId, String userId, String firstName, String lastName, String province, String city, String barangay, String zipCode, String zone, String status, String purchaseId, String productImageUrl, String category, String purchaseDate, double deliveryPayment, double total, double fee, String productType, String timestamp) {
        this.storeName = storeName;
        this.productName = productName;
        this.quantity = quantity;
        this.deliveryMethod = deliveryMethod;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.price = price;
        this.type = type;
        this.productId = productId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.province = province;
        this.city = city;
        this.barangay = barangay;
        this.zipCode = zipCode;
        this.zone = zone;
        this.status = status;
        this.purchaseId = purchaseId;
        this.productImageUrl = productImageUrl;
        this.category = category;
        this.purchaseDate = purchaseDate;
        this.deliveryPayment = deliveryPayment;
        this.total = total;
        this.fee = fee;
        this.productType = productType;
        this.timestamp = timestamp;
    }

    // Getters and Setters
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

    public String getprice() {
        return price;
    }

    public void setprice(String price) {
        this.price = price;
    }

    public String gettype() {
        return type;
    }

    public void settype(String type) {
        this.type = type;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getproductImageUrl() {
        return productImageUrl;
    }

    public void setproductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getDeliveryPayment() {
        return deliveryPayment;
    }

    public void setDeliveryPayment(double deliveryPayment) {
        this.deliveryPayment = deliveryPayment;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getFee() {
        return fee; // Getter for fee
    }

    public void setFee(double fee) {
        this.fee = fee; // Setter for fee
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
