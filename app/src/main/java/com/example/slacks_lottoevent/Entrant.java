package com.example.slacks_lottoevent;

public class Entrant {
    EventList entrantEventList;

    public EventList getEntrantEventList() {
        return entrantEventList;
    }

    public void setEntrantEventList(EventList entrantEventList) {
        this.entrantEventList = entrantEventList;
    }

    public Entrant(){

        this.entrantEventList = new EventList();

    }

}
