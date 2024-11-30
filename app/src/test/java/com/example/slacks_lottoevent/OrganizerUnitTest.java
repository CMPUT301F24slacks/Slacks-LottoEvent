package com.example.slacks_lottoevent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.slacks_lottoevent.model.Organizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * Unit tests for the Organizer class.
 */
public class OrganizerUnitTest {
    private Organizer organizer;

    /**
     * Set up the organizer object before each test.
     */
    @BeforeEach
    public void setUp(){
        organizer = new Organizer("fe8e1540320d3c3d", new ArrayList<>());
    }

    /**
     * Test the Organizer constructor.
     */
    @Test
    public void testGetUserId() {
        // Check if the userId is correctly assigned
        assertEquals("fe8e1540320d3c3d", organizer.getDeviceId());
    }

    /**
     * Test the Organizer constructor with a null userId.
     */
    @Test
    public void testGetEventsInitiallyEmpty() {
        // Events list should be empty upon initialization
        assertTrue(organizer.getEvents().isEmpty());
    }

    /**
     * Test the Organizer constructor with a null userId.
     */
    @Test
    public void testAddEvent() {
        // Add an event to the events list and verify it
        organizer.getEvents().add("0d7577ae-16f9-4f46-9be4-fe65e9a4e180");
        assertEquals(1, organizer.getEvents().size());
        assertTrue(organizer.getEvents().contains("0d7577ae-16f9-4f46-9be4-fe65e9a4e180"));
    }

    /**
     * Test the Organizer constructor with a null userId.
     */
    @Test
    public void testAddMultipleEvents() {
        // Add multiple events and verify the events list
        organizer.getEvents().add("1d7577ae-16f9-4f46-9be4-fe65e9a4e690");
        organizer.getEvents().add("2d7277ue-15f9-4f06-8be4-op90e9a4e790");
        organizer.getEvents().add("3y0096pe-14f9-4f96-4be4-ip90e3a4e390");
        assertEquals(3, organizer.getEvents().size());
        assertTrue(organizer.getEvents().contains("1d7577ae-16f9-4f46-9be4-fe65e9a4e690"));
        assertTrue(organizer.getEvents().contains("2d7277ue-15f9-4f06-8be4-op90e9a4e790"));
        assertTrue(organizer.getEvents().contains("3y0096pe-14f9-4f96-4be4-ip90e3a4e390"));
    }

    /**
     * Test the Organizer constructor with a null userId.
     */
    @Test
    public void testGetEventsReturnsModifiableList() {
        // Ensure the returned list can be modified without issues
        ArrayList<String> events = organizer.getEvents();
        events.add("7y1196pe-03f9-4f96-4be4-zp76e3a4e307");
        assertEquals(1, organizer.getEvents().size());
        assertTrue(organizer.getEvents().contains("7y1196pe-03f9-4f96-4be4-zp76e3a4e307"));
    }

}
