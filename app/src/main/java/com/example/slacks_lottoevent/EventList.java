package com.example.slacks_lottoevent;

import java.util.ArrayList;

public class EventList {
    ArrayList<Event> eventList;

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