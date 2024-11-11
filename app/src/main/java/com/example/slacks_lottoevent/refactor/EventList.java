package com.example.slacks_lottoevent.refactor;

import java.util.ArrayList;

/**
 * EventList class is a list of events.
 * It contains an ArrayList of Event objects.
 */
public class EventList {
    ArrayList<Event> eventList;

    /**
     * Constructor for EventList class.
     * Initializes an empty ArrayList of Event objects.
     */
    public EventList() {
        this.eventList = new ArrayList<Event>();
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public void addEvent(Event event) {
        eventList.add(event);
    }
}