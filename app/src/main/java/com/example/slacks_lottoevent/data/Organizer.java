package com.example.slacks_lottoevent.data;

import java.util.ArrayList;

/**
 * Organizer class is a class that represents the organizer of the event.
 * It has a facilityId and a list of events.
 */
public class Organizer {

    private final String userId;
    private String facilityId;
    private final ArrayList<String> events;

    /**
     * Constructor for Organizer class.
     */
    public Organizer(String userId) {
        this.userId = userId;
        this.events = new ArrayList<>();
        this.facilityId = null;
    }

    public String getUserId() {
        return userId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public ArrayList<String> getEvents() {
        return events;
    }
}