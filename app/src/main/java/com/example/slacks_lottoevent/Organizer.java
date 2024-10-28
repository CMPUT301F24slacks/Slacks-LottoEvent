package com.example.slacks_lottoevent;

public class Organizer {

    private Facility facility;
    private final EventList events;

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