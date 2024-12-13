package com.example.allgoods2024;

public class Buyer {
    public String userId;
    public String login_email;
    public String firstName;
    public String lastName;
    public String phone;
    public String provinces;
    public String cities;
    public String barangay;
    public String profileImageUrl;
    public String valididImageUrl;
    public String status;
    public String zipCode;  // New field
    public String zone;     // New field
    private int unreadMessageCount;
    private String password;

    public Buyer() {
        // Default constructor required for calls to DataSnapshot.getValue(Buyer.class)
    }

    public Buyer(String userId, String login_email, String firstName, String lastName, String phone, String provinces, String cities, String barangay, String profileImageUrl, String valididImageUrl, String status, String zipCode, String zone, String password) {
        this.userId = userId;
        this.login_email = login_email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.provinces = provinces;
        this.cities = cities;
        this.barangay = barangay;
        this.profileImageUrl = profileImageUrl;
        this.valididImageUrl = valididImageUrl;
        this.status = status;
        this.zipCode = zipCode;
        this.zone = zone;
        this.password = password;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getLogin_email() { return login_email; }
    public void setLogin_email(String login_email) { this.login_email = login_email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProvinces() { return provinces; }
    public void setProvinces(String provinces) { this.provinces = provinces; }

    public String getCities() { return cities; }
    public void setCities(String cities) { this.cities = cities; }

    public String getBarangay() { return barangay; }
    public void setBarangay(String barangay) { this.barangay = barangay; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getValididImageUrl() { return valididImageUrl; }
    public void setValididImageUrl(String valididImageUrl) { this.valididImageUrl = valididImageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

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
