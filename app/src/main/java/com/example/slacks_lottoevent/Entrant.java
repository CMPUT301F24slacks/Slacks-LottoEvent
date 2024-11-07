package com.example.slacks_lottoevent;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

/**
 * Entrant class represents an entrant in the app.
 * An entrant has a list of waitlisted events, finalist events, invited events, and uninvited events.
 */
public class Entrant {

    private final String userId;
    private final ArrayList<String> waitlistedEvents;
    private final ArrayList<String> finalistEvents;
    private final ArrayList<String> invitedEvents;
    private final ArrayList<String> uninvitedEvents;

    private final ArrayList<String> waitlistedEventsNotis;
    private final ArrayList<String> finalistEventsNotis;
    private final ArrayList<String> invitedEventsNotis;
    private final ArrayList<String> uninvitedEventsNotis;

    public Entrant(String userId) {
        this.userId = userId;
        this.waitlistedEvents = new ArrayList<>();
        this.finalistEvents = new ArrayList<>();
        this.invitedEvents = new ArrayList<>();
        this.uninvitedEvents = new ArrayList<>();

        this.waitlistedEventsNotis = new ArrayList<>();
        this.finalistEventsNotis = new ArrayList<>();
        this.invitedEventsNotis = new ArrayList<>();
        this.uninvitedEventsNotis = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
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

    public ArrayList<String> getWaitlistedEventsNotis() {
        return waitlistedEventsNotis;
    }

    public ArrayList<String> getFinalistEventsNotis() {
        return finalistEventsNotis;
    }

    public ArrayList<String> getInvitedEventsNotis() {
        return invitedEventsNotis;
    }

    public ArrayList<String> getUninvitedEventsNotis() {
        return uninvitedEventsNotis;
    }

    public void addWaitlistedEventsNotis(String eventID){ waitlistedEventsNotis.add(eventID);}

    public void addFinalistEventsNotis(String eventID){ finalistEventsNotis.add(eventID);}

    public void addInvitedEventsNotis(String eventID){ invitedEventsNotis.add(eventID);}

    public void addUninvitedEventsNotis(String eventID){ uninvitedEventsNotis.add(eventID);}


}