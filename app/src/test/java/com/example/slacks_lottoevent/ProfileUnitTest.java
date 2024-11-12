//package com.example.slacks_lottoevent;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Unit tests for the Profile class.
// */
//public class ProfileUnitTest {
//    private Profile profile;
//
//    /**
//     * Set up the profile object before each test.
//     */
//    @BeforeEach
//    public void setup() {
//        // Initialize Profile without Context
//        profile = new Profile("Tate McRae", "7804448883", "tateMcRae@gmail.com", null);
//    }
//
//    /**
//     * Test setting and getting the name.
//     */
//    @Test
//    public void testSetName() {
//        profile.setName("Taylor Swift", null);
//        assertEquals("Taylor Swift", profile.getName(), "Name updates to the new value.");
//    }
//
//    /**
//     * Test setting and getting the phone number.
//     */
//    @Test
//    public void testSetPhone() {
//        profile.setPhone("5878895544");
//        assertEquals("5878895544", profile.getPhone(), "Phone number updates to the new value.");
//    }
//
//    /**
//     * Test setting and getting the email.
//     */
//    @Test
//    public void testSetEmail() {
//        profile.setEmail("taylorSwift@ualberta.ca");
//        assertEquals("taylorSwift@ualberta.ca", profile.getEmail(), "Email updates to the new value.");
//    }
//
//    /**
//     * Test setting and getting the admin notifications preference.
//     */
//    @Test
//    public void testSetAdminNotifications() {
//        profile.setAdminNotifications(false);
//        assertFalse(profile.getAdminNotifications(), "Admin notifications preference updates to false.");
//
//        profile.setAdminNotifications(true);
//        assertTrue(profile.getAdminNotifications(), "Admin notifications preference updates to true.");
//    }
//
//    /**
//     * Test setting and getting the profile picture path.
//     */
//    @Test
//    public void testSetProfilePicturePath() {
//        profile.setProfilePicturePath("/path/to/image.png");
//        assertEquals("/path/to/image.png", profile.getProfilePicturePath(), "Profile picture path updates to the new value.");
//    }
//
//    /**
//     * Test setting and getting the 'using default picture' flag.
//     */
//    @Test
//    public void testSetUsingDefaultPicture() {
//        profile.setUsingDefaultPicture(false);
//        assertFalse(profile.isUsingDefaultPicture(), "Using default picture flag updates to false.");
//
//        profile.setUsingDefaultPicture(true);
//        assertTrue(profile.isUsingDefaultPicture(), "Using default picture flag updates to true.");
//    }
//}
