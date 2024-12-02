package com.example.slacks_lottoevent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import com.example.slacks_lottoevent.model.Event;

class EventUnitTest {

    private Event event;

    @BeforeEach
    void setUp() {
        event = new Event(
                "HackEd",
                "2024-12-31",
                "9120 116 St NW, Edmonton, AB T6G 2V4",
                "12:00 PM",
                "10.00",
                "Hackathon hosted by UAlberta students",
                200,
                500,
                "0001",
                "091fac3e-983a-44c0-b7b8-fcf720958082",
                true,
                "8568c0a5f070fe48fd6037dcab028910a844addee1a7502a01134396b4b9b76c",
                "le8e1540320d5c6d",
                "2024-12-28",
                "https://firebasestorage.googleapis.com/v0/b/slacks-lottoevent.appspot.com/o/event_posters%2F2024_12_01_18_27_34?alt=media&token=7016f30c-145d-4817-a7bd-7ade9dab09db"
        );
    }

    @Test
    void testConstructor() {
        assertEquals("HackEd", event.getName());
        assertEquals("2024-12-31", event.getEventDate());
        assertEquals("12:00 PM", event.getTime());
        assertEquals("10.00", event.getPrice());
        assertEquals("Hackathon hosted by UAlberta students", event.getDescription());
        assertEquals(200, event.getEventSlots());
        assertEquals(500, event.getWaitListCapacity());
        assertEquals("9120 116 St NW, Edmonton, AB T6G 2V4", event.getLocation());
        assertFalse(event.getDisabled());
        assertTrue(event.getgeoLocation());
        assertEquals("le8e1540320d5c6d", event.getDeviceId());
        assertEquals("0001", event.getQRData());
        assertEquals("091fac3e-983a-44c0-b7b8-fcf720958082", event.getEventID());
        assertFalse(event.getEntrantsChosen());
    }

    @Test
    void testAddWaitlisted() {
        event.addWaitlisted("op8e1540320d5c6d");
        assertEquals(1, event.getWaitlisted().size());
        assertTrue(event.getWaitlisted().contains("op8e1540320d5c6d"));
    }

    @Test
    void testAddFinalist() {
        event.addFinalist("lw8e1540320d5c6d");
        assertEquals(1, event.getFinalists().size());
        assertTrue(event.getFinalists().contains("lw8e1540320d5c6d"));
    }

    @Test
    void testAddSelected() {
        event.addSelected("lw8e1540320d5c8u");
        assertEquals(1, event.getSelected().size());
        assertTrue(event.getSelected().contains("lw8e1540320d5c8u"));
    }

    @Test
    void testAddCancelled() {
        event.addCancelled("lw8e188750d5c8u");
        assertEquals(1, event.getCancelled().size());
        assertTrue(event.getCancelled().contains("lw8e188750d5c8u"));
    }

    @Test
    void testLotterySystem() {
        event.addWaitlisted("lw8e188750d5c9q");
        event.addWaitlisted("qw8e103750d5c9q");
        event.addWaitlisted("pa5e103750d5c9q");
        event.setEventSlots(2);
        event.lotterySystem();

        assertEquals(2, event.getSelected().size());
        assertEquals(1, event.getWaitlisted().size());
        assertTrue(event.getEntrantsChosen());
    }

    @Test
    void testReSelecting() {
        event.addWaitlisted("jj1e103750d5c9q");
        event.addWaitlisted("oo5e103750d5c5q");
        event.setEventSlots(3);
        event.addFinalist("pa5e103750d5c0k");

        event.reSelecting();

        assertEquals(1, event.getFinalists().size());
        assertEquals(0, event.getWaitlisted().size());
    }

    @Test
    void testFullEvent() {
        event.addWaitlisted("lo5e103750d5c9q");
        event.addWaitlistedNotification("lo5e103750d5c9q");
        event.fullEvent();

        assertTrue(event.getWaitlisted().isEmpty());
        assertTrue(event.getWaitlistedNotificationsList().isEmpty());
    }

    @Test
    void testRemoveEntrant() {
        event.addWaitlisted("my5e103750d5c9q");
        event.addSelected("my5e103750d5c9q");
        event.addFinalist("my5e103750d5c9q");
        event.addCancelled("my5e103750d5c9q");
        event.addReselected("my5e103750d5c9q");

        event.removeEntrant("my5e103750d5c9q");

        assertFalse(event.getWaitlisted().contains("my5e103750d5c9q"));
        assertFalse(event.getSelected().contains("my5e103750d5c9q"));
        assertFalse(event.getFinalists().contains("my5e103750d5c9q"));
        assertFalse(event.getCancelled().contains("my5e103750d5c9q"));
        assertFalse(event.getReselected().contains("my5e103750d5c9q"));
    }


    @Test
    void testSettersAndGetters() {
        // Set and test event name
        event.setName("Tech Summit 2025");
        assertEquals("Tech Summit 2025", event.getName());

        // Set and test event date
        event.setEventDate("2025-03-20");
        assertEquals("2025-03-20", event.getEventDate());

        // Set and test event time
        event.setTime("09:30");
        assertEquals("09:30", event.getTime());

        // Set and test event price
        event.setPrice("10.99");
        assertEquals("10.99", event.getPrice());

        // Set and test event description
        event.setDescription("A premier technology summit focusing on AI, blockchain, and cloud innovations.");
        assertEquals("A premier technology summit focusing on AI, blockchain, and cloud innovations.", event.getDescription());

        // Set and test waitlist capacity
        event.setWaitListCapacity(100);
        assertEquals(100, event.getWaitListCapacity());

        // Set and test event slots
        event.setEventSlots(500);
        assertEquals(500, event.getEventSlots());

        // Set and test event location
        event.setLocation("Silicon Valley Conference Center");
        assertEquals("Silicon Valley Conference Center", event.getLocation());

        // Set and test event disabled status
        event.setDisabled(false);
        assertFalse(event.getDisabled());

        // Set and test QR data
        event.setQRData("1001");
        assertEquals("1001", event.getQRData());

        // Set and test QR hash
        event.setQRHash("0468c0a5f070fe48fd6037dcab028910a844addee1a7502a01134396b4b9b76c");
        assertEquals("0468c0a5f070fe48fd6037dcab028910a844addee1a7502a01134396b4b9b76c", event.getQRHash());

        // Set and test event ID
        event.setEventID("091fac3e-983a-44c0-b7b8-fcf720958082");
        assertEquals("091fac3e-983a-44c0-b7b8-fcf720958082", event.getEventID());

        // Set and test geolocation status
        event.setGeoLocation(true);
        assertTrue(event.getgeoLocation());

        // Set and test device ID
        event.setDeviceId("oj5e103750d5c9q");
        assertEquals("oj5e103750d5c9q", event.getDeviceId());

        // Set and test signup deadline
        event.setSignupDeadline("2025-03-01");
        assertEquals("2025-03-01", event.getSignupDeadline());

        // Set and test entrants chosen status
        event.setEntrantsChosen(true);
        assertTrue(event.getEntrantsChosen());

        // Set and test event poster URL
        event.setEventPosterURL("https://firebasestorage.googleapis.com/v0/b/slacks-lottoevent.appspot.com/o/event_posters%2F2024_12_01_18_27_34?alt=media&token=7016f30c-145d-4817-a7bd-7ade9dab09db");
        assertEquals("https://firebasestorage.googleapis.com/v0/b/slacks-lottoevent.appspot.com/o/event_posters%2F2024_12_01_18_27_34?alt=media&token=7016f30c-145d-4817-a7bd-7ade9dab09db", event.getEventPosterURL());

        // Set and test waitlisted users
        ArrayList<String> waitlisted = new ArrayList<>(List.of("my5e103750d5c9q", "my5e103750d5c9r"));
        event.setWaitlisted(waitlisted);
        assertEquals(waitlisted, event.getWaitlisted());

        // Set and test finalists
        ArrayList<String> finalists = new ArrayList<>(List.of("my5e103750d5c9s", "my5e103750d5c9t"));
        event.setFinalists(finalists);
        assertEquals(finalists, event.getFinalists());

        // Set and test selected users
        ArrayList<String> selected = new ArrayList<>(List.of("my5e103750d5c9u", "my5e103750d5c9v"));
        event.setSelected(selected);
        assertEquals(selected, event.getSelected());

        // Set and test reselected users
        ArrayList<String> reselected = new ArrayList<>(List.of("my5e103750d5c9w", "my5e103750d5c9x"));
        event.setReselected(reselected);
        assertEquals(reselected, event.getReselected());

        // Set and test cancelled users
        ArrayList<String> cancelled = new ArrayList<>(List.of("my5e103750d5c9y", "my5e103750d5c9z"));
        event.setCancelled(cancelled);
        assertEquals(cancelled, event.getCancelled());

        // Set and test waitlist notifications
        ArrayList<String> waitlistedNotifications = new ArrayList<>(List.of("my5e103750d5c9q", "my5e103750d5c9r"));
        event.setWaitlistedNotificationsList(waitlistedNotifications);
        assertEquals(waitlistedNotifications, event.getWaitlistedNotificationsList());

        // Set and test selected notifications
        ArrayList<String> selectedNotifications = new ArrayList<>(List.of("my5e103750d5c9s", "my5e103750d5c9t"));
        event.setSelectedNotificationsList(selectedNotifications);
        assertEquals(selectedNotifications, event.getSelectedNotificationsList());

        // Set and test joined notifications
        ArrayList<String> joinedNotifications = new ArrayList<>(List.of("lk5e103750d5c9y", "jq5e103750d5c9z"));
        event.setJoinedNotificationsList(joinedNotifications);
        assertEquals(joinedNotifications, event.getJoinedNotificationsList());

        // Set and test cancelled notifications
        ArrayList<String> cancelledNotifications = new ArrayList<>(List.of("my5e103750d5c9y", "my5e103750d5c9z"));
        event.setCancelledNotificationsList(cancelledNotifications);
        assertEquals(cancelledNotifications, event.getCancelledNotificationsList());

        // Set and test join locations
        ArrayList<HashMap<String, List<Double>>> joinLocations = new ArrayList<>();
        HashMap<String, List<Double>> locationMap = new HashMap<>();
        locationMap.put("my5e103750d5c9a", List.of(37.7749, -122.4194)); // Latitude and longitude for San Francisco
        joinLocations.add(locationMap);
        event.setJoinLocations(joinLocations);
        assertEquals(joinLocations, event.getJoinLocations());
    }



    @Test
    void testJoinLocations() {
        HashMap<String, List<Double>> locationMap = new HashMap<>();
        locationMap.put("le8e1540320d5c6d", List.of(53.5444, -113.4909));
        ArrayList<HashMap<String, List<Double>>> joinLocations = new ArrayList<>();
        joinLocations.add(locationMap);

        event.setJoinLocations(joinLocations);

        assertEquals(1, event.getJoinLocations().size());
        assertEquals(locationMap, event.getJoinLocations().get(0));
    }
}
