package com.example.slacks_lottoevent;

import java.util.ArrayList;

public class EventList {
    ArrayList eventList;

    public ArrayList getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList eventList) {
        this.eventList = eventList;
    }

    public EventList(){
        this.eventList = new ArrayList<String>();
    }
}
