package com.example.slacks_lottoevent.model;

/**
 * Facility represents a location where an event can be held.
 */
public class Facility {
    private String facilityName;
    private String streetAddress1;
    private String streetAddress2;
    private String city;
    private String province;
    private String country;
    private String postalCode;
    private String facilityId;
    private String organizerId;
    private String deviceId;

    // No-argument constructor required for Firestore
    public Facility() {}

    public Facility(String facilityName, String streetAddress1, String streetAddress2, String city, String province, String country, String postalCode, String organizerId, String deviceId){
        this.facilityName = facilityName;
        this.streetAddress1 = streetAddress1;
        this.streetAddress2 = streetAddress2;
        this.city = city;
        this.province = province;
        this.country = country;
        this.postalCode = postalCode;
        this.organizerId = organizerId;
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

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public void setStreetAddress2(String streetAddress2) {
        this.streetAddress2 = streetAddress2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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
