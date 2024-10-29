package com.example.slacks_lottoevent;

/**
 * Organizer class is a class that represents the organizer of the event.
 * It has a facility and a list of events.
 */
public class Organizer {

    private Facility facility;
    private final EventList events;

    /**
     * Constructor for Organizer class.
     */
    public Organizer() {
        this.events = new EventList();
        this.facility = null;
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