package com.example.slacks_lottoevent;

import java.util.ArrayList;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Event class that holds all the information for an event
 */
public class Event implements Serializable {

    private String name;
    private String eventDate;
    private String time;
    private String description;
    private String price;
    private int waitListCapacity;
    private int eventSlots;
    private ArrayList<String> waitlisted;
    private ArrayList<String> finalists;
    private ArrayList<String> cancelled;
    private ArrayList<String> selected;
    private ArrayList<String> reselected;

    private String qrCodeData;
    private String qrHash;
    private String eventID;
    private Boolean geoLocation;
    private ArrayList<String> waitlistedNotificationsList;
    private ArrayList<String> selectedNotificationsList;
    private ArrayList<String> joinedNotificationsList;
    private ArrayList<String> cancelledNotificationsList;
    private ArrayList<HashMap<String,List<Double>>> joinLocations;
    private String location;
    private String deviceId;
    private String signupDeadline;
    private Boolean entrantsChosen;
    public Event(){
    }

    /**
     * Constructor for Event
     * @param name
     * @param eventDate
     * @param location
     * @param time
     * @param price
     * @param description
     * @param eventSlots
     * @param waitListCapacity
     * @param qrData
     * @param eventID
     * @param geoLocation
     * @param qrHash
     * @param signupDeadline
     */
    public Event(String name, String eventDate, String location, String time, String price, String description, int eventSlots, int waitListCapacity, String qrData, String eventID, Boolean geoLocation, String qrHash, String deviceId, String signupDeadline) {
        this.name = name;
        this.eventDate = eventDate;
        this.time = time;
        this.price = price;
        this.description = description;
        this.eventSlots = eventSlots;
        this.waitListCapacity = waitListCapacity;
        this.location = location;

        this.waitlisted = new ArrayList<>();
        this.finalists = new ArrayList<>();
        this.cancelled = new ArrayList<>();
        this.selected = new ArrayList<>();
        this.reselected = new ArrayList<>();

        this.waitlistedNotificationsList = new ArrayList<>();
        this.selectedNotificationsList = new ArrayList<>();
        this.cancelledNotificationsList = new ArrayList<>();
        this.joinedNotificationsList = new ArrayList<>();
        if (geoLocation) {
            this.joinLocations = new ArrayList<>();
        }
        this.qrCodeData = qrData;
        this.eventID = eventID;
        this.qrHash = qrHash;
        this.geoLocation = geoLocation;

        this.deviceId = deviceId;
        this.signupDeadline = signupDeadline;

        this.entrantsChosen = false;

    }

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventDate() {
        return this.eventDate;
    }

    public void setEventDate(String date) {
        this.eventDate = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWaitListCapacity() {
        return waitListCapacity;
    }

    public void setWaitListCapacity(int waitListCapacity) { this.waitListCapacity = waitListCapacity;}

    public int getEventSlots() {
        return eventSlots;
    }

    public void setEventSlots(int eventSlots) {
        this.eventSlots = eventSlots;
    }

    public String getQRData() {
        return qrCodeData;
    }

    public void setQRData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }

    public String getQRHash() {
        return qrHash;
    }

    public void setQRHash(String qrHash) {
        this.qrHash = qrHash;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public Boolean getgeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(Boolean new_geoLocation) {
        this.geoLocation = new_geoLocation;
    }

    public ArrayList<String> getWaitlisted() {
        return waitlisted;
    }

    public void addWaitlisted(String entrant) { this.waitlisted.add(entrant); }

    public void setWaitlisted(ArrayList<String> entrants) { this.waitlisted = entrants;}

    public ArrayList<String> getFinalists() {
        return finalists;
    }

    public void addFinalist(String entrant) {this.finalists.add(entrant);}

    public void setFinalists(ArrayList<String> entrants) { this.finalists = entrants;}

    public ArrayList<String> getCancelled() {
        return cancelled;
    }

    public void addCancelled(String entrant) {this.cancelled.add(entrant);}

    public void setCancelled(ArrayList<String> entrants) { this.cancelled = entrants;}

    public ArrayList<String> getSelected() {
        return selected;
    }

    public void addSelected(String entrant) {this.selected.add(entrant);}

    public void setSelected(ArrayList<String> entrants) { this.selected = entrants;}

    public void addReselected(String entrant) {this.reselected.add(entrant);}

    public void setReselected(ArrayList<String> entrants) { this.reselected = entrants;}

    public ArrayList<String> getReselected() {
        return reselected;
    }

    public ArrayList<String> getSelectedNotificationsList(){return selectedNotificationsList; }

    public void addSelectedNotification(String notification) { this.selectedNotificationsList.add(notification); }

    public void setSelectedNotificationsList(ArrayList<String> entrants) { this.selectedNotificationsList = entrants;}

    public ArrayList<String> getWaitlistedNotificationsList(){return waitlistedNotificationsList; }

    public void addWaitlistedNotification(String notification) { this.waitlistedNotificationsList.add(notification); }

    public void setWaitlistedNotificationsList(ArrayList<String> entrants) { this.waitlistedNotificationsList = entrants;}

    public ArrayList<String> getJoinedNotificationsList(){return joinedNotificationsList; }

    public void addJoinedNotification(String notification) { this.joinedNotificationsList.add(notification);}

    public void setJoinedNotificationsList(ArrayList<String> entrants) { this.joinedNotificationsList = entrants;}

    public ArrayList<String> getCancelledNotificationsList(){return cancelledNotificationsList; }

    public void addCancelledNotification(String notification) {this.cancelledNotificationsList.add(notification);}

    public void setCancelledNotificationsList(ArrayList<String> entrants) { this.cancelledNotificationsList = entrants;}

    public String getDeviceId(){
        return deviceId;
    }

    public void setDeviceId(String deviceId){
        this.deviceId = deviceId;
    }

    public String getSignupDeadline() {
        return signupDeadline;
    }

    public void setSignupDeadline(String signupDeadline) {this.signupDeadline = signupDeadline;}

    public void setEntrantsChosen(Boolean answer){this.entrantsChosen = answer;}

    public Boolean getEntrantsChosen(){return this.entrantsChosen;}


    /**
     * Checks if the event is full
     * @return true if the event is full, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Event event = (Event) obj;
        return eventID.equals(event.eventID); // Assuming eventId uniquely identifies an Event
    }

    /**
     * Hashcode for the event
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return eventID.hashCode();
    }


    /**
     * Lottery System for the event
     */
    public void lotterySystem(){
        Integer numOfSelectedEntrants = this.waitlisted.size() >= this.eventSlots ? this.eventSlots : this.waitlisted.size();

        if (!this.waitlisted.isEmpty()){
            Collections.shuffle(this.waitlisted);
            this.selected = new ArrayList<>(this.waitlisted.subList(0, numOfSelectedEntrants));

//            Removes from both the waitlist and reselected if they get picked - TODO: Can remove?
            this.waitlisted.removeAll(this.selected);
            this.reselected.removeAll(this.selected);

            this.waitlisted.removeAll(this.reselected);

        }
        this.entrantsChosen = true;
    }

    /**
     * Re-Selecting System for the event
     */
    public void reSelecting(){
//        Need to check if the slots are not filled up


        if (!(this.eventSlots == this.finalists.size())){
//            current selected list cannot fill the the finalists or the selected size is 0 (meaning declined or cancelled) and the finalists still need more people
            if(this.selected.size() < (this.eventSlots - this.finalists.size()) || (this.selected.size() == 0 && this.finalists.size() < this.eventSlots)) {
                Integer potentialNum = this.eventSlots - this.finalists.size() - this.selected.size();
                Integer numOfSelectedEntrants = this.waitlisted.size() > potentialNum && (potentialNum > 0) ? potentialNum : this.waitlisted.size();

                if (!this.waitlisted.isEmpty()) {
                    Collections.shuffle(this.waitlisted);
                    this.selected = new ArrayList<>(this.waitlisted.subList(0, numOfSelectedEntrants)); // This will remove all entrants who haven't responded "Cancelling them"
                    this.waitlisted.removeAll(this.selected);
                }
            }
        }
    }

    /**
     * Clearing list if event is full*
     */
    public void fullEvent(){
        this.waitlisted.clear();
        this.waitlistedNotificationsList.clear();
    }

    public ArrayList<HashMap<String, List<Double>>> getJoinLocations() {
        return joinLocations;
    }

    public void setJoinLocations(ArrayList<HashMap<String, List<Double>>> joinLocations) {
        this.joinLocations = joinLocations;
    }
}