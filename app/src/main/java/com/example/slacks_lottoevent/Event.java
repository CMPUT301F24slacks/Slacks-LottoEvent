package com.example.slacks_lottoevent;

public class Event {

    private Organizer organizer;
    private Facility facility;
    private String name;
    private String date;
    private String time;
    private int waitlistCapacity;
    private int capacity;
    private final EntrantList waitlisted;
    private final EntrantList finalists;

    public Event(Organizer organizer, Facility facility, String name, String date, String time, int waitlistCapacity, int capacity) {
        this.organizer = organizer;
        this.facility = facility;
        this.name = name;
        this.date = date;
        this.time = time;
        this.waitlistCapacity = waitlistCapacity;
        this.capacity = capacity;
        this.waitlisted = new EntrantList();
        this.finalists = new EntrantList();
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
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

    public int getWaitlistCapacity() {
        return waitlistCapacity;
    }

    public void setWaitlistCapacity(int waitlistCapacity) {
        this.waitlistCapacity = waitlistCapacity;
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

}
