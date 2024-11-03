package com.example.slacks_lottoevent;

import com.google.firebase.firestore.auth.User;

/**
 * Organizer class is a class that represents the organizer of the event.
 * It has a facility and a list of events.
 */
public class Organizer {

    private final Profile user;
    private Facility facility;
    private final EventList events;

    /**
     * Constructor for Organizer class.
     */
    public Organizer(Profile user) {
        this.user = user;
        this.events = new EventList();
        this.facility = null;
    }

    public Profile getUser() {
        return user;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public EventList getEvents() {
        return events;
    }
}