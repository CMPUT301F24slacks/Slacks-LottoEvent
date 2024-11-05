package com.example.slacks_lottoevent;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

/**
 * Entrant class represents an entrant in the app.
 * An entrant has a list of waitlisted events, finalist events, invited events, and uninvited events.
 */
public class Entrant {

    private final ArrayList<String> waitlistedEvents;
    private final ArrayList<String> finalistEvents;
    private final ArrayList<String> invitedEvents;
    private final ArrayList<String> uninvitedEvents;

    public Entrant() {
        this.waitlistedEvents = new ArrayList<>();
        this.finalistEvents = new ArrayList<>();
        this.invitedEvents = new ArrayList<>();
        this.uninvitedEvents = new ArrayList<>();
    }



    public ArrayList<String> getWaitlistedEvents() {
        return waitlistedEvents;
    }

    public ArrayList<String> getFinalistEvents() {
        return finalistEvents;
    }

    public ArrayList<String> getInvitedEvents() {
        return invitedEvents;
    }

    public ArrayList<String> getUninvitedEvents() {
        return uninvitedEvents;
    }
    public void addWaitlistedEvents(String eventID){
        waitlistedEvents.add(eventID);

    }
    public void addFinalistEvents(String eventID){
        finalistEvents.add(eventID);

    }
    public void addInvitedEvents(String eventID){
        invitedEvents.add(eventID);

    }
    public void addUninvitedEvents(String eventID){
        uninvitedEvents.add(eventID);

    }


}