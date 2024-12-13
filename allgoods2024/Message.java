package com.example.allgoods2024;

public class Message {
    private String senderId;
    private String receiverId;
    private String category;
    private String text;
    private String image;
    private String timestamp;
    private boolean isRead;

    public Message() {
    }

    public Message(String senderId, String receiverId, String category, String text, String image, String timestamp, Boolean isRead) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.category = category;
        this.text = text;
        this.image = image;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean getIsRead() { return isRead; }
    public void setIsRead(boolean isRead) { this.isRead = isRead; }
}
