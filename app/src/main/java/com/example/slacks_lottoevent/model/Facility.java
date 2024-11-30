package com.example.slacks_lottoevent.model;

/**
 * Facility represents a location where an event can be held.
 */
public class Facility {
    private String facilityName;
    private String streetAddress;
    private String deviceId;

    public Facility() {
    } // Needed for Firestore

    /**
     * Constructor for the Facility class.
     *
     * @param facilityName  The name of the facility
     * @param streetAddress The first line of the street address
     * @param deviceId      The ID of the device that created the facility
     */
    public Facility(String facilityName, String streetAddress, String deviceId) {
        this.facilityName = facilityName;
        this.streetAddress = streetAddress;
        this.deviceId = deviceId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
