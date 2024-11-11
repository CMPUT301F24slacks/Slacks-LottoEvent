package com.example.slacks_lottoevent;

import java.util.ArrayList;

import java.io.Serializable;

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
    private String qrCodeData;
    private String qrHash;
    private String eventID;
    private Boolean geoLocation;
    private ArrayList<String> waitlistedNotificationsList;
    private ArrayList<String> selectedNotificationsList;
    private ArrayList<String> joinedNotificationsList;
    private ArrayList<String> cancelledNotificationsList;
    private String location;
    private String deviceId;
    private String signupDeadline;
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
     *
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

        this.waitlistedNotificationsList = new ArrayList<>();
        this.selectedNotificationsList = new ArrayList<>();
        this.cancelledNotificationsList = new ArrayList<>();
        this.joinedNotificationsList = new ArrayList<>();

        this.qrCodeData = qrData;
        this.eventID = eventID;
        this.qrHash = qrHash;
        this.geoLocation = geoLocation;

        this.deviceId = deviceId;
        this.signupDeadline = signupDeadline;

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
        return eventDate;
    }

    public void setEventDate(String date) {
        this.eventDate = eventDate;
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

    public ArrayList<String> getFinalists() {
        return finalists;
    }

    public void addFinalist(String entrant) {this.finalists.add(entrant);}

    public ArrayList<String> getCancelled() {
        return cancelled;
    }

    public void addCancelled(String entrant) {this.cancelled.add(entrant);}

    public ArrayList<String> getSelected() {
        return selected;
    }

    public void addSelected(String entrant) {this.selected.add(entrant);}

    public ArrayList<String> getSelectedNotificationsList(){return selectedNotificationsList; }

    public void addSelectedNotification(String notification) { this.selectedNotificationsList.add(notification); }

    public ArrayList<String> getWaitlistedNotificationsList(){return waitlistedNotificationsList; }

    public void addWaitlistedNotification(String notification) { this.waitlistedNotificationsList.add(notification); }

    public ArrayList<String> getJoinedNotificationsList(){return joinedNotificationsList; }

    public void addJoinedNotification(String notification) { this.joinedNotificationsList.add(notification);}

    public ArrayList<String> getCancelledNotificationsList(){return cancelledNotificationsList; }

    public void addCancelledNotification(String notification) {this.cancelledNotificationsList.add(notification);}

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
}