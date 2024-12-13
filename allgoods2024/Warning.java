package com.example.allgoods2024;

public class Warning {
    private String name;
    private String reason;
    private String userId;
    private boolean isRead;

    public Warning() {
        // Default constructor required for Firebase
    }

    public Warning(String name, String reason, String userId, boolean isRead) {
        this.name = name;
        this.reason = reason;
        this.userId = userId;
        this.isRead = isRead;
    }

    public String getName() {
        return name;
    }

    public String getReason() {
        return reason;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
