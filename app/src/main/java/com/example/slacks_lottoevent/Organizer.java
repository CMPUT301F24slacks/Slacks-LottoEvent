package com.example.slacks_lottoevent;

public class Organizer {

    private Facility facility;
    private final EventList events;

    public Organizer(Facility facility) {
        this.facility = facility;
        this.events = new EventList();
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