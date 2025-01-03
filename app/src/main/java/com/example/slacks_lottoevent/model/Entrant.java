package com.example.slacks_lottoevent.model;

import java.util.ArrayList;

/**
 * The {@code Entrant} class represents a participant in the app.
 * Each entrant has lists of events they are waitlisted for, have become finalists in,
 * have been invited to, and have not been invited to, as well as a list of event invites.
 */
public class Entrant {

    private ArrayList<String> waitlistedEvents;
    private ArrayList<String> finalistEvents;
    private ArrayList<String> invitedEvents;
    private ArrayList<String> uninvitedEvents;

    /**
     * Constructs an {@code Entrant} instance with empty event lists.
     */
    public Entrant() {
        this.waitlistedEvents = new ArrayList<>();
        this.finalistEvents = new ArrayList<>();
        this.invitedEvents = new ArrayList<>();
        this.uninvitedEvents = new ArrayList<>();
    }

    /**
     * Retrieves the list of events the entrant is waitlisted for.
     *
     * @return a list of waitlisted event IDs.
     */
    public ArrayList<String> getWaitlistedEvents() {
        return waitlistedEvents;
    }

    public void setWaitlistedEvents(ArrayList<String> waitlistedEvents) {
        this.waitlistedEvents = waitlistedEvents;
    }

    /**
     * Retrieves the list of events where the entrant is a finalist.
     *
     * @return a list of finalist event IDs.
     */
    public ArrayList<String> getFinalistEvents() {
        return finalistEvents;
    }

    public void setFinalistEvents(ArrayList<String> finalistEvents) {
        this.finalistEvents = finalistEvents;
    }

    /**
     * Retrieves the list of events the entrant has been invited to.
     *
     * @return a list of invited event IDs.
     */
    public ArrayList<String> getInvitedEvents() {
        return invitedEvents;
    }

    public void setInvitedEvents(ArrayList<String> invitedEvents) {
        this.invitedEvents = invitedEvents;
    }

    /**
     * Retrieves the list of events the entrant has not been invited to.
     *
     * @return a list of uninvited event IDs.
     */
    public ArrayList<String> getUninvitedEvents() {
        return uninvitedEvents;
    }

    public void setUninvitedEvents(ArrayList<String> uninvitedEvents) {
        this.uninvitedEvents = uninvitedEvents;
    }

    /**
     * Adds an event to the waitlisted events list.
     *
     * @param eventID the ID of the event to add.
     */
    public void addWaitlistedEvents(String eventID) {
        waitlistedEvents.add(eventID);
    }

    /**
     * Adds an event to the finalist events list.
     *
     * @param eventID the ID of the event to add.
     */
    public void addFinalistEvents(String eventID) {
        finalistEvents.add(eventID);
    }

    /**
     * Adds an event to the invited events list.
     *
     * @param eventID the ID of the event to add.
     */
    public void addInvitedEvents(String eventID) {
        invitedEvents.add(eventID);
    }

    /**
     * Adds an event to the uninvited events list.
     *
     * @param eventID the ID of the event to add.
     */
    public void addUninvitedEvents(String eventID) {
        uninvitedEvents.add(eventID);
    }

}
