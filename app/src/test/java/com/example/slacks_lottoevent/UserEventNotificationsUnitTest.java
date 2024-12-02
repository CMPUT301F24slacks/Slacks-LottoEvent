package com.example.slacks_lottoevent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserEventNotificationsUnitTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String expectedName = "CMPUT 301 Final";
        String expectedDate = "2024-12-15";
        String expectedTime = "10:00";
        String expectedLocation = "9120 116 St NW, Edmonton, AB T6G 2V4";
        String expectedEventId = "091fac3e-983a-44c0-b7b8-fcf720958082";
        Boolean expectedSelected = true;

        // Act
        UserEventNotifications notification = new UserEventNotifications(
                expectedName, expectedDate, expectedTime, expectedLocation, expectedEventId, expectedSelected
        );

        // Assert
        assertEquals(expectedName, notification.getName(), "Name should match");
        assertEquals(expectedDate, notification.getDate(), "Date should match");
        assertEquals(expectedTime, notification.getTime(), "Time should match");
        assertEquals(expectedLocation, notification.getLocation(), "Location should match");
        assertEquals(expectedEventId, notification.getEventId(), "Event ID should match");
        assertEquals(expectedSelected, notification.getSelected(), "Selected status should match");
    }

    @Test
    void testUnselectedEvent() {
        // Arrange
        String name = "CMPUT 291 Final";
        String date = "2024-12-14";
        String time = "10:00";
        String location = "9211 116 St NW, Edmonton, AB T6G 1H9\n";
        String eventId = "662fac3e-983a-44c0-b7b8-fcf720958082";
        Boolean selected = false;

        // Act
        UserEventNotifications notification = new UserEventNotifications(
                name, date, time, location, eventId, selected
        );

        // Assert
        assertFalse(notification.getSelected(), "Selected status should be false for unselected events");
    }

    @Test
    void testNullFields() {
        // Arrange
        String name = null;
        String date = null;
        String time = null;
        String location = null;
        String eventId = null;
        Boolean selected = null;

        // Act
        UserEventNotifications notification = new UserEventNotifications(
                name, date, time, location, eventId, selected
        );

        // Assert
        assertNull(notification.getName(), "Name should be null");
        assertNull(notification.getDate(), "Date should be null");
        assertNull(notification.getTime(), "Time should be null");
        assertNull(notification.getLocation(), "Location should be null");
        assertNull(notification.getEventId(), "Event ID should be null");
        assertNull(notification.getSelected(), "Selected status should be null");
    }
}
