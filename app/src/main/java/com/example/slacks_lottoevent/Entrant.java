package com.example.slacks_lottoevent;

public class Entrant {
    private final EventList waitlistedEvents;
    private final EventList finalistEvents;
    private final EventList invitedEvents;
    private final EventList uninvitedEvents;

    public Entrant() {
        this.waitlistedEvents = new EventList();
        this.finalistEvents = new EventList();
        this.invitedEvents = new EventList();
        this.uninvitedEvents = new EventList();
    }

    public EventList getWaitlistedEvents() {
        return waitlistedEvents;
    }

    public EventList getFinalistEvents() {
        return finalistEvents;
    }

    public EventList getInvitedEvents() {
        return invitedEvents;
    }

    public EventList getUninvitedEvents() {
        return uninvitedEvents;
    }

}