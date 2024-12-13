package com.example.allgoods2024;

public class Notification {
    private String message;
    private String date;
    private int colorResId;

    public Notification() {}

    public Notification(String message, String date, int colorResId) {
        this.message = message;
        this.date = date;
        this.colorResId = colorResId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getColorResId() {
        return colorResId;
    }

    public void setColorResId(int colorResId) {
        this.colorResId = colorResId;
    }
}


