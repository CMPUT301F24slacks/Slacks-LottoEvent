package com.example.slacks_lottoevent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FacilityUnitTest {
    private Facility facility;

    @BeforeEach
    void setUp(){
        facility = new Facility("DICE", "116 St NW", "9211", "Edmonton", "Alberta", "Canada", "T6G 1H9", "jw7e1540320d3c3d" );
    }
    @Test
    void testFacilityConstructor() {
        assertEquals("DICE", facility.getFacilityName());
        assertEquals("116 St NW", facility.getStreetAddress1());
        assertEquals("9211", facility.getStreetAddress2());
        assertEquals("Edmonton", facility.getCity());
        assertEquals("Alberta", facility.getProvince());
        assertEquals("Canada", facility.getCountry());
        assertEquals("T6G 1H9", facility.getPostalCode());
        assertEquals("jw7e1540320d3c3d", facility.getOrganizerId());
    }

    @Test
    void testSetAndGetFacilityName() {
        facility.setFacilityName("CN Tower");
        assertEquals("CN Tower", facility.getFacilityName());
    }

    @Test
    void testSetAndGetStreetAddress1() {
        facility.setStreetAddress1("290 Bremner Blvd");
        assertEquals("290 Bremner Blvd", facility.getStreetAddress1());
    }

    @Test
    void testSetAndGetStreetAddress2() {
        facility.setStreetAddress2("8900");
        assertEquals("8900", facility.getStreetAddress2());
    }

    @Test
    void testSetAndGetCity() {
        facility.setCity("Toronto");
        assertEquals("Toronto", facility.getCity());
    }

    @Test
    void testSetAndGetProvince() {
        facility.setProvince("Ontario");
        assertEquals("Ontario", facility.getProvince());
    }

    @Test
    void testSetAndGetCountry() {
        facility.setCountry("USA");
        assertEquals("USA", facility.getCountry());
    }

    @Test
    void testSetAndGetPostalCode() {
        facility.setPostalCode("M5V 3L9");
        assertEquals("M5V 3L9", facility.getPostalCode());
    }

    @Test
    void testSetAndGetFacilityId() {
        facility.setFacilityId("PnALTPQ1wbG9CtqVAPDo");
        assertEquals("PnALTPQ1wbG9CtqVAPDo", facility.getFacilityId());
    }

    @Test
    void testGetOrganizerId() {
        assertEquals("jw7e1540320d3c3d", facility.getOrganizerId());
    }

}
