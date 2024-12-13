package com.example.allgoods2024;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Product implements Parcelable {
    private String productId;
    private String name;
    private String stock;
    private String description;
    private String price;
    private String category;
    private List<String> imageUrls;
    private String status;
    private String userId;
    private String type;
    private String sale;
    private String storeName;
    private String deliveryOption;
    private String sold;
    private String paymentOption;

    // Rating fields
    private int userRatingCount;
    private int totalRatings;
    private float averageRating;

    // GCash fields
    private String gcashNumber;
    private String gcashName;

    // PayMaya fields
    private String mayaNumber;
    private String mayaName;

    // Unit field
    private String unit;

    // Voucher fields
    private String voucherCode;
    private String voucherAmount;

    private boolean isEvent;
    private String productType;

    private boolean selected;


    public Product() {
    }

    public Product(String productId, String name, String stock, String description, String price, String category,
                   List<String> imageUrls, String status, String userId, String type, String sale, String storeName,
                   String deliveryOption, String sold, String paymentOption, int userRatingCount, int totalRatings,
                   float averageRating, String gcashNumber, String gcashName, String mayaNumber, String mayaName, String unit,
                   String voucherCode, String voucherAmount, boolean isEvent, String productType) { // Added productType
        this.productId = productId;
        this.name = name;
        this.stock = stock;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrls = imageUrls;
        this.status = status;
        this.userId = userId;
        this.type = type;
        this.sale = sale;
        this.storeName = storeName;
        this.deliveryOption = deliveryOption;
        this.sold = sold;
        this.paymentOption = paymentOption;
        this.userRatingCount = userRatingCount;
        this.totalRatings = totalRatings;
        this.averageRating = averageRating;
        this.gcashNumber = gcashNumber;
        this.gcashName = gcashName;
        this.mayaNumber = mayaNumber;
        this.mayaName = mayaName;
        this.unit = unit;
        this.voucherCode = voucherCode;
        this.voucherAmount = voucherAmount;
        this.isEvent = isEvent;
        this.productType = productType; // Initialize the new field
    }

    protected Product(Parcel in) {
        productId = in.readString();
        name = in.readString();
        stock = in.readString();
        description = in.readString();
        price = in.readString();
        category = in.readString();
        imageUrls = in.createStringArrayList(); // Modified to read List<String>
        status = in.readString();
        userId = in.readString();
        type = in.readString();
        sale = in.readString();
        storeName = in.readString();
        deliveryOption = in.readString();
        sold = in.readString();
        paymentOption = in.readString();
        userRatingCount = in.readInt();
        totalRatings = in.readInt();
        averageRating = in.readFloat();
        gcashNumber = in.readString();
        gcashName = in.readString();
        mayaNumber = in.readString();
        mayaName = in.readString();
        unit = in.readString();
        voucherCode = in.readString(); // Read voucher code
        voucherAmount = in.readString(); // Read voucher amount
        productType = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(name);
        dest.writeString(stock);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(category);
        dest.writeStringList(imageUrls);
        dest.writeString(status);
        dest.writeString(userId);
        dest.writeString(type);
        dest.writeString(sale);
        dest.writeString(storeName);
        dest.writeString(deliveryOption);
        dest.writeString(sold);
        dest.writeString(paymentOption);
        dest.writeInt(userRatingCount);
        dest.writeInt(totalRatings);
        dest.writeFloat(averageRating);
        dest.writeString(gcashNumber);
        dest.writeString(gcashName);
        dest.writeString(mayaNumber);
        dest.writeString(mayaName);
        dest.writeString(unit);
        dest.writeString(voucherCode);  // Write voucher code
        dest.writeString(voucherAmount); // Write voucher amount
        dest.writeString(productType);
    }

    // Getters and Setters
    public boolean getEvent() {
        return isEvent;
    }

    public void setEvent(boolean isEvent) {
        this.isEvent = isEvent;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getStock() { return stock; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getCategory() { return category; }
    public List<String> getImageUrls() { return imageUrls; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getUserId() { return userId; }
    public String getType() { return type; }
    public String getSale() { return sale; }
    public String getStoreName() { return storeName; }
    public String getDeliveryOption() { return deliveryOption; }
    public String getSold() { return sold; }
    public String getPaymentOption() { return paymentOption; }
    public int getUserRatingCount() { return userRatingCount; }
    public int getTotalRatings() { return totalRatings; }
    public float getAverageRating() { return averageRating; }
    public String getGcashNumber() { return gcashNumber; }
    public String getGcashName() { return gcashName; }
    public String getMayaNumber() { return mayaNumber; }
    public String getMayaName() { return mayaName; }
    public String getUnit() { return unit; }

    // New Getters and Setters for Voucher
    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }

    public String getVoucherAmount() { return voucherAmount; }
    public void setVoucherAmount(String voucherAmount) { this.voucherAmount = voucherAmount; }

    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public void setStock(String stock) { this.stock = stock; }
    public void setSold(String sold) { this.sold = sold; }
    public void setPaymentOption(String paymentOption) { this.paymentOption = paymentOption; }
    public void setUserRatingCount(int userRatingCount) { this.userRatingCount = userRatingCount; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }
    public void setAverageRating(float averageRating) { this.averageRating = averageRating; }
    public void setGcashNumber(String gcashNumber) { this.gcashNumber = gcashNumber; }
    public void setGcashName(String gcashName) { this.gcashName = gcashName; }
    public void setMayaNumber(String mayaNumber) { this.mayaNumber = mayaNumber; }
    public void setMayaName(String mayaName) { this.mayaName = mayaName; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getProductName() { return name; }
    public void setProductName(String name) { this.name = name; }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
