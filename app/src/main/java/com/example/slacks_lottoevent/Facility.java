package com.example.slacks_lottoevent;

/**
 * Facility represents a location where an event can be held.
 */
public class Facility {
    private String name;
    private String streetAddress1;
    private String streetAddress2;
    private String postalCode;
    private String facilityId;
    private String organizerId;
    private String deviceId;

    /**
     * Default constructor for the Facility class.
     */
    public Facility() {}

    /**
     * Constructor for the Facility class.
     *
     * @param name The name of the facility
     * @param streetAddress1 The first line of the street address
     * @param streetAddress2 The second line of the street address
     * @param postalCode The postal code of the facility
     * @param organizerId The ID of the organizer who owns the facility
     * @param deviceId The ID of the device that created the facility
     */
    public Facility(String name, String streetAddress1, String streetAddress2, String postalCode, String organizerId, String deviceId){
        this.name = name;
        this.streetAddress1 = streetAddress1;
        this.streetAddress2 = streetAddress2;
        this.postalCode = postalCode;
        this.organizerId = organizerId;
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public void setStreetAddress1(String streetAddress1) {
        this.streetAddress1 = streetAddress1;
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public void setStreetAddress2(String streetAddress2) {
        this.streetAddress2 = streetAddress2;
    }



    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
