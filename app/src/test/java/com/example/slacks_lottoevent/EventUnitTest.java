package com.example.slacks_lottoevent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventUnitTest {
    private Event event;

    @BeforeEach
    public void setUp() {
        event = new Event("HackEd", "2024-11-22", "9:00", "$10", "Hackathon hosted by UAlberta students", 200, 500, "0001", "091fac3e-983a-44c0-b7b8-fcf720958082", true, "20b0a9a34c950844a2e0edd7aa5604691dc1ec738a412e9ad436ed19b00acd98", true, false, true);
    }

    @Test
    public void testConstructor() {
        assertEquals("HackEd", event.getName());
        assertEquals("2024-11-22", event.getDate());
        assertEquals("9:00", event.getTime());
        assertEquals("$10", event.getPrice());
        assertEquals("Hackathon hosted by UAlberta students", event.getDescription());
        assertEquals(200, event.getEventSlots());
        assertEquals(500, event.getWaitListCapacity());
        assertEquals("0001", event.getQRData());
        assertEquals("091fac3e-983a-44c0-b7b8-fcf720958082", event.getEventID());
        assertTrue(event.getgeoLocation());
        assertEquals("20b0a9a34c950844a2e0edd7aa5604691dc1ec738a412e9ad436ed19b00acd98", event.getQRHash());
        assertTrue(event.getWaitlistNotifications());
        assertFalse(event.getSelectedNotifications());
        assertTrue(event.getCancelledNotifications());
    }

    @Test
    public void testSettersAndGetters() {
        event.setName("natHacks");
        assertEquals("natHacks", event.getName());

        event.setDate("2024-01-01");
        assertEquals("2024-01-01", event.getDate());

        event.setTime("12:00");
        assertEquals("12:00", event.getTime());

        event.setPrice("$15");
        assertEquals("$15", event.getPrice());

        event.setDescription("Alberta's Neurotech Hackathon");
        assertEquals("Alberta's Neurotech Hackathon", event.getDescription());

        event.setEventSlots(400);
        assertEquals(400, event.getEventSlots());

        event.setWaitListCapacity(600);
        assertEquals(600, event.getWaitListCapacity());

        event.setQRData("1001");
        assertEquals("1001", event.getQRData());

        event.setEventID("732fac3e-983a-44c0-b7b8-fcf720952024");
        assertEquals("732fac3e-983a-44c0-b7b8-fcf720952024", event.getEventID());

        event.setGeoLocation(false);
        assertFalse(event.getgeoLocation());

        event.setQRHash("03b0a9a34c950844a2e0edd7aa5604691dc1ec738a412e9ad436ed19b00usv98");
        assertEquals("03b0a9a34c950844a2e0edd7aa5604691dc1ec738a412e9ad436ed19b00usv98", event.getQRHash());

        event.setWaitlistNotifications(false);
        assertFalse(event.getWaitlistNotifications());

        event.setSelectedNotifications(true);
        assertTrue(event.getSelectedNotifications());

        event.setCancelledNotifications(false);
        assertFalse(event.getCancelledNotifications());
    }

    @Test
    public void testAddAndRetrieveWaitlisted() {
        event.addWaitlisted("iu767e14c6aa6e0d");
        assertTrue(event.getWaitlisted().contains("iu767e14c6aa6e0d"));
        assertEquals(1, event.getWaitlisted().size());
    }

    @Test
    public void testAddAndRetrieveFinalists() {
        event.addFinalist("fu767e14c6aa6e0d");
        assertTrue(event.getFinalists().contains("fu767e14c6aa6e0d"));
        assertEquals(1, event.getFinalists().size());
    }

    @Test
    public void testAddAndRetrieveCancelled() {
        event.addCancelled("po767e14c6aa6e4d");
        assertTrue(event.getCancelled().contains("po767e14c6aa6e4d"));
        assertEquals(1, event.getCancelled().size());
    }

    @Test
    public void testAddAndRetrieveSelected() {
        event.addSelected("uq767e14c6aa6e4q");
        assertTrue(event.getSelected().contains("uq767e14c6aa6e4q"));
        assertEquals(1, event.getSelected().size());
    }

    @Test
    public void testAddAndRetrieveNotifications() {
        event.addWaitlistedNotification("1650c1cc5910ab1a");
        assertTrue(event.getWaitlistedNotificationsList().contains("1650c1cc5910ab1a"));

        event.addSelectedNotification("1773c1cc5910ab7b");
        assertTrue(event.getSelectedNotificationsList().contains("1773c1cc5910ab7b"));

        event.addCancelledNotification("1850c1cc5910ab9q");
        assertTrue(event.getCancelledNotificationsList().contains("1850c1cc5910ab9q"));

        event.addJoinedNotification("2004c1uc5910ab5q");
        assertTrue(event.getJoinedNotificationsList().contains("2004c1uc5910ab5q"));
    }

    @Test
    public void testAddMultipleAndRetrieveNotifications() {
        // Add multiple waitlisted notifications
        event.addWaitlistedNotification("1650c1cc5910ab1a");
        event.addWaitlistedNotification("1650c1cc5910ab1b");
        event.addWaitlistedNotification("1650c1cc5910ab1c");
        assertTrue(event.getWaitlistedNotificationsList().contains("1650c1cc5910ab1a"));
        assertTrue(event.getWaitlistedNotificationsList().contains("1650c1cc5910ab1b"));
        assertTrue(event.getWaitlistedNotificationsList().contains("1650c1cc5910ab1c"));
        assertEquals(3, event.getWaitlistedNotificationsList().size());

        // Add multiple selected notifications
        event.addSelectedNotification("1773c1cc5910ab7b");
        event.addSelectedNotification("1773c1cc5910ab7c");
        event.addSelectedNotification("1773c1cc5910ab7d");
        assertTrue(event.getSelectedNotificationsList().contains("1773c1cc5910ab7b"));
        assertTrue(event.getSelectedNotificationsList().contains("1773c1cc5910ab7c"));
        assertTrue(event.getSelectedNotificationsList().contains("1773c1cc5910ab7d"));
        assertEquals(3, event.getSelectedNotificationsList().size());

        // Add multiple cancelled notifications
        event.addCancelledNotification("1850c1cc5910ab9q");
        event.addCancelledNotification("1850c1cc5910ab9r");
        event.addCancelledNotification("1850c1cc5910ab9s");
        assertTrue(event.getCancelledNotificationsList().contains("1850c1cc5910ab9q"));
        assertTrue(event.getCancelledNotificationsList().contains("1850c1cc5910ab9r"));
        assertTrue(event.getCancelledNotificationsList().contains("1850c1cc5910ab9s"));
        assertEquals(3, event.getCancelledNotificationsList().size());

        // Add multiple joined notifications
        event.addJoinedNotification("2004c1uc5910ab5q");
        event.addJoinedNotification("2004c1uc5910ab5r");
        event.addJoinedNotification("2004c1uc5910ab5s");
        assertTrue(event.getJoinedNotificationsList().contains("2004c1uc5910ab5q"));
        assertTrue(event.getJoinedNotificationsList().contains("2004c1uc5910ab5r"));
        assertTrue(event.getJoinedNotificationsList().contains("2004c1uc5910ab5s"));
        assertEquals(3, event.getJoinedNotificationsList().size());
    }


    @Test
    public void testNotificationsListsInitialization() {
        assertNotNull(event.getWaitlistedNotificationsList());
        assertNotNull(event.getSelectedNotificationsList());
        assertNotNull(event.getJoinedNotificationsList());
        assertNotNull(event.getCancelledNotificationsList());
    }

    @Test
    public void testEmptyConstructor() {
        Event emptyEvent = new Event();
        assertNotNull(emptyEvent);
        assertNull(emptyEvent.getName());
        assertNull(emptyEvent.getDate());
        assertNull(emptyEvent.getTime());
        assertNull(emptyEvent.getPrice());
        assertNull(emptyEvent.getDescription());
        assertEquals(0, emptyEvent.getEventSlots());
        assertEquals(0, emptyEvent.getWaitListCapacity());
        assertNull(emptyEvent.getQRData());
        assertNull(emptyEvent.getQRHash());
        assertNull(emptyEvent.getEventID());
        assertNull(emptyEvent.getgeoLocation());
        assertNull(emptyEvent.getWaitlistNotifications());
        assertNull(emptyEvent.getSelectedNotifications());
        assertNull(emptyEvent.getCancelledNotifications());
    }
}
