package com.example.slacks_lottoevent;

/**
 * Entrant class represents an entrant in the app.
 * An entrant has a list of waitlisted events, finalist events, invited events, and uninvited events.
 */
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