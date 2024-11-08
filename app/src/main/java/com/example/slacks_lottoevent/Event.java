package com.example.slacks_lottoevent;

import java.util.ArrayList;

/**
 * Event class that holds all the information for an event
 */
public class Event {

    private String name;
    private String date;
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
    private Boolean waitlistNotifications;
    private Boolean selectedNotifications;
    private Boolean cancelledNotifications;
    private ArrayList<String> waitlistedNotificationsList;
    private ArrayList<String> selectedNotificationsList;
    private ArrayList<String> joinedNotificationsList;
    private ArrayList<String> cancelledNotificationsList;
    private String location;
    public Event(){

    }

    /**
     * Constructor for Event
     * @param name
     * @param date
     * @param time
     * @param price
     * @param description
     * @param eventSlots
     * @param waitListCapacity
     * @param qrData
     * @param eventID
     * @param geoLocation
     * @param waitlistNotifications
     * @param selectedNotifications
     * @param cancelledNotifications
     * @param qrHash
     */
    public Event(String name, String date, String location, String time, String price, String description, int eventSlots, int waitListCapacity, String qrData, String eventID, Boolean geoLocation, String qrHash, Boolean waitlistNotifications, Boolean selectedNotifications, Boolean cancelledNotifications) {
        this.name = name;
        this.date = date;
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

        this.waitlistNotifications = waitlistNotifications;
        this.cancelledNotifications = cancelledNotifications;
        this.selectedNotifications = selectedNotifications;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public void setWaitListCapacity(int waitListCapacity) {
        this.waitListCapacity = waitListCapacity;
    }

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

    public Boolean getWaitlistNotifications() {
        return waitlistNotifications;
    }

    public void setWaitlistNotifications(Boolean waitlistNotifications) {
        this.waitlistNotifications = waitlistNotifications;
    }

    public Boolean getSelectedNotifications() {
        return selectedNotifications;
    }

    public void setSelectedNotifications(Boolean selectedNotifications) {
        this.selectedNotifications = selectedNotifications;
    }

    public Boolean getCancelledNotifications() {
        return cancelledNotifications;
    }

    public void setCancelledNotifications(Boolean cancelled_notifications) { this.cancelledNotifications = cancelled_notifications; }

    public ArrayList<String> getSelectedNotificationsList(){return selectedNotificationsList; }

    public void addSelectedNotification(String notification) { this.selectedNotificationsList.add(notification); }

    public ArrayList<String> getWaitlistedNotificationsList(){return waitlistedNotificationsList; }

    public void addWaitlistedNotification(String notification) { this.waitlistedNotificationsList.add(notification); }

    public ArrayList<String> getJoinedNotificationsList(){return joinedNotificationsList; }

    public void addJoinedNotification(String notification) { this.joinedNotificationsList.add(notification);}

    public ArrayList<String> getCancelledNotificationsList(){return cancelledNotificationsList; }

    public void addCancelledNotification(String notification) {this.cancelledNotificationsList.add(notification);}

}