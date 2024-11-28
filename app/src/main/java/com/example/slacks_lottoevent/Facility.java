package com.example.slacks_lottoevent;

/**
 * Facility represents a location where an event can be held.
 */
public class Facility {
    private String facilityName;
    private String streetAddress1;
    private String facilityId;
    private String organizerID;
    private String deviceId;

    /**
     * Default constructor for the Facility class.
     */
    public Facility() {}

    /**
     * Constructor for the Facility class.
     *
     * @param facilityName The name of the facility
     * @param streetAddress1 The first line of the street address
     * @param organizerID The ID of the organizer who owns the facility
     * @param deviceId The ID of the device that created the facility
     */
    public Facility(String facilityName, String streetAddress1, String organizerID, String deviceId){
        this.facilityName = facilityName;
        this.streetAddress1 = streetAddress1;
        this.organizerID = organizerID;
        this.deviceId = deviceId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public void setStreetAddress1(String streetAddress1) {
        this.streetAddress1 = streetAddress1;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getOrganizerID() {
        return organizerID;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
