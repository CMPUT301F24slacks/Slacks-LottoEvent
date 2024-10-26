package com.example.slacks_lottoevent;

public class Facility {

    private final Organizer organizer;
    private String name;
    private String address;

    public Facility(Organizer organizer, String name, String address) {
        this.organizer = organizer;
        this.name = name;
        this.address = address;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}