package com.example.allgoods2024;

public class ArchivedPermit {
    private String oldPermitImageUrl;
    private String oldExpirationDate;

    public ArchivedPermit() {
        // Default constructor required for calls to DataSnapshot.getValue(ArchivedPermit.class)
    }

    public ArchivedPermit(String oldPermitImageUrl, String oldExpirationDate) {
        this.oldPermitImageUrl = oldPermitImageUrl;
        this.oldExpirationDate = oldExpirationDate;
    }

    public String getOldPermitImageUrl() {
        return oldPermitImageUrl;
    }

    public String getOldExpirationDate() {
        return oldExpirationDate;
    }
}