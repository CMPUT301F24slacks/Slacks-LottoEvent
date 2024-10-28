package com.example.slacks_lottoevent;

/**
 * Facility represents a location where an event can be held.
 */
public class Facility {

    private final Organizer organizer;
    private String name;
    private String address;

    /**
     * Constructs a new Facility object.
     *
     * @param organizer the organizer of the facility
     * @param name the name of the facility
     * @param address the address of the facility
     */
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