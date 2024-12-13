package com.example.allgoods2024;

import java.util.List;

public class Event {
    private String title;
    private String description;
    private String discount;
    private String eventDate;
    private String event_category;
    private List<String> images;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String title, String description, String discount, String eventDate, String event_category, List<String> images) {
        this.title = title;
        this.description = description;
        this.discount = discount;
        this.eventDate = eventDate;
        this.event_category = event_category;
        this.images = images;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDiscount() {
        return discount;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventCategory() {
        return event_category;
    }

    public List<String> getImages() {
        return images;
    }
}
