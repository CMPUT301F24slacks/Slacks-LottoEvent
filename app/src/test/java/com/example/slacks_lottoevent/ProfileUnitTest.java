package com.example.slacks_lottoevent;

import android.content.Context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.jupiter.api.Assertions.*;

import com.example.slacks_lottoevent.model.Profile;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
class ProfileUnitTest {

    private Profile profile;
    private Context context;

    @BeforeEach
    void setUp() {
        // Use Robolectric's application context
        context = org.robolectric.RuntimeEnvironment.getApplication();
        profile = new Profile("Divya Prasad", "7804489907", "dprasad1@ualberta.ca", "1299dd2bdf615aec", context);
    }

    @Test
    void testConstructor() {
        assertEquals("Divya Prasad", profile.getName());
        assertEquals("7804489907", profile.getPhone());
        assertEquals("dprasad1@ualberta.ca", profile.getEmail());
        assertEquals("1299dd2bdf615aec", profile.getDeviceId());
        assertTrue(profile.getAdminNotifications());
    }

    @Test
    void testSetNameWithoutContext() {
        profile.setUsingDefaultPicture(false);
        profile.setName("Divya Prasad", null);
        assertEquals("Divya Prasad", profile.getName());
    }

    @Test
    void testSetEmail() {
        profile.setEmail("scott34@ualberta.ca");
        assertEquals("scott34@ualberta.ca", profile.getEmail());
    }

    @Test
    void testSetPhone() {
        profile.setPhone("78044898845");
        assertEquals("78044898845", profile.getPhone());
    }

    @Test
    void testSetAdminNotifications() {
        profile.setAdminNotifications(false);
        assertFalse(profile.getAdminNotifications());
    }

    @Test
    void testSetUsingDefaultPicture() {
        profile.setUsingDefaultPicture(false);
        assertFalse(profile.getUsingDefaultPicture());
    }

    @Test
    void testSetAdmin() {
        profile.setAdmin(true);
        assertTrue(profile.getAdmin());
    }
}
