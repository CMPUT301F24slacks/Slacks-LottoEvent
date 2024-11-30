package com.example.slacks_lottoevent.model;

import java.util.ArrayList;

/**
 * Organizer class is a class that represents the organizer of the event.
 * It has a facilityId and a list of events.
 */
public class Organizer {

    private String deviceId;
    private ArrayList<String> events;

    public Organizer() {
    } // Empty constructor needed for Firestore

    /**
     * Constructor for Organizer class.
     */
    public Organizer(String deviceId, ArrayList<String> events) {
        this.deviceId = deviceId;
        this.events = events;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public ArrayList<String> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<String> events) {
        this.events = events;
    }
}