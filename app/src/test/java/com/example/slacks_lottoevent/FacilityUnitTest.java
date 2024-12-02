package com.example.slacks_lottoevent;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.slacks_lottoevent.model.Facility;

class FacilityUnitTest {

    @Test
    void testDefaultConstructor() {
        Facility facility = new Facility();
        assertNull(facility.getFacilityName());
        assertNull(facility.getStreetAddress());
        assertNull(facility.getDeviceId());
    }

    @Test
    void testParameterizedConstructor() {
        String facilityName = "DICE";
        String streetAddress = "9211 116 St NW, Edmonton, AB T6G 1H9";
        String deviceId = "fe8e1540320d3c3d";

        Facility facility = new Facility(facilityName, streetAddress, deviceId);

        assertEquals(facilityName, facility.getFacilityName());
        assertEquals(streetAddress, facility.getStreetAddress());
        assertEquals(deviceId, facility.getDeviceId());
    }

    @Test
    void testGetAndSetFacilityName() {
        Facility facility = new Facility();
        String facilityName = "Donnadeo ICE 8-244";

        facility.setFacilityName(facilityName);
        assertEquals(facilityName, facility.getFacilityName());
    }

    @Test
    void testGetAndSetStreetAddress() {
        Facility facility = new Facility();
        String streetAddress = "9120 116 St NW, Edmonton, AB T6G 2V4";

        facility.setStreetAddress(streetAddress);
        assertEquals(streetAddress, facility.getStreetAddress());
    }

    @Test
    void testGetAndSetDeviceId() {
        Facility facility = new Facility();
        String deviceId = "le8e1540320d5c6d";

        facility.setDeviceId(deviceId);
        assertEquals(deviceId, facility.getDeviceId());
    }

    @Test
    void testFacilityEquality() {
        String facilityName = "ETLC";
        String streetAddress = "9120 116 St NW, Edmonton, AB T6G 2V4";
        String deviceId = "pj8e1390320d5c6e";

        Facility facility1 = new Facility(facilityName, streetAddress, deviceId);
        Facility facility2 = new Facility(facilityName, streetAddress, deviceId);

        assertEquals(facility1.getFacilityName(), facility2.getFacilityName());
        assertEquals(facility1.getStreetAddress(), facility2.getStreetAddress());
        assertEquals(facility1.getDeviceId(), facility2.getDeviceId());
    }
}
