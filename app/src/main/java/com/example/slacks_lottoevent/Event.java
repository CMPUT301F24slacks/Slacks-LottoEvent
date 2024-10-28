package com.example.slacks_lottoevent;

import com.google.zxing.common.BitMatrix;

public class Event {

    private final Organizer organizer;
    private Facility facility;
    private String name;
    private String date;
    private String time;
    private String description;
    private String price;
    private int capacity;
    private int signupAcpt;
    private String extraDesc;
    private final EntrantList waitlisted;
    private final EntrantList finalists;
    private final EntrantList unselected;
    private final EntrantList invited;
    private String qrCodeData;

    public Event(Organizer organizer, Facility facility, String name, String date, String time, String price, String description, int capacity, int signupAccpt, String xtraDesc, String qrData) {
        this.organizer = organizer;
        this.facility = facility;
        this.name = name;
        this.date = date;
        this.time = time;
        this.price = price;
        this.description = description;
        this.capacity = capacity;
        this.signupAcpt = signupAccpt;
        this.extraDesc = xtraDesc;
        this.waitlisted = new EntrantList();
        this.finalists = new EntrantList();
        this.unselected = new EntrantList();
        this.invited = new EntrantList();
        this.qrCodeData = qrData;
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

    public int getSignupAcpt() {
        return signupAcpt;
    }

    public void setSignupAcpt(int signupAcpt) {
        this.signupAcpt = signupAcpt;
    }

    public String getQRData() {
        return qrCodeData;
    }

    public void setQRData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }

    public String getExtraDesc() {
        return extraDesc;
    }

    public void setExtraDesc(String xtraDescription) { this.description = xtraDescription; }

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