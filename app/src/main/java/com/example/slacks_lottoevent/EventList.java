package com.example.slacks_lottoevent;

import com.example.slacks_lottoevent.model.Event;

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

    /**
     * Getter method for the event list.
     *
     * @return The ArrayList of Event objects
     */
    public ArrayList<Event> getEventList() {
        return eventList;
    }

    /**
     * Setter method for the event list.
     *
     * @param event The ArrayList of Event objects
     */
    public void addEvent(Event event) {
        eventList.add(event);
    }
}