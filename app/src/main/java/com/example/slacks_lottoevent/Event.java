package com.example.slacks_lottoevent;

import com.google.zxing.common.BitMatrix;

/**
 * Event class that holds all the information for an event
 */
public class Event {

    private final Organizer organizer;
    private Facility facility;
    private String name;
    private String date;
    private String time;
    private String description;
    private String price;
    private int capacity;
    private int pplSelected;
    private final EntrantList waitlisted;
    private final EntrantList finalists;
    private final EntrantList unselected;
    private final EntrantList invited;
    private String qrCodeData;
    private String eventID;
    private Boolean geoLocation;

    /**
     * Constructor for Event
     * @param organizer
     * @param facility
     * @param name
     * @param date
     * @param time
     * @param price
     * @param description
     * @param pplSelected
     * @param capacity
     * @param qrData
     * @param eventId
     * @param geoLocation
     */
    public Event(Organizer organizer, Facility facility, String name, String date, String time, String price, String description, int pplSelected, int capacity, String qrData, String eventId, Boolean geoLocation) {
        this.organizer = organizer;
        this.facility = facility;
        this.name = name;
        this.date = date;
        this.time = time;
        this.price = price;
        this.description = description;
        this.capacity = capacity;
        this.pplSelected = pplSelected;
        this.waitlisted = new EntrantList();
        this.finalists = new EntrantList();
        this.unselected = new EntrantList();
        this.invited = new EntrantList();
        this.qrCodeData = qrData;
        this.eventID = eventId;
        this.geoLocation = geoLocation;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getPplSelected() {
        return pplSelected;
    }

    public void setPplSelected(int pplSelected) {
        this.pplSelected = pplSelected;
    }

    public String getQRData() {
        return qrCodeData;
    }

    public void setQRData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
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

    public EntrantList getWaitlisted() {
        return waitlisted;
    }

    public EntrantList getFinalists() {
        return finalists;
    }

    public EntrantList getUnselected() {
        return unselected;
    }

    public EntrantList getInvited() {
        return invited;
    }

}