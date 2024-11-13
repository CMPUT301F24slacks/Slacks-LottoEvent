package com.example.slacks_lottoevent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.slacks_lottoevent.model.Event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * EventUnitTest is a JUnit test class that tests the Event class.
 */
public class EventUnitTest {
    private Event event;

    /**
     * setUp method initializes the Event object before each test.
     */
    @BeforeEach
    public void setUp() {
        event = new Event("HackEd", "2024-01-01", "3339 607 St NW Edmonton AB Canada T5J 0X6", "9:00", "29.99", "Hackathon hosted by UAlberta students", 200, 500, "0001", "091fac3e-983a-44c0-b7b8-fcf720958082", true, "20b0a9a34c950844a2e0edd7aa5604691dc1ec738a412e9ad436ed19b00acd98", "3b48474333", "2024-08-08");
    }

    /**
     * testEventConstructor tests the Event constructor.
     */
    @Test
    public void testSetName() {
        event.setName("natHacks");
        assertEquals("natHacks", event.getName(), "Name should be updated correctly.");
    }

    /**
     * Tests that the event date can be set and retrieved correctly.
     */
    @Test
    public void testSetEventDate() {
        event.setEventDate("2024-01-01");
        assertEquals("2024-01-01", event.getEventDate(), "Date should be updated correctly.");
    }

    /**
     * Tests that the event signupDeadline can be set and retrieved correctly.
     */
    @Test
    public void testSetSignUpDeadline() {
        event.setSignupDeadline("2024-08-08");
        assertEquals("2024-08-08", event.getSignupDeadline(), "Date should be updated correctly.");
    }

    /**
     * Tests that the event time can be set and retrieved correctly.
     */
    @Test
    public void testSetLocation() {
        event.setLocation("9923 602 St NW Edmonton AB Canada T5K 0M6");
        assertEquals("9923 602 St NW Edmonton AB Canada T5K 0M6", event.getLocation(), "Location should be updated correctly.");
    }
    /**
     * Tests that the event time can be set and retrieved correctly.
     */
    @Test
    public void testSetTime() {
        event.setTime("12:00");
        assertEquals("12:00", event.getTime(), "Time should be updated correctly.");
    }

    /**
     * Tests that the event price can be set and retrieved correctly.
     */
    @Test
    public void testSetPrice() {
        event.setPrice("29.99");
        assertEquals("29.99", event.getPrice(), "Price should be updated correctly.");
    }

    /**
     * Tests that the event description can be set and retrieved correctly.
     */
    @Test
    public void testSetDescription() {
        event.setDescription("Alberta's Neurotech Hackathon");
        assertEquals("Alberta's Neurotech Hackathon", event.getDescription(), "Description should be updated correctly.");
    }

    /**
     * Tests that the number of event slots can be set and retrieved correctly.
     */
    @Test
    public void testSetEventSlots() {
        event.setEventSlots(400);
        assertEquals(400, event.getEventSlots(), "Event slots should be updated correctly.");
    }

    /**
     * Tests that the event waitlist capacity can be set and retrieved correctly.
     */
    @Test
    public void testSetWaitListCapacity() {
        event.setWaitListCapacity(600);
        assertEquals(600, event.getWaitListCapacity(), "Waitlist capacity should be updated correctly.");
    }

    /**
     * Tests that the QR data for the event can be set and retrieved correctly.
     */
    @Test
    public void testSetQRData() {
        event.setQRData("1001");
        assertEquals("1001", event.getQRData(), "QR Data should be updated correctly.");
    }

    /**
     * Tests that the event ID can be set and retrieved correctly.
     */
    @Test
    public void testSetEventID() {
        event.setEventID("732fac3e-983a-44c0-b7b8-fcf720952024");
        assertEquals("732fac3e-983a-44c0-b7b8-fcf720952024", event.getEventID(), "Event ID should be updated correctly.");
    }

    /**
     * Tests that the event GeoLocation flag can be set and retrieved correctly.
     */
    @Test
    public void testSetGeoLocation() {
        event.setGeoLocation(false);
        assertFalse(event.getgeoLocation(), "GeoLocation should be updated correctly.");
    }

    /**
     * Tests that the QR hash for the event can be set and retrieved correctly.
     */
    @Test
    public void testSetQRHash() {
        event.setQRHash("03b0a9a34c950844a2e0edd7aa5604691dc1ec738a412e9ad436ed19b00usv98");
        assertEquals("03b0a9a34c950844a2e0edd7aa5604691dc1ec738a412e9ad436ed19b00usv98", event.getQRHash(), "QR Hash should be updated correctly.");
    }

    /**
     * Tests that the joined notifications flag can be set and retrieved correctly.
     */
    @Test
    public void testAddAndRetrieveWaitlisted() {
        event.addWaitlisted("iu767e14c6aa6e0d");
        assertTrue(event.getWaitlisted().contains("iu767e14c6aa6e0d"));
        assertEquals(1, event.getWaitlisted().size());
    }

    /**
     * Tests that the finalists list can be updated and retrieved correctly.
     */
    @Test
    public void testAddAndRetrieveFinalists() {
        event.addFinalist("fu767e14c6aa6e0d");
        assertTrue(event.getFinalists().contains("fu767e14c6aa6e0d"));
        assertEquals(1, event.getFinalists().size());
    }

    /**
     * Tests that the cancelled list can be updated and retrieved correctly.
     */
    @Test
    public void testAddAndRetrieveCancelled() {
        event.addCancelled("po767e14c6aa6e4d");
        assertTrue(event.getCancelled().contains("po767e14c6aa6e4d"));
        assertEquals(1, event.getCancelled().size());
    }

    /**
     * Tests that the selected list can be updated and retrieved correctly.
     */
    @Test
    public void testAddAndRetrieveSelected() {
        event.addSelected("uq767e14c6aa6e4q");
        assertTrue(event.getSelected().contains("uq767e14c6aa6e4q"));
        assertEquals(1, event.getSelected().size());
    }

    /**
     * Tests that the joined list can be updated and retrieved correctly.
     */
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

    /**
     * Tests adding and retrieving waitlisted notifications.
     */
    @Test
    public void testAddAndRetrieveWaitlistedNotifications() {
        // Add multiple waitlisted notifications
        event.addWaitlistedNotification("1650c1cc5910ab1a");
        event.addWaitlistedNotification("1650c1cc5910ab1b");
        event.addWaitlistedNotification("1650c1cc5910ab1c");

        // Assertions
        assertTrue(event.getWaitlistedNotificationsList().contains("1650c1cc5910ab1a"), "Waitlisted notifications should include 1650c1cc5910ab1a.");
        assertTrue(event.getWaitlistedNotificationsList().contains("1650c1cc5910ab1b"), "Waitlisted notifications should include 1650c1cc5910ab1b.");
        assertTrue(event.getWaitlistedNotificationsList().contains("1650c1cc5910ab1c"), "Waitlisted notifications should include 1650c1cc5910ab1c.");
        assertEquals(3, event.getWaitlistedNotificationsList().size(), "There should be 3 waitlisted notifications.");
    }

    /**
     * Tests adding and retrieving selected notifications.
     */
    @Test
    public void testAddAndRetrieveSelectedNotifications() {
        // Add multiple selected notifications
        event.addSelectedNotification("1773c1cc5910ab7b");
        event.addSelectedNotification("1773c1cc5910ab7c");
        event.addSelectedNotification("1773c1cc5910ab7d");

        // Assertions
        assertTrue(event.getSelectedNotificationsList().contains("1773c1cc5910ab7b"), "Selected notifications should include 1773c1cc5910ab7b.");
        assertTrue(event.getSelectedNotificationsList().contains("1773c1cc5910ab7c"), "Selected notifications should include 1773c1cc5910ab7c.");
        assertTrue(event.getSelectedNotificationsList().contains("1773c1cc5910ab7d"), "Selected notifications should include 1773c1cc5910ab7d.");
        assertEquals(3, event.getSelectedNotificationsList().size(), "There should be 3 selected notifications.");
    }

    /**
     * Tests adding and retrieving cancelled notifications.
     */
    @Test
    public void testAddAndRetrieveCancelledNotifications() {
        // Add multiple cancelled notifications
        event.addCancelledNotification("1850c1cc5910ab9q");
        event.addCancelledNotification("1850c1cc5910ab9r");
        event.addCancelledNotification("1850c1cc5910ab9s");

        // Assertions
        assertTrue(event.getCancelledNotificationsList().contains("1850c1cc5910ab9q"), "Cancelled notifications should include 1850c1cc5910ab9q.");
        assertTrue(event.getCancelledNotificationsList().contains("1850c1cc5910ab9r"), "Cancelled notifications should include 1850c1cc5910ab9r.");
        assertTrue(event.getCancelledNotificationsList().contains("1850c1cc5910ab9s"), "Cancelled notifications should include 1850c1cc5910ab9s.");
        assertEquals(3, event.getCancelledNotificationsList().size(), "There should be 3 cancelled notifications.");
    }

    /**
     * Tests adding and retrieving joined notifications.
     */
    @Test
    public void testAddAndRetrieveJoinedNotifications() {
        // Add multiple joined notifications
        event.addJoinedNotification("2004c1uc5910ab5q");
        event.addJoinedNotification("2004c1uc5910ab5r");
        event.addJoinedNotification("2004c1uc5910ab5s");

        // Assertions
        assertTrue(event.getJoinedNotificationsList().contains("2004c1uc5910ab5q"), "Joined notifications should include 2004c1uc5910ab5q.");
        assertTrue(event.getJoinedNotificationsList().contains("2004c1uc5910ab5r"), "Joined notifications should include 2004c1uc5910ab5r.");
        assertTrue(event.getJoinedNotificationsList().contains("2004c1uc5910ab5s"), "Joined notifications should include 2004c1uc5910ab5s.");
        assertEquals(3, event.getJoinedNotificationsList().size(), "There should be 3 joined notifications.");
    }

    /**
     * Tests that the event name can be retrieved correctly.
     */
    @Test
    public void testNotificationsListsInitialization() {
        assertNotNull(event.getWaitlistedNotificationsList());
        assertNotNull(event.getSelectedNotificationsList());
        assertNotNull(event.getJoinedNotificationsList());
        assertNotNull(event.getCancelledNotificationsList());
    }

    /**
     * Tests that the event name can be created with no fields.
     */
    @Test
    public void testEmptyConstructor() {
        Event emptyEvent = new Event();
        assertNotNull(emptyEvent);
        assertNull(emptyEvent.getName());
        assertNull(emptyEvent.getEventDate());
        assertNull(emptyEvent.getLocation());
        assertNull(emptyEvent.getTime());
        assertNull(emptyEvent.getPrice());
        assertNull(emptyEvent.getDescription());
        assertEquals(0, emptyEvent.getEventSlots());
        assertEquals(0, emptyEvent.getWaitListCapacity());
        assertNull(emptyEvent.getQRData());
        assertNull(emptyEvent.getQRHash());
        assertNull(emptyEvent.getEventID());
        assertNull(emptyEvent.getgeoLocation());
        assertNull(emptyEvent.getDeviceId());
        assertNull(emptyEvent.getSignupDeadline());

    }

    /**
     * Tests that the event lottery system correctly selects and updates the right lists
     */
    @Test
    public void testLotterySystem() {
        // Initialize event with a predefined waitlist and slots
        Event event = new Event();
        event.setEventSlots(3); // Assume the event has 3 slots
        event.setWaitlisted(new ArrayList<>(Arrays.asList("user1", "user2", "user3", "user4", "user5")));

        // Run lottery system
        event.lotterySystem();

        // Assertions to check the result
        assertEquals(3, event.getSelected().size(), "There should be exactly 3 selected entrants.");
        assertEquals(2, event.getWaitlisted().size(), "The waitlist should have 2 remaining entrants.");
        assertFalse(event.getSelected().isEmpty(), "The selected list should not be empty.");
        assertFalse(event.getWaitlisted().isEmpty(), "The waitlisted list should not be empty.");

        // Ensure that no selected entrants are in the waitlisted list
        for (String entrant : event.getSelected()) {
            assertFalse(event.getWaitlisted().contains(entrant), "Selected entrants should not be in the waitlisted list.");
        }
    }
}
