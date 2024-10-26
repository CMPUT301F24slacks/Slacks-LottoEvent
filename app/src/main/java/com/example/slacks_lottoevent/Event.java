package com.example.slacks_lottoevent;

public class Event {

    private final Organizer organizer;
    private Facility facility;
    private String name;
    private String date;
    private String time;
    private String description;
    private int capacity;
    private final EntrantList waitlisted;
    private final EntrantList finalists;
    private final EntrantList unselected;
    private final EntrantList invited;

    public Event(Organizer organizer, Facility facility, String name, String date, String time, String description, int capacity) {
        this.organizer = organizer;
        this.facility = facility;
        this.name = name;
        this.date = date;
        this.time = time;
        this.description = description;
        this.capacity = capacity;
        this.waitlisted = new EntrantList();
        this.finalists = new EntrantList();
        this.unselected = new EntrantList();
        this.invited = new EntrantList();
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