package com.example.slacks_lottoevent;

public class UserEventNotifications {
    private String name;
    private String date;
    private String time;
    private String location;
    private String eventId; // Add event ID field

    /**
     * UserEventNotifications is a class that represents the user's event notifications.
     * It takes in the event's name, date, time, location, and event ID.
     */
    public UserEventNotifications(String name, String date, String time, String location, String eventId) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.eventId = eventId; // Initialize event ID
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getEventId() {
        return eventId; // Provide access to event ID
    }
}
