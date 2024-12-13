package com.example.allgoods2024;

public class Vendor {
    public String userId;
    public String email;
    public String firstName;
    public String lastName;
    public String storeName;
    public String phone;
    public String province;
    public String city;
    public String barangay;
    public String profileImageUrl; // New field for profile image URL
    public String permitImageUrl;
    public String status;
    private String password;
    private int unreadMessageCount;

    public Vendor() {
        // Default constructor required for calls to DataSnapshot.getValue(Vendor.class)
    }

    public Vendor(String userId, String email, String firstName, String lastName, String storeName, String phone, String province, String city, String barangay, String profileImageUrl, String permitImageUrl, String status, String password) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.storeName = storeName;
        this.phone = phone;
        this.province = province;
        this.city = city;
        this.barangay = barangay;
        this.profileImageUrl = profileImageUrl; // Initialize new field
        this.permitImageUrl = permitImageUrl;
        this.status = status;
        this.password = password;
    }


    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPermitImageUrl() {
        return permitImageUrl;
    }

    public void setPermitImageUrl(String permitImageUrl) {
        this.permitImageUrl = permitImageUrl;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
